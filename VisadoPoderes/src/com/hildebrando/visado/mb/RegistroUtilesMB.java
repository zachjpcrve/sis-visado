package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsFeriado;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;

/**
 * Clase que se contiene metodos utiles necesarios para el registro de 
 * la solicitude de visado. Algunos de estos metodos son: obtenerEstudioMenorCarga, calcularComision,
 * calcularComision, obtenerSiguienteDiaHabil, etc
 * @author 
 * **/

@ManagedBean(name = "registroUtilesMB")
@SessionScoped
public class RegistroUtilesMB {
	
	public static Logger logger = Logger.getLogger(RegistroUtilesMB.class);
	private String entrada;
	private String resultado;

	/**
	  * Obtiene el código de un Estudio de Abogado con la menor cantidad
	  * de solicitudes asignadas
	  * @return codigo de estudio de abogados
	  */
	public String obtenerEstudioMenorCarga() {
		logger.debug("==== obtenerEstudioMenorCarga()====");			
		String codigoEstado = ConstantesVisado.CODIGO_ESTADO_ENVIADO;		
		String sHoraCorte = getHoraCorte();  //Para obtener hora de corte (parámetro de sistema)
		String resultEstudio = null;
		String []aHora = null;
		
		try{			
			aHora = sHoraCorte.split(":");
		}catch(Exception e){
			aHora = "00:00".split(":");
		}
		
		//Query para obtener estudio con menos solicitudes pendientes			
		String sql = "SELECT "+
				" EST.COD_ESTUDIO,"+
				" COUNT(sol.cod_soli) NRO_SOLICITUDES"+
				" FROM VISPOD.tiivs_estudio EST"+ 
				" LEFT JOIN VISPOD.TIIVS_SOLICITUD SOL" +
				" ON EST.COD_ESTUDIO = SOL.COD_ESTUDIO " +
				" AND TRIM(SOL.ESTADO) = '"+ codigoEstado +"'" + 
				" AND SOL.FECHA < " + 
				" CAST((SYSDATE - (SYSDATE - TRUNC(SYSDATE)) + "+aHora[0]+"/24 + "+aHora[1]+"/1440) AS TIMESTAMP) "+
				" WHERE ACTIVO = 1 " + 
				" GROUP BY EST.COD_ESTUDIO"+
				" ORDER BY NRO_SOLICITUDES ASC";
		
		logger.debug("Consulta Estudio Menor carga: "+sql);
		

		try {			
			Query query = SpringInit.devolverSession().createSQLQuery(sql);									
			List<Object[]> lstEstudios = query.list();				
			if(lstEstudios.size()>0){
				resultEstudio = (String) lstEstudios.get(0)[0];
			} else { //asignación automática
				logger.info("Estudios sin solicitudes pendientes - Se asignará aleatoriamente");
				logger.debug("Estudios sin solicitudes pendientes - Se asignará aleatoriamente");
				resultEstudio = getRandomEstudio();
			}
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+": " +e);
		}	
		return resultEstudio;
		
	}
	
	
	
	/**
	  * Calcula la comisión final de una solicitud.
	  * @param solicitud.
	  * @return Valor final de la comisión
	  */
	public Double calcularComision(TiivsSolicitud solicitud){

		Double comisionFinal = null;//Almacena al valor final de la comision
		String sTipoComision=null; //Almacena el tipo de comision que se debe aplicar
					
		Double dMontoLimite;
		try{
			String sMontoLimite = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_COMISION,
					ConstantesVisado.CODIGO_CAMPO_COMISION_X).getValor2();	
			dMontoLimite = Double.valueOf(sMontoLimite);
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener el parametro Comision: " +e);
			dMontoLimite = null;
		}
		
		//Obtenemos el listado de poderdantes de una solicitud
		List<TiivsAgrupacionPersona> lstPoderdantes;
		lstPoderdantes = getPoderdantesFromSolicitud(solicitud);//From consulta sql		
		
				
		if(lstPoderdantes!=null && lstPoderdantes.size()>0){
			if (existeFallecido(lstPoderdantes)) {
				if (solicitud.getImporte() > dMontoLimite) {
					sTipoComision = ConstantesVisado.CODIGO_CAMPO_FALLECIDO_MAYORA;
				} else {
					sTipoComision = ConstantesVisado.CODIGO_CAMPO_FALLECIDO_MENORA;
				}
			} else {
				if (sonTodasNaturales(lstPoderdantes)) {
					sTipoComision = ConstantesVisado.CODIGO_CAMPO_PERSONA_NATURAL;
				} else if (sonTodasJuridicas(lstPoderdantes)) {
					sTipoComision = ConstantesVisado.CODIGO_CAMPO_PERSONA_JURIDICA;
				} else {
					// Regla no permitida
					logger.info("Regla no permitida");
					logger.debug("Regla no permitida");
					sTipoComision = null;
				}
			}
			comisionFinal = obtenerComision(sTipoComision);	
		} else {
			comisionFinal = new Double(0);
			logger.debug("La solicitud no tiene Poderdantes");
		}				
		solicitud.setTipoComision(sTipoComision);
		solicitud.setComision(comisionFinal);
		return comisionFinal;
	}
	
	
	/**
	  * Obtiene el tipo de comisión de una solicitud.
	  * @param solicitud.
	  * @return Tipo de comisión de la comisión
	  */
	public String obtenerTipoComision(TiivsSolicitud solicitud){
		
		String sTipoComision=null; //Almacena el tipo de comision que se debe aplicar
					
		Double dMontoLimite;
		try{
			String sMontoLimite = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_COMISION,
					ConstantesVisado.CODIGO_CAMPO_COMISION_X).getValor2();	
			dMontoLimite = Double.valueOf(sMontoLimite);
				
			//Obtenemos el listado de poderdantes de una solicitud
			List<TiivsAgrupacionPersona> lstPoderdantes;
			lstPoderdantes = getPoderdantesFromSolicitud(solicitud);//From consulta sql		
			
					
			if(lstPoderdantes!=null && lstPoderdantes.size()>0){
				if (existeFallecido(lstPoderdantes)) {
					if (solicitud.getImporte() > dMontoLimite) {
						sTipoComision = ConstantesVisado.CODIGO_CAMPO_FALLECIDO_MAYORA;
					} else {
						sTipoComision = ConstantesVisado.CODIGO_CAMPO_FALLECIDO_MENORA;
					}
				} else {
					if (sonTodasNaturales(lstPoderdantes)) {
						sTipoComision = ConstantesVisado.CODIGO_CAMPO_PERSONA_NATURAL;
					} 
					else if (sonTodasJuridicas(lstPoderdantes)) {
						sTipoComision = ConstantesVisado.CODIGO_CAMPO_PERSONA_JURIDICA;
					} else {
						// Regla no permitida
						logger.info("Regla no permitida");
						logger.debug("Regla no permitida");
						sTipoComision = null;
					}
				}				
			} else {			
				logger.debug("La solicitud no tiene Poderdantes");
			}	
		
		}
		catch(Exception e){
			logger.error("No se pudo calcular la comisión: "+e);							
		}
		return sTipoComision;
	}


	
	/**
	 * Obtiene la fecha de respuesta de una solicitud - P026 CU02 [RN018]
	 * @return fecha de respuesta
	 * */
	public Date obtenerFechaRespuesta(){
		
		Date fechaRetorno;
		String sHoraCorte = getHoraCorte();  //Para obtener hora de corte (parámetro de sistema)		
		String []aHora = null;		
		try{			
			aHora = sHoraCorte.split(":");
		}catch(Exception e){
			aHora = "00:00".split(":");
		}
		int iHoraCorte  =Integer.valueOf(aHora[0] + aHora[1]);
		
		//Hora actual
		Calendar fechaActual = Calendar.getInstance();	
		int iHoraActual = Integer.valueOf(String.valueOf(fechaActual.get(Calendar.HOUR_OF_DAY))
				+ String.valueOf(fechaActual.get(Calendar.MINUTE)));		
		
		logger.debug("Compara fecha:"+iHoraActual+"<"+iHoraCorte);
		logger.info("Compara fecha:"+iHoraActual+"<"+iHoraCorte);
		if(iHoraActual <= iHoraCorte ){ //Si hora actual es menor igual a la hora de corte	
			logger.info("Hora es menor o igual que la fecha de corte");
			fechaRetorno = fechaActual.getTime();	 //Retornamos la fecha actual
		} else {
			logger.info("Hora es mayor a la hora de corte, se obtendrá siguiente fecha");
			fechaRetorno = obtenSiguienteDiaHabil(fechaActual);
		}		
		return fechaRetorno;
	}
	
	
	/**
	 * Obtiene la hora de corte de MultiTabla
	 * */
	private String getHoraCorte() {
		
		String sHoraCorte = null;
		GenericDao<TiivsMultitabla, Object> multiTablaDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);		
		TiivsMultitablaId multId = new TiivsMultitablaId(ConstantesVisado.CODIGO_TABLA_HORA_CORTE, 
				ConstantesVisado.CODIGO_CAMPO_HORA_CORTE);		
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ID_ALIAS, multId));
		List<TiivsMultitabla> multitabla= new ArrayList<TiivsMultitabla>();				
		try {
			multitabla = multiTablaDAO.buscarDinamico(filtro);
			sHoraCorte = multitabla.get(0).getValor1();
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en sHoraCorte: "+e);
		}
		return sHoraCorte;
	}	
	
	
	/**
	 * Obtiene la fila de una tabla de la MultiTabla de acuerdo a los parámetros
	 * @param Código de tabla
	 * @param Código de campo de la tabla
	 * @return TiivsMultitabla
	 * */
	public static TiivsMultitabla getRowFromMultitabla(String codigoMultitablaMoneda,
			String codigoCampo) {
		TiivsMultitabla resultMultiTabla = null;
		GenericDao<TiivsMultitabla, Object> multiTablaDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		try {
			TiivsMultitablaId tablaId = new TiivsMultitablaId(codigoMultitablaMoneda, codigoCampo);			
			resultMultiTabla = multiTablaDAO.buscarById(TiivsMultitabla.class,tablaId);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en resultMultiTabla: "+e);
		}
		return resultMultiTabla;
	}

	
	/**
	 * Obtiene aleatriamente el código de estudio en estado activo
	 * */
	private String getRandomEstudio() {
		
		String codEstudio = null;
		GenericDao<TiivsEstudio, Object> estudioDAO = (GenericDao<TiivsEstudio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsEstudio.class);					
		filtro.add(Restrictions.eq("activo", '1'));
		List<TiivsEstudio> lstEstudios= new ArrayList<TiivsEstudio>();				
		try {
			lstEstudios = estudioDAO.buscarDinamico(filtro);
			codEstudio = lstEstudios.get( 0 + (int)(Math.random()*lstEstudios.size())).getCodEstudio();
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en codEstudio: "+e);
		}
		return codEstudio;
	}
	
	/**
	  * Retorna el listado de poderdantes de una solicitud.
	  * @param solicitud.
	  * @return Listado de poderdantes
	  */
	private List<TiivsAgrupacionPersona> getPoderdantesFromSolicitud(
			TiivsSolicitud solicitud) {

		List<TiivsAgrupacionPersona> lstPoderdantes = new ArrayList<TiivsAgrupacionPersona>();
			
		Set<TiivsSolicitudAgrupacion> setSolicitudAgrupacion;
		setSolicitudAgrupacion = solicitud.getTiivsSolicitudAgrupacions();

		logger.info("solicitud.getTiivsSolicitudAgrupacions()" + solicitud.getTiivsSolicitudAgrupacions().size());
		
		Iterator itAgrupacion = setSolicitudAgrupacion.iterator();

		while (itAgrupacion.hasNext()) 
		{
			TiivsSolicitudAgrupacion valueAgrupacion = (TiivsSolicitudAgrupacion) itAgrupacion.next();
			Set<TiivsAgrupacionPersona> setAgruPersona;
			setAgruPersona = valueAgrupacion.getTiivsAgrupacionPersonas();
			
			logger.info("valueAgrupacion.getTiivsAgrupacionPersonas()" + valueAgrupacion.getTiivsAgrupacionPersonas().size());
			
			Iterator itAgruPersona = setAgruPersona.iterator();

			while (itAgruPersona.hasNext()){
				TiivsAgrupacionPersona valueAgruPersona = (TiivsAgrupacionPersona) itAgruPersona.next();
				
				if (valueAgruPersona.getTipPartic().equalsIgnoreCase(ConstantesVisado.CODIGO_CAMPO_PODERDANTE)) 
				{
					lstPoderdantes.add(valueAgruPersona);
				}
			}
		}

		return lstPoderdantes;		
	}	
		
	/**
	  * Verifica si todas las personas son de tipo Juridaca.
	  * @param lista de personas.
	  * @return TRUE: si todas las personas son de tipo Juridica
	  * 		FALSE: si al menos una no es Juridica
	  */
	private boolean sonTodasJuridicas(
			List<TiivsAgrupacionPersona> lstAgrupacionPersona) {
		boolean bRet = false;

		int cont = 0;
		for (TiivsAgrupacionPersona agruPersona : lstAgrupacionPersona) {
			TiivsPersona persona = agruPersona.getTiivsPersona();
			if(persona!=null){
				TiivsMultitabla multi = getRowFromMultitabla(
						ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC,persona.getTipDoi());
				if (multi != null) {
					if (multi.getId().getCodElem().equalsIgnoreCase(ConstantesVisado.CODIGO_CAMPO_TIPODOI_RUC)) {
						cont++;
					}
				}
			} else {
				logger.debug("Atributo persona nulo para la agrupacion: "+ agruPersona.getNumGrupo());
			}
		}
		if (lstAgrupacionPersona.size() == cont) {
			bRet = true;
		} else {
			bRet = false;
		}
		return bRet;
	}

	/**
	 * Verifica si todas las personas son de tipo natural.
	 * 
	 * @param lista
	 *            de personas.
	 * @return TRUE: si todas las personas no son de tipo Juridica FALSE: si al
	 *         menos una es Juridica
	 */
	private boolean sonTodasNaturales(
			List<TiivsAgrupacionPersona> lstAgrupacionPersona) {

		boolean bRet = false;

		int cont = 0;
		for (TiivsAgrupacionPersona agruPersona : lstAgrupacionPersona) {			
			TiivsPersona persona = agruPersona.getTiivsPersona();
			if(persona!=null){
				String tipopersona = persona.getTipDoi();
				TiivsMultitabla multi = getRowFromMultitabla(
						ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC,
						tipopersona);
				if (multi != null) {
					if (!multi.getId().getCodElem().equalsIgnoreCase(ConstantesVisado.CODIGO_CAMPO_TIPODOI_RUC)) {
						cont++;
					}
				}
			} else {
				logger.debug("Atributo persona nulo para la agrupacion:" + agruPersona.getNumGrupo());
			}
		}
		if (lstAgrupacionPersona.size() == cont) {
			bRet = true;
		} else {
			bRet = false;
		}
		return bRet;
	}

	/**
	  * Verifica si al menos una persona tiene la clasificacion
	  * de fallecido
	  */
	private boolean existeFallecido(
			List<TiivsAgrupacionPersona> lstAgrupacionPersona) {
		boolean bRet = false;
		for (TiivsAgrupacionPersona agruPersona : lstAgrupacionPersona) {
			TiivsAgrupacionPersona agruPersonaId = agruPersona;
			TiivsMultitabla multi = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION_PERSONA,
					agruPersonaId.getClasifPer());
			if (multi != null) {
				if (multi.getId().getCodElem().equalsIgnoreCase(ConstantesVisado.CODIGO_TIPO_CLASIFICACION_FALLECIDO)) {
					bRet = true;
					break;
				}
			}
		}
		return bRet;
	}
	
	/**
	 * Devuelve la comisión según el tipo de comisión
	 * @param tipo de comisión 
	 * */
	public Double obtenerComision(String sTipoComision) {
		TiivsMultitabla multi = getRowFromMultitabla(ConstantesVisado.CODIGO_MULTITABLA_COMISION,sTipoComision);
		Double comision;
		try {
			if(multi!=null){
				comision = Double.valueOf(multi.getValor2());
			} else {
				logger.info("Registro no encontrado para el tipo de comisión: " + sTipoComision);
				comision = new Double(0);
			}
		} catch (Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en obtenerComision: "+e);
			comision = new Double(0);
		}
		return comision;
	}
		
	/**
	  * Obtiene el siguiente día hábil considerando los feriados nacionales, 
	  * sábados y domingos como días no hábiles.
	  * @return Fecha Habil
	  */
	private Date obtenSiguienteDiaHabil(Calendar fecha) {
		
		Date fechaRetorno;	
		
		fecha.add(Calendar.DATE, 1); // sumamos 1 día a la fecha			
		if(fecha.get(Calendar.DAY_OF_WEEK)==7){ //si es sabado sumamos 2 dias a la fecha
			fecha.add(Calendar.DATE, 2);
			logger.info("Fecha:'" + fecha + "' es Sábado se obtendrá el siguiente día hábil ");
		}		
		if(fecha.get(Calendar.DAY_OF_WEEK)==1){ //si es Domingo sumamos 1 dia a la fecha
			fecha.add(Calendar.DATE, 1);
			logger.info("Fecha:'" + fecha + "' es Domingo se obtendrá el siguiente día hábil ");
			logger.info("Domingo se agregara 1 día");
		}
				
		//Buscamos si la fecha es feriado
		GenericDao<TiivsFeriado, Object> feriadoDAO = (GenericDao<TiivsFeriado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsFeriado.class);				
		filtro.add(Restrictions.eq("indicador", 'N'));
		filtro.add(Restrictions.eq("fecha", fecha.getTime()));
		
		List<TiivsFeriado> lstFeriados= new ArrayList<TiivsFeriado>();				
		try {
			lstFeriados = feriadoDAO.buscarDinamico(filtro);			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en obtener lstFeriados: "+e);
		}
		
		if(!lstFeriados.isEmpty()){ // Si la fecha es feriado 
			logger.info("Fecha:'" + fecha + "' es Feriado se obtendrá el siguiente día hábil ");
			fechaRetorno = obtenSiguienteDiaHabil(fecha);
		} else {
			fechaRetorno = fecha.getTime();
		}		
		return fechaRetorno;
	}
	
	
	public String getEntrada() {
		return entrada;
	}

	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}
	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

}

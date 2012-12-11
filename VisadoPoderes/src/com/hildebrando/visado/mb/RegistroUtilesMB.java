package com.hildebrando.visado.mb;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.hildebrando.visado.modelo.TiivsAgrupacionPersonaId;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;



@ManagedBean(name = "registroUtilesMB")
@SessionScoped
public class RegistroUtilesMB {
	
	public static Logger logger = Logger.getLogger(RegistroUtilesMB.class);
	private String entrada;
	private String resultado;
	
	
	/**
     * Metodo prueba
     * */
	public void asignarEstudio() {
		
		String estudioAsignado = obtenerEstudioMenorCarga();
		System.out.println("Estudio Asignado:" + estudioAsignado);
		this.resultado = estudioAsignado;
	}
	
	
    /**
     * Metodo prueba
     * */
	public void calcularComision() {
						
		TiivsPersona persona = null;
		GenericDao<TiivsPersona, Object> personaDAO = (GenericDao<TiivsPersona, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);
		filtro.add(Restrictions.eq("codPer", 1));
		List<TiivsPersona> lstPersonas = new ArrayList<TiivsPersona>();
		try {
			lstPersonas = personaDAO.buscarDinamico(filtro);
			persona = lstPersonas.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TiivsSolicitud solicitud = new TiivsSolicitud();
		solicitud.setCodSoli("1234567");
		solicitud.setImporte(new BigDecimal(40000));

		TiivsSolicitudAgrupacion agrupacion = new TiivsSolicitudAgrupacion(
				new TiivsSolicitudAgrupacionId(solicitud.getCodSoli(), 1),
				solicitud);

		TiivsAgrupacionPersona aPersona = new TiivsAgrupacionPersona(
				new TiivsAgrupacionPersonaId(solicitud.getCodSoli(), agrupacion
						.getId().getNumGrupo(), 1, "0001", "0002"), agrupacion,
				persona);

		List<TiivsAgrupacionPersona> lstAPersona = new ArrayList<TiivsAgrupacionPersona>();
		lstAPersona.add(aPersona);	
		Set<TiivsAgrupacionPersona> setAPersona = new HashSet<TiivsAgrupacionPersona>(lstAPersona);
		agrupacion.setTiivsAgrupacionPersonas(setAPersona);
		
		List<TiivsSolicitudAgrupacion> lstAgrupacion = new ArrayList<TiivsSolicitudAgrupacion>();
		lstAgrupacion.add(agrupacion);	
		Set<TiivsSolicitudAgrupacion> setAgrupacion = new HashSet<TiivsSolicitudAgrupacion>(lstAgrupacion);
		solicitud.setTiivsSolicitudAgrupacions(setAgrupacion);
					
		resultado = this.calcularComision(solicitud).toString();		
		System.out.println("Fin metodo");
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
			e.printStackTrace();
			logger.error("No se pudo obtener el parámero de comisión:"+e.getMessage(),e);			
			System.out.println("No se pudo obtener el parámero de comisión");			
			dMontoLimite = null;
		}
		
		//Obtenemos el listado de poderdantes de una solicitud
		List<TiivsAgrupacionPersona> lstPoderdantes;
		lstPoderdantes = getPoderdantesFromSolicitud(solicitud);//From consulta sql		
		
				
		if(lstPoderdantes!=null && lstPoderdantes.size()>0){
			if (existeFallecido(lstPoderdantes)) {
				if (solicitud.getImporte().doubleValue() > dMontoLimite) {
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
					System.out.println("Regla no permitida");
					sTipoComision = null;
				}
			}
			comisionFinal = obtenerComision(sTipoComision);	
		} else {
			comisionFinal = new Double(0);
			logger.info("La solicitud no tiene Poderdantes");
			System.out.println("La solicitud no tiene Poderdantes");
		}					
		return comisionFinal;
	}


	/**
	  * Obtiene el código de un Estudio de Abogado con la menor cantidad
	  * de solicitudes asignadas
	  * @return codigo de estudio de abogados
	  */
	public String obtenerEstudioMenorCarga() {
					
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
		String sql = " SELECT "+ 
			 " EST.COD_ESTUDIO,"+
			 " COUNT(SOL.COD_SOLI) NRO_SOLICITUDES" +
			 " FROM TIIVS_SOLICITUD SOL" +
			 " INNER JOIN TIIVS_ESTUDIO EST ON EST.COD_ESTUDIO = SOL.COD_ESTUDIO" +
			 " WHERE" +
			 " TRIM(SOL.ESTADO) = '"+ codigoEstado +"'" + 
			 " AND EST.ACTIVO = 1" + 			 
			 " AND SOL.FECHA < " +			 
			 " CAST((SYSDATE - (SYSDATE - TRUNC(SYSDATE)) + "+aHora[0]+"/24 + "+aHora[1]+"/1440) AS TIMESTAMP) "+		 
			 " GROUP BY" +
			 " EST.COD_ESTUDIO" +
			 " ORDER BY NRO_SOLICITUDES ASC";	
		
		System.out.println("Consulta:"+sql);
		

		try {			
			Query query = SpringInit.devolverSession().createSQLQuery(sql);									
			List<Object[]> lstEstudios = query.list();				
			if(lstEstudios.size()>0){
				resultEstudio = (String) lstEstudios.get(0)[0];
			} else { //asignación automática
				logger.info("Estudios sin solicitudes pendientes - Se asignará aleatoriamente");
				System.out.println("Estudios sin solicitudes pendientes - Se asignará aleatoriamente");
				resultEstudio = getRandomEstudio();
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
			
		return resultEstudio;
		
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
			e.printStackTrace();
		}
		return codEstudio;
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
			e.printStackTrace();
		}
		return sHoraCorte;
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

		Iterator itAgrupacion = setSolicitudAgrupacion.iterator();

		while (itAgrupacion.hasNext()) {
			TiivsSolicitudAgrupacion valueAgrupacion = (TiivsSolicitudAgrupacion) itAgrupacion
					.next();
			Set<TiivsAgrupacionPersona> setAgruPersona;
			setAgruPersona = valueAgrupacion.getTiivsAgrupacionPersonas();
			Iterator itAgruPersona = setAgruPersona.iterator();

			while (itAgruPersona.hasNext()) {
				TiivsAgrupacionPersona valueAgruPersona = (TiivsAgrupacionPersona) itAgruPersona
						.next();
				if (valueAgruPersona
						.getId()
						.getTipPartic()
						.equalsIgnoreCase(
								ConstantesVisado.CODIGO_CAMPO_PODERDANTE)) {
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
						ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC,
						persona.getTipDoi());
				if (multi != null) {
					if (multi.getValor1().equalsIgnoreCase(
							ConstantesVisado.TIPO_DOCUMENTO_RUC)) {
						cont++;
					}
				}
			} else {
				System.out.println("Atributo persona nulo para la agrupacion:"
						+ agruPersona.getId().getNumGrupo());
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
					if (!multi.getValor1().equalsIgnoreCase(
							ConstantesVisado.TIPO_DOCUMENTO_RUC)) {
						cont++;
					}
				}
			} else {
				System.out.println("Atributo persona nulo para la agrupacion:" + agruPersona.getId().getNumGrupo());
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
			TiivsAgrupacionPersonaId agruPersonaId = agruPersona.getId();
			TiivsMultitabla multi = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION_PERSONA,
					agruPersonaId.getClasifPer());
			if (multi != null) {
				if (multi.getValor1().equalsIgnoreCase(
						ConstantesVisado.TIPO_CLASIFICACION_FALLECIDO)) {
					bRet = true;
					break;
				}
			}
		}
		return bRet;
	}
	
	private Double obtenerComision(String sTipoComision) {
		TiivsMultitabla multi = getRowFromMultitabla(
				ConstantesVisado.CODIGO_MULTITABLA_COMISION,
				sTipoComision);
		Double comision;
		try {
			if(multi!=null){
				comision = Double.valueOf(multi.getValor2());
			} else {
				logger.info("Registro no encontrado para el tipo de comisión: " + sTipoComision);
				System.out.println("Registro no encontrado para el tipo de comisión: " + sTipoComision);
				comision = new Double(0);
			}
		} catch (Exception e){
			e.printStackTrace();
			comision = new Double(0);
		}
		return comision;
	}
	
	private TiivsMultitabla getRowFromMultitabla(String codigoMultitablaMoneda,
			String codigoCampo) {
		// TODO Apéndice de método generado automáticamente
		TiivsMultitabla resultMultiTabla = null;
		GenericDao<TiivsMultitabla, Object> multiTablaDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		try {
			TiivsMultitablaId tablaId = new TiivsMultitablaId(
					codigoMultitablaMoneda, codigoCampo);			
			resultMultiTabla = multiTablaDAO.buscarById(TiivsMultitabla.class,
					tablaId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMultiTabla;
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

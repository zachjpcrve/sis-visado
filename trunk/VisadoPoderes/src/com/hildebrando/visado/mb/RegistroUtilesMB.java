package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

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
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;



@ManagedBean(name = "registroUtilesMB")
@SessionScoped
public class RegistroUtilesMB {
	
	public static Logger logger = Logger.getLogger(RegistroUtilesMB.class);
	private String nroSolicitud;
	private String resultado;
	
	public void asignarSolicitud() {
		
		String abogadoAsignado = getAbogadoPorSolicitudes();		
		System.out.println("Abogado Asignado:" + abogadoAsignado);
	}
	
	public void calcularComision() {
			
		GenericDao<TiivsSolicitud, Object> solicitudDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsSolicitud.class);					
		filtro.add(Restrictions.eq("codSoli", this.getNroSolicitud()));
		List<TiivsSolicitud> lstSolicitud = new ArrayList<TiivsSolicitud>();				
		try {
			lstSolicitud = solicitudDAO.buscarDinamico(filtro);			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		if(lstSolicitud!=null && lstSolicitud.size()>0)
			resultado = this.calcularComision(lstSolicitud.get(0)).toString();
		else
			resultado = "No hay solicitud";
		
	}

	/**
	  * Asigna un abogado a una solicitud según carga de trabajo.
	  * @param solicitud.
	  * @return codigo de registro de un abogado
	  */
	public TiivsSolicitud asignarSolicitud(TiivsSolicitud solicitud) {
		
		String abogadoAsignado = getAbogadoPorSolicitudes();		
		System.out.println("Abogado Asignado:" + abogadoAsignado);
		solicitud.setRegAbogado(abogadoAsignado);
		return solicitud;
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
		lstPoderdantes = getPoderdantesFromSolicitud(solicitud);
		
				
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


	private String getAbogadoPorSolicitudes() {
					
		String codigoEstado = ConstantesVisado.CODIGO_ESTADO_ENVIADO;		
		String sHoraCorte = getHoraCorte();  //Para obtener hora de corte (parámetro de sistema)
		String resultEstudio = null;
		String resultAbogado = null;
		
		//Query para obtener estudio con menos solicitudes pendientes
		String hql = " SELECT "+ 
			 " EST.COD_ESTUDIO,"+
			 " COUNT(SOL.COD_SOLI) NRO_SOLICITUDES" +
			 " FROM TIIVS_SOLICITUD SOL" +
			 " INNER JOIN TIIVS_MIEMBRO ABO ON SOL.REG_ABOGADO = ABO.COD_MIEMBRO" +
			 " INNER JOIN TIIVS_ESTUDIO EST ON EST.COD_ESTUDIO = ABO.ESTUDIO" +
			 " WHERE" +
			 " TRIM(SOL.ESTADO) = '"+ codigoEstado +"'" + 
			 " AND EST.ACTIVO = 1" + 			 
			 " AND (" +
			 " EXTRACT(YEAR FROM SOL.FECHA) = EXTRACT(YEAR FROM SYSDATE) AND"+
			 " EXTRACT(MONTH FROM SOL.FECHA) = EXTRACT(MONTH FROM SYSDATE) AND"+
			 " EXTRACT(DAY FROM SOL.FECHA) = EXTRACT(DAY FROM SYSDATE) AND"+
			 " EXTRACT(HOUR FROM SOL.FECHA) <= "+sHoraCorte+")"+		 
			 " GROUP BY" +
			 " EST.COD_ESTUDIO" +
			 " ORDER BY NRO_SOLICITUDES ASC";
		 
		//System.out.println("SQL2 : "+hql);
		

		try {			
			Query query = SpringInit.devolverSession().createSQLQuery(hql);									
			List<Object[]> lstEstudios = query.list();			
			//System.out.println("Tamaño result:" + lstEstudios.size());	
			if(lstEstudios.size()>0){
				resultEstudio = (String) lstEstudios.get(0)[0];
			} else { //asignación automática
				System.out.println("Estudios sin solicitudes pendientes");
				System.out.println("Asignación aleatoria");
				resultAbogado = getRandomAbogado();
			}
		} catch (Exception e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
		
		
		if(resultEstudio!=null){
			
			//Query para obtener abogado con menos solicitudes pendientes
			hql = " SELECT "+   
			  	" SOL.REG_ABOGADO,"+
			  	" COUNT(SOL.COD_SOLI) NRO_SOLICITUDES"+
			  	" FROM TIIVS_SOLICITUD SOL"+ 
			  	" INNER JOIN TIIVS_MIEMBRO ABO ON SOL.REG_ABOGADO = ABO.COD_MIEMBRO"+
			  	" WHERE"+
			  	" TRIM(SOL.ESTADO) = '"+codigoEstado+"'"+ 
			  	" AND TRIM(ABO.ESTUDIO) = '"+resultEstudio+"'"+
			  	" AND (" +
				" EXTRACT(YEAR FROM SOL.FECHA) = EXTRACT(YEAR FROM SYSDATE) AND"+
				" EXTRACT(MONTH FROM SOL.FECHA) = EXTRACT(MONTH FROM SYSDATE) AND"+
				" EXTRACT(DAY FROM SOL.FECHA) = EXTRACT(DAY FROM SYSDATE) AND"+
				" EXTRACT(HOUR FROM SOL.FECHA) <= "+sHoraCorte+")"+		
			  	" GROUP BY"+ 
			  	" SOL.REG_ABOGADO"+
			  	" ORDER BY NRO_SOLICITUDES ASC";
			
			try {			
				Query query = SpringInit.devolverSession().createSQLQuery(hql);									
				List<Object[]> lstEstudios = query.list();			
				System.out.println("Tamaño result:" + lstEstudios.size());
				if(lstEstudios.size()>0){
					resultAbogado = (String) lstEstudios.get(0)[0];
				}
			} catch (Exception e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}
		} 
		
		return resultAbogado;
		
	}

	private String getRandomAbogado() {
		
		String regAbogado = null;
		GenericDao<TiivsMiembro, Object> abogadoDAO = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);			
		TiivsGrupo grupoJuridico = new TiivsGrupo();
		grupoJuridico.setCodGrupo(ConstantesVisado.COD_GRUPO_JRD);
		filtro.add(Restrictions.eq("tiivsGrupo", grupoJuridico));
		List<TiivsMiembro> lstAbogados= new ArrayList<TiivsMiembro>();				
		try {
			lstAbogados = abogadoDAO.buscarDinamico(filtro);
			regAbogado = lstAbogados.get( 0 + (int)(Math.random()*lstAbogados.size())).getCodMiembro();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return regAbogado;
	}

	//Obtiene hora de corte de la BD
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
		
		List<TiivsAgrupacionPersona> lstPoderdantes;
		
		String codigoPoderdante = ConstantesVisado.CODIGO_CAMPO_PODERDANTE;
		
		GenericDao<TiivsAgrupacionPersona, Object> agruPersonaDAO = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsAgrupacionPersona.class);
		filtro.add(Restrictions.eq(
				ConstantesVisado.CAMPO_ID_CODIGO_SOLICITUD_ALIAS,
				solicitud.getCodSoli()));
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ID_TIPO_PARTIC_ALIAS,
				codigoPoderdante));
		try {
			lstPoderdantes = new ArrayList<TiivsAgrupacionPersona>();
			lstPoderdantes = agruPersonaDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			lstPoderdantes = null;
			e.printStackTrace();
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
			TiivsMultitabla multi = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC,
					persona.getTipDoi());
			if (multi != null) {
				if (multi.getValor1().equalsIgnoreCase(
						ConstantesVisado.TIPO_DOCUMENTO_RUC)) {
					cont++;
				}
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
			String tipopersona = agruPersona.getTiivsPersona().getTipDoi();
			TiivsMultitabla multi = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC,
					tipopersona);
			if (multi != null) {
				if (!multi.getValor1().equalsIgnoreCase(
						ConstantesVisado.TIPO_DOCUMENTO_RUC)) {
					cont++;
				}
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
		
	public String getNroSolicitud() {
		return nroSolicitud;
	}

	public void setNroSolicitud(String nroSolicitud) {
		this.nroSolicitud = nroSolicitud;
	}
	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

}

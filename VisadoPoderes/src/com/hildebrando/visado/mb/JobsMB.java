package com.hildebrando.visado.mb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Expression;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.general.entities.Feriado;
import com.bbva.general.service.TablaGeneral;
import com.bbva.general.service.TablaGeneralServiceLocator;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.impl.GenericDaoImpl;

/**
 * Clase que se encarga de manejar los jobs de la aplicación: Incluye por ejemplo
 * los feriados, etc
 * @author  
 * */

@ManagedBean(name = "admJobs")
@SessionScoped
public class JobsMB {
	@ManagedProperty(value = "#{consultarSolicitudMB}")
	private static ConsultarSolicitudMB consultarMB;
	public static Logger logger = Logger.getLogger(JobsMB.class);
	
	public static void cargarFeriados() {
		try {

			// 2 digitos: Departamento
			// 2 digitos: Provincia
			// 3 digitos: Distrito

			Timestamp tstInicio = new Timestamp(new java.util.Date().getTime());
			logger.debug("==== INICIA PROCESO CARGA FERIADOS === Inicio: " + tstInicio);

			Feriado[] resultado = obtenerDatosWebService().getFeriadoListado();

			Timestamp tsLatencia = new Timestamp(new java.util.Date().getTime());
			double segundosUtilizadosLat = restarFechas(tstInicio, tsLatencia);
			logger.debug("Tiempo demora obtenerDatosWebService: ["+ segundosUtilizadosLat + "] segundos.");

			for (Feriado feriado : resultado) {		
				
				int anio = feriado.getFecha().get(Calendar.YEAR);				
				int anioActual = Calendar.getInstance().get(Calendar.YEAR);
				
				if (anio >= anioActual) {
					
					if (feriado.getUbigeo() != null) {
						if(logger.isDebugEnabled()){
							logger.debug("------------------FERIADOS LOCALES------------------------");
							logger.debug("Ind. Feriado: " + feriado.getIndicador());
							logger.debug("Fecha: "+ feriado.getFecha().getTime().toGMTString());
							logger.debug("Ubigeo: " + feriado.getUbigeo());
							logger.debug("Descripcion: " + feriado.getDescripcion());
							logger.debug("--------------------------------------------------");
						}
					} else {
						if(logger.isDebugEnabled()){
							logger.debug("------------------FERIADOS NACIONALES------------------------");
							logger.debug("Ind. Feriado: " + feriado.getIndicador());
							logger.debug("Fecha: "+ feriado.getFecha().getTime().toGMTString());
							logger.debug("Descripcion: " + feriado.getDescripcion());
							logger.debug("--------------------------------------------------");
						}
					}

					com.hildebrando.visado.modelo.TiivsFeriado ferid = new com.hildebrando.visado.modelo.TiivsFeriado();
					
					if (feriado.getIndicador().equals(ConstantesVisado.FERIADO.LOCAL)) {
						ferid.setIndicador('L');
					} else {
						ferid.setIndicador('N');
					}

					ferid.setFecha(feriado.getFecha().getTime());					
					ferid.setCodDist(feriado.getUbigeo());

//					if (!validarSiExiste("feriado", ferid)) {
						grabarFeriado(ferid);						
//					}
				}
			}

			Timestamp tstFin = new Timestamp(new java.util.Date().getTime());
			logger.debug("=== TERMINA PROCESO CARGA FERIADOS === Fin: " + tstFin);

			double segundosUtilizados = restarFechas(tstInicio, tstFin);
			logger.debug("Proceso de carga de feriados finalizado en ["+ segundosUtilizados + "] segundos");

		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al cargar los feriados: "+e);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static boolean validarSiExisteFeriado(String tabla, String campo,Object valor) {
		boolean existe = false;

		if (tabla.equals(ConstantesVisado.FERIADO.tablaferiado)) {
			List<com.hildebrando.visado.modelo.TiivsFeriado> results = new ArrayList<com.hildebrando.visado.modelo.TiivsFeriado>();
			GenericDaoImpl<com.hildebrando.visado.modelo.TiivsFeriado, Integer> feriadoDAO = (GenericDaoImpl<com.hildebrando.visado.modelo.TiivsFeriado, Integer>) SpringInit
					.getApplicationContext().getBean("genericoDao");

			Busqueda filtro = Busqueda.forClass(com.hildebrando.visado.modelo.TiivsFeriado.class);
			filtro.add(Expression.eq(campo, valor));

			try {
				results = feriadoDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.debug(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al consultar feriados: "+e);
			}
			if (results.size() > 0) {
				existe = true;
			}
		}
		return existe;
	}

	@SuppressWarnings({ "unchecked" })
	public static void grabarFeriado(com.hildebrando.visado.modelo.TiivsFeriado ferid) {
		GenericDao<com.hildebrando.visado.modelo.TiivsFeriado, Object> feriadoDAO = (GenericDao<com.hildebrando.visado.modelo.TiivsFeriado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			if (validarSiExisteFeriado("feriado", "fecha",ferid.getFecha())) {
				feriadoDAO.modificar(ferid);
			} else {
				feriadoDAO.insertar(ferid);
				logger.debug(ConstantesVisado.MENSAJE.REGISTRO_OK+"el feriado -> Dist:"+ferid.getCodDist() + "  tipo: "+ferid.getIndicador());
			}
			// FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al grabar el feriado: "+e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("No registro","No se registro el feriado desde el webservice"));
		}
	}

	public static boolean validarSiExiste(String tabla, Object obj) {
		boolean existe = false;
		if (tabla.equals(ConstantesVisado.FERIADO.tablaferiado)) {
			/*
			 * List<com.hildebrando.legal.modelo.Feriado> results = new
			 * ArrayList<com.hildebrando.legal.modelo.Feriado>(); String
			 * query="select * from " + tabla + " where " + campo + " = '" +
			 * valor + "'" ; Query queryFer = SpringInit.devolverSession().
			 * createSQLQuery
			 * (query).addEntity(com.hildebrando.legal.modelo.Feriado.class);
			 * 
			 * results = queryFer.list();
			 */
			com.hildebrando.visado.modelo.TiivsFeriado feridTMP = (com.hildebrando.visado.modelo.TiivsFeriado) obj;
			List<com.hildebrando.visado.modelo.TiivsFeriado> results = new ArrayList<com.hildebrando.visado.modelo.TiivsFeriado>();
			GenericDaoImpl<com.hildebrando.visado.modelo.TiivsFeriado, Integer> feriadoDAO = (GenericDaoImpl<com.hildebrando.visado.modelo.TiivsFeriado, Integer>) SpringInit
					.getApplicationContext().getBean("genericoDao");

			Busqueda filtro = Busqueda.forClass(com.hildebrando.visado.modelo.TiivsFeriado.class);
			filtro.add(Expression.eq("fecha", feridTMP.getFecha()));			
			filtro.add(Expression.eq("indicador", feridTMP.getIndicador()));

			try {
				results = feriadoDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.debug(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"en la validacion feriados: "+e);
			}

			if (results.size() > 0) {
				existe = true;
			}
		}
		return existe;
	}
	
	public static void validarSolicitudesVencidas()
	{
		consultarMB = new ConsultarSolicitudMB();
		consultarMB.validarCambioEstadoVencido();
	}

	public static TablaGeneral obtenerDatosWebService() {
		TablaGeneral tbGeneralWS = null;
		try {
			TablaGeneralServiceLocator tablaGeneralServiceLocator = (TablaGeneralServiceLocator) SpringInit
					.getApplicationContext().getBean("tablaGeneralServiceLocator");
			tbGeneralWS = tablaGeneralServiceLocator.getTablaGeneral();
		} catch (ServiceException e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al obtener datos del WebService: "+e);
		}
		return tbGeneralWS;
	}

	private static double restarFechas(Timestamp fhInicial, Timestamp fhFinal) {
		long fhInicialms = fhInicial.getTime();
		long fhFinalms = fhFinal.getTime();
		long diferencia = fhFinalms - fhInicialms;
		double a = (double) diferencia / (double) (1000);

		return a;
	}

	public ConsultarSolicitudMB getConsultarMB() {
		return consultarMB;
	}

	public void setConsultarMB(ConsultarSolicitudMB consultarMB) {
		this.consultarMB = consultarMB;
	}
}

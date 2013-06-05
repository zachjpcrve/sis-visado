package com.hildebrando.visado.mb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.general.entities.Feriado;
import com.bbva.general.service.TablaGeneral;
import com.bbva.general.service.TablaGeneralServiceLocator;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.dao.impl.GenericDaoImpl;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsSolicitud;

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
	
	public  void validarSolicitudesVencidas()
	{logger.debug("******** validarSolicitudesVencidas **************");
			int diasUtiles = 0;
			
			GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
			
			filtroSol.add(Restrictions.or(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO, ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02),
										  Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)));
			
			List<TiivsSolicitud> solicitudes = new ArrayList<TiivsSolicitud>();
			
			try {
				solicitudes = solicDAO.buscarDinamico(filtroSol);
				
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.debug("Error al buscar las solicitudes");
			}
			
			GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
			
			List<TiivsMultitabla> lstMultitabla=new ArrayList<TiivsMultitabla>();
			try {
				lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//Se obtiene los dias utiles de la Multitabla
			for (TiivsMultitabla tmp : lstMultitabla) 
			{
				if (tmp.getId().getCodMult().trim().equals(ConstantesVisado.DIAS_FOR_EJECUCION.CODIGO_MULTITABLA_DIAS)
						&& tmp.getId().getCodElem().trim().equals(ConstantesVisado.DIAS_FOR_EJECUCION.CODIGO_MULTITABLA_DIAS_VENCIDOS)) 
				{
					diasUtiles = Integer.valueOf(tmp.getValor2());
					break;
				}
			}
			
			for (TiivsSolicitud tmpSol: solicitudes)
			{
				Date fechaSolicitud = tmpSol.getFechaEstado();

				if (tmpSol.getFechaEstado()!=null)
				{
					Date fechaLimite = aumentarFechaxVen(fechaSolicitud, diasUtiles);

					java.util.Date fechaActual = new java.util.Date();

					if (fechaActual.after(fechaLimite)) 
					{
						logger.info("Se supero el plazo. Cambiar la solicitud a estado vencido");
						
						try {
							actualizarEstadoVencidoSolicitud(tmpSol);
						} catch (Exception e) {
							logger.info("No se pudo cambiar el estado de la solicitud: " + tmpSol.getCodSoli()	+ " a vencida");
							logger.info(e.getStackTrace());
						}
					} 
					else 
					{
						logger.info("No se supero el plazo. El estado de la solicitud se mantiene");
					}
				}
			}
			
		}
	
	public Date aumentarFechaxVen(Date fecha, int nroDias) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);

		Date nuevaFecha = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, nroDias);
		nuevaFecha = cal.getTime();
		return nuevaFecha;
	}
	public  void actualizarEstadoVencidoSolicitud(TiivsSolicitud solicitud) throws Exception {
		logger.info("*********************** actualizarEstadoVencidoSolicitud **************************");

		solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02);
		solicitud.setFechaEstado(new Timestamp(new Date().getTime()));
		solicitud.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_VENCIDO_T02);
		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		service.modificar(solicitud);

		registrarHistorial(solicitud);
		//obtenerHistorialSolicitud();
		//seguimientoMB.busquedaSolicitudes();
	}
	public void registrarHistorial(TiivsSolicitud solicitud) throws Exception {
		SolicitudDao<String, Object> serviceMaxMovi = (SolicitudDao<String, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		String numeroMovimiento = serviceMaxMovi.obtenerMaximoMovimiento(solicitud.getCodSoli());

		int num = 0;
		if (!numeroMovimiento.equals("")) {
			num = Integer.parseInt(numeroMovimiento) + 1;
		} else {
			num = 1;
		}
		numeroMovimiento = num + "";
		TiivsHistSolicitud objHistorial = new TiivsHistSolicitud();
		objHistorial.setId(new TiivsHistSolicitudId(solicitud.getCodSoli(),
				numeroMovimiento));
		objHistorial.setEstado(solicitud.getEstado());
		objHistorial.setNomUsuario("SISTEMA");
		objHistorial.setObs(solicitud.getObs());
		objHistorial.setFecha(new Timestamp(new Date().getTime()));
		objHistorial.setRegUsuario("SISTEMA");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		serviceHistorialSolicitud.insertar(objHistorial);
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

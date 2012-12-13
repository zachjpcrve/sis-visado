package com.hildebrando.visado.mb;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Expression;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.general.entities.Feriado;
import com.bbva.general.service.TablaGeneral;
import com.bbva.general.service.TablaGeneralServiceLocator;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.impl.GenericDaoImpl;

@ManagedBean(name = "admJobs")
@SessionScoped
public class JobsMB {
	public static Logger logger = Logger.getLogger(JobsMB.class);

	public static void cargarFeriados() {
		try {

			// 2 digitos: Departamento
			// 2 digitos: Provincia
			// 3 digitos: Distrito

			Timestamp tstInicio = new Timestamp(new java.util.Date().getTime());
			logger.debug("INICIA PROCESO CARGA FERIADOS: " + tstInicio);

			Feriado[] resultado = obtenerDatosWebService().getFeriadoListado();

			Timestamp tsLatencia = new Timestamp(new java.util.Date().getTime());
			double segundosUtilizadosLat = restarFechas(tstInicio, tsLatencia);
			logger.debug("SE DEMORO EN OBTENER LOS DATOS DEL WEB SERVICE DE FERIADOS: "
					+ segundosUtilizadosLat + " SEGUNDOS");

			for (Feriado feriado : resultado) {		
				
				int anio = feriado.getFecha().get(Calendar.YEAR);				
				int anioActual = Calendar.getInstance().get(Calendar.YEAR);
				
				if (anio >= anioActual) {
					
					if (feriado.getUbigeo() != null) {
						logger.debug("------------------FERIADOS LOCALES------------------------");
						logger.debug("Ind. Feriado: " + feriado.getIndicador());
						logger.debug("Fecha: "
								+ feriado.getFecha().getTime().toGMTString());
						logger.debug("Ubigeo: " + feriado.getUbigeo());
						logger.debug("Descripcion: " + feriado.getDescripcion());
						logger.debug("--------------------------------------------------");
					} else {
						logger.debug("------------------FERIADOS NACIONALES------------------------");
						logger.debug("Ind. Feriado: " + feriado.getIndicador());
						logger.debug("Fecha: "
								+ feriado.getFecha().getTime().toGMTString());
						logger.debug("Descripcion: " + feriado.getDescripcion());
						logger.debug("--------------------------------------------------");
					}

					com.hildebrando.visado.modelo.TiivsFeriado ferid = new com.hildebrando.visado.modelo.TiivsFeriado();
					

					if (feriado.getIndicador().equals("L")) {
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
			logger.debug("TERMINA PROCESO CARGA FERIADOS: " + tstFin);

			double segundosUtilizados = restarFechas(tstInicio, tstFin);
			logger.debug("PROCESO CARGA FERIADOS REALIZADO EN: "
					+ segundosUtilizados + " SEGUNDOS");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static boolean validarSiExisteFeriado(String tabla, String campo,
			Object valor) {
		boolean existe = false;

		if (tabla.equals("feriado")) {
			List<com.hildebrando.visado.modelo.TiivsFeriado> results = new ArrayList<com.hildebrando.visado.modelo.TiivsFeriado>();
			GenericDaoImpl<com.hildebrando.visado.modelo.TiivsFeriado, Integer> feriadoDAO = (GenericDaoImpl<com.hildebrando.visado.modelo.TiivsFeriado, Integer>) SpringInit
					.getApplicationContext().getBean("genericoDao");

			Busqueda filtro = Busqueda
					.forClass(com.hildebrando.visado.modelo.TiivsFeriado.class);
			filtro.add(Expression.eq(campo, valor));

			try {
				results = feriadoDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (results.size() > 0) {
				existe = true;
			}
		}

		return existe;
	}

	@SuppressWarnings({ "unchecked" })
	public static void grabarFeriado(
			com.hildebrando.visado.modelo.TiivsFeriado ferid) {
		GenericDao<com.hildebrando.visado.modelo.TiivsFeriado, Object> feriadoDAO = (GenericDao<com.hildebrando.visado.modelo.TiivsFeriado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		try {
			if (validarSiExisteFeriado("feriado", "fecha",
					ferid.getFecha())) {
				feriadoDAO.modificar(ferid);
			} else {
				feriadoDAO.insertar(ferid);
			}
			// FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			logger.debug("Se inserto feriado exitosamente!");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage("No registro",
							"No se registro el feriado desde el webservice"));
			logger.debug("No registro el feriado!");
		}
	}

	public static boolean validarSiExiste(String tabla, Object obj) {
		boolean existe = false;
		if (tabla.equals("feriado")) {
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

			Busqueda filtro = Busqueda
					.forClass(com.hildebrando.visado.modelo.TiivsFeriado.class);
			filtro.add(Expression.eq("fecha", feridTMP.getFecha()));			
			filtro.add(Expression.eq("indicador", feridTMP.getIndicador()));

			try {
				results = feriadoDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (results.size() > 0) {
				existe = true;
			}
		}
		return existe;
	}

	public static TablaGeneral obtenerDatosWebService() {
		TablaGeneral tbGeneralWS = null;
		try {
			
			TablaGeneralServiceLocator tablaGeneralServiceLocator = new TablaGeneralServiceLocator();			
			
			tbGeneralWS = tablaGeneralServiceLocator.getTablaGeneral();
		} catch (ServiceException e) {
			e.printStackTrace();
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

}

package com.hildebrando.visado.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsMultitabla;

public class ClasificacionService {
	public static Logger logger = Logger.getLogger(ClasificacionService.class);

	@SuppressWarnings("unchecked")
	public <E> String obtenerMaximo() {
		logger.info("ClasificacionService : obtenerMaximo");
		List<TiivsMultitabla> secuencial = new ArrayList<TiivsMultitabla>();
		String contador = "0";

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			secuencial = service.buscarDinamico(filtro.add(
					Restrictions.eq("id.codMult",
							ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION))
					.setProjection(Projections.rowCount()));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			contador = parse.get(0).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ClasificacionService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}

		return contador;
	}
	
	@SuppressWarnings("unchecked")
	public List<TiivsMultitabla> listarClasificaciones() {
		logger.info("ClasificacionService : listarClasificaciones");
		List<TiivsMultitabla> clasificaciones = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			clasificaciones = service
					.buscarDinamico(filtro.add(Restrictions.eq("id.codMult",
							ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION)));
			for (int i = 0; i < clasificaciones.size(); i++) {
				
				if (clasificaciones.get(i).getValor2()
						.equals(ConstantesVisado.VALOR2_ESTADO_ACTIVO)) {
					clasificaciones.get(i).setValor2(
							ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				} else {
					clasificaciones.get(i).setValor2(
							ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ClasificacionService : listarClasificaciones: "
					+ ex.getLocalizedMessage());
		}
		return clasificaciones;
	}
	
	@SuppressWarnings("unchecked")
	public void registrar(TiivsMultitabla clasificacion) {
		logger.info("ClasificacionService : registrar");
		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			service.insertarMerge(clasificacion);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ClasificacionService : registrar: "
					+ ex.getLocalizedMessage());
		}

	}
	@SuppressWarnings("unchecked")
	public List<TiivsMultitabla> editarClasificacion(String codMultitabla,
			String codElemento) {
		
		logger.info("ClasificacionService : editarClasificacion");
		List<TiivsMultitabla> clasificacionEditar = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			clasificacionEditar = service.buscarDinamico(filtro.add(
					Restrictions.eq("id.codMult", codMultitabla)).add(
					Restrictions.eq("id.codElem", codElemento)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ClasificacionService : editarClasificacion: "
					+ ex.getLocalizedMessage());
		}
		return clasificacionEditar;
	}
}
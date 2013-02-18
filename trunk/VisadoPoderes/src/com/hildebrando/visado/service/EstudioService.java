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
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsMultitabla;

public class EstudioService {
	public static Logger logger = Logger.getLogger(EstudioService.class);
	String estadoString = "1";
	char estadoChar = estadoString.charAt(0);
	Character estadoSi = Character.valueOf(estadoChar);
	String estadoNoString = "0";
	char estadoNoChar = estadoNoString.charAt(0);
	Character estadoNo = Character.valueOf(estadoNoChar);

	public List<TiivsEstudio> listarEstudios() {
		logger.info("EstudioService : listarEstudios");
		List<TiivsEstudio> estudios = new ArrayList<TiivsEstudio>();
		/*******************************/

		/**********************/
		GenericDao<TiivsEstudio, Object> service = (GenericDao<TiivsEstudio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsEstudio.class);
		try {
			estudios = service.buscarDinamico(filtro);

			for (int i = 0; i < estudios.size(); i++) {
				if (estudios.get(i).getActivo() == estadoSi) {
					estudios.get(i).setDesActivo(
							ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				} else {
					estudios.get(i).setDesActivo(
							ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("EstudioService : listarEstudios: "
					+ ex.getLocalizedMessage());
		}
		return estudios;
	}

	public <E> String obtenerMaximo() {
		logger.info("EstudioService : obtenerMaximo");
		List<TiivsEstudio> secuencial = new ArrayList<TiivsEstudio>();
		String contador = "0";

		GenericDao<TiivsEstudio, Object> service = (GenericDao<TiivsEstudio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsEstudio.class);
		try {
			secuencial = service.buscarDinamico(filtro
					.setProjection(Projections.max("codEstudio")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			contador = parse.get(0).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("EstudioService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}

		return contador;
	}

	public void registrar(TiivsEstudio estudio) {
		logger.info("EstudioService : registrar");
		GenericDao<TiivsEstudio, Object> service = (GenericDao<TiivsEstudio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			if (estudio.getDesActivo().equals(
					ConstantesVisado.VALOR2_ESTADO_ACTIVO)) {
				estudio.setActivo(estadoSi);
			} else {
				estudio.setActivo(estadoNo);
			}
			service.insertarMerge(estudio);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("EstudioService : registrar: "
					+ ex.getLocalizedMessage());
		}
	}

	public List<TiivsEstudio> editarEstudio(String codEstudio) {
		logger.info("EstudioService : editarEstudio");
		List<TiivsEstudio> estudioEditar = new ArrayList<TiivsEstudio>();

		GenericDao<TiivsEstudio, Object> service = (GenericDao<TiivsEstudio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsEstudio.class);
		try {
			estudioEditar = service.buscarDinamico(filtro.add(Restrictions.eq(
					"codEstudio", codEstudio)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ClasificacionService : editarClasificacion: "
					+ ex.getLocalizedMessage());
		}
		return estudioEditar;
	}

}

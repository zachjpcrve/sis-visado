package com.hildebrando.visado.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;

public class NivelService {
	public static Logger logger = Logger.getLogger(NivelService.class);
	
	public List<TiivsMultitabla> listarMonedas() {
		logger.info("NivelService : listarMonedas");
		List<TiivsMultitabla> monedas = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);

		try {
			monedas = service.buscarDinamico(filtro.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_MONEDA)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : listarMonedas: "
					+ ex.getLocalizedMessage());
		}
		return monedas;
	}

	public List<TiivsNivel> listarNiveles(List<TiivsMultitabla> moneda) {
		logger.info("NivelService : listarNiveles");
		List<TiivsNivel> niveles = new ArrayList<TiivsNivel>();
		
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);

		try {
			niveles = service.buscarDinamico(filtro.addOrder(Order.asc("id")));
			
			for (int i = 0; i < niveles.size(); i++) {
				if(niveles.get(i).getEstado().intValue() == 1){
					niveles.get(i).setDesEstado(ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				}else{
					niveles.get(i).setDesEstado(ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
				for(int j = 0; j < moneda.size(); j++){
					if(niveles.get(i).getMoneda().equals(moneda.get(j).getId().getCodElem())){
						niveles.get(i).setMoneda(moneda.get(j).getValor1());
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : listarNiveles: "
					+ ex.getLocalizedMessage());
		}
		return niveles;
	}

	@SuppressWarnings("unchecked")
	public <E> String obtenerMaximo() {
		logger.info("NivelService : obtenerMaximo");
		List<TiivsNivel> secuencial = new ArrayList<TiivsNivel>();
		String contador = "0";

		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		try {
			secuencial = service.buscarDinamico(filtro.setProjection(Projections.max("codNiv")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			contador = parse.get(0).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}

		return contador;
	}

	public void registrar(TiivsNivel tiivsNivel) {
		logger.info("NivelService : registrar");
		try{
			GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			
			service.insertarMerge(tiivsNivel);
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("NivelService : registrar: "
					+ e.getLocalizedMessage());
		}
		
	}

	public <E> int obtenerSecuencialNivel() {
		logger.info("NivelService : obtenerSecuencialNivel");
		List<TiivsNivel> secuencial = new ArrayList<TiivsNivel>();
		int seq = 0;
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		try {
			secuencial = service.buscarDinamico(filtro.setProjection(Projections.max("id")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			seq = (Integer) parse.get(0);
			seq = seq + 1;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}
		return seq;
	}

	public <E> int obtenerRangoInicio(String codElem) {
		logger.info("NivelService : obtenerSecuencialNivel");
		List<TiivsNivel> rInicio = new ArrayList<TiivsNivel>();
		int rangoInicio = 0;
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		try {
			rInicio = service.buscarDinamico(filtro.add(Restrictions.eq("moneda", codElem)).setProjection(Projections.max("rangoFin")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) rInicio;
			rangoInicio = (Integer) parse.get(0);
			rangoInicio = rangoInicio + 1;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}
		return rangoInicio;
	}

	public <E> int compararDesNivel(String desNivel) {
		logger.info("NivelService : obtenerSecuencialNivel");
		List<TiivsNivel> rInicio = new ArrayList<TiivsNivel>();
		int contador = 0; 
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		try {
			rInicio = service.buscarDinamico(filtro.add(Restrictions.eq("desNiv", desNivel)).setProjection(Projections.count("desNiv")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) rInicio;
			contador = Integer.parseInt(parse.get(0).toString());
		//	contador = (Integer) parse.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : obtenerSecuencialNivel: "
					+ ex.getLocalizedMessage());
		}
		return contador;
	}

	public List<TiivsNivel> editarNivel(String codNiv) {
		logger.info("NivelService : editarNivel");
		List<TiivsNivel> editarNiv = new ArrayList<TiivsNivel>();
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		
		try{
			editarNiv = service.buscarDinamico(filtro.add(Restrictions.eq("codNiv", codNiv)));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("NivelService : editarNivel: "
					+ ex.getLocalizedMessage());
		}
		return editarNiv;
	}


}

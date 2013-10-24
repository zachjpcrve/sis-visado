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
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;

public class OperacionBancariaService {
	public static Logger logger = Logger.getLogger(OperacionBancariaService.class);
	
	@SuppressWarnings("unchecked")
	public <E> String obtenerMaximo() {
		logger.info("DocumentoService : obtenerMaximo");
		List<TiivsOperacionBancaria> secuencial = new ArrayList<TiivsOperacionBancaria>();
		String contador = "0";

		GenericDao<TiivsOperacionBancaria, Object> service = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOperacionBancaria.class);
		try {
			secuencial = service.buscarDinamico(filtro
					.setProjection(Projections.max("codOperBan")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			contador = parse.get(0).toString();
		} catch (Exception ex) {
			logger.error("DocumentoService : obtenerMaximo: "+ ex.getLocalizedMessage());
		}
		return contador;
	}
	
	@SuppressWarnings("unchecked")
	public List<TiivsOperacionBancaria> listarOperaciones() 
	{
		logger.info("OperacionBancariaService : listarOperaciones");
		List<TiivsOperacionBancaria> operaciones = new ArrayList<TiivsOperacionBancaria>();

		GenericDao<TiivsOperacionBancaria, Object> service = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOperacionBancaria.class);
		
		try 
		{
				operaciones = service.buscarDinamico(filtro);
				for (int i = 0; i < operaciones.size(); i++) 
			{					
				if (operaciones.get(i).getActivo().equals(ConstantesVisado.VALOR2_ESTADO_ACTIVO)) 
				{
					operaciones.get(i).setActivo(ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				} 
				else 
				{
					operaciones.get(i).setActivo(ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
			}
		} catch (Exception ex) {
			logger.error("ClasificacionService : listarClasificaciones: " + ex.getLocalizedMessage());
		}
		return operaciones;
	}
	
	@SuppressWarnings("unchecked")
	public void registrar(TiivsOperacionBancaria operacion) {
		logger.info("ClasificacionService : registrar");
		GenericDao<TiivsOperacionBancaria, Object> service = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		try {
			service.insertarMerge(operacion);
		} catch (Exception ex) {
			logger.error("OperacionBancariaService : registrar: " + ex.getLocalizedMessage());
		}

	}
	@SuppressWarnings("unchecked")
	public List<TiivsOperacionBancaria> editarOperacion(String codElemento) {
		
		logger.info("OperacionBancariaService : editarOperacion");
		List<TiivsOperacionBancaria> operacionEditar = new ArrayList<TiivsOperacionBancaria>();

		GenericDao<TiivsOperacionBancaria, Object> service = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOperacionBancaria.class);
		try {
			operacionEditar = service.buscarDinamico(filtro.add(
					Restrictions.eq("codOperBan", codElemento)));
		} catch (Exception ex) {
			logger.error("ClasificacionService : editarClasificacion: "
					+ ex.getLocalizedMessage());
		}
		return operacionEditar;
	}
}

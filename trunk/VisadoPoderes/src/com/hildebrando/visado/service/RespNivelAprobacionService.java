package com.hildebrando.visado.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsNivel;

public class RespNivelAprobacionService {

	public static Logger logger = Logger.getLogger(RespNivelAprobacionService.class);
	
	public String obtenerDesNivel(String codNivel) {
		logger.info("RespNivelAprobacionService : obtenerDesNivel");
		List<TiivsNivel> nivel = new ArrayList<TiivsNivel>();
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		
		try{
			nivel = service.buscarDinamico(filtro.add(Restrictions.eq("codNiv", codNivel)));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("RespNivelAprobacionService : obtenerDesNivel: "
					+ ex.getLocalizedMessage());
		}
		return nivel.get(0).getDesNiv();
	}

	public String obtenerDesEstado(String codNivel) {
		logger.info("RespNivelAprobacionService : obtenerDesEstado");
		String estado = null;
		List<TiivsNivel> nivel = new ArrayList<TiivsNivel>();
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		
		try{
			nivel = service.buscarDinamico(filtro.add(Restrictions.eq("codNiv", codNivel)));

			for (int i = 0; i < nivel.size(); i++) {
				if(nivel.get(i).getEstado().intValue() == 1){
					nivel.get(i).setDesEstado(ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				}else{
					nivel.get(i).setDesEstado(ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
			}
			estado = nivel.get(0).getDesEstado();
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("RespNivelAprobacionService : obtenerDesEstado: "
					+ ex.getLocalizedMessage());
		}
		return estado;
	}
	

}

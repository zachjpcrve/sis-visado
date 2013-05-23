package com.hildebrando.visado.service;

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
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;

public class MultiTablaService {
	public static Logger logger = Logger.getLogger(AbogadoService.class);

	public List<TiivsMultitabla> listarMultiTabla(String codigoSubTabla,
			String codigoElemento) {

		logger.info("MultiTablaService : listarMultiTabla");
		List<TiivsMultitabla> multiTablas = new ArrayList<TiivsMultitabla>();

		String codCriterio = "";

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		
		if(!codigoSubTabla.equals("")){
			filtro.add(Restrictions.eq("id.codMult", codigoSubTabla));
		}
		
		if(!codigoElemento.equals("")){
			filtro.add(Restrictions.eq("id.codElem", codigoElemento));
		}
		
		filtro.addOrder(Order.asc("id"));

		try {

			multiTablas = service.buscarDinamico(filtro);

		} catch (Exception ex) {

			logger.error("MultiTablaService : listarMultiTabla: ", ex);
		}

		return multiTablas;

	}

	public List<String> listarCodigoMultiTabla() {

		List<TiivsMultitabla> listaMultiTabla = this.listarMultiTabla("", "");

		List<String> listaCodigoMultiTabla = new ArrayList<String>();

		String codigo = "";
		for (TiivsMultitabla mult : listaMultiTabla) {
			if (!codigo.equals(mult.getId().getCodMult())) {
				listaCodigoMultiTabla.add(mult.getId().getCodMult());
				codigo = mult.getId().getCodMult();
			}
		}

		return listaCodigoMultiTabla;

	}

	public  boolean registrar(TiivsMultitabla multiTabla) {
		
		boolean result = false;
		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
	
		try {			
			service.insertarMerge(multiTabla);
			result =  true;
		} catch (Exception ex) {
			result = false;
			logger.error("registrar : ", ex);
		}
		
		return result;
	}

	public boolean eliminarMultiTabla(TiivsMultitabla multiTabla) {
		
		boolean result = false;
		
		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			service.eliminar(multiTabla);
			result = true;
		} catch (Exception ex) {
			result = false;
			logger.error("eliminarMultiTabla : " , ex);
		}
		
		return result;
		
	}

	
}

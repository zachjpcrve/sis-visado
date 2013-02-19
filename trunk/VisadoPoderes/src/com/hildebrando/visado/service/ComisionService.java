package com.hildebrando.visado.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import sun.print.resources.serviceui;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsMultitabla;

public class ComisionService {
	public static Logger logger = Logger.getLogger(ComisionService.class);

	public List<TiivsMultitabla> listarComisiones() {
		logger.info("DocumentoService : listarDocumentos");
		List<TiivsMultitabla> comisiones = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			comisiones = service
					.buscarDinamico(filtro.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_COMISION)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
		return comisiones;
	}

	public void Actualizar(TiivsMultitabla comision) {
		logger.info("DocumentoService : Actualizar");
		try{
			GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			
			service.modificar(comision);
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("DocumentoService : Actualizar: "
					+ e.getLocalizedMessage());
		}
		
	}
}

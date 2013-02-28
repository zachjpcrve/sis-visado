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
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.dao.impl.SolicitudDaoImpl;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;

public class DelegadosService {
	public static Logger logger = Logger.getLogger(DelegadosService.class);

	public List<TiivsNivel> listarNiveles() {
		logger.info("DelegadosService : listarNiveles ");
		List<TiivsNivel> niveles = new ArrayList<TiivsNivel>();
		SolicitudDao<TiivsNivel, Object> service = (SolicitudDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("solicitudEspDao");
		try {
			niveles = service.listarNivelesDistinct();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
		return niveles;
	}

	public List<TiivsMiembro> obtenerDatosMiembro(String codRegistro) {
		logger.info("DelegadosService : obtenerDatosMiembro ");
		List<TiivsMiembro> miembro = new ArrayList<TiivsMiembro>();

		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);
		try {
			miembro = service.buscarDinamico(filtro.add(Restrictions.eq("codMiembro", codRegistro)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosService : obtenerDatosMiembro: "
					+ ex.getLocalizedMessage());
		}
		return miembro;
	}

	public <E> int obtenerGrupo() {
		logger.info("DelegadosService : obtenerDatosMiembro ");
		List<TiivsMiembroNivel> grupo = new ArrayList<TiivsMiembroNivel>();
		int grupoI = 0;
		GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembroNivel.class);
		try {
			grupo = service.buscarDinamico(filtro.setProjection(Projections.max("grupo")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) grupo;
			grupoI = (Integer) parse.get(0);
			grupoI = grupoI + 1;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}
		return grupoI;
	}

	public void registrarAgrupacion(TiivsMiembroNivel tiivsMiembroNivel) {
		logger.info("DelegadosService : registrarAgrupacion ");
		try{
			GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			
			service.insertar(tiivsMiembroNivel);
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("DocumentoService : registrarAgrupacion: "
					+ e.getLocalizedMessage());
		}
	}

	public <E> int obtenerId() {
		logger.info("DelegadosService : obtenerId ");
		List<TiivsMiembroNivel> id = new ArrayList<TiivsMiembroNivel>();
		int grupoI = 0;
		GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembroNivel.class);
		try {
			id = service.buscarDinamico(filtro.setProjection(Projections.max("id")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) id;
			grupoI = (Integer) parse.get(0);
			grupoI = grupoI + 1;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("NivelService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}
		return grupoI;
	}

}

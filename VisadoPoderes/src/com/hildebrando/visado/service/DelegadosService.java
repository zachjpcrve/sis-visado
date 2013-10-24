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
			logger.error("DocumentoService : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
		return niveles;
	}

	public List<TiivsMiembro> obtenerDatosMiembro(String codRegistro) {
		//logger.info("DelegadosService : obtenerDatosMiembro ");
		List<TiivsMiembro> miembro = new ArrayList<TiivsMiembro>();
		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);
		try {
			miembro = service.buscarDinamico(filtro.add(Restrictions.eq("codMiembro", codRegistro)));
		} catch (Exception ex) {
			logger.error("DelegadosService : obtenerDatosMiembro: "+ ex.getLocalizedMessage());
		}
		return miembro;
	}

	public <E> int obtenerGrupo(String codNiv) {
		logger.info("DelegadosService : obtenerDatosMiembro ");
		List<TiivsMiembroNivel> grupo = new ArrayList<TiivsMiembroNivel>();
		List<TiivsMiembroNivel> contador = new ArrayList<TiivsMiembroNivel>();
		int grupoI = 0;
		int grupoCount = 0;
		GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembroNivel.class);
		try {
			
			contador = service.buscarDinamico(filtro.add(Restrictions.eq("codNiv", codNiv)).setProjection(Projections
					.count("grupo")));
			List<E> parseCount = new ArrayList<E>();
			parseCount = (List<E>) contador;
			grupoCount = Integer.parseInt(parseCount.get(0).toString());
			if(grupoCount > 0){
				grupo = service.buscarDinamico(filtro.add(Restrictions.eq("codNiv", codNiv)).setProjection(Projections
						.max("grupo")));

				List<E> parse = new ArrayList<E>();
				parse = (List<E>) grupo;
				grupoI = (Integer) parse.get(0);
				grupoI = grupoI + 1;
			}else{
				grupoI = 1;
			}

		} catch (Exception ex) {
			logger.error("NivelService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}
		return grupoI;
	}

	public void registrarAgrupacion(TiivsMiembroNivel tiivsMiembroNivel) {
		logger.info("DelegadosService : registrarAgrupacion ");
		try {
			GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");

			service.insertar(tiivsMiembroNivel);

		} catch (Exception e) {
			logger.error("DocumentoService : registrarAgrupacion: "+ e.getLocalizedMessage());
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
			id = service.buscarDinamico(filtro.setProjection(Projections
					.max("id")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) id;
			grupoI = (Integer) parse.get(0);
			grupoI = grupoI + 1;

		} catch (Exception ex) {
			logger.error("NivelService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}
		return grupoI;
	}

	public List<TiivsMiembroNivel> editarAgrupacion(String codigoGrupo, String desNivel) {
		logger.info("DelegadosService : editarAgrupacion " + desNivel);
		List<TiivsMiembroNivel> delegadosEditar = new ArrayList<TiivsMiembroNivel>();
		int grupo = 0;
		grupo = Integer.parseInt(codigoGrupo);
		String codigoNivel = null;
		
		codigoNivel = obtenerCodNivel(desNivel);
		
		GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembroNivel.class);
		try{
			delegadosEditar = service.buscarDinamico(filtro.add(Restrictions.eq("grupo", grupo)).add(Restrictions.eq("codNiv", codigoNivel)).add(Restrictions.eq("estadoMiembro", "1")));
			
		}catch(Exception ex){
			logger.error("DelegadosService : editarAgrupacion: "
					+ ex.getLocalizedMessage());
		}
		return delegadosEditar;
	}

	public String obtenerCodNivel(String desNivel) {
		logger.info("DelegadosService : obtenerCodNivel " +desNivel);
		List<TiivsNivel> lstCodNivel = new ArrayList<TiivsNivel>();
		String codNivel = null;
		
		GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
		
		try{
			lstCodNivel = service.buscarDinamico(filtro.add(Restrictions.eq("desNiv", desNivel)));
			logger.info("lstCodNivel.size() " +lstCodNivel.size());
			codNivel = lstCodNivel.get(0).getCodNiv();
		}catch(Exception ex){
			logger.error("DelegadosService : obtenerCodNivel: "
					+ ex.getLocalizedMessage());
		}
		return codNivel;
	}

	public void actualizarAgrupacion(TiivsMiembroNivel tiivsMiembroNivel) {
		logger.info("DelegadosService : actualizarAgrupacion ");
		try {
			GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");

			service.insertarMerge(tiivsMiembroNivel);

		} catch (Exception e) {
			logger.error("DocumentoService : actualizarAgrupacion: "
					+ e.getLocalizedMessage());
		}
		
	}

	public List<TiivsMiembroNivel> estadoAgrupacion(String grupo, String nivel) {
		logger.info("DelegadosService : estadoAgrupacion ");
		List<TiivsMiembroNivel> delegadosEstado = new ArrayList<TiivsMiembroNivel>();
		int grupo2 = 0;
		grupo2 = Integer.parseInt(grupo);
		String codigoNivel = null;
		codigoNivel = obtenerCodNivel(nivel);
		
		GenericDao<TiivsMiembroNivel, Object> service = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembroNivel.class);
		try{
			delegadosEstado = service.buscarDinamico(filtro.add(Restrictions.eq("grupo", grupo2)).add(Restrictions.eq("codNiv", codigoNivel)));
			
		}catch(Exception ex){
			logger.error("DelegadosService : estadoAgrupacion: "
					+ ex.getLocalizedMessage());
		}
		return delegadosEstado;
	}

}

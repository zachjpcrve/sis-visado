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
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

public class TiposSolicitudService {
	public static Logger logger = Logger.getLogger(DocumentoService.class);

	@SuppressWarnings("unchecked")
	public void registrar(TiivsTipoSolicitud tipoSolicitud) {
		logger.info("DocumentoService : registrar");
		GenericDao<TiivsTipoSolicitud, Object> service = (GenericDao<TiivsTipoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			service.insertarMerge(tipoSolicitud);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : registrar: "
					+ ex.getLocalizedMessage());
		}

	}

	@SuppressWarnings("unchecked")
	public List<TiivsTipoSolicitud> listarTiposSolicitud(/*List<TiivsMultitabla> tipoDocumento*/) {
		logger.info("DocumentoService : listarTiposSolicitud");
		List<TiivsTipoSolicitud> documentos = new ArrayList<TiivsTipoSolicitud>();

		GenericDao<TiivsTipoSolicitud, Object> service = (GenericDao<TiivsTipoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTipoSolicitud.class);
		try {
			documentos = service.buscarDinamico(filtro);
			
			for(TiivsTipoSolicitud solicitud:documentos){
				if(Character.valueOf(solicitud.getActivo()).toString().
							compareTo(ConstantesVisado.VALOR2_ESTADO_ACTIVO)==0){
					solicitud.setDesActivo(ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				}else{
					solicitud.setDesActivo(ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
			}
		
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : listarTiposSolicitud: "
					+ ex.getLocalizedMessage());
		}
		return documentos;
	}

	@SuppressWarnings("unchecked")
	public <E> String obtenerMaximo() {
		logger.info("DocumentoService : obtenerMaximo");
		List<TiivsTipoSolicitud> secuencial = new ArrayList<TiivsTipoSolicitud>();
		String contador = "0";

		GenericDao<TiivsTipoSolicitud, Object> service = (GenericDao<TiivsTipoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTipoSolicitud.class);
		try {
			secuencial = service.buscarDinamico(filtro
					.setProjection(Projections.max("codTipSolic")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			contador = parse.get(0).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}

		return contador;
	}

	/*@SuppressWarnings("unchecked")
	public List<TiivsMultitabla> cargarComboTipoDocumento() {
		logger.info("DocumentoService : cargarComboTipoDocumento");
		List<TiivsMultitabla> documentos = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			documentos = service.buscarDinamico(filtro.add(Restrictions.eq(
					"id.codMult",
					ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOCUMENTO)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : cargarComboTipoDocumento: "
					+ ex.getLocalizedMessage());
		}
		return documentos;
	}*/

	@SuppressWarnings("unchecked")
	public List<TiivsTipoSolicitud> editarDocumento(
			String codElemento) {
		
		logger.info("DocumentoService : editarDocumento");
		List<TiivsTipoSolicitud> documentoEditar = new ArrayList<TiivsTipoSolicitud>();

		GenericDao<TiivsTipoSolicitud, Object> service = (GenericDao<TiivsTipoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTipoSolicitud.class);
		try {
			documentoEditar = service.buscarDinamico(filtro.add(
					Restrictions.eq("codTipSolic", codElemento)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : editarDocumento: "
					+ ex.getLocalizedMessage());
		}
		return documentoEditar;
	}

	/*public String obtenerCodPersonaJuridica() {
				
		String sCodRazSocial = "";

		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);

		filtroMultitabla.add(Restrictions.eq("id.codMult",
				ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC));

		List<TiivsMultitabla> listaMultiTabla = new ArrayList<TiivsMultitabla>();

		try {
			listaMultiTabla = multiDAO.buscarDinamico(filtroMultitabla);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA
					+ "de multitablas: " + e);
		}

		for (TiivsMultitabla multi : listaMultiTabla) {
			if (multi.getValor2() != null && multi.getValor2().equals("1")) {
				if (multi.getValor6() != null && multi.getValor6().equals("1")) {
					sCodRazSocial = multi.getId().getCodElem();
					break;
				}
			}

		}
		return sCodRazSocial;
		
	}*/

}

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
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;

public class DocumentoTiivsService {
	public static Logger logger = Logger.getLogger(DocumentoService.class);

	@SuppressWarnings("unchecked")
	public <E> String obtenerMaximo() {
		logger.info("DocumentoTiivsService : obtenerMaximo");
		List<TiivsDocumento> secuencial = new ArrayList<TiivsDocumento>();
		String contador = "0";

		GenericDao<TiivsDocumento, Object> service = (GenericDao<TiivsDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsDocumento.class);
		try {
			secuencial = service.buscarDinamico(filtro
					.setProjection(Projections.max("codDocumento")));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			contador = parse.get(0).toString();
		} catch (Exception ex) {
			logger.error("DocumentoTiivsService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}

		return contador;
	}

	@SuppressWarnings("unchecked")
	public List<TiivsDocumento> listarDocumentos() {
		logger.info("DocumentoTiivsService : listarDocumentos");
		List<TiivsDocumento> documentos = new ArrayList<TiivsDocumento>();

		GenericDao<TiivsDocumento, Object> service = (GenericDao<TiivsDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsDocumento.class);
		try {
			documentos = service.buscarDinamico(filtro);
			for (int i = 0; i < documentos.size(); i++) {

				if (documentos.get(i).getActivo()
						.equals(ConstantesVisado.VALOR2_ESTADO_ACTIVO)) {
					documentos.get(i).setActivo(
							ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				} else {
					documentos.get(i).setActivo(
							ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
			}
		} catch (Exception ex) {
			logger.error("DocumentoTiivsService : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
		return documentos;
	}

	public void registrar(TiivsDocumento documento) {
		logger.info("DocumentoTiivsService : registrar");
		GenericDao<TiivsDocumento, Object> service = (GenericDao<TiivsDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		try {
			documento.setDescripcion(documento.getDescripcion().toUpperCase());
			documento.setNombre(documento.getNombre().toUpperCase());
			documento.setFormato(documento.getFormato().toUpperCase());
			documento.setCodBarra(documento.getCodBarra().toUpperCase());
			service.insertarMerge(documento);
		} catch (Exception ex) {
			logger.error("DocumentoTiivsService : registrar: "+ ex.getLocalizedMessage());
		}
	}

	public List<TiivsDocumento> editarDocumento(String codDocumento) {
		logger.info("DocumentoTiivsService : editarDocumento");
		List<TiivsDocumento> documentoEditar = new ArrayList<TiivsDocumento>();

		GenericDao<TiivsDocumento, Object> service = (GenericDao<TiivsDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsDocumento.class);
		try {
			documentoEditar = service.buscarDinamico(filtro.add(
					Restrictions.eq("codDocumento", codDocumento)));
		} catch (Exception ex) {
			logger.error("DocumentoTiivsService : editarDocumento: "+ ex.getLocalizedMessage());
		}
		return documentoEditar;
	}

	public <E> boolean validarNombre(String nombre) {
		logger.info("DocumentoService : validarRegistro");
		boolean validacion = false;
		int contador = 0;

		List<TiivsDocumento> existe = new ArrayList<TiivsDocumento>();
		GenericDao<TiivsDocumento, Object> service = (GenericDao<TiivsDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsDocumento.class);
		try {
			existe = service.buscarDinamico(filtro
					.add(Restrictions.eq("nombre", nombre))
					.setProjection(Projections.rowCount()));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) existe;
			contador = Integer.parseInt(parse.get(0).toString());
			if (contador == 0) {
				validacion = true;
			} else {
				validacion = false;
			}

		} catch (Exception ex) {
			logger.error("DocumentoService : obtenerMaximo: "+ ex.getLocalizedMessage());
		}

		return validacion;
	}
}

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
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

public class DocumentoService<E> {
	public static Logger logger = Logger.getLogger(DocumentoService.class);
	String estadoString = "1";
	char estadoChar = estadoString.charAt(0);
	Character estadoSi = Character.valueOf(estadoChar);
	String estadoNoString = "0";
	char estadoNoChar = estadoNoString.charAt(0);
	Character estadoNo = Character.valueOf(estadoNoChar);

	@SuppressWarnings("unchecked")
	public void registrar(TiivsTipoSolicDocumento documento) {
		logger.info("DocumentoService : registrar");
		GenericDao<TiivsTipoSolicDocumento, Object> service = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		documento.setTiivsDocumento(new TiivsDocumento(documento.getId()
				.getCodDoc()));
		documento.setTiivsTipoSolicitud(new TiivsTipoSolicitud(documento
				.getId().getCodTipoSolic()));

		if (documento.getDesActivo().equals("1")) {
			documento.setActivo(estadoSi);
		} else {
			documento.setActivo(estadoNo);
		}
		try {
			service.insertarMerge(documento);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : registrar: "
					+ ex.getLocalizedMessage());
		}

	}
	
	@SuppressWarnings("unchecked")
	public void actualizar(TiivsTipoSolicDocumento documento) {
		logger.info("DocumentoService : actualizar");
		GenericDao<TiivsTipoSolicDocumento, Object> service = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		documento.setTiivsDocumento(new TiivsDocumento(documento.getId()
				.getCodDoc()));
		documento.setTiivsTipoSolicitud(new TiivsTipoSolicitud(documento
				.getId().getCodTipoSolic()));

		if (documento.getDesActivo().equals("1")) {
			documento.setActivo(estadoSi);
		} else {
			documento.setActivo(estadoNo);
		}
		try {
			service.modificar(documento);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : actualizar: "
					+ ex.getLocalizedMessage());
		}

	}

	@SuppressWarnings("unchecked")
	public List<TiivsTipoSolicDocumento> listarDocumentos() {
		logger.info("DocumentoService : listarDocumentos");
		List<TiivsTipoSolicDocumento> documentos = new ArrayList<TiivsTipoSolicDocumento>();

		GenericDao<TiivsTipoSolicDocumento, Object> service = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTipoSolicDocumento.class);
		try {
			documentos = service.buscarDinamico(filtro.addOrder(Order.asc("id.codTipoSolic")));
			for (int i = 0; i < documentos.size(); i++) {

				if (documentos.get(i).getObligatorio()
						.equals(ConstantesVisado.VALOR2_ESTADO_ACTIVO)) {
					documentos.get(i).setObligatorio(
							ConstantesVisado.VALOR4_OBLIGATORIO_SI_LISTA);
				} else {
					documentos.get(i).setObligatorio(
							ConstantesVisado.VALOR4_OBLIGATORIO_NO_LISTA);
				}

				if (documentos.get(i).getActivo() == estadoSi) {
					documentos.get(i).setDesActivo(
							ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				} else {
					documentos.get(i).setDesActivo(
							ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
			}
			/*
			 * for(int j = 0; j < tipoDocumento.size(); j++){
			 * if(documentos.get(i
			 * ).getValor3().equals(tipoDocumento.get(j).getId().getCodElem())){
			 * documentos.get(i).setValor3(tipoDocumento.get(j).getValor1()); }
			 * }
			 * 
			 * }
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
		return documentos;
	}

	@SuppressWarnings("unchecked")
	public <E> String obtenerMaximo() {
		logger.info("DocumentoService : obtenerMaximo");
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
			ex.printStackTrace();
			logger.error("DocumentoService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}

		return contador;
	}

	@SuppressWarnings("unchecked")
	public List<TiivsTipoSolicitud> cargarComboTipoDocumento() {
		logger.info("DocumentoService : cargarComboTipoDocumento");
		List<TiivsTipoSolicitud> documentos = new ArrayList<TiivsTipoSolicitud>();

		GenericDao<TiivsTipoSolicitud, Object> service = (GenericDao<TiivsTipoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTipoSolicitud.class);
		try {
			documentos = service.buscarDinamico(filtro.add(Restrictions.eq(
					"activo", estadoSi)));

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : cargarComboTipoDocumento: "
					+ ex.getLocalizedMessage());
		}
		return documentos;
	}

	@SuppressWarnings("unchecked")
	public List<TiivsDocumento> cargarComboDocumento() {
		logger.info("DocumentoService : cargarComboDocumento");
		List<TiivsDocumento> listaDocumentos = new ArrayList<TiivsDocumento>();
		GenericDao<TiivsDocumento, Object> service = (GenericDao<TiivsDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsDocumento.class);

		try {
			listaDocumentos = service.buscarDinamico(filtro/*
															 * .add(Restrictions.
															 * eq( "activo",
															 * estadoSi))
															 */);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : cargarComboDocumento: "
					+ ex.getLocalizedMessage());
		}

		return listaDocumentos;
	}

	@SuppressWarnings("unchecked")
	public List<TiivsTipoSolicDocumento> editarDocumento(String codDocumento,
			String codTipoSolicitud) {

		logger.info("DocumentoService : editarDocumento");
		List<TiivsTipoSolicDocumento> documentoEditar = new ArrayList<TiivsTipoSolicDocumento>();

		GenericDao<TiivsTipoSolicDocumento, Object> service = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTipoSolicDocumento.class);
		try {
			documentoEditar = service.buscarDinamico(filtro.add(
					Restrictions.eq("id.codDoc", codDocumento)).add(
					Restrictions.eq("id.codTipoSolic", codTipoSolicitud)));
			for(int i = 0; i< documentoEditar.size(); i ++){
				if(documentoEditar.get(i).getActivo() == estadoSi){
					documentoEditar.get(i).setDesActivo("1");
				}else{
					documentoEditar.get(i).setDesActivo("0");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : editarDocumento: "
					+ ex.getLocalizedMessage());
		}
		return documentoEditar;
	}

	public boolean validarRegistro(String codDoc, String codTipoSolic) {
		logger.info("DocumentoService : validarRegistro");
		boolean validacion = false;
		int contador = 0;

		List<TiivsTipoSolicDocumento> secuencial = new ArrayList<TiivsTipoSolicDocumento>();
		GenericDao<TiivsTipoSolicDocumento, Object> service = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTipoSolicDocumento.class);
		try {
			secuencial = service.buscarDinamico(filtro
					.add(Restrictions.eq("id.codDoc", codDoc))
					.add(Restrictions.eq("id.codTipoSolic", codTipoSolic))
					.setProjection(Projections.rowCount()));

			List<E> parse = new ArrayList<E>();
			parse = (List<E>) secuencial;
			contador = Integer.parseInt(parse.get(0).toString());
			if (contador == 0) {
				validacion = true;
			} else {
				validacion = false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : obtenerMaximo: "
					+ ex.getLocalizedMessage());
		}

		return validacion;
	}

}

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
import com.hildebrando.visado.mb.DocumentoMB;
import com.hildebrando.visado.modelo.TiivsMultitabla;

public class TiposDoiService {
	public static Logger logger = Logger.getLogger(DocumentoService.class);

	@SuppressWarnings("unchecked")
	public void registrar(TiivsMultitabla documento) {
		logger.info("DocumentoService : registrar");
		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			service.insertarMerge(documento);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : registrar: "
					+ ex.getLocalizedMessage());
		}

	}

	@SuppressWarnings("unchecked")
	public List<TiivsMultitabla> listarDocumentos(/*List<TiivsMultitabla> tipoDocumento*/) {
		logger.info("DocumentoService : listarDocumentos");
		List<TiivsMultitabla> documentos = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			documentos = service
					.buscarDinamico(filtro.add(Restrictions.eq("id.codMult",
							ConstantesVisado.CODIGO_MULTITABLA_DOCUMENTO)));
			for (int i = 0; i < documentos.size(); i++) {
				
				if (documentos.get(i).getValor2()
						.equals(ConstantesVisado.VALOR2_ESTADO_ACTIVO)) {
					documentos.get(i).setValor2(
							ConstantesVisado.VALOR2_ESTADO_ACTIVO_LISTA);
				} else {
					documentos.get(i).setValor2(
							ConstantesVisado.VALOR2_ESTADO_INACTIVO_LISTA);
				}
			/*	if (documentos.get(i).getValor4()
						.equals(ConstantesVisado.VALOR4_OBLIGATORIO_SI)) {
					documentos.get(i).setValor4(
							ConstantesVisado.VALOR4_OBLIGATORIO_SI_LISTA);
				} else {
					documentos.get(i).setValor4(
							ConstantesVisado.VALOR4_OBLIGATORIO_NO_LISTA);
				}*/
			}
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
		List<TiivsMultitabla> secuencial = new ArrayList<TiivsMultitabla>();
		String contador = "0";

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			secuencial = service.buscarDinamico(filtro.add(
					Restrictions.eq("id.codMult",
							ConstantesVisado.CODIGO_MULTITABLA_DOCUMENTO))
					.setProjection(Projections.max("id.codElem")));

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
	}

	@SuppressWarnings("unchecked")
	public List<TiivsMultitabla> editarDocumento(String codMultitabla,
			String codElemento) {
		
		logger.info("DocumentoService : editarDocumento");
		List<TiivsMultitabla> documentoEditar = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		try {
			documentoEditar = service.buscarDinamico(filtro.add(
					Restrictions.eq("id.codMult", codMultitabla)).add(
					Restrictions.eq("id.codElem", codElemento)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : editarDocumento: "
					+ ex.getLocalizedMessage());
		}
		return documentoEditar;
	}

	public String obtenerCodPersonaJuridica() {
				
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
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA
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
		
	}

}

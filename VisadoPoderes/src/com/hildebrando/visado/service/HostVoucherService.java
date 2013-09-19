package com.hildebrando.visado.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsHostVoucher;

public class HostVoucherService {
	public static Logger logger = Logger.getLogger(DocumentoService.class);

	@SuppressWarnings("unchecked")
	public void registrar(TiivsHostVoucher voucher) {
		logger.info("DocumentoService : registrar");
		GenericDao<TiivsHostVoucher, Object> service = (GenericDao<TiivsHostVoucher, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			service.insertarMerge(voucher);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : registrar: "
					+ ex.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<TiivsHostVoucher> listarDatosVoucher() {
		logger.info("DocumentoService : listarDatosVoucher");
		List<TiivsHostVoucher> documentos = new ArrayList<TiivsHostVoucher>();

		GenericDao<TiivsHostVoucher, Object> service = (GenericDao<TiivsHostVoucher, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHostVoucher.class);
		try {
			documentos = service.buscarDinamico(filtro);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : listarDatosVoucher: "
					+ ex.getLocalizedMessage());
		}
		return documentos;
	}

	@SuppressWarnings("unchecked")
	public List<TiivsHostVoucher> editarVoucher(
			String codElemento) {
		
		logger.info("DocumentoService : editarVoucher");
		List<TiivsHostVoucher> documentoEditar = new ArrayList<TiivsHostVoucher>();

		GenericDao<TiivsHostVoucher, Object> service = (GenericDao<TiivsHostVoucher, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHostVoucher.class);
		try {
			documentoEditar = service.buscarDinamico(filtro.add(
					Restrictions.eq("nroVoucher", codElemento)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoService : editarDocumento: "
					+ ex.getLocalizedMessage());
		}
		return documentoEditar;
	}

}

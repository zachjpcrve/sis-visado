package com.hildebrando.visado.mb;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumentoId;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;
import com.hildebrando.visado.service.DocumentoService;

@ManagedBean(name = "documentoMB")
@SessionScoped
public class DocumentoMB {
	public static Logger logger = Logger.getLogger(DocumentoMB.class);
	private DocumentoService documentoService;
	private TiivsTipoSolicDocumento tipoSolicDocumento;
	private List<TiivsTipoSolicDocumento> documentos;
	private List<TiivsTipoSolicDocumento> documentosEditar;
	private List<TiivsTipoSolicitud> tipoSolicitud;
	private List<TiivsDocumento> listaDocumentos;
	private boolean bValor4;
	private boolean bMsgActualizar;
	private boolean bValidacion;
	private boolean bEditar;
	
	public DocumentoMB() {
		TiivsDocumento documento = new TiivsDocumento();
		TiivsTipoSolicitud tipoSol = new TiivsTipoSolicitud();
		TiivsTipoSolicDocumentoId tipoSolcDocumentoId = new TiivsTipoSolicDocumentoId();
		
		tipoSolicDocumento = new TiivsTipoSolicDocumento();
	
		tipoSolicDocumento.setId(tipoSolcDocumentoId);
		tipoSolicDocumento.setTiivsDocumento(documento);
		tipoSolicDocumento.setTiivsTipoSolicitud(tipoSol);
		bValor4 = false;
		bMsgActualizar = false;
		bValidacion = false;
		bEditar = false;
		documentoService = new DocumentoService();
		
		
		cargarComboTipoDocumento();
		cargarComboDocumento();
		listarDocumentos();
/*		obtenerMaximo();*/

	}

	public void cargarComboTipoDocumento() {
		logger.info("DocumentoMB : cargarComboTipoDocumento");
		try {
			tipoSolicitud = documentoService.cargarComboTipoDocumento();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+"tipoSolicitud:" + e.getLocalizedMessage());
		}

	}
	public void cargarComboDocumento() {
		logger.info("DocumentoMB : cargarComboDocumento");
		try {
			listaDocumentos = documentoService.cargarComboDocumento();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoMB : cargarComboDocumento" + e.getLocalizedMessage());
		}

	}

	
	/*public void obtenerMaximo() {
		logger.info("DocumentoMB : obtenerMaximo");
		String secuencial = null;
		int parseSecuencial = 0;
		try {

			secuencial = documentoService.obtenerMaximo();
			parseSecuencial = Integer.parseInt(secuencial) + 1;
			secuencial = String.valueOf(parseSecuencial);


			if (secuencial.length() == 1) {
				secuencial = "000000" + secuencial;
			} else {
				if (secuencial.length() == 2) {
					secuencial = "00000" + secuencial;
				} else {
					if (secuencial.length() == 3) {
						secuencial = "0000" + secuencial;
					} else {
						if (secuencial.length() == 4) {
							secuencial = "000" + secuencial;
						} else {
							if (secuencial.length() == 5) {
								secuencial = "00" + secuencial;
							} else {
								if (secuencial.length() == 6) {
									secuencial = "0" + secuencial;
								} else {
									secuencial = secuencial;
								}
							}
						}
					}
				}
			}

			logger.info("DocumentoMB : secuencial" + " " + secuencial);
			documento.setCodDocumento(secuencial);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoMB :" + e.getLocalizedMessage());
		}
	}*/

	public void registrar() {
		logger.info("DocumentoMB : Registrar");
		boolean validacion;
		if(tipoSolicDocumento.getId().getCodDoc().equals("-1")){

			Utilitarios.mensajeError("Error", "Debe Seleccionar un Documento");
		}else{
			if(tipoSolicDocumento.getId().getCodTipoSolic().equals("-1")){
				Utilitarios.mensajeError("Error", "Debe Seleccionar un Tipo de Solicitud");
			}else{
				
				validacion =validarRegistro(tipoSolicDocumento.getId().getCodDoc(), tipoSolicDocumento.getId().getCodTipoSolic());
				try{
					
					if (isbValor4() == true) {
						tipoSolicDocumento.setObligatorio(ConstantesVisado.VALOR4_OBLIGATORIO_SI);
						} else {
							tipoSolicDocumento.setObligatorio(ConstantesVisado.VALOR4_OBLIGATORIO_NO);
						}

						documentoService.registrar(tipoSolicDocumento);
					
						if (validacion == false) {
							Utilitarios.mensajeInfo("NIVEL", "Se actualizo correctamente");
						} else {
							Utilitarios.mensajeInfo("NIVEL", "Se registro correctamente");
						}
						
					
						
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("DocumentoMB :" + e.getLocalizedMessage());
						Utilitarios
								.mensajeError("Error", "Error al registrar el Documento");
					}		
			}
		}
		setbValor4(false);
		bMsgActualizar = false;
		bValidacion = false;
		limpiarCampos();
	}

	private boolean validarRegistro(String codDoc, String codTipoSolic) {
		logger.info("DocumentoMB : validarRegistro");
		boolean validacion= false;
		try{
			validacion = documentoService.validarRegistro(codDoc, codTipoSolic);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("DocumentoMB : validarRegistro :" + e.getLocalizedMessage());
		}
		return validacion;
	}

	public void listarDocumentos() {
		logger.info("DocumentoMB : listarDocumentos");
		try {
			documentos = documentoService.listarDocumentos();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoMB :" + e.getLocalizedMessage());
		}
	}
	
	
	public void limpiarCampos() {
		logger.info("DocumentoMB : limpiarCampos");
		tipoSolicDocumento = new TiivsTipoSolicDocumento();
		TiivsDocumento documento = new TiivsDocumento();
		TiivsTipoSolicitud tipoSol = new TiivsTipoSolicitud();
		TiivsTipoSolicDocumentoId tipoSolcDocumentoId = new TiivsTipoSolicDocumentoId();
		
		setbValor4(false);
		tipoSolicDocumento.setId(tipoSolcDocumentoId);
		tipoSolicDocumento.setTiivsDocumento(documento);
		tipoSolicDocumento.setTiivsTipoSolicitud(tipoSol);
		setbEditar(false);
	}
	

	public void editarDocumento() {
		logger.info("DocumentoMB : listarDocumentos");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codDocumento;
		String codTipoSolicitud;
		codDocumento = params.get("codDocumento");
		codTipoSolicitud = params.get("codTipoSolicitud");
		try {
			documentosEditar = documentoService.editarDocumento(codDocumento,
					codTipoSolicitud);
			for (int i = 0; i < documentosEditar.size(); i++) {
				tipoSolicDocumento.getId().setCodDoc(documentosEditar.get(i).getId().getCodDoc());
				tipoSolicDocumento.getId().setCodTipoSolic(documentosEditar.get(i).getId().getCodTipoSolic());
				tipoSolicDocumento.setActivo(documentosEditar.get(i).getActivo());
				tipoSolicDocumento.setObligatorio(documentosEditar.get(i).getObligatorio());
				tipoSolicDocumento.setDesActivo(documentosEditar.get(i).getDesActivo());
			}
			if (tipoSolicDocumento.getObligatorio().equals(
					ConstantesVisado.VALOR4_OBLIGATORIO_SI)) {
				bValor4 = true;
			} else {
				bValor4 = false;
			}
			bMsgActualizar = true;
			bEditar = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoMB : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
	}
	public boolean isbValor4() {
		return bValor4;
	}

	public void setbValor4(boolean bValor4) {
		this.bValor4 = bValor4;
	}

	public boolean isbMsgActualizar() {
		return bMsgActualizar;
	}

	public void setbMsgActualizar(boolean bMsgActualizar) {
		this.bMsgActualizar = bMsgActualizar;
	}

	public boolean isbValidacion() {
		return bValidacion;
	}

	public void setbValidacion(boolean bValidacion) {
		this.bValidacion = bValidacion;
	}

/*	public List<TiivsDocumento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<TiivsDocumento> documentos) {
		this.documentos = documentos;
	}*/

/*	public TiivsDocumento getDocumento() {
		return documento;
	}*/

/*
	public void setDocumento(TiivsDocumento documento) {
		this.documento = documento;
	}*/

	public List<TiivsTipoSolicitud> getTipoSolicitud() {
		return tipoSolicitud;
	}

	public void setTipoSolicitud(List<TiivsTipoSolicitud> tipoSolicitud) {
		this.tipoSolicitud = tipoSolicitud;
	}

	public TiivsTipoSolicDocumento getTipoSolicDocumento() {
		return tipoSolicDocumento;
	}

	public void setTipoSolicDocumento(TiivsTipoSolicDocumento tipoSolicDocumento) {
		this.tipoSolicDocumento = tipoSolicDocumento;
	}

	public List<TiivsTipoSolicDocumento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<TiivsTipoSolicDocumento> documentos) {
		this.documentos = documentos;
	}

	public List<TiivsDocumento> getListaDocumentos() {
		return listaDocumentos;
	}

	public void setListaDocumentos(List<TiivsDocumento> listaDocumentos) {
		this.listaDocumentos = listaDocumentos;
	}

	public DocumentoService getDocumentoService() {
		return documentoService;
	}

	public void setDocumentoService(DocumentoService documentoService) {
		this.documentoService = documentoService;
	}

	public boolean isbEditar() {
		return bEditar;
	}

	public void setbEditar(boolean bEditar) {
		this.bEditar = bEditar;
	}
	
}

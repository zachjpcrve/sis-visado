package com.hildebrando.visado.mb;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.service.DocumentoTiivsService;

@ManagedBean(name = "documentoTiivsMB")
@SessionScoped
public class DocumentoTiivsMB {
	public static Logger logger = Logger.getLogger(DocumentoTiivsMB.class);
	private TiivsDocumento documento;
	private List<TiivsDocumento> documentos;
	private List<TiivsDocumento> documentosEditar;
	private DocumentoTiivsService documentoTiivsService;

	private boolean bMsgActualizar;
	
	public DocumentoTiivsMB() {
		documentoTiivsService = new DocumentoTiivsService();
		documento = new TiivsDocumento();
		bMsgActualizar = false;
		obtenerMaximo();
		listarDocumentos();
	}

	public void listarDocumentos() {
		logger.info("DocumentoTiivsMB : listarDocumentos");
		try {
			documentos = documentoTiivsService.listarDocumentos();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoTiivsMB : listarDocumentos :"
					+ e.getLocalizedMessage());
		}
	}

	public void obtenerMaximo() {
		logger.info("DocumentoTiivsMB : obtenerMaximo");
		String secuencial = null;
		int parseSecuencial = 0;
		try {

			secuencial = documentoTiivsService.obtenerMaximo();
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

			logger.info("DocumentoTiivsMB : secuencial" + " " + secuencial);
			documento.setCodDocumento(secuencial);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoTiivsMB :" + e.getLocalizedMessage());
		}
	}

	public void registrar() {
		logger.info("DocumentoTiivsMB : Registrar");

		try {
			if (documento.getDescripcion().isEmpty() == false) {
				if (documento.getNombre().isEmpty() == false) {
					if (documento.getFormato().isEmpty() == false) {
						if (isbMsgActualizar() == true) {
							documentoTiivsService.registrar(documento);
							Utilitarios.mensajeInfo("NIVEL",
									"Se actualizó correctamente");
						}else{
							if (validarNombre() == true) {
								documentoTiivsService.registrar(documento);
								Utilitarios.mensajeInfo("NIVEL",
										"Se registró correctamente");
							}else{
								Utilitarios.mensajeError("Error",
										"El Nombre ya se encuentra registrado");
							}
						}

					} else {
						Utilitarios.mensajeError("Error",
								"El Campo Formato es Obligatorio");
					}

				} else {
					Utilitarios.mensajeError("Error",
							"El Campo Nombre es Obligatorio");
				}
			} else {
				Utilitarios.mensajeError("Error",
						"El Campo Descripción es Obligatorio");
			}

			documento = new TiivsDocumento();
			obtenerMaximo();
			bMsgActualizar = false;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoTiivsMB :" + e.getLocalizedMessage());
			Utilitarios
					.mensajeError("Error", "Error al registrar el Documento");
		}
	}

	public boolean validarNombre() {
		logger.info("DocumentoTiivsMB : validarNombre");
		boolean validacion = false;
		try {
			validacion = documentoTiivsService.validarNombre(documento
					.getNombre());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoTiivsMB : validarNombre :"
					+ e.getLocalizedMessage());
		}
		return validacion;
	}

	public void editarDocumento() {
		logger.info("DocumentoTiivsMB : editarDocumento");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String codDocumento;
		codDocumento = params.get("codDocumento");
		try {
			documentosEditar = documentoTiivsService
					.editarDocumento(codDocumento);
			for (int i = 0; i < documentosEditar.size(); i++) {
				documento.setCodDocumento(documentosEditar.get(i)
						.getCodDocumento());
				documento.setDescripcion(documentosEditar.get(i)
						.getDescripcion());
				documento.setNombre(documentosEditar.get(i).getNombre());
				documento.setFormato(documentosEditar.get(i).getFormato());
				documento.setActivo(documentosEditar.get(i).getActivo());
				documento.setCodBarra(documentosEditar.get(i).getCodBarra());
				documento.setTiivsTipoSolicDocumentos(documentosEditar.get(i)
						.getTiivsTipoSolicDocumentos());
			}
			bMsgActualizar = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoTiivsMB : editarDocumento: "
					+ ex.getLocalizedMessage());
		}
	}

	public void limpiarCampos() {
		logger.info("DocumentoTiivsMB : limpiarCampos");
		documento = new TiivsDocumento();
		bMsgActualizar = false;
		obtenerMaximo();
	}

	public TiivsDocumento getDocumento() {
		return documento;
	}

	public void setDocumento(TiivsDocumento documento) {
		this.documento = documento;
	}

	public List<TiivsDocumento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<TiivsDocumento> documentos) {
		this.documentos = documentos;
	}

	public boolean isbMsgActualizar() {
		return bMsgActualizar;
	}

	public void setbMsgActualizar(boolean bMsgActualizar) {
		this.bMsgActualizar = bMsgActualizar;
	}

	public List<TiivsDocumento> getDocumentosEditar() {
		return documentosEditar;
	}

	public void setDocumentosEditar(List<TiivsDocumento> documentosEditar) {
		this.documentosEditar = documentosEditar;
	}

}

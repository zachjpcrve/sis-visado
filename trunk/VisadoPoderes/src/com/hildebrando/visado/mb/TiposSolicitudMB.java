package com.hildebrando.visado.mb;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;
import com.hildebrando.visado.service.TiposSolicitudService;

@ManagedBean(name = "tiposSolicitudMB")
@SessionScoped
public class TiposSolicitudMB {
	public static Logger logger = Logger.getLogger(DocumentoMB.class);
	private TiposSolicitudService tipoSolicitudService; //SE AGREGA
	/*private TiivsMultitabla documento;
	private List<TiivsMultitabla> documentos;
	private List<TiivsMultitabla> documentoEditar;*/
	//private boolean bValor4;
	private TiivsTipoSolicitud solicitud;
	private List<TiivsTipoSolicitud> solicitudEditar;
	private List<TiivsTipoSolicitud> solicitudes;
	
	private boolean bMsgActualizar;
	private boolean bValidacion;

	public TiposSolicitudMB() {
		/*documento = new TiivsMultitabla();
		TiivsMultitablaId documentoId = new TiivsMultitablaId();
		documento.setId(documentoId);*/
		//bValor4 = false;
		solicitud = new TiivsTipoSolicitud();
		bMsgActualizar = false;
		bValidacion = false;
		tipoSolicitudService = new TiposSolicitudService();
		listarTiposSolicitud();
		obtenerMaximo();

	}
	
	public void obtenerMaximo() {
		logger.info("DocumentoMB : obtenerMaximo");
		String secuencial = null;
		int parseSecuencial = 0;
		try {

			secuencial = tipoSolicitudService.obtenerMaximo();
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
						secuencial = secuencial;
					}
				}

			}
			logger.info("DocumentoMB : secuencial" + " " + secuencial);
			solicitud.setCodTipSolic(secuencial);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoMB :" + e.getLocalizedMessage());
		}
	}

	public void registrar() {
		logger.info("DocumentoMB : Registrar");
		if(solicitud.getActivo()== '\0'){
			bValidacion = false;
		}else{
			bValidacion=true;
		}
		if(isbValidacion()==true){
			try {
				tipoSolicitudService.registrar(solicitud);
				
				if (isbMsgActualizar() == true) {
					Utilitarios.mensajeInfo("NIVEL", "Se actualizó correctamente");
				} else {
					Utilitarios.mensajeInfo("NIVEL", "Se registró correctamente");
				}
				solicitud = new TiivsTipoSolicitud();
				obtenerMaximo();
				bMsgActualizar = false;
				bValidacion = false;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("DocumentoMB :" + e.getLocalizedMessage());
				Utilitarios
						.mensajeError("Error", "Error al registrar el Documento");
			}
		}else{
			Utilitarios
			.mensajeError("Error", "El campo descripción es requerido");
		}
		
	}

	public void listarTiposSolicitud() {
		logger.info("DocumentoMB : listarTiposSolicitud");
		try {
			solicitudes = tipoSolicitudService.listarTiposSolicitud(/*Tipos de solicitudes*/);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DocumentoMB :" + e.getLocalizedMessage());
		}
	}
	
	
	public void limpiarCampos() {
		logger.info("DocumentoMB : limpiarCampos");
		solicitud = new TiivsTipoSolicitud();
		obtenerMaximo();
		bMsgActualizar = false;
	}
	

	public void editarTipoSolicitud() {
		logger.info("DocumentoMB : editarTiposSolicitud");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codElemento;
		codElemento = params.get("codElemento");
		try {
			solicitudEditar = tipoSolicitudService.editarDocumento(codElemento);
			
			for(TiivsTipoSolicitud solicitudE:solicitudEditar){
				solicitud = solicitudE;
			}
			bMsgActualizar = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoMB : editarTiposSolicitud: "
					+ ex.getLocalizedMessage());
		}
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

	public List<TiivsTipoSolicitud> getSolicitudEditar() {
		return solicitudEditar;
	}

	public void setSolicitudEditar(List<TiivsTipoSolicitud> solicitudEditar) {
		this.solicitudEditar = solicitudEditar;
	}

	public List<TiivsTipoSolicitud> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(List<TiivsTipoSolicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

	public TiivsTipoSolicitud getSolicitud() {
		return solicitud;
	}

	public void setSolicitud(TiivsTipoSolicitud solicitud) {
		this.solicitud = solicitud;
	}
	
	
}

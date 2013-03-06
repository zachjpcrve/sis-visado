package com.hildebrando.visado.mb;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.service.ClasificacionService;

@ManagedBean(name = "clasificacionMB")
@SessionScoped
public class ClasificacionMB {
	public static Logger logger = Logger.getLogger(DocumentoMB.class);
	private ClasificacionService clasificacionService;
	private TiivsMultitabla clasificacion;
	private List<TiivsMultitabla> clasificaciones;
	private List<TiivsMultitabla> clasificacionEditar;
	private boolean bMsgActualizar;
	private boolean bValidacion;

	public ClasificacionMB() {
		clasificacion = new TiivsMultitabla();
		TiivsMultitablaId clasificacionId = new TiivsMultitablaId();
		clasificacion.setId(clasificacionId);
		clasificacionService = new ClasificacionService();
		bMsgActualizar = false;
		bValidacion = false;
		listarClasificaciones();
		obtenerMaximo();
	}

	public void obtenerMaximo() {
		logger.info("ClasificacionMB : obtenerMaximo");
		String secuencial = null;
		int parseSecuencial = 0;
		try {

			secuencial = clasificacionService.obtenerMaximo();
			parseSecuencial = Integer.parseInt(secuencial) + 1;
			secuencial = String.valueOf(parseSecuencial);

			if (secuencial.length() == 1) {
				secuencial = "000" + secuencial;
			} else {
				if (secuencial.length() == 2) {
					secuencial = "00" + secuencial;
				} else {
					if (secuencial.length() == 3) {
						secuencial = "0" + secuencial;
					} else {
						secuencial = secuencial;
					}
				}

			}
			logger.info("ClasificacionMB : secuencial" + " " + secuencial);
			clasificacion.getId().setCodElem(secuencial);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ClasificacionMB :" + e.getLocalizedMessage());
		}
	}

	public void listarClasificaciones() {
		logger.info("ClasificacionMB: listarClasificaciones");
		try {
			clasificaciones = clasificacionService.listarClasificaciones();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ClasificacionMB :" + e.getLocalizedMessage());
		}

	}

	public void registrar() {
		logger.info("ClasificacionMB : Registrar");
		clasificacion.getId().setCodMult(
				ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION);
		if (clasificacion.getValor1().isEmpty()) {
			bValidacion = false;
		} else {
			bValidacion = true;
		}
		if (isbValidacion() == true) {
			try {
				clasificacionService.registrar(clasificacion);
				if (isbMsgActualizar() == true) {
					Utilitarios.mensajeInfo("NIVEL",
							"Se actualizo correctamente");
				} else {
					Utilitarios.mensajeInfo("NIVEL",
							"Se registro correctamente");
				}
				clasificacion = new TiivsMultitabla();
				TiivsMultitablaId clasificacionId = new TiivsMultitablaId();
				clasificacion.setId(clasificacionId);
				obtenerMaximo();
				bMsgActualizar = false;
				bValidacion = false;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("ClasificacionMB :" + e.getLocalizedMessage());
				Utilitarios.mensajeError("Error",
						"Error al registrar el Tipo de Clasificacion");
			}
		} else {
			Utilitarios.mensajeError("Error",
					"El campo descripcion es requerido");
		}

	}

	public void editarClasificacion() {
		logger.info("ClasificacionMB : listarClasificacion");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codMultitabla;
		String codElemento;
		codMultitabla = params.get("codMultitabla");
		codElemento = params.get("codElemento");
		try {
			clasificacionEditar = clasificacionService.editarClasificacion(
					codMultitabla, codElemento);
			for (int i = 0; i < clasificacionEditar.size(); i++) {
				clasificacion.setId(clasificacionEditar.get(i).getId());
				clasificacion.setValor1(clasificacionEditar.get(i).getValor1());
				clasificacion.setValor2(clasificacionEditar.get(i).getValor2());
			}
			bMsgActualizar = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoMB : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
	}

	public void limpiarCampos() {
		logger.info("ClasificacionMB : limpiarCampos");
		clasificacion = new TiivsMultitabla();
		TiivsMultitablaId clasificacionId = new TiivsMultitablaId();
		clasificacion.setId(clasificacionId);
		obtenerMaximo();
		bMsgActualizar = false;
	}

	public TiivsMultitabla getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(TiivsMultitabla clasificacion) {
		this.clasificacion = clasificacion;
	}

	public List<TiivsMultitabla> getClasificaciones() {
		return clasificaciones;
	}

	public void setClasificaciones(List<TiivsMultitabla> clasificaciones) {
		this.clasificaciones = clasificaciones;
	}

	public List<TiivsMultitabla> getClasificacionEditar() {
		return clasificacionEditar;
	}

	public void setClasificacionEditar(List<TiivsMultitabla> clasificacionEditar) {
		this.clasificacionEditar = clasificacionEditar;
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
}
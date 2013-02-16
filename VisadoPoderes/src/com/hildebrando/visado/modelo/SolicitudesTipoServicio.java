package com.hildebrando.visado.modelo;

public class SolicitudesTipoServicio {
	private String codSolicitud;
	private String estudio;
	private String tipoServicio;
	private String tipoOperacion;
	private String moneda;
	private String importe;
	private String tipoCambio;
	private String importeSoles;
	public String getCodSolicitud() {
		return codSolicitud;
	}
	public void setCodSolicitud(String codSolicitud) {
		this.codSolicitud = codSolicitud;
	}
	public String getEstudio() {
		return estudio;
	}
	public void setEstudio(String estudio) {
		this.estudio = estudio;
	}
	public String getTipoServicio() {
		return tipoServicio;
	}
	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}
	public String getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getImporte() {
		return importe;
	}
	public void setImporte(String importe) {
		this.importe = importe;
	}
	public String getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getImporteSoles() {
		return importeSoles;
	}
	public void setImporteSoles(String importeSoles) {
		this.importeSoles = importeSoles;
	}
}

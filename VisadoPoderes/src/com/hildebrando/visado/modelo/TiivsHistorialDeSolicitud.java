package com.hildebrando.visado.modelo;

public class TiivsHistorialDeSolicitud {
	private Long ROWID;
	private String codSolicitud;
	private String estado;
	public Long getROWID() {
		return ROWID;
	}
	public void setROWID(Long rOWID) {
		ROWID = rOWID;
	}
	public String getCodSolicitud() {
		return codSolicitud;
	}
	public void setCodSolicitud(String codSolicitud) {
		this.codSolicitud = codSolicitud;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
}

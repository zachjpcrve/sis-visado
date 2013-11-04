package com.hildebrando.visado.dto;

public class ParametrosHost {
	
	private String usuarioConsulta;
	private String flagDummy;
	private String tipoAplicacion;
	private String rutaServicio;
	
	public String getUsuarioConsulta() {
		return usuarioConsulta;
	}
	public void setUsuarioConsulta(String usuarioConsulta) {
		this.usuarioConsulta = usuarioConsulta;
	}
	public String getTipoAplicacion() {
		return tipoAplicacion;
	}
	public void setTipoAplicacion(String tipoAplicacion) {
		this.tipoAplicacion = tipoAplicacion;
	}
	public String getRutaServicio() {
		return rutaServicio;
	}
	public void setRutaServicio(String rutaServicio) {
		this.rutaServicio = rutaServicio;
	}
	public String getFlagDummy() {
		return flagDummy;
	}
	public void setFlagDummy(String flagDummy) {
		this.flagDummy = flagDummy;
	}
}

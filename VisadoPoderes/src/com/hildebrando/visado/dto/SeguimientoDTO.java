package com.hildebrando.visado.dto;

public class SeguimientoDTO {
private String estado;
private String nivel;
private String fecha;
private String usuario;
private String obs;


public SeguimientoDTO(String estado, String nivel, String fecha,
		String usuario, String obs) {
	super();
	this.estado = estado;
	this.nivel = nivel;
	this.fecha = fecha;
	this.usuario = usuario;
	this.obs = obs;
}

public String getEstado() {
	return estado;
}
public void setEstado(String estado) {
	this.estado = estado;
}
public String getNivel() {
	return nivel;
}
public void setNivel(String nivel) {
	this.nivel = nivel;
}
public String getFecha() {
	return fecha;
}
public void setFecha(String fecha) {
	this.fecha = fecha;
}
public String getUsuario() {
	return usuario;
}
public void setUsuario(String usuario) {
	this.usuario = usuario;
}
public String getObs() {
	return obs;
}
public void setObs(String obs) {
	this.obs = obs;
}





}

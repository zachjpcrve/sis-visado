package com.hildebrando.visado.dto;

import java.io.Serializable;

public class EstadosNivel implements Serializable{
	private String codigoEstadoNivel;
	private String descripcion;
	
	public String getCodigoEstadoNivel() {
		return codigoEstadoNivel;
	}
	public void setCodigoEstadoNivel(String codigoEstadoNivel) {
		this.codigoEstadoNivel = codigoEstadoNivel;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

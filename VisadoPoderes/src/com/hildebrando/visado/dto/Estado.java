package com.hildebrando.visado.dto;

import java.io.Serializable;

public class Estado implements Serializable{
	private String codEstado;
	private String descripcion;
	
	public String getCodEstado() {
		return codEstado;
	}
	public void setCodEstado(String codEstado) {
		this.codEstado = codEstado;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

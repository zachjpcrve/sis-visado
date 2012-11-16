package com.hildebrando.visado.dto;

import java.io.Serializable;

public class RangosImporte implements Serializable{
	private String codigoRango;
	private String descripcion;
	
	public String getCodigoRango() {
		return codigoRango;
	}
	public void setCodigoRango(String codigoRango) {
		this.codigoRango = codigoRango;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

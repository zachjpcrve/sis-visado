package com.hildebrando.visado.dto;

import java.io.Serializable;

public class TiposFecha implements Serializable{
	private String codigoTipoFecha;
	private String descripcion;
	
	public String getCodigoTipoFecha() {
		return codigoTipoFecha;
	}
	public void setCodigoTipoFecha(String codigoTipoFecha) {
		this.codigoTipoFecha = codigoTipoFecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

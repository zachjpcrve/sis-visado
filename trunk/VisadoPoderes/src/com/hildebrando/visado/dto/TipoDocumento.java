package com.hildebrando.visado.dto;

import java.io.Serializable;

public class TipoDocumento implements Serializable{
	private String codTipoDoc;
	private String descripcion;
	
	public String getCodTipoDoc() {
		return codTipoDoc;
	}
	public void setCodTipoDoc(String codTipoDoc) {
		this.codTipoDoc = codTipoDoc;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

package com.hildebrando.visado.dto;

import java.io.Serializable;

public class DocumentosPDF implements Serializable {
	private String item;
	private String descripcion;
	
	public DocumentosPDF(String item, String descripcion){
		this.item = item;
		this.descripcion = descripcion;
	}
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

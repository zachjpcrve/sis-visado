package com.hildebrando.visado.dto;

public class DocumentoTipoSolicitudDTO {
private String item;
private String documento;
private String obligacion;
private boolean bobligacion;

public DocumentoTipoSolicitudDTO(String item, String documento,String obligacion) {
	super();
	this.item = item;
	this.documento = documento;
	this.obligacion = obligacion;
}
public DocumentoTipoSolicitudDTO() {
	super();
}

public String getItem() {
	return item;
}
public void setItem(String item) {
	this.item = item;
}
public String getDocumento() {
	return documento;
}
public void setDocumento(String documento) {
	this.documento = documento;
}
public String getObligacion() {
	return obligacion;
}
public void setObligacion(String obligacion) {
	this.obligacion = obligacion;
}

public boolean isBobligacion() {
	return bobligacion;
}
public void setBobligacion(boolean bobligacion) {
	this.bobligacion = bobligacion;
}



}

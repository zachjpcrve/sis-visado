package com.hildebrando.visado.dto;

public class DocumentoTipoSolicitudDTO {
	private String item;
	private String documento;
	private String obligacion;
	private String alias;
	private boolean bobligacion;
	private String rutaDoc;

	public DocumentoTipoSolicitudDTO(String item, String documento,
			String obligacion, String alias) {
		super();
		this.item = item;
		this.documento = documento;
		this.obligacion = obligacion;
		this.alias = alias;
		this.bobligacion = Boolean.valueOf(obligacion);
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getRutaDoc() {
		return rutaDoc;
	}

	public void setRutaDoc(String rutaDoc) {
		this.rutaDoc = rutaDoc;
	}
}

package com.hildebrando.visado.dto;

public class DocumentoTipoSolicitudDTO {
	private String item;
	private String documento;
	private String obligacion;
	private String alias;
	private String aliasTemporal;
	private boolean bobligacion;
	private String rutaDoc;
	private String nombreCorto;
	private String formato;

	public DocumentoTipoSolicitudDTO(String item, String documento,
			String obligacion, String alias, String aliasTemporal,
			String nombreCorto, String formato) {
		super();
		this.item = item;
		this.documento = documento;
		this.obligacion = obligacion;
		this.alias = alias;
		this.bobligacion = Boolean.valueOf(obligacion);
		this.aliasTemporal = aliasTemporal;
		this.nombreCorto = nombreCorto;
		this.formato = formato;

	}

	public DocumentoTipoSolicitudDTO() {
		super();
	}

	public DocumentoTipoSolicitudDTO(String item, String aliasArchivo) {
		this.item = item;
		this.documento = aliasArchivo;
	}
	
	public DocumentoTipoSolicitudDTO(String item, String documento, String aliasTemporal) {
		this.item = item;
		this.documento = documento;
		this.aliasTemporal = aliasTemporal;
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

	public String getAliasTemporal() {
		return aliasTemporal;
	}

	public void setAliasTemporal(String aliasTemporal) {
		this.aliasTemporal = aliasTemporal;
	}

	public String getNombreCorto() {
		return nombreCorto;
	}

	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}		
}

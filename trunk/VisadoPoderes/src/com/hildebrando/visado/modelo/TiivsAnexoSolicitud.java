package com.hildebrando.visado.modelo;

// Generated 05/12/2012 11:41:39 AM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsAnexoSolicitud generated by hbm2java
 */
public class TiivsAnexoSolicitud implements java.io.Serializable {

	private TiivsAnexoSolicitudId id;
	private TiivsTipoSolicDocumento tiivsTipoSolicDocumento;
	private String aliasArchivo;

	public TiivsAnexoSolicitud() {
	}

	public TiivsAnexoSolicitud(TiivsAnexoSolicitudId id,
			TiivsTipoSolicDocumento tiivsTipoSolicDocumento, String aliasArchivo) {
		this.id = id;
		this.tiivsTipoSolicDocumento = tiivsTipoSolicDocumento;
		this.aliasArchivo = aliasArchivo;
	}

	public TiivsAnexoSolicitudId getId() {
		return this.id;
	}

	public void setId(TiivsAnexoSolicitudId id) {
		this.id = id;
	}

	public TiivsTipoSolicDocumento getTiivsTipoSolicDocumento() {
		return this.tiivsTipoSolicDocumento;
	}

	public void setTiivsTipoSolicDocumento(
			TiivsTipoSolicDocumento tiivsTipoSolicDocumento) {
		this.tiivsTipoSolicDocumento = tiivsTipoSolicDocumento;
	}

	public String getAliasArchivo() {
		return this.aliasArchivo;
	}

	public void setAliasArchivo(String aliasArchivo) {
		this.aliasArchivo = aliasArchivo;
	}

}

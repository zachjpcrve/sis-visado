package com.hildebrando.visado.modelo;

// Generated 28/12/2012 11:45:22 AM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsAnexoSolicitud generated by hbm2java
 */
public class TiivsAnexoSolicitud implements java.io.Serializable {

	private TiivsAnexoSolicitudId id;
	private String aliasArchivo;
	private String aliasTemporal;

	public TiivsAnexoSolicitud() {
	}

	public TiivsAnexoSolicitud(TiivsAnexoSolicitudId id, String aliasArchivo) {
		this.id = id;
		this.aliasArchivo = aliasArchivo;
	}

	public TiivsAnexoSolicitudId getId() {
		return this.id;
	}

	public void setId(TiivsAnexoSolicitudId id) {
		this.id = id;
	}

	public String getAliasArchivo() {
		return this.aliasArchivo;
	}

	public void setAliasArchivo(String aliasArchivo) {
		this.aliasArchivo = aliasArchivo;
	}
	
	public String getAliasTemporal() {
		return aliasTemporal;
	}

	public void setAliasTemporal(String aliasTemporal) {
		this.aliasTemporal = aliasTemporal;
	}

}

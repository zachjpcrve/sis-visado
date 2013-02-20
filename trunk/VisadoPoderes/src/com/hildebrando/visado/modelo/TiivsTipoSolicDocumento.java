package com.hildebrando.visado.modelo;

// Generated 10/01/2013 11:57:43 AM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsTipoSolicDocumento generated by hbm2java
 */
public class TiivsTipoSolicDocumento implements java.io.Serializable {

	private TiivsTipoSolicDocumentoId id;
	private TiivsDocumento tiivsDocumento;
	private TiivsTipoSolicitud tiivsTipoSolicitud;
	private char activo;
	private String obligatorio;
	private String desActivo;

	public TiivsTipoSolicDocumento() {
	}

	public TiivsTipoSolicDocumento(TiivsTipoSolicDocumentoId id,
			TiivsDocumento tiivsDocumento,
			TiivsTipoSolicitud tiivsTipoSolicitud, char activo) {
		this.id = id;
		this.tiivsDocumento = tiivsDocumento;
		this.tiivsTipoSolicitud = tiivsTipoSolicitud;
		this.activo = activo;
	}

	public TiivsTipoSolicDocumento(TiivsTipoSolicDocumentoId id,
			TiivsDocumento tiivsDocumento,
			TiivsTipoSolicitud tiivsTipoSolicitud, char activo,
			String obligatorio) {
		this.id = id;
		this.tiivsDocumento = tiivsDocumento;
		this.tiivsTipoSolicitud = tiivsTipoSolicitud;
		this.activo = activo;
		this.obligatorio = obligatorio;
	}

	public TiivsTipoSolicDocumentoId getId() {
		return this.id;
	}

	public void setId(TiivsTipoSolicDocumentoId id) {
		this.id = id;
	}

	public TiivsDocumento getTiivsDocumento() {
		return this.tiivsDocumento;
	}

	public void setTiivsDocumento(TiivsDocumento tiivsDocumento) {
		this.tiivsDocumento = tiivsDocumento;
	}

	public TiivsTipoSolicitud getTiivsTipoSolicitud() {
		return this.tiivsTipoSolicitud;
	}

	public void setTiivsTipoSolicitud(TiivsTipoSolicitud tiivsTipoSolicitud) {
		this.tiivsTipoSolicitud = tiivsTipoSolicitud;
	}

	public char getActivo() {
		return this.activo;
	}

	public void setActivo(char activo) {
		this.activo = activo;
	}

	public String getObligatorio() {
		return this.obligatorio;
	}

	public void setObligatorio(String obligatorio) {
		this.obligatorio = obligatorio;
	}
	
	
	
	public String getDesActivo() {
		return desActivo;
	}

	public void setDesActivo(String desActivo) {
		this.desActivo = desActivo;
	}

	public boolean equals(Object object) {
		boolean ret = object == this ? true
				: ((object instanceof TiivsTipoSolicDocumento))
						&& (this.id != null) ? this.id
						.equals(((TiivsTipoSolicDocumento) object).id)
						: false;
		return ret;
	}

}

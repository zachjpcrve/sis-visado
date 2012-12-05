package com.hildebrando.visado.modelo;

// Generated 05/12/2012 11:17:13 AM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsAnexoSolicitudId generated by hbm2java
 */
public class TiivsAnexoSolicitudId implements java.io.Serializable {

	private String codSoli;
	private String codDoc;

	public TiivsAnexoSolicitudId() {
	}

	public TiivsAnexoSolicitudId(String codSoli, String codDoc) {
		this.codSoli = codSoli;
		this.codDoc = codDoc;
	}

	public String getCodSoli() {
		return this.codSoli;
	}

	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}

	public String getCodDoc() {
		return this.codDoc;
	}

	public void setCodDoc(String codDoc) {
		this.codDoc = codDoc;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TiivsAnexoSolicitudId))
			return false;
		TiivsAnexoSolicitudId castOther = (TiivsAnexoSolicitudId) other;

		return ((this.getCodSoli() == castOther.getCodSoli()) || (this
				.getCodSoli() != null && castOther.getCodSoli() != null && this
				.getCodSoli().equals(castOther.getCodSoli())))
				&& ((this.getCodDoc() == castOther.getCodDoc()) || (this
						.getCodDoc() != null && castOther.getCodDoc() != null && this
						.getCodDoc().equals(castOther.getCodDoc())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCodSoli() == null ? 0 : this.getCodSoli().hashCode());
		result = 37 * result
				+ (getCodDoc() == null ? 0 : this.getCodDoc().hashCode());
		return result;
	}

}

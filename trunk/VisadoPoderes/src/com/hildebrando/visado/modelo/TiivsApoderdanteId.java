package com.hildebrando.visado.modelo;

// Generated 07/11/2012 02:39:40 PM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsApoderdanteId generated by hbm2java
 */
public class TiivsApoderdanteId implements java.io.Serializable {

	private String codSoli;
	private String codApoder;
	private String tipDocApoder;
	private String numDocPoder;

	public TiivsApoderdanteId() {
	}

	public TiivsApoderdanteId(String codSoli, String codApoder,
			String tipDocApoder, String numDocPoder) {
		this.codSoli = codSoli;
		this.codApoder = codApoder;
		this.tipDocApoder = tipDocApoder;
		this.numDocPoder = numDocPoder;
	}

	public String getCodSoli() {
		return this.codSoli;
	}

	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}

	public String getCodApoder() {
		return this.codApoder;
	}

	public void setCodApoder(String codApoder) {
		this.codApoder = codApoder;
	}

	public String getTipDocApoder() {
		return this.tipDocApoder;
	}

	public void setTipDocApoder(String tipDocApoder) {
		this.tipDocApoder = tipDocApoder;
	}

	public String getNumDocPoder() {
		return this.numDocPoder;
	}

	public void setNumDocPoder(String numDocPoder) {
		this.numDocPoder = numDocPoder;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TiivsApoderdanteId))
			return false;
		TiivsApoderdanteId castOther = (TiivsApoderdanteId) other;

		return ((this.getCodSoli() == castOther.getCodSoli()) || (this
				.getCodSoli() != null && castOther.getCodSoli() != null && this
				.getCodSoli().equals(castOther.getCodSoli())))
				&& ((this.getCodApoder() == castOther.getCodApoder()) || (this
						.getCodApoder() != null
						&& castOther.getCodApoder() != null && this
						.getCodApoder().equals(castOther.getCodApoder())))
				&& ((this.getTipDocApoder() == castOther.getTipDocApoder()) || (this
						.getTipDocApoder() != null
						&& castOther.getTipDocApoder() != null && this
						.getTipDocApoder().equals(castOther.getTipDocApoder())))
				&& ((this.getNumDocPoder() == castOther.getNumDocPoder()) || (this
						.getNumDocPoder() != null
						&& castOther.getNumDocPoder() != null && this
						.getNumDocPoder().equals(castOther.getNumDocPoder())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCodSoli() == null ? 0 : this.getCodSoli().hashCode());
		result = 37 * result
				+ (getCodApoder() == null ? 0 : this.getCodApoder().hashCode());
		result = 37
				* result
				+ (getTipDocApoder() == null ? 0 : this.getTipDocApoder()
						.hashCode());
		result = 37
				* result
				+ (getNumDocPoder() == null ? 0 : this.getNumDocPoder()
						.hashCode());
		return result;
	}

}

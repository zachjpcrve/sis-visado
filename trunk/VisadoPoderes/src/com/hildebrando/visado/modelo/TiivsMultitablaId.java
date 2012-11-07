package com.hildebrando.visado.modelo;

// Generated 07/11/2012 02:39:40 PM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsMultitablaId generated by hbm2java
 */
public class TiivsMultitablaId implements java.io.Serializable {

	private String codMult;
	private String codElem;

	public TiivsMultitablaId() {
	}

	public TiivsMultitablaId(String codMult, String codElem) {
		this.codMult = codMult;
		this.codElem = codElem;
	}

	public String getCodMult() {
		return this.codMult;
	}

	public void setCodMult(String codMult) {
		this.codMult = codMult;
	}

	public String getCodElem() {
		return this.codElem;
	}

	public void setCodElem(String codElem) {
		this.codElem = codElem;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TiivsMultitablaId))
			return false;
		TiivsMultitablaId castOther = (TiivsMultitablaId) other;

		return ((this.getCodMult() == castOther.getCodMult()) || (this
				.getCodMult() != null && castOther.getCodMult() != null && this
				.getCodMult().equals(castOther.getCodMult())))
				&& ((this.getCodElem() == castOther.getCodElem()) || (this
						.getCodElem() != null && castOther.getCodElem() != null && this
						.getCodElem().equals(castOther.getCodElem())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCodMult() == null ? 0 : this.getCodMult().hashCode());
		result = 37 * result
				+ (getCodElem() == null ? 0 : this.getCodElem().hashCode());
		return result;
	}

}

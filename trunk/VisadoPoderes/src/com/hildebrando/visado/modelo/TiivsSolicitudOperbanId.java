package com.hildebrando.visado.modelo;

// Generated 04/03/2013 04:14:47 PM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsSolicitudOperbanId generated by hbm2java
 */
public class TiivsSolicitudOperbanId implements java.io.Serializable {

	private String codSoli;
	private String codOperBan;
	private String moneda;
	private String sDescMoneda;

	public TiivsSolicitudOperbanId() {
	}

	public TiivsSolicitudOperbanId(String codSoli, String codOperBan,
			String moneda) {
		this.codSoli = codSoli;
		this.codOperBan = codOperBan;
		this.moneda = moneda;
	}

	public String getCodSoli() {
		return this.codSoli;
	}

	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}

	public String getCodOperBan() {
		return this.codOperBan;
	}

	public void setCodOperBan(String codOperBan) {
		this.codOperBan = codOperBan;
	}

	public String getMoneda() {
		return this.moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	
	
	public String getsDescMoneda() {
		return this.sDescMoneda;
	}

	public void setsDescMoneda(String sDescMoneda) {
		this.sDescMoneda = sDescMoneda;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TiivsSolicitudOperbanId))
			return false;
		TiivsSolicitudOperbanId castOther = (TiivsSolicitudOperbanId) other;

		return ((this.getCodSoli() == castOther.getCodSoli()) || (this
				.getCodSoli() != null && castOther.getCodSoli() != null && this
				.getCodSoli().equals(castOther.getCodSoli())))
				&& ((this.getCodOperBan() == castOther.getCodOperBan()) || (this
						.getCodOperBan() != null
						&& castOther.getCodOperBan() != null && this
						.getCodOperBan().equals(castOther.getCodOperBan())))
				&& ((this.getMoneda() == castOther.getMoneda()) || (this
						.getMoneda() != null && castOther.getMoneda() != null && this
						.getMoneda().equals(castOther.getMoneda())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCodSoli() == null ? 0 : this.getCodSoli().hashCode());
		result = 37
				* result
				+ (getCodOperBan() == null ? 0 : this.getCodOperBan()
						.hashCode());
		result = 37 * result
				+ (getMoneda() == null ? 0 : this.getMoneda().hashCode());
		return result;
	}

}

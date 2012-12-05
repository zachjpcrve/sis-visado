package com.hildebrando.visado.modelo;

// Generated 05/12/2012 11:17:13 AM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsHistSolicitudId generated by hbm2java
 */
public class TiivsHistSolicitudId implements java.io.Serializable {

	private String codSoli;
	private String movimiento;

	public TiivsHistSolicitudId() {
	}

	public TiivsHistSolicitudId(String codSoli, String movimiento) {
		this.codSoli = codSoli;
		this.movimiento = movimiento;
	}

	public String getCodSoli() {
		return this.codSoli;
	}

	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}

	public String getMovimiento() {
		return this.movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TiivsHistSolicitudId))
			return false;
		TiivsHistSolicitudId castOther = (TiivsHistSolicitudId) other;

		return ((this.getCodSoli() == castOther.getCodSoli()) || (this
				.getCodSoli() != null && castOther.getCodSoli() != null && this
				.getCodSoli().equals(castOther.getCodSoli())))
				&& ((this.getMovimiento() == castOther.getMovimiento()) || (this
						.getMovimiento() != null
						&& castOther.getMovimiento() != null && this
						.getMovimiento().equals(castOther.getMovimiento())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCodSoli() == null ? 0 : this.getCodSoli().hashCode());
		result = 37
				* result
				+ (getMovimiento() == null ? 0 : this.getMovimiento()
						.hashCode());
		return result;
	}

}

package com.hildebrando.visado.modelo;

// Generated 05/12/2012 11:17:13 AM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsEstudio generated by hbm2java
 */
public class TiivsEstudio implements java.io.Serializable {

	private String codEstudio;
	private String desEstudio;
	private Character activo;

	public TiivsEstudio() {
	}

	public TiivsEstudio(String codEstudio) {
		this.codEstudio = codEstudio;
	}

	public TiivsEstudio(String codEstudio, String desEstudio, Character activo) {
		this.codEstudio = codEstudio;
		this.desEstudio = desEstudio;
		this.activo = activo;
	}

	public String getCodEstudio() {
		return this.codEstudio;
	}

	public void setCodEstudio(String codEstudio) {
		this.codEstudio = codEstudio;
	}

	public String getDesEstudio() {
		return this.desEstudio;
	}

	public void setDesEstudio(String desEstudio) {
		this.desEstudio = desEstudio;
	}

	public Character getActivo() {
		return this.activo;
	}

	public void setActivo(Character activo) {
		this.activo = activo;
	}

}

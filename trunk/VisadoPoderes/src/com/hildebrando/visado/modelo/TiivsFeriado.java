package com.hildebrando.visado.modelo;

// Generated 13/12/2012 11:35:28 AM by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * TiivsFeriado generated by hbm2java
 */
public class TiivsFeriado implements java.io.Serializable {

	private int idFeriado;
	private Date fecha;
	private Character indicador;
	private String codDist;
	private Character estado;

	public TiivsFeriado() {
	}

	public TiivsFeriado(int idFeriado) {
		this.idFeriado = idFeriado;
	}

	public TiivsFeriado(int idFeriado, Date fecha, Character indicador,
			String codDist, Character estado) {
		this.idFeriado = idFeriado;
		this.fecha = fecha;
		this.indicador = indicador;
		this.codDist = codDist;
		this.estado = estado;
	}

	public int getIdFeriado() {
		return this.idFeriado;
	}

	public void setIdFeriado(int idFeriado) {
		this.idFeriado = idFeriado;
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Character getIndicador() {
		return this.indicador;
	}

	public void setIndicador(Character indicador) {
		this.indicador = indicador;
	}

	public String getCodDist() {
		return this.codDist;
	}

	public void setCodDist(String codDist) {
		this.codDist = codDist;
	}

	public Character getEstado() {
		return this.estado;
	}

	public void setEstado(Character estado) {
		this.estado = estado;
	}

}

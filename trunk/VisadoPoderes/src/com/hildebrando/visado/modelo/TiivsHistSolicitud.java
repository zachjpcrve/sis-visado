package com.hildebrando.visado.modelo;

// Generated 05/12/2012 11:17:13 AM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;

/**
 * TiivsHistSolicitud generated by hbm2java
 */
public class TiivsHistSolicitud implements java.io.Serializable {

	private TiivsHistSolicitudId id;
	private Serializable fecha;
	private String regUsuario;
	private String nomUsuario;
	private String obs;
	private String regAbogado;
	private String estado;

	public TiivsHistSolicitud() {
	}

	public TiivsHistSolicitud(TiivsHistSolicitudId id, Serializable fecha,
			String regUsuario, String nomUsuario, String estado) {
		this.id = id;
		this.fecha = fecha;
		this.regUsuario = regUsuario;
		this.nomUsuario = nomUsuario;
		this.estado = estado;
	}

	public TiivsHistSolicitud(TiivsHistSolicitudId id, Serializable fecha,
			String regUsuario, String nomUsuario, String obs,
			String regAbogado, String estado) {
		this.id = id;
		this.fecha = fecha;
		this.regUsuario = regUsuario;
		this.nomUsuario = nomUsuario;
		this.obs = obs;
		this.regAbogado = regAbogado;
		this.estado = estado;
	}

	public TiivsHistSolicitudId getId() {
		return this.id;
	}

	public void setId(TiivsHistSolicitudId id) {
		this.id = id;
	}

	public Serializable getFecha() {
		return this.fecha;
	}

	public void setFecha(Serializable fecha) {
		this.fecha = fecha;
	}

	public String getRegUsuario() {
		return this.regUsuario;
	}

	public void setRegUsuario(String regUsuario) {
		this.regUsuario = regUsuario;
	}

	public String getNomUsuario() {
		return this.nomUsuario;
	}

	public void setNomUsuario(String nomUsuario) {
		this.nomUsuario = nomUsuario;
	}

	public String getObs() {
		return this.obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getRegAbogado() {
		return this.regAbogado;
	}

	public void setRegAbogado(String regAbogado) {
		this.regAbogado = regAbogado;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}

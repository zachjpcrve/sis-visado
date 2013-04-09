package com.hildebrando.visado.modelo;

// Generated 23/01/2013 04:04:35 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * TiivsMovimientoNivel generated by hbm2java
 */
public class TiivsMovimientoNivel implements java.io.Serializable {

	private int idMovimiento;
	private TiivsSolicitudNivel tiivsSolicitudNivel;
	private String estado;
	private Integer grupo;
	private String usuarioRegistro;
	private Timestamp fechaRegistro;

	public TiivsMovimientoNivel() {
	}

	public TiivsMovimientoNivel(int idMovimiento) {
		this.idMovimiento = idMovimiento;
	}

	public TiivsMovimientoNivel(int idMovimiento,
			TiivsSolicitudNivel tiivsSolicitudNivel, String estado,
			Integer grupo, String usuarioRegistro, Timestamp fechaRegistro) {
		this.idMovimiento = idMovimiento;
		this.tiivsSolicitudNivel = tiivsSolicitudNivel;
		this.estado = estado;
		this.grupo = grupo;
		this.usuarioRegistro = usuarioRegistro;
		this.fechaRegistro = fechaRegistro;
	}

	public int getIdMovimiento() {
		return this.idMovimiento;
	}

	public void setIdMovimiento(int idMovimiento) {
		this.idMovimiento = idMovimiento;
	}

	public TiivsSolicitudNivel getTiivsSolicitudNivel() {
		return this.tiivsSolicitudNivel;
	}

	public void setTiivsSolicitudNivel(TiivsSolicitudNivel tiivsSolicitudNivel) {
		this.tiivsSolicitudNivel = tiivsSolicitudNivel;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getGrupo() {
		return this.grupo;
	}

	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}

	public String getUsuarioRegistro() {
		return this.usuarioRegistro;
	}

	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public Timestamp getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}
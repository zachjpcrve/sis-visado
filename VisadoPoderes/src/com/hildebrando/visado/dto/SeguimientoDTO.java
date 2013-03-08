package com.hildebrando.visado.dto;

import java.sql.Timestamp;

import org.apache.commons.net.ntp.TimeStamp;

import com.hildebrando.visado.modelo.TiivsHistSolicitudId;

public class SeguimientoDTO {
	
	private String estado;
	private String nivel;
	private Timestamp fecha;
	private String usuario;
	private String obs;
	private String regUsuario;
	private String codSoli;
	private String movimiento;

	public SeguimientoDTO() {

	}

	public SeguimientoDTO(String estado, String nivel, Timestamp fecha,
			String usuario, String obs) {
		super();
		this.estado = estado;
		this.nivel = nivel;
		this.fecha = fecha;
		this.usuario = usuario;
		this.obs = obs;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public Timestamp getFecha() {
		return fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getRegUsuario() {
		return regUsuario;
	}

	public void setRegUsuario(String regUsuario) {
		this.regUsuario = regUsuario;
	}

	

	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
		
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
		
	}

	public String getCodSoli() {
		return codSoli;
	}

	public String getMovimiento() {
		return movimiento;
	}
	

}

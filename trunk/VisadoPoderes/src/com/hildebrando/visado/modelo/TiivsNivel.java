package com.hildebrando.visado.modelo;

import java.util.Date;

// Generated 07/11/2012 02:39:40 PM by Hibernate Tools 3.4.0.CR1

/**
 * TiivsNivel generated by hbm2java
 */
public class TiivsNivel implements java.io.Serializable {
	private long ID;
	private String codNiv;
	private String desNiv;
	private int estado;
	private int rangoInicio;
	private int rangoFin;
	private String moneda;
	private Date fechaReg;
	private String usuarioReg;
	private Date fechaAct;
	private String usuarioAct;

	public TiivsNivel() {
	}

	public TiivsNivel(String codNiv) {
		this.codNiv = codNiv;
	}

	public TiivsNivel(Long id,String codNiv, String desNiv, int estado, int rangoInicio, int rangoFin,
					  String moneda, Date fechaReg, String usuReg, Date fechaAct, String usuAct) 
	{
		this.ID=id;
		this.codNiv = codNiv;
		this.desNiv = desNiv;
		this.estado = estado;
		this.rangoInicio=rangoInicio;
		this.rangoFin=rangoFin;
		this.moneda=moneda;
		this.fechaReg=fechaReg;
		this.usuarioReg=usuReg;
		this.usuarioAct=usuAct;
		this.fechaAct=fechaAct;
	}

	public String getCodNiv() {
		return this.codNiv;
	}

	public void setCodNiv(String codNiv) {
		this.codNiv = codNiv;
	}

	public String getDesNiv() {
		return this.desNiv;
	}

	public void setDesNiv(String desNiv) {
		this.desNiv = desNiv;
	}

	public int getEstado() {
		return this.estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public int getRangoInicio() {
		return rangoInicio;
	}

	public void setRangoInicio(int rangoInicio) {
		this.rangoInicio = rangoInicio;
	}

	public int getRangoFin() {
		return rangoFin;
	}

	public void setRangoFin(int rangoFin) {
		this.rangoFin = rangoFin;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public Date getFechaReg() {
		return fechaReg;
	}

	public void setFechaReg(Date fechaReg) {
		this.fechaReg = fechaReg;
	}

	public String getUsuarioReg() {
		return usuarioReg;
	}

	public void setUsuarioReg(String usuarioReg) {
		this.usuarioReg = usuarioReg;
	}

	public Date getFechaAct() {
		return fechaAct;
	}

	public void setFechaAct(Date fechaAct) {
		this.fechaAct = fechaAct;
	}

	public String getUsuarioAct() {
		return usuarioAct;
	}

	public void setUsuarioAct(String usuarioAct) {
		this.usuarioAct = usuarioAct;
	}
	
}

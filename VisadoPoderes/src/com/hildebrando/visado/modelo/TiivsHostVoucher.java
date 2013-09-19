package com.hildebrando.visado.modelo;

import java.sql.Timestamp;

public class TiivsHostVoucher implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private String nroVoucher;
	private String tipoPago;
	private String cuenta;
	private String montoComision;
	private String divisa;
	private String codServicio;
	private String fechaPago;
	private String horaPago;
	private String centroPago;
	private String terminalPago;
	private String usuarioPago;
	private String usuarioRegistro;
	private Timestamp fechaRegistro;
	private String estado;
	
	public String getNroVoucher() {
		return nroVoucher;
	}
	public void setNroVoucher(String nroVoucher) {
		this.nroVoucher = nroVoucher;
	}
	public String getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public String getMontoComision() {
		return montoComision;
	}
	public void setMontoComision(String montoComision) {
		this.montoComision = montoComision;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public String getCodServicio() {
		return codServicio;
	}
	public void setCodServicio(String codServicio) {
		this.codServicio = codServicio;
	}
	public String getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}
	public String getHoraPago() {
		return horaPago;
	}
	public void setHoraPago(String horaPago) {
		this.horaPago = horaPago;
	}
	public String getCentroPago() {
		return centroPago;
	}
	public void setCentroPago(String centroPago) {
		this.centroPago = centroPago;
	}
	public String getTerminalPago() {
		return terminalPago;
	}
	public void setTerminalPago(String terminalPago) {
		this.terminalPago = terminalPago;
	}
	public String getUsuarioPago() {
		return usuarioPago;
	}
	public void setUsuarioPago(String usuarioPago) {
		this.usuarioPago = usuarioPago;
	}
	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}
	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}
	public Timestamp getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}

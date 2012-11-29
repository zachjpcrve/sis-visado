package com.hildebrando.visado.modelo;

// Generated 07/11/2012 02:39:40 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * TiivsSolicitud generated by hbm2java
 */
public class TiivsSolicitud implements java.io.Serializable {

	private String codSoli;
	private TiivsTipoServicio tiivsTipoServicio;
	private TiivsOficina1 tiivsOficina1;
	private String poderante;
	private String tipDocPoder;
	private String numDocPoder;
	private String codCentral;
	private String apoderado;
	private String tipDocApoder;
	private String numDocApoder;
	private String estado;
	private Timestamp fecha;
	private String nomSolicitante;
	private String regAbogado;
	private String obs;
	private String regUsuario;
	private String nomUsuario;
	private BigDecimal importe;
	private Timestamp fechaEnvio;
	private Timestamp fechaEstado;
	private String operacionesBancarias;
	private String moneda;
	private Timestamp fechaRespuesta;
	private Set tiivsApoderdantes = new HashSet(0);
	private Set tiivsPoderdantes = new HashSet(0);
	private String desMoneda;
	private String estudio;
	private String txtPoderdante;
	private String txtApoderado;
	private String txtEstado;
	private String txtNivel;

	public TiivsSolicitud() {
	}

	public TiivsSolicitud(String codSoli, TiivsTipoServicio tiivsTipoServicio,
			TiivsOficina1 tiivsOficina1, String poderante, String tipDocPoder,
			String numDocPoder, String apoderado, String tipDocApoder,
			String numDocApoder, String estado, Timestamp fecha,
			String nomSolicitante, String regUsuario, String nomUsuario, String desMoneda) {
		this.codSoli = codSoli;
		this.tiivsTipoServicio = tiivsTipoServicio;
		this.tiivsOficina1 = tiivsOficina1;
		this.poderante = poderante;
		this.tipDocPoder = tipDocPoder;
		this.numDocPoder = numDocPoder;
		this.apoderado = apoderado;
		this.tipDocApoder = tipDocApoder;
		this.numDocApoder = numDocApoder;
		this.estado = estado;
		this.fecha = fecha;
		this.nomSolicitante = nomSolicitante;
		this.regUsuario = regUsuario;
		this.nomUsuario = nomUsuario;
		this.desMoneda=desMoneda;
	}

	public TiivsSolicitud(String codSoli, TiivsTipoServicio tiivsTipoServicio,
			TiivsOficina1 tiivsOficina1, String poderante, String tipDocPoder,
			String numDocPoder, String codCentral, String apoderado,
			String tipDocApoder, String numDocApoder, String estado,
			Timestamp fecha, String nomSolicitante, String regAbogado,
			String obs, String regUsuario, String nomUsuario,
			BigDecimal importe, Timestamp fechaEnvio,
			Timestamp fechaEstado, String operacionesBancarias,
			String moneda, Timestamp fechaRespuesta, Set tiivsApoderdantes,
			Set tiivsPoderdantes) {
		this.codSoli = codSoli;
		this.tiivsTipoServicio = tiivsTipoServicio;
		this.tiivsOficina1 = tiivsOficina1;
		this.poderante = poderante;
		this.tipDocPoder = tipDocPoder;
		this.numDocPoder = numDocPoder;
		this.codCentral = codCentral;
		this.apoderado = apoderado;
		this.tipDocApoder = tipDocApoder;
		this.numDocApoder = numDocApoder;
		this.estado = estado;
		this.fecha = fecha;
		this.nomSolicitante = nomSolicitante;
		this.regAbogado = regAbogado;
		this.obs = obs;
		this.regUsuario = regUsuario;
		this.nomUsuario = nomUsuario;
		this.importe = importe;
		this.fechaEnvio = fechaEnvio;
		this.fechaEstado = fechaEstado;
		this.operacionesBancarias = operacionesBancarias;
		this.moneda = moneda;
		this.fechaRespuesta = fechaRespuesta;
		this.tiivsApoderdantes = tiivsApoderdantes;
		this.tiivsPoderdantes = tiivsPoderdantes;
	}

	public String getCodSoli() {
		return this.codSoli;
	}

	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}

	public TiivsTipoServicio getTiivsTipoServicio() {
		return this.tiivsTipoServicio;
	}

	public void setTiivsTipoServicio(TiivsTipoServicio tiivsTipoServicio) {
		this.tiivsTipoServicio = tiivsTipoServicio;
	}

	public TiivsOficina1 getTiivsOficina1() {
		return this.tiivsOficina1;
	}

	public void setTiivsOficina1(TiivsOficina1 tiivsOficina1) {
		this.tiivsOficina1 = tiivsOficina1;
	}

	public String getPoderante() {
		return this.poderante;
	}

	public void setPoderante(String poderante) {
		this.poderante = poderante;
	}

	public String getTipDocPoder() {
		return this.tipDocPoder;
	}

	public void setTipDocPoder(String tipDocPoder) {
		this.tipDocPoder = tipDocPoder;
	}

	public String getNumDocPoder() {
		return this.numDocPoder;
	}

	public void setNumDocPoder(String numDocPoder) {
		this.numDocPoder = numDocPoder;
	}

	public String getCodCentral() {
		return this.codCentral;
	}

	public void setCodCentral(String codCentral) {
		this.codCentral = codCentral;
	}

	public String getApoderado() {
		return this.apoderado;
	}

	public void setApoderado(String apoderado) {
		this.apoderado = apoderado;
	}

	public String getTipDocApoder() {
		return this.tipDocApoder;
	}

	public void setTipDocApoder(String tipDocApoder) {
		this.tipDocApoder = tipDocApoder;
	}

	public String getNumDocApoder() {
		return this.numDocApoder;
	}

	public void setNumDocApoder(String numDocApoder) {
		this.numDocApoder = numDocApoder;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Serializable getFecha() {
		return this.fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public String getNomSolicitante() {
		return this.nomSolicitante;
	}

	public void setNomSolicitante(String nomSolicitante) {
		this.nomSolicitante = nomSolicitante;
	}

	public String getRegAbogado() {
		return this.regAbogado;
	}

	public void setRegAbogado(String regAbogado) {
		this.regAbogado = regAbogado;
	}

	public String getObs() {
		return this.obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
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

	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Serializable getFechaEnvio() {
		return this.fechaEnvio;
	}

	public void setFechaEnvio(Timestamp fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public Serializable getFechaEstado() {
		return this.fechaEstado;
	}

	public void setFechaEstado(Timestamp fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	public String getOperacionesBancarias() {
		return this.operacionesBancarias;
	}

	public void setOperacionesBancarias(String operacionesBancarias) {
		this.operacionesBancarias = operacionesBancarias;
	}

	public String getMoneda() {
		return this.moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public Serializable getFechaRespuesta() {
		return this.fechaRespuesta;
	}

	public void setFechaRespuesta(Timestamp fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}

	public Set getTiivsApoderdantes() {
		return this.tiivsApoderdantes;
	}

	public void setTiivsApoderdantes(Set tiivsApoderdantes) {
		this.tiivsApoderdantes = tiivsApoderdantes;
	}

	public Set getTiivsPoderdantes() {
		return this.tiivsPoderdantes;
	}

	public void setTiivsPoderdantes(Set tiivsPoderdantes) {
		this.tiivsPoderdantes = tiivsPoderdantes;
	}

	public String getDesMoneda() {
		return desMoneda;
	}

	public void setDesMoneda(String desMoneda) {
		this.desMoneda = desMoneda;
	}

	public String getEstudio() {
		return estudio;
	}

	public void setEstudio(String estudio) {
		this.estudio = estudio;
	}

	public String getTxtPoderdante() {
		return txtPoderdante;
	}

	public void setTxtPoderdante(String txtPoderdante) {
		this.txtPoderdante = txtPoderdante;
	}

	public String getTxtApoderado() {
		return txtApoderado;
	}

	public void setTxtApoderado(String txtApoderado) {
		this.txtApoderado = txtApoderado;
	}

	public String getTxtEstado() {
		return txtEstado;
	}

	public void setTxtEstado(String txtEstado) {
		this.txtEstado = txtEstado;
	}

	public String getTxtNivel() {
		return txtNivel;
	}

	public void setTxtNivel(String txtNivel) {
		this.txtNivel = txtNivel;
	}
}

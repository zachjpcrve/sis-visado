package com.hildebrando.visado.modelo;

// Generated 05/12/2012 11:17:13 AM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * TiivsSolicitudAgrupacion generated by hbm2java
 */
public class TiivsSolicitudAgrupacion implements java.io.Serializable {

	private BigDecimal codAgrup;
	private TiivsSolicitud tiivsSolicitud;
	private Integer numGrupo;
	private String activo;
	private Set tiivsAgrupacionPersonas = new HashSet(0);

	public TiivsSolicitudAgrupacion() {
	}

	public TiivsSolicitudAgrupacion(BigDecimal codAgrup) {
		this.codAgrup = codAgrup;
	}

	public TiivsSolicitudAgrupacion(BigDecimal codAgrup,
			TiivsSolicitud tiivsSolicitud, Integer numGrupo, String activo,
			Set tiivsAgrupacionPersonas) {
		this.codAgrup = codAgrup;
		this.tiivsSolicitud = tiivsSolicitud;
		this.numGrupo = numGrupo;
		this.activo = activo;
		this.tiivsAgrupacionPersonas = tiivsAgrupacionPersonas;
	}

	public BigDecimal getCodAgrup() {
		return this.codAgrup;
	}

	public void setCodAgrup(BigDecimal codAgrup) {
		this.codAgrup = codAgrup;
	}

	public TiivsSolicitud getTiivsSolicitud() {
		return this.tiivsSolicitud;
	}

	public void setTiivsSolicitud(TiivsSolicitud tiivsSolicitud) {
		this.tiivsSolicitud = tiivsSolicitud;
	}

	public Integer getNumGrupo() {
		return this.numGrupo;
	}

	public void setNumGrupo(Integer numGrupo) {
		this.numGrupo = numGrupo;
	}

	public String getActivo() {
		return this.activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public Set getTiivsAgrupacionPersonas() {
		return this.tiivsAgrupacionPersonas;
	}

	public void setTiivsAgrupacionPersonas(Set tiivsAgrupacionPersonas) {
		this.tiivsAgrupacionPersonas = tiivsAgrupacionPersonas;
	}

}

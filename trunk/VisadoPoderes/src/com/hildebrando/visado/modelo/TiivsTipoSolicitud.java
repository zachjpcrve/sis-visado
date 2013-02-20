package com.hildebrando.visado.modelo;

// Generated 05/12/2012 11:41:39 AM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

/**
 * TiivsTipoSolicitud generated by hbm2java
 */
public class TiivsTipoSolicitud implements java.io.Serializable {

	private String codTipSolic;
	private String desTipServicio;
	private char activo;
	private Set<TiivsSolicitud> tiivsSolicituds = new HashSet<TiivsSolicitud>();
	private Set<TiivsTipoSolicDocumento> tiivsTipoSolicDocumentos = new HashSet<TiivsTipoSolicDocumento>();
	private String desActivo;

	public TiivsTipoSolicitud() {

	}

	public TiivsTipoSolicitud(String codTipSolic) {
		this.codTipSolic= codTipSolic;
	}

	public TiivsTipoSolicitud(String codTipSolic, String desTipServicio,
			char activo) {
		this.codTipSolic = codTipSolic;
		this.desTipServicio = desTipServicio;
		this.activo = activo;
	}

	public TiivsTipoSolicitud(String codTipSolic, String desTipServicio,
			char activo, Set tiivsSolicituds, Set tiivsTipoSolicDocumentos) {
		this.codTipSolic = codTipSolic;
		this.desTipServicio = desTipServicio;
		this.activo = activo;
		this.tiivsSolicituds = tiivsSolicituds;
		this.tiivsTipoSolicDocumentos = tiivsTipoSolicDocumentos;
	}

	public String getCodTipSolic() {
		return this.codTipSolic;
	}

	public void setCodTipSolic(String codTipSolic) {
		this.codTipSolic = codTipSolic;
	}

	public String getDesTipServicio() {
		return this.desTipServicio;
	}

	public void setDesTipServicio(String desTipServicio) {
		this.desTipServicio = desTipServicio;
	}

	public char getActivo() {
		return this.activo;
	}

	public void setActivo(char activo) {
		this.activo = activo;
	}

	public Set<TiivsSolicitud> getTiivsSolicituds() {
		return tiivsSolicituds;
	}

	public void setTiivsSolicituds(Set<TiivsSolicitud> tiivsSolicituds) {
		this.tiivsSolicituds = tiivsSolicituds;
	}

	public Set<TiivsTipoSolicDocumento> getTiivsTipoSolicDocumentos() {
		return tiivsTipoSolicDocumentos;
	}

	public void setTiivsTipoSolicDocumentos(
			Set<TiivsTipoSolicDocumento> tiivsTipoSolicDocumentos) {
		this.tiivsTipoSolicDocumentos = tiivsTipoSolicDocumentos;
	}

	public String getDesActivo() {
		return desActivo;
	}

	public void setDesActivo(String desActivo) {
		this.desActivo = desActivo;
	}

}

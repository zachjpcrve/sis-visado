package com.hildebrando.visado.modelo;

// Generated 11/01/2013 11:19:20 AM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

/**
 * TiivsOficina1 generated by hbm2java
 */
public class TiivsOficina1 implements java.io.Serializable {

	private String codOfi;
	private TiivsTerritorio tiivsTerritorio;
	private String desOfi;
	private Character activo;
	private Set<TiivsSolicitud> tiivsSolicituds = new HashSet<TiivsSolicitud>();
	/**/
	private String descTerritorio;
	private String estado;
	private String descEstado;
	/**/
	
	private String descripcionMostrar;
	private String nombreDetallado;

	public TiivsOficina1() {
	}

	public TiivsOficina1(String codOfi) {
		this.codOfi = codOfi;
	}

	public TiivsOficina1(String codOfi, TiivsTerritorio tiivsTerritorio,
			String desOfi, Character activo, Set tiivsSolicituds) {
		this.codOfi = codOfi;
		this.tiivsTerritorio = tiivsTerritorio;
		this.desOfi = desOfi;
		this.activo = activo;
		this.tiivsSolicituds = tiivsSolicituds;
	}

	public String getCodOfi() {
		return this.codOfi;
	}

	public void setCodOfi(String codOfi) {
		this.codOfi = codOfi;
	}

	public TiivsTerritorio getTiivsTerritorio() {
		return this.tiivsTerritorio;
	}

	public void setTiivsTerritorio(TiivsTerritorio tiivsTerritorio) {
		this.tiivsTerritorio = tiivsTerritorio;
	}

	public String getDesOfi() {
		return this.desOfi;
	}

	public void setDesOfi(String desOfi) {
		this.desOfi = desOfi;
	}

	public Character getActivo() {
		return this.activo;
	}

	public void setActivo(Character activo) {
		this.activo = activo;
	}

	public Set getTiivsSolicituds() {
		return this.tiivsSolicituds;
	}

	public void setTiivsSolicituds(Set tiivsSolicituds) {
		this.tiivsSolicituds = tiivsSolicituds;
	}

	public String getNombreDetallado() {
		return nombreDetallado;
	}

	public void setNombreDetallado(String nombreDetallado) {
		this.nombreDetallado = nombreDetallado;
	}

	public String getDescTerritorio() {
		return descTerritorio;
	}

	public void setDescTerritorio(String descTerritorio) {
		this.descTerritorio = descTerritorio;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getDescEstado() {
		return descEstado;
	}

	public void setDescEstado(String descEstado) {
		this.descEstado = descEstado;
	}

	public String getDescripcionMostrar() {
		if(getCodOfi()!=null){
		if(getCodOfi()!=""){
		descripcionMostrar=getCodOfi()+"-"+getDesOfi()+"("+getTiivsTerritorio().getCodTer()+"-"+getTiivsTerritorio().getDesTer()+")";
		}}
		return this.descripcionMostrar;
	}

	public void setDescripcionMostrar(String descripcionMostrar) {
		this.descripcionMostrar = descripcionMostrar;
	}
	
	

}

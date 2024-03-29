package com.hildebrando.visado.modelo;

// Generated 12/12/2012 12:08:20 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

/**
 * TiivsMiembro generated by hbm2java
 */
public class TiivsMiembro implements java.io.Serializable {

	private String codMiembro;
	private TiivsGrupo tiivsGrupo;
	private String descripcion;
	private String criterio;
	private String estudio;
	private Set<TiivsMiembroNivel> tiivsMiembroNivels = new HashSet<TiivsMiembroNivel>();
	private String activo;

	public TiivsMiembro() {
	}

	public TiivsMiembro(String codMiembro, String descripcion, String criterio) {
		this.codMiembro = codMiembro;
		this.descripcion = descripcion;
		this.criterio = criterio;
	}

	public TiivsMiembro(String codMiembro, TiivsGrupo tiivsGrupo,
			String descripcion, String criterio, String estudio,
			Set tiivsMiembroNivels) {
		this.codMiembro = codMiembro;
		this.tiivsGrupo = tiivsGrupo;
		this.descripcion = descripcion;
		this.criterio = criterio;
		this.estudio = estudio;
		this.tiivsMiembroNivels = tiivsMiembroNivels;
	}

	public String getCodMiembro() {
		return this.codMiembro;
	}

	public void setCodMiembro(String codMiembro) {
		this.codMiembro = codMiembro;
	}

	public TiivsGrupo getTiivsGrupo() {
		return this.tiivsGrupo;
	}

	public void setTiivsGrupo(TiivsGrupo tiivsGrupo) {
		this.tiivsGrupo = tiivsGrupo;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCriterio() {
		return this.criterio;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	public String getEstudio() {
		return this.estudio;
	}

	public void setEstudio(String estudio) {
		this.estudio = estudio;
	}

	

	public Set<TiivsMiembroNivel> getTiivsMiembroNivels() {
		return this.tiivsMiembroNivels;
	}

	public void setTiivsMiembroNivels(Set<TiivsMiembroNivel> tiivsMiembroNivels) {
		this.tiivsMiembroNivels = tiivsMiembroNivels;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	
}

package com.hildebrando.visado.modelo;

// Generated 12/12/2012 12:08:20 PM by Hibernate Tools 3.4.0.CR1

import java.sql.Timestamp;
import java.util.List;

/**
 * TiivsMiembroNivel generated by hbm2java
 */
public class TiivsMiembroNivel implements java.io.Serializable {

	private int id;
	private TiivsMiembro tiivsMiembro;
	private String codNiv;
	private Integer grupo;
	private String tipoRol;
	private String estado;
	private String usuarioRegistro;
	private Timestamp fechaRegistro;
	private String usuarioAct;
	private Timestamp fechaAct;
	private String estadoMiembro;
	
	/** Samira*/
	private String descNiv;
	private String descEstado;
	public String getDescEstado() {
		return this.descEstado;
	}

	public void setDescEstado(String descEstado) {
		this.descEstado = descEstado;
	}

	private List<TiivsNivel> listaNiveles;
	/** Fin  Samira*/
	
	public TiivsMiembroNivel() {
	}

	public TiivsMiembroNivel(int id) {
		this.id = id;
	}

	public TiivsMiembroNivel(int id, TiivsMiembro tiivsMiembro, String codNiv,
			Integer grupo, String tipoRol, String estado,
			String usuarioRegistro, Timestamp fechaRegistro,
			String usuarioAct, Timestamp fechaAct) {
		this.id = id;
		this.tiivsMiembro = tiivsMiembro;
		this.codNiv = codNiv;
		this.grupo = grupo;
		this.tipoRol = tipoRol;
		this.estado = estado;
		this.usuarioRegistro = usuarioRegistro;
		this.fechaRegistro = fechaRegistro;
		this.usuarioAct = usuarioAct;
		this.fechaAct = fechaAct;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TiivsMiembro getTiivsMiembro() {
		return this.tiivsMiembro;
	}

	public void setTiivsMiembro(TiivsMiembro tiivsMiembro) {
		this.tiivsMiembro = tiivsMiembro;
	}

	public String getCodNiv() {
		return this.codNiv;
	}

	public void setCodNiv(String codNiv) {
		this.codNiv = codNiv;
	}

	public Integer getGrupo() {
		return this.grupo;
	}

	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}

	public String getTipoRol() {
		return this.tipoRol;
	}

	public void setTipoRol(String tipoRol) {
		this.tipoRol = tipoRol;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
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

	public String getUsuarioAct() {
		return this.usuarioAct;
	}

	public void setUsuarioAct(String usuarioAct) {
		this.usuarioAct = usuarioAct;
	}

	public Timestamp getFechaAct() {
		return this.fechaAct;
	}

	public void setFechaAct(Timestamp fechaAct) {
		this.fechaAct = fechaAct;
	}

	public String getEstadoMiembro() {
		return estadoMiembro;
	}

	public void setEstadoMiembro(String estadoMiembro) {
		this.estadoMiembro = estadoMiembro;
	}

	public String getDescNiv() {
		return this.descNiv;
	}

	public void setDescNiv(String descNiv) {
		this.descNiv = descNiv;
	}

	public List<TiivsNivel> getListaNiveles() {
		return this.listaNiveles;
	}

	public void setListaNiveles(List<TiivsNivel> listaNiveles) {
		this.listaNiveles = listaNiveles;
	}

	@Override
	public String toString() {
		return "TiivsMiembroNivel [id=" + this.id + ", tiivsMiembro="
				+ this.tiivsMiembro + ", codNiv=" + this.codNiv + ", grupo="
				+ this.grupo + ", tipoRol=" + this.tipoRol + ", estado="
				+ this.estado + ", usuarioRegistro=" + this.usuarioRegistro
				+ ", fechaRegistro=" + this.fechaRegistro + ", usuarioAct="
				+ this.usuarioAct + ", fechaAct=" + this.fechaAct
				+ ", estadoMiembro=" + this.estadoMiembro + ", descNiv="
				+ this.descNiv + ", listaNiveles.size() =" + this.listaNiveles.size()+ "]";
	}

	
	
	
}

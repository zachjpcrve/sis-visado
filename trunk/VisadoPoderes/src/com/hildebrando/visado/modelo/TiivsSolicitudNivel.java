package com.hildebrando.visado.modelo;

// Generated 23/01/2013 04:04:35 PM by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * TiivsSolicitudNivel generated by hbm2java
 */
public class TiivsSolicitudNivel implements java.io.Serializable{

	private int id;
	private TiivsSolicitud tiivsSolicitud;
	private String codNiv;
	private String estadoNivel;
	private String usuarioRegistro;
	private Timestamp fechaRegistro;
	private String estadoSolicitud;
	private Set<TiivsMovimientoNivel> tiivsMovimientoNivels = new HashSet<TiivsMovimientoNivel>();

	public TiivsSolicitudNivel() {
	}

	public TiivsSolicitudNivel(int id, TiivsSolicitud tiivsSolicitud,
			String codNiv) {
		this.id = id;
		this.tiivsSolicitud = tiivsSolicitud;
		this.codNiv = codNiv;
	}

	public TiivsSolicitudNivel(int id, TiivsSolicitud tiivsSolicitud,
			String codNiv, String estadoNivel, String usuarioRegistro,
			Timestamp fechaRegistro, String estadoSolicitud,
			Set tiivsMovimientoNivels) {
		this.id = id;
		this.tiivsSolicitud = tiivsSolicitud;
		this.codNiv = codNiv;
		this.estadoNivel = estadoNivel;
		this.usuarioRegistro = usuarioRegistro;
		this.fechaRegistro = fechaRegistro;
		this.estadoSolicitud = estadoSolicitud;
		this.tiivsMovimientoNivels = tiivsMovimientoNivels;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TiivsSolicitud getTiivsSolicitud() {
		return this.tiivsSolicitud;
	}

	public void setTiivsSolicitud(TiivsSolicitud tiivsSolicitud) {
		this.tiivsSolicitud = tiivsSolicitud;
	}

	public String getCodNiv() {
		return this.codNiv;
	}

	public void setCodNiv(String codNiv) {
		this.codNiv = codNiv;
	}

	public String getEstadoNivel() {
		return this.estadoNivel;
	}

	public void setEstadoNivel(String estadoNivel) {
		this.estadoNivel = estadoNivel;
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

	public String getEstadoSolicitud() {
		return this.estadoSolicitud;
	}

	public void setEstadoSolicitud(String estadoSolicitud) {
		this.estadoSolicitud = estadoSolicitud;
	}

	public Set getTiivsMovimientoNivels() {
		return this.tiivsMovimientoNivels;
	}

	public void setTiivsMovimientoNivels(Set tiivsMovimientoNivels) {
		this.tiivsMovimientoNivels = tiivsMovimientoNivels;
	}

}

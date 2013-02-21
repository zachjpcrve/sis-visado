package com.hildebrando.visado.dto;

import java.io.Serializable;

public class MiembroNivelDTO implements Serializable {
	
	private int id;
	private String codNivel;
	private String desNivel;
	private String registro;
	private String descripcion;
	private String codGrupo;
	private String desGrupo;
	private String fechaRegistroToString;
	private String usuarioRegistro;
	private String estado;
	
	public MiembroNivelDTO(int id,String codNivel, String desNivel, String registro,
			String descripcion, String codGrupo, String desGrupo,
			String fechaRegistroToString, String usuarioRegistro, String estado) {
		super();
		this.id = id;
		this.codNivel = codNivel;
		this.desNivel = desNivel;
		this.registro = registro;
		this.descripcion = descripcion;
		this.codGrupo = codGrupo;
		this.desGrupo = desGrupo;
		this.fechaRegistroToString = fechaRegistroToString;
		this.usuarioRegistro = usuarioRegistro;
		this.estado = estado;
	}
	public MiembroNivelDTO() {
		// TODO Auto-generated constructor stub
	}
	public String getRegistro() {
		return registro;
	}
	public void setRegistro(String registro) {
		this.registro = registro;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getCodGrupo() {
		return codGrupo;
	}
	public void setCodGrupo(String codGrupo) {
		this.codGrupo = codGrupo;
	}
	public String getFechaRegistroToString() {
		return fechaRegistroToString;
	}
	public void setFechaRegistroToString(String fechaRegistroToString) {
		this.fechaRegistroToString = fechaRegistroToString;
	}
	public String getUsuarioRegistro() {
		return usuarioRegistro;
	}
	public void setUsuarioRegistro(String usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getDesGrupo() {
		return desGrupo;
	}
	public void setDesGrupo(String desGrupo) {
		this.desGrupo = desGrupo;
	}
	public String getCodNivel() {
		return codNivel;
	}
	public void setCodNivel(String codNivel) {
		this.codNivel = codNivel;
	}
	public String getDesNivel() {
		return desNivel;
	}
	public void setDesNivel(String desNivel) {
		this.desNivel = desNivel;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	

}

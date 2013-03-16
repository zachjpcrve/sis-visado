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
	
	private String fechaActualizacionToString;
	private String usuarioActualizacion;
	
	private String codEstado;
	private String estado;
	
	private int rangoInicioSoles;
	private int rangoFinSoles;
	
	private int rangoInicioDolares;
	private int rangoFinDolares;
	
	private int rangoInicioEuros;
	private int rangoFinEuros;
	private String labelAccion;
	private int nuevo;
	
	public MiembroNivelDTO(int nuevo, int id,String codNivel, String desNivel, String registro,
			String descripcion, String codGrupo, String desGrupo,
			String fechaRegistroToString, String usuarioRegistro,String codEstado, String estado,
			int rangoInicioSoles,int rangoFinSoles,int rangoInicioDolares,
			int rangoFinDolares,int rangoInicioEuros,int rangoFinEuros,String labelAccion) 
	{
		super();
		
		this.nuevo =  nuevo;
		this.id = id;
		this.codNivel = codNivel;
		this.desNivel = desNivel;
		this.registro = registro;
		this.descripcion = descripcion;
		this.codGrupo = codGrupo;
		this.desGrupo = desGrupo;
		this.fechaRegistroToString = fechaRegistroToString;
		this.usuarioRegistro = usuarioRegistro;
		this.codEstado = codEstado;
		this.estado = estado;
		this.rangoInicioSoles = rangoInicioSoles;
		this.rangoFinSoles = rangoFinSoles;
		this.rangoInicioDolares = rangoInicioDolares;
		this.rangoFinDolares = rangoFinDolares;
		this.rangoInicioEuros = rangoInicioEuros;
		this.rangoFinEuros = rangoFinEuros;
		this.labelAccion=labelAccion;
	}
	public MiembroNivelDTO() {
		
	}
	public MiembroNivelDTO(int id,String codNivel, String desNivel, String registro,
			String descripcion, String codGrupo, String desGrupo,
			String fechaRegistroToString, String usuarioRegistro,String codEstado, String estado) {
		this.id = id;
		this.codNivel = codNivel;
		this.desNivel = desNivel;
		this.registro = registro;
		this.descripcion = descripcion;
		this.codGrupo = codGrupo;
		this.desGrupo = desGrupo;
		this.fechaRegistroToString = fechaRegistroToString;
		this.usuarioRegistro = usuarioRegistro;
		this.codEstado = codEstado;
		this.estado = estado;
	}
	public MiembroNivelDTO(int nuevo,String descripcion, String codNiv, String desNivel, String codMiembro,
			String fechaRegistroToString, String usuarioRegistro, String codEstado, String descEstado,
			int ris, int rfs, int rid, int rfd, int rie, int rfe, String labelAccion) {
		
		this.nuevo =  nuevo;
		this.descripcion = descripcion;
		this.codNivel = codNiv;
		this.desNivel = desNivel;
		this.registro = codMiembro;
		this.fechaRegistroToString = fechaRegistroToString;
		this.usuarioRegistro = usuarioRegistro;
		this.codEstado = codEstado;
		this.estado = descEstado;
		this.rangoInicioSoles = ris;
		this.rangoFinSoles = rfs;
		this.rangoInicioDolares = rid;
		this.rangoFinDolares = rfd;
		this.rangoInicioEuros = rie;
		this.rangoFinEuros = rfe;
	    this.labelAccion=labelAccion;
	}
	
	public MiembroNivelDTO(int id, String codNivel, String desNivel,
			String registro, String descripcion, String codGrupo,
			String desGrupo, String fechaRegistroToString,
			String usuarioRegistro, String fechaActualizacionToString,
			String usuarioActualizacion, String codEstado, String estado,
			int rangoInicioSoles, int rangoFinSoles, int rangoInicioDolares,
			int rangoFinDolares, int rangoInicioEuros, int rangoFinEuros) {
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
		this.fechaActualizacionToString = fechaActualizacionToString;
		this.usuarioActualizacion = usuarioActualizacion;
		this.codEstado = codEstado;
		this.estado = estado;
		this.rangoInicioSoles = rangoInicioSoles;
		this.rangoFinSoles = rangoFinSoles;
		this.rangoInicioDolares = rangoInicioDolares;
		this.rangoFinDolares = rangoFinDolares;
		this.rangoInicioEuros = rangoInicioEuros;
		this.rangoFinEuros = rangoFinEuros;
		
	}
	
	
	public MiembroNivelDTO(int id, String codNivel, String desNivel,
			String registro, String descripcion, String codGrupo,
			String desGrupo, String fechaRegistroToString,
			String usuarioRegistro, String fechaActualizacionToString,
			String usuarioActualizacion, String codEstado, String estado,
			int rangoInicioSoles, int rangoFinSoles, int rangoInicioDolares,
			int rangoFinDolares, int rangoInicioEuros, int rangoFinEuros,
			String labelAccion) {
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
		this.fechaActualizacionToString = fechaActualizacionToString;
		this.usuarioActualizacion = usuarioActualizacion;
		this.codEstado = codEstado;
		this.estado = estado;
		this.rangoInicioSoles = rangoInicioSoles;
		this.rangoFinSoles = rangoFinSoles;
		this.rangoInicioDolares = rangoInicioDolares;
		this.rangoFinDolares = rangoFinDolares;
		this.rangoInicioEuros = rangoInicioEuros;
		this.rangoFinEuros = rangoFinEuros;
		this.labelAccion = labelAccion;
	}

	public MiembroNivelDTO(int i, String descripcion2, String codNiv,
			String desNivel2, String codMiembro, String string, String uid,
			String estado2, String descEstado, int ris, int rfs, int rid,
			int rfd, int rie, int rfe) {
		// TODO Apéndice de constructor generado automáticamente
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
	public String getCodEstado() {
		return codEstado;
	}
	public void setCodEstado(String codEstado) {
		this.codEstado = codEstado;
	}
	public String getFechaActualizacionToString() {
		return fechaActualizacionToString;
	}
	public void setFechaActualizacionToString(String fechaActualizacionToString) {
		this.fechaActualizacionToString = fechaActualizacionToString;
	}
	public String getUsuarioActualizacion() {
		return usuarioActualizacion;
	}
	public void setUsuarioActualizacion(String usuarioActualizacion) {
		this.usuarioActualizacion = usuarioActualizacion;
	}
	public int getRangoInicioSoles() {
		return rangoInicioSoles;
	}
	public void setRangoInicioSoles(int rangoInicioSoles) {
		this.rangoInicioSoles = rangoInicioSoles;
	}
	public int getRangoFinSoles() {
		return rangoFinSoles;
	}
	public void setRangoFinSoles(int rangoFinSoles) {
		this.rangoFinSoles = rangoFinSoles;
	}
	public int getRangoInicioDolares() {
		return rangoInicioDolares;
	}
	public void setRangoInicioDolares(int rangoInicioDolares) {
		this.rangoInicioDolares = rangoInicioDolares;
	}
	public int getRangoFinDolares() {
		return rangoFinDolares;
	}
	public void setRangoFinDolares(int rangoFinDolares) {
		this.rangoFinDolares = rangoFinDolares;
	}
	public int getRangoInicioEuros() {
		return rangoInicioEuros;
	}
	public void setRangoInicioEuros(int rangoInicioEuros) {
		this.rangoInicioEuros = rangoInicioEuros;
	}
	public int getRangoFinEuros() {
		return rangoFinEuros;
	}
	public void setRangoFinEuros(int rangoFinEuros) {
		this.rangoFinEuros = rangoFinEuros;
	}
	public int getNuevo() {
		return nuevo;
	}
	public void setNuevo(int nuevo) {
		this.nuevo = nuevo;
	}
	public String getLabelAccion() {
		return this.labelAccion;
	}
	public void setLabelAccion(String labelAccion) {
		this.labelAccion = labelAccion;
	}
	
}

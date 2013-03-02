package com.hildebrando.visado.dto;

public class AgrupacionDelegadosDto {
	
	private String grupo;
	private String des_niv;
	private String cod_miembro;
	private String descripcion;
	private String estado;
	
	public String getGrupo() {
		return this.grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getDes_niv() {
		return this.des_niv;
	}
	public void setDes_niv(String des_niv) {
		this.des_niv = des_niv;
	}
	public String getCod_miembro() {
		return this.cod_miembro;
	}
	public void setCod_miembro(String cod_miembro) {
		this.cod_miembro = cod_miembro;
	}
	public String getDescripcion() {
		return this.descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
}

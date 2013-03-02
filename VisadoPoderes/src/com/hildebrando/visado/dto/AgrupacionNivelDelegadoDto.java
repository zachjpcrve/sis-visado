package com.hildebrando.visado.dto;

import java.util.List;

public class AgrupacionNivelDelegadoDto {
    private String codNivel;
    private String codGrupo;
	private String nivel;
	private String estado;
	private String estadoEtiqueta;
	
	private String cod_delegado_A;
	private String cod_delegado_B;
	private String cod_delegado_C;
	private String cod_delegado_D;
	private String cod_delegado_E;
	
	private String cod_nombre_delegado_A;
	private String cod_nombre_delegado_B;
	private String cod_nombre_delegado_C;
	private String cod_nombre_delegado_D;
	private String cod_nombre_delegado_E;
	
	private List<ComboDto> lstDelegados;

	
	public String getCodNivel() {
		return this.codNivel;
	}
	public void setCodNivel(String codNivel) {
		this.codNivel = codNivel;
	}
	public String getNivel() {
		return this.nivel;
	}
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	public String getCod_delegado_A() {
		return this.cod_delegado_A;
	}
	public void setCod_delegado_A(String cod_delegado_A) {
		this.cod_delegado_A = cod_delegado_A;
	}
	public String getCod_delegado_B() {
		return this.cod_delegado_B;
	}
	public void setCod_delegado_B(String cod_delegado_B) {
		this.cod_delegado_B = cod_delegado_B;
	}
	public String getCod_delegado_C() {
		return this.cod_delegado_C;
	}
	public void setCod_delegado_C(String cod_delegado_C) {
		this.cod_delegado_C = cod_delegado_C;
	}
	public String getCod_delegado_D() {
		return this.cod_delegado_D;
	}
	public void setCod_delegado_D(String cod_delegado_D) {
		this.cod_delegado_D = cod_delegado_D;
	}
	public String getCod_delegado_E() {
		return this.cod_delegado_E;
	}
	public void setCod_delegado_E(String cod_delegado_E) {
		this.cod_delegado_E = cod_delegado_E;
	}
	public String getCodGrupo() {
		return this.codGrupo;
	}
	public void setCodGrupo(String codGrupo) {
		this.codGrupo = codGrupo;
	}
	public List<ComboDto> getLstDelegados() {
		return this.lstDelegados;
	}
	public void setLstDelegados(List<ComboDto> lstDelegados) {
		this.lstDelegados = lstDelegados;
	}
	public String getCod_nombre_delegado_A() {
		return this.cod_nombre_delegado_A;
	}
	public void setCod_nombre_delegado_A(String cod_nombre_delegado_A) {
		this.cod_nombre_delegado_A = cod_nombre_delegado_A;
	}
	public String getCod_nombre_delegado_B() {
		return this.cod_nombre_delegado_B;
	}
	public void setCod_nombre_delegado_B(String cod_nombre_delegado_B) {
		this.cod_nombre_delegado_B = cod_nombre_delegado_B;
	}
	public String getCod_nombre_delegado_C() {
		return this.cod_nombre_delegado_C;
	}
	public void setCod_nombre_delegado_C(String cod_nombre_delegado_C) {
		this.cod_nombre_delegado_C = cod_nombre_delegado_C;
	}
	public String getCod_nombre_delegado_D() {
		return this.cod_nombre_delegado_D;
	}
	public void setCod_nombre_delegado_D(String cod_nombre_delegado_D) {
		this.cod_nombre_delegado_D = cod_nombre_delegado_D;
	}
	public String getCod_nombre_delegado_E() {
		return this.cod_nombre_delegado_E;
	}
	public void setCod_nombre_delegado_E(String cod_nombre_delegado_E) {
		this.cod_nombre_delegado_E = cod_nombre_delegado_E;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getEstadoEtiqueta() {
		return estadoEtiqueta;
	}
	public void setEstadoEtiqueta(String estadoEtiqueta) {
		this.estadoEtiqueta = estadoEtiqueta;
	}

}

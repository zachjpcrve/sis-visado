package com.hildebrando.visado.dto;

import java.util.List;

public class ComboDto {
	private String key;
	private String descripcion;
	private Integer numGrupo;

	private List<ComboDto>  listaPersonas;
	
	
	public ComboDto() {
		super();
	}

	public ComboDto(String key, String descripcion) {
		this.key = key;
		this.descripcion = descripcion;
	}


	public ComboDto(Integer numGrupo, List<ComboDto> listaPersonas) {
		super();
		this.numGrupo = numGrupo;
		this.listaPersonas = listaPersonas;
	}

	public ComboDto(String key, String descripcion, Integer numGrupo) {
		super();
		this.key = key;
		this.descripcion = descripcion;
		this.numGrupo = numGrupo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getNumGrupo() {
		return this.numGrupo;
	}

	public void setNumGrupo(Integer numGrupo) {
		this.numGrupo = numGrupo;
	}

	@Override
	public String toString() {
		return "ComboDto [key=" + this.key + ", descripcion="
				+ this.descripcion + ", numGrupo=" + this.numGrupo + "]";
	}

	public List<ComboDto> getListaPersonas() {
		return this.listaPersonas;
	}

	public void setListaPersonas(List<ComboDto> listaPersonas) {
		this.listaPersonas = listaPersonas;
	}

	

}

package com.hildebrando.visado.dto;

public class ComboDto {
	private String key;
	private String descripcion;

	
	
	
	public ComboDto() {
		super();
	}

	public ComboDto(String key, String descripcion) {
		this.key = key;
		this.descripcion = descripcion;
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

}

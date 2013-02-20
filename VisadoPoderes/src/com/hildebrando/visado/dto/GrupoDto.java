package com.hildebrando.visado.dto;

import java.io.Serializable;

public class GrupoDto implements Serializable {
	
	private String cod;
	private String des;
	
	
	
	public GrupoDto(String cod, String des) {
		super();
		this.cod = cod;
		this.des = des;
	}
	
	public String getCod() {
		return cod;
	}
	public void setCod(String cod) {
		this.cod = cod;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	
	
}

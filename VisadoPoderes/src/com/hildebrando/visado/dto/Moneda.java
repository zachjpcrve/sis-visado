package com.hildebrando.visado.dto;

import java.io.Serializable;

public class Moneda implements Serializable{
	private String codMoneda;
	private String desMoneda;
	
	public String getCodMoneda() {
		return codMoneda;
	}
	public void setCodMoneda(String codMoneda) {
		this.codMoneda = codMoneda;
	}
	public String getDesMoneda() {
		return desMoneda;
	}
	public void setDesMoneda(String desMoneda) {
		this.desMoneda = desMoneda;
	}
}

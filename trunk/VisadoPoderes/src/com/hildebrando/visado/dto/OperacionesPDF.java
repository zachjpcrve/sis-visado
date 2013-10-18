package com.hildebrando.visado.dto;

import java.io.Serializable;

public class OperacionesPDF implements Serializable{
	private String item;
	private String descripcion;
	private String moneda;
	private String importe;
	private Double tipoCambio;
	private String soles;
	
	public OperacionesPDF(String item, String descripcion, String moneda, String importe, Double tipoCambio, String soles){
		this.item = item;
		this.descripcion = descripcion;
		this.moneda = moneda;
		this.importe = importe;
		this.tipoCambio = tipoCambio;
		this.soles = soles;
	}
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getImporte() {
		return importe;
	}
	public void setImporte(String importe) {
		this.importe = importe;
	}
	public Double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(Double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getSoles() {
		return soles;
	}
	public void setSoles(String soles) {
		this.soles = soles;
	}
}

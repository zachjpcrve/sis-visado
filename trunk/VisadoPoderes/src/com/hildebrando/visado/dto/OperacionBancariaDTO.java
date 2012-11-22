package com.hildebrando.visado.dto;

public class OperacionBancariaDTO {
	private String item;
	private String operacion_bancaria;
	private String moneda;
	private String importe;
	private String tipo_cambio;
	private String importe_soles;
	
	
	
	public OperacionBancariaDTO(String item, String operacion_bancaria,
			String moneda, String importe, String tipo_cambio,
			String importe_soles) {
		super();
		this.item = item;
		this.operacion_bancaria = operacion_bancaria;
		this.moneda = moneda;
		this.importe = importe;
		this.tipo_cambio = tipo_cambio;
		this.importe_soles = importe_soles;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getOperacion_bancaria() {
		return operacion_bancaria;
	}
	public void setOperacion_bancaria(String operacion_bancaria) {
		this.operacion_bancaria = operacion_bancaria;
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
	public String getTipo_cambio() {
		return tipo_cambio;
	}
	public void setTipo_cambio(String tipo_cambio) {
		this.tipo_cambio = tipo_cambio;
	}
	public String getImporte_soles() {
		return importe_soles;
	}
	public void setImporte_soles(String importe_soles) {
		this.importe_soles = importe_soles;
	}


	
}

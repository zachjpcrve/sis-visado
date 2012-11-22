package com.hildebrando.visado.dto;

public class ApoderadoDTO {

	private String codigo_central;
	private String cliente;
	private String tipo_registro;
	private String clasificacion;
	private String nro_celular;
	private String e_mail;
	
	
	public ApoderadoDTO(String codigo_central, String cliente,
			String tipo_registro, String clasificacion, String nro_celular,
			String e_mail) {
		super();
		this.codigo_central = codigo_central;
		this.cliente = cliente;
		this.tipo_registro = tipo_registro;
		this.clasificacion = clasificacion;
		this.nro_celular = nro_celular;
		this.e_mail = e_mail;
	}
	
	
	public String getCodigo_central() {
		return codigo_central;
	}
	public void setCodigo_central(String codigo_central) {
		this.codigo_central = codigo_central;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getTipo_registro() {
		return tipo_registro;
	}
	public void setTipo_registro(String tipo_registro) {
		this.tipo_registro = tipo_registro;
	}
	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	public String getNro_celular() {
		return nro_celular;
	}
	public void setNro_celular(String nro_celular) {
		this.nro_celular = nro_celular;
	}
	public String getE_mail() {
		return e_mail;
	}
	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}
	
	
	
}

package com.hildebrando.visado.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class PersonaPDF implements Serializable 
{
	private String numDOI;
	private String cliente;
	private String tipoRegistro;
	private String clasificacion;
	private String celular;
	private String email;
	private String estado;
	private String numDoi;

	
	public String getNumDOI() {
		return numDOI;
	}
	public void setNumDOI(String numDOI) {
		this.numDOI = numDOI;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getTipoRegistro() {
		return tipoRegistro;
	}
	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}
	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getNumDoi() {
		return numDoi;
	}
	public void setNumDoi(String numDoi) {
		this.numDoi = numDoi;
	}


	
	
}

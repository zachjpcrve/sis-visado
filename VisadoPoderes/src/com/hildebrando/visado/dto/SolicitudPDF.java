package com.hildebrando.visado.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class SolicitudPDF implements Serializable 
{
	private String codSoli;
	private String estado;
	private String nroVoucher;
	private String comision;
	private String oficina;
	private String importe;
	private  JRDataSource PERSONADS;
	private List<PersonaPDF> lstPersonas=new ArrayList<PersonaPDF>();	
	
	
	public JRDataSource getPERSONADS() {
		return new JRBeanCollectionDataSource(lstPersonas); 
	}
	public String getCodSoli() {
		return codSoli;
	}
	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getNroVoucher() {
		return nroVoucher;
	}
	public void setNroVoucher(String nroVoucher) {
		this.nroVoucher = nroVoucher;
	}
	public String getComision() {
		return comision;
	}
	public void setComision(String comision) {
		this.comision = comision;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getImporte() {
		return importe;
	}
	public void setImporte(String importe) {
		this.importe = importe;
	}
	public List<PersonaPDF> getLstPersonas() {
		return lstPersonas;
	}

	public void setLstPersonas(List<PersonaPDF> lstPersonas) {
		this.lstPersonas = lstPersonas;
	}
}

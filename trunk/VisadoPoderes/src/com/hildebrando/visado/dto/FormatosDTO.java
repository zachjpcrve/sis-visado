package com.hildebrando.visado.dto;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

public class FormatosDTO {
	
	private String numeroSolicitud;
	private String instrucciones;
	private String numeroDiasForEjecucion;
	private String oficina;
	private List<OperacionesPDF> lstOperaciones=new ArrayList<OperacionesPDF>();
	private JRDataSource lstOperacionesDS;
	private String poderdantes;
	
	private String observaciones;

	public JRDataSource getLstOperacionesDS() {
		return new JRBeanCollectionDataSource(lstOperaciones); 
	}

	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public void setNumeroSolicitud(String numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
	}

	public String getInstrucciones() {
		return instrucciones;
	}

	public void setInstrucciones(String instrucciones) {
		this.instrucciones = instrucciones;
	}

	public String getNumeroDiasForEjecucion() {
		return numeroDiasForEjecucion;
	}

	public void setNumeroDiasForEjecucion(String numeroDiasForEjecucion) {
		this.numeroDiasForEjecucion = numeroDiasForEjecucion;
	}

	public String getOficina() {
		return oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	public String getPoderdantes() {
		return poderdantes;
	}

	public void setPoderdantes(String poderdantes) {
		this.poderdantes = poderdantes;
	}
	
	public List<OperacionesPDF> getLstOperaciones() {
		return lstOperaciones;
	}

	public void setLstOperaciones(List<OperacionesPDF> lstOperaciones) {
		this.lstOperaciones = lstOperaciones;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}	

	
}
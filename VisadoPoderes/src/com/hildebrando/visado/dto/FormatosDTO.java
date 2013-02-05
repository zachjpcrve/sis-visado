package com.hildebrando.visado.dto;

import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

public class FormatosDTO {
	
	private String numeroSolicitud;
	private String instrucciones;
	private String numeroDiasForEjecucion;
	private String oficina;
	private List<TiivsSolicitudOperban> lstSolicitudOperban;
	private List<JRDataSource> lstDsSolicitudOperban;
	private JRDataSource Operaciones ;

	
	
	public JRDataSource getOperaciones() {
		return new JRBeanCollectionDataSource(lstDsSolicitudOperban); 
	}

	
	public void setOperaciones(JRDataSource operaciones) {
		Operaciones = operaciones;
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

	public List<TiivsSolicitudOperban> getLstSolicitudOperban() {
		return lstSolicitudOperban;
	}

	public void setLstSolicitudOperban(List<TiivsSolicitudOperban> lstSolicitudOperban) {
		this.lstSolicitudOperban = lstSolicitudOperban;
	}


	public List<JRDataSource> getLstDsSolicitudOperban() {
		return lstDsSolicitudOperban;
	}


	public void setLstDsSolicitudOperban(List<JRDataSource> lstDsSolicitudOperban) {
		this.lstDsSolicitudOperban = lstDsSolicitudOperban;
	}



	

	
	
}

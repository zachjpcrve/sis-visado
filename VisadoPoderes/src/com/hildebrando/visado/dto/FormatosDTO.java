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
	private List<TiivsSolicitudOperban> listaSolicitudOperban;
	private JRDataSource lstSolicitudOperban ;

	
	
	public JRDataSource getLstSolicitudOperban() {
		return new JRBeanCollectionDataSource(listaSolicitudOperban); 
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




	public List<TiivsSolicitudOperban> getListaSolicitudOperban() {
		return listaSolicitudOperban;
	}




	public void setListaSolicitudOperban(
			List<TiivsSolicitudOperban> listaSolicitudOperban) {
		this.listaSolicitudOperban = listaSolicitudOperban;
	}

	

	


	

	
	
}
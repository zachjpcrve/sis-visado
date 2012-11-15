package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.hildebrando.visado.dto.Solicitud;

@ManagedBean(name = "solicitudMB")
@SessionScoped
public class SolicitudMB {

	Solicitud solicitudModificar;
	Solicitud bandeja;
	private List<Solicitud> solicitudes;
	private String textoTotalResultados;
	
	public SolicitudMB() {
		solicitudModificar = new Solicitud();
		solicitudes= new ArrayList<Solicitud>();
		
		if (solicitudes.size()==0)
		{
			textoTotalResultados="No se encontraron coincidencias con los criterios ingresados";
		}
		else
		{
			textoTotalResultados="Total de registros: "+ solicitudes.size() + " registros";
		}
	}

	public Solicitud getSolicitudModificar() {
		return solicitudModificar;
	}

	public void setSolicitudModificar(Solicitud solicitudModificar) {
		this.solicitudModificar = solicitudModificar;
	}

	public Solicitud getBandeja() {
		return bandeja;
	}

	public void setBandeja(Solicitud bandeja) {
		this.bandeja = bandeja;
	}

	public List<Solicitud> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(List<Solicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

	public String getTextoTotalResultados() {
		return textoTotalResultados;
	}

	public void setTextoTotalResultados(String textoTotalResultados) {
		this.textoTotalResultados = textoTotalResultados;
	}

	
}

package com.hildebrando.visado.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.hildebrando.visado.dto.Solicitud;

@ManagedBean(name = "solicitudMB")
@SessionScoped
public class SolicitudMB {

	Solicitud solicitudModificar;

	public SolicitudMB() {
		solicitudModificar = new Solicitud();
	}

	public Solicitud getSolicitudModificar() {
		return solicitudModificar;
	}

	public void setSolicitudModificar(Solicitud solicitudModificar) {
		this.solicitudModificar = solicitudModificar;
	}

	

}

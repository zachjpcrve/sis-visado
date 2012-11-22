package com.hildebrando.visado.mb;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.modelo.TiivsMultitabla;

@ManagedBean(name = "solicitudRegMB")
@SessionScoped
public class SolicitudRegistroMB {
	private Solicitud solicitudModificar;
	private List<TiivsMultitabla> lstMultitabla;
	
	public static Logger logger = Logger.getLogger(SolicitudRegistroMB.class);
	
	public SolicitudRegistroMB() 
	{
		solicitudModificar = new Solicitud();
		
		
	}
	
	public String redirectDetalleSolicitud(){
		logger.info(" **** redirectDetalleSolicitud ***");
		return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
	}
	
	
	


	

	public void cargarMultitabla()
	{
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
				
		try {
			lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de multitablas");
		}
	}
	

	//Descripcion: Metodo que se encarga de cargar los combos que se mostraran en la pantalla de Registro de solicitudes 
	//			   de acuerdo a la lista de la multitabla previamente cargada.
	//@Autor: Samira Benazar
	//@Version: 1.0
	//@param: -
	public void cargarCombosFormularioRegistro(String codigo){
		logger.debug("Buscando valores en multitabla con codigo: " + codigo);
		
		
		}

	public Solicitud getSolicitudModificar() {
		return solicitudModificar;
	}

	public void setSolicitudModificar(Solicitud solicitudModificar) {
		this.solicitudModificar = solicitudModificar;
	}

	public List<TiivsMultitabla> getLstMultitabla() {
		return lstMultitabla;
	}

	public void setLstMultitabla(List<TiivsMultitabla> lstMultitabla) {
		this.lstMultitabla = lstMultitabla;
	}
		
	
	
	

	

}

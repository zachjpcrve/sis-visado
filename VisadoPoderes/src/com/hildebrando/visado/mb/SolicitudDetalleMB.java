package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsTerritorio;

/**
 * Clase que se encarga de obtener la solicitud de visado (Estado, Moneda, Territorio 
 * y Anexos) en base al codigo de solicitud enviado como parametro.
 * */

@ManagedBean(name = "solDetMB")
@SessionScoped
public class SolicitudDetalleMB {
	
	private List<TiivsMultitabla> lstMultitabla;
	private List<TiivsAnexoSolicitud> lstAnexos;
	private TiivsSolicitud solicitud;
	private TiivsMultitabla moneda;
	private TiivsMultitabla estado;
	private TiivsTerritorio territorio;
		

	public static Logger logger = Logger.getLogger(SolicitudDetalleMB.class);
	
	public SolicitudDetalleMB() 
	{
		cargarMultitabla();
	}
	
	public String redirectDetalleSolicitud(){
		logger.info(" **** redirectDetalleSolicitud ***");
		FacesContext context = FacesContext.getCurrentInstance();  
		Map requestMap = context.getExternalContext().getRequestParameterMap();  
		String codSolicitud = (String)requestMap.get("prm_codSoli");  		
		obtenerDatosSolicitud(codSolicitud);
		
		return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
	}
	
	private void obtenerDatosSolicitud(String codSolicitud) {	
		//solicitudDTO = new SolicitudDTO(codSolicitud);
		
		// Obtención de solicitud
		logger.debug("[obtenerDatosSolicitud-PARAM]-codSolicitud: "+codSolicitud);
		
		obtenerDetallesolicitud(codSolicitud);
				
		if (solicitud != null) {
			moneda = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_MONEDA,solicitud.getMoneda());

			estado = getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_ESTADOS,solicitud.getEstado());

			obtenerTerritorio(solicitud.getTiivsOficina1().getTiivsTerritorio().getCodTer());
			
			obtenerListadoAnexos(solicitud.getCodSoli());
						
			logger.info("[SOLICIT_OBTENIDA]-Estado:" + estado.getValor1());
			logger.info("[SOLICIT_OBTENIDA]-moneda:" + moneda.getValor3());
			logger.info("[SOLICIT_OBTENIDA]-territorio:" + territorio.getCodTer());
			logger.info("[SOLICIT_OBTENIDA]-Cantidad Anexos:" + lstAnexos.size());

		} else {
			logger.info("La solicitud es nula");
		}
	}
		
	
	private void obtenerListadoAnexos(String codSoli) {
		GenericDao<TiivsAnexoSolicitud, Object> anexosDAO = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsAnexoSolicitud.class);
		filtro.add(Restrictions.eq("id.codSoli", codSoli));
		try {
			lstAnexos = anexosDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtenerListadoAnexos: "+e);
		}
		if(lstAnexos!=null){
			logger.info("Se han cargado ["+lstAnexos.size()+"] anexos.");
		}
	}

	private void obtenerTerritorio(String codTerr) {
		GenericDao<TiivsTerritorio, Object> territorioDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			territorio = territorioDAO.buscarById(TiivsTerritorio.class,codTerr);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtenerTerritorio: "+e);
		}
	}

	private void obtenerDetallesolicitud(String codSolicitud) {
		GenericDao<TiivsSolicitud, Object> solicitudDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			solicitud = solicitudDAO.buscarById(TiivsSolicitud.class,codSolicitud);
			logger.info("[SOLICITUD]-Codigo obtenido: " + solicitud.getCodSoli());
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtenerDetallesolicitud: "+e);
		}
	}

	private TiivsMultitabla getRowFromMultitabla(String codigoMultitablaMoneda,
			String codigoCampo) {
		// Obtención de moneda
		TiivsMultitabla resultMultiTabla = null;
		GenericDao<TiivsMultitabla, Object> multiTablaDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			TiivsMultitablaId tablaId = new TiivsMultitablaId(codigoMultitablaMoneda, codigoCampo);			
			resultMultiTabla = multiTablaDAO.buscarById(TiivsMultitabla.class,tablaId);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener Monedas: "+e);
		}
		return resultMultiTabla;
	}

	public void cargarMultitabla()
	{
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
				
		try {
			lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+"multitabla: "+e);
		}
	}

	public TiivsSolicitud getSolicitud() {
		return solicitud;
	}

	public void setSolicitud(TiivsSolicitud solicitud) {
		this.solicitud = solicitud;
	}

	public TiivsMultitabla getMoneda() {
		return moneda;
	}

	public void setMoneda(TiivsMultitabla moneda) {
		this.moneda = moneda;
	}

	public TiivsMultitabla getEstado() {
		return estado;
	}

	public void setEstado(TiivsMultitabla estado) {
		this.estado = estado;
	}

	public TiivsTerritorio getTerritorio() {
		return territorio;
	}

	public void setTerritorio(TiivsTerritorio territorio) {
		this.territorio = territorio;
	}

	public List<TiivsAnexoSolicitud> getLstAnexos() {
		return lstAnexos;
	}

	public void setLstAnexos(List<TiivsAnexoSolicitud> lstAnexos) {
		this.lstAnexos = lstAnexos;
	}	
}

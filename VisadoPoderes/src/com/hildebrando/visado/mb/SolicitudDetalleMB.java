package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.ApoderadoDTO;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.OperacionBancariaDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsSolicitud;

@ManagedBean(name = "solicitudRegMB")
@SessionScoped
public class SolicitudDetalleMB {
	private Solicitud solicitudModificar;
	private List<TiivsMultitabla> lstMultitabla;
	private List<ApoderadoDTO> lstClientes;
	private List<OperacionBancariaDTO> lstOperaciones;
	private List<DocumentoTipoSolicitudDTO> lstdocumentos;
	private List<SeguimientoDTO> lstSeguimiento;
	
	private TiivsSolicitud solicitud = null;
	
	public static Logger logger = Logger.getLogger(SolicitudDetalleMB.class);
	
	public SolicitudDetalleMB() 
	{
		solicitudModificar = new Solicitud();
		listarDataMaqueteado();		
		
	}
	public void listarDataMaqueteado(){
		lstClientes=new ArrayList<ApoderadoDTO>();
		lstOperaciones=new ArrayList<OperacionBancariaDTO>();
		lstdocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
		lstSeguimiento=new ArrayList<SeguimientoDTO>();
		lstClientes.add(new ApoderadoDTO("123456", "DNI:43863696 Samira Benazar", "Apoderado", "Beneficiario", "555555555", "samiray.yas@gmail.com"));
		lstClientes.add(new ApoderadoDTO("789654", "DNI:82553665 Diego Clemente", "Poderdante", "Fallecido", "8926358858","diemgo_clemente@hotmail.com"));
		lstOperaciones.add(new OperacionBancariaDTO("001", "DNI:43863696 Samira Benazar", "PEN", "500.00", "0", "500.00"));
		lstOperaciones.add(new OperacionBancariaDTO("001", "DNI:43863696 Diego Clemente", "DOL", "300.00", "3.50", "1050.00"));
	    lstdocumentos.add(new DocumentoTipoSolicitudDTO("001", "Copia de DNI", "Yes"));
	    lstdocumentos.add(new DocumentoTipoSolicitudDTO("001", "Copia Literal", "Nou"));
	    lstSeguimiento.add(new SeguimientoDTO("Registrado", "Nivel 01", "22/11/2012", "P025245 :Samira Benazar", "observacion"));
	    lstSeguimiento.add(new SeguimientoDTO("Enviado", " ", "22/11/2012", "P025245 :Diego Clemente", "observacion"));

	}
	public String redirectDetalleSolicitud(){
		logger.info(" **** redirectDetalleSolicitud ***");
		String codSolicitud = "0000181";
		obtenerDetalleSolicitud(codSolicitud);
		return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
	}
	
	private void obtenerDetalleSolicitud(String codSolicitud) {
		
		GenericDao<TiivsSolicitud, Object> solicitudDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsSolicitud.class);					
		filtro.add(Restrictions.eq("codSoli", codSolicitud));
		List<TiivsSolicitud> lstSolicitudes= new ArrayList<TiivsSolicitud>();				
		try {
			lstSolicitudes = solicitudDAO.buscarDinamico(filtro);
			if(lstSolicitudes.size()>0){
				this.solicitud = lstSolicitudes.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public TiivsSolicitud getSolicitud() {
		return solicitud;
	}
	public void setSolicitud(TiivsSolicitud solicitud) {
		this.solicitud = solicitud;
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

	public List<ApoderadoDTO> getLstClientes() {
		return lstClientes;
	}

	public void setLstClientes(List<ApoderadoDTO> lstClientes) {
		this.lstClientes = lstClientes;
	}

	public List<OperacionBancariaDTO> getLstOperaciones() {
		return lstOperaciones;
	}

	public void setLstOperaciones(List<OperacionBancariaDTO> lstOperaciones) {
		this.lstOperaciones = lstOperaciones;
	}

	public List<DocumentoTipoSolicitudDTO> getLstdocumentos() {
		return lstdocumentos;
	}

	public void setLstdocumentos(List<DocumentoTipoSolicitudDTO> lstdocumentos) {
		this.lstdocumentos = lstdocumentos;
	}

	public List<SeguimientoDTO> getLstSeguimiento() {
		return lstSeguimiento;
	}

	public void setLstSeguimiento(List<SeguimientoDTO> lstSeguimiento) {
		this.lstSeguimiento = lstSeguimiento;
	}
		
	
	
	

	

}

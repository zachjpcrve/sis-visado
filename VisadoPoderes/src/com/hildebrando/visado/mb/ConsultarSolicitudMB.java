package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

@ManagedBean(name = "consultarSolicitudMB")
@SessionScoped
public class ConsultarSolicitudMB {
	public static Logger logger = Logger.getLogger(ConsultarSolicitudMB.class);
	
	
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	
	private TiivsSolicitud solicitudRegistrarT;
	private List<TiivsSolicitudOperban> lstOperacionesBancarias;
	private List<TiivsAnexoSolicitud> lstAnexosSolicitudes;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	
	public ConsultarSolicitudMB() {
		inicializarContructor();
	}
	public void inicializarContructor(){
		solicitudRegistrarT=new TiivsSolicitud();
		lstOperacionesBancarias=new ArrayList<TiivsSolicitudOperban>();
		lstAnexosSolicitudes=new ArrayList<TiivsAnexoSolicitud>();
	}
	public String redirectDetalleSolicitud() {
		logger.info(" **** redirectDetalleSolicitud ***");
		obtenerSolicitud();
		//return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
		return "/faces/paginas/detalleSolicitud.xhtml";
		
	}

	@SuppressWarnings("unchecked")
	public void obtenerSolicitud(){
		try {
		   
		   String codigoSolicitud=Utilitarios.capturarParametro("prm_codSoli");
		   logger.info("codigoSolicitud : "+codigoSolicitud);
		   TiivsSolicitud solicitud =new TiivsSolicitud();
		   solicitud.setCodSoli(codigoSolicitud);
		   SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		   solicitudRegistrarT= solicitudService.obtenerTiivsSolicitud(solicitud);
		   logger.info("getTiivsSolicitudAgrupacions :" +solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
		   logger.info("getNomUsuario : " +solicitudRegistrarT.getNomUsuario());
		   
		   lstOperacionesBancarias=solicitudService.obtenerListarOperacionesBancarias(solicitud);
		   lstAnexosSolicitudes=solicitudService.obtenerListarAnexosSolicitud(solicitud);
		  
		    
		    List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
		   AgrupacionSimpleDto agrupacionSimpleDto  =new AgrupacionSimpleDto(); ;
		   
		   for (TiivsSolicitudAgrupacion x : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
			   for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) {
				if(d.getTiivsPersona().getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					lstPoderdantes.add(d.getTiivsPersona());
					}
				else  if(d.getTiivsPersona().getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					lstApoderdantes.add(d.getTiivsPersona());
				}
					
			}
			   agrupacionSimpleDto =new AgrupacionSimpleDto();
			   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudRegistrarT.getCodSoli(), x.getId().getNumGrupo()));
			   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
			   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
			   agrupacionSimpleDto.setsEstado(x.getActivo());
			  // agrupacionSimpleDto.setiEstado(x.getActivo());
		   }
		  
		  lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public CombosMB getCombosMB() {
		return combosMB;
	}
	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}
	public TiivsSolicitud getSolicitudRegistrarT() {
		return solicitudRegistrarT;
	}
	public void setSolicitudRegistrarT(TiivsSolicitud solicitudRegistrarT) {
		this.solicitudRegistrarT = solicitudRegistrarT;
	}
	public List<TiivsSolicitudOperban> getLstOperacionesBancarias() {
		return lstOperacionesBancarias;
	}
	public void setLstOperacionesBancarias(
			List<TiivsSolicitudOperban> lstOperacionesBancarias) {
		this.lstOperacionesBancarias = lstOperacionesBancarias;
	}
	public List<TiivsAnexoSolicitud> getLstAnexosSolicitudes() {
		return lstAnexosSolicitudes;
	}
	public void setLstAnexosSolicitudes(
			List<TiivsAnexoSolicitud> lstAnexosSolicitudes) {
		this.lstAnexosSolicitudes = lstAnexosSolicitudes;
	}
	public List<AgrupacionSimpleDto> getLstAgrupacionSimpleDto() {
		return lstAgrupacionSimpleDto;
	}
	public void setLstAgrupacionSimpleDto(
			List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;
	}
	
	

}

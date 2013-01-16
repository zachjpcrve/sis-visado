package com.hildebrando.visado.mb;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
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
	private List<TiivsSolicitudOperban> lstSolicBancarias;
	private List<TiivsAnexoSolicitud> lstAnexosSolicitudes;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	private AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado;
	private List<DocumentoTipoSolicitudDTO> lstdocumentos;
	private List<SeguimientoDTO> lstSeguimientoDTO;
	
	public ConsultarSolicitudMB() {
		inicializarContructor();
	}
	public void inicializarContructor(){
		solicitudRegistrarT=new TiivsSolicitud();
		lstSolicBancarias=new ArrayList<TiivsSolicitudOperban>();
		lstAnexosSolicitudes=new ArrayList<TiivsAnexoSolicitud>();
		lstAgrupacionSimpleDto=new ArrayList<AgrupacionSimpleDto>();
		objAgrupacionSimpleDtoCapturado=new AgrupacionSimpleDto();
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		lstSeguimientoDTO=new ArrayList<SeguimientoDTO>();
	}
	public String redirectDetalleSolicitud() {
		logger.info(" **** redirectDetalleSolicitud ***");
		obtenerSolicitud();
		//obtenerHistorialSolicitud();
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
		   solicitudRegistrarT.setDescEstado(Utilitarios.obternerDescripcionEstado(solicitudRegistrarT.getEstado()));
		   
		   lstSolicBancarias=solicitudService.obtenerListarOperacionesBancarias(solicitud);
		   int y=0;
		   for (TiivsSolicitudOperban f : lstSolicBancarias) {
			y++;
			 f.setsItem(String.format("%03d",y));
			 f.setsDescMoneda(Utilitarios.obternerDescripcionMoneda(f.getMoneda()));
		   }
		   lstAnexosSolicitudes=solicitudService.obtenerListarAnexosSolicitud(solicitud);
		   
		   //descargar anexos
		   descargarAnexosFileServer();
		   
		   lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		   int i=0;
		  for (TiivsAnexoSolicitud v : lstAnexosSolicitudes) {
			  i++;
			  //lstdocumentos.add(new DocumentoTipoSolicitudDTO(String.format("%03d",i) , v.getAliasArchivo()));
			  lstdocumentos.add(new DocumentoTipoSolicitudDTO(String.format("%03d",i) , v.getAliasArchivo(), v.getAliasTemporal()));
		     }
		   // PODERDANTES Y APODERADOS
		    List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
		   AgrupacionSimpleDto agrupacionSimpleDto  =new AgrupacionSimpleDto(); ;
		   
		   for (TiivsSolicitudAgrupacion x : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
			   for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) {
				if(d.getId().getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					lstPoderdantes.add(d.getTiivsPersona());
					}
				else  if(d.getId().getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					lstApoderdantes.add(d.getTiivsPersona());
				}
			}
			   agrupacionSimpleDto =new AgrupacionSimpleDto();
			   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudRegistrarT.getCodSoli(), x.getId().getNumGrupo()));
			   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
			   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
			   agrupacionSimpleDto.setsEstado(Utilitarios.obternerDescripcionMoneda(x.getActivo().trim()) );
			   lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
		   }
		  
		  
		  this.actualizarEstadoReservadoSolicitud();
		  this.obtenerHistorialSolicitud();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
					
				
	public void actualizarEstadoReservadoSolicitud() throws Exception{
		logger.info("*********************** actualizarEstadoReservadoSolicitud **************************");
		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02);
		this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_RESERVADO_T02);
		 GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		 service.modificar(solicitudRegistrarT);
		 GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			
		  TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
		  objHistorial.setId(new TiivsHistSolicitudId(this.solicitudRegistrarT.getCodSoli(),2+""));
		  objHistorial.setEstado(this.solicitudRegistrarT.getEstado());
		  objHistorial.setNomUsuario(this.solicitudRegistrarT.getNomUsuario());
		  objHistorial.setObs(this.solicitudRegistrarT.getObs());
		  objHistorial.setFecha(new Timestamp(new Date().getDate()));
		  objHistorial.setRegUsuario(this.solicitudRegistrarT.getRegUsuario());
		  serviceHistorialSolicitud.insertar(objHistorial);
	}
						
	
	public void verAgrupacion() {
		logger.info("********************** verAgrupacion *********************************** ");

		logger.info("this.objAgrupacionSimpleDtoCapturado  "+ this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.objAgrupacionSimpleDtoCapturado  " + this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		//logger.info("this.objAgrupacionSimpleDtoCapturado  " + this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
	}
	
	
	public void obtenerHistorialSolicitud(){
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + solicitudRegistrarT.getCodSoli());
		
		String sCodSolicitud=solicitudRegistrarT.getCodSoli();
		try {
		GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
		filtroHist.add(Restrictions.eq("id.codSoli",sCodSolicitud));
		
		List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
        lstHist = histDAO.buscarDinamico(filtroHist);
			
		logger.info("Numero de registros encontrados:"+lstHist.size());
		
		if(lstHist!=null && lstHist.size()>0){
			lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
			
			for(TiivsHistSolicitud h : lstHist){
				SeguimientoDTO seg = new SeguimientoDTO();
				String estado = h.getEstado();
				if(estado!=null)
					seg.setEstado(buscarEstadoxCodigo(estado.trim()));
				seg.setNivel("");
				seg.setFecha(h.getFecha());
				seg.setUsuario(h.getNomUsuario());
				seg.setRegUsuario(h.getRegUsuario());
				seg.setObs(h.getObs());
				lstSeguimientoDTO.add(seg);				
			}
		}
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el historial de la solicitud");			
			exp.printStackTrace();
		}
			
		
	}
	
	public String buscarEstadoxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstado().size(); i++) {
			if (combosMB.getLstEstado().get(i).getCodEstado().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstado().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}
	
	public boolean descargarAnexosFileServer() {

		logger.info("********************** descargarAnexosFileServer():INICIO ********************************");
		
		boolean iRet = true;
		
		String ubicacionTemporal = Utilitarios.getProjectPath() + File.separator + ConstantesVisado.FILES + File.separator;
		logger.debug("ubicacion temporal " + ubicacionTemporal);
		
		for (TiivsAnexoSolicitud a : this.lstAnexosSolicitudes) {

			File fileTemporal = new File(ubicacionTemporal + a.getAliasTemporal());
			if (!fileTemporal.exists()) {
				logger.info("Archivo no existe se descargara:" + a.getAliasArchivo() );
								
				File fichTemp = null;
				boolean bSaved = false;
				try {
					File fDirectory = new File(ubicacionTemporal);
					if (!fDirectory.exists()){
						fDirectory.mkdirs();
					}

					String fileName = a.getAliasArchivo();
					String extension = fileName.substring(fileName.lastIndexOf("."));
					String sNombreTemporal = "";
					fichTemp = File.createTempFile("temp", extension, new File(ubicacionTemporal));
					sNombreTemporal = fichTemp.getName().substring(1 + fichTemp.getName().lastIndexOf(File.separator));					
					logger.debug("sNombreTemporal: " + sNombreTemporal);

					PDFViewerMB pdfViewerMB = new PDFViewerMB();
					if(pdfViewerMB.descargarArchivo(fichTemp.getAbsolutePath(),fileName)){
						a.setAliasTemporal(sNombreTemporal);						
					} else {	
						a.setAliasTemporal("");
						logger.debug("Archivo no existe en el File Server");						
					}
					bSaved = true;
					
				} catch (IOException e) {
					logger.debug("Error al descargar archivo: " + a.getAliasArchivo());
					logger.debug(e.toString());
					e.printStackTrace();
					bSaved = false;
				} finally {
					fichTemp.deleteOnExit(); // Delete the file when the
												// JVM terminates					
				}
				if (bSaved) {
					GenericDao<TiivsAnexoSolicitud, Object> anexoDAO = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
					try {
						anexoDAO.modificar(a);
					} catch (Exception ex) {
						logger.debug("No se actualizará el anexo " + ex.getMessage());
					}
					iRet = iRet && true;
				} else {
					logger.debug("Error no se actualizará anexo");
					iRet = iRet && false;
				}

			} else {
				logger.info("Archivo ya existe en ubicacion temporal ");
			}
		}	
		logger.info("********************** descargarAnexosFileServer():FIN ***********************************");
		return iRet;
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
	public List<TiivsSolicitudOperban> getLstSolicBancarias() {
		return lstSolicBancarias;
	}
	public void setLstSolicBancarias(List<TiivsSolicitudOperban> lstSolicBancarias) {
		this.lstSolicBancarias = lstSolicBancarias;
	}
	public AgrupacionSimpleDto getObjAgrupacionSimpleDtoCapturado() {
		return objAgrupacionSimpleDtoCapturado;
	}
	public void setObjAgrupacionSimpleDtoCapturado(
			AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado) {
		this.objAgrupacionSimpleDtoCapturado = objAgrupacionSimpleDtoCapturado;
	}
	public List<DocumentoTipoSolicitudDTO> getLstdocumentos() {
		return lstdocumentos;
	}
	public void setLstdocumentos(List<DocumentoTipoSolicitudDTO> lstdocumentos) {
		this.lstdocumentos = lstdocumentos;
	}
	public List<SeguimientoDTO> getLstSeguimientoDTO() {
		return lstSeguimientoDTO;
	}
	public void setLstSeguimientoDTO(List<SeguimientoDTO> lstSeguimientoDTO) {
		this.lstSeguimientoDTO = lstSeguimientoDTO;
	}
	
	

}

package com.hildebrando.visado.mb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.primefaces.event.FileUploadEvent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecDUMMY;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Persona;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.converter.PersonaDataModal;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ApoderadoDTO;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.OperacionBancariaDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersonaId;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitudId;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsSolicitudOperbanId;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

@ManagedBean(name = "solicitudRegMB")
@SessionScoped
public class SolicitudRegistroMB {
	
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	
	@ManagedProperty(value = "#{registroUtilesMB}")
	RegistroUtilesMB objRegistroUtilesMB;
	private TiivsSolicitudOperban objSolicBancaria;
	private List<TiivsSolicitudOperban> lstSolicBancarias;
	private TiivsSolicitud solicitudRegistrarT;
	private Solicitud solicitudRegistrar;
	private List<TiivsMultitabla> lstMultitabla;
	private List<ApoderadoDTO> lstClientes;
	private List<OperacionBancariaDTO> lstOperaciones;
	private List<DocumentoTipoSolicitudDTO> lstdocumentos;
	private List<DocumentoTipoSolicitudDTO> lstdocumentosOpcional;
	private List<SeguimientoDTO> lstSeguimiento;
	private List<TiivsOperacionBancaria> lstTiivsOperacionBancaria;
	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaResultado;
	private TiivsPersona objTiivsPersonaSeleccionado;
	private Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas;
	private Set<TiivsSolicitudAgrupacion> lstTiivsSolicitudAgrupacion;
	private List<AgrupacionSimpleDto>  lstAgrupacionSimpleDto;
	private List<TiivsAnexoSolicitud> lstAnexoSolicitud;
	private PersonaDataModal personaDataModal;
	private String iTipoSolicitud="";
//
	private String sCodDocumento;
	private IILDPeUsuario  usuario;
	private int numGrupo=0;
	private List<TiivsPersona> lstTiivsPersona;
	private List<TiivsPersona> lstTiivsPersonaResultado;
	private List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos;
	private List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp;
	boolean bBooleanPopup =false;
	private boolean flagUpdateOperacionSolic=false;
	private boolean flagUpdateOperacionSolcAgrupac=false;
	private boolean flagUpdateOperacionSolcDocumen=false;
	private String sEstadoSolicitud="";
	private TiivsSolicitudOperban objSolicitudOperacionCapturado;
	private AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado;
	private TiivsTipoSolicDocumento objDocumentoXSolicitudCapturado ;
	private int indexUpdateOperacion=0;
	private int indexUpdateAgrupacionSimpleDto=0;
	private int indexUpdateSolicDocumentos=0;
	public static Logger logger = Logger.getLogger(SolicitudRegistroMB.class);
	
	public SolicitudRegistroMB() 
	{   
		solicitudRegistrar = new Solicitud();
		lstTiivsPersona=new ArrayList<TiivsPersona>();
		lstTiivsPersonaResultado=new ArrayList<TiivsPersona>();
		personaDataModal=new PersonaDataModal(lstTiivsPersonaResultado);
		objTiivsPersonaBusqueda=new TiivsPersona();
		objTiivsPersonaResultado=new TiivsPersona();
		objTiivsPersonaSeleccionado=new TiivsPersona();
		lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
		lstAgrupacionSimpleDto=new ArrayList<AgrupacionSimpleDto>();
		lstTipoSolicitudDocumentos=new ArrayList<TiivsTipoSolicDocumento>();
		
		lstDocumentosXTipoSolTemp=new ArrayList<TiivsTipoSolicDocumento>();
		lstAnexoSolicitud=new ArrayList<TiivsAnexoSolicitud>();
		lstdocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
		objSolicBancaria=new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		objSolicBancaria.setTiivsOperacionBancaria(new TiivsOperacionBancaria());
		lstSolicBancarias=new ArrayList<TiivsSolicitudOperban>();
		lstOperaciones=new ArrayList<OperacionBancariaDTO>();
		 usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		this.instanciarSolicitudRegistro();
		
	}
	
	public void handleFileUpload(FileUploadEvent event) {  
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
        System.out.println(event.getComponent());
        System.out.println(event.getFile().getFileName());
        
        System.out.println(event.getComponent().getId());
    }  
	
	public void listarDocumentosXSolicitud(ValueChangeEvent e){
	System.out.println("ValuechanceEvent :  " +e.getNewValue());
	GenericDao<TiivsTipoSolicDocumento, Object> genTipoSolcDocumDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	Busqueda filtroTipoSolcDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);
	filtroTipoSolcDoc.add(Restrictions.eq("tiivsTipoSolicitud.codTipSolic", (String)e.getNewValue()));
	try {
		lstDocumentosXTipoSolTemp= genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);
		lstTipoSolicitudDocumentos = this.lstDocumentosXTipoSolTemp;
		logger.info(" e.getNewValue()  "+(String)e.getNewValue()+"  lstTipoSolicitudDocumentos.size : " +lstTipoSolicitudDocumentos.size());
	} catch (Exception ex) {
		logger.info("Error al cargar el listado de documentos por tipo de soliciitud");
		ex.printStackTrace();
	}
	}

	public void obtenerPersonaSeleccionada(){
		System.out.println(objTiivsPersonaSeleccionado.getCodPer());
		this.objTiivsPersonaResultado=this.objTiivsPersonaSeleccionado;
	}
	
	public void buscarPersona(){
		logger.info("******************** buscarPersona **********************");
		logger.info("***objTiivsPersonaBusqueda.getCodCen() "+objTiivsPersonaBusqueda.getCodCen());
		logger.info("***objTiivsPersonaBusqueda.getTipDoi() "+objTiivsPersonaBusqueda.getTipDoi());
		logger.info("***objTiivsPersonaBusqueda.getNumDoi() "+objTiivsPersonaBusqueda.getNumDoi());
		try {
		List<TiivsPersona> lstTiivsPersonaLocal=new ArrayList<TiivsPersona>();
		                   lstTiivsPersonaLocal=this.buscarPersonaLocal();
		System.out.println("lstTiivsPersonaLocal  "+lstTiivsPersonaLocal.size());
		List<TiivsPersona> lstTiivsPersonaReniec=new ArrayList<TiivsPersona>();
		 if(lstTiivsPersonaLocal.size()==0){
			   lstTiivsPersonaReniec=this.buscarPersonaReniec();
			if(lstTiivsPersonaReniec.size()==0){
				objTiivsPersonaResultado=new TiivsPersona();
				 this.bBooleanPopup=false;
				//Utilitarios.mensajeInfo("INFO", "No se encontro resultados para la busqueda.");
			}else if(lstTiivsPersonaReniec.size()==1){
				objTiivsPersonaResultado=lstTiivsPersonaReniec.get(0);
				 this.bBooleanPopup=false;
			}else if(lstTiivsPersonaReniec.size()>1){
				 this.bBooleanPopup=true;
				 lstTiivsPersonaResultado=lstTiivsPersonaReniec;
			}
		}else if(lstTiivsPersonaLocal.size()==1){
			 this.bBooleanPopup=false;
			objTiivsPersonaResultado=lstTiivsPersonaLocal.get(0);
		}else if(lstTiivsPersonaLocal.size()>1){
			 this.bBooleanPopup=true;
			 lstTiivsPersonaResultado=lstTiivsPersonaLocal;
			 
			 personaDataModal=new PersonaDataModal(lstTiivsPersonaResultado);
		}else{
			 this.bBooleanPopup=true;
		}
		
		 
		} catch (Exception e) {
			Utilitarios.mensajeError("ERROR", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void limpiarCriteriosBusqueda()
	{
		objTiivsPersonaBusqueda.setCodCen("");
		objTiivsPersonaBusqueda.setTipDoi("");
		objTiivsPersonaBusqueda.setNumDoi("");
		objTiivsPersonaResultado.setTipDoi("");
		objTiivsPersonaResultado.setNumDoi("");
		objTiivsPersonaBusqueda.setCodCen("");
		objTiivsPersonaResultado.setApePat("");
		objTiivsPersonaResultado.setApeMat("");
		objTiivsPersonaResultado.setNombre("");
		objTiivsPersonaResultado.setTipPartic("");
		objTiivsPersonaResultado.setClasifPer("");
		objTiivsPersonaResultado.setClasifPerOtro("");
		objTiivsPersonaResultado.setEmail("");
		objTiivsPersonaResultado.setNumCel("");	
	}
	
	public List<TiivsPersona> buscarPersonaReniec() throws Exception{
		    List<TiivsPersona>  lstTiivsPersona=new ArrayList<TiivsPersona>();
		       BResult resultado = null;
		       TiivsPersona objPersona=null;
		       Persona persona=null;
		       if (objTiivsPersonaBusqueda.getNumDoi()!=null) {
					logger.info("El DNI a buscar es: "+objTiivsPersonaBusqueda.getNumDoi());
				
				//ObtenerPersonaReniecService reniecService = new ObtenerPersonaReniecDUMMY();
				ObtenerPersonaReniecDUMMY reniecService = new ObtenerPersonaReniecDUMMY();
				resultado =reniecService.devolverPersonaReniecDNI("", "", objTiivsPersonaBusqueda.getNumDoi());
				if(resultado.getCode()==0){
					 persona = (Persona)resultado.getObject();
					logger.info("PERSONA : "+persona.getNombreCompleto() + "\nDNI: "+persona.getNumerodocIdentidad());
					objPersona=new TiivsPersona();
					objPersona.setNumDoi(persona.getNumerodocIdentidad());
					objPersona.setNombre(persona.getNombre());
					objPersona.setApePat(persona.getApellidoPaterno());
					objPersona.setApeMat(persona.getApellidoMaterno());
					objPersona.setTipDoi(objTiivsPersonaBusqueda.getTipDoi());
					objPersona.setCodCen(objTiivsPersonaBusqueda.getCodCen());
					lstTiivsPersona.add(objPersona);
				}
		       }
		return lstTiivsPersona;
	}
	public List<TiivsPersona> buscarPersonaLocal() throws Exception{
		    List<TiivsPersona>  lstTiivsPersona=new ArrayList<TiivsPersona>();
	        GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	        Busqueda filtro = Busqueda.forClass(TiivsPersona.class);
	      
	        if((objTiivsPersonaBusqueda.getCodCen()==null||objTiivsPersonaBusqueda.getCodCen().equals(""))
	         &&(objTiivsPersonaBusqueda.getTipDoi()==null||objTiivsPersonaBusqueda.getTipDoi().equals(""))
	         &&(objTiivsPersonaBusqueda.getNumDoi()==null||objTiivsPersonaBusqueda.getNumDoi().equals(""))){
	        	Utilitarios.mensajeInfo("INFO", "Ingrese al menos un criterio de busqueda");
	        }else if(objTiivsPersonaBusqueda.getNumDoi()==null||objTiivsPersonaBusqueda.getNumDoi().equals("")){
	        	Utilitarios.mensajeInfo("INFO", "Ingrese el N�mero de Doi");
	        }else{
	        	  if(objTiivsPersonaBusqueda.getTipDoi()!=null && objTiivsPersonaBusqueda.getNumDoi()!=null){
		                 filtro.add(Restrictions.eq("tipDoi", objTiivsPersonaBusqueda.getTipDoi()));
		                 filtro.add(Restrictions.eq("numDoi", objTiivsPersonaBusqueda.getNumDoi()));
		        }
		        if(objTiivsPersonaBusqueda.getCodCen()!=null && objTiivsPersonaBusqueda.getCodCen().compareTo("")!=0){
		        	     filtro.add(Restrictions.eq("codCen", objTiivsPersonaBusqueda.getCodCen()));
		        }
		       lstTiivsPersona=service.buscarDinamico(filtro);
		        //lstTiivsPersona =this.manipularDataPruebaPopup(objTiivsPersonaBusqueda.getNumDoi());
			    for (TiivsPersona tiivsPersona : lstTiivsPersona) {
				    for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
						if(tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())){
							tiivsPersona.setsDesctipDoi(p.getDescripcion());
						}
					}
			    }
			    
			    if (lstTiivsPersona.size()==0)
			    {
			    	Utilitarios.mensajeInfo("INFO", "No se han encontrado resultados para los criterios de busqueda seleccionados");
			    }
	        }
	        
			return lstTiivsPersona;
	}

	public List<TiivsPersona> manipularDataPruebaPopup(String numDoi){
		TiivsPersona p = new TiivsPersona();
		p.setCodPer(new Integer(1));
		p.setNombre("SAMIRA");
		p.setApePat("CLEMENTE");
		p.setApeMat("KIMA ");
		p.setNumDoi("12345678");
		p.setTipDoi("0001") ;
		TiivsPersona s = new TiivsPersona();
		s.setCodPer(2);
		s.setNombre("DIEGO");
		s.setApePat("CLEMENTE");
		s.setApeMat("KIMA ");
		s.setNumDoi("12345678");
		s.setTipDoi("0001") ;
		
		List<TiivsPersona> lstPersona = new ArrayList<TiivsPersona>();
		if(p.getNumDoi().equals(numDoi)){
			lstPersona.add(p);
		}
		if(s.getNumDoi().equals(numDoi)){
			lstPersona.add(s);
		}
		return lstPersona;
		
	}

	
	public String redirectDetalleSolicitud(){
		logger.info(" **** redirectDetalleSolicitud ***");
		return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
	}
	
	
	
	public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(int iNumGrupo){
		  
		  Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion=new HashSet<TiivsSolicitudAgrupacion>();
		  TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion=new TiivsSolicitudAgrupacion();
		  TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId=new TiivsSolicitudAgrupacionId();
		  tiivsSolicitudAgrupacionId.setCodSoli(solicitudRegistrarT.getCodSoli());
		  tiivsSolicitudAgrupacionId.setNumGrupo(iNumGrupo);
		  tiivsSolicitudAgrupacion.setId(tiivsSolicitudAgrupacionId);
		  tiivsSolicitudAgrupacion.setTiivsAgrupacionPersonas(lstTiivsAgrupacionPersonas);
		  tiivsSolicitudAgrupacion.setActivo("1");
		  
		  lstSolicitudArupacion.add(tiivsSolicitudAgrupacion);
		  return lstSolicitudArupacion;
	}
    public void  agregarAgrupacionPersona(TiivsPersona objTiivsPersonaResultado ){
	TiivsAgrupacionPersona objTiivsAgrupacionPersona=new TiivsAgrupacionPersona();
	objTiivsAgrupacionPersona.setTiivsPersona(objTiivsPersonaResultado);
	objTiivsAgrupacionPersona.setId(new TiivsAgrupacionPersonaId(this.solicitudRegistrarT.getCodSoli(), numGrupo, objTiivsPersonaResultado.getCodPer(), objTiivsPersonaResultado.getTipPartic(), objTiivsPersonaResultado.getClasifPer()));
	}
	public void agregarPersona(){
		logger.info("****************** agregarPersona ********************");
		if(validarPersona()){
			if(validarRegistroDuplicado()){
		      for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
					if(objTiivsPersonaResultado.getTipDoi().equals(p.getCodTipoDoc())){
						objTiivsPersonaResultado.setsDesctipDoi(p.getDescripcion());
					}
				}
			  for (ComboDto p : combosMB.getLstTipoRegistroPersona()) {
					if(objTiivsPersonaResultado.getTipPartic().equals(p.getKey())){
						objTiivsPersonaResultado.setsDesctipPartic(p.getDescripcion());
					}
				}
			  for (ComboDto p : combosMB.getLstClasificacionPersona()) {
					if(objTiivsPersonaResultado.getClasifPer().equals(p.getKey())){
						objTiivsPersonaResultado.setsDescclasifPer(p.getDescripcion());
					}
					if(objTiivsPersonaResultado.getClasifPer().equals("99")){
						objTiivsPersonaResultado.setsDescclasifPer(objTiivsPersonaResultado.getClasifPerOtro());
					}
				}
			  
		      lstTiivsPersona.add(objTiivsPersonaResultado);
		      objTiivsPersonaResultado=new TiivsPersona();
		      objTiivsPersonaBusqueda=new TiivsPersona();
		      objTiivsPersonaSeleccionado=new TiivsPersona();
		      lstTiivsPersonaResultado=new ArrayList<TiivsPersona>();
		      personaDataModal=new PersonaDataModal(lstTiivsPersonaResultado);
			  }
		}
		//return objTiivsPersonaBusqueda;
	
	}

	public boolean validarRegistroDuplicado(){
		logger.info("******************************* validarRegistroDuplicado ******************************* " +objTiivsPersonaResultado.getNumDoi());
		boolean bResult=true;
		String sMensaje="";
	     for (TipoDocumento c : combosMB.getLstTipoDocumentos()) {
				if(objTiivsPersonaResultado.getTipDoi().equals(c.getCodTipoDoc())){
					objTiivsPersonaResultado.setsDesctipDoi(c.getDescripcion());
				}
			}
		for (ComboDto c : combosMB.getLstTipoRegistroPersona()) {
				if(objTiivsPersonaResultado.getTipPartic().equals(c.getKey())){
					objTiivsPersonaResultado.setsDesctipPartic(c.getDescripcion());
				}
			}
		for (TiivsPersona p : lstTiivsPersona) {
			if(objTiivsPersonaResultado.getNumDoi().equals(p.getNumDoi())){
				 bResult=false;
				 sMensaje="Ya existe una persona con n�mero de Doi :  "+p.getNumDoi()  +" y Tipo de Registro : " +objTiivsPersonaResultado.getsDesctipPartic();
				 Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}
		return bResult;
	}
	public boolean validarPersona(){
		logger.info("******************************* validarPersona ******************************* " +objTiivsPersonaResultado.getTipPartic());
		boolean bResult=true;
		String sMensaje="";
		System.out.println("objTiivsPersonaResultado.getClasifPer() "+objTiivsPersonaResultado.getClasifPer());
		if(objTiivsPersonaResultado.getTipDoi().equals("")){
			 sMensaje="Seleccione el Tipo de Documento";
			 bResult=false;
			 Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if(objTiivsPersonaResultado.getNumDoi().equals("")){
			 sMensaje="Ingrese el N�mero de Doi";
			 bResult=false;
			 Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if(objTiivsPersonaResultado.getClasifPer()==null||objTiivsPersonaResultado.getClasifPer().equals("")){
			sMensaje="Ingrese el Tipo de Clasificaci�n";
			 bResult=false;
			 Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if((objTiivsPersonaResultado.getTipPartic()==null||objTiivsPersonaResultado.getTipPartic().equals(""))
				){
			sMensaje="Ingrese el Tipo de Participacion";
			 bResult=false;
			 Utilitarios.mensajeInfo("INFO", sMensaje);
		}else if(objTiivsPersonaResultado.getClasifPer().equals("99")&&
				(objTiivsPersonaResultado.getClasifPerOtro().equals("")||objTiivsPersonaResultado.getClasifPerOtro()==null)){
			sMensaje="Ingrese la descipcion Tipo de Participacion";
			 bResult=false;
			 Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		
		return bResult;
		
	}
  public void agregarAgrupacion(){
	  numGrupo=+1;
	  logger.info("********************** agregarAgrupacion ********************* " +numGrupo);
	  
	  List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
	  List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
	  TiivsAgrupacionPersona tiivsAgrupacionPersona=null;
	  for (TiivsPersona objTiivsPersonaResultado : lstTiivsPersona) {
		  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
			  lstPoderdantes.add(objTiivsPersonaResultado);
		  }
		  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.APODERADO)){
			  lstApoderdantes.add(objTiivsPersonaResultado);
		  }
		  System.out.println("objTiivsPersonaResultado.getCodPer() : "+objTiivsPersonaResultado.getCodPer());
		  tiivsAgrupacionPersona =new TiivsAgrupacionPersona();
		  TiivsAgrupacionPersonaId  tiivsAgrupacionPersonaId =new TiivsAgrupacionPersonaId();
		  tiivsAgrupacionPersonaId.setNumGrupo(numGrupo);
		  tiivsAgrupacionPersonaId.setCodSoli(solicitudRegistrarT.getCodSoli());
		  tiivsAgrupacionPersonaId.setCodPer(objTiivsPersonaResultado.getCodPer());
		  tiivsAgrupacionPersonaId.setClasifPer(objTiivsPersonaResultado.getClasifPer());
		  tiivsAgrupacionPersonaId.setTipPartic(objTiivsPersonaResultado.getTipPartic());
		  tiivsAgrupacionPersona.setTiivsPersona(objTiivsPersonaResultado);
		  tiivsAgrupacionPersona.setId(tiivsAgrupacionPersonaId);
		  lstTiivsAgrupacionPersonas.add(tiivsAgrupacionPersona);
		 
		 
	}  
	  System.out.println("lstPoderdantes " +lstPoderdantes.size());
	  System.out.println("lstApoderdantes " +lstApoderdantes.size());
	  AgrupacionSimpleDto agrupacionSimpleDto =new AgrupacionSimpleDto();
	   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudRegistrarT.getCodSoli(), numGrupo));
	   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
	   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
	   agrupacionSimpleDto.setsEstado("Activo");
	   agrupacionSimpleDto.setiEstado(1);
	  lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
	  solicitudRegistrarT.setTiivsSolicitudAgrupacions(this.agregarSolicitudArupacion(numGrupo));
	  this.llamarComision();
	 System.out.println("tamanio de lstTiivsAgrupacionPersonas "+lstTiivsAgrupacionPersonas.size());
 }
  

	public void instanciarSolicitudRegistro() {
		logger.info("********************** instanciarSolicitudRegistro *********************");
		sEstadoSolicitud="BORRADOR";
		 lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
		 lstTiivsSolicitudAgrupacion=new HashSet<TiivsSolicitudAgrupacion>();
		 solicitudRegistrarT=new TiivsSolicitud();
		 solicitudRegistrarT.setTiivsSolicitudAgrupacions(lstTiivsSolicitudAgrupacion);
		 solicitudRegistrarT.setTiivsOficina1(new TiivsOficina1());
		 solicitudRegistrarT.setTiivsTipoSolicitud(new TiivsTipoSolicitud());
		String grupoAdm = (String)Utilitarios.getObjectInSession("GRUPO_ADM");
		String grupoOfi = (String)Utilitarios.getObjectInSession("GRUPO_OFI");
		
		logger.debug("********grupoAdm ****** "+grupoAdm +"  ******* grupoOfi ******** " +grupoOfi);
		//if (grupoAdm == null && grupoOfi!= null) {
		    IILDPeUsuario  usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		    logger.debug("usuario en session? --> "+usuario.getNombre());
		    logger.debug("CodOfi: "+usuario.getBancoOficina().getCodigo().trim());
		    logger.debug("DesOfi: "+usuario.getBancoOficina().getDescripcion().trim());
			
			
			TiivsOficina1 oficina=new TiivsOficina1();
			oficina.setCodOfi(usuario.getBancoOficina().getCodigo());
			
			 List<TiivsOficina1> lstOficinas1=new ArrayList<TiivsOficina1>();
			 combosMB=new CombosMB();
			 lstOficinas1 =combosMB.getLstOficina();
			 
			if(lstOficinas1!=null && lstOficinas1.size()!=0){
			for (TiivsOficina1 o : lstOficinas1) {
				if(usuario.getBancoOficina().getCodigo().equals(o.getCodOfi())){
					this.solicitudRegistrarT.setTiivsOficina1(o);
				}else{
					this.solicitudRegistrarT.setTiivsOficina1(new TiivsOficina1());
				}
			}
			}
			 SolicitudDao<TiivsPersona, Object> service = (SolicitudDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
			 try {
				String sCodigoSol=service.obtenerPKNuevaSolicitud();
				logger.debug(" sCodigoSol " + sCodigoSol);
				this.solicitudRegistrarT.setCodSoli(sCodigoSol);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 this.solicitudRegistrarT.setEstado("1");
			 this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_REGISTRADO);
			 
				lstTiivsPersonaResultado=new ArrayList<TiivsPersona>();
				personaDataModal=new PersonaDataModal(lstTiivsPersonaResultado);
				objTiivsPersonaBusqueda=new TiivsPersona();
				objTiivsPersonaResultado=new TiivsPersona();
				objTiivsPersonaSeleccionado=new TiivsPersona();
				lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
				lstAgrupacionSimpleDto=new ArrayList<AgrupacionSimpleDto>();
				lstTipoSolicitudDocumentos=new ArrayList<TiivsTipoSolicDocumento>();
				
				lstDocumentosXTipoSolTemp=new ArrayList<TiivsTipoSolicDocumento>();
				lstAnexoSolicitud=new ArrayList<TiivsAnexoSolicitud>();
				lstdocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
				objSolicBancaria=new TiivsSolicitudOperban();
				objSolicBancaria.setTipoCambio(0);
				objSolicBancaria.setId(new TiivsSolicitudOperbanId());
				lstSolicBancarias=new ArrayList<TiivsSolicitudOperban>();
				iTipoSolicitud="";
		
		//}		
		
	}
	public void actualizarListaDocumentosXTipo(){
		logger.info("****************************** actualizarListaDocumentosXTipo *********************************");
		
		for (TiivsTipoSolicDocumento s : lstDocumentosXTipoSolTemp) {
			if(s.getCodDoc().equals(sCodDocumento)){
				this.lstTipoSolicitudDocumentos.remove(s);
				break;
			}
		}
	}
	public boolean validarTotalDocumentos(){
		boolean result=false;
		String sMensaje="";
		for (TiivsTipoSolicDocumento x : this.lstTipoSolicitudDocumentos) {
			for (TiivsAnexoSolicitud a : lstAnexoSolicitud) {
				if(x.getObligatorio()==ConstantesVisado.DOCUMENTO_OBLIGATORIO && x.getCodDoc().equals(a.getId().getCodDoc())){
					result=true;
				}else{
					sMensaje="Faltan Ingresar Documentos Obligatorios.";
					Utilitarios.mensajeInfo("INFO", sMensaje);
					break;
				}
			}
		}
		return result;
	}
	public boolean ValidarDocumentosDuplicados(){
		boolean result=true;
		String sMensaje="";
		for (TiivsAnexoSolicitud a : this.lstAnexoSolicitud) {
			if(a.getId().getCodDoc().equals(sCodDocumento)){
				result=false;
				sMensaje="Documento ya se encuentra en la lista de Documentos";
				Utilitarios.mensajeInfo("INFO ", sMensaje);
			}
		}
		return result;
	}
	public void agrearDocumentosXTipoSolicitud(){
		logger.info(" ************************** agrearDocumentosXTipoSolicitud  ****************************** ");
		logger.info("iTipoSolicitud  : " +iTipoSolicitud);
		logger.info("scodDocumento :  " +sCodDocumento);
		logger.info("lstAnexoSolicitud.size() :  " +lstAnexoSolicitud.size());
		if(this.ValidarDocumentosDuplicados()){
        TiivsAnexoSolicitud objAnexo =new TiivsAnexoSolicitud();
        String aliasArchivo="";
        aliasArchivo=this.solicitudRegistrarT.getCodSoli()+"_"+sCodDocumento;
        logger.info("aliasArchivo *** " +aliasArchivo);
        objAnexo.setId(new TiivsAnexoSolicitudId(this.solicitudRegistrarT.getCodSoli(), sCodDocumento));
        objAnexo.setAliasArchivo(aliasArchivo);
		lstAnexoSolicitud.add(objAnexo);
		//this.actualizarListaDocumentosXTipo();
		
		for (TiivsTipoSolicDocumento e : lstDocumentosXTipoSolTemp) {
			if(e.getCodDoc().equals(sCodDocumento)){
				lstdocumentos.add(new DocumentoTipoSolicitudDTO(e.getCodDoc(), e.getDesDoc(), e.getObligatorio()+""));	
			}
		}
		
		for (TiivsTipoSolicitud x : combosMB.getLstTipoSolicitud()) {
			if(x.getCodTipSolic().equals(iTipoSolicitud)){
				solicitudRegistrarT.setTiivsTipoSolicitud(x);
			}
		}
		
		}
	//	solicitudRegistrarT.getTiivsTipoSolicitud().setTiivsTipoSolicDocumentos(tiivsTipoSolicDocumentos);
		
	}
  public boolean validarOperacionBancaria(){
	  boolean result=true;
	  String sMensaje="";
	  if(objSolicBancaria.getId().getCodOperBan().equals("")){
		  sMensaje="Ingrese un tipo de Operacion";
		  Utilitarios.mensajeInfo("", sMensaje);
		  result=false;
	  }
	  if(objSolicBancaria.getMoneda().equals("")){
		  sMensaje="Seleccione una moneda";
		  Utilitarios.mensajeInfo("", sMensaje);
		  result=false;
	  }
	  if(!objSolicBancaria.getMoneda().equals("")&&!objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)
			  &&objSolicBancaria.getTipoCambio()==0){
		  sMensaje="Ingrese el Tipo de Cambio";
		  Utilitarios.mensajeInfo("", sMensaje);
		  result=false;
	  }
	  if(objSolicBancaria.getImporte()==0){
		  sMensaje="Ingrese el Importe";
		  Utilitarios.mensajeInfo("", sMensaje);
		  result=false;
	  }
	  if(!flagUpdateOperacionSolic){
	  for (TiivsSolicitudOperban x : lstSolicBancarias) {
		if(x.getId().getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan())){
			 sMensaje="Tipo de Operaci�n ya registrado, Ingrese otro Tipo de Operaci�n. ";
			  Utilitarios.mensajeInfo("", sMensaje);
			  result=false;
			break;
		}
	}
	  }
	  return result;
  }
  int icontSoles=0, icontDolares=0, icontEuros=0; double valorSoles_C=0,  valorSolesD=0,valorSolesE=0, valorEuro=0, valorDolar=0, valorFinal=0;
  int item=0;

  public void agrearOperacionBancaria(){
		logger.info(" ************************** agrearOperacionBancaria  ****************************** ");
		double valor=0 ;
		if(this.validarOperacionBancaria()){
			for (TiivsOperacionBancaria n : combosMB.getLstOpeBancaria()) {
				if(n.getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan())){
					this.objSolicBancaria.setTiivsOperacionBancaria(n);
				}
			}
		if(!this.flagUpdateOperacionSolic){
			if(objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
				valorSoles_C+=objSolicBancaria.getImporte();
				objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
				icontSoles++;
				this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
			}
			if(objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
				valor=objSolicBancaria.getTipoCambio()*objSolicBancaria.getImporte();
				valorSolesD+=valor;
				valorDolar+=objSolicBancaria.getImporte();
				objSolicBancaria.setImporteSoles(valor);
				icontDolares++;
				this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
			}
			if(objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
				valor=objSolicBancaria.getTipoCambio()*objSolicBancaria.getImporte();
				valorSolesE+=valor;
				valorEuro+=objSolicBancaria.getImporte();
				objSolicBancaria.setImporteSoles(valor);
				icontEuros++;
				this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
			}
	
          if(icontDolares==0&&icontEuros==0&&icontSoles>0){
        	  valorFinal=valorSoles_C;
        	  this.solicitudRegistrarT.setImporte(valorFinal);
          }
          if(icontDolares>0&&icontEuros==0&&icontSoles==0){
        	  valorFinal=valorDolar;
        	  this.solicitudRegistrarT.setImporte(valorFinal);
          }
          if(icontDolares==0&&icontEuros>0&&icontSoles==0){
        	  valorFinal=valorEuro;
        	  this.solicitudRegistrarT.setImporte(valorFinal);
          }
          if(icontDolares>0&&icontEuros>0&&icontSoles==0
        		  ||icontDolares>0&&icontEuros==0&&icontSoles>0
        		  ||icontDolares==0&&icontEuros>0&&icontSoles>0
        		  ||icontDolares==0&&icontEuros==0&&icontSoles==0){
        	  valorFinal=valorSoles_C+valorSolesD+valorSolesE;
        	  this.solicitudRegistrarT.setImporte(valorFinal);
          }
          
          
        	item++;
            objSolicBancaria.setsItem("00"+item);
          this.lstSolicBancarias.add(objSolicBancaria);
          }else{
        	  if(objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
    				valorSoles_C-=objSolicitudOperacionCapturado.getImporte();
    				icontSoles--;
    			}
    			if(objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
    				valor=objSolicitudOperacionCapturado.getTipoCambio()*objSolicitudOperacionCapturado.getImporte();
    				valorSolesD-=valor;
    				valorDolar-=objSolicitudOperacionCapturado.getImporte();
    				icontDolares--;
    			}
    			if(objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
    				valor=objSolicitudOperacionCapturado.getTipoCambio()*objSolicitudOperacionCapturado.getImporte();
    				valorSolesE-=valor;
    				valorEuro-=objSolicBancaria.getImporte();
    				icontEuros--;
    			}
    			
    			if(objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
    				valorSoles_C+=objSolicBancaria.getImporte();
    				objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
    				icontSoles++;
    				this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
    			}
    			if(objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
    				valor=objSolicBancaria.getTipoCambio()*objSolicBancaria.getImporte();
    				valorSolesD+=valor;
    				valorDolar+=objSolicBancaria.getImporte();
    				objSolicBancaria.setImporteSoles(valor);
    				icontDolares++;
    				this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
    			}
    			if(objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
    				valor=objSolicBancaria.getTipoCambio()*objSolicBancaria.getImporte();
    				valorSolesE+=valor;
    				valorEuro+=objSolicBancaria.getImporte();
    				objSolicBancaria.setImporteSoles(valor);
    				icontEuros++;
    				this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
    			}
        
              if(icontDolares==0&&icontEuros==0&&icontSoles>0){
            	  valorFinal=valorSoles_C;
            	  this.solicitudRegistrarT.setImporte(valorFinal);
              }
              if(icontDolares>0&&icontEuros==0&&icontSoles==0){
            	  valorFinal=valorDolar;
            	  this.solicitudRegistrarT.setImporte(valorFinal);
              }
              if(icontDolares==0&&icontEuros>0&&icontSoles==0){
            	  valorFinal=valorEuro;
            	  this.solicitudRegistrarT.setImporte(valorFinal);
              }
              if(icontDolares>0&&icontEuros>0&&icontSoles==0
            		  ||icontDolares>0&&icontEuros==0&&icontSoles>0
            		  ||icontDolares==0&&icontEuros>0&&icontSoles>0
            		  ||icontDolares==0&&icontEuros==0&&icontSoles==0){
            	  valorFinal=valorSoles_C+valorSolesD+valorSolesE;
            	  this.solicitudRegistrarT.setImporte(valorFinal);
              }
        		
        	  this.lstSolicBancarias.set(indexUpdateOperacion, objSolicBancaria);
          }
          objSolicBancaria=new TiivsSolicitudOperban();
          objSolicBancaria.setId(new TiivsSolicitudOperbanId());
          this.flagUpdateOperacionSolic=false;
          this.objSolicitudOperacionCapturado=new TiivsSolicitudOperban();
          this.objSolicitudOperacionCapturado.setId(new TiivsSolicitudOperbanId());
          this.llamarComision();
          

		}
		
	}
	public void limpiarListaSolicitudesBancarias(){
		logger.info("**************************** limpiarListaSolicitudesBancarias ****************************");
		this.lstSolicBancarias=new ArrayList<TiivsSolicitudOperban>();
	}
	public void seterComentario(){
		logger.info("**************************** Setear Comentario ****************************");
		System.out.println("Comentario : " +this.solicitudRegistrarT.getObs());
	}
	
	public void eliminarOperacionBancaria(){
		double valor=0;
		logger.info("**************************** eliminarOperacionBancaria ****************************");
		//String valor = Utilitarios.capturarParametro("objOperacion");
		// System.out.println("CODIGO DE OPERACION "+valor);
		 System.out.println(objSolicitudOperacionCapturado.getImporte());
		lstSolicBancarias.remove(objSolicitudOperacionCapturado);
		if(objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
			valorSoles_C-=objSolicitudOperacionCapturado.getImporte();
			icontSoles--;
		}
		if(objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
			valor=objSolicitudOperacionCapturado.getTipoCambio()*objSolicitudOperacionCapturado.getImporte();
			valorSolesD-=valor;
			valorDolar-=objSolicitudOperacionCapturado.getImporte();
			icontDolares--;
		}
		if(objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
			valor=objSolicitudOperacionCapturado.getTipoCambio()*objSolicitudOperacionCapturado.getImporte();
			valorSolesE-=valor;
			valorEuro-=objSolicitudOperacionCapturado.getImporte();
			icontEuros--;
		}

      if(icontDolares==0&&icontEuros==0&&icontSoles>0){
    	  valorFinal=valorSoles_C;
    	  this.solicitudRegistrarT.setImporte(valorFinal);
      }
      if(icontDolares>0&&icontEuros==0&&icontSoles==0){
    	  valorFinal=valorDolar;
    	  this.solicitudRegistrarT.setImporte(valorFinal);
      }
      if(icontDolares==0&&icontEuros>0&&icontSoles==0){
    	  valorFinal=valorEuro;
    	  this.solicitudRegistrarT.setImporte(valorFinal);
      }
      if(icontDolares>0&&icontEuros>0&&icontSoles==0
    		  ||icontDolares>0&&icontEuros==0&&icontSoles>0
    		  ||icontDolares==0&&icontEuros>0&&icontSoles>0
    		  ||icontDolares==0&&icontEuros==0&&icontSoles==0){
    	  valorFinal=valorSoles_C+valorSolesD+valorSolesE;
    	  this.solicitudRegistrarT.setImporte(valorFinal);
      }
     
      objSolicitudOperacionCapturado=new TiivsSolicitudOperban();
      this.llamarComision();
	     
	}
	public void eliminarArupacion(){
		logger.info("********************** eliminarArupacion *********************************** ");
		this.lstAgrupacionSimpleDto.remove(this.objAgrupacionSimpleDtoCapturado);
		//this.lstTiivsAgrupacionPersonas.remove(this.objAgrupacionSimpleDtoCapturado.getId().)
		this.llamarComision();
	}
	public void editarOperacionBancaria(){
		for (int i = 0; i < this.lstSolicBancarias.size(); i++) {
			if(objSolicitudOperacionCapturado.equals(this.lstSolicBancarias.get(i))){
				indexUpdateOperacion=i;
			}
		}
		this.objSolicBancaria=this.objSolicitudOperacionCapturado;
		this.flagUpdateOperacionSolic=true;
		
	}
	public void llamarComision(){
		logger.info("************************** llamar Comusion *****************************");
		this.solicitudRegistrarT.setComision(objRegistroUtilesMB.calcularComision(this.solicitudRegistrarT));
		logger.info("COMISION : " +this.solicitudRegistrarT.getComision());
		
	}
	
	@SuppressWarnings("unused")
	public void registrarSolicitud(){
		String mensaje="";
		logger.info("*********************** registrarSolicitud ************************");
		 GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		 GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		 GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			 
		 GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	       try {
	    	   this.solicitudRegistrarT.setFecha(new Date());
			   
			   logger.info("usuario.getUID() " +usuario.getUID());
			   this.solicitudRegistrarT.setRegUsuario(usuario.getUID());
			   this.solicitudRegistrarT.setNomUsuario(usuario.getNombre());
			   logger.info("tiivsOficina1.codOfi ::::::: "+this.solicitudRegistrarT.getTiivsOficina1().getCodOfi());
			   for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
				if(tiivsOficina1.getCodOfi().equals(this.solicitudRegistrarT.getTiivsOficina1().getCodOfi())){
					this.solicitudRegistrarT.setTiivsOficina1(tiivsOficina1);
				}
			}
			   
			  System.out.println("solicitudRegistrarT.getTiivsSolicitudAgrupacions() : "+solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
			  if(this.validarRegistroSolicitud()){
				  if(!this.sEstadoSolicitud.equals("BORRADOR")){
					  this.enviarSolicitudSSJJ();  
		        	}
			  TiivsSolicitud objResultado= service.insertar(this.solicitudRegistrarT);
			/*  for (TiivsSolicitudAgrupacion x : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
				  for (TiivsAgrupacionPersona b : x.getTiivsAgrupacionPersonas()) {
					TiivsPersona objPer=servicePers.insertar(b.getTiivsPersona());
					b.setTiivsPersona(objPer);
					System.out.println("objPer "+objPer.getCodPer());
					serviceAgru.insertar(b);
				  }
			}*/
			  for (TiivsSolicitudOperban a : this.lstSolicBancarias) {
				  System.out.println("a.getId().getCodOperBan() **** " +a.getId().getCodOperBan());
				  a.getId().setCodSoli(this.solicitudRegistrarT.getCodSoli());
				  System.out.println("a.getId().getCodSoli() **** " +a.getId().getCodSoli());
				  TiivsSolicitudOperban objResulOperaciones=serviceSoli.insertar(a);
			   }
			  if(objResultado.getCodSoli()!=""||objResultado!=null){ 
				  mensaje="Se registro correctamente la Solicitud con codigo : "+objResultado.getCodSoli();
				 Utilitarios.mensajeInfo("INFO", mensaje);
			  }else{
				  mensaje="Error al generar la Solicitud ";
				 Utilitarios.mensajeInfo("INFO", mensaje);
				 }
			  
			  System.out.println("objResultado.getCodSoli(); "+objResultado.getCodSoli());
			  System.out.println("objResultado.getTiivsSolicitudAgrupacions() "+objResultado.getTiivsSolicitudAgrupacions().size());
			  
			  instanciarSolicitudRegistro();
			  }
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	public void enviarSolicitudSSJJ(){
		Timestamp time = new Timestamp(objRegistroUtilesMB.obtenerFechaRespuesta().getTime());
		System.out.println("time : " +time);
		String sCodigoEstudio=objRegistroUtilesMB.obtenerEstudioMenorCarga();
		System.out.println(" sCodigoEstudio +  "+sCodigoEstudio);
		for (TiivsEstudio x : combosMB.getLstEstudio()) {
			if(x.getCodEstudio().equals(sCodigoEstudio)){
				this.solicitudRegistrarT.setTiivsEstudio(x);
			}
		}
		this.solicitudRegistrarT.setEstado(ConstantesVisado.CODIGO_ESTADO_ENVIADO);
		this.solicitudRegistrarT.setFechaRespuesta(time);
		this.solicitudRegistrarT.setFechaEnvio(new Timestamp(new Date().getTime()));
	}
	
	public void registrarSolicitudBorrador(){
		sEstadoSolicitud="BORRADOR";
	}
	public void registrarSolicitudEnviado(){
		sEstadoSolicitud="ENVIADO";
	}

	public boolean validarRegistroSolicitud(){
		boolean retorno=true;
		String mensaje="";
	/*	if(solicitudRegistrarT.getTiivsOficina1()==null){
			//solicitudRegistrarT.getTiivsOficina1().getCodOfi().equals(""))

			mensaje="Ingrese la Oficina";
			retorno=false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if(solicitudRegistrarT.getNroVoucher()==null||solicitudRegistrarT.getNroVoucher().equals("")){
			mensaje="Ingrese el Nro Voucher";
			retorno=false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}else if(solicitudRegistrarT.getNroVoucher().length()<11){
			mensaje="Ingrese Nro Voucher correcto de 11 digitos";
			retorno=false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}*/
		if(solicitudRegistrarT.getTiivsSolicitudAgrupacions().size()==0){
			mensaje="Ingrese la secci�n Apoderado y Poderdante";
			retorno=false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if(solicitudRegistrarT.getTiivsTipoSolicitud()==(null)){
			mensaje="Seleccione el Tipo de Solicitud ";
			retorno=false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if(this.lstAnexoSolicitud.size()==0){
			mensaje="Ingrese los documentos Obligatorios";
			retorno=false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}

		if(this.lstSolicBancarias.size()==0){

			mensaje="Ingrese al menos una Operaci�n Bancaria";
			retorno=false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		return retorno;
	}
	
	public String prepararURLEscaneo(){
		
		String cadFinal="";
		
		for (TiivsParametros tmp: pdfViewerMB.getLstParametros()){
			cadFinal=tmp.getUrlAPP()+"?"+"idEmpresa="+tmp.getIdEmpresa()+"&"+"idSistema="+tmp.getIdSistema()+"&"+"txtLogin="+usuario.getUID();
		}
		
		System.out.println("URL: " + cadFinal);
		
		return cadFinal;
	}
	
	public void upload(){
		//if (pdfViewerMB.cargarPDFRemoto("o"))
		{
		//	logger.info("pdfViewerMB.cargarPDFRemoto(:  " +pdfViewerMB.cargarPDFRemoto("o"));
		}
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
	public List<DocumentoTipoSolicitudDTO> getLstdocumentosOpcional() {
		return lstdocumentosOpcional;
	}
	public void setLstdocumentosOpcional(
			List<DocumentoTipoSolicitudDTO> lstdocumentosOpcional) {
		this.lstdocumentosOpcional = lstdocumentosOpcional;
	}
	public List<TiivsOperacionBancaria> getLstTiivsOperacionBancaria() {
		return lstTiivsOperacionBancaria;
	}
	public void setLstTiivsOperacionBancaria(List<TiivsOperacionBancaria> lstTiivsOperacionBancaria) {
		this.lstTiivsOperacionBancaria = lstTiivsOperacionBancaria;
	}
	public Solicitud getSolicitudRegistrar() {
		return solicitudRegistrar;
	}
	public void setSolicitudRegistrar(Solicitud solicitudRegistrar) {
		this.solicitudRegistrar = solicitudRegistrar;
	}
	
	public TiivsPersona getObjTiivsPersonaBusqueda() {
		return objTiivsPersonaBusqueda;
	}

	public void setObjTiivsPersonaBusqueda(TiivsPersona objTiivsPersonaBusqueda) {
		this.objTiivsPersonaBusqueda = objTiivsPersonaBusqueda;
	}

	public TiivsPersona getObjTiivsPersonaResultado() {
		return objTiivsPersonaResultado;
	}
	public void setObjTiivsPersonaResultado(TiivsPersona objTiivsPersonaResultado) {
		this.objTiivsPersonaResultado = objTiivsPersonaResultado;
	}
	public List<TiivsPersona> getLstTiivsPersona() {
		return lstTiivsPersona;
	}
	public void setLstTiivsPersona(List<TiivsPersona> lstTiivsPersona) {
		this.lstTiivsPersona = lstTiivsPersona;
	}

	public PersonaDataModal getPersonaDataModal() {
		return personaDataModal;
	}

	public void setPersonaDataModal(PersonaDataModal personaDataModal) {
		this.personaDataModal = personaDataModal;
	}
		
	
	public TiivsPersona getObjTiivsPersonaSeleccionado() {
		return objTiivsPersonaSeleccionado;
	}

	public void setObjTiivsPersonaSeleccionado(
			TiivsPersona objTiivsPersonaSeleccionado) {
		this.objTiivsPersonaSeleccionado = objTiivsPersonaSeleccionado;
	}
	
	public List<TiivsPersona> getLstTiivsPersonaResultado() {
		return lstTiivsPersonaResultado;
	}

	public void setLstTiivsPersonaResultado(
			List<TiivsPersona> lstTiivsPersonaResultado) {
		this.lstTiivsPersonaResultado = lstTiivsPersonaResultado;
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public Set<TiivsAgrupacionPersona> getLstTiivsAgrupacionPersonas() {
		return lstTiivsAgrupacionPersonas;
	}

	public void setLstTiivsAgrupacionPersonas(
			Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas) {
		this.lstTiivsAgrupacionPersonas = lstTiivsAgrupacionPersonas;
	}
	
	public boolean isbBooleanPopup() {
		return bBooleanPopup;
	}

	public void setbBooleanPopup(boolean bBooleanPopup) {
		this.bBooleanPopup = bBooleanPopup;
	}
	public List<AgrupacionSimpleDto> getLstAgrupacionSimpleDto() {
		return lstAgrupacionSimpleDto;
	}
	public void setLstAgrupacionSimpleDto(
			List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;
	}
	public String getiTipoSolicitud() {
		return iTipoSolicitud;
	}
	public void setiTipoSolicitud(String iTipoSolicitud) {
		this.iTipoSolicitud = iTipoSolicitud;
	}
	public List<TiivsTipoSolicDocumento> getLstTipoSolicitudDocumentos() {
		return lstTipoSolicitudDocumentos;
	}
	public void setLstTipoSolicitudDocumentos(
			List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos) {
		this.lstTipoSolicitudDocumentos = lstTipoSolicitudDocumentos;
	}

	public List<TiivsAnexoSolicitud> getLstAnexoSolicitud() {
		return lstAnexoSolicitud;
	}

	public void setLstAnexoSolicitud(List<TiivsAnexoSolicitud> lstAnexoSolicitud) {
		this.lstAnexoSolicitud = lstAnexoSolicitud;
	}

	public TiivsSolicitud getSolicitudRegistrarT() {
		return solicitudRegistrarT;
	}

	public void setSolicitudRegistrarT(TiivsSolicitud solicitudRegistrarT) {
		this.solicitudRegistrarT = solicitudRegistrarT;
	}

	

	public String getsCodDocumento() {
		return sCodDocumento;
	}

	public void setsCodDocumento(String sCodDocumento) {
		this.sCodDocumento = sCodDocumento;
	}

	public TiivsSolicitudOperban getObjSolicBancaria() {
		return objSolicBancaria;
	}

	public void setObjSolicBancaria(TiivsSolicitudOperban objSolicBancaria) {
		this.objSolicBancaria = objSolicBancaria;
	}

	public List<TiivsSolicitudOperban> getLstSolicBancarias() {
		return lstSolicBancarias;
	}

	public void setLstSolicBancarias(List<TiivsSolicitudOperban> lstSolicBancarias) {
		this.lstSolicBancarias = lstSolicBancarias;
	}

	public PDFViewerMB getPdfViewerMB() {
		return pdfViewerMB;
	}

	public void setPdfViewerMB(PDFViewerMB pdfViewerMB) {
		this.pdfViewerMB = pdfViewerMB;
	}

	public Set<TiivsSolicitudAgrupacion> getLstTiivsSolicitudAgrupacion() {
		return lstTiivsSolicitudAgrupacion;
	}

	public void setLstTiivsSolicitudAgrupacion(
			Set<TiivsSolicitudAgrupacion> lstTiivsSolicitudAgrupacion) {
		this.lstTiivsSolicitudAgrupacion = lstTiivsSolicitudAgrupacion;
	}

	public RegistroUtilesMB getObjRegistroUtilesMB() {
		return objRegistroUtilesMB;
	}

	public void setObjRegistroUtilesMB(RegistroUtilesMB objRegistroUtilesMB) {
		this.objRegistroUtilesMB = objRegistroUtilesMB;
	}

	public TiivsSolicitudOperban getObjSolicitudOperacionCapturado() {
		return objSolicitudOperacionCapturado;
	}

	public void setObjSolicitudOperacionCapturado(
			TiivsSolicitudOperban objSolicitudOperacionCapturado) {
		this.objSolicitudOperacionCapturado = objSolicitudOperacionCapturado;
	}

	public AgrupacionSimpleDto getObjAgrupacionSimpleDtoCapturado() {
		return objAgrupacionSimpleDtoCapturado;
	}

	public void setObjAgrupacionSimpleDtoCapturado(
			AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado) {
		this.objAgrupacionSimpleDtoCapturado = objAgrupacionSimpleDtoCapturado;
	}

	public TiivsTipoSolicDocumento getObjDocumentoXSolicitudCapturado() {
		return objDocumentoXSolicitudCapturado;
	}

	public void setObjDocumentoXSolicitudCapturado(
			TiivsTipoSolicDocumento objDocumentoXSolicitudCapturado) {
		this.objDocumentoXSolicitudCapturado = objDocumentoXSolicitudCapturado;
	}
	

}

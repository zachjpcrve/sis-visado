package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.primefaces.event.FileUploadEvent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
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
import com.hildebrando.visado.dto.UsuarioLDAP2;
import com.hildebrando.visado.modelo.Ldapperu2;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersonaId;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitudId;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
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
	private TiivsSolicitudOperban objSolicBancaria;
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
	private List<AgrupacionSimpleDto>  lstAgrupacionSimpleDto;
	private List<TiivsAnexoSolicitud> lstAnexoSolicitud;
	private PersonaDataModal personaDataModal;
	private String iTipoSolicitud="";
	//private TiivsTipoSolicDocumento objDocumentoXSolicitud ;
	private String sCodDocumento;

	private int numGrupo=0;
	
	

	private List<TiivsPersona> lstTiivsPersona;
	private List<TiivsPersona> lstTiivsPersonaResultado;
	private List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos;
	private List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp;
	boolean bBooleanPopup =false;
	
	
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
		solicitudRegistrarT=new TiivsSolicitud();
		solicitudRegistrarT.setTiivsOficina1(new TiivsOficina1());
		lstDocumentosXTipoSolTemp=new ArrayList<TiivsTipoSolicDocumento>();
		lstAnexoSolicitud=new ArrayList<TiivsAnexoSolicitud>();
		//objDocumentoXSolicitud=new TiivsTipoSolicDocumento();
		lstdocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
		objSolicBancaria=new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		instanciarSolicitudRegistro();
		
	}
	
	public void handleFileUpload(FileUploadEvent event) {  
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
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
	        	Utilitarios.mensajeInfo("INFO", "Ingrese el Número de Doi");
	        }else{
	        	  if(objTiivsPersonaBusqueda.getTipDoi()!=null && objTiivsPersonaBusqueda.getNumDoi()!=null){
		                 filtro.add(Restrictions.eq("tipDoi", objTiivsPersonaBusqueda.getTipDoi()));
		                 filtro.add(Restrictions.eq("numDoi", objTiivsPersonaBusqueda.getNumDoi()));
		        }
		        if(objTiivsPersonaBusqueda.getCodCen()!=null){
		        	     filtro.add(Restrictions.eq("codCen", objTiivsPersonaBusqueda.getCodCen()));
		        }
		       // lstTiivsPersona=service.buscarDinamico(filtro);
		        lstTiivsPersona =this.manipularDataPruebaPopup(objTiivsPersonaBusqueda.getNumDoi());
			    for (TiivsPersona tiivsPersona : lstTiivsPersona) {
				    for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
						if(tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())){
							tiivsPersona.setsDesctipDoi(p.getDescripcion());
						}
					}
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
	
	
	
	public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(){
		  int iNumGrupo=0;
		  Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion=new HashSet<TiivsSolicitudAgrupacion>();
		  TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion=new TiivsSolicitudAgrupacion();
		  TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId=new TiivsSolicitudAgrupacionId();
		  tiivsSolicitudAgrupacionId.setCodSoli(solicitudRegistrarT.getCodSoli());
		  tiivsSolicitudAgrupacionId.setNumGrupo(iNumGrupo+1);
		  tiivsSolicitudAgrupacion.setId(tiivsSolicitudAgrupacionId);
		  tiivsSolicitudAgrupacion.setActivo("1");
		  lstSolicitudArupacion.add(tiivsSolicitudAgrupacion);
		  return lstSolicitudArupacion;
	}
	public void  agregarAgrupacionPersona(TiivsPersona objTiivsPersonaResultado ){
	TiivsAgrupacionPersona objTiivsAgrupacionPersona=new TiivsAgrupacionPersona();
	objTiivsAgrupacionPersona.setTiivsPersona(objTiivsPersonaResultado);
	objTiivsAgrupacionPersona.setId(new TiivsAgrupacionPersonaId(null, numGrupo, objTiivsPersonaResultado.getCodPer(), objTiivsPersonaResultado.getTipPartic(), objTiivsPersonaResultado.getClasifPer()));
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
				 sMensaje="Ya existe una persona con número de Doi :  "+p.getNumDoi()  +" y Tipo de Registro : " +objTiivsPersonaResultado.getsDesctipPartic();
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
		if(objTiivsPersonaResultado.getClasifPer()==null||objTiivsPersonaResultado.getClasifPer().equals("")){
			sMensaje="Ingrese el Tipo de Clasificación";
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
	   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId("xxxxx", numGrupo));
	   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
	   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
	   agrupacionSimpleDto.setsEstado("Activo");
	   agrupacionSimpleDto.setiEstado(1);
	  lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
	 System.out.println("tamanio de lstTiivsAgrupacionPersonas "+lstTiivsAgrupacionPersonas.size());
 }
  

	public void instanciarSolicitudRegistro() {
		logger.info("********************** instanciarSolicitudRegistro *********************");
		Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
		TiivsSolicitud objSolicitudRegistro=new TiivsSolicitud();
		objSolicitudRegistro.setTiivsSolicitudAgrupacions(lstTiivsAgrupacionPersonas);
		objSolicitudRegistro.setTiivsOficina1(new TiivsOficina1());
		objSolicitudRegistro.setTiivsTipoSolicitud(new TiivsTipoSolicitud());
		String grupoAdm = (String)Utilitarios.getObjectInSession("GRUPO_ADM");
		String grupoOfi = (String)Utilitarios.getObjectInSession("GRUPO_OFI");
		
		logger.info("********grupoAdm ****** "+grupoAdm +"  ******* grupoOfi ******** " +grupoOfi);
		//if (grupoAdm == null && grupoOfi!= null) {
		    IILDPeUsuario  usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		    logger.info("usuario en session? --> "+usuario.getNombre());
			System.out.println("CodOfi: "+usuario.getBancoOficina().getCodigo().trim());
			System.out.println("DesOfi: "+usuario.getBancoOficina().getDescripcion().trim());
			
			
			TiivsOficina1 oficina=new TiivsOficina1();
			oficina.setCodOfi(usuario.getBancoOficina().getCodigo());
			
			 List<TiivsOficina1> lstOficinas1=new ArrayList<TiivsOficina1>();
			 combosMB=new CombosMB();
			 lstOficinas1 =combosMB.getLstOficina();
			 
			if(lstOficinas1!=null && lstOficinas1.size()!=0){
			for (TiivsOficina1 o : lstOficinas1) {
				if(usuario.getBancoOficina().getCodigo().equals(o.getCodOfi())){
					this.solicitudRegistrarT.setTiivsOficina1(o);
				}
			}
			}
			 SolicitudDao<TiivsPersona, Object> service = (SolicitudDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
			 try {
				String sCodigoSol=service.obtenerPKNuevaSolicitud();
				logger.info(" sCodigoSol " + sCodigoSol);
				this.solicitudRegistrarT.setCodSoli(sCodigoSol);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 this.solicitudRegistrarT.setEstado("1");
			 this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_REGISTRADO);
		
		//}		
		
	}
	public void actualizarListaDocumentosXTipo(){
		logger.info("****************************** actualizarListaDocumentosXTipo *********************************");
		
		for (TiivsTipoSolicDocumento s : lstDocumentosXTipoSolTemp) {
			if(s.getCodDoc().equals(sCodDocumento)){
				//this.lstTipoSolicitudDocumentos.remove(s);
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
	

}

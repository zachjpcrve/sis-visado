package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecDUMMY;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Persona;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
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
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

@ManagedBean(name = "solicitudRegMB")
@SessionScoped
public class SolicitudRegistroMB {
	
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
	private PersonaDataModal personaDataModal;
	
	private int numGrupo=0;
	
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;

	private List<TiivsPersona> lstTiivsPersona;
	private List<TiivsPersona> lstTiivsPersonaResultado;
	
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
		inicializarValores();
		instanciarSolicitudRegistro();
		
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
		return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
	}
	
	
	public void inicializarValores(){
		System.out.println(" **********************inicializarValores *********************");
		IILDPeUsuario usuario=(IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		logger.info("usuario en session? --> "+usuario.getNombre());
		System.out.println("usuario en session? --> "+usuario.getNombre());
	}
	public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(){
		  int iNumGrupo=0;
		  Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion=new HashSet<TiivsSolicitudAgrupacion>();
		  TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion=new TiivsSolicitudAgrupacion();
		  TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId=new TiivsSolicitudAgrupacionId();
		  tiivsSolicitudAgrupacionId.setCodSoli("");
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
		  tiivsAgrupacionPersonaId.setCodSoli("XXXXXXXX");
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
	public void instanciarSolicitudRegistro(){
		logger.info("********************** instanciarSolicitudRegistro *********************");
		Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
		TiivsSolicitud objSolicitudRegistro=new TiivsSolicitud();
		objSolicitudRegistro.setTiivsSolicitudAgrupacions(lstTiivsAgrupacionPersonas);
		objSolicitudRegistro.setTiivsOficina1(new TiivsOficina1());
		objSolicitudRegistro.setTiivsTipoSolicitud(new TiivsTipoSolicitud());
		String grupoAdm = (String)Utilitarios.getObjectInSession("GRUPO_ADM");
		String grupoOfi = (String)Utilitarios.getObjectInSession("GRUPO_OFI");
		
		logger.info("********grupoAdm ****** "+grupoAdm +"  ******* grupoOfi ******** " +grupoOfi);
		if (grupoAdm == null && grupoOfi!= null) {
			UsuarioLDAP2 usuario = (UsuarioLDAP2) Utilitarios.getObjectInSession("USUARIO_SESION");
			System.out.println("CodOfi: "+usuario.getCodofi().trim());
			System.out.println("DesOfi: "+usuario.getDesofi().trim());
		}
	}
	
/*	public String btnNuevoAction() {
		try {
			
			logger.info("---btnNuevoAction---");
			String grupoAdm = (String)Utilitarios.getObjectInSession("GRUPO_ADM");
			String grupoOfi = (String)Utilitarios.getObjectInSession("GRUPO_OFI");
			this.solicitudModificar=new Solicitud();
		    //TiivsSolicitud solicitud = new TiivsSolicitud();
			lstClientes=new ArrayList<ApoderadoDTO>();
			lstOperaciones=new ArrayList<OperacionBancariaDTO>();
			lstdocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
			lstdocumentosOpcional=new ArrayList<DocumentoTipoSolicitudDTO>();
			
			if (grupoAdm == null && grupoOfi!= null) {
				
				UsuarioLDAP2 usuario = (UsuarioLDAP2) Utilitarios.getObjectInSession("USUARIO_SESION");
				System.out.println("CodOfi: "+usuario.getCodofi().trim());
				System.out.println("DesOfi: "+usuario.getDesofi().trim());
				
				TiivsOficina1 oficina=new TiivsOficina1();
				oficina.setCodOfi(usuario.getCodofi());
				GenericDao<TiivsOficina1, Object> genericDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				//Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);
				List<TiivsOficina1> lstOficina1=(List<TiivsOficina1>) genericDAO.buscarById(TiivsOficina1.class, usuario.getCodofi());
				ArrayList listaOfi=(ArrayList) this.getSolicitudModel().getOficina1Service().selectDynamicWhere(oficina);
				if(listaOfi!=null)
				{
					oficina=(Oficina1) listaOfi.get(0);
					System.out.println("CodOfi: "+oficina.getCodOfi().trim());
					System.out.println("DesOfi: "+oficina.getDesOfi().trim());
					this.getSolicitudModel().getSolicitudRegistrar().setCodOfi(oficina.getCodOfi());				
					this.getSolicitudModel().getSolicitudRegistrar().setDesOfi(oficina.getDesOfi().trim());
				}
			}		
			
			String codSoli=this.getSolicitudModel().getSolicitudService().selectNextPK();
			System.out.println("codSoli: "+codSoli);
			if (codSoli==null) {
				this.getSolicitudModel().getSolicitudRegistrar().setCodSoli("0000001");	
			}else
			this.getSolicitudModel().getSolicitudRegistrar().setCodSoli(codSoli);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			return null;
		}
			this.getSolicitudModel().setLblError_Text("");
			this.getSolicitudModel().setLblInfo_Text("");
			logger.trace("--nuevo--");
			System.out.println("--nuevo--");
			return "nuevo";
	}
	*/
 

	

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
	

}

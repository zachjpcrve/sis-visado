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
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

@ManagedBean(name = "consultarSolicitudMB")
@SessionScoped
public class ConsultarSolicitudMB 
{
	public static Logger logger = Logger.getLogger(ConsultarSolicitudMB.class);
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	@ManagedProperty(value = "#{seguimientoMB}")
	private SeguimientoMB seguimientoMB;
	private TiivsSolicitud solicitudRegistrarT;
	private List<TiivsSolicitudOperban> lstSolicBancarias;
	private List<TiivsAnexoSolicitud> lstAnexosSolicitudes;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	private AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado;
	private List<DocumentoTipoSolicitudDTO> lstdocumentos;
	private List<SeguimientoDTO> lstSeguimientoDTO;
    private List<TiivsDocumento> lstTiivsDocumentos;	
    private List<TiivsMiembro> lstAbogados;
    private List<ComboDto>lstComboDictamen;
	private boolean bSeccionDictaminar=false;
	private boolean bSeccionReasignacion=false;
	private boolean bSeccionCartaAtencion=false;
	private boolean bSeccionComentario =false;
	private String valorDictamen="";
	private String descValorDictamen="";
	private List<ComboDto> lstDocumentosGenerados;
	private String textoMensajeCartaAtencion;
	private boolean bMostrarCartaAtencion;
	
	public ConsultarSolicitudMB() {
		inicializarContructor();
		cargarDocumentos();
		listarComboDictamen();
		modificarTextoVentanaCartaAtencion();
		mostrarCartaAtencion();
	}	
	
	public void modificarTextoVentanaCartaAtencion()
	{
		String PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ))
		{
			setTextoMensajeCartaAtencion(ConstantesVisado.MENSAJE_CARTA_ATENCION.MENSA_SSJJ);
		}
		else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
		{
			setTextoMensajeCartaAtencion(ConstantesVisado.MENSAJE_CARTA_ATENCION.MENSAJE_OFICINA);
		}
		
		logger.info("Texto Ventana: " + getTextoMensajeCartaAtencion());
	}
	
	public void mostrarCartaAtencion()
	{
		String PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		String codigoSolicitud=Utilitarios.capturarParametro("prm_codSoli");
	    
		if (codigoSolicitud!=null)
		{
			try {
				logger.info("codigoSolicitud : "+codigoSolicitud);
				TiivsSolicitud solicitud =new TiivsSolicitud();
				solicitud.setCodSoli(codigoSolicitud);
				SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
				solicitudRegistrarT= solicitudService.obtenerTiivsSolicitud(solicitud);
				solicitudRegistrarT.setDescEstado(Utilitarios.obternerDescripcionEstado(solicitudRegistrarT.getEstado()));
			}  catch (Exception e) {
				logger.info("No se pueden obtener los datos de la solicitud");
			}
			   
			if((PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) || PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
			  && (!this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02)))
			{
				if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02) 
					&& PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
				{
					setbMostrarCartaAtencion(false);
					logger.info("No Se debe mostrar el link de carta de atencion");
				}
				else
				{
					setbMostrarCartaAtencion(true);
					logger.info("Se debe mostrar el link de carta de atencion");
				}
			}
			else
			{
				setbMostrarCartaAtencion(false);
				logger.info("No Se debe mostrar el link de carta de atencion");
			}
		}
		else
		{
			logger.info("Solicitud no valida o nula");
		}
	}
	
	public void inicializarContructor(){
		solicitudRegistrarT=new TiivsSolicitud();
		
		lstSolicBancarias=new ArrayList<TiivsSolicitudOperban>();
		lstAnexosSolicitudes=new ArrayList<TiivsAnexoSolicitud>();
		lstAgrupacionSimpleDto=new ArrayList<AgrupacionSimpleDto>();
		objAgrupacionSimpleDtoCapturado=new AgrupacionSimpleDto();
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		lstSeguimientoDTO=new ArrayList<SeguimientoDTO>();
		lstAbogados=new ArrayList<TiivsMiembro>();
		lstTiivsDocumentos= new ArrayList<TiivsDocumento>();
		lstDocumentosGenerados=new ArrayList<ComboDto>();
	}			
    public void listarComboDictamen(){
    	lstComboDictamen=new ArrayList<ComboDto>();
    	lstComboDictamen.add(new ComboDto(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02,ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02));
    	lstComboDictamen.add(new ComboDto(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02,ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02));
    }
	public String redirectDetalleSolicitud() {
		logger.info(" **** redirectDetalleSolicitud ***");
		obtenerSolicitud();
		return "/faces/paginas/detalleSolicitud.xhtml";
		
	}
	
	public void abogadosXEstudios(ValueChangeEvent e){
		logger.info("*************** abogadosXEstudios *********************" +e.getNewValue());
	List<TiivsMiembro>	lstAbogadosMiembro=combosMB.getLstAbogados();
	lstAbogados=new ArrayList<TiivsMiembro>();
		for (TiivsMiembro x : lstAbogadosMiembro) {
			if(x.getEstudio().trim().equals(e.getNewValue())){
				lstAbogados.add(x);
			}
			
		}
	}
    
	public void reasignacionManual(){
		 logger.info("********** reasignacionManual *********");
		 logger.info(""+solicitudRegistrarT.getTiivsEstudio().getCodEstudio());
		 logger.info(""+solicitudRegistrarT.getRegAbogado());
			try {
		     GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		     solicitudRegistrarT= service.modificar(solicitudRegistrarT);
		     Utilitarios.mensajeInfo("INFO", "Se realizó la reasignación manual Correctamente");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerSolicitud(){
		try {
		   lstAgrupacionSimpleDto=new ArrayList<AgrupacionSimpleDto>();
		   String codigoSolicitud=Utilitarios.capturarParametro("prm_codSoli");
		   logger.info("codigoSolicitud : "+codigoSolicitud);
		   TiivsSolicitud solicitud =new TiivsSolicitud();
		   solicitud.setCodSoli(codigoSolicitud);
		   SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		   solicitudRegistrarT= solicitudService.obtenerTiivsSolicitud(solicitud);
		   solicitudRegistrarT.setDescEstado(Utilitarios.obternerDescripcionEstado(solicitudRegistrarT.getEstado()));
		   if(solicitudRegistrarT.getTiivsEstudio()==null){
		   solicitudRegistrarT.setTiivsEstudio(new TiivsEstudio());
		   }
		   lstSolicBancarias=solicitudService.obtenerListarOperacionesBancarias(solicitud);
		   int y=0;
		   for (TiivsSolicitudOperban f : lstSolicBancarias) {
			y++;
			 f.setsItem(String.format("%03d",y));
			 f.setsDescMoneda(Utilitarios.obternerDescripcionMoneda(f.getMoneda()));
		   }
		   lstAnexosSolicitudes=solicitudService.obtenerListarAnexosSolicitud(solicitud);
		   
		   //descargar anexos
		  // descargarAnexosFileServer();
		   
		   lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		   int i=0;
		  for (TiivsAnexoSolicitud v : lstAnexosSolicitudes) {
			  i++;
			  lstdocumentos.add(new DocumentoTipoSolicitudDTO(String.format("%03d",i) , obtenerDescripcionDocumento(v.getId().getCodDoc()), v.getAliasTemporal()));
		     }
		   // PODERDANTES Y APODERADOS
		    List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
		   AgrupacionSimpleDto agrupacionSimpleDto  =new AgrupacionSimpleDto(); ;
		   List<TiivsPersona>lstPersonas=new ArrayList<TiivsPersona>();
		   TiivsPersona objPersona=new TiivsPersona();
		   for (TiivsSolicitudAgrupacion x : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
			   for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) {
				   System.out.println("d.getTiivsPersona() "+d.getTiivsPersona());
				   objPersona=new TiivsPersona();
				   objPersona=d.getTiivsPersona();
				   objPersona.setTipPartic(d.getId().getTipPartic());
				   objPersona.setClasifPer(d.getId().getClasifPer());
				  lstPersonas.add(objPersona);
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
			   agrupacionSimpleDto.setLstPersonas(lstPersonas);
			   lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
			   
		   }
		  
		  
		  this.actualizarEstadoReservadoSolicitud();
		  this.obtenerHistorialSolicitud();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	public void actualizarEstadoReservadoSolicitud() throws Exception{
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		String PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		logger.info("*********************** actualizarEstadoReservadoSolicitud **************************");
		if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02)){
		if(PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO) ){
			//Seccion Dictaminar 
		this.bSeccionDictaminar=true;
		this.bSeccionComentario=true;
		this.bSeccionReasignacion=false;
			
		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02);
		this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_RESERVADO_T02);
		 GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		 service.modificar(solicitudRegistrarT);
		 this.registrarHistorial(solicitudRegistrarT,"2");
		 
		}else if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
			//Seccion Reasgnar 
		this.bSeccionDictaminar=false;
		this.bSeccionComentario=false;
		this.bSeccionReasignacion=true;
		
		}else if(PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)){
			this.bSeccionDictaminar=false;
			this.bSeccionComentario=false;
			this.bSeccionReasignacion=false;
		}
	}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02)){
		if(PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO) ){
			this.bSeccionDictaminar=true;
			this.bSeccionComentario=true;
			this.bSeccionReasignacion=false;
		}else if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
			this.bSeccionDictaminar=true;
			this.bSeccionComentario=true;
			this.bSeccionReasignacion=false;
			
		}else if(PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)){
			this.bSeccionDictaminar=false;
			this.bSeccionComentario=false;
			this.bSeccionReasignacion=false;
		}
	}
		
	}
	public void obtenerDictamen(ValueChangeEvent e){
		logger.info("****************** obtenerDictamen ********************** "+e.getNewValue());
		
		if(e.getNewValue()!=null){
			if(e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02))
			descValorDictamen=ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02;
			else if(e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02))
				descValorDictamen=ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02;
		}
		
	}
	public void dictaminarSolicitud(){
		logger.info("********************** dictaminarSolicitud *********************************** ");
		logger.info("********** "+valorDictamen);
		 GenericDao<TiivsSolicitud, Object> serviceS = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		if(this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
			
			//Llamada a los Niveles
			try {
				this.agregarNiveles(solicitudRegistrarT);
			
			this.bSeccionDictaminar=false;
			this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
			  
				solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
				this.registrarHistorial(solicitudRegistrarT,"3");
			} catch (Exception e) {
				e.printStackTrace();
			}
		     Utilitarios.mensajeInfo("INFO", "Se dictaminó correctamente la solicitud");
			
		}else if(this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)){
			 try {
				this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
					solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT,"3");
					  Utilitarios.mensajeInfo("INFO", "Se dictaminó correctamente la solicitud");
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		if(this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)
				|| this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){
			bSeccionCartaAtencion=true;
			bSeccionComentario=false;
		}
		this.obtenerHistorialSolicitud();
	}
	
	  public void agregarNiveles(TiivsSolicitud solicitud) throws Exception{
		  logger.info("*********************************** agregarNiveles ********************************************");
		  GenericDao<TiivsNivel, Object> service=(GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		  List<TiivsNivel>lstNiveles=service.buscarDinamico(Busqueda.forClass(TiivsNivel.class));
		 List<String> lstCodNivel=new ArrayList<String>();
			if(solicitud.getImporte().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
				logger.info("*********************************** COD_SOLES ********************************************"+this.solicitudRegistrarT.getImporte());
				 for (TiivsNivel x : lstNiveles) {
					 if(x.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
						 if(x.getId().getRangoInicio()<=this.solicitudRegistrarT.getImporte()&&this.solicitudRegistrarT.getImporte()<=x.getId().getRangoFin()){
							 lstCodNivel.add(x.getId().getCodNiv());
							 logger.info("x.getId().getDesNiv() ::: " +x.getId().getDesNiv()); 
							 logger.info("x.getId().getRangoInicio(): " +x.getId().getRangoInicio());
							 logger.info("x.getId().getRangoFin(): " +x.getId().getRangoFin());
						 }
					 }
			     }
			}
			else if(solicitud.getImporte().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
				logger.info("*********************************** COD_DOLAR ********************************************");
				 for (TiivsNivel x : lstNiveles) {
					 if(x.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
						 if(x.getId().getRangoInicio()<=this.solicitudRegistrarT.getImporte()&&this.solicitudRegistrarT.getImporte()<=x.getId().getRangoFin()){
							 lstCodNivel.add(x.getId().getCodNiv());
							 logger.info("x.getId().getDesNiv() ::: " +x.getId().getDesNiv()); 
						 }
					 }
			     }
			}
			else if(solicitud.getImporte().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
				logger.info("*********************************** COD_EUROS ********************************************");
				 for (TiivsNivel x : lstNiveles) {
					 if(x.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
						 if(x.getId().getRangoInicio()<=this.solicitudRegistrarT.getImporte()&&this.solicitudRegistrarT.getImporte()<=x.getId().getRangoFin()){
							 lstCodNivel.add(x.getId().getCodNiv());
							 logger.info("x.getId().getDesNiv() ::: " +x.getId().getDesNiv()); 
						 }
					 }
			     }
			}else {
				logger.info("*********************************** NO ENTRO EN NINGUNO ********************************************");
			}
			System.out.println("Tamanio de la lista de Niveles : " +lstCodNivel.size());
			if(lstCodNivel.size()>0){
				// SI LA SOLICITUD SOPERA ALGUN NIVEL, ENTONCES PASA A ESTADO EN VERIFICACION A, SI NO A ACEPTADO
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02);
			}else{
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
			}
		
	  }
	public void registrarHistorial(TiivsSolicitud solicitud,String numeroMovimiento) throws Exception{
		 TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
		  objHistorial.setId(new TiivsHistSolicitudId(solicitud.getCodSoli(),numeroMovimiento));
		  objHistorial.setEstado(solicitud.getEstado());
		  objHistorial.setNomUsuario(solicitud.getNomUsuario());
		  objHistorial.setObs(solicitud.getObs());
		  objHistorial.setFecha(new Timestamp(new Date().getDate()));
		  objHistorial.setRegUsuario(solicitud.getRegUsuario());
		  GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		  serviceHistorialSolicitud.insertar(objHistorial);
	}
	
	public void actualizarEstadoEjecutadoSolicitud() throws Exception
	{
		String PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");		

		if((PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) || PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) 
		   && this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02))
		{
			  logger.info("*********************** actualizarEstadoEjecutadoSolicitud **************************");
			  
			  this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02);
			  this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02);
			  
			  GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			  service.modificar(solicitudRegistrarT);
			  
			  this.registrarHistorial(solicitudRegistrarT, "5");
			  this.obtenerHistorialSolicitud();
			  this.seguimientoMB.busquedaSolicitudes();
		}
	}

							
	public void verAgrupacion() {
		logger.info("********************** verAgrupacion *********************************** ");

		logger.info("this.objAgrupacionSimpleDtoCapturado  "+ this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.objAgrupacionSimpleDtoCapturado  " + this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		logger.info("this.objAgrupacionSimpleDtoCapturado  " + this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
	}
	
	
	public void obtenerHistorialSolicitud(){
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + solicitudRegistrarT.getCodSoli());
		
		String sCodSolicitud=solicitudRegistrarT.getCodSoli();
		try {
		GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
		filtroHist.add(Restrictions.eq("id.codSoli",sCodSolicitud));
		//filtroHist.add(Restrictions.or(lhs, rhs))
		
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
	
	private void cargarDocumentos() {
		
		GenericDao<TiivsDocumento, Object> documentoDAO = (GenericDao<TiivsDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroDocumento = Busqueda.forClass(TiivsDocumento.class);
		try {
			lstTiivsDocumentos = documentoDAO.buscarDinamico(filtroDocumento);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de territorios");
		}
		
	}
				
	private String obtenerDescripcionDocumento(String codDoc) {		
		if(codDoc.contains(ConstantesVisado.PREFIJO_OTROS)){
			return ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS;
		}
		for(TiivsDocumento doc : lstTiivsDocumentos){
			if(doc.getCodDocumento().equalsIgnoreCase(codDoc.trim())){
				return doc.getDescripcion();
			}
		}
		return "";
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
	
	public List<TiivsMiembro> getLstAbogados() {
		return lstAbogados;
	}

	public void setLstAbogados(List<TiivsMiembro> lstAbogados) {
		this.lstAbogados = lstAbogados;
	}
	public boolean isbSeccionDictaminar() {
		return bSeccionDictaminar;
	}
	public void setbSeccionDictaminar(boolean bSeccionDictaminar) {
		this.bSeccionDictaminar = bSeccionDictaminar;
	}
	public boolean isbSeccionReasignacion() {
		return bSeccionReasignacion;
	}
	public void setbSeccionReasignacion(boolean bSeccionReasignacion) {
		this.bSeccionReasignacion = bSeccionReasignacion;
	}
	public List<ComboDto> getLstComboDictamen() {
		return lstComboDictamen;
	}
	public void setLstComboDictamen(List<ComboDto> lstComboDictamen) {
		this.lstComboDictamen = lstComboDictamen;
	}
	public String getValorDictamen() {
		return valorDictamen;
	}
	public void setValorDictamen(String valorDictamen) {
		this.valorDictamen = valorDictamen;
	}
	public String getDescValorDictamen() {
		return descValorDictamen;
	}
	public void setDescValorDictamen(String descValorDictamen) {
		this.descValorDictamen = descValorDictamen;
	}
	public List<ComboDto> getLstDocumentosGenerados() {
		return lstDocumentosGenerados;
	}
	public void setLstDocumentosGenerados(List<ComboDto> lstDocumentosGenerados) {
		this.lstDocumentosGenerados = lstDocumentosGenerados;
	}
	public String getTextoMensajeCartaAtencion() {
		return textoMensajeCartaAtencion;
	}
	public void setTextoMensajeCartaAtencion(String textoMensajeCartaAtencion) {
		this.textoMensajeCartaAtencion = textoMensajeCartaAtencion;
	}
	public boolean isbMostrarCartaAtencion() {
		return bMostrarCartaAtencion;
	}
	public void setbMostrarCartaAtencion(boolean bMostrarCartaAtencion) {
		this.bMostrarCartaAtencion = bMostrarCartaAtencion;
	}

	public boolean isbSeccionCartaAtencion() {
		return bSeccionCartaAtencion;
	}

	public void setbSeccionCartaAtencion(boolean bSeccionCartaAtencion) {
		this.bSeccionCartaAtencion = bSeccionCartaAtencion;
	}

	public boolean isbSeccionComentario() {
		return bSeccionComentario;
	}

	public void setbSeccionComentario(boolean bSeccionComentario) {
		this.bSeccionComentario = bSeccionComentario;
	}

	public SeguimientoMB getSeguimientoMB() {
		return seguimientoMB;
	}

	public void setSeguimientoMB(SeguimientoMB seguimientoMB) {
		this.seguimientoMB = seguimientoMB;
	}
}

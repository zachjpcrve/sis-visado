package com.hildebrando.visado.mb;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
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
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsSolicitudNivelId;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsMovimientoNivel;

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
	private boolean bSeccionAccion =false;
	private String valorDictamen="";
	private String descValorDictamen="";
	private List<ComboDto> lstDocumentosGenerados;
	private String textoMensajeCartaAtencion;
	private boolean bMostrarCartaAtencion;
	private boolean bMostrarCartaRevision;
	private boolean bRevision=false;
	IILDPeUsuario usuario;
	private boolean bMostrarGenerarRevision=true;
	private boolean bMostrarComentario=true;
	private boolean bMostrarCartaRechazo=false;
	private String PERFIL_USUARIO;
	
	private String sCodigoEstadoNivel;
	private Integer iGrupoDelegados;
	private String sNivelSolicitud; 
	private List<TiivsMiembroNivel> lstResponsables;
	private List<TiivsMiembroNivel> lstDelegados;	
	private List<TiivsSolicitudNivel> lstSolicitudNivel;
	private boolean verPnlEvaluarNivel = false;
	
	public ConsultarSolicitudMB() 
	{
		inicializarContructor();
		cargarDocumentos();
		listarComboDictamen();
		modificarTextoVentanaCartaAtencion();
		mostrarCartaAtencion();
		ocultarCartas();
		 usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA) && this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02))
		{
			setbMostrarComentario(false);
		}
		
		if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) && this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){			
			verPnlEvaluarNivel = true;
		}
		
		combosMB= new CombosMB();
		combosMB.cargarMultitabla();
	}	
	
	public void modificarTextoVentanaCartaAtencion()
	{
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
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
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
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
	
	public void ocultarCartas()
	{
		if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02))
		{
			setbMostrarCartaRevision(false);
			setbMostrarCartaAtencion(false);
		}
		else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02))
		{
			setbMostrarCartaRevision(true);
			setbMostrarCartaAtencion(false);
		}
	}
	
	public void mostrarCartaRevision()
	{
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		if((PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) || PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
			&& (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)))
		{
			setbMostrarCartaRevision(true);
			setbMostrarCartaAtencion(false);
			setbMostrarGenerarRevision(false);
		}
		else
		{
			setbMostrarCartaRevision(false);
			setbMostrarCartaAtencion(false);
		}
	}
	
	public void validarCambioEstadoVencido()
	{
		int diasUtiles=0;
		
		if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)
			|| this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02))
		{
			for (TiivsMultitabla tmp: combosMB.getLstMultitabla())
			{
				if (tmp.getId().getCodMult().trim().equals(ConstantesVisado.CODIGO_MULTITABLA_DIAS_UTILES))
				{
					diasUtiles=Integer.valueOf(tmp.getValor2());
					break;
				}
			}
			
			Date fechaSolicitud = this.solicitudRegistrarT.getFecha();
			Date fechaLimite = aumentarFechaxVen(fechaSolicitud, diasUtiles);
			
			java.util.Date fechaActual = new java.util.Date();
			
			if (fechaActual.after(fechaLimite))
			{
				logger.info("Se supero el plazo. Cambiar la solicitud a estado vencido");
				try {
					actualizarEstadoVencidoSolicitud();
				} catch (Exception e) {
					logger.info("No se pudo cambiar el estado de la solicitud: " + this.solicitudRegistrarT.getCodSoli() + " a vencida");
					logger.info(e.getStackTrace());
				}
			}		
			else
			{
				logger.info("No se supero el plazo. El estado de la solicitud se mantiene");
			}
		}	
	}
	
	public Date aumentarFechaxVen(Date fecha, int nroDias)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
				
		Date nuevaFecha = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, nroDias);
		nuevaFecha = cal.getTime();
		return nuevaFecha;
	}
	
	public void habilitarComentario()
	{
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ))
		{
			setbMostrarComentario(false);
		}	
	}
	
	public void ocultarComentario()
	{
		setbSeccionComentario(false);
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
		 	 if (f.getMoneda()!=null)
			 {
		 		 y++;
		 		 f.setsItem(String.format("%03d",y));
		 		 f.setsDescMoneda(Utilitarios.obternerDescripcionMoneda(f.getMoneda()));
			 }
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
		  TiivsSolicitudNivel solNivel = this.obtenerNivelSolicitud();
		  if(solNivel!=null){
			  this.sNivelSolicitud = solNivel.getId().getCodNiv();
		  }
		  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	public void actualizarEstadoReservadoSolicitud() throws Exception{
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
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
		 this.registrarHistorial(solicitudRegistrarT);
		 
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
	}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)){
		if(PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO) ){
			this.bSeccionAccion=false;
			this.bMostrarCartaRechazo=true;
		}else if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
			this.bSeccionComentario=true;
			this.bSeccionAccion=true;
			this.bMostrarCartaRechazo=false;
		}else if(PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)){
			this.bSeccionComentario=true;
			this.bSeccionAccion=true;
			this.bMostrarCartaRechazo=false;
		}
	}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)){
		if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) ){
			this.bSeccionDictaminar=true;
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
				solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
				this.registrarHistorial(solicitudRegistrarT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		     Utilitarios.mensajeInfo("INFO", "Se dictaminó correctamente la solicitud");
			
		}else if(this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)){
			 try {
				this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
					solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					  Utilitarios.mensajeInfo("INFO", "Se dictaminó correctamente la solicitud");
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		if(this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
			bSeccionCartaAtencion=true;
			bSeccionComentario=false;
		}
		this.obtenerHistorialSolicitud();
	}
	
	  public void agregarNiveles(TiivsSolicitud solicitud) throws Exception{
		  logger.info("*********************************** agregarNiveles ********************************************");
		  GenericDao<TiivsNivel, Object> service=(GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		  List<String> lstCodNivel=new ArrayList<String>();
			if(solicitud.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
				 List<TiivsNivel>lstNiveles=service.buscarDinamico(Busqueda.forClass(TiivsNivel.class).add(Restrictions.eq("id.moneda", ConstantesVisado.MONEDAS.COD_SOLES)));
				 if(solicitud.getImporte()>=lstNiveles.get(0).getId().getRangoInicio()){
					 System.out.println("a" +lstNiveles.get(0).getId().getDesNiv());
					 
					 if(this.solicitudRegistrarT.getImporte()>lstNiveles.get(lstNiveles.size()-1).getId().getRangoFin()){
						 System.out.println("c");
					 }else{
						 System.out.println("d");
						 for (TiivsNivel x : lstNiveles) {
							 if(solicitud.getImporte()>=x.getId().getRangoInicio()){
								 System.out.println("g " +x.getId().getDesNiv());
								 lstCodNivel.add(x.getId().getCodNiv());
								
							 }
								
					     }
					 }
				 }
				 
				
			}
			else if(solicitud.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
				logger.info("*********************************** COD_DOLAR ********************************************");
				List<TiivsNivel>lstNiveles=service.buscarDinamico(Busqueda.forClass(TiivsNivel.class).add(Restrictions.eq("id.moneda", ConstantesVisado.MONEDAS.COD_DOLAR)));
				 if(solicitud.getImporte()>=lstNiveles.get(0).getId().getRangoInicio()){
					 System.out.println("a" +lstNiveles.get(0).getId().getDesNiv());
					 
					 if(this.solicitudRegistrarT.getImporte()>lstNiveles.get(lstNiveles.size()-1).getId().getRangoFin()){
						 System.out.println("c");
					 }else{
						 System.out.println("d");
						 for (TiivsNivel x : lstNiveles) {
							 if(solicitud.getImporte()>=x.getId().getRangoInicio()){
								 System.out.println("g " +x.getId().getDesNiv());
								 lstCodNivel.add(x.getId().getCodNiv());
								
							 }
								
					     }
					 }
				 }
			}
			else if(solicitud.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
				logger.info("*********************************** COD_EUROS ********************************************");
				List<TiivsNivel>lstNiveles=service.buscarDinamico(Busqueda.forClass(TiivsNivel.class).add(Restrictions.eq("id.moneda", ConstantesVisado.MONEDAS.COD_EUROS)));
				 if(solicitud.getImporte()>=lstNiveles.get(0).getId().getRangoInicio()){
					 System.out.println("a" +lstNiveles.get(0).getId().getDesNiv());
					 
					 if(this.solicitudRegistrarT.getImporte()>lstNiveles.get(lstNiveles.size()-1).getId().getRangoFin()){
						 System.out.println("c");
					 }else{
						 System.out.println("d");
						 for (TiivsNivel x : lstNiveles) {
							 if(solicitud.getImporte()>=x.getId().getRangoInicio()){
								 System.out.println("g " +x.getId().getDesNiv());
								 lstCodNivel.add(x.getId().getCodNiv());
								
							 }
								
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
				GenericDao<TiivsSolicitudNivel, Object> serviceSolicitud=(GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				TiivsSolicitudNivel soliNivel=null;
				System.out.println("Calendar.DATE " +Calendar.DATE);
				for (String codNivel : lstCodNivel) {
					soliNivel=new TiivsSolicitudNivel();
					soliNivel.setId(new TiivsSolicitudNivelId(solicitud.getCodSoli(), codNivel));
					soliNivel.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_Pendiente_T09);
					soliNivel.setUsuarioRegistro(usuario.getUID());
					soliNivel.setFechaRegistro(new Timestamp(Calendar.DATE));
					serviceSolicitud.insertar(soliNivel);
				}
				
			}else{
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
			}
			
			
		
	  }
	public void registrarHistorial(TiivsSolicitud solicitud) throws Exception{
		  SolicitudDao<String, Object> serviceMaxMovi = (SolicitudDao<String, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		  String numeroMovimiento=serviceMaxMovi.obtenerMaximoMovimiento(solicitud.getCodSoli());
		
		  int num=0;
		  if(!numeroMovimiento.equals("")){
		  num= Integer.parseInt(numeroMovimiento)+1;
		  }else{
			  num=1;
		  }
		  numeroMovimiento=num+"";
		 logger.info("Numero de Movimiento a registrar para el CodSolicitud : " +solicitud.getCodSoli());
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
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");		

		if((PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) || PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) 
		   && this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02))
		{
			  logger.info("*********************** actualizarEstadoEjecutadoSolicitud **************************");
			  
			  this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02);
			  this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02);
			  
			  GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			  service.modificar(solicitudRegistrarT);
			  
			  this.registrarHistorial(solicitudRegistrarT);
			  this.obtenerHistorialSolicitud();
			  this.seguimientoMB.busquedaSolicitudes();
		}
	}
	
	public void actualizarEstadoEnRevisionSolicitud() throws Exception
	{
		  logger.info("*********************** actualizarEstadoEnRevisionSolicitud **************************");
				  
		  this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02);
		  this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02);
		  
		  GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		  service.modificar(solicitudRegistrarT);
		  
		  this.registrarHistorial(solicitudRegistrarT);
		  this.obtenerHistorialSolicitud();
		  this.seguimientoMB.busquedaSolicitudes();
	}
	
	public void actualizarEstadoVencidoSolicitud() throws Exception
	{
		  logger.info("*********************** actualizarEstadoVencidoSolicitud **************************");
				  
		  this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02);
		  this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02);
		  
		  GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		  service.modificar(solicitudRegistrarT);
		  
		  this.registrarHistorial(solicitudRegistrarT);
		  this.obtenerHistorialSolicitud();
		  this.seguimientoMB.busquedaSolicitudes();
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
		filtroHist.addOrder(Order.desc("id.movimiento"));
		
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
	
	public void seterComentario() 
	{
        logger.info("************************** Setear Comentario **************************");
        logger.info("Comentario : " + this.solicitudRegistrarT.getObs());
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
	
	
	
	
	/****************** Evaluacion por Niveles*************************************/	
	public void registrarAccionSolicitud(){
		
		logger.info("**************registrarAccionSolicitud()*********************");
		
		if(!sCodigoEstadoNivel.equals(ConstantesVisado.ESTADOS.ESTADO_COD_APROBADO_T09) && 
				!sCodigoEstadoNivel.equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESAPROBADO_T09)){
			Utilitarios.mensajeInfo("INFO", "Debe seleccionar una opción correcta");
			logger.info("Debe seleccionar una opción correcta:" + sCodigoEstadoNivel);
			return;
		}
						
		String sCodUsuario = (String) usuario.getUID();
//		String sCodUsuario = this.usuario; 
		TiivsSolicitudNivel solicitudNivel;
		
		String sCodigoEstadoActual = this.solicitudRegistrarT.getEstado().trim();
		String sCodigoEstadoNuevo = this.getsCodigoEstadoNivel();
		String sCodigoSolicitud= this.solicitudRegistrarT.getCodSoli();				
		boolean bRegistro = false;				
		String sCodNivel ="";

		
		
		if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
			//llena variable: lstSolicitudNivel
			solicitudNivel = obtenerNivelSolicitud();
			sCodNivel = solicitudNivel.getId().getCodNiv().trim();	
			
			//llena variables: lstResponsables,lstDelegados
			obtenerDatosDelegadosResponsables(solicitudNivel);
//			obtenerDatosDelegadosResponsables(sCodNivel);
			Integer iGrupo = obtenerGrupoEvaluador(solicitudNivel);
			
			logger.info("Solicitud: " + sCodigoSolicitud);
			logger.info("Nivel: " + sCodNivel);
			logger.info("Grupo Evaluador: " + iGrupo);
			logger.info("Responsables: " + lstResponsables.size());
			logger.info("Delegados: " + lstDelegados.size());
			logger.info("sCodUsuario: " + sCodUsuario);

			
			if(!sCodigoEstadoActual.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){
				Utilitarios.mensajeInfo("INFO", "No se permite el cambio de estado de la solicitud");
				logger.info("No se permite el cambio de estado de: " + sCodigoEstadoActual);		
				return;
			}			
			if(sCodNivel==null || sCodNivel.equals("")){
				Utilitarios.mensajeInfo("INFO", "No se obtuvo el nivel de la solicitud");
				logger.info("No se obtuvo el nivel de la solicitud, no se realizará ninguna cambio");
				return;
			}		
			
			if(usuarioEsMiembroDe(sCodUsuario,lstResponsables,null)){
				modificarEstadoSolicitudNivel(solicitudNivel,sCodigoEstadoNuevo, sCodUsuario);
				bRegistro = true;
			} else if(usuarioEsMiembroDe(sCodUsuario,lstDelegados,iGrupo)){			
				registrarEstadoMovimientoNivel(solicitudNivel, sCodigoEstadoNuevo, sCodUsuario);
				if(verificarCalificacionPorDelegados(solicitudNivel, sCodigoEstadoNuevo)){	
					logger.info("Grupo Delegados: " + iGrupoDelegados);
					modificarEstadoSolicitudNivel(solicitudNivel, sCodigoEstadoNuevo,iGrupoDelegados.toString());
				}
				bRegistro = true;
			} 		
			
			if(bRegistro){
				Utilitarios.mensajeInfo("INFO", "Acción registrada correctamente");
			} else {
				Utilitarios.mensajeInfo("INFO", "Acción no registrada");
			}
		}
		
		logger.info("***********registrarAccionSolicitud():FIN ****************");		
	}
	
	private Integer obtenerGrupoEvaluador(TiivsSolicitudNivel tiivsSolicitudNivel) {
		List<TiivsMovimientoNivel> lstMovimientoNivel = new ArrayList<TiivsMovimientoNivel>();
		Integer iGrupo = null;
		try {
			GenericDao<TiivsMovimientoNivel, Object> moviNivelDAO = (GenericDao<TiivsMovimientoNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroNivel = Busqueda.forClass(TiivsMovimientoNivel.class);
			filtroNivel.add(Restrictions.eq("tiivsSolicitudNivel", tiivsSolicitudNivel));
			lstMovimientoNivel = moviNivelDAO.buscarDinamico(filtroNivel);			
			int i=0;			
			for(TiivsMovimientoNivel m : lstMovimientoNivel){
				iGrupo=m.getGrupo();
				i++;
			}			
			if(i != lstMovimientoNivel.size()){
				logger.debug("Existe mas de un grupo evaluador para el nivel:" + tiivsSolicitudNivel.getId().getCodNiv());
				iGrupo = null;
			}						
		}catch(Exception e){
			iGrupo = null;
		}
		return iGrupo;
	}

	private void modificarEstadoSolicitudNivel(TiivsSolicitudNivel tiivsSolicitudNivel,String sCodigoEstado,String sRegistro){
						
		logger.info("*********************** modificarEstadoSolicitudNivel **************************");

		tiivsSolicitudNivel.setEstado(sCodigoEstado);
		tiivsSolicitudNivel.setUsuarioRegistro(sRegistro);
		tiivsSolicitudNivel.setFechaRegistro(new Timestamp((new Date().getTime())));
		try {
			GenericDao<TiivsSolicitudNivel, Object> service = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			service.modificar(tiivsSolicitudNivel);
		} catch (Exception e) {
			logger.debug("Error al cambiar el estado de la tabla SolicitudNivel");
		}		
		
		if(sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESAPROBADO_T09)){
			modificarEstadoSolicitud(this.solicitudRegistrarT,sCodigoEstado);
		} else if(sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_APROBADO_T09)){						
			if(verificarCalificacionPorNiveles(sCodigoEstado)){
				modificarEstadoSolicitud(this.solicitudRegistrarT,sCodigoEstado);
			}		
		}		
		/*if(verificarCalificacionPorNiveles(sCodigoEstado)){
			modificarEstadoSolicitud(this.solicitudRegistrarT,sCodigoEstado);
		}*/
	}
	
	private void registrarEstadoMovimientoNivel(TiivsSolicitudNivel tiivsSolicitudNivel, String sCodigoEstado,String sCodUsuario){
						
		List<TiivsMovimientoNivel> lstMovimientoNivel = new ArrayList<TiivsMovimientoNivel>();
		TiivsMovimientoNivel movimientoNivel;
		
		try {
			GenericDao<TiivsMovimientoNivel, Object> moviNivelDAO = (GenericDao<TiivsMovimientoNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroNivel = Busqueda.forClass(TiivsMovimientoNivel.class);
			filtroNivel.add(Restrictions.eq("tiivsSolicitudNivel", tiivsSolicitudNivel));
			filtroNivel.add(Restrictions.eq("usuarioRegistro", sCodUsuario));
			lstMovimientoNivel = moviNivelDAO.buscarDinamico(filtroNivel);
			
			if(lstMovimientoNivel.size()>0){	//...Si ya existe movimiento modificar
				/*
				movimientoNivel = lstMovimientoNivel.get(0);
				movimientoNivel.setEstado(sCodigoEstado);
				moviNivelDAO.modificar(movimientoNivel);
				*/				
				for(TiivsMovimientoNivel m : lstMovimientoNivel){
					m.setEstado(sCodigoEstado);
					moviNivelDAO.modificar(m);
				}
				
			} else {							//.. Si no insertar nuevo movimiento
				for(TiivsMiembroNivel m : this.lstDelegados){ //Insertar tantos movimientos como facultades tenga el usuario
					if(m.getTiivsMiembro().getCodMiembro().equals(sCodUsuario)){						
						movimientoNivel = new TiivsMovimientoNivel();
						movimientoNivel.setTiivsSolicitudNivel(tiivsSolicitudNivel);
						movimientoNivel.setEstado(sCodigoEstado);
						movimientoNivel.setGrupo(m.getGrupo());
						movimientoNivel.setUsuarioRegistro(m.getTiivsMiembro().getCodMiembro());
						movimientoNivel.setFechaRegistro(new Timestamp((new Date()).getTime()));
						moviNivelDAO.insertar(movimientoNivel);
					}
				}
				
			}						
			
		} catch (Exception e) {
			lstMovimientoNivel = null;
			logger.error("Error al registrar el movimiento");
		}    
		
//		this.registrarHistorial(this.solicitudRegistrarT);
				
	}
	
	//private boolean usuarioEsMiembroDe(String sCodUsuario, List<TiivsMiembroNivel> lstMiembros ){
	private boolean usuarioEsMiembroDe(String sCodUsuario, List<TiivsMiembroNivel> lstMiembros, Integer iGrupo){
		if(sCodUsuario == null){
			return false;
		}
		for(TiivsMiembroNivel res : lstMiembros){
			if(res.getTiivsMiembro().getCodMiembro().equals(sCodUsuario.trim())){
				if(iGrupo==null){
					return true;
				} else {					
					if(res.getGrupo().equals(iGrupo)){
						return true;
					} else {
						return false;
					}
				}				
			}
		}
		return false;
	}
	
	private TiivsSolicitudNivel obtenerNivelSolicitud(){
		
		this.lstSolicitudNivel = new ArrayList<TiivsSolicitudNivel>();
		TiivsSolicitudNivel solNivel = null;
		String sCodSolicitud = this.solicitudRegistrarT.getCodSoli();
		try {
			GenericDao<TiivsSolicitudNivel, Object> soliNivelDAO = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroNivel = Busqueda.forClass(TiivsSolicitudNivel.class);
			filtroNivel.add(Restrictions.eq("id.codSoli", sCodSolicitud));
			filtroNivel.addOrder(Order.asc("id.codNiv"));
			this.lstSolicitudNivel = soliNivelDAO.buscarDinamico(filtroNivel);
		} catch (Exception e) {
			this.lstSolicitudNivel = null;
			logger.error("Error al obtener datos de nivel");
		}        
		
		for(TiivsSolicitudNivel n : this.lstSolicitudNivel){
			if(n.getEstado()==null || n.getEstado().trim().equals("")){
				solNivel = n;				
				break;
			}
				
		}        		
		return solNivel;
	}
	
	private void obtenerDatosDelegadosResponsables(TiivsSolicitudNivel solicitudNivel){
		
		List<TiivsMiembroNivel> lstMiembroNivel;
		lstMiembroNivel = new ArrayList<TiivsMiembroNivel>();
		this.lstResponsables = new ArrayList<TiivsMiembroNivel>();
		this.lstDelegados = new ArrayList<TiivsMiembroNivel>();
		String sCodNivel = solicitudNivel.getId().getCodNiv();
//		String sCodNivel = solicitudNivel;
		
		try {
			GenericDao<TiivsMiembroNivel, Object> miembroNivelDAO = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);
			filtroMiembroNivel.add(Restrictions.eq("codNiv", sCodNivel));
			lstMiembroNivel = miembroNivelDAO.buscarDinamico(filtroMiembroNivel);
		} catch (Exception e) {
			lstMiembroNivel = null;
			logger.error("Error al obtener datos de los responsables:" + e.getMessage());
		}        
		
		for(TiivsMiembroNivel m : lstMiembroNivel){
			if(m.getTipoRol().equalsIgnoreCase(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE)){
				this.lstResponsables.add(m);
			}
			if(m.getTipoRol().equalsIgnoreCase(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO)){
				this.lstDelegados.add(m);
			}
				
		}        
	}
		
	
	
	private boolean verificarCalificacionPorDelegados(TiivsSolicitudNivel tiivsSolicitudNivel, String sCodigoEstado){				

		List<TiivsMovimientoNivel> lstMovimientoNivel = new ArrayList<TiivsMovimientoNivel>();	
		lstMovimientoNivel = obtenerMovimientoNivel(tiivsSolicitudNivel);						
		Integer grupo = new Integer(0);
		List<Integer> lstGrupos = new ArrayList<Integer>();
		for(TiivsMiembroNivel delegado : lstDelegados){
			if(grupo!=delegado.getGrupo()){	
				grupo = delegado.getGrupo();
				lstGrupos.add(grupo);		
			}
		}		
		for(Integer iGrupo : lstGrupos){
			int cont=0;
			for(TiivsMovimientoNivel movNivel : lstMovimientoNivel){				
				if(movNivel.getGrupo().equals(iGrupo) && movNivel.getEstado().equals(sCodigoEstado)){
					cont++;
				}
			}
			if(cont==obtenerNroDelegados(lstDelegados,iGrupo)){
				this.iGrupoDelegados = iGrupo;
				return true;				
			}
		}
		return false;
		
	}
	
	private int obtenerNroDelegados(List<TiivsMiembroNivel> lstDelegados, Integer iGrupo) {
		int cont=0;
		for(TiivsMiembroNivel m : lstDelegados){
			if(m.getGrupo().equals(iGrupo)){
				cont++;
			}
		}
		return cont;
	}
	
	
	private List<TiivsMovimientoNivel> obtenerMovimientoNivel(TiivsSolicitudNivel tiivsSolicitudNivel) {
		List<TiivsMovimientoNivel> lstMovimientoNivel = new ArrayList<TiivsMovimientoNivel>();
		try {
			GenericDao<TiivsMovimientoNivel, Object> movimientoNivelDAO = (GenericDao<TiivsMovimientoNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsMovimientoNivel.class);
			filtro.add(Restrictions.eq("tiivsSolicitudNivel", tiivsSolicitudNivel));
			lstMovimientoNivel = movimientoNivelDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			lstMovimientoNivel = null;
			logger.error("Error al obtener datos de movimientos por nivel:"+ e.getMessage());
		}
		return lstMovimientoNivel;
	}

	private boolean verificarCalificacionPorNiveles(String sEstado){
		boolean iRet = false;
		int i=0;
		for(TiivsSolicitudNivel sn : lstSolicitudNivel){
			if(sn.getEstado()!=null && sn.getEstado().equals(sEstado)){
				i++;
			}
		}
		if(i==lstSolicitudNivel.size()){
			iRet = true;
		} 
		return iRet;
	}
	
	private void modificarEstadoSolicitud(TiivsSolicitud tiivsSolicitud, String sCodigoEstado) {
				
		if(sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_APROBADO_T09)){
			sCodigoEstado = ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02;
		} else if (sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESAPROBADO_T09)){
			sCodigoEstado = ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02;
		}
		
				
		String sCodUsuario = usuario.getUID();
		
		tiivsSolicitud.setEstado(sCodigoEstado);
		tiivsSolicitud.setFechaEstado(new Timestamp((new Date().getTime())));
		tiivsSolicitud.setRegUsuario(sCodUsuario);//validar con diego
		
		
		try {
			GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			service.modificar(tiivsSolicitud);
			registrarHistorial(tiivsSolicitud);
		} catch (Exception e) {
			logger.debug("No se pudo modificar la solicitud" + e.getMessage());
		}		 				
	}	
		
	/*************************************************************************************/
	
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
		
	public String getsCodigoEstadoNivel() {
		return sCodigoEstadoNivel;
	}

	public void setsCodigoEstadoNivel(String sCodigoEstadoNivel) {
		this.sCodigoEstadoNivel = sCodigoEstadoNivel;
	}

	public String getsNivelSolicitud() {
		return sNivelSolicitud;
	}

	public void setsNivelSolicitud(String sNivelSolicitud) {
		this.sNivelSolicitud = sNivelSolicitud;
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

	public boolean isbMostrarCartaRevision() {
		return bMostrarCartaRevision;
	}

	public void setbMostrarCartaRevision(boolean bMostrarCartaRevision) {
		this.bMostrarCartaRevision = bMostrarCartaRevision;
	}

	public boolean isbRevision() {
		return bRevision;
	}

	public void setbRevision(boolean bRevision) {
		this.bRevision = bRevision;
	}

	public boolean isbSeccionAccion() {
		return bSeccionAccion;
	}

	public void setbSeccionAccion(boolean bSeccionAccion) {
		this.bSeccionAccion = bSeccionAccion;
	}

	public boolean isbMostrarGenerarRevision() {
		return bMostrarGenerarRevision;
	}

	public void setbMostrarGenerarRevision(boolean bMostrarGenerarRevision) {
		this.bMostrarGenerarRevision = bMostrarGenerarRevision;
	}

	public boolean isbMostrarComentario() {
		return bMostrarComentario;
	}

	public void setbMostrarComentario(boolean bMostrarComentario) {
		this.bMostrarComentario = bMostrarComentario;
	}

	public boolean isbMostrarCartaRechazo() {
		return bMostrarCartaRechazo;
	}

	public void setbMostrarCartaRechazo(boolean bMostrarCartaRechazo) {
		this.bMostrarCartaRechazo = bMostrarCartaRechazo;
	}
	
	public String getPERFIL_USUARIO() {
		return PERFIL_USUARIO;
	}

	public void setPERFIL_USUARIO(String pERFIL_USUARIO) {
		PERFIL_USUARIO = pERFIL_USUARIO;
	}
	
	public boolean isVerPnlEvaluarNivel() {
		return verPnlEvaluarNivel;
	}

	public void setVerPnlEvaluarNivel(boolean verPnlEvaluarNivel) {
		this.verPnlEvaluarNivel = verPnlEvaluarNivel;
	}	
	
}

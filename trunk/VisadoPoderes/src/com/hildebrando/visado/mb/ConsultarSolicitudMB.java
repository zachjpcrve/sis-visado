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
import com.hildebrando.visado.converter.PersonaDataModal;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.TipoDocumento;
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
	/*@ManagedProperty(value = "#{solEdicionMB}")
	private SolicitudEdicionMB solicitudEdicionMB;*/
	
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
    private List<TiivsPersona> lstTiivsPersona;
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
	private boolean bMostrarCartaImprocedente=false;
	private boolean bMostrarCartaRespuesta=false;
	boolean bBooleanPopupTipoCambio = true;
	private String PERFIL_USUARIO;
	private String sCodigoEstadoNivel;	
	private String sNivelSolicitud; 	
	private boolean verPnlEvaluarNivel = false;	
	private EvaluacionNivelesMB evaluacionNivelesMB;
	private TiivsSolicitudOperban objSolicBancaria;
	private PersonaDataModal personaDataModal;
	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaResultado;
	private TiivsPersona objTiivsPersonaSeleccionado;
	private TiivsPersona objTiivsPersonaCapturado;
	
	public ConsultarSolicitudMB() 
	{
		inicializarContructor();
		cargarDocumentos();
		
		modificarTextoVentanaCartaAtencion();
		mostrarCartaAtencion();
		ocultarCartas();
		 usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA) && this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02))
		{
			setbMostrarComentario(false);
		}
		
		if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
			
			if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02) || 
					this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02)){
				
				
				evaluacionNivelesMB = new EvaluacionNivelesMB(this.solicitudRegistrarT);	
				TiivsSolicitudNivel solNivel = evaluacionNivelesMB.obtenerNivelSolicitud();
				if (solNivel != null) {
					this.sNivelSolicitud = solNivel.getCodNiv();
				}											
				
				verPnlEvaluarNivel = true;
			}
			
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
		else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02))
		{
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
    
	public void listarComboDictamen()
    {
    	lstComboDictamen=new ArrayList<ComboDto>();
    	logger.info("this.solicitudRegistrarT.getEstado() " +this.solicitudRegistrarT.getEstado());
    	//SOLO SERVICIOS JURIDICOS
    	if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)||PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO))
    	{
    		if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02))
    		{
    			lstComboDictamen.add(new ComboDto(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02,ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02));
    			lstComboDictamen.add(new ComboDto(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02,ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02));
    		}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02))
    		{
    			lstComboDictamen.add(new ComboDto(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02,ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02));
    			lstComboDictamen.add(new ComboDto(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02,ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02));
    		}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)) 
    		{
    			lstComboDictamen.add(new ComboDto(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02,ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02));
    		}
    	}
    }
    
	public String redirectDetalleSolicitud() 
	{
		logger.info(" **** redirectDetalleSolicitud ***");
		
		String redirect="";
		
		obtenerSolicitud();
		///if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02))
		//{   
			/*String codigoSolicitud=Utilitarios.capturarParametro("prm_codSoli");
			logger.info("codigoSolicitud : "+codigoSolicitud);
			SolicitudEdicionMB solicitud= new SolicitudEdicionMB();
			solicitud.actualizarVista(solicitudRegistrarT);*/
			redirect ="/faces/paginas/solicitudEdicion.xhtml";
		//}
	//	else
		//{
			redirect ="/faces/paginas/detalleSolicitud.xhtml";
		//}
		
		return redirect;	
	}
	
	public void abogadosXEstudios(ValueChangeEvent e)
	{
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
		     Utilitarios.mensajeInfo("INFO", "Se realiz� la reasignaci�n manual Correctamente");
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
		   //descargarAnexosFileServer();
		   
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
				   logger.info("d.getTiivsPersona() "+d.getTiivsPersona().getTipDoi());
				   objPersona=new TiivsPersona();
				   objPersona=d.getTiivsPersona();
				   objPersona.setTipPartic(d.getId().getTipPartic());
				   objPersona.setsDesctipPartic(this.obtenerDescripcionTipoRegistro(d.getId().getTipPartic().trim()));
				   objPersona.setClasifPer(d.getId().getClasifPer());
				   objPersona.setsDescclasifPer(this.obtenerDescripcionClasificacion(d.getId().getClasifPer().trim()));
				   objPersona.setsDesctipDoi(this.obtenerDescripcionDocumentos(d.getTiivsPersona().getTipDoi().trim()));
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
			   agrupacionSimpleDto.setsEstado(Utilitarios.obternerDescripcionEstado(x.getActivo().trim()) );
			   agrupacionSimpleDto.setLstPersonas(lstPersonas);
			   lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
			   
		   }
		  
		  
		  this.actualizarEstadoReservadoSolicitud();
		  this.obtenerHistorialSolicitud();			  		 
		  
		  //Listar ComboDictamen
		  listarComboDictamen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String obtenerDescripcionDocumentos(String idTipoDocumentos){
		String descripcion="";
		for (TipoDocumento z : combosMB.getLstTipoDocumentos()) {
			if(z.getCodTipoDoc().trim().equals(idTipoDocumentos)){
				 descripcion=z.getDescripcion();
				 break;
			}
		}
		return descripcion;
	}
	public String obtenerDescripcionClasificacion(String idTipoClasificacion){
		String descripcion="";
		for (ComboDto z : combosMB.getLstClasificacionPersona()) {
			if(z.getKey().trim().equals(idTipoClasificacion)){
				 descripcion=z.getDescripcion();
				 break;
			}
		}
		return descripcion;
	}
	public String obtenerDescripcionTipoRegistro(String idTipoTipoRegistro){
		String descripcion="";
		for (ComboDto z : combosMB.getLstTipoRegistroPersona()) {
			if(z.getKey().trim().equals(idTipoTipoRegistro)){
				 descripcion=z.getDescripcion();
				 break;
			}
		}
		return descripcion;
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
			this.bSeccionComentario=true;
		}
	}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)){
		if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) ){
			this.bMostrarCartaRechazo=true;
			this.bMostrarCartaRevision=true;
			this.bMostrarCartaAtencion=true;
		}
	}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)){
			if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) ){
				this.bMostrarCartaRechazo=true;
				this.bMostrarCartaImprocedente=true;
				this.bMostrarCartaRespuesta=true;
			}
		}else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02)){
		this.bSeccionAccion=false;
	}	
	}
	
	
	public void obtenerDictamen(ValueChangeEvent e){
		logger.info("****************** obtenerDictamen ********************** "+e.getNewValue());
		
		if(e.getNewValue()!=null){
			valorDictamen=e.getNewValue().toString();
			if(e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02))
			descValorDictamen=ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02;
			else if(e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02))
			descValorDictamen=ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02;
			else if(e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02))
			descValorDictamen=ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02;
			else if(e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02))
			descValorDictamen=ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02;
		}
		
	}
	public void dictaminarSolicitud(){
		logger.info("********************** dictaminarSolicitud *********************************** ");
		logger.info("********** "+valorDictamen);
		try {
			
		
		
		 GenericDao<TiivsSolicitud, Object> serviceS = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		 if(this.PERFIL_USUARIO.trim().equals(ConstantesVisado.ABOGADO)||this.PERFIL_USUARIO.trim().equals(ConstantesVisado.SSJJ)){
		if(this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
			
			//Llamada a los Niveles
		
				this.agregarNiveles(solicitudRegistrarT);
			    this.bSeccionDictaminar=false;
				this.solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
				this.registrarHistorial(solicitudRegistrarT);
			
		     Utilitarios.mensajeInfo("INFO", "Se dictamin� correctamente la solicitud");
			
		}else if(this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)){
			
				this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
					solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					  Utilitarios.mensajeInfo("INFO", "Se dictamin� correctamente la solicitud");
			
		}
		else if(this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)){
			
				 this.agregarNiveles(solicitudRegistrarT);
				 this.bSeccionDictaminar=false;
				 this.solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					  Utilitarios.mensajeInfo("INFO", "Se dictamin� correctamente la solicitud");
				
		}
		else if(this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)){
			
				this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02);
					solicitudRegistrarT= serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					  Utilitarios.mensajeInfo("INFO", "Se dictamin� correctamente la solicitud");
				
		}
		if(this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
			bSeccionCartaAtencion=true;
			bSeccionComentario=false;
		}else if(this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)){
			if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) ){
				this.bMostrarCartaRechazo=true;
				this.bMostrarCartaRevision=true;
				this.bMostrarCartaAtencion=true;
				this.bSeccionComentario=false;
			}
		}
		
		
		this.obtenerHistorialSolicitud();
		 
		 }
		 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  public void agregarNiveles(TiivsSolicitud solicitud) throws Exception{
		  logger.info("*********************************** agregarNiveles ********************************************");
		  GenericDao<TiivsNivel, Object> service=(GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		  List<String> lstCodNivel=new ArrayList<String>();
			if(solicitud.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
				logger.info("****************** COD_SOLES ******************** " +solicitud.getMoneda());
				
				 List<TiivsNivel>lstNiveles=service.buscarDinamico(Busqueda.forClass(TiivsNivel.class).add(Restrictions.eq("id.moneda", ConstantesVisado.MONEDAS.COD_SOLES)));
				 if(solicitud.getImporte()>=lstNiveles.get(0).getId().getRangoInicio()){
					 logger.info("a" +lstNiveles.get(0).getId().getDesNiv());
					 
					 if(this.solicitudRegistrarT.getImporte()>lstNiveles.get(lstNiveles.size()-1).getId().getRangoFin()){
						 //System.out.println("c");
					 }else{
						 //System.out.println("d");
						 for (TiivsNivel x : lstNiveles) {
							 if(solicitud.getImporte()>=x.getId().getRangoInicio()){
								 logger.info("g " +x.getId().getDesNiv());
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
					 logger.info("a" +lstNiveles.get(0).getId().getDesNiv());
					 
					 if(this.solicitudRegistrarT.getImporte()>lstNiveles.get(lstNiveles.size()-1).getId().getRangoFin()){
						 //System.out.println("c");
					 }else{
						 //System.out.println("d");
						 for (TiivsNivel x : lstNiveles) {
							 if(solicitud.getImporte()>=x.getId().getRangoInicio()){
								 logger.info("g " +x.getId().getDesNiv());
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
					 logger.info("a" +lstNiveles.get(0).getId().getDesNiv());
					 
					 if(this.solicitudRegistrarT.getImporte()>lstNiveles.get(lstNiveles.size()-1).getId().getRangoFin()){
						 //System.out.println("c");
					 }else{
						 //System.out.println("d");
						 for (TiivsNivel x : lstNiveles) {
							 if(solicitud.getImporte()>=x.getId().getRangoInicio()){
								 logger.info("g " +x.getId().getDesNiv());
								 lstCodNivel.add(x.getId().getCodNiv());
								
							 }
								
					     }
					 }
				 }
			}else {
				logger.info("*********************************** NO ENTRO EN NINGUNO ********************************************");
			}
			logger.info("Tamanio de la lista de Niveles : " +lstCodNivel.size());
			if(lstCodNivel.size()>0){
				// SI LA SOLICITUD SOPERA ALGUN NIVEL, ENTONCES PASA A ESTADO EN VERIFICACION A, SI NO A ACEPTADO
				if(this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
				 solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02);
				}else if(this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)){
				 solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02);
		     	}   
				GenericDao<TiivsSolicitudNivel, Object> serviceSolicitud=(GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				TiivsSolicitudNivel soliNivel=null;
				logger.info("Calendar.DATE " +Calendar.DATE);
				for (String codNivel : lstCodNivel) {
					soliNivel=new TiivsSolicitudNivel();
					soliNivel.setCodNiv(codNivel);
					soliNivel.setEstadoSolicitud(solicitud.getEstado());
					soliNivel.setTiivsSolicitud(solicitud);
					soliNivel.setEstadoNivel(ConstantesVisado.ESTADOS.ESTADO_COD_Pendiente_T09);
					soliNivel.setUsuarioRegistro(usuario.getUID());
					soliNivel.setFechaRegistro(new Timestamp(Calendar.DATE));
					serviceSolicitud.insertar(soliNivel);
				}
				
			}else if(lstCodNivel.size()==0){
				if(this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
				}else if(this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)){
				 solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02);
		     	} 
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
		   && (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02) || 
				this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)))
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
		filtroHist.addOrder(Order.desc("fecha"));
		
		List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
        lstHist = histDAO.buscarDinamico(filtroHist);
			
		logger.info("Numero de registros encontrados:"+lstHist.size());
		
		if(lstHist!=null && lstHist.size()>0){
			lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
			
			for(TiivsHistSolicitud h : lstHist){
				SeguimientoDTO seg = new SeguimientoDTO();
				
				String estado = h.getEstado();
				if(estado!=null){
					seg.setEstado(buscarEstadoxCodigo(estado.trim()));
				}
					
				String desEstadoNivel="";
				String desRolNivel="";
				Integer iCodNivel=0;
				String descripcionNivel="";
				
				if(h.getNivel()!=null){
					if(h.getNivelRol()!=null && h.getNivelRol().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE)){
						desRolNivel = "Responsable";
					}
					if(h.getNivelRol()!=null && h.getNivelRol().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO)){
						desRolNivel = "Delegado";
					}
					if(h.getNivelEstado()!=null && h.getNivelEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_Desaprobado_T09)){
						desEstadoNivel = ConstantesVisado.ESTADOS.ESTADO_Desaprobado_T09;
					}
					if(h.getNivelEstado()!=null && h.getNivelEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_Aprobado_T09)){
						desEstadoNivel = ConstantesVisado.ESTADOS.ESTADO_Aprobado_T09;
					}
					iCodNivel = Integer.parseInt(h.getNivel());
					descripcionNivel = "Nivel " + iCodNivel + " " + desRolNivel + ": " + desEstadoNivel;
				}								
				seg.setNivel(descripcionNivel);
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
						logger.debug("No se actualizar� el anexo " + ex.getMessage());
					}
					iRet = iRet && true;
				} else {
					logger.debug("Error no se actualizar� anexo");
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
		
	public void registrarEvaluacionNivel(){
//		evaluacionNivelesMB.setRegistroUsuario(this.registroUsuario);
		evaluacionNivelesMB.registrarEvaluacionNivel(sCodigoEstadoNivel);
		
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
	
	public boolean isbMostrarCartaImprocedente() {
		return bMostrarCartaImprocedente;
	}

	public void setbMostrarCartaImprocedente(boolean bMostrarCartaImprocedente) {
		this.bMostrarCartaImprocedente = bMostrarCartaImprocedente;
	}

	public boolean isbMostrarCartaRespuesta() {
		return bMostrarCartaRespuesta;
	}

	public void setbMostrarCartaRespuesta(boolean bMostrarCartaRespuesta) {
		this.bMostrarCartaRespuesta = bMostrarCartaRespuesta;
	}

	public TiivsSolicitudOperban getObjSolicBancaria() {
		return objSolicBancaria;
	}

	public void setObjSolicBancaria(TiivsSolicitudOperban objSolicBancaria) {
		this.objSolicBancaria = objSolicBancaria;
	}

	public boolean isbBooleanPopupTipoCambio() {
		return bBooleanPopupTipoCambio;
	}

	public void setbBooleanPopupTipoCambio(boolean bBooleanPopupTipoCambio) {
		this.bBooleanPopupTipoCambio = bBooleanPopupTipoCambio;
	}

	public PersonaDataModal getPersonaDataModal() {
		return personaDataModal;
	}

	public void setPersonaDataModal(PersonaDataModal personaDataModal) {
		this.personaDataModal = personaDataModal;
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

	public TiivsPersona getObjTiivsPersonaSeleccionado() {
		return objTiivsPersonaSeleccionado;
	}

	public void setObjTiivsPersonaSeleccionado(
			TiivsPersona objTiivsPersonaSeleccionado) {
		this.objTiivsPersonaSeleccionado = objTiivsPersonaSeleccionado;
	}

	public TiivsPersona getObjTiivsPersonaCapturado() {
		return objTiivsPersonaCapturado;
	}

	public void setObjTiivsPersonaCapturado(TiivsPersona objTiivsPersonaCapturado) {
		this.objTiivsPersonaCapturado = objTiivsPersonaCapturado;
	}

	public List<TiivsDocumento> getLstTiivsDocumentos() {
		return lstTiivsDocumentos;
	}

	public void setLstTiivsDocumentos(List<TiivsDocumento> lstTiivsDocumentos) {
		this.lstTiivsDocumentos = lstTiivsDocumentos;
	}

	public List<TiivsPersona> getLstTiivsPersona() {
		return lstTiivsPersona;
	}

	public void setLstTiivsPersona(List<TiivsPersona> lstTiivsPersona) {
		this.lstTiivsPersona = lstTiivsPersona;
	}
	
	/*public SolicitudEdicionMB getSolicitudEdicionMB() {
		return solicitudEdicionMB;
	}
		*/
}

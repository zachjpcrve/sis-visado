package com.hildebrando.visado.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.common.util.EstilosNavegador;
import com.bbva.consulta.host.impl.ObtenerDatosVoucherDUMMY;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecDUMMY;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.bbva.consulta.reniec.util.Persona;
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
import com.hildebrando.visado.modelo.TiivsAnexoSolicitudId;
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
import com.hildebrando.visado.modelo.TiivsHostVoucher;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsRevocado;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsSolicitudOperbanId;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;
import com.hildebrando.visado.service.NivelService;
import com.hildebrando.visado.service.TiposDoiService;

@ManagedBean(name = "consultarSolicitudMB")
@SessionScoped
public class ConsultarSolicitudMB {
	public static Logger logger = Logger.getLogger(ConsultarSolicitudMB.class);
	//@ManagedProperty(value = "#{combosMB}")
	private List<ComboDto> lstClasificacionPersona;
	private CombosMB combosMB;
	@ManagedProperty(value = "#{seguimientoMB}")
	private SeguimientoMB seguimientoMB;
	@ManagedProperty(value = "#{registroUtilesMB}")
	RegistroUtilesMB objRegistroUtilesMB;
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	@ManagedProperty(value = "#{visadoDocumentosMB}")
	private VisadoDocumentosMB visadoDocumentosMB;
	private String patter;
	EstilosNavegador estilosNavegador;
	private TiivsSolicitud solicitudRegistrarT;
	private TiivsSolicitud solicitudRegistrarTCopia;
	private List<TiivsSolicitudOperban> lstSolicBancarias;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	private AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado;
	private List<DocumentoTipoSolicitudDTO> lstdocumentos;
	private List<SeguimientoDTO> lstSeguimientoDTO;
	private List<TiivsDocumento> lstTiivsDocumentos;
	private List<TiivsAnexoSolicitud> lstAnexoSolicitud;
	private List<TiivsMiembro> lstAbogados;
	private List<ComboDto> lstComboDictamen;
	private List<TiivsPersona> lstTiivsPersona;
	private List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos;
	private List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp;
	private TiivsSolicitudOperban objSolicitudOperacionCapturado;
	private TiivsSolicitudOperban objSolicitudOperacionCapturadoOld = new TiivsSolicitudOperban();
	private List<String> aliasFilesToDelete;	
	private boolean bSeccionDictaminar = false;
	private boolean bSeccionReasignacion = false;
	private boolean bSeccionCartaAtencion = false;
	private boolean bSeccionComentario = false;
	//private boolean bcartaRevision = false;
	private boolean bSeccionDocumentos = false;
	private boolean bSeccionEvaluarNivel = false;
	private int indexUpdatePoderdanteApoderado=0;
	private boolean flagUpdatePoderdanteApoderados=false;
	private String valorDictamen = "";
	private String descValorDictamen = "";
	private List<ComboDto> lstDocumentosGenerados;
	private String textoMensajeCartaAtencion;
	private boolean bMostrarCartaAtencion = false;
	private boolean bMostrarReImprimirCartaAtencion = false;
	private boolean bMostrarCartaRevision = false;
	private boolean bRevision = false;
	private boolean flagUpdateOperacionSolic = false;
	private boolean flagMostrarACOficina=false;
	private boolean flagMostrarSOMOficina=true;
	IILDPeUsuario usuario;
	private boolean bMostrarGenerarRevision = true;
	private boolean bMostrarComentario = true;
	private boolean bMostrarCartaRechazo = false;
	private boolean bMostrarCartaImprocedente = false;
	private boolean bMostrarCartaRespuesta = false;
	private boolean bMostrarSolicitudVisado = false;
	private boolean bMostrarMSGEstado_Reservado = false;
	boolean bBooleanPopupTipoCambio = true;
	boolean bBooleanPopup = false;
	private String PERFIL_USUARIO;
	private String sCodigoEstadoNivel;
	private String sNivelSolicitud;
	private String sMonedaImporteGlobal;
	private String sCodDocumento;
	private String sEstadoSolicitud = "";
	private String sTextoEstadoReservado="";
	String cadenaEscanerFinal = "";
//	private boolean verPnlEvaluarNivel = false;
	private EvaluacionNivelesMB evaluacionNivelesMB;
	private TiivsSolicitudOperban objSolicBancaria;
	private PersonaDataModal personaDataModal;
	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaResultado;
	private TiivsPersona objTiivsPersonaSeleccionado;
	private TiivsPersona objTiivsPersonaCapturado;
	private TiivsOficina1 oficina;
	private DocumentoTipoSolicitudDTO selectedDocumentoDTO = new DocumentoTipoSolicitudDTO();
	private String iTipoSolicitud = "";
	private TiivsTipoSolicDocumento selectedTipoDocumento;
	Map<Integer, TiivsSolicitudOperban> mapSolicitudes;
	private int indexUpdateOperacion = 0;
	private List<TiivsPersona> lstTiivsPersonaResultado;
	private Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas;
	private boolean flagUpdatePersona = false;
	private int indexUpdatePersona = 0;
	private UploadedFile file;
	private int numGrupoUpdatePoderdanteApoderado=0;

	int icontSoles = 0, icontDolares = 0, icontEuros = 0;
	double valorSoles_C = 0, valorSolesD = 0, valorSolesE = 0, valorEuro = 0,
			valorDolar = 0, valorFinal = 0;
	int item = 0;

	private int numGrupo = 0;
	List<TiivsSolicitudOperban> listaTemporalEliminarOperacionesBancarias;
	List<TiivsSolicitudOperban> lstSolicBancariasCopia;
	List<TiivsPersona> listaTemporalPersonasBorradores;
	List<TiivsPersona> lstTiivsPersonaCopia;
	List<TiivsAgrupacionPersona> listaTemporalAgrupacionesPersonaBorradores;
	List<TiivsAgrupacionPersona> listaAgrupacionesPersonaCopia;
	List<TiivsSolicitudAgrupacion > listaTemporalSolicitudAgrupacionesBorradores;
	List<TiivsSolicitudAgrupacion> listaSolicitudAgrupacionesCopia;
	
	
	private TiivsSolicitudAgrupacion tiivsSolicitudAgrupacionCapturado;
	private TiivsAgrupacionPersona tiivsAgrupacionPersonaCapturado;
	private String redirect = "";
	private String mesajeConfirmacion = "";
	private boolean mostrarRazonSocial = false;
	private String codigoRazonSocial = "0000";
	
	private String tipoRegistro;
	private String poderdante;
	private String apoderdante;
	private boolean bBooleanMoneda;
	private boolean bBooleanImporte;
	
	private boolean bComision;
	private boolean bFlagComision;
	private String descripcionComision;
	private String glosaComision;
	
	private String mesajeValidacionHost = "";
	private TiivsHostVoucher objVoucher;
	
	public ConsultarSolicitudMB() {		
		inicializarContructor();
		cargarDocumentos();

		//Para Incidencia 34
//		modificarTextoVentanaCartaAtencion();
//		mostrarCartaAtencion();
//		ocultarCartas();
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		this.cadenaEscanerFinal = this.prepararURLEscaneo();
		combosMB = new CombosMB();
		combosMB.cargarMultitabla();
		
		if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ))
		{
			setFlagMostrarSOMOficina(false);
			setFlagMostrarACOficina(true);
		}
		estilosNavegador=new EstilosNavegador();
		obtenCodRazonSocial();
		//Agregados 09/2013
		obtenerTipoRegistro();
		obtenerEtiquetasTipoRegistro();
		obtenerPagoComision();
	}
	
	
	/*
	 * Metodo que actualiza los listados de los Combos
	 * */
	public void actualizarListas(){
		logger.debug("*************actualizarListas************");
		combosMB = new CombosMB();	
	}
	
	public void modificarTextoVentanaCartaAtencion() {
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");

		if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
			setTextoMensajeCartaAtencion(ConstantesVisado.MENSAJE_CARTA_ATENCION.MENSA_SSJJ);
		} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
			setTextoMensajeCartaAtencion(ConstantesVisado.MENSAJE_CARTA_ATENCION.MENSAJE_OFICINA);
		}

		logger.info("TextoMensajeCartaAtencion:" + getTextoMensajeCartaAtencion());
	}
	
	public List<TiivsOficina1> completeNomOficina(String query) 
	{	
		List<TiivsOficina1> results = new ArrayList<TiivsOficina1>();

		for (TiivsOficina1 oficina : combosMB.getLstOficina1()) 
		{
			if (oficina.getCodOfi() != null)
			{
				String texto = oficina.getDesOfi();

				if (texto.contains(query.toUpperCase())) 
				{
					results.add(oficina);
				}
			}
		}

		return results;
	}
	
	public void actualizarClasificacion()
	{
		if (objTiivsPersonaResultado!=null)
		{
			if (objTiivsPersonaResultado.getTipPartic()!=null)
			{
				lstClasificacionPersona= new ArrayList<ComboDto>();
				
				for (ComboDto tmp: lstClasificacionPersona)
				{
					lstClasificacionPersona.remove(tmp);
				}		
				
				GenericDao<TiivsMultitabla, Object> serviceClas = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				Busqueda filtroTipoClas = Busqueda.forClass(TiivsMultitabla.class);
				filtroTipoClas.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION_PERSONA));
				filtroTipoClas.add(Restrictions.eq("valor3", objTiivsPersonaResultado.getTipPartic()));
				filtroTipoClas.add(Restrictions.eq("valor2", "1"));
				filtroTipoClas.addOrder(Order.asc("valor1"));
				List<TiivsMultitabla> lstTmpMult = new ArrayList<TiivsMultitabla>();
				
				try
				{
					lstTmpMult=serviceClas.buscarDinamico(filtroTipoClas);
				}
				catch (Exception e) 
				{
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+" de datos de Clasificacion de personas: ",e);
				}
				
				for (TiivsMultitabla mult: lstTmpMult)
				{
					ComboDto t = new ComboDto();
					t.setKey(mult.getId().getCodElem());
					t.setDescripcion(mult.getValor1());
					lstClasificacionPersona.add(t);
				}
				
				logger.debug("tamanio lista clasificacion: "+lstClasificacionPersona.size());
			}
			else
			{
				lstClasificacionPersona= new ArrayList<ComboDto>();
				
				for (ComboDto tmp: lstClasificacionPersona)
				{
					lstClasificacionPersona.remove(tmp);
				}				
				
				GenericDao<TiivsMultitabla, Object> serviceClas = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				Busqueda filtroTipoClas = Busqueda.forClass(TiivsMultitabla.class);
				filtroTipoClas.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION_PERSONA));
				filtroTipoClas.add(Restrictions.eq("valor2", "1"));
				filtroTipoClas.addOrder(Order.asc("valor1"));
				List<TiivsMultitabla> lstTmpMult = new ArrayList<TiivsMultitabla>();
				
				try
				{
					lstTmpMult=serviceClas.buscarDinamico(filtroTipoClas);
				}
				catch (Exception e) 
				{
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+" de datos de Clasificacion de personas: "+e);
				}
				
				for (TiivsMultitabla mult: lstTmpMult)
				{
					ComboDto t = new ComboDto();
					t.setKey(mult.getId().getCodElem());
					t.setDescripcion(mult.getValor1());
					lstClasificacionPersona.add(t);
				}
				
				logger.debug("tamanio lista clasificacion: "+lstClasificacionPersona.size());
			}
		}
	}

	/*
	public void mostrarCartaAtencion() {
		PERFIL_USUARIO = (String) Utilitarios
				.getObjectInSession("PERFIL_USUARIO");
		String codigoSolicitud = Utilitarios.capturarParametro("prm_codSoli");
		String sEstado="";

		if (codigoSolicitud != null) {
			try {
				logger.info("codigoSolicitud : " + codigoSolicitud);
				TiivsSolicitud solicitud = new TiivsSolicitud();
				solicitud.setCodSoli(codigoSolicitud);
				SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit
						.getApplicationContext().getBean("solicitudEspDao");
				solicitudRegistrarT = solicitudService
						.obtenerTiivsSolicitud(solicitud);
				solicitudRegistrarT.setDescEstado(Utilitarios
						.obternerDescripcionEstado(solicitudRegistrarT
								.getEstado()));
				sEstado = this.solicitudRegistrarT.getEstado().trim();
			} catch (Exception e) {
				logger.error("No se pueden obtener los datos de la solicitud",e);
			}

			
			
			boolean bMostrarCarta = false;
						
			if(sEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
				if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
					bMostrarCarta = true;
				}
				if(PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)){
					bMostrarCarta = true;
				}
			} else if(sEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)){
				if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
					bMostrarCarta = true;
				}
				
			} else if(sEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02)){
				if(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
					bMostrarCarta = true;
				}				
			}
						
			setbMostrarCartaAtencion(bMostrarCarta);
			
			
//			if ((PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) || PERFIL_USUARIO
//					.equals(ConstantesVisado.OFICINA))
//					&& (!sEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02))) {
//				if (sEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02)
//						&& PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
//					setbMostrarCartaAtencion(false);
//					logger.info("No Se debe mostrar el link de carta de atencion");
//				} else if(sEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02) ||
//						sEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02) ) {
//					setbMostrarCartaAtencion(true);
//					logger.info("Se debe mostrar el link de carta de atencion");
//				}
//			} else {
//				setbMostrarCartaAtencion(false);
//				logger.info("No Se debe mostrar el link de carta de atencion");
//			}
			
			
		} else {
			logger.info("Solicitud no valida o nula");
		}
	}
	
	*/

	/*
	public void ocultarCartas() {
		if (this.solicitudRegistrarT.getEstado().trim()
				.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)) {
			setbMostrarCartaRevision(false);
			setbMostrarCartaAtencion(false);
		} else if (this.solicitudRegistrarT.getEstado().trim()
				.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)) {
			setbMostrarCartaRevision(true);
			setbMostrarCartaAtencion(false);
		} else if (this.solicitudRegistrarT.getEstado().trim()
				.equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02)) {
			setbMostrarCartaAtencion(false);
		}
	}
	*/

	
	
	
	//Ref: SeguimientoPestania: Botón "Generar Revision"
	public void mostrarCartaRevision() {
		logger.debug("=== en mostrarCartaRevision() ===");
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		logger.debug("[SHOW_CARTA_REV]-PERFIL_USUARIO: "+PERFIL_USUARIO);
		if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
			if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)){
				setbMostrarCartaRevision(true);
				setbMostrarCartaAtencion(false);
				setbMostrarGenerarRevision(false);
			} else {
				setbMostrarCartaRevision(false);
				setbMostrarCartaAtencion(false);
			}
			 
		} else if(PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)){
			if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)){
				setbMostrarCartaRevision(true);
				setbMostrarCartaAtencion(false);
				setbMostrarGenerarRevision(false);
				setbSeccionDocumentos(true);
				//setbcartaRevision(false);
				
			} else {
				setbMostrarCartaRevision(false);
				setbMostrarCartaAtencion(false);
			}
		}
		
		
//				PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
//				&& (this.solicitudRegistrarT.getEstado().trim()
//						.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02))) {
//			setbMostrarCartaRevision(true);
//			setbMostrarCartaAtencion(false);
//			setbMostrarGenerarRevision(false);
//		} else {
//			setbMostrarCartaRevision(false);
//			setbMostrarCartaAtencion(false);
//		}
	}
	


	public Date aumentarFechaxVen(Date fecha, int nroDias) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);

		Date nuevaFecha = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, nroDias);
		nuevaFecha = cal.getTime();
		return nuevaFecha;
	}

	public void habilitarComentario() {
		PERFIL_USUARIO = (String) Utilitarios
				.getObjectInSession("PERFIL_USUARIO");

		if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)
				|| PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)
				|| PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
			setbMostrarComentario(false);
		}		
	}

	public void inicializarContructor() {
		solicitudRegistrarT = new TiivsSolicitud();

		lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
//		lstAnexosSolicitudes = new ArrayList<TiivsAnexoSolicitud>();
		//lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		objAgrupacionSimpleDtoCapturado = new AgrupacionSimpleDto();
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
		lstAbogados = new ArrayList<TiivsMiembro>();
		lstTiivsDocumentos = new ArrayList<TiivsDocumento>();
		lstDocumentosGenerados = new ArrayList<ComboDto>();
		objSolicBancaria = new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		objSolicBancaria.setTiivsOperacionBancaria(new TiivsOperacionBancaria());
		lstDocumentosXTipoSolTemp = new ArrayList<TiivsTipoSolicDocumento>();
		aliasFilesToDelete = new ArrayList<String>();
		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
		objSolicitudOperacionCapturado = new TiivsSolicitudOperban();
		mapSolicitudes=new HashMap<Integer, TiivsSolicitudOperban>();
		objTiivsPersonaResultado = new TiivsPersona();
		objTiivsPersonaCapturado = new TiivsPersona();
		objTiivsPersonaBusqueda = new TiivsPersona();
		lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		
		listaTemporalEliminarOperacionesBancarias=new ArrayList<TiivsSolicitudOperban>();				
			
	}

	public void listarComboDictamen() throws Exception {
		lstComboDictamen = new ArrayList<ComboDto>();
		/*SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");

		solicitudRegistrarT = (TiivsSolicitud) solicitudService.buscarDinamico(Busqueda.forClass(TiivsSolicitud.class)
				               .add(Restrictions.eq("codSoli", solicitudRegistrarT.getCodSoli()))).get(0);*/
	
		
		logger.info("this.solicitudRegistrarT.getEstado() "+ this.solicitudRegistrarT.getEstado());
		// SOLO SERVICIOS JURIDICOS
		if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)
				|| PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
			
			if (this.solicitudRegistrarT.getEstado().trim()
					.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02)) {
				if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
					lstComboDictamen.add(new ComboDto(
							ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02,
							ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02));
					lstComboDictamen.add(new ComboDto(
							ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02,
							ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02));
				}
			}
			else if (this.solicitudRegistrarT.getEstado().trim()
					.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02)) {
				logger.info("xxxx  ESTADO_COD_RESERVADO_T02");
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02,
						ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02));
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02,
						ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02));
			} else if (this.solicitudRegistrarT
					.getEstado()
					.trim()
					.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)) {
				logger.info("xxxx  ESTADO_COD_EN_REVISION_T02");
			
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02,
						ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02));
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02,
						ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02));
			} else if (this.solicitudRegistrarT
					.getEstado()
					.trim()
					.equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)) {
				logger.info("xxxx  ESTADO_COD_IMPROCEDENTE_T02");
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02,
						ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02));
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02,
						ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02));
			} else if (this.solicitudRegistrarT
					.getEstado()
					.trim()
					.equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
				logger.info("xxxx  ESTADO_COD_PROCEDENTE_T02");
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02,
						ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02));
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02,
						ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02));
			} else if (this.solicitudRegistrarT
				.getEstado()
				.trim()
				.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)) {
				logger.info("xxxx  ESTADO_COD_RECHAZADO_T02");
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02,
						ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02));
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02,
						ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02));
			} else if (this.solicitudRegistrarT
					.getEstado()
					.trim()
					.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
					logger.info("xxxx  ESTADO_COD_ACEPTADO_T02");
					lstComboDictamen.add(new ComboDto(
							ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02,
							ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02));
					lstComboDictamen.add(new ComboDto(
							ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02,
							ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02));
			}
		}
	}

	public String redirectDetalleSolicitud() {
		logger.info(" ===== redirectDetalleSolicitud ()====");
		obtenerSolicitud(Utilitarios.capturarParametro("prm_codSoli"));
		return getRedirectDetalleSolicitud();
	}
	
	public String redirectDetalleSolicitud(String codigoSolicitud) {
		logger.info(" ===== redirectDetalleSolicitud(s) ====");
		logger.debug("[redirectDetallSol]-codigoSolicitud:"+codigoSolicitud);
		obtenerSolicitud(codigoSolicitud);		
		return getRedirectDetalleSolicitud();
	}
	
	private String getRedirectDetalleSolicitud(){
		logger.info("[getRedirecDetSol]-Estado de la solicitud :: " +this.solicitudRegistrarT.getEstado());
		String redirect = "";		
		if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02)) {
			redirect = "/faces/paginas/solicitudEdicion.xhtml";
			//obtenerSolicitud();
		} else {					
			redirect = "/faces/paginas/detalleSolicitud.xhtml";
		}
		/*** SE MUEVEN LAS 2 LINEAS DE ABAJO, ORIGINALMENTE ESTABAN DENTRO DEL ELSE ***/
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		request.getSession(true).setAttribute("SOLICITUD_TEMP", solicitudRegistrarT);
		logger.debug("[getRedirecDetSol]-redirect: "+redirect);
		return redirect;
	}

	//METODO PARA OBTENER ETIQUETAS DE TIPO DE REGISTRO DESDE BD Y MOSTRARLO EN GRILLA
	private void obtenerEtiquetasTipoRegistro(){
		obtenerPonderdante();
		obtenerAponderdante();
	}
	
	private void obtenerPonderdante(){
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultitabla.add(Restrictions.eq("valor3", ConstantesVisado.R1_PODERDANTE));
		List<TiivsMultitabla> listaMultiTabla = new ArrayList<TiivsMultitabla>();
		Integer contador = 0;
		try {
			listaMultiTabla = multiDAO.buscarDinamico(filtroMultitabla);
			poderdante = "";
			if(listaMultiTabla.size()>0){
				for(TiivsMultitabla multitabla:listaMultiTabla){
					contador++;
					if(contador.compareTo(listaMultiTabla.size())==0){
						poderdante += multitabla.getValor1();	
					}else{
						poderdante += multitabla.getValor1() + " - ";
					}
				}
			}
		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+ "de multitablas: " + e);
		}
	}
	
	private void obtenerAponderdante(){
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultitabla.add(Restrictions.eq("valor3", ConstantesVisado.R2_APODERADO));
		List<TiivsMultitabla> listaMultiTabla = new ArrayList<TiivsMultitabla>();
		Integer contador = 0;
		try {
			listaMultiTabla = multiDAO.buscarDinamico(filtroMultitabla);
			apoderdante = "";
			if(listaMultiTabla.size()>0){
				for(TiivsMultitabla multitabla:listaMultiTabla){
					contador++;
					if(contador.compareTo(listaMultiTabla.size())==0){
						apoderdante += multitabla.getValor1();	
					}else{
						apoderdante += multitabla.getValor1() + " - ";
					}
				}
			}
		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+ "de multitablas: " + e);
		}
	}
		
	public void abogadosXEstudios(ValueChangeEvent e) {
		logger.info("*************** abogadosXEstudios *********************"
				+ e.getNewValue());
		List<TiivsMiembro> lstAbogadosMiembro = combosMB.getLstAbogados();
		lstAbogados = new ArrayList<TiivsMiembro>();
		for (TiivsMiembro x : lstAbogadosMiembro) {
			if(x.getEstudio()!=null){
			if (x.getEstudio().trim().equals(e.getNewValue())) {
				lstAbogados.add(x);
			}
			}else{
				logger.info("El estudio del Abogado es nulll");
			}
		}
	}

	public void reasignacionManual() {
		logger.info("********** reasignacionManual *********");
		logger.info("" + solicitudRegistrarT.getTiivsEstudio().getCodEstudio());
		logger.info("" + solicitudRegistrarT.getRegAbogado());
		try {
			GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			solicitudRegistrarT = service.modificar(solicitudRegistrarT);
			Utilitarios.mensajeInfo("INFO","Se realizó la reasignación manual Correctamente");
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en reasignacionManual():",e);
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerSolicitud(String codigoSolicitud)
	{
		try {
			logger.debug("==== obtenerSolicitud(s):INICIO ==== ");
			lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
			listaTemporalSolicitudAgrupacionesBorradores=new ArrayList<TiivsSolicitudAgrupacion>();
			
			TiivsSolicitud solicitud;
			logger.info("[obtenerSolic]-codSol: " + codigoSolicitud);
			solicitud = new TiivsSolicitud();
			solicitud.setCodSoli(codigoSolicitud);
			
			SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
			solicitudRegistrarT = solicitudService.obtenerTiivsSolicitud(solicitud);
			//Estado
			solicitudRegistrarT.setDescEstado(Utilitarios.obternerDescripcionEstado(solicitudRegistrarT.getEstado()));
						
			lstSolicBancarias = solicitudService.obtenerListarOperacionesBancarias(solicitud);
			// Por SAMIRA 
			solicitudRegistrarT.setLstSolicBancarias(lstSolicBancarias);
			lstSolicBancariasCopia = new ArrayList<TiivsSolicitudOperban>();
			lstSolicBancariasCopia.addAll(solicitudService.obtenerListarOperacionesBancarias(solicitud));
						
			if(lstSolicBancariasCopia!=null)
			{
				logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"lstSolicBancariasCopia es: ["+lstSolicBancariasCopia.size()+"].");	
			}
			//Exonera comision
			if(solicitudRegistrarT.getExoneraComision()!=null
					&& solicitudRegistrarT.getExoneraComision().compareTo("1")==0){
				this.setbFlagComision(true);
			}else{
				this.setbFlagComision(false);
			}
			
			//FIN  BY SAMIRA 
			int y = 0;
			//Operacion bancaria - Solicitud
			for (TiivsSolicitudOperban f : lstSolicBancarias) 
			{ 
				logger.info("SolOperBanc-Moneda: " +f.getId().getMoneda());
				if (f.getId().getMoneda() != null) 
				{
					y++;
					f.setsItem(String.format("%03d", y));
					f.getId().setsDescMoneda(Utilitarios.obternerDescripcionMoneda(f.getId().getMoneda()));
					
					if (f.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
					{
						f.setImporteSoles(f.getImporte());
						f.setTipoCambio(0.0);						
					}
					if (f.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) 
					{
						f.setImporteSoles(f.getTipoCambio() * f.getImporte());						
					}
					if (f.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
					{
						f.setImporteSoles(f.getTipoCambio() * f.getImporte());						
					}
				}
			}
			//Oficina
			if (solicitudRegistrarT.getTiivsOficina1()!=null){
				setOficina(solicitudRegistrarT.getTiivsOficina1());
			}
			//Importes
			obtenerImporteTotalxSolicitud(lstSolicBancarias);
			
			//this.lstAnexosSolicitudes = solicitudService.obtenerListarAnexosSolicitud(solicitud);			
			this.lstAnexoSolicitud = solicitudService.obtenerListarAnexosSolicitud(solicitud);
			if(lstAnexoSolicitud!=null){
				logger.info(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"documentos(Anexos) es:[" + this.lstAnexoSolicitud.size()+"]");
			}
			//Tipo de Solicitud
			this.iTipoSolicitud =solicitudRegistrarT.getTiivsTipoSolicitud().getCodTipSolic(); 
			
			//Descargar Anexos
			descargarAnexosFileServer();						
			
			boolean isEditar=false;
			if(this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02)){
				isEditar=true;
				bBooleanPopup=false;
			} else {
				isEditar=false;
				bBooleanPopup=false;
			}
			//Documentos
			llenarListaDocumentosSolicitud(isEditar);
			
			if(lstAnexoSolicitud!=null){
				logger.info(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"documentos(Anexos) - NUEVOS es:[" + this.lstAnexoSolicitud.size()+"]");
			}
			
			solicitudRegistrarT.setLstDocumentos(lstdocumentos); //Para reportes
			//solicitudRegistrarTCopia=new TiivsSolicitud(); //06-05-2013 Se comenta por falta de uso Incidencia 259
			//solicitudRegistrarTCopia=this.solicitudRegistrarT;		
			
		   listaSolicitudAgrupacionesCopia=new ArrayList<TiivsSolicitudAgrupacion>();
		   listaSolicitudAgrupacionesCopia.addAll(this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) ;
		   
		   armaAgrupacionSimple();		   
		   		   		   
		   solicitudRegistrarT.setLstAgrupacionSimpleDto(lstAgrupacionSimpleDto); //reporte
		   
		   if(solicitudRegistrarT.getTiivsSolicitudAgrupacions()!=null){
			   logger.info("Obtener solicitud agrupaciones: " + solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
		   }
		   if(lstAgrupacionSimpleDto!=null){
			   logger.info("Size lstAgrupacionSimpleDto: " + lstAgrupacionSimpleDto.size());
		   }
		  
			this.actualizarEstadoReservadoSolicitud();
			this.obtenerHistorialSolicitud();
			
			//Estudio Abogado
			if (solicitudRegistrarT.getTiivsEstudio() == null) {
				solicitudRegistrarT.setTiivsEstudio(new TiivsEstudio());
			}else{
				if(this.solicitudRegistrarT.getTiivsEstudio()!=null){
					List<TiivsMiembro> lstAbogadosMiembro = combosMB.getLstAbogados();
					lstAbogados = new ArrayList<TiivsMiembro>();
					
					for (TiivsMiembro x : lstAbogadosMiembro) {
						if(x.getEstudio()!=null){
							if (x.getEstudio().trim().equals(this.solicitudRegistrarT.getTiivsEstudio().getCodEstudio())) {
								lstAbogados.add(x);
							}
						}else{
							//logger.info("LA SOLICITUD NO TIENE UN ESTUDIO ASIGNADO");
						}
					}
				}			
			}
			// Listar ComboDictamen
			listarComboDictamen();
			
			//Actualizar clasificacion
			actualizarClasificacion();
			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION +"al recuperar el Detalle Solicitud: ",e);
		}
		logger.debug("==== obtenerSolicitud(s):FIN ==== ");
	}
	
	private void armaAgrupacionSimple() {
		
		List<TiivsPersona> lstPoderdantes = null;
		List<TiivsPersona> lstApoderdantes = null;
		AgrupacionSimpleDto agrupacionSimpleDto  =null; ;
		List<TiivsPersona>lstPersonas=null;
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		
		for (TiivsSolicitudAgrupacion x : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) 
		   {
			   lstPoderdantes = new ArrayList<TiivsPersona>();
			   lstApoderdantes = new ArrayList<TiivsPersona>();
			   lstPersonas=new ArrayList<TiivsPersona>();
			   for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) 
			   {    
				    d.getTiivsPersona().setTipPartic(d.getTipPartic());
				    d.getTiivsPersona().setClasifPer(d.getClasifPer());
				    d.getTiivsPersona().setsDesctipPartic(this.obtenerDescripcionTipoRegistro(d.getTipPartic().trim()));
				    d.getTiivsPersona().setsDescclasifPer(this.obtenerDescripcionClasificacion(d.getClasifPer().trim()));
				    d.getTiivsPersona().setsDesctipDoi(this.obtenerDescripcionDocumentos(d.getTiivsPersona().getTipDoi().trim()));
				    if(d.getIdAgrupacion()!=null){
				    	d.getTiivsPersona().setIdAgrupacion(d.getIdAgrupacion());
				    }
				    
				    lstPersonas.add(d.getTiivsPersona());
				   
					if(d.getTipPartic().trim().equals(ConstantesVisado.PODERDANTE))
					{
						lstPoderdantes.add(d.getTiivsPersona());
					}
					else  if(d.getTipPartic().trim().equals(ConstantesVisado.APODERADO))
					{
						lstApoderdantes.add(d.getTiivsPersona());
					}else if(d.getTipPartic().trim().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO))
					{
						lstApoderdantes.add(d.getTiivsPersona());
					}
			   }
			    agrupacionSimpleDto = new AgrupacionSimpleDto();
				agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudRegistrarT.getCodSoli(), x.getId().getNumGrupo()));
				agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
				agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
				agrupacionSimpleDto.setsEstado(Utilitarios.obternerDescripcionEstado(x.getEstado().trim()));
				agrupacionSimpleDto.setLstPersonas(lstPersonas);
			    lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
			   
			   
		   }
		
			logger.info("Lista Poderdantes: " + lstPoderdantes.size());
		   logger.info("Lista Apoderados: " + lstApoderdantes.size());
		
	}

	private void llenarListaDocumentosSolicitud(boolean isEditar){
		
		//String sEstado = this.solicitudRegistrarT.getEstado();
		
		//En caso de editar una solicitud
		if(isEditar){ 
			
			String scodTipoSolicitud = solicitudRegistrarT.getTiivsTipoSolicitud().getCodTipSolic();
			listarDocumentosXSolicitud(scodTipoSolicitud);
			
			
			logger.info("lstAnexoSolicitud.size:" + this.lstAnexoSolicitud.size());
		
			for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){
				if(anexo.getId().getCodDoc().contains(ConstantesVisado.PREFIJO_OTROS)){
					DocumentoTipoSolicitudDTO doc = new DocumentoTipoSolicitudDTO();
					doc.setItem(anexo.getId().getCodDoc());
					doc.setDocumento(obtenerDescripcionDocumento(anexo.getId().getCodDoc()));
					doc.setObligacion(false+"");
					doc.setAlias(anexo.getAliasArchivo());
					doc.setAliasTemporal(anexo.getAliasTemporal());
					lstdocumentos.add(doc);
//					lstdocumentos.add(new DocumentoTipoSolicitudDTO(anexo.getId().getCodDoc(), 
//							obtenerDescripcionDocumento(anexo.getId().getCodDoc()),false+"",
//							anexo.getAliasArchivo(),anexo.getAliasTemporal(),"","")); //nombre corto, 
				} else {
					for(DocumentoTipoSolicitudDTO doc : lstdocumentos){
						if(doc.getItem().equals(anexo.getId().getCodDoc())){
							doc.setAlias(anexo.getAliasArchivo());
							doc.setAliasTemporal(anexo.getAliasTemporal());
							TiivsTipoSolicDocumento solDocRemover=new TiivsTipoSolicDocumento();
							for(TiivsTipoSolicDocumento solDoc : lstTipoSolicitudDocumentos){
								if(solDoc.getId().getCodDoc().equals(doc.getItem())){
									solDocRemover = solDoc;
									break;
								}
							}
							lstTipoSolicitudDocumentos.remove(solDocRemover);
						} 
					}
				}				
			}					
			
		} else {	//Para consulta		
			lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
			//for (TiivsAnexoSolicitud v : this.lstAnexosSolicitudes) 
			for (TiivsAnexoSolicitud v : this.lstAnexoSolicitud)
			{				
				if (v.getId().getCodDoc().contains(ConstantesVisado.PREFIJO_OTROS))
				{
					DocumentoTipoSolicitudDTO doc = new  DocumentoTipoSolicitudDTO();
					doc.setItem(v.getId().getCodDoc());
					doc.setDocumento(obtenerDescripcionDocumento(v.getId().getCodDoc()));
					doc.setObligacion(false+"");
					doc.setAlias(v.getAliasArchivo());
					doc.setAliasTemporal(v.getAliasTemporal());	
					lstdocumentos.add(doc);
//					lstdocumentos.add(new DocumentoTipoSolicitudDTO(v.getId().getCodDoc(), 
//							obtenerDescripcionDocumento(v.getId().getCodDoc()),false+"",
//							v.getAliasArchivo(),v.getAliasTemporal()));
				}
				else
				{
					DocumentoTipoSolicitudDTO doc = new  DocumentoTipoSolicitudDTO();
					doc.setItem(v.getId().getCodDoc());
					doc.setDocumento(obtenerDescripcionDocumento(v.getId().getCodDoc()));
					doc.setObligacion(obtenerFlagObligatorioxDoc(v.getId().getCodDoc())+"");
					doc.setAlias(v.getAliasArchivo());
					doc.setAliasTemporal(v.getAliasTemporal());	
					lstdocumentos.add(doc);					
//					lstdocumentos.add(new DocumentoTipoSolicitudDTO(v.getId().getCodDoc(), 
//									obtenerDescripcionDocumento(v.getId().getCodDoc()),obtenerFlagObligatorioxDoc(v.getId().getCodDoc())+"",
//									v.getAliasArchivo(),v.getAliasTemporal()));
				}
			}			
						
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean obtenerFlagObligatorioxDoc(String codDoc)
	{
		boolean flag=false;
		
		List<TiivsTipoSolicDocumento> lista = new ArrayList<TiivsTipoSolicDocumento>();
		
		GenericDao<TiivsTipoSolicDocumento, Object> genTipoSolcDocumDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSolcDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);
		//filtroTipoSolcDoc.createAlias("id", "tipoSolDoc");
		filtroTipoSolcDoc.add(Restrictions.eq("id.codDoc",codDoc));
		
		try {
			lista = genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);
		} catch (Exception ex) {
			logger.info("Error al cargar el listado de documentos por tipo de soliciitud");
			ex.printStackTrace();
		}
		
		for (TiivsTipoSolicDocumento tmp: lista)
		{
			if (tmp.getId().getCodDoc().equals(codDoc))
			{
				if (tmp.getObligatorio().trim().equals("1"))
				{
					flag=true;
				}
				break;
			}
		}
		
		return flag;
	}

	public String obtenerDescripcionDocumentos(String idTipoDocumentos) {
		String descripcion = "";
		for (TipoDocumento z : combosMB.getLstTipoDocumentos()) {
			if (z.getCodTipoDoc().trim().equals(idTipoDocumentos)) {
				descripcion = z.getDescripcion();
				break;
			}
		}
		return descripcion;
	}

	public String obtenerDescripcionClasificacion(String idTipoClasificacion) {
		String descripcion = "";
		for (ComboDto z : combosMB.getLstClasificacionPersona()) {
			if (z.getKey().trim().equals(idTipoClasificacion)) {
				descripcion = z.getDescripcion();
				break;
			}
		}
		return descripcion;
	}

	public String obtenerDescripcionTipoRegistro(String idTipoTipoRegistro) {
		String descripcion = "";
		for (ComboDto z : combosMB.getLstTipoRegistroPersona()) {
			if (z.getKey().trim().equals(idTipoTipoRegistro)) {
				descripcion = z.getDescripcion();
				break;
			}
		}
		return descripcion;
	}

	public void actualizarEstadoReservadoSolicitud() throws Exception 
	{
		logger.debug("==== En el actualizarEstadoReservadoSolicitud() ====");
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		logger.debug("[ActEstRESERV]-Usuario: " + usuario.getUID());
		logger.debug("[ActEstRESERV]-Estado Solicitud: "+this.solicitudRegistrarT.getEstado());
		
		//adecuar
		//Para Incidencia 34
		modificarTextoVentanaCartaAtencion();
		
		//Adecuado, verificar 02/03/13
//		mostrarCartaAtencion();		
//		ocultarCartas();

		logger.info("*********************** actualizarEstadoReservadoSolicitud **************************");
		if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02)) 
		{
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) 
			{   this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bSeccionDocumentos = true;
				this.bMostrarGenerarRevision = false;
				
				this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02);
				this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
				this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_RESERVADO_T02);
				GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				service.modificar(solicitudRegistrarT);
				this.registrarHistorial(solicitudRegistrarT);

			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {

				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = true;
				this.bSeccionEvaluarNivel = false;				
				this.bMostrarGenerarRevision = false;
				this.bMostrarReImprimirCartaAtencion = false;
			
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarSolicitudVisado = false;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = false;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.bMostrarReImprimirCartaAtencion = false;
			}
		} else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02)) {
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) 
			{	
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.bMostrarReImprimirCartaAtencion = false;				
				IILDPeUsuario usuarioRPTA = obtenerOrigenEstadoReservado(solicitudRegistrarT);
				
				logger.debug("Usuario quien reservo: " + usuarioRPTA.getUID());
				
				if (usuarioRPTA.getUID().compareTo(usuario.getUID())==0)
				{
					this.bMostrarMSGEstado_Reservado=false;
					logger.debug("Usuarios con Ids iguales no se mostrará mensaje de estado reservado");
				}
				else
				{
					logger.debug("Usuarios con Ids diferentes se mostrará mensaje de estado reservado");
					
					this.bMostrarMSGEstado_Reservado=true;
					
					if (usuarioRPTA!=null)
					{	
						if (usuarioRPTA.getUID()!=null)
						{
							setsTextoEstadoReservado(ConstantesVisado.TEXTO_MSG_ESTADO_RESERVADO + usuarioRPTA.getUID() + " - " + usuarioRPTA.getNombre());
						}
					}
				}				
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.bMostrarMSGEstado_Reservado=false;
				this.bMostrarReImprimirCartaAtencion = false;
				/*IILDPeUsuario usuarioRPTA = obtenerOrigenEstadoReservado(solicitudRegistrarT);
				
				if (usuarioRPTA!=null)
				{	
					if (usuarioRPTA.getUID()!=null)
					{
						setsTextoEstadoReservado("La solicitud está reservada por el usuario: " + usuarioRPTA.getUID() + " - " + usuarioRPTA.getNombre());
					}
				}*/

			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarSolicitudVisado = false;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = false;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.bMostrarReImprimirCartaAtencion = false;
			}
			
		}else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {

				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.bMostrarReImprimirCartaAtencion = false;
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = this.validarSiDictaminadoEnRevision();
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.listarComboDictamen();

			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = this.validarSiDictaminadoEnRevision();
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			}
		}
		else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){
			
			evaluacionNivelesMB = new EvaluacionNivelesMB(solicitudRegistrarT);
			TiivsSolicitudNivel solNivel = evaluacionNivelesMB.obtenerNivelSolicitud();
			if (solNivel != null) {
				
				String sCodigoNivel = solNivel.getCodNiv();				
				NivelService nivelService = new NivelService(); 
				this.sNivelSolicitud = nivelService.buscarNivelxCodigo(sCodigoNivel);
			}
			
            if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
            	
            	this.bMostrarReImprimirCartaAtencion = false;
            	this.bMostrarSolicitudVisado = true;
            	this.bMostrarCartaAtencion = false;
            	this.bMostrarCartaRechazo = false;
            	this.bMostrarCartaRevision = false;
            	this.bMostrarCartaImprocedente = false;
            	this.bSeccionDocumentos = true;
            	this.bSeccionDictaminar = false;
            	this.bSeccionComentario = false;
            	this.bSeccionReasignacion = false;
            	this.bSeccionEvaluarNivel = false;
            	this.bMostrarGenerarRevision = false;
            	
        	} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
        		
        		this.bMostrarReImprimirCartaAtencion = false;
        		this.bMostrarSolicitudVisado = true;
            	this.bMostrarCartaAtencion = false;
            	this.bMostrarCartaRechazo = false;
            	this.bMostrarCartaRevision = false;
            	this.bMostrarCartaImprocedente = false;
            	this.bSeccionDocumentos = true;
            	this.bSeccionDictaminar = false;
            	this.bSeccionComentario = true;
            	this.bSeccionReasignacion = false;
            	this.bSeccionEvaluarNivel = true;
            	this.bMostrarGenerarRevision = false;
            	
        	} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
        		
        		this.bMostrarReImprimirCartaAtencion = false;
        		this.bMostrarSolicitudVisado = true;
            	this.bMostrarCartaAtencion = false;
            	this.bMostrarCartaRechazo = false;
            	this.bMostrarCartaRevision = false;
            	this.bMostrarCartaImprocedente = false;
            	this.bSeccionDocumentos = true;
            	this.bSeccionDictaminar = false;
            	this.bSeccionComentario = false;
            	this.bSeccionReasignacion = false;
            	this.bSeccionEvaluarNivel = false;
            	this.bMostrarGenerarRevision = false;
            	
        	}			
				
		}
		else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02)){
			evaluacionNivelesMB = new EvaluacionNivelesMB(solicitudRegistrarT);
			TiivsSolicitudNivel solNivel = evaluacionNivelesMB.obtenerNivelSolicitud();
			if (solNivel != null) {
				String sCodigoNivel = solNivel.getCodNiv();				
				NivelService nivelService = new NivelService(); 
				this.sNivelSolicitud = nivelService.buscarNivelxCodigo(sCodigoNivel);
			}
			
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
            	this.bMostrarSolicitudVisado = true;
            	this.bMostrarCartaAtencion = false;
            	this.bMostrarCartaRechazo = true;
            	this.bMostrarCartaRevision = true;
            	this.bMostrarCartaImprocedente = false;
            	this.bSeccionDocumentos = true;
            	this.bSeccionDictaminar = false;
            	this.bSeccionComentario = false;
            	this.bSeccionReasignacion = false;
            	this.bSeccionEvaluarNivel = false;
            	this.bMostrarGenerarRevision = false;
            	
        	} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
        		
        		this.bMostrarReImprimirCartaAtencion = false;
        		this.bMostrarSolicitudVisado = true;
            	this.bMostrarCartaAtencion = false;
            	this.bMostrarCartaRechazo = true;
            	this.bMostrarCartaRevision = true;
            	this.bMostrarCartaImprocedente = false;
            	this.bSeccionDocumentos = true;
            	this.bSeccionDictaminar = false;
            	this.bSeccionComentario = true;
            	this.bSeccionReasignacion = false;
            	this.bSeccionEvaluarNivel = true;
            	this.bMostrarGenerarRevision = false;
        		
        	} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
        		
        		this.bMostrarReImprimirCartaAtencion = false;
        		this.bMostrarSolicitudVisado = true;
        		this.bMostrarCartaAtencion = false;
        		this.bMostrarCartaRechazo = true;
        		this.bMostrarCartaRevision = true;
        		this.bMostrarCartaImprocedente = false;
        		this.bSeccionDocumentos = true;
        		this.bSeccionDictaminar = false;
        		this.bSeccionComentario = false;
        		this.bSeccionReasignacion = false;
        		this.bSeccionEvaluarNivel = false;
        		this.bMostrarGenerarRevision = false;
        		
        	}
			
		}
	
	else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)) {
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = true;				
				this.listarComboDictamen();
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = true;
			}
			
		} else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)) {
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarReImprimirCartaAtencion = false;				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
				listarComboDictamen();
				
			}else if(PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)){
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			}
		} else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
			
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = false;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.listarComboDictamen();
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			}
		} else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)) {
			
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = false;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = true;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = true;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				this.listarComboDictamen();
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = true;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			}
			
		} else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02)) {
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = false;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				/* MEJORA */
				this.bMostrarReImprimirCartaAtencion = true;
				/* MEJORA */
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = this.validarSiDictaminadoEnRevision();
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarReImprimirCartaAtencion = false;
				/*Modificado 24/09/2013. Cuando SSJJ ejecuta la opción de ReImprimir, para oficina se activa nuevamente el link*/
				if(this.solicitudRegistrarT.getFlagReimprimir().equals(ConstantesVisado.ACTIVO_FLAG_REIMPRIMIR)){
					this.bMostrarCartaAtencion = true;
			    /*Modificado 24/09/2013. Para la oficina si es la primera vez debera aparecer la Carta de Atención (Flujo Actual)*/
				}else if(this.solicitudRegistrarT.getFlagReimprimir().equals(null)){
					this.bMostrarCartaAtencion = true;
				/*Modificado 24/09/2013. Si luego de que oficina vuelva a reimprimir, se le desactivara la opción 
				 * Hasta que SSJJ vuelva a activar la Reimpresión*/
				}else if(this.solicitudRegistrarT.getFlagReimprimir().equals(ConstantesVisado.DESACTIVO_FLAG_REIMPRIMIR)){
					this.bMostrarCartaAtencion = false;
				}
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = this.validarSiDictaminadoEnRevision();
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} 
			
		}else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02)) {
			
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = false;
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = false;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = this.validarSiDictaminadoEnRevision();
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarReImprimirCartaAtencion = false;
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = this.validarSiDictaminadoRechazado();
				this.bMostrarCartaRevision = this.validarSiDictaminadoEnRevision();
				this.bMostrarCartaImprocedente = this.validarSiDictaminadoEnImprocedente();
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;				
			} 
		}
	}
	/* Validar si la solicitud fue dictaminado rechazado*/
	public boolean validarSiDictaminadoRechazado() throws Exception{
		    boolean retorno=false;
		    String sCodSolicitud = solicitudRegistrarT.getCodSoli();
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
			filtroHist.add(Restrictions.eq("id.codSoli", sCodSolicitud));
			filtroHist.add(Restrictions.eq("estado", ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02));
			/** RECHAZADO PERO X DICTAMINACION , NO VIA revocacion**/
			filtroHist.add(Restrictions.isNull("flagRevocado"));
					//eq("flagRevocado", ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO));
			List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
			lstHist = histDAO.buscarDinamico(filtroHist);
			if(lstHist!=null){
				if(lstHist.size()>0){
					/** Existe en el historico un rechazado*/
					 retorno=true;
				}
			}
			return retorno;
	}
	/* Validar si la solicitud fue dictaminado en Revision*/
	public boolean validarSiDictaminadoEnRevision() throws Exception{
		    boolean retorno=false;
		    String sCodSolicitud = solicitudRegistrarT.getCodSoli();
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
			filtroHist.add(Restrictions.eq("id.codSoli", sCodSolicitud));
			filtroHist.add(Restrictions.eq("estado", ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02));

			List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
			lstHist = histDAO.buscarDinamico(filtroHist);
			if(lstHist!=null){
				if(lstHist.size()>0){
					/** Existe en el historico un rechazado*/
					 retorno=true;
				}
			}
			return retorno;
	}
	/* Validar si la solicitud fue dictaminado en Improcedente*/
	public boolean validarSiDictaminadoEnImprocedente() throws Exception{
		    boolean retorno=false;
		    String sCodSolicitud = solicitudRegistrarT.getCodSoli();
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
			filtroHist.add(Restrictions.eq("id.codSoli", sCodSolicitud));
			filtroHist.add(Restrictions.eq("estado", ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02));

			List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
			lstHist = histDAO.buscarDinamico(filtroHist);
			if(lstHist!=null){
				if(lstHist.size()>0){
					/** Existe en el historico un rechazado*/
					 retorno=true;
				}
			}
			return retorno;
	}
	
	public IILDPeUsuario obtenerOrigenEstadoReservado(TiivsSolicitud soli)
	{
		logger.debug("---------En el obtenerOrigenEstadoReservado---------");
		
		IILDPeUsuario usuarioRPTA = new IILDPeUsuario();
		
		if (soli!=null)
		{
			GenericDao<TiivsHistSolicitud, Object> service = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
			
			filtro.add(Restrictions.eq("id.codSoli", soli.getCodSoli()));
			filtro.add(Restrictions.eq("estado", ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02));
			filtro.addOrder(Order.desc("fecha"));
			
			List<TiivsHistSolicitud> lstHstSolic = new ArrayList<TiivsHistSolicitud>();
			
			try {
				lstHstSolic = service.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"lstHstSolic:",e);
			}
			if(lstHstSolic!=null){
				logger.debug("Tamanio lista historial: " + lstHstSolic.size());	
			}
			
			if (lstHstSolic.size()>=1)
			{
				if (lstHstSolic.get(0).getRegUsuario()!=null)
				{
					usuarioRPTA.setUID(lstHstSolic.get(0).getRegUsuario());
					usuarioRPTA.setNombre(lstHstSolic.get(0).getNomUsuario());
				}
			}
			
		}
		
		return usuarioRPTA;
	}

	public void obtenerDictamen(ValueChangeEvent e) 
	{
		

		if (e.getNewValue() != null){
			logger.info("****************** obtenerDictamen ********************** "+ e.getNewValue());
			valorDictamen = e.getNewValue().toString();
			if (e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02))
				descValorDictamen = ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02;
			else if (e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02))
				descValorDictamen = ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02;
			else if (e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02))
				descValorDictamen = ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02;
			else if (e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02))
				descValorDictamen = ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02;
			
		} else {
			
			descValorDictamen = "";
			
		}
		logger.debug("[obtenerDictamen]-Estado:");		
	}
	/* Modificado 24/09/2013 . Metodo que realiza la acción de ReImpresión de SSJJ hacia Oficina
	 * */
    public void ejecutarReimpresion(){
    	if ( this.PERFIL_USUARIO.trim().equals(ConstantesVisado.SSJJ)){
    	 GenericDao<TiivsSolicitud, Object> serviceS = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
    	 solicitudRegistrarT.setFlagReimprimir(ConstantesVisado.ACTIVO_FLAG_REIMPRIMIR);
    	 try {
			this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
			this.registrarHistorial(solicitudRegistrarT);
		} catch (Exception e) {
			e.printStackTrace();
		}
       	 Utilitarios.mensajeInfo("INFO",	"Se Ejecuto la Reimpresión correctamente para oficina");
    	}
		
    }
	public void dictaminarSolicitud() 
	{
		boolean actualizarBandeja=false;
		
		logger.info("********************** dictaminarSolicitud *********************************** ");
		logger.info("**[valorDictamen]: " + valorDictamen);
		try {

			GenericDao<TiivsSolicitud, Object> serviceS = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			
			if (this.PERFIL_USUARIO.trim().equals(ConstantesVisado.ABOGADO)	|| this.PERFIL_USUARIO.trim().equals(ConstantesVisado.SSJJ)) 
			{
				if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) 
				{
					  if(solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)
							  && PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
	                    	 solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
	                    	 solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
	                    	 this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
	                    	 this.registrarHistorial(solicitudRegistrarT);
	                    	 actualizarBandeja=true;
	                    	 Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");
						} else if(solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)
								  && PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
							 solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
	                    	 solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
	                    	 this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
	                    	 this.registrarHistorial(solicitudRegistrarT);
	                    	 actualizarBandeja=true;
	                    	 Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");
						} else{
							if(this.agregarNiveles(solicitudRegistrarT)){								
								this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
								this.registrarHistorial(solicitudRegistrarT);
								actualizarBandeja=true;
								Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");
							}else{
								logger.info("No se dictamino la solicitud, verificar los rangos del mantenimiento de Niveles");
							}
						}

			

				} else if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)) {

					this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
					this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
					solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					actualizarBandeja=true;
					bMostrarCartaRechazo=true;
					Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");

				} else if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
					if(solicitudRegistrarT.getTiivsEstudio()!=null){
						if(solicitudRegistrarT.getTiivsEstudio().getCodEstudio()==null){
							solicitudRegistrarT.setTiivsEstudio(null);
						}
					}
                    if(solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)
                    		          && PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
                    	 this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02);
                    	 this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));                    	 
                    	 this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
                    	 this.registrarHistorial(solicitudRegistrarT);
                    	 actualizarBandeja=true;
                    	 Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");
                    	 
					} else if(solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)
          		          && PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
						 this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02);
                    	 this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));                    	
                    	 this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
                    	 this.registrarHistorial(solicitudRegistrarT);
                    	 actualizarBandeja=true;
                    	 Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");
					} else {
						if(this.agregarNiveles(solicitudRegistrarT)){							
							this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
							this.registrarHistorial(solicitudRegistrarT);
							actualizarBandeja=true;
							Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");
						}else{
							logger.info("No se dictamino la solicitud, verificar los rangos del mantenimiento de Niveles");
						}
					}
					

				} else if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)) {

					this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02);
					this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
					
					logger.debug("this.solicitudRegistrarT.getTiivsEstudio() "+this.solicitudRegistrarT.getTiivsEstudio().toString());
					try {
						if(this.solicitudRegistrarT.getTiivsEstudio().getCodEstudio()==null){
							this.solicitudRegistrarT.setTiivsEstudio(null);
						}
						solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
					} catch (Exception e) {
						e.printStackTrace();
					}
					this.registrarHistorial(solicitudRegistrarT);
					actualizarBandeja=true;
					bMostrarCartaImprocedente=true;
					Utilitarios.mensajeInfo("INFO",	"Se dictaminó correctamente la solicitud");

				}
				
//				if (this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
//					bSeccionCartaAtencion = true;
//					bSeccionComentario = false;
//				} else if (this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
//					if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
//						this.bMostrarCartaRechazo = true;
//						this.bMostrarCartaRevision = true;
//						this.bMostrarCartaAtencion = true;
//						this.bSeccionComentario = false;
//					}
//				}
				
				//actualizar secciones según estado/rol
				this.actualizarEstadoReservadoSolicitud();						

				this.obtenerHistorialSolicitud();
				
				if (actualizarBandeja)
				{
					this.seguimientoMB.busquedaSolicitudxCodigo(solicitudRegistrarT.getCodSoli());
				}

			}

		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al dictaminar solicitud:",e);
			e.printStackTrace();
		}
	}

	 public List<String> ObtenerNivelesXMoneda(TiivsSolicitud solicitud) throws Exception {
				logger.info("****************** MONEDA  ******************** " + solicitud.getMoneda());
				GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				List<String> lstCodNivel = new ArrayList<String>();
				
	            Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
	            		 filtro.add(Restrictions.eq("moneda",solicitud.getMoneda().trim()));
	            		 filtro.add(Restrictions.eq("estado", Integer.valueOf(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO)));
	            		 filtro.addOrder(Order.asc("codNiv"));
	            		 
				List<TiivsNivel> lstNiveles = service.buscarDinamico(filtro);
				 for (TiivsNivel z : lstNiveles) {
					 logger.info("************** NIVEL   : " +z.getCodNiv() + " rango inicio " +z.getRangoInicio() + "rango fin" +z.getRangoFin());
				    }
				logger.info("************** lstNiveles  : " +lstNiveles.size());
				logger.info("************** solicitud.getImporte(): " +solicitud.getImporte());
				logger.info("************** lstNiveles.get(0).getRangoInicio() : " +lstNiveles.get(0).getRangoInicio());

				if (solicitud.getImporte() >= lstNiveles.get(0).getRangoInicio()) 
				{
					logger.info("Paso Nivel " + lstNiveles.get(0).getDesNiv());
					logger.debug("a"+ lstNiveles.get(0).getDesNiv());
					logger.debug("this.solicitudRegistrarT.getImporte() !"+ this.solicitudRegistrarT.getImporte());
					logger.debug("lstNiveles.get(lstNiveles.size() - 1).getRangoFin() !"+ lstNiveles.get(lstNiveles.size() - 1).getRangoFin());
					if (this.solicitudRegistrarT.getImporte() > lstNiveles.get(lstNiveles.size() - 1).getRangoFin()) 
					{    logger.info("DATA INCONCISTENTE, EL RANGO FINAL DE TODOS LOS NIVELES , NO DEBE SER MENOR AL RANGO DE INICIO.");
						 lstCodNivel=null;
					} else {
						for (TiivsNivel x : lstNiveles) 
						{
							if (solicitud.getImporte() >= x.getRangoInicio()) 
							{
								logger.info("NIVEL  " + x.getDesNiv());
								lstCodNivel.add(x.getCodNiv());
							}
						}
					}
					}else{
					logger.info("El importe no  supero el rango de inicio . " );
					//lstCodNivel.add("SPRI");
				}

		return lstCodNivel;

	 }
	
	@SuppressWarnings({ "unchecked", "unused" })
	public boolean agregarNiveles(TiivsSolicitud solicitud) throws Exception 
	{ boolean retorno=true;
		logger.info("*********************************** agregarNiveles ********************************************");
		List<String> lstCodNivel = new ArrayList<String>();
		lstCodNivel=ObtenerNivelesXMoneda(solicitud);
				
		if(lstCodNivel==null){
			String mensaje = "DATA INCONCISTENTE, EL RANGO FINAL DE TODOS LOS NIVELES , NO DEBE SER MENOR AL RANGO DE INICIO.";
			Utilitarios.mensajeInfo("INFO", mensaje);
			retorno=false;
		}
		else if (lstCodNivel.size() > 0) {
			logger.info(" ******************** Tamanio de la lista de Niveles *************** para probar: " + lstCodNivel.size());
			/** SI LA SOLICITUD SOPERA ALGUN NIVEL, ENTONCES PASA A ESTADO EN
			 VERIFICACION A, SI NO A ACEPTADO*/
			logger.info(" ******  LA SOLICITUD SUPERO ALGUN NIVEL, ENTONCES PASA A ESTADO EN  VERIFICACION A, SI NO A ACEPTADO");
			if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02);
				solicitud.setFechaEstado(new Timestamp(new Date().getTime()));
			} else if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02);
				solicitud.setFechaEstado(new Timestamp(new Date().getTime()));
			}
			GenericDao<TiivsSolicitudNivel, Object> serviceSolicitud = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit
					                                                    .getApplicationContext().getBean("genericoDao");
			TiivsSolicitudNivel soliNivel = null;
			logger.info("Calendar.DATE " + Calendar.DATE);
			for (String codNivel : lstCodNivel) {
				soliNivel = new TiivsSolicitudNivel();
				//soliNivel.setTiivsSolicitud(this.solicitudRegistrarT);
				soliNivel.setCodNiv(codNivel);
				soliNivel.setEstadoSolicitud(solicitud.getEstado());
				soliNivel.setTiivsSolicitud(solicitud);
				soliNivel.setEstadoNivel(ConstantesVisado.ESTADOS.ESTADO_COD_Pendiente_T09);
				//soliNivel.setEstadoNivel(null);
				//soliNivel.setUsuarioRegistro(usuario.getUID());
				//soliNivel.setFechaRegistro(new Timestamp(Calendar.DATE));
				
				logger.info("************************************************************");
				logger.info("Datos del nivel a insertar: ");
				logger.info("Cod Soli: " + soliNivel.getTiivsSolicitud().getCodSoli());
				logger.info("Cod Nivel: " + soliNivel.getCodNiv());
				logger.info("Estado Solicitud: " + soliNivel.getEstadoSolicitud());
				logger.info("Estado Nivel: " + soliNivel.getEstadoNivel());
				//logger.info("Usuario registro: " + soliNivel.getUsuarioRegistro());
				//logger.info("Fecha registro: " + soliNivel.getFechaRegistro());
				logger.info("************************************************************");
				
				serviceSolicitud.insertar(soliNivel);
			}
			retorno=true;
		} else if (lstCodNivel.size() == 0) {
			if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
				solicitud.setFechaEstado(new Timestamp(new Date().getTime()));
			} else if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02);
				solicitud.setFechaEstado(new Timestamp(new Date().getTime()));
			}
			retorno=true;
		}
		return retorno;
	}

	public void registrarHistorial(TiivsSolicitud solicitud) throws Exception {
		SolicitudDao<String, Object> serviceMaxMovi = (SolicitudDao<String, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		String numeroMovimiento = serviceMaxMovi.obtenerMaximoMovimiento(solicitud.getCodSoli());

		int num = 0;
		if (!numeroMovimiento.equals("")) {
			num = Integer.parseInt(numeroMovimiento) + 1;
		} else {
			num = 1;
		}
		numeroMovimiento = num + "";
		TiivsHistSolicitud objHistorial = new TiivsHistSolicitud();
		objHistorial.setId(new TiivsHistSolicitudId(solicitud.getCodSoli(),
				numeroMovimiento));
		objHistorial.setEstado(solicitud.getEstado());
		objHistorial.setNomUsuario(usuario.getNombre());
		objHistorial.setObs(solicitud.getObs());
		objHistorial.setFecha(new Timestamp(new Date().getTime()));
		objHistorial.setRegUsuario(usuario.getUID());
		if(PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)){
		objHistorial.setRegAbogado(usuario.getUID());
		}
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		serviceHistorialSolicitud.insertar(objHistorial);
	}

	public void actualizarEstadoEjecutadoSolicitud() throws Exception 
	{
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");

		if ((PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) || PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
				&& (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02) 
				|| this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02))) 
		{
			logger.info("*********************** actualizarEstadoEjecutadoSolicitud **************************");

			this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02);
			this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
			this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_EJECUTADO_T02);

			GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			service.modificar(solicitudRegistrarT);

			this.registrarHistorial(solicitudRegistrarT);
			this.obtenerHistorialSolicitud();
			this.seguimientoMB.busquedaSolicitudes();
			
			this.actualizarEstadoReservadoSolicitud();//
		}
		
		/*Modificado 24/09/2013. Cuando la oficina vuelve a Ejecutar la Carta, se desactiva le flag de Reimprimir
		  Hasta que SSJJ vuelva a ejecutar la opcion de ReImprimir*/
		if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)
				&& (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02))){
		this.solicitudRegistrarT.setFlagReimprimir(ConstantesVisado.DESACTIVO_FLAG_REIMPRIMIR);
		
		
		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		service.modificar(solicitudRegistrarT);

		this.registrarHistorial(solicitudRegistrarT);
		this.obtenerHistorialSolicitud();
		this.seguimientoMB.busquedaSolicitudes();
		
		this.actualizarEstadoReservadoSolicitud(); 
		}
	}

	public void actualizarEstadoEnRevisionSolicitud() throws Exception {
		logger.info("*********************** actualizarEstadoEnRevisionSolicitud **************************");

		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02);
		this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
		this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_EN_REVISION_T02);

		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		service.modificar(solicitudRegistrarT);

		this.registrarHistorial(solicitudRegistrarT);
		this.obtenerHistorialSolicitud();
		this.seguimientoMB.busquedaSolicitudxCodigo(solicitudRegistrarT.getCodSoli());
		
		Utilitarios.mensajeInfo("INFO",	"Se generó revisión correctamente");
		
		this.actualizarEstadoReservadoSolicitud();
	}

	public void actualizarEstadoVencidoSolicitud(TiivsSolicitud solicitud) throws Exception {
		logger.info("*********************** actualizarEstadoVencidoSolicitud **************************");

		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02);
		this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
		this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_VENCIDO_T02);

		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		service.modificar(solicitud);

		this.registrarHistorial(solicitud);
		this.obtenerHistorialSolicitud();
		this.seguimientoMB.busquedaSolicitudes();
	}

	public void verAgrupacion() {
		logger.info("********************** verAgrupacion *********************************** ");

		logger.info("this.objAgrupacionSimpleDtoCapturado  " + this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.objAgrupacionSimpleDtoCapturado  " + this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		logger.info("this.objAgrupacionSimpleDtoCapturado  " + this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
	}
	
	public void consultarVoucherHost(){
		logger.info("********************** consultarVoucherHost *********************************** ");
		if(solicitudRegistrarT!=null){
			GenericDao<TiivsHostVoucher, Object> service = 
										(GenericDao<TiivsHostVoucher, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			
			Busqueda filtroHost = Busqueda.forClass(TiivsHostVoucher.class);
			filtroHost.add(Restrictions.eq("nroVoucher", solicitudRegistrarT.getNroVoucher()));
			
			try {
				List<TiivsHostVoucher> vouchers = service.buscarDinamico(filtroHost);
				if(vouchers.size()>0){
					objVoucher = vouchers.get(0);
				}
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"el Voucher de Host:" + e);
			}
		}
	}

	public void obtenerHistorialSolicitud() {
		logger.debug("=== obtenerHistorialSolicitud() ===");
		logger.info("[Historial]-CodigoSolicitud:" + solicitudRegistrarT.getCodSoli());

		String sCodSolicitud = solicitudRegistrarT.getCodSoli();
		try {
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
			filtroHist.add(Restrictions.eq("id.codSoli", sCodSolicitud));
			filtroHist.addOrder(Order.desc("fecha"));

			List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
			lstHist = histDAO.buscarDinamico(filtroHist);

			if (lstHist != null && lstHist.size() > 0) {
				logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"historial-solicitud es:"+lstHist.size());
				lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();

				for (TiivsHistSolicitud h : lstHist) {
					SeguimientoDTO seg = new SeguimientoDTO();

					String estado = h.getEstado();
					if (estado != null) {
						seg.setEstado(buscarEstadoxCodigo(estado.trim()));
					}

					String desEstadoNivel = "";
					String desRolNivel = "";
					Integer iCodNivel = 0;
					String descripcionNivel = "";

					if (h.getNivel() != null) {
						if (h.getNivelRol() != null && h.getNivelRol().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE)) {
							desRolNivel = "Responsable";
						}
						if (h.getNivelRol() != null && h.getNivelRol().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO)) {
							desRolNivel = "Delegado";
						}
						if (h.getNivelEstado() != null && h.getNivelEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_Desaprobado_T09)) {
							desEstadoNivel = ConstantesVisado.ESTADOS.ESTADO_Desaprobado_T09;
						}
						if (h.getNivelEstado() != null && h.getNivelEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_Aprobado_T09)) {
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
					seg.setCodSoli(h.getId().getCodSoli());
					seg.setMovimiento(h.getId().getMovimiento());
					lstSeguimientoDTO.add(seg);
				}
			}
		} catch (Exception exp) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"el Historial-Solicitud:" +exp);
		}

	}

	public String buscarEstadoxCodigo(String codigo) 
	{
		/*int i = 0;
		String res = "";
		
		for (; i < combosMB.getLstEstado().size(); i++) 
		{
			if (combosMB.getLstEstado().get(i).getCodEstado().equalsIgnoreCase(codigo)) 
			{
				res = combosMB.getLstEstado().get(i).getDescripcion();
				break;
			}
		}
		
		return res;*/
		
		String resultado="";
		List<TiivsMultitabla> tmpLista = new ArrayList<TiivsMultitabla>();
		GenericDao<TiivsMultitabla, Object> busDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		filtro.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		filtro.add(Restrictions.eq("id.codElem", codigo));
		
		try {
			tmpLista = busDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar codigos de estados");
		}
		
		if (tmpLista.size()>0)
		{
			resultado = tmpLista.get(0).getValor1().toUpperCase();
		}
		
		return resultado;
	}

	/**
	 * Metodo encargado de obtener los anexos (archivos PDF) asociados a la
	 * solicitud de visado. Verifica la existencia en la ubicación temporal, si 
	 * este no es el caso se conecta via FTP y devuelve los .PDFs requeridos.
	 * @return iRet Puede tomar el valor true/false
	 * **/
	public boolean descargarAnexosFileServer() {
		logger.debug("====== INICIANDO descargarAnexosFileServer() ======");
		boolean iRet = true;
		String sUbicacionTemporal;
		String sUbicacionFinal;
				
		//Si la solicitud tiene no esta en estado REGISTRADO (Vista de solicitud) se copian la carpeta de la aplicacion;
		
		if(this.getSolicitudRegistrarT().getEstado().equalsIgnoreCase(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02)){
			sUbicacionTemporal = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER) + File.separator + ConstantesVisado.FILES + File.separator;
		} else {
			sUbicacionTemporal = Utilitarios.getProjectPath() + File.separator + ConstantesVisado.FILES + File.separator;
		}
		
		sUbicacionFinal = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER) + File.separator;
		
		logger.debug("[Ubicacion Temporal]:" + sUbicacionTemporal);
		if(lstAnexoSolicitud!=null){
			logger.info("[FileServer]-Cantidad Anexos:" + this.lstAnexoSolicitud.size());	
		}
		
		File srcFile = null;
		File fichTemp = null;
		File fDirectory =null;
		
		for (TiivsAnexoSolicitud a : this.lstAnexoSolicitud)
		{
			logger.debug("---------------------- Recuperando archivo ---------------------------------------------");
			fichTemp = new File(sUbicacionTemporal	+ a.getAliasTemporal());
			if (!fichTemp.exists()) 
			{
				logger.debug("Archivo no existe, se descargara:" + a.getAliasArchivo());
				
				boolean bSaved = false;
				try {
					fDirectory = new File(sUbicacionTemporal);
					if (!fDirectory.exists()) {
						fDirectory.mkdirs();
					}

					//Crea archivo temporal
					String fileName = a.getId().getCodSoli() + "_" + a.getAliasArchivo();
					logger.debug("[Anexo]-fileName:"+fileName);
					String extension = fileName.substring(fileName.lastIndexOf("."));
					String sNombreTemporal = "";
					String sNombrePrefijo = a.getAliasArchivo().substring(0, a.getAliasArchivo().lastIndexOf(".")) + "_";
					fichTemp = File.createTempFile(sNombrePrefijo, extension, new File(sUbicacionTemporal));
					sNombreTemporal = fichTemp.getName().substring(1 + fichTemp.getName().lastIndexOf(File.separator));
					logger.debug("[Anexo]-sNombreTemporal: " + sNombreTemporal);

					srcFile = new File(sUbicacionFinal + a.getId().getCodSoli() +  "_" + a.getAliasArchivo());
					
					//Copia los archivos del File server a la carpeta temporal
					FileUtils.copyFile(srcFile, fichTemp);
					
					if(fichTemp!=null && fichTemp.exists()){
						a.setAliasTemporal(sNombreTemporal);
					} else {
						a.setAliasTemporal("");
						logger.debug("Archivo no existe en el File Server");
					}
					
					bSaved = true;
					
//					PDFViewerMB pdfViewerMB = new PDFViewerMB();
//					if (pdfViewerMB.descargarArchivo(fichTemp.getAbsolutePath(), fileName)) {
//						a.setAliasTemporal(sNombreTemporal);
//					} else {
//						a.setAliasTemporal("");
//						logger.debug("Archivo no existe en el File Server");
//					}
//					bSaved = true;					

				} catch (IOException e) {
					logger.error("Error al descargar archivo: "		+ a.getAliasArchivo(),e);
					bSaved = false;
				} catch (Exception e){
					logger.error(e);
					bSaved = false;
				}
				finally {
					fichTemp.deleteOnExit(); 												
				}
				if (bSaved) {
					GenericDao<TiivsAnexoSolicitud, Object> anexoDAO = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit
							.getApplicationContext().getBean("genericoDao");
					try {
						logger.debug("[MODIFICAR]-a.getAliasTemporal(): "+a.getAliasTemporal());
						anexoDAO.modificar(a);
					} catch (Exception ex) {
						logger.error("No se actualizara el anexo "	+ ex.getMessage());
					}
					iRet = iRet && true;
				} else {
					logger.error("Error no se actualizara anexo");
					iRet = iRet && false;
				}
			} else {
				logger.info("Archivo ya existe en ubicacion temporal ");
			}
		}
		logger.info("====== SALIENDO de descargarAnexosFileServer() ======");
		return iRet;
	}

	public void seterComentario() {
		logger.info("************************** Setear Comentario **************************");
		logger.info("Comentario : " + this.solicitudRegistrarT.getObs());
	}

	private void cargarDocumentos() {
		GenericDao<TiivsDocumento, Object> documentoDAO = (GenericDao<TiivsDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroDocumento = Busqueda.forClass(TiivsDocumento.class);
		try {
			lstTiivsDocumentos = documentoDAO.buscarDinamico(filtroDocumento);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al cargarDocumentos():",e);
		}
	}

	private String obtenerDescripcionDocumento(String codDoc) 
	{
		if (codDoc.contains(ConstantesVisado.PREFIJO_OTROS)) {
			return ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS;
		}
		for (TiivsDocumento doc : lstTiivsDocumentos) {
			if (doc.getCodDocumento().equalsIgnoreCase(codDoc.trim())) {
				return doc.getDescripcion();
			}
		}
		return "";
	}

	public void registrarEvaluacionNivel() {
		logger.info("this.solicitudRegistrarT.getObs():" + this.solicitudRegistrarT.getObs());
		evaluacionNivelesMB = new EvaluacionNivelesMB(solicitudRegistrarT);
		evaluacionNivelesMB.registrarEvaluacionNivel(sCodigoEstadoNivel);
		try {
			this.actualizarEstadoReservadoSolicitud();
			this.obtenerHistorialSolicitud();
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION,e);
		}
	}
			
	public void obtenerDictamenNivel(ValueChangeEvent e) 
	{
		logger.info("****************** obtenerDictamen Nivel ********************** "
				+ e.getNewValue());

		if (e.getNewValue() != null){
			
			valorDictamen = e.getNewValue().toString();
			if (e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_APROBADO_T09))
				descValorDictamen = ConstantesVisado.ESTADOS.ESTADO_Aprobado_T09;
			else if (e.getNewValue().toString().equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESAPROBADO_T09))
				descValorDictamen = ConstantesVisado.ESTADOS.ESTADO_Desaprobado_T09;						
		} else {			
			descValorDictamen = "";			
		}
	}

	/* Metodos del registro - Refactorizar 04/02/13 */

	public void registrarSolicitudBorrador() {
		sEstadoSolicitud = "BORRADOR";
	}

	public void registrarSolicitudEnviado() {
		sEstadoSolicitud = "ENVIADO";
	}

	private boolean validarRegistroSolicitud() throws Exception {
		boolean retorno = true;
		String mensaje = "";	
		
		logger.info("solicitudRegistrarT.getTiivsOficina1() "+solicitudRegistrarT.getTiivsOficina1().getCodOfi());
		if (solicitudRegistrarT.getTiivsOficina1() == null) {
			mensaje = "Ingrese una Oficina";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		} else if(solicitudRegistrarT.getTiivsOficina1().getCodOfi()==null){
			mensaje = "Ingrese una Oficina";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		} else if (solicitudRegistrarT.getTiivsOficina1().getCodOfi().equals("")) {
			mensaje = "Ingrese una Oficina";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
		
		
		if (solicitudRegistrarT.getTiivsSolicitudAgrupacions().size() == 0) {
			mensaje = "Ingrese la sección Apoderado y Poderdante";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}else{
			Set<TiivsAgrupacionPersona> lstAgrupacionPersona=null;
			int conuntNumAgru=0;
			
			for (TiivsSolicitudAgrupacion a : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
				conuntNumAgru=a.getId().getNumGrupo();
				logger.info("conuntNumAgru ****** : " +conuntNumAgru);
				lstAgrupacionPersona=a.getTiivsAgrupacionPersonas();
				logger.info(" - lstAgrupacionPersona  -: " +lstAgrupacionPersona.size());
				int contPoderdante=0, contApoderado=0;
				for (TiivsAgrupacionPersona xa : lstAgrupacionPersona) {
					if(xa.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					   contPoderdante++;
					}else if(xa.getTipPartic().equals(ConstantesVisado.APODERADO)){
						contApoderado++;
					}else if(xa.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)){
						contApoderado++;
					}
				}
				if(contPoderdante==0||contApoderado==0){
//					retorno= false;
//					Utilitarios.mensajeInfo("INFO", "Ingrese por lo menos un Poderdante y un Apoderado, por cada Combinación");
					break;
					
				}
				logger.info("contPoderdante : " +contPoderdante);
				logger.info("contApoderado : " +contApoderado);
			}
			
		}
		
		if (solicitudRegistrarT.getTiivsTipoSolicitud() == (null)) {
			mensaje = "Seleccione el Tipo de Solicitud ";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}

		logger.info("lstAnexoSolicitud.size()" + this.lstAnexoSolicitud.size());
		if (this.lstAnexoSolicitud.size() == 0) {
			mensaje = "Ingrese los documentos Obligatorios";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
		if (this.lstSolicBancarias.size() == 0) {

			mensaje = "Ingrese al menos una Operación Bancaria";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		/*if (!this.validarNroVoucher()) {
			retorno = false;
		}*/
		return retorno;
	}
	
	private boolean validarEnvioSolicitud() throws Exception {  
		boolean retorno = true;
		String mensaje = "";
		
		logger.info("solicitudRegistrarT.getTiivsOficina1() "+solicitudRegistrarT.getTiivsOficina1().getCodOfi());
		
		//09-10 Validacion ingreso NroVoucher si check "Exonerado PagComis" no es marcado.
		logger.debug("Valida[EnvioSolicitud]-getExoneraComision: " + bFlagComision);
				
		if (!isbFlagComision()) {
			logger.debug("Valida[EnvioSolicitud]-Se valida que se ingrese Nro Voucher.");
			// Validacion de numero de voucher
			if (solicitudRegistrarT.getNroVoucher() == null) {
				mensaje = "Ingrese el Nro Voucher";
				retorno = false;
				Utilitarios.mensajeInfo("INFO", mensaje);
			} else if (solicitudRegistrarT.getNroVoucher().equals("")) {
				mensaje = "Ingrese el Nro Voucher";
				retorno = false;
				Utilitarios.mensajeInfo("INFO", mensaje);
			} else if (solicitudRegistrarT.getNroVoucher().length() < 11) {
				mensaje = "Ingrese Nro Voucher correcto de 11 digitos";
				retorno = false;
				Utilitarios.mensajeInfo("INFO", mensaje);
			} else {
				logger.info("Valida[EnvioSolicitud]-NroVoucher:"
						+ solicitudRegistrarT.getNroVoucher());
				retorno = this.validarNroVoucher();
			}
		} else {
			logger.debug("Valida[EnvioSolicitud]-No se valida el ingreso de Nro Voucher porque el check no esta marcado.");
		}
		
		//Validacion de oficina
		if (solicitudRegistrarT.getTiivsOficina1() == null) {
			mensaje = "Ingrese una Oficina";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		} else if(solicitudRegistrarT.getTiivsOficina1().getCodOfi()==null){
			mensaje = "Ingrese una Oficina";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		} else if (solicitudRegistrarT.getTiivsOficina1().getCodOfi().equals("")) {
			mensaje = "Ingrese una Oficina";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
		//Validacion de numero de voucher
		/*if (solicitudRegistrarT.getNroVoucher()==null){
			mensaje = "Ingrese el Nro Voucher";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
	      }
		else if (solicitudRegistrarT.getNroVoucher().equals("")){
				mensaje = "Ingrese el Nro Voucher";
				retorno = false;
				Utilitarios.mensajeInfo("INFO", mensaje);
		 }else if (solicitudRegistrarT.getNroVoucher().length() < 11) {
				mensaje = "Ingrese Nro Voucher correcto de 11 digitos";
				retorno = false;
				Utilitarios.mensajeInfo("INFO", mensaje);
			}
		 else {
			retorno =this.validarNroVoucher();
		    }*/
				 
		if (solicitudRegistrarT.getTiivsSolicitudAgrupacions().size() == 0) {
			mensaje = "Ingrese la sección Apoderado y Poderdante";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}else{
			Set<TiivsAgrupacionPersona> lstAgrupacionPersona=null;
			int conuntNumAgru=0;
			
			for (TiivsSolicitudAgrupacion a : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
				conuntNumAgru=a.getId().getNumGrupo();
				logger.info("conuntNumAgru : " +conuntNumAgru);
				lstAgrupacionPersona=a.getTiivsAgrupacionPersonas();
				logger.info("lstAgrupacionPersona : " +lstAgrupacionPersona.size());
				int contPoderdante=0, contApoderado=0;
				for (TiivsAgrupacionPersona xa : lstAgrupacionPersona) {
					if(xa.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					   contPoderdante++;
					
					}else if(xa.getTipPartic().equals(ConstantesVisado.APODERADO)){
						contApoderado++;
						
					}else if(xa.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)){
						contApoderado++;
					}
				}
				if(contPoderdante==0||contApoderado==0){
					retorno= false;
					Utilitarios.mensajeInfo("INFO", "Ingrese por lo menos un Poderdante y un Apoderado, por cada Combinación");
					break;
					
				}
				logger.info("contPoderdante : " +contPoderdante);
				logger.info("contApoderado : " +contApoderado);
			}
			
		}
		if (solicitudRegistrarT.getTiivsTipoSolicitud() == (null)) {
			mensaje = "Seleccione el Tipo de Solicitud ";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
		if (this.lstAnexoSolicitud.size() == 0) {
			mensaje = "Ingrese los documentos Obligatorios";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
		for(TiivsTipoSolicDocumento docRequerido :  lstTipoSolicitudDocumentos){
			if(docRequerido.getObligatorio().equals("1")){
				mensaje = "Ingrese los documentos Obligatorios";
				retorno = false;
				Utilitarios.mensajeInfo("INFO", mensaje);
				break;
			}
		}

		if (this.lstSolicBancarias.size() == 0) {

			mensaje = "Ingrese al menos una Operación Bancaria";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
			
		
		return retorno;
	}

	@SuppressWarnings({ "unchecked", "null" })
	public boolean validarNroVoucher() throws Exception {
		boolean booleano = true;
		if (!this.sEstadoSolicitud.equals("BORRADOR")) {
			String mensaje = "Ingrese un Nro de Vourcher no registrado ";
			Busqueda filtroNroVoucher = Busqueda.forClass(TiivsSolicitud.class);
			filtroNroVoucher.add(Restrictions.not(Restrictions.eq("estado", ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02)));
			GenericDao<TiivsSolicitud, String> serviceNroVoucher = (GenericDao<TiivsSolicitud, String>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			List<TiivsSolicitud> lstSolicitud = new ArrayList<TiivsSolicitud>();
			lstSolicitud = serviceNroVoucher.buscarDinamico(filtroNroVoucher);
			if (lstSolicitud != null) {
				for (TiivsSolicitud a : lstSolicitud) {
					if (a != null || !a.equals("")) {
						if (a.getNroVoucher() != (null)) {
							if (a.getNroVoucher().equals(this.solicitudRegistrarT.getNroVoucher())) {
								booleano = false;
								Utilitarios.mensajeInfo("INFO", mensaje);
								break;
							}
						}
					}
				}
			}
		}
		return booleano;
	}

	/** Enviar la solicitud a SSJJ */
	public void enviarSolicitudSSJJ() 
	{
		Timestamp time = new Timestamp(objRegistroUtilesMB.obtenerFechaRespuesta().getTime());
		logger.info("time : " + time);
		String sCodigoEstudio = objRegistroUtilesMB.obtenerEstudioMenorCarga();
		logger.info(" sCodigoEstudio +  " + sCodigoEstudio);
		
		for (TiivsEstudio x : combosMB.getLstEstudio()) 
		{
			if (x.getCodEstudio().equals(sCodigoEstudio)) 
			{
				this.solicitudRegistrarT.setTiivsEstudio(x);
			}
		}
		
		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02);
		this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
		this.solicitudRegistrarT.setFechaRespuesta(time);
		this.solicitudRegistrarT.setFechaEnvio(new Timestamp(new Date().getTime()));
				
	}

	public boolean cargarArchivosFTP() {

		logger.info("************cargarArchivosFTP()*¨**************");

		boolean exito = true;
				
		String sUbicacionTemporal = Utilitarios
				.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
				+ File.separator + ConstantesVisado.FILES + File.separator;
		
		logger.info("ubicacion temporal " + sUbicacionTemporal);

		for (TiivsAnexoSolicitud anexo : lstAnexoSolicitud) {

			String ruta = pdfViewerMB.cargarUnicoPDF(anexo.getId().getCodSoli() + "_" +  anexo.getAliasArchivo(),
					sUbicacionTemporal + anexo.getAliasTemporal());
			if (ruta.compareTo("") != 0) {
				logger.debug("subio: " + anexo.getAliasTemporal());
				exito = exito && true;
			} else {
				logger.debug("no subio: " + anexo.getAliasTemporal());
				exito = exito && false;
			}
		}
		return exito;
	}
	
	
	/**
	 * Metodo que se encarga de cargar los archivos .PDF hacia el FileServer
	 * @return boolean true/false Indica el exito de la operacion
	 * */
	public boolean cargarArchivosFileServer(){			
		logger.info("========= cargarArchivosFileServer() ========");		
		boolean exito = true;
		String ubicacionFinal = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)  + File.separator;		
		String sUbicacionTemporal = ubicacionFinal + ConstantesVisado.FILES + File.separator;
		logger.info("[CARGAR-FILESERVER]-Ubicacion final "+ ubicacionFinal);
		logger.info("[CARGAR-FILESERVER]-Ubicacion temporal "+ sUbicacionTemporal);	
		if(lstAnexoSolicitud!=null){
			logger.debug("[CARGAR-FILESERVER]-lstAnexoSolicitud-size:"+lstAnexoSolicitud.size());
		}
		for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){													
			File srcFile = new File(sUbicacionTemporal + anexo.getAliasTemporal());						
			File destFile = new File(ubicacionFinal + anexo.getId().getCodSoli() + "_" + anexo.getAliasArchivo());
			try {
				FileUtils.copyFile(srcFile, destFile);
			} catch (IOException e) {
				logger.error("Error al mover el archivo al fileServer", e);
			}
			catch (Exception ex) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al mover archivo al fileServer:" , ex);
			}
			if(!destFile.isFile() && destFile.length()>0){
				exito = false;
			}
		}
		logger.debug("exito:"+exito);
		return exito;		
	}

	public void eliminarArchivosTemporales() {
		logger.info("************eliminarArchivosTemporales()*¨**************");
		logger.info("Archivos a eliminar:" + aliasFilesToDelete.size());
		File fileToDelete = null;

		String sUbicacionTemporal = Utilitarios
				.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
				+ File.separator + ConstantesVisado.FILES + File.separator;
		
		for (String sfile : aliasFilesToDelete) {
			logger.debug("borrar archivo: " + sUbicacionTemporal
					+ sfile);
			fileToDelete = new File(sUbicacionTemporal + sfile);
			if (fileToDelete.delete()) {
				logger.debug("borro archivo temporal :" + sfile);
			} else {
				logger.debug("no borro archivo temporal :" + sfile);
			}
		}

		fileToDelete = null;
		aliasFilesToDelete = new ArrayList<String>();
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Transactional (rollbackFor=Exception.class)
	public void registrarSolicitud() 
	{
		String mensaje = "";
		redirect = "";
		boolean actualizarBandeja=false;
		
		logger.info("*********************** registrarSolicitud ************************");
		
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		TiivsPersona objPersonaRetorno = new TiivsPersona();
		TiivsAgrupacionPersona objAgruPer = new TiivsAgrupacionPersona();
		
		try {
			logger.info("this.solicitudRegistrarT.importe : " + this.solicitudRegistrarT.getMoneda());
			this.solicitudRegistrarT.setFecha(new Date());
			this.solicitudRegistrarT.setEstado(this.solicitudRegistrarT.getEstado().trim());
			this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));

			logger.info("usuario.getUID() " + usuario.getUID());
			this.solicitudRegistrarT.setRegUsuario(usuario.getUID());
			this.solicitudRegistrarT.setNomUsuario(usuario.getNombre());
			logger.info("tiivsOficina1.codOfi ::::::: "	+ this.solicitudRegistrarT.getTiivsOficina1().getCodOfi());
			
			if (flagMostrarACOficina)
			{
				logger.info("tiivsOficina1.codOfi ::::::: "+ oficina.getCodOfi());
				for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
					if (tiivsOficina1.getCodOfi().equals(oficina.getCodOfi())) {
						this.solicitudRegistrarT.setTiivsOficina1(oficina);
						break;
					} 
				}
			}
			else
			{
				logger.info("tiivsOficina1.codOfi ::::::: "+ this.solicitudRegistrarT.getTiivsOficina1().getCodOfi());
				for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
					if (tiivsOficina1.getCodOfi().equals(this.solicitudRegistrarT.getTiivsOficina1().getCodOfi())) {
						this.solicitudRegistrarT.setTiivsOficina1(tiivsOficina1);
						break;
					} 
				}
			}

			
			this.limpiarAgrupacionesVacias();
			
			logger.info("solicitudRegistrarT.getTiivsSolicitudAgrupacions() : " + solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
			
			for(TiivsSolicitudAgrupacion agrusol : solicitudRegistrarT.getTiivsSolicitudAgrupacions()){
				logger.info("agrusol.getTiivsAgrupacionPersonas() " + agrusol.getId().getCodSoli() + "_" + agrusol.getId().getNumGrupo() + " :" + agrusol.getTiivsAgrupacionPersonas().size());
			}
		
			
			logger.info("this.sEstadoSolicitud " + this.sEstadoSolicitud);
			
			boolean esValido = false;
			if(!this.sEstadoSolicitud.equals("BORRADOR")){ 	//Validacion para envio de solicitud a SSJJ
				esValido = this.validarEnvioSolicitud();
			} else { 	//Validacion para registro de solicitud (Borrador)
				esValido = this.validarRegistroSolicitud();
			}
			
			if (esValido)
			{
				if (!this.sEstadoSolicitud.equals("BORRADOR"))  //ssjj
				{
					/*** REALIZAR VALIDACION DE COBRO DE COMISIONES ***/
					if(validarCobroComisiones()){
						logger.info("[REGISTR_SOLIC]-Solicitud con validacion de cobro de comisiones restrictiva");
						return;
					}
					this.enviarSolicitudSSJJ();
					logger.info("ESTUDIOOOO : " +solicitudRegistrarT.getTiivsEstudio().getCodEstudio());
					actualizarBandeja=true;
				}else{
					//borrador
					this.solicitudRegistrarT.setTiivsEstudio(null);
				}
								
				Set<TiivsSolicitudAgrupacion> listaTMP = this.solicitudRegistrarT.getTiivsSolicitudAgrupacions();
				logger.info("listaTMP listaTMP "+listaTMP.size());
																						
				//Guardar datos de solicitud
				TiivsSolicitud objResultado = service.guardarModificar(this.solicitudRegistrarT);	
				
				//Registrar Historial de Solicitud
				if (!this.sEstadoSolicitud.equals("BORRADOR"))  //Si se envia solicitud
				{
					this.registrarHistorial(solicitudRegistrarT);
				}
				
								  
				// Carga ficheros al FTP
//				boolean bRet = cargarArchivosFTP();
//				logger.info("Resultado de carga de archivos al FTP:" + bRet);
				 //Carga ficheros al File Server
				boolean bRet = cargarArchivosFileServer();
				logger.info("[REGISTR_SOLIC]-Resultado de carga de archivos al FileServer:" + bRet);
				// Elimina archivos temporales
				eliminarArchivosTemporales();
                /*** look */
			
				//Eliminar tabla de anexos anteriores menos el listado: lstAnexoSolicitud
				eliminaAnexosAnterioresMenos(solicitudRegistrarT,lstAnexoSolicitud);
				
				logger.info("Numero de anexos a insertar: " + this.lstAnexoSolicitud.size());
				for (TiivsAnexoSolicitud n : this.lstAnexoSolicitud) 
				{				
					logger.info("insertar anexo: " + n.getId().getCodSoli() + "_" + n.getId().getCodDoc());
					serviceAnexos.insertarMerge(n);
				}	
				

				 logger.info("Tamanio Lista original que se trajo de base : "+lstSolicBancariasCopia.size());
				 logger.info("Tamanio Lista Temporal a Eliminar  : "+this.listaTemporalEliminarOperacionesBancarias.size());
				 logger.info("Tamanio Lista que se tiene en memoria : "+lstSolicBancarias.size());
				 
				 /** Validar si realmente se ah hecho algun cambio para ir a bd, si no no hace nada */
				 if(!lstSolicBancariasCopia.equals(lstSolicBancarias)){
					 logger.info(" Las listas de las Operaciones Bancarias no son iguales, iran a base");
				 /** Eliminar la lista de Operaciones Bancarias */ 
				for (TiivsSolicitudOperban b : this.listaTemporalEliminarOperacionesBancarias) {
					serviceSoli.eliminar(b);
				  } 
				/*** Limpiar la lista temporal a Eliminar de las Operaciones Bancarias */
				listaTemporalEliminarOperacionesBancarias=new ArrayList<TiivsSolicitudOperban>();
				/** Insertar, actualizar la lista de Operaciones Bancarias */ 
				for (TiivsSolicitudOperban a : this.lstSolicBancarias) 
				{
					logger.info("a.getId().getCodOperBan() **** " + a.getId().getCodOperBan());
					a.getId().setCodSoli(this.solicitudRegistrarT.getCodSoli());
					logger.info("a.getId().getCodSoli() **** " + a.getId().getCodSoli());
					serviceSoli.insertarMerge(a);
				}
				 }else{
					logger.info(" Las listas de las Operaciones Bancarias son iguales, no ira a base");
				 }
				 
				 /** Fin  Validar si realmente se ah hecho algun cambio para ir a bd, si no no hace nada */
				//depurarTablas();
				
				if (objResultado.getCodSoli() != "" || objResultado != null) 
				{
					if (this.sEstadoSolicitud.equals("BORRADOR")) 
					{
						mesajeConfirmacion = "Se registró correctamente la Solicitud con codigo : " + objResultado.getCodSoli() + " en Borrador";		
						aliasFilesToDelete = new ArrayList<String>();
						actualizarBandeja=true;
						Utilitarios.mensajeInfo("INFO", mesajeConfirmacion);
					} 
					else //Enviar solicitud
					{
						mesajeConfirmacion = "Se envió a SSJJ correctamente la Solicitud con codigo : "+ 
								objResultado.getCodSoli() + "\n" + mesajeValidacionHost;
						actualizarBandeja=true;						
					}
					
					redirect = this.redirectDetalleSolicitud(objResultado.getCodSoli());
					logger.info("redirect:" + redirect);

				} 
				else 
				{
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
				}

				logger.info("objResultado.getCodSoli(); " + objResultado.getCodSoli());
				logger.info("this.solicitudRegistrarT.importe : " + this.solicitudRegistrarT.getImporte());
				
				//Verificado el resultado y haciendo el redirect correcto.
				logger.info("[REGISTR_SOLIC]-sEstadoSolicitud: "+this.sEstadoSolicitud);
				if (objResultado.getCodSoli() != "" || objResultado != null) {
					if (this.sEstadoSolicitud.equals("BORRADOR")) {
						mesajeConfirmacion = "Se registró correctamente la Solicitud con codigo : "+ objResultado.getCodSoli() + " en Borrador";
						actualizarBandeja=true;
					} else {
						mesajeConfirmacion = "Se envió a SSJJ correctamente la Solicitud con codigo : "+ 
											objResultado.getCodSoli() + "\n" + mesajeValidacionHost;
						actualizarBandeja=true;
					}
					logger.debug("[REGISTR_SOLIC]-msjConfirmacion:"+mesajeConfirmacion);
					//redirect = "/faces/paginas/bandejaSeguimiento.xhtml";					
					this.redirect = this.redirectDetalleSolicitud(objResultado.getCodSoli());					
				} else {
					logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar la solicitud.");
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
				}
				/*** Fin look*/
				
				if (actualizarBandeja)
				{
					this.seguimientoMB.busquedaSolicitudxCodigo(objResultado.getCodSoli());
				}
				
			}
			bBooleanPopup=false;
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION,e);
			Utilitarios.mensajeError("ERROR", "Ha ocurrido un error al grabar la Solicitud");
		}catch(Throwable t){
			logger.error("Throwable ::: "+ConstantesVisado.MENSAJE.OCURRE_EXCEPCION,t);
		}				
	}
	
	/** Metodo encargado de leer los valores parametrizados en la multitabla (T18), 
	 * para hacer o no la validacion de Comision.
	 * @return restrictivo Indicador para realizar la validacion booleano: true/false
	 * **/
	private boolean validarCobroComisiones(){
		boolean restrictivo = false;
		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		logger.debug("ExoneraFlagComision():" + solicitudRegistrarT.getExoneraComision());
		//Si no esta exonerado a comision
		/*if(solicitudRegistrarT.getExoneraComision()!=null &&
				(solicitudRegistrarT.getExoneraComision().equalsIgnoreCase("0")
						|| solicitudRegistrarT.getExoneraComision().compareTo(ConstantesVisado.FALSE)==0)){*/
		if(!isbFlagComision()){
			Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
			filtroMultitabla.add(Restrictions.eq("id.codMult",ConstantesVisado.CODIGO_MULTITABLA_VALIDACION_COMISION));
			filtroMultitabla.add(Restrictions.eq("valor2",ConstantesVisado.VALOR2_ESTADO_ACTIVO));
			TiivsMultitabla validacionVoucher = null;
			
			try {
				List<TiivsMultitabla> comisiones = service.buscarDinamico(filtroMultitabla);
				if(comisiones.size()>0){
					validacionVoucher = comisiones.get(0);
				}
				if(validacionVoucher!=null){
					logger.debug("[Parametria]-validacionVoucher.getId().getCodElem():"+validacionVoucher.getId().getCodElem());
					logger.debug("[Parametria]-validacionVoucher-valor1:"+validacionVoucher.getValor1());
					logger.debug("[Parametria]-validacionVoucher-valor2:"+validacionVoucher.getValor2());
					logger.debug("[Parametria]-validacionVoucher-valor3:"+validacionVoucher.getValor3());
					if(validacionVoucher.getId().getCodElem().compareTo(ConstantesVisado.VALIDACION_INACTIVA)==0){
						logger.info("No se validara contra el servicio de Comision, ya que no esta habilitado.");
						restrictivo = false;
					}else{
						logger.debug("Se validara contra el servicio de Comision (Advertencia / Restrictivo)");
						restrictivo = validaComision(validacionVoucher.getId().getCodElem(), validacionVoucher);
					}
				}
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"en validarCobroComisiones(): ",e);
			}	
		}
		return restrictivo;
	}
	
	/**
	 * Metodo encargado de obtener los datos del pago de comision desde Host y realizar 
	 * la validacion respectiva del Monto/Nro Voucher y mostrar el mensaje de 
	 * validación como Advertencia o Restrictiva.
	 * @param tipoValidacion Representa el tipo de validacion a realizar
	 * @param validacionVoucher Valor del multitabla  del tipo {@link TiivsMultitabla}
	 * @return flagValida Indicador de validacion del tipo booleano: true/false
	 * **/
	private boolean validaComision(String tipoValidacion, TiivsMultitabla validacionVoucher){
		boolean flagValida = true;
		BResult resultado = null;
		ObtenerDatosVoucherDUMMY voucherService = new ObtenerDatosVoucherDUMMY();
		TiivsHostVoucher voucherHost = null;
		try {
			resultado = voucherService.obtenerDatosVoucher(solicitudRegistrarT.getNroVoucher());
			//Inserta en tabla VISPOD el voucher obtenido de Host
			if(resultado.getCode()==Integer.parseInt(Constantes.VOUCHER_EXITO)){
				logger.debug("Se debe registrar el NroVoucher que si existe en Host");
				insertarVoucher(resultado);
			}			
			
			logger.debug("[ValidaComision]-tipoValidacion: "+tipoValidacion);
			logger.debug("[ValidaComision]-solicitudRegistrarT.getComision():"+solicitudRegistrarT.getComision());
			
			if(tipoValidacion!=null && tipoValidacion.compareTo(ConstantesVisado.ACTIVA_ADVERTENCIA)==0){
				flagValida = false;
				if(resultado.getObject()!=null){
					voucherHost = (TiivsHostVoucher) resultado.getObject();
					logger.debug("[Advertencia]-ANTES-voucherMontoHost:"+Double.valueOf(voucherHost.getMontoComision()));
					
					if(voucherHost.getMontoComision()!=null &&
							Double.valueOf(voucherHost.getMontoComision()).compareTo(solicitudRegistrarT.getComision())!=0){
						//Utilitarios.mensajeInfo("INFO", validacionVoucher.getValor3());	
						logger.debug("[Advertencia]-voucherMontoHost: "+Double.valueOf(voucherHost.getMontoComision()));
						mesajeValidacionHost = validacionVoucher.getValor3();
						logger.debug("[Advertencia]-mensaje: "+mesajeValidacionHost);
						
					}
					
				}
			}else if (tipoValidacion!=null && tipoValidacion.compareTo(ConstantesVisado.ACTIVA_RESTRICTIVA)==0){
				flagValida = false;
				if(resultado.getObject()!=null){
					voucherHost = (TiivsHostVoucher) resultado.getObject();
					logger.debug("[Restrictiva]-ANTES-voucherMontoHost:"+Double.valueOf(voucherHost.getMontoComision()));
					if(voucherHost.getMontoComision()!=null &&
							Double.valueOf(voucherHost.getMontoComision()).compareTo(solicitudRegistrarT.getComision())!=0){
						flagValida = true;
						logger.debug("[Restrictiva]-voucherMontoHost:"+Double.valueOf(voucherHost.getMontoComision()));
//						Utilitarios.mensajeError("ERROR", validacionVoucher.getValor3());	
						mesajeConfirmacion = validacionVoucher.getValor3();
						logger.debug("[Restrictiva]-mensaje:"+mesajeConfirmacion);
						Utilitarios.mensajeError("ERROR", mesajeConfirmacion);
					}
				}
			}
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"en validaComision():",e);
		}
		logger.debug("[ValidaComision]-flagValida: "+flagValida);
		return flagValida;
	}
	
	/**
	 * Metodo encargado de insertar la información del Voucher recuperado de Host 
	 * mediante un servicio web en la BD local de Visado.
	 * @param result Representa el objeto VoucherHost del tipo {@link BResult}
	 * **/
	private void insertarVoucher(BResult result){
		GenericDao<TiivsHostVoucher, Object> service = (GenericDao<TiivsHostVoucher, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroNroVoucher = Busqueda.forClass(TiivsHostVoucher.class);
		//Se consulta las solicitudes con estado: 0001
		filtroNroVoucher.add(Restrictions.eq("nroVoucher", solicitudRegistrarT.getNroVoucher()));
		try {
			List<TiivsHostVoucher> vouchers = service.buscarDinamico(filtroNroVoucher);
			if(vouchers.size()==0){
				if(result!=null && result.getObject()!=null){
					TiivsHostVoucher voucher = (TiivsHostVoucher) result.getObject();
					try {
						voucher.setUsuarioRegistro(this.usuario.getUID());
						voucher.setFechaRegistro(new Timestamp(new Date().getTime()));
						voucher.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO);
						service.insertar(voucher);
					} catch (Exception e) {
						logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al guardar el NroVoucher-Host",e);
					}
				}	
			}
		} catch (Exception e1) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al guardar el NroVoucher-Host",e1);
			e1.printStackTrace();
		}
	}
		
	public String redireccionar(){
		Utilitarios.mensajeInfo("INFO", mesajeConfirmacion);
		return redirect;
	}
	
	public void setearComision(){
		logger.info("================================== setearComision ==================================");
		logger.debug("exonera comision: " + bFlagComision);
		if(solicitudRegistrarT!=null){
			if(bFlagComision){
				solicitudRegistrarT.setExoneraComision(ConstantesVisado.VALOR2_ESTADO_ACTIVO);
			}else{
				solicitudRegistrarT.setExoneraComision(ConstantesVisado.VALOR2_ESTADO_INACTIVO);
			}
		}
	}
	
	private void eliminaAnexosAnterioresMenos(TiivsSolicitud solicitudRegistrarT2, List<TiivsAnexoSolicitud> lstAnexoSolicitud2) throws Exception {
		
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");		
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		
		List<TiivsAnexoSolicitud> lstAnexoSolicitudActual = solicitudService.obtenerListarAnexosSolicitud(this.solicitudRegistrarT);
		
		logger.info("Numero de anexos actuales: " + lstAnexoSolicitudActual.size());
		int i=0;
		for(TiivsAnexoSolicitud anexoActual: lstAnexoSolicitudActual){
			for(TiivsAnexoSolicitud anexoNuevo : lstAnexoSolicitud){
				if(!anexoActual.getId().equals(anexoNuevo.getId())){
					i++;
					serviceAnexos.eliminar(anexoActual);
				}
			}
		}	
		
	}

	public void depurarTablas()
	{
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		List<TiivsAgrupacionPersona> tmpListBD = buscarAgrupacionesBD(this.solicitudRegistrarT.getCodSoli());
		Set<TiivsSolicitudAgrupacion> tmpVista = this.solicitudRegistrarT.getTiivsSolicitudAgrupacions();
		
		logger.info("Tamanio Lista Agrupaciones BD: " + tmpListBD.size());
		logger.info("Tamanio Lista Agrupaciones Solicitud: " + tmpVista.size());
		
		//if (tmpVista.size()!=tmpListBD.size())
		//{
			logger.info("Depurando registros de BD de Agrupaciones de persona");
			for (TiivsSolicitudAgrupacion x : tmpVista) 
			{
				for (TiivsAgrupacionPersona b :x.getTiivsAgrupacionPersonas()) 
				{ 
					for (TiivsAgrupacionPersona bd: tmpListBD)
					{
						logger.debug("b.getCodSoli() " +b.getCodSoli() + " b.getNumGrupo() "+b.getNumGrupo());
						if (!b.getCodSoli().equals(bd.getCodSoli()) && b.getNumGrupo().equals(bd.getNumGrupo()))
						{
							logger.info("Eliminando grupo Nro: " + b.getNumGrupo() + " de la solicitud: " + b.getCodSoli());
							
							try {
								serviceAgru.eliminar(bd);
							} catch (Exception e) {
								logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+e.getMessage());
								Utilitarios.mensajeError("ERROR", "Ocurrio un Error al depurar la base de datos de agrupaciones!!");
								e.printStackTrace();
							}
						}
					}
				}
			}
		//}
	}
	
	public TiivsAgrupacionPersona existeAgrupacionPersona(TiivsAgrupacionPersona objAgrupacion)
	{
		Boolean existe=false;
		TiivsAgrupacionPersona objReturn =null;
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		List<TiivsAgrupacionPersona> lista = new ArrayList<TiivsAgrupacionPersona>();
		
		Busqueda filtroAgrPer = Busqueda.forClass(TiivsAgrupacionPersona.class);
		filtroAgrPer.add(Restrictions.eq("codSoli", objAgrupacion.getCodSoli()));
		filtroAgrPer.add(Restrictions.eq("numGrupo",objAgrupacion.getNumGrupo()));
		filtroAgrPer.add(Restrictions.eq("codPer",objAgrupacion.getCodPer()));
		filtroAgrPer.add(Restrictions.eq("tipPartic",objAgrupacion.getTipPartic()));
		filtroAgrPer.add(Restrictions.eq("clasifPer",objAgrupacion.getClasifPer()));
				
		try {
			lista = serviceAgru.buscarDinamico(filtroAgrPer);
			
		} catch (Exception ex) {
			logger.error("Error al buscar la agrupacion de personas: ",ex);
		}
		
		if (lista.size()>0)
		{
			existe=true;
			objReturn= lista.get(0);
		}else{
			objReturn=null;
		}
		
		return objReturn;
		///return existe;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean existeSolicitudAgrupacion(TiivsSolicitudAgrupacion objSolAgrupacion)
	{
		Boolean existe=false;
		
		GenericDao<TiivsSolicitudAgrupacion, Object> serviceAgru = (GenericDao<TiivsSolicitudAgrupacion, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		List<TiivsSolicitudAgrupacion> lista = new ArrayList<TiivsSolicitudAgrupacion>();
		
		Busqueda filtroSolAgrp = Busqueda.forClass(TiivsSolicitudAgrupacion.class);
		
		filtroSolAgrp.add(Restrictions.eq("id.codSoli", objSolAgrupacion.getId().getCodSoli()));
		filtroSolAgrp.add(Restrictions.eq("id.numGrupo",objSolAgrupacion.getId().getNumGrupo()));
				
		try {
			lista = serviceAgru.buscarDinamico(filtroSolAgrp);
			
		} catch (Exception ex) {
			logger.debug("Error al buscar la solicitud agrupacion: ",ex);
		}
		
		if (lista.size()>0)
		{
			existe=true;
		}
		
		return existe;
	}
	
	@SuppressWarnings("unchecked")
	public List<TiivsAgrupacionPersona> buscarAgrupacionesBD(String codSoli)
	{
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		List<TiivsAgrupacionPersona> listaP = new ArrayList<TiivsAgrupacionPersona>();
		List<TiivsAgrupacionPersona> listaF = new ArrayList<TiivsAgrupacionPersona>();
		
		Busqueda filtroAgrPer = Busqueda.forClass(TiivsAgrupacionPersona.class);
		filtroAgrPer.add(Restrictions.eq("codSoli", codSoli));
				
		try {
			listaP = serviceAgru.buscarDinamico(filtroAgrPer);
			
		} catch (Exception ex) {
			logger.error("Error al buscar la agrupacion de personas",ex);
		}
		
		for (TiivsAgrupacionPersona b: listaP)
		{
			if (b.getNumGrupo()>0)
			{
				listaF.add(b);
			}
		}
			
		return listaF;
	}

	//metodo Samira update
	public String verEditarAgrupacion(){
		logger.info("********************** verEditarAgrupacion *********************************** ");
		logger.info("this.getCodSoli  "+ this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.getNumGrupo  "+ this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		logger.info("this.getLstPersonas  "+ this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
		
		
		combosMB=new CombosMB();
		//combosMB.obtenerClasificacionPersona();
		lstClasificacionPersona=combosMB.getLstClasificacionPersona();
		logger.info("tamanioo actual de la lista de Clasificacion **** " +lstClasificacionPersona.size());
		
		for (TiivsSolicitudAgrupacion a : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
			if (a.getId().equals(objAgrupacionSimpleDtoCapturado.getId())) {
				tiivsSolicitudAgrupacionCapturado = a;
				break;
			}
		}
		
//		for (int i = 0; i < lstAgrupacionSimpleDto.size(); i++) {
//           if(lstAgrupacionSimpleDto.get(i).equals(objAgrupacionSimpleDtoCapturado)){
//        	   indexUpdatePoderdanteApoderado=i;
//        	   numGrupoUpdatePoderdanteApoderado=lstAgrupacionSimpleDto.get(i).getId().getNumGrupo();
//        	   for (TiivsSolicitudAgrupacion a : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
//				if(a.getId().getNumGrupo()==numGrupoUpdatePoderdanteApoderado){
//					lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
//					lstTiivsAgrupacionPersonas.addAll(a.getTiivsAgrupacionPersonas());
//					//lstTiivsAgrupacionPersonas=a.getTiivsAgrupacionPersonas();
//				}
//			   }
//        	   break;
//			}
//		}
		
				  
		/**Recien se empieza a llenar la lista de Persona */
		
		setLstTiivsPersona(objAgrupacionSimpleDtoCapturado.getLstPersonas());
		
		for (AgrupacionSimpleDto dd: this.solicitudRegistrarT.getLstAgrupacionSimpleDto()) {
			if(dd.equals(objAgrupacionSimpleDtoCapturado)){
				lstTiivsPersonaCopia =new ArrayList<TiivsPersona>();
				lstTiivsPersonaCopia.addAll(dd.getLstPersonas());				
			}
			break;
		}
		
		listaTemporalPersonasBorradores=new ArrayList<TiivsPersona>();
		listaTemporalAgrupacionesPersonaBorradores=new ArrayList<TiivsAgrupacionPersona>();
		logger.info("this.getLstPersonas  "+ lstTiivsPersona.size());
		flagUpdatePoderdanteApoderados=true;
		bBooleanPopup=false;
		//return  "/faces/paginas/solicitudEdicion.xhtml";
		return "";
	}

	public void agregarActionListenerAgrupacion(){
		  logger.info("********************** agregarActionListenerAgrupacion ********************* " );
		  lstTiivsPersona=new ArrayList<TiivsPersona>();
		  objTiivsPersonaBusqueda=new TiivsPersona();
		  objTiivsPersonaResultado=new TiivsPersona();
		  flagUpdatePoderdanteApoderados=false;
		  combosMB=new CombosMB();
		  lstClasificacionPersona=combosMB.getLstClasificacionPersona();
		  logger.info("tamanioo actual **** " +combosMB.getLstClasificacionPersona().size());
		 listaTemporalAgrupacionesPersonaBorradores=new ArrayList<TiivsAgrupacionPersona>();
		 listaTemporalPersonasBorradores=new ArrayList<TiivsPersona>();
		 lstTiivsPersonaCopia=new ArrayList<TiivsPersona>();
		 int NumeroGrupoMax=0;
		
		 if (lstAgrupacionSimpleDto.size() > 0) {
			NumeroGrupoMax = lstAgrupacionSimpleDto.get(0).getId().getNumGrupo();
			for (int i = 0; i < lstAgrupacionSimpleDto.size(); i++) {
				logger.info("lstAgrupacionSimpleDto.get(i).getId().getNumGrupo() :: " + lstAgrupacionSimpleDto.get(i).getId().getNumGrupo());
				if (lstAgrupacionSimpleDto.get(i).getId().getNumGrupo() >= NumeroGrupoMax) {
					NumeroGrupoMax = lstAgrupacionSimpleDto.get(i).getId().getNumGrupo();
				}
			}

		}
		 
		 
		 		 
		numGrupo=NumeroGrupoMax;
		logger.info("El maximo numero de Grupo :: " +numGrupo);
		
		TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion = new TiivsSolicitudAgrupacion();
		TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId = new TiivsSolicitudAgrupacionId();
		tiivsSolicitudAgrupacionId.setCodSoli(solicitudRegistrarT.getCodSoli());
		tiivsSolicitudAgrupacionId.setNumGrupo(numGrupo+1);
		tiivsSolicitudAgrupacion.setId(tiivsSolicitudAgrupacionId);
		tiivsSolicitudAgrupacion.setTiivsSolicitud(this.solicitudRegistrarT);
		tiivsSolicitudAgrupacion.setEstado("1");
		
		this.solicitudRegistrarT.getTiivsSolicitudAgrupacions().add(tiivsSolicitudAgrupacion);		
		this.tiivsSolicitudAgrupacionCapturado = tiivsSolicitudAgrupacion;		
		
	  }

	public boolean validarAgregarAgrupacion() {
		boolean returno = true;
		logger.info("***************************** validarAgregarAgrupacion ***************************************");
		String mensaje = "Por lo menos ingrese un Poderdante o Apoderado";
		if (lstTiivsPersona.size() == 0) {
			returno = false;
			Utilitarios.mensajeInfo("", mensaje);
		}
		return returno;
	}

	public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(TiivsSolicitud solicitud,  int iNumGrupo, Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas) {
		logger.info("iNumGrupo : " + iNumGrupo);

		Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion = new HashSet<TiivsSolicitudAgrupacion>();
		lstSolicitudArupacion =solicitud.getTiivsSolicitudAgrupacions();
		TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion = new TiivsSolicitudAgrupacion();
		TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId = new TiivsSolicitudAgrupacionId();
		tiivsSolicitudAgrupacionId.setCodSoli(solicitud.getCodSoli());
		tiivsSolicitudAgrupacionId.setNumGrupo(iNumGrupo);
		tiivsSolicitudAgrupacion.setId(tiivsSolicitudAgrupacionId);
		tiivsSolicitudAgrupacion.setTiivsAgrupacionPersonas(lstTiivsAgrupacionPersonas);
		tiivsSolicitudAgrupacion.setEstado("1");

		lstSolicitudArupacion.add(tiivsSolicitudAgrupacion);
		logger.info("TAMANIO DE LA SOLICI AGRUPA " + lstSolicitudArupacion.size());
		return lstSolicitudArupacion;
	}

	public List<Integer> existe(List<Integer> listaTemporal2){
        List<Integer> listaTemporal=new ArrayList<Integer>();
        List<Integer> arraycar=new ArrayList<Integer>();
        arraycar=listaTemporal2;
        
	        for(int i=0;i<arraycar.size();i++){
	            for(int j=0;j<arraycar.size()-1;j++){
	                if(i!=j){
	                    if(arraycar.get(i)==arraycar.get(j)){
	                        arraycar.set(j, 99);
	                    }
	                }
	            }
	        }   
	        for (Integer integer : arraycar) {
	        	if(integer!=99){
	        		listaTemporal.add(integer);
	        	}
	        	}
      return listaTemporal;
}
	public boolean validarSiAgrupacionEstaRevocada(){
		boolean retorno =false;
		logger.info("***************************** validarSiAgrupacionEstaRevocada ***************************************");
		try {
		List<TiivsRevocado> listaRevocadoDeDondeComparar=new ArrayList<TiivsRevocado>();
		GenericDao<TiivsRevocado, Object> serviceSolicitud = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		filtro.add(Restrictions.eq("estado", ConstantesVisado.ESTADOS.ESTADO_ACTIVO_REVOCADO));
		filtro.addOrder(Order.asc("codAgrup"));
		listaRevocadoDeDondeComparar = serviceSolicitud.buscarDinamico(filtro);
		logger.info("**** listaRevocadoDeDondeComparar **** "+listaRevocadoDeDondeComparar.size());
		List<ComboDto> listaPersonasXAgrupacionXAgrupacion=null;
		int i;
		List<Integer> listaCodAgrupaciones = new ArrayList<Integer>();
		for (TiivsRevocado e : listaRevocadoDeDondeComparar) {
			listaCodAgrupaciones.add(e.getCodAgrup());
		}
		List<Integer> listaCodAgrupacionesAcomparar = new ArrayList<Integer>();
		listaCodAgrupacionesAcomparar=existe(listaCodAgrupaciones);
		
		for (Integer a : listaCodAgrupacionesAcomparar) {
			listaPersonasXAgrupacionXAgrupacion =new ArrayList<ComboDto>();
		for (TiivsRevocado x : listaRevocadoDeDondeComparar) {
			if(a.equals(x.getCodAgrup())){
				logger.info("Codigo Agrupacion - 1 - ::::: " +a + " x.getCodPer()::: " +x.getTiivsPersona().getCodPer() +" x.getTipPartic() ::: " +x.getTipPartic() +" x.getNumGrupo():: " +x.getCodAgrup());
				listaPersonasXAgrupacionXAgrupacion.add(new ComboDto(x.getTiivsPersona().getCodPer()+"",x.getTipPartic(),x.getCodAgrup()));
			}
		}
		logger.info(" lstTiivsAgrupacionPersonas :: " +lstTiivsAgrupacionPersonas.size());
		if(listaPersonasXAgrupacionXAgrupacion.size()==lstTiivsAgrupacionPersonas.size()){
			i = 0;
			for (TiivsAgrupacionPersona an : lstTiivsAgrupacionPersonas) {
				for (ComboDto bn : listaPersonasXAgrupacionXAgrupacion) {
					logger.info("an.getCodPer() :: " +an.getCodPer() + " ::: an.getTipPartic()  "+an.getTipPartic());
					logger.info("bn.getKey() :: " +bn.getKey() + " ::: bn.getDescripcion()  "+bn.getDescripcion());
					if(an.getCodPer()==Integer.parseInt(bn.getKey()) && an.getTipPartic().trim().equals(bn.getDescripcion().trim()) ){
						//logger.info("Una combinacion es igual " + an.getNumGrupo());
						i++;
					}
				}
			}
			if(i==listaPersonasXAgrupacionXAgrupacion.size()){
				logger.info("Toda la combinación es igual a la de Revocado Num Grupo : " +a);
				Utilitarios.mensajeInfo("INFO ", "La combinación fue Revocada" );
				retorno =true;
				break;
				
			}
		}
		
		}
		} catch (Exception e) {
			logger.error(e);
		}
		return retorno;
	}
	public void agregarAgrupacion() {
		if (validarAgregarAgrupacion()) {
			logger.info("***************************** agregarAgrupacion ***************************************");
			
			lstTiivsAgrupacionPersonas = this.tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas();
			
			if (lstTiivsAgrupacionPersonas == null){
				lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
			}			
			
			limpiarAgrupacionesVacias();					
			
			logger.info("lstTiivsAgrupacionPersonas: inicio " + lstTiivsAgrupacionPersonas.size());

			List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();

			for (TiivsPersona n : lstTiivsPersona) {
				if (n.getTipPartic().equals(ConstantesVisado.PODERDANTE)) {
					lstPoderdantes.add(n);
					logger.info(" poderdante : " + n.getCodPer());
				}
				if (n.getTipPartic().equals(ConstantesVisado.APODERADO)) {
					lstApoderdantes.add(n);
					logger.info(" apoderado : " + n.getCodPer());
				}
				if (n.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)) {
					lstApoderdantes.add(n);
					logger.info(" apoderado-heredero : " + n.getCodPer());
				}

			}
			
			this.llamarComision();
			
			logger.info("lstTiivsAgrupacionPersonas: fin " + lstTiivsAgrupacionPersonas.size());
			String estadoRevocado=null;
			if(validarSiAgrupacionEstaRevocada()){
				/** Se revocara la Combinacion**/
				 estadoRevocado =ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_3;
				 this.tiivsSolicitudAgrupacionCapturado.setEstado(estadoRevocado);
			}else{
				estadoRevocado=null;
			}
			this.armaAgrupacionSimple();

		}
		
		this.tiivsSolicitudAgrupacionCapturado = null;
		
	}
			  


	private void limpiarAgrupacionesVacias() {
		
		Set<TiivsSolicitudAgrupacion> lstSolAgruToRemove = new HashSet<TiivsSolicitudAgrupacion>();
		for(TiivsSolicitudAgrupacion solAgru: this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()){
			if(solAgru.getTiivsAgrupacionPersonas().size()==0){					
				lstSolAgruToRemove.add(solAgru);
			}
		}			
		this.solicitudRegistrarT.getTiivsSolicitudAgrupacions().removeAll(lstSolAgruToRemove);
		
	}

	public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(int iNumGrupo) {
		logger.info("iNumGrupo : " + iNumGrupo);

		Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion = new HashSet<TiivsSolicitudAgrupacion>();
		lstSolicitudArupacion = this.solicitudRegistrarT.getTiivsSolicitudAgrupacions();
		TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion = new TiivsSolicitudAgrupacion();
		TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId = new TiivsSolicitudAgrupacionId();
		tiivsSolicitudAgrupacionId.setCodSoli(solicitudRegistrarT.getCodSoli());
		tiivsSolicitudAgrupacionId.setNumGrupo(iNumGrupo);
		tiivsSolicitudAgrupacion.setId(tiivsSolicitudAgrupacionId);
		tiivsSolicitudAgrupacion.setTiivsAgrupacionPersonas(lstTiivsAgrupacionPersonas);
		tiivsSolicitudAgrupacion.setEstado("1");

		lstSolicitudArupacion.add(tiivsSolicitudAgrupacion);
		logger.info("TAMANIO DE LA SOLICI AGRUPA "
				+ lstSolicitudArupacion.size());
		return lstSolicitudArupacion;
	}

	public void llamarComision() {
		logger.info("************************** llamar Comision *****************************");
		this.solicitudRegistrarT.setTipoComision(objRegistroUtilesMB.obtenerTipoComision(this.solicitudRegistrarT));
		this.solicitudRegistrarT.setComision(objRegistroUtilesMB.obtenerComision(solicitudRegistrarT.getTipoComision()));
		logger.info("TIPO COMISION : " + this.solicitudRegistrarT.getTipoComision());
		logger.info("COMISION : " + this.solicitudRegistrarT.getComision());

	}

	public String eliminarArupacion() {
		logger.info("********************** eliminarArupacion *********************************** ");
		this.lstAgrupacionSimpleDto.remove(this.objAgrupacionSimpleDtoCapturado);						
		
		Set<TiivsSolicitudAgrupacion> lstSolicitudAgrupacion = (Set<TiivsSolicitudAgrupacion>) this.solicitudRegistrarT.getTiivsSolicitudAgrupacions();

		logger.info("Tamanio de la lista Solicitud Agrupacion : " + lstSolicitudAgrupacion.size());
		
		for (TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion : lstSolicitudAgrupacion) 
		{
			if (tiivsSolicitudAgrupacion.getId().equals(this.objAgrupacionSimpleDtoCapturado.getId())) 
			{
				//lstSolicitudAgrupacion.remove(tiivsSolicitudAgrupacion);
				
				this.solicitudRegistrarT.getTiivsSolicitudAgrupacions().remove(tiivsSolicitudAgrupacion);
				
				break;
			}
		}
		
				
//		 for (TiivsSolicitudAgrupacion x : listaSolicitudAgrupacionesCopia) {
//			 if (x.getId().equals(this.objAgrupacionSimpleDtoCapturado.getId())) {
//					logger.info(" Ya existe en base de datos");
//					/** Agregar la persona a la lista de temporal a eliminar*/
//					listaTemporalSolicitudAgrupacionesBorradores.add(x);
//			 }else{
//					// Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita
//					logger.info(" Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita");
//				}
//			}

			logger.info(" listaTemporalSolicitudAgrupacionesBorradores.size() "+	listaTemporalSolicitudAgrupacionesBorradores.size());
		logger.info("Tamanio de la lista Solicitud Agrupacion : " + lstSolicitudAgrupacion.size());
		this.llamarComision();
		this.objAgrupacionSimpleDtoCapturado = new AgrupacionSimpleDto();
		
		return  "/faces/paginas/solicitudEdicion.xhtml";
	}

	public void listarDocumentosXSolicitud(ValueChangeEvent e) {
		// logger.info("ValuechanceEvent :  " + e.getNewValue());		
		String sCodTipoSol = (String) e.getNewValue();		
		addArchivosTemporalesToDelete(); //agregado 21-05-13
		listarDocumentosXSolicitud(sCodTipoSol);	
		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
	}
	
	public void listarDocumentosXSolicitud(String sCodTipoSol){
		
		GenericDao<TiivsTipoSolicDocumento, Object> genTipoSolcDocumDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSolcDoc = Busqueda
				.forClass(TiivsTipoSolicDocumento.class);
		filtroTipoSolcDoc.add(Restrictions.eq("tiivsTipoSolicitud.codTipSolic",sCodTipoSol));
		filtroTipoSolcDoc.add(Restrictions.eq("activo",'1'));
		filtroTipoSolcDoc.addOrder(Order.desc("obligatorio"));
		try {
			lstDocumentosXTipoSolTemp = genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);
			lstTipoSolicitudDocumentos = new ArrayList<TiivsTipoSolicDocumento>();
			lstTipoSolicitudDocumentos.addAll(lstDocumentosXTipoSolTemp);
			
			logger.info("lstDocumentosXTipoSolTemp.size():" + lstDocumentosXTipoSolTemp.size());
			logger.info("lstTipoSolicitudDocumentos.size():" + lstTipoSolicitudDocumentos.size());
			actualizarListadoDocumentos();

		} catch (Exception ex) {
			logger.error("Error al cargar el listado de documentos:",ex);			
		}
		
		// 120913
	     visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(lstTipoSolicitudDocumentos));
	}

	public void actualizarListadoDocumentos() {

		//addArchivosTemporalesToDelete();

		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();

		for (TiivsTipoSolicDocumento s : lstTipoSolicitudDocumentos) {
			if (s.getObligatorio() != null && s.getObligatorio().equals("1")) {								
				DocumentoTipoSolicitudDTO doc = new  DocumentoTipoSolicitudDTO();
				doc.setItem(s.getId().getCodDoc());
				doc.setDocumento(s.getTiivsDocumento().getDescripcion());
				doc.setObligacion(true + "");
				doc.setBobligacion(true);
				doc.setAlias("");
				doc.setAliasTemporal("");
				doc.setNombreCorto(s.getTiivsDocumento().getNombre());
				doc.setFormato(s.getTiivsDocumento().getFormato());
				lstdocumentos.add(doc);									
//				lstdocumentos.add(new DocumentoTipoSolicitudDTO(s.getId()
//						.getCodDoc(), s.getTiivsDocumento().getDescripcion(),
//						true + "", "", ""));
			} else {
				
				DocumentoTipoSolicitudDTO doc = new  DocumentoTipoSolicitudDTO();
				doc.setItem(s.getId().getCodDoc());
				doc.setDocumento(s.getTiivsDocumento().getDescripcion());
				doc.setObligacion(false + "");
				doc.setBobligacion(false);
				doc.setAlias("");
				doc.setAliasTemporal("");
				doc.setNombreCorto(s.getTiivsDocumento().getNombre());
				doc.setFormato(s.getTiivsDocumento().getFormato());
				lstdocumentos.add(doc);						
//				lstdocumentos.add(new DocumentoTipoSolicitudDTO(s.getId()
//						.getCodDoc(), s.getTiivsDocumento().getDescripcion(),
//						false + "", "", ""));
			}
		}

//		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
	}

	public void addArchivosTemporalesToDelete() {
		for (TiivsAnexoSolicitud a : lstAnexoSolicitud) {
			aliasFilesToDelete.add(a.getAliasTemporal());
		}
	}

	
	public String prepararURLEscaneo() {			
		logger.info("***********prepararURLEscaneo***************");
		
		String sCadena = "";		
		try{				
			pdfViewerMB = new PDFViewerMB();	
			logger.info("usuario.getUID() ******************** " +usuario.getUID());
			sCadena = pdfViewerMB.prepararURLEscaneo(usuario.getUID());			
		}catch(Exception e){
			logger.error("Error al obtener parámetros de APPLET: ",e);
		}
		return sCadena;
		
	}

	public void agregarDocumentosXTipoSolicitud() {
		logger.info(" ************************** agrearDocumentosXTipoSolicitud  ****************************** ");
		logger.info("iTipoSolicitud  : " + iTipoSolicitud);
		String aliasCortoDocumento="";
		
		setbBooleanPopup(false);
		
		if (selectedTipoDocumento != null) {
			sCodDocumento = selectedTipoDocumento.getId().getCodDoc();
		} else {
			sCodDocumento = null;
		}
		logger.info("codDocumento :  " + sCodDocumento);
		logger.info("lstAnexoSolicitud.size() :  " + lstAnexoSolicitud.size());

		if (sCodDocumento == null || sCodDocumento.isEmpty()) {
			Utilitarios.mensajeInfo("", "Debe seleccionar un documento");
			return;
		}

		if (this.ValidarDocumentosDuplicados()) {

			if (sCodDocumento.equalsIgnoreCase(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
				sCodDocumento = ConstantesVisado.PREFIJO_OTROS + String.format("%06d", lstdocumentos.size() + 1);
				aliasCortoDocumento = sCodDocumento;
			} else {
				aliasCortoDocumento = selectedTipoDocumento.getTiivsDocumento().getNombre();
			}

			String sAliasTemporal = cargarUnicoPDF(aliasCortoDocumento);

			if (sAliasTemporal == null || sAliasTemporal.trim() == "") {
				return;
			}

			String sExtension = sAliasTemporal.substring(sAliasTemporal
					.lastIndexOf("."));
//			String sAliasArchivo = this.solicitudRegistrarT.getCodSoli() + "_"
//					+ sCodDocumento + sExtension;
//			String sAliasArchivo = this.sCodDocumento + sExtension;
			aliasCortoDocumento += sExtension;

			logger.info("aliasArchivo *** " + aliasCortoDocumento.toLowerCase());
			logger.info("aliasArchivoTemporal *** " + sAliasTemporal);

			TiivsAnexoSolicitud objAnexo = new TiivsAnexoSolicitud();
			objAnexo.setId(new TiivsAnexoSolicitudId(this.solicitudRegistrarT
					.getCodSoli(), sCodDocumento));
			objAnexo.setAliasArchivo(aliasCortoDocumento.toUpperCase());
			objAnexo.setAliasTemporal(sAliasTemporal);
			lstAnexoSolicitud.add(objAnexo);

			this.actualizarListaDocumentosXTipo(objAnexo);

			establecerTipoSolicitud();	
			//120913
			visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(lstTipoSolicitudDocumentos));

		}
		// solicitudRegistrarT.getTiivsTipoSolicitud().setTiivsTipoSolicDocumentos(tiivsTipoSolicDocumentos);
	}
	
	private void establecerTipoSolicitud(){
		for (TiivsTipoSolicitud tipoSoli : combosMB.getLstTipoSolicitud()) {
			if (tipoSoli.getCodTipSolic().equals(iTipoSolicitud)) {
				solicitudRegistrarT.setTiivsTipoSolicitud(tipoSoli);
			}
		}
	}
	

	public String cargarUnicoPDF(String aliasArchivo) {

		if (file == null) {
			Utilitarios.mensajeInfo("", "No se ha seleccionado ningún archivo");
			return "";
		}

		byte fileBytes[] = getFile().getContents();

		File fichTemp = null;
		String sUbicacionTemporal = "";
		String sNombreTemporal = "";
		FileOutputStream canalSalida = null;

		try {
			
			sUbicacionTemporal = Utilitarios
					.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
					+ File.separator + ConstantesVisado.FILES + File.separator;			

			logger.debug("ubicacion temporal " + sUbicacionTemporal);

			File fDirectory = new File(sUbicacionTemporal);
			fDirectory.mkdirs();

			String extension = file.getFileName().substring(
					getFile().getFileName().lastIndexOf("."));

			if(aliasArchivo.equals("")){
				aliasArchivo = "temp";
			} else {
				aliasArchivo = aliasArchivo + "_";
			}
			
			fichTemp = File.createTempFile(aliasArchivo, extension, new File(
					sUbicacionTemporal));

			sNombreTemporal = fichTemp.getName();

			logger.debug("Nombre temporal de archivo:  " + sNombreTemporal);

			canalSalida = new FileOutputStream(fichTemp);
			canalSalida.write(fileBytes);

			canalSalida.flush();
			
			return sNombreTemporal;

		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"IOException al cargarUnicoPDF: " , e);
			String sMensaje = "Se produjo un error al adjuntar fichero";
			Utilitarios.mensajeInfo("", sMensaje);
			return "";
		}catch (Exception ex) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al cargarUnicoPDF: " , ex);
			String sMensaje = "Se produjo un error al adjuntar fichero";
			Utilitarios.mensajeInfo("", sMensaje);
			return "";
		}  
		finally {

			fichTemp.deleteOnExit();
			if (canalSalida != null) {
				try {
					canalSalida.close();
				} catch (IOException x) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"IOException: ",x);
				}catch(Exception e){
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR,e);
				}
			}
		}
	}

	public void quitarDocumentosXTipoSolicitud() {
		logger.info(" ************************** quitarDocumentosXTipoSolicitud()  ****************************** ");
		logger.info("iTipoSolicitud  : " + iTipoSolicitud);
		logger.info("lstAnexoSolicitud.size() :  " + lstAnexoSolicitud.size());
		logger.info("SelectedTipoDocumento : " + this.selectedDocumentoDTO.getItem());
		
		if (this.selectedDocumentoDTO.getAlias().isEmpty()) {
			return;
		}
		
		TiivsAnexoSolicitud anexoToRemove = null;
		for (TiivsAnexoSolicitud anexo : lstAnexoSolicitud) {
			if (anexo.getId().getCodDoc().equals(selectedDocumentoDTO.getItem())) {
				anexoToRemove  = anexo;				
				this.aliasFilesToDelete.add(anexo.getAliasTemporal());
				break;
			}
		}
		
		if(anexoToRemove!=null)
			lstAnexoSolicitud.remove(anexoToRemove);

		// Para el llenado del listado (listBox)
		int i = 0;
		for (TiivsTipoSolicDocumento s : lstDocumentosXTipoSolTemp) {
			if (s.getId().getCodDoc().equals(selectedDocumentoDTO.getItem())) {
				//this.lstTipoSolicitudDocumentos.add(i, s);
				this.lstTipoSolicitudDocumentos.add(s);
				break;
			}
			i++;
		}

		// listado documentos
		for (DocumentoTipoSolicitudDTO doc : lstdocumentos) {			
			if (doc.getItem().equals(selectedDocumentoDTO.getItem())) {
				doc.setAlias("");
				doc.setAliasTemporal("");
				if(doc.getItem().contains(ConstantesVisado.PREFIJO_OTROS)){
					logger.info("Este documento es de tipo otros");
					lstdocumentos.remove(doc);
				}
				break;
			}			
		}
		
	}
	
	
	public void actualizarDocumentosXTipoSolicitud(ActionEvent ae){		
		logger.info("*****************actualizarDocumentosXTipoSolicitud*****************");
		
		//logger.info("documentos Leidos: " + documentosLeidos);		
		logger.info("documentos Leidos: " + visadoDocumentosMB.getDocumentosLeidos());
		logger.info("documentos Cargados: " + visadoDocumentosMB.getDocumentosCargados());
		String []aDocumentosLeidos = visadoDocumentosMB.getDocumentosLeidos().split(",");
		String []aDocumentosCargados = visadoDocumentosMB.getDocumentosCargados().split(",");
		String nombreDoc = "";
		
		//Actualiza lista de documentos
		
		if(aDocumentosLeidos.length == aDocumentosCargados.length){
			
			
			//for(String documento : aDocumentosLeidos){
			for(int i=0;i<aDocumentosLeidos.length;i++){
				String documentoLeido = aDocumentosLeidos[i];
				String documentoCargado = aDocumentosCargados[i];
				logger.info("Buscando coincidencias para:" + documentoLeido);
				if(!documentoLeido.trim().isEmpty()){
					nombreDoc = documentoLeido.substring(0, documentoLeido.lastIndexOf("."));			
					
					//Agregar a listado de documentos tabla documentos
					for(DocumentoTipoSolicitudDTO doc : lstdocumentos){
						logger.info("nombreDoc = doc.getItem():" + nombreDoc + "=" + doc.getNombreCorto());
						if(doc.getNombreCorto().equals(nombreDoc)){
							doc.setAlias(documentoLeido);
							doc.setAliasTemporal(Utilitarios.modificarExtension(documentoCargado));
							logger.info("actualizó nombre documento:" + doc.getAlias());
							
							//agregar a lista de anexos de la solicitud
							TiivsAnexoSolicitud objAnexo = new TiivsAnexoSolicitud();
							objAnexo.setId(new TiivsAnexoSolicitudId(this.solicitudRegistrarT.getCodSoli(), doc.getItem()));
							objAnexo.setAliasArchivo(doc.getAlias());
							objAnexo.setAliasTemporal(doc.getAliasTemporal());
							lstAnexoSolicitud.add(objAnexo);
													
							//Actualiza lstTipoSolicitudDocumentos (listBox de documentos)		
							for (TiivsTipoSolicDocumento s : lstDocumentosXTipoSolTemp) {
								if (s.getId().getCodDoc().equals(objAnexo.getId().getCodDoc())) {
									this.lstTipoSolicitudDocumentos.remove(s);
									break;
								}
							}												
						}
					}										
				}
			}		
			
		}
		// 120913
		visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(lstTipoSolicitudDocumentos));
		
		//ver si tambien cambia tipo solicitud
		establecerTipoSolicitud();
				
		logger.info("(Tabla) lstdocumentos tamaño:" + lstdocumentos.size());
		logger.info("(Anexos)lstAnexoSolicitud tamaño:" + lstAnexoSolicitud.size());
		logger.info("(Combo) lstTipoSolicitudDocumentos tamaño:" + lstTipoSolicitudDocumentos.size());
	}
	

	public boolean validarOperacionBancaria() {
		boolean result = true;
		String sMensaje = "";
		if (objSolicBancaria.getId().getCodOperBan().equals("")) {
			sMensaje = "Ingrese un tipo de Operacion";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (objSolicBancaria.getId().getMoneda().equals("")) {
			sMensaje = "Seleccione una moneda";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (!objSolicBancaria.getId().getMoneda().equals("")
				&& !objSolicBancaria.getId().getMoneda().equals(
						ConstantesVisado.MONEDAS.COD_SOLES)
				&& objSolicBancaria.getTipoCambio() == 0) {
			sMensaje = "Ingrese el Tipo de Cambio";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (objSolicBancaria.getImporte() == 0) {
			/*** SE AGREGA CONDICIONANTE DE TIPO DE OPERACION ***/
			if(isDineraria(objSolicBancaria.getId().getCodOperBan())){
				sMensaje = "Ingrese el Importe";
				Utilitarios.mensajeInfo("", sMensaje);
				result = false;	
			}
		}
		if (!flagUpdateOperacionSolic) {
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				if (x.getId().getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan())
						&& x.getId().getMoneda().trim().equals(objSolicBancaria.getId().getMoneda().trim())) {
					sMensaje = "Tipo de Operación con la misma moneda ya registrado, Ingrese otra moneda";
					Utilitarios.mensajeInfo("", sMensaje);
					result = false;
					break;
				}
			}
		}else{
			int conunt =0, index=0;
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				index++;
				if (x.getId().getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan())){
					logger.debug("x.getMoneda().trim() " +x.getId().getMoneda().trim());
					logger.debug("objSolicBancaria.getMoneda().trim()) " +objSolicBancaria.getId().getMoneda().trim());
					
					if(x.getId().getMoneda().trim().equals(objSolicBancaria.getId().getMoneda().trim())) {
						conunt++;
						logger.info("conunt "+conunt);
						logger.debug("x.getMoneda() " +x.getId().getMoneda());
						logger.debug("objSolicBancaria.getMoneda() " + objSolicBancaria.getId().getMoneda());
						
						logger.debug("lo deja ");
							break;
						
				}/*else if(!x.getMoneda().trim().equals(objSolicBancaria.getMoneda().trim())) {
					logger.debug(" no lo deja ");
					sMensaje = "Tipo de Operación con la misma moneda ya registrado, Ingrese otra moneda";
					Utilitarios.mensajeInfo("", sMensaje);
					result = false;
					break;
				}*/
					
				}
			}
		}
		return result;
	}

	private boolean isDineraria(String codigoOperacion){
		GenericDao<TiivsOperacionBancaria, Object> service = 
							(GenericDao<TiivsOperacionBancaria, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		try {
			TiivsOperacionBancaria operacion = service.buscarById(TiivsOperacionBancaria.class, (Serializable) codigoOperacion);
			if(operacion!=null){
				if(operacion.getTipo().compareTo(ConstantesVisado.NO_DINERARIA)!=0){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("+++ Fallo al obtener Operacion bancaria", e);
		}		
		return false;
	}
	public void obtenerAccionAgregarOperacionBancaria() {
		logger.info("**************************** obtenerAccionAgregarOperacionBancaria ****************************");
		logger.info("********************************************* : "
				+ objSolicBancaria.getImporte());
		
		if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
			objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
		}
		if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
			objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
		}
		if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
			objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
		}
		

	}

	public void limpiarListaSolicitudesBancarias() {
		logger.info("**************************** limpiarListaSolicitudesBancarias ****************************");
		this.objSolicBancaria = new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		this.objSolicBancaria.setTipoCambio(0.00);
		this.objSolicBancaria.setImporte(0.00);
		bBooleanPopupTipoCambio=true;
		flagUpdateOperacionSolic = false;
		// this.lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
	}

	public void eliminarOperacionBancaria() {
		logger.info("**************************** eliminarOperacionBancaria ****************************");
		// String valor = Utilitarios.capturarParametro("objOperacion");
		// logger.info("CODIGO DE OPERACION "+valor);
		logger.info("Importe ::  ::: "+objSolicitudOperacionCapturado.getImporte());
	   
	    logger.info("Tamanio Lista original que se trajo de base : "+lstSolicBancariasCopia.size());
	    lstSolicBancarias.remove(objSolicitudOperacionCapturado);
	   for (TiivsSolicitudOperban x : lstSolicBancariasCopia) {
		//if(x.equals(objSolicitudOperacionCapturado)){
		if(x.getId().getCodOperBan().trim().equals(objSolicitudOperacionCapturado.getId().getCodOperBan().trim())
		   && x.getId().getMoneda().trim().equals(objSolicitudOperacionCapturado.getId().getMoneda().trim())
		   && x.getId().getCodSoli().trim().equals(objSolicitudOperacionCapturado.getId().getCodSoli().trim()))
		{
			// Ya existe en base de datos
			logger.info(" Ya existe en base de datos");
			/** Agregar la operacion bancaria a la lista de temporal a eliminar*/
			if(listaTemporalEliminarOperacionesBancarias.size()==0){
				/**Ya no existe en la lista de temporales a Elimnar, recien lo agregar, si existe no lo agrega*/
				listaTemporalEliminarOperacionesBancarias.add(objSolicitudOperacionCapturado);
			}
			for (TiivsSolicitudOperban n : listaTemporalEliminarOperacionesBancarias) {
				logger.info("a ");
				if(!n.equals(objSolicitudOperacionCapturado)){
					logger.info(" b ");
					/**Ya no existe en la lista de temporales a Elimnar, recien lo agregar, si existe no lo agrega*/
					listaTemporalEliminarOperacionesBancarias.add(objSolicitudOperacionCapturado);
				}
			}
			
		}else{
			// Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita
			logger.info(" Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita");
		}
	    }
	   logger.info("Tamanio Lista a Eliminar en base : "+listaTemporalEliminarOperacionesBancarias.size());
	   logger.info("Tamanio Lista original que se trajo de base : "+lstSolicBancariasCopia.size());
	   logger.info("Tamanio Lista que queda en Memoria : "+lstSolicBancarias.size());
		
		
		
		
		for (TiivsSolicitudOperban x : lstSolicBancarias) {
			if (x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
				icontSoles++;
			} else if (x.getId().getMoneda().trim()
					.equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
				icontDolares++;
			} else if (x.getId().getMoneda().trim()
					.equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
				icontEuros++;
			}
		}

		if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) {
			// SOLO SOLES
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				valorFinal = valorFinal + x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			this.solicitudRegistrarT
					.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES
							+ ((double) Math.round(valorFinal*100)/100));
			this.solicitudRegistrarT
					.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
		}
		if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
			// SOLO DOLARES
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				valorFinal = valorFinal + x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			this.solicitudRegistrarT
					.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR
							+ ((double) Math.round(valorFinal*100)/100));
			this.solicitudRegistrarT
					.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
		}
		if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
			// SOLO EUROS
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				valorFinal = valorFinal + x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			this.solicitudRegistrarT
					.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO
							+ ((double) Math.round(valorFinal*100)/100));
			this.solicitudRegistrarT
					.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
		}
		if (icontDolares > 0 && icontEuros > 0 && icontSoles == 0
				|| icontDolares > 0 && icontEuros == 0 && icontSoles > 0
				|| icontDolares == 0 && icontEuros > 0 && icontSoles > 0
				|| icontDolares > 0 && icontEuros > 0 && icontSoles > 0) {
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				valorFinal = valorFinal + x.getImporteSoles();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			this.solicitudRegistrarT
					.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES
							+ ((double) Math.round(valorFinal*100)/100));
			this.solicitudRegistrarT
					.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
		}

		icontDolares = 0;
		icontEuros = 0;
		icontSoles = 0;
		valorFinal = 0;
		objSolicBancaria = new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		objSolicBancaria.setTipoCambio(0.0);
		this.flagUpdateOperacionSolic = false;
		this.objSolicitudOperacionCapturado = new TiivsSolicitudOperban();
		this.objSolicitudOperacionCapturado
				.setId(new TiivsSolicitudOperbanId());
		this.valorFinal = 0.0;
		if (lstSolicBancarias.size() == 0) {
			this.solicitudRegistrarT.setsImporteMoneda(valorFinal + "");
			this.solicitudRegistrarT.setMoneda(null);
		}
		this.llamarComision();

	}

	public void obtenerPersonaSeleccionada() {
		logger.info(objTiivsPersonaSeleccionado.getCodPer());
		this.objTiivsPersonaResultado = this.objTiivsPersonaSeleccionado;
		bBooleanPopup=false;
	}

	public List<TiivsPersona> buscarPersonaLocal() throws Exception {
		logger.info("Buscando en Persona Local");
		boolean busco = false;
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);

		if ((objTiivsPersonaBusqueda.getTipDoi() == null 
		  || objTiivsPersonaBusqueda.getTipDoi().equals(""))
		 && (objTiivsPersonaBusqueda.getNumDoi() == null
		  || objTiivsPersonaBusqueda.getNumDoi().equals(""))) {
			Utilitarios.mensajeInfo("INFO","Ingrese al menos un criterio de busqueda");
		} else if (objTiivsPersonaBusqueda.getNumDoi() == null
				|| objTiivsPersonaBusqueda.getNumDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Número de Doi");
		} else if (objTiivsPersonaBusqueda.getTipDoi() == null
				|| objTiivsPersonaBusqueda.getTipDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Tipo de Doi");
		} else {
			if (objTiivsPersonaBusqueda.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_CODIGO_CENTRAL)) {
				filtro.add(Restrictions.eq("codCen",objTiivsPersonaBusqueda.getNumDoi()));
				busco = true;
				
				lstTiivsPersona = service.buscarDinamico(filtro);
				
				for (TiivsPersona tiivsPersona : lstTiivsPersona) {
					for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
						if (tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())) {
							tiivsPersona.setsDesctipDoi(p.getDescripcion());
						}
					}
				}
			}else{
				filtro.add(Restrictions.eq("tipDoi",objTiivsPersonaBusqueda.getTipDoi().trim()));
				filtro.add(Restrictions.eq("numDoi",objTiivsPersonaBusqueda.getNumDoi().trim()));
				busco = true;
				
               lstTiivsPersona = service.buscarDinamico(filtro);
				for (TiivsPersona tiivsPersona : lstTiivsPersona) {
					for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
						if (tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())) {
							tiivsPersona.setsDesctipDoi(p.getDescripcion());
						}
					}
				} 
			}
			
			if (lstTiivsPersona.size() == 0 && busco) {
				//Utilitarios.mensajeInfo("INFO","No se han encontrado resultados para los criterios de busqueda seleccionados");
			}
		}

		return lstTiivsPersona;
	}

	public List<TiivsPersona> buscarPersonaReniec() throws Exception {
		logger.debug("==== inicia buscarPersonaReniec() ==== ");
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		BResult resultado = null;
		TiivsPersona objPersona = null;
		Persona persona = null;
		if (objTiivsPersonaBusqueda.getNumDoi() != null) {
			logger.info("[RENIEC]-Dni:" + objTiivsPersonaBusqueda.getNumDoi());

			// ObtenerPersonaReniecService reniecService = new
			// ObtenerPersonaReniecServiceImpl();
			// logger.debug("reniecService="+reniecService);
			ObtenerPersonaReniecDUMMY reniecService = new ObtenerPersonaReniecDUMMY();
			resultado = reniecService.devolverPersonaReniecDNI("P013371",
					"0553", objTiivsPersonaBusqueda.getNumDoi());
			logger.debug("[RENIEC]-resultado: " + resultado);

			if (resultado.getCode() == 0) {

				persona = (Persona) resultado.getObject();
				logger.info("PERSONA : " + persona.getNombreCompleto()
						+ "\nDNI: " + persona.getNumerodocIdentidad());
				objPersona = new TiivsPersona();
				objPersona.setNumDoi(persona.getNumerodocIdentidad());
				objPersona.setNombre(persona.getNombre());
				objPersona.setApePat(persona.getApellidoPaterno());
				objPersona.setApeMat(persona.getApellidoMaterno());
				objPersona.setTipDoi(objTiivsPersonaBusqueda.getTipDoi());
				objPersona.setCodCen(objTiivsPersonaBusqueda.getCodCen());
				lstTiivsPersona.add(objPersona);
			}
		}

		logger.debug("==== saliendo de buscarPersonaReniec() ==== ");
		return lstTiivsPersona;
	}

	public boolean validarRegistroDuplicado() {
		logger.info("******************************* validarRegistroDuplicado ******************************* "
				+ objTiivsPersonaResultado.getNumDoi());
		boolean bResult = true;
		String sMensaje = "";
		for (TipoDocumento c : combosMB.getLstTipoDocumentos()) {
			if (objTiivsPersonaResultado.getTipDoi().equals(c.getCodTipoDoc())) {
				objTiivsPersonaResultado.setsDesctipDoi(c.getDescripcion());
			}
		}
		for (ComboDto c : combosMB.getLstTipoRegistroPersona()) {
			if (objTiivsPersonaResultado.getTipPartic().equals(c.getKey())) {
				objTiivsPersonaResultado.setsDesctipPartic(c.getDescripcion());
			}
		}
		if (!flagUpdatePersona) {
			for (TiivsPersona p : lstTiivsPersona) {
				if (objTiivsPersonaResultado.getNumDoi().equals(p.getNumDoi())) {
					bResult = false;
					sMensaje = "Ya existe una persona con número de Doi :  "
							+ p.getNumDoi() + " y Tipo de Registro : "
							+ objTiivsPersonaResultado.getsDesctipPartic();
					Utilitarios.mensajeInfo("INFO", sMensaje);
				}
			}
		}

		return bResult;
	}

	public void limpiarCriteriosBusqueda() {
		
		logger.info("***************************** limpiar ************************************ ");
		objTiivsPersonaBusqueda.setCodCen("");
		objTiivsPersonaBusqueda.setTipDoi("");
		objTiivsPersonaBusqueda.setNumDoi("");
		objTiivsPersonaResultado.setTipDoi("");
		objTiivsPersonaResultado.setNumDoi("");
		objTiivsPersonaResultado.setCodCen("");
		objTiivsPersonaResultado.setApePat("");
		objTiivsPersonaResultado.setApeMat("");
		objTiivsPersonaResultado.setNombre("");
		objTiivsPersonaResultado.setTipPartic("");
		objTiivsPersonaResultado.setClasifPer("");
		objTiivsPersonaResultado.setClasifPerOtro("");
		objTiivsPersonaResultado.setEmail("");
		objTiivsPersonaResultado.setNumCel("");
	}

	public void editarPersona() {
		logger.info("***************************** editarPersona ************************************ ");
		for (int i = 0; i < this.lstTiivsPersona.size(); i++) {
			if (objTiivsPersonaCapturado.equals(this.lstTiivsPersona.get(i))) {
				indexUpdatePersona = i;
			}
		}
		
		//Captura agrupacion persona
		for(TiivsAgrupacionPersona agruPersona: this.tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas()){
			//Si Personacapturado es igual a algun elemento de  this.tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas
			//if(agruPersona.getCodPer().equals(objTiivsPersonaCapturado.getCodPer()) && agruPersona.getTipPartic().equals(objTiivsPersonaCapturado.getTipPartic())){
			//if(agruPersona.getIdAgrupacion().equals(objTiivsPersonaCapturado.getIdAgrupacion())){
			if(agruPersona.getTiivsPersona().equals(objTiivsPersonaCapturado)){
				tiivsAgrupacionPersonaCapturado = agruPersona;
				break;
			}
		}
		
		//this.objTiivsPersonaResultado=new TiivsPersona();  
		this.objTiivsPersonaResultado.setApeMat(this.objTiivsPersonaCapturado.getApeMat());
		this.objTiivsPersonaResultado.setTipDoi(this.objTiivsPersonaCapturado.getTipDoi());
		this.objTiivsPersonaResultado.setNumDoi(this.objTiivsPersonaCapturado.getNumDoi());
		this.objTiivsPersonaResultado.setCodCen(this.objTiivsPersonaCapturado.getCodCen());
		this.objTiivsPersonaResultado.setApePat(this.objTiivsPersonaCapturado.getApePat());
		this.objTiivsPersonaResultado.setNombre(this.objTiivsPersonaCapturado.getNombre());
		this.objTiivsPersonaResultado.setTipPartic(this.objTiivsPersonaCapturado.getTipPartic());
		this.actualizarClasificacion();
		this.objTiivsPersonaResultado.setClasifPer(this.objTiivsPersonaCapturado.getClasifPer());
		this.objTiivsPersonaResultado.setClasifPerOtro(this.objTiivsPersonaCapturado.getClasifPerOtro());
		this.objTiivsPersonaResultado.setEmail(this.objTiivsPersonaCapturado.getEmail());
		this.objTiivsPersonaResultado.setNumCel(this.objTiivsPersonaCapturado.getNumCel());
		this.objTiivsPersonaResultado.setCodPer(this.objTiivsPersonaCapturado.getCodPer());
		this.objTiivsPersonaResultado.setIdAgrupacion(tiivsAgrupacionPersonaCapturado.getIdAgrupacion());
		this.flagUpdatePersona = true;
		
		if(objTiivsPersonaCapturado.getTipDoi()!=null){ 
			if(objTiivsPersonaCapturado.getTipDoi().equals(this.codigoRazonSocial)){ //CODIGO RAZON SOCIAL
				this.mostrarRazonSocial = true;
			} else {
				this.mostrarRazonSocial = false;
			}
		}
	}

	public void eliminarPersona() {
		logger.info("**************************** eliminarPersona ****************************");
		logger.info("Codigo de la persona capturada a Eliminar " +objTiivsPersonaCapturado.getCodPer());
		//logger.info(" Lista de las Personas Original de base " +lstTiivsPersonaCopia.size());
		logger.info(" Lista de las Personas Antes de Remover " +lstTiivsPersona.size());
//		logger.info("listaTemporalPersonasBorradores Antes de eliminarse ::: " +listaTemporalPersonasBorradores.size());
//		logger.info(" Lista de las lstTiivsAgrupacionPersonas Antes de Remover " +lstTiivsAgrupacionPersonas.size());
		logger.info(" q es '????  " +objTiivsPersonaCapturado.getTipPartic());
		
		
		logger.info(" objTiivsPersonaCapturado  " + objTiivsPersonaCapturado.getCodPer());
		
		logger.info(" getTiivsAgrupacionPersonas  " + objTiivsPersonaCapturado.getCodPer());
		
		
		logger.info(" tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas() inicio " + tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas().size());
		
		
		TiivsAgrupacionPersona agruPersonaRemove = null;
		for(TiivsAgrupacionPersona aPer : tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas()){
			if(aPer.getCodPer() == objTiivsPersonaCapturado.getCodPer() && aPer.getTipPartic() == objTiivsPersonaCapturado.getTipPartic()){
				agruPersonaRemove = aPer;
			}
		}
		
		this.tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas().remove(agruPersonaRemove);
		
		lstTiivsPersona.remove(objTiivsPersonaCapturado);
		
		logger.info(" tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas() despues " + tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas().size());
		logger.info(" lstTiivsPersona despues " + lstTiivsPersona.size());
		
//		List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
//		List<TiivsPersona>   lstApoderdantes = new ArrayList<TiivsPersona>();
//		for (TiivsPersona s : lstTiivsPersona) {
//		
//		if(s.getTipPartic().trim().equals(ConstantesVisado.PODERDANTE))
//		{
//			lstPoderdantes.add(s);
//		}
//		else  if(s.getTipPartic().trim().equals(ConstantesVisado.APODERADO))
//		{
//			lstApoderdantes.add(s);
//		}
//		}
//		
//		logger.info("lstPoderdantes antes de remover " +lstPoderdantes.size());
//		logger.info("lstApoderdantes antes de remover " +lstApoderdantes.size());
//		
//		lstTiivsPersona.remove(objTiivsPersonaCapturado);
//		
//		
//		 lstPoderdantes = new ArrayList<TiivsPersona>();
//		  lstApoderdantes = new ArrayList<TiivsPersona>();
//		for (TiivsPersona s : lstTiivsPersona) {
//		
//		if(s.getTipPartic().trim().equals(ConstantesVisado.PODERDANTE))
//		{
//			lstPoderdantes.add(s);
//		}
//		else  if(s.getTipPartic().trim().equals(ConstantesVisado.APODERADO))
//		{
//			lstApoderdantes.add(s);
//		}
//		}
//		
//		logger.info("lstPoderdantes despues de remover  " +lstPoderdantes.size());
//		logger.info("lstApoderdantes  despues de remover " +lstApoderdantes.size());
//		
//		
//		for (TiivsAgrupacionPersona n : lstTiivsAgrupacionPersonas) {
//			if(n.getTiivsPersona().equals(objTiivsPersonaCapturado)){
//				lstTiivsAgrupacionPersonas.remove(n);
//				listaTemporalAgrupacionesPersonaBorradores.add(n);
//				break;
//			}
//			
//		}
//		logger.info(" Lista de las lstTiivsAgrupacionPersonas Despues de Remover " +lstTiivsAgrupacionPersonas.size());
//		logger.info(" Lista de las Personas despues de Remover " +lstTiivsPersona.size());
//		
//		/* for (TiivsPersona x : lstTiivsPersonaCopia) {
//				if(x.equals(objTiivsPersonaCapturado)){
//					//logger.info("La persona  Ya existe en base de datos, se agrega a la lista para borrar");
//					*//** Agregar la persona a la lista de temporal a eliminar*//*
//					listaTemporalPersonasBorradores.add(objTiivsPersonaCapturado);
//				}else{
//					// Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita
//					//logger.info(" Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita");
//				}
//			}*/
//		 logger.info("Lista de las Personas temporales a Borrar " +listaTemporalPersonasBorradores.size());
//		//objTiivsPersonaCapturado = new TiivsPersona();
//		this.flagUpdatePersona = false;
	}
	public boolean validarbuscarPersonaLocal() {
		logger.info("=== validarbuscarPersonaLocal() ===");
		boolean busco = false;
		boolean retorno = false;
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);

		if ((objTiivsPersonaResultado.getTipDoi() == null 
		  || objTiivsPersonaResultado.getTipDoi().equals(""))
		 && (objTiivsPersonaResultado.getNumDoi() == null
		  || objTiivsPersonaResultado.getNumDoi().equals(""))) {
		} else if (objTiivsPersonaResultado.getNumDoi() == null
				|| objTiivsPersonaResultado.getNumDoi().equals("")) {
		} else if (objTiivsPersonaResultado.getTipDoi() == null
				|| objTiivsPersonaResultado.getTipDoi().equals("")) {
		} else {
			if (objTiivsPersonaResultado.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_CODIGO_CENTRAL)) {
				filtro.add(Restrictions.eq("codCen",objTiivsPersonaResultado.getNumDoi()));
				busco = true;
				
				try {
					lstTiivsPersona = service.buscarDinamico(filtro);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				for (TiivsPersona tiivsPersona : lstTiivsPersona) {
					for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
						if (tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())) {
							retorno =true;
						}
					}
				}
			}else{
				filtro.add(Restrictions.eq("tipDoi",objTiivsPersonaResultado.getTipDoi().trim()));
				filtro.add(Restrictions.eq("numDoi",objTiivsPersonaResultado.getNumDoi().trim()));
				busco = true;
				
               try {
				lstTiivsPersona = service.buscarDinamico(filtro);
			} catch (Exception e) {
				e.printStackTrace();
			}
               retorno=true;
			}
			
			if (lstTiivsPersona.size() == 0 && busco) {
				retorno=false;
				}else{
				retorno=true;
			}
		}

		return retorno;
	}
	public boolean validarPersona() {
		logger.info("******************************* validarPersona ******************************* "
				+ objTiivsPersonaResultado.getTipPartic());
		boolean bResult = true;
		String sMensaje = "";
		logger.info("objTiivsPersonaResultado.getClasifPer() "+ objTiivsPersonaResultado.getClasifPer());
		if (objTiivsPersonaResultado.getTipDoi().equals("")) {
			sMensaje = "Seleccione el Tipo de Documento";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if (objTiivsPersonaResultado.getNumDoi().equals("")) {
			sMensaje = "Ingrese el Número de Doi";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}else{
			if (!objTiivsPersonaResultado.getTipDoi().equals("")) {
				bResult=validarTipoDocumentos();
				}
		}
		
		if(objTiivsPersonaResultado.getTipDoi()!=null && !objTiivsPersonaResultado.getTipDoi().equals(this.codigoRazonSocial)){ //CODIGO RAZONSOCIAL
			if (objTiivsPersonaResultado.getNombre() == null
					|| objTiivsPersonaResultado.getNombre().equals("")) {
				sMensaje = "Ingrese el Nombre";
				bResult = false;
				Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}
		
		if(objTiivsPersonaResultado.getTipDoi()!=null && objTiivsPersonaResultado.getTipDoi().equals(this.codigoRazonSocial)){ //CODIGO RAZONSOCIAL
			if (objTiivsPersonaResultado.getApePat() == null
					|| objTiivsPersonaResultado.getApePat().equals("")) {
				sMensaje = "Ingrese la Razón Social";
				bResult = false;
				Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}
		
		if(!objTiivsPersonaResultado.getCodCen().isEmpty()){
		if(objTiivsPersonaResultado.getCodCen().length()!=8){
			sMensaje = "El código central debe ser de 8 caracteres";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		}
		if (objTiivsPersonaResultado.getClasifPer() == null
				|| objTiivsPersonaResultado.getClasifPer().equals("")) {
			sMensaje = "Ingrese el Tipo de Clasificación";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if (!objTiivsPersonaResultado.getEmail().equals("")) {
			if (!Utilitarios.validateEmail(objTiivsPersonaResultado.getEmail())) {
				sMensaje = "Ingrese un email valido";
				bResult = false;
				Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}
		if ((objTiivsPersonaResultado.getTipPartic() == null || objTiivsPersonaResultado
				.getTipPartic().equals(""))) {
			sMensaje = "Ingrese el Tipo de Participacion";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		} else if (objTiivsPersonaResultado.getClasifPer().equals("99")
				&& (objTiivsPersonaResultado.getClasifPerOtro().equals("") || objTiivsPersonaResultado
						.getClasifPerOtro() == null)) {
			sMensaje = "Ingrese la descipcion Tipo de Participacion";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		} else if (!flagUpdatePersona) {
			/** VALIDAR QUE EL NUMERO DE DOCUMENTO  NO SE ENCUENTRE REGISTRADO TAMPOCO EN BD */
			 if(objTiivsPersonaResultado.getCodPer()==0&&validarbuscarPersonaLocal()){
				sMensaje = "Persona ya registrada, Ingrese una nueva, o busque a la persona ";
				bResult = false;
				Utilitarios.mensajeInfo("", sMensaje);
			
				/** FIN DE LA VALIDACION **/
			}else if(validarPersonaEnListaDeAgrupaciones()){
				bResult=false;
			}
			else{
			for (TiivsPersona x : lstTiivsPersona) {
				logger.debug("x.getCodPer() " +x.getCodPer());
				logger.debug("objTiivsPersonaResultado.getCodPer() " +objTiivsPersonaResultado.getCodPer());
				
				
				if(x.getTipDoi().equals(objTiivsPersonaResultado.getTipDoi())
						&& x.getNumDoi().equals(objTiivsPersonaResultado.getNumDoi())){
					sMensaje = "Persona con Documento ya ingresado, Ingrese otros datos de persona. ";
					Utilitarios.mensajeInfo("", sMensaje);
					bResult = false;
					break;
				}else if(objTiivsPersonaResultado.getCodPer()!=0){
					if (x.getCodPer() == objTiivsPersonaResultado.getCodPer()) {
						sMensaje = "Persona ya registrada, Ingrese otros datos de persona. ";
						Utilitarios.mensajeInfo("", sMensaje);
						bResult = false;
						break;
					}
					}
					
			}
			}
		}
		logger.info("bResult " +bResult);

		return bResult;


	}
	public boolean validarPersonaEnListaDeAgrupaciones(){
		logger.info("=== validarPersonaEnListaDeAgrupaciones() ===");
		boolean retorno = false;
		if(lstTiivsAgrupacionPersonas!=null){
		logger.info("=== lstTiivsAgrupacionPersonas  ==="+lstTiivsAgrupacionPersonas.size());
		for (TiivsAgrupacionPersona x : lstTiivsAgrupacionPersonas) {
			logger.debug("x.getCodPer() " +x.getCodPer());
			logger.debug("objTiivsPersonaResultado.getTipPartic() " +objTiivsPersonaResultado.getTipPartic());
			
			if(x.getTiivsPersona().getTipDoi().equals(objTiivsPersonaResultado.getTipDoi())
					&& x.getTiivsPersona().getNumDoi().equals(objTiivsPersonaResultado.getNumDoi())
					&& !(x.getTipPartic().equals(objTiivsPersonaResultado.getTipPartic()))){
				String sMensaje = "No Se puede agregar una misma persona como representante y representado";
				Utilitarios.mensajeInfo("", sMensaje);
				retorno = true;
				break;
			}/*else if(objTiivsPersonaResultado.getCodPer()!=0){
				if (x.getCodPer() == objTiivsPersonaResultado.getCodPer()) {
					String sMensaje = "Persona ya registrada, Ingrese otros datos de persona. ";
					Utilitarios.mensajeInfo("", sMensaje);
					retorno = false;
					break;
				}
				}*/
			}
		}
		return retorno;
	}
	public boolean 	validarTipoDocumentos() {
		logger.info("***************** validarTipoDocumentos ********************* ");
		boolean bResult = true;
		String sMensaje = "";
		if (objTiivsPersonaResultado.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_CODIGO_CENTRAL)) {
			if(objTiivsPersonaResultado.getNumDoi().length()!=8){
			sMensaje = "El Número de documento debe ser de 8 caracteres";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}else if (objTiivsPersonaResultado.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.CODIGO_C_U_S_P_P)) {
			if(objTiivsPersonaResultado.getNumDoi().length()!=11){
			sMensaje = "El código CUSPP debe ser de 11 caracteres";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}else if (objTiivsPersonaResultado.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_RUC)) {
			if(objTiivsPersonaResultado.getNumDoi().length()!=11){
			sMensaje = "El Ruc debe ser de 11 caracteres";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}else if (objTiivsPersonaResultado.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_RUC_ANTIGUO)) {
			if(objTiivsPersonaResultado.getNumDoi().length()!=8){
			sMensaje = "El Ruc Antiguo debe ser de 8 caracteres";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}else if (objTiivsPersonaResultado.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_DNI)) {
			if(objTiivsPersonaResultado.getNumDoi().length()!=8){
			sMensaje = "El Dni debe ser de 8 caracteres";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}else if (objTiivsPersonaResultado.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_CARNET_DIPLOMATICO)) {
			if(objTiivsPersonaResultado.getNumDoi().length()!=8){
			sMensaje = "El Carnet Diplomático debe ser de 8 caracteres";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}
			
		
		return bResult;
	}

	public void agregarPersona() {
		logger.info("****************** agregarPersona ********************");
		if (validarPersona()) {
			if (validarRegistroDuplicado()) {
				for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
					if (objTiivsPersonaResultado.getTipDoi().equals(p.getCodTipoDoc())) {objTiivsPersonaResultado.setsDesctipDoi(p.getDescripcion());}}
				for (ComboDto p : combosMB.getLstTipoRegistroPersona()) {
					if (objTiivsPersonaResultado.getTipPartic().equals(p.getKey())) {objTiivsPersonaResultado.setsDesctipPartic(p.getDescripcion());}}
				for (ComboDto p : combosMB.getLstClasificacionPersona()) {
					if (objTiivsPersonaResultado.getClasifPer().equals(p.getKey())) {objTiivsPersonaResultado.setsDescclasifPer(p.getDescripcion());}
					if (objTiivsPersonaResultado.getClasifPer().equals("99")) {objTiivsPersonaResultado.setsDescclasifPer(objTiivsPersonaResultado.getClasifPerOtro());}
				}
				
				/** Se agrega a la lista de personas en la grilla*/				
				if (!flagUpdatePersona) {
					if(objTiivsPersonaResultado.getCodPer()==0){
						objTiivsPersonaResultado = actualizarPersona(objTiivsPersonaResultado);						
					}
					
					TiivsAgrupacionPersona agruPersona = new TiivsAgrupacionPersona();
					agruPersona.setTiivsPersona(objTiivsPersonaResultado);
					agruPersona.setClasifPer(objTiivsPersonaResultado.getClasifPer());
					agruPersona.setTipPartic(objTiivsPersonaResultado.getTipPartic());
					agruPersona.setCodPer(objTiivsPersonaResultado.getCodPer());
					
					if(tiivsSolicitudAgrupacionCapturado!=null){
						agruPersona.setCodSoli(tiivsSolicitudAgrupacionCapturado.getId().getCodSoli());
						agruPersona.setNumGrupo(tiivsSolicitudAgrupacionCapturado.getId().getNumGrupo());
						agruPersona.setTiivsSolicitudAgrupacion(tiivsSolicitudAgrupacionCapturado);						
					}
										
					this.tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas().add(agruPersona);
					lstTiivsPersona.add(objTiivsPersonaResultado);
				} else {
					// update
					
					objTiivsPersonaResultado = actualizarPersona(objTiivsPersonaResultado);
					
					logger.info("Index Update: " + indexUpdatePersona);
					this.lstTiivsPersona.set(indexUpdatePersona,objTiivsPersonaResultado);
					
					if(tiivsAgrupacionPersonaCapturado!=null){
						objTiivsPersonaResultado.setIdAgrupacion(tiivsAgrupacionPersonaCapturado.getIdAgrupacion());
						this.tiivsAgrupacionPersonaCapturado.setTiivsPersona(objTiivsPersonaResultado);
						this.tiivsAgrupacionPersonaCapturado.setClasifPer(objTiivsPersonaResultado.getClasifPer());
						this.tiivsAgrupacionPersonaCapturado.setTipPartic(objTiivsPersonaResultado.getTipPartic());
						this.tiivsAgrupacionPersonaCapturado.setCodPer(objTiivsPersonaResultado.getCodPer());
					}
															
					flagUpdatePersona = false;
				}
				logger.info("Tamanio de la lista lstTiivsPersona " +lstTiivsPersona.size());
				logger.info("Tamanio de la lista PersonaResultado " +lstTiivsPersonaResultado.size());
				personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
				logger.info("tamanio de personaDataModal  en el metodo agregar ::::: " +personaDataModal.getRowCount());
				objTiivsPersonaResultado = new TiivsPersona();
				objTiivsPersonaBusqueda = new TiivsPersona();
				objTiivsPersonaSeleccionado = new TiivsPersona();
				lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
				this.tiivsAgrupacionPersonaCapturado = null;
				this.mostrarRazonSocial = false;//mostrar campo datos de cliente
			}

		}
	}


	private TiivsPersona actualizarPersona(TiivsPersona persona) {
		logger.info("********actualizarPersona******************");
		TiivsPersona personaRetorno = new TiivsPersona();
		
		GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit
		.getApplicationContext().getBean("genericoDao");
		
		try {
			persona.setUsuarioRegistro(this.usuario.getUID());
			persona.setFechaRegistro(new Timestamp(new Date().getTime()));
			personaRetorno=servicePers.insertarMerge(persona);
			
			logger.info("Codigo de la persona a Insertar : "+personaRetorno.getCodPer());
						
			persona.setCodPer(personaRetorno.getCodPer());
			persona.setCodCen(personaRetorno.getCodCen());
			persona.setTipDoi(personaRetorno.getTipDoi());
			persona.setNumDoi(personaRetorno.getNumDoi());
			persona.setNombre(personaRetorno.getNombre());
			persona.setApeMat(personaRetorno.getApeMat());
			persona.setApePat(personaRetorno.getApePat());
			persona.setNumCel(personaRetorno.getNumCel());
			persona.setEmail(personaRetorno.getEmail());
			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR,e);
		}
		return persona;
	}

	public void buscarPersona() {
		logger.info("******************** buscarPersona **********************");
		logger.info("***objTiivsPersonaBusqueda.getCodCen() "
				+ objTiivsPersonaBusqueda.getCodCen());
		logger.info("***objTiivsPersonaBusqueda.getTipDoi() "
				+ objTiivsPersonaBusqueda.getTipDoi());
		logger.info("***objTiivsPersonaBusqueda.getNumDoi() "
				+ objTiivsPersonaBusqueda.getNumDoi());
		try {
			List<TiivsPersona> lstTiivsPersonaLocal = new ArrayList<TiivsPersona>();
			lstTiivsPersonaLocal = this.buscarPersonaLocal();
			logger.info("lstTiivsPersonaLocal  " + lstTiivsPersonaLocal.size());
			List<TiivsPersona> lstTiivsPersonaReniec = new ArrayList<TiivsPersona>();
			if (lstTiivsPersonaLocal.size() == 0) {
				lstTiivsPersonaReniec = this.buscarPersonaReniec();
				if (lstTiivsPersonaReniec.size() == 0) {
					objTiivsPersonaResultado = new TiivsPersona();
					this.bBooleanPopup = false;
					Utilitarios.mensajeInfo("INFO",
							"No se encontro resultados para la busqueda.");
				} else if (lstTiivsPersonaReniec.size() == 1) {
					objTiivsPersonaResultado = lstTiivsPersonaReniec.get(0);
					this.bBooleanPopup = false;
				} else if (lstTiivsPersonaReniec.size() > 1) {
					this.bBooleanPopup = true;
					lstTiivsPersonaResultado = lstTiivsPersonaReniec;
				}
			} else if (lstTiivsPersonaLocal.size() == 1) {
				this.bBooleanPopup = false;
				objTiivsPersonaResultado = lstTiivsPersonaLocal.get(0);
			} else if (lstTiivsPersonaLocal.size() > 1) {
				this.bBooleanPopup = true;
				lstTiivsPersonaResultado = lstTiivsPersonaLocal;

				personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
			} else {
				this.bBooleanPopup = true;
			}

		} catch (Exception e) {
			Utilitarios.mensajeError("ERROR", e.getMessage());
			e.printStackTrace();
		}
	}

	public void editarOperacionBancaria() {
		int y = 0;
		logger.info("**************************** editarOperacionBancaria ****************************");

		for (int i = 0; i < this.lstSolicBancarias.size(); i++) {
			if (objSolicitudOperacionCapturado.equals(this.lstSolicBancarias
					.get(i))) {
				indexUpdateOperacion = i;
				break;
			}
		}
		y = y + 1;
		logger.info("yyyyyyy : " + y);
		// Map<String, TiivsSolicitudOperban> mapSolicitudes=new HashMap<String, TiivsSolicitudOperban>();
		mapSolicitudes.put(y, objSolicitudOperacionCapturado);

		logger.info("mapSolicitudes.get(mapSolicitudes.size()).getImporte()"
				+ mapSolicitudes.get(mapSolicitudes.size()).getImporte());
		// if(!flagUpdateOperacionSolic){
		this.objSolicitudOperacionCapturadoOld = this.objSolicitudOperacionCapturado;
		// }
		this.objSolicBancaria = this.objSolicitudOperacionCapturado;
		
		 /**  Validar el disabled del tipo de cambio */
		if (objSolicitudOperacionCapturado.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
			this.objSolicBancaria.setTipoCambio(0.0);
			bBooleanPopupTipoCambio=true;
		}else{
			bBooleanPopupTipoCambio=false;
		
		}
        /** Fin del Validar el disabled del tipo de cambio */
		
		/** Setear el flag para actualizar las operaciones bancarias*/
		this.flagUpdateOperacionSolic = true;
	}
	
	public void obtenerImporteTotalxSolicitud(List<TiivsSolicitudOperban> lstOperBan)
	{
		logger.info(" ===== obtenerImporteTotalxSolicitud() ===  ");

		for (TiivsSolicitudOperban x : lstOperBan) 
		{
			if (x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
			{
				icontSoles++;
			} 
			else if (x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) 
			{
				icontDolares++;
			} 
			else if (x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
			{
				icontEuros++;
			}

		}
		if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) 
		{
			// ONLI SOLES
			for (TiivsSolicitudOperban x : lstSolicBancarias) 
			{
				valorFinal = valorFinal + x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
						
			/*DecimalFormat formateador = new DecimalFormat("###,###.##");
			DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			dfs.setGroupingSeparator(',');
			formateador.setDecimalFormatSymbols(dfs);
			
			NumberFormat NF = NumberFormat.getInstance();
			NF.setMaximumFractionDigits(2);*/
			
			//Double monto=(double) Math.round(valorFinal*100)/100;
					
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ NF.format(valorFinal));
			
			if (valorFinal % 1 == 0)
			{
				DecimalFormat formateador = new DecimalFormat("###,###");
				DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				dfs.setGroupingSeparator(',');
				formateador.setDecimalFormatSymbols(dfs);
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ formateador.format(valorFinal) + ".00");
			}
			else
			{
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ valorFinal);
			}
			
			
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ ((double) Math.round(valorFinal*100)/100));
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ (Utilitarios.redondear(valorFinal)));
			this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
		}
		if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) 
		{
			// ONLI DOLARES
			for (TiivsSolicitudOperban x : lstSolicBancarias) 
			{
				valorFinal = valorFinal + x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			
			/*DecimalFormat formateador = new DecimalFormat("###,###.##");
			DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			dfs.setGroupingSeparator(',');
			formateador.setDecimalFormatSymbols(dfs);
			
			NumberFormat NF = NumberFormat.getInstance();
			NF.setMaximumFractionDigits(2);*/
			
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ NF.format(valorFinal));
			if (valorFinal % 1 == 0)
			{
				DecimalFormat formateador = new DecimalFormat("###,###");
				DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				dfs.setGroupingSeparator(',');
				formateador.setDecimalFormatSymbols(dfs);
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+ formateador.format(valorFinal) + ".00");
			}
			else
			{
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+ valorFinal);
			}
			
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR + ((double) Math.round(valorFinal*100)/100));
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR + (Utilitarios.redondear(valorFinal)));
			this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
		}
		if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) 
		{
			// ONLI EUROS
			for (TiivsSolicitudOperban x : lstSolicBancarias) 
			{
				valorFinal = valorFinal + x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			/*
			DecimalFormat formateador = new DecimalFormat("###,###.##");
			DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			dfs.setGroupingSeparator(',');
			formateador.setDecimalFormatSymbols(dfs);
			
			NumberFormat NF = NumberFormat.getInstance();
			NF.setMaximumFractionDigits(2);*/
			
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ NF.format(valorFinal));
			if (valorFinal % 1 == 0)
			{
				DecimalFormat formateador = new DecimalFormat("###,###");
				DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				dfs.setGroupingSeparator(',');
				formateador.setDecimalFormatSymbols(dfs);
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+ formateador.format(valorFinal) + ".00");
			}
			else
			{
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+ valorFinal);
			}
			
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO + ((double) Math.round(valorFinal*100)/100));
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO + (Utilitarios.redondear(valorFinal)));
			this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
		}
		if (icontDolares > 0 && icontEuros > 0 && icontSoles == 0
				|| icontDolares > 0 && icontEuros == 0
				&& icontSoles > 0 || icontDolares == 0
				&& icontEuros > 0 && icontSoles > 0 || icontDolares > 0
				&& icontEuros > 0 && icontSoles > 0) 
		{
			for (TiivsSolicitudOperban x : lstSolicBancarias) 
			{
				valorFinal = valorFinal + x.getImporteSoles();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			
			/*DecimalFormat formateador = new DecimalFormat("###,###.##");
			DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			dfs.setGroupingSeparator(',');
			formateador.setDecimalFormatSymbols(dfs);*/
			
			/*NumberFormat NF = NumberFormat.getInstance();
			NF.setMaximumFractionDigits(2);*/
			
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ Double.valueOf(valorFinal));
			if (valorFinal % 1 == 0)
			{
				DecimalFormat formateador = new DecimalFormat("###,###");
				DecimalFormatSymbols dfs = formateador.getDecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				dfs.setGroupingSeparator(',');
				formateador.setDecimalFormatSymbols(dfs);
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ formateador.format(valorFinal) + ".00");
			}
			else
			{
				this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ valorFinal);
			}
			
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES + ((double) Math.round(valorFinal*100)/100));
			//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES + (Utilitarios.redondear(valorFinal)));
			this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
		}

		icontDolares = 0;
		icontEuros = 0;
		icontSoles = 0;
		valorFinal = 0;
		
	}
	// METODO SAMIRA
	public void agregarOperacionBancaria() {
		logger.info(" ************************** agrearOperacionBancaria  ****************************** ");
		
		if (this.validarOperacionBancaria()) {
			for (TiivsOperacionBancaria n : combosMB.getLstOpeBancaria()) {
				if (n.getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan())) {
					this.objSolicBancaria.setTiivsOperacionBancaria(n);
					break;
				}
			}
			if (!this.flagUpdateOperacionSolic) {
				if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
					objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
					this.objSolicBancaria.getId().setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
				}
				if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
					objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
					this.objSolicBancaria.getId().setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
				}
				if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
					objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
					this.objSolicBancaria.getId().setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
				}

				item++;
				objSolicBancaria.setsItem("00" + item);
				this.lstSolicBancarias.add(objSolicBancaria);
				for (TiivsSolicitudOperban x : lstSolicBancarias) {
					if(x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
						icontSoles++;
					}else 
						if(x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
						icontDolares++;
					}else 
						if(x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
						icontEuros++;
					}
					
				}
				
				
				if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) {
					//ONLI SOLES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+((double) Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
					//ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+((double) Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
					//ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+((double) Math.round(valorFinal*100)/100));
	                this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (       icontDolares  > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares  > 0 && icontEuros == 0&& icontSoles > 0 
						|| icontDolares == 0 && icontEuros > 0 && icontSoles > 0
						|| icontDolares > 0  && icontEuros > 0 && icontSoles > 0) {
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporteSoles();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+((double) Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}

				
			}else {
				
				logger.info("objSolicitudOperacionCapturadoOld"+objSolicBancaria.getImporte());
				
				if (objSolicitudOperacionCapturado.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getImporte());
					objSolicitudOperacionCapturado.setTipoCambio(0.0);
					this.objSolicitudOperacionCapturado.getId().setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
				}
				if (objSolicitudOperacionCapturado.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getTipoCambio()* objSolicitudOperacionCapturado.getImporte());
					this.objSolicitudOperacionCapturado.getId().setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
				}
				if (objSolicitudOperacionCapturado.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getTipoCambio()* objSolicitudOperacionCapturado.getImporte());
					this.objSolicitudOperacionCapturado.getId().setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
				}
				//actualizar registro 
				this.lstSolicBancarias.set(indexUpdateOperacion,objSolicitudOperacionCapturado);
				
				for (TiivsSolicitudOperban x : lstSolicBancarias) {
					if(x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
						icontSoles++;
					}else 
						if(x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
						icontDolares++;
					}else 
						if(x.getId().getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
						icontEuros++;
					}
				}
				
				if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) {
					//ONLI SOLES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+((double) Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
					//ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+((double) Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
					//ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+((double) Math.round(valorFinal*100)/100));
	                this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (       icontDolares  > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares  > 0 && icontEuros == 0&& icontSoles > 0 
						|| icontDolares == 0 && icontEuros > 0 && icontSoles > 0
						|| icontDolares > 0  && icontEuros > 0 && icontSoles > 0) {
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporteSoles();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+((double) Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}

				
			}
		  icontDolares=0;icontEuros=0;icontSoles=0;valorFinal=0;
          objSolicBancaria=new TiivsSolicitudOperban();
          objSolicBancaria.setId(new TiivsSolicitudOperbanId());
          objSolicBancaria.setTipoCambio(0.0);
          objSolicBancaria.setImporte(0.0);
          objSolicBancaria.setImporteSoles(0.0);
          this.bBooleanPopupTipoCambio=true;
          this.flagUpdateOperacionSolic=false;
          this.objSolicitudOperacionCapturado=new TiivsSolicitudOperban();
          this.objSolicitudOperacionCapturado.setId(new TiivsSolicitudOperbanId());
          this.valorFinal=0;
          this.llamarComision();
		}
  
			

		

	}
// METODO CESAR ANTIGUO
	/*public void agregarOperacionBancaria() 
	{
		logger.info(" ************************** agrearOperacionBancaria  ****************************** ");

		if (this.validarOperacionBancaria()) 
		{
			for (TiivsOperacionBancaria n : combosMB.getLstOpeBancaria()) 
			{
				if (n.getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan())) 
				{
					this.objSolicBancaria.setTiivsOperacionBancaria(n);
					break;
				}
			}
			if (!this.flagUpdateOperacionSolic) 
			{
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
				{
					objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
					objSolicBancaria.setTipoCambio(0.0);
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) 
				{
					objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio() * objSolicBancaria.getImporte());
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
				{
					objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio() * objSolicBancaria.getImporte());
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
				}

				item++;
				objSolicBancaria.setsItem("00" + item);
				this.lstSolicBancarias.add(objSolicBancaria);
				for (TiivsSolicitudOperban x : lstSolicBancarias) 
				{
					if (x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
					{
						icontSoles++;
					} 
					else if (x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) 
					{
						icontDolares++;
					} 
					else if (x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
					{
						icontEuros++;
					}

				}
				if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) 
				{
					// ONLI SOLES
					for (TiivsSolicitudOperban x : lstSolicBancarias) 
					{
						valorFinal = valorFinal + x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ valorFinal);
					this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) 
				{
					// ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) 
					{
						valorFinal = valorFinal + x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR + valorFinal);
					this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) 
				{
					// ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) 
					{
						valorFinal = valorFinal + x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO + valorFinal);
					this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (icontDolares > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares > 0 && icontEuros == 0
						&& icontSoles > 0 || icontDolares == 0
						&& icontEuros > 0 && icontSoles > 0 || icontDolares > 0
						&& icontEuros > 0 && icontSoles > 0) 
				{
					for (TiivsSolicitudOperban x : lstSolicBancarias) 
					{
						valorFinal = valorFinal + x.getImporteSoles();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES + valorFinal);
					this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}

			} 
			else 
			{
				logger.info("objSolicitudOperacionCapturadoOld"	+ objSolicBancaria.getImporte());

				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
				{
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getImporte());
					this.objSolicitudOperacionCapturado.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
				}
				if (objSolicitudOperacionCapturado.getMoneda().equals(
						ConstantesVisado.MONEDAS.COD_DOLAR)) 
				{
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getTipoCambio()* objSolicitudOperacionCapturado.getImporte());
					this.objSolicitudOperacionCapturado.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
				}
				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
				{
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getTipoCambio() * objSolicitudOperacionCapturado.getImporte());
					this.objSolicitudOperacionCapturado.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
				}
				// actualizar registro
				this.lstSolicBancarias.set(indexUpdateOperacion,objSolicitudOperacionCapturado);

				for (TiivsSolicitudOperban x : lstSolicBancarias) {
					if (x.getMoneda().trim()
							.equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
						icontSoles++;
					} else if (x.getMoneda().trim()
							.equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
						icontDolares++;
					} else if (x.getMoneda().trim()
							.equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
						icontEuros++;
					}
				}

				if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) {
					// ONLI SOLES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal = valorFinal + x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT
							.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES
									+ valorFinal);
					this.solicitudRegistrarT
							.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
					// ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal = valorFinal + x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT
							.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR
									+ valorFinal);
					this.solicitudRegistrarT
							.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
					// ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal = valorFinal + x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT
							.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO
									+ valorFinal);
					this.solicitudRegistrarT
							.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (icontDolares > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares > 0 && icontEuros == 0
						&& icontSoles > 0 || icontDolares == 0
						&& icontEuros > 0 && icontSoles > 0 || icontDolares > 0
						&& icontEuros > 0 && icontSoles > 0) {
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal = valorFinal + x.getImporteSoles();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT
							.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES
									+ valorFinal);
					this.solicitudRegistrarT
							.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}

			}
			icontDolares = 0;
			icontEuros = 0;
			icontSoles = 0;
			valorFinal = 0;
			objSolicBancaria = new TiivsSolicitudOperban();
			objSolicBancaria.setId(new TiivsSolicitudOperbanId());
			objSolicBancaria.setTipoCambio(0.0);
			this.flagUpdateOperacionSolic = false;
			this.objSolicitudOperacionCapturado = new TiivsSolicitudOperban();
			this.objSolicitudOperacionCapturado.setId(new TiivsSolicitudOperbanId());
			this.valorFinal = 0;
			this.llamarComision();
		}
	}
*/
	public void validarTipoCambioDisabled(ValueChangeEvent e) {
		if (e.getNewValue() != null) {
			logger.info(" validarTipoCambioDisabled " + e.getNewValue());
			if (e.getNewValue().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
				this.objSolicBancaria.setTipoCambio(0.0);
				bBooleanPopupTipoCambio = true;
			} else {
				bBooleanPopupTipoCambio = false;

			}
		}
	}

	public void actualizarListaDocumentosXTipo(TiivsAnexoSolicitud objAnexo) {
		logger.info("****************************** actualizarListaDocumentosXTipo *********************************");
		if (objAnexo.getId().getCodDoc()
				.contains(ConstantesVisado.PREFIJO_OTROS)) {
			String sAlias = objAnexo.getAliasArchivo();
			String sAliasTemporal = objAnexo.getAliasTemporal();
			
			DocumentoTipoSolicitudDTO doc = new  DocumentoTipoSolicitudDTO();
			doc.setItem(objAnexo.getId().getCodDoc());
			doc.setDocumento(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS);
			doc.setObligacion(false + "");
			doc.setAlias(sAlias);
			doc.setAliasTemporal(sAliasTemporal);
			//doc.setNombreCorto(s.getTiivsDocumento().getNombre());
			//doc.setFormato(s.getTiivsDocumento().getFormato());
			lstdocumentos.add(doc);			
//			DocumentoTipoSolicitudDTO doc = new DocumentoTipoSolicitudDTO(
//					objAnexo.getId().getCodDoc(),
//					ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS, false + "",
//					sAlias, sAliasTemporal);			
//			lstdocumentos.add(doc);
			return;
		}

		for (TiivsTipoSolicDocumento s : lstDocumentosXTipoSolTemp) {

			if (s.getId().getCodDoc().equals(objAnexo.getId().getCodDoc())) {
				this.lstTipoSolicitudDocumentos.remove(s);
				break;
			}
		}

		for (DocumentoTipoSolicitudDTO doc : lstdocumentos) {
			if (doc.getItem().equals(objAnexo.getId().getCodDoc())) {
				doc.setAlias(objAnexo.getAliasArchivo());
				doc.setAliasTemporal(objAnexo.getAliasTemporal());
				break;
			}
		}
	}

	public boolean ValidarDocumentosDuplicados() {
		boolean result = true;
		String sMensaje = "";
		for (TiivsAnexoSolicitud a : this.lstAnexoSolicitud) {
			if (a.getId().getCodDoc().equals(sCodDocumento)
					&& !sCodDocumento
							.equalsIgnoreCase(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
				result = false;
				sMensaje = "Documento ya se encuentra en la lista de Documentos";
				Utilitarios.mensajeInfo("INFO ", sMensaje);
			}
		}
		return result;
	}

	//SE AGREGA METODO PARA OBTENER EL TIPO DE REGISTRO POR BD
	private void obtenerTipoRegistro() {
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultitabla.add(Restrictions.eq("id.codMult",
				ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA));
		List<TiivsMultitabla> listaMultiTabla = new ArrayList<TiivsMultitabla>();
		Integer contador = 0;
		try {
			listaMultiTabla = multiDAO.buscarDinamico(filtroMultitabla);
			tipoRegistro = "";
			if (listaMultiTabla.size() > 0) {
				for (TiivsMultitabla multitabla : listaMultiTabla) {
					contador++;
					if (contador.compareTo(listaMultiTabla.size()) == 0) {
						tipoRegistro += multitabla.getValor1();
					} else {
						tipoRegistro += multitabla.getValor1() + " / ";
					}
				}
			}

		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT
					+ "de multitablas: " + e);
		}
	}
		
	/* Termino metodos del registro */
	
	private void obtenerPagoComision(){
		bComision = false;
		bFlagComision = false;
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultitabla.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_PAGO_COMISION));
		filtroMultitabla.add(Restrictions.eq("id.codElem", ConstantesVisado.CODIGO_FLAG_PAGO_COMISION));
		try {
			List<TiivsMultitabla> listaMultiTabla = multiDAO.buscarDinamico(filtroMultitabla);
			if(listaMultiTabla.size()>0){
				if(listaMultiTabla.get(0).getValor2().compareTo("1")==0){
					bComision = true;
					descripcionComision = listaMultiTabla.get(0).getValor1();
					glosaComision = listaMultiTabla.get(0).getValor3();
				}
			}
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+ "de multitablas: " , e);
		}
	}
	
	public void guardarComentario(){
		logger.info("**************************** Guardar Comentario ****************************");
		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit
		.getApplicationContext().getBean("genericoDao");
		try {
			TiivsSolicitud objResultado = service.modificar(this.solicitudRegistrarT);
			setbMostrarComentario(true);				
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al guardarComentario(): ",e);
		}
	}

	public void limpiarComentario() {
		logger.info("**************************** limpiar Comentario ****************************");
		 this.solicitudRegistrarT.setObs(null);
	}
	
	public void cambiarRazonSocial(ValueChangeEvent e){		
		
		String codTipoDocumento = (String) e.getNewValue();
		if (codTipoDocumento!=null && codTipoDocumento.equals(this.codigoRazonSocial)) {//CODIGO RAZONSOCIAL
			this.mostrarRazonSocial = true;
			objTiivsPersonaResultado.setTipDoi(this.codigoRazonSocial);
			objTiivsPersonaResultado.setApeMat("");
			objTiivsPersonaResultado.setNombre("");
		} else {
			this.mostrarRazonSocial = false;
			objTiivsPersonaResultado.setTipDoi("");
		}	
		
		this.obterPatterDelTipoDocumento(codTipoDocumento);
	}
	@SuppressWarnings("unchecked")
	private void obterPatterDelTipoDocumento(String codTipoDocumento){
		logger.debug("=== obterPatterDelTipoDocumento() ===");
		logger.debug("codTipoDocumento: "+codTipoDocumento);
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultitabla.add(Restrictions.eq("id.codMult",ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC));
		filtroMultitabla.add(Restrictions.eq("id.codElem",codTipoDocumento));
		List<TiivsMultitabla> listaMultiTabla = new ArrayList<TiivsMultitabla>();
		try {
			listaMultiTabla = multiDAO.buscarDinamico(filtroMultitabla);
			patter=listaMultiTabla.get(0).getValor4();
			logger.info("patter : "+patter);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+ "de multitablas: " , e);
		}
	}
	
	private void obtenCodRazonSocial() {					
		TiposDoiService tiposDoiService = new TiposDoiService(); 			
		codigoRazonSocial = tiposDoiService.obtenerCodPersonaJuridica();
	}
	
	public void actualizarVoucher(){
		//solicitudRegistrarT.nroVoucher
		logger.info("this.getSolicitudRegistrarT().getNroVoucher():" + this.getSolicitudRegistrarT().getNroVoucher());
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}
	
	public String descargarDocumento() {
		logger.debug("=== inicia descargarDocumento() ====");
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		String nombreDocumento = params.get("nombreArchivo");
		logger.debug("[DESCARG_DOC]-nombreDocumento: "+nombreDocumento);
		
		String rutaDocumento = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
				+ File.separator + ConstantesVisado.FILES + File.separator + nombreDocumento;
		
		logger.debug("[DESCARG_DOC]-rutaDocumento: "+rutaDocumento);
		String outputFileName = rutaDocumento;
		
		File outputPDF = new File(outputFileName);

		// Get ready to return pdf to user
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			// Open file.
			input = new BufferedInputStream(new FileInputStream(outputPDF),10240);

			// Return PDF to user
			// Init servlet response.
			response.reset();
			response.setHeader("Content-Type", "application/pdf");
			response.setHeader("Content-Length",String.valueOf(outputPDF.length()));
			response.setHeader("Content-Disposition", "attachment; filename=\""+ nombreDocumento + "\"");
			output = new BufferedOutputStream(response.getOutputStream(), 10240);

			// Write file contents to response.
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			logger.debug("finalizando OK");
			// Finalize task.
			output.flush();
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 1 al descargarDocumento:",e);
		} catch (Exception ex) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "general al descargarDocumento:",ex);
		} 
		finally {
			try {
				output.close();
			} catch (IOException e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 2 al descargarDocumento:",e);
			}
			try {
				input.close();
			} catch (IOException e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 3 al descargarDocumento:",e);
			}
		}
		FacesContext.getCurrentInstance().responseComplete();
		
		logger.debug("=== saliendo de descargarDocumento() ====");
		
		return "";		
	}
	
	public void validarOperacionesBancarias(ValueChangeEvent e){
		if(e.getNewValue()!=null){
			GenericDao<TiivsOperacionBancaria, Object> service = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			try {
				TiivsOperacionBancaria operacion = service.buscarById(TiivsOperacionBancaria.class, (Serializable) e.getNewValue());
				if(operacion!=null){
					if(operacion.getTipo().compareTo(ConstantesVisado.NO_DINERARIA)==0){
						objSolicBancaria.getId().setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
						objSolicBancaria.setImporte(0.00);
						objSolicBancaria.setImporteSoles(0.00);
						objSolicBancaria.setTipoCambio(0.00);
						bBooleanPopupTipoCambio = true;
						bBooleanMoneda = true;
						bBooleanImporte = true;
					}else{
						objSolicBancaria.getId().setMoneda("");
						objSolicBancaria.setImporte(null);
						objSolicBancaria.setImporteSoles(null);
						objSolicBancaria.setTipoCambio(0.00);
						bBooleanMoneda = false;
						bBooleanImporte = false;
					}
				}
			} catch (Exception e1) {
				logger.error("+++ Falló al buscar operacion", e1);
			}
		}
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

	public void setLstSolicBancarias(
			List<TiivsSolicitudOperban> lstSolicBancarias) {
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
	
	public boolean isbSeccionDocumentos() {
		return bSeccionDocumentos;
	}

	public void setbSeccionDocumentos(boolean bSeccionDocumentos) {
		this.bSeccionDocumentos = bSeccionDocumentos;
	}		

	public boolean isbSeccionEvaluarNivel() {
		return bSeccionEvaluarNivel;
	}

	public void setbSeccionEvaluarNivel(boolean bSeccionEvaluarNivel) {
		this.bSeccionEvaluarNivel = bSeccionEvaluarNivel;
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
	
	public boolean isbMostrarReImprimirCartaAtencion() {
		return this.bMostrarReImprimirCartaAtencion;
	}


	public void setbMostrarReImprimirCartaAtencion(boolean bMostrarReImprimirCartaAtencion) {
		this.bMostrarReImprimirCartaAtencion = bMostrarReImprimirCartaAtencion;
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

	/*
	public boolean isbcartaRevision() {
		return bcartaRevision;
	}

	public void setbcartaRevision(boolean bcartaRevision) {
		this.bcartaRevision = bcartaRevision;
	}
	*/

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

	public void setObjTiivsPersonaResultado(
			TiivsPersona objTiivsPersonaResultado) {
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

	public void setObjTiivsPersonaCapturado(
			TiivsPersona objTiivsPersonaCapturado) {
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

	public TiivsTipoSolicDocumento getSelectedTipoDocumento() {
		return selectedTipoDocumento;
	}

	public void setSelectedTipoDocumento(
			TiivsTipoSolicDocumento selectedTipoDocumento) {
		this.selectedTipoDocumento = selectedTipoDocumento;
	}

	public boolean isbBooleanPopup() {
		return bBooleanPopup;
	}

	public void setbBooleanPopup(boolean bBooleanPopup) {
		this.bBooleanPopup = bBooleanPopup;
	}

	public List<TiivsTipoSolicDocumento> getLstDocumentosXTipoSolTemp() {
		return lstDocumentosXTipoSolTemp;
	}

	public void setLstDocumentosXTipoSolTemp(
			List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp) {
		this.lstDocumentosXTipoSolTemp = lstDocumentosXTipoSolTemp;
	}

	public List<TiivsAnexoSolicitud> getLstAnexoSolicitud() {
		return lstAnexoSolicitud;
	}

	public void setLstAnexoSolicitud(List<TiivsAnexoSolicitud> lstAnexoSolicitud) {
		this.lstAnexoSolicitud = lstAnexoSolicitud;
	}

	public List<String> getAliasFilesToDelete() {
		return aliasFilesToDelete;
	}

	public void setAliasFilesToDelete(List<String> aliasFilesToDelete) {
		this.aliasFilesToDelete = aliasFilesToDelete;
	}

	public boolean isFlagUpdateOperacionSolic() {
		return flagUpdateOperacionSolic;
	}

	public void setFlagUpdateOperacionSolic(boolean flagUpdateOperacionSolic) {
		this.flagUpdateOperacionSolic = flagUpdateOperacionSolic;
	}

	public String getsMonedaImporteGlobal() {
		return sMonedaImporteGlobal;
	}

	public void setsMonedaImporteGlobal(String sMonedaImporteGlobal) {
		this.sMonedaImporteGlobal = sMonedaImporteGlobal;
	}

	public TiivsSolicitudOperban getObjSolicitudOperacionCapturadoOld() {
		return objSolicitudOperacionCapturadoOld;
	}

	public void setObjSolicitudOperacionCapturadoOld(
			TiivsSolicitudOperban objSolicitudOperacionCapturadoOld) {
		this.objSolicitudOperacionCapturadoOld = objSolicitudOperacionCapturadoOld;
	}

	public TiivsSolicitudOperban getObjSolicitudOperacionCapturado() {
		return objSolicitudOperacionCapturado;
	}

	public void setObjSolicitudOperacionCapturado(
			TiivsSolicitudOperban objSolicitudOperacionCapturado) {
		this.objSolicitudOperacionCapturado = objSolicitudOperacionCapturado;
	}

	public int getIndexUpdateOperacion() {
		return indexUpdateOperacion;
	}

	public void setIndexUpdateOperacion(int indexUpdateOperacion) {
		this.indexUpdateOperacion = indexUpdateOperacion;
	}

	public RegistroUtilesMB getObjRegistroUtilesMB() {
		return objRegistroUtilesMB;
	}

	public void setObjRegistroUtilesMB(RegistroUtilesMB objRegistroUtilesMB) {
		this.objRegistroUtilesMB = objRegistroUtilesMB;
	}

	public List<TiivsPersona> getLstTiivsPersonaResultado() {
		return lstTiivsPersonaResultado;
	}

	public void setLstTiivsPersonaResultado(
			List<TiivsPersona> lstTiivsPersonaResultado) {
		this.lstTiivsPersonaResultado = lstTiivsPersonaResultado;
	}

	public int getNumGrupo() {
		return numGrupo;
	}

	public void setNumGrupo(int numGrupo) {
		this.numGrupo = numGrupo;
	}

	public Set<TiivsAgrupacionPersona> getLstTiivsAgrupacionPersonas() {
		return lstTiivsAgrupacionPersonas;
	}

	public void setLstTiivsAgrupacionPersonas(
			Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas) {
		this.lstTiivsAgrupacionPersonas = lstTiivsAgrupacionPersonas;
	}

	public boolean isFlagUpdatePersona() {
		return flagUpdatePersona;
	}

	public void setFlagUpdatePersona(boolean flagUpdatePersona) {
		this.flagUpdatePersona = flagUpdatePersona;
	}

	public int getIndexUpdatePersona() {
		return indexUpdatePersona;
	}

	public void setIndexUpdatePersona(int indexUpdatePersona) {
		this.indexUpdatePersona = indexUpdatePersona;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getsCodDocumento() {
		return sCodDocumento;
	}

	public void setsCodDocumento(String sCodDocumento) {
		this.sCodDocumento = sCodDocumento;
	}

//	public String getUbicacionTemporal() {
//		return ubicacionTemporal;
//	}
//
//	public void setUbicacionTemporal(String ubicacionTemporal) {
//		this.ubicacionTemporal = ubicacionTemporal;
//	}

	public DocumentoTipoSolicitudDTO getSelectedDocumentoDTO() {
		return selectedDocumentoDTO;
	}

	public void setSelectedDocumentoDTO(
			DocumentoTipoSolicitudDTO selectedDocumentoDTO) {
		this.selectedDocumentoDTO = selectedDocumentoDTO;
	}

	public String getsEstadoSolicitud() {
		return sEstadoSolicitud;
	}

	public void setsEstadoSolicitud(String sEstadoSolicitud) {
		this.sEstadoSolicitud = sEstadoSolicitud;
	}

	public PDFViewerMB getPdfViewerMB() {
		return pdfViewerMB;
	}

	public void setPdfViewerMB(PDFViewerMB pdfViewerMB) {
		this.pdfViewerMB = pdfViewerMB;
	}

	public Map<Integer, TiivsSolicitudOperban> getMapSolicitudes() {
		return mapSolicitudes;
	}

	public void setMapSolicitudes(
			Map<Integer, TiivsSolicitudOperban> mapSolicitudes) {
		this.mapSolicitudes = mapSolicitudes;
	}
	
	public List<ComboDto> getLstClasificacionPersona() {
		return this.lstClasificacionPersona;
	}

	public void setLstClasificacionPersona(List<ComboDto> lstClasificacionPersona) {
		this.lstClasificacionPersona = lstClasificacionPersona;
	}

	public VisadoDocumentosMB getVisadoDocumentosMB() {
		return visadoDocumentosMB;
	}

	public void setVisadoDocumentosMB(VisadoDocumentosMB visadoDocumentosMB) {
		this.visadoDocumentosMB = visadoDocumentosMB;
	}

	public String getCadenaEscanerFinal() {
		return this.cadenaEscanerFinal;
	}

	public void setCadenaEscanerFinal(String cadenaEscanerFinal) {
		this.cadenaEscanerFinal = cadenaEscanerFinal;
	}

	public boolean isFlagMostrarACOficina() {
		return flagMostrarACOficina;
	}

	public void setFlagMostrarACOficina(boolean flagMostrarACOficina) {
		this.flagMostrarACOficina = flagMostrarACOficina;
	}

	public boolean isFlagMostrarSOMOficina() {
		return flagMostrarSOMOficina;
	}

	public void setFlagMostrarSOMOficina(boolean flagMostrarSOMOficina) {
		this.flagMostrarSOMOficina = flagMostrarSOMOficina;
	}

	public TiivsOficina1 getOficina() {
		return oficina;
	}

	public void setOficina(TiivsOficina1 oficina) {
		this.oficina = oficina;
	}

	public boolean isbMostrarMSGEstado_Reservado() {
		return bMostrarMSGEstado_Reservado;
	}

	public void setbMostrarMSGEstado_Reservado(boolean bMostrarMSGEstado_Reservado) {
		this.bMostrarMSGEstado_Reservado = bMostrarMSGEstado_Reservado;
	}

	public String getsTextoEstadoReservado() {
		return sTextoEstadoReservado;
	}

	public void setsTextoEstadoReservado(String sTextoEstadoReservado) {
		this.sTextoEstadoReservado = sTextoEstadoReservado;
	}	
	
	public String getRedirect() {		
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public boolean isMostrarRazonSocial() {
		return mostrarRazonSocial;
	}

	public void setMostrarRazonSocial(boolean mostrarRazonSocial) {
		this.mostrarRazonSocial = mostrarRazonSocial;
	}

	public String getPatter() {
		return this.patter;
	}

	public void setPatter(String patter) {
		this.patter = patter;
	}
	
	public String getTipoRegistro() {
		return this.tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}
	
	public String getPoderdante() {
		return this.poderdante;
	}

	public void setPoderdante(String poderdante) {
		this.poderdante = poderdante;
	}
	
	public String getApoderdante() {
		return this.apoderdante;
	}

	public void setApoderdante(String apoderdante) {
		this.apoderdante = apoderdante;
	}
	
	public boolean isbBooleanMoneda() {
		return bBooleanMoneda;
	}

	public void setbBooleanMoneda(boolean bBooleanMoneda) {
		this.bBooleanMoneda = bBooleanMoneda;
	}

	public boolean isbBooleanImporte() {
		return bBooleanImporte;
	}

	public void setbBooleanImporte(boolean bBooleanImporte) {
		this.bBooleanImporte = bBooleanImporte;
	}

	public boolean isbComision() {
		return bComision;
	}

	public void setbComision(boolean bComision) {
		this.bComision = bComision;
	}

	public boolean isbFlagComision() {
		return bFlagComision;
	}

	public void setbFlagComision(boolean bFlagComision) {
		this.bFlagComision = bFlagComision;
	}

	public String getGlosaComision() {
		return glosaComision;
	}

	public void setGlosaComision(String glosaComision) {
		this.glosaComision = glosaComision;
	}

	public String getDescripcionComision() {
		return descripcionComision;
	}

	public void setDescripcionComision(String descripcionComision) {
		this.descripcionComision = descripcionComision;
	}

	public TiivsHostVoucher getObjVoucher() {
		return objVoucher;
	}

	public void setObjVoucher(TiivsHostVoucher objVoucher) {
		this.objVoucher = objVoucher;
	}
	
}

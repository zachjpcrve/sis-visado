package com.hildebrando.visado.mb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
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

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;

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
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsSolicitudOperbanId;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

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
	/*
	 * @ManagedProperty(value = "#{solEdicionMB}") private SolicitudEdicionMB
	 * solicitudEdicionMB;
	 */

	private TiivsSolicitud solicitudRegistrarT;
	private TiivsSolicitud solicitudRegistrarTCopia;
	private List<TiivsSolicitudOperban> lstSolicBancarias;
	private List<TiivsAnexoSolicitud> lstAnexosSolicitudes;
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
	private boolean bMostrarCartaRevision = false;
	private boolean bRevision = false;
	private boolean flagUpdateOperacionSolic = false;
	IILDPeUsuario usuario;
	private boolean bMostrarGenerarRevision = true;
	private boolean bMostrarComentario = true;
	private boolean bMostrarCartaRechazo = false;
	private boolean bMostrarCartaImprocedente = false;
	private boolean bMostrarCartaRespuesta = false;
	private boolean bMostrarSolicitudVisado = false;
	boolean bBooleanPopupTipoCambio = true;
	boolean bBooleanPopup = false;
	private String PERFIL_USUARIO;
	private String sCodigoEstadoNivel;
	private String sNivelSolicitud;
	private String sMonedaImporteGlobal;
	private String sCodDocumento;
	private String ubicacionTemporal;
	private String sEstadoSolicitud = "";
	String cadenaEscanerFinal = "";
//	private boolean verPnlEvaluarNivel = false;
	private EvaluacionNivelesMB evaluacionNivelesMB;
	private TiivsSolicitudOperban objSolicBancaria;
	private PersonaDataModal personaDataModal;
	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaResultado;
	private TiivsPersona objTiivsPersonaSeleccionado;
	private TiivsPersona objTiivsPersonaCapturado;
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
	
	public ConsultarSolicitudMB() {		
		inicializarContructor();
		cargarDocumentos();

		//Para Incidencia 34
//		modificarTextoVentanaCartaAtencion();
//		mostrarCartaAtencion();
//		ocultarCartas();
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");

		combosMB = new CombosMB();
		combosMB.cargarMultitabla();
	}
	
	public void modificarTextoVentanaCartaAtencion() {
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");

		if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
			setTextoMensajeCartaAtencion(ConstantesVisado.MENSAJE_CARTA_ATENCION.MENSA_SSJJ);
		} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
			setTextoMensajeCartaAtencion(ConstantesVisado.MENSAJE_CARTA_ATENCION.MENSAJE_OFICINA);
		}

		logger.info("Texto Ventana: " + getTextoMensajeCartaAtencion());
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

	
	
	
	//Ref: SeguimientoPestania: Bot�n "Generar Revision"
	public void mostrarCartaRevision() {
		PERFIL_USUARIO = (String) Utilitarios
				.getObjectInSession("PERFIL_USUARIO");

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
	

	@SuppressWarnings("unchecked")
	public void validarCambioEstadoVencido() {
		int diasUtiles = 0;
		
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		
		filtroSol.add(Restrictions.or(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO, ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02),
									  Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)));
		
		List<TiivsSolicitud> solicitudes = new ArrayList<TiivsSolicitud>();
		
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes");
		}
		
		//Se obtiene los dias utiles de la Multitabla
		for (TiivsMultitabla tmp : combosMB.getLstMultitabla()) 
		{
			if (tmp.getId().getCodMult().trim().equals(ConstantesVisado.CODIGO_MULTITABLA_DIAS_UTILES)) 
			{
				diasUtiles = Integer.valueOf(tmp.getValor2());
				break;
			}
		}
		
		for (TiivsSolicitud tmpSol: solicitudes)
		{
			Date fechaSolicitud = tmpSol.getFechaEstado();

			if (tmpSol.getFechaEstado()!=null)
			{
				Date fechaLimite = aumentarFechaxVen(fechaSolicitud, diasUtiles);

				java.util.Date fechaActual = new java.util.Date();

				if (fechaActual.after(fechaLimite)) 
				{
					logger.info("Se supero el plazo. Cambiar la solicitud a estado vencido");
					
					try {
						actualizarEstadoVencidoSolicitud(tmpSol);
					} catch (Exception e) {
						logger.info("No se pudo cambiar el estado de la solicitud: " + tmpSol.getCodSoli()	+ " a vencida");
						logger.info(e.getStackTrace());
					}
				} 
				else 
				{
					logger.info("No se supero el plazo. El estado de la solicitud se mantiene");
				}
			}
		}
		
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
				|| PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
			setbMostrarComentario(false);
		}		
	}

	public void ocultarComentario() {
		setbSeccionComentario(false);
	}

	public void inicializarContructor() {
		solicitudRegistrarT = new TiivsSolicitud();

		lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
		lstAnexosSolicitudes = new ArrayList<TiivsAnexoSolicitud>();
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
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
			} else if (this.solicitudRegistrarT
				.getEstado()
				.trim()
				.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)) {
				logger.info("xxxx  ESTADO_COD_RECHAZADO_T02");
				lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02,
						ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02));
				/*lstComboDictamen.add(new ComboDto(
						ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02,
						ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02));*/
		}
		}
	}

	public String redirectDetalleSolicitud() {
		logger.info(" **** redirectDetalleSolicitud ***");
		obtenerSolicitud(Utilitarios.capturarParametro("prm_codSoli"));
		return getRedirectDetalleSolicitud();
	}
	
	public String redirectDetalleSolicitud(String codigoSolicitud) {
		logger.info(" **** redirectDetalleSolicitud ***");		
		obtenerSolicitud(codigoSolicitud);		
		return getRedirectDetalleSolicitud();
	}
	
	private String getRedirectDetalleSolicitud(){
		String redirect = "";		
		if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02)) {
			redirect = "/faces/paginas/solicitudEdicion.xhtml";
			//obtenerSolicitud();
		} else {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			request.getSession(true).setAttribute("SOLICITUD_TEMP", solicitudRegistrarT);						
			redirect = "/faces/paginas/detalleSolicitud.xhtml";
		}
		return redirect;
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
			GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			solicitudRegistrarT = service.modificar(solicitudRegistrarT);
			Utilitarios.mensajeInfo("INFO",
					"Se realiz� la reasignaci�n manual Correctamente");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void obtenerSolicitud(String codigoSolicitud){
		try {
			lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
			listaTemporalSolicitudAgrupacionesBorradores=new ArrayList<TiivsSolicitudAgrupacion>();
			//String codigoSolicitud = Utilitarios.capturarParametro("prm_codSoli");
			TiivsSolicitud solicitud;
			logger.info("codigoSolicitud : " + codigoSolicitud);
			 solicitud = new TiivsSolicitud();
			solicitud.setCodSoli(codigoSolicitud);
			
			SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
			solicitudRegistrarT = solicitudService.obtenerTiivsSolicitud(solicitud);
			
			solicitudRegistrarT.setDescEstado(Utilitarios.obternerDescripcionEstado(solicitudRegistrarT.getEstado()));
			
			
			lstSolicBancarias = solicitudService.obtenerListarOperacionesBancarias(solicitud);
			// BY SAMIRA 
			solicitudRegistrarT.setLstSolicBancarias(lstSolicBancarias);
			lstSolicBancariasCopia = new ArrayList<TiivsSolicitudOperban>();
			lstSolicBancariasCopia.addAll(lstSolicBancarias);
			 logger.info("Tamanio Lista original que se trajo de base : "+lstSolicBancariasCopia.size());
			//FIN  BY SAMIRA 
			int y = 0;
			
			for (TiivsSolicitudOperban f : lstSolicBancarias) 
			{ logger.info("id moneda .::: " +f.getId().getMoneda());
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
			
			obtenerImporteTotalxSolicitud(lstSolicBancarias);
			
			this.lstAnexosSolicitudes = solicitudService.obtenerListarAnexosSolicitud(solicitud);			
			this.iTipoSolicitud =solicitudRegistrarT.getTiivsTipoSolicitud().getCodTipSolic(); 
			
			//descargar anexos
			descargarAnexosFileServer();			
			
			boolean isEditar=false;
			if(this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02)){
				isEditar=true;
				bBooleanPopup=false;
			} else {
				isEditar=false;
				bBooleanPopup=false;
			}
			llenarListaDocumentosSolicitud(isEditar);
			
			solicitudRegistrarT.setLstDocumentos(lstdocumentos); //Para reportes
			solicitudRegistrarTCopia=new TiivsSolicitud();
			solicitudRegistrarTCopia=this.solicitudRegistrarT;
			// PODERDANTES Y APODERADOS
			List<TiivsPersona> lstPoderdantes = null;
			List<TiivsPersona> lstApoderdantes = null;
		   AgrupacionSimpleDto agrupacionSimpleDto  =null; ;
		   List<TiivsPersona>lstPersonas=null;
		   listaSolicitudAgrupacionesCopia=new ArrayList<TiivsSolicitudAgrupacion>();
		   listaSolicitudAgrupacionesCopia.addAll(this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) ;
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
				    lstPersonas.add(d.getTiivsPersona());
				   
					if(d.getTipPartic().trim().equals(ConstantesVisado.PODERDANTE))
					{
						lstPoderdantes.add(d.getTiivsPersona());
					}
					else  if(d.getTipPartic().trim().equals(ConstantesVisado.APODERADO))
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
		   		   		   
		   solicitudRegistrarT.setLstAgrupacionSimpleDto(lstAgrupacionSimpleDto); //reporte
		   
		   logger.info("Lista Poderdantes: " + lstPoderdantes.size());
		   logger.info("Lista Apoderados: " + lstApoderdantes.size());
		   logger.info("Obtener solicitud agrupaciones: " + solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
		   logger.info("Size lstAgrupacionSimpleDto: " + lstAgrupacionSimpleDto.size());
		  
			this.actualizarEstadoReservadoSolicitud();
			this.obtenerHistorialSolicitud();
			
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
							logger.info("LA SOLICITUD NO TIENE UN ESTUDIO ASIGNADO");
						}
				}
					}
			}
			// Listar ComboDictamen
			listarComboDictamen();
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION,e);
		}
	}
	
	private void llenarListaDocumentosSolicitud(boolean isEditar){
		
		String sEstado = this.solicitudRegistrarT.getEstado();
		
		//En caso de editar una solicitud
		if(isEditar){ 
			
			String scodTipoSolicitud = solicitudRegistrarT.getTiivsTipoSolicitud().getCodTipSolic();
			listarDocumentosXSolicitud(scodTipoSolicitud);
			
			
			logger.info("lstAnexosSolicitudes:" + this.lstAnexosSolicitudes);

			for(TiivsAnexoSolicitud anexo : lstAnexosSolicitudes){				
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
			for (TiivsAnexoSolicitud v : this.lstAnexosSolicitudes) 
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
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
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
			{
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
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = true;
				this.bSeccionReasignacion = true;
				this.bSeccionEvaluarNivel = false;				
				this.bMostrarGenerarRevision = false;

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
				
			}
		} else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02)) {
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
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
				
			}
			
		}else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {

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
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;

			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {

				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
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
		else if(this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){
			
			evaluacionNivelesMB = new EvaluacionNivelesMB(solicitudRegistrarT);
			TiivsSolicitudNivel solNivel = evaluacionNivelesMB.obtenerNivelSolicitud();
			if (solNivel != null) {
				this.sNivelSolicitud = solNivel.getCodNiv();
			}
			
            if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
           	
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
            	
        	} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
        		
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
				this.sNivelSolicitud = solNivel.getCodNiv();
			}
			
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
           	
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
            	
        	} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
        		
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
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = true;
				this.bMostrarCartaRevision = true;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
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
		} else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)) {
			
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
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
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
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
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
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
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
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
			
		}else if (this.solicitudRegistrarT.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02)) {
			
			if (PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO)) {
				
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
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
				this.bMostrarCartaRechazo = false;
				this.bMostrarCartaRevision = false;
				this.bMostrarCartaImprocedente = false;
				this.bSeccionDocumentos = true;
				this.bSeccionDictaminar = false;
				this.bSeccionComentario = false;
				this.bSeccionReasignacion = false;
				this.bSeccionEvaluarNivel = false;
				this.bMostrarGenerarRevision = false;
				
			} else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA)) {
				
				this.bMostrarSolicitudVisado = true;
				this.bMostrarCartaAtencion = true;
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
	}

	public void dictaminarSolicitud() 
	{
		boolean actualizarBandeja=false;
		
		logger.info("********************** dictaminarSolicitud *********************************** ");
		logger.info("********** " + valorDictamen);
		try {

			GenericDao<TiivsSolicitud, Object> serviceS = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			
			if (this.PERFIL_USUARIO.trim().equals(ConstantesVisado.ABOGADO)	|| this.PERFIL_USUARIO.trim().equals(ConstantesVisado.SSJJ)) 
			{
				if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) 
				{
					// Llamada a los Niveles

					this.agregarNiveles(solicitudRegistrarT);
					this.bSeccionDictaminar = false;
					this.solicitudRegistrarT = serviceS
							.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					actualizarBandeja=true;
					Utilitarios.mensajeInfo("INFO",	"Se dictamin� correctamente la solicitud");

				} else if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)) {

					this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
					solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					actualizarBandeja=true;
					bMostrarCartaRechazo=true;
					Utilitarios.mensajeInfo("INFO",	"Se dictamin� correctamente la solicitud");

				} else if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {

					this.agregarNiveles(solicitudRegistrarT);
					this.bSeccionDictaminar = false;
					this.solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					actualizarBandeja=true;
					Utilitarios.mensajeInfo("INFO",	"Se dictamin� correctamente la solicitud");

				} else if (this.valorDictamen.equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)) {

					this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02);
					solicitudRegistrarT = serviceS.modificar(solicitudRegistrarT);
					this.registrarHistorial(solicitudRegistrarT);
					actualizarBandeja=true;
					bMostrarCartaImprocedente=true;
					Utilitarios.mensajeInfo("INFO",	"Se dictamin� correctamente la solicitud");

				}
				if (this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
					bSeccionCartaAtencion = true;
					bSeccionComentario = false;
				} else if (this.solicitudRegistrarT.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
					if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)) {
						this.bMostrarCartaRechazo = true;
						this.bMostrarCartaRevision = true;
						this.bMostrarCartaAtencion = true;
						this.bSeccionComentario = false;
					}
				}

				this.obtenerHistorialSolicitud();
				
				if (actualizarBandeja)
				{
					this.seguimientoMB.busquedaSolicitudxCodigo(solicitudRegistrarT.getCodSoli());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 public List<String> ObtenerNivelesXMoneda(TiivsSolicitud solicitud) throws Exception {
				logger.info("****************** MONEDA  ******************** " + solicitud.getMoneda());
				GenericDao<TiivsNivel, Object> service = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				List<String> lstCodNivel = new ArrayList<String>();
				
	            Busqueda filtro = Busqueda.forClass(TiivsNivel.class);
	            		 filtro.add(Restrictions.eq("moneda",solicitud.getMoneda().trim()));
	            		 filtro.addOrder(Order.asc("codNiv"));
	            		 
				List<TiivsNivel> lstNiveles = service.buscarDinamico(filtro);
				 for (TiivsNivel z : lstNiveles) {
					 logger.info("************** NIVEL   : " +z.getCodNiv());
				    }
				logger.info("************** lstNiveles  : " +lstNiveles.size());
				logger.info("************** solicitud.getImporte(): " +solicitud.getImporte());
				logger.info("************** lstNiveles.get(0).getRangoInicio() : " +lstNiveles.get(0).getRangoInicio());

				if (solicitud.getImporte() >= lstNiveles.get(0).getRangoInicio()) 
				{
					logger.info("Paso Nivel " + lstNiveles.get(0).getDesNiv());
					 System.out.println("a"+ lstNiveles.get(0).getDesNiv());
					 System.out.println("this.solicitudRegistrarT.getImporte() !"+ this.solicitudRegistrarT.getImporte());
					 System.out.println("lstNiveles.get(lstNiveles.size() - 1).getRangoFin() !"+ lstNiveles.get(lstNiveles.size() - 1).getRangoFin());
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
					logger.info("El importe supero el rango de inicio . " +lstNiveles.get(lstNiveles.size() - 1).getRangoFin());
					lstCodNivel.add("SPRI");
				}

		return lstCodNivel;

	 }
	
	@SuppressWarnings({ "unchecked", "unused" })
	public void agregarNiveles(TiivsSolicitud solicitud) throws Exception 
	{
		logger.info("*********************************** agregarNiveles ********************************************");
		List<String> lstCodNivel = new ArrayList<String>();
		lstCodNivel=ObtenerNivelesXMoneda(solicitud);
				logger.info(" ******************** Tamanio de la lista de Niveles *************** para probar: " + lstCodNivel.size());
		if(lstCodNivel==null){
			String mensaje = "DATA INCONCISTENTE, EL RANGO FINAL DE TODOS LOS NIVELES , NO DEBE SER MENOR AL RANGO DE INICIO.";
			Utilitarios.mensajeInfo("INFO", mensaje);
			
		}
		else if (lstCodNivel.size() > 0) {
			// SI LA SOLICITUD SOPERA ALGUN NIVEL, ENTONCES PASA A ESTADO EN
			// VERIFICACION A, SI NO A ACEPTADO
			logger.info(" ******  LA SOLICITUD SUPERO ALGUN NIVEL, ENTONCES PASA A ESTADO EN  VERIFICACION A, SI NO A ACEPTADO");
			if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02);
			} else if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02);
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
				//soliNivel.setEstadoNivel(ConstantesVisado.ESTADOS.ESTADO_COD_Pendiente_T09);
				soliNivel.setEstadoNivel(null);
				soliNivel.setUsuarioRegistro(usuario.getUID());
				soliNivel.setFechaRegistro(new Timestamp(Calendar.DATE));
				
				logger.info("************************************************************");
				logger.info("Datos del nivel a insertar: ");
				logger.info("Cod Soli: " + soliNivel.getTiivsSolicitud().getCodSoli());
				logger.info("Cod Nivel: " + soliNivel.getCodNiv());
				logger.info("Estado Solicitud: " + soliNivel.getEstadoSolicitud());
				logger.info("Estado Nivel: " + soliNivel.getEstadoNivel());
				logger.info("Usuario registro: " + soliNivel.getUsuarioRegistro());
				logger.info("Fecha registro: " + soliNivel.getFechaRegistro());
				logger.info("************************************************************");
				
				serviceSolicitud.insertar(soliNivel);
			}

		} else if (lstCodNivel.size() == 0) {
			if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
			} else if (this.valorDictamen.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)) {
				solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02);
			}
		}

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
		logger.info("Numero de Movimiento a registrar para el CodSolicitud : "
				+ solicitud.getCodSoli());
		TiivsHistSolicitud objHistorial = new TiivsHistSolicitud();
		objHistorial.setId(new TiivsHistSolicitudId(solicitud.getCodSoli(),
				numeroMovimiento));
		objHistorial.setEstado(solicitud.getEstado());
		objHistorial.setNomUsuario(usuario.getNombre());
		objHistorial.setObs(solicitud.getObs());
		objHistorial.setFecha(new Timestamp(new Date().getTime()));
		objHistorial.setRegUsuario(usuario.getUID());
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
			this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_EJECUTADO_T02);

			GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			service.modificar(solicitudRegistrarT);

			this.registrarHistorial(solicitudRegistrarT);
			this.obtenerHistorialSolicitud();
			this.seguimientoMB.busquedaSolicitudes();
		}
	}

	public void actualizarEstadoEnRevisionSolicitud() throws Exception {
		logger.info("*********************** actualizarEstadoEnRevisionSolicitud **************************");

		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02);
		this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_EN_REVISION_T02);

		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		service.modificar(solicitudRegistrarT);

		this.registrarHistorial(solicitudRegistrarT);
		this.obtenerHistorialSolicitud();
		this.seguimientoMB.busquedaSolicitudxCodigo(solicitudRegistrarT.getCodSoli());
	}

	public void actualizarEstadoVencidoSolicitud(TiivsSolicitud solicitud) throws Exception {
		logger.info("*********************** actualizarEstadoVencidoSolicitud **************************");

		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02);
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

	public void obtenerHistorialSolicitud() {
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + solicitudRegistrarT.getCodSoli());

		String sCodSolicitud = solicitudRegistrarT.getCodSoli();
		try {
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
			filtroHist.add(Restrictions.eq("id.codSoli", sCodSolicitud));
			filtroHist.addOrder(Order.desc("fecha"));

			List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
			lstHist = histDAO.buscarDinamico(filtroHist);

			logger.info("Numero de registros encontrados:" + lstHist.size());

			if (lstHist != null && lstHist.size() > 0) {
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
					lstSeguimientoDTO.add(seg);
				}
			}
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el historial de la solicitud");
			exp.printStackTrace();
		}

	}

	public String buscarEstadoxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstado().size(); i++) {
			if (combosMB.getLstEstado().get(i).getCodEstado()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstado().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}

	public boolean descargarAnexosFileServer() 
	{
		logger.info("********************** descargarAnexosFileServer():INICIO ********************************");

		boolean iRet = true;

		String ubicacionTemporal = Utilitarios.getProjectPath()
				+ File.separator + ConstantesVisado.FILES + File.separator;
		logger.debug("ubicacion temporal " + ubicacionTemporal);

		for (TiivsAnexoSolicitud a : this.lstAnexosSolicitudes) 
		{
			File fileTemporal = new File(ubicacionTemporal
					+ a.getAliasTemporal());
			if (!fileTemporal.exists()) 
			{
				logger.info("Archivo no existe se descargara:" 	+ a.getAliasArchivo());

				File fichTemp = null;
				boolean bSaved = false;
				try {
					File fDirectory = new File(ubicacionTemporal);
					if (!fDirectory.exists()) {
						fDirectory.mkdirs();
					}

					String fileName = a.getId().getCodSoli() + "_" + a.getAliasArchivo();
					String extension = fileName.substring(fileName
							.lastIndexOf("."));
					String sNombreTemporal = "";
					fichTemp = File.createTempFile("temp", extension, new File(
							ubicacionTemporal));
					sNombreTemporal = fichTemp.getName().substring(
							1 + fichTemp.getName().lastIndexOf(File.separator));
					logger.debug("sNombreTemporal: " + sNombreTemporal);

					PDFViewerMB pdfViewerMB = new PDFViewerMB();
					if (pdfViewerMB.descargarArchivo(
							fichTemp.getAbsolutePath(), fileName)) {
						a.setAliasTemporal(sNombreTemporal);
					} else {
						a.setAliasTemporal("");
						logger.debug("Archivo no existe en el File Server");
					}
					bSaved = true;

				} catch (IOException e) {
					logger.debug("Error al descargar archivo: "
							+ a.getAliasArchivo());
					logger.debug(e.toString());
					e.printStackTrace();
					bSaved = false;
				} finally {
					fichTemp.deleteOnExit(); // Delete the file when the
												// JVM terminates
				}
				if (bSaved) {
					GenericDao<TiivsAnexoSolicitud, Object> anexoDAO = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit
							.getApplicationContext().getBean("genericoDao");
					try {
						anexoDAO.modificar(a);
					} catch (Exception ex) {
						logger.debug("No se actualizar� el anexo "
								+ ex.getMessage());
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
			e.printStackTrace();
			logger.debug("Error al cargar el listado de territorios");
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

	public boolean validarRegistroSolicitud() throws Exception {
		boolean retorno = true;
		String mensaje = "";
		/*
		 * if(solicitudRegistrarT.getTiivsOficina1()==null){
		 * //solicitudRegistrarT.getTiivsOficina1().getCodOfi().equals(""))
		 * 
		 * mensaje="Ingrese la Oficina"; retorno=false;
		 * Utilitarios.mensajeInfo("INFO", mensaje); }
		 * if(solicitudRegistrarT.getNroVoucher
		 * ()==null||solicitudRegistrarT.getNroVoucher().equals("")){
		 * mensaje="Ingrese el Nro Voucher"; retorno=false;
		 * Utilitarios.mensajeInfo("INFO", mensaje); }else
		 * if(solicitudRegistrarT.getNroVoucher().length()<11){
		 * mensaje="Ingrese Nro Voucher correcto de 11 digitos"; retorno=false;
		 * Utilitarios.mensajeInfo("INFO", mensaje); }
		 */
		
		/*if (solicitudRegistrarT.getTiivsSolicitudAgrupacions().size()==0)
		{
			this.obtenerSolicitud();
		}*/
		if (solicitudRegistrarT.getTiivsSolicitudAgrupacions().size() == 0) {
			mensaje = "Ingrese la secci�n Apoderado y Poderdante";
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
						
					}
				}
				if(contPoderdante==0||contApoderado==0){
					//retorno= false;
					Utilitarios.mensajeInfo("INFO", "Ingrese por lo menos un Poderdante y un Apoderado, por cada Combinaci�n");
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
		
		for (TiivsTipoSolicDocumento b : lstTipoSolicitudDocumentos) {
			if(b.getObligatorio().equals("1")){
				mensaje = "Ingrese los documentos Obligatorios";
				retorno = false;
				Utilitarios.mensajeInfo("INFO", mensaje);
				break;
			}
		}
		
		if (this.lstSolicBancarias.size() == 0) {

			mensaje = "Ingrese al menos una Operaci�n Bancaria";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		/*if (!this.validarNroVoucher()) {
			retorno = false;
		}*/
		return retorno;
	}

	@SuppressWarnings({ "unchecked", "null" })
	public boolean validarNroVoucher() throws Exception {
		boolean booleano = true;
		if (!this.sEstadoSolicitud.equals("BORRADOR")) {
			String mensaje = "Ingrese un Nro de Vourcher no registrado ";
			Busqueda filtroNroVoucher = Busqueda.forClass(TiivsSolicitud.class);
			GenericDao<TiivsSolicitud, String> serviceNroVoucher = (GenericDao<TiivsSolicitud, String>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			List<TiivsSolicitud> lstSolicitud = new ArrayList<TiivsSolicitud>();
			lstSolicitud = serviceNroVoucher.buscarDinamico(filtroNroVoucher);
			if (lstSolicitud != null) {
				for (TiivsSolicitud a : lstSolicitud) {
					if (a != null || !a.equals("")) {
						if (a.getNroVoucher() != (null)) {
							if (a.getNroVoucher().equals(
									this.solicitudRegistrarT.getNroVoucher())) {
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
		this.solicitudRegistrarT.setFechaRespuesta(time);
		this.solicitudRegistrarT.setFechaEnvio(new Timestamp(new Date().getTime()));
	}

	public boolean cargarArchivosFTP() {

		logger.info("************cargarArchivosFTP()*�**************");

		boolean exito = true;
		String sUbicacionTemporal = getUbicacionTemporal();
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

	public void eliminarArchivosTemporales() {
		logger.info("************eliminarArchivosTemporales()*�**************");
		logger.info("Archivos a eliminar:" + aliasFilesToDelete.size());
		File fileToDelete = null;

		for (String sfile : aliasFilesToDelete) {
			logger.debug("borrar archivo: " + this.getUbicacionTemporal()
					+ sfile);
			fileToDelete = new File(this.getUbicacionTemporal() + sfile);
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
	@Transactional
	public void registrarSolicitud() 
	{
		String mensaje = "";
		boolean actualizarBandeja=false;
		
		logger.info("*********************** registrarSolicitud ************************");
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		
		GenericDao<TiivsSolicitudAgrupacion, Object> serviceSolAgru = (GenericDao<TiivsSolicitudAgrupacion, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		TiivsPersona objPersonaRetorno = new TiivsPersona();
		TiivsAgrupacionPersona objAgruPer = new TiivsAgrupacionPersona();
		
		try {
			logger.info("this.solicitudRegistrarT.importe : " + this.solicitudRegistrarT.getMoneda());
			this.solicitudRegistrarT.setFecha(new Date());
			this.solicitudRegistrarT.setEstado(this.solicitudRegistrarT.getEstado().trim());

			logger.info("usuario.getUID() " + usuario.getUID());
			this.solicitudRegistrarT.setRegUsuario(usuario.getUID());
			this.solicitudRegistrarT.setNomUsuario(usuario.getNombre());
			logger.info("tiivsOficina1.codOfi ::::::: "	+ this.solicitudRegistrarT.getTiivsOficina1().getCodOfi());
			
			for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) 
			{
				if (tiivsOficina1.getCodOfi().equals(this.solicitudRegistrarT.getTiivsOficina1().getCodOfi())) 
				{
					this.solicitudRegistrarT.setTiivsOficina1(tiivsOficina1);
				}
			}

			logger.info("solicitudRegistrarT.getTiivsSolicitudAgrupacions() : " + solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
			logger.info("this.sEstadoSolicitud " + this.sEstadoSolicitud);
			
			if (this.validarRegistroSolicitud()) 
			{
				if (!this.sEstadoSolicitud.equals("BORRADOR"))  //ssjj
				{
					this.enviarSolicitudSSJJ();
					logger.info("ESTUDIOOOO : " +solicitudRegistrarT.getTiivsEstudio().getCodEstudio());
					actualizarBandeja=true;
				}else{
					//borrador
					this.solicitudRegistrarT.setTiivsEstudio(null);
				}
								
				Set<TiivsSolicitudAgrupacion> listaTMP = this.solicitudRegistrarT.getTiivsSolicitudAgrupacions();
				logger.info("listaTMP listaTMP "+listaTMP.size());
			
				if(listaTemporalAgrupacionesPersonaBorradores!=null){
				for (TiivsAgrupacionPersona c : listaTemporalAgrupacionesPersonaBorradores) {
					serviceAgru.eliminar(c);
				}
				}
				for (TiivsSolicitudAgrupacion d : listaTemporalSolicitudAgrupacionesBorradores){
					
					for (TiivsAgrupacionPersona e : d.getTiivsAgrupacionPersonas()) {
						serviceAgru.eliminar(e);
					}
					
					serviceSolAgru.eliminar(d);
				}
				
				
				for (TiivsSolicitudAgrupacion x1 : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) 
				{
					if (!existeSolicitudAgrupacion(x1))
					  {
						  serviceSolAgru.insertar(x1);
					  }
					
					logger.debug("Tamanio agrupaciones: " + x1.getTiivsAgrupacionPersonas().size());
					TiivsPersona personaTemporal=null;
					for (TiivsAgrupacionPersona b :x1.getTiivsAgrupacionPersonas()) 
				    { logger.info("b.getTiivsPersona() "+b.getTiivsPersona());
				      personaTemporal=b.getTiivsPersona();
					  objPersonaRetorno=servicePers.insertarMerge(personaTemporal);
					  logger.info("Codigo de la persona a Insertar : "+objPersonaRetorno.getCodPer());
					  b.setTiivsPersona(null);
					  b.setCodPer(objPersonaRetorno.getCodPer());
					  
					  if (existeAgrupacionPersona(b))
					  {
						  serviceAgru.modificar(b);
					  }
					  else
					  {
						  serviceAgru.insertar(b);
					  }
					  b.setTiivsPersona(personaTemporal);
				   }
				}
																				
				TiivsSolicitud objResultado = service.insertarMerge(this.solicitudRegistrarT);
								  
				// Carga ficheros al FTP
				boolean bRet = cargarArchivosFTP();
				logger.info("Resultado de carga de archivos al FTP:" + bRet);
				// Elimina archivos temporales
				eliminarArchivosTemporales();

				for (TiivsAnexoSolicitud n : this.lstAnexoSolicitud) 
				{
					logger.info("nnnnnnnnnnnnnnnnnnnnnnn " + n.getAliasArchivo());
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
				depurarTablas();
				
				if (objResultado.getCodSoli() != "" || objResultado != null) 
				{
					if (this.sEstadoSolicitud.equals("BORRADOR")) 
					{
						mensaje = "Se registro correctamente la Solicitud con codigo : " + objResultado.getCodSoli() + " en Borrador";
						Utilitarios.mensajeInfo("INFO", mensaje);
						actualizarBandeja=true;
					} 
					else 
					{
						mensaje = "Se envio a SSJJ correctamente la Solicitud con codigo : " + objResultado.getCodSoli();
						Utilitarios.mensajeInfo("INFO", mensaje);
						actualizarBandeja=true;
					}

				} 
				else 
				{
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
				}

				logger.info("objResultado.getCodSoli(); " + objResultado.getCodSoli());
				logger.info("this.solicitudRegistrarT.importe : " + this.solicitudRegistrarT.getImporte());
				
				if (actualizarBandeja)
				{
					this.seguimientoMB.busquedaSolicitudxCodigo(objResultado.getCodSoli());
				}
				
			}
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+e.getMessage());
			Utilitarios.mensajeError("ERROR", "Ocurrio un Error al grabar la Solicitud");
			e.printStackTrace();

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
	
	public Boolean existeAgrupacionPersona(TiivsAgrupacionPersona objAgrupacion)
	{
		Boolean existe=false;
		
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		
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
			ex.printStackTrace();
			logger.debug("Error al buscar la agrupacion de personas");
		}
		
		if (lista.size()>0)
		{
			existe=true;
		}
		
		return existe;
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
			ex.printStackTrace();
			logger.debug("Error al buscar la solicitud agrupacion");
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
			ex.printStackTrace();
			logger.debug("Error al buscar la agrupacion de personas");
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
	public void verEditarAgrupacion(){
		logger.info("********************** verEditarAgrupacion *********************************** ");
		logger.info("this.getCodSoli  "+ this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.getNumGrupo  "+ this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		logger.info("this.getLstPersonas  "+ this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
		
		 combosMB=new CombosMB();
		  lstClasificacionPersona=combosMB.getLstClasificacionPersona();
		  logger.info("tamanioo actual de la lista de Clasificacion **** " +lstClasificacionPersona.size());
		
		for (int i = 0; i < lstAgrupacionSimpleDto.size(); i++) {
           if(lstAgrupacionSimpleDto.get(i).equals(objAgrupacionSimpleDtoCapturado)){
        	   indexUpdatePoderdanteApoderado=i;
        	   numGrupoUpdatePoderdanteApoderado=lstAgrupacionSimpleDto.get(i).getId().getNumGrupo();
        	   for (TiivsSolicitudAgrupacion a : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
				if(a.getId().getNumGrupo()==numGrupoUpdatePoderdanteApoderado){
					lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
					lstTiivsAgrupacionPersonas.addAll(a.getTiivsAgrupacionPersonas());
					//lstTiivsAgrupacionPersonas=a.getTiivsAgrupacionPersonas();
				}
			   }
        	   break;
			}
		}
		/**Recien se empieza a llenar la lista de Persona */
		
		setLstTiivsPersona(objAgrupacionSimpleDtoCapturado.getLstPersonas());
		
		for (AgrupacionSimpleDto dd: this.solicitudRegistrarTCopia.getLstAgrupacionSimpleDto()) {
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
		 int primerNumeroGrupo=0, NumeroGrupoMax=0;
		 primerNumeroGrupo= lstAgrupacionSimpleDto.get(0).getId().getNumGrupo();
		 for (int i = 0; i < lstAgrupacionSimpleDto.size(); i++) {
			 logger.info("lstAgrupacionSimpleDto.get(i).getId().getNumGrupo() :: " +lstAgrupacionSimpleDto.get(i).getId().getNumGrupo());
			 if(lstAgrupacionSimpleDto.get(i).getId().getNumGrupo()>=primerNumeroGrupo) {
				 NumeroGrupoMax=lstAgrupacionSimpleDto.get(i).getId().getNumGrupo();
			 }else{
				 NumeroGrupoMax=primerNumeroGrupo;
			 }
		}
		
		 numGrupo=NumeroGrupoMax;
		 logger.info("El maximo numero de Grupo :: " +numGrupo);
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

	

	public void agregarAgrupacion(){
		  if(validarAgregarAgrupacion()){
			  logger.info("***************************** agregarAgrupacion ***************************************");
			  if(!flagUpdatePoderdanteApoderados){// INDICA QUE ES UN REGISTRO NUEVO
				
		  numGrupo=numGrupo+1;
		  logger.info("********************** agregarAgrupacion ********************* " +numGrupo);
		  List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
		  List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
		  
		  if (lstTiivsAgrupacionPersonas == null)
		  {
			  lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
		  }
		  logger.info(" lstTiivsAgrupacionPersonas antes  : "+lstTiivsAgrupacionPersonas.size());
		  logger.info("****** Tamanio *** lstTiivsPersona ********************* " +lstTiivsPersona.size());
		  for (TiivsPersona n : lstTiivsPersona) {
			  if(n.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
				  lstPoderdantes.add(n);logger.info(" poderdante : "+n.getCodPer());}
			  if(n.getTipPartic().equals(ConstantesVisado.APODERADO)){
				  lstApoderdantes.add(n);logger.info(" apoderado : "+n.getCodPer());}
			  
			  TiivsAgrupacionPersona  tiivsAgrupacionPersona =new TiivsAgrupacionPersona();
			  tiivsAgrupacionPersona.setNumGrupo(numGrupo);
			  tiivsAgrupacionPersona.setCodSoli(solicitudRegistrarT.getCodSoli());
			  tiivsAgrupacionPersona.setCodPer(n.getCodPer());
			  tiivsAgrupacionPersona.setClasifPer(n.getClasifPer());
			  tiivsAgrupacionPersona.setTipPartic(n.getTipPartic());
			  tiivsAgrupacionPersona.setTiivsPersona(n);
			  this.lstTiivsAgrupacionPersonas.add(tiivsAgrupacionPersona);
		}  
		  logger.info(" lstTiivsAgrupacionPersonas despues  : "+lstTiivsAgrupacionPersonas.size());
		  this.solicitudRegistrarT.setLstAgrupacionSimpleDto(lstAgrupacionSimpleDto);
		  logger.info("lstPoderdantes " +lstPoderdantes.size());
		   logger.info("lstApoderdantes " +lstApoderdantes.size());
		   AgrupacionSimpleDto agrupacionSimpleDto =new AgrupacionSimpleDto();
		   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudRegistrarT.getCodSoli(), numGrupo));
		   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
		   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
		   agrupacionSimpleDto.setsEstado(ConstantesVisado.ESTADOS.ESTADO_ACTIVO);
		   agrupacionSimpleDto.setiEstado(Integer.parseInt(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO));
		   agrupacionSimpleDto.setLstPersonas(this.lstTiivsPersona);
		   logger.info("Tamanio Lista Personas: " + lstTiivsPersona.size());
		   logger.info("Tamanio Lista Agrupacion Personas: " + lstTiivsAgrupacionPersonas.size());
		   lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
		  
		   solicitudRegistrarT.setTiivsSolicitudAgrupacions(this.agregarSolicitudArupacion(solicitudRegistrarT,numGrupo,lstTiivsAgrupacionPersonas ));
		  //solicitudRegistrarT.setTiivsSolicitudAgrupacions(lstTiivsAgrupacionPersonas );
		   logger.info("tamanio de lstTiivsAgrupacionPersonas "+lstTiivsAgrupacionPersonas.size());
		   lstTiivsPersona=new ArrayList<TiivsPersona>();
		   lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
			logger.info("Tamanio de la lista Solicitud Agrupacion : " +solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
		   this.llamarComision();
		   numGrupoUpdatePoderdanteApoderado=0;
		   numGrupo=0;
		   bBooleanPopup=false;
		   
			  } else if(flagUpdatePoderdanteApoderados){ //INDICA QUE SE VA A MODIFICAR una agrupacion 
				 logger.info("SE MODIFICARA " +indexUpdatePoderdanteApoderado);
				 logger.info(" La lista de Personas que se tiene en memoria " +lstTiivsPersona.size());
				 logger.info(" La lista de Personas que se tiene en memoria " +objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
				 logger.info(" lstAgrupacionSimpleDto " +lstAgrupacionSimpleDto.size());
				 TiivsAgrupacionPersona  tiivsAgrupacionPersona=null;
				 //objAgrupacionSimpleDtoCapturado.getLstPersonas()
				 for (AgrupacionSimpleDto x : lstAgrupacionSimpleDto) {
					
				}
				 
				  List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
				  List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
				  Set<TiivsAgrupacionPersona> lstTempAgrupacion=null;
				  for (TiivsPersona objTiivsPersonaResultado : lstTiivsPersona) {
					  
					  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
						  lstPoderdantes.add(objTiivsPersonaResultado);}
					  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.APODERADO)){
						  lstApoderdantes.add(objTiivsPersonaResultado);}
					    tiivsAgrupacionPersona =new TiivsAgrupacionPersona();
					  //logger.info("numGrupo cambiar esto  " +numGrupoUpdatePoderdanteApoderado);
					  tiivsAgrupacionPersona.setNumGrupo(numGrupoUpdatePoderdanteApoderado);
					  tiivsAgrupacionPersona.setCodSoli(solicitudRegistrarT.getCodSoli());
					  tiivsAgrupacionPersona.setCodPer(objTiivsPersonaResultado.getCodPer());
					  tiivsAgrupacionPersona.setClasifPer(objTiivsPersonaResultado.getClasifPer());
					  tiivsAgrupacionPersona.setTipPartic(objTiivsPersonaResultado.getTipPartic());
					  tiivsAgrupacionPersona.setTiivsPersona(objTiivsPersonaResultado);
					  lstTempAgrupacion=new HashSet<TiivsAgrupacionPersona>();
					  for (TiivsAgrupacionPersona  x : lstTiivsAgrupacionPersonas) {
						  if(!x.equals(tiivsAgrupacionPersona)){
							  lstTempAgrupacion.add(tiivsAgrupacionPersona);}
					}
				}
				  //lstTiivsAgrupacionPersonas.addAll(lstTempAgrupacion);
				  logger.info(" lstAgrupacionSimpleDto despues " +lstAgrupacionSimpleDto.size());
				  logger.info("lstTempAgrupacion  tamanio" +lstTempAgrupacion.size());
				  logger.info("lstPoderdantes " +lstPoderdantes.size());
				  logger.info("lstApoderdantes " +lstApoderdantes.size());
				  logger.info(" lstTiivsAgrupacionPersonas " +lstTiivsAgrupacionPersonas.size());
				  AgrupacionSimpleDto agrupacionSimpleDto =null;
				for (TiivsSolicitudAgrupacion x : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
					lstTiivsAgrupacionPersonas.addAll(x.getTiivsAgrupacionPersonas());
					
					logger.info(" lstTiivsAgrupacionPersonas " +lstTiivsAgrupacionPersonas.size());
					for (TiivsAgrupacionPersona c : lstTiivsAgrupacionPersonas) {
						   agrupacionSimpleDto =new AgrupacionSimpleDto();
						   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(c.getCodSoli(), c.getNumGrupo()));
						   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
						   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
						   agrupacionSimpleDto.setsEstado(ConstantesVisado.ESTADOS.ESTADO_ACTIVO);
						   agrupacionSimpleDto.setiEstado(Integer.parseInt(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO));
						   agrupacionSimpleDto.setLstPersonas(this.lstTiivsPersona);
						   this.lstAgrupacionSimpleDto.set(indexUpdatePoderdanteApoderado, agrupacionSimpleDto);
					}
					 
				} 
				
			  }
		  
		  
		  } 
		  
	 }

	public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(int iNumGrupo) {
		logger.info("iNumGrupo : " + iNumGrupo);

		Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion = new HashSet<TiivsSolicitudAgrupacion>();
		lstSolicitudArupacion = this.solicitudRegistrarT
				.getTiivsSolicitudAgrupacions();
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

	public void eliminarArupacion() {
		logger.info("********************** eliminarArupacion *********************************** ");

		this.lstAgrupacionSimpleDto.remove(this.objAgrupacionSimpleDtoCapturado);
		Set<TiivsSolicitudAgrupacion> lstSolicitudAgrupacion = (Set<TiivsSolicitudAgrupacion>) this.solicitudRegistrarT.getTiivsSolicitudAgrupacions();

		logger.info("Tamanio de la lista Solicitud Agrupacion : " + lstSolicitudAgrupacion.size());
		
		for (TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion : lstSolicitudAgrupacion) 
		{
			if (tiivsSolicitudAgrupacion.getId().equals(this.objAgrupacionSimpleDtoCapturado.getId())) 
			{
				lstSolicitudAgrupacion.remove(tiivsSolicitudAgrupacion);
				break;
			}
		}
		
				
		 for (TiivsSolicitudAgrupacion x : listaSolicitudAgrupacionesCopia) {
			 if (x.getId().equals(this.objAgrupacionSimpleDtoCapturado.getId())) {
					logger.info(" Ya existe en base de datos");
					/** Agregar la persona a la lista de temporal a eliminar*/
					listaTemporalSolicitudAgrupacionesBorradores.add(x);
			 }else{
					// Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita
					logger.info(" Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita");
				}
			}

			logger.info(" listaTemporalSolicitudAgrupacionesBorradores.size() "+	listaTemporalSolicitudAgrupacionesBorradores.size());
		logger.info("Tamanio de la lista Solicitud Agrupacion : " + lstSolicitudAgrupacion.size());
		this.llamarComision();
		this.objAgrupacionSimpleDtoCapturado = new AgrupacionSimpleDto();
	}

	public void listarDocumentosXSolicitud(ValueChangeEvent e) {
		// logger.info("ValuechanceEvent :  " + e.getNewValue());		
		String sCodTipoSol = (String) e.getNewValue();		
		listarDocumentosXSolicitud(sCodTipoSol);				
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
			logger.info("Error al cargar el listado de documentos:",ex);			
		}
		
		//eramos
		this.visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(this.lstTipoSolicitudDocumentos));
	}

	public void actualizarListadoDocumentos() {

		addArchivosTemporalesToDelete();

		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();

		for (TiivsTipoSolicDocumento s : lstTipoSolicitudDocumentos) {
			if (s.getObligatorio() != null && s.getObligatorio().equals("1")) {								
				DocumentoTipoSolicitudDTO doc = new  DocumentoTipoSolicitudDTO();
				doc.setItem(s.getId().getCodDoc());
				doc.setDocumento(s.getTiivsDocumento().getDescripcion());
				doc.setObligacion(true + "");
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

		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
	}

	public void addArchivosTemporalesToDelete() {
		for (TiivsAnexoSolicitud a : lstAnexoSolicitud) {
			aliasFilesToDelete.add(a.getAliasTemporal());
		}
	}

	public String prepararURLEscaneo() {

		usuario.setUID("P014773");
		for (TiivsParametros tmp : pdfViewerMB.getLstParametros()) {
			cadenaEscanerFinal = tmp.getUrlAPP() + "?" + "idEmpresa="
					+ tmp.getIdEmpresa() + "&" + "idSistema="
					+ tmp.getIdSistema() + "&" + "txLogin=" + usuario.getUID();
		}

		logger.info("URL APPLET: " + cadenaEscanerFinal);

		return cadenaEscanerFinal;
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

			String sAliasTemporal = cargarUnicoPDF();

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
	

	public String cargarUnicoPDF() {

		if (file == null) {
			Utilitarios.mensajeInfo("", "No se ha seleccionado ning�n archivo");
			return "";
		}

		byte fileBytes[] = getFile().getContents();

		File fichTemp = null;
		String sUbicacionTemporal = "";
		String sNombreTemporal = "";
		FileOutputStream canalSalida = null;

		try {

			// Obteniendo ubicaci�n del proyecto
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			sUbicacionTemporal = request.getRealPath(File.separator)
					+ File.separator + "files" + File.separator;
			this.setUbicacionTemporal(sUbicacionTemporal);

			logger.debug("ubicacion temporal " + sUbicacionTemporal);

			File fDirectory = new File(sUbicacionTemporal);
			fDirectory.mkdirs();

			String extension = file.getFileName().substring(
					getFile().getFileName().lastIndexOf("."));

			fichTemp = File.createTempFile("temp", extension, new File(
					sUbicacionTemporal));

			sNombreTemporal = fichTemp.getName();

			logger.debug("Nombre temporal de archivo:  " + sNombreTemporal);

			canalSalida = new FileOutputStream(fichTemp);
			canalSalida.write(fileBytes);

			canalSalida.flush();
			return sNombreTemporal;

		} catch (IOException e) {
			logger.debug("error anexo " + e.toString());
			String sMensaje = "Se produjo un error al adjuntar fichero";
			Utilitarios.mensajeInfo("", sMensaje);
			return "";
		} finally {

			fichTemp.deleteOnExit(); // Delete the file when the
			// JVM terminates

			if (canalSalida != null) {
				try {
					canalSalida.close();
				} catch (IOException x) {
					// handle error
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
		
		for (TiivsAnexoSolicitud anexo : lstAnexoSolicitud) {
			if (anexo.getId().getCodDoc().equals(selectedDocumentoDTO.getItem())) {
				lstAnexoSolicitud.remove(anexo);
				this.aliasFilesToDelete.add(anexo.getAliasTemporal());
				break;
			}
		}

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
	
	
	public void actualizarDocumentosXTipoSolicitud(ActionEvent ae){		//eramos
		logger.info("*****************actualizarDocumentosXTipoSolicitud*****************");
		
		//logger.info("documentos Leidos: " + documentosLeidos);		
		logger.info("documentos Leidos: " + visadoDocumentosMB.getDocumentosLeidos());
		String []aDocumentos = visadoDocumentosMB.getDocumentosLeidos().split(",");
		String nombreDoc = "";
		
		//Actualiza lista de documentos
		for(String documento : aDocumentos){
			logger.info("Buscando coincidencias para:" + documento);
			if(!documento.trim().isEmpty()){
				nombreDoc = documento.substring(0, documento.lastIndexOf("."));			
				
				//Agregar a listado de documentos tabla documentos
				for(DocumentoTipoSolicitudDTO doc : lstdocumentos){
					logger.info("nombreDoc = doc.getItem():" + nombreDoc + "=" + doc.getNombreCorto());
					if(doc.getNombreCorto().equals(nombreDoc)){
						doc.setAlias(documento);
						logger.info("actualizo nombre documento:" + doc.getAlias());
						
						//agregar a lista de anexos de la solicitud
						TiivsAnexoSolicitud objAnexo = new TiivsAnexoSolicitud();
						objAnexo.setId(new TiivsAnexoSolicitudId(null, doc.getItem()));
						objAnexo.setAliasArchivo(doc.getAlias());
						objAnexo.setAliasTemporal("");
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
		
		//ver si tambien cambia tipo solicitud
		establecerTipoSolicitud();
				
		logger.info("(Tabla) lstdocumentos tama�o:" + lstdocumentos.size());
		logger.info("(Anexos)lstAnexoSolicitud tama�o:" + lstdocumentos.size());
		logger.info("(Combo) lstTipoSolicitudDocumentos tama�o:" + lstdocumentos.size());
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
			sMensaje = "Ingrese el Importe";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (!flagUpdateOperacionSolic) {
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				if (x.getId().getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan())
						&& x.getId().getMoneda().trim().equals(objSolicBancaria.getId().getMoneda().trim())) {
					sMensaje = "Tipo de Operaci�n con la misma moneda ya registrado, Ingrese otra moneda";
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
					System.out.println("x.getMoneda().trim() " +x.getId().getMoneda().trim());
					System.out.println("objSolicBancaria.getMoneda().trim()) " +objSolicBancaria.getId().getMoneda().trim());
					
					if(x.getId().getMoneda().trim().equals(objSolicBancaria.getId().getMoneda().trim())) {
						conunt++;
						logger.info("conunt "+conunt);
						System.out.println("x.getMoneda() " +x.getId().getMoneda());
						System.out.println("objSolicBancaria.getMoneda() " + objSolicBancaria.getId().getMoneda());
						
							System.out.println("lo deja ");
							break;
						
				}/*else if(!x.getMoneda().trim().equals(objSolicBancaria.getMoneda().trim())) {
					System.out.println(" no lo deja ");
					sMensaje = "Tipo de Operaci�n con la misma moneda ya registrado, Ingrese otra moneda";
					Utilitarios.mensajeInfo("", sMensaje);
					result = false;
					break;
				}*/
					
				}
			}
		}
		return result;
	}

	public void obtenerAccionAgregarOperacionBancaria() {
		logger.info("**************************** obtenerAccionAgregarOperacionBancaria ****************************");
		logger.info("********************************************* : "
				+ objSolicBancaria.getImporte());

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
		if(x.equals(objSolicitudOperacionCapturado)){
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
				|| icontDolares > 0 && icontEuros == 0 && icontSoles > 0
				|| icontDolares == 0 && icontEuros > 0 && icontSoles > 0
				|| icontDolares > 0 && icontEuros > 0 && icontSoles > 0) {
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
			Utilitarios.mensajeInfo("INFO", "Ingrese el N�mero de Doi");
		} else if (objTiivsPersonaBusqueda.getTipDoi() == null
				|| objTiivsPersonaBusqueda.getTipDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Tipo de Doi");
		} else {
			if (objTiivsPersonaBusqueda.getTipDoi().equals(ConstantesVisado.CODIGO_CENTRAL.COD_CODIGO_CENTRAL)) {
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
					sMensaje = "Ya existe una persona con n�mero de Doi :  "
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
		//this.objTiivsPersonaResultado=new TiivsPersona();  
		this.objTiivsPersonaResultado.setApeMat(this.objTiivsPersonaCapturado.getApeMat());
		this.objTiivsPersonaResultado.setTipDoi(this.objTiivsPersonaCapturado.getTipDoi());
		this.objTiivsPersonaResultado.setNumDoi(this.objTiivsPersonaCapturado.getNumDoi());
		this.objTiivsPersonaResultado.setCodCen(this.objTiivsPersonaCapturado.getCodCen());
		this.objTiivsPersonaResultado.setApePat(this.objTiivsPersonaCapturado.getApePat());
		this.objTiivsPersonaResultado.setNombre(this.objTiivsPersonaCapturado.getNombre());
		this.objTiivsPersonaResultado.setTipPartic(this.objTiivsPersonaCapturado.getTipPartic());
		this.objTiivsPersonaResultado.setClasifPer(this.objTiivsPersonaCapturado.getClasifPer());
		this.objTiivsPersonaResultado.setClasifPerOtro(this.objTiivsPersonaCapturado.getClasifPerOtro());
		this.objTiivsPersonaResultado.setEmail(this.objTiivsPersonaCapturado.getEmail());
		this.objTiivsPersonaResultado.setNumCel(this.objTiivsPersonaCapturado.getNumCel());
		this.flagUpdatePersona = true;
	}

	public void eliminarPersona() {
		logger.info("**************************** eliminarPersona ****************************");
		logger.info("Codigo de la persona capturada a Eliminar " +objTiivsPersonaCapturado.getCodPer());
		//logger.info(" Lista de las Personas Original de base " +lstTiivsPersonaCopia.size());
		logger.info(" Lista de las Personas Antes de Remover " +lstTiivsPersona.size());
		logger.info("listaTemporalPersonasBorradores Antes de eliminarse ::: " +listaTemporalPersonasBorradores.size());
		logger.info(" Lista de las lstTiivsAgrupacionPersonas Antes de Remover " +lstTiivsAgrupacionPersonas.size());
		logger.info(" q es '????  " +objTiivsPersonaCapturado.getTipPartic());
		
		List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
		List<TiivsPersona>   lstApoderdantes = new ArrayList<TiivsPersona>();
		for (TiivsPersona s : lstTiivsPersona) {
		
		if(s.getTipPartic().trim().equals(ConstantesVisado.PODERDANTE))
		{
			lstPoderdantes.add(s);
		}
		else  if(s.getTipPartic().trim().equals(ConstantesVisado.APODERADO))
		{
			lstApoderdantes.add(s);
		}
		}
		
		logger.info("lstPoderdantes antes de remover " +lstPoderdantes.size());
		logger.info("lstApoderdantes antes de remover " +lstApoderdantes.size());
		
		lstTiivsPersona.remove(objTiivsPersonaCapturado);
		
		
		 lstPoderdantes = new ArrayList<TiivsPersona>();
		  lstApoderdantes = new ArrayList<TiivsPersona>();
		for (TiivsPersona s : lstTiivsPersona) {
		
		if(s.getTipPartic().trim().equals(ConstantesVisado.PODERDANTE))
		{
			lstPoderdantes.add(s);
		}
		else  if(s.getTipPartic().trim().equals(ConstantesVisado.APODERADO))
		{
			lstApoderdantes.add(s);
		}
		}
		
		logger.info("lstPoderdantes despues de remover  " +lstPoderdantes.size());
		logger.info("lstApoderdantes  despues de remover " +lstApoderdantes.size());
		
		
		for (TiivsAgrupacionPersona n : lstTiivsAgrupacionPersonas) {
			if(n.getTiivsPersona().equals(objTiivsPersonaCapturado)){
				lstTiivsAgrupacionPersonas.remove(n);
				listaTemporalAgrupacionesPersonaBorradores.add(n);
				break;
			}
			
		}
		logger.info(" Lista de las lstTiivsAgrupacionPersonas Despues de Remover " +lstTiivsAgrupacionPersonas.size());
		logger.info(" Lista de las Personas despues de Remover " +lstTiivsPersona.size());
		
		/* for (TiivsPersona x : lstTiivsPersonaCopia) {
				if(x.equals(objTiivsPersonaCapturado)){
					//logger.info("La persona  Ya existe en base de datos, se agrega a la lista para borrar");
					*//** Agregar la persona a la lista de temporal a eliminar*//*
					listaTemporalPersonasBorradores.add(objTiivsPersonaCapturado);
				}else{
					// Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita
					//logger.info(" Solo existe en la lista q aun no esta en base, por tal se eliminara solo a nivel de lsita");
				}
			}*/
		 logger.info("Lista de las Personas temporales a Borrar " +listaTemporalPersonasBorradores.size());
		//objTiivsPersonaCapturado = new TiivsPersona();
		this.flagUpdatePersona = false;
	}

	public boolean validarPersona() {
		logger.info("******************************* validarPersona ******************************* "
				+ objTiivsPersonaResultado.getTipPartic());
		boolean bResult = true;
		String sMensaje = "";
		logger.info("objTiivsPersonaResultado.getClasifPer() "
				+ objTiivsPersonaResultado.getClasifPer());
		if (objTiivsPersonaResultado.getTipDoi().equals("")) {
			sMensaje = "Seleccione el Tipo de Documento";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if (objTiivsPersonaResultado.getNumDoi().equals("")) {
			sMensaje = "Ingrese el N�mero de Doi";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if (objTiivsPersonaResultado.getClasifPer() == null
				|| objTiivsPersonaResultado.getClasifPer().equals("")) {
			sMensaje = "Ingrese el Tipo de Clasificaci�n";
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
			for (TiivsPersona x : lstTiivsPersona) {
				if (x.getCodPer() == objTiivsPersonaResultado.getCodPer()) {
					sMensaje = "Persona ya registrada, Ingrese otros datos de persona. ";
					Utilitarios.mensajeInfo("", sMensaje);
					bResult = false;
					break;
				}
			}
		}
		logger.info("bResult " +bResult);

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
					if (objTiivsPersonaResultado.getClasifPer().equals(p.getKey())) {
						objTiivsPersonaResultado.setsDescclasifPer(p.getDescripcion());
					}
					if (objTiivsPersonaResultado.getClasifPer().equals("99")) {
						objTiivsPersonaResultado.setsDescclasifPer(objTiivsPersonaResultado.getClasifPerOtro());
					}
				}
				
				/** Se agrega a la lista de personas en la grilla*/
				
				if (!flagUpdatePersona) {
					lstTiivsPersona.add(objTiivsPersonaResultado);
					
				} else {
					// update
					logger.info("Index Update: " + indexUpdatePersona);
					this.lstTiivsPersona.set(indexUpdatePersona,objTiivsPersonaResultado);
					flagUpdatePersona = false;
				}
				personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
				logger.info("tamanio de personaDataModal  en el metodo agregar ::::: " +personaDataModal.getRowCount());
				objTiivsPersonaResultado = new TiivsPersona();
				objTiivsPersonaBusqueda = new TiivsPersona();
				objTiivsPersonaSeleccionado = new TiivsPersona();
				lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
			}

		}
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
		logger.info(" ************************** obtenerImporteTotalxSolicitud  ****************************** ");

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
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
					//ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+valorFinal);
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
					//ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+valorFinal);
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
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
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
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
					//ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+valorFinal);
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
					//ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+valorFinal);
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
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
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

	/* Termino metodos del registro */
	

	public void limpiarComentario() {
		logger.info("**************************** limpiar Comentario ****************************");
		 this.solicitudRegistrarT.setObs("");
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

	public String getUbicacionTemporal() {
		return ubicacionTemporal;
	}

	public void setUbicacionTemporal(String ubicacionTemporal) {
		this.ubicacionTemporal = ubicacionTemporal;
	}

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
	
	
}
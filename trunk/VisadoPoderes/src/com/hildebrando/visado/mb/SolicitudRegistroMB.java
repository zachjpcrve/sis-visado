package com.hildebrando.visado.mb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

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
import com.hildebrando.visado.dto.ApoderadoDTO;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.OperacionBancariaDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitudId;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
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
	private List<ComboDto> lstClasificacionPersona;
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	@ManagedProperty(value = "#{seguimientoMB}")
	private SeguimientoMB seguimientoMB;	
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
	private TiivsPersona objTiivsPersonaCapturado;
	private Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas;
	private Set<TiivsSolicitudAgrupacion> lstTiivsSolicitudAgrupacion;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	private List<TiivsAnexoSolicitud> lstAnexoSolicitud;
	private PersonaDataModal personaDataModal;
	private String iTipoSolicitud = "";
	private TiivsSolicitudOperban objSolicitudOperacionCapturadoOld =new TiivsSolicitudOperban();
	private String sCodDocumento;
	private TiivsTipoSolicDocumento selectedTipoDocumento;
	private DocumentoTipoSolicitudDTO selectedDocumentoDTO = new DocumentoTipoSolicitudDTO();
	private String ubicacionTemporal;
	private List<String> aliasFilesToDelete;

	private IILDPeUsuario usuario;
	private int numGrupo = 0;
	private List<TiivsPersona> lstTiivsPersona;
	private List<TiivsPersona> lstTiivsPersonaResultado;
	private List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos;
	private List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp;
	boolean bBooleanPopup = false;
	//boolean bBooleanPopupTipoCambio = true;
	boolean bBooleanPopupEmail = true;
	private boolean flagUpdatePoderdanteApoderados=false;
	private boolean flagUpdateOperacionSolic = false;
	private boolean flagUpdateOperacionSolcAgrupac = false;
	private boolean flagUpdateOperacionSolcDocumen = false;
	private boolean flagUpdatePersona = false;
	private String sEstadoSolicitud = "";
	private TiivsSolicitudOperban objSolicitudOperacionCapturado;
	private AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado;
	private TiivsTipoSolicDocumento objDocumentoXSolicitudCapturado;
	private int indexUpdateOperacion = 0;
	private int indexUpdateAgrupacionSimpleDto = 0;
	private int indexUpdateSolicDocumentos = 0;
	private int indexUpdatePersona = 0;
	private int indexUpdatePoderdanteApoderado=0;
	private int numGrupoUpdatePoderdanteApoderado=0;
	Map<Integer, TiivsSolicitudOperban> mapSolicitudes;
	public static Logger logger = Logger.getLogger(SolicitudRegistroMB.class);
	String cadenaEscanerFinal = "";
	//http://172.31.9.41:9080/NAEWeb/pages/escaner/InvocaEscaner.xhtml?idEmpresa=1&idSistema=98&txLogin=P014773
	private UploadedFile file;
	private String sMonedaImporteGlobal;  
	
	//private String pathCliente="D:/Escaneados";
	private String documentosLeer="";
	private String documentosLeidos="";
	
	/*private boolean boleanoMensajeInfoGeneral=true;
	private boolean boleanoMensajeApoderdantePoderdante=true;
	private boolean boleanoMensajeOperacionesBancarias=true;
	private boolean boleanoMensajeDocumentos=true;
	*/
    public UploadedFile getFile() {  
        return file;  
    }  
  
    public void setFile(UploadedFile file) {  
        this.file = file;  
    }  

	public SolicitudRegistroMB() {
		solicitudRegistrar = new Solicitud();
		lstTiivsPersona = new ArrayList<TiivsPersona>();
		lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
		objTiivsPersonaBusqueda = new TiivsPersona();
		objTiivsPersonaResultado = new TiivsPersona();
		objTiivsPersonaSeleccionado = new TiivsPersona();
		lstTiivsAgrupacionPersonas = new HashSet<TiivsAgrupacionPersona>();
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		lstTipoSolicitudDocumentos = new ArrayList<TiivsTipoSolicDocumento>();

		lstDocumentosXTipoSolTemp = new ArrayList<TiivsTipoSolicDocumento>();
		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		objSolicBancaria = new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		objSolicBancaria.setTiivsOperacionBancaria(new TiivsOperacionBancaria());
		lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
		lstOperaciones = new ArrayList<OperacionBancariaDTO>();
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		this.instanciarSolicitudRegistro();

		
		aliasFilesToDelete = new ArrayList<String>();
		
		selectedTipoDocumento = new TiivsTipoSolicDocumento();
		mapSolicitudes=new HashMap<Integer, TiivsSolicitudOperban>();
		
		this.cadenaEscanerFinal = this.prepararURLEscaneo();	
						

	}	
	
	public void eliminarPersona() {
		logger.info("**************************** eliminarPersona ****************************");
		logger.info(objTiivsPersonaCapturado.getCodPer());
		lstTiivsPersona.remove(objTiivsPersonaCapturado);
		objTiivsPersonaCapturado=new TiivsPersona();
        this.flagUpdatePersona=false;
	}


	public String cargarUnicoPDF() {
		
		if(file == null){
			Utilitarios.mensajeInfo("", "No se ha seleccionado ningún archivo");
			return "";
		}
		
		byte fileBytes[] = getFile().getContents();

		File fichTemp = null;
		String sUbicacionTemporal = "";
		String sNombreTemporal = "";
		FileOutputStream canalSalida = null;
		
		try {
			
			//Obteniendo ubicación del proyecto
			sUbicacionTemporal = Utilitarios.getProjectPath()  + File.separator + ConstantesVisado.FILES + File.separator;			
			this.setUbicacionTemporal(sUbicacionTemporal);
			
			logger.debug("ubicacion temporal "+ sUbicacionTemporal);
			
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

	public void listarDocumentosXSolicitud(ValueChangeEvent e) {
		//logger.info("ValuechanceEvent :  " + e.getNewValue());
		GenericDao<TiivsTipoSolicDocumento, Object> genTipoSolcDocumDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSolcDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);
		filtroTipoSolcDoc.add(Restrictions.eq("tiivsTipoSolicitud.codTipSolic",(String) e.getNewValue()));
		filtroTipoSolcDoc.add(Restrictions.eq("activo",'1'));
		filtroTipoSolcDoc.addOrder(Order.desc("obligatorio"));
		try {
			lstDocumentosXTipoSolTemp = genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);			
			lstTipoSolicitudDocumentos = (ArrayList<TiivsTipoSolicDocumento>) ((ArrayList) lstDocumentosXTipoSolTemp).clone();
			
			logger.info("lstDocumentosXTipoSolTemp.size()" + lstDocumentosXTipoSolTemp.size());
			logger.info("lstTipoSolicitudDocumentos.size()" + lstTipoSolicitudDocumentos.size());

			actualizarListadoDocumentos();

			//logger.info(" e.getNewValue()  " + (String) e.getNewValue()+ "  lstTipoSolicitudDocumentos.size : "+ lstTipoSolicitudDocumentos.size());
		} catch (Exception ex) {
			logger.info("Error al cargar el listado de documentos por tipo de soliciitud");
			ex.printStackTrace();
		}
		
		this.documentosLeer = armaListaDocumentos(lstDocumentosXTipoSolTemp);
		logger.info("Documentos a Leer:" + this.documentosLeer);		
		
	}
	
	public String armaListaDocumentos (List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp){
		String sResult = "";		
		StringBuilder sb = new StringBuilder(); 
		for(TiivsTipoSolicDocumento doc : lstDocumentosXTipoSolTemp){
			if(sb.length()>1){
				sb.append(",");
			}			
			sb.append(doc.getTiivsDocumento().getNombre());
			sb.append(".");
			sb.append(doc.getTiivsDocumento().getFormato());			
		}
		sResult = sb.toString();
		return sResult;
	}

	public void actualizarListadoDocumentos() {
		
		addArchivosTemporalesToDelete();
		
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();

		for (TiivsTipoSolicDocumento s : lstTipoSolicitudDocumentos) {
			String nombreCorto = s.getTiivsDocumento().getNombre();
			String formato = s.getTiivsDocumento().getFormato();
			if (s.getObligatorio()!=null && s.getObligatorio().equals("1") ){
				lstdocumentos.add(new DocumentoTipoSolicitudDTO(s.getId()
						.getCodDoc(), s.getTiivsDocumento().getDescripcion(),
						true + "", "", "", nombreCorto, formato));
			} else {								
				lstdocumentos.add(new DocumentoTipoSolicitudDTO(s.getId()
						.getCodDoc(), s.getTiivsDocumento().getDescripcion(),
						false + "", "", "", nombreCorto, formato));
			}
		}

		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
	}
	
	public void addArchivosTemporalesToDelete(){		
		for(TiivsAnexoSolicitud a : lstAnexoSolicitud){	
			aliasFilesToDelete.add(a.getAliasTemporal());
		}				
	}

	public void obtenerPersonaSeleccionada() {
		logger.info(objTiivsPersonaSeleccionado.getCodPer());
		this.objTiivsPersonaResultado = this.objTiivsPersonaSeleccionado;
	}

	public void buscarPersona() {
		logger.info("******************** buscarPersona **********************");
		logger.info("***objTiivsPersonaBusqueda.getCodCen() "+ objTiivsPersonaBusqueda.getCodCen());
		logger.info("***objTiivsPersonaBusqueda.getTipDoi() "+ objTiivsPersonaBusqueda.getTipDoi());
		logger.info("***objTiivsPersonaBusqueda.getNumDoi() "	+ objTiivsPersonaBusqueda.getNumDoi());
		try {
			List<TiivsPersona> lstTiivsPersonaLocal = new ArrayList<TiivsPersona>();
			lstTiivsPersonaLocal = this.buscarPersonaLocal();
			logger.info("lstTiivsPersonaLocal  "+ lstTiivsPersonaLocal.size());
			List<TiivsPersona> lstTiivsPersonaReniec = new ArrayList<TiivsPersona>();
			if (lstTiivsPersonaLocal.size() == 0) {
				lstTiivsPersonaReniec = this.buscarPersonaReniec();
				if (lstTiivsPersonaReniec.size() == 0) {
					objTiivsPersonaResultado = new TiivsPersona();
					this.bBooleanPopup = false;
					 Utilitarios.mensajeInfo("INFO","No se encontro resultados para la busqueda.");
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

				personaDataModal = new PersonaDataModal(
						lstTiivsPersonaResultado);
			} else {
				this.bBooleanPopup = true;
			}
		
		} catch (Exception e) {
			Utilitarios.mensajeError("ERROR", e.getMessage());
			e.printStackTrace();
		}
	}

	public void limpiarCriteriosBusqueda() {
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

	public List<TiivsPersona> buscarPersonaReniec() throws Exception {
		logger.debug("==== inicia buscarPersonaReniec() ==== ");
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		BResult resultado = null;
		TiivsPersona objPersona = null;
		Persona persona = null;
		if (objTiivsPersonaBusqueda.getNumDoi() != null) {
			logger.info("[RENIEC]-Dni:"+ objTiivsPersonaBusqueda.getNumDoi());

			//ObtenerPersonaReniecService reniecService = new ObtenerPersonaReniecServiceImpl();
			//logger.debug("reniecService="+reniecService);
			ObtenerPersonaReniecDUMMY reniecService = new ObtenerPersonaReniecDUMMY();
			resultado = reniecService.devolverPersonaReniecDNI("P013371", "0553",objTiivsPersonaBusqueda.getNumDoi());
			logger.debug("[RENIEC]-resultado: "+resultado);
			
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

	public List<TiivsPersona> buscarPersonaLocal() throws Exception {
		boolean busco = false;
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);

		if ((objTiivsPersonaBusqueda.getCodCen() == null || objTiivsPersonaBusqueda.getCodCen().equals(""))
				&& (objTiivsPersonaBusqueda.getTipDoi() == null || objTiivsPersonaBusqueda
						.getTipDoi().equals(""))
				&& (objTiivsPersonaBusqueda.getNumDoi() == null || objTiivsPersonaBusqueda
						.getNumDoi().equals(""))) {
			Utilitarios.mensajeInfo("INFO",
					"Ingrese al menos un criterio de busqueda");

		} else if (objTiivsPersonaBusqueda.getNumDoi() == null
				|| objTiivsPersonaBusqueda.getNumDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Número de Doi");
		} else if (objTiivsPersonaBusqueda.getTipDoi() == null
				|| objTiivsPersonaBusqueda.getTipDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Tipo de Doi");
		} else {
			if (objTiivsPersonaBusqueda.getTipDoi() != null
					&& objTiivsPersonaBusqueda.getNumDoi() != null
					&& objTiivsPersonaBusqueda.getTipDoi().compareTo("") != 0
					&& objTiivsPersonaBusqueda.getNumDoi().compareTo("") != 0) {
				filtro.add(Restrictions.eq("tipDoi",
						objTiivsPersonaBusqueda.getTipDoi()));
				filtro.add(Restrictions.eq("numDoi",
						objTiivsPersonaBusqueda.getNumDoi()));
				busco = true;
			}
			if (objTiivsPersonaBusqueda.getCodCen() != null
					&& objTiivsPersonaBusqueda.getCodCen().compareTo("") != 0) {
				filtro.add(Restrictions.eq("codCen",
						objTiivsPersonaBusqueda.getCodCen()));
				busco = true;
			}
			lstTiivsPersona = service.buscarDinamico(filtro);
		
			for (TiivsPersona tiivsPersona : lstTiivsPersona) {
				for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
					if (tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())) {
						tiivsPersona.setsDesctipDoi(p.getDescripcion());
					}
				}
			}

			if (lstTiivsPersona.size() == 0 && busco) {
				//Utilitarios.mensajeInfo("INFO","No se han encontrado resultados para los criterios de busqueda seleccionados");
			}
		}

		return lstTiivsPersona;
	}



	public String redirectDetalleSolicitud() {
		logger.info(" **** redirectDetalleSolicitud ***");
		return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
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
		tiivsSolicitudAgrupacion.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO);

		lstSolicitudArupacion.add(tiivsSolicitudAgrupacion);
		logger.info("TAMANIO DE LA SOLICI AGRUPA " + lstSolicitudArupacion.size());
		return lstSolicitudArupacion;
	}



	public void agregarPersona() {
		logger.info("****************** agregarPersona ********************");
		if (validarPersona()) {
			if (validarRegistroDuplicado()) {

				for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
					if (objTiivsPersonaResultado.getTipDoi().equals(p.getCodTipoDoc())) {
						objTiivsPersonaResultado.setsDesctipDoi(p.getDescripcion());
					}
				}
				for (ComboDto p : combosMB.getLstTipoRegistroPersona()) {
					if (objTiivsPersonaResultado.getTipPartic().equals(
							p.getKey())) {
						objTiivsPersonaResultado.setsDesctipPartic(p
								.getDescripcion());
					}
				}
				for (ComboDto p : combosMB.getLstClasificacionPersona()) {
					if (objTiivsPersonaResultado.getClasifPer().equals(
							p.getKey())) {
						objTiivsPersonaResultado.setsDescclasifPer(p
								.getDescripcion());
					}
					if (objTiivsPersonaResultado.getClasifPer().equals("99")) {
						objTiivsPersonaResultado
								.setsDescclasifPer(objTiivsPersonaResultado
										.getClasifPerOtro());
					}
				}

				if (!flagUpdatePersona) {
					lstTiivsPersona.add(objTiivsPersonaResultado);
					logger.info("lstTiivsPersona.size : " + lstTiivsPersona.size());
					objTiivsPersonaResultado = new TiivsPersona();
					objTiivsPersonaBusqueda = new TiivsPersona();
					objTiivsPersonaSeleccionado = new TiivsPersona();
					lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
					personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
					logger.info("lstTiivsPersonaResultado.size : " + lstTiivsPersonaResultado.size());
				} else {
					// update
					logger.info("Index Update: " + indexUpdatePersona);

					this.lstTiivsPersona.set(indexUpdatePersona,objTiivsPersonaCapturado);
					personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
					objTiivsPersonaResultado = new TiivsPersona();
					objTiivsPersonaBusqueda = new TiivsPersona();
					objTiivsPersonaSeleccionado = new TiivsPersona();
					flagUpdatePersona = false;
				}
			}

		}
		// return objTiivsPersonaBusqueda;

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
			
			//if(objTiivsPersonaResultado.getTipDoi())
		}

		return bResult;
	}

	public void editarPersona() {
		logger.info("***************************** editarPersona ************************************ ");
		for (int i = 0; i < this.lstTiivsPersona.size(); i++) {
			if (objTiivsPersonaCapturado.equals(this.lstTiivsPersona.get(i))) {
				indexUpdatePersona = i;
			}
		}

		this.objTiivsPersonaResultado = this.objTiivsPersonaCapturado;
		this.flagUpdatePersona = true;
	}

	public boolean validarPersona() {
		logger.info("******************************* validarPersona ******************************* "
				+ objTiivsPersonaResultado.getTipPartic());
		boolean bResult = true;
		String sMensaje = "";
		logger.info("objTiivsPersonaResultado.getClasifPer() " + objTiivsPersonaResultado.getClasifPer());
		
		if (objTiivsPersonaResultado.getTipDoi().equals("")) {
			sMensaje = "Seleccione el Tipo de Documento";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if (objTiivsPersonaResultado.getNumDoi().equals("")) {
			sMensaje = "Ingrese el Número de Doi";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if (objTiivsPersonaResultado.getClasifPer() == null
				|| objTiivsPersonaResultado.getClasifPer().equals("")) {
			sMensaje = "Ingrese el Tipo de Clasificación";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if(!objTiivsPersonaResultado.getEmail().equals("")){
		if(!Utilitarios.validateEmail(objTiivsPersonaResultado.getEmail())){
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

		return bResult;

	}


  public void agregarActionListenerAgrupacion(){
	  logger.info("********************** agregarActionListenerAgrupacion ********************* " );
	  lstTiivsPersona=new ArrayList<TiivsPersona>();
	  objTiivsPersonaBusqueda=new TiivsPersona();
	  objTiivsPersonaResultado=new TiivsPersona();
	  lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
	  flagUpdatePoderdanteApoderados=false;
	  combosMB=new CombosMB();
	  lstClasificacionPersona=combosMB.getLstClasificacionPersona();
	  logger.info("tamanioo actual **** " +combosMB.getLstClasificacionPersona().size());
	    
  }
	public boolean validarAgregarAgrupacion(){
		boolean returno=true;
		logger.info("***************************** validarAgregarAgrupacion ***************************************");
		String mensaje="Por lo menos ingrese un Poderdante o Apoderado";
		if(lstTiivsPersona.size()==0){
			returno=false;
			Utilitarios.mensajeInfo("", mensaje);
		}
		return returno;
	}
	

	
  public void agregarAgrupacion(){
	  if(validarAgregarAgrupacion()){
		  logger.info("***************************** agregarAgrupacion ***************************************");
		  if(!flagUpdatePoderdanteApoderados){// INDICA QUE ES UN REGISTRO NUEVO
			
	  numGrupo=numGrupo+1;
	  logger.info("********************** agregarAgrupacion ********************* " +numGrupo);
	  List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
	  List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
	  for (TiivsPersona n : lstTiivsPersona) {
		  if(n.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
			  lstPoderdantes.add(n);}
		  if(n.getTipPartic().equals(ConstantesVisado.APODERADO)){
			  lstApoderdantes.add(n);}
		  logger.info("objTiivsPersonaResultado.getCodPer() : "+n.getCodPer());
	
		  TiivsAgrupacionPersona  tiivsAgrupacionPersonaId =new TiivsAgrupacionPersona();
		  tiivsAgrupacionPersonaId.setNumGrupo(numGrupo);
		  tiivsAgrupacionPersonaId.setCodSoli(solicitudRegistrarT.getCodSoli());
		  tiivsAgrupacionPersonaId.setCodPer(n.getCodPer());
		  tiivsAgrupacionPersonaId.setClasifPer(n.getClasifPer());
		  tiivsAgrupacionPersonaId.setTipPartic(n.getTipPartic());
		  tiivsAgrupacionPersonaId.setTiivsPersona(n);
		  //tiivsAgrupacionPersona.setId(tiivsAgrupacionPersonaId);
		  //lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
		  lstTiivsAgrupacionPersonas.add(tiivsAgrupacionPersonaId);
	}  
	  logger.info("lstPoderdantes " +lstPoderdantes.size());
	  logger.info("lstApoderdantes " +lstApoderdantes.size());
	  AgrupacionSimpleDto agrupacionSimpleDto =new AgrupacionSimpleDto();
	   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudRegistrarT.getCodSoli(), numGrupo));
	   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
	   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
	   agrupacionSimpleDto.setsEstado(ConstantesVisado.ESTADOS.ESTADO_ACTIVO);
	   agrupacionSimpleDto.setiEstado(Integer.parseInt(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO));
	   agrupacionSimpleDto.setLstPersonas(this.lstTiivsPersona);
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
			 
			  List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			  List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
			  Set<TiivsAgrupacionPersona> lstTempAgrupacion=null;
			  for (TiivsPersona objTiivsPersonaResultado : lstTiivsPersona) {
				  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					  lstPoderdantes.add(objTiivsPersonaResultado);}
				  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.APODERADO)){
					  lstApoderdantes.add(objTiivsPersonaResultado);}
				  logger.info("objTiivsPersonaResultado.getCodPer() : "+objTiivsPersonaResultado.getCodPer());
				  TiivsAgrupacionPersona  tiivsAgrupacionPersonaId =new TiivsAgrupacionPersona();
				  logger.info("numGrupo cambiar esto  " +numGrupoUpdatePoderdanteApoderado);
				  tiivsAgrupacionPersonaId.setNumGrupo(numGrupoUpdatePoderdanteApoderado);
				  tiivsAgrupacionPersonaId.setCodSoli(solicitudRegistrarT.getCodSoli());
				  tiivsAgrupacionPersonaId.setCodPer(objTiivsPersonaResultado.getCodPer());
				  tiivsAgrupacionPersonaId.setClasifPer(objTiivsPersonaResultado.getClasifPer());
				  tiivsAgrupacionPersonaId.setTipPartic(objTiivsPersonaResultado.getTipPartic());
				  tiivsAgrupacionPersonaId.setTiivsPersona(objTiivsPersonaResultado);
				  lstTempAgrupacion=new HashSet<TiivsAgrupacionPersona>();
				  for (TiivsAgrupacionPersona  x : lstTiivsAgrupacionPersonas) {
					  if(!x.equals(tiivsAgrupacionPersonaId)){
						  lstTempAgrupacion.add(tiivsAgrupacionPersonaId);}
				}
			}  
			  
			  lstTiivsAgrupacionPersonas.addAll(lstTempAgrupacion);
			  
			  logger.info("lstPoderdantes " +lstPoderdantes.size());
			  logger.info("lstApoderdantes " +lstApoderdantes.size());

			  AgrupacionSimpleDto agrupacionSimpleDto =null;
			for (TiivsSolicitudAgrupacion x : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
				lstTiivsAgrupacionPersonas=x.getTiivsAgrupacionPersonas();
				System.out.println(" lstTiivsAgrupacionPersonas " +lstTiivsAgrupacionPersonas.size());
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
  

	public void instanciarSolicitudRegistro() {
		logger.info("********************** instanciarSolicitudRegistro *********************");
		sEstadoSolicitud = "BORRADOR";
		lstTiivsAgrupacionPersonas = new HashSet<TiivsAgrupacionPersona>();
		lstTiivsSolicitudAgrupacion = new HashSet<TiivsSolicitudAgrupacion>();
		solicitudRegistrarT = new TiivsSolicitud();
		solicitudRegistrarT.setImporte((double) 0);
		solicitudRegistrarT.setTiivsSolicitudAgrupacions(lstTiivsSolicitudAgrupacion);
		solicitudRegistrarT.setTiivsOficina1(new TiivsOficina1());
		solicitudRegistrarT.setTiivsTipoSolicitud(new TiivsTipoSolicitud());
		String grupoAdm = (String) Utilitarios.getObjectInSession("GRUPO_ADM");
		String grupoOfi = (String) Utilitarios.getObjectInSession("GRUPO_OFI");

		logger.debug("********grupoAdm ****** " + grupoAdm
				+ "  ******* grupoOfi ******** " + grupoOfi);
		// if (grupoAdm == null && grupoOfi!= null) {
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		logger.debug("usuario en session? --> " + usuario.getNombre());
		logger.debug("CodOfi: " + usuario.getBancoOficina().getCodigo().trim());
		logger.debug("DesOfi: "+ usuario.getBancoOficina().getDescripcion().trim());

		TiivsOficina1 oficina = new TiivsOficina1();
		oficina.setCodOfi(usuario.getBancoOficina().getCodigo());

		List<TiivsOficina1> lstOficinas1 = new ArrayList<TiivsOficina1>();
		combosMB = new CombosMB();
		lstOficinas1 = combosMB.getLstOficina();

		if (lstOficinas1 != null && lstOficinas1.size() != 0) {
			for (TiivsOficina1 o : lstOficinas1) {
				if (usuario.getBancoOficina().getCodigo().equals(o.getCodOfi())) {
					this.solicitudRegistrarT.setTiivsOficina1(o);
				} else {
					this.solicitudRegistrarT.setTiivsOficina1(new TiivsOficina1());
				}
			}
		
		}
	/*	try {
			SolicitudDao<TiivsPersona, Object> servicePK = (SolicitudDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
			String sCodigoSol = servicePK.obtenerPKNuevaSolicitud();
			logger.debug(" sCodigoSol " + sCodigoSol);
			this.solicitudRegistrarT.setCodSoli(sCodigoSol);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02);
		this.solicitudRegistrarT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_REGISTRADO_T02);

		lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
		objTiivsPersonaBusqueda = new TiivsPersona();
		objTiivsPersonaResultado = new TiivsPersona();
		objTiivsPersonaSeleccionado = new TiivsPersona();
		lstTiivsAgrupacionPersonas = new HashSet<TiivsAgrupacionPersona>();
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		lstTipoSolicitudDocumentos = new ArrayList<TiivsTipoSolicDocumento>();

		lstDocumentosXTipoSolTemp = new ArrayList<TiivsTipoSolicDocumento>();
		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		objSolicBancaria = new TiivsSolicitudOperban();
		objSolicBancaria.setTipoCambio(0.0);
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
		iTipoSolicitud = "";
		lstTiivsPersona = new ArrayList<TiivsPersona>();

		// }

	}

	public void actualizarListaDocumentosXTipo(TiivsAnexoSolicitud objAnexo) {
		logger.info("****************************** actualizarListaDocumentosXTipo *********************************");
		if (objAnexo.getId().getCodDoc().contains(ConstantesVisado.PREFIJO_OTROS)) {
			String sAlias = objAnexo.getAliasArchivo();
			String sAliasTemporal = objAnexo.getAliasTemporal();
			DocumentoTipoSolicitudDTO doc = new DocumentoTipoSolicitudDTO(
					objAnexo.getId().getCodDoc(),
					ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS, false + "",
					sAlias, sAliasTemporal, "", "");
			lstdocumentos.add(doc);
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

	public boolean validarTotalDocumentos() {
		boolean result = false;
		String sMensaje = "";
		for (TiivsTipoSolicDocumento x : this.lstTipoSolicitudDocumentos) {
			for (TiivsAnexoSolicitud a : lstAnexoSolicitud) {
				if (Integer.parseInt(x.getObligatorio()) == ConstantesVisado.DOCUMENTO_OBLIGATORIO&& x.getId().getCodDoc().equals(a.getId().getCodDoc())) {
					result = true; 
				} else {
					sMensaje = "Faltan Ingresar Documentos Obligatorios.";
					Utilitarios.mensajeInfo("INFO", sMensaje);
					break;
				}
			}
		}
		return result;
	}

	public boolean ValidarDocumentosDuplicados() {
		boolean result = true;
		String sMensaje = "";
		for (TiivsAnexoSolicitud a : this.lstAnexoSolicitud) {
			if (a.getId().getCodDoc().equals(sCodDocumento) 
					&& !sCodDocumento.equalsIgnoreCase(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
				result = false;
				sMensaje = "Documento ya se encuentra en la lista de Documentos";
				Utilitarios.mensajeInfo("INFO ", sMensaje);
			}
		}
		return result;
	}

	public void agregarDocumentosXTipoSolicitud() {
	/*	boleanoMensajeInfoGeneral=false;
		 boleanoMensajeApoderdantePoderdante=false;
		 boleanoMensajeOperacionesBancarias=false;
		 boleanoMensajeDocumentos=true;*/
		logger.info(" ************************** agrearDocumentosXTipoSolicitud  ****************************** ");
		logger.info("iTipoSolicitud  : " + iTipoSolicitud);
		if(selectedTipoDocumento!=null){
			sCodDocumento = selectedTipoDocumento.getId().getCodDoc();
		} else {
			sCodDocumento = null;
		}
		logger.info("codDocumento :  " + sCodDocumento);
		logger.info("lstAnexoSolicitud.size() :  " + lstAnexoSolicitud.size());

		if(sCodDocumento == null || sCodDocumento.isEmpty()){
			Utilitarios.mensajeInfo("", "Debe seleccionar un documento");
			 return;
		}

		if (this.ValidarDocumentosDuplicados()) {
												
			if (sCodDocumento.equalsIgnoreCase(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
				sCodDocumento = ConstantesVisado.PREFIJO_OTROS+String.format("%06d", lstdocumentos.size() + 1);
			} 
						
			String sAliasTemporal = cargarUnicoPDF();
									
			if(sAliasTemporal == null || sAliasTemporal.trim() ==""){				
				return;
			}
			
			String sExtension = sAliasTemporal.substring(sAliasTemporal.lastIndexOf("."));
			//String sAliasArchivo = this.solicitudRegistrarT.getCodSoli() + "_" +
			String sAliasArchivo = sCodDocumento + sExtension;
			
			logger.info("aliasArchivo *** " + sAliasArchivo);
			logger.info("aliasArchivoTemporal *** " + sAliasTemporal);
			
			TiivsAnexoSolicitud objAnexo = new TiivsAnexoSolicitud();
			objAnexo.setId(new TiivsAnexoSolicitudId(null, sCodDocumento));
			objAnexo.setAliasArchivo(sAliasArchivo);
			objAnexo.setAliasTemporal(sAliasTemporal);
			lstAnexoSolicitud.add(objAnexo);
			this.actualizarListaDocumentosXTipo(objAnexo);

			establecerTipoSolicitud();
			
		}
		
		
	
	}
	
	private void establecerTipoSolicitud(){
		for (TiivsTipoSolicitud tipoSoli : combosMB.getLstTipoSolicitud()) {
			if (tipoSoli.getCodTipSolic().equals(iTipoSolicitud)) {
				solicitudRegistrarT.setTiivsTipoSolicitud(tipoSoli);
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
				this.lstTipoSolicitudDocumentos.add(s);				
				//this.lstTipoSolicitudDocumentos.add(i,s);											
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
		
		logger.info("documentos Leidos: " + documentosLeidos);		
		String []aDocumentos = documentosLeidos.split(",");
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
		
		establecerTipoSolicitud();
		
		
		logger.info("(Tabla) lstdocumentos tamaño:" + lstdocumentos.size());
		logger.info("(Anexos)lstAnexoSolicitud tamaño:" + lstdocumentos.size());
		logger.info("(Combo) lstTipoSolicitudDocumentos tamaño:" + lstdocumentos.size());
	}
	

	public boolean validarOperacionBancaria() {
		boolean result = true;
		String sMensaje = "";
		if (objSolicBancaria.getId().getCodOperBan().equals("")) {
			sMensaje = "Ingrese un tipo de Operacion";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (objSolicBancaria.getMoneda().equals("")) {
			sMensaje = "Seleccione una moneda";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (!objSolicBancaria.getMoneda().equals("")&& !objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)
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
						&& x.getMoneda().trim().equals(objSolicBancaria.getMoneda().trim())) {
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
					System.out.println("x.getMoneda().trim() " +x.getMoneda().trim());
					System.out.println("objSolicBancaria.getMoneda().trim()) " +objSolicBancaria.getMoneda().trim());
					
					if(x.getMoneda().trim().equals(objSolicBancaria.getMoneda().trim())) {
						conunt++;
						logger.info("conunt "+conunt);
						System.out.println("x.getMoneda() " +x.getMoneda());
						System.out.println("objSolicBancaria.getMoneda() " + objSolicBancaria.getMoneda());
						
							System.out.println("lo deja ");
							break;
						
				}/*else if(!x.getMoneda().trim().equals(objSolicBancaria.getMoneda().trim())) {
					System.out.println(" no lo deja ");
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

	int icontSoles = 0, icontDolares = 0, icontEuros = 0;
	double valorFinal = 0;
	int item = 0;
	
	public void validarTipoCambioDisabled(ValueChangeEvent e){
		if(e.getNewValue()!=null){
		logger.info(" validarTipoCambioDisabled " +e.getNewValue());
		if (e.getNewValue().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
			this.objSolicBancaria.setTipoCambio(0.0);
		}else{
			//bBooleanPopupTipoCambio=false;
		
		}
		}
	}

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
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
					objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
					objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
					objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
				}

				item++;
				objSolicBancaria.setsItem("00" + item);
				this.lstSolicBancarias.add(objSolicBancaria);
				for (TiivsSolicitudOperban x : lstSolicBancarias) {
					if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
						icontSoles++;
					}else 
						if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
						icontDolares++;
					}else 
						if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
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
				
				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getImporte());
					objSolicitudOperacionCapturado.setTipoCambio(0.0);
					this.objSolicitudOperacionCapturado.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
				}
				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getTipoCambio()* objSolicitudOperacionCapturado.getImporte());
					this.objSolicitudOperacionCapturado.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
				}
				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
					objSolicitudOperacionCapturado.setImporteSoles(objSolicitudOperacionCapturado.getTipoCambio()* objSolicitudOperacionCapturado.getImporte());
					this.objSolicitudOperacionCapturado.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
				}
				//actualizar registro 
				this.lstSolicBancarias.set(indexUpdateOperacion,objSolicitudOperacionCapturado);
				
				for (TiivsSolicitudOperban x : lstSolicBancarias) {
					if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
						icontSoles++;
					}else 
						if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
						icontDolares++;
					}else 
						if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
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
          this.flagUpdateOperacionSolic=false;
          this.objSolicitudOperacionCapturado=new TiivsSolicitudOperban();
          this.objSolicitudOperacionCapturado.setId(new TiivsSolicitudOperbanId());
          this.valorFinal=0;
          this.llamarComision();
		}
  
			

		

	}

	public void limpiarListaSolicitudesBancarias() {
		logger.info("**************************** limpiarListaSolicitudesBancarias ****************************");
		this.objSolicBancaria=new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		flagUpdateOperacionSolic=false;
		//this.lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
	}

	public void seterComentario() {
		logger.info("**************************** Setear Comentario ****************************");
		logger.info("Comentario : " + this.solicitudRegistrarT.getObs());
	}
	public void limpiarComentario() {
		logger.info("**************************** Setear Comentario ****************************");
		 this.solicitudRegistrarT.setObs("");
	}

	public void eliminarOperacionBancaria() {
		logger.info("**************************** eliminarOperacionBancaria ****************************");
		// String valor = Utilitarios.capturarParametro("objOperacion");
		// logger.info("CODIGO DE OPERACION "+valor);
		logger.info(objSolicitudOperacionCapturado.getImporte());
		lstSolicBancarias.remove(objSolicitudOperacionCapturado);
		
		for (TiivsSolicitudOperban x : lstSolicBancarias) {
			if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
				icontSoles++;
			}else 
				if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
				icontDolares++;
			}else 
				if(x.getMoneda().trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
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

		
	
  icontDolares=0;icontEuros=0;icontSoles=0;valorFinal=0;
  objSolicBancaria=new TiivsSolicitudOperban();
  objSolicBancaria.setId(new TiivsSolicitudOperbanId());
  objSolicBancaria.setTipoCambio(0.0);
  this.flagUpdateOperacionSolic=false;
  this.objSolicitudOperacionCapturado=new TiivsSolicitudOperban();
  this.objSolicitudOperacionCapturado.setId(new TiivsSolicitudOperbanId());
  this.valorFinal=0.0;
  if(lstSolicBancarias.size()==0){
		this.solicitudRegistrarT.setsImporteMoneda(valorFinal+"");
  	this.solicitudRegistrarT.setMoneda(null);
	}
  this.llamarComision();
		

	     

	}

	public void eliminarArupacion() {
		logger.info("********************** eliminarArupacion *********************************** ");

		this.lstAgrupacionSimpleDto.remove(this.objAgrupacionSimpleDtoCapturado);
		Set<TiivsSolicitudAgrupacion> lstSolicitudAgrupacion = 
				          (Set<TiivsSolicitudAgrupacion>) this.solicitudRegistrarT.getTiivsSolicitudAgrupacions();

		logger.info("Tamanio de la lista Solicitud Agrupacion : "+ lstSolicitudAgrupacion.size());
		for (TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion : lstSolicitudAgrupacion) {
			if (tiivsSolicitudAgrupacion.getId().equals(
					this.objAgrupacionSimpleDtoCapturado.getId())) {
				lstSolicitudAgrupacion.remove(tiivsSolicitudAgrupacion);
				break;
			}
		}
		//numGrupo--;
		
		
	/*	 for (TiivsPersona objTiivsPersonaResultado : lstTiivsPersona) {
			  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
				  lstPoderdantes.add(objTiivsPersonaResultado);}
			  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.APODERADO)){
				  lstApoderdantes.add(objTiivsPersonaResultado);}
			  logger.info("objTiivsPersonaResultado.getCodPer() : "+objTiivsPersonaResultado.getCodPer());
			  TiivsAgrupacionPersona  tiivsAgrupacionPersonaId =new TiivsAgrupacionPersona();
			  logger.info("numGrupo cambiar esto  " +numGrupoUpdatePoderdanteApoderado);
			  tiivsAgrupacionPersonaId.setNumGrupo(numGrupoUpdatePoderdanteApoderado);
			  tiivsAgrupacionPersonaId.setCodSoli(solicitudRegistrarT.getCodSoli());
			  tiivsAgrupacionPersonaId.setCodPer(objTiivsPersonaResultado.getCodPer());
			  tiivsAgrupacionPersonaId.setClasifPer(objTiivsPersonaResultado.getClasifPer());
			  tiivsAgrupacionPersonaId.setTipPartic(objTiivsPersonaResultado.getTipPartic());
			  tiivsAgrupacionPersonaId.setTiivsPersona(objTiivsPersonaResultado);
			  lstTempAgrupacion=new HashSet<TiivsAgrupacionPersona>();
			  //lstTempAgrupacion.addAll(lstTiivsAgrupacionPersonas);
			  for (TiivsAgrupacionPersona  x : lstTiivsAgrupacionPersonas) {
				  if(!x.equals(tiivsAgrupacionPersonaId)){
					  lstTempAgrupacion.add(tiivsAgrupacionPersonaId);}
			}
		} 
		*/
		
		// lstTiivsAgrupacionPersonas.remove(objAgrupacionSimpleDtoCapturado.g)
		logger.info("Tamanio de la lista Solicitud Agrupacion : "+ lstSolicitudAgrupacion.size());
		this.llamarComision();
		this.objAgrupacionSimpleDtoCapturado = new AgrupacionSimpleDto();
		
		
	}

	public void verAgrupacion() {
		logger.info("********************** verAgrupacion *********************************** ");

		logger.info("this.getCodSoli  "+ this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.getNumGrupo  "+ this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		logger.info("this.getLstPersonas  "+ this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
	}
	public void verEditarAgrupacion(){
		logger.info("********************** verEditarAgrupacion *********************************** ");
		logger.info("this.getCodSoli  "+ this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.getNumGrupo  "+ this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		logger.info("this.getLstPersonas  "+ this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
		
		for (int i = 0; i < lstAgrupacionSimpleDto.size(); i++) {
           if(lstAgrupacionSimpleDto.get(i).equals(objAgrupacionSimpleDtoCapturado)){
        	   indexUpdatePoderdanteApoderado=i;
        	   numGrupoUpdatePoderdanteApoderado=lstAgrupacionSimpleDto.get(i).getId().getNumGrupo();
        	   for (TiivsSolicitudAgrupacion a : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
				if(a.getId().getNumGrupo()==numGrupoUpdatePoderdanteApoderado){
					lstTiivsAgrupacionPersonas=a.getTiivsAgrupacionPersonas();
				}
			   }
        	   break;
			}
		}
		setLstTiivsPersona(this.objAgrupacionSimpleDtoCapturado.getLstPersonas());
		flagUpdatePoderdanteApoderados=true;
	}
	
	
	
	int y=0;
    public void obtenerAccionAgregarOperacionBancaria(){
    	logger.info("**************************** obtenerAccionAgregarOperacionBancaria ****************************");
    	logger.info("********************************************* : "+objSolicBancaria.getImporte());
    	
    }
	public void editarOperacionBancaria() {
		logger.info("**************************** editarOperacionBancaria ****************************");
		
		for (int i = 0; i < this.lstSolicBancarias.size(); i++) {
			if (objSolicitudOperacionCapturado.equals(this.lstSolicBancarias.get(i))) {
				indexUpdateOperacion = i;
				break;
			}
		}
		y=y+1;
		logger.info("yyyyyyy : " +y);
	//	Map<String, TiivsSolicitudOperban> mapSolicitudes=new HashMap<String, TiivsSolicitudOperban>();
		mapSolicitudes.put(y, objSolicitudOperacionCapturado);
		
		Set set = mapSolicitudes.entrySet(); 
		// Get an iterator 
		Iterator iterate = set.iterator(); 
		// Display elements 
		
		while(iterate.hasNext()) { 
		Map.Entry me = (Map.Entry)iterate.next(); 
		System.out.print(me.getKey() + ": "); 
		logger.info(me.getValue()); 
		} 
		
		logger.info("mapSolicitudes.get(mapSolicitudes.size()).getImporte()"+mapSolicitudes.get(mapSolicitudes.size()).getImporte());
		//if(!flagUpdateOperacionSolic){
		this.objSolicitudOperacionCapturadoOld=this.objSolicitudOperacionCapturado;
		//}
		this.objSolicBancaria = this.objSolicitudOperacionCapturado;
		this.flagUpdateOperacionSolic = true;
	}

	public void editarAgrupacionSimpleDto() {
		logger.info("************************** editarAgrupacionSimpleDto *****************************");
		for (int i = 0; i < this.lstAgrupacionSimpleDto.size(); i++) {
			if (objAgrupacionSimpleDtoCapturado
					.equals(this.lstAgrupacionSimpleDto.get(i))) {
				indexUpdateAgrupacionSimpleDto = i;
			}
		}
		this.lstTiivsPersona = new ArrayList<TiivsPersona>();
		/*
		 * for (TiivsPersona f :
		 * this.objAgrupacionSimpleDtoCapturado.getLstApoderdantes()) {
		 * lstTiivsPersona. }
		 */
		this.flagUpdateOperacionSolcAgrupac = true;
	}

	public void llamarComision() {
		logger.info("************************** llamar Comision *****************************");
		this.solicitudRegistrarT.setTipoComision(objRegistroUtilesMB.obtenerTipoComision(this.solicitudRegistrarT));
		this.solicitudRegistrarT.setComision(objRegistroUtilesMB.obtenerComision(solicitudRegistrarT.getTipoComision()));
		logger.info("TIPO COMISION : " + this.solicitudRegistrarT.getTipoComision());
		logger.info("COMISION : " + this.solicitudRegistrarT.getComision());

	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Transactional
	public String registrarSolicitud() {
	/*	boleanoMensajeInfoGeneral=true;
		 boleanoMensajeApoderdantePoderdante=true;
		 boleanoMensajeOperacionesBancarias=true;
		 boleanoMensajeDocumentos=false;*/
		String mensaje = "";
		String redirect = "";
		boolean actualizarBandeja=false;
		
		logger.info("*********************** registrarSolicitud ************************");
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
        GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	    TiivsPersona objPersonaRetorno=new TiivsPersona();
	    TiivsAgrupacionPersona objAgruId=new TiivsAgrupacionPersona();
		try {
			logger.info("this.solicitudRegistrarT.importe : " +this.solicitudRegistrarT.getMoneda());
			
			this.solicitudRegistrarT.setFecha(new Date());
			this.solicitudRegistrarT.setEstado(this.solicitudRegistrarT.getEstado().trim());

			logger.info("usuario.getUID() " + usuario.getUID());
			this.solicitudRegistrarT.setRegUsuario(usuario.getUID());
			this.solicitudRegistrarT.setNomUsuario(usuario.getNombre());
			logger.info("tiivsOficina1.codOfi ::::::: "+ this.solicitudRegistrarT.getTiivsOficina1().getCodOfi());
			for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
				if (tiivsOficina1.getCodOfi().equals(this.solicitudRegistrarT.getTiivsOficina1().getCodOfi())) {
					this.solicitudRegistrarT.setTiivsOficina1(tiivsOficina1);
				}
			}

			logger.info("solicitudRegistrarT.getTiivsSolicitudAgrupacions() : "+ solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
			if (this.validarRegistroSolicitud()) {
				if (!this.sEstadoSolicitud.equals("BORRADOR")) {
					this.enviarSolicitudSSJJ();
					logger.info(solicitudRegistrarT.getTiivsEstudio().getCodEstudio());
					
				}
				
			
				
						SolicitudDao<TiivsPersona, Object> servicePK = (SolicitudDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
						String sCodigoSol = servicePK.obtenerPKNuevaSolicitud();
						logger.debug(" sCodigoSol " + sCodigoSol);
						this.solicitudRegistrarT.setCodSoli(sCodigoSol);
				
				
						for (TiivsSolicitudAgrupacion x : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
							  //x.setTiivsSolicitud(this.solicitudRegistrarT);
							  x.getId().setCodSoli(sCodigoSol);
						}
				TiivsSolicitud objResultado = service.insertar(this.solicitudRegistrarT);
				this.solicitudRegistrarT.setCodSoli(objResultado.getCodSoli());
				  for (TiivsSolicitudAgrupacion x : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
					  x.setTiivsSolicitud(this.solicitudRegistrarT);
					  x.getId().setCodSoli(this.solicitudRegistrarT.getCodSoli());
				  for (TiivsAgrupacionPersona b :x.getTiivsAgrupacionPersonas()) { 
					  logger.info("b.getTiivsPersona().getCodPer()  " +b.getTiivsPersona().getCodPer());
					  objPersonaRetorno=servicePers.insertarMerge(b.getTiivsPersona());
					     b.setCodSoli(this.solicitudRegistrarT.getCodSoli());
					     b.setTiivsPersona(null);
					     logger.info("objPersonaRetorno.getCodPer() " +objPersonaRetorno.getCodPer());
					     b.setCodPer(objPersonaRetorno.getCodPer());
					     serviceAgru.insertar(b);
					     } 
				  
				  }
				  TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
				  objHistorial.setId(new TiivsHistSolicitudId(this.solicitudRegistrarT.getCodSoli(),1+""));
				  objHistorial.setEstado(this.solicitudRegistrarT.getEstado());
				  objHistorial.setNomUsuario(usuario.getNombre());
				  objHistorial.setObs(this.solicitudRegistrarT.getObs());
				  objHistorial.setFecha(new Timestamp(new Date().getTime()));
				  objHistorial.setRegUsuario(usuario.getUID());
				  serviceHistorialSolicitud.insertar(objHistorial);
				 
				  
				  for (TiivsAnexoSolicitud n : this.lstAnexoSolicitud) {
					  n.getId().setCodSoli(solicitudRegistrarT.getCodSoli());
					  serviceAnexos.insertar(n);
				   }
				  
				 
				for (TiivsSolicitudOperban a : this.lstSolicBancarias) {
					logger.info("a.getId().getCodOperBan() **** "+ a.getId().getCodOperBan());
					a.getId().setCodSoli(this.solicitudRegistrarT.getCodSoli());
					logger.info("a.getId().getCodSoli() **** "+ a.getId().getCodSoli());
					 serviceSoli.insertar(a);
				}
				
				 //Carga ficheros al FTP
				  boolean bRet = cargarArchivosFTP();
				  logger.info("Resultado de carga de archivos al FTP:" + bRet);
				  //Elimina archivos temporales
				  eliminarArchivosTemporales();
				  
				if (objResultado.getCodSoli() != "" || objResultado != null) {
					if (this.sEstadoSolicitud.equals("BORRADOR")) {
						mensaje = "Se registro correctamente la Solicitud con codigo : "+ objResultado.getCodSoli() + " en Borrador";
						Utilitarios.mensajeInfo("INFO", mensaje);
						actualizarBandeja=true;
					} else {
						mensaje = "Se envio a SSJJ correctamente la Solicitud con codigo : "+ objResultado.getCodSoli();
						Utilitarios.mensajeInfo("INFO", mensaje);
						actualizarBandeja=true;
					}
					
				} else {
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
				
					
				}

				logger.info("objResultado.getCodSoli(); "+ objResultado.getCodSoli());
				logger.info("objResultado.getTiivsSolicitudAgrupacions() "+ objResultado.getTiivsSolicitudAgrupacions().size());
				logger.info("this.solicitudRegistrarT.importe : " +this.solicitudRegistrarT.getImporte());
				//instanciarSolicitudRegistro();
			/*	if (!this.sEstadoSolicitud.equals("BORRADOR")) {
					
					ConsultarSolicitudMB a =new ConsultarSolicitudMB(solicitudRegistrarT);
					//a.obtenerSolicitud();
					redirect = "/faces/paginas/detalleSolicitud.xhtml";
				}else{
					ConsultarSolicitudMB a =new ConsultarSolicitudMB(solicitudRegistrarT);
					a.obtenerSolicitud();
					redirect = "/faces/paginas/solicitudEdicion.xhtml";
				}*/
//				if (actualizarBandeja)
//				{
//					this.seguimientoMB.busquedaSolicitudes();
//				}
				
			}
		} catch (Exception e) {
			redirect="";
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+e.getMessage());
			Utilitarios.mensajeError("ERROR", "Ocurrio un Error al grabar la Solicitud");
			e.printStackTrace();

		}
		return redirect;

	}

	
/**Enviar la solicitud a SSJJ*/
	public void enviarSolicitudSSJJ() {
		Timestamp time = new Timestamp(objRegistroUtilesMB.obtenerFechaRespuesta().getTime());
		logger.info("time : " + time);
		String sCodigoEstudio = objRegistroUtilesMB.obtenerEstudioMenorCarga();
		logger.info(" sCodigoEstudio +  " + sCodigoEstudio);
		for (TiivsEstudio x : combosMB.getLstEstudio()) {
			if (x.getCodEstudio().equals(sCodigoEstudio)) {
				this.solicitudRegistrarT.setTiivsEstudio(x);
			}
		}
		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02);
		this.solicitudRegistrarT.setFechaRespuesta(time);
		this.solicitudRegistrarT.setFechaEnvio(new Timestamp(new Date().getTime()));
	}
	@SuppressWarnings({ "unchecked", "null" })
	public boolean validarNroVoucher() throws Exception{
		boolean booleano=true;
		if (!this.sEstadoSolicitud.equals("BORRADOR")) {
		String mensaje="Ingrese un Nro de Vourcher no registrado ";
		Busqueda filtroNroVoucher = Busqueda.forClass(TiivsSolicitud.class);
		GenericDao<TiivsSolicitud, String> serviceNroVoucher=(GenericDao<TiivsSolicitud, String>) SpringInit.getApplicationContext().getBean("genericoDao");
		List<TiivsSolicitud> lstSolicitud =new ArrayList<TiivsSolicitud>();
		lstSolicitud=serviceNroVoucher.buscarDinamico(filtroNroVoucher);
		if(lstSolicitud!=null){
		for (TiivsSolicitud a : lstSolicitud) {
			if(a!=null||!a.equals("")){
			if(a.getNroVoucher()!=(null)){
			if(a.getNroVoucher().equals(this.solicitudRegistrarT.getNroVoucher())){
				booleano=false;
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

	public void registrarSolicitudBorrador() {
		sEstadoSolicitud = "BORRADOR";
	}

	public void registrarSolicitudEnviado() {
		sEstadoSolicitud = "ENVIADO";
	}

	public boolean validarRegistroSolicitud() throws Exception {
		
		  
		boolean retorno = true;
		String mensaje = "";
		System.out.println("solicitudRegistrarT.getTiivsOficina1() "+solicitudRegistrarT.getTiivsOficina1().getCodOfi());
		/*if (solicitudRegistrarT.getTiivsOficina1() == null) {
			mensaje = "Ingrese la Oficina";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if (solicitudRegistrarT.getNroVoucher() == null ) {
			mensaje = "Ingrese el Nro Voucher";
			retorno =this.validarNroVoucher();;
			Utilitarios.mensajeInfo("INFO", mensaje);
			
		}else if (solicitudRegistrarT.getNroVoucher().equals("")){
			mensaje = "Ingrese el Nro Voucher";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
			
		} else if (solicitudRegistrarT.getNroVoucher().length() < 11) {
			mensaje = "Ingrese Nro Voucher correcto de 11 digitos";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
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

		if (this.lstSolicBancarias.size() == 0) {

			mensaje = "Ingrese al menos una Operación Bancaria";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
			
		
		return retorno;
	}

	public String prepararURLEscaneo() {			
		logger.info("***********prepararURLEscaneo***************");
		
		String sCadena = "";		
		try{
//			usuario.setUID("P014773");				
			pdfViewerMB = new PDFViewerMB();	
			sCadena = pdfViewerMB.prepararURLEscaneo(usuario.getUID());			
		}catch(Exception e){
			logger.error("Error al obtener parámetros de APPLET",e);
		}
		return sCadena;
		
	}
	 
	public boolean cargarArchivosFTP(){
		
		logger.info("************cargarArchivosFTP()*¨**************");
		
		boolean exito = true;
		String sUbicacionTemporal = getUbicacionTemporal();									
		logger.info("ubicacion temporal "+ sUbicacionTemporal);

		for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){
			
			String ruta = pdfViewerMB.cargarUnicoPDF(anexo.getId().getCodSoli() + "_" + anexo.getAliasArchivo(),sUbicacionTemporal + anexo.getAliasTemporal());					
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
		logger.info("************eliminarArchivosTemporales()*¨**************");
		logger.info("Archivos a eliminar:" + aliasFilesToDelete.size()); 	
		File fileToDelete = null;
		
		for(String sfile : aliasFilesToDelete){
			logger.debug("borrar archivo: " + this.getUbicacionTemporal() + sfile);
			fileToDelete = new File(this.getUbicacionTemporal() + sfile);
			if(fileToDelete.delete()){
				logger.debug("borro archivo temporal :" + sfile);
			} else {
				logger.debug("no borro archivo temporal :" + sfile);
			}
		}
		
		fileToDelete = null;
		aliasFilesToDelete = new ArrayList<String>();		
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

	public void setLstTiivsOperacionBancaria(
			List<TiivsOperacionBancaria> lstTiivsOperacionBancaria) {
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

	public void setObjTiivsPersonaResultado(
			TiivsPersona objTiivsPersonaResultado) {
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

	public TiivsSolicitudOperban getObjSolicBancaria() {
		return objSolicBancaria;
	}

	public void setObjSolicBancaria(TiivsSolicitudOperban objSolicBancaria) {
		this.objSolicBancaria = objSolicBancaria;
	}

	public List<TiivsSolicitudOperban> getLstSolicBancarias() {
		return lstSolicBancarias;
	}

	public void setLstSolicBancarias(
			List<TiivsSolicitudOperban> lstSolicBancarias) {
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

	public DocumentoTipoSolicitudDTO getSelectedDocumentoDTO() {
		return selectedDocumentoDTO;
	}

	public void setSelectedDocumentoDTO(DocumentoTipoSolicitudDTO selectedDocumentoDTO) {
		this.selectedDocumentoDTO = selectedDocumentoDTO;
	}

	public TiivsPersona getObjTiivsPersonaCapturado() {
		return objTiivsPersonaCapturado;
	}

	public void setObjTiivsPersonaCapturado(
			TiivsPersona objTiivsPersonaCapturado) {
		this.objTiivsPersonaCapturado = objTiivsPersonaCapturado;
	}

	public int getIndexUpdatePersona() {
		return indexUpdatePersona;
	}

	public void setIndexUpdatePersona(int indexUpdatePersona) {
		this.indexUpdatePersona = indexUpdatePersona;
	}	
	
	public TiivsTipoSolicDocumento getSelectedTipoDocumento() {
		return selectedTipoDocumento;
	}

	public void setSelectedTipoDocumento(
			TiivsTipoSolicDocumento selectedTipoDocumento) {
		this.selectedTipoDocumento = selectedTipoDocumento;
	}

	public String getsCodDocumento() {
		return sCodDocumento;
	}

	public void setsCodDocumento(String sCodDocumento) {
		this.sCodDocumento = sCodDocumento;
	}

	public String getCadenaEscanerFinal() {
		return cadenaEscanerFinal;
	}

	public void setCadenaEscanerFinal(String cadenaEscanerFinal) {
		this.cadenaEscanerFinal = cadenaEscanerFinal;
	}
	
	

	/*public boolean isbBooleanPopupTipoCambio() {
		return bBooleanPopupTipoCambio;
	}

	public void setbBooleanPopupTipoCambio(boolean bBooleanPopupTipoCambio) {
		this.bBooleanPopupTipoCambio = bBooleanPopupTipoCambio;
	}*/	

	public String getUbicacionTemporal() {
		return ubicacionTemporal;
	}

	public void setUbicacionTemporal(String ubicacionTemporal) {
		this.ubicacionTemporal = ubicacionTemporal;
	}

	public List<ComboDto> getLstClasificacionPersona() {
		return this.lstClasificacionPersona;
	}

	public void setLstClasificacionPersona(List<ComboDto> lstClasificacionPersona) {
		this.lstClasificacionPersona = lstClasificacionPersona;
	}

	/*public boolean isBoleanoMensajeInfoGeneral() {
		return this.boleanoMensajeInfoGeneral;
	}

	public void setBoleanoMensajeInfoGeneral(boolean boleanoMensajeInfoGeneral) {
		this.boleanoMensajeInfoGeneral = boleanoMensajeInfoGeneral;
	}

	public boolean isBoleanoMensajeApoderdantePoderdante() {
		return this.boleanoMensajeApoderdantePoderdante;
	}

	public void setBoleanoMensajeApoderdantePoderdante(
			boolean boleanoMensajeApoderdantePoderdante) {
		this.boleanoMensajeApoderdantePoderdante = boleanoMensajeApoderdantePoderdante;
	}

	public boolean isBoleanoMensajeOperacionesBancarias() {
		return this.boleanoMensajeOperacionesBancarias;
	}

	public void setBoleanoMensajeOperacionesBancarias(
			boolean boleanoMensajeOperacionesBancarias) {
		this.boleanoMensajeOperacionesBancarias = boleanoMensajeOperacionesBancarias;
	}

	public boolean isBoleanoMensajeDocumentos() {
		return this.boleanoMensajeDocumentos;
	}

	public void setBoleanoMensajeDocumentos(boolean boleanoMensajeDocumentos) {
		this.boleanoMensajeDocumentos = boleanoMensajeDocumentos;
	}*/	

	public String getDocumentosLeer() {
		return documentosLeer;
	}

	public String getDocumentosLeidos() {
		return documentosLeidos;
	}

	public void setDocumentosLeidos(String documentosLeidos) {
		this.documentosLeidos = documentosLeidos;
	}

	public SeguimientoMB getSeguimientoMB() {
		return seguimientoMB;
	}

	public void setSeguimientoMB(SeguimientoMB seguimientoMB) {
		this.seguimientoMB = seguimientoMB;
	}
	
	
	

}

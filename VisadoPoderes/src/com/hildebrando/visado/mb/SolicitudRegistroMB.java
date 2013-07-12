package com.hildebrando.visado.mb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import com.bbva.common.util.EstilosNavegador;
import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecServiceImpl;
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
import com.hildebrando.visado.modelo.TiivsRevocado;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsSolicitudOperbanId;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;
import com.hildebrando.visado.service.TiposDoiService;

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
	@ManagedProperty(value = "#{visadoDocumentosMB}")
	private VisadoDocumentosMB visadoDocumentosMB;
	@ManagedProperty(value = "#{consultarSolicitudMB}")
	private ConsultarSolicitudMB consultarSolicitudMB;
	private String patter;
	
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
	boolean bBooleanPopupTipoCambio = true;
	boolean bBooleanPopupEmail = true;
	private boolean flagUpdatePoderdanteApoderados=false;
	private boolean flagUpdateOperacionSolic = false;
	private boolean flagUpdateOperacionSolcAgrupac = false;
	private boolean flagUpdateOperacionSolcDocumen = false;
	private boolean flagUpdatePersona = false;
	private boolean flagMostrarACOficina=false;
	private boolean flagMostrarSOMOficina=true;
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
	//Map<Integer, TiivsSolicitudOperban> mapSolicitudes;
	public static Logger logger = Logger.getLogger(SolicitudRegistroMB.class);
	String cadenaEscanerFinal = "";
	//http://172.31.9.41:9080/NAEWeb/pages/escaner/InvocaEscaner.xhtml?idEmpresa=1&idSistema=98&txLogin=P014773
	private UploadedFile file;
	private String sMonedaImporteGlobal;  
	private String ancho_FieldSet;
	private String ancho_FieldSet_Poder;
	private String ancho_Popup_Poder;
	private String ancho_Revoc_Poder;
	EstilosNavegador estilosNavegador;
	private TiivsAgrupacionPersona tiivsAgrupacionPersonaCapturado;
	private TiivsSolicitudAgrupacion tiivsSolicitudAgrupacionCapturado;
	private TiivsSolicitud solicitudRegistrarTCopia;
	List<TiivsAgrupacionPersona> listaTemporalAgrupacionesPersonaBorradores;
	List<TiivsPersona> listaTemporalPersonasBorradores;
	List<TiivsPersona> lstTiivsPersonaCopia;
	private TiivsOficina1 oficina;
	private String redirect = "";
	private String mesajeConfirmacion = "";
	
	/*private boolean boleanoMensajeInfoGeneral=true;
	private boolean boleanoMensajeApoderdantePoderdante=true;
	private boolean boleanoMensajeOperacionesBancarias=true;
	private boolean boleanoMensajeDocumentos=true;
	*/
	private boolean mostrarRazonSocial = false;
	private String codigoRazonSocial = "0000";
	
	
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
		oficina = new TiivsOficina1();

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
		//mapSolicitudes=new HashMap<Integer, TiivsSolicitudOperban>();
		
		this.cadenaEscanerFinal = this.prepararURLEscaneo();	
		 estilosNavegador=new EstilosNavegador();
		estilosNavegador.estilosNavegador();
		ancho_FieldSet = (String) Utilitarios.getObjectInSession("ANCHO_FIELDSET");
		ancho_FieldSet_Poder = (String) Utilitarios.getObjectInSession("ANCHO_FIELDSET_PODER");
		ancho_Popup_Poder=(String) Utilitarios.getObjectInSession("ANCHO_POPUP_PODER");
		ancho_Revoc_Poder=(String) Utilitarios.getObjectInSession("ANCHO_REVOC_PODER");
		
		logger.info("Ancho Fieldset Datos Generales: " + ancho_FieldSet);
		logger.info("Ancho Fieldset Datos Poderdantes: " + ancho_FieldSet_Poder);
		
		String sPerfilUsu=(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		if (sPerfilUsu.equals(ConstantesVisado.SSJJ))
		{
			setFlagMostrarSOMOficina(false);
			setFlagMostrarACOficina(true);
		}
		//logger.info("desdefe feedfcdf " +this.solicitudRegistrarT.getTiivsOficina1().getDescripcionMostrar());
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
	
	public void eliminarPersona() {
		logger.info("**************************** eliminarPersona ****************************");
		logger.info("Codigo de la persona capturada a Eliminar " +objTiivsPersonaCapturado.getCodPer());
		logger.info(" Lista de las Personas Antes de Remover " +lstTiivsPersona.size());
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
		

	}

	/*public void eliminarPersona() {
		logger.info("**************************** eliminarPersona ****************************");
		logger.info(objTiivsPersonaCapturado.getCodPer());
		lstTiivsPersona.remove(objTiivsPersonaCapturado);
		objTiivsPersonaCapturado=new TiivsPersona();
        this.flagUpdatePersona=false;
	}*/

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
	
	public String cargarUnicoPDF(String aliasArchivo) {
		
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
			
			logger.debug("  Ubicacion Temporal:"+ sUbicacionTemporal);
			
			File fDirectory = new File(sUbicacionTemporal);
			fDirectory.mkdirs();	
			
			String extension = file.getFileName().substring(getFile().getFileName().lastIndexOf("."));
						
			if(aliasArchivo.equals("")){
				aliasArchivo = "temp";
			} else {
				aliasArchivo = aliasArchivo + "_";
			}
			
			fichTemp = File.createTempFile(aliasArchivo, extension, new File(sUbicacionTemporal));
			
			sNombreTemporal = fichTemp.getName();
									
			logger.debug("  NombreArchivoTEMP: " + sNombreTemporal);
			
			canalSalida = new FileOutputStream(fichTemp);
			canalSalida.write(fileBytes);
			
			canalSalida.flush();
			return sNombreTemporal;

		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"IO Exception:"+e);
			String sMensaje = "Se produjo un error al adjuntar fichero";
			Utilitarios.mensajeInfo("", sMensaje);
			return "";
		} finally {
			if(fichTemp!=null){
				fichTemp.deleteOnExit(); // Delete the file when the JVM terminates
			}
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
		
		visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(lstDocumentosXTipoSolTemp));	
		
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
		bBooleanPopup=false;
	}

	public void buscarPersona() {
		logger.info("=== buscarPersona() ===");
		logger.info("[BUSQ_PERS]-TipoDoi:"+ objTiivsPersonaBusqueda.getTipDoi());
		logger.info("[BUSQ_PERS]-NroDoi:"+ objTiivsPersonaBusqueda.getNumDoi());
		try {
			List<TiivsPersona> lstTiivsPersonaLocal = new ArrayList<TiivsPersona>();
			//Se realiza la busqueda en BD Local
			lstTiivsPersonaLocal = this.buscarPersonaLocal();
			
			List<TiivsPersona> lstTiivsPersonaReniec = new ArrayList<TiivsPersona>();
			if (lstTiivsPersonaLocal.size() == 0) {
				//Se realiza la busqueda mediente servicio de RENIEC
				lstTiivsPersonaReniec = this.buscarPersonaReniec();
				if (lstTiivsPersonaReniec.size() == 0) {
					objTiivsPersonaResultado = new TiivsPersona();
					this.bBooleanPopup = false;
					 Utilitarios.mensajeInfo("INFO",ConstantesVisado.MENSAJE.NO_RESULTADOS+"para la busqueda.");
				} else if (lstTiivsPersonaReniec.size() == 1) {
					objTiivsPersonaResultado = lstTiivsPersonaReniec.get(0);
					this.bBooleanPopup = false;
				} else if (lstTiivsPersonaReniec.size() > 1) {
					this.bBooleanPopup = true;
					lstTiivsPersonaResultado = lstTiivsPersonaReniec;
				}
			} else if (lstTiivsPersonaLocal.size() == 1) {
				logger.info(ConstantesVisado.MENSAJE.SI_RESULTADOS+"lstTiivsPersonaLocal:  "+ lstTiivsPersonaLocal.size());
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
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"la persona (Local/Reniec)");
		}
	}

	public void limpiarCriteriosBusqueda() {
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

	public List<TiivsPersona> buscarPersonaReniec() throws Exception {
		logger.debug("==== inicia buscarPersonaReniec() ==== ");
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		try{
			BResult resultado = null;
			TiivsPersona objPersona = null;
			Persona persona = null;
			if (objTiivsPersonaBusqueda.getNumDoi() != null) {
				logger.info("[RENIEC]-DNI:"+ objTiivsPersonaBusqueda.getNumDoi());

				ObtenerPersonaReniecService reniecService = new ObtenerPersonaReniecServiceImpl();
				//ObtenerPersonaReniecDUMMY reniecService = new ObtenerPersonaReniecDUMMY();
				
				resultado = reniecService.devolverPersonaReniecDNI("P013371", "0553",objTiivsPersonaBusqueda.getNumDoi());
				logger.debug("[RENIEC]-resultado: "+resultado.getCode());
				
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
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"");
		}
		return lstTiivsPersona;
		
	}

	public List<TiivsPersona> buscarPersonaLocal() throws Exception {
		logger.info("=== buscarPersonaLocal() ===");
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
			
			//if (lstTiivsPersona.size() == 0 && busco) {
				//Utilitarios.mensajeInfo("INFO","No se han encontrado resultados para los criterios de busqueda seleccionados");
			//}
		}

		return lstTiivsPersona;
	}
	public boolean validarPersonaEnListaDeAgrupaciones(){
		logger.info("=== validarPersonaEnListaDeAgrupaciones() ===");
		boolean retorno = false;
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
		return retorno;
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
			//Utilitarios.mensajeInfo("INFO","Ingrese al menos un criterio de busqueda");
		} else if (objTiivsPersonaResultado.getNumDoi() == null
				|| objTiivsPersonaResultado.getNumDoi().equals("")) {
		//	Utilitarios.mensajeInfo("INFO", "Ingrese el Número de Doi");
		} else if (objTiivsPersonaResultado.getTipDoi() == null
				|| objTiivsPersonaResultado.getTipDoi().equals("")) {
			//Utilitarios.mensajeInfo("INFO", "Ingrese el Tipo de Doi");
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
				/*for (TiivsPersona tiivsPersona : lstTiivsPersona) {
					for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
						if (tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())) {
							tiivsPersona.setsDesctipDoi(p.getDescripcion());
						}
					}
				} */
               retorno=true;
			}
			
			if (lstTiivsPersona.size() == 0 && busco) {
				retorno=false;
				//Utilitarios.mensajeInfo("INFO","No se han encontrado resultados para los criterios de busqueda seleccionados");
			}else{
				retorno=true;
			}
		}

		return retorno;
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

	public void agregarPersona() 
	{
		logger.info("****************** agregarPersona ********************");
		if (validarPersona()) {
			if (validarRegistroDuplicado()) 
			{
				for (TipoDocumento p : combosMB.getLstTipoDocumentos()) 
				{
					if (objTiivsPersonaResultado.getTipDoi().equals(p.getCodTipoDoc())) {objTiivsPersonaResultado.setsDesctipDoi(p.getDescripcion());}
				}
				for (ComboDto p : combosMB.getLstTipoRegistroPersona()) 
				{
					if (objTiivsPersonaResultado.getTipPartic().equals(p.getKey())) {objTiivsPersonaResultado.setsDesctipPartic(p.getDescripcion());}
				}
				for (ComboDto p : combosMB.getLstClasificacionPersona()) 
				{
					if (objTiivsPersonaResultado.getClasifPer().equals(p.getKey())) {objTiivsPersonaResultado.setsDescclasifPer(p.getDescripcion());}
					//if (objTiivsPersonaResultado.getClasifPer().equals("99")) {objTiivsPersonaResultado.setsDescclasifPer(objTiivsPersonaResultado.getClasifPerOtro());}
				}
				
				/** Se agrega a la lista de personas en la grilla*/				
				if (!flagUpdatePersona) {
					if(objTiivsPersonaResultado.getCodPer()==0){
						objTiivsPersonaResultado = actualizarPersona(objTiivsPersonaResultado);		
					}
					//objTiivsPersonaResultado.setIdAgrupacion(tiivsAgrupacionPersonaCapturado.getIdAgrupacion());
					
					TiivsAgrupacionPersona agruPersona = new TiivsAgrupacionPersona();
					agruPersona.setTiivsPersona(objTiivsPersonaResultado);
					agruPersona.setClasifPer(objTiivsPersonaResultado.getClasifPer());
					agruPersona.setTipPartic(objTiivsPersonaResultado.getTipPartic());
					agruPersona.setCodPer(objTiivsPersonaResultado.getCodPer());
					
					agruPersona.setNumGrupo(tiivsSolicitudAgrupacionCapturado.getId().getNumGrupo()); 
					agruPersona.setCodSoli(tiivsSolicitudAgrupacionCapturado.getId().getCodSoli()); 
					agruPersona.setIdAgrupacion(tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas().size()+1); 
					
					agruPersona.setTiivsSolicitudAgrupacion(tiivsSolicitudAgrupacionCapturado);					
					objTiivsPersonaResultado.setIdAgrupacion(agruPersona.getIdAgrupacion());					
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

/*
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

					this.lstTiivsPersona.set(indexUpdatePersona,objTiivsPersonaResultado);
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
*/
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
		
		//Captura agrupacion persona
		for(TiivsAgrupacionPersona agruPersona: this.tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas()){
			//Si Personacapturado es igual a algun elemento de  this.tiivsSolicitudAgrupacionCapturado.getTiivsAgrupacionPersonas		
			
			if(agruPersona.getIdAgrupacion().equals(objTiivsPersonaCapturado.getIdAgrupacion())){
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
		this.objTiivsPersonaResultado.setClasifPer(this.objTiivsPersonaCapturado.getClasifPer());
		actualizarClasificacion();
		//this.objTiivsPersonaResultado.setClasifPerOtro(this.objTiivsPersonaCapturado.getClasifPerOtro());
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
		 if(lstAgrupacionSimpleDto.size()!=0){
			 NumeroGrupoMax= lstAgrupacionSimpleDto.get(0).getId().getNumGrupo();
		 }
		 
		 for (int i = 0; i < lstAgrupacionSimpleDto.size(); i++) {
			 logger.info("lstAgrupacionSimpleDto.get(i).getId().getNumGrupo() :: " +lstAgrupacionSimpleDto.get(i).getId().getNumGrupo());
			 if(lstAgrupacionSimpleDto.get(i).getId().getNumGrupo()>=NumeroGrupoMax) {
				 NumeroGrupoMax=lstAgrupacionSimpleDto.get(i).getId().getNumGrupo();
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
	

	
 /* public void agregarAgrupacion(){
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
	  
 }*/
  
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
			e.printStackTrace();
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
			
			logger.info("lstTiivsAgrupacionPersonas: inicio " + lstTiivsAgrupacionPersonas.size());
			
			limpiarAgrupacionesVacias();			

			List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();

			logger.debug("lstTiivsPersona " +lstTiivsPersona.size());

			for (TiivsPersona n : lstTiivsPersona) {
				if (n.getTipPartic().equals(ConstantesVisado.PODERDANTE)) {
					lstPoderdantes.add(n);
					logger.info(" poderdante : " + n.getCodPer());
				}
				if (n.getTipPartic().equals(ConstantesVisado.APODERADO)) {
					lstApoderdantes.add(n);
					logger.info(" apoderado : " + n.getCodPer());
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
	public String instanciarSolicitudRegistro() {
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
		
		if (usuario!=null)
		{
			logger.debug("usuario en session? --> " + usuario.getNombre());
			logger.debug("CodOfi: " + usuario.getBancoOficina().getCodigo().trim());
			logger.debug("DesOfi: "+ usuario.getBancoOficina().getDescripcion().trim());
		}
		
		TiivsOficina1 oficina = new TiivsOficina1();
		oficina.setCodOfi(usuario.getBancoOficina().getCodigo());

		List<TiivsOficina1> lstOficinas1 = new ArrayList<TiivsOficina1>();
		combosMB = new CombosMB();
		lstOficinas1 = combosMB.getLstOficina();

		if (lstOficinas1 != null && lstOficinas1.size() != 0) {
			for (TiivsOficina1 o : lstOficinas1) {
			//	logger.debug("codigo ofi usuario : "+ usuario.getBancoOficina().getCodigo().trim()+" :::: "+o.getCodOfi());
				if (usuario.getBancoOficina().getCodigo().trim().equals(o.getCodOfi())) {
					this.solicitudRegistrarT.setTiivsOficina1(o);
					break;
				} else {
					if(this.solicitudRegistrarT.getTiivsOficina1()==null){
					this.solicitudRegistrarT.setTiivsOficina1(new TiivsOficina1());
					}
				}
			}
		
		}
	
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
		oficina = new TiivsOficina1();
		
		obtenCodRazonSocial(); 


		return "/faces/paginas/solicitud.xhtml";
	}	

	public void actualizarListaDocumentosXTipo(TiivsAnexoSolicitud objAnexo) {
		
		try{
			logger.info("=== actualizarListaDocumentosXTipo() ===");
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
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al actualizarListaDocumentosXTipo"+e);
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
	
	/**
	 * Metodo que se encarga de validar que los documentos no sean 
	 * duplicados, es decir si ya existe un documento en la lista.
	 * @return result Flag booleano
	 * **/
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

	/**
	 * Metodo que se encarga de adjuntar y asociar el archivo 
	 * seleccionado a la lista de documentos requeridos por 
	 * tipo de solicitud en la aplicacion
	 * **/
	public void agregarDocumentosXTipoSolicitud() {
	/*	boleanoMensajeInfoGeneral=false;
		 boleanoMensajeApoderdantePoderdante=false;
		 boleanoMensajeOperacionesBancarias=false;
		 boleanoMensajeDocumentos=true;*/
		try{
			logger.info("=== agregarDocumentosXTipoSolicitud() ===");
			logger.info("[Adjuntar]-TipoSolicitud: " + iTipoSolicitud);
			String aliasCortoDocumento="";
			if(selectedTipoDocumento!=null){
				sCodDocumento = selectedTipoDocumento.getId().getCodDoc();
			} else {
				logger.debug("selectedTipoDocumento es null");
				sCodDocumento = null;
			}
			//Se valida que el doc no sea vacio o nulo.
			if(sCodDocumento == null || sCodDocumento.isEmpty()){
				Utilitarios.mensajeInfo("", "Debe seleccionar un documento");
				 return;
			}
			logger.info("[Adjuntar]-codDocumento: " + sCodDocumento);
			logger.info("lstAnexoSolicitud.size() :  " + lstAnexoSolicitud.size());
			
			//Se valida la duplicidad de documentos
			if (this.ValidarDocumentosDuplicados()) {
													
				if (sCodDocumento.equalsIgnoreCase(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
					sCodDocumento = ConstantesVisado.PREFIJO_OTROS+String.format("%06d", lstdocumentos.size() + 1);
					aliasCortoDocumento = sCodDocumento;
				} else {
					aliasCortoDocumento = selectedTipoDocumento.getTiivsDocumento().getNombre();
				}
							
				String sAliasTemporal = cargarUnicoPDF(aliasCortoDocumento);
										
				if(sAliasTemporal == null || sAliasTemporal.trim() ==""){	
					logger.debug("El sAliasTemporal es nulo o vacio.");
					return;
				}
				
				String sExtension = sAliasTemporal.substring(sAliasTemporal.lastIndexOf("."));
				aliasCortoDocumento += sExtension;
				
				logger.info("[Adjuntar]-aliasArchivo: " + aliasCortoDocumento);
				logger.info("[Adjuntar]-aliasArchivoTemporal: " + sAliasTemporal);
				
				TiivsAnexoSolicitud objAnexo = new TiivsAnexoSolicitud();
				objAnexo.setId(new TiivsAnexoSolicitudId(null, sCodDocumento));
				objAnexo.setAliasArchivo(aliasCortoDocumento.toUpperCase());
				objAnexo.setAliasTemporal(sAliasTemporal);
				
				lstAnexoSolicitud.add(objAnexo);
				
				this.actualizarListaDocumentosXTipo(objAnexo);

				establecerTipoSolicitud();
				
			}
			
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al agregarDocumentosXTipoSolicitud():"+e);
		}	
	}
	/**
	 * Metodo que se encarga de establecer/setear el 
	 * tipo de solicitud según el valor iTipoSolicitud.
	 * **/
	private void establecerTipoSolicitud(){
		try{
			for (TiivsTipoSolicitud tipoSoli : combosMB.getLstTipoSolicitud()) {
				if (tipoSoli.getCodTipSolic().equals(iTipoSolicitud)) {
					solicitudRegistrarT.setTiivsTipoSolicitud(tipoSoli);
					break;
				} else {
					solicitudRegistrarT.setTiivsTipoSolicitud(null);
				}
			}
		}catch(Exception e){
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al establecerTipoSolicitud(): "+e);
		}
	}

	public void quitarDocumentosXTipoSolicitud() {
		try{
			logger.info("== quitarDocumentosXTipoSolicitud() ==");						
			if (this.selectedDocumentoDTO.getAlias().isEmpty()) {
				return;
			}
			logger.info("[QUITAR_DOC]-iTipoSolicitud: " + iTipoSolicitud);
			logger.info("[QUITAR_DOC]-lstAnexoSolicitud: " + lstAnexoSolicitud.size());
			logger.info("[QUITAR_DOC]-SelectedTipoDocumento: " + this.selectedDocumentoDTO.getItem());
			
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
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al quitar documentos: "+e);
		}
	}
	
	/*
	 * Metodo que actualiza la lista de documentos, este método es indirectamente
	 * invocado desde el applet
	 * */
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
							objAnexo.setId(new TiivsAnexoSolicitudId(null, doc.getItem()));
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
		
		establecerTipoSolicitud();
				
		logger.info("(Tabla) lstdocumentos tamaño:" + lstdocumentos.size());
		logger.info("(Anexos)lstAnexoSolicitud tamaño:" + lstdocumentos.size());
		logger.info("(Combo) lstTipoSolicitudDocumentos tamaño:" + lstdocumentos.size());
	}
	
	/**
	 * Metodo que valida la operacion bancaria, incluye validaciones
	 * de los diferentes campos en el formulario (No vacios, no nulos, etc)
	 * @return resul Tipo booelan
	 * **/
	public boolean validarOperacionBancaria() {
		boolean result = true;
		String sMensaje = "";
		if (objSolicBancaria.getId().getCodOperBan().equals("")) {
			sMensaje = "Ingrese un tipo de Operación";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (objSolicBancaria.getId().getMoneda().equals("")) {
			sMensaje = "Seleccione una moneda";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (!objSolicBancaria.getId().getMoneda().equals("")&& !objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)
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
					//System.out.println("x.getMoneda().trim() " +x.getId().getMoneda().trim());
					//System.out.println("objSolicBancaria.getMoneda().trim()) " +objSolicBancaria.getId().getMoneda().trim());
					
					if(x.getId().getMoneda().trim().equals(objSolicBancaria.getId().getMoneda().trim())) {
						conunt++;
						//logger.info("conunt "+conunt);
						//System.out.println("x.getMoneda() " +x.getId().getMoneda());
						//System.out.println("objSolicBancaria.getMoneda() " + objSolicBancaria.getId().getMoneda());
						
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
	String AntiguoValorDelTipoCambio =null;
	public void validarTipoCambioDisabled(ValueChangeEvent e){
		if(e.getNewValue()!=null){
		logger.info(" validarTipoCambioDisabled " +e.getNewValue());
		AntiguoValorDelTipoCambio=(String) e.getOldValue();
		if (e.getNewValue().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
			this.objSolicBancaria.setTipoCambio(0.0);
			bBooleanPopupTipoCambio=true;
		}else{
			//this.objSolicBancaria.setTipoCambio(0.0);
			bBooleanPopupTipoCambio=false;
		
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
					//Double valorTemp = (double) (Math.round(valorFinal*100)/100);
					
					/*DecimalFormat formateador = new DecimalFormat("###,###.##");
					Double monto=(double) Math.round(valorFinal*100)/100;
					formateador.format(monto);
					
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ monto);*/
					
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+  ((double) Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
					//ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					
					/*DecimalFormat formateador = new DecimalFormat("###,###.##");
					Double monto=(double) Math.round(valorFinal*100)/100;
					formateador.format(monto);
					
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ monto);*/
					
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+  ((double)Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
					//ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					
					/*DecimalFormat formateador = new DecimalFormat("###,###.##");
					Double monto=(double) Math.round(valorFinal*100)/100;
					formateador.format(monto);
					
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ monto);*/
					
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+ ( (double) Math.round(valorFinal*100)/100));
	                this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (icontDolares  > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares  > 0 && icontEuros == 0&& icontSoles > 0 
						|| icontDolares == 0 && icontEuros > 0 && icontSoles > 0
						|| icontDolares > 0  && icontEuros > 0 && icontSoles > 0) 
				{
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporteSoles();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					
					/*DecimalFormat formateador = new DecimalFormat("###,###.##");
					Double monto=(double) Math.round(valorFinal*100)/100;
					formateador.format(monto);
					
					this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ monto);*/
					
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+  ((double) Math.round(valorFinal*100)/100));
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+ ((double)Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
					//ONLI DOLARES
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+((double)Math.round(valorFinal*100)/100));
	            	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
					//ONLI EUROS
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporte();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+((double)Math.round(valorFinal*100)/100));
	                this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (icontDolares  > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares  > 0 && icontEuros == 0&& icontSoles > 0 
						|| icontDolares == 0 && icontEuros > 0 && icontSoles > 0
						|| icontDolares > 0  && icontEuros > 0 && icontSoles > 0) 
				{
					for (TiivsSolicitudOperban x : lstSolicBancarias) {
						valorFinal=valorFinal+x.getImporteSoles();
						this.solicitudRegistrarT.setImporte(valorFinal);
					}
					
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
					
					//this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+((double)Math.round(valorFinal*100)/100));
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

	public void limpiarListaSolicitudesBancarias() {
		logger.info("**************************** limpiarListaSolicitudesBancarias ****************************");
		this.objSolicBancaria=new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		this.objSolicBancaria.setTipoCambio(0.00);
		this.objSolicBancaria.setImporte(0.00);
		bBooleanPopupTipoCambio=true;
		
		flagUpdateOperacionSolic=false;
		//this.lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
	}

	public void seterComentario() {
		logger.info("**************************** Setear Comentario ****************************");
		logger.info("Comentario : " + this.solicitudRegistrarT.getObs());
	}
	public void limpiarComentario() {
		logger.info("**************************** limpiar Comentario ****************************");
		 this.solicitudRegistrarT.setObs("");
	}

	public void eliminarOperacionBancaria() {
		logger.info("**************************** eliminarOperacionBancaria ****************************");
		// String valor = Utilitarios.capturarParametro("objOperacion");
		// logger.info("CODIGO DE OPERACION "+valor);
		
		
		logger.info(objSolicitudOperacionCapturado.getImporte());
		lstSolicBancarias.remove(objSolicitudOperacionCapturado);
		
		
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

		
		this.limpiarOperacionesBancarias();
        this.llamarComision();
		

	     

	}
	public void limpiarOperacionesBancarias(){
		icontDolares=0;icontEuros=0;icontSoles=0;valorFinal=0;
		  objSolicBancaria=new TiivsSolicitudOperban();
		  objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		  objSolicBancaria.setTipoCambio(0.0);
          objSolicBancaria.setImporte(0.0);
          objSolicBancaria.setImporteSoles(0.0);
		  this.flagUpdateOperacionSolic=false;
		  this.objSolicitudOperacionCapturado=new TiivsSolicitudOperban();
		  this.objSolicitudOperacionCapturado.setId(new TiivsSolicitudOperbanId());
		  this.valorFinal=0.0;
		  if(lstSolicBancarias.size()==0){
				this.solicitudRegistrarT.setsImporteMoneda(valorFinal+"");
		  	this.solicitudRegistrarT.setMoneda(null);
			}
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
	public String verEditarAgrupacion(){
		logger.info("********************** verEditarAgrupacion *********************************** ");
		logger.info("this.getCodSoli  "+ this.objAgrupacionSimpleDtoCapturado.getId().getCodSoli());
		logger.info("this.getNumGrupo  "+ this.objAgrupacionSimpleDtoCapturado.getId().getNumGrupo());
		logger.info("this.getLstPersonas  "+ this.objAgrupacionSimpleDtoCapturado.getLstPersonas().size());
		
		
		combosMB=new CombosMB();
		lstClasificacionPersona=combosMB.getLstClasificacionPersona();
		logger.info("tamanioo actual de la lista de Clasificacion **** " +lstClasificacionPersona.size());
		
		for (TiivsSolicitudAgrupacion a : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
			if (a.getId().equals(objAgrupacionSimpleDtoCapturado.getId())) {
				tiivsSolicitudAgrupacionCapturado = a;
				break;
			}
		}

				  
		/**Recien se empieza a llenar la lista de Persona */
		
		setLstTiivsPersona(objAgrupacionSimpleDtoCapturado.getLstPersonas());
		
		/*for (AgrupacionSimpleDto dd: this.solicitudRegistrarTCopia.getLstAgrupacionSimpleDto()) {
			if(dd.equals(objAgrupacionSimpleDtoCapturado)){
				lstTiivsPersonaCopia =new ArrayList<TiivsPersona>();
				lstTiivsPersonaCopia.addAll(dd.getLstPersonas());				
			}
			break;
		}
		*/
		listaTemporalPersonasBorradores=new ArrayList<TiivsPersona>();
		listaTemporalAgrupacionesPersonaBorradores=new ArrayList<TiivsAgrupacionPersona>();
		logger.info("this.getLstPersonas  "+ lstTiivsPersona.size());
		flagUpdatePoderdanteApoderados=true;
		bBooleanPopup=false;
		//return  "/faces/paginas/solicitudEdicion.xhtml";
		return "";
	}
/*	public void verEditarAgrupacion(){
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
	*/
	
	
	int y=0;
	public void obtenerAccionAgregarOperacionBancaria() {
		logger.info("**************************** obtenerAccionAgregarOperacionBancaria ****************************");
		logger.info("********************************************* : "+ objSolicBancaria.getImporte());
		
		if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
			objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
			objSolicBancaria.setTipoCambio(0.0);
		}
		
		if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) {
			
			objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
			
		}
		if (objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) {
			
			objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
			
		}
		/** SAMIRA*/
		if(!objSolicBancaria.getId().getMoneda().equals(AntiguoValorDelTipoCambio)&&!objSolicBancaria.getId().getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)&&AntiguoValorDelTipoCambio!=null){
			logger.info("*** objSolicBancaria.getId().getMoneda() ***"+objSolicBancaria.getId().getMoneda());
			logger.info("nuevoValorDelTipoCambio ********* " + AntiguoValorDelTipoCambio);
			objSolicBancaria.setTipoCambio(0.0);
			objSolicBancaria.setImporteSoles(objSolicBancaria.getTipoCambio()* objSolicBancaria.getImporte());
			AntiguoValorDelTipoCambio=null;
		}
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
		//mapSolicitudes.put(y, objSolicitudOperacionCapturado);
		
		//Set set = mapSolicitudes.entrySet(); 
		// Get an iterator 
		//Iterator iterate = set.iterator(); 
		// Display elements 
		
		
		//logger.info("mapSolicitudes.get(mapSolicitudes.size()).getImporte()"+mapSolicitudes.get(mapSolicitudes.size()).getImporte());
		//if(!flagUpdateOperacionSolic){
		this.objSolicitudOperacionCapturadoOld= this.objSolicitudOperacionCapturado;
		this.objSolicitudOperacionCapturadoOld.setId(this.objSolicitudOperacionCapturado.getId());
		this.objSolicitudOperacionCapturadoOld.setImporte(this.objSolicitudOperacionCapturado.getImporte());
		this.objSolicitudOperacionCapturadoOld.setImporteSoles(this.objSolicitudOperacionCapturado.getImporteSoles());
		this.objSolicitudOperacionCapturadoOld.setTiivsOperacionBancaria(this.objSolicitudOperacionCapturado.getTiivsOperacionBancaria());
		this.objSolicitudOperacionCapturadoOld.setTipoCambio(this.objSolicitudOperacionCapturado.getTipoCambio());
		this.objSolicitudOperacionCapturadoOld.setsItem(this.objSolicitudOperacionCapturado.getsItem());
		//}
		this.objSolicBancaria = this.objSolicitudOperacionCapturado;
		
		/** Validar el disabled del tipo de cambio */
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
	public void registrarSolicitud() 
	{
	/*	boleanoMensajeInfoGeneral=true;
		 boleanoMensajeApoderdantePoderdante=true;
		 boleanoMensajeOperacionesBancarias=true;
		 boleanoMensajeDocumentos=false;*/
		String mensaje = "";
//		String redirect = "";
		this.redirect = "";
		boolean actualizarBandeja=false;
		
		establecerTipoSolicitud();
		
		logger.info("*********************** registrarSolicitud ************************");
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
        GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	    
		try {
			logger.info("this.solicitudRegistrarT.importe : " +this.solicitudRegistrarT.getMoneda());
			
			this.solicitudRegistrarT.setFecha(new Date());
			this.solicitudRegistrarT.setEstado(this.solicitudRegistrarT.getEstado().trim());
			this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));

			logger.info("usuario.getUID() " + usuario.getUID());
			this.solicitudRegistrarT.setRegUsuario(usuario.getUID());
			this.solicitudRegistrarT.setNomUsuario(usuario.getNombre());
			
			if (flagMostrarACOficina)
			{
				if (oficina!=null)
				{
					logger.info("tiivsOficina1.codOfi ::::::: "+ oficina.getCodOfi());
					for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
						if (tiivsOficina1.getCodOfi().equals(oficina.getCodOfi())) {
							this.solicitudRegistrarT.setTiivsOficina1(oficina);
							break;
						} 
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

			logger.info("solicitudRegistrarT.getTiivsSolicitudAgrupacions() : "+ solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
			
			boolean esValido = false;
			if(!this.sEstadoSolicitud.equals("BORRADOR")){ 	//Validacion para envio de solicitud a SSJJ
				esValido = this.validarEnvioSolicitud();
			} else { 	//Validacion para registro de solicitud (Borrador)
				esValido = this.validarRegistroSolicitud();
			}
			
			//if (this.validarRegistroSolicitud()) 
			if (esValido)
			{
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
					  for (TiivsAgrupacionPersona a : x.getTiivsAgrupacionPersonas()) {
						a.setCodSoli(x.getId().getCodSoli());
						a.setNumGrupo(x.getId().getNumGrupo());
						a.setIdAgrupacion(null);//para que la bd asigne id agrupacion
					}
				}
				TiivsSolicitud objResultado = service.insertar(this.solicitudRegistrarT);
				
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
						mesajeConfirmacion = "Se registró correctamente la Solicitud con codigo : "+ objResultado.getCodSoli() + " en Borrador";
						actualizarBandeja=true;
					} else {
						mesajeConfirmacion = "Se envió a SSJJ correctamente la Solicitud con codigo : "+ objResultado.getCodSoli();
						actualizarBandeja=true;
					}
					//redirect = "/faces/paginas/bandejaSeguimiento.xhtml";					
					this.redirect = consultarSolicitudMB.redirectDetalleSolicitud(objResultado.getCodSoli());					
				} else {
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
				}

				logger.info("objResultado.getCodSoli(); "+ objResultado.getCodSoli());
				logger.info("objResultado.getTiivsSolicitudAgrupacions() "+ objResultado.getTiivsSolicitudAgrupacions().size());
				logger.info("this.solicitudRegistrarT.importe : " +this.solicitudRegistrarT.getImporte());					
				
				if (actualizarBandeja)
				{
					this.seguimientoMB.busquedaSolicitudxCodigo(objResultado.getCodSoli());
				}
				
			}
		} catch (Exception e) {
			this.redirect="";
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+e.getMessage(),e);
			Utilitarios.mensajeError("ERROR", "Ocurrió un Error al grabar la Solicitud");

		}
		logger.info("Redirec:" + this.redirect);
//		return this.redirect;

	}
		
	public String redireccionar(){
		Utilitarios.mensajeInfo("INFO", mesajeConfirmacion);
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
				break;
			}
		}
		this.solicitudRegistrarT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02);
		this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));
		this.solicitudRegistrarT.setFechaRespuesta(time);
		this.solicitudRegistrarT.setFechaEnvio(new Timestamp(new Date().getTime()));
	}
	public void actualizarVoucher(){
		//solicitudRegistrarT.nroVoucher
		logger.info("this.getSolicitudRegistrarT().getNroVoucher():" + this.getSolicitudRegistrarT().getNroVoucher());
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
		
		/*
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
		}
		*/
		 
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
		
//		if (this.lstAnexoSolicitud.size() == 0) {
//			mensaje = "Ingrese los documentos Obligatorios";
//			retorno = false;
//			Utilitarios.mensajeInfo("INFO", mensaje);
//		}

		if (this.lstSolicBancarias.size() == 0) {

			mensaje = "Ingrese al menos una Operación Bancaria";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		
		return retorno;
	}
	
	private boolean validarEnvioSolicitud() throws Exception {
		
		  
		boolean retorno = true;
		String mensaje = "";
		
		logger.info("solicitudRegistrarT.getTiivsOficina1() "+solicitudRegistrarT.getTiivsOficina1().getCodOfi());
		
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
				if (solicitudRegistrarT.getNroVoucher()==null){
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

	public String prepararURLEscaneo() {			
		logger.info("***********prepararURLEscaneo***************");
		
		String sCadena = "";		
		try{				
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
				logger.debug("Se subio el archivo: [" + anexo.getAliasTemporal() +"] al servidor.");
				exito = exito && true;
			} else {
				logger.debug("No se pudo subir el archivo: [" + anexo.getAliasTemporal() +"] al servidor.");
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
	
	public void cambiarRazonSocial(ValueChangeEvent e){		
		logger.info("************cambiarRazonSocial()*¨**************");
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
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+ "de multitablas: " + e);
		}
		
		
	}

	private void obtenCodRazonSocial() {
		TiposDoiService tiposDoiService = new TiposDoiService(); 			
		codigoRazonSocial = tiposDoiService.obtenerCodPersonaJuridica();
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
	
	

	public boolean isbBooleanPopupTipoCambio() {
		return bBooleanPopupTipoCambio;
	}

	public void setbBooleanPopupTipoCambio(boolean bBooleanPopupTipoCambio) {
		this.bBooleanPopupTipoCambio = bBooleanPopupTipoCambio;
	}

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

	public SeguimientoMB getSeguimientoMB() {
		return seguimientoMB;
	}

	public void setSeguimientoMB(SeguimientoMB seguimientoMB) {
		this.seguimientoMB = seguimientoMB;
	}

	public VisadoDocumentosMB getVisadoDocumentosMB() {
		return visadoDocumentosMB;
	}

	public void setVisadoDocumentosMB(VisadoDocumentosMB visadoDocumentosMB) {
		this.visadoDocumentosMB = visadoDocumentosMB;
	}

	public ConsultarSolicitudMB getConsultarSolicitudMB() {
		return consultarSolicitudMB;
	}

	public void setConsultarSolicitudMB(ConsultarSolicitudMB consultarSolicitudMB) {
		this.consultarSolicitudMB = consultarSolicitudMB;
	}

	public String getAncho_FieldSet() {
		return ancho_FieldSet;
	}

	public void setAncho_FieldSet(String ancho_FieldSet) {
		this.ancho_FieldSet = ancho_FieldSet;
	}

	public String getAncho_FieldSet_Poder() {
		return ancho_FieldSet_Poder;
	}

	public void setAncho_FieldSet_Poder(String ancho_FieldSet_Poder) {
		this.ancho_FieldSet_Poder = ancho_FieldSet_Poder;
	}

	public String getAncho_Popup_Poder() {
		return ancho_Popup_Poder;
	}

	public void setAncho_Popup_Poder(String ancho_Popup_Poder) {
		this.ancho_Popup_Poder = ancho_Popup_Poder;
	}

	public String getAncho_Revoc_Poder() {
		return ancho_Revoc_Poder;
	}

	public void setAncho_Revoc_Poder(String ancho_Revoc_Poder) {
		this.ancho_Revoc_Poder = ancho_Revoc_Poder;
	}

	public TiivsAgrupacionPersona getTiivsAgrupacionPersonaCapturado() {
		return this.tiivsAgrupacionPersonaCapturado;
	}

	public void setTiivsAgrupacionPersonaCapturado(
			TiivsAgrupacionPersona tiivsAgrupacionPersonaCapturado) {
		this.tiivsAgrupacionPersonaCapturado = tiivsAgrupacionPersonaCapturado;
	}

	public TiivsSolicitudAgrupacion getTiivsSolicitudAgrupacionCapturado() {
		return this.tiivsSolicitudAgrupacionCapturado;
	}

	public void setTiivsSolicitudAgrupacionCapturado(
			TiivsSolicitudAgrupacion tiivsSolicitudAgrupacionCapturado) {
		this.tiivsSolicitudAgrupacionCapturado = tiivsSolicitudAgrupacionCapturado;
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
	
	
	
}
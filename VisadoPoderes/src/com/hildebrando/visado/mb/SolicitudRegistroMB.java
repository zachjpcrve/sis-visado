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
import java.util.Date;
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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.common.util.EstilosNavegador;
import com.bbva.consulta.host.impl.ObtenerDatosVoucherDUMMY;
import com.bbva.consulta.pea.ObtenerDatosPersonaPEAService;
import com.bbva.consulta.pea.impl.ObtenerDatosPersonaPEAServiceDummy;
import com.bbva.consulta.pea.impl.ObtenerDatosPersonaPEAServiceImpl;
import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecDUMMY;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecServiceImpl;
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
import com.hildebrando.visado.modelo.TiivsHostVoucher;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsParametros;
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

/**
 * Clase encargada del registro de la solicitud de visado, esto implica
 * el envio de archivos adjuntos, obtener listados de BD, validaciones antes
 * del registro de la solcicitud, etc.
 * @author hildebrando
 * @version 1.0
 * **/

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
	private String tipoRegistro;
	private String poderdante;
	private String apoderdante;
	private boolean bBooleanMoneda;
	private boolean bBooleanImporte;
	private boolean bComision;
	private boolean bFlagComision;
	private String descripcionComision;
	private String glosaComision;
	
	
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
	//private String ubicacionTemporal;
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
	private UploadedFile fileUpload;
	private StreamedContent fileDownload;
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
	private String mesajeValidacionHost = "";
	
	/*private boolean boleanoMensajeInfoGeneral=true;
	private boolean boleanoMensajeApoderdantePoderdante=true;
	private boolean boleanoMensajeOperacionesBancarias=true;
	private boolean boleanoMensajeDocumentos=true;
	*/
	private boolean mostrarRazonSocial = false;
	private String codigoRazonSocial = "0000";
	
	
    public UploadedFile getFileUpload() {  
        return fileUpload;  
    }  
  
    public void setFileUpload(UploadedFile fileUpload) {  
        this.fileUpload = fileUpload;  
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
		
		//logger.debug("Ancho Fieldset Datos Generales: " + ancho_FieldSet);
		//logger.debug("Ancho Fieldset Datos Poderdantes: " + ancho_FieldSet_Poder);
		
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
		logger.info(" TipoParticipe: " +objTiivsPersonaCapturado.getTipPartic());
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
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+" de datos de Clasificacion de personas: ",e);
				}
				
				for (TiivsMultitabla mult: lstTmpMult)
				{
					ComboDto t = new ComboDto();
					t.setKey(mult.getId().getCodElem());
					t.setDescripcion(mult.getValor1());
					lstClasificacionPersona.add(t);
				}
				
				logger.debug("Tamanio lista clasificacion: "+lstClasificacionPersona.size());
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
		}
	}
	
	public String cargarUnicoPDF(String aliasArchivo) {
		logger.debug("== inicia cargarUnicoPDF()====");
		if(fileUpload == null){
			Utilitarios.mensajeInfo("", "No se ha seleccionado ningún archivo");
			return "";
		}
		
		byte fileBytes[] = getFileUpload().getContents();

		File fichTemp = null;
		String sUbicacionTemporal = "";
		String sNombreTemporal = "";
		FileOutputStream canalSalida = null;
		
		try {			
			//Obteniendo ubicación del file server			
			sUbicacionTemporal = Utilitarios
					.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
					+ File.separator + ConstantesVisado.FILES + File.separator;			
			
			logger.debug("[cargarUnicoPDF]-Ubicacion Temp:"+ sUbicacionTemporal);
			
			File fDirectory = new File(sUbicacionTemporal);
			fDirectory.mkdirs();	
			
			String extension = fileUpload.getFileName().substring(getFileUpload().getFileName().lastIndexOf("."));
						
			if(aliasArchivo.equals("")){
				aliasArchivo = "temp";
			} else {
				aliasArchivo = aliasArchivo + "_";
			}
			
			fichTemp = File.createTempFile(aliasArchivo, extension, new File(sUbicacionTemporal));
			
			sNombreTemporal = fichTemp.getName();
									
			logger.debug("[cargarUnicoPDF]-sNombreArchivoTemporal: " + sNombreTemporal);
			
			canalSalida = new FileOutputStream(fichTemp);
			canalSalida.write(fileBytes);
			
			canalSalida.flush();
			
			return sNombreTemporal;

		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"IO Exception al adjuntar:",e);
			String sMensaje = "Se produjo un error al adjuntar fichero";
			Utilitarios.mensajeInfo("", sMensaje);
			return "";
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al adjuntar:",e);
			String sMensaje = "Se produjo un error al adjuntar fichero";
			Utilitarios.mensajeInfo("", sMensaje);
			return "";
		} 
		finally {
			if(fichTemp!=null){
				fichTemp.deleteOnExit();
			}
			if (canalSalida != null) {
				try {
					canalSalida.close();
				} catch (IOException x) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION,x);
				}
			}
		}
	}	

	/**
	 * Metodo encargado de listar los documentos de visado por tipo de solicitud
	 * en base al item seleccionado. Muestra los documentos obligatorios y 
	 * opcionales según sea el caso.
	 * @param e Evento de seleccion del tipo {@link ValueChangeEvent}
	 * **/
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
			
		} catch (Exception ex) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al cargar el listado de documentos por tipo de soliciitud" ,ex);
		}
			
		// 120913
		visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(lstTipoSolicitudDocumentos));
		
	}		

	/**
	 * Metodo encargado de asociar la lista de documentos escaneados/existentes en
	 * la carpeta D:\VisadoPoderes\Escaneados con la lista de documentos mostrados
	 * en el combo del formulario de registro.
	 * **/
	public void actualizarListadoDocumentos() {
		
		addArchivosTemporalesToDelete();
		
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();

		for (TiivsTipoSolicDocumento s : lstTipoSolicitudDocumentos) {
			String nombreCorto = s.getTiivsDocumento().getNombre();
			String formato = s.getTiivsDocumento().getFormato();
			logger.debug("nombreCorto:"+nombreCorto + "   formato:"+formato);
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
		try{
			for(TiivsAnexoSolicitud a : lstAnexoSolicitud){	
				aliasFilesToDelete.add(a.getAliasTemporal());
			}
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al addArchivosTemporalesToDelete():",e);
		}
	}

	public void obtenerPersonaSeleccionada() {
		logger.info(objTiivsPersonaSeleccionado.getCodPer());
		this.objTiivsPersonaResultado = this.objTiivsPersonaSeleccionado;
		bBooleanPopup=false;
	}

	/**
	 * Metodo encargado de realizar la busqueda de clientes/no clientes en diferentes 
	 * fuentes para su registro en la solicitud de visado. Se consulta a la BD Local, 
	 * si no hubieran resultados se consulta a Reniec.
	 * **/
	public void buscarPersona() {
		logger.info("=== buscarPersona():MEJORADO ===");
		logger.info("[BUSQ_PERS]-TipoDoi:"+ objTiivsPersonaBusqueda.getTipDoi());
		logger.info("[BUSQ_PERS]-NroDoi:"+ objTiivsPersonaBusqueda.getNumDoi());
		
		try {
			List<TiivsPersona> lstTiivsPersonaLocal = new ArrayList<TiivsPersona>();
			//Se realiza la busqueda en BD Local
			lstTiivsPersonaLocal = this.buscarPersonaLocal();
			
			List<TiivsPersona> lstTiivsPersonaHost = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstTiivsPersonaReniec = new ArrayList<TiivsPersona>();			
			if (lstTiivsPersonaLocal.size() == 0) {
				logger.debug("1.NO HAY PERSONA LOCAL");				
				//Se realizara la busqueda mediante el servicio PEA (HOST)
				lstTiivsPersonaHost = this.buscarPersonaHost();
				if(lstTiivsPersonaHost.size()==0){
					logger.debug("2.NO HAY PERSONA HOST");				
					//Se realiza la busqueda mediente servicio de RENIEC
					lstTiivsPersonaReniec = this.buscarPersonaReniec();
					if (lstTiivsPersonaReniec.size() == 0) {
						logger.debug("3.NO HAY PERSONA RENIEC");
						objTiivsPersonaResultado = new TiivsPersona();
						this.bBooleanPopup = false;
						 Utilitarios.mensajeInfo("INFO",ConstantesVisado.MENSAJE.NO_RESULTADOS+"para la busqueda.");
					} else if (lstTiivsPersonaReniec.size() == 1) {
						logger.debug("3.b -> EXISTE EN RENIEC 1 PERSONA");	
						objTiivsPersonaResultado = lstTiivsPersonaReniec.get(0);
						this.bBooleanPopup = false;
					} else if (lstTiivsPersonaReniec.size() > 1) {
						logger.debug("3.c EXISTE EN RENIEC MAS DE 1 PERSONA");
						this.bBooleanPopup = true;
						lstTiivsPersonaResultado = lstTiivsPersonaReniec;
					}
					
				}else if(lstTiivsPersonaHost.size()==1){
					logger.debug("2.b -> EXISTE EN HOST 1 PERSONA");	
					objTiivsPersonaResultado = lstTiivsPersonaHost.get(0);
					bBooleanPopup = false;
				}else if(lstTiivsPersonaHost.size()>1){
					logger.debug("2.c -> EXISTE EN HOST MAS DE 1 PERSONA");	
					lstTiivsPersonaResultado = lstTiivsPersonaHost;
					bBooleanPopup = true;
				}
				
			} else if (lstTiivsPersonaLocal.size() == 1) {
				logger.debug("1.b -> EXISTE EN LOCAL 1 PERSONA");	
				logger.info(ConstantesVisado.MENSAJE.SI_RESULTADOS+"lstTiivsPersonaLocal:  "+ lstTiivsPersonaLocal.size());
				this.bBooleanPopup = false;
				objTiivsPersonaResultado = lstTiivsPersonaLocal.get(0);
			} else if (lstTiivsPersonaLocal.size() > 1) {
				logger.debug("1.c -> EXISTE EN LOCAL MAS DE 1 PERSONA");
				this.bBooleanPopup = true;
				lstTiivsPersonaResultado = lstTiivsPersonaLocal;

				personaDataModal = new PersonaDataModal(
						lstTiivsPersonaResultado);
			} else {
				this.bBooleanPopup = true;
			}
		
		} 
		catch (Exception e) {
			Utilitarios.mensajeError("ERROR", e.getMessage());
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"la persona (Local/Host/Reniec): ",e);
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
	
	public List<TiivsPersona> buscarPersonaHost() throws Exception {
		logger.debug("==== inicia buscarPersonaHost() ==== ");
		List<TiivsPersona> lstTiivsPers = new ArrayList<TiivsPersona>();
		try{
			String enableServicio = Utilitarios.getPropiedad("enableServPEA");
			//Se valida la habilitacion de la consulta al servicio PEA - HOST
			if(enableServicio.equalsIgnoreCase("true")){
				logger.debug("= La consulta al servicio PEA-HOST esta habilitado.");
				BResult resultado = null;
				TiivsPersona objPersonaHost = null;
				Persona pers = null;
				
				if (objTiivsPersonaBusqueda.getNumDoi() != null) {
					logger.debug("[HOST]-NumeroDOI:"+ objTiivsPersonaBusqueda.getNumDoi());
					logger.debug("[HOST]-TipoDoi:"+objTiivsPersonaBusqueda.getTipDoi());
					//Leyendo parametros desde el properties
					String busqServDummy = Utilitarios.getPropiedad("busqServDummy");
					String usuConsHost = Utilitarios.getPropiedad("usuConsHost");
					String urlServicio = Utilitarios.getPropiedad("urlServHost");
					logger.debug("[HOST]-URL:"+urlServicio);				
					logger.debug("Realizar la consulta en el servicio DUMMY: "+busqServDummy);
					//Validando si se consultara el servicio DUMMY
					if(busqServDummy.equalsIgnoreCase("true")){
						ObtenerDatosPersonaPEAService hostServiceDummy = new ObtenerDatosPersonaPEAServiceDummy();
						resultado = hostServiceDummy.obtenerDatosGeneralesPEA1(usuConsHost, objTiivsPersonaBusqueda.getTipDoi(), objTiivsPersonaBusqueda.getNumDoi(), urlServicio);
					}else{
						ObtenerDatosPersonaPEAService hostService = new ObtenerDatosPersonaPEAServiceImpl();
						resultado = hostService.obtenerDatosGeneralesPEA1(usuConsHost, objTiivsPersonaBusqueda.getTipDoi(), objTiivsPersonaBusqueda.getNumDoi(), urlServicio);
					}
					//resultado = hostServiceDummy.obtenerDatosGeneralesPEA1(usuConsulta, objTiivsPersonaBusqueda.getTipDoi(), objTiivsPersonaBusqueda.getNumDoi(), urlServicio);
					if(resultado!=null){
						logger.debug("[HOST RPTA]-code:"+resultado.getCode());
						//Se valida la respuesta: EXITO
						if(resultado.getCode()== 0 ){
							if(resultado.getObject()!=null){
								logger.debug("== Se recupera la persona de Host ==");
								pers = (Persona) resultado.getObject();
								objPersonaHost= new TiivsPersona();
								if(pers.getNombre()!=null){
									objPersonaHost.setNombre(pers.getNombre());
									logger.debug("[HOST RPTA]-Nombre:"+objPersonaHost.getNombre());
								}
								if(pers.getApellidoPaterno()!=null){
									objPersonaHost.setApePat(pers.getApellidoPaterno());
									logger.debug("[HOST RPTA]-Apepat:"+objPersonaHost.getApePat());
								}
								if(pers.getApellidoMaterno()!=null){
									objPersonaHost.setApeMat(pers.getApellidoMaterno());
									logger.debug("[HOST RPTA]-Apemat:"+objPersonaHost.getApeMat());
								}
								if(pers.getNumerodocIdentidad()!=null){
									objPersonaHost.setNumDoi(pers.getNumerodocIdentidad());
									logger.debug("[HOST RPTA]-DocIdentidad:"+objPersonaHost.getNumDoi());
								}else{
									objPersonaHost.setNumDoi(objTiivsPersonaBusqueda.getNumDoi());
								}
								if(pers.getTelefono()!=null){
									objPersonaHost.setNumCel(pers.getTelefono());
									logger.debug("[HOST RPTA]-Telefono:"+objPersonaHost.getNumCel());
								}else{
									objPersonaHost.setNumCel("");
								}
								if(pers.getCodCentral()!=null){
									objPersonaHost.setCodCen(pers.getCodCentral());
									logger.debug("[HOST RPTA]-CodCentral:"+objPersonaHost.getCodCen());
								}
								
								objPersonaHost.setTipDoi(objTiivsPersonaBusqueda.getTipDoi());
								objPersonaHost.setCodCen(objTiivsPersonaBusqueda.getCodCen());
								
								lstTiivsPers.add(objPersonaHost);
								
								logger.debug("== despues de poblar el objeto Persona ==");
							}
							
						}else if(resultado.getCode() == 900){
							logger.debug("[900]-No hay resultados para esta consulta en HOST");
						}else{
							logger.debug("[GENERICO]-No hay resultados para esta consulta en HOST");
						}
					}else{
						logger.debug("El resultado HOST es nulo");
					}				
				}
				logger.debug("==== saliendo de buscarPersonaHOST() ==== ");
			}
			
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al buscarPersonaHOST: ",e);
		}
		return lstTiivsPers;
	}
	public List<TiivsPersona> buscarPersonaReniec() throws Exception {
		logger.debug("==== inicia buscarPersonaReniec() ==== ");
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		try{
			String enableServReniec = Utilitarios.getPropiedad("enableServReniec");
			//Se valida la habilitacion de la consulta al servicio RENIEC
			if(enableServReniec.equalsIgnoreCase("true")){
				BResult resultado = null;
				TiivsPersona objPersona = null;
				Persona persona = null;
				if (objTiivsPersonaBusqueda.getNumDoi() != null) {
					logger.info("[RENIEC]-DNI:"+ objTiivsPersonaBusqueda.getNumDoi());
					//Leyendo parametros desde el properties
					String busqServReniecDummy = Utilitarios.getPropiedad("busqServReniecDummy");
					String usuConsReniec = Utilitarios.getPropiedad("usuConsReniec");
					String centroConsulta =Utilitarios.getPropiedad("centroConsulta");
					logger.debug("[RENIEC]-busqServReniecDummy:"+busqServReniecDummy);
					logger.debug("[RENIEC]-usuConsulta:"+usuConsReniec);
					logger.debug("[RENIEC]-centroConsulta:"+centroConsulta);
					
					if(busqServReniecDummy.equalsIgnoreCase("true")){
						ObtenerPersonaReniecDUMMY reniecServiceDummy = new ObtenerPersonaReniecDUMMY();
						resultado = reniecServiceDummy.devolverPersonaReniecDNI(usuConsReniec, centroConsulta,objTiivsPersonaBusqueda.getNumDoi());
					}else{
						ObtenerPersonaReniecService reniecService = new ObtenerPersonaReniecServiceImpl();
						resultado = reniecService.devolverPersonaReniecDNI(usuConsReniec, centroConsulta,objTiivsPersonaBusqueda.getNumDoi());
					}
					logger.debug("[RENIEC]-resultado: "+resultado.getCode());
					
					if (resultado.getCode() == 0) {
						if(resultado.getObject()!=null){
							persona = (Persona) resultado.getObject();
							logger.info("[RENIEC -RPTA]-Nombre Completo:" + persona.getNombreCompleto());
							logger.info("[RENIEC -RPTA]-NumeroDocIdentidad:" + persona.getNumerodocIdentidad());
									
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
				}
				logger.debug("==== saliendo de buscarPersonaReniec() ==== ");
			}
			
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al buscaar en Reniec: ",e);
		}
		return lstTiivsPersona;
		
	}
	
	/**
	 * Se encarga de realizar busqueda de una persona en la BD Local (VISPOD).
	 * @param getTipDoi Tipo de Documento
	 * @param getNumDoi Numero de Documento
	 * @return lstTiivsPersona Representa una lista de <code>TiivsPersona</code>
	 * **/
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
		logger.info("lstTiivsAgrupacionPersonas: "+lstTiivsAgrupacionPersonas.size());
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
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al buscar lstTiivsPersona:",e);
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
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al buscar Personas en validarbuscarPersonaLocal():",e);
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
		logger.info("[AgregSolAgrupac]-iNumGrupo : " + iNumGrupo);

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
		logger.info("[AgregSolAgrupac]-lstSolicitudArupacion tamanhio: " + lstSolicitudArupacion.size());
		return lstSolicitudArupacion;
	}

	public void agregarPersona() 
	{
		logger.info("========= agregarPersona():INICIO ======");
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
				logger.info("[AgregPersona]-Tamanio lista lstTiivsPersona:" +lstTiivsPersona.size());
				logger.info("[AgregPersona]-Tamanio lista PersonaResultado " +lstTiivsPersonaResultado.size());
				personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
				logger.info("[AgregPersona]-Tamanio personaDataModal: " +personaDataModal.getRowCount());
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
		logger.info("==== actualizarPersona =====");
		TiivsPersona personaRetorno = new TiivsPersona();
		
		GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit
		.getApplicationContext().getBean("genericoDao");
		
		try {
			persona.setUsuarioRegistro(this.usuario.getUID());
			persona.setFechaRegistro(new Timestamp(new Date().getTime()));
			personaRetorno=servicePers.insertarMerge(persona);
			
			logger.info("[actPersona]-CodPersona a grabar: "+personaRetorno.getCodPer());
						
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
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al actualizarPersona(): ",e);
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
		logger.info("====== validarRegistroDuplicado ======");
		logger.debug("[validRegDuplic]-NumDoi:"+objTiivsPersonaResultado.getNumDoi());
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
		logger.info("[validRegDuplic]-bResult:"+bResult);
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
		logger.info("===== validarPersona ======= "
				+ objTiivsPersonaResultado.getTipPartic());
		boolean bResult = true;
		String sMensaje = "";
		logger.info("[validaPersona]-Clasificacion:"+ objTiivsPersonaResultado.getClasifPer());
		
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
				logger.debug("[validaPersona]-CodigoPers:" +x.getCodPer() + "  TipoDoi:"+x.getTipDoi());
				logger.debug("[validaPersona]-objTiivsPersonaResultado.getCodPer():" +objTiivsPersonaResultado.getCodPer());
				
				
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
		
		logger.info("[validaPersona]-bResult:" +bResult);

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
		String mensaje="Por lo menos ingrese un Poderdante o Apoderado";
		if(lstTiivsPersona.size()==0){
			logger.info("validarAgregarAgrupacion:"+mensaje);
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
				logger.debug(" lstTiivsAgrupacionPersonas " +lstTiivsAgrupacionPersonas.size());
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
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en validarSiAgrupacionEstaRevocada(): ",e);
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
				//SE AGREGO IF DE HEREDEROS
				if (n.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)) {
					lstApoderdantes.add(n);
					logger.info(" heredero : " + n.getCodPer());
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
					}else if(d.getTipPartic().trim().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)){
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
			logger.debug("Usuario Sesion:" + usuario.getNombre());
			logger.debug("CodigoOficina: " + usuario.getBancoOficina().getCodigo().trim());
			logger.debug("DescripcionOficina: "+ usuario.getBancoOficina().getDescripcion().trim());
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
		obtenerTipoRegistro();
		obtenerEtiquetasTipoRegistro();
		obtenerPagoComision();

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
				
				// 120913
				visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(lstTipoSolicitudDocumentos));
				
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
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al establecerTipoSolicitud(): ",e);
		}
	}

	/**
	 * Metodo encargado de borrar un archivo en la seccion documentos del
	 * formulario de registro de solicitud de visado, con el botón "Eliminar".
	 * */
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
		logger.info("====== actualizarDocumentosXTipoSolicitud =====");
		
		//logger.info("documentos Leidos: " + documentosLeidos);		
		logger.info("[Applet-Actualizar]-Documentos LEIDOS: " + visadoDocumentosMB.getDocumentosLeidos());
		logger.info("[Applet-Actualizar]-Documentos CARGADOS: " + visadoDocumentosMB.getDocumentosCargados());
		String []aDocumentosLeidos = visadoDocumentosMB.getDocumentosLeidos().split(",");
		String []aDocumentosCargados = visadoDocumentosMB.getDocumentosCargados().split(",");
		String nombreDoc = "";
		
		//Actualiza lista de documentos		
		if(aDocumentosLeidos.length == aDocumentosCargados.length){
			
			//for(String documento : aDocumentosLeidos){
			for(int i=0;i<aDocumentosLeidos.length;i++){
				String documentoLeido = aDocumentosLeidos[i];
				String documentoCargado = aDocumentosCargados[i];
				logger.info("[Applet-Actualizar]-\tBuscando coincidencias para:" + documentoLeido);
				if(!documentoLeido.trim().isEmpty()){
					nombreDoc = documentoLeido.substring(0, documentoLeido.lastIndexOf("."));			
					
					//Agregar a listado de documentos tabla documentos
					for(DocumentoTipoSolicitudDTO doc : lstdocumentos){
						//logger.info("nombreDoc = doc.getItem():" + nombreDoc + "=" + doc.getNombreCorto());
						if(doc.getNombreCorto().equals(nombreDoc)){
							logger.debug("\t[CONCIDEN]-nombreDoc = doc.getItem():" + nombreDoc + "  =" + doc.getNombreCorto());
							doc.setAlias(documentoLeido);
							doc.setAliasTemporal(Utilitarios.modificarExtension(documentoCargado));
							logger.info("\t[Applet-Actualizar]-Se actualiza nombre documento:" + doc.getAlias());
							
							//Agregar a lista de anexos de la solicitud
							TiivsAnexoSolicitud objAnexo = new TiivsAnexoSolicitud();
							objAnexo.setId(new TiivsAnexoSolicitudId(null, doc.getItem()));
							objAnexo.setAliasArchivo(doc.getAlias());
							objAnexo.setAliasTemporal(doc.getAliasTemporal());
							
							logger.debug("==> doc.getAlias():"+doc.getAlias() + "\tdoc.getAliasTemporal():"+doc.getAliasTemporal());
							
							
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
			
			// 120913
			visadoDocumentosMB.setDocumentosLeer(VisadoDocumentosMB.armaTramaDocumentosALeer(lstTipoSolicitudDocumentos));
			
		}
		
		establecerTipoSolicitud();
		
		logger.info("(Tabla) lstdocumentos tamaño:" + lstdocumentos.size());
		logger.info("(Anexos)lstAnexoSolicitud tamaño:" + lstAnexoSolicitud.size());
		logger.info("(Combo) lstTipoSolicitudDocumentos tamaño:" + lstTipoSolicitudDocumentos.size());
        logger.info("====== SALIENDO de actualizarDocumentosXTipoSolicitud =====");
        
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
					//logger.debug("x.getMoneda().trim() " +x.getId().getMoneda().trim());
					//logger.debug("objSolicBancaria.getMoneda().trim()) " +objSolicBancaria.getId().getMoneda().trim());
					
					if(x.getId().getMoneda().trim().equals(objSolicBancaria.getId().getMoneda().trim())) {
						conunt++;
						//logger.info("conunt "+conunt);
						//logger.debug("x.getMoneda() " +x.getId().getMoneda());
						//logger.debug("objSolicBancaria.getMoneda() " + objSolicBancaria.getId().getMoneda());
						
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
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener OperacionBancaria: ", e);
		}		
		return false;
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

	/**
	 * Metodo que se encarga de agregar una operacion bancaria en el 
	 * formulario de registro de solicitud de visado.
	 * */
	public void agregarOperacionBancaria() {
		logger.info(" ===== agregarOperacionBancaria() ===== ");
		
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

	/**
	 * Se encarga de limpiar las operaciones bancarias
	 * **/
	public void limpiarListaSolicitudesBancarias() {
		logger.info("==== limpiarListaSolicitudesBancarias() ====");
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
			//SOLO SOLES
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				valorFinal=valorFinal+x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+((double) Math.round(valorFinal*100)/100));
        	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
		}
		if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) {
			//SOLO DOLARES
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				valorFinal=valorFinal+x.getImporte();
				this.solicitudRegistrarT.setImporte(valorFinal);
			}
			this.solicitudRegistrarT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+((double) Math.round(valorFinal*100)/100));
        	this.solicitudRegistrarT.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
		}
		if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) {
			//SOLO EUROS
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

	/**
	 * Metodo encargado de calcular la comision, primero se obtiene el 
	 * tipo de comision y después el monto.
	 * **/
	public void llamarComision() {
		logger.info("=== llamarComision():INICIO ===");
		this.solicitudRegistrarT.setTipoComision(objRegistroUtilesMB.obtenerTipoComision(this.solicitudRegistrarT));
		this.solicitudRegistrarT.setComision(objRegistroUtilesMB.obtenerComision(solicitudRegistrarT.getTipoComision()));
		logger.info("[llamarComision]-TipoComision: " + this.solicitudRegistrarT.getTipoComision());
		logger.info("[llamarComision]-Comision : " + this.solicitudRegistrarT.getComision());
		logger.info("=== llamarComision():FIN ===");
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
		
		logger.info(" =========== registrarSolicitud [v2] ===========");
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
        GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	    
		try {
			logger.info("[REGISTR_SOLIC]-Moneda: " +this.solicitudRegistrarT.getMoneda());
			
			this.solicitudRegistrarT.setFecha(new Date());
			this.solicitudRegistrarT.setEstado(this.solicitudRegistrarT.getEstado().trim());
			this.solicitudRegistrarT.setFechaEstado(new Timestamp(new Date().getTime()));

			logger.info("[REGISTR_SOLIC]-Usuario:" + usuario.getUID());
			this.solicitudRegistrarT.setRegUsuario(usuario.getUID());
			this.solicitudRegistrarT.setNomUsuario(usuario.getNombre());
			
			if (flagMostrarACOficina)
			{
				if (oficina!=null)
				{
					logger.info("[REGISTR_SOLIC]-codOficina: "+ oficina.getCodOfi());
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
				logger.info("[REGISTR_SOLIC]-codOficina: "+ this.solicitudRegistrarT.getTiivsOficina1().getCodOfi());
				for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
					if (tiivsOficina1.getCodOfi().equals(this.solicitudRegistrarT.getTiivsOficina1().getCodOfi())) {
						this.solicitudRegistrarT.setTiivsOficina1(tiivsOficina1);
						break;
					} 
				}
			}
			
			if(isbFlagComision()){
				this.solicitudRegistrarT.setExoneraComision(ConstantesVisado.VALOR2_ESTADO_ACTIVO);
			}else{
				this.solicitudRegistrarT.setExoneraComision(ConstantesVisado.VALOR2_ESTADO_INACTIVO);
			}
			logger.debug("[REGISTR_SOLIC]-flagExoneraComision: "+this.solicitudRegistrarT.getExoneraComision());
			
			this.limpiarAgrupacionesVacias();

			if(solicitudRegistrarT.getTiivsSolicitudAgrupacions()!=null){
				logger.info("[REGISTR_SOLIC]-AgrupacionPersona-tamanhio:"+ solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());	
			}
			
			boolean esValido = false;
			if(!this.sEstadoSolicitud.equals("BORRADOR")){ 	//Validacion para envio de solicitud a SSJJ
				esValido = this.validarEnvioSolicitud();
			} else { 	//Validacion para registro de solicitud (Borrador)
				esValido = this.validarRegistroSolicitud();
			}
			
			logger.debug("[REGISTR_SOLIC]-esValido:"+esValido);
			
			//if (this.validarRegistroSolicitud()) 
			if (esValido)
			{
				/*** CAMBIAR DE ESTADO DE SOLICITUD A ENVIADA ***/
				String mensajeValidacion = "";
				if (!this.sEstadoSolicitud.equals("BORRADOR")) {
					/*** REALIZAR VALIDACION DE COBRO DE COMISIONES ***/
					if(validarCobroComisiones()){
						logger.info("[REGISTR_SOLIC]-Solicitud con validacion de cobro de comisiones restrictiva");
						return;
					}
					this.enviarSolicitudSSJJ();
				}
				
				SolicitudDao<TiivsPersona, Object> servicePK = (SolicitudDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
				String sCodigoSol = servicePK.obtenerPKNuevaSolicitud();
				logger.debug("[REGISTR_SOLIC]-sCodigoSolicitud:" + sCodigoSol);
				this.solicitudRegistrarT.setCodSoli(sCodigoSol);
		
				//Registrando las Agrupaciones
				for (TiivsSolicitudAgrupacion x : this.solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
					  //x.setTiivsSolicitud(this.solicitudRegistrarT);
					  x.getId().setCodSoli(sCodigoSol);
					  for (TiivsAgrupacionPersona a : x.getTiivsAgrupacionPersonas()) {
						a.setCodSoli(x.getId().getCodSoli());
						a.setNumGrupo(x.getId().getNumGrupo());
						a.setIdAgrupacion(null);//Para que la BD asigne IdAgrupacion
					}
				}
				//Registrando el Historial de la Solicitud
				TiivsSolicitud objResultado = service.insertar(this.solicitudRegistrarT); //INSERTA LA SOLICITUD
				TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
				  objHistorial.setId(new TiivsHistSolicitudId(this.solicitudRegistrarT.getCodSoli(),1+""));
				  objHistorial.setEstado(this.solicitudRegistrarT.getEstado());
				  objHistorial.setNomUsuario(usuario.getNombre());
				  objHistorial.setObs(this.solicitudRegistrarT.getObs());
				  objHistorial.setFecha(new Timestamp(new Date().getTime()));
				  objHistorial.setRegUsuario(usuario.getUID());
				  
				  serviceHistorialSolicitud.insertar(objHistorial);
				  
				//Registrando la lista de Documentos (Anexos)
				if(this.lstAnexoSolicitud!=null){
					logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+" de Anexos es:"+this.lstAnexoSolicitud.size());
				}
				for (TiivsAnexoSolicitud n : this.lstAnexoSolicitud) {
					  n.getId().setCodSoli(solicitudRegistrarT.getCodSoli());
					  logger.debug("[REGISTR_SOLIC][Anexo]-Id:"+n.getId().getCodDoc() + "  Alias:"+n.getAliasArchivo()+"  AliasTemp:"+n.getAliasTemporal() + "  Hora:"+new Date());
					  serviceAnexos.insertar(n);
				}
				 
				//Registrando las Operaciones Bancarias
				for (TiivsSolicitudOperban a : this.lstSolicBancarias) {
					logger.info("[REGISTR_SOLIC]-OperacionBancaria-codOper:"+ a.getId().getCodOperBan() + "   nombreOper:"+a.getTiivsOperacionBancaria().getDesOperBan());
					a.getId().setCodSoli(this.solicitudRegistrarT.getCodSoli());
					serviceSoli.insertar(a);
				}
				
				 //Carga ficheros al File Server
				boolean bRet = cargarArchivosFileServer();
				logger.info("[REGISTR_SOLIC]-Resultado de carga de archivos al FileServer:" + bRet);
				//Elimina archivos temporales
				eliminarArchivosTemporales();
				
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
					this.redirect = consultarSolicitudMB.redirectDetalleSolicitud(objResultado.getCodSoli());					
				} else {
					logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar la solicitud.");
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
				}

				logger.info("[REGISTR_SOLIC]-objResultado.getCodSoli(); "+ objResultado.getCodSoli());
				logger.info("[REGISTR_SOLIC]-objResultado.getTiivsSolicitudAgrupacions() "+ objResultado.getTiivsSolicitudAgrupacions().size());
				logger.info("[REGISTR_SOLIC]-this.solicitudRegistrarT.importe : " +this.solicitudRegistrarT.getImporte());					
				
				if (actualizarBandeja)
				{
					this.seguimientoMB.busquedaSolicitudxCodigo(objResultado.getCodSoli());
				}
				
			}
		} catch (Exception e) {
			this.redirect="";
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al registrar la solicitud: ",e);
			Utilitarios.mensajeError("ERROR", "Ha ocurrido un error al grabar la Solicitud");

		}
		logger.info("Redirec:" + this.redirect);
//		return this.redirect;

	}
	
	/** Metodo encargado de leer los valores parametrizados en la multitabla (T18), 
	 * para hacer o no la validacion de Comision.
	 * @return restrictivo Indicador para realizar la validacion booleano: true/false
	 * **/
	private boolean validarCobroComisiones(){
		boolean restrictivo = false;
		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		logger.debug("ExoneraFlagComision():"+isbFlagComision());
		//Si no esta exonerado a comision
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
		
	public String redireccionar(){
		Utilitarios.mensajeInfo("INFO", mesajeConfirmacion);
		return redirect;
	}		

	/**
	 * Metodo que se encarga del envío de la solicitud de visado a  SSJJ (Servicios 
	 * Juridicos). Aqui es donse se obtiene el estudio de menor carga y se setea el
	 * estado ENVIADO.
	 * */
	public void enviarSolicitudSSJJ() {
		Timestamp time = new Timestamp(objRegistroUtilesMB.obtenerFechaRespuesta().getTime());
		logger.info("\t[enviarSolicitudSSJJ]-FechaRespuesta: " + time);
		String sCodigoEstudio = objRegistroUtilesMB.obtenerEstudioMenorCarga();
		logger.info("\t[enviarSolicitudSSJJ]-CodEstudioMenorCarga:+  " + sCodigoEstudio);
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
	
	/**
	 * Metodo que se encarga de la validacion del Nro de Voucher ingresado en el 
	 * formulario de registro de solicitud contra la informacion de BD Local 
	 * @return booleano Indica si ya existe el NroVoucher en BD. true/false
	* */	
	@SuppressWarnings({ "unchecked", "null" })
	public boolean validarNroVoucher() throws Exception {
		boolean booleano = true;
		if (!this.sEstadoSolicitud.equals("BORRADOR")) {
			String mensaje = "Ingrese un Nro de Vourcher no registrado ";
			Busqueda filtroNroVoucher = Busqueda.forClass(TiivsSolicitud.class);
			//Se consulta las solicitudes con estado: 0001
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
								logger.debug("\tvalidarNroVoucher:Ya existe una solicitud con el NroVoucher: "+this.solicitudRegistrarT.getNroVoucher());
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
	
	/**
	 * Metodo que se encarga de la validacion de la solicitud de visado, entre
	 * las validaciones se consideran que los campos no sean nulos, obligatoriedad
	 * de algunas secciones, etc
	 * @return true/false Respuesta de validacion
	 * */
	private boolean validarEnvioSolicitud() throws Exception {		
		logger.debug("=== validarEnvioSolicitud() ===");  
		boolean retorno = true;
		String mensaje = "";
		
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
		logger.info("Valida[EnvioSolicitud]-CodOficina:"+solicitudRegistrarT.getTiivsOficina1().getCodOfi());
		
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
					logger.info("Valida[EnvioSolicitud]-NroVoucher:"+solicitudRegistrarT.getNroVoucher());
					retorno =this.validarNroVoucher();
				 }
		//Validacion de Agrupaciones: Apoderado / Poderante		 
		if (solicitudRegistrarT.getTiivsSolicitudAgrupacions().size() == 0) {
			mensaje = "Ingrese la sección Apoderado y Poderdante";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}else{
			Set<TiivsAgrupacionPersona> lstAgrupacionPersona=null;
			int conuntNumAgru=0;
			logger.info("Valida[EnvioSolicitud]-[Agrupaciones]size:"+solicitudRegistrarT.getTiivsSolicitudAgrupacions().size());
			
			for (TiivsSolicitudAgrupacion a : solicitudRegistrarT.getTiivsSolicitudAgrupacions()) {
				conuntNumAgru=a.getId().getNumGrupo();
				logger.info("\t[Agrupaciones]-NroAgrup: " +conuntNumAgru);
				lstAgrupacionPersona=a.getTiivsAgrupacionPersonas();
				logger.info("\t[Agrupaciones]-ListaAgrupPers: " +lstAgrupacionPersona.size());
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
				logger.info("\t[Agrupaciones]-CantPoderdante:" +contPoderdante);
				logger.info("\t[Agrupaciones]-CantApoderado:" +contApoderado);
			}			
		}
		//Validacion de Tipo de Solicitud
		if (solicitudRegistrarT.getTiivsTipoSolicitud() == (null)) {
			mensaje = "Seleccione el Tipo de Solicitud ";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		//Validacion de documentos adjuntos y obligatorios
		if (this.lstAnexoSolicitud.size() == 0) {
			mensaje = "Ingrese los documentos Obligatorios";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if(lstTipoSolicitudDocumentos!=null){
			for(TiivsTipoSolicDocumento docRequerido :  lstTipoSolicitudDocumentos){
				if(docRequerido.getObligatorio().equals("1")){
					mensaje = "Ingrese los documentos Obligatorios";
					retorno = false;
					Utilitarios.mensajeInfo("INFO", mensaje);
					break;
				}
			}
		}
		
		//Validacion de Operacion Bancaria
		if (this.lstSolicBancarias.size() == 0) {
			mensaje = "Ingrese al menos una Operación Bancaria";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		logger.debug("Valida[EnvioSolicitud]-retorno:"+retorno);
		return retorno;
	}

	public String prepararURLEscaneo() {			
		logger.info("== prepararURLEscaneo ==");		
		String sCadena = "";		
		try{				
			pdfViewerMB = new PDFViewerMB();	
			sCadena = pdfViewerMB.prepararURLEscaneo(usuario.getUID());			
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener parametros " +
					"de configuracion para el APINAE: ",e);
		}
		return sCadena;
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
				
		logger.info("[CARGAR-FILESERVER]-Ubicacion temporal: "+ sUbicacionTemporal);
		logger.info("[CARGAR-FILESERVER]-Ubicacion final: "+ ubicacionFinal);
		if(lstAnexoSolicitud!=null){
			logger.debug("[CARGAR-FILESERVER]-lstAnexoSolicitud-size:"+lstAnexoSolicitud.size());
		}
		for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){		
			logger.debug("======== Mover archivo ========");
			File srcFile = new File(sUbicacionTemporal + anexo.getAliasTemporal());	
			logger.debug("srcFile:"+sUbicacionTemporal + anexo.getAliasTemporal());
			File destFile = new File(ubicacionFinal + anexo.getId().getCodSoli() + "_" + anexo.getAliasArchivo());
			logger.debug("destFile:"+ubicacionFinal + anexo.getId().getCodSoli() + "_" + anexo.getAliasArchivo());
			try {
				FileUtils.copyFile(srcFile, destFile);
				logger.debug("Despues de mover el archivo ...");
			} catch (IOException e) {
				logger.error("Error al mover el archivo al fileServer", e);
			} catch (Exception ex) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al mover archivo al fileServer:" , ex);
			}
			if(!destFile.isFile() && destFile.length()>0){
				exito = false;
			}
		}
		logger.debug("[CARGAR-FILESERVER]-exito:"+exito);
		return exito;		
	}
	
	public boolean cargarArchivosFileServerNuevo(){			
		logger.info("========= cargarArchivosFileServer():NUEVO ========");		
		boolean exito = true;
		TiivsParametros parametro = obtenerParametro();
		
//		String ubicacionFinal = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)  + File.separator;
		String ubicacionFinal = null;
		String sUbicacionTemporal = null;
		
		if(lstAnexoSolicitud!=null){
			logger.debug("NUEVO-[CARGAR-FILESERVER]-lstAnexoSolicitud-size:"+lstAnexoSolicitud.size());
		}
		if(parametro!=null){
			try {
				ubicacionFinal = parametro.getCarpetaRemota(); //+ File.separator;
				sUbicacionTemporal = ubicacionFinal + ConstantesVisado.FILES + File.separator;
				
				logger.info("NUEVO-[CARGAR-FILESERVER]-Ubicacion final "+ ubicacionFinal);
				logger.info("NUEVO-[CARGAR-FILESERVER]-Ubicacion temporal "+ sUbicacionTemporal);		
				
				// Instanciamos la conexión por FTP con el file server
				FTPClient cliente=new FTPClient();
				try {
					logger.debug("parametro.getServer(): "+parametro.getServer());
					logger.debug("parametro.getLoginServer(): "+parametro.getLoginServer());
					logger.debug("parametro.getPassServer(): "+parametro.getPassServer());
					cliente.connect(parametro.getServer());
				    boolean login=cliente.login(parametro.getLoginServer(),parametro.getPassServer());
				    logger.debug("login:"+login);
				    if(login){
				    	logger.info("+++ Conexión file server exitosa ");
				    	cliente.changeWorkingDirectory(parametro.getCarpetaRemota()); //nos movemos dentro del arbol de directorios
				    	for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){
				    		FileInputStream fis = new FileInputStream(new File(sUbicacionTemporal + anexo.getAliasTemporal())); 
				    		cliente.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
				    		boolean res = cliente.storeFile(anexo.getId().getCodSoli() + "_" + anexo.getAliasArchivo(), fis );
				    		logger.debug("NUEVO -> "+res);
				    		if(!res){
				    			logger.error("NUEVO +++ ERROR moviendo archivo: " + anexo.getId().getCodSoli() + "_" + anexo.getAliasArchivo());
				    			return false;
				    		}
				    		fis.close();
						}
				    }
				} catch (Exception e) {
					exito = false;
					logger.error("+++ERROR al instanciar FTP: ", e);
				} finally{
					cliente.logout();
			        cliente.disconnect();
				}
			} catch (Exception e) {
				exito = false;
				logger.error("+++ERROR cargando archivos por FTP: " + e.getMessage());
			}	
		}
		logger.debug("exito:"+exito);
		return exito;		
	}
	
	private TiivsParametros obtenerParametro(){
		GenericDao<TiivsParametros, Object> multiDAO = (GenericDao<TiivsParametros, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsParametros.class);
		List<TiivsParametros> parametros = new ArrayList<TiivsParametros>();
		try {
			parametros = multiDAO.buscarDinamico(filtro);
			if(parametros.size()>0){
				return parametros.get(0);
			}
		} catch (Exception e) {
			logger.error("+++ERROR obteniendo parametros: " , e);
		}	
		return null;
	}
	
	/**
	 * Metodo que se encarga de cargar los archivos .PDF hacia el servidor FTP
	 * @return boolean true/false Indica el exito de la operacion
	 * */
	public boolean cargarArchivosFTP(){					
		logger.info("========= cargarArchivosFileServer() ========");		
		boolean exito = true;
						
		String sUbicacionTemporal = Utilitarios
				.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
				+ File.separator + ConstantesVisado.FILES + File.separator;
		
		logger.info("[CARGAR-FTP]-Ubicacion temporal "+ sUbicacionTemporal);
		if(lstAnexoSolicitud!=null){
			logger.debug("[CARGAR-FTP]-lstAnexoSolicitud-size:"+lstAnexoSolicitud.size());
		}
		for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){			
			String ruta = pdfViewerMB.cargarUnicoPDF(anexo.getId().getCodSoli() + "_" + anexo.getAliasArchivo(),sUbicacionTemporal + anexo.getAliasTemporal());					
			logger.debug("** [CARGAR-FTP]-ruta"+ruta);
			if (ruta.compareTo("") != 0) {
				logger.debug("[CARGAR-FTP]-Se subio el archivo: [" + anexo.getAliasTemporal() +"] al servidor.");
				exito = exito && true;
			} else {
				logger.debug("[CARGAR-FTP]-No se pudo subir el archivo: [" + anexo.getAliasTemporal() +"] al servidor.");
				exito = exito && false;
			}
		}
		return exito;
	}
				
	/**
	 * Metodo que se encargar de limpiar los documentos temporales que ya no
	 * son necesarios almacenar en el directorio de visado.
	 * **/
	public void eliminarArchivosTemporales() {	
		
		if(aliasFilesToDelete!=null){
			logger.info("==== eliminarArchivosTemporales() ====");
			logger.info("Archivos a eliminar:" + aliasFilesToDelete.size()); 
		}	
		File fileToDelete = null;
		
		String sUbicacionTemporal = Utilitarios
				.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
				+ File.separator + ConstantesVisado.FILES + File.separator;
		
		for(String sfile : aliasFilesToDelete){
			logger.debug("Borrar archivo: " + sUbicacionTemporal + sfile);
			fileToDelete = new File(sUbicacionTemporal + sfile);
			if(fileToDelete.delete()){
				logger.debug("Se ha BORRADO el archivo temporal :" + sfile);
			} else {
				logger.debug("No se ha BORRADO el archivo temporal :" + sfile);
			}
		}
		
		fileToDelete = null;
		aliasFilesToDelete = new ArrayList<String>();		
	}		
	
	public void cambiarRazonSocial(ValueChangeEvent e){		
		logger.info("==== cambiarRazonSocial()=====");
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
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+ "de multitablas: " , e);
		}
		
		
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
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
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

	private void obtenCodRazonSocial() {
		TiposDoiService tiposDoiService = new TiposDoiService(); 			
		codigoRazonSocial = tiposDoiService.obtenerCodPersonaJuridica();
	}
	
	//SE AGREGA METODO PARA OBTENER EL TIPO DE REGISTRO POR BD
	private void obtenerTipoRegistro(){
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultitabla.add(Restrictions.eq("id.codMult",ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA));
		List<TiivsMultitabla> listaMultiTabla = new ArrayList<TiivsMultitabla>();
		Integer contador = 0;
		try {
			listaMultiTabla = multiDAO.buscarDinamico(filtroMultitabla);
			tipoRegistro = "";
			if(listaMultiTabla.size()>0){
				for(TiivsMultitabla multitabla:listaMultiTabla){
					contador++;
					if(contador.compareTo(listaMultiTabla.size())==0){
						tipoRegistro += multitabla.getValor1();	
					}else{
						tipoRegistro += multitabla.getValor1() + " / ";
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+ "de multitablas: " , e);
		}
	}
	
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
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+ "de multitablas: " , e);
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
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+ "de multitablas: " , e);
		}
	}
	
	//METODO PARA OBTENER ETIQUETAS DE TIPO DE REGISTRO DESDE BD Y MOSTRARLO EN GRILLA
	private void obtenerEtiquetasTipoRegistro(){
		obtenerPonderdante();
		obtenerAponderdante();
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

	public String getDescripcionComision() {
		return descripcionComision;
	}

	public void setDescripcionComision(String descripcionComision) {
		this.descripcionComision = descripcionComision;
	}

	public String getGlosaComision() {
		return glosaComision;
	}

	public void setGlosaComision(String glosaComision) {
		this.glosaComision = glosaComision;
	}

	public boolean isbFlagComision() {
		return bFlagComision;
	}

	public void setbFlagComision(boolean bFlagComision) {
		this.bFlagComision = bFlagComision;
	}

	public StreamedContent getFileDownload() {
		return fileDownload;
	}

	public void setFileDownload(StreamedContent fileDownload) {
		this.fileDownload = fileDownload;
	}
	
}
package com.hildebrando.visado.mb;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.UploadedFile;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.converter.PersonaDataModal;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ApoderadoDTO;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.OperacionBancariaDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersonaId;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsDocumento;
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

@ManagedBean(name = "solEdicionMB")
@SessionScoped
public class SolicitudEdicionMB 
{
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	@ManagedProperty(value = "#{registroUtilesMB}")
	RegistroUtilesMB objRegistroUtilesMB;
	
	public static Logger logger = Logger.getLogger(SolicitudEdicionMB.class);	
	
	private TiivsSolicitud solicitudEdicionT;
	private List<TiivsAnexoSolicitud> lstAnexosSolicitudes;
	private List<TiivsDocumento> lstTiivsDocumentos;
	private List<SeguimientoDTO> lstSeguimientoDTO;
	private TiivsSolicitudOperban objSolicBancaria;
	private List<TiivsSolicitudOperban> lstSolicBancarias;
	private Solicitud solicitudEdicion;
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
	private DocumentoTipoSolicitudDTO selectedDocumentoDTO;
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
	Map<Integer, TiivsSolicitudOperban> mapSolicitudes;
	String cadenaEscanerFinal = "";
	private UploadedFile file;
	private String sMonedaImporteGlobal; 
	
	
	public SolicitudEdicionMB()
	{
		solicitudEdicion = new Solicitud();
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
		
		aliasFilesToDelete = new ArrayList<String>();
		
		selectedTipoDocumento = new TiivsTipoSolicDocumento();
		mapSolicitudes=new HashMap<Integer, TiivsSolicitudOperban>();
		
		combosMB= new CombosMB();
		combosMB.cargarMultitabla();
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_MONEDA);
		
		cargarDocumentos();
		obtenerSolicitud();
	}
	
	public boolean validarRegistroSolicitud() throws Exception 
	{
		boolean retorno = true;
		String mensaje = "";
		
		if (solicitudEdicionT.getTiivsSolicitudAgrupacions().size() == 0) {
			mensaje = "Ingrese la sección Apoderado y Poderdante";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if (solicitudEdicionT.getTiivsTipoSolicitud() == (null)) {
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
		if (!this.sEstadoSolicitud.equals("BORRADOR")) {
		this.validarNroVoucher();
		}
		return retorno;
	}
	
	public void validarNroVoucher() throws Exception
	{	
		String mensaje="Ingrese un Nro de Vourcher no registrado ";
		Busqueda filtroNroVoucher = Busqueda.forClass(TiivsSolicitud.class);
		GenericDao<TiivsSolicitud, String> serviceNroVoucher=(GenericDao<TiivsSolicitud, String>) SpringInit.getApplicationContext().getBean("genericoDao");
		List<TiivsSolicitud> lstSolicitud =new ArrayList<TiivsSolicitud>();
		lstSolicitud=serviceNroVoucher.buscarDinamico(filtroNroVoucher);
		if(lstSolicitud!=null)
		{
			for (TiivsSolicitud a : lstSolicitud) 
			{
				if(a!=null||!a.equals(""))
				{
					if(a.getNroVoucher()!=(null))
					{
						if(a.getNroVoucher().equals(this.solicitudEdicionT.getNroVoucher()))
						{
							Utilitarios.mensajeInfo("INFO", mensaje);
							break;
						}
					}
				}
			}
		}
	}
	
	public boolean cargarArchivosFTP(){
		
		logger.info("************cargarArchivosFTP()*¨**************");
		
		boolean exito = true;
		String sUbicacionTemporal = getUbicacionTemporal();									
		logger.info("ubicacion temporal "+ sUbicacionTemporal);

		for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){
			
			String ruta = pdfViewerMB.cargarUnicoPDF(anexo.getAliasArchivo(),sUbicacionTemporal + anexo.getAliasTemporal());					
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
	
	public void actualizarBorradorSolicitud() {
		String mensaje = "";
		logger.info("*********************** registrarSolicitud ************************");
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
        GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	    TiivsPersona objPersonaRetorno=new TiivsPersona();
	    TiivsAgrupacionPersonaId objAgruId=new TiivsAgrupacionPersonaId();
		try {
			logger.info("this.solicitudEdicionT.importe : " +this.solicitudEdicionT.getImporte());
			this.solicitudEdicionT.setFecha(new Date());
			this.solicitudEdicionT.setEstado(this.solicitudEdicionT.getEstado().trim());

			logger.info("usuario.getUID() " + usuario.getUID());
			this.solicitudEdicionT.setRegUsuario(usuario.getUID());
			this.solicitudEdicionT.setNomUsuario(usuario.getNombre());
			logger.info("tiivsOficina1.codOfi ::::::: "+ this.solicitudEdicionT.getTiivsOficina1().getCodOfi());
			for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
				if (tiivsOficina1.getCodOfi().equals(this.solicitudEdicionT.getTiivsOficina1().getCodOfi())) {
					this.solicitudEdicionT.setTiivsOficina1(tiivsOficina1);
				}
			}

			logger.info("solicitudEdicionT.getTiivsSolicitudAgrupacions() : "+ solicitudEdicionT.getTiivsSolicitudAgrupacions().size());
			if (this.validarRegistroSolicitud()) {
				if (!this.sEstadoSolicitud.equals("BORRADOR")) {
					this.enviarSolicitudSSJJ();
				}
				logger.info(solicitudEdicionT.getTiivsEstudio().getCodEstudio());
				TiivsSolicitud objResultado = service.insertar(this.solicitudEdicionT);
				  for (TiivsSolicitudAgrupacion x : this.solicitudEdicionT.getTiivsSolicitudAgrupacions()) {
				  for (TiivsAgrupacionPersona b :x.getTiivsAgrupacionPersonas()) { 
					  logger.info("b.getTiivsPersona() " +b.getId().getCodPer());
					     //b.setId
					  objPersonaRetorno=servicePers.insertarMerge(b.getTiivsPersona());
					  // System.out.println("ccdcdcd : "+objPersonaRetorno.getCodPer());
					     b.setTiivsPersona(null);
					     objAgruId=b.getId();
					     objAgruId.setCodPer(objPersonaRetorno.getCodPer());
					     b.setId(objAgruId);
					     serviceAgru.insertar(b);
					     } 
				  
				  }
				  TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
				  objHistorial.setId(new TiivsHistSolicitudId(this.solicitudEdicionT.getCodSoli(),1+""));
				  objHistorial.setEstado(this.solicitudEdicionT.getEstado());
				  objHistorial.setNomUsuario(this.solicitudEdicionT.getNomUsuario());
				  objHistorial.setObs(this.solicitudEdicionT.getObs());
				  objHistorial.setFecha(new Timestamp(new Date().getDate()));
				  objHistorial.setRegUsuario(this.solicitudEdicionT.getRegUsuario());
				  serviceHistorialSolicitud.insertar(objHistorial);
				  //Carga ficheros al FTP
				  boolean bRet = cargarArchivosFTP();
				  logger.info("Resultado de carga de archivos al FTP:" + bRet);
				  //Elimina archivos temporales
				  eliminarArchivosTemporales();
				  
				  for (TiivsAnexoSolicitud n : this.lstAnexoSolicitud) {
					  logger.info("nnnnnnnnnnnnnnnnnnnnnnn "+n.getAliasArchivo());
					  serviceAnexos.insertar(n);
				   }
				 
				for (TiivsSolicitudOperban a : this.lstSolicBancarias) {
					logger.info("a.getId().getCodOperBan() **** "+ a.getId().getCodOperBan());
					a.getId().setCodSoli(this.solicitudEdicionT.getCodSoli());
					logger.info("a.getId().getCodSoli() **** "+ a.getId().getCodSoli());
					 serviceSoli.insertar(a);
				}
				if (objResultado.getCodSoli() != "" || objResultado != null) {
					if (this.sEstadoSolicitud.equals("BORRADOR")) {
						mensaje = "Se registro correctamente la Solicitud con codigo : "+ objResultado.getCodSoli() + " en Borrador";
						Utilitarios.mensajeInfo("INFO", mensaje);
					} else {
						mensaje = "Se envio a SSJJ correctamente la Solicitud con codigo : "+ objResultado.getCodSoli();
						Utilitarios.mensajeInfo("INFO", mensaje);
					}
					
					
					//--this.eliminarArchivosTemporales();
					
				} else {
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
					
					//Elimina archivo temporal
					//this.eliminarArchivosTemporales();
					
				}

				logger.info("objResultado.getCodSoli(); "+ objResultado.getCodSoli());
				logger.info("objResultado.getTiivsSolicitudAgrupacions() "+ objResultado.getTiivsSolicitudAgrupacions().size());
				logger.info("this.solicitudEdicionT.importe : " +this.solicitudEdicionT.getImporte());
				instanciarSolicitudRegistro();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	public void enviarSolicitudSSJJ() {
		Timestamp time = new Timestamp(objRegistroUtilesMB.obtenerFechaRespuesta().getTime());
		logger.info("time : " + time);
		String sCodigoEstudio = objRegistroUtilesMB.obtenerEstudioMenorCarga();
		logger.info(" sCodigoEstudio +  " + sCodigoEstudio);
		for (TiivsEstudio x : combosMB.getLstEstudio()) {
			if (x.getCodEstudio().equals(sCodigoEstudio)) {
				this.solicitudEdicionT.setTiivsEstudio(x);
			}
		}
		this.solicitudEdicionT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02);
		this.solicitudEdicionT.setFechaRespuesta(time);
		this.solicitudEdicionT.setFechaEnvio(new Timestamp(new Date().getTime()));
	}
	
	public void instanciarSolicitudRegistro() {
		logger.info("********************** instanciarSolicitudRegistro *********************");
		sEstadoSolicitud = "BORRADOR";
		lstTiivsAgrupacionPersonas = new HashSet<TiivsAgrupacionPersona>();
		lstTiivsSolicitudAgrupacion = new HashSet<TiivsSolicitudAgrupacion>();
		solicitudEdicionT = new TiivsSolicitud();
		solicitudEdicionT.setImporte((double) 0);
		solicitudEdicionT.setTiivsSolicitudAgrupacions(lstTiivsSolicitudAgrupacion);
		solicitudEdicionT.setTiivsOficina1(new TiivsOficina1());
		solicitudEdicionT.setTiivsTipoSolicitud(new TiivsTipoSolicitud());
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
					this.solicitudEdicionT.setTiivsOficina1(o);
				} else {
					this.solicitudEdicionT.setTiivsOficina1(new TiivsOficina1());
				}
			}
		
		}
		SolicitudDao<TiivsPersona, Object> service = (SolicitudDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		try {
			String sCodigoSol = service.obtenerPKNuevaSolicitud();
			logger.debug(" sCodigoSol " + sCodigoSol);
			this.solicitudEdicionT.setCodSoli(sCodigoSol);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.solicitudEdicionT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02);
		this.solicitudEdicionT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_REGISTRADO_T02);

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
	
	private void cargarDocumentos() {
		
		GenericDao<TiivsDocumento, Object> documentoDAO = (GenericDao<TiivsDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroDocumento = Busqueda.forClass(TiivsDocumento.class);
		try {
			lstTiivsDocumentos = documentoDAO.buscarDinamico(filtroDocumento);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de documentos");
		}
		
	}
	
	public String buscarAbrevMoneda(String codigo) {
		int i = 0;
		String descripcion = "";

		for (; i <= combosMB.getLstMoneda().size() - 1; i++) 
		{
			if (combosMB.getLstMoneda().get(i).getCodMoneda().equalsIgnoreCase(codigo)) {
				if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_SOLES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_SOLES;
				} 
				else if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_DOLARES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_DOLARES;
				} 
				else if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_EUROS_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_EUROS;
				} 
				else 
				{
					descripcion = combosMB.getLstMoneda().get(i).getDesMoneda();
				}
				break;
			}
		}

		return descripcion;
	}
	
	public void obtenerSolicitud()
	{
		try 
		{
		   lstAgrupacionSimpleDto=new ArrayList<AgrupacionSimpleDto>();
		   String codigoSolicitud=Utilitarios.capturarParametro("prm_codSoli");
		   logger.info("codigoSolicitud : "+codigoSolicitud);
		   TiivsSolicitud solicitud =new TiivsSolicitud();
		   solicitud.setCodSoli(codigoSolicitud);
		   SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		   solicitudEdicionT= solicitudService.obtenerTiivsSolicitud(solicitud);
		   solicitudEdicionT.setDescEstado(Utilitarios.obternerDescripcionEstado(solicitudEdicionT.getEstado()));
		   
		   if (solicitudEdicionT.getMoneda()!=null && solicitudEdicionT.getImporte()!=null)
		   {
			   solicitudEdicionT.setsImporteMoneda(buscarAbrevMoneda(solicitudEdicionT.getMoneda()).concat(ConstantesVisado.DOS_PUNTOS).concat(solicitudEdicionT.getImporte().toString()));
		   }
		   else
		   {
			   if (solicitudEdicionT.getImporte()!=null)
			   {
				   solicitudEdicionT.setsImporteMoneda(solicitudEdicionT.getImporte().toString());
			   }
		   }
		   
		   if(solicitudEdicionT.getTiivsEstudio()==null)
		   {
			   solicitudEdicionT.setTiivsEstudio(new TiivsEstudio());
		   }
		   
		   lstSolicBancarias=solicitudService.obtenerListarOperacionesBancarias(solicitud);
		   int y=0;
		   
		   for (TiivsSolicitudOperban f : lstSolicBancarias) 
		   {
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
		   
		   for (TiivsAnexoSolicitud v : lstAnexosSolicitudes) 
		   {
			  i++;
			  lstdocumentos.add(new DocumentoTipoSolicitudDTO(String.format("%03d",i) , obtenerDescripcionDocumento(v.getId().getCodDoc()), v.getAliasTemporal()));
		   }
		   
		   // PODERDANTES Y APODERADOS
		   List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
		   List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
		   AgrupacionSimpleDto agrupacionSimpleDto  =new AgrupacionSimpleDto(); ;
		   List<TiivsPersona>lstPersonas=new ArrayList<TiivsPersona>();
		   TiivsPersona objPersona=new TiivsPersona();
		   
		   for (TiivsSolicitudAgrupacion x : solicitudEdicionT.getTiivsSolicitudAgrupacions()) 
		   {
			   for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) 
			   {
				   logger.info("d.getTiivsPersona() "+d.getTiivsPersona());
				   objPersona=new TiivsPersona();
				   objPersona=d.getTiivsPersona();
				   objPersona.setTipPartic(d.getId().getTipPartic());
				   objPersona.setClasifPer(d.getId().getClasifPer());
				   lstPersonas.add(objPersona);
				   
				   if(d.getId().getTipPartic().equals(ConstantesVisado.PODERDANTE))
				   {
					  lstPoderdantes.add(d.getTiivsPersona());
				   }
				   else  if(d.getId().getTipPartic().equals(ConstantesVisado.PODERDANTE))
				   {
					  lstApoderdantes.add(d.getTiivsPersona());
				   }
			   }
			   
			   agrupacionSimpleDto =new AgrupacionSimpleDto();
			   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudEdicionT.getCodSoli(), x.getId().getNumGrupo()));
			   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
			   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
			   agrupacionSimpleDto.setsEstado(Utilitarios.obternerDescripcionMoneda(x.getActivo().trim()) );
			   agrupacionSimpleDto.setLstPersonas(lstPersonas);
			   lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
		   }
		  		  
		 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String obtenerDescripcionDocumento(String codDoc) 
	{		
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
	
	public void obtenerHistorialSolicitud(){
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + solicitudEdicionT.getCodSoli());
		
		String sCodSolicitud=solicitudEdicionT.getCodSoli();
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

	public TiivsSolicitud getSolicitudEdicionT() {
		return solicitudEdicionT;
	}

	public void setSolicitudEdicionT(TiivsSolicitud solicitudEdicionT) {
		this.solicitudEdicionT = solicitudEdicionT;
	}

	public List<TiivsSolicitudOperban> getLstSolicBancarias() {
		return lstSolicBancarias;
	}

	public void setLstSolicBancarias(List<TiivsSolicitudOperban> lstSolicBancarias) {
		this.lstSolicBancarias = lstSolicBancarias;
	}

	public List<TiivsAnexoSolicitud> getLstAnexosSolicitudes() {
		return lstAnexosSolicitudes;
	}

	public void setLstAnexosSolicitudes(
			List<TiivsAnexoSolicitud> lstAnexosSolicitudes) {
		this.lstAnexosSolicitudes = lstAnexosSolicitudes;
	}

	public List<DocumentoTipoSolicitudDTO> getLstdocumentos() {
		return lstdocumentos;
	}

	public void setLstdocumentos(List<DocumentoTipoSolicitudDTO> lstdocumentos) {
		this.lstdocumentos = lstdocumentos;
	}

	public List<AgrupacionSimpleDto> getLstAgrupacionSimpleDto() {
		return lstAgrupacionSimpleDto;
	}

	public void setLstAgrupacionSimpleDto(
			List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public List<TiivsDocumento> getLstTiivsDocumentos() {
		return lstTiivsDocumentos;
	}

	public void setLstTiivsDocumentos(List<TiivsDocumento> lstTiivsDocumentos) {
		this.lstTiivsDocumentos = lstTiivsDocumentos;
	}

	public List<SeguimientoDTO> getLstSeguimientoDTO() {
		return lstSeguimientoDTO;
	}

	public void setLstSeguimientoDTO(List<SeguimientoDTO> lstSeguimientoDTO) {
		this.lstSeguimientoDTO = lstSeguimientoDTO;
	}


	public PDFViewerMB getPdfViewerMB() {
		return pdfViewerMB;
	}


	public void setPdfViewerMB(PDFViewerMB pdfViewerMB) {
		this.pdfViewerMB = pdfViewerMB;
	}


	public RegistroUtilesMB getObjRegistroUtilesMB() {
		return objRegistroUtilesMB;
	}


	public void setObjRegistroUtilesMB(RegistroUtilesMB objRegistroUtilesMB) {
		this.objRegistroUtilesMB = objRegistroUtilesMB;
	}


	public TiivsSolicitudOperban getObjSolicBancaria() {
		return objSolicBancaria;
	}


	public void setObjSolicBancaria(TiivsSolicitudOperban objSolicBancaria) {
		this.objSolicBancaria = objSolicBancaria;
	}


	public Solicitud getSolicitudEdicion() {
		return solicitudEdicion;
	}


	public void setSolicitudEdicion(Solicitud solicitudEdicion) {
		this.solicitudEdicion = solicitudEdicion;
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


	public List<DocumentoTipoSolicitudDTO> getLstdocumentosOpcional() {
		return lstdocumentosOpcional;
	}


	public void setLstdocumentosOpcional(
			List<DocumentoTipoSolicitudDTO> lstdocumentosOpcional) {
		this.lstdocumentosOpcional = lstdocumentosOpcional;
	}


	public List<SeguimientoDTO> getLstSeguimiento() {
		return lstSeguimiento;
	}


	public void setLstSeguimiento(List<SeguimientoDTO> lstSeguimiento) {
		this.lstSeguimiento = lstSeguimiento;
	}


	public List<TiivsOperacionBancaria> getLstTiivsOperacionBancaria() {
		return lstTiivsOperacionBancaria;
	}


	public void setLstTiivsOperacionBancaria(
			List<TiivsOperacionBancaria> lstTiivsOperacionBancaria) {
		this.lstTiivsOperacionBancaria = lstTiivsOperacionBancaria;
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


	public Set<TiivsAgrupacionPersona> getLstTiivsAgrupacionPersonas() {
		return lstTiivsAgrupacionPersonas;
	}


	public void setLstTiivsAgrupacionPersonas(
			Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas) {
		this.lstTiivsAgrupacionPersonas = lstTiivsAgrupacionPersonas;
	}


	public Set<TiivsSolicitudAgrupacion> getLstTiivsSolicitudAgrupacion() {
		return lstTiivsSolicitudAgrupacion;
	}


	public void setLstTiivsSolicitudAgrupacion(
			Set<TiivsSolicitudAgrupacion> lstTiivsSolicitudAgrupacion) {
		this.lstTiivsSolicitudAgrupacion = lstTiivsSolicitudAgrupacion;
	}


	public List<TiivsAnexoSolicitud> getLstAnexoSolicitud() {
		return lstAnexoSolicitud;
	}


	public void setLstAnexoSolicitud(List<TiivsAnexoSolicitud> lstAnexoSolicitud) {
		this.lstAnexoSolicitud = lstAnexoSolicitud;
	}


	public PersonaDataModal getPersonaDataModal() {
		return personaDataModal;
	}


	public void setPersonaDataModal(PersonaDataModal personaDataModal) {
		this.personaDataModal = personaDataModal;
	}


	public String getiTipoSolicitud() {
		return iTipoSolicitud;
	}


	public void setiTipoSolicitud(String iTipoSolicitud) {
		this.iTipoSolicitud = iTipoSolicitud;
	}


	public TiivsSolicitudOperban getObjSolicitudOperacionCapturadoOld() {
		return objSolicitudOperacionCapturadoOld;
	}


	public void setObjSolicitudOperacionCapturadoOld(
			TiivsSolicitudOperban objSolicitudOperacionCapturadoOld) {
		this.objSolicitudOperacionCapturadoOld = objSolicitudOperacionCapturadoOld;
	}


	public String getsCodDocumento() {
		return sCodDocumento;
	}


	public void setsCodDocumento(String sCodDocumento) {
		this.sCodDocumento = sCodDocumento;
	}


	public TiivsTipoSolicDocumento getSelectedTipoDocumento() {
		return selectedTipoDocumento;
	}


	public void setSelectedTipoDocumento(
			TiivsTipoSolicDocumento selectedTipoDocumento) {
		this.selectedTipoDocumento = selectedTipoDocumento;
	}


	public DocumentoTipoSolicitudDTO getSelectedDocumentoDTO() {
		return selectedDocumentoDTO;
	}


	public void setSelectedDocumentoDTO(
			DocumentoTipoSolicitudDTO selectedDocumentoDTO) {
		this.selectedDocumentoDTO = selectedDocumentoDTO;
	}


	public String getUbicacionTemporal() {
		return ubicacionTemporal;
	}


	public void setUbicacionTemporal(String ubicacionTemporal) {
		this.ubicacionTemporal = ubicacionTemporal;
	}


	public List<String> getAliasFilesToDelete() {
		return aliasFilesToDelete;
	}


	public void setAliasFilesToDelete(List<String> aliasFilesToDelete) {
		this.aliasFilesToDelete = aliasFilesToDelete;
	}


	public IILDPeUsuario getUsuario() {
		return usuario;
	}


	public void setUsuario(IILDPeUsuario usuario) {
		this.usuario = usuario;
	}


	public int getNumGrupo() {
		return numGrupo;
	}


	public void setNumGrupo(int numGrupo) {
		this.numGrupo = numGrupo;
	}


	public List<TiivsPersona> getLstTiivsPersona() {
		return lstTiivsPersona;
	}


	public void setLstTiivsPersona(List<TiivsPersona> lstTiivsPersona) {
		this.lstTiivsPersona = lstTiivsPersona;
	}


	public List<TiivsPersona> getLstTiivsPersonaResultado() {
		return lstTiivsPersonaResultado;
	}


	public void setLstTiivsPersonaResultado(
			List<TiivsPersona> lstTiivsPersonaResultado) {
		this.lstTiivsPersonaResultado = lstTiivsPersonaResultado;
	}


	public List<TiivsTipoSolicDocumento> getLstTipoSolicitudDocumentos() {
		return lstTipoSolicitudDocumentos;
	}


	public void setLstTipoSolicitudDocumentos(
			List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos) {
		this.lstTipoSolicitudDocumentos = lstTipoSolicitudDocumentos;
	}


	public List<TiivsTipoSolicDocumento> getLstDocumentosXTipoSolTemp() {
		return lstDocumentosXTipoSolTemp;
	}


	public void setLstDocumentosXTipoSolTemp(
			List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp) {
		this.lstDocumentosXTipoSolTemp = lstDocumentosXTipoSolTemp;
	}


	public boolean isbBooleanPopup() {
		return bBooleanPopup;
	}


	public void setbBooleanPopup(boolean bBooleanPopup) {
		this.bBooleanPopup = bBooleanPopup;
	}


	public boolean isbBooleanPopupTipoCambio() {
		return bBooleanPopupTipoCambio;
	}


	public void setbBooleanPopupTipoCambio(boolean bBooleanPopupTipoCambio) {
		this.bBooleanPopupTipoCambio = bBooleanPopupTipoCambio;
	}


	public boolean isFlagUpdateOperacionSolic() {
		return flagUpdateOperacionSolic;
	}


	public void setFlagUpdateOperacionSolic(boolean flagUpdateOperacionSolic) {
		this.flagUpdateOperacionSolic = flagUpdateOperacionSolic;
	}


	public boolean isFlagUpdateOperacionSolcAgrupac() {
		return flagUpdateOperacionSolcAgrupac;
	}


	public void setFlagUpdateOperacionSolcAgrupac(
			boolean flagUpdateOperacionSolcAgrupac) {
		this.flagUpdateOperacionSolcAgrupac = flagUpdateOperacionSolcAgrupac;
	}


	public boolean isFlagUpdateOperacionSolcDocumen() {
		return flagUpdateOperacionSolcDocumen;
	}


	public void setFlagUpdateOperacionSolcDocumen(
			boolean flagUpdateOperacionSolcDocumen) {
		this.flagUpdateOperacionSolcDocumen = flagUpdateOperacionSolcDocumen;
	}


	public boolean isFlagUpdatePersona() {
		return flagUpdatePersona;
	}


	public void setFlagUpdatePersona(boolean flagUpdatePersona) {
		this.flagUpdatePersona = flagUpdatePersona;
	}


	public String getsEstadoSolicitud() {
		return sEstadoSolicitud;
	}


	public void setsEstadoSolicitud(String sEstadoSolicitud) {
		this.sEstadoSolicitud = sEstadoSolicitud;
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


	public int getIndexUpdateOperacion() {
		return indexUpdateOperacion;
	}


	public void setIndexUpdateOperacion(int indexUpdateOperacion) {
		this.indexUpdateOperacion = indexUpdateOperacion;
	}


	public int getIndexUpdateAgrupacionSimpleDto() {
		return indexUpdateAgrupacionSimpleDto;
	}


	public void setIndexUpdateAgrupacionSimpleDto(int indexUpdateAgrupacionSimpleDto) {
		this.indexUpdateAgrupacionSimpleDto = indexUpdateAgrupacionSimpleDto;
	}


	public int getIndexUpdateSolicDocumentos() {
		return indexUpdateSolicDocumentos;
	}


	public void setIndexUpdateSolicDocumentos(int indexUpdateSolicDocumentos) {
		this.indexUpdateSolicDocumentos = indexUpdateSolicDocumentos;
	}


	public int getIndexUpdatePersona() {
		return indexUpdatePersona;
	}


	public void setIndexUpdatePersona(int indexUpdatePersona) {
		this.indexUpdatePersona = indexUpdatePersona;
	}


	public Map<Integer, TiivsSolicitudOperban> getMapSolicitudes() {
		return mapSolicitudes;
	}


	public void setMapSolicitudes(Map<Integer, TiivsSolicitudOperban> mapSolicitudes) {
		this.mapSolicitudes = mapSolicitudes;
	}


	public String getCadenaEscanerFinal() {
		return cadenaEscanerFinal;
	}


	public void setCadenaEscanerFinal(String cadenaEscanerFinal) {
		this.cadenaEscanerFinal = cadenaEscanerFinal;
	}


	public UploadedFile getFile() {
		return file;
	}


	public void setFile(UploadedFile file) {
		this.file = file;
	}


	public String getsMonedaImporteGlobal() {
		return sMonedaImporteGlobal;
	}


	public void setsMonedaImporteGlobal(String sMonedaImporteGlobal) {
		this.sMonedaImporteGlobal = sMonedaImporteGlobal;
	}	
}

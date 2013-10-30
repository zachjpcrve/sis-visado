package com.hildebrando.visado.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.common.util.EstilosNavegador;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecDUMMY;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.converter.PersonaDataModal;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.Estado;
import com.hildebrando.visado.dto.Revocado;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.ftp.ClienteFTP;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsRevocado;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.service.TiposDoiService;

@ManagedBean(name = "revocadosMB")
@SessionScoped
//@RequestScoped
public class RevocadosMB {

	public static Logger logger = Logger.getLogger(RevocadosMB.class);

	private List<Revocado> revocados;
	private Revocado revocadoDelete;
	private Revocado revocadoVer;
	private Revocado revocadoEdit;
	private String nroRegistros;
	
	private String rutaArchivoExcel;
	
	private StreamedContent file;  
	
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	
	//para la busqueda principal
	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaBusquedaNombre;
	private String estadoRevocado;
	private Date fechaInicio;
	private Date fechaFin;
	
	//busqueda de los nuevos
	private TiivsPersona objTiivsPersonaBusquedaDlg;
	//el resultado de la busqueda y se utilizara para agregarlo
	
	private TiivsPersona objTiivsPersonaAgregar;
	
//	private TiivsRevocado objTiivsPersonaAgregar;
	
	private IILDPeUsuario usuario;
	
	boolean bBooleanPopup = false;
	
	
//	private TiivsPersona deletePersonaEdit;
	private TiivsRevocado deletePersonaEdit;

	private List<TiivsPersona> personaClientes;
	private List<Revocado> personaClientesActivoEdit;
	private List<Revocado> personaClientesPendEdit;
	
	private List<Revocado> personaClientesVer;
	
	private TiivsPersona objTiivsPersonaSeleccionado;
	
	private PersonaDataModal personaDataModal;
	
	
	private TiivsPersona selectPersonaBusqueda;
	private TiivsPersona selectPersonaPendEdit;
	private TiivsPersona selectPersonaActEdit;
	
	private boolean flagRevocar;
	private boolean flagGuardar;
	
	private List<String>  listCodSoli;
	private List<Integer> listNumGrupo;
	private List<String> listSolicResult;
	
	//combos
	private List<TiivsMultitabla> listDocumentos;
	private List<TiivsMultitabla> listEstados;
	private List<TiivsMultitabla> listTipoRegistro;
	private List<TiivsMultitabla> listEstadoRevocados;
	
//	private List<TiivsPersona> apoderadosNuevo;
//	private List<TiivsPersona> poderdantesNuevo;
	
	private List<TiivsRevocado> apoderadosNuevo;
	private List<TiivsRevocado> poderdantesNuevo;
	
	private String msjResultRevocados;
	private String ancho_Popup_Revoc_Poder;
	private String alto_Popup_Revoc_Poder;

	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	EstilosNavegador estilosNavegador;
	private Boolean deshabilitarExportar=true;
	private List<String> lstElimRevo;
	
	/**** SE AGREGAN VARIABLES DE ABAJO ***/
	private String codigoRazonSocial = "0000";
	private boolean mostrarRazonSocial = false;
	private String patter;
	private String tipoRegistro;
	private String poderdante;
	private String apoderdante;
	private UploadedFile fileUpload;
	private String aliasCortoDocumento;
	private String sAliasTemporal;
	private Integer flagLinkRevocados;
	String cadenaEscanerFinal = "";
	
	@ManagedProperty(value = "#{visadoDocumentosMB}")
	private VisadoDocumentosMB visadoDocumentosMB;
	
	public UploadedFile getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(UploadedFile fileUpload) {
		this.fileUpload = fileUpload;
	}

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public RevocadosMB() {
		combosMB = new CombosMB();
		
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		
		objTiivsPersonaBusqueda = new TiivsPersona();
		objTiivsPersonaBusquedaNombre = new TiivsPersona();
		lstElimRevo = new ArrayList<String>();
		objTiivsPersonaBusquedaDlg = new TiivsPersona();
		
		//Se agrego esta linea
		/*if(objTiivsPersonaAgregar==null) objTiivsPersonaAgregar = new TiivsPersona();
		if(personaClientesPendEdit==null) personaClientesPendEdit = new ArrayList<Revocado>();
		if(apoderadosNuevo==null) apoderadosNuevo = new ArrayList<TiivsRevocado>();
		if(poderdantesNuevo==null) poderdantesNuevo = new ArrayList<TiivsRevocado>();*/
		
		estadoRevocado= "S";
		listSolicResult = new ArrayList<String>();
		estilosNavegador=new EstilosNavegador();
		estilosNavegador.estilosNavegador();
		ancho_Popup_Revoc_Poder=(String) Utilitarios.getObjectInSession("ANCHO_POPUP_REVOC_PODER");
		alto_Popup_Revoc_Poder=(String) Utilitarios.getObjectInSession("ALTO_POPUP_REVOC_PODER");
		
		cargarCombos();
		revocados = new ArrayList<Revocado>();
		cargarCombinacionesRevocadas();
		//Se agrega
		obtenCodRazonSocial();
		obtenerTipoRegistro();
		obtenerEtiquetasTipoRegistro();
		this.cadenaEscanerFinal = this.prepararURLEscaneo();
	}
	
	public void cargarCombinacionesRevocadas()
	{
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		List<TiivsRevocado> lstRevocadosPre = new ArrayList<TiivsRevocado>();
		revocados = new ArrayList<Revocado>();
		
		try {
			lstRevocadosPre = service.buscarDinamico(filtro.addOrder(Order.desc("codAgrup")).addOrder(Order.desc("fechaRevocatoria")));
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar lstRevocadosPre:",  e);
		}
		
		List<Integer> tmpAgrup = new ArrayList<Integer>();
		
		for (TiivsRevocado tmp: lstRevocadosPre)
		{
			if (tmp!=null)
			{
				tmpAgrup.add(tmp.getCodAgrup());
			}
		}
		
		//Busqueda de agrupaciones de revocados
		List<TiivsRevocado> tiivsrevocados = new ArrayList<TiivsRevocado>();
		GenericDao<TiivsRevocado, Object> serviceRev = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroRev = Busqueda.forClass(TiivsRevocado.class);
		filtroRev.add(Restrictions.in("codAgrup", tmpAgrup));
		
		try {
			tiivsrevocados = serviceRev.buscarDinamico(filtroRev.addOrder(Order.desc("codAgrup")).addOrder(Order.desc("fechaRevocatoria")));
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar la lista final de revocados:",  e);
		}
		
		if (tiivsrevocados.size()>0)
		{
			setDeshabilitarExportar(false);
		}
		else
		{
			setDeshabilitarExportar(true);
		}
				
		Revocado revocado;
		String nombreCompletoApoderados="";
		String nombreCompletoPoderdantes="";
		String fecha="";
		String estado="";
		int numCorrelativo=0;
		
		List<TiivsRevocado> apoderados;				
		List<TiivsRevocado> poderdantes;
		
		 List<Integer>  listCodAgrup =  obtenerListCodAgrupacion();
		
		if(listCodAgrup != null)
		{	
			if(listCodAgrup.size() > 0)
			{
				
				for(Integer tiivsRevocado2:listCodAgrup)
				{
					numCorrelativo++;
					apoderados= new ArrayList<TiivsRevocado>();
					poderdantes= new ArrayList<TiivsRevocado>();
					
					try
					{
						for(TiivsRevocado tiivsRevocado:tiivsrevocados)
						{
							if(tiivsRevocado.getCodAgrup().compareTo(tiivsRevocado2)==0)
							{		
								fecha=getDate(tiivsRevocado.getFechaRevocatoria());
								estado=getValor1(tiivsRevocado.getEstado(),listEstados);
								
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO)){
																	
									String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
									
									nombreCompletoApoderados = nombreCompletoApoderados
																	+ " " + descDoiApod
																	+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
																  + " - " + (tiivsRevocado.getTiivsPersona().getApePat()==null?"":tiivsRevocado.getTiivsPersona().getApePat()) 
																	+ " " + (tiivsRevocado.getTiivsPersona().getApeMat()==null?"":tiivsRevocado.getTiivsPersona().getApeMat())
																	+ " " + (tiivsRevocado.getTiivsPersona().getNombre()==null?"":tiivsRevocado.getTiivsPersona().getNombre()) + "\n";
									
									tiivsRevocado.setsDesctipDoi( descDoiApod);
									tiivsRevocado.setsDesctipPartic(descTipPart);
									apoderados.add(tiivsRevocado);
								}
								
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)){
									
									String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
									
									nombreCompletoApoderados = nombreCompletoApoderados
																	+ " " + descDoiApod
																	+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
																  + " - " + (tiivsRevocado.getTiivsPersona().getApePat()==null?"":tiivsRevocado.getTiivsPersona().getApePat()) 
																	+ " " + (tiivsRevocado.getTiivsPersona().getApeMat()==null?"":tiivsRevocado.getTiivsPersona().getApeMat())
																	+ " " + (tiivsRevocado.getTiivsPersona().getNombre()==null?"":tiivsRevocado.getTiivsPersona().getNombre()) + "\n";
									
									tiivsRevocado.setsDesctipDoi( descDoiApod);
									tiivsRevocado.setsDesctipPartic(descTipPart);
									apoderados.add(tiivsRevocado);
								}
									
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
								
									String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
									
											
									nombreCompletoPoderdantes = nombreCompletoPoderdantes 
																		+ " " + descDoiPod
																		+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
																		 + " - " + (tiivsRevocado.getTiivsPersona().getApePat()==null?"":tiivsRevocado.getTiivsPersona().getApePat()) 
																	+ " " + (tiivsRevocado.getTiivsPersona().getApeMat()==null?"":tiivsRevocado.getTiivsPersona().getApeMat())
																	+ " " + (tiivsRevocado.getTiivsPersona().getNombre()==null?"":tiivsRevocado.getTiivsPersona().getNombre()) + "\n";
															
									tiivsRevocado.setsDesctipDoi( descDoiPod);
									tiivsRevocado.setsDesctipPartic(descTipPart);
									
									poderdantes.add(tiivsRevocado);
								}
							}							
						}
						
						revocado = new Revocado();
						revocado.setCodAgrupacion(tiivsRevocado2+"");
						revocado.setFechaRegistro(fecha);
						revocado.setEstado(estado);
						revocado.setCorrelativo(String.valueOf(numCorrelativo));
						
						revocado.setNombreCompletoApoderados(nombreCompletoApoderados.trim());
						revocado.setApoderados(apoderados);
							
						revocado.setNombreCompletoPoderdantes(nombreCompletoPoderdantes.trim());
						revocado.setPoderdantes(poderdantes);
						revocado.setAliasArchivo((poderdantes.size()>0?poderdantes.get(0).getAliasArchivo():null));
						
						
						if(estado.compareTo("Activo")==0){
							revocado.setFlagEditAct(true);
							revocado.setFlagEditPend(false);
							revocado.setFlagDelete(false);
						}
						
						if(estado.compareTo("Pendiente")==0){
							revocado.setFlagEditPend(true);
							revocado.setFlagEditAct(false);
							revocado.setFlagDelete(true);
						}
						
						if(estado.compareTo("Inactivo")==0){
							revocado.setFlagEditPend(false);
							revocado.setFlagEditAct(false);
							revocado.setFlagDelete(false);
							
							
						}
						
						if(apoderados.size() > 0  || poderdantes.size() > 0){
							revocados.add(revocado);
						}
						
						nombreCompletoApoderados="";
						nombreCompletoPoderdantes="";
						fecha="";
						estado="";
					
					} catch(Exception e){
						logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR,e);
					}
				}
				
			}
		}
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
	
	public String obtenerGenerador()
	{
		String resultado="";
		
		if (usuario != null)
		{
			resultado = usuario.getUID() + ConstantesVisado.GUION + usuario.getNombre() +  ConstantesVisado.ESPACIO_BLANCO + 
						usuario.getApellido1() + ConstantesVisado.ESPACIO_BLANCO + usuario.getApellido2();
		}
		else
		{
			logger.debug("Error al obtener datos del usuario de session para mostrar en el excel");
		}
		return resultado;
	}
	
	public void descargarArchivoRecaudacion()
	{
		exportarExcelRevocados();
		InputStream stream=null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.error("Error al obtener archivo excel debido a: " , e);
		}catch(Exception e){
			logger.error("Error al obtener archivo excel : " , e);
		}
		
		if (stream!=null)
		{
			file = new DefaultStreamedContent(stream, "application/excel", "Revocados" + ConstantesVisado.EXTENSION_XLS);
		}
	}
	
	public void exportarExcelRevocados()
	{
		rptRevocados();
	}
	
	
	public void rptRevocados() { 
		
		try 
		{
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 2, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, ConstantesVisado.TITULO_REPORTE_REVOCADOS,14);
						
			//Se crea la leyenda de quien genero el archivo y la hora respectiva
			Row rowG = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerGenerador(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			Row rowG1 = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG1, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.obtenerFechaHoraActual(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			//Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 5);
			
			Utilitarios.crearCell(wb, row2, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_REGISTRO, false, false,false,HSSFColor.DARK_BLUE.index);
			
			if (objTiivsPersonaBusqueda.getTipPartic() != null)
			{
				if (objTiivsPersonaBusqueda.getTipPartic().length()>0)
				{
					Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, devolverDescripcionCampo(objTiivsPersonaBusqueda.getTipPartic(),"tipoRegistro"), true, false,true,HSSFColor.DARK_BLUE.index);
				}
				else
				{
					Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
				}
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row row3 = sheet.createRow((short) 7);
			
			Utilitarios.crearCell(wb, row3, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_T_DOI_C_CENTRAL, false, false,false,HSSFColor.DARK_BLUE.index);
			
			if (objTiivsPersonaBusqueda.getTipDoi().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, devolverDescripcionCampo(objTiivsPersonaBusqueda.getTipDoi(),"tipoDOI"), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Utilitarios.crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_NRO_DOI, false, false,false,HSSFColor.DARK_BLUE.index);
			if (objTiivsPersonaBusqueda.getNumDoi().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, objTiivsPersonaBusqueda.getNumDoi(), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Utilitarios.crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_NOMBRE, false, false,false,HSSFColor.DARK_BLUE.index);
			
			if (objTiivsPersonaBusquedaNombre!=null)
			{
				if (objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().compareTo("")!=0)
				{
					Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, 
							objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[0] +" "+ 
							objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[1] +" "+
							objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[2], true, false,true,HSSFColor.DARK_BLUE.index);
				}
				else
				{
					Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
				}
				
			}else{
				
				Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row row4 = sheet.createRow((short) 9);
			
			Utilitarios.crearCell(wb, row4, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO, false, false,false,HSSFColor.DARK_BLUE.index);
			if (estadoRevocado.compareTo("S")!=0)
			{
				Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, devolverDescripcionCampo(estadoRevocado,"estado"), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
		
			Utilitarios.crearCell(wb, row4, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaInicio()!=null)
			{
				Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaInicio()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row rowFec = sheet.createRow((short) 11);
			Utilitarios.crearCell(wb, rowFec, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaFin()!=null)
			{
				Utilitarios.crearCell(wb, rowFec, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaFin()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, rowFec, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row rowTot = sheet.createRow((short) 13);
			Utilitarios.crearCell(wb, rowTot, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Total de Registros: " + (revocados.size()) , false, false,false,HSSFColor.DARK_BLUE.index);
			
		
			if (revocados.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 14);

				// Creo las celdas de mi fila, se puede poner un diseño a la celda
				Utilitarios.crearCellRevocados(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_NRO, true, true,false,HSSFColor.DARK_BLUE.index);
				
				Utilitarios.crearCellRevocados(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_PODERDANTE, true, true,false,HSSFColor.DARK_BLUE.index);
				
				Utilitarios.crearCellRevocados(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_APODERADO, true, true,false,HSSFColor.DARK_BLUE.index);
				
				Utilitarios.crearCellRevocados(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_F_REVOCACION, true, true,false,HSSFColor.DARK_BLUE.index);
				
				Utilitarios.crearCellRevocados(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ESTADO, true, true,false,HSSFColor.DARK_BLUE.index);
				
				int numReg=15;
				
				for (Revocado tmp: revocados)
				{
					Row row = sheet.createRow((short) numReg);
					
					//Columna Correlativo en Excel
					Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCorrelativo(),true, false,true,HSSFColor.DARK_BLUE.index);
										
					//Columna Poderdantes en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getNombreCompletoPoderdantes(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Apoderados en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getNombreCompletoApoderados(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Fecha Registro en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getFechaRegistro(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Estado en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getEstado(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					numReg++;
				}
			}
			
			//Arregla ancho de columnas
			int pos=0;
			for (;pos<=18;pos++)
			{
				sheet.autoSizeColumn(pos);
			}
						
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta="";
			if (obtenerRutaExcel().compareTo("")!=0)
			{
				
				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + "Revocados");
				
				strRuta = obtenerRutaExcel() + "Revocados" + ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				logger.debug("Ruta final donde encontrar el archivo excel: " + strRuta);
				
				setRutaArchivoExcel(strRuta);
			}
						
		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Estado de Solicitud",e);
		}
	
	}
	
	public String devolverDescripcionCampo(String filtro, String tipoBusqueda)
	{
		String resultado="";
		
		if (tipoBusqueda.equals("tipoRegistro"))
		{
			if (filtro.equals(ConstantesVisado.APODERADO) || filtro.equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO))
			{
				resultado="Apoderado";
			}
			else
			{
				resultado="Poderdante";
			}
		}
		else if (tipoBusqueda.equals("tipoDOI"))
		{
			for (TipoDocumento tmp: combosMB.getLstTipoDocumentos())
			{
				if (tmp.getCodTipoDoc().trim().equals(filtro))
				{
					resultado = tmp.getDescripcion();
					break;
				}
			}
		}
		else if (tipoBusqueda.equals("estado"))
		{
			for (Estado tmp: combosMB.getLstEstado())
			{
				if (tmp.getCodEstado().trim().equals(filtro))
				{
					resultado = tmp.getDescripcion();
					break;
				}
			}
		}
		
		return resultado;
	}
	
	public String obtenerRutaExcel()
	{
		String res="";
		
		TiivsParametros tmp = pdfViewerMB.getParametros();
		res = tmp.getRutaArchivoExcel();
		
//		for (TiivsParametros tmp: pdfViewerMB.getLstParametros())
//		{
//			if (usuario.getUID().equals(tmp.getCodUsuario()))
//			{
//				res=tmp.getRutaArchivoExcel();
//				break;
//			}
//		}
		
		if (res.compareTo("")==0)
		{
			logger.debug("No se encontro el parametro de ruta para exportar excel para el usuario: " + usuario.getUID());
		}
		else
		{
			logger.debug("Parametro ruta encontrada para el usuario: " + usuario.getUID() + " es: " +  res);
		}
		
		return res;
	}
	
	//@Samira 09/03/2012
	public void revocarApodPod() 
	{
		int tamanioApod = revocadoEdit.getApoderados().size();
		int tamanioPoder = revocadoEdit.getPoderdantes().size();
		logger.debug("====revocarApodPod() == ");
		logger.debug("tamanioApod:"+tamanioApod);
		logger.debug("tamanioPoder:"+tamanioPoder);
		if (tamanioApod>0 && tamanioPoder>0)
		{
			try {
				logger.info("**************** revocarActivar  ***************************" );
				
				//eliminarRevocadoBD();
				
				/** Actualizar el estado de la Revocacion a Activo*/
				this.actualizarEstadoRevocacionActivo();
			
			    String[] values  =new String[7];
				values[0]=ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02;
				values[1]=ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02;
				values[2]=ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02;
				values[3]=ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02;
				
				values[4]=ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02;
				values[5]=ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02;
				values[6]=ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02;
				
				List<TiivsSolicitudAgrupacionId> listaTiivsSolicitudAgrupacionId=new ArrayList<TiivsSolicitudAgrupacionId>();
				List<TiivsSolicitud> listaSolicitudesDeDondeComparar=new ArrayList<TiivsSolicitud>();
				GenericDao<TiivsSolicitud, Object> serviceSolicitud = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				Busqueda filtro = Busqueda.forClass(TiivsSolicitud.class);
				filtro.add(Restrictions.in("estado", values));
				listaSolicitudesDeDondeComparar = serviceSolicitud.buscarDinamico(filtro);
				logger.info("******** Tamanio de la lista de Solicitudes donde buscar ************* "+listaSolicitudesDeDondeComparar.size());
				
				List<String> listaCodSolicitudes = new ArrayList<String>();
				for (TiivsSolicitud e : listaSolicitudesDeDondeComparar) {
					listaCodSolicitudes.add(e.getCodSoli());
				}
				
				List<TiivsAgrupacionPersona> listaAgrupacionPersonacontraQuienComparar=new ArrayList<TiivsAgrupacionPersona>();
				GenericDao<TiivsAgrupacionPersona, Object> serviceSolicitudAgrupacionPersona = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				Busqueda filtroAgrupacionPersona = Busqueda.forClass(TiivsAgrupacionPersona.class);
				filtroAgrupacionPersona.add(Restrictions.in("codSoli", listaCodSolicitudes));
				listaAgrupacionPersonacontraQuienComparar=serviceSolicitudAgrupacionPersona.buscarDinamico(filtroAgrupacionPersona);
			
				logger.info("******** Tamanio de la lista de Agrupacion Personas contra quien comparar ************* "+listaAgrupacionPersonacontraQuienComparar.size());
				logger.info("******** Tamanio de la lista de Solicitudes donde buscar ************* "+listaSolicitudesDeDondeComparar.size());
				
				///revocadoEdit.get
				/** Pasar Revocados a lista de ComboDto y comparar */
				List<ComboDto> lstRevocadosComboDto = new ArrayList<ComboDto>();
	//			for (TiivsPersona a : revocadoEdit.getApoderados()) {
	//				lstRevocadosComboDto.add(new ComboDto(a.getCodPer()+"", ConstantesVisado.APODERADO));
	//			}
	//			for (TiivsPersona b : revocadoEdit.getPoderdantes()) {
	//				lstRevocadosComboDto.add(new ComboDto(b.getCodPer()+"", ConstantesVisado.PODERDANTE));
	//			}
				
				for (TiivsRevocado a : revocadoEdit.getApoderados()) {
					lstRevocadosComboDto.add(new ComboDto(a.getTiivsPersona().getCodPer()+"", ConstantesVisado.APODERADO));
					logger.info("Apoderados ::: " +lstRevocadosComboDto.size());
				}
				for (TiivsRevocado b : revocadoEdit.getPoderdantes()) {
					lstRevocadosComboDto.add(new ComboDto(b.getTiivsPersona().getCodPer()+"", ConstantesVisado.PODERDANTE));
					logger.info("Poderdartes ::: " +lstRevocadosComboDto.size());
				}
				logger.info("******** Tamanio de la lista de Solicitu ********** ::: " +lstRevocadosComboDto.size());
				List<ComboDto> listaPersonasXAgrupacionXSolicitud=null;
				List<Integer> listaStringCodAgrupacionesXCodSol=null;
				List<ComboDto> listaPersonas=null;
				 ComboDto combo=null;
				 List<ComboDto> listaNum_ListaPersonas;
				
				 for (TiivsSolicitud a : listaSolicitudesDeDondeComparar) {
					listaPersonasXAgrupacionXSolicitud =new ArrayList<ComboDto>();
					for (TiivsAgrupacionPersona x : listaAgrupacionPersonacontraQuienComparar) {
						if(a.getCodSoli().equals(x.getCodSoli())){
							logger.info("Codigo Solicitud - 1 - ::::: " +a.getCodSoli() + " x.getCodPer()::: " +x.getCodPer() +" x.getTipPartic() ::: " +x.getTipPartic() +" x.getNumGrupo():: " +x.getNumGrupo());
							listaPersonasXAgrupacionXSolicitud.add(new ComboDto(x.getCodPer().toString(),x.getTipPartic(),x.getNumGrupo()));
						}
					}
					logger.info("Tamanio listaPersonasXAgrupacionXSolicitud " +listaPersonasXAgrupacionXSolicitud.size());
				    listaStringCodAgrupacionesXCodSol=new ArrayList<Integer>();
					listaStringCodAgrupacionesXCodSol=existe(listaPersonasXAgrupacionXSolicitud);
					listaNum_ListaPersonas =new ArrayList<ComboDto>();
					     
					for (Integer w : listaStringCodAgrupacionesXCodSol) {
						listaPersonas=new ArrayList<ComboDto>();
						for (ComboDto v : listaPersonasXAgrupacionXSolicitud) {
							if(w==v.getNumGrupo()){
								listaPersonas.add(v);
							}
						}
						combo = new ComboDto(w,listaPersonas);
						listaNum_ListaPersonas.add(combo);
					}
					a.setListaNum_ListaPersonas(listaNum_ListaPersonas);
					int i=0;
					for (ComboDto m : listaNum_ListaPersonas) {
						if(m.getListaPersonas().size()==lstRevocadosComboDto.size()){
						    for (ComboDto comboDto : m.getListaPersonas()) {
								logger.debug("###### comboDto.getKey()  " +  comboDto.getKey() +" %%%%% " +comboDto.getDescripcion() );
							}
							for (ComboDto comboDto : lstRevocadosComboDto) {
								logger.debug("&&&&&&& comboDto.getKey()  " +  comboDto.getKey() +" T_T " +comboDto.getDescripcion() );
							}
							i = 0;
							for (ComboDto an : m.getListaPersonas()) {
								for (ComboDto bn : lstRevocadosComboDto) {
									
									if(an.getKey().equals(bn.getKey()) && an.getDescripcion().equals(bn.getDescripcion()) ){
											//logger.info("Una combinacion es igual " + an.getNumGrupo());
												i++;
											}
									
								}
							}
							
							//logger.info("Contador ::: " +i);
							
							if(i==lstRevocadosComboDto.size()){
								logger.info("Toda la combinación es igual a la de Revocado Num Grupo : " + m.getNumGrupo());
								listaTiivsSolicitudAgrupacionId.add(new TiivsSolicitudAgrupacionId(a.getCodSoli(), m.getNumGrupo()));
								
							}
							
						}
					}
				
			}
			
			/** Por ultimo modificar el estado de las combinaciones iguales, pasarlas al estado Revocados */
			this.actualizarEstadoA_Revocado_SolicitudAgrupacion(listaTiivsSolicitudAgrupacionId);
			bBooleanPopup=false;
			Utilitarios.mensajeInfo("INFO", "La Revocación fue ejecutada, correctamente");
			buscarRevocado();
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en revocados:",  e);
			}
		}	
		else{
			bBooleanPopup=false;
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se puede revocar, debe completar los datos de representado y/o representante!","");
			FacesContext.getCurrentInstance().addMessage("msmGrowlPopup", msg);
			logger.debug("No se grabo la combinacion de revocados");
			buscarRevocado();
		}
	}
	
	//@Samira 09/03/2012
	public void actualizarEstadoRevocacionActivo() throws Exception{
		List<TiivsRevocado> listaTiivsRevocado=new ArrayList<TiivsRevocado>();
		GenericDao<TiivsRevocado, Object> serviceTiivsRevocado = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsRevocado = Busqueda.forClass(TiivsRevocado.class);
		filtroTiivsRevocado.add(Restrictions.eq("codAgrup", Integer.parseInt(revocadoEdit.getCodAgrupacion())));
		listaTiivsRevocado=serviceTiivsRevocado.buscarDinamico(filtroTiivsRevocado);
		logger.info("Tamanio de listaTiivsRevocado " +listaTiivsRevocado.size());
		for (TiivsRevocado yy : listaTiivsRevocado) {
			yy.setEstado(ConstantesVisado.ESTADOS.ESTADO_ACTIVO_REVOCADO);
			serviceTiivsRevocado.modificar(yy);
		}
	}
	//@Samira 09/03/2012
	public void actualizarEstadoA_Revocado_SolicitudAgrupacion(List<TiivsSolicitudAgrupacionId> listaTiivsSolicitudAgrupacionId) throws Exception{
		
		for (TiivsSolicitudAgrupacionId x : listaTiivsSolicitudAgrupacionId) {
			logger.debug(x.toString());
		}
		if(listaTiivsSolicitudAgrupacionId.size()>0){
		List<TiivsSolicitudAgrupacion> listaTiivsSolicitudAgrupacion=new ArrayList<TiivsSolicitudAgrupacion>();
		GenericDao<TiivsSolicitudAgrupacion, Object> serviceSolicitudAgrupacionPersona = (GenericDao<TiivsSolicitudAgrupacion, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSolicitudAgrupacion = Busqueda.forClass(TiivsSolicitudAgrupacion.class);
		filtroSolicitudAgrupacion.add(Restrictions.in("id", listaTiivsSolicitudAgrupacionId));
		listaTiivsSolicitudAgrupacion=serviceSolicitudAgrupacionPersona.buscarDinamico(filtroSolicitudAgrupacion);
		for (TiivsSolicitudAgrupacion xx : listaTiivsSolicitudAgrupacion) {
			xx.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_3);
			xx=serviceSolicitudAgrupacionPersona.modificar(xx);
		}
		}
		/** Luego validar si se cambiara el estado de la Solicitud */
		this.validarSolicitudesSiTodasCombinacionesRevocadas();
	}
	//@Samira 09/03/2012
	public void validarSolicitudesSiTodasCombinacionesRevocadas() throws Exception{
		List<String> solicitudes = new ArrayList<String>();
		SolicitudDao<TiivsSolicitud, Object> service = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		solicitudes=service.obtenerCodigosSolicitudesARevocarRechazar();	
			/** Actualizar Estado Solicitudes**/
		if(solicitudes.size()>0){
			this.actualizarEstadoSolicitudes(solicitudes);
		}  
	}
	
	//@Samira 09/03/2012
	public void actualizarEstadoSolicitudes(List<String> solicitudes) throws Exception{
		logger.info("===== actualizarEstadoSolicitudes() ====");

		 /**Insertando la GLosa La solicitud ha sido revocada ... desde base de datos*/
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultitabla.add(Restrictions.eq("id.codMult", "T16"));
		filtroMultitabla.add(Restrictions.eq("id.codElem", "0001"));
		TiivsMultitabla parametroGlosa = new TiivsMultitabla();
		parametroGlosa=multiDAO.buscarDinamico(filtroMultitabla).get(0);
		
		logger.info("parametroGlosa " +parametroGlosa.getValor1());
		    String[] codigosParaPasarSolicitudRechazada  =new String[4];
		    String[] codigosParaPasarSolicitudImprocedente  =new String[3];
		    codigosParaPasarSolicitudRechazada[0]=ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02;
			codigosParaPasarSolicitudRechazada[1]=ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02;
			codigosParaPasarSolicitudRechazada[2]=ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02;
			codigosParaPasarSolicitudRechazada[3]=ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02;
			
			codigosParaPasarSolicitudImprocedente[0]=ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02;
			codigosParaPasarSolicitudImprocedente[1]=ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02;
			codigosParaPasarSolicitudImprocedente[2]=ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02;
			
		String codigoRetorno="";	
		
		List<TiivsSolicitud> listaTiivsSolicitudRechazdas=new ArrayList<TiivsSolicitud>();
		List<TiivsSolicitud> listaTiivsSolicitudImprocedentes=new ArrayList<TiivsSolicitud>();
		GenericDao<TiivsSolicitud, Object> serviceSolicitudAgrupacionPersona = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSolicitudRechazado = Busqueda.forClass(TiivsSolicitud.class);
		filtroSolicitudRechazado.add(Restrictions.in("estado", codigosParaPasarSolicitudRechazada));
		filtroSolicitudRechazado.add(Restrictions.in("codSoli", solicitudes));
		listaTiivsSolicitudRechazdas=serviceSolicitudAgrupacionPersona.buscarDinamico(filtroSolicitudRechazado);
		for (TiivsSolicitud xx : listaTiivsSolicitudRechazdas) {
			xx.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
			xx.setFechaEstado(new Timestamp(new Date().getTime()));
			xx.setObs(parametroGlosa.getValor1());
			xx=serviceSolicitudAgrupacionPersona.modificar(xx);
			registraSolicitudHistorial(xx);
			codigoRetorno+=xx.getCodSoli()+".  ";
		}
		
		Busqueda filtroSolicitudImprocedente = Busqueda.forClass(TiivsSolicitud.class);
		filtroSolicitudImprocedente.add(Restrictions.in("estado", codigosParaPasarSolicitudImprocedente));
		filtroSolicitudImprocedente.add(Restrictions.in("codSoli", solicitudes));
		listaTiivsSolicitudImprocedentes=serviceSolicitudAgrupacionPersona.buscarDinamico(filtroSolicitudImprocedente);
		for (TiivsSolicitud xx : listaTiivsSolicitudImprocedentes) {
			xx.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02);
			xx.setFechaEstado(new Timestamp(new Date().getTime()));
			xx.setObs(parametroGlosa.getValor1());
			xx=serviceSolicitudAgrupacionPersona.modificar(xx);
			registraSolicitudHistorial(xx);
			codigoRetorno+=xx.getCodSoli()+".  ";
		}

		if(listaTiivsSolicitudRechazdas.size()>0||listaTiivsSolicitudImprocedentes.size()>0){
			Utilitarios.mensajeInfo("INFO", "La(s) Solicitud(es) Revocada(s) es(son) : "+codigoRetorno);
		}else{
			Utilitarios.mensajeInfo("INFO", "La Revocación fue ejecutada correctamente ..");
		}
	}
	
	public void registraSolicitudHistorial (TiivsSolicitud solicitudModificado) throws Exception
	{
		  GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		  SolicitudDao<String, Object> serviceMaxMovi = (SolicitudDao<String, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		  String numeroMovimiento = serviceMaxMovi.obtenerMaximoMovimiento(solicitudModificado.getCodSoli());
	
		  int num = 0;
		  if (!numeroMovimiento.equals("")) 
		  {
			  num = Integer.parseInt(numeroMovimiento) + 1;
		  } 
		  else 
		  {
			  num = 1;
		  }
		  	
		  numeroMovimiento = num + "";
		  TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
		  objHistorial.setId(new TiivsHistSolicitudId(solicitudModificado.getCodSoli(),numeroMovimiento));
		  objHistorial.setEstado(solicitudModificado.getEstado());
		  //[24-10] Se agrega nombreCompleto
		  String nombreCompleto="".concat(usuario.getNombre()!=null?usuario.getNombre():"")
					.concat(" ").concat(usuario.getApellido1()!=null?usuario.getApellido1():"")
					.concat(" ").concat(usuario.getApellido2()!=null?usuario.getApellido2():"");
		  objHistorial.setNomUsuario(nombreCompleto);
		  objHistorial.setObs(solicitudModificado.getObs());
		  objHistorial.setFecha(new Timestamp(new Date().getTime()));
		  objHistorial.setRegUsuario(usuario.getUID());
		  
		  /**SAMIRA MEJORA 29/04/2013 */
		  objHistorial.setFlagRevocado(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO);
		  serviceHistorialSolicitud.insertar(objHistorial);
	}
	
	//@Samira 09/03/2012
	public List<String>listaCodSoli(List<TiivsSolicitudAgrupacion> listaTiivsSolicitudAgrupacion)throws Exception{
		List<String>listaCodSoli =new ArrayList<String>();
		for(int i=0;i<listaTiivsSolicitudAgrupacion.size();i++){
            for(int j=0;j<listaTiivsSolicitudAgrupacion.size()-1;j++){
                if(i!=j){
                	if(listaTiivsSolicitudAgrupacion.get(i)!=null&&listaTiivsSolicitudAgrupacion.get(j)!=null){
                    if(listaTiivsSolicitudAgrupacion.get(i).getId().getCodSoli().equals
                    		(listaTiivsSolicitudAgrupacion.get(j).getId().getCodSoli())){
                    	listaTiivsSolicitudAgrupacion.set(j, null);
                    }
                	}
                }
            }
        }  
	  
	  for (TiivsSolicitudAgrupacion obj : listaTiivsSolicitudAgrupacion) {
        	if(obj!=null){
        		listaCodSoli.add(obj.getId().getCodSoli());
        	}
        	}
	  logger.info("Tamanio de la lista a Devolver listaCodSoli " +listaCodSoli.size());
	  return listaCodSoli;
	}
	//@Samira 09/03/2012
	public List<Integer> existe(List<ComboDto> listaTemporal2){
		           List<Integer> listaTemporal=new ArrayList<Integer>();
		           List<Integer> arraycar=new ArrayList<Integer>();
		           for (ComboDto c : listaTemporal2) {
				    	  arraycar.add(c.getNumGrupo());
				   }
			
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
	
/*	public void revocarApodPod(){
		logger.info("******************************* revocarApodPod ***************************");
		List<TiivsAgrupacionPersona>  agrupacionPersonas= new ArrayList<TiivsAgrupacionPersona>();
		GenericDao<TiivsAgrupacionPersona, Object> service = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitud, Object> service2 = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		
		int sizeListas=0;
		int contadorPoderdantes=0;
		int contadorApoderados=0;
		int contadorCombinacion=0;
		
		List<CombinacionSolicitudUtil> combinacionSolicitudUtils= new ArrayList<CombinacionSolicitudUtil>();
		CombinacionSolicitudUtil combinacionSolicitudUtil= new CombinacionSolicitudUtil(); 
		
		for(String numCodSoli:listCodSoli){
			
			contadorCombinacion=0;
			for(Integer numGrupo:listNumGrupo){
			
			try{
				Busqueda filtro2 = Busqueda.forClass(TiivsAgrupacionPersona.class);
				agrupacionPersonas = service.buscarDinamico(filtro2.add(Restrictions.eq("codSoli", numCodSoli)).add(Restrictions.eq("numGrupo", numGrupo)));
				sizeListas = agrupacionPersonas.size();
			}catch(Exception e){
				
				logger.error("error al obtener el total de agrupacion personas" + e.getMessage());
			}
			
			for(TiivsAgrupacionPersona  agrupacionPersona: agrupacionPersonas ){
				
				for(Revocado revocado:personaClientesPendEdit){
					
					if(agrupacionPersona.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
						for(TiivsPersona tiivsPersona: revocado.getPoderdantes()){
							
							if(agrupacionPersona.getCodPer().equals(tiivsPersona.getCodPer())){
								contadorPoderdantes++;
							}
							
						}
					}
					
					if(agrupacionPersona.getTipPartic().equals(ConstantesVisado.APODERADO)){
						for(TiivsPersona tiivsPersona: revocado.getApoderados()){
							
							if(agrupacionPersona.getCodPer().equals(tiivsPersona.getCodPer())){
								contadorApoderados++;
							}
						}
					}
					
				}
				
			}
		
			int contadorTotalxGrupo = contadorPoderdantes + contadorApoderados;
			
			if(contadorTotalxGrupo!=0){
				if(sizeListas == contadorTotalxGrupo){
					contadorCombinacion++;
				}
			}
			
			if(contadorCombinacion!=0){
				combinacionSolicitudUtil.setCodSoli(numCodSoli);
				combinacionSolicitudUtil.setNumCombinaciones(contadorCombinacion);
				try {
					TiivsSolicitud tiivsSolicitud = service2.buscarById(TiivsSolicitud.class, numCodSoli);
					combinacionSolicitudUtil.setEstado(tiivsSolicitud.getEstado());
				} catch (Exception e) {
					
					logger.error("error al obtener el estado de la solicitud" + e.getMessage());
				}
				
				combinacionSolicitudUtil.setNumGrupo(numGrupo);
				combinacionSolicitudUtils.add(combinacionSolicitudUtil);
			}
			
			
			
		  }
			
		 
		}
		
		
		GenericDao<TiivsSolicitudAgrupacion, Object> serviceSolicAgrup = (GenericDao<TiivsSolicitudAgrupacion, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsSolicitudAgrupacion.class);
		
		listSolicResult = new ArrayList<String>();
		
		for(CombinacionSolicitudUtil solicitudUtil:combinacionSolicitudUtils){
			
			TiivsSolicitudAgrupacionId solicitudAgrupacionId= new TiivsSolicitudAgrupacionId(solicitudUtil.getCodSoli(),solicitudUtil.getNumGrupo());
			TiivsSolicitudAgrupacion solicitudAgrupacion= new TiivsSolicitudAgrupacion();
			
			try {
				solicitudAgrupacion = serviceSolicAgrup.buscarById(TiivsSolicitudAgrupacion.class, solicitudAgrupacionId);
			} catch (Exception e) {
				logger.error("error al obtener el estado de la solicitud" + e.getMessage());
			
			}
			
			if(solicitudUtil.getCodSoli()!="" && solicitudUtil.getCodSoli()!=null){
				
				if(solicitudUtil.getNumCombinaciones()==1 && 
						(  solicitudUtil.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02)
						|| solicitudUtil.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02)
						|| solicitudUtil.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)
						|| solicitudUtil.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02))){
					
					
					try {
						//Estado Revocado
						solicitudAgrupacion.setEstado(3+"");
						serviceSolicAgrup.modificar(solicitudAgrupacion);
						
						TiivsSolicitud tiivsSolicitud = service2.buscarById(TiivsSolicitud.class, solicitudUtil.getCodSoli());
						tiivsSolicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
						tiivsSolicitud.setObs("La solicitud ha sido revocada porque su(s) apoderados(s) ha(n) sido revocado(s).");
						
						service2.modificar(tiivsSolicitud);
						
						listSolicResult.add(solicitudUtil.getCodSoli());
						
					} catch (Exception e) {
						logger.error("error al modificar a rechazadao la solicitud con grupo" + e.getMessage());
					
					}
			
					
				}
				
				
				if(solicitudUtil.getNumCombinaciones()==1 && 
						(  solicitudUtil.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)
						|| solicitudUtil.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)
						|| solicitudUtil.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02)
						)){
					
					try {
						//Estado Revocado
						solicitudAgrupacion.setEstado(3+"");
						serviceSolicAgrup.modificar(solicitudAgrupacion);
						
						
						TiivsSolicitud tiivsSolicitud = service2.buscarById(TiivsSolicitud.class, solicitudUtil.getCodSoli());
						tiivsSolicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02);
						tiivsSolicitud.setObs("La solicitud ha sido revocada porque su(s) apoderados(s) ha(n) sido revocado(s).");
						
						
						service2.modificar(tiivsSolicitud);
						
						listSolicResult.add(solicitudUtil.getCodSoli());
						
					} catch (Exception e) {
						logger.error("error al modificar a rechazadao la solicitud con grupo" + e.getMessage());
					
					}
					
				}
				
				if(solicitudUtil.getNumCombinaciones()>1){
					
					try {
						//Estado Revocado
						solicitudAgrupacion.setEstado(3+"");
						serviceSolicAgrup.modificar(solicitudAgrupacion);
						
						
						TiivsSolicitud tiivsSolicitud = service2.buscarById(TiivsSolicitud.class, solicitudUtil.getCodSoli());
						tiivsSolicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_T02);
						tiivsSolicitud.setObs("");
						
						service2.modificar(tiivsSolicitud);
						
						listSolicResult.add(solicitudUtil.getCodSoli());
						
					} catch (Exception e) {
						logger.error("error al modificar a rechazadao la solicitud con grupo" + e.getMessage());
					
					}
					
				}
				
			}
			
		}
		
		if(listSolicResult.size()==0){
			setMsjResultRevocados("No hay solicitudes actualizadas con los apoderados/poderdantes especificados. ");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Informacion", getMsjResultRevocados());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}else{
			
			String msj="";
			
			for(String s:listSolicResult){
				msj= msj + s + ",";
			}
			setMsjResultRevocados("Los numeros de solicitudes actualizados son: "+ msj);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Informacion", getMsjResultRevocados());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		
	}*/
	
	public void cargarCombos(){
		
		
		
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgrupPers = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro_ = Busqueda.forClass(TiivsAgrupacionPersona.class).setProjection(Projections.distinct(Projections.property("numGrupo")));
		
		try {
			listNumGrupo = serviceAgrupPers.buscarDinamicoInteger(filtro_.addOrder(Order.desc("numGrupo")));
		} catch (Exception e) {
			
			logger.debug("error al obtener la lista de cod de agrupacion "+  e.toString());
		}
		
		Busqueda filtro_2 = Busqueda.forClass(TiivsAgrupacionPersona.class).setProjection(Projections.distinct(Projections.property("codSoli")));
		
		try {
			listCodSoli = serviceAgrupPers.buscarDinamicoString(filtro_2.addOrder(Order.desc("codSoli")));
		} catch (Exception e) {
			
			logger.debug("error al obtener la lista de cod de agrupacion "+  e.toString());
		}

		GenericDao<TiivsMultitabla, Object> serviceMul = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro3 = Busqueda.forClass(TiivsMultitabla.class);
		filtro3.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC));
		
		try {
			listDocumentos = serviceMul.buscarDinamico(filtro3);
		} catch (Exception e) {
			
			logger.debug("error al obtener la lista de cod de agrupacion "+  e.toString());
		}
		
		Busqueda filtro4 = Busqueda.forClass(TiivsMultitabla.class);
		filtro4.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		
		try {
			listEstados = serviceMul.buscarDinamico(filtro4);
		} catch (Exception e) {
			logger.debug("error al obtener la lista de estados "+  e.toString());
		}
		
		Busqueda filtro5 = Busqueda.forClass(TiivsMultitabla.class);
		filtro5.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA)).addOrder(Order.asc("valor1"));
		
		try {
			listTipoRegistro = serviceMul.buscarDinamico(filtro5);
		} catch (Exception e) {
			logger.debug("error al obtener la lista de registros "+  e.toString());
		}
		
		for (TiivsMultitabla tmpLista: listTipoRegistro)
		{
			if (tmpLista!=null)
			{
				if (tmpLista.getValor1()!=null)
				{
					String varText = tmpLista.getValor1().toUpperCase();
					tmpLista.setValor1(varText);
				}
			}
		}
		
		
		String[] codEstadosRevocados= new String[3];
		codEstadosRevocados[0] = ConstantesVisado.ESTADOS.ESTADO_ACTIVO_REVOCADO;
		codEstadosRevocados[1] = ConstantesVisado.ESTADOS.ESTADO_INACTIVO_REVOCADO;
		codEstadosRevocados[2] = ConstantesVisado.ESTADOS.ESTADO_PENDIENTE_REVOCADO;
		
		
		Busqueda filtro6 = Busqueda.forClass(TiivsMultitabla.class);
		filtro6.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		filtro6.add(Restrictions.in("id.codElem", codEstadosRevocados));
		
		try {
			listEstadoRevocados = serviceMul.buscarDinamico(filtro6);
		} catch (Exception e) {
			
			logger.debug("error al obtener la lista de cod de agrupacion "+  e.toString());
		}
		
		for (TiivsMultitabla tmpLista: listEstadoRevocados)
		{
			if (tmpLista!=null)
			{
				if (tmpLista.getValor1()!=null)
				{
					String varText = tmpLista.getValor1().toUpperCase();
					tmpLista.setValor1(varText);
				}
			}
		}
		
		
	}

	
	
	public void verRevocado() {

		personaClientesVer = new ArrayList<Revocado>();
		personaClientesVer.add(revocadoVer);
		sAliasTemporal = null;
		aliasCortoDocumento = null;
		
		// Se agrega condición para mostrar link de descarga de archivo de revocados
		if(revocadoVer.getAliasArchivo()!=null){
			flagLinkRevocados = 1;
			String extension = revocadoVer.getAliasArchivo().substring(revocadoVer.getAliasArchivo().lastIndexOf("."));
			sAliasTemporal = Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA) + extension;
			aliasCortoDocumento = revocadoVer.getAliasArchivo();
		}
	}
	
	public void editPendRevocado() {
		logger.info("******************** editPendRevocado ************" );
		
		objTiivsPersonaBusquedaDlg= new TiivsPersona();
		objTiivsPersonaAgregar = new TiivsPersona();

		personaClientesPendEdit = new ArrayList<Revocado>();
		personaClientesPendEdit.add(revocadoEdit);
		
		setFlagRevocar(true);
		setFlagGuardar(false);
		
		// Se agregaron lineas
		String extension = null;
		if(revocadoEdit.getAliasArchivo()!=null){
			extension = revocadoEdit.getAliasArchivo().substring(revocadoEdit.getAliasArchivo().lastIndexOf("."));
			flagLinkRevocados = 1;
			sAliasTemporal = Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA) + extension;
			aliasCortoDocumento = revocadoEdit.getAliasArchivo();	
		}else{
			flagLinkRevocados = 0;
			sAliasTemporal = null;
			aliasCortoDocumento = null;
		}
	}
	
	/**
	 * Metodo invocado al seleccionar el botón 'Nuevo' desde la 
	 * bandeja de consultas de revocados. Se inicializan los objetos
	 * para el registro de una combinación de revocados.
	 * **/
	public void editPendRevocadoNuevo() {
		logger.debug("== editPendRevocadoNuevo() ===");
		objTiivsPersonaBusquedaDlg= new TiivsPersona();
		objTiivsPersonaAgregar = new TiivsPersona();

		personaClientesPendEdit = new ArrayList<Revocado>();
//		apoderadosNuevo = new ArrayList<TiivsPersona>();
//		poderdantesNuevo = new ArrayList<TiivsPersona>();
		
		apoderadosNuevo = new ArrayList<TiivsRevocado>();
		poderdantesNuevo = new ArrayList<TiivsRevocado>();
		
		setFlagRevocar(false);
		setFlagGuardar(true);
		
		// Se agregan lineas de abajo
		flagLinkRevocados = 0;
		sAliasTemporal = null;
		aliasCortoDocumento = null;

		visadoDocumentosMB.setDocumentosLeer(Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA) + ConstantesVisado.EXTENSION_PDF);
	}
	
	public void editActRevocado() {

		personaClientesActivoEdit = new ArrayList<Revocado>();
		personaClientesActivoEdit.add(revocadoEdit);
		
		// Se agregan lineas
		String extension = null;
		if(revocadoEdit.getAliasArchivo()!=null){
			extension = revocadoEdit.getAliasArchivo().substring(revocadoEdit.getAliasArchivo().lastIndexOf("."));
			flagLinkRevocados = 1;
			sAliasTemporal = Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA) + extension;
			aliasCortoDocumento = revocadoEdit.getAliasArchivo();	
		}else{
			flagLinkRevocados = 0;
			sAliasTemporal = null;
			aliasCortoDocumento = null;
		}
	}
	
	public void obtenerPersonaSeleccionada() {
		logger.info(objTiivsPersonaSeleccionado.getCodPer());
		this.objTiivsPersonaAgregar = this.objTiivsPersonaSeleccionado;
	}
	
	
	public void  buscarPersona() {
		
		List<TiivsPersona> lstTiivsPersonaNuevo = new ArrayList<TiivsPersona>();
		personaDataModal = new PersonaDataModal(lstTiivsPersonaNuevo);
		
		try {
			List<TiivsPersona> lstTiivsPersonaLocal = new ArrayList<TiivsPersona>();
			lstTiivsPersonaLocal = this.buscarPersonaLocal();
			logger.info("lstTiivsPersonaLocal  "+ lstTiivsPersonaLocal.size());
			
			List<TiivsPersona> lstTiivsPersonaReniec = new ArrayList<TiivsPersona>();
			
			if (lstTiivsPersonaLocal.size() == 0) {
				
				lstTiivsPersonaReniec = this.buscarPersonaReniec();
				
				if (lstTiivsPersonaReniec.size() == 0) {
					
					objTiivsPersonaAgregar = new TiivsPersona();
					this.bBooleanPopup = false;
					if ((objTiivsPersonaBusquedaDlg.getTipDoi() == null 
							  || objTiivsPersonaBusquedaDlg.getTipDoi().equals("")) 
							 || (objTiivsPersonaBusquedaDlg.getNumDoi() == null
							  || objTiivsPersonaBusquedaDlg.getNumDoi().equals(""))) {
					}else{
						Utilitarios.mensajeInfo("INFO","No se encontro resultados para la busqueda.");
					}
					
					
				} else if (lstTiivsPersonaReniec.size() == 1) {
					this.bBooleanPopup = false;
					objTiivsPersonaAgregar = lstTiivsPersonaReniec.get(0);
					
				} else if (lstTiivsPersonaReniec.size() > 1) {
					this.bBooleanPopup = true;
					personaDataModal = new PersonaDataModal(lstTiivsPersonaReniec);
				}
				
			} else if (lstTiivsPersonaLocal.size() == 1) {
				this.bBooleanPopup = false;
				objTiivsPersonaAgregar = lstTiivsPersonaLocal.get(0);
				
			} else if (lstTiivsPersonaLocal.size() > 1) {
				this.bBooleanPopup = true;
				personaDataModal = new PersonaDataModal(lstTiivsPersonaLocal);
			} else {
				this.bBooleanPopup = true;
			}		
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al buscarPersona():",  e);
			Utilitarios.mensajeError("ERROR", e.getMessage());
		}
	}
	
	public void deleteCombinacion(ActionEvent event){		
		logger.info("********************** deleteCombinacion *********************************** ");
		List<TiivsRevocado> tiivsrevocados= new ArrayList<TiivsRevocado>();
		List<TiivsRevocado> tiivsrevocados2= new ArrayList<TiivsRevocado>();
		
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		String codAgrup= getRevocadoDelete().getCodAgrupacion();
		
		try {
			filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(codAgrup)));
			tiivsrevocados = service.buscarDinamico(filtro);
			
			for(TiivsRevocado revocado:tiivsrevocados){
				service.eliminar(revocado);
			}			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al deleteCombinacion():",  e);
		}
		
		buscarRevocado();
	}
	
	public void limpiarCriteriosBusqueda() {
		logger.debug("==limpiarCriteriosBusqueda==");
//		objTiivsPersonaBusquedaDlg.setCodCen("");
//		objTiivsPersonaBusquedaDlg.setCodCen("");
//		objTiivsPersonaAgregar.setClasifPer("");
//		objTiivsPersonaAgregar.setClasifPerOtro("");
//		objTiivsPersonaAgregar.setEmail("");
//		objTiivsPersonaAgregar.setNumCel("");
		
		objTiivsPersonaBusquedaDlg= new TiivsPersona();
		objTiivsPersonaAgregar = new TiivsPersona();
		
		//Se comenta 3 lineas de abajo 29/10/2013
		/*fileUpload = null;
		aliasCortoDocumento = null;
		sAliasTemporal = null;*/
		
		//objTiivsPersonaBusquedaDlg.setTipDoi("");
		//objTiivsPersonaBusquedaDlg.setNumDoi("");
		
//		objTiivsPersonaAgregar.setTipDoi("");
//		objTiivsPersonaAgregar.setNumDoi("");
//		objTiivsPersonaAgregar.setCodCen("");
//		objTiivsPersonaAgregar.setApePat("");
//		objTiivsPersonaAgregar.setApeMat("");
//		objTiivsPersonaAgregar.setNombre("");
//		objTiivsPersonaAgregar.setTipPartic("");
	}
	
	
	public void agregarRevocado() {
		
		if (!validarRevocado()) {
			return;
		}
		
		if(!validarRegistroDuplicado()){
			return;
		}
		
		if(isFlagRevocar()==true)
		{
			logger.info("****************** agregarRevocado ********************");

			for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
				if (objTiivsPersonaAgregar.getTipDoi().equals(p.getCodTipoDoc())) {
					objTiivsPersonaAgregar.setsDesctipDoi(p.getDescripcion());
				}
			}
			for (ComboDto p : combosMB.getLstTipoRegistroPersona()) {
				if (objTiivsPersonaAgregar.getTipPartic().equals(p.getKey())) {
					objTiivsPersonaAgregar.setsDesctipPartic(p.getDescripcion());
				}
			}
			
			List<TiivsRevocado> tiivsrevocados= new ArrayList<TiivsRevocado>();
			
			GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
			
			try {
				tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(personaClientesPendEdit.get(0).getCodAgrupacion()))));
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar tiivsrevocados:",  e);
			}
			
			
			TiivsRevocado tiivsRevocadoAux= new TiivsRevocado();
			tiivsRevocadoAux = tiivsrevocados.get(0);
			
			tiivsRevocadoAux.setCodRevocado(0);
			tiivsRevocadoAux.setTiivsPersona(objTiivsPersonaAgregar);
			tiivsRevocadoAux.setTipPartic(objTiivsPersonaAgregar.getTipPartic());
			
			try {
				service.insertar(tiivsRevocadoAux);
				tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(personaClientesPendEdit.get(0).getCodAgrupacion()))));
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar tiivsrevocados2:",  e);
			}
					
			List<TiivsRevocado> apoderados= new ArrayList<TiivsRevocado>();
			List<TiivsRevocado> poderdantes= new ArrayList<TiivsRevocado>();
			
			List<Revocado> revocadosAux= new ArrayList<Revocado>();
				
			for(TiivsRevocado tiivsRevocado:tiivsrevocados)
			{
				if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO)){
					
					String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
					String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
					
					tiivsRevocado.setsDesctipDoi( descDoiApod);
					tiivsRevocado.setsDesctipPartic(descTipPart);
					
					apoderados.add(tiivsRevocado);
				}
				
				if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)){
					
					String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
					String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
					
					tiivsRevocado.setsDesctipDoi( descDoiApod);
					tiivsRevocado.setsDesctipPartic(descTipPart);
					
					apoderados.add(tiivsRevocado);
				}
					
				if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					
					String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
					String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
					
					tiivsRevocado.setsDesctipDoi( descDoiPod);
					tiivsRevocado.setsDesctipPartic(descTipPart);
					
					poderdantes.add(tiivsRevocado);
				}
			}
			
			Revocado revocado = new Revocado();
			revocado.setCodAgrupacion(personaClientesPendEdit.get(0).getCodAgrupacion());
			revocado.setApoderados(apoderados);
			revocado.setPoderdantes(poderdantes);
		
			revocadosAux.add(revocado);
		
			personaClientesPendEdit = revocadosAux;
			objTiivsPersonaSeleccionado = new TiivsPersona();
							
		}else{
			
			TiivsRevocado apoderado;
			TiivsRevocado poderdante;
			
			int maximoCodAgrupacion = getMaximoCodAgrupacion();
			List<Revocado> revocadosAux= new ArrayList<Revocado>();
			
			if(objTiivsPersonaAgregar.getTipPartic().equals(ConstantesVisado.APODERADO))
			{
				String descDoiApod =  getValor1(objTiivsPersonaAgregar.getTipDoi(),listDocumentos);
				String descTipPart =  getValor1(objTiivsPersonaAgregar.getTipPartic(), listTipoRegistro);
				
				apoderado = new TiivsRevocado();				
				apoderado.setTiivsPersona(objTiivsPersonaAgregar);
				apoderado.setTipPartic(objTiivsPersonaAgregar.getTipPartic());
				apoderado.setsDesctipDoi( descDoiApod);
				apoderado.setsDesctipPartic(descTipPart);			
				
				apoderadosNuevo.add(apoderado);
			}
			
			if(objTiivsPersonaAgregar.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO))
			{
				String descDoiApod =  getValor1(objTiivsPersonaAgregar.getTipDoi(),listDocumentos);
				String descTipPart =  getValor1(objTiivsPersonaAgregar.getTipPartic(), listTipoRegistro);
				
				apoderado = new TiivsRevocado();				
				apoderado.setTiivsPersona(objTiivsPersonaAgregar);
				apoderado.setTipPartic(objTiivsPersonaAgregar.getTipPartic());
				apoderado.setsDesctipDoi( descDoiApod);
				apoderado.setsDesctipPartic(descTipPart);			
				
				apoderadosNuevo.add(apoderado);
			}
							
			if(objTiivsPersonaAgregar.getTipPartic().equals(ConstantesVisado.PODERDANTE))
			{
				String descDoiPod =  getValor1(objTiivsPersonaAgregar.getTipDoi(),listDocumentos);
				String descTipPart =  getValor1(objTiivsPersonaAgregar.getTipPartic(), listTipoRegistro);
				
				poderdante = new TiivsRevocado();
				poderdante.setTiivsPersona(objTiivsPersonaAgregar);
				poderdante.setTipPartic(objTiivsPersonaAgregar.getTipPartic());
				poderdante.setsDesctipDoi( descDoiPod);
				poderdante.setsDesctipPartic(descTipPart);
				poderdantesNuevo.add(poderdante);
			}
			
			Revocado revocado = new Revocado();
			revocado.setCodAgrupacion((maximoCodAgrupacion+1)+"");
			revocado.setApoderados(apoderadosNuevo);
			revocado.setPoderdantes(poderdantesNuevo);
		
			revocadosAux.add(revocado);
			
			personaClientesPendEdit = revocadosAux;
			
			objTiivsPersonaAgregar = new TiivsPersona();
			objTiivsPersonaBusquedaDlg = new TiivsPersona();
			bBooleanPopup=true;
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	public void guardarApodPod(ActionEvent actionEvent)
	{	
		logger.info("=== guardarApodPod() ====");
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		int flag=0;
		logger.info("personaClientesPendEdit.size :::: " +personaClientesPendEdit.size());
		logger.info("+++ aliasCortoDocumento = " + aliasCortoDocumento);
		
		for(Revocado revocado: personaClientesPendEdit)
		{	
			int tamanioApod = revocado.getApoderados().size();
			int tamanioPode = revocado.getPoderdantes().size();
			logger.debug("tamanioApod:"+tamanioApod);
			logger.debug("tamanioPode:"+tamanioPode);
			
			if (tamanioApod>0 && tamanioPode>0)
			{
//				for(TiivsPersona tiivsPersona:revocado.getApoderados()){
				if(aliasCortoDocumento!=null){
					logger.debug("aliasCortoDocumento:"+aliasCortoDocumento);
					boolean flagArchivo = cargarDocumentoRevocatoria();
					logger.debug("flagArchivo: "+flagArchivo);
					if(!flagArchivo){
						flag=-1;
						break;
					}
				}
				
				for(TiivsRevocado tiivsRevocado:revocado.getApoderados())
				{			
//					tiivsRevocadoAux= new TiivsRevocado();
//					tiivsRevocadoAux.setCodRevocado(0);
//					tiivsRevocadoAux.setTiivsPersona(tiivsPersona);
//					tiivsRevocadoAux.setTipPartic(tiivsPersona.getTipPartic());
//					tiivsRevocadoAux.setFechaRevocatoria(new Date());
//					tiivsRevocadoAux.setEstado(ConstantesVisado.ESTADOS.ESTADO_PENDIENTE_REVOCADO);
//					tiivsRevocadoAux.setCodAgrup(Integer.parseInt(revocado.getCodAgrupacion()));
//					tiivsRevocadoAux= new TiivsRevocado();
					tiivsRevocado.setCodRevocado(0);
//					tiivsRevocado.setTipPartic(tiivsRevocado.getTipPartic());
					tiivsRevocado.setFechaRevocatoria(new Date());
					tiivsRevocado.setEstado(ConstantesVisado.ESTADOS.ESTADO_PENDIENTE_REVOCADO);
					tiivsRevocado.setCodAgrup(Integer.parseInt(revocado.getCodAgrupacion()));
					tiivsRevocado.setAliasArchivo(aliasCortoDocumento);
					
					try {
//						service.save(tiivsRevocadoAux);
						logger.debug("[REG-tiivsRevocado(Apod)]-codRevocado:"+tiivsRevocado.getCodRevocado());
						logger.debug("[REG-tiivsRevocado(Apod)]-codAgrup:"+tiivsRevocado.getCodAgrup());
						logger.debug("[REG-tiivsRevocado(Apod)]-aliasArchivo:"+tiivsRevocado.getAliasArchivo());
						service.save(tiivsRevocado);
						flag=1;
					} catch (Exception e) {
						logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al guardar Revocado(Apoderados)",e);
					}
				}
				
//				for(TiivsPersona tiivsPersona:revocado.getPoderdantes()){
				for(TiivsRevocado tiivsRevocado:revocado.getPoderdantes())
				{			
//					tiivsRevocadoAux= new TiivsRevocado();
//					tiivsRevocadoAux.setCodRevocado(0);
//					tiivsRevocadoAux.setTiivsPersona(tiivsPersona);
//					tiivsRevocadoAux.setTipPartic(tiivsPersona.getTipPartic());
//					tiivsRevocadoAux.setFechaRevocatoria(new Date());
//					tiivsRevocadoAux.setEstado(ConstantesVisado.ESTADOS.ESTADO_PENDIENTE_REVOCADO);
//					tiivsRevocadoAux.setCodAgrup(Integer.parseInt(revocado.getCodAgrupacion()));				
//					tiivsRevocadoAux= new TiivsRevocado();
					tiivsRevocado.setCodRevocado(0);
//					tiivsRevocado.setTiivsPersona(tiivsPersona);
//					tiivsRevocadoAux.setTipPartic(tiivsPersona.getTipPartic());
					tiivsRevocado.setFechaRevocatoria(new Date());
					tiivsRevocado.setEstado(ConstantesVisado.ESTADOS.ESTADO_PENDIENTE_REVOCADO);
					tiivsRevocado.setCodAgrup(Integer.parseInt(revocado.getCodAgrupacion()));
					tiivsRevocado.setAliasArchivo(aliasCortoDocumento);
					
					try {
//						service.save(tiivsRevocadoAux);
						logger.debug("[REG-tiivsRevocado(Poderd)]-codRevocado:"+tiivsRevocado.getCodRevocado());
						logger.debug("[REG-tiivsRevocado(Poderd)]-codAgrup:"+tiivsRevocado.getCodAgrup());
						logger.debug("[REG-tiivsRevocado(Poderd)]-aliasArchivo:"+tiivsRevocado.getAliasArchivo());
						
						service.save(tiivsRevocado);
						flag=1;
					} catch (Exception e) {
						logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al guardar Revocado(Poderdantes)",e);
					}
				}	
			}else{
				logger.debug("No hay mas de (1) apod/poderd. para hacer el registro de revocados");
			}
		}
		
		logger.debug("[REG-Revocados]-flag"+flag);
		if(flag==1)
		{
			//Utilitarios.mensajeInfo("Guardado!", "Registros agregados correctamente!");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Guardado! Registros agregados correctamente!","");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
			bBooleanPopup=false;
			
			cargarCombinacionesRevocadas();
		}
		else if(flag==-1)
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Ocurrió un error al adjuntar archivo de revocados","");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
			bBooleanPopup=false;
			
			logger.debug("No se grabó la combinacion de revocados. Error al adjuntar archivo de revocados");
			
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se puede guardar, debe completar los datos de representado y/o representante!","");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
			bBooleanPopup=false;
			eliminarArchivoRevocados();
			logger.debug("No se grabó la combinacion de revocados");
		}
	}
	
	private void eliminarArchivoRevocados(){
		if(aliasCortoDocumento!=null){
			String sUbicacionTemporal = Utilitarios
					.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
					+ File.separator;
			String archivoEliminar = sUbicacionTemporal + aliasCortoDocumento;
			File file = new File(archivoEliminar);
			file.delete();
		}
	}
	
	public int getMaximoCodAgrupacion(){
		
		List<TiivsRevocado> tiivsrevocados= new ArrayList<TiivsRevocado>();
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		
		try {
			tiivsrevocados = service.buscarDinamico(filtro.addOrder(Order.desc("codAgrup")));
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener el getMaximoCodAgrupacion(): ",e);
		}
		
		if(tiivsrevocados.size() > 0){			
			return tiivsrevocados.get(0).getCodAgrup();
		}else{
			return 0;
		}
	}
	
	
	public void eliminarRevocado() 
	{
		logger.info("********************** eliminarArupacion *********************************** ");
		
		if(isFlagRevocar()==false){ //Para nuevo boton
						
			for(Revocado revocado : this.personaClientesPendEdit){

//				TiivsPersona personaRemover=null;
				TiivsRevocado personaRemover=null;
//				for(TiivsPersona persona: revocado.getPoderdantes()){
				for(TiivsRevocado personaRevocado: revocado.getPoderdantes()){
//					if(persona.getCodPer()==deletePersonaEdit.getCodPer()){
					if(personaRevocado.getTiivsPersona().getCodPer()==deletePersonaEdit.getTiivsPersona().getCodPer()){
//						personaRemover = persona;
						personaRemover = personaRevocado;
						break;
					}
				}
				
				if(personaRemover!=null){
					revocado.getPoderdantes().remove(personaRemover);
				}
				
				personaRemover=null;
//				for(TiivsPersona persona: revocado.getApoderados()){
				for(TiivsRevocado personaRevocado: revocado.getApoderados()){
//					if(persona.getCodPer()==deletePersonaEdit.getCodPer()){
					if(personaRevocado.getTiivsPersona().getCodPer()==deletePersonaEdit.getTiivsPersona().getCodPer()){
//						personaRemover = persona;
						personaRemover = personaRevocado;
						break;
					}
				}
				
				if(personaRemover!=null){
					revocado.getApoderados().remove(personaRemover);
				}
				
			}
			
			//Agrega a la lista de elementos por eliminar en BD
			/*if(isFlagRevocar()){
				lstElimRevo.add(String.valueOf(deletePersonaEdit.getTiivsPersona().getCodPer()));
			}*/
			
		} else {
			
			List<TiivsRevocado> tiivsrevocados= new ArrayList<TiivsRevocado>();
			
			List<TiivsRevocado> tiivsrevocados2= new ArrayList<TiivsRevocado>();
			
			GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
			Busqueda filtro2 = Busqueda.forClass(TiivsRevocado.class);
			
			
			String codAgrup= personaClientesPendEdit.get(0).getCodAgrupacion();
			
			try {
				filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(codAgrup)));
//				tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsPersona.codPer", deletePersonaEdit.getCodPer())));
				tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsPersona.codPer", deletePersonaEdit.getTiivsPersona().getCodPer())));
				
				if (tiivsrevocados!=null)
				{
					if (tiivsrevocados.size()>0)
					{
						service.eliminar(tiivsrevocados.get(0));
					}
				}
							
				tiivsrevocados2 = service.buscarDinamico(filtro2.add(Restrictions.eq("codAgrup", Integer.parseInt(codAgrup))));
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al eliminar tiivsrevocados2:",  e);
			}
			
//			List<TiivsPersona> apoderados= new ArrayList<TiivsPersona>();
//			List<TiivsPersona> poderdantes= new ArrayList<TiivsPersona>();
//			TiivsPersona apoderado;
//			TiivsPersona poderdante;
			
			List<TiivsRevocado> apoderados= new ArrayList<TiivsRevocado>();
			List<TiivsRevocado> poderdantes= new ArrayList<TiivsRevocado>();
			TiivsRevocado apoderado;
			TiivsRevocado poderdante;
			List<Revocado> revocadosAux= new ArrayList<Revocado>();
				
			for(TiivsRevocado tiivsRevocado:tiivsrevocados2)
			{
					if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO))
					{
//						apoderado= new TiivsPersona();
//						apoderado = tiivsRevocado.getTiivsPersona();
						
						
						String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
						String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
						

//						apoderado.setsDesctipDoi( descDoiApod);
//						apoderado.setsDesctipPartic(descTipPart);
						
						tiivsRevocado.setsDesctipDoi( descDoiApod);
						tiivsRevocado.setsDesctipPartic(descTipPart);
						
//						apoderados.add(apoderado);
						apoderados.add(tiivsRevocado);
					}
					
					if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO))
					{
//						apoderado= new TiivsPersona();
//						apoderado = tiivsRevocado.getTiivsPersona();
						

						String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
						String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
						

//						apoderado.setsDesctipDoi( descDoiApod);
//						apoderado.setsDesctipPartic(descTipPart);
						
						tiivsRevocado.setsDesctipDoi( descDoiApod);
						tiivsRevocado.setsDesctipPartic(descTipPart);
						
//						apoderados.add(apoderado);
						apoderados.add(tiivsRevocado);
					}
						
					if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE))
					{
//						poderdante = new TiivsPersona();
//						poderdante = tiivsRevocado.getTiivsPersona();
						
						String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
						String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);

						
//						poderdante.setsDesctipDoi( descDoiPod);
//						poderdante.setsDesctipPartic(descTipPart);
						
						tiivsRevocado.setsDesctipDoi( descDoiPod);
						tiivsRevocado.setsDesctipPartic(descTipPart);
						
						
//						poderdantes.add(poderdante);
						poderdantes.add(tiivsRevocado);
					}
			}
		
			Revocado revocado = new Revocado();
			revocado.setCodAgrupacion(codAgrup);
			revocado.setApoderados(apoderados);
			revocado.setPoderdantes(poderdantes);
			
			this.revocadoEdit = revocado;
		
			revocadosAux.add(revocado);
			
			
			personaClientesPendEdit = revocadosAux;
			
		}
		
	}
	
	public void eliminarRevocadoBD()
	{
		if (lstElimRevo != null)
		{
			for (String codigo: lstElimRevo)
			{
				List<TiivsRevocado> tiivsrevocados= new ArrayList<TiivsRevocado>();
				
				List<TiivsRevocado> tiivsrevocados2= new ArrayList<TiivsRevocado>();
				
				GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
				Busqueda filtro2 = Busqueda.forClass(TiivsRevocado.class);
				
				
				String codAgrup= personaClientesPendEdit.get(0).getCodAgrupacion();
				
				try {
					filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(codAgrup)));
//					tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsPersona.codPer", deletePersonaEdit.getCodPer())));
					tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsPersona.codPer", Integer.valueOf(codigo))));
					
					if (tiivsrevocados!=null)
					{
						if (tiivsrevocados.size()>0)
						{
							service.eliminar(tiivsrevocados.get(0));
						}
					}
								
					tiivsrevocados2 = service.buscarDinamico(filtro2.add(Restrictions.eq("codAgrup", Integer.parseInt(codAgrup))));
				} catch (Exception e) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar eliminar tiivsrevocados2:",  e);
				}
							
				List<TiivsRevocado> apoderados= new ArrayList<TiivsRevocado>();
				List<TiivsRevocado> poderdantes= new ArrayList<TiivsRevocado>();
				List<Revocado> revocadosAux= new ArrayList<Revocado>();
					
				for(TiivsRevocado tiivsRevocado:tiivsrevocados2)
				{
						if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO))
						{
//							apoderado= new TiivsPersona();
//							apoderado = tiivsRevocado.getTiivsPersona();
							
							
							String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
							String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
							

//							apoderado.setsDesctipDoi( descDoiApod);
//							apoderado.setsDesctipPartic(descTipPart);
							
							tiivsRevocado.setsDesctipDoi( descDoiApod);
							tiivsRevocado.setsDesctipPartic(descTipPart);
							
//							apoderados.add(apoderado);
							apoderados.add(tiivsRevocado);
						}
						
						if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO))
						{
//							apoderado= new TiivsPersona();
//							apoderado = tiivsRevocado.getTiivsPersona();
							
							
							String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
							String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
							

//							apoderado.setsDesctipDoi( descDoiApod);
//							apoderado.setsDesctipPartic(descTipPart);
							
							tiivsRevocado.setsDesctipDoi( descDoiApod);
							tiivsRevocado.setsDesctipPartic(descTipPart);
							
//							apoderados.add(apoderado);
							apoderados.add(tiivsRevocado);
						}
							
						if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE))
						{			
							String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
							String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
							
							tiivsRevocado.setsDesctipDoi( descDoiPod);
							tiivsRevocado.setsDesctipPartic(descTipPart);
							
							poderdantes.add(tiivsRevocado);
						}
				}
			
				Revocado revocado = new Revocado();
				revocado.setCodAgrupacion(codAgrup);
				revocado.setApoderados(apoderados);
				revocado.setPoderdantes(poderdantes);
				
				this.revocadoEdit = revocado;
			
				revocadosAux.add(revocado);
				
				
				personaClientesPendEdit = revocadosAux;
			}
		}		
	}
	
	
	public boolean validarRegistroDuplicado() {
		logger.info("****validarRegistroDuplicado *********** "+ objTiivsPersonaAgregar.getNumDoi());
		boolean bResult = true;
		String sMensaje = "";
		for (TipoDocumento c : combosMB.getLstTipoDocumentos()) {
			if (objTiivsPersonaAgregar.getTipDoi().equals(c.getCodTipoDoc())) {
				objTiivsPersonaAgregar.setsDesctipDoi(c.getDescripcion());
				break;
			}
		}
		for (ComboDto c : combosMB.getLstTipoRegistroPersona()) {
			if (objTiivsPersonaAgregar.getTipPartic().equals(c.getKey())) {
				objTiivsPersonaAgregar.setsDesctipPartic(c.getDescripcion());
				break;
			}
		}
		
		
		for (Revocado rev : personaClientesPendEdit) {
			
//			for(TiivsPersona  tiivsPersona:rev.getApoderados()){
			for(TiivsRevocado  persona:rev.getApoderados()){
//				if (objTiivsPersonaAgregar.getTipDoi().equals(tiivsPersona.getTipDoi())
//						&& objTiivsPersonaAgregar.getNumDoi().equals(tiivsPersona.getNumDoi())) {
				if (objTiivsPersonaAgregar.getTipDoi().equals(persona.getTiivsPersona().getTipDoi())
						&& objTiivsPersonaAgregar.getNumDoi().equals(persona.getTiivsPersona().getNumDoi())) {
					bResult = false;
//					sMensaje = "Ya existe una persona con número de Doi :  "
//							+ tiivsPersona.getNumDoi() ;
					sMensaje = "Ya existe una persona con número de Doi :  "
							+ persona.getTiivsPersona().getNumDoi() ;
					Utilitarios.mensajeInfo("INFO", sMensaje);
					break;
				}
			}
			
			if(bResult){
				
//				for(TiivsPersona  tiivsPersona:rev.getPoderdantes()){
				for(TiivsRevocado  persona:rev.getPoderdantes()){
					
//					if (objTiivsPersonaAgregar.getTipDoi().equals(tiivsPersona.getTipDoi())
//							&& objTiivsPersonaAgregar.getNumDoi().equals(tiivsPersona.getNumDoi())) {
					if (objTiivsPersonaAgregar.getTipDoi().equals(persona.getTiivsPersona().getTipDoi())
							&& objTiivsPersonaAgregar.getNumDoi().equals(persona.getTiivsPersona().getNumDoi())) {
//						bResult = false;
//						sMensaje = "Ya existe una persona con número de Doi :  "
//								+ tiivsPersona.getNumDoi() ;
						sMensaje = "Ya existe una persona con número de Doi :  "
								+ persona.getTiivsPersona().getNumDoi() ;
						Utilitarios.mensajeInfo("INFO", sMensaje);
						break;
					}
				}
				
			}
		}
		
		return bResult;
		
	}

	
	public boolean validarRevocado() {
		logger.info("******************************* validarRevocado ******************************* "	+ objTiivsPersonaAgregar.getTipPartic());
		boolean bResult = true;
		String sMensaje = "";
		
		if (objTiivsPersonaAgregar.getTipDoi() == null ||  objTiivsPersonaAgregar.getTipDoi().equals("")) {
			sMensaje = "Seleccione el Tipo de Documento";
			bResult = false;
			Utilitarios.mensaje(sMensaje, "");
		}
		
		if (objTiivsPersonaAgregar.getNumDoi() == null || objTiivsPersonaAgregar.getNumDoi().equals("")) {
			sMensaje = "Ingrese el Número de Doi";
			bResult = false;
			Utilitarios.mensaje(sMensaje, "");
		}
		
	/*	if (objTiivsPersonaAgregar.getCodCen() == null ||  objTiivsPersonaAgregar.getCodCen().equals("")) {
			sMensaje = "Ingrese el Cod Cen";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		*/
		
		logger.info("objTiivsPersonaAgregar.getTipDoi():" + objTiivsPersonaAgregar.getTipDoi().trim());
		logger.info("ConstantesVisado.CODIGO_CAMPO_TIPODOI_RUC:" + ConstantesVisado.CODIGO_CAMPO_TIPODOI_RUC.trim());
		
		/*if(!objTiivsPersonaAgregar.getTipDoi().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPODOI_RUC.trim()))
		{	*/
			if (objTiivsPersonaAgregar.getApePat() == null ||  objTiivsPersonaAgregar.getApePat().equals("")) {
				if(!objTiivsPersonaAgregar.getTipDoi().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPODOI_RUC.trim()))
				{
				sMensaje = "Ingrese el Apellido Paterno";
				}else{
				sMensaje = "Ingrese la Razón Social";	
				}
				bResult = false;
				Utilitarios.mensaje(sMensaje, "");
			}
			if(!objTiivsPersonaAgregar.getTipDoi().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPODOI_RUC.trim()))
			{
			if (objTiivsPersonaAgregar.getApeMat() == null  || objTiivsPersonaAgregar.getApeMat().equals("")) {
				sMensaje = "Ingrese el Apellido Materno";
				bResult = false;
				Utilitarios.mensaje(sMensaje, "");
			}
			
		//}
		
		if (objTiivsPersonaAgregar.getNombre() == null  ||  objTiivsPersonaAgregar.getNombre().equals("")) {
			sMensaje = "Ingrese el Nombre";
			bResult = false;
			Utilitarios.mensaje(sMensaje, "");
		}
		}
		
		if ((objTiivsPersonaAgregar.getTipPartic() == null || objTiivsPersonaAgregar.getTipPartic().equals(""))) {
			sMensaje = "Ingrese el Tipo de Participacion";
			bResult = false;
			Utilitarios.mensaje(sMensaje, "");
		}

		return bResult;

	}
	
	public List<TiivsPersona> buscarPersonaLocal() throws Exception {
		logger.info("Buscando en Persona Local");
		boolean busco = false;
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);

		if ((objTiivsPersonaBusquedaDlg.getTipDoi() == null 
		  || objTiivsPersonaBusquedaDlg.getTipDoi().equals(""))
		 && (objTiivsPersonaBusquedaDlg.getNumDoi() == null
		  || objTiivsPersonaBusquedaDlg.getNumDoi().equals(""))) {
			Utilitarios.mensajeInfo("INFO","Ingrese al menos un criterio de busqueda");
		} else if (objTiivsPersonaBusquedaDlg.getNumDoi() == null
				|| objTiivsPersonaBusquedaDlg.getNumDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Número de Doi");
		} else if (objTiivsPersonaBusquedaDlg.getTipDoi() == null
				|| objTiivsPersonaBusquedaDlg.getTipDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Tipo de Doi");
		} else {
			if (objTiivsPersonaBusquedaDlg.getTipDoi().equals(ConstantesVisado.TIPOS_DOCUMENTOS_DOI.COD_CODIGO_CENTRAL)) {
				filtro.add(Restrictions.eq("codCen",objTiivsPersonaBusquedaDlg.getNumDoi()));
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
				filtro.add(Restrictions.eq("tipDoi",objTiivsPersonaBusquedaDlg.getTipDoi().trim()));
				filtro.add(Restrictions.eq("numDoi",objTiivsPersonaBusquedaDlg.getNumDoi().trim()));
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
		com.bbva.consulta.reniec.util.Persona persona = null;
		if (objTiivsPersonaBusquedaDlg.getNumDoi() != null) {
			logger.info("[RENIEC]-Dni:"+ objTiivsPersonaBusquedaDlg.getNumDoi());

			//ObtenerPersonaReniecService reniecService = new ObtenerPersonaReniecServiceImpl();
			//logger.debug("reniecService="+reniecService);
			ObtenerPersonaReniecDUMMY reniecService = new ObtenerPersonaReniecDUMMY();
			resultado = reniecService.devolverPersonaReniecDNI("P013371", "0553",objTiivsPersonaBusquedaDlg.getNumDoi());
			logger.debug("[RENIEC]-resultado: "+resultado);
			
			if (resultado.getCode() == 0) {
				
				persona = (com.bbva.consulta.reniec.util.Persona) resultado.getObject();
				logger.info("PERSONA : " + persona.getNombreCompleto()
						+ "\nDNI: " + persona.getNumerodocIdentidad());
				objPersona = new TiivsPersona();
				objPersona.setNumDoi(persona.getNumerodocIdentidad());
				objPersona.setNombre(persona.getNombre());
				objPersona.setApePat(persona.getApellidoPaterno());
				objPersona.setApeMat(persona.getApellidoMaterno());
				objPersona.setTipDoi(objTiivsPersonaBusquedaDlg.getTipDoi());
				objPersona.setCodCen(objTiivsPersonaBusquedaDlg.getCodCen());
				lstTiivsPersona.add(objPersona);
			}
		}
		
		logger.debug("==== saliendo de buscarPersonaReniec() ==== ");
		return lstTiivsPersona;
	}
	public void inactivarCombinacion(ActionEvent actionEvent) {
		logger.info( " ==== inactivarCombinacion() ====");
		List<TiivsMultitabla> tiivsMultitablas2 = new ArrayList<TiivsMultitabla>();
		
		GenericDao<TiivsMultitabla, Object> service3 = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		Busqueda filtro4 = Busqueda.forClass(TiivsMultitabla.class);
		filtro4.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		
		try {
			tiivsMultitablas2 = service3.buscarDinamico(filtro4);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"la multitabla de estados: ",e);
		}
		
		//Empieza la inactivacion
		List<TiivsRevocado> tiivsrevocados = new ArrayList<TiivsRevocado>();
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(revocadoEdit.getCodAgrupacion())));
		//String estadoInactivo = getValor1(ConstantesVisado.ESTADOS.ESTADO_INACTIVO_REVOCADO,tiivsMultitablas2);
		try {
			tiivsrevocados = service.buscarDinamico(filtro);
			for(TiivsRevocado  tiivsRevocado:tiivsrevocados){
				tiivsRevocado.setEstado(ConstantesVisado.ESTADOS.ESTADO_INACTIVO_REVOCADO);
				service.modificar(tiivsRevocado);
			}
			logger.debug("Se ha inactivado correctamente!");
			Utilitarios.mensajeInfo("Info", "Se ha inactivado correctamente");			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al inactivar Revocados: ",e);
		}
		
		buscarRevocado();
	}
	
	public List<TiivsPersona> completePersona(String query) 
	{
		List<TiivsPersona> lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		List<TiivsPersona> lstTiivsPersonaBusqueda = new ArrayList<TiivsPersona>();

		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);
		try {
			lstTiivsPersonaBusqueda = service.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"las Personas: ",e);
		}

		for (TiivsPersona pers : lstTiivsPersonaBusqueda) 
		{
			if(pers!= null)
			{	
//				if (pers.getApePat()!=null && pers.getApeMat()!=null)
//				{
					String nombre = pers.getNombre()!=null?pers.getNombre().toUpperCase():"";
					String apePat = pers.getApePat()!=null?pers.getApePat().toUpperCase():"";
					String apeMat = pers.getApeMat()!=null?pers.getApeMat().toUpperCase():"";;
					
//					if (nombre != "" && apePat != "" && apeMat != "") 
//					{
						
						String nombreCompletoMayuscula = nombre+ " " + apePat + " " + apeMat;

						if(nombreCompletoMayuscula.trim() != ""){

							String nombreCompletoMayusculaBusqueda = apePat + " " + apePat + " " + nombre;

							if (nombreCompletoMayusculaBusqueda.trim().contains(query.toUpperCase())) {

								pers.setNombreCompletoMayuscula(nombreCompletoMayuscula);

								lstTiivsPersonaResultado.add(pers);
							}
							
						}
						
//					}
//				}
				
			}
		}

		return lstTiivsPersonaResultado;
	}
	
	public void buscarRevocado() {
		logger.info("==== buscarRevocado() =====");
		revocados = new ArrayList<Revocado>();
		List<TiivsRevocado> tiivsrevocados = new ArrayList<TiivsRevocado>();
		List<TiivsRevocado> lstRevocadosPre = new ArrayList<TiivsRevocado>();
		
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		filtro.createAlias("tiivsPersona", "persona");
		
		if(objTiivsPersonaBusqueda.getTipPartic().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("tipPartic", objTiivsPersonaBusqueda.getTipPartic()));
		
		}
		
		if(objTiivsPersonaBusqueda.getTipDoi().compareTo("")!=0){
			
			
			filtro.add(Restrictions.eq("persona.tipDoi", objTiivsPersonaBusqueda.getTipDoi()));
		}
		
		
		if(objTiivsPersonaBusqueda.getNumDoi().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("persona.numDoi", objTiivsPersonaBusqueda.getNumDoi()));
		}
		
		if(objTiivsPersonaBusquedaNombre != null){
			
			if(objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().compareTo("")!=0){
//				filtro.add(Restrictions.eq("persona.nombre", objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[0]));
//				filtro.add(Restrictions.eq("persona.apePat", objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[1]));
//				filtro.add(Restrictions.eq("persona.apeMat", objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[2]));
				
				filtro.add(Restrictions.eq("persona.nombre", objTiivsPersonaBusquedaNombre.getNombre()));
				if(objTiivsPersonaBusquedaNombre.getApePat()!=null)
					filtro.add(Restrictions.eq("persona.apePat", objTiivsPersonaBusquedaNombre.getApePat()));
				if(objTiivsPersonaBusquedaNombre.getApeMat()!=null)
					filtro.add(Restrictions.eq("persona.apeMat", objTiivsPersonaBusquedaNombre.getApeMat()));
				
			}
			
		}
		
		if(estadoRevocado.compareTo("S")!=0){
			logger.info(" **************** estadoRevocado "+estadoRevocado);
			filtro.add(Restrictions.eq("estado", estadoRevocado));
		}
		
		if(fechaInicio!= null ||  fechaFin!=null){

			filtro.add(Restrictions.between("fechaRevocatoria", fechaInicio, fechaFin));
		}
		
		
		try {
			lstRevocadosPre = service.buscarDinamico(filtro.addOrder(Order.desc("codAgrup")).addOrder(Order.desc("fechaRevocatoria")));
			if(lstRevocadosPre!=null){
				logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"revocados es:["+lstRevocadosPre.size()+"].");
			}
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"Revocados:", e);
		}
		
		List<Integer> tmpAgrup = new ArrayList<Integer>();
		
		for (TiivsRevocado tmp: lstRevocadosPre)
		{
			if (tmp!=null)
			{
				tmpAgrup.add(tmp.getCodAgrup());
			}
		}
		
		//Busqueda de agrupaciones de revocados
		GenericDao<TiivsRevocado, Object> serviceRev = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroRev = Busqueda.forClass(TiivsRevocado.class);
		filtroRev.add(Restrictions.in("codAgrup", tmpAgrup));
		filtroRev.addOrder(Order.desc("codAgrup"));
		filtroRev.addOrder(Order.desc("fechaRevocatoria"));
		
		try {
			tiivsrevocados = serviceRev.buscarDinamico(filtroRev);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener la lista final de revocados: ",e);
		}
				
		if (tiivsrevocados.size()>0)
		{
			setDeshabilitarExportar(false);
		}
		else
		{
			setDeshabilitarExportar(true);
		}
		
		
		Revocado revocado;
		String nombreCompletoApoderados="";
		String nombreCompletoPoderdantes="";
		String fecha="";
		String estado="";
		int numCorrelativo=0;
		
//		List<TiivsPersona> apoderados;				
//		List<TiivsPersona> poderdantes;
		
		List<TiivsRevocado> apoderados;				
		List<TiivsRevocado> poderdantes;
		String aliasArchivo = null;
		 List<Integer>  listCodAgrup =  obtenerListCodAgrupacion();
		
		if(listCodAgrup != null){
			
			if(listCodAgrup.size() > 0){
				
				for(Integer tiivsRevocado2:listCodAgrup){
					numCorrelativo++;
					//apoderados= new ArrayList<TiivsPersona>();
					//poderdantes= new ArrayList<TiivsPersona>();
					apoderados= new ArrayList<TiivsRevocado>();
					poderdantes= new ArrayList<TiivsRevocado>();
					
					try{
						
						for(TiivsRevocado tiivsRevocado:tiivsrevocados){

							if(tiivsRevocado.getCodAgrup().compareTo(tiivsRevocado2)==0){

								
								fecha=getDate(tiivsRevocado.getFechaRevocatoria());
								estado=getValor1(tiivsRevocado.getEstado(),listEstados);
								
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO)){
									
	//								TiivsPersona apoderado;									
	//								apoderado = new TiivsPersona();
	//								apoderado = tiivsRevocado.getTiivsPersona();
									
									String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
									
									aliasArchivo = tiivsRevocado.getAliasArchivo();
									
									nombreCompletoApoderados = nombreCompletoApoderados
																	+ " " + descDoiApod
																	+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
																  + " - " + (tiivsRevocado.getTiivsPersona().getApePat()==null?"":tiivsRevocado.getTiivsPersona().getApePat()) 
																	+ " " + (tiivsRevocado.getTiivsPersona().getApeMat()==null?"":tiivsRevocado.getTiivsPersona().getApeMat())
																	+ " " + (tiivsRevocado.getTiivsPersona().getNombre()==null?"":tiivsRevocado.getTiivsPersona().getNombre()) + "\n";
									
									
	//								apoderado.setsDesctipDoi( descDoiApod);
	//								apoderado.setsDesctipPartic(descTipPart);								
									tiivsRevocado.setsDesctipDoi( descDoiApod);
									tiivsRevocado.setsDesctipPartic(descTipPart);									
	//								apoderados.add(apoderado);
									apoderados.add(tiivsRevocado);
								}
								
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.TIPO_PARTICIPACION.CODIGO_HEREDERO)){

									String descDoiApod = getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart = getValor1(tiivsRevocado.getTipPartic(),listTipoRegistro);
									
									aliasArchivo = tiivsRevocado.getAliasArchivo();

									nombreCompletoApoderados = nombreCompletoApoderados
											+ " "+ descDoiApod+ ":"+ tiivsRevocado.getTiivsPersona().getNumDoi()
											+ " - "	+ (tiivsRevocado.getTiivsPersona().getApePat() == null ? "": tiivsRevocado.getTiivsPersona().getApePat())
											+ " "+ (tiivsRevocado.getTiivsPersona().getApeMat() == null ? "": tiivsRevocado.getTiivsPersona().getApeMat())
											+ " "+ (tiivsRevocado.getTiivsPersona().getNombre() == null ? "": tiivsRevocado.getTiivsPersona().getNombre())
											+ "\n";

									tiivsRevocado.setsDesctipDoi(descDoiApod);
									tiivsRevocado.setsDesctipPartic(descTipPart);

									apoderados.add(tiivsRevocado);
								}
									
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
									
	//								TiivsPersona poderdante;									
	//								poderdante = new TiivsPersona();
	//								poderdante = tiivsRevocado.getTiivsPersona();
									aliasArchivo = tiivsRevocado.getAliasArchivo();
									
									String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
									
											
									nombreCompletoPoderdantes = nombreCompletoPoderdantes 
																		+ " " + descDoiPod
																		+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
																		 + " - " + (tiivsRevocado.getTiivsPersona().getApePat()==null?"":tiivsRevocado.getTiivsPersona().getApePat()) 
																	+ " " + (tiivsRevocado.getTiivsPersona().getApeMat()==null?"":tiivsRevocado.getTiivsPersona().getApeMat())
																	+ " " + (tiivsRevocado.getTiivsPersona().getNombre()==null?"":tiivsRevocado.getTiivsPersona().getNombre()) + "\n";
									
	//								poderdante.setsDesctipDoi( descDoiPod);
	//								poderdante.setsDesctipPartic(descTipPart);
									
									tiivsRevocado.setsDesctipDoi( descDoiPod);
									tiivsRevocado.setsDesctipPartic(descTipPart);
									
									poderdantes.add(tiivsRevocado);
								}
								

							}
							
							
						}
						
						revocado = new Revocado();
						revocado.setCodAgrupacion(tiivsRevocado2+"");
						revocado.setFechaRegistro(fecha);
						revocado.setEstado(estado);
						revocado.setCorrelativo(String.valueOf(numCorrelativo));
						revocado.setAliasArchivo(aliasArchivo);
						
						revocado.setNombreCompletoApoderados(nombreCompletoApoderados.trim());
						revocado.setApoderados(apoderados);
							
						revocado.setNombreCompletoPoderdantes(nombreCompletoPoderdantes.trim());
						revocado.setPoderdantes(poderdantes);
						
						
						if(estado.compareTo(ConstantesVisado.ESTADOS.ESTADO_ACTIVO)==0){
							revocado.setFlagEditAct(true);
							revocado.setFlagEditPend(false);
							revocado.setFlagDelete(false);
						}
						
						if(estado.compareTo("Pendiente")==0){
							revocado.setFlagEditPend(true);
							revocado.setFlagEditAct(false);
							revocado.setFlagDelete(true);
						}
						
						if(estado.compareTo(ConstantesVisado.ESTADOS.ESTADO_DESACTIVO)==0){
							revocado.setFlagEditPend(false);
							revocado.setFlagEditAct(false);
							revocado.setFlagDelete(false);
							
							
						}
						
						if(apoderados.size() > 0  || poderdantes.size() > 0){
							revocados.add(revocado);
						}
						
						nombreCompletoApoderados="";
						nombreCompletoPoderdantes="";
						fecha="";
						estado="";
					
					} catch(Exception e){
						logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en Revocados: ",e);
					}
				}
				
			}
			
		}
		bBooleanPopup=false;
	}
	
	public List<Integer> obtenerListCodAgrupacion(){
		List<Integer> listCodAgrup= new ArrayList<Integer>();
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro2 = Busqueda.forClass(TiivsRevocado.class).setProjection(Projections.distinct(Projections.property("codAgrup")));
		try {
			listCodAgrup = service.buscarDinamicoInteger(filtro2.addOrder(Order.desc("codAgrup")));
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener la lista de cod de agrupacion: ",e);
		}
		return listCodAgrup;
	}
	
	public void cambiarRazonSocial(ValueChangeEvent e){		
		logger.info("==== cambiarRazonSocial()=======");
		String codTipoDocumento = (String) e.getNewValue();
		if (codTipoDocumento!=null && codTipoDocumento.equals(this.codigoRazonSocial)) {//CODIGO RAZONSOCIAL
			this.mostrarRazonSocial = true;
			objTiivsPersonaAgregar.setTipDoi(this.codigoRazonSocial);
			objTiivsPersonaAgregar.setApeMat("");
			objTiivsPersonaAgregar.setNombre("");
		} else {
			this.mostrarRazonSocial = false;
			objTiivsPersonaAgregar.setTipDoi("");
		}
		//15-10 Se agrega que el tipo Doi no sea nulo
		if(codTipoDocumento!=null){
			this.obterPatterDelTipoDocumento(codTipoDocumento);	
		}
	}
	
	private void obtenCodRazonSocial() {
		TiposDoiService tiposDoiService = new TiposDoiService(); 			
		codigoRazonSocial = tiposDoiService.obtenerCodPersonaJuridica();
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
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"de multitablas: " , e);
		}
	}
	
	//METODO PARA OBTENER ETIQUETAS DE TIPO DE REGISTRO DESDE BD Y MOSTRARLO EN GRILLA
	private void obtenerEtiquetasTipoRegistro() {
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
						poderdante += multitabla.getValor1() + " / ";
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
						apoderdante += multitabla.getValor1() + " / ";
					}
				}
			}
		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+ "de multitablas: " + e);
		}
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
			logger.info("[obterPatterTipDoc]-pattern: "+patter);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+ "de multitablas: " , e);
		}
	}
	
	private String getValor1(String codElem, List<TiivsMultitabla> multitabla){
		for(TiivsMultitabla obj:multitabla){
			if(obj.getId().getCodElem().compareTo(codElem)==0){
				return obj.getValor1();
			}	
		}
		return "";
	}
	
	private boolean cargarDocumentoRevocatoria(){
		logger.info("=== cargarDocumentoRevocatoria() ===");
		logger.info("[CargarDocRev]-fileUpload: " + fileUpload);
		byte fileBytes[] = null;
		File fichTemp = null;
		FileOutputStream canalSalida = null;
		String sUbicacionTemporal = "";
		sUbicacionTemporal = Utilitarios
				.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
				+ File.separator;
		logger.debug("[CargarDocRev]-sUbicacionTemporal: "+sUbicacionTemporal);
		boolean exito = true;
		
		if(fileUpload!=null){
			aliasCortoDocumento = Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA);
			String extension = fileUpload.getFileName().substring(getFileUpload().getFileName().lastIndexOf("."));
			sAliasTemporal = aliasCortoDocumento + extension;
			fileBytes = getFileUpload().getContents();
			try {
				File fDirectory = new File(sUbicacionTemporal);
				fDirectory.mkdirs();
				if (aliasCortoDocumento.equals("")) {
					aliasCortoDocumento = "temp";
				} else {
					aliasCortoDocumento = aliasCortoDocumento + "_";
				}
				
				fichTemp = File.createTempFile(aliasCortoDocumento, extension,
						new File(sUbicacionTemporal));
				
				aliasCortoDocumento = fichTemp.getName();
				logger.debug("[CargarDocRev]-NombreArchivoTEMP: " + aliasCortoDocumento);

				canalSalida = new FileOutputStream(fichTemp);
				canalSalida.write(fileBytes);

				canalSalida.flush();
				canalSalida.close();
				return true;
			} catch (IOException e) {
				exito = false;
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IO Exception:" , e);
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "al CargarDocRev:" , e);
			}
		}else{
			if(aliasCortoDocumento!=null && sAliasTemporal!=null){
				String ruta_files = sUbicacionTemporal + ConstantesVisado.FILES + File.separator;  
				logger.debug("[CargarDocRev]-ruta_files: "+ruta_files);
				logger.debug("[CargarDocRev]-sUbicacionTemporal: "+sUbicacionTemporal);
				
				File srcFile = new File(ruta_files + aliasCortoDocumento);
				File destFile = new File(sUbicacionTemporal + aliasCortoDocumento);
				
				try {
					FileUtils.copyFile(srcFile, destFile);
					logger.debug("Despues de mover el archivo de revocados ...");
					eliminarTemporal(ruta_files);
				} catch (IOException e) {
					exito = false;
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al mover archivo revocados a carpeta: " ,e);
				} 
			}
		}
		logger.info("[CargarDocRev]-aliasCortoDocumento: " + aliasCortoDocumento);
		logger.info("[CargarDocRev]-sAliasTemporal: " + sAliasTemporal);
		logger.info("=== termina metodo cargarDocumentoRevocatoria() ===");
		return exito;
	}
	
	private void eliminarTemporal(String ruta_temporal){
		if(ruta_temporal!=null){
			File file = new File(ruta_temporal + aliasCortoDocumento);
			if(file.delete()){
				logger.info("Se eliminó correctamente el archivo: " + aliasCortoDocumento);
			}else{
				logger.error("+++ Problemas al eliminar archivo: " + aliasCortoDocumento);
			}
		}
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
	
	 public void handleFileUpload(FileUploadEvent event) {  
		logger.info("=== handleFileUpload() === ");
		logger.debug("event.getFile().getFileName():"+event.getFile().getFileName());
		logger.debug("event.getFile().getSize():"+event.getFile().getSize());
		logger.debug("event.getFile().getContentType():"+event.getFile().getContentType());
		
	    FacesMessage msg = new FacesMessage("Archivo", event.getFile().getFileName() + " almacenado correctamente.");  
	    FacesContext.getCurrentInstance().addMessage(null, msg);  
	    
	    setFileUpload(event.getFile());
	     
	    aliasCortoDocumento = Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA);
		String extension = fileUpload.getFileName().substring(getFileUpload().getFileName().lastIndexOf("."));
		sAliasTemporal = aliasCortoDocumento + extension;
			
		logger.info("aliasCortoDocumento: " + aliasCortoDocumento);
		
	 } 
	  
	 public void actualizarArchivo(){
		logger.info("=== actualizarArchivo() === ");
		aliasCortoDocumento = null;
		sAliasTemporal = null;
		flagLinkRevocados = 0;
		
		if(revocadoEdit!=null){
			flagLinkRevocados = 1;
			String extension = revocadoEdit.getAliasArchivo().substring(revocadoEdit.getAliasArchivo().lastIndexOf("."));	
			aliasCortoDocumento = revocadoEdit.getAliasArchivo();
			sAliasTemporal = Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA) + extension;
			logger.debug("[actArchivo]-sAliasTemporal: "+sAliasTemporal);
		}
		logger.info("=== fin de actualizarArchivo() === ");
	 }
	 
	 /*
	  * Metodo que actualiza la lista de documentos, este método es indirectamente
	  * invocado desde el applet
	  * */
	 public void actualizarDocumentosRevocados(ActionEvent ae){
		logger.info("====== actualizarDocumentosRevocados =====");
		logger.info("[Applet-ActualRevocad]-Documentos LEIDOS: " + visadoDocumentosMB.getDocumentosLeidos());
		logger.info("[Applet-ActualRevocad]-Documentos CARGADOS: " + visadoDocumentosMB.getDocumentosCargados());
		 
		String []aDocumentosLeidos = visadoDocumentosMB.getDocumentosLeidos().split(",");
		String []aDocumentosCargados = visadoDocumentosMB.getDocumentosCargados().split(",");
		fileUpload = null;
		
		//Actualiza lista de documentos		
		if(aDocumentosLeidos.length == aDocumentosCargados.length){
			//sAliasTemporal
			String extension = null;
			logger.info("[Applet-ActualRevocad]-Tamanio archivos:" + aDocumentosCargados.length);
			for(String archivo:aDocumentosCargados){
				aliasCortoDocumento = archivo;
				extension = archivo.substring(archivo.lastIndexOf("."));
			}
			sAliasTemporal = Utilitarios.getPropiedad(ConstantesVisado.NOMBRE_ARCHIVO_REVOCATORIA) + extension;
			flagLinkRevocados = 1;
		}
	 }
	
	 /****
	  * 
	  * @param ubicacionLocal
	  * @param ubicacion 0:FILES 1:DOCUMENTOS
	  * @return
	  */
	 public boolean descargarDocumento(String ubicacionLocal, String ubicacion) {
			logger.debug("==== INICIA descargarDocumento()===");
			logger.debug("[descargarArchivo]-ubicacionLocal:"+ubicacionLocal);
			logger.debug("[descargarArchivo]-ubicacion:"+ubicacion);
			/*String urlServer="";
			String server="";
			String loginServer="";
			String passServer="";
			String carpetaRemota="";
			String rutaResultante="";*/
			boolean iRet = true; 
			
			String sUbicacionTemporal;
			String rutaArchivoProyecto = Utilitarios.getProjectPath() + File.separator + ConstantesVisado.FILES + File.separator;
			
			if(ubicacion.compareTo("0")==0){
				sUbicacionTemporal = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER) + File.separator + ConstantesVisado.FILES + File.separator;
			} else {
				sUbicacionTemporal = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER) + File.separator;
			}
			
			//Archivo a descargar
			File fichTemp = new File(sUbicacionTemporal	+ ubicacionLocal);
			
			//Si existe el archivo
			if(fichTemp.exists()){
				File destFile = new File(rutaArchivoProyecto + ubicacionLocal);
				try {
					//Copia los archivos del File server a la carpeta temporal
					FileUtils.copyFile(fichTemp, destFile);
				} catch (IOException e) {
					logger.error("Error al descargar archivo: "	+ ubicacionLocal ,e);
				}
			}
			
			/*TiivsParametros parametros = obtenerParametro();
			int iResultCargaParametros = 0;
			if (parametros != null) {
				urlServer = ConstantesVisado.PROTOCOLO_FTP
						+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
						+ File.separator + parametros.getLoginServer()
						+ ConstantesVisado.DOS_PUNTOS.trim()
						+ parametros.getPassServer() + ConstantesVisado.ARROBA
						+ parametros.getServer() + File.separator
						+ parametros.getCarpetaRemota();

				server = parametros.getServer();
				loginServer = parametros.getLoginServer();
				passServer = parametros.getPassServer();
				carpetaRemota = parametros.getCarpetaRemota();
				iResultCargaParametros = 1;
				
				String rutaPrincipal = obtenerRutaPrincipal(parametros.getCarpetaRemota());
				if(rutaPrincipal.compareTo(ConstantesVisado.DOCUMENTOS)==0){
					carpetaRemota = File.separator + ConstantesVisado.FILES + File.separator;
				}else if(rutaPrincipal.compareTo(ConstantesVisado.VISADO)==0){
					carpetaRemota = File.separator + ConstantesVisado.DOCUMENTOS + File.separator + ConstantesVisado.FILES + File.separator;	
				}

			}
			
			logger.debug(" -- Parametros BD-FTP --");
			logger.debug("\tDir Server: " + urlServer);
			logger.debug("\tDir Local: " + dirLocal);
			logger.debug("\tServer: " + server);
			logger.debug("\tLogin Server:_" + loginServer + "Pass: " + passServer);
			logger.debug("\tCarpeta Remota: " + carpetaRemota);		
			
			if(iResultCargaParametros == 0){
				logger.debug("No se ha logrado cargar los parámetros de conexión al FTP");
				iRet = false;
			} else {			
				ClienteFTP cliente = null;
				try {
					cliente = new ClienteFTP(server, loginServer, passServer);
					cliente.setDirectorio(carpetaRemota);
				} catch (IOException e1) {
					logger.error("Error al instanciar clienteFTP ",e1);			
				}
				if (cliente!=null){
					if(ubicacion.compareTo("0")==0){
						rutaResultante = ConstantesVisado.FILES;
					}else{
						rutaResultante = ConstantesVisado.DOCUMENTOS;
					}
					iRet = cliente.descargarArchivo(ubicacionLocal, rutaResultante);
					logger.debug("[descargarArchivo]-iRet:"+iRet);
				}			
			}*/
			logger.debug("==== SALIENDO de descargarDocumento()===");
			return iRet;		
	}
	
	public String descargarDocumentoTemporal() {
//	 public void descargarDocumentoTemporal() {
		logger.debug("=== inicia descargarDocumentoTemporal() ====");
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		String nombreDocumento = params.get("nombreArchivo");
		logger.debug("[DESCARG_DOC]-nombreDocumento: "+nombreDocumento);
		String rutaDocumento = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
				+ File.separator + ConstantesVisado.FILES + File.separator + nombreDocumento;
		
		if(revocadoEdit!=null){
			rutaDocumento = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
					+ File.separator + nombreDocumento;
		}
		
		logger.debug("[DESCARG_DOC]-rutaDocumento: "+rutaDocumento);
		String outputFileName = rutaDocumento;
		
		File outputPDF = new File(outputFileName);

		// Get ready to return pdf to user
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(outputPDF),10240);

			response.reset();
			response.setHeader("Content-Type", "application/pdf");
			response.setHeader("Content-Length",String.valueOf(outputPDF.length()));
			response.setHeader("Content-Disposition", "attachment; filename=\""+ nombreDocumento + "\"");
			output = new BufferedOutputStream(response.getOutputStream(), 10240);

			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			logger.debug("finalizando OK");
			output.flush();
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 1 al descargarDocumento: ",e);
		} catch (Exception ex) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "general al descargarDocumento: ",ex);
		} 
		finally {
			try {
				output.close();
			} catch (IOException e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 2 al descargarDocumento: ",e);
			}
			try {
				input.close();
			} catch (IOException e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 3 al descargarDocumento: ",e);
			}
		}
		FacesContext.getCurrentInstance().responseComplete();
		
		logger.debug("=== saliendo de descargarDocumento() ====");
		
		return "";		
	}
	
//	public void descargarDocumento() {
	public String descargarDocumento() {
		logger.debug("=== inicia descargarDocumento()-Revocados ====");
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		
//		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
//		String nombreDocumento = params.get("nombreArchivo");
//		logger.debug("[DESCARG_DOC]-nombreDocumento: "+nombreDocumento);
		logger.debug("[DESCARG_DOC]-nombreDocumento: "+ aliasCortoDocumento);
		String rutaDocumento = Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
//				+ File.separator + nombreDocumento;
				+ File.separator + aliasCortoDocumento;
		
		logger.debug("[DESCARG_DOC]-rutaDocumento: "+ rutaDocumento);
		String outputFileName = rutaDocumento;
		
		File outputPDF = new File(outputFileName);
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(outputPDF),10240);

			response.reset();
			response.setHeader("Content-Type", "application/pdf");
			response.setHeader("Content-Length",String.valueOf(outputPDF.length()));
//			response.setHeader("Content-Disposition", "attachment; filename=\""+ nombreDocumento + "\"");
			response.setHeader("Content-Disposition", "attachment; filename=\""+ aliasCortoDocumento + "\"");
			output = new BufferedOutputStream(response.getOutputStream(), 10240);

			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			logger.debug("[DescargDoc-Revoc]-finalizando OK OK");
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
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 2 al descargarDocumento: ",e);
			}
			try {
				input.close();
			} catch (IOException e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+ "IOException 3 al descargarDocumento: ",e);
			}
		}
		FacesContext.getCurrentInstance().responseComplete();
		
		logger.debug("=== saliendo de descargarDocumento() ====");
		
		return "";		
	}

	
	public String cargarUnicoPDF(String aliasArchivo) {
		logger.debug("== inicia cargarUnicoPDF()-Revocados ====");
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
			logger.debug("[cargarUnicoPDFRevoc]-Ubicacion Temporal:"+ sUbicacionTemporal);
			
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
									
			logger.debug("[cargarUnicoPDFRevoc]-NombreArchivoTEMP: " + sNombreTemporal);
			
			canalSalida = new FileOutputStream(fichTemp);
			canalSalida.write(fileBytes);
			
			canalSalida.flush();
			
			return sNombreTemporal;

		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"IO Exception-Revocados: ",e);
			String sMensaje = "Se produjo un error al adjuntar fichero";
			Utilitarios.mensajeInfo("", sMensaje);
			return "";
		} catch(Exception ex){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"Revocados: ",ex);
			String sMensaje = "Se produjo un error al adjuntar fichero.";
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
					logger.error("Error: ",x);
				}
			}
		}
	}	
	
	private String getDate(Date date)
    {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return df.format(date);
        } catch (Exception ex) {
        }
        return null;
    }

	public void limpiar() {
		revocados = null;
	}
	
	public void limpiarFiltros()
	{
		logger.debug("== limpiarFiltros ==");
		setObjTiivsPersonaBusqueda(new TiivsPersona());
		setObjTiivsPersonaBusquedaNombre(new TiivsPersona());
		setEstadoRevocado("");
		setFechaInicio(null);
		setFechaFin(null);
	}

	public String reset() {
		return "/faces/paginas/registrarRevocado.xhtml";
	}

	public String editar() {
		return "/faces/paginas/registrarRevocado.xhtml";
	}

	public String guardar() {
		return "/faces/paginas/bandejaRevocados.xhtml";
	}	

	public String getNroRegistros() {
		Integer nReg;
		if (revocados != null) {
			nReg = new Integer(revocados.size());
		} else {
			nReg = new Integer(0);
		}
		return nReg.toString();
	}

	public TiivsPersona getObjTiivsPersonaBusqueda() {
		return objTiivsPersonaBusqueda;
	}

	public void setObjTiivsPersonaBusqueda(TiivsPersona objTiivsPersonaBusqueda) {
		this.objTiivsPersonaBusqueda = objTiivsPersonaBusqueda;
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public List<Revocado> getRevocados() {
		return revocados;
	}

	public void setRevocados(List<Revocado> revocados) {
		this.revocados = revocados;
	}

	public void setNroRegistros(String nroRegistros) {
		this.nroRegistros = nroRegistros;
	}

	public TiivsPersona getObjTiivsPersonaBusquedaDlg() {
		return objTiivsPersonaBusquedaDlg;
	}

	public void setObjTiivsPersonaBusquedaDlg(
			TiivsPersona objTiivsPersonaBusquedaDlg) {
		this.objTiivsPersonaBusquedaDlg = objTiivsPersonaBusquedaDlg;
	}

	public TiivsPersona getObjTiivsPersonaAgregar() {
		return objTiivsPersonaAgregar;
	}

	public void setObjTiivsPersonaAgregar(TiivsPersona objTiivsPersonaAgregar) {
		this.objTiivsPersonaAgregar = objTiivsPersonaAgregar;
	}

	public List<TiivsPersona> getPersonaClientes() {
		return personaClientes;
	}

	public void setPersonaClientes(List<TiivsPersona> personaClientes) {
		this.personaClientes = personaClientes;
	}

	public TiivsPersona getSelectPersonaBusqueda() {
		return selectPersonaBusqueda;
	}

	public void setSelectPersonaBusqueda(TiivsPersona selectPersonaBusqueda) {
		this.selectPersonaBusqueda = selectPersonaBusqueda;
	}

	public TiivsPersona getSelectPersonaPendEdit() {
		return selectPersonaPendEdit;
	}

	public void setSelectPersonaPendEdit(TiivsPersona selectPersonaPendEdit) {
		this.selectPersonaPendEdit = selectPersonaPendEdit;
	}

	public TiivsPersona getSelectPersonaActEdit() {
		return selectPersonaActEdit;
	}
	
	public boolean isbBooleanPopup() {
		return bBooleanPopup;
	}

	public void setbBooleanPopup(boolean bBooleanPopup) {
		this.bBooleanPopup = bBooleanPopup;
	}

	public void setSelectPersonaActEdit(TiivsPersona selectPersonaActEdit) {
		this.selectPersonaActEdit = selectPersonaActEdit;
	}

	public Revocado getRevocadoVer() {
		return revocadoVer;
	}

	public void setRevocadoVer(Revocado revocadoVer) {
		this.revocadoVer = revocadoVer;
	}

	public List<Revocado> getPersonaClientesVer() {
		return personaClientesVer;
	}

	public void setPersonaClientesVer(List<Revocado> personaClientesVer) {
		this.personaClientesVer = personaClientesVer;
	}

	public List<Revocado> getPersonaClientesActivoEdit() {
		return personaClientesActivoEdit;
	}

	public void setPersonaClientesActivoEdit(
			List<Revocado> personaClientesActivoEdit) {
		this.personaClientesActivoEdit = personaClientesActivoEdit;
	}

	public List<Revocado> getPersonaClientesPendEdit() {
		return personaClientesPendEdit;
	}

	public void setPersonaClientesPendEdit(List<Revocado> personaClientesPendEdit) {
		this.personaClientesPendEdit = personaClientesPendEdit;
	}

	public Revocado getRevocadoEdit() {
		return revocadoEdit;
	}

	public void setRevocadoEdit(Revocado revocadoEdit) {
		this.revocadoEdit = revocadoEdit;
	}

//	public TiivsPersona getDeletePersonaEdit() {
//		return deletePersonaEdit;
//	}
//
//	public void setDeletePersonaEdit(TiivsPersona deletePersonaEdit) {
//		this.deletePersonaEdit = deletePersonaEdit;
//	}
	
	public TiivsRevocado getDeletePersonaEdit() {
		return deletePersonaEdit;
	}

	public void setDeletePersonaEdit(TiivsRevocado deletePersonaEdit) {
		this.deletePersonaEdit = deletePersonaEdit;
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

	public TiivsPersona getObjTiivsPersonaBusquedaNombre() {
		return objTiivsPersonaBusquedaNombre;
	}

	public void setObjTiivsPersonaBusquedaNombre(
			TiivsPersona objTiivsPersonaBusquedaNombre) {
		this.objTiivsPersonaBusquedaNombre = objTiivsPersonaBusquedaNombre;
	}

	public boolean isFlagRevocar() {
		return flagRevocar;
	}

	public void setFlagRevocar(boolean flagRevocar) {
		this.flagRevocar = flagRevocar;
	}

//	public List<TiivsPersona> getApoderadosNuevo() {
//		return apoderadosNuevo;
//	}
//
//	public void setApoderadosNuevo(List<TiivsPersona> apoderadosNuevo) {
//		this.apoderadosNuevo = apoderadosNuevo;
//	}
//
//	public List<TiivsPersona> getPoderdantesNuevo() {
//		return poderdantesNuevo;
//	}
//
//	public void setPoderdantesNuevo(List<TiivsPersona> poderdantesNuevo) {
//		this.poderdantesNuevo = poderdantesNuevo;
//	}
	
	public List<TiivsRevocado> getApoderadosNuevo() {
		return apoderadosNuevo;
	}

	public void setApoderadosNuevo(List<TiivsRevocado> apoderadosNuevo) {
		this.apoderadosNuevo = apoderadosNuevo;
	}

	public List<TiivsRevocado> getPoderdantesNuevo() {
		return poderdantesNuevo;
	}

	public void setPoderdantesNuevo(List<TiivsRevocado> poderdantesNuevo) {
		this.poderdantesNuevo = poderdantesNuevo;
	}

	public boolean isFlagGuardar() {
		return flagGuardar;
	}

	public void setFlagGuardar(boolean flagGuardar) {
		this.flagGuardar = flagGuardar;
	}

	

	public List<Integer> getListNumGrupo() {
		return listNumGrupo;
	}

	public void setListNumGrupo(List<Integer> listNumGrupo) {
		this.listNumGrupo = listNumGrupo;
	}

	public List<String> getListCodSoli() {
		return listCodSoli;
	}

	public void setListCodSoli(List<String> listCodSoli) {
		this.listCodSoli = listCodSoli;
	}

	public List<String> getListSolicResult() {
		return listSolicResult;
	}

	public void setListSolicResult(List<String> listSolicResult) {
		this.listSolicResult = listSolicResult;
	}

	public String getMsjResultRevocados() {
		return msjResultRevocados;
	}

	public void setMsjResultRevocados(String msjResultRevocados) {
		this.msjResultRevocados = msjResultRevocados;
	}

	public List<TiivsMultitabla> getListEstadoRevocados() {
		return listEstadoRevocados;
	}

	public void setListEstadoRevocados(List<TiivsMultitabla> listEstadoRevocados) {
		this.listEstadoRevocados = listEstadoRevocados;
	}

	public String getEstadoRevocado() {
		return estadoRevocado;
	}

	public void setEstadoRevocado(String estadoRevocado) {
		this.estadoRevocado = estadoRevocado;
	}

	public List<TiivsMultitabla> getListTipoRegistro() {
		return listTipoRegistro;
	}

	public void setListTipoRegistro(List<TiivsMultitabla> listTipoRegistro) {
		this.listTipoRegistro = listTipoRegistro;
	}

	public Revocado getRevocadoDelete() {
		return revocadoDelete;
	}

	public void setRevocadoDelete(Revocado revocadoDelete) {
		this.revocadoDelete = revocadoDelete;
	}
	
	public IILDPeUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(IILDPeUsuario usuario) {
		this.usuario = usuario;
	}

	public PDFViewerMB getPdfViewerMB() {
		return pdfViewerMB;
	}

	public void setPdfViewerMB(PDFViewerMB pdfViewerMB) {
		this.pdfViewerMB = pdfViewerMB;
	}

	public String getRutaArchivoExcel() {
		return rutaArchivoExcel;
	}

	public void setRutaArchivoExcel(String rutaArchivoExcel) {
		this.rutaArchivoExcel = rutaArchivoExcel;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getAncho_Popup_Revoc_Poder() {
		return ancho_Popup_Revoc_Poder;
	}

	public void setAncho_Popup_Revoc_Poder(String ancho_Popup_Revoc_Poder) {
		this.ancho_Popup_Revoc_Poder = ancho_Popup_Revoc_Poder;
	}

	public String getAlto_Popup_Revoc_Poder() {
		return alto_Popup_Revoc_Poder;
	}

	public void setAlto_Popup_Revoc_Poder(String alto_Popup_Revoc_Poder) {
		this.alto_Popup_Revoc_Poder = alto_Popup_Revoc_Poder;
	}

	public Boolean getDeshabilitarExportar() {
		return deshabilitarExportar;
	}

	public void setDeshabilitarExportar(Boolean deshabilitarExportar) {
		this.deshabilitarExportar = deshabilitarExportar;
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
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getPoderdante() {
		return poderdante;
	}

	public void setPoderdante(String poderdante) {
		this.poderdante = poderdante;
	}

	public String getApoderdante() {
		return apoderdante;
	}

	public void setApoderdante(String apoderdante) {
		this.apoderdante = apoderdante;
	}

	public String getAliasCortoDocumento() {
		return aliasCortoDocumento;
	}

	public void setAliasCortoDocumento(String aliasCortoDocumento) {
		this.aliasCortoDocumento = aliasCortoDocumento;
	}

	public String getsAliasTemporal() {
		return sAliasTemporal;
	}

	public void setsAliasTemporal(String sAliasTemporal) {
		this.sAliasTemporal = sAliasTemporal;
	}

	public Integer getFlagLinkRevocados() {
		return flagLinkRevocados;
	}

	public void setFlagLinkRevocados(Integer flagLinkRevocados) {
		this.flagLinkRevocados = flagLinkRevocados;
	}

	public String getCadenaEscanerFinal() {
		return cadenaEscanerFinal;
	}

	public void setCadenaEscanerFinal(String cadenaEscanerFinal) {
		this.cadenaEscanerFinal = cadenaEscanerFinal;
	}
	
	public VisadoDocumentosMB getVisadoDocumentosMB() {
		return visadoDocumentosMB;
	}

	public void setVisadoDocumentosMB(VisadoDocumentosMB visadoDocumentosMB) {
		this.visadoDocumentosMB = visadoDocumentosMB;
	}
}

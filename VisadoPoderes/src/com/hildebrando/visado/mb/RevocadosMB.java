package com.hildebrando.visado.mb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
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

@ManagedBean(name = "revocadosMB")
@SessionScoped
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
	
	private IILDPeUsuario usuario;
	
	boolean bBooleanPopup = false;
	
	
	private TiivsPersona deletePersonaEdit;

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
	
	private List<TiivsPersona> apoderadosNuevo;
	private List<TiivsPersona> poderdantesNuevo;
	
	private String msjResultRevocados;
	private String ancho_Popup_Revoc_Poder;
	private String alto_Popup_Revoc_Poder;

	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	
	private Boolean deshabilitarExportar=true;

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public RevocadosMB() {
		combosMB = new CombosMB();
		
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		
		objTiivsPersonaBusqueda = new TiivsPersona();
		objTiivsPersonaBusquedaNombre = new TiivsPersona();
		
		objTiivsPersonaBusquedaDlg = new TiivsPersona();
		
		estadoRevocado= "S";
		listSolicResult = new ArrayList<String>();
		ancho_Popup_Revoc_Poder=(String) Utilitarios.getObjectInSession("ANCHO_POPUP_REVOC_PODER");
		alto_Popup_Revoc_Poder=(String) Utilitarios.getObjectInSession("ALTO_POPUP_REVOC_PODER");
		
		cargarCombos();
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
			logger.debug("Error al obtener archivo excel debido a: " + e.getMessage());
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
			if (filtro.equals(ConstantesVisado.APODERADO))
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
		
		for (TiivsParametros tmp: pdfViewerMB.getLstParametros())
		{
			if (usuario.getUID().equals(tmp.getCodUsuario()))
			{
				res=tmp.getRutaArchivoExcel();
				break;
			}
		}
		
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
	public void revocarApodPod() {
		try {
			logger.info("**************** revocarActivar  ***************************" );
			
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
		listaSolicitudesDeDondeComparar=serviceSolicitud.buscarDinamico(filtro);
		logger.info("******** Tamanio de la lista de Solicitudes donde buscar ************* "+listaSolicitudesDeDondeComparar.size());
		
		List<String> listaCodSolicitudes =new ArrayList<String>();
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
		for (TiivsPersona a : revocadoEdit.getApoderados()) {
			lstRevocadosComboDto.add(new ComboDto(a.getCodPer()+"", ConstantesVisado.APODERADO));
		}
		for (TiivsPersona b : revocadoEdit.getPoderdantes()) {
			lstRevocadosComboDto.add(new ComboDto(b.getCodPer()+"", ConstantesVisado.PODERDANTE));
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
					listaPersonasXAgrupacionXSolicitud.add(new ComboDto(x.getCodPer().toString(),x.getTipPartic(),x.getNumGrupo()));
				}
			}
			
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
			/*	    for (ComboDto comboDto : m.getListaPersonas()) {
						System.out.println("###### comboDto.getKey()  " +  comboDto.getKey() +" %%%%% " +comboDto.getDescripcion() );
					}
					for (ComboDto comboDto : lstRevocadosComboDto) {
						System.out.println("&&&&&&& comboDto.getKey()  " +  comboDto.getKey() +" T_T " +comboDto.getDescripcion() );
					}
					*/
					for (ComboDto an : m.getListaPersonas()) {
						for (ComboDto bn : lstRevocadosComboDto) {
							
							if(an.getKey().equals(bn.getKey()) 
									&& an.getDescripcion().equals(bn.getDescripcion()) ){
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
			e.printStackTrace();
		}
		
	}
	
	//@Samira 09/03/2012
	public void actualizarEstadoRevocacionActivo() throws Exception{
		List<TiivsRevocado> listaTiivsRevocado=new ArrayList<TiivsRevocado>();
		GenericDao<TiivsRevocado, Object> serviceTiivsRevocado = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsRevocado = Busqueda.forClass(TiivsRevocado.class);
		filtroTiivsRevocado.add(Restrictions.eq("codAgrup", Integer.parseInt(revocadoEdit.getCodAgrupacion())));
		listaTiivsRevocado=serviceTiivsRevocado.buscarDinamico(filtroTiivsRevocado);
		for (TiivsRevocado yy : listaTiivsRevocado) {
			yy.setEstado(ConstantesVisado.ESTADOS.ESTADO_ACTIVO_REVOCADO);
			serviceTiivsRevocado.modificar(yy);
		}
	}
	//@Samira 09/03/2012
	public void actualizarEstadoA_Revocado_SolicitudAgrupacion(List<TiivsSolicitudAgrupacionId> listaTiivsSolicitudAgrupacionId) throws Exception{
		
		for (TiivsSolicitudAgrupacionId x : listaTiivsSolicitudAgrupacionId) {
			System.out.println(x.toString());
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
		   
			
			List<TiivsSolicitudAgrupacion> listaTiivsSolicitudAgrupacion=new ArrayList<TiivsSolicitudAgrupacion>();
			GenericDao<TiivsSolicitudAgrupacion, Object> serviceSolicitudAgrupacionPersona = (GenericDao<TiivsSolicitudAgrupacion, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroSolicitudAgrupacion = Busqueda.forClass(TiivsSolicitudAgrupacion.class);
			//filtroSolicitudAgrupacion.add(Restrictions.eq("estado", ""))
			listaTiivsSolicitudAgrupacion=serviceSolicitudAgrupacionPersona.buscarDinamico(filtroSolicitudAgrupacion);
			logger.info("Tmanio listaTiivsSolicitudAgrupacion desde bd :::" +listaTiivsSolicitudAgrupacion.size());
			List<String> lstString = new ArrayList<String>();
			 lstString = this.listaCodSoli(listaTiivsSolicitudAgrupacion);
			 if(lstString!=null){
			for (String cc : lstString) {
			int i=0,j=0;
             for (TiivsSolicitudAgrupacion vv : listaTiivsSolicitudAgrupacion) {
            	 if(vv!=null){
            	 if(vv.getId()!=null){
				if(cc.equals(vv.getId().getCodSoli())){
					j++;
					if(vv.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_3)){
						i++;
					}
				    }
            	 }
            	}
			}
            // logger.info("Total "  +" Contador i  "+i + " Contador j : " +j) ;
             if(i==j){
            	 if(i!=0&&j!=0){
            	 logger.info(" Todas las combinaciones estan revocadas , se cambiara el estado de la solicitud, segun corresponda");
            	 solicitudes.add(cc);
            	 }
             }else{
            	// logger.info(" No todas las combinaciones estan revocadas, no se cambia el estado de la solicitud");
             }
             
		}
			
			/** Actualizar Estado Solicitudes**/
			this.actualizarEstadoSolicitudes(solicitudes);
		}	  
			
	}
	
	//@Samira 09/03/2012
	public void actualizarEstadoSolicitudes(List<String> solicitudes) throws Exception{

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
			
		List<TiivsSolicitud> listaTiivsSolicitudRechazdas=new ArrayList<TiivsSolicitud>();
		List<TiivsSolicitud> listaTiivsSolicitudImprocedentes=new ArrayList<TiivsSolicitud>();
		GenericDao<TiivsSolicitud, Object> serviceSolicitudAgrupacionPersona = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSolicitudRechazado = Busqueda.forClass(TiivsSolicitud.class);
		filtroSolicitudRechazado.add(Restrictions.in("estado", codigosParaPasarSolicitudRechazada));
		filtroSolicitudRechazado.add(Restrictions.in("codSoli", solicitudes));
		listaTiivsSolicitudRechazdas=serviceSolicitudAgrupacionPersona.buscarDinamico(filtroSolicitudRechazado);
		for (TiivsSolicitud xx : listaTiivsSolicitudRechazdas) {
			xx.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
			xx.setObs(parametroGlosa.getValor1());
			xx=serviceSolicitudAgrupacionPersona.modificar(xx);
			registraSolicitudHistorial(xx);
		}
		
		Busqueda filtroSolicitudImprocedente = Busqueda.forClass(TiivsSolicitud.class);
		filtroSolicitudImprocedente.add(Restrictions.in("estado", codigosParaPasarSolicitudImprocedente));
		filtroSolicitudImprocedente.add(Restrictions.in("codSoli", solicitudes));
		listaTiivsSolicitudImprocedentes=serviceSolicitudAgrupacionPersona.buscarDinamico(filtroSolicitudImprocedente);
		for (TiivsSolicitud xx : listaTiivsSolicitudImprocedentes) {
			xx.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02);
			xx.setObs(parametroGlosa.getValor1());
			xx=serviceSolicitudAgrupacionPersona.modificar(xx);
			registraSolicitudHistorial(xx);
		}
		
	}
	
	public void registraSolicitudHistorial (TiivsSolicitud solicitudModificado) throws Exception{
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		SolicitudDao<String, Object> serviceMaxMovi = (SolicitudDao<String, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		String numeroMovimiento = serviceMaxMovi.obtenerMaximoMovimiento(solicitudModificado.getCodSoli());

		int num = 0;
		if (!numeroMovimiento.equals("")) {
			num = Integer.parseInt(numeroMovimiento) + 1;
		} else {
			num = 1;
		}
		numeroMovimiento = num + "";
		  TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
		  objHistorial.setId(new TiivsHistSolicitudId(solicitudModificado.getCodSoli(),numeroMovimiento));
		  objHistorial.setEstado(solicitudModificado.getEstado());
		  objHistorial.setNomUsuario(usuario.getNombre());
		  objHistorial.setObs(solicitudModificado.getObs());
		  objHistorial.setFecha(new Timestamp(new Date().getTime()));
		  objHistorial.setRegUsuario(usuario.getUID());
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
		
		
	}

	
	
	public void verRevocado() {

		personaClientesVer = new ArrayList<Revocado>();
		personaClientesVer.add(revocadoVer);

	}
	
	public void editPendRevocado() {
		
		objTiivsPersonaBusquedaDlg= new TiivsPersona();
		objTiivsPersonaAgregar = new TiivsPersona();

		personaClientesPendEdit = new ArrayList<Revocado>();
		personaClientesPendEdit.add(revocadoEdit);
		
		setFlagRevocar(true);
		setFlagGuardar(false);
	}
	
	public void editPendRevocadoNuevo() {
		
		objTiivsPersonaBusquedaDlg= new TiivsPersona();
		objTiivsPersonaAgregar = new TiivsPersona();

		personaClientesPendEdit = new ArrayList<Revocado>();
		apoderadosNuevo = new ArrayList<TiivsPersona>();
		poderdantesNuevo = new ArrayList<TiivsPersona>();
		
		setFlagRevocar(false);
		setFlagGuardar(true);

	}
	
	public void editActRevocado() {

		personaClientesActivoEdit = new ArrayList<Revocado>();
		personaClientesActivoEdit.add(revocadoEdit);

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
			Utilitarios.mensajeError("ERROR", e.getMessage());
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
		buscarRevocado();
		
	}
	
	public void limpiarCriteriosBusqueda() {
//		objTiivsPersonaBusquedaDlg.setCodCen("");
//		objTiivsPersonaBusquedaDlg.setCodCen("");
//		objTiivsPersonaAgregar.setClasifPer("");
//		objTiivsPersonaAgregar.setClasifPerOtro("");
//		objTiivsPersonaAgregar.setEmail("");
//		objTiivsPersonaAgregar.setNumCel("");
		
		objTiivsPersonaBusquedaDlg= new TiivsPersona();
		objTiivsPersonaAgregar = new TiivsPersona();
		
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
		
		if(isFlagRevocar()==true){
			
			logger.info("****************** agregarRevocado ********************");
			if (validarRevocado()) {
				if (validarRegistroDuplicado()) {

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
					
					GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit
							.getApplicationContext().getBean("genericoDao");
					Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
					
					try {
						tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(personaClientesPendEdit.get(0).getCodAgrupacion()))));
					} catch (Exception e) {
						e.printStackTrace();
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
						
						e.printStackTrace();
					}
					
					
					List<TiivsPersona> apoderados= new ArrayList<TiivsPersona>();
					List<TiivsPersona> poderdantes= new ArrayList<TiivsPersona>();
					TiivsPersona apoderado;
					TiivsPersona poderdante;
					List<Revocado> revocadosAux= new ArrayList<Revocado>();
						
						for(TiivsRevocado tiivsRevocado:tiivsrevocados){
							
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO)){
									
									apoderado= new TiivsPersona();
									apoderado = tiivsRevocado.getTiivsPersona();
									
									String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
									
									
									apoderado.setsDesctipDoi( descDoiApod);
									apoderado.setsDesctipPartic(descTipPart);
									
									apoderados.add(apoderado);
								}
									
								if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
									
									poderdante = new TiivsPersona();
									poderdante = tiivsRevocado.getTiivsPersona();
									
									String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
									String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
									
									
									
									poderdante.setsDesctipDoi( descDoiPod);
									poderdante.setsDesctipPartic(descTipPart);
									
									poderdantes.add(poderdante);
								}
						
						
						
					}
					
						Revocado revocado = new Revocado();
						revocado.setCodAgrupacion(personaClientesPendEdit.get(0).getCodAgrupacion());
						revocado.setApoderados(apoderados);
						revocado.setPoderdantes(poderdantes);
					
						revocadosAux.add(revocado);
					
						personaClientesPendEdit = revocadosAux;
						objTiivsPersonaSeleccionado = new TiivsPersona();
					
				}

			}
			
		}else{
			
			TiivsPersona apoderado;
			TiivsPersona poderdante;
			int maximoCodAgrupacion = getMaximoCodAgrupacion();
			List<Revocado> revocadosAux= new ArrayList<Revocado>();
			
			if(objTiivsPersonaAgregar.getTipPartic().equals(ConstantesVisado.APODERADO)){
				
				apoderado= new TiivsPersona();
				apoderado = objTiivsPersonaAgregar;
				
				String descDoiApod =  getValor1(objTiivsPersonaAgregar.getTipDoi(),listDocumentos);
				String descTipPart =  getValor1(objTiivsPersonaAgregar.getTipPartic(), listTipoRegistro);
				
				apoderado.setsDesctipDoi( descDoiApod);
				apoderado.setsDesctipPartic(descTipPart);
				
				apoderadosNuevo.add(apoderado);
			}
							
			if(objTiivsPersonaAgregar.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
				
				poderdante = new TiivsPersona();
				poderdante = objTiivsPersonaAgregar;
				
				String descDoiPod =  getValor1(objTiivsPersonaAgregar.getTipDoi(),listDocumentos);
				String descTipPart =  getValor1(objTiivsPersonaAgregar.getTipPartic(), listTipoRegistro);
				
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
	
	public void guardarApodPod(ActionEvent  actionEvent){
		
		TiivsRevocado tiivsRevocadoAux;
		
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		int flag=0;
		logger.info("personaClientesPendEdit.size :::: " +personaClientesPendEdit.size());
		for(Revocado revocado: personaClientesPendEdit){
			
			for(TiivsPersona tiivsPersona:revocado.getApoderados()){
				
				tiivsRevocadoAux= new TiivsRevocado();
				tiivsRevocadoAux.setCodRevocado(0);
				tiivsRevocadoAux.setTiivsPersona(tiivsPersona);
				tiivsRevocadoAux.setTipPartic(tiivsPersona.getTipPartic());
				tiivsRevocadoAux.setFechaRevocatoria(new Date());
				tiivsRevocadoAux.setEstado(ConstantesVisado.ESTADOS.ESTADO_PENDIENTE_REVOCADO);
				tiivsRevocadoAux.setCodAgrup(Integer.parseInt(revocado.getCodAgrupacion()));
				
				try {
					service.save(tiivsRevocadoAux);
					flag=1;
				} catch (Exception e) {
					logger.error("error al guardar revocado");
				}
			}
			
			for(TiivsPersona tiivsPersona:revocado.getPoderdantes()){
				
				tiivsRevocadoAux= new TiivsRevocado();
				tiivsRevocadoAux.setCodRevocado(0);
				tiivsRevocadoAux.setTiivsPersona(tiivsPersona);
				tiivsRevocadoAux.setTipPartic(tiivsPersona.getTipPartic());
				tiivsRevocadoAux.setFechaRevocatoria(new Date());
				tiivsRevocadoAux.setEstado(ConstantesVisado.ESTADOS.ESTADO_PENDIENTE_REVOCADO);
				tiivsRevocadoAux.setCodAgrup(Integer.parseInt(revocado.getCodAgrupacion()));
				
				try {
					service.save(tiivsRevocadoAux);
					flag=1;
				} catch (Exception e) {
					logger.error("error al guardar revocado");
				}
			}
			
		}
		
		if(flag==1){
			
			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Guardado!",
					"Registros agregados correctamente!");
			FacesContext.getCurrentInstance().addMessage(null, msg);

			
		}
		
		bBooleanPopup=false;
	}
	
	public int getMaximoCodAgrupacion(){
		
		List<TiivsRevocado> tiivsrevocados= new ArrayList<TiivsRevocado>();
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		
		try {
			tiivsrevocados = service.buscarDinamico(filtro.addOrder(Order.desc("codAgrup")));
		} catch (Exception e) {
			e.printStackTrace();
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
		
		List<TiivsRevocado> tiivsrevocados= new ArrayList<TiivsRevocado>();
		
		List<TiivsRevocado> tiivsrevocados2= new ArrayList<TiivsRevocado>();
		
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		Busqueda filtro2 = Busqueda.forClass(TiivsRevocado.class);
		
		
		String codAgrup= personaClientesPendEdit.get(0).getCodAgrupacion();
		
		try {
			filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(codAgrup)));
			tiivsrevocados = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsPersona.codPer", deletePersonaEdit.getCodPer())));
			
			if (tiivsrevocados!=null)
			{
				if (tiivsrevocados.size()>0)
				{
					service.eliminar(tiivsrevocados.get(0));
				}
			}
						
			tiivsrevocados2 = service.buscarDinamico(filtro2.add(Restrictions.eq("codAgrup", Integer.parseInt(codAgrup))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<TiivsPersona> apoderados= new ArrayList<TiivsPersona>();
		List<TiivsPersona> poderdantes= new ArrayList<TiivsPersona>();
		TiivsPersona apoderado;
		TiivsPersona poderdante;
		List<Revocado> revocadosAux= new ArrayList<Revocado>();
			
		for(TiivsRevocado tiivsRevocado:tiivsrevocados2)
		{
				if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO))
				{
					apoderado= new TiivsPersona();
					apoderado = tiivsRevocado.getTiivsPersona();
					
					String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
					String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
					
					
					apoderado.setsDesctipDoi( descDoiApod);
					apoderado.setsDesctipPartic(descTipPart);
					
					apoderados.add(apoderado);
				}
					
				if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE))
				{
					poderdante = new TiivsPersona();
					poderdante = tiivsRevocado.getTiivsPersona();
					
					String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
					String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
					
					
					
					poderdante.setsDesctipDoi( descDoiPod);
					poderdante.setsDesctipPartic(descTipPart);
					
					poderdantes.add(poderdante);
				}
		}
	
		Revocado revocado = new Revocado();
		revocado.setCodAgrupacion(codAgrup);
		revocado.setApoderados(apoderados);
		revocado.setPoderdantes(poderdantes);
	
		revocadosAux.add(revocado);
		
		
		personaClientesPendEdit = revocadosAux;
		
		
		
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
			
			for(TiivsPersona  tiivsPersona:rev.getApoderados()){
				if (objTiivsPersonaAgregar.getTipDoi().equals(tiivsPersona.getTipDoi())
						&& objTiivsPersonaAgregar.getNumDoi().equals(tiivsPersona.getNumDoi())) {
					bResult = false;
					sMensaje = "Ya existe una persona con número de Doi :  "
							+ tiivsPersona.getNumDoi() ;
					Utilitarios.mensajeInfo("INFO", sMensaje);
					break;
				}
			}
			
			if(bResult){
				
				for(TiivsPersona  tiivsPersona:rev.getPoderdantes()){
					
					if (objTiivsPersonaAgregar.getTipDoi().equals(tiivsPersona.getTipDoi())
							&& objTiivsPersonaAgregar.getNumDoi().equals(tiivsPersona.getNumDoi())) {
						bResult = false;
						sMensaje = "Ya existe una persona con número de Doi :  "
								+ tiivsPersona.getNumDoi() ;
						Utilitarios.mensajeInfo("INFO", sMensaje);
						break;
					}
				}
				
			}
		}
		
		return bResult;
		
	}

	
	public boolean validarRevocado() {
		logger.info("******************************* validarRevocado ******************************* "
				+ objTiivsPersonaAgregar.getTipPartic());
		boolean bResult = true;
		String sMensaje = "";
		
		if (objTiivsPersonaAgregar.getTipDoi() == null ||  objTiivsPersonaAgregar.getTipDoi().equals("")) {
			sMensaje = "Seleccione el Tipo de Documento";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		if (objTiivsPersonaAgregar.getNumDoi() == null || objTiivsPersonaAgregar.getNumDoi().equals("")) {
			sMensaje = "Ingrese el Número de Doi";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		
	/*	if (objTiivsPersonaAgregar.getCodCen() == null ||  objTiivsPersonaAgregar.getCodCen().equals("")) {
			sMensaje = "Ingrese el Cod Cen";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		*/
		if (objTiivsPersonaAgregar.getApePat() == null ||  objTiivsPersonaAgregar.getApePat().equals("")) {
			sMensaje = "Ingrese el Apellido Paterno";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		
		if (objTiivsPersonaAgregar.getApeMat() == null  || objTiivsPersonaAgregar.getApeMat().equals("")) {
			sMensaje = "Ingrese el Apellido Materno";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		
		if (objTiivsPersonaAgregar.getNombre() == null  ||  objTiivsPersonaAgregar.getNombre().equals("")) {
			sMensaje = "Ingrese el Nombre";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
		}
		
		if ((objTiivsPersonaAgregar.getTipPartic() == null || objTiivsPersonaAgregar.getTipPartic().equals(""))) {
			sMensaje = "Ingrese el Tipo de Participacion";
			bResult = false;
			Utilitarios.mensajeInfo("INFO", sMensaje);
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

		List<TiivsMultitabla> tiivsMultitablas2 = new ArrayList<TiivsMultitabla>();
		
		GenericDao<TiivsMultitabla, Object> service3 = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		Busqueda filtro4 = Busqueda.forClass(TiivsMultitabla.class);
		filtro4.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		
		try {
			tiivsMultitablas2 = service3.buscarDinamico(filtro4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//aqui lo inactivamos
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
			

			logger.debug("exitoso al inactivar revocados!");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block

			logger.debug("error al inactivar revocados!" + e.toString());
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
			e.printStackTrace();
		}

		for (TiivsPersona pers : lstTiivsPersonaBusqueda) 
		{
			if(pers!= null)
			{	
				if (pers.getApePat()!=null && pers.getApeMat()!=null)
				{
					if (pers.getNombre().toUpperCase() != ""
							&& pers.getApePat().toUpperCase() != ""
							&& pers.getApeMat().toUpperCase() != "") 
					{
						
						String nombreCompletoMayuscula = pers.getNombre().toUpperCase()
								+ " " + pers.getApePat().toUpperCase() + " "
								+ pers.getApeMat().toUpperCase();

						String nombreCompletoMayusculaBusqueda = pers.getApePat()
								.toUpperCase()
								+ " "
								+ pers.getApeMat().toUpperCase()
								+ " " + pers.getNombre().toUpperCase();

						if (nombreCompletoMayusculaBusqueda.contains(query
								.toUpperCase())) {

							pers.setNombreCompletoMayuscula(nombreCompletoMayuscula);

							lstTiivsPersonaResultado.add(pers);
						}
					}
				}
				
			}
		}

		return lstTiivsPersonaResultado;
	}

	public void buscarRevocado() {
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
				filtro.add(Restrictions.eq("persona.nombre", objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[0]));
				filtro.add(Restrictions.eq("persona.apePat", objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[1]));
				filtro.add(Restrictions.eq("persona.apeMat", objTiivsPersonaBusquedaNombre.getNombreCompletoMayuscula().split(" ")[2]));
				
			}
			
		}
		
		if(estadoRevocado.compareTo("S")!=0){
			
			filtro.add(Restrictions.eq("estado", estadoRevocado));
		}
		
		if(fechaInicio!= null ||  fechaFin!=null){

			filtro.add(Restrictions.between("fechaRevocatoria", fechaInicio, fechaFin));
		}
		
		
		try {
			lstRevocadosPre = service.buscarDinamico(filtro.addOrder(Order.desc("codAgrup")).addOrder(Order.desc("fechaRevocatoria")));
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("error al obtener la lista de revocados "+  e.toString());
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
		
		try {
			tiivsrevocados = serviceRev.buscarDinamico(filtroRev.addOrder(Order.desc("codAgrup")).addOrder(Order.desc("fechaRevocatoria")));
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("error al obtener la lista final de revocados "+  e.toString());
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
		List<TiivsPersona> apoderados;
		TiivsPersona apoderado;
		
		List<TiivsPersona> poderdantes;
		TiivsPersona poderdante;
		 List<Integer>  listCodAgrup =  obtenerListCodAgrupacion();
		
		if(listCodAgrup != null){
			
			if(listCodAgrup.size() > 0){
				
				for(Integer tiivsRevocado2:listCodAgrup){
					numCorrelativo++;
					apoderados= new ArrayList<TiivsPersona>();
					poderdantes= new ArrayList<TiivsPersona>();
					for(TiivsRevocado tiivsRevocado:tiivsrevocados){
						
						if(tiivsRevocado.getCodAgrup().compareTo(tiivsRevocado2)==0){
							
							fecha=getDate(tiivsRevocado.getFechaRevocatoria());
							estado=getValor1(tiivsRevocado.getEstado(),listEstados);
							
							if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO)){
								
								apoderado= new TiivsPersona();
								apoderado = tiivsRevocado.getTiivsPersona();
								
								String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
								String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
								
								nombreCompletoApoderados = nombreCompletoApoderados
																+ " " + descDoiApod
																+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
															  + " - " + (tiivsRevocado.getTiivsPersona().getApePat()==null?"":tiivsRevocado.getTiivsPersona().getApePat()) 
																+ " " + (tiivsRevocado.getTiivsPersona().getApeMat()==null?"":tiivsRevocado.getTiivsPersona().getApeMat())
																+ " " + (tiivsRevocado.getTiivsPersona().getNombre()==null?"":tiivsRevocado.getTiivsPersona().getNombre()) + "\n";
								
								apoderado.setsDesctipDoi( descDoiApod);
								apoderado.setsDesctipPartic(descTipPart);
								
								apoderados.add(apoderado);
							}
								
							if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
								
								poderdante = new TiivsPersona();
								poderdante = tiivsRevocado.getTiivsPersona();
								
								String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),listDocumentos);
								String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), listTipoRegistro);
								
										
								nombreCompletoPoderdantes = nombreCompletoPoderdantes 
																	+ " " + descDoiPod
																	+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
																	 + " - " + (tiivsRevocado.getTiivsPersona().getApePat()==null?"":tiivsRevocado.getTiivsPersona().getApePat()) 
																+ " " + (tiivsRevocado.getTiivsPersona().getApeMat()==null?"":tiivsRevocado.getTiivsPersona().getApeMat())
																+ " " + (tiivsRevocado.getTiivsPersona().getNombre()==null?"":tiivsRevocado.getTiivsPersona().getNombre()) + "\n";
								
								poderdante.setsDesctipDoi( descDoiPod);
								poderdante.setsDesctipPartic(descTipPart);
								
								poderdantes.add(poderdante);
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
			
			logger.debug("error al obtener la lista de cod de agrupacion "+  e.toString());
		}
		
		return listCodAgrup;
		
		
		
	}
	
	private String getValor1(String codElem, List<TiivsMultitabla> multitabla){
		
		for(TiivsMultitabla obj:multitabla){
			
			if(obj.getId().getCodElem().compareTo(codElem)==0){
				return obj.getValor1();
			}
			
		}
		
		return "";
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

	public String reset() {
		return "/faces/paginas/registrarRevocado.xhtml";
	}

	public String editar() {
		System.out.print("editar");
		return "/faces/paginas/registrarRevocado.xhtml";
	}

	public String guardar() {
		System.out.print("guardar");
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

	public TiivsPersona getDeletePersonaEdit() {
		return deletePersonaEdit;
	}

	public void setDeletePersonaEdit(TiivsPersona deletePersonaEdit) {
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

	public List<TiivsPersona> getApoderadosNuevo() {
		return apoderadosNuevo;
	}

	public void setApoderadosNuevo(List<TiivsPersona> apoderadosNuevo) {
		this.apoderadosNuevo = apoderadosNuevo;
	}

	public List<TiivsPersona> getPoderdantesNuevo() {
		return poderdantesNuevo;
	}

	public void setPoderdantesNuevo(List<TiivsPersona> poderdantesNuevo) {
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
}

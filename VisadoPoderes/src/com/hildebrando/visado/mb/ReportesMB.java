package com.hildebrando.visado.mb;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsTerritorio;

@ManagedBean(name = "reportesMB")
@SessionScoped
public class ReportesMB 
{
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	
	private List<TiivsSolicitud> solicitudes;
	private List<TiivsAgrupacionPersona> lstAgrupPer;
	private List<TiivsHistSolicitud> lstHistorial;
	private List<String> lstEstudioSelected;
	private List<String> lstEstadoNivelSelected;
	private List<String> lstTipoSolicitudSelected;
	private List<String> lstEstadoSelected;
	private List<String> lstNivelSelected;
	private List<String> lstSolicitudesSelected;
	private List<String> lstSolicitudesxOpeBan;
	private Date fechaInicio;
	private Date fechaFin;
	private String nombreArchivoExcel;
	private String rutaArchivoExcel;
	private StreamedContent file;  
	private IILDPeUsuario usuario;
	private String PERFIL_USUARIO ;
	
	public static Logger logger = Logger.getLogger(ReportesMB.class);
	
	public ReportesMB()
	{
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");	
		PERFIL_USUARIO=(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		generarNombreArchivo();
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
	
	public String obtenerFechaHoraActual()
	{
		String fechaActualizacion="";
		String horaActualizacion="";
		
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		fechaActualizacion = fecha.substring(0, 2) + ConstantesVisado.SLASH + fecha.substring(3, 5) + ConstantesVisado.SLASH + fecha.substring(6, fecha.length());

		java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("HH:mm:ss");
		horaActualizacion = sdf2.format(date);
		
		return fechaActualizacion + ConstantesVisado.GUION + horaActualizacion;		
	}
	
	public void exportarExcelPOI()
	{
		crearExcel();
	}
	
	// Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a
	// los filtros seleccionados.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	@SuppressWarnings("unchecked")
	public void buscarSolicitudes() 
	{
		logger.info("Buscando solicitudes a exportar");
		
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		
		//Busqueda por fecha de Registro
		if (getFechaInicio()!=null && getFechaFin()!=null)
		{
			LocalDate newFechaInicio = null;
			LocalDate newFechaFin = null;
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy");
			DateFormat formato = new SimpleDateFormat("dd/MM/yy");
			
			String tmpFecIni = formato.format(getFechaInicio());
			String tmpFecFin = formato.format(getFechaFin());
			
			newFechaInicio=fmt.parseLocalDate(tmpFecIni);
			newFechaFin= fmt.parseLocalDate(tmpFecFin);
			
			Date dFechaIni = newFechaInicio.toDateTimeAtStartOfDay().toDate();
			Date dFechaFin = newFechaFin.toDateTimeAtStartOfDay().toDate();
			
			filtroSol.add(Restrictions.between(ConstantesVisado.CAMPO_FECHA_REGISTRO, dFechaIni,dFechaFin));
		}
				
		//Busqueda por estado de la solicitud
		if (lstEstadoSelected.size() > 0) 
		{
			int ind = 0;

			for (; ind <= lstEstadoSelected.size() - 1; ind++) 
			{
				logger.info("Filtro por estados: " + lstEstadoSelected.get(ind));
			}
			
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,lstEstadoSelected));
		}
		
		// Buscar solicitudes de acuerdo a criterios seleccionados
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes: " + ex.getStackTrace());
		}
	}
	
	public void generarNombreArchivo() 
	{
		String grupoSSJJ = (String) Utilitarios.getObjectInSession("GRUPO_JRD");
		String grupoADM = (String) Utilitarios.getObjectInSession("GRUPO_ADM");
		String grupoOFI = (String) Utilitarios.getObjectInSession("GRUPO_OFI");
		String rol="";
		
		if (grupoSSJJ!=null)
		{
			rol="SSJJ";
		}
		else if (grupoADM!=null)
		{
			rol="ADM";
		}
		else if (grupoOFI!=null)
		{
			rol="OFI";
		}
		
		setNombreArchivoExcel("Solicitudes_Visado "	+ obtenerFechaArchivoExcel() + ConstantesVisado.UNDERLINE + rol);
	}

	public String obtenerFechaArchivoExcel() 
	{
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		String nuevaFecha = fecha.substring(0, 2) + "" + fecha.substring(3, 5) + "" + fecha.substring(6, fecha.length());

		return nuevaFecha;
	}
	
	private void crearExcel() 
	{
		try 
		{
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb.createSheet(obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			if (solicitudes.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un diseño a la celda
				crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ITEM, true, true,false);
				crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_NRO_SOLICITUD, true, true,false);
				crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_COD_OFICINA, true, true,false);
				crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_OFICINA, true, true,false);
				crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, true, true,false);
				crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ESTADO, true, true,false);
				crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_IMPORTE, true, true,false);
				crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_PODERDANTE, true, true,false);
				crearCell(wb, rowT, 8, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_APODERADO, true, true,false);
				crearCell(wb, rowT, 9, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TIPO_SOL, true, true,false);
				crearCell(wb, rowT, 10, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TIPO_OPE, true, true,false);
				crearCell(wb, rowT, 11, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ESTUDIO, true, true,false);
				crearCell(wb, rowT, 12, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_NIVEL, true, true,false);
				crearCell(wb, rowT, 13, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_ENVIO, true, true,false);
				crearCell(wb, rowT, 14, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_RPTA, true, true,false);
				crearCell(wb, rowT, 15, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_ESTADO, true, true,false);
				crearCell(wb, rowT, 16, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_COMISION, true, true,false);
				crearCell(wb, rowT, 17, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_LIBERADO, true, true,false);
				crearCell(wb, rowT, 18, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_DELEGADO, true, true,false);
				crearCell(wb, rowT, 19, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_EN_REVISION, true, true,false);
				crearCell(wb, rowT, 20, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_REVOCATORIA, true, true,false);
				
				int numReg=13;
				int contador=0;
				for (TiivsSolicitud tmp: solicitudes)
				{
					contador++;
					//Columna Item en Excel
					Row row = sheet.createRow((short) numReg);
					if (contador<=9)
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.TRES_CEROS + contador, true, false,true);
					}
					else if (contador<=99 && contador >9)
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, ConstantesVisado.DOS_CEROS + contador, true, false,true);
					}
					else if(contador>=99 && contador<999)
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.CERO + contador, true, false,true);
					}
					else
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(contador), true, false,true);
					}
					
					//Columna Nro Solicitud en Excel
					crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCodSoli(), true, false,true);
										
					//Columna Cod Oficina en Excel
					crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsOficina1().getCodOfi()),true, false,true);
					
					//Columna Oficina en Excel
					crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsOficina1().getDesOfi()),true, false,true);
					
					//Columna Territorio en Excel
					crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarDesTerritorio(tmp.getTiivsOficina1().getTiivsTerritorio().getCodTer()),true, false,true);
					
					//Columna Estado en Excel
					crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtEstado(), true, false,true);
					
					//Columna Importe en Excel
					crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtImporte(), true, false,true);
					
					//Columna Poderdante en Excel
					crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtPoderdante(), true, false,true);
					
					//Columna Apoderado en Excel
					crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtApoderado(), true, false,true);
					
					//Columna Tipo Solicitud en Excel
					crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsTipoSolicitud().getDesTipServicio()), true, false,true);
					
					//Columna Operaciones Bancarias en Excel
					crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtOpeBan(), true, false,true);
					
					//Columna Estudio en Excel
					if (tmp.getTiivsEstudio()!=null)
					{
						crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsEstudio().getDesEstudio()) , true, false,true);
					}
					else
					{
						crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(null),true,false,true);
					}
					
					//Columna Nivel en Excel
					crearCell(wb, row, 12, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtNivel(), true, false,true);
					
					//Columna Fecha Envio en Excel
					if (tmp.getFechaEnvio()!=null)
					{
						crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaEnvio()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					}
							
					//Columna Fecha Rpta en Excel
					if (tmp.getFechaRespuesta()!=null)
					{
						crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaRespuesta()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					}
					
					//Columna Fecha Estado en Excel
					if (tmp.getFechaEstado()!=null)
					{
						crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaEstado()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					}
					
					//Columna Comision en Excel
					if (tmp.getComision()!=null)
					{
						crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getComision()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					}
					
					//Columna Liberado
					if (tmp.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02))
					{
						crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
					}
					else
					{
						if (tmp.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02))
						{
							crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true);
						}
						else
						{
							crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
						}
					}
					
					//Columna Delegado
					if (validarSolicitudConDelegacion(tmp.getCodSoli()))
					{
						crearCell(wb, row, 18, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true);
					}
					else
					{
						crearCell(wb, row, 18, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
					}
					
					//Columna En Revision
					if (validarSolicitudEnRevision(tmp.getCodSoli()))
					{
						crearCell(wb, row, 19, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true);
					}
					else
					{
						crearCell(wb, row, 19, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
					}
					
					//Columna Revocatoria
					if (validarSolicitudRevocada(tmp.getCodSoli()))
					{
						crearCell(wb, row, 20, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true);
					}
					else
					{
						crearCell(wb, row, 20, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
					}
					
					numReg++;
				}
			}
			
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.setColumnWidth(3,256*20);
			sheet.setColumnWidth(4,256*20);
			sheet.setColumnWidth(6,256*15);
			sheet.setColumnWidth(7,256*20);
			sheet.setColumnWidth(8,256*20);
			sheet.autoSizeColumn(9);
			sheet.setColumnWidth(10,256*20);
			sheet.setColumnWidth(11,256*12);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			sheet.autoSizeColumn(17);
			sheet.autoSizeColumn(18);
			sheet.autoSizeColumn(19);
			sheet.autoSizeColumn(20);
						
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta="";
			if (obtenerRutaExcel().compareTo("")!=0)
			{
				
				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreArchivoExcel());
				
				strRuta = obtenerRutaExcel() + getNombreArchivoExcel() + ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				logger.debug("Ruta final donde encontrar el archivo excel: " + strRuta);
				
				setRutaArchivoExcel(strRuta);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
			//logger.info("Error al generar el archivo excel debido a: " + e.getStackTrace());
		}	
	}
	
	public void abrirExcel()
	{
		try {
			exportarExcelPOI();
			//Abrir archivo excel
				
			if (rutaArchivoExcel!=null && rutaArchivoExcel.length()>0)
			{
				Desktop.getDesktop().open(new File(rutaArchivoExcel));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("Error al abrir archivo excel debido a: " + e.getMessage());
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	public void descargarArchivo()
	{
		exportarExcelPOI();
		InputStream stream=null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: " + e.getMessage());
		}
		
		if (stream!=null)
		{
			file = new DefaultStreamedContent(stream, "application/excel", nombreArchivoExcel+ConstantesVisado.EXTENSION_XLS);
		}
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
	
	private String validarCampoNull(String campo)
	{
		String resultado="";
		if (campo==null)
		{
			resultado="";
		}
		else
		{
			resultado=campo;
		}
		return resultado;
	}
	
	private static void crearTituloCell(HSSFWorkbook wb, Row row, int column, short halign, short valign, String strContenido) 
	{
		CreationHelper ch = wb.getCreationHelper();
		Cell cell = row.createCell(column);
		cell.setCellValue(ch.createRichTextString(strContenido));

		HSSFFont cellFont = wb.createFont();
		cellFont.setFontHeightInPoints((short) 10);
		cellFont.setFontName(HSSFFont.FONT_ARIAL);
		cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFont.setUnderline((byte) 1);

		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		cellStyle.setFont(cellFont);
		cell.setCellStyle(cellStyle);
	}

	private static void crearCell(Workbook wb, Row row, int column, short halign, short valign, String strContenido, boolean booBorde,
			boolean booCabecera, boolean booFiltrosBus) 
	{
		CreationHelper ch = wb.getCreationHelper();
		Cell cell = row.createCell(column);
		cell.setCellValue(ch.createRichTextString(strContenido));
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		
		if (booBorde) 
		{
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setBottomBorderColor((short) 8);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setLeftBorderColor((short) 8);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setRightBorderColor((short) 8);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setTopBorderColor((short) 8);
		}
		
		if (booCabecera) 
		{
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setBottomBorderColor((short) 8);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setLeftBorderColor((short) 8);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setRightBorderColor((short) 8);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setTopBorderColor((short) 8);

			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}
		
		if (booFiltrosBus) 
		{
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setBottomBorderColor((short) 8);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setLeftBorderColor((short) 8);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setRightBorderColor((short) 8);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setTopBorderColor((short) 8);
			//cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}
		
		cell.setCellStyle(cellStyle);
	}
	
	public Boolean validarSolicitudConDelegacion(String codSoli)
	{
		boolean bEncontrado=false;
		
		String codigoSolicVerA = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A);
		String codigoSolicVerB = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_B);

		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.or(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO, codigoSolicVerA),Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicVerB)));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}
		
		lstSolicitudesSelected.clear();
		if (lstHistorial.size() > 0) 
		{
			// Colocar aqui la logica para filtrar los niveles aprobados o rechazados
			GenericDao<TiivsSolicitudNivel, Object> busqSolNivDAO = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro2 = Busqueda.forClass(TiivsSolicitudNivel.class);
			List<TiivsSolicitudNivel> lstSolNivel = new ArrayList<TiivsSolicitudNivel>();
							
			try {
				lstSolNivel = busqSolNivDAO.buscarDinamico(filtro2);
			} catch (Exception e) {
				logger.debug("Error al buscar los estados de los niveles en las solicitudes");
			}
			
			for (TiivsSolicitudNivel tmp: lstSolNivel)
			{
				for (TiivsHistSolicitud hist: lstHistorial)
				{
					if (tmp.getTiivsSolicitud().getCodSoli().equals(hist.getId().getCodSoli()))
					{
						lstSolicitudesSelected.add(hist.getId().getCodSoli());
					}
				}
			}
		}
		
		int ind=0;
		
		for (;ind<=lstSolicitudesSelected.size()-1;ind++)
		{
			if (lstSolicitudesSelected.get(ind).equals(codSoli))
			{
				bEncontrado=true;
				break;
			}
		}
		return bEncontrado;
	}
	
	public Boolean validarSolicitudEnRevision(String codSoli)
	{
		boolean bEncontrado=false;
		String codigoSolicEnRevision = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_EN_REVISION);
		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicEnRevision));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}
		
		lstSolicitudesSelected.clear();
		for (TiivsHistSolicitud tmp: lstHistorial)
		{
			if (lstHistorial!=null && lstHistorial.size()>0)
			{
				lstSolicitudesSelected.add(tmp.getId().getCodSoli());
			}
		}
		
		int ind=0;
		
		for (;ind<=lstSolicitudesSelected.size()-1;ind++)
		{
			if (lstSolicitudesSelected.get(ind).equals(codSoli))
			{
				bEncontrado=true;
				break;
			}
		}
		return bEncontrado;
	}
	
	public Boolean validarSolicitudRevocada(String codSoli)
	{
		boolean bEncontrado=false;
		String codigoSolicRevocado = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_REVOCADO);
		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicRevocado));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}
		
		lstSolicitudesSelected.clear();
		for (TiivsHistSolicitud tmp: lstHistorial)
		{
			if (lstHistorial!=null && lstHistorial.size()>0)
			{
				lstSolicitudesSelected.add(tmp.getId().getCodSoli());
			}
		}
		
		int ind=0;
		
		for (;ind<=lstSolicitudesSelected.size()-1;ind++)
		{
			if (lstSolicitudesSelected.get(ind).equals(codSoli))
			{
				bEncontrado=true;
				break;
			}
		}
		return bEncontrado;
	}
	
	public String buscarCodigoEstado(String estado) 
	{
		int i = 0;
		String codigo = "";
		for (; i < combosMB.getLstEstado().size(); i++) {
			if (combosMB.getLstEstado().get(i).getDescripcion().equalsIgnoreCase(estado)) {
				codigo = combosMB.getLstEstado().get(i).getCodEstado();
				break;
			}
		}
		return codigo;
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
	
	public String buscarNivelxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstNivel().size(); i++) {
			if (combosMB.getLstNivel().get(i).getId().getCodNiv().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstNivel().get(i).getId().getDesNiv();
				break;
			}
		}
		return res;
	}
	
	public String buscarEstNivelxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstadoNivel().size(); i++) {
			if (combosMB.getLstEstadoNivel().get(i).getCodigoEstadoNivel().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstadoNivel().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}
	
	public String buscarEstudioxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstudio().size(); i++) {
			if (combosMB.getLstEstudio().get(i).getCodEstudio().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstudio().get(i).getDesEstudio();
				break;
			}
		}
		return res;
	}
	
	public String buscarOpeBanxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstOpeBancaria().size(); i++) {
			if (combosMB.getLstOpeBancaria().get(i).getCodOperBan().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstOpeBancaria().get(i).getDesOperBan();
				break;
			}
		}
		return res;
	}
	
	public String buscarTipoSolxCodigo(String codigo)
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstTipoSolicitud().size(); i++) {
			if (combosMB.getLstTipoSolicitud().get(i).getCodTipSolic().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstTipoSolicitud().get(i).getDesTipServicio();
				break;
			}
		}
		return res;
	}
	
	public String buscarTipoFechaxCodigo(String codigo)
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstTiposFecha().size(); i++) {
			if (combosMB.getLstTiposFecha().get(i).getCodigoTipoFecha().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstTiposFecha().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}
	
	public String buscarDesTerritorio(String codigoTerritorio) 
	{
		String resultado = "";
		logger.debug("Buscando Territorio por codigo: " + codigoTerritorio);
		// System.out.println("Buscando Territorio por codigo: " +
		// codigoTerritorio);

		GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);
		filtroTerr.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_TERRITORIO,
				codigoTerritorio));

		List<TiivsTerritorio> lstTmp = new ArrayList<TiivsTerritorio>();

		try {
			lstTmp = terrDAO.buscarDinamico(filtroTerr);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre del territorio");
		}

		if (lstTmp.size() == 1) {
			resultado = lstTmp.get(0).getDesTer();
		}
		return resultado;
	}
	
	public List<TiivsSolicitud> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(List<TiivsSolicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public List<TiivsAgrupacionPersona> getLstAgrupPer() {
		return lstAgrupPer;
	}

	public void setLstAgrupPer(List<TiivsAgrupacionPersona> lstAgrupPer) {
		this.lstAgrupPer = lstAgrupPer;
	}

	public List<String> getLstEstadoSelected() {
		return lstEstadoSelected;
	}

	public void setLstEstadoSelected(List<String> lstEstadoSelected) {
		this.lstEstadoSelected = lstEstadoSelected;
	}

	public List<String> getLstTipoSolicitudSelected() {
		return lstTipoSolicitudSelected;
	}

	public void setLstTipoSolicitudSelected(List<String> lstTipoSolicitudSelected) {
		this.lstTipoSolicitudSelected = lstTipoSolicitudSelected;
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

	public List<String> getLstNivelSelected() {
		return lstNivelSelected;
	}

	public void setLstNivelSelected(List<String> lstNivelSelected) {
		this.lstNivelSelected = lstNivelSelected;
	}

	public List<String> getLstEstadoNivelSelected() {
		return lstEstadoNivelSelected;
	}

	public void setLstEstadoNivelSelected(List<String> lstEstadoNivelSelected) {
		this.lstEstadoNivelSelected = lstEstadoNivelSelected;
	}

	public List<String> getLstEstudioSelected() {
		return lstEstudioSelected;
	}

	public void setLstEstudioSelected(List<String> lstEstudioSelected) {
		this.lstEstudioSelected = lstEstudioSelected;
	}

	public List<String> getLstSolicitudesSelected() {
		return lstSolicitudesSelected;
	}

	public void setLstSolicitudesSelected(List<String> lstSolicitudesSelected) {
		this.lstSolicitudesSelected = lstSolicitudesSelected;
	}

	public List<String> getLstSolicitudesxOpeBan() {
		return lstSolicitudesxOpeBan;
	}

	public void setLstSolicitudesxOpeBan(List<String> lstSolicitudesxOpeBan) {
		this.lstSolicitudesxOpeBan = lstSolicitudesxOpeBan;
	}

	public String getNombreArchivoExcel() {
		return nombreArchivoExcel;
	}

	public void setNombreArchivoExcel(String nombreArchivoExcel) {
		this.nombreArchivoExcel = nombreArchivoExcel;
	}

	public List<TiivsHistSolicitud> getLstHistorial() {
		return lstHistorial;
	}

	public void setLstHistorial(List<TiivsHistSolicitud> lstHistorial) {
		this.lstHistorial = lstHistorial;
	}

	public PDFViewerMB getPdfViewerMB() {
		return pdfViewerMB;
	}

	public void setPdfViewerMB(PDFViewerMB pdfViewerMB) {
		this.pdfViewerMB = pdfViewerMB;
	}

	public String rutaArchivoExcel() {
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

	public IILDPeUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(IILDPeUsuario usuario) {
		this.usuario = usuario;
	}

	public String getPERFIL_USUARIO() {
		return PERFIL_USUARIO;
	}

	public void setPERFIL_USUARIO(String pERFIL_USUARIO) {
		PERFIL_USUARIO = pERFIL_USUARIO;
	}
}

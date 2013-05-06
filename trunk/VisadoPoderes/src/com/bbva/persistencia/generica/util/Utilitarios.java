package com.bbva.persistencia.generica.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.bbva.common.util.ConstantesVisado;

public class Utilitarios {
	
     private static Logger log = Logger.getLogger(Utilitarios.class);
      private static String PAQUETEMODELO="com/bbva/persistencia/generica/modelo";
      private static String NOMBREPROYECTO="BBVACOMMON";
      
	public static String TRACE =  "TRACE";
	public static String DEBUG =  "DEBUG";
	public static String INFO =   "INFO";
	public static String WARN =   "WARN";
	public static String ERROR =  "ERROR";
	public static String FATAL =  "FATAL";
	
	public static String buscarMesxCodigo(int codigo)
	{
		String mes="";
		
		switch (codigo) 
		{
			case 1: 
				mes = "ENERO";
				break;
			case 2: 
				mes = "FEBRERO";
				break;
			case 3: 
				mes = "MARZO";
				break;	
			case 4: 
				mes = "ABRIL";
				break;
			case 5: 
				mes = "MAYO";
				break;
			case 6: 
				mes = "JUNIO";
				break;
			case 7: 
				mes = "JULIO";
				break;
			case 8: 
				mes = "AGOSTO";
				break;
			case 9: 
				mes = "SEPTIEMBRE";
				break;
			case 10: 
				mes = "OCTUBRE";
				break;
			case 11: 
				mes = "NOVIEMBRE";
				break;
			case 12: 
				mes = "DICIEMBRE";
				break;
			default:
				break;
		}
		
		return mes;
	}
	
	
	
	public static double redondear(double numero){
	   /* BigDecimal bd = new BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	    */
	    //public double Redondear(double numero)
	    //{
	           return Math.rint(numero*100)/100;
	    //}
	}
	
	public static int buscarAnioxCodigo(int codigo)
	{
		System.out.println("Buscando codigo: " + codigo);
		
		int pAnio=0;
		
		if (codigo!=0)
		{
			switch (codigo) {
			case 1:
				pAnio=2013;
				break;
			case 2:
				pAnio=2014;
				break;
			case 3:
				pAnio=2015;
				break;
			case 4:
				pAnio=2016;
				break;
			default:
				break;
			}
		}
		
		System.out.println("Resultado: " + pAnio);
		
		return pAnio;
	}
	
	public static String obternerDescripcionEstado(String estado){
		String descEstado="";
		if(estado!=null){
		if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_ACTIVO;
		}else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESACTIVO)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_DESACTIVO;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_REGISTRADO_T02;
		}else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_RESERVADO_T02;
		}else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_ENVIADOSSJJ_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_VENCIDO_T02;
		}
		
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_EN_VERIFICACION_A_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_EN_VERIFICACION_B_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_PRE_EJECUTADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_PRE_EJECUTADO_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EJECUTADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_EJECUTADO_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_REVOCADO_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_REVOCADO_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_EN_REVISION_T02;
		}
		else if(estado.trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_3)){
			return descEstado=ConstantesVisado.ESTADOS.ESTADO_REVOCADO_T02;
		}
		}
		return descEstado;
	}
	
	public static String obternerDescripcionMoneda(String moneda){
		String desMoneda="";
		 if(moneda.trim().equals(ConstantesVisado.MONEDAS.COD_SOLES)){
			return desMoneda=ConstantesVisado.MONEDAS.SOLES;
		}else if(moneda.trim().equals(ConstantesVisado.MONEDAS.COD_DOLAR)){
			return desMoneda=ConstantesVisado.MONEDAS.DOLARES;
		}else  if(moneda.trim().equals(ConstantesVisado.MONEDAS.COD_EUROS)){
			return desMoneda=ConstantesVisado.MONEDAS.EUROS;
		}
		 return desMoneda;
	}
	
	
	public static String capturarParametro(String codigo){
		  Map<String, String> paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		  String strCodigoParametro = paramMap.get(codigo);
	      return strCodigoParametro;
		}
	

	public static Timestamp getFechaActual() {

		Date fecha = new Date();
		Timestamp time = new Timestamp(fecha.getTime());
		return time;

	}
	public static List<String> listarCLasesSearchables(){
		String ruta = Utilitarios.class.getClassLoader().getResource(PAQUETEMODELO).getFile();
		ruta= ruta.replaceFirst("WEB-INF/classes", "src");
	    ruta =ruta.substring(0,ruta.lastIndexOf(".metadata")).concat(ruta.substring(ruta.lastIndexOf(NOMBREPROYECTO)));
		String rutajava = ruta.replaceFirst("build/classes", "src");
		 List<String> lstString = new ArrayList<String>();
		  File file = new File(rutajava);
		  if(file.exists()){
		   File[] daos = file.listFiles();
		   for (int i = 0; i < daos.length; i++) {
			if(verificar(daos[i])!=""){
			   lstString.add(verificar(daos[i]));
			}
	         	}
		  }
		  return  lstString;
	}
	
	public static String verificar(File file){
		BufferedReader bReader;
		String sretorno="";
		   try {
				bReader = new BufferedReader(new FileReader(file));
				String linea = bReader.readLine();
			    while (linea != null) {
			    	linea = bReader.readLine();
			    	if(linea!=null&&linea.startsWith("@Searchable")){
			    		 sretorno= file.getName().trim().substring(0,file.getName().trim().lastIndexOf('.'));
			    	    break;
			    	}
				    }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}   		  
		   return sretorno;
	}
	 public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
	        int size = selectOne ? entities.size() + 1 : entities.size();
	        SelectItem[] items = new SelectItem[size];
	        int i = 0;
	        if (selectOne) {
	            items[0] = new SelectItem("", "---");
	            i++;
	        }
	        for (Object x : entities) {
	            items[i++] = new SelectItem(x, x.toString());
	        }
	        return items;
	    }
	
	public static String formatoFecha(Date fecha) {

		String fechaActualizacion = "";
		String horaActualizacion = "";  

		Calendar calFechaAct = Calendar.getInstance();
		calFechaAct.setTimeInMillis(fecha.getTime());
		fechaActualizacion = calFechaAct.get(Calendar.YEAR)+ "-"+
		                     calFechaAct.get(Calendar.MONTH) + "-"+
		                     calFechaAct.get(Calendar.DATE) + "";
		horaActualizacion = calFechaAct.get(Calendar.HOUR) + ":"
				+ calFechaAct.get(Calendar.MINUTE) + ":"
				+ calFechaAct.get(Calendar.SECOND);

		return fechaActualizacion + " " + horaActualizacion;
	}
	public static String formatoFecha_2(Date fecha) {

		String fechaActualizacion = "";
		String horaActualizacion = "";

		Calendar calFechaAct = Calendar.getInstance();
		calFechaAct.setTimeInMillis(fecha.getTime());
		fechaActualizacion = calFechaAct.get(Calendar.DATE) + "/"
				+ calFechaAct.get(Calendar.MONTH) + "/"
				+ calFechaAct.get(Calendar.YEAR);
		horaActualizacion = calFechaAct.get(Calendar.HOUR) + ":"
				+ calFechaAct.get(Calendar.MINUTE) + ":"
				+ calFechaAct.get(Calendar.SECOND);

		return fechaActualizacion + " " + horaActualizacion;
	}
	public static void crearTituloCell(HSSFWorkbook wb, Row row, int column, short halign, short valign, String strContenido, int iTamanioTitulo) 
	{
		CreationHelper ch = wb.getCreationHelper();
		//Cell cell = row.createCell(column);
		Cell cell = row.createCell(column);
		cell.setCellValue(ch.createRichTextString(strContenido));
		

		HSSFFont cellFont = wb.createFont();
		cellFont.setFontHeightInPoints((short) iTamanioTitulo);
		cellFont.setFontName(HSSFFont.FONT_ARIAL);
		cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFont.setUnderline((byte) 1);
		
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		cellStyle.setFont(cellFont);
		cellStyle.setWrapText(true);
		cell.setCellStyle(cellStyle);
	}
	
	public static void crearCellRPT(Workbook wb, Row row, int column, short halign, short valign, String strContenido, boolean booBorde,
			boolean booCabecera, boolean booFiltrosBus, Short color) 
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

			cellStyle.setFillForegroundColor(color);
			
			Font cellFont = wb.createFont();
			cellFont.setColor((short) HSSFColor.WHITE.index);
			cellStyle.setFont(cellFont);
			
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
	
	public static void crearCell(Workbook wb, Row row, int column, short halign, short valign, String strContenido, boolean booBorde,
			boolean booCabecera, boolean booFiltrosBus, Short color) 
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

			cellStyle.setFillForegroundColor(color);
			
			Font cellFont = wb.createFont();
			cellFont.setColor((short) HSSFColor.BLACK.index);
			cellStyle.setFont(cellFont);
			
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
	
	
	public static void crearCeldaCombinada(Sheet sheet, int row1, int row2, int col1, int col2, short halign, short valign, String strContenido, boolean booBorde,
			boolean booCabecera, boolean booFiltrosBus, Short color) 
	{
		
		CreationHelper ch = sheet.getWorkbook().getCreationHelper();
		for(int i=row1;i<=row2;i++){
			for(int j=col1;j<=col2;j++){
				Cell cell = sheet.getRow(row1).createCell(j);
				CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
				cellStyle.setAlignment(halign);
				cellStyle.setVerticalAlignment(valign);
				cellStyle.setWrapText(true);
				
				if(i==row1 && j == col1){
					cell.setCellValue(ch.createRichTextString(strContenido));
				}
				
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

					cellStyle.setFillForegroundColor(color);
					
					Font cellFont = sheet.getWorkbook().createFont();
					cellFont.setColor((short) HSSFColor.BLACK.index);
					cellStyle.setFont(cellFont);
					
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
		}		
		sheet.addMergedRegion(new CellRangeAddress(row1, row2, col1, col2));
	}
	
	public static void crearCellRevocados(Workbook wb, Row row, int column, short halign, short valign, String strContenido, boolean booBorde,
			boolean booCabecera, boolean booFiltrosBus, Short color) 
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

			cellStyle.setFillForegroundColor(color);
			
			Font cellFont = wb.createFont();
			cellFont.setColor((short) HSSFColor.WHITE.index);
			cellStyle.setFont(cellFont);
			
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
	
	
	
	public static CellStyle definirSoloEstiloCelda(Workbook wb, short halign, short valign, boolean booBorde,
			boolean booCabecera, boolean booFiltrosBus, Short color) 
	{
		//CreationHelper ch = wb.getCreationHelper();
		//Cell cell = row.createCell(column);
		//cell.setCellValue(ch.createRichTextString(strContenido));
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

			cellStyle.setFillForegroundColor(color);
			
			Font cellFont = wb.createFont();
			cellFont.setColor((short) HSSFColor.WHITE.index);
			cellStyle.setFont(cellFont);
			
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
		return cellStyle;
		//cell.setCellStyle(cellStyle);
	}
	
	public static void SetearEstiloCelda(Workbook wb,Row row, int column,String strContenido,CellStyle cellStyle)
	{
		CreationHelper ch = wb.getCreationHelper();
		Cell cell = row.createCell(column);
		cell.setCellValue(ch.createRichTextString(strContenido));
		cell.setCellStyle(cellStyle);
	}
	
	public static String formatoFechaSinHora(Date fecha) {

		String fechaActualizacion = "";
		
		Calendar calFechaAct = Calendar.getInstance();
		calFechaAct.setTimeInMillis(fecha.getTime());
		
		fechaActualizacion = calFechaAct.get(Calendar.DATE) + "/"
				+ obtenerMesSistema(calFechaAct.get(Calendar.MONTH)) + "/"
				+ calFechaAct.get(Calendar.YEAR);

		return fechaActualizacion ;
	}
	
	public static String obtenerMesSistema(int iMes)
	{
		String mes="";
		switch (iMes) {
		case 0:
			mes = "01";
			break;
		case 1:
			mes = "02";
			break;
		case 2:
			mes = "03";
			break;
		case 3:
			mes = "04";
			break;
		case 4:
			mes = "05";
			break;
		case 5:
			mes = "06";
			break;
		case 6:
			mes = "07";
			break;
		case 7:
			mes = "08";
			break;
		case 8:
			mes = "09";
			break;
		case 9:
			mes = "10";
			break;
		case 10:
			mes = "11";
			break;	
		case 11:
			mes = "12";
			break;
		default:
			break;
		}
		return mes;
	}
	
	public static String obtenerParteFecha(String fecha, String tipo)
	{
		String tipoRes="";
		
		if (fecha.compareTo("")!=0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			Date nFecha=null;
			try {
				nFecha = sdf.parse(fecha);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if (nFecha!=null)
			{
				Calendar calFechaAct = Calendar.getInstance();
				calFechaAct.setTimeInMillis(nFecha.getTime());
				
				if (tipo.equals("d"))
				{
					tipoRes = String.valueOf(calFechaAct.get(Calendar.DAY_OF_MONTH));
				}
				if (tipo.equals("m"))
				{
					tipoRes = obtenerMesSistema(calFechaAct.get(Calendar.MONTH));
				}
				if (tipo.equals("y"))
				{
					tipoRes = String.valueOf(calFechaAct.get(Calendar.YEAR));
				}
			}
		}	
		
		return tipoRes;
	}
	
	public static boolean comparadorFechas(String fecha1, String fecha2)
	{
		boolean aTiempo=true;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Date nFecha=null;
		try {
			nFecha = sdf.parse(fecha1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Date nFecha1=null;
		try {
			nFecha1 = sdf.parse(fecha2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (nFecha.after(nFecha1))
		{
			aTiempo=false;
		}
		
		return aTiempo;
	}
	
	public static String formatoFechaHora(Date fecha) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy_hhmmss");
		String sFecha = sdf.format(fecha);		
		return sFecha;
	}

	public static void mensaje(String titulo, String mensaje) {
		FacesContext ct = FacesContext.getCurrentInstance();
		ct.addMessage(null, new FacesMessage(titulo, mensaje));
	}

	public static void mensajeInfo(String titulo, String mensaje) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, mensaje, mensaje));
	}

	public static void mensajeError(String titutlo, String mensaje) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_WARN,mensaje ,"Atención"));
	}

	public static void putObjectInSession(String value, Object var) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) context.getSession(true);
		session.setAttribute(value, var);
	}

	public static Object getObjectInSession(String value) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession sessionhttp = (HttpSession) context.getSession(true);
		return (Object) sessionhttp.getAttribute(value);
	}
	
	public static void removeObjectInSession(String value) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) context.getSession(false);
		session.removeAttribute(value);
		session.invalidate();
	}
	
	public static String getProjectPath(){
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		return request.getRealPath(File.separator);
	}
	public static boolean validaCaracteresEspeciales(String email) {
		 
        Pattern p = Pattern.compile("[^A-Za-z0-9\\.\\áéíóúÁÉÍÓÚ\\/\\@_\\-~#]+");
 
        boolean caracteresIlegales = false;
        
        if(email.contains(" ")){
        	
        	StringTokenizer st = new StringTokenizer(email, " ");
    		while(st.hasMoreElements()){
    			Matcher m = p.matcher(st.nextToken());
    	        StringBuffer sb = new StringBuffer();
    	        boolean resultado = m.find();
    	       // boolean caracteresIlegales = false;
    	        while(resultado) {
    	            caracteresIlegales = true;
    	            m.appendReplacement(sb, "");
    	            resultado = m.find();
    	        }
    	    
    		}
    	
        }
        else{
        	
        	Matcher m = p.matcher(email);
            StringBuffer sb = new StringBuffer();
            boolean resultado = m.find();
            
            while(resultado) {
                caracteresIlegales = true;
                m.appendReplacement(sb, "");
                resultado = m.find();
            }
        }
        
        
        if(caracteresIlegales)
        	return true;
        else
        	return false;
	}
	
	  public static boolean validateEmail(String email) {
			 
	        Pattern p = Pattern.compile("[a-zA-Z0-9]+[.[a-zA-Z0-9_-]+]*@[a-z0-9][\\w\\.-]*[a-z0-9]\\.[a-z][a-z\\.]*[a-z]$",Pattern.CASE_INSENSITIVE);//me gusta esta
	        
	        Matcher m = p.matcher(email);
	        return m.matches();
	  }
	  
	  public static String obtenerFechaArchivoExcel() 
	  {
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		String nuevaFecha = fecha.substring(0, 2) + "" + fecha.substring(3, 5) + "" + fecha.substring(6, fecha.length());
	
		return nuevaFecha;
	  }
		
	  public static String obtenerHoraArchivoExcel() 
	  {
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
		String fecha = sdf.format(date);
		String nuevaFecha = fecha.substring(0, 2) + "" + fecha.substring(3, 5) + "" + fecha.substring(6, fecha.length());
	
		return nuevaFecha;
	  }
	  
	  public static String obtenerFechaHoraActual()
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
	  
	  public static Date getDateWithoutTime(Date date) {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
		    return cal.getTime();
		}
	  
	  public static String validarCampoNull(String campo)
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

	public static String modificarExtension(String nombre) {		
		if(nombre!=null && nombre.length()>2){ //nombre.PDF
			String ext = nombre.substring(nombre.lastIndexOf("."), nombre.length()).toLowerCase();//.PDF
			nombre = nombre.substring(0, nombre.lastIndexOf(".")) + ext;//nombre.pdf
		}
		return nombre;
	}

	public static<T extends Comparable<? super T>> List<T> ordenarLista(Collection<T> c) {
		  List<T> list = new ArrayList<T>(c);
		  java.util.Collections.sort(list);
		  return list;
		}
}

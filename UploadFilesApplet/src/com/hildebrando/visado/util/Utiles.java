package com.hildebrando.visado.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utiles {

	public static String armaTramaLista(List<String> lista) {		
		String resultado="";
		for(String item : lista){
			resultado = resultado + item + Constantes.SEPARADOR;              
        }
		return resultado;
	}
	
	public static String formatoFechaHora(Date fecha) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		String sFecha = sdf.format(fecha);		
		return sFecha; 
	}
	
	/**
	 * Metodo encargado de escribir eventos en un archivo log
	 * @param idSsistema Código de aplicacion
	 * @param mensaje Tipo de Mensaje. Ejm: ERROR / INFO
	 * @param deMensaje Es el mensaje a escribir en el archivo
	 * @param opc Opcion adicional
	 * **/
	public static void escribirEnLog(String tipoMsj, String mensaje, String opc) {
		FileWriter objFileWriter = null;
		PrintWriter objPrintWriter = null;
		try {
			String deRutaLog = Constantes.FORMATO_RUTA_SISTEMA + Constantes.FORMATO_RUTA_LOG;
			Calendar objCalendar = Calendar.getInstance();
			SimpleDateFormat sdfFecha = new SimpleDateFormat(Constantes.FORMATO_FECHA);
			SimpleDateFormat sdfHora = new SimpleDateFormat(Constantes.FORMATO_HORA);

			String noFicheroLog = Constantes.FORMATO_NOMBRE_ARCHIVO_LOG;
			noFicheroLog = noFicheroLog+ "_"+ sdfFecha.format(objCalendar.getTime())+ 
					"."+Constantes.FORMATO_ARCHIVO_GENERAR_LOG;

			File objCarpeta = new File(deRutaLog);
			if (!objCarpeta.exists())
				objCarpeta.mkdirs();
			
			File objFile = new File(deRutaLog, noFicheroLog);

			if (mensaje == null)
				mensaje = "";

			if (objFile.exists()) {
				System.out.println(Constantes.MSJ_ARCHIVO_EXISTS);
				objFileWriter = new FileWriter(objFile.getPath(), true);
				objPrintWriter = new PrintWriter(objFileWriter);
				String descMensaje = sdfHora.format(objCalendar.getTime())
						+ " - " + tipoMsj.toUpperCase() + " : "
						+ mensaje.toUpperCase();
				System.out.println("descMensaje: "+descMensaje);
				objPrintWriter.println(descMensaje);

			} else {
				System.out.println(Constantes.MSJ_ARCHIVO_NO_EXISTS);
				if (objFile.createNewFile()) {
					objFileWriter = new FileWriter(objFile.getPath());
					objPrintWriter = new PrintWriter(objFileWriter);
					objPrintWriter.println("/=================================================================/");
					objPrintWriter.println("\t\t\t" +Constantes.MSJ_INICIAL_APPLET+" ");
					objPrintWriter.println("/=================================================================/");
					objPrintWriter.println("");
					String descMensaje = sdfHora.format(objCalendar.getTime())
							+ " - " + tipoMsj.toUpperCase()+" : "
							+ mensaje.toUpperCase();
					objPrintWriter.println(descMensaje);
				}
			}
		} catch (IOException ioe) {
			System.out.println(Constantes.MSJ_OCURRE_EXCEPCION+" en escribirEnLog-io:"+ioe);
		} catch (Exception e) {
			System.out.println(Constantes.MSJ_OCURRE_EXCEPCION+" en escribirEnLog-e:"+e);
		} finally {
			try {
				if (null != objFileWriter) {
					objFileWriter.close();
				}
			} catch (Exception ex) {
				System.out.println(Constantes.MSJ_OCURRE_EXCEPCION+" en escribirEnLog-ex:"+ex);
			}
		}
	}
	
	public static void main(String []args){
		escribirEnLog("INFO", "Mensaje de prueba", "");
	}

}

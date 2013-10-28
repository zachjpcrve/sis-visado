package com.hildebrando.visado.quartz.jobs;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;

public class QuartzJob_Files implements Job  {
	
	public static Logger logger = Logger.getLogger(QuartzJob_Files.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		limpiarDirectorioFiles();	
	}	

	/**
	 * Realiza la limpieza de los archivos temporales que 
	 * se almacena en la carpeta "files" de la ruta del File Server
	 * */
	public void limpiarDirectorioFiles(){
		logger.info("==== limpiarDirectorioFiles() ==== ");
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		logger.info("[LimpiarFiles]-Path:" + path);
		
		path = path.substring(0,path.lastIndexOf("WEB-INF")) + ConstantesVisado.FILES + File.separator;
		path = path.replace("/", File.separator);
		logger.info("[LimpiarFiles]-Path con FileSystem: " + path);
		
		File directory = null;
		if(new File(path).exists()==false){
			path = path.substring(1,path.length());	//Path Windows
			logger.info("[LimpiarFiles]-Path modificado: " + path);
		}
		//Limpiar '/files/' - temp
		directory = new File(path);
		logger.info("[LimpiarFiles]-Existe directorio:" + directory.exists());
		try {
			logger.info("[LimpiarFiles]-Proc[1]:Se procedera con la limpieza de archivos de la carpeta:" + directory.getAbsolutePath());
			limpiarDirectorio(directory);
		} catch (Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+":",e);
		}
		//Limpiar '/files/'
		directory = new File(Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
			+ File.separator + ConstantesVisado.FILES);
		try {
			logger.info("[LimpiarFiles]-Proc[2]:Se procedera con la limpieza de archivos de la carpeta:" + directory.getAbsolutePath());
			limpiarDirectorio(directory);
		} catch (Exception e){
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR + ":",e);
		}
	}

	@SuppressWarnings("unchecked")
	private void limpiarDirectorio(File directory) {	
		logger.debug("== limpiarDirectorio():INICIO ==");
		int diferenciaEnDias = Integer.parseInt(Utilitarios.getPropiedad("nroDiasLimpFiles")); //1;
		Date fechaActual = Calendar.getInstance().getTime();
		long tiempoActual = fechaActual.getTime();
		long unDia = diferenciaEnDias * 24 * 60 * 60 * 1000;
		Date fechaAyer = new Date(tiempoActual - unDia);
		logger.debug("Iniciando tarea de limpieza de archivos:");
		logger.debug("[limpiarDirectorio]-Fecha corte :" + fechaAyer);
		//Si el directorio existe
		if (directory.exists()) {
			List<File> files = null;
			String ext[] = { "pdf", "PDF" };
			files = (List<File>) FileUtils.listFiles(directory, ext, true);
			logger.debug("[limpiarDirectorio]-Nro archivos(PDF) a depurar:"
					+ FileUtils.listFiles(directory, ext, false).size());
			int num1 = 0, num2 = 0;
			for (File f : files) {
				if (FileUtils.isFileOlder(f, fechaAyer)) {
					logger.info("[limpiarDirectorio]-Archivo-Depurar: " + f.getName());
					f.delete();
					num1++;
				} else {
					logger.info("[limpiarDirectorio]-Archivo-Mantener: " + f.getName());
					num2++;
				}
			}
			logger.debug("[limpiarDirectorio]-Nro archivos (depurados): " + num1);
			logger.debug("[limpiarDirectorio]-Nro archivos (mantenidos): " + num2);
		} else {
			logger.info("El directorio de depuracion no existe.");
		}
		logger.debug("== limpiarDirectorio():FIN ==");
	}
	
}
 
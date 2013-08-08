package com.hildebrando.visado.quartz.jobs;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.mb.JobsMB;

public class QuartzJob_Files implements Job  {
	
	public static Logger logger = Logger.getLogger(QuartzJob_Files.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		limpiarDirectorioFiles();	
	}	

	/*
	 * Realiza la limpieza de los archivos temporales que 
	 * se almacena en la carpeta "files" de la ruta del File Server
	 * 
	 * */
	public void limpiarDirectorioFiles(){
		
				
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		
		logger.info("path:" + path);
		
		path = path.substring(0,path.lastIndexOf("WEB-INF")) + ConstantesVisado.FILES + File.separator;
		path = path.replace("/", File.separator);
		
		logger.info("path with FS:" + path);
				
		File directory = null;
		
		if(new File(path).exists()==false){
			path = path.substring(1,path.length());	//Windows path
			logger.info("modify path:" + path);
		} 
		
		directory = new File(path);
				
		logger.info("directory exists?:" + directory.exists());
		
		try {
			logger.info("Se procederá a la limpieza de archivos de la carpeta:" + directory.getAbsolutePath());
			limpiarDirectorio(directory);
		} catch (Exception e){
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR + ":" + e.getMessage());
		}
		
		
		directory = new File(
				Utilitarios.getPropiedad(ConstantesVisado.KEY_PATH_FILE_SERVER)
						+ File.separator + ConstantesVisado.FILES);
		
		try {
			logger.info("Se procederá a la limpieza de archivos de la carpeta:" + directory.getAbsolutePath());
			limpiarDirectorio(directory);
		} catch (Exception e){
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR + ":" + e.getMessage());
		}		

	}

	@SuppressWarnings("unchecked")
	private void limpiarDirectorio(File directory) {
		
		int diferenciaEnDias = 1;
		Date fechaActual = Calendar.getInstance().getTime();
		long tiempoActual = fechaActual.getTime();
		long unDia = diferenciaEnDias * 24 * 60 * 60 * 1000;
		Date fechaAyer = new Date(tiempoActual - unDia);
		logger.debug("Inciando tarea de limpieza de archivos:");
		logger.debug("Fecha corte :" + fechaAyer);

		if (directory.exists()) {

			List<File> files = null;
			String ext[] = { "pdf", "PDF" };
			files = (List<File>) FileUtils.listFiles(directory, ext, true);

			logger.debug("Numero de archivos PDF en el directorio:"
					+ FileUtils.listFiles(directory, ext, false).size());

			int num1 = 0, num2 = 0;
			for (File f : files) {

				if (FileUtils.isFileOlder(f, fechaAyer)) {
					logger.info("Eliminar: " + f.getName());
					f.delete();
					num1++;
				} else {
					logger.info("Mantener: " + f.getName());
					num2++;
				}
			}

			logger.debug("Número de archivos eliminados: " + num1);
			logger.debug("Número de archivos mantenidos: " + num2);

		} else {
			logger.info("Ubicación de carpeta no existe:");
		}

	}
	
	
}
 
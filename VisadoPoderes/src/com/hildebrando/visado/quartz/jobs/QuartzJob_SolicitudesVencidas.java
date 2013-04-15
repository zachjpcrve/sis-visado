package com.hildebrando.visado.quartz.jobs;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.hildebrando.visado.mb.JobsMB;

public class QuartzJob_SolicitudesVencidas  implements Job{
	public static Logger logger = Logger.getLogger(QuartzJob_SolicitudesVencidas.class);
	
	public  void execute(JobExecutionContext arg0) throws JobExecutionException {
		this.ejecutarSolicitudesVencidas();
	}
	public void ejecutarSolicitudesVencidas(){
		logger.info("Iniciando Metodo ejecutar del  QuartzJob_SolicitudesVencidas");
		JobsMB job = new JobsMB();
		job.validarSolicitudesVencidas();
	}
/*	public void ejecutar(){
		logger.info("Iniciando Metodo ejecutar del  QuartzJob_SolicitudesVencidas");
		if(SpringInit.getApplicationContext()!=null){
			logger.info("Spring Init diferente de nulll");
		Runnable run = new Runnable(){

			@Override
			public void run() {
				
				Timer time = new Timer();
				TimerTask timerTask = new TimerTask() {			
					@Override
					public void run() {
						JobsMB job = new JobsMB();
						job.validarSolicitudesVencidas();	
						
					}
				};
				
				Date date = new Date();
				date.setHours(0);
				date.setMinutes(0);
				logger.info("Hora en la que se ejecuta del job :: "+date);
				//time.schedule(timerTask,date , 1000*60*1440);
				time.schedule(timerTask, 1000,5*1000*60);
				
			}
			
		};
		
		Thread th = new Thread(run);
		
		th.start();
		}
		
	}*/
}
 
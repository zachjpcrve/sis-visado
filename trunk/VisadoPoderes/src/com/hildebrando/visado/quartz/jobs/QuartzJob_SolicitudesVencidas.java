package com.hildebrando.visado.quartz.jobs;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.hildebrando.visado.mb.JobsMB;

public class QuartzJob_SolicitudesVencidas  {
	public static Logger logger = Logger.getLogger(QuartzJob_SolicitudesVencidas.class);
	
	public static void main(String []args){
		QuartzJob_SolicitudesVencidas job = new QuartzJob_SolicitudesVencidas(); 
		System.out.println("main");
		job.ejecutar();
	}
	public void ejecutar(){
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
		
	}
}
 
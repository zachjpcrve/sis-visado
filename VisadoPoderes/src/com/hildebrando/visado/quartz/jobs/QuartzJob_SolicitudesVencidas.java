package com.hildebrando.visado.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hildebrando.visado.mb.JobsMB;

public class QuartzJob_SolicitudesVencidas implements Job 
{
	@SuppressWarnings("static-access")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobsMB job = new JobsMB();
		job.validarSolicitudesVencidas();		
	}
}

package com.hildebrando.visado.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.general.service.ConfigBean;
import com.hildebrando.visado.mb.JobsMB;

public class QuartzJob_Feriados implements Job 
{
	@SuppressWarnings("static-access")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobsMB job = new JobsMB();
		job.cargarFeriados();
	}
}

package com.hildebrando.visado.mb;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.QrtzCronTriggers;
import com.hildebrando.visado.modelo.QrtzCronTriggersId;
import com.hildebrando.visado.modelo.QrtzJobDetails;
import com.hildebrando.visado.modelo.QrtzJobDetailsId;
import com.hildebrando.visado.modelo.QrtzSimpleTriggers;
import com.hildebrando.visado.modelo.QrtzSimpleTriggersId;
import com.hildebrando.visado.modelo.QrtzTriggers;
import com.hildebrando.visado.modelo.QrtzTriggersId;

@ManagedBean(name="quartzMB")
@SessionScoped
public class QuartzMB  implements Serializable
{
    private static Logger log = Logger.getLogger(QuartzMB.class);
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;
    private SelectItem[] lstTiposTriggers; 
    private boolean bsimple, bcrom, bqsimple, bqcron , bPararSchedul, bVisibleDialogShed, bVolatile, bStateful, bDurable;
    private String sMessajeSchedule, sLabelShedule;
    private List<QrtzJobDetails> lstQrtzlJobDetails;
   	private  QrtzTriggers objParamQrtz;
    private List<QrtzTriggers> lstQrtzTriggers;
    private QrtzJobDetailsId qrtzJobDetailsId, qrtzJobDetailsId2;
    private QrtzTriggers qrtzTriggers;
    private QrtzSimpleTriggers qrtzSimpleTriggers;
    private QrtzCronTriggers  qrtzCronTriggers;
    private QrtzTriggersId qrtzTriggersId;
       
    public QuartzMB() 
    {
       	objParamQrtz=new QrtzTriggers();
    	objParamQrtz.setId(new QrtzTriggersId());
    	
    	QrtzSimpleTriggers qrtzSimpleTriggers = new QrtzSimpleTriggers();
    	qrtzSimpleTriggers.setId(new QrtzSimpleTriggersId());
    	objParamQrtz.setQrtzSimpleTriggers(qrtzSimpleTriggers);
    	
    	QrtzCronTriggers qrtzCronTriggers = new QrtzCronTriggers();
    	qrtzCronTriggers.setId(new QrtzCronTriggersId());
    	objParamQrtz.setQrtzCronTriggers(qrtzCronTriggers);
    	
    	QrtzJobDetails jobDetail = new QrtzJobDetails();
    	QrtzJobDetailsId qrtzJobDetailsId= new QrtzJobDetailsId();
    	jobDetail.setId(qrtzJobDetailsId);
    	objParamQrtz.setQrtzJobDetails(jobDetail);
    	
    	lstTiposTriggers= listarTipoTrigger();
    	lstQrtzTriggers=new ArrayList<QrtzTriggers>();
    	lstQrtzlJobDetails=new ArrayList<QrtzJobDetails>();
    	    	
    	this.validarSchedule();
    	this.listarTriggers();
	}
    
    public SelectItem[] listarTipoTrigger(){
    	List<String> listaTTriggers=new ArrayList<String>();
    	listaTTriggers.add("SIMPLE");
    	listaTTriggers.add("CRON");
    	return lstTiposTriggers=Utilitarios.getSelectItems(listaTTriggers, false);
    }
    
    @SuppressWarnings("unchecked")
	public void listarTriggers(){
  
    	int i=0;
    	GenericDao<QrtzTriggers, QrtzTriggersId> entidadDAO = (GenericDao<QrtzTriggers, QrtzTriggersId>) SpringInit.getApplicationContext().getBean("genericoDao");
    	Busqueda filtro =Busqueda.forClass(QrtzTriggers.class);
    	try {
			lstQrtzTriggers =entidadDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	for (QrtzTriggers item : lstQrtzTriggers) {
			item.setItem(++i);
			item.setsNextFireTime(Utilitarios.formatoFecha(new Date(item.getNextFireTime())));
			item.setsPrevFireTime(Utilitarios.formatoFecha(new Date(item.getPrevFireTime())));
			item.setsStartTime(Utilitarios.formatoFecha(new Date(item.getStartTime())));
			item.setsEndTime(Utilitarios.formatoFecha(new Date(item.getEndTime())));
		}
      }
    public void onTabChange(TabChangeEvent  event) {  
        if(event.getTab().getId().equals("idTabConsultarJobDetail")){
        	listarJobDetail();
        }
    }  
    @SuppressWarnings("unchecked")
	public void listarJobDetail(){
    	int i=0;
    	GenericDao<QrtzJobDetails, QrtzJobDetailsId> entidadDAO = (GenericDao<QrtzJobDetails, QrtzJobDetailsId>) SpringInit.getApplicationContext().getBean("genericoDao");
        try {
			lstQrtzlJobDetails=entidadDAO.buscarDinamico(Busqueda.forClass(QrtzJobDetails.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
        for (QrtzJobDetails item : lstQrtzlJobDetails) {
			item.setItem(++i);
			item.setIsDurable(item.getIsDurable().equals("1") ? "Si" :"No");
			item.setIsVolatile(item.getIsVolatile().equals("1") ? "Si" :"No");
			item.setIsStateful(item.getIsStateful().equals("1") ? "Si" :"No");
		}
    }
    public void crearTrigger(){
    try {
    	if(objParamQrtz.getTriggerType().equals("SIMPLE")){
				crearSimpleTrigger();
    	}
    	if(objParamQrtz.getTriggerType().equals("CRON")){
				crearCronTrigger();
    	}
    	this.listarTriggers();
    	this.limpiar();
    	
    } catch (ClassNotFoundException e) {
			e.printStackTrace();
			Utilitarios.mensajeInfo("Mensajito", "Error : " + e.getMessage());
	} catch (SchedulerException e) {
			e.printStackTrace();
			Utilitarios.mensajeInfo("Mensajito", "Error : " + e.getMessage());
	}  catch (ParseException e) {
			e.printStackTrace();
			Utilitarios.mensajeInfo("Mensajito", "Error : " + e.getMessage());
	}
    
    }
    public void limpiar(){
    	bDurable=false;
    	bVolatile=false;
    	objParamQrtz=new QrtzTriggers();
    	objParamQrtz.setId(new QrtzTriggersId());
    	
    	qrtzCronTriggers = new QrtzCronTriggers();
    	qrtzCronTriggers.setId(new QrtzCronTriggersId());

    	objParamQrtz.setQrtzSimpleTriggers(new QrtzSimpleTriggers());	
    	objParamQrtz.setQrtzCronTriggers(new QrtzCronTriggers());
    	
    	
    	QrtzJobDetails jobDetail = new QrtzJobDetails();
    	QrtzJobDetailsId qrtzJobDetailsId= new QrtzJobDetailsId();
    	jobDetail.setId(qrtzJobDetailsId);
    	objParamQrtz.setQrtzJobDetails(jobDetail);
    	
    }

    
    @SuppressWarnings("rawtypes")
	public JobDetail crearJobDetail() throws SchedulerException, ClassNotFoundException{
    	
    	JobDetail jobDetail = null;
    	scheduler = SpringInit.getApplicationContext().getBean(Scheduler.class);
    	if(scheduler!=null){  
    		log.info("En el if schedule != null");
    		if(((Scheduler) scheduler).getJobDetail(objParamQrtz.getQrtzJobDetails().getId().getJobName(),
	                                                objParamQrtz.getQrtzJobDetails().getId().getJobGroup())!=null){
              jobDetail = ((Scheduler) scheduler).getJobDetail(objParamQrtz.getQrtzJobDetails().getId().getJobName(),
            		                             objParamQrtz.getQrtzJobDetails().getId().getJobGroup());
    		}else{
    		  jobDetail = new JobDetail();
    		  jobDetail.setName(objParamQrtz.getQrtzJobDetails().getId().getJobName());
    		  jobDetail.setGroup(objParamQrtz.getQrtzJobDetails().getId().getJobGroup());
    		 
    		} 
    		  Class cls = Class.forName(objParamQrtz.getQrtzJobDetails().getJobClassName());
   		 	  jobDetail.setJobClass(cls);
   		 	  jobDetail.setDescription(objParamQrtz.getQrtzJobDetails().getDescription());
   		 	  jobDetail.setDurability(bDurable);
   		      jobDetail.setVolatility(bVolatile);
   		      
   		     /* scheduler.addJobListener(new JobListenerClase());
   		      jobDetail.addJobListener("Caramba");
   		      Set<String> lst = scheduler.getJobListenerNames();
   		      
   		      for (String jobListener : lst) {
		      }*/
   		    
   		     
    	}
    	return jobDetail; 
    }
    
    public void crearSimpleTrigger() throws ClassNotFoundException, SchedulerException{
    	
    
    	
    		SimpleTrigger simpleTrigger = new SimpleTrigger();
			simpleTrigger.setName(objParamQrtz.getId().getTriggerName());
			simpleTrigger.setGroup(objParamQrtz.getId().getTriggerGroup());
			simpleTrigger.setRepeatInterval(objParamQrtz.getQrtzSimpleTriggers().getRepeatInterval());
	    	simpleTrigger.setRepeatCount(objParamQrtz.getQrtzSimpleTriggers().getRepeatCount());
	    	simpleTrigger.setStartTime(objParamQrtz.getdStartTime());
	    	simpleTrigger.setPriority(objParamQrtz.getPriority().intValue());
	    	
	    	JobDetail jobDetail =crearJobDetail();
	    	scheduler.scheduleJob(jobDetail, simpleTrigger);
	    	Utilitarios.mensajeInfo("Mensajito", "Se registró correctamente un : " + simpleTrigger.getFullJobName());
    }
    
    public void crearCronTrigger() throws ClassNotFoundException, SchedulerException, ParseException{
    	
    	    //log.info("En el crear cron ");
    	   
            CronTrigger cronTrigger = new CronTrigger();
        	
            cronTrigger.setName(objParamQrtz.getId().getTriggerName());
			cronTrigger.setGroup(objParamQrtz.getId().getTriggerGroup());
			cronTrigger.setCronExpression(objParamQrtz.getQrtzCronTriggers().getCronExpression());
			cronTrigger.setPriority(objParamQrtz.getPriority().intValue());
			
			/* 
			 
			scheduler.scheduleJob(jobDetail, cronTrigger);*/
			/* 
			Trigger tri = scheduler.getTrigger(objParamQrtz.getId().getTriggerName(), objParamQrtz.getId().getTriggerGroup());
			
		    	if(tri!=null){
		    		
		    		scheduler.rescheduleJob(tri.getName(), tri.getGroup(), cronTrigger);
		    	}
			*/
			
    	    boolean msValidacion =false;
    	    if(objParamQrtz.getQrtzJobDetails().getId().getJobName().equals("")||objParamQrtz.getQrtzJobDetails().getId().getJobName()==null){
    	    	Utilitarios.mensajeInfo(""," *Ingrese un Nombre para el JobDetail");
    	    	msValidacion=true;
    	    }
    	    if(objParamQrtz.getQrtzJobDetails().getId().getJobGroup().equals("")||objParamQrtz.getQrtzJobDetails().getId().getJobGroup()==null){
    	    	Utilitarios.mensajeInfo(""," *Ingrese un GroupName para el JobDetail");
    	    	msValidacion=true;
    	    }
    	    if(objParamQrtz.getId().getTriggerName().equals("")||objParamQrtz.getId().getTriggerName()==null){
    	    	Utilitarios.mensajeInfo(""," *Ingrese un Name para el Trigger");
    	    	msValidacion=true;
    	    }
    	    if(objParamQrtz.getId().getTriggerGroup().equals("")||objParamQrtz.getId().getTriggerGroup()==null){
    	    	Utilitarios.mensajeInfo(""," *Ingrese un GroupName para el Trigger");
    	    	msValidacion=true;
    	    }
    	    if(cronTrigger.getCronExpression().equals("")||cronTrigger.getCronExpression()==null){
    	    	Utilitarios.mensajeInfo(""," *Ingrese un CronExpression.");
    	    	msValidacion=true;
    	    }
    	    try {
    	    	 CronTrigger cronTriggers = new CronTrigger();
    	    	 cronTriggers.setCronExpression(cronTrigger.getCronExpression());
			} catch (Exception e) {
				msValidacion=true;
				Utilitarios.mensajeInfo(""," *Ingrese un Cron Expresión Correcto");
			}
			if(!msValidacion){
				
				
				JobDetail jobDetail =crearJobDetail();
				 
				scheduler.scheduleJob(jobDetail, cronTrigger);
				Utilitarios.mensajeInfo("Mensajito", "Se registró correctamente un : " + cronTrigger.getFullJobName());
				try {
					this.listarTriggers();
				} catch (Exception e) {
					Utilitarios.mensajeInfo("ERROR : ",e.getMessage());
				}
		    	this.limpiar(); 
			}
		
    }
    
    @SuppressWarnings("unchecked")
	public void actualizarTrigger()
    {
    	
    	QrtzCronTriggers  qrtzCronTriggerTMP = new QrtzCronTriggers();
    	GenericDao<QrtzCronTriggers, QrtzCronTriggersId> entidadDAO = (GenericDao<QrtzCronTriggers, QrtzCronTriggersId>) SpringInit.getApplicationContext().getBean("genericoDao");
	    QrtzCronTriggersId id=new QrtzCronTriggersId();
    	id.setTriggerName(qrtzTriggers.getId().getTriggerName());
    	id.setTriggerGroup(qrtzTriggers.getId().getTriggerGroup());
		try {
			qrtzCronTriggerTMP=	entidadDAO.buscarById(QrtzCronTriggers.class,id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String cronExpression="";
		
		cronExpression=qrtzCronTriggers.getCronExpression();
		
		if (cronExpression!=null)
		{
			qrtzCronTriggerTMP.setCronExpression(cronExpression);
			try {
				entidadDAO.modificar(qrtzCronTriggerTMP);
				Utilitarios.mensajeInfo("Info", "Se actualizó correctamente el CronTriggers");
				bqsimple=false;
	        	bqcron=false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			log.info("Error!!, no se encontro expression para actualizar");
		}
    }
	
    public void eliminarTrigger(){
    	try { 
    	log.info("En el eliminarTrigger " +qrtzJobDetailsId.getJobName() + " xx " +qrtzJobDetailsId.getJobGroup()); 
    	scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
    	boolean valor =false;
    	//JobKey jobKey = new JobKey(name, group)
        if(scheduler!=null){
        	//valor =scheduler.unscheduleJob(qrtzJobDetailsId.getJobName(), qrtzJobDetailsId.getJobGroup());
        	valor =scheduler.deleteJob(qrtzJobDetailsId.getJobName(), qrtzJobDetailsId.getJobGroup());
        	
        }
        if(valor){
         Utilitarios.mensajeInfo("Info : ", "El trigger fue Eliminado Correctamente");
        }else{
         Utilitarios.mensajeInfo("Info : ", "No se pudo eliminar el Trigger.");	
        }
        
         this.listarTriggers();
    	
		} catch (SchedulerException e) {
			Utilitarios.mensajeInfo("Info : ", "No se pudo eliminar el Trigger: "+e.getMessage());	
		}catch (Exception e) {
			Utilitarios.mensajeInfo("Info : ", "No se pudo eliminar el Trigger: "+e.getMessage());
			//e.printStackTrace();
		}
    }
    public void pararSchedule(){
    	scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
    	log.info("bPararSchedulxxxxx " +bPararSchedul);
    	try {
			if(bPararSchedul==true){
				scheduler.shutdown();
				//VERIFICAR ESTO
				if(scheduler.isShutdown()){
					   bPararSchedul=false;
					   sLabelShedule="Iniciar Schedule ? ";
				   }
				if(scheduler.isStarted()){
					   bPararSchedul=true;
					   sLabelShedule="Parar Schedule ? ";
				   }
				log.info("Se paro el schedule");
				Utilitarios.mensajeInfo("Info : ", "Se paro el Schedule " );
			}
			if(bPararSchedul==false){
				scheduler.start();
				if(scheduler.isShutdown()){
					   bPararSchedul=false;
					   sLabelShedule="Iniciar Schedule ? ";
				   }
				if(scheduler.isStarted()){
					   bPararSchedul=true;
					   sLabelShedule="Parar Schedule ? ";
				   }
				log.info("Se inicio el Schedule");
				Utilitarios.mensajeInfo("Info : ", "Se inicio el Schedule " );
			}

			this.listarTriggers();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    }
    public void obtenerBooleanoShedule(){
    	log.info("bPararSchedul " +bPararSchedul);
    	if(bPararSchedul==true){
    	sMessajeSchedule="¿Está usted seguro que desea PARAR el Shedule?";
    	}if(bPararSchedul==false){
    		sMessajeSchedule="¿Está usted seguro que desea INICIAR el Shedule?";
    	}
    	
    	bVisibleDialogShed=true;
    }
   public void validarSchedule(){
	   scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
	
	   try {
		   log.info("EL SCHEDULE ESTA : " +scheduler.isStarted());
		if(scheduler.isShutdown()){
			   bPararSchedul=true;
			   sLabelShedule="Iniciar Schedule ? ";
		   }
		if(scheduler.isStarted()){
			   bPararSchedul=false;
			   sLabelShedule="Parar Schedule ? ";
		   }
	} catch (SchedulerException e) {
		e.printStackTrace();
	}
   }

	public void executarTrigger(){
    	scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
    	/*try {
			scheduler.triggerJob(qrtzJobDetailsId.getJobName(), qrtzJobDetailsId.getJobGroup());
			Utilitarios.mensajeInfo("Info : ", "El trigger fue Executa ?" );
			this.listarTriggers();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}*/
    	
    	try {
    		Trigger tri = scheduler.getTrigger(qrtzTriggersId.getTriggerName(),qrtzTriggersId.getTriggerGroup());
    		if(tri!=null){
    		    scheduler.triggerJob(tri.getJobName(),tri.getJobGroup());
    			Utilitarios.mensajeInfo(""," *Info : El trigger fue Executado");
    			//MANUAL_TRIGGER Triger Test
    			this.listarTriggers();
						for (QrtzTriggers x : lstQrtzTriggers) {
							if(x.getId().getTriggerGroup().equals("MANUAL_TRIGGER")&&x.getQrtzJobDetails().getId().getJobName().equals(tri.getJobName())){
								
							}
							// scheduler.resumeTrigger(x.getId().getTriggerName(),x.getId().getTriggerGroup());
				         }
			   this.listarTriggers();
    		}else{
    			Utilitarios.mensajeInfo(""," *Info : No Trigger no pudo ser ejecutado");	
    		}
			
		} catch (SchedulerException e) {
			Utilitarios.mensajeInfo("ERROR"," *Info : "+ e.getMessage());
			//e.printStackTrace();
		}catch (Exception e) {
			Utilitarios.mensajeInfo("ERROR"," *Info : "+ e.getMessage());
			//e.printStackTrace();
		}
    }
    
    public void resumirTrigger(){
    	try {
    		scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
            scheduler.resumeTrigger(qrtzTriggersId.getTriggerName(),qrtzTriggersId.getTriggerGroup());
        	Utilitarios.mensajeInfo("Info : ", "Se restauro el Trigger con Key : "+qrtzTriggersId.getTriggerName()+ " : " +qrtzTriggersId.getTriggerGroup() );
			
        	this.listarTriggers();
    		} catch (SchedulerException e) {
    			e.printStackTrace();
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
    	
    }
    public void resumirJob(){
    	try {
    		scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
            scheduler.resumeJob(qrtzJobDetailsId2.getJobName(),
			                        qrtzJobDetailsId2.getJobGroup());
        	Utilitarios.mensajeInfo("Info : ", "Se restauro el Job con Key : "+qrtzJobDetailsId2.getJobName()+ " " +qrtzJobDetailsId2.getJobGroup() );
			
        	this.listarJobDetail();
    		} catch (SchedulerException e) {
    			e.printStackTrace();
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
    	
    }
    public void pausarTrigger(){
		try {
			scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
			scheduler.pauseTrigger(qrtzTriggersId.getTriggerName(),
            		               qrtzTriggersId.getTriggerGroup());
		
			log.info("En e pausar bean " );
			Utilitarios.mensajeInfo("Info : ", "Se pauso el Trigger con Key : "+qrtzTriggersId.getTriggerName()+ " " +qrtzTriggersId.getTriggerGroup() );
			this.listarTriggers();
	
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
    }
    public void pausarJob(){
 		try {
 			scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
 			scheduler.pauseJob(qrtzJobDetailsId.getJobName(),
 					           qrtzJobDetailsId.getJobGroup());
 			log.info("En e pausar bean " );
 			Utilitarios.mensajeInfo("Info : ", "Se pauso el Job con Key : "+qrtzJobDetailsId.getJobName()+ " " +qrtzJobDetailsId.getJobGroup() );
 			this.listarJobDetail();
 	
 		} catch (SchedulerException e) {
 			e.printStackTrace();
 		}
 		
     }
    @SuppressWarnings("unchecked")
	public void mostrarDetalleTrigger(){
    	log.info("El Id del Trigger " + qrtzTriggers.getTriggerType());
    	scheduler = (Scheduler) SpringInit.getApplicationContext().getBean("quartzScheduler");
    	
    	 
    	if(qrtzTriggers.getTriggerType().equals("SIMPLE")){
    		GenericDao<QrtzSimpleTriggers, QrtzSimpleTriggersId> entidadDAO = (GenericDao<QrtzSimpleTriggers, QrtzSimpleTriggersId>) SpringInit.getApplicationContext().getBean("genericoDao");	
    		QrtzSimpleTriggersId id=new QrtzSimpleTriggersId();
        	id.setTriggerName(qrtzTriggers.getId().getTriggerName());
        	id.setTriggerGroup(qrtzTriggers.getId().getTriggerGroup());
        	try {
				qrtzSimpleTriggers =  entidadDAO.buscarById(QrtzSimpleTriggers.class, id);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	bqsimple=true;
        	bqcron=false;

    	}
    	if(qrtzTriggers.getTriggerType().equals("CRON")){
    		GenericDao<QrtzCronTriggers, QrtzCronTriggersId> entidadDAO = (GenericDao<QrtzCronTriggers, QrtzCronTriggersId>) SpringInit.getApplicationContext().getBean("genericoDao");
    	    QrtzCronTriggersId id=new QrtzCronTriggersId();
        	id.setTriggerName(qrtzTriggers.getId().getTriggerName());
        	id.setTriggerGroup(qrtzTriggers.getId().getTriggerGroup());
    		try {
				qrtzCronTriggers=	entidadDAO.buscarById(QrtzCronTriggers.class,id);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		bqsimple=false;
        	bqcron=true;
    	}
    	/*qrtzTriggers=new QrtzTriggers();
    	qrtzTriggers.setId(new QrtzTriggersId());*/
    }
    
	
	
	public void cambiarDisabled(ValueChangeEvent e){
      if(e.getNewValue()!=null){
       if(e.getNewValue().toString().equals("SIMPLE")){
  			bsimple=true;
  			bcrom=false;
  			}
  		if(e.getNewValue().toString().equals("CRON")){
  			bsimple=false;
  			bcrom=true;
  			} 
  		if(e.getNewValue().toString().equals("-1")){
  			bsimple=false;
  			bcrom=false;
  		}
      }
	}

	
    
	public QrtzTriggers getObjParamQrtz() {
		return objParamQrtz;
	}
	public void setObjParamQrtz(QrtzTriggers objParamQrtz) {
		this.objParamQrtz = objParamQrtz;
	}

	public SelectItem[] getLstTiposTriggers() {
		return lstTiposTriggers;
	}

	public void setLstTiposTriggers(SelectItem[] lstTiposTriggers) {
		this.lstTiposTriggers = lstTiposTriggers;
	}
	public boolean isBsimple() {
		return bsimple;
	}

	public void setBsimple(boolean bsimple) {
		this.bsimple = bsimple;
	}

	public boolean isBcrom() {
		return bcrom;
	}

	public void setBcrom(boolean bcrom) {
		this.bcrom = bcrom;
	}

	public List<QrtzTriggers> getLstQrtzTriggers() {
		return lstQrtzTriggers;
	}

	public void setLstQrtzTriggers(List<QrtzTriggers> lstQrtzTriggers) {
		this.lstQrtzTriggers = lstQrtzTriggers;
	}

	public QrtzJobDetailsId getQrtzJobDetailsId() {
		return qrtzJobDetailsId;
	}

	public void setQrtzJobDetailsId(QrtzJobDetailsId qrtzJobDetailsId) {
		this.qrtzJobDetailsId = qrtzJobDetailsId;
	}

	public QrtzTriggers getQrtzTriggers() {
		log.info("EN EL getQrtzTriggers");

		return qrtzTriggers;
	}

	public void setQrtzTriggers(QrtzTriggers qrtzTriggers) {
		log.info("EN EL setQrtzTriggers");
		this.qrtzTriggers = qrtzTriggers;
	}

	public QrtzSimpleTriggers getQrtzSimpleTriggers() {
		return qrtzSimpleTriggers;
	}

	public void setQrtzSimpleTriggers(QrtzSimpleTriggers qrtzSimpleTriggers) {
		this.qrtzSimpleTriggers = qrtzSimpleTriggers;
	}

	public QrtzCronTriggers getQrtzCronTriggers() {
		return qrtzCronTriggers;
	}

	public void setQrtzCronTriggers(QrtzCronTriggers qrtzCronTriggers) {
		this.qrtzCronTriggers = qrtzCronTriggers;
	}

	 public String getsMessajeSchedule() {
			return sMessajeSchedule;
		}

		public void setsMessajeSchedule(String sMessajeSchedule) {
			this.sMessajeSchedule = sMessajeSchedule;
		}

	
		 public String getsLabelShedule() {
				return sLabelShedule;
			}

			public void setsLabelShedule(String sLabelShedule) {
				this.sLabelShedule = sLabelShedule;
			}

			public boolean isbVisibleDialogShed() {
				return bVisibleDialogShed;
			}

			public void setbVisibleDialogShed(boolean bVisibleDialogShed) {
				this.bVisibleDialogShed = bVisibleDialogShed;
			}

			public boolean getbPararSchedul() {
				return bPararSchedul;
			}

			public void setbPararSchedul(boolean bPararSchedul) {
				this.bPararSchedul = bPararSchedul;
			}

			public boolean isBqsimple() {
				return bqsimple;
			}

			public void setBqsimple(boolean bqsimple) {
				this.bqsimple = bqsimple;
			}

			public boolean isBqcron() {
				return bqcron;
			}

			public void setBqcron(boolean bqcron) {
				this.bqcron = bqcron;
			}

			public List<QrtzJobDetails> getLstQrtzlJobDetails() {
				return lstQrtzlJobDetails;
			}

			public void setLstQrtzlJobDetails(List<QrtzJobDetails> lstQrtzlJobDetails) {
				this.lstQrtzlJobDetails = lstQrtzlJobDetails;
			}

			public QrtzJobDetailsId getQrtzJobDetailsId2() {
				return qrtzJobDetailsId2;
			}

			public void setQrtzJobDetailsId2(QrtzJobDetailsId qrtzJobDetailsId2) {
				this.qrtzJobDetailsId2 = qrtzJobDetailsId2;
			}

			public QrtzTriggersId getQrtzTriggersId() {
				return qrtzTriggersId;
			}

			public void setQrtzTriggersId(QrtzTriggersId qrtzTriggersId) {
				this.qrtzTriggersId = qrtzTriggersId;
			}

			public boolean getbVolatile() {
				return bVolatile;
			}

			public void setbVolatile(boolean bVolatile) {
				this.bVolatile = bVolatile;
			}

			public boolean getbStateful() {
				return bStateful;
			}

			public void setbStateful(boolean bStateful) {
				this.bStateful = bStateful;
			}

			public boolean getbDurable() {
				return bDurable;
			}

			public void setbDurable(boolean bDurable) {
				this.bDurable = bDurable;
			}

    
}

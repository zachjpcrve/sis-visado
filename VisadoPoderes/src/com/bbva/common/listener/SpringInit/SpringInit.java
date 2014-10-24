
package com.bbva.common.listener.SpringInit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hildebrando.visado.mb.InfoDeployMB;
import com.hildebrando.visado.quartz.jobs.QuartzJob_SolicitudesVencidas;

public class SpringInit implements ServletContextListener {

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SpringInit.class);
    private static WebApplicationContext springContext;
    
    public void contextInitialized(ServletContextEvent event) {
        springContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        /**
    	 * CAMBIO 24/07/2014 HVB
    	 * SE INICIALIZA EL BEAN INFODEPLOY PARA CARGAR LOS DATOS AL INICIO DE LA APLICACION
    	 */
        event.getServletContext().setAttribute("infoDeployMB",new InfoDeployMB());
        /*QuartzJob_SolicitudesVencidas job =new QuartzJob_SolicitudesVencidas();
        job.ejecutar();*/
    }
    
    public void contextDestroyed(ServletContextEvent event) {
        springContext = null;
    }
    
    public static ApplicationContext getApplicationContext() {
        return springContext;
    }
    
	public static void openSession() {
		SessionFactory sessionFactory = (SessionFactory) SpringInit.getApplicationContext().getBean("sessionFactory");
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));

	}

	public static void closeSession() {
		SessionFactory sessionFactory = (SessionFactory) SpringInit.getApplicationContext().getBean("sessionFactory");
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
		sessionHolder.getSession().flush();
		SessionFactoryUtils.closeSession(sessionHolder.getSession());

	}
	
	public static Session devolverSession()
	{
		SessionFactory sessionFactory = (SessionFactory) SpringInit.getApplicationContext().getBean("sessionFactory");
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		return session;
	}


}

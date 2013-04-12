package com.bbva.common.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class EstilosNavegador {
	public static Logger logger = Logger.getLogger(EstilosNavegador.class);
	
	public void estilosNavegador(){
		
	     String agente = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("User-Agent");
	     HttpServletRequest request;    
	     request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
         HttpSession sesion = request.getSession(true);
         String explorador="";
		if (agente.contains("MSIE 7.0")) {
             explorador = "IE7";
 	    }
         if (agente.contains("MSIE 9.0")) {
             explorador = "IE9";
 	    }
         if (agente.contains("MSIE 8.0")) {
             explorador = "IE8";
 	    }
 	    if (agente.contains("Opera")) {
 	        explorador = "Opera";
 	    }
 	    if (agente.contains("Firefox")) {
 	        explorador = "Firefox";
 	    }
 	    if (agente.contains("Safari")) {
 	        explorador = "Safari";
 	    }
 	    if (agente.contains("Chrome")) {
 	        explorador = "Chrome";
 	    }
 	    
 	    logger.info("Navegador detectado: " + explorador);
 	    
 	    if (explorador.equals("IE7"))
 	    {
 	    	sesion.setAttribute("ANCHO_FIELDSET", "350%");
 	    	sesion.setAttribute("ANCHO_FIELDSET_PODER", "95%");
 	    	sesion.setAttribute("ANCHO_POPUP_PODER", "850");
 	    	sesion.setAttribute("ANCHO_REVOC_PODER", "100%");
 	    	sesion.setAttribute("ANCHO_POPUP_REVOC_PODER", "850");
 	    	sesion.setAttribute("ALTO_POPUP_REVOC_PODER", "300");
 	    }
 	    if (explorador.equals("IE9"))
 	    {
 	    	sesion.setAttribute("ANCHO_FIELDSET", "105%");
 	    	sesion.setAttribute("ANCHO_FIELDSET_PODER", "105%");
 	    	sesion.setAttribute("ANCHO_POPUP_PODER", "850");
 	    	sesion.setAttribute("ANCHO_REVOC_PODER", "150%");
 	    	sesion.setAttribute("ANCHO_POPUP_REVOC_PODER", "800");
 	    	sesion.setAttribute("ALTO_POPUP_REVOC_PODER", "400");
 	    }
 	    
 	    if (explorador.equals("IE8"))
 	    {
 	    	sesion.setAttribute("ANCHO_FIELDSET", "105%");
 	    	sesion.setAttribute("ANCHO_FIELDSET_PODER", "105%");
 	    	sesion.setAttribute("ANCHO_POPUP_PODER", "850");
 	    	sesion.setAttribute("ANCHO_REVOC_PODER", "150%");
 	    	sesion.setAttribute("ANCHO_POPUP_REVOC_PODER", "800");
 	    	sesion.setAttribute("ALTO_POPUP_REVOC_PODER", "400");
 	    }
 	    if (explorador.equals("Firefox"))
 	    {
 	    	sesion.setAttribute("ANCHO_FIELDSET", "105%");
 	    	sesion.setAttribute("ANCHO_FIELDSET_PODER", "105%");
 	    	sesion.setAttribute("ANCHO_POPUP_PODER", "950");
 	    	sesion.setAttribute("ANCHO_REVOC_PODER", "150%");
 	    	sesion.setAttribute("ANCHO_POPUP_REVOC_PODER", "930");
 	    	sesion.setAttribute("ALTO_POPUP_REVOC_PODER", "400");
 	    }
 	    
 	    if (explorador.equals("Chrome"))
 	    {
 	    	sesion.setAttribute("ANCHO_FIELDSET", "105%");
 	    	sesion.setAttribute("ANCHO_FIELDSET_PODER", "105%");
 	    	sesion.setAttribute("ANCHO_POPUP_PODER", "950");
 	    	sesion.setAttribute("ANCHO_REVOC_PODER", "150%");
 	    	sesion.setAttribute("ANCHO_POPUP_REVOC_PODER", "930");
 	    	sesion.setAttribute("ALTO_POPUP_REVOC_PODER", "400");
 	    }
           
 		
	}
	
}

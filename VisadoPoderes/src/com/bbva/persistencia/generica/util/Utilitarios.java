package com.bbva.persistencia.generica.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class Utilitarios {
	
     private static Logger log = Logger.getLogger(Utilitarios.class);
      private static String PAQUETEMODELO="com/bbva/persistencia/generica/modelo";
      private static String NOMBREPROYECTO="BBVACOMMON";
      
	public static String TRACE =  "TRACE";
	public static String DEBUG =  "DEBUG";
	public static String INFO =   "INFO";
	public static String WARN =   "WARN";
	public static String ERROR =  "ERROR";
	public static String FATAL =  "FATAL";
	
	public static String capturarParametro(String codigo){
		  Map<String, String> paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		  String strCodigoParametro = paramMap.get(codigo);
	      return strCodigoParametro;
		}
	
	public static Timestamp getFechaActual() {

		Date fecha = new Date();
		Timestamp time = new Timestamp(fecha.getTime());
		return time;

	}
	public static List<String> listarCLasesSearchables(){
		String ruta = Utilitarios.class.getClassLoader().getResource(PAQUETEMODELO).getFile();
		ruta= ruta.replaceFirst("WEB-INF/classes", "src");
	    ruta =ruta.substring(0,ruta.lastIndexOf(".metadata")).concat(ruta.substring(ruta.lastIndexOf(NOMBREPROYECTO)));
		String rutajava = ruta.replaceFirst("build/classes", "src");
		 List<String> lstString = new ArrayList<String>();
		  File file = new File(rutajava);
		  if(file.exists()){
		   File[] daos = file.listFiles();
		   for (int i = 0; i < daos.length; i++) {
			if(verificar(daos[i])!=""){
			   lstString.add(verificar(daos[i]));
			}
	         	}
		  }
		  return  lstString;
	}
	
	public static String verificar(File file){
		BufferedReader bReader;
		String sretorno="";
		   try {
				bReader = new BufferedReader(new FileReader(file));
				String linea = bReader.readLine();
			    while (linea != null) {
			    	linea = bReader.readLine();
			    	if(linea!=null&&linea.startsWith("@Searchable")){
			    		 sretorno= file.getName().trim().substring(0,file.getName().trim().lastIndexOf('.'));
			    	    break;
			    	}
				    }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}   		  
		   return sretorno;
	}
	 public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
	        int size = selectOne ? entities.size() + 1 : entities.size();
	        SelectItem[] items = new SelectItem[size];
	        int i = 0;
	        if (selectOne) {
	            items[0] = new SelectItem("", "---");
	            i++;
	        }
	        for (Object x : entities) {
	            items[i++] = new SelectItem(x, x.toString());
	        }
	        return items;
	    }
	
	public static String formatoFecha(Date fecha) {

		String fechaActualizacion = "";
		String horaActualizacion = "";

		Calendar calFechaAct = Calendar.getInstance();
		calFechaAct.setTimeInMillis(fecha.getTime());
		fechaActualizacion = calFechaAct.get(Calendar.DATE) + "/"
				+ calFechaAct.get(Calendar.MONTH) + "/"
				+ calFechaAct.get(Calendar.YEAR);
		horaActualizacion = calFechaAct.get(Calendar.HOUR) + ":"
				+ calFechaAct.get(Calendar.MINUTE) + ":"
				+ calFechaAct.get(Calendar.SECOND);

		return fechaActualizacion + " " + horaActualizacion;
	}
	
	public static String formatoFechaSinHora(Date fecha) {

		String fechaActualizacion = "";

		Calendar calFechaAct = Calendar.getInstance();
		calFechaAct.setTimeInMillis(fecha.getTime());
		fechaActualizacion = calFechaAct.get(Calendar.DATE) + "/"
				+ calFechaAct.get(Calendar.MONTH) + "/"
				+ calFechaAct.get(Calendar.YEAR);

		return fechaActualizacion ;
	}


	public static void mensaje(String titulo, String mensaje) {
		FacesContext ct = FacesContext.getCurrentInstance();
		ct.addMessage(null, new FacesMessage(titulo, mensaje));
	}

	public static void mensajeInfo(String titutlo, String mensaje) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Información", mensaje));
	}

	public static void mensajeError(String titutlo, String mensaje) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_WARN, "Atención", mensaje));
	}

	public static void putObjectInSession(String value, Object var) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) context.getSession(true);
		session.setAttribute(value, var);
	}

	public static Object getObjectInSession(String value) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession sessionhttp = (HttpSession) context.getSession(true);
		return (Object) sessionhttp.getAttribute(value);
	}
	
	public static void removeObjectInSession(String value) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) context.getSession(false);
		session.removeAttribute(value);
		session.invalidate();
	}

}

package com.hildebrando.visado.mb;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


import org.apache.log4j.Logger;




@ManagedBean(name = "revocadosMB")
@SessionScoped
public class RevocadosMB 
{
	
	
	public static Logger logger = Logger.getLogger(RevocadosMB.class);
	
	public RevocadosMB() 
	{		
	}
	
	public void busquedaRevocados()
	{
		
	}
		
	public String reset(){
		return "/faces/paginas/registrarRevocado.xhtml";
	}
	
}

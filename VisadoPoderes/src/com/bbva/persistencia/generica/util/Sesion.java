package com.bbva.persistencia.generica.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sesion{
	
	private static List<Map<String,Object>> sesion=new ArrayList<Map<String,Object>>();
	
	private static int cantidad=0;
	
	public synchronized static int crearNuevaSesion(){
		int insertar=cantidad;
		for(int i=0;i<sesion.size();i++){
			Map<String,Object> data=sesion.get(i);
			if(data==null){
				insertar=i;
				i=sesion.size();
			}
		}
		if(insertar==cantidad){
			sesion.add(insertar,new HashMap<String,Object>());
			cantidad++;
		}
		else{
			sesion.set(insertar,new HashMap<String,Object>());
		}
		return insertar;
	}
	
	public synchronized static void addAttribute(int indice,String llave,Object valor){
		Map<String,Object> data=sesion.get(indice);
		if(data==null){
			data=new HashMap<String,Object>();
		}
		data.put(llave,valor);
		sesion.set(indice,data);
	}

	
	public synchronized static Object getAttribute(int indice,String llave){
		Map<String,Object> data=sesion.get(indice);
		if(data==null){
			return null;
		}
		return data.get(llave);
	}

	public synchronized static Set<String> getAttributeNames(int indice){
		Map<String,Object> data=null;
		if (indice < sesion.size()){
			data=sesion.get(indice);
		}
		if(data==null){
			return null;
		}
		return data.keySet();
	}

	
	public synchronized static void cerrarSesion(int idSesion){
		if(idSesion < sesion.size()){
			sesion.set(idSesion,null);
		}
	}

	public static void setAttribute(Integer indice,String llave,Object valor){
		Map<String,Object> data=sesion.get(indice);
		if(data==null){
			return;
		}
		data.put(llave,valor);
		sesion.set(indice,data);
	}

}


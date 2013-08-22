package com.hildebrando.visado.util;

import java.util.Date;
import java.util.List;

public class Utiles {

	public static String armaTramaLista(List<String> lista) {		
		String resultado="";
		for(String item : lista){
			resultado = resultado + item + Constantes.SEPARADOR;              
        }
		return resultado;
	}
	
	public static String formatoFechaHora(Date fecha) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		String sFecha = sdf.format(fecha);		
		return sFecha; 
	}


}

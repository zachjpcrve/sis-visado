package com.hildebrando.visado.util;

import java.io.File;
import java.util.List;

public class Utiles {

	public static String armaTramaLista(List<String> lista) {		
		String resultado="";
		for(String item : lista){
			resultado = resultado + item + Constantes.SEPARADOR;              
        }
		return resultado;
	}

}

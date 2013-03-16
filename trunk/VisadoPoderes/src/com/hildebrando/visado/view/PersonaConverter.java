package com.hildebrando.visado.view;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsPersona;

@FacesConverter(value="personaCvtr")
public class PersonaConverter implements Converter {
	
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		
		List<TiivsPersona> personas = new ArrayList<TiivsPersona>();
		GenericDao<TiivsPersona, Object> personaDAO = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);
		try {
			personas = personaDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		if (value.trim().equals("")) {  
            return null;  
        } else {  
            try {  
                int number = Integer.parseInt(value);  
  
                String nombre="";
                String apePat="";
                String apeMat="";
                
                for (TiivsPersona p : personas) {
                    if (p.getCodPer() == number) {  
                    	
                    	nombre = p.getNombre()!=null?p.getNombre().toUpperCase():"";
                    	apePat = p.getApePat()!=null?p.getApePat().toUpperCase():"";
                    	apeMat = p.getApeMat()!=null?p.getApeMat().toUpperCase():"";
                    	
                    	String nombreCompletoMayuscula = nombre + " " + apePat + " " + apeMat;
                    	
                    	p.setNombreCompletoMayuscula(nombreCompletoMayuscula);
                    	
                        return p;  
                    }  
                }  
  
            } catch(NumberFormatException exception) {  
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nombre de Persona invalida", "Nombre de Persona invalida"));  
            }  
        }  
  
        return null;  
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		 if (value == null || value.equals("")) {  
	            return "";  
	        } else {  
	            return String.valueOf(((TiivsPersona) value).getCodPer());  
	        }  
	}
	
	

}


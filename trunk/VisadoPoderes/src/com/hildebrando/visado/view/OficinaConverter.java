package com.hildebrando.visado.view;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsOficina1;

@FacesConverter(value="oficinaConverter")
public class OficinaConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		

		if (value.trim().equals("")) {  
            return null;  
        } else {  
            try {  
                String number = value;  
                
        		@SuppressWarnings("unchecked")
				GenericDao<TiivsOficina1, Object> oficinaDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
        				.getApplicationContext().getBean("genericoDao");
        		try {
        			TiivsOficina1 tiivsOficina1 = oficinaDAO.buscarById(TiivsOficina1.class, number);
        			
        			if (tiivsOficina1!=null)
        			{
	        			if(tiivsOficina1.getCodOfi()!= ""
	    						&&  tiivsOficina1.getDesOfi() != ""
	    						  && tiivsOficina1.getTiivsTerritorio().getCodTer() != ""
	    						    && tiivsOficina1.getTiivsTerritorio().getDesTer()  != ""){
	    					
	    					String texto = tiivsOficina1.getCodOfi() + "-"
	    							+ tiivsOficina1.getDesOfi().toUpperCase() + "("
	    							+ tiivsOficina1.getTiivsTerritorio().getCodTer()
	    							+ tiivsOficina1.getTiivsTerritorio().getDesTer() + ")";
	
	    						tiivsOficina1.setNombreDetallado(texto);
	    				}
        			}
        			return tiivsOficina1;
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
  
            } catch(NumberFormatException exception) {  
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "No es una oficina valida"));  
            }  
        }  
		return null;  

	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		 if (value == null || value.equals("")) {  
	            return "";  
	        } else {  
	            return String.valueOf(((TiivsOficina1) value).getCodOfi());  
	        }  
	}
}

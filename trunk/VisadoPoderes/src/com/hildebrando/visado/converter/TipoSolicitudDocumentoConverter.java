package com.hildebrando.visado.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;

@FacesConverter(value = "tipoSolicitudDocumentoConverter")
public class TipoSolicitudDocumentoConverter implements Converter {

	public static List<TiivsTipoSolicDocumento> documentosDB;
	
/*	static {				
		GenericDao<TiivsTipoSolicDocumento, Object> genTipoSolcDocumDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSolcDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);		
		filtroTipoSolcDoc.add(Restrictions.isNotNull("codDoc"));
		try {
			documentosDB = genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);			
		} catch (Exception ex) {			
			ex.printStackTrace();
		}		
	}*/
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String submittedValue)
			throws ConverterException {
//		System.out.println("getAsObject():INICIO");
//		System.out.println(submittedValue);
		if(submittedValue ==null || submittedValue.trim().equals("")){
			return null;
		} else if(submittedValue.equals(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)){
			TiivsTipoSolicDocumento docu = new TiivsTipoSolicDocumento ();
			docu.setCodDoc((String) submittedValue);
//			System.out.println("otros");
//			System.out.println("getAsObject():FIN");
			return docu;			
		} else {
			try {				
				for(TiivsTipoSolicDocumento doc : documentosDB){
					if(doc.getCodDoc().equals(submittedValue)){
//						System.out.println("from documentosDB");
//						System.out.println("getAsObject():FIN");
						return doc;
					}
				}				
			} catch (Exception e){
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error de conversión","No valido documento"));
			}
			
		}	
//		System.out.println("return null");
//		System.out.println("getAsObject():FIN");
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value)
			throws ConverterException {
//		System.out.println("getAsString():INICIO");
//		System.out.println(value);
		if(value == null || value.equals("")) {
//			System.out.println("getAsString():FIN");
			return "";
		} else if(value instanceof String && value.equals(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)){
//			System.out.println("return otros");
//			System.out.println("getAsString():FIN");
			return (String) value;
		}else {
		
			TiivsTipoSolicDocumento docu = (TiivsTipoSolicDocumento) value;
//			System.out.println("return tipo documento");
//			System.out.println("getAsString():FIN");
			return docu.getCodDoc();
		}		
	}

}

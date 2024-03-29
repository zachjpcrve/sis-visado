package com.hildebrando.visado.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.ListasIniciales;
import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumentoId;

@FacesConverter("tipoSolicitudDocumentoConverter")
public class TipoSolicitudDocumentoConverter
  implements Converter
{
  private static Logger logger = Logger.getLogger(TipoSolicitudDocumentoConverter.class);
  public static List<TiivsTipoSolicDocumento> documentosDB;

  public TipoSolicitudDocumentoConverter()
  {
		try {
			GenericDao genTipoSolcDocumDAO = (GenericDao) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTipoSolcDoc = Busqueda
					.forClass(TiivsTipoSolicDocumento.class);
			filtroTipoSolcDoc.add(Restrictions.isNotNull("id"));
			documentosDB = genTipoSolcDocumDAO
					.buscarDinamico(filtroTipoSolcDoc);

		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION
					+ " al obtener lista de documentos", e);
		}
  }

  public Object getAsObject(FacesContext arg0, UIComponent arg1, String submittedValue)
    throws ConverterException
  {
    if ((submittedValue == null) || (submittedValue.trim().equals("")))
      return null;
    if (submittedValue.contains(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
      TiivsTipoSolicDocumento docu = new TiivsTipoSolicDocumento();
//    docu.setCodDoc(submittedValue);
      docu.setId(new TiivsTipoSolicDocumentoId("",submittedValue));
      return docu;
    }
    try {
      for (TiivsTipoSolicDocumento doc : documentosDB)
        if (submittedValue.equals(doc.getId().getCodDoc()+"_"+doc.getId().getCodTipoSolic()))
        {
          return doc;
        }
    }
    catch (Exception e) {
      throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de conversión", "No valido documento"));
    }

    return null;
  }

  public String getAsString(FacesContext arg0, UIComponent arg1, Object value)
    throws ConverterException
  {
    if ((value == null) || (value.equals("")))
    {
      return "";
    }if (((value instanceof String)) && (((String) value).contains(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)))
    {
      return (String)value;
    }

    TiivsTipoSolicDocumento docu = (TiivsTipoSolicDocumento)value;
    
    if(docu.getId()!=null)
    	return docu.getId().getCodDoc()+"_"+docu.getId().getCodTipoSolic();
    
    return null;
  }
}
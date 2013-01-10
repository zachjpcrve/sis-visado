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

@FacesConverter(value = "tipoSolicitudDocumentoConverter")
public class TipoSolicitudDocumentoConverter implements Converter {
	
	private static Logger logger = Logger.getLogger(TipoSolicitudDocumentoConverter.class);
	public static List<TiivsTipoSolicDocumento> documentosDB;

	public TipoSolicitudDocumentoConverter() {
		ListasIniciales lst = (ListasIniciales) SpringInit.getApplicationContext().getBean("metodoInicial");
		try {
			GenericDao genTipoSolcDocumDAO = (GenericDao) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTipoSolcDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);
			filtroTipoSolcDoc.add(Restrictions.isNotNull("id"));

			documentosDB = genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);
			logger.info("****** " + documentosDB.size());
			System.out.println(" iniciarListas : " + documentosDB.size());

			logger.info("************* TipoSolicitudDocumentoConverter ***********************"
					+ lst);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getAsObject(FacesContext arg0, UIComponent arg1,
			String submittedValue) throws ConverterException {
		if ((submittedValue == null) || (submittedValue.trim().equals("")))
			return null;
		if (submittedValue.equals("Otros")) {
			TiivsTipoSolicDocumento docu = new TiivsTipoSolicDocumento();
//			docu.setCodDoc(submittedValue);
			docu.setTiivsDocumento(new TiivsDocumento(submittedValue));

			return docu;
		}
		try {
			for (TiivsTipoSolicDocumento doc : documentosDB)
				if (doc.getTiivsDocumento().getCodDocumento().equals(submittedValue)) {
					return doc;
				}
		} catch (Exception e) {
			throw new ConverterException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Error de conversión",
					"No valido documento"));
		}

		System.out.println("return null " + documentosDB);

		return null;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object value)
			throws ConverterException {
		if ((value == null) || (value.equals(""))) {
			return "";
		}
		if (((value instanceof String)) && (value.equals("Otros"))) {
			return (String) value;
		}

		TiivsTipoSolicDocumento docu = (TiivsTipoSolicDocumento) value;

//		return docu.getTiivsDocumento().getCodDocumento();
		return null;
	}
}
package com.bbva.common.listener.SpringInit;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.mb.SeguridadMB;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;

public class ListasIniciales {
	public static Logger logger = Logger.getLogger(ListasIniciales.class);
	public static List<TiivsTipoSolicDocumento> documentosDB;
	
	public ListasIniciales() {
	}
	
	
	public void iniciarListas(){
		logger.info("*******************  iniciarListas s****************************");
		
		if(SpringInit.getApplicationContext()!=null){
			logger.info("********************** tipoSolicitudDocumentoConverter *****************");
		GenericDao<TiivsTipoSolicDocumento, Object> genTipoSolcDocumDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSolcDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);		
		filtroTipoSolcDoc.add(Restrictions.isNotNull("codDoc"));
		try {
			documentosDB = genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);
			logger.info("****** "+ documentosDB);
		} catch (Exception ex) {			
			ex.printStackTrace();
		}		
	}
	}
}

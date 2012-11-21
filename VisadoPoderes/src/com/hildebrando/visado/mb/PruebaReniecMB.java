package com.hildebrando.visado.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecDUMMY;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Persona;

/**
 * @author dmclemente
 * @version 1.0
 */

@ManagedBean(name="pruebita")
@SessionScoped
public class PruebaReniecMB {

	Persona persona;
	private String nroDoc;
	
	public static Logger logger = Logger.getLogger(PruebaReniecMB.class);
	
	/**
	 * Método de prueba del servicio de consulta Reniec DUMMY con el  
	 * NroDoc enviado desde el formulario web.
	 * @param event Representa el ActionEvent * 
	 */
	public void buscarReniec(ActionEvent event){
		persona = new Persona();
		BResult resultado = null;
		if (getNroDoc()!=null) {
			logger.info("El DNI a buscar es: "+getNroDoc());
		}
		ObtenerPersonaReniecService reniecService = new ObtenerPersonaReniecDUMMY();
		try {
			resultado = reniecService.devolverPersonaReniecDNI("", "", getNroDoc());
		} catch (ServiceException e) {
			logger.error(e);
			e.printStackTrace();
		}
		//Verificando el resultado: EXITO=0, NO_EXISTE_RENIEC=5200
		logger.info("[Resultado]-Code: "+resultado.getCode());		
		if(resultado.getCode()==0){
			persona = (Persona)resultado.getObject();
			logger.info("PERSONA : "+persona.getNombreCompleto() + 
					"\nDNI: "+persona.getNumerodocIdentidad());
		}
		
	}
	
	
	/**
	 * @return the persona
	 */
	public Persona getPersona() {
		return persona;
	}

	/**
	 * @param persona the persona to set
	 */
	public void setPersona(Persona persona) {
		this.persona = persona;
	}


	/**
	 * @return the nroDoc
	 */
	public String getNroDoc() {
		return nroDoc;
	}


	/**
	 * @param nroDoc the nroDoc to set
	 */
	public void setNroDoc(String nroDoc) {
		this.nroDoc = nroDoc;
	}
	
	
	
}

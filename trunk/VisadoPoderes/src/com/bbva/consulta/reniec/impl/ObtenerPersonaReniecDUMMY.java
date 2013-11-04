
package com.bbva.consulta.reniec.impl;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.bbva.consulta.reniec.util.Persona;
import com.hildebrando.visado.dto.ParametrosReniec;

/**
 * Implementación DUMMY del servicio consulta Reniec, solo se está verificando
 * que el dni enviado como parámetro coincida con el valor definido.
 * @author dmclemente
 * @version 1.0
 */
public class ObtenerPersonaReniecDUMMY implements ObtenerPersonaReniecService {
	
	private Logger logger = Logger.getLogger(ObtenerPersonaReniecDUMMY.class);
	
	public BResult devolverPersonaReniecDNI(String usuario, String oficina,String dni) throws ServiceException {
		logger.debug("===== devolverPersonaReniecDNI() - DUMMY ====");
		
		BResult result = new BResult();		
		Persona p = null;
		
		if(dni.equalsIgnoreCase("11111111")){
			p = new Persona();
			p.setNombre("DIEGO");
			p.setApellidoPaterno("CLEMENTE");
			p.setApellidoMaterno("O ");
			p.setNombreCompleto("DIEGO CLEMENTE O ");
			p.setDireccion("URB. ALAMEDA G 53");
			p.setFechaNac("19870220");
			p.setNumerodocIdentidad("11111111");
			
			result.setCode(Constantes.EXITO);
			result.setMessage(Constantes.VACIO);
			
		}else{
			result.setCode(Integer.parseInt(Constantes.RENIEC_NO_EXISTE));	
			result.setMessage(Constantes.RENIEC_NO_EXISTE_CLIENTE);
		}
				
		result.setObject(p);
		return result;
	}

	@Override
	public BResult devolverPersonaReniecDNI(ParametrosReniec parametrosReniec,
			String dni) throws ServiceException {
		logger.debug("===== devolverPersonaReniecDNI() - DUMMY ====");
		
		BResult result = new BResult();		
		Persona p = null;
		
		//TODO Agregar mas personas DUMMY si se desea.
		if(dni.equalsIgnoreCase("11111111")){
			p = new Persona();
			p.setNombre("DIEGO");
			p.setApellidoPaterno("CLEMENTE");
			p.setApellidoMaterno("O ");
			p.setNombreCompleto("DIEGO CLEMENTE O ");
			p.setDireccion("URB. ALAMEDA G 53");
			p.setFechaNac("19870220");
			p.setNumerodocIdentidad("11111111");
			
			result.setCode(Constantes.EXITO);
			result.setMessage(Constantes.VACIO);
			
		}else{
			result.setCode(Integer.parseInt(Constantes.RENIEC_NO_EXISTE));	
			result.setMessage(Constantes.RENIEC_NO_EXISTE_CLIENTE);
		}
				
		result.setObject(p);
		return result;
	}

}

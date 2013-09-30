package com.bbva.consulta.pea.impl;

import org.apache.log4j.Logger;

import com.bbva.consulta.pea.ObtenerDatosPersonaPEAService;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.bbva.consulta.reniec.util.Persona;


public class ObtenerDatosPersonaPEAServiceDummy implements ObtenerDatosPersonaPEAService{

	private Logger logger = Logger.getLogger(ObtenerDatosPersonaPEAServiceDummy.class);
	
	@Override
	public BResult obtenerDatosGeneralesPEA1(String usuConsulta, String tipDoc,
			String numDoc, String servUrl) {
		logger.debug("=== [DUMMY-HOST] obtenerDatosGeneralesPEA1() === ");
		BResult res= new BResult();
		res.setCode(900);
		res.setMessage("Respuesta no satisfactoria");
		Persona persona= null;
		logger.debug("[DUMMY-HOST]-numDoc:"+numDoc);
		
		if(numDoc.equalsIgnoreCase("03160115")){	
			persona = new Persona();
			persona.setNombre("LEON");
			persona.setApellidoPaterno("CANO");
			persona.setApellidoMaterno("CABALLERO");
			persona.setNumerodocIdentidad("03160115");
			persona.setTelefono("997712648");//4182666233
			
			logger.debug("[DUMMY-HOST]-Nombre:"+persona.getNombre());
			logger.debug("[DUMMY-HOST]-ApPaterno:"+persona.getApellidoPaterno());
			
			//<NS1:codigo>0000</NS1:codigo>
			//<NS1:descripcion>Respuesta satisfactoria</NS1:descripcion>

			//<NS1:codigoCentral>00001481</NS1:codigoCentral>
			//<NS1:doi>
	        	//<NS1:tipo>L</NS1:tipo>
	        	//<NS1:numero>03160115</NS1:numero>
	        	//<NS1:vigencia/>
			//</NS1:doi>
			//<NS1:nombres>FELICITAS</NS1:nombres>
			//<NS1:apellidoPaterno>TRES AMANTES</NS1:apellidoPaterno>
			//<NS1:apellidoMaterno>COLUNGA</NS1:apellidoMaterno>
			//<NS1:telefono>4182666233</NS1:telefono>
			//<NS1:error>
	        //<NS1:codigo>PS900</NS1:codigo>
	        //<NS1:mensaje>Respuesta satisfactoria</NS1:mensaje>
			//</NS1:error>
			
			res.setObject(persona);
			res.setCode(Constantes.EXITO);
			res.setMessage("Respuesta satisfactoria");
		}
		logger.debug("[DUMMY-HOST]-"+res.getCode());
		logger.debug("[DUMMY-HOST]-"+res.getMessage());
		return res;
	}

}

package com.bbva.consulta.pea;

import com.bbva.consulta.reniec.util.BResult;

/**
 * Interface que obtiene datos de una persona consultando la PEA1
 * desde Host.
 * @author dmclemente
 * @version 1.0
 */

public interface ObtenerDatosPersonaPEAService {
	
	public abstract BResult obtenerDatosGeneralesPEA1(String usuConsulta,String tipDoc, String numDoc,String servUrl);

}

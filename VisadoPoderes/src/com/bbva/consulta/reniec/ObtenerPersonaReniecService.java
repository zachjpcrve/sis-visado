package com.bbva.consulta.reniec;

import javax.xml.rpc.ServiceException;

import com.bbva.consulta.reniec.util.BResult;

/**
 * Interface que obtiene datos de una persona registrada en la RENIEC.
 * @author dmclemente
 * @version 1.0
 */
public interface ObtenerPersonaReniecService {

	/**
	 * M�todo que obtiene los datos de una persona seg�n su DNI
	 * @param usuario Cuenta del Oficinista que desa obtener los datos de una persona
	 * @param oficina Oficina desde donde se desea acceder a los datos de una persona
	 * @param dni DNI de la persona de la que se desea obtener los datos
	 * @return {@link BResult} con los datos de la {@link Persona}, un mensaje y un
	 * 		c�digo de &eacute;xito o error.
	 */
	public abstract BResult devolverPersonaReniecDNI(String usuario, String oficina,String dni) throws ServiceException;
}

package com.bbva.consulta.pea.impl;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pe.com.grupobbva.harecservice.BodyRq;
import pe.com.grupobbva.harecservice.DatosGeneralesXPersonaRq;
import pe.com.grupobbva.harecservice.HarecServiceStub;
import pe.com.grupobbva.harecservice.HeadRq;
import pe.com.grupobbva.harecservice.ObtenerDatosXPersonaDocument;
import pe.com.grupobbva.harecservice.ObtenerDatosXPersonaResponseDocument;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.consulta.pea.ObtenerDatosPersonaPEAService;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.bbva.consulta.reniec.util.Persona;
import com.bbva.persistencia.generica.util.Utilitarios;

/**
 * Clase que obtiene los datos de una persona desde HOST mediante un Servicio Web. La
 * transaccion consultada en HOST es la PEA 1. Implementa la interface {@link ObtenerDatosPersonaPEAService }
 * @author dmclemente
 * @version 1.0
 */

public class ObtenerDatosPersonaPEAServiceImpl implements ObtenerDatosPersonaPEAService {

	/**
	 * Escribe los logs en un archivo externo seg&uacute;n la configuraci&oacute;n
	 * del <code>log4j.properties</code>.
	 */
	private Logger logger = Logger.getLogger(ObtenerDatosPersonaPEAServiceImpl.class);
	
	/**
	 * Metodo encargado de setear el proxy del servicio web en base al endpoint, también 
	 * se encarga de realizar la consulta en HOST.
	 * @param usuConsulta Usuario que consultara a la tx
	 * @param tipDoc Tipo de Documento
	 * @param numDoc Numero de documento
	 * @param urlServicio URL del servicio web
	 * @return res Objeto del tipo {@link BResult} donde se guarda el resultado 
	 * **/
	@Override
	public BResult obtenerDatosGeneralesPEA1(String usuConsulta,String tipDoc, String numDoc,String urlServicio) {
		logger.debug("==== obtenerDatosGeneralesPEA1()-HOST ===");
		BResult res= new BResult();
		res.setCode(900);
		res.setMessage("Respuesta no satisfactoria");
		HarecServiceStub stub = null;
		
		try {
			stub = starEndPointService(urlServicio);
			Persona persona= new Persona();
			logger.debug("[HOST]-Stub :"+stub);
			ObtenerDatosXPersonaResponseDocument rpta = null;
			if (stub != null) {
				rpta =  buscarDatosPersonaHost(stub, usuConsulta, tipDoc, numDoc);
			} else{
				logger.debug("[HOST]-El stub es nulo ..");
			}
			if (rpta != null) {
				logger.debug("[HOST]-CodigoHead Rpta:"+rpta.getObtenerDatosXPersonaResponse().getHead().getCodigo());
				logger.debug("[HOST]-ResultadoBody Rpta:"+rpta.getObtenerDatosXPersonaResponse().getBody().getResultado());
	        
				if (("0000".equals(rpta.getObtenerDatosXPersonaResponse().getHead().getCodigo())) && ("1".equals(rpta.getObtenerDatosXPersonaResponse().getBody().getResultado())))
				{
					if (StringUtils.isNotBlank(rpta.getObtenerDatosXPersonaResponse().getBody().getNombres()))
					{
						persona.setNombre(StringUtils.trimToEmpty(rpta.getObtenerDatosXPersonaResponse().getBody().getNombres()));
						logger.debug("persona.getNombre(): "+persona.getNombre());
					}
					if (StringUtils.isNotBlank(rpta.getObtenerDatosXPersonaResponse().getBody().getApellidoPaterno()))
					{
						persona.setApellidoPaterno(StringUtils.trimToEmpty(rpta.getObtenerDatosXPersonaResponse().getBody().getApellidoPaterno()));
						logger.debug("persona.getApepat(): "+persona.getApellidoPaterno());
					}
					if (StringUtils.isNotBlank(rpta.getObtenerDatosXPersonaResponse().getBody().getApellidoMaterno()))
					{
						persona.setApellidoMaterno(StringUtils.trimToEmpty(rpta.getObtenerDatosXPersonaResponse().getBody().getApellidoMaterno()));
						logger.debug("persona.getApemat(): "+persona.getApellidoMaterno());
					}
					if (StringUtils.isNotBlank(rpta.getObtenerDatosXPersonaResponse().getBody().getTelefono()))
					{
						persona.setTelefono(StringUtils.trimToEmpty(rpta.getObtenerDatosXPersonaResponse().getBody().getTelefono()));
						logger.debug("persona.getTelefono(): "+persona.getTelefono());
					}
					if (StringUtils.isNotBlank(rpta.getObtenerDatosXPersonaResponse().getBody().getDoi().getNumero()))
					{
						persona.setNumerodocIdentidad(StringUtils.trimToEmpty(rpta.getObtenerDatosXPersonaResponse().getBody().getDoi().getNumero()));
						logger.debug("persona.getNumerodocIdentidad(): "+persona.getNumerodocIdentidad());
					}
					if (StringUtils.isNotBlank(rpta.getObtenerDatosXPersonaResponse().getBody().getCodigoCentral()))
					{
						persona.setCodCentral(StringUtils.trimToEmpty(rpta.getObtenerDatosXPersonaResponse().getBody().getCodigoCentral()));
						logger.debug("persona.getCodCentral(): "+persona.getCodCentral());
					}
					res.setObject(persona);
					res.setCode(Constantes.EXITO);
					res.setMessage(rpta.getObtenerDatosXPersonaResponse().getHead().getDescripcion());
				}
				else {
					res.setCode(900);
					res.setMessage(rpta.getObtenerDatosXPersonaResponse().getBody().getError().getMensaje());
					logger.debug("[else]-Ha ocurrido un error al obtenerDatosPersona en Host");
				}
			}else{
				logger.debug("La rpta es nulo");
			}
		//Antes res.setObject(persona);	
		}
		finally{
			try{
				if (stub != null){
					stub.cleanup();
				}
			} catch (AxisFault e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"del tipo AxisFault: ",e);
			}
		}
		logger.debug("[HOST]-"+res.getCode());
		logger.debug("[HOST]-"+res.getMessage());
		return res;
	}
	
	/**
	 * Metodo que se encarga de setear el endpoint del servicio de consulta
	 * @param  object Representa el endPoint
	 * @return Retorna un stub del tipo {@link HarecServiceStub}
	 * **/
	private HarecServiceStub starEndPointService(Object object) {
		logger.debug("=== inicia starEndPointService() ===");
		HarecServiceStub proxy = null;
		try {
			proxy = new HarecServiceStub(object.toString());
			logger.debug("proxy: "+proxy);
		} catch (AxisFault e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"del tipo AxisFault: ",e);
			return null;
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"general en starEndPointService: ",e);
			return null;
		}
		//Setear el timeOut del servicio Web	
		// proxy._getServiceClient().getOptions().setTimeOutInMilliSeconds(Long.parseLong("50"));
		logger.debug("=== saliendo de starEndPointService() ===");
		return proxy;
	}

	/**
	 * Se encarga de realizar la consulta de datosPersona en Host.
	 * @param proxy El proxy de consulta del tipo {@link HarecServiceStub}
	 * @param usuaCons Usuario que consulta la transaccion
	 * @param tipoDoi Tipo de Documento a consultar
	 * @param numDoi Numero de Documento a consultar
	 * @return Objeto del tipo {@link ObtenerDatosXPersona} que contiene
	 * el dato de la persona encontada en Host
	 * */
	private ObtenerDatosXPersonaResponseDocument buscarDatosPersonaHost(HarecServiceStub proxy, 
			String usuaCons, String tipoDoi, String numDoi)
	{
		DatosGeneralesXPersonaRq datosGeneralesXPersonaRq = DatosGeneralesXPersonaRq.Factory.newInstance();
	    HeadRq headRq = HeadRq.Factory.newInstance();
	    BodyRq bodyRq = BodyRq.Factory.newInstance();
	    datosGeneralesXPersonaRq.setHead(headRq);
	    datosGeneralesXPersonaRq.setBody(bodyRq);
	    logger.debug("====== ANTES DE CONSULTAR A HOST ====");
	    logger.debug("usuarioConsulta:"+usuaCons);
	    logger.debug("tipoDoi:"+tipoDoi);
	    logger.debug("numeroDoi:"+numDoi);
	    String tipoDoiCons = Utilitarios.asignarEquivTipoDoi(tipoDoi);
	    logger.debug("nuevo tipoDoiCons:"+tipoDoiCons);
	    
	    ObtenerDatosXPersonaDocument obtenerDatosXPersona = ObtenerDatosXPersonaDocument.Factory.newInstance();
	    obtenerDatosXPersona.setObtenerDatosXPersona(datosGeneralesXPersonaRq);
	    obtenerDatosXPersona.getObtenerDatosXPersona().getHead().setUsuario(usuaCons);
	    if(tipoDoiCons.equalsIgnoreCase("C")){
	    	logger.debug("->Se buscara solo por CodigoCentral");
	    	obtenerDatosXPersona.getObtenerDatosXPersona().getBody().setCodigoCentral(numDoi);
	    }else{
	    	logger.debug("->Se buscara por Tipo y NroDoi");
	    	obtenerDatosXPersona.getObtenerDatosXPersona().getBody().setTipoDOI(tipoDoiCons);
		    obtenerDatosXPersona.getObtenerDatosXPersona().getBody().setNroDOI(numDoi);
		}
	    
	    try {
	    	logger.debug("Se consulta al servicioHost: ObtenerDatosXPersona");
	    	return proxy.ObtenerDatosXPersona(obtenerDatosXPersona);
	    } catch (RemoteException e) {
	    	logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al buscarDatosPersonaHost(): ",e);
	    } catch (Exception e) {
	    	logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"en buscarDatosPersonaHost(): ",e);
	    }
        return null;
    }

}

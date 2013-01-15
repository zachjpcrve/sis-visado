package com.bbva.consulta.reniec.impl;

import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;

import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.bbva.consulta.reniec.util.Persona;

import com.ibm.www.BBVA_RENIEC_WSDLPortType;
import com.ibm.www.BBVA_RENIEC_WSDLSOAP_HTTP_ServiceLocator;
import com.ibm.www.RENIEC_7_REPLY_Type;
import com.ibm.www.RENIEC_7_REQUEST_Type;
import com.ibm.www.Cabecera;
import com.ibm.www.SubTramaConsulta7;


/**
 * Clase que obtiene los datos de una persona registrada en la RENIEC 
 * mediante un Servicio Web
 * @author dmclemente
 * @version 1.0
 */
public class ObtenerPersonaReniecServiceImpl implements ObtenerPersonaReniecService {
	private Logger logger = Logger.getLogger(ObtenerPersonaReniecServiceImpl.class);
	private static boolean initialized = false;
	
	/**
	 * Escribe los logs en un archivo externo seg&uacute;n la configuraci&oacute;n
	 * del <code>log4j.properties</code>.
	 */
	private Logger log = Logger.getLogger(ObtenerPersonaReniecServiceImpl.class);
	
	/**
	 * Clase para acceder a las propiedades de un archivo .properties
	 */
	private Properties properties;
	private static String vvProxySet = Constantes.VACIO;
	private static String vvProxyHost = Constantes.VACIO;
	private static String vvProxyPort = Constantes.VACIO;
	private static String vvUrl = Constantes.VACIO;

	/**
	 * @return the vvProxySet
	 */
	public static String getVvProxySet() {
		return vvProxySet;
	}

	/**
	 * @param vvProxySet the vvProxySet to set
	 */
	public static void setVvProxySet(String vvProxySet) {
		ObtenerPersonaReniecServiceImpl.vvProxySet = vvProxySet;
	}

	/**
	 * @return the vvProxyHost
	 */
	public static String getVvProxyHost() {
		return vvProxyHost;
	}

	/**
	 * @param vvProxyHost the vvProxyHost to set
	 */
	public static void setVvProxyHost(String vvProxyHost) {
		ObtenerPersonaReniecServiceImpl.vvProxyHost = vvProxyHost;
	}

	/**
	 * @return the vvProxyPort
	 */
	public static String getVvProxyPort() {
		return vvProxyPort;
	}

	/**
	 * @param vvProxyPort the vvProxyPort to set
	 */
	public static void setVvProxyPort(String vvProxyPort) {
		ObtenerPersonaReniecServiceImpl.vvProxyPort = vvProxyPort;
	}

	/**
	 * @return the vvUrl
	 */
	public static String getVvUrl() {
		return vvUrl;
	}

	/**
	 * @param vvUrl the vvUrl to set
	 */
	public static void setVvUrl(String vvUrl) {
		ObtenerPersonaReniecServiceImpl.vvUrl = vvUrl;
	}

	/**
	 * Método de inicializaci&oacute;n de la clase.
	 */
	public synchronized void initialize() {
		if (!initialized) {
			try {
				setVvProxySet(properties.getProperty(Constantes.PROXY_HOST));
				setVvProxyHost(properties.getProperty(Constantes.PROXY_HOST));
				setVvProxyPort(properties.getProperty(Constantes.PROXY_PORT));
				setVvUrl(properties.getProperty(Constantes.RENIEC_URL_WS));
				
				logger.info("[RENIEC]-URL Servicio: "+ getVvUrl());
				logger.info("[RENIEC]-PROXY_SET_RENIEC=" + getVvProxySet());
				logger.info("[RENIEC]-PROXY_HOST_RENIEC="+ getVvProxyHost());
				logger.info("[RENIEC]-PROXY_PORT_RENIEC="+ getVvProxyPort());	
			} catch (Exception e) {
				logger.error("Ocurrio un error en initialize(): " + e);
			}
		}
	}
	
	/**
	 * Método que obtiene los datos de una persona, mediante un Servicio Web, según su DNI
	 * @param usuario Cuenta del Oficinista que desea obtener los datos de una persona.
	 * @param oficina Oficina desde donde se desea acceder a los datos de una persona.
	 * @param dni DNI de la persona de la que se desea obtener los datos.
	 * @return {@link BResult} con los datos de la {@link Persona}, un mensaje y un
	 * 		código de &eacute;xito o error.
	 */
	public BResult devolverPersonaReniecDNI(String usuario, String oficina, String dni) {
		logger.debug(" ==== devolverPersonaReniecDNI ==== ");
		RENIEC_7_REPLY_Type rpta7 = null;
		Properties props = null;
		Properties newprops = null;
		Persona persona = null;
		BResult result = new BResult();
		try {
			if (!(getVvProxyHost() == null || getVvProxyHost().equals(Constantes.VACIO))) {
				props = new Properties(System.getProperties());
				props.put(Constantes.HTTP_PROXY_SET, getVvProxySet());
				props.put(Constantes.HTTP_PROXY_HOST, getVvProxyHost());
				props.put(Constantes.HTTP_PROXY_PORT, getVvProxyPort());
				newprops = new Properties(props);
				System.setProperties(newprops);
			}

			BBVA_RENIEC_WSDLSOAP_HTTP_ServiceLocator loc = new BBVA_RENIEC_WSDLSOAP_HTTP_ServiceLocator();
			
			//TODO [RENIEC] Cambiar valor
			BBVA_RENIEC_WSDLPortType port= loc.getSOAP_HTTP_Port();
			//BBVA_RENIEC_WSDLPortType port = loc.getSOAP_HTTP_Port(new URL(getVvUrl()));
			
			RENIEC_7_REQUEST_Type request7 = new RENIEC_7_REQUEST_Type();
			SubTramaConsulta7 subtrama7 = new SubTramaConsulta7();			

			//Se completa la Cabecera
			Cabecera cab = new Cabecera();
			cab.setVersion(Constantes.RENIEC_VERSION);
			cab.setLongCabecera(Constantes.RENIEC_LONG_CABECERA);
			cab.setTipoServicio(Constantes.RENIEC_TIPO_SERVICIO);
			cab.setLongTotalTrama(Constantes.RENIEC_LONG_CABECERA + Constantes.RENIEC_LONG_SUB_TRAMA_CONSULTA);
			cab.setFragmentacion(Constantes.RENIEC_FRAGMENTACION);
			cab.setTTL(Constantes.RENIEC_TTL);
			cab.setTipoConsulta(Constantes.RENIEC_TIPO_CONSULTA);
			cab.setCaracVerificacion(Constantes.RENIEC_CARACTERES_VERIFICACION);			
			cab.setCodInstSolicitante(Constantes.RENIEC_COD_INST_SOLICITANTE);
			cab.setCodServReniec(Constantes.RENIEC_COD_SERVIDOR_RENIEC);
			cab.setAgenciaInstSolic(oficina);
			cab.setUsuarioFinalInstSol(usuario);
			cab.setHostFinalInstSol(Constantes.RENIEC_HOST_INST_SOLICITANTE);
			cab.setReservado(Constantes.RENIEC_RESERVADO_CHAR10);
			//Se completa la SubTrama de consulta.
			subtrama7.setNumDNI(dni);
			subtrama7.setReservado(Constantes.RENIEC_RESERVADO_CHAR5);
			subtrama7.setCaracVerificacion(Constantes.RENIEC_CARACTER_VERIFICACION);
			subtrama7.setTipoDocumento(Constantes.RENIEC_TIPO_DOCUMENTO);

			request7.setCabecera(cab);
			request7.setSubTramaConsulta(subtrama7);
			
			rpta7 = port.operation7(request7);			
			
		} catch (Exception e1) {
			result.setMessage(Constantes.ERROR_SERVICIO_WEB);
			result.setCode(Constantes.ERROR_GENERAL);
			log.error("Error en conexión del servicio web en devolverPersonaReniecDNI=" + e1);
			return result;
		}

		try {
			
			if(rpta7.getSubTramaRespuesta().getCodError()!=null && 
					!rpta7.getSubTramaRespuesta().getCodError().equalsIgnoreCase(Constantes.VACIO) 
						&& rpta7.getSubTramaRespuesta().getCodError().substring(0,4).trim().equalsIgnoreCase(Constantes.RENIEC_EXITO)){
				
				persona = new Persona();
				persona.setNombre(rpta7.getSubTramaRespuesta().getNombres().trim());
				persona.setApellidoPaterno(rpta7.getSubTramaRespuesta().getApellidoPat().trim());
				persona.setApellidoMaterno(rpta7.getSubTramaRespuesta().getApellidoMat().trim());
				persona.setNombreCompleto(rpta7.getSubTramaRespuesta().getNombres().trim());
				persona.setDireccion(rpta7.getSubTramaRespuesta().getDireccion().trim());
				persona.setNumerodocIdentidad((rpta7.getSubTramaRespuesta().getNumDNI().trim()));
				
				result.setMessage(Constantes.VACIO);
				result.setCode(Constantes.EXITO);
				result.setObject(persona);			
				
			} else if (rpta7.getSubTramaRespuesta().getCodError().equals(Constantes.RENIEC_NO_EXISTE)) {
				result.setMessage(Constantes.RENIEC_NO_EXISTE_CLIENTE);
				result.setCode(Integer.parseInt(Constantes.RENIEC_NO_EXISTE));
			} else {
				result.setMessage(rpta7.getSubTramaRespuesta().getCodError());
				result.setCode(Constantes.ERROR);
			}
		} catch (Throwable e) {
			result.setMessage(Constantes.RENIEC_ERROR_PROCESAR_TRAMA_RESPUESTA);
			result.setCode(Constantes.ERROR_GENERAL);
			log.error(Constantes.RENIEC_ERROR_PROCESAR_TRAMA_RESPUESTA + e);
		}
		return result;
	}
	
	
	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}

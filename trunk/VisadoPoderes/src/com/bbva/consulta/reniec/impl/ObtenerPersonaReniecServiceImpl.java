package com.bbva.consulta.reniec.impl;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.bbva.consulta.reniec.util.Persona;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.pe.SIR.ents.body.consultaPorDNI.DatosDomicilio;
import com.grupobbva.pe.SIR.ents.body.consultaPorDNI.DatosNacimiento;
import com.grupobbva.pe.SIR.ents.body.consultaPorDNI.DatosPersona;
import com.grupobbva.pe.SIR.ents.header.RequestHeader;
import com.grupobbva.pe.SIR.service.message.ConsultaPorDNIRequest;
import com.grupobbva.pe.SIR.service.message.ConsultaPorDNIResponse;
import com.grupobbva.pe.SIR.service.message.WS_PersonaReniec_ServiceLocator;
import com.hildebrando.visado.dto.ParametrosReniec;
import com.ibm.www.BBVA_RENIEC_WSDLPortType;
import com.ibm.www.BBVA_RENIEC_WSDLSOAP_HTTP_ServiceLocator;
import com.ibm.www.Cabecera;
import com.ibm.www.RENIEC_7_REPLY_Type;
import com.ibm.www.RENIEC_7_REQUEST_Type;
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

	@Override
	public BResult devolverPersonaReniecDNI(ParametrosReniec parametrosReniec,
			String dni) throws ServiceException {
		
		Persona persona = null;
		BResult result = new BResult();
		
		if(parametrosReniec!=null){
			logger.debug("[ws]-usuarioConsulta: "+parametrosReniec.getUsuario());
			logger.debug("[ws]-RutaNuevoServicio: "+parametrosReniec.getRutaServicio());
			
			RequestHeader cabecera = new RequestHeader();
			cabecera.setCanal(parametrosReniec.getCanal());
			cabecera.setCodigoAplicacion(parametrosReniec.getCodigoAplicacion());
			cabecera.setCodigoInterfaz(parametrosReniec.getCodigoInterfaz());
			//Formato: 2013-11-08-12.24.01.123456
			cabecera.setFechaHoraEnvio(Utilitarios.obtenerFechaHoraSegMil());
			cabecera.setIdEmpresa(parametrosReniec.getEmpresa());
			//Formato: 20131108122401123456VISADOP007734
			cabecera.setIdTransaccion(Utilitarios.obtenerFechaHoraSegMilTx() 
					+ cabecera.getCodigoAplicacion()+cabecera.getUsuario());		
			cabecera.setUsuario(parametrosReniec.getUsuario());
			logger.debug("======== [cabecera]==========");
			logger.debug("[WSReniec][cabecera]-canal: "+cabecera.getCanal());
			logger.debug("[WSReniec][cabecera]-codAplicacion: "+cabecera.getCodigoAplicacion());
			logger.debug("[WSReniec][cabecera]-empresa: "+cabecera.getIdEmpresa());
			logger.debug("[WSReniec][cabecera]-usuario: "+cabecera.getUsuario());
			logger.debug("[WSReniec][cabecera]-fechHoraEnvio: "+cabecera.getFechaHoraEnvio());
			logger.debug("[WSReniec][cabecera]-IdTransaccion: "+cabecera.getIdTransaccion());
			logger.debug("[WSReniec][cabecera]-CodInterfaz: "+cabecera.getCodigoInterfaz());
			
			com.grupobbva.pe.SIR.ents.body.consultaPorDNI.ConsultaPorDNIRequest refConsultaPorDNIRequest = new com.grupobbva.pe.SIR.ents.body.consultaPorDNI.ConsultaPorDNIRequest();
			refConsultaPorDNIRequest.setCentroCostos(parametrosReniec.getCentroCosto());
			refConsultaPorDNIRequest.setFormatoFirma(parametrosReniec.getFormatoFirma());
			refConsultaPorDNIRequest.setHostSolicitante(parametrosReniec.getHostSolicitante());
			refConsultaPorDNIRequest.setIndConsultaDatos(parametrosReniec.getConsultaDatos());
			refConsultaPorDNIRequest.setIndConsultaFirma(parametrosReniec.getConsultaFirma());
			refConsultaPorDNIRequest.setIndConsultaFoto(parametrosReniec.getConsultaFoto());
			refConsultaPorDNIRequest.setNumeroDNIConsultado(dni);
			refConsultaPorDNIRequest.setNumeroDNISolicitante(parametrosReniec.getDniSolicitante());
			refConsultaPorDNIRequest.setRegistroCodUsuario(parametrosReniec.getRegistroUsuario());
			refConsultaPorDNIRequest.setTipoAplicacion(parametrosReniec.getTipoAplicacion());
			
			logger.debug("======== [Body]==========");
			logger.debug("[WSReniec][body]-CentroCosto: "+refConsultaPorDNIRequest.getCentroCostos());
			logger.debug("[WSReniec][body]-HostSolicitante: "+refConsultaPorDNIRequest.getHostSolicitante());
			logger.debug("[WSReniec][body]-tipoAplicacion: "+refConsultaPorDNIRequest.getTipoAplicacion());
			logger.debug("[WSReniec][body]-DNISolicitante: "+refConsultaPorDNIRequest.getNumeroDNISolicitante());
			logger.debug("[WSReniec][body]-DNIConsultado: "+refConsultaPorDNIRequest.getNumeroDNIConsultado());
			logger.debug("[WSReniec][body]-IndicadorConsDatos: "+refConsultaPorDNIRequest.getIndConsultaDatos());
			logger.debug("[WSReniec][body]-IndicadorConsFoto: "+refConsultaPorDNIRequest.getIndConsultaFoto());
			logger.debug("[WSReniec][body]-IndicadorConsFirma: "+refConsultaPorDNIRequest.getIndConsultaFirma());
			logger.debug("[WSReniec][body]-FormatoFirma: "+refConsultaPorDNIRequest.getFormatoFirma());
			
			ConsultaPorDNIRequest consulta = new ConsultaPorDNIRequest(cabecera, refConsultaPorDNIRequest);
			ConsultaPorDNIResponse rpta = null;
			
			WS_PersonaReniec_ServiceLocator proxy = new  WS_PersonaReniec_ServiceLocator();
			proxy.setWS_PersonaReniecEndpointAddress(parametrosReniec.getRutaServicio());
			
			try {
				rpta = proxy.getWS_PersonaReniec().consultaPorDNI(consulta);
				if(rpta!=null){
					logger.debug("======== [Respuesta]==========");
					logger.debug("[WSReniec][RPTA]-CodRespuesta: "+rpta.getRefResponseHeader().getCodigoRespuesta());
					
					if(rpta.getRefResponseHeader().getCodigoRespuesta().equalsIgnoreCase(Constantes.RENIEC_EXITO)){
						
						com.grupobbva.pe.SIR.ents.body.consultaPorDNI.ConsultaPorDNIResponse response = rpta.getRefConsultaPorDNIResponse();
						
						DatosPersona datosPersona = response.getRespuestaDatos().getDatosPersona();
						DatosDomicilio datosDomicilio = response.getRespuestaDatos().getDatosDomicilio();
						DatosNacimiento datosNacimiento = response.getRespuestaDatos().getDatosNacimiento();
						
						persona = new Persona();
						if(datosPersona.getNombres()!=null){
							logger.debug("\t[WSReniec][RPTA]-Nombres: "+datosPersona.getNombres());
							persona.setNombre(datosPersona.getNombres().trim());
						}else{
							persona.setNombre("");
						}
						if(datosPersona.getApellidoPaterno()!=null){
							logger.debug("\t[WSReniec][RPTA]-ApePaterno: "+datosPersona.getApellidoPaterno());
							persona.setApellidoPaterno(datosPersona.getApellidoPaterno().trim());
						}else{
							persona.setApellidoPaterno("");
						}
						if(datosPersona.getApellidoMaterno()!=null){
							logger.debug("\t[WSReniec][RPTA]-ApeMaterno: "+datosPersona.getApellidoMaterno());
							persona.setApellidoMaterno(datosPersona.getApellidoMaterno().trim());
						}else{
							persona.setApellidoMaterno("");
						}
						if(datosPersona.getApellidoCasada()!=null){
							logger.debug("\t[WSReniec][RPTA]-Ap.Casada: "+datosPersona.getApellidoCasada());
							persona.setApellidoMaterno(persona.getApellidoMaterno()+ " " +datosPersona.getApellidoCasada().trim() );
						}
						
						persona.setNombreCompleto(persona.getNombre()+" "+
								persona.getApellidoPaterno()+" "+persona.getApellidoMaterno());
						logger.debug("\t[WSReniec][RPTA]-Nombre Completo: "+persona.getNombreCompleto());
						
						if(datosDomicilio.getDireccion()!=null){
							persona.setDireccion(datosDomicilio.getDireccion().trim());	
						}else{
							persona.setDireccion("");
						}
						logger.debug("\t[WSReniec][RPTA]-Direccion: "+persona.getDireccion());
						
						if(datosPersona.getNumeroDNIConsultado()!=null){
							persona.setNumerodocIdentidad(datosPersona.getNumeroDNIConsultado().trim());	
						}else{
							persona.setNumerodocIdentidad(dni);
						}
						logger.debug("\t[WSReniec][RPTA]-NumDocIdentidad: "+persona.getNumerodocIdentidad());
						
						if(datosNacimiento.getFecha()!=null){
							persona.setFechaNac(datosNacimiento.getFecha().trim());	
						}else{
							persona.setFechaNac("");
						}
						
						result.setMessage(Constantes.VACIO);
						result.setCode(Constantes.EXITO);
						result.setObject(persona);	
					}
				}	
				
			} catch (RemoteException e) {
				result.setMessage(Constantes.RENIEC_ERROR_PROCESAR_TRAMA_RESPUESTA);
				result.setCode(Constantes.ERROR_GENERAL);
				log.error(Constantes.RENIEC_ERROR_PROCESAR_TRAMA_RESPUESTA + e);
			}catch(Exception ex){
				log.error(Constantes.RENIEC_ERROR_PROCESAR_TRAMA_RESPUESTA +" ->"+ ex);
			}
		}else{
			result.setMessage(Constantes.RENIEC_NO_EXISTE_PARAMETRO);
			result.setCode(Integer.parseInt(Constantes.RENIEC_NO_EXISTE_PARAMETRO_MENSAJE));
		}		
		return result;
	}

}

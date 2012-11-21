package com.bbva.consulta.reniec.util;

public class Constantes {
	
	public static final int EXITO = 0;
	public static final int ERROR = 1;
	public static final int ERROR_GENERAL = 100;
	public static final String VACIO = "";
	
	// Variables para el properties, configuración del proxy en el Servicio Web.
	public static final String HTTP_PROXY_SET = "http.proxySet";
	public static final String HTTP_PROXY_HOST = "http.proxyHost";
	public static final String HTTP_PROXY_PORT = "http.proxyPort";
	
	public static final String RENIEC_EXITO = "0000";
	public static final String RENIEC_NO_EXISTE = "5200";
    
    public static final String RENIEC_ERROR_PROCESAR_TRAMA_RESPUESTA = "Error al procesar trama de respuesta";
    //public static final String RENIEC_NO_EXISTE_CLIENTE = "La persona no figura en la Reniec";
    public static final String RENIEC_NO_EXISTE_CLIENTE = "";    
    public static final String ERROR_SERVICIO_WEB = "No se ha podido realizar la conexi\u00F3n al servicio web del banco para autentificar el usuario.";
}
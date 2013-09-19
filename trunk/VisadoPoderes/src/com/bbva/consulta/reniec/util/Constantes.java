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
	public static final String VOUCHER_EXITO = "0000";
	public static final String VOUCHER_NO_EXISTE = "9999";
	public static final String VOUCHER_NO_EXISTE_CLIENTE = "No existe información del voucher consultado";
    public static final int    RENIEC_TTL = 0;
    public static final int    RENIEC_LONG_CABECERA = 128;
    public static final int    RENIEC_LONG_SUB_TRAMA_CONSULTA = 15;
    public static final String RENIEC_URL_WS = "VV_URL_WS_RENIEC";
    public static final String PROXY_HOST = "VV_PROXY_HOST";
    public static final String PROXY_PORT = "VV_PROXY_PORT";
    public static final String PROXY_SET = "VV_PROXY_SET";
    public static final String RENIEC_FRAGMENTACION = "                      ";
    public static final String RENIEC_RESERVADO_CHAR5  = "     ";
    public static final String RENIEC_RESERVADO_CHAR10 = "          ";
    public static final String RENIEC_CARACTER_VERIFICACION = " ";
    public static final String RENIEC_TIPO_SERVICIO  = "000";
    public static final String RENIEC_TIPO_CONSULTA  = "7";
    public static final String RENIEC_TIPO_DOCUMENTO = " ";
    public static final String RENIEC_VERSION = "0001";
    public static final String RENIEC_CARACTERES_VERIFICACION = "RENIECPERURENIEC";
    public static final String RENIEC_COD_INST_SOLICITANTE  = "LD2019"; //Antes: LDD019
    public static final String RENIEC_COD_SERVIDOR_RENIEC   = "RENIEC001";
    public static final String RENIEC_HOST_INST_SOLICITANTE = "BBVA";
    public static final String RENIEC_ERROR_PROCESAR_TRAMA_RESPUESTA = "Error al procesar trama de respuesta";
    //public static final String RENIEC_NO_EXISTE_CLIENTE = "La persona no figura en la Reniec";
    public static final String RENIEC_NO_EXISTE_CLIENTE = "";
    public static final String ERROR_SERVICIO_WEB = "No se ha podido realizar la conexi\u00F3n al servicio web del banco para autentificar el usuario.";
}
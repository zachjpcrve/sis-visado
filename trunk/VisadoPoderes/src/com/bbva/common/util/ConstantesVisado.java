package com.bbva.common.util;

public class ConstantesVisado {
	public static final java.lang.String ID_RANGO_IMPORTE_MENOR_CINCUENTA = "0001";
	public static final java.lang.String ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE = "0002";
	public static final java.lang.String ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA = "0003";
	public static final java.lang.String ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA = "0004";
	public static final java.lang.Double VALOR_RANGO_CINCUENTA=50.0;
	public static final java.lang.Double VALOR_RANGO_CIENTO_VEINTE=120.0;
	public static final java.lang.Double VALOR_RANGO_DOSCIENTOS_CINCUENTA=250.0;
	public static final java.lang.String CODIGO_MULTITABLA_IMPORTES = "T07";
	public static final java.lang.String CODIGO_MULTITABLA_ESTADOS = "T02";
	public static final java.lang.String CODIGO_MULTITABLA_ESTADOS_NIVEL = "T09";
	public static final java.lang.String CODIGO_MULTITABLA_TIPOS_FECHA = "T10";
	public static final java.lang.String CODIGO_MULTITABLA_MONEDA = "T08";
	public static final java.lang.String CODIGO_MULTITABLA_TIPO_DOC = "T04";
	public static final java.lang.String TIPO_FECHA_ENVIO="0001";
	public static final java.lang.String TIPO_FECHA_RPTA="0002";
	public static final java.lang.String SIMBOLO_PORCENTAJE="%";
	/*SAMIRA*/
	public static final String COD_GRUPO_ADM= "0000003";	
	public static final String COD_GRUPO_JRD= "0000002";
	public static final String COD_GRUPO_OFI= "0000001";
	public static final String DES_GRUPO_ADM= "SERVICIOS JURIDICOS";	
	public static final String DES_GRUPO_JRD= "ABOGADO";
	public static final String DES_GRUPO_OFI= "OFICINA";
	
	public static final String TOOLTIP_TIPO_FECHA_ENVIO="-Fecha de envio";
	public static final String TOOLTIP_TIPO_FECHA_RPTA="-Fecha de respuesta";
	
	public static final String MSG_TOTAL_SIN_REGISTROS="No se encontraron coincidencias con los criterios ingresados";
	public static final String MSG_TOTAL_REGISTROS="Total de registros: ";
	public static final String MSG_REGISTROS=" registros.";
	public static final String MSG_REGISTRO=" registro.";
	public static final String MSG_TODOS="--Todos--";
	
	public static final String NOM_TBL_OFICINA="tiivsOficina1";
	public static final String ALIAS_TBL_OFICINA="ofic";
	public static final String CAMPO_COD_SOLICITUD="codSoli";
	public static final String CAMPO_COD_OFICINA_ALIAS="ofic.codOfi";
	public static final String CAMPO_NOM_OFICINA_ALIAS="ofic.desOfi";
	public static final String CAMPO_COD_TERR_ALIAS="ofic.codTerr";
	public static final String CAMPO_COD_TERR_NO_ALIAS="codTerr";
	public static final String CAMPO_OPE_BANCARIAS="operacionesBancarias";
	public static final String CAMPO_IMPORTE="importe";
	public static final String CAMPO_PODERDANTE="poderante";
	public static final String CAMPO_APODERADO="apoderado";
	public static final String CAMPO_NUMDOC_PODERDANTE="numDocPoder";
	public static final String CAMPO_NUMDOC_APODERADO="numDocApoder";
	public static final String CAMPO_ESTUDIO="regAbogado";
	public static final String CAMPO_FECHA_ENVIO="fechaEnvio";
	public static final String CAMPO_FECHA_RPTA="fechaRespuesta";
	public static final String CAMPO_COD_OFICINA="codOfi";
	public static final String CAMPO_ESTADO="estado";
	
	public static final String NOM_TBL_TIPO_SERVICIO="tiivsTipoServicio";
	public static final String ALIAS_TBL_TIPO_SERVICIO="tipoSer";
	public static final String CAMPO_COD_OPE_ALIAS="tipoServ.codOper";
	
	public static final String CAMPO_COD_TERRITORIO="codTer";
	public static final String ETIQUETA_COD_CENTRAL="Cod Central: ";
	public static final String CAMPO_ESTADO_ENVIADO="ENVIADOSSJJ";
	public static final String CAMPO_ESTADO_ACEPTADO="ACEPTADO";
	public static final String CAMPO_ESTADO_RECHAZADO="RECHAZADO";
	public static final String CAMPO_ESTADO_VERIFICACION_A="En Verificación A";
}

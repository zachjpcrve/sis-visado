package com.bbva.common.util;


public class ConstantesVisado {
	
	public static final String USUARIO_ID = "usuarioId";
	public static final String USUARIO_NOMBRE = "usuarioNombre";
	public static final String USUARIO_CODIGO = "usuarioCodigo";
	public static final String USUARIO_ESTADO = "usuarioEstado";
	
	public static final java.lang.String APODERADO="0002";
	public static final java.lang.String PODERDANTE="0001";
	
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
	/*eramoz*/
	public static final java.lang.String CODIGO_MULTITABLA_TIPO_PARTI = "T10"; //PODERDANTE|APODERADO	
	public static final java.lang.String CODIGO_MULTITABLA_COMISION = "T11"; //COMISION	
	public static final java.lang.String CODIGO_CAMPO_PODERDANTE = "0001"; //PODERDANTE
	public static final java.lang.String CODIGO_CAMPO_COMISION_X = "0005"; //COMISION
	public static final java.lang.String CODIGO_CAMPO_PERSONA_NATURAL = "0001"; //COMISION
	public static final java.lang.String CODIGO_CAMPO_PERSONA_JURIDICA = "0002"; //COMISION
	public static final java.lang.String CODIGO_CAMPO_FALLECIDO_MAYORA = "0003"; //COMISION
	public static final java.lang.String CODIGO_CAMPO_FALLECIDO_MENORA = "0004"; //COMISION
	public static final java.lang.String CODIGO_CAMPO_TIPODOI_RUC = "0005"; //COMISION
	public static final java.lang.String TIPO_DOCUMENTO_RUC = "RUC"; //COMISION
	public static final java.lang.String TIPO_CLASIFICACION_FALLECIDO="Fallecido";//COMISION
	public static final java.lang.String VALOR_TIPO_DOCUMENTO_OTROS="Otros";
	public static final java.lang.String PREFIJO_OTROS="O";
	public static final java.lang.String FILES="files";
	public static final java.lang.String CODIGO_CAMPO_TIPO_ROL_RESPONSABLE="R";
	public static final java.lang.String CODIGO_CAMPO_TIPO_ROL_DELEGADO="D";
	
	/*clarosa*/
	public static final java.lang.String CODIGO_MULTITABLA_DIAS_UTILES = "T14";
	
	/*SAMIRA*/
	public static final java.lang.String CODIGO_MULTITABLA_CLASIFICACION_PERSONA = "T13";
	public static final java.lang.String CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA = "T12";
	/* FIN SAMIRA*/
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
	
	public static final String OFICINA= "OFICINA";
	public static final String ABOGADO= "ABOGADO";
	public static final String SSJJ= "SSJJ";
	
    		 
	/* FIN SAMIRA*/
	public static final String TOOLTIP_TIPO_FECHA_ENVIO="-Fecha de envio";
	public static final String TOOLTIP_TIPO_FECHA_RPTA="-Fecha de respuesta";
	
	public static final String MSG_TOTAL_SIN_REGISTROS="No se encontraron coincidencias con los criterios ingresados";
	public static final String MSG_TOTAL_REGISTROS="Total de registros: ";
	public static final String MSG_REGISTROS=" registros.";
	public static final String MSG_REGISTRO=" registro.";
	public static final String MSG_TODOS="--Todos--";
	
	public static final String NOM_TBL_OFICINA="tiivsOficina1";
	public static final String NOM_TBL_ESTUDIO="tiivsEstudio";
	public static final String ALIAS_TBL_OFICINA="ofic";
	public static final String ALIAS_TBL_OFICINA_FILTRO="ofic1";
	public static final String ALIAS_TBL_ESTUDIO="estu";
	public static final String ALIAS_COD_ESTUDIO="estu.codEstudio";
	public static final String CAMPO_COD_SOLICITUD="codSoli";
	public static final String CAMPO_COD_OFICINA_ALIAS="ofic.codOfi";
	public static final String CAMPO_COD_OFICINA_ALIAS_FILTRO="ofic1.codOfi";
	public static final String CAMPO_NOM_OFICINA_ALIAS="ofic.desOfi";
	public static final String CAMPO_COD_ESTUDIO_ALIAS="estu.codEstudio";
	public static final String CAMPO_NOM_OFICINA = "desOfi";
	public static final String CAMPO_COD_TERR_ALIAS="ofic.codTerr";
	public static final String CAMPO_COD_TERR_NO_ALIAS="codTerr";
	public static final String CAMPO_OPE_BANCARIAS="operacionesBancarias";
	public static final String CAMPO_IMPORTE="importe";
	public static final String CAMPO_PODERDANTE="poderante";
	public static final String CAMPO_APODERADO="apoderado";
	public static final String CAMPO_NUMDOC_PODERDANTE="numDocPoder";
	public static final String CAMPO_NUMDOC_APODERADO="numDocApoder";
	public static final String CAMPO_ESTUDIO="cod_estudio";
	public static final String CAMPO_FECHA_ENVIO="fechaEnvio";
	public static final String CAMPO_FECHA_RPTA="fechaRespuesta";
	public static final String CAMPO_COD_OFICINA="codOfi";
	public static final String CAMPO_ESTADO="estado";
	public static final String CAMPO_COD_MIEMBRO="codMiembro";
	public static final String CAMPO_ALIAS_ANEXO_ARCHIVO = "aliasArchivo";
	
	/*eramoz*/
	public static final String CAMPO_ID_CODIGO_SOLICITUD_ALIAS="id.codSoli";
	public static final String CAMPO_ID_TIPO_PARTIC_ALIAS="id.tipPartic";
	public static final String CAMPO_ID_ALIAS="id";
	
	public static final String NOM_TBL_TIPO_SOLICITUD="tiivsTipoSolicitud";
	public static final String ALIAS_TBL_TIPO_SOLICITUD="tipoSol";
	public static final String CAMPO_COD_TIPO_SOL_ALIAS="tipoSol.codTipSolic";
	
	public static final String CAMPO_COD_TERRITORIO="codTer";
	public static final String ETIQUETA_COD_CENTRAL="Cod Central: ";
	public static final String CAMPO_ESTADO_ENVIADO="ENVIADOSSJJ";
	public static final String CAMPO_ESTADO_ACEPTADO="ACEPTADO";
	public static final String CAMPO_ESTADO_RECHAZADO="RECHAZADO";
	public static final String CAMPO_ESTADO_VERIFICACION_A="En Verificación A";
		
	//ASIG AUTOMATICA
	public static final java.lang.String CODIGO_ESTADO_ENVIADO="0007";
	public static final java.lang.String CODIGO_TABLA_HORA_CORTE="T06";
	public static final java.lang.String CODIGO_CAMPO_HORA_CORTE="0002";
	public static final java.lang.String CODIGO_GRUPO_ABOGADOS="0000002";
	
	public static final String CAMPO_ESTADO_VERIFICACION_B="En Verificación B";
	public static final String CAMPO_ESTADO_EN_REVISION="En Revisión";
	public static final String CAMPO_ESTADO_REVOCADO="Revocado";
	
	public static final String CAMPO_RANGO_INICIO = "rangoInicio";
	public static final String CAMPO_RANGO_FIN = "rangoFin";
	public static final String CAMPO_MONEDA= "moneda";
	public static final String CAMPO_SOLES_TBL_MONEDA="PEN NUEVO SOL PERUANO";
	public static final String CAMPO_DOLARES_TBL_MONEDA="USD  D¿AR USA";
	public static final String CAMPO_EUROS_TBL_MONEDA ="EUR EURO";
	public static final String CAMPO_SOLES = "soles";
	public static final String CAMPO_ABREV_SOLES = "PEN";
	public static final String CAMPO_DOLARES = "dolares";
	public static final String CAMPO_ABREV_DOLARES = "USD";
	public static final String CAMPO_EUROS = "euros";
	public static final String CAMPO_ABREV_EUROS = "EUR";
	public static final String CAMPO_NIVEL1 = "N1";
	public static final String CAMPO_NIVEL2 = "N2";
	public static final String CAMPO_NIVEL3 = "N3";
	public static final String CAMPO_NIVEL4 = "N4";
	
	public static final String DOS_PUNTOS = ": ";
	public static final String GUION = " - ";
	public static final String ARROBA = "@";
	public static final String SLASH = "/";
	public static final String TRES_CEROS = "000";
	public static final String DOS_CEROS = "00";
	public static final String CERO = "0";
	public static final String PROTOCOLO_FTP="ftp";
	public static final String SALTO_LINEA = "\n";
	public static final String EXTENSION_XLS = ".xls";
	public static final String ESPACIO_BLANCO =" ";
	public static final String UNDERLINE = "_";
	
	//Constantes para archivo Excel
	public static final String TITULO_CABECERA_EXCEL ="CONSULTA DE SOLICITUDES DE VISADO";
	public static final String ETIQUETA_FILTRO_BUS_NRO_SOL="Nro. Solicitud";
	public static final String ETIQUETA_FILTRO_BUS_TIPO_FECHA="Tipo de Fecha";
	public static final String ETIQUETA_FILTRO_BUS_COD_OFICINA="Código de Oficina";
	public static final String ETIQUETA_FILTRO_BUS_DOI_APODERADO="DOI Apoderado";
	public static final String ETIQUETA_FILTRO_BUS_REVISION="En Revision";
	public static final String ETIQUETA_FILTRO_BUS_NIVEL="Nivel";
	public static final String ETIQUETA_FILTRO_BUS_ESTADO_NIVEL="Estados de Nivel";
	public static final String ETIQUETA_FILTRO_BUS_ESTADO="Estado";
	public static final String ETIQUETA_FILTRO_BUS_FECHA_INICIO ="Fecha de Inicio";
	public static final String ETIQUETA_FILTRO_BUS_OFICINA="Oficina";
	public static final String ETIQUETA_FILTRO_BUS_APODERADO="Apoderado";
	public static final String ETIQUETA_FILTRO_BUS_DELEGADO="Delegado";
	public static final String ETIQUETA_FILTRO_BUS_IMPORTE="Importe";
	public static final String ETIQUETA_FILTRO_BUS_FECHA_FIN="Fecha de Fin";
	public static final String ETIQUETA_FILTRO_BUS_COMB_COD_OFICINA="Código de Oficina";
	public static final String ETIQUETA_FILTRO_BUS_DOI_PODERDANTE="DOI Poderdante";
	public static final String ETIQUETA_FILTRO_BUS_REVOCATORIA="Revocatoria";
	public static final String ETIQUETA_FILTRO_BUS_TIPO_SOL="Tipo de solicitud";
	public static final String ETIQUETA_FILTRO_BUS_TIPO_OPE="Tipo de operación";
	public static final String ETIQUETA_FILTRO_BUS_COMB_OFICINA="Oficina";
	public static final String ETIQUETA_FILTRO_BUS_PODERDANTE="Poderdante";
	public static final String ETIQUETA_FILTRO_BUS_ESTUDIO="Estudio";
	public static final String ETIQUETA_FILTRO_BUS_GENERADOR="Generador:";
	public static final String ETIQUETA_FILTRO_BUS_FECHA_HORA = "Fecha y Hora:";
	public static final String ETIQUETA_COLUMNA_ITEM = "Item";
	public static final String ETIQUETA_COLUMNA_NRO_SOLICITUD = "Nro Solicitud";
	public static final String ETIQUETA_COLUMNA_COD_OFICINA = "Cod Oficina";
	public static final String ETIQUETA_COLUMNA_OFICINA = "Oficina";
	public static final String ETIQUETA_COLUMNA_TERRITORIO = "Territorio";
	public static final String ETIQUETA_COLUMNA_ESTADO = "Estado";
	public static final String ETIQUETA_COLUMNA_IMPORTE = "Importe";
	public static final String ETIQUETA_COLUMNA_PODERDANTE = "Poderdante";
	public static final String ETIQUETA_COLUMNA_APODERADO = "Apoderado";
	public static final String ETIQUETA_COLUMNA_TIPO_SOL = "Tipo de Solicitud";
	public static final String ETIQUETA_COLUMNA_TIPO_OPE = "Tipo de Operacion";
	public static final String ETIQUETA_COLUMNA_ESTUDIO = "Estudio";
	public static final String ETIQUETA_COLUMNA_NIVEL = "Nivel";
	public static final String ETIQUETA_COLUMNA_FECHA_ENVIO = "Fecha de Envio";
	public static final String ETIQUETA_COLUMNA_FECHA_RPTA = "Fecha de Respuesta";
	public static final String ETIQUETA_COLUMNA_FECHA_ESTADO = "Fecha de Estado";
	public static final String ETIQUETA_COLUMNA_COMISION = "Comision";
	public static final String ETIQUETA_COLUMNA_LIBERADO = "Liberado";
	public static final String ETIQUETA_COLUMNA_DELEGADO = "Delegado";
	public static final String ETIQUETA_COLUMNA_EN_REVISION = "En revision";
	public static final String ETIQUETA_COLUMNA_REVOCATORIA = "Revocatoria";
	
	static public class ESTADOS {
		
		
		public static final String ESTADO_ACTIVO_REVOCADO="0018";
		public static final String ESTADO_INACTIVO_REVOCADO="0019";
		public static final String ESTADO_PENDIENTE_REVOCADO="0020";
		
		public static final String ESTADO_COD_Pendiente_T09="0001";
		public static final String ESTADO_COD_Aprobado_T09="0002";
		public static final String ESTADO_COD_Desaprobado_T09="0003";
		
		
		public static final String ESTADO_Pendiente_T09="Pendiente";
		public static final String ESTADO_Aprobado_T09="Aprobado";
		public static final String ESTADO_Desaprobado_T09="Desaprobado";
		
		
		public static final String ESTADO_COD_ACTIVO="1";
		public static final String ESTADO_COD_DESACTIVO="0";
		
		public static final String ESTADO_COD_REGISTRADO_T02="0001";					
		public static final String ESTADO_COD_RESERVADO_T02="0002";					
		public static final String ESTADO_COD_ACEPTADO_T02="0003";				
		public static final String ESTADO_COD_RECHAZADO_T02="0004";				
		public static final String ESTADO_COD_ENVIADOSSJJ_T02="0007";					
		public static final String ESTADO_COD_VENCIDO_T02="0008";					
		public static final String ESTADO_COD_EN_VERIFICACION_A_T02="0009";					
		public static final String ESTADO_COD_EN_VERIFICACION_B_T02="0010";						
		public static final String ESTADO_COD_PROCEDENTE_T02="0012";						
		public static final String ESTADO_COD_IMPROCEDENTE_T02="0013";						
		public static final String ESTADO_COD_PRE_EJECUTADO_T02="0014";						
		public static final String ESTADO_COD_EJECUTADO_T02="0015";					
		public static final String ESTADO_COD_REVOCADO_T02="0016";						
		public static final String ESTADO_COD_EN_REVISION_T02="0017";	
		
		public static final String ESTADO_COD_APROBADO_T09="0002";
		public static final String ESTADO_COD_DESAPROBADO_T09="0003";
		
		
		
		public static final String ESTADO_ACTIVO="Activo";
		public static final String ESTADO_DESACTIVO="Desactivo";
		
		public static final String ESTADO_REGISTRADO_T02="Registrado";					
		public static final String ESTADO_RESERVADO_T02="Reservado";					
		public static final String ESTADO_ACEPTADO_T02="Aceptado";				
		public static final String ESTADO_RECHAZADO_T02="Rechazado";				
		public static final String ESTADO_ENVIADOSSJJ_T02="Enviado a SSJJ";					
		public static final String ESTADO_VENCIDO_T02="Vencido";					
		public static final String ESTADO_EN_VERIFICACION_A_T02="En Verificación A";					
		public static final String ESTADO_EN_VERIFICACION_B_T02="En Verificación B";						
		public static final String ESTADO_PROCEDENTE_T02="Procedente";						
		public static final String ESTADO_IMPROCEDENTE_T02="Improcedente";						
		public static final String ESTADO_PRE_EJECUTADO_T02="Pre Ejecutado";						
		public static final String ESTADO_EJECUTADO_T02="Ejecutado";					
		public static final String ESTADO_REVOCADO_T02="Revocado";						
		public static final String ESTADO_EN_REVISION_T02="En revisión";	
		
	}
	static public class MONEDAS {
		public static final String COD_SOLES = "0001";
		public static final String COD_DOLAR = "0002";
		public static final String COD_EUROS = "0003";
		public static final String SOLES = "SOLES";
		public static final String DOLARES = "DOLARES";
		public static final String EUROS = "EUROS";
		public static final String PREFIJO_SOLES = "PEN S/.";
		public static final String PREFIJO_DOLAR = "USD $/.";
		public static final String PREFIJO_EURO = "EUR /.";
	}
	
		public static final int DOCUMENTO_OBLIGATORIO = 1;
	
	static public class MENSAJE_CARTA_ATENCION {
		public static final String MENSAJE_OFICINA="¿Está seguro de generar la Carta de atención? Luego de la generación no podrá volverse a imprimir.";
		public static final String MENSA_SSJJ="¿Esta seguro de generar la Carta de Atencion?";
	}
	
	static public class MENSAJE{
		public static final String OCURRE_EXCEPCION = "Ha ocurrido una excepcion ";
		public static final String OCURRE_ERROR = "Ha ocurrido un error ";		
		public static final String REGISTRO_OK = "Se ha registrado correctamente ";		
		public static final String NO_RESULTADOS = "No se han encontrado resultados ";
		public static final String TAMANHIO_LISTA = "El tamaño de la lista ";
		
	}
}


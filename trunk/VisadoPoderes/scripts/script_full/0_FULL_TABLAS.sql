--------------------------------------------------------
-- Archivo creado  - viernes-enero-24-2014   
--------------------------------------------------------
  DROP TABLE "VISPOD"."LDAPPERU2" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_BLOB_TRIGGERS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_CALENDARS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_CRON_TRIGGERS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_FIRED_TRIGGERS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_JOB_DETAILS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_JOB_LISTENERS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_LOCKS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_PAUSED_TRIGGER_GRPS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_SCHEDULER_STATE" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_SIMPLE_TRIGGERS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_TRIGGER_LISTENERS" cascade constraints;
  DROP TABLE "VISPOD"."QRTZ_TRIGGERS" cascade constraints;
  DROP TABLE "VISPOD"."TEMP_TCDT051" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_AGRUPACION_PERSONA" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_ANEXO_SOLICITUD" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_DOCUMENTO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_ESTUDIO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_FERIADO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_GRUPO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_MIEMBRO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_MIEMBRO_NIVEL" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_MOVIMIENTO_NIVEL" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_MULTITABLA" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_NIVEL" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_OFICINA1" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_OPERACION_BANCARIA" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_PERSONA" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_REVOCADO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_SOLICITUD" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_SOLICITUD_NIVEL" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_SOLICITUD_OPERBAN" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_TAREA" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_TAREA_GRUPO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_TERRITORIO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_TIEMPO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" cascade constraints;
  DROP TABLE "VISPOD"."TIIVS_TIPO_SOLICITUD" cascade constraints;
  DROP TABLE "VISPOD"."TTIVS_HOST_VOUCHER" cascade constraints;
  DROP TABLE "VISPOD"."TTIVS_PARAMETROS" cascade constraints;
  DROP SEQUENCE "VISPOD"."ID_AGRUPACION_SEQ";
  DROP SEQUENCE "VISPOD"."SEQ_ESTUDIO";
  DROP SEQUENCE "VISPOD"."SEQ_FERIADO";
  DROP SEQUENCE "VISPOD"."SEQ_MIEMBRO_NIVEL";
  DROP SEQUENCE "VISPOD"."SEQ_MOVIMIENTO_NIVEL";
  DROP SEQUENCE "VISPOD"."SEQ_OPER_BAN";
  DROP SEQUENCE "VISPOD"."SEQ_PER";
  DROP SEQUENCE "VISPOD"."SEQ_REV";
  DROP SEQUENCE "VISPOD"."SEQ_SOL";
  DROP SEQUENCE "VISPOD"."SEQ_SOLICITUD";
  DROP SEQUENCE "VISPOD"."SEQ_SOL_NIVEL";
  DROP SEQUENCE "VISPOD"."SEQ_TIPO_DOC";
  DROP SEQUENCE "VISPOD"."SEQ_TIPO_OPER";
--------------------------------------------------------
--  DDL for Sequence ID_AGRUPACION_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."ID_AGRUPACION_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 3412 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_ESTUDIO
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_ESTUDIO"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 201 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_FERIADO
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_FERIADO"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 341 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_MIEMBRO_NIVEL
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_MIEMBRO_NIVEL"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 640 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_MOVIMIENTO_NIVEL
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_MOVIMIENTO_NIVEL"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 401 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_OPER_BAN
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_OPER_BAN"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 241 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PER
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_PER"  MINVALUE 1 MAXVALUE 99999999 INCREMENT BY 1 START WITH 1044 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_REV
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_REV"  MINVALUE 1 MAXVALUE 99999999 INCREMENT BY 1 START WITH 1121 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_SOL
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_SOL"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 25 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_SOLICITUD
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_SOLICITUD"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 ORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_SOL_NIVEL
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_SOL_NIVEL"  MINVALUE 1 MAXVALUE 99999999 INCREMENT BY 1 START WITH 913 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_TIPO_DOC
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_TIPO_DOC"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 209 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_TIPO_OPER
--------------------------------------------------------

   CREATE SEQUENCE  "VISPOD"."SEQ_TIPO_OPER"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 84 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Table LDAPPERU2
--------------------------------------------------------

  CREATE TABLE "VISPOD"."LDAPPERU2" 
   (	"REGISTROU" VARCHAR2(7 BYTE), 
	"REGISTROP" VARCHAR2(7 BYTE), 
	"CODPAIS" VARCHAR2(4 BYTE), 
	"DESPAIS" VARCHAR2(45 BYTE), 
	"CODEMP" VARCHAR2(4 BYTE), 
	"DESEMP" VARCHAR2(45 BYTE), 
	"NUMMAT" VARCHAR2(7 BYTE), 
	"CODUSU" VARCHAR2(7 BYTE), 
	"NOMBRE" VARCHAR2(25 BYTE), 
	"APEPAT" VARCHAR2(25 BYTE), 
	"APEMAT" VARCHAR2(25 BYTE), 
	"SEXO" VARCHAR2(1 BYTE), 
	"FECNAC" VARCHAR2(10 BYTE), 
	"TIPDOC" VARCHAR2(1 BYTE), 
	"NUMDOC" VARCHAR2(9 BYTE), 
	"TELEFONO1" VARCHAR2(9 BYTE), 
	"TELEFONO2" VARCHAR2(9 BYTE), 
	"CORELEC" VARCHAR2(45 BYTE), 
	"CODCARGO" VARCHAR2(4 BYTE), 
	"NOMCARGO" VARCHAR2(45 BYTE), 
	"CODEMPANT" VARCHAR2(4 BYTE), 
	"CODOFI" VARCHAR2(4 BYTE), 
	"DESOFI" VARCHAR2(45 BYTE), 
	"TIPOFI" VARCHAR2(1 BYTE), 
	"GESPER" VARCHAR2(7 BYTE), 
	"FECING" VARCHAR2(10 BYTE), 
	"NROHIJOS" NUMBER(2,0), 
	"CODGER" VARCHAR2(8 BYTE), 
	"NOMGER" VARCHAR2(18 BYTE), 
	"TELOFI1" VARCHAR2(18 BYTE), 
	"TELOFI2" VARCHAR2(18 BYTE), 
	"CARBLAN" VARCHAR2(15 BYTE), 
	"PROINF" VARCHAR2(1 BYTE), 
	"CODUSUJEFE" VARCHAR2(7 BYTE), 
	"CODUNIORG" VARCHAR2(8 BYTE), 
	"DESUNIORG" VARCHAR2(45 BYTE), 
	"CODNIVEL1" VARCHAR2(8 BYTE), 
	"DESNIVEL1" VARCHAR2(45 BYTE), 
	"CODNIVEL2" VARCHAR2(8 BYTE), 
	"DESNIVEL2" VARCHAR2(45 BYTE), 
	"CODNIVEL3" VARCHAR2(8 BYTE), 
	"DESNIVEL3" VARCHAR2(45 BYTE), 
	"CODNIVEL4" VARCHAR2(8 BYTE), 
	"DESNIVEL4" VARCHAR2(45 BYTE), 
	"CODNIVEL5" VARCHAR2(8 BYTE), 
	"DESNIVEL5" VARCHAR2(45 BYTE), 
	"CODNIVEL6" VARCHAR2(8 BYTE), 
	"DESNIVEL6" VARCHAR2(45 BYTE), 
	"CODNIVEL7" VARCHAR2(8 BYTE), 
	"DESNIVEL7" VARCHAR2(45 BYTE), 
	"CODNIVEL8" VARCHAR2(8 BYTE), 
	"DESNIVEL8" VARCHAR2(45 BYTE), 
	"CODNIVEL9" VARCHAR2(8 BYTE), 
	"DESNIVEL9" VARCHAR2(45 BYTE), 
	"CODNIVEL10" VARCHAR2(8 BYTE), 
	"DESNIVEL10" VARCHAR2(45 BYTE), 
	"USOFUT1" VARCHAR2(30 BYTE), 
	"USOFUT2" VARCHAR2(30 BYTE), 
	"USOFUT3" VARCHAR2(30 BYTE), 
	"USOFUT4" VARCHAR2(30 BYTE), 
	"USOFUT5" VARCHAR2(30 BYTE), 
	"USOFUT6" VARCHAR2(30 BYTE), 
	"USOFUT7" VARCHAR2(30 BYTE), 
	"USOFUT8" VARCHAR2(30 BYTE), 
	"USOFUT9" VARCHAR2(30 BYTE), 
	"USOFUT10" VARCHAR2(30 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_BLOB_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_BLOB_TRIGGERS" 
   (	"TRIGGER_NAME" VARCHAR2(80 BYTE), 
	"TRIGGER_GROUP" VARCHAR2(80 BYTE), 
	"BLOB_DATA" BLOB
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_CALENDARS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_CALENDARS" 
   (	"CALENDAR_NAME" VARCHAR2(80 BYTE), 
	"CALENDAR" BLOB
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_CRON_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_CRON_TRIGGERS" 
   (	"TRIGGER_NAME" VARCHAR2(80 BYTE), 
	"TRIGGER_GROUP" VARCHAR2(80 BYTE), 
	"CRON_EXPRESSION" VARCHAR2(80 BYTE), 
	"TIME_ZONE_ID" VARCHAR2(80 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_FIRED_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_FIRED_TRIGGERS" 
   (	"ENTRY_ID" VARCHAR2(95 BYTE), 
	"TRIGGER_NAME" VARCHAR2(80 BYTE), 
	"TRIGGER_GROUP" VARCHAR2(80 BYTE), 
	"IS_VOLATILE" VARCHAR2(1 BYTE), 
	"INSTANCE_NAME" VARCHAR2(80 BYTE), 
	"FIRED_TIME" NUMBER(13,0), 
	"PRIORITY" NUMBER(13,0), 
	"STATE" VARCHAR2(16 BYTE), 
	"JOB_NAME" VARCHAR2(80 BYTE), 
	"JOB_GROUP" VARCHAR2(80 BYTE), 
	"IS_STATEFUL" VARCHAR2(1 BYTE), 
	"REQUESTS_RECOVERY" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_JOB_DETAILS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_JOB_DETAILS" 
   (	"JOB_NAME" VARCHAR2(80 BYTE), 
	"JOB_GROUP" VARCHAR2(80 BYTE), 
	"DESCRIPTION" VARCHAR2(120 BYTE), 
	"JOB_CLASS_NAME" VARCHAR2(128 BYTE), 
	"IS_DURABLE" VARCHAR2(1 BYTE), 
	"IS_VOLATILE" VARCHAR2(1 BYTE), 
	"IS_STATEFUL" VARCHAR2(1 BYTE), 
	"REQUESTS_RECOVERY" VARCHAR2(1 BYTE), 
	"JOB_DATA" BLOB
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_JOB_LISTENERS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_JOB_LISTENERS" 
   (	"JOB_NAME" VARCHAR2(80 BYTE), 
	"JOB_GROUP" VARCHAR2(80 BYTE), 
	"JOB_LISTENER" VARCHAR2(80 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_LOCKS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_LOCKS" 
   (	"LOCK_NAME" VARCHAR2(40 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_PAUSED_TRIGGER_GRPS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_PAUSED_TRIGGER_GRPS" 
   (	"TRIGGER_GROUP" VARCHAR2(80 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_SCHEDULER_STATE
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_SCHEDULER_STATE" 
   (	"INSTANCE_NAME" VARCHAR2(80 BYTE), 
	"LAST_CHECKIN_TIME" NUMBER(13,0), 
	"CHECKIN_INTERVAL" NUMBER(13,0)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_SIMPLE_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_SIMPLE_TRIGGERS" 
   (	"TRIGGER_NAME" VARCHAR2(80 BYTE), 
	"TRIGGER_GROUP" VARCHAR2(80 BYTE), 
	"REPEAT_COUNT" NUMBER(7,0), 
	"REPEAT_INTERVAL" NUMBER(12,0), 
	"TIMES_TRIGGERED" NUMBER(7,0)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_TRIGGER_LISTENERS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_TRIGGER_LISTENERS" 
   (	"TRIGGER_NAME" VARCHAR2(80 BYTE), 
	"TRIGGER_GROUP" VARCHAR2(80 BYTE), 
	"TRIGGER_LISTENER" VARCHAR2(80 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table QRTZ_TRIGGERS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."QRTZ_TRIGGERS" 
   (	"TRIGGER_NAME" VARCHAR2(80 BYTE), 
	"TRIGGER_GROUP" VARCHAR2(80 BYTE), 
	"JOB_NAME" VARCHAR2(80 BYTE), 
	"JOB_GROUP" VARCHAR2(80 BYTE), 
	"IS_VOLATILE" VARCHAR2(1 BYTE), 
	"DESCRIPTION" VARCHAR2(120 BYTE), 
	"NEXT_FIRE_TIME" NUMBER(13,0), 
	"PREV_FIRE_TIME" NUMBER(13,0), 
	"PRIORITY" NUMBER(13,0), 
	"TRIGGER_STATE" VARCHAR2(16 BYTE), 
	"TRIGGER_TYPE" VARCHAR2(8 BYTE), 
	"START_TIME" NUMBER(13,0), 
	"END_TIME" NUMBER(13,0), 
	"CALENDAR_NAME" VARCHAR2(80 BYTE), 
	"MISFIRE_INSTR" NUMBER(2,0), 
	"JOB_DATA" BLOB
   ) ;
--------------------------------------------------------
--  DDL for Table TEMP_TCDT051
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TEMP_TCDT051" 
   (	"TCCENTIT" VARCHAR2(20 BYTE), 
	"TCCCEGEN" VARCHAR2(20 BYTE), 
	"TCCAGRUP" VARCHAR2(20 BYTE), 
	"TCCCEASS" VARCHAR2(20 BYTE), 
	"TCCNIVEL" VARCHAR2(20 BYTE), 
	"TCCCESUP" VARCHAR2(20 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_AGRUPACION_PERSONA
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_AGRUPACION_PERSONA" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"NUM_GRUPO" NUMBER(8,0), 
	"COD_PER" NUMBER(8,0), 
	"TIP_PARTIC" VARCHAR2(4 BYTE), 
	"CLASIF_PER" VARCHAR2(4 BYTE), 
	"ID_AGRUPACION" NUMBER(8,0)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_ANEXO_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_ANEXO_SOLICITUD" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"ALIAS_ARCHIVO" VARCHAR2(50 BYTE), 
	"COD_DOC" VARCHAR2(7 BYTE), 
	"ALIAS_TEMPORAL" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_DOCUMENTO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_DOCUMENTO" 
   (	"COD_DOCUMENTO" VARCHAR2(7 BYTE), 
	"DESCRIPCION" VARCHAR2(200 BYTE), 
	"ACTIVO" VARCHAR2(1 BYTE), 
	"NOMBRE" VARCHAR2(7 BYTE), 
	"FORMATO" VARCHAR2(20 BYTE), 
	"COD_BARRA" VARCHAR2(20 BYTE)
   ) ;

   COMMENT ON COLUMN "VISPOD"."TIIVS_DOCUMENTO"."NOMBRE" IS 'Nombre corto, campo que nos indica la descripción de los documentos a digitalizar';
   COMMENT ON COLUMN "VISPOD"."TIIVS_DOCUMENTO"."FORMATO" IS 'campo que nos indica el formato de los documentos a digitalizar';
--------------------------------------------------------
--  DDL for Table TIIVS_ESTUDIO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_ESTUDIO" 
   (	"COD_ESTUDIO" VARCHAR2(7 BYTE), 
	"DES_ESTUDIO" VARCHAR2(50 BYTE), 
	"ACTIVO" CHAR(1 BYTE), 
	"COSTO" NUMBER(8,2)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_FERIADO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_FERIADO" 
   (	"ID_FERIADO" NUMBER(8,0), 
	"FECHA" DATE, 
	"INDICADOR" CHAR(1 BYTE), 
	"COD_DIST" VARCHAR2(7 BYTE), 
	"ESTADO" CHAR(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_GRUPO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_GRUPO" 
   (	"COD_GRUPO" VARCHAR2(7 BYTE), 
	"DES_GRUPO" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_HIST_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" 
   (	"MOVIMIENTO" VARCHAR2(3 BYTE), 
	"FECHA" TIMESTAMP (6), 
	"REG_USUARIO" VARCHAR2(7 BYTE), 
	"NOM_USUARIO" VARCHAR2(75 BYTE), 
	"COD_SOLI" VARCHAR2(7 BYTE), 
	"OBS" VARCHAR2(4000 BYTE), 
	"REG_ABOGADO" VARCHAR2(7 BYTE), 
	"ESTADO" CHAR(4 BYTE), 
	"NIVEL" VARCHAR2(4 BYTE), 
	"NIVEL_ESTADO" VARCHAR2(4 BYTE), 
	"NIVEL_ROL" VARCHAR2(4 BYTE), 
	"FLAG_REVOCADO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_MIEMBRO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_MIEMBRO" 
   (	"COD_MIEMBRO" VARCHAR2(7 BYTE), 
	"DESCRIPCION" VARCHAR2(100 BYTE), 
	"CRITERIO" VARCHAR2(7 BYTE), 
	"COD_GRUPO" VARCHAR2(7 BYTE), 
	"ESTUDIO" VARCHAR2(7 BYTE), 
	"ACTIVO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_MIEMBRO_NIVEL
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_MIEMBRO_NIVEL" 
   (	"ID" NUMBER(8,0), 
	"COD_MIEMBRO" VARCHAR2(7 BYTE), 
	"COD_NIV" VARCHAR2(4 BYTE), 
	"GRUPO" NUMBER(8,0), 
	"TIPO_ROL" VARCHAR2(1 BYTE), 
	"ESTADO" VARCHAR2(4 BYTE), 
	"USUARIO_REGISTRO" VARCHAR2(7 BYTE), 
	"FECHA_REGISTRO" TIMESTAMP (6), 
	"USUARIO_ACT" VARCHAR2(7 BYTE), 
	"FECHA_ACT" TIMESTAMP (6), 
	"ESTADO_MIEMBRO" VARCHAR2(1 BYTE)
   ) ;

   COMMENT ON COLUMN "VISPOD"."TIIVS_MIEMBRO_NIVEL"."GRUPO" IS 'Nro grupo / nro combinacion';
   COMMENT ON COLUMN "VISPOD"."TIIVS_MIEMBRO_NIVEL"."TIPO_ROL" IS 'Delegado (D) / Responsable (R)';
   COMMENT ON COLUMN "VISPOD"."TIIVS_MIEMBRO_NIVEL"."ESTADO" IS '"activo", "inactivo", "pendiente"';
--------------------------------------------------------
--  DDL for Table TIIVS_MOVIMIENTO_NIVEL
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_MOVIMIENTO_NIVEL" 
   (	"ID_MOVIMIENTO" NUMBER(8,0), 
	"ESTADO" VARCHAR2(4 BYTE), 
	"GRUPO" NUMBER(8,0), 
	"USUARIO_REGISTRO" VARCHAR2(7 BYTE), 
	"FECHA_REGISTRO" TIMESTAMP (6), 
	"ID_SOL_NIVEL" NUMBER
   ) ;

   COMMENT ON COLUMN "VISPOD"."TIIVS_MOVIMIENTO_NIVEL"."USUARIO_REGISTRO" IS 'Se registra el codigo de usuario del delegado';
--------------------------------------------------------
--  DDL for Table TIIVS_MULTITABLA
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_MULTITABLA" 
   (	"COD_MULT" VARCHAR2(3 BYTE), 
	"COD_ELEM" VARCHAR2(4 BYTE), 
	"VALOR1" VARCHAR2(100 BYTE), 
	"VALOR2" VARCHAR2(100 BYTE), 
	"VALOR3" VARCHAR2(100 BYTE), 
	"VALOR4" VARCHAR2(100 BYTE), 
	"VALOR5" VARCHAR2(100 BYTE), 
	"VALOR6" VARCHAR2(100 BYTE), 
	"VALOR7" VARCHAR2(100 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_NIVEL
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_NIVEL" 
   (	"ID" NUMBER(8,0), 
	"COD_NIV" VARCHAR2(4 BYTE), 
	"DES_NIV" VARCHAR2(45 BYTE), 
	"RANGO_INICIO" NUMBER(8,0), 
	"RANGO_FIN" NUMBER(8,0), 
	"MONEDA" VARCHAR2(100 BYTE), 
	"FECHA_REG" DATE, 
	"USUARIO_REG" VARCHAR2(50 BYTE), 
	"FECHA_ACT" DATE, 
	"USUARIO_ACT" VARCHAR2(50 BYTE), 
	"ESTADO" NUMBER(*,0)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_OFICINA1
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_OFICINA1" 
   (	"COD_OFI" VARCHAR2(8 BYTE), 
	"DES_OFI" VARCHAR2(45 BYTE), 
	"ACTIVO" CHAR(1 BYTE), 
	"COD_TERR" VARCHAR2(5 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_OPERACION_BANCARIA
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_OPERACION_BANCARIA" 
   (	"COD_OPER_BAN" VARCHAR2(7 BYTE), 
	"DES_OPER_BAN" VARCHAR2(50 BYTE), 
	"ACTIVO" CHAR(1 BYTE), 
	"TIPO" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_PERSONA
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_PERSONA" 
   (	"COD_PER" NUMBER(8,0), 
	"COD_CEN" VARCHAR2(8 BYTE), 
	"TIP_DOI" VARCHAR2(4 BYTE), 
	"NUM_DOI" VARCHAR2(13 BYTE), 
	"NOMBRE" VARCHAR2(50 BYTE), 
	"APE_PAT" VARCHAR2(500 BYTE), 
	"APE_MAT" VARCHAR2(50 BYTE), 
	"NUM_CEL" VARCHAR2(11 BYTE), 
	"EMAIL" VARCHAR2(50 BYTE), 
	"FECHA_REGISTRO" TIMESTAMP (6), 
	"USUARIO_REGISTRO" VARCHAR2(7 BYTE)
   ) ;

   COMMENT ON COLUMN "VISPOD"."TIIVS_PERSONA"."USUARIO_REGISTRO" IS 'Se registra: 
- El código identificador de la fuente de datos(Base de datos de otros sistemas, Reniec, Sunarp). o
- El código de usuario que registra al cliente';
--------------------------------------------------------
--  DDL for Table TIIVS_REVOCADO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_REVOCADO" 
   (	"COD_REVOCADO" NUMBER(8,0), 
	"COD_PER" NUMBER(8,0), 
	"FECHA_REVOCATORIA" DATE, 
	"COD_AGRUP" NUMBER(8,0), 
	"TIP_PARTIC" VARCHAR2(4 BYTE), 
	"ESTADO" VARCHAR2(4 BYTE), 
	"ALIAS_ARCHIVO" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_SOLICITUD" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"COD_OFI" VARCHAR2(8 BYTE), 
	"ESTADO" CHAR(4 BYTE), 
	"FECHA" TIMESTAMP (6), 
	"COD_TIPO_SOLIC" VARCHAR2(7 BYTE), 
	"REG_ABOGADO" VARCHAR2(7 BYTE), 
	"OBS" VARCHAR2(4000 BYTE), 
	"REG_USUARIO" VARCHAR2(7 BYTE), 
	"NOM_USUARIO" VARCHAR2(75 BYTE), 
	"IMPORTE" NUMBER(12,2), 
	"FECHA_ENVIO" TIMESTAMP (6), 
	"FECHA_ESTADO" TIMESTAMP (6), 
	"MONEDA" VARCHAR2(7 BYTE), 
	"FECHA_RESPUESTA" TIMESTAMP (6), 
	"COMISION" NUMBER, 
	"NRO_VOUCHER" VARCHAR2(12 BYTE), 
	"COD_ESTUDIO" VARCHAR2(7 BYTE), 
	"TIPO_COMISION" VARCHAR2(7 BYTE), 
	"EXONERA_COMISION" VARCHAR2(1 BYTE), 
	"FLAG_REIMPRIMIR" VARCHAR2(1 BYTE) DEFAULT NULL
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD_AGRUPACION
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"NUM_GRUPO" NUMBER(8,0), 
	"ESTADO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD_NIVEL
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_SOLICITUD_NIVEL" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"COD_NIV" VARCHAR2(4 BYTE), 
	"ESTADO_NIVEL" VARCHAR2(4 BYTE), 
	"USUARIO_REGISTRO" VARCHAR2(7 BYTE), 
	"FECHA_REGISTRO" TIMESTAMP (6), 
	"ESTADO_SOLICITUD" VARCHAR2(4 BYTE), 
	"ID" NUMBER(8,0)
   ) ;

   COMMENT ON COLUMN "VISPOD"."TIIVS_SOLICITUD_NIVEL"."USUARIO_REGISTRO" IS 'Código de usuario del responsable o grupo de delegados que efectuan el cambió de estado del nivel ';
   COMMENT ON COLUMN "VISPOD"."TIIVS_SOLICITUD_NIVEL"."FECHA_REGISTRO" IS 'Fecha en que se realizó el cambio de estado de nivel';
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD_OPERBAN
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_SOLICITUD_OPERBAN" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"COD_OPER_BAN" VARCHAR2(7 BYTE), 
	"IMPORTE" NUMBER(12,2), 
	"MONEDA" VARCHAR2(20 BYTE), 
	"TIPO_CAMBIO" NUMBER(12,2)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TAREA
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_TAREA" 
   (	"COD_TAREA" VARCHAR2(7 BYTE), 
	"DES_TAREA" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TAREA_GRUPO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_TAREA_GRUPO" 
   (	"COD_TAREA" VARCHAR2(7 BYTE), 
	"COD_GRUPO" VARCHAR2(7 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TERRITORIO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_TERRITORIO" 
   (	"COD_TER" VARCHAR2(8 BYTE), 
	"DES_TER" VARCHAR2(45 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TIEMPO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_TIEMPO" 
   (	"TIEMPO_ID" NUMBER(8,0), 
	"NUMERO_DIA" NUMBER, 
	"NUMERO_MES" NUMBER, 
	"NOMBRE_MES" VARCHAR2(20 BYTE), 
	"ANIO" NUMBER(*,0), 
	"FECHA_FORMATO" DATE
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TIPO_SOLIC_DOCUMENTO
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" 
   (	"COD_TIPO_SOLIC" VARCHAR2(7 BYTE), 
	"COD_DOC" VARCHAR2(7 BYTE), 
	"ACTIVO" CHAR(1 BYTE), 
	"OBLIGATORIO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TIPO_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TIIVS_TIPO_SOLICITUD" 
   (	"COD_TIP_SOLIC" VARCHAR2(7 BYTE), 
	"DES_TIP_SERVICIO" VARCHAR2(50 BYTE), 
	"ACTIVO" CHAR(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TTIVS_HOST_VOUCHER
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TTIVS_HOST_VOUCHER" 
   (	"NRO_VOUCHER" VARCHAR2(12 BYTE), 
	"TIPO_PAGO" VARCHAR2(25 BYTE), 
	"CUENTA" VARCHAR2(25 BYTE), 
	"MONTO_COMISION" VARCHAR2(15 BYTE), 
	"DIVISA" VARCHAR2(20 BYTE), 
	"COD_SERVICIO" VARCHAR2(100 BYTE), 
	"FECHA_PAGO" VARCHAR2(10 BYTE), 
	"HORA_PAGO" VARCHAR2(5 BYTE), 
	"CENTRO_PAGO" VARCHAR2(20 BYTE), 
	"TERMINAL_PAGO" VARCHAR2(30 BYTE), 
	"USUARIO_PAGO" VARCHAR2(8 BYTE), 
	"USUARIO_REGISTRO" VARCHAR2(7 BYTE), 
	"FECHA_REGISTRO" TIMESTAMP (6), 
	"ESTADO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TTIVS_PARAMETROS
--------------------------------------------------------

  CREATE TABLE "VISPOD"."TTIVS_PARAMETROS" 
   (	"ID_PARAM" NUMBER(8,0), 
	"ID_EMPRESA" NUMBER(8,0), 
	"ID_SISTEMA" NUMBER(8,0), 
	"RUTA_FICHEROS_LOCAL" VARCHAR2(200 BYTE), 
	"SERVER" VARCHAR2(20 BYTE), 
	"LOGIN_SERVER" VARCHAR2(100 BYTE), 
	"PASS_SERVER" VARCHAR2(100 BYTE), 
	"CARPETA_REMOTA" VARCHAR2(200 BYTE), 
	"URL_APP_SCAN" VARCHAR2(255 BYTE), 
	"COD_USUARIO" VARCHAR2(7 BYTE), 
	"RUTA_ARCHIVO_EXPORTAR_EXCEL" VARCHAR2(20 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index PK_FERIADO
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."PK_FERIADO" ON "VISPOD"."TIIVS_FERIADO" ("ID_FERIADO") 
  ;
--------------------------------------------------------
--  DDL for Index PK_PERSONA
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."PK_PERSONA" ON "VISPOD"."TIIVS_PERSONA" ("COD_PER") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_HIST_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_HIST_SOLICITUD" ON "VISPOD"."TIIVS_HIST_SOLICITUD" ("COD_SOLI", "MOVIMIENTO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_NIVEL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_NIVEL_PK" ON "VISPOD"."TIIVS_NIVEL" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_SOLICITUD_AGRUPACIO_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_SOLICITUD_AGRUPACIO_PK" ON "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" ("COD_SOLI", "NUM_GRUPO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_MOVIMIENTO_NIVEL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_MOVIMIENTO_NIVEL_PK" ON "VISPOD"."TIIVS_MOVIMIENTO_NIVEL" ("ID_MOVIMIENTO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_ESTUDIO_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_ESTUDIO_PK" ON "VISPOD"."TIIVS_ESTUDIO" ("COD_ESTUDIO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_OFICINA1_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_OFICINA1_PK" ON "VISPOD"."TIIVS_OFICINA1" ("COD_OFI") 
  ;
--------------------------------------------------------
--  DDL for Index DIM_TIEMPO_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."DIM_TIEMPO_PK" ON "VISPOD"."TIIVS_TIEMPO" ("TIEMPO_ID") 
  ;
--------------------------------------------------------
--  DDL for Index PK_REVOCADO
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."PK_REVOCADO" ON "VISPOD"."TIIVS_REVOCADO" ("COD_REVOCADO") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_SOLICITUD" ON "VISPOD"."TIIVS_SOLICITUD" ("COD_SOLI") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_DOCUMENTO_UK1
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_DOCUMENTO_UK1" ON "VISPOD"."TIIVS_DOCUMENTO" ("NOMBRE") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_MULTITABLA
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_MULTITABLA" ON "VISPOD"."TIIVS_MULTITABLA" ("COD_MULT", "COD_ELEM") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_OPERACION_BANCARIA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_OPERACION_BANCARIA_PK" ON "VISPOD"."TIIVS_OPERACION_BANCARIA" ("COD_OPER_BAN") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_TIPO_OPERACION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_TIPO_OPERACION_PK" ON "VISPOD"."TIIVS_DOCUMENTO" ("COD_DOCUMENTO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_AGRUPACION_PERSONA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_AGRUPACION_PERSONA_PK" ON "VISPOD"."TIIVS_AGRUPACION_PERSONA" ("ID_AGRUPACION") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TIPO_OPERACION
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_TIPO_OPERACION" ON "VISPOD"."TIIVS_TIPO_SOLICITUD" ("COD_TIP_SOLIC") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_GRUPO
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_GRUPO" ON "VISPOD"."TIIVS_GRUPO" ("COD_GRUPO") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_MIEMBRO
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_MIEMBRO" ON "VISPOD"."TIIVS_MIEMBRO" ("COD_MIEMBRO") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_ANEXO_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_ANEXO_SOLICITUD" ON "VISPOD"."TIIVS_ANEXO_SOLICITUD" ("COD_SOLI", "COD_DOC") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_SOLICITUD_OPERBAN_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_SOLICITUD_OPERBAN_PK" ON "VISPOD"."TIIVS_SOLICITUD_OPERBAN" ("COD_SOLI", "COD_OPER_BAN", "MONEDA") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TERRITORIO
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_TERRITORIO" ON "VISPOD"."TIIVS_TERRITORIO" ("COD_TER") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_TIPO_SOLIC_DOCUMENT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENT_PK" ON "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" ("COD_TIPO_SOLIC", "COD_DOC") 
  ;
--------------------------------------------------------
--  DDL for Index TTIVS_PARAMETROS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TTIVS_PARAMETROS_PK" ON "VISPOD"."TTIVS_PARAMETROS" ("ID_PARAM") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_SOLICITUD_NIVEL_PK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_SOLICITUD_NIVEL_PK_PK" ON "VISPOD"."TIIVS_SOLICITUD_NIVEL" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TAREA
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_TAREA" ON "VISPOD"."TIIVS_TAREA" ("COD_TAREA") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TAREA_GRUPO
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."XPKTIIVS_TAREA_GRUPO" ON "VISPOD"."TIIVS_TAREA_GRUPO" ("COD_TAREA", "COD_GRUPO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_DELEGADO_NIVEL_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_DELEGADO_NIVEL_PK" ON "VISPOD"."TIIVS_MIEMBRO_NIVEL" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_HOST_VOUCHER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VISPOD"."TIIVS_HOST_VOUCHER_PK" ON "VISPOD"."TTIVS_HOST_VOUCHER" ("NRO_VOUCHER") 
  ;
--------------------------------------------------------
--  Constraints for Table TIIVS_MULTITABLA
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_MULTITABLA" MODIFY ("COD_MULT" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_MULTITABLA" MODIFY ("COD_ELEM" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_MULTITABLA" ADD CONSTRAINT "XPKTIIVS_MULTITABLA" PRIMARY KEY ("COD_MULT", "COD_ELEM") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_ANEXO_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_ANEXO_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_ANEXO_SOLICITUD" MODIFY ("ALIAS_ARCHIVO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_ANEXO_SOLICITUD" MODIFY ("COD_DOC" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_ANEXO_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_ANEXO_SOLICITUD" PRIMARY KEY ("COD_SOLI", "COD_DOC") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_OPERACION_BANCARIA
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_OPERACION_BANCARIA" MODIFY ("COD_OPER_BAN" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_OPERACION_BANCARIA" ADD CONSTRAINT "TIIVS_OPERACION_BANCARIA_PK" PRIMARY KEY ("COD_OPER_BAN") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_OPERACION_BANCARIA" MODIFY ("TIPO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_ESTUDIO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_ESTUDIO" MODIFY ("COD_ESTUDIO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_ESTUDIO" ADD CONSTRAINT "TIIVS_ESTUDIO_PK" PRIMARY KEY ("COD_ESTUDIO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_TAREA
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TAREA" MODIFY ("COD_TAREA" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TAREA" MODIFY ("DES_TAREA" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TAREA" ADD CONSTRAINT "XPKTIIVS_TAREA" PRIMARY KEY ("COD_TAREA") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_FERIADO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_FERIADO" ADD CONSTRAINT "PK_FERIADO" PRIMARY KEY ("ID_FERIADO") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_FERIADO" MODIFY ("ID_FERIADO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_GRUPO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_GRUPO" MODIFY ("COD_GRUPO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_GRUPO" MODIFY ("DES_GRUPO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_GRUPO" ADD CONSTRAINT "XPKTIIVS_GRUPO" PRIMARY KEY ("COD_GRUPO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_TIEMPO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TIEMPO" ADD CONSTRAINT "DIM_TIEMPO_PK" PRIMARY KEY ("TIEMPO_ID") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_TIEMPO" MODIFY ("TIEMPO_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_MIEMBRO_NIVEL
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO_NIVEL" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO_NIVEL" ADD CONSTRAINT "TIIVS_DELEGADO_NIVEL_PK" PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_OFICINA1
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_OFICINA1" MODIFY ("COD_OFI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_OFICINA1" ADD CONSTRAINT "TIIVS_OFICINA1_PK" PRIMARY KEY ("COD_OFI") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_TERRITORIO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TERRITORIO" MODIFY ("COD_TER" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TERRITORIO" MODIFY ("DES_TER" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TERRITORIO" ADD CONSTRAINT "XPKTIIVS_TERRITORIO" PRIMARY KEY ("COD_TER") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD_NIVEL
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_NIVEL" MODIFY ("COD_SOLI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_NIVEL" MODIFY ("COD_NIV" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_NIVEL" ADD CONSTRAINT "TIIVS_SOLICITUD_NIVEL_PK_PK" PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_NIVEL" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_REVOCADO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_REVOCADO" ADD CONSTRAINT "PK_REVOCADO" PRIMARY KEY ("COD_REVOCADO") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_REVOCADO" MODIFY ("COD_REVOCADO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_HIST_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" MODIFY ("MOVIMIENTO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" MODIFY ("FECHA" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" MODIFY ("REG_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" MODIFY ("NOM_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" MODIFY ("ESTADO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_HIST_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_HIST_SOLICITUD" PRIMARY KEY ("COD_SOLI", "MOVIMIENTO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD_AGRUPACION
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" MODIFY ("COD_SOLI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" MODIFY ("NUM_GRUPO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" ADD CONSTRAINT "TIIVS_SOLICITUD_AGRUPACIO_PK" PRIMARY KEY ("COD_SOLI", "NUM_GRUPO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" MODIFY ("COD_OFI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" MODIFY ("ESTADO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" MODIFY ("FECHA" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" MODIFY ("COD_TIPO_SOLIC" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" MODIFY ("REG_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" MODIFY ("NOM_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_SOLICITUD" PRIMARY KEY ("COD_SOLI") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_DOCUMENTO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_DOCUMENTO" ADD CONSTRAINT "TIIVS_DOCUMENTO_UK1" UNIQUE ("NOMBRE") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_DOCUMENTO" MODIFY ("COD_DOCUMENTO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_DOCUMENTO" ADD CONSTRAINT "TIIVS_TIPO_OPERACION_PK" PRIMARY KEY ("COD_DOCUMENTO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_AGRUPACION_PERSONA
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_AGRUPACION_PERSONA" ADD CONSTRAINT "TIIVS_AGRUPACION_PERSONA_PK" PRIMARY KEY ("ID_AGRUPACION") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_AGRUPACION_PERSONA" MODIFY ("ID_AGRUPACION" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_NIVEL
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_NIVEL" ADD CONSTRAINT "TIIVS_NIVEL_PK" PRIMARY KEY ("ID") ENABLE;
--------------------------------------------------------
--  Constraints for Table TTIVS_HOST_VOUCHER
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TTIVS_HOST_VOUCHER" MODIFY ("NRO_VOUCHER" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TTIVS_HOST_VOUCHER" ADD CONSTRAINT "TIIVS_HOST_VOUCHER_PK" PRIMARY KEY ("NRO_VOUCHER") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_MIEMBRO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO" MODIFY ("COD_MIEMBRO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO" MODIFY ("CRITERIO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO" ADD CONSTRAINT "XPKTIIVS_MIEMBRO" PRIMARY KEY ("COD_MIEMBRO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_MOVIMIENTO_NIVEL
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_MOVIMIENTO_NIVEL" MODIFY ("ID_MOVIMIENTO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_MOVIMIENTO_NIVEL" ADD CONSTRAINT "TIIVS_MOVIMIENTO_NIVEL_PK" PRIMARY KEY ("ID_MOVIMIENTO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD_OPERBAN
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_OPERBAN" MODIFY ("COD_SOLI" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_OPERBAN" MODIFY ("COD_OPER_BAN" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_OPERBAN" MODIFY ("MONEDA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_TAREA_GRUPO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TAREA_GRUPO" MODIFY ("COD_TAREA" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TAREA_GRUPO" MODIFY ("COD_GRUPO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TAREA_GRUPO" ADD CONSTRAINT "XPKTIIVS_TAREA_GRUPO" PRIMARY KEY ("COD_TAREA", "COD_GRUPO") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_TIPO_SOLIC_DOCUMENTO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" ADD CONSTRAINT "TIIVS_TIPO_SOLIC_DOCUMENT_PK" PRIMARY KEY ("COD_TIPO_SOLIC", "COD_DOC") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("COD_TIPO_SOLIC" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("COD_DOC" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("ACTIVO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_PERSONA
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_PERSONA" MODIFY ("COD_PER" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_PERSONA" ADD CONSTRAINT "PK_PERSONA" PRIMARY KEY ("COD_PER") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_TIPO_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_TIPO_SOLICITUD" PRIMARY KEY ("COD_TIP_SOLIC") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLICITUD" MODIFY ("COD_TIP_SOLIC" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLICITUD" MODIFY ("DES_TIP_SERVICIO" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLICITUD" MODIFY ("ACTIVO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TTIVS_PARAMETROS
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TTIVS_PARAMETROS" MODIFY ("ID_PARAM" NOT NULL ENABLE);
  ALTER TABLE "VISPOD"."TTIVS_PARAMETROS" ADD CONSTRAINT "TTIVS_PARAMETROS_PK" PRIMARY KEY ("ID_PARAM") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_AGRUPACION_PERSONA
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_AGRUPACION_PERSONA" ADD CONSTRAINT "TIIVS_AGRUP_PERSONA_AGRU_FK1" FOREIGN KEY ("COD_SOLI", "NUM_GRUPO")
	  REFERENCES "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" ("COD_SOLI", "NUM_GRUPO") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_AGRUPACION_PERSONA" ADD CONSTRAINT "TIIVS_AGRUP_PERSONA_PER_FK" FOREIGN KEY ("COD_PER")
	  REFERENCES "VISPOD"."TIIVS_PERSONA" ("COD_PER") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_MIEMBRO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO" ADD CONSTRAINT "R_12" FOREIGN KEY ("COD_GRUPO")
	  REFERENCES "VISPOD"."TIIVS_GRUPO" ("COD_GRUPO") ON DELETE SET NULL ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_MIEMBRO_NIVEL
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_MIEMBRO_NIVEL" ADD CONSTRAINT "TIIVS_MIEMBRO_NIVEL_MIEMBRO_FK" FOREIGN KEY ("COD_MIEMBRO")
	  REFERENCES "VISPOD"."TIIVS_MIEMBRO" ("COD_MIEMBRO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_MOVIMIENTO_NIVEL
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_MOVIMIENTO_NIVEL" ADD CONSTRAINT "TIIVS_MOVIMIENTO_NIVEL_TI_FK1" FOREIGN KEY ("ID_SOL_NIVEL")
	  REFERENCES "VISPOD"."TIIVS_SOLICITUD_NIVEL" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_OFICINA1
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_OFICINA1" ADD CONSTRAINT "TIIVS_OFICINA1_TERRITORIO" FOREIGN KEY ("COD_TERR")
	  REFERENCES "VISPOD"."TIIVS_TERRITORIO" ("COD_TER") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_REVOCADO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_REVOCADO" ADD CONSTRAINT "FK_USU1" FOREIGN KEY ("COD_PER")
	  REFERENCES "VISPOD"."TIIVS_PERSONA" ("COD_PER") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_ESTUDIO_FK" FOREIGN KEY ("COD_ESTUDIO")
	  REFERENCES "VISPOD"."TIIVS_ESTUDIO" ("COD_ESTUDIO") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_OFICINA" FOREIGN KEY ("COD_OFI")
	  REFERENCES "VISPOD"."TIIVS_OFICINA1" ("COD_OFI") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_TIIVS_SOL_FK1" FOREIGN KEY ("COD_TIPO_SOLIC")
	  REFERENCES "VISPOD"."TIIVS_TIPO_SOLICITUD" ("COD_TIP_SOLIC") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD_AGRUPACION
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_AGRUPACION" ADD CONSTRAINT "FK_PARTICI_SOLICITUD" FOREIGN KEY ("COD_SOLI")
	  REFERENCES "VISPOD"."TIIVS_SOLICITUD" ("COD_SOLI") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD_NIVEL
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_NIVEL" ADD CONSTRAINT "TIIVS_SOLICITUD_NIVEL_FK" FOREIGN KEY ("COD_SOLI")
	  REFERENCES "VISPOD"."TIIVS_SOLICITUD" ("COD_SOLI") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD_OPERBAN
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_SOLICITUD_OPERBAN" ADD CONSTRAINT "TIIVS_SOLICITUD_OPERBAN_T_FK2" FOREIGN KEY ("COD_OPER_BAN")
	  REFERENCES "VISPOD"."TIIVS_OPERACION_BANCARIA" ("COD_OPER_BAN") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_TAREA_GRUPO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TAREA_GRUPO" ADD CONSTRAINT "R_3" FOREIGN KEY ("COD_TAREA")
	  REFERENCES "VISPOD"."TIIVS_TAREA" ("COD_TAREA") ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_TAREA_GRUPO" ADD CONSTRAINT "R_4" FOREIGN KEY ("COD_GRUPO")
	  REFERENCES "VISPOD"."TIIVS_GRUPO" ("COD_GRUPO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_TIPO_SOLIC_DOCUMENTO
--------------------------------------------------------

  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" ADD CONSTRAINT "R_6" FOREIGN KEY ("COD_TIPO_SOLIC")
	  REFERENCES "VISPOD"."TIIVS_TIPO_SOLICITUD" ("COD_TIP_SOLIC") ON DELETE SET NULL ENABLE;
  ALTER TABLE "VISPOD"."TIIVS_TIPO_SOLIC_DOCUMENTO" ADD CONSTRAINT "TIIVS_TIPO_SOLIC_DOCUMENT_DOC" FOREIGN KEY ("COD_DOC")
	  REFERENCES "VISPOD"."TIIVS_DOCUMENTO" ("COD_DOCUMENTO") ENABLE;

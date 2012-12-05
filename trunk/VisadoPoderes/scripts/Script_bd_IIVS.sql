--------------------------------------------------------
-- Archivo creado  - martes-diciembre-04-2012   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence SEQ_ESTUDIO
--------------------------------------------------------

   CREATE SEQUENCE  "IIVS"."SEQ_ESTUDIO"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 201 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_OPER_BAN
--------------------------------------------------------

   CREATE SEQUENCE  "IIVS"."SEQ_OPER_BAN"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 241 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_SOL
--------------------------------------------------------

   CREATE SEQUENCE  "IIVS"."SEQ_SOL"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 641 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_TIPO_DOC
--------------------------------------------------------

   CREATE SEQUENCE  "IIVS"."SEQ_TIPO_DOC"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 209 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence SEQ_TIPO_OPER
--------------------------------------------------------

   CREATE SEQUENCE  "IIVS"."SEQ_TIPO_OPER"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 84 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Table LDAPPERU2
--------------------------------------------------------

  CREATE TABLE "IIVS"."LDAPPERU2" 
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
--  DDL for Table TEMP_TCDT050
--------------------------------------------------------

  CREATE TABLE "IIVS"."TEMP_TCDT050" 
   (	"TCCENTIT" VARCHAR2(20 BYTE), 
	"TCCOFICI" VARCHAR2(20 BYTE), 
	"TCMOFICI" VARCHAR2(100 BYTE), 
	"TCMOFCUR" VARCHAR2(100 BYTE), 
	"TCYOFBAI" VARCHAR2(20 BYTE), 
	"TCTOFOPE" VARCHAR2(20 BYTE), 
	"TCTOFICI" VARCHAR2(20 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TEMP_TCDT051
--------------------------------------------------------

  CREATE TABLE "IIVS"."TEMP_TCDT051" 
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

  CREATE TABLE "IIVS"."TIIVS_AGRUPACION_PERSONA" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"NUM_GRUPO" NUMBER(8,0), 
	"COD_PER" NUMBER(8,0), 
	"TIP_PARTIC" VARCHAR2(4 BYTE), 
	"CLASIF_PER" VARCHAR2(4 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_ANEXO_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_ANEXO_SOLICITUD" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"ALIAS_ARCHIVO" VARCHAR2(50 BYTE), 
	"COD_DOC" VARCHAR2(7 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_ESTUDIO
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_ESTUDIO" 
   (	"COD_ESTUDIO" VARCHAR2(7 BYTE), 
	"DES_ESTUDIO" VARCHAR2(50 BYTE), 
	"ACTIVO" CHAR(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_GRUPO
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_GRUPO" 
   (	"COD_GRUPO" VARCHAR2(7 BYTE), 
	"DES_GRUPO" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_HIST_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_HIST_SOLICITUD" 
   (	"MOVIMIENTO" VARCHAR2(3 BYTE), 
	"FECHA" TIMESTAMP (6), 
	"REG_USUARIO" VARCHAR2(7 BYTE), 
	"NOM_USUARIO" VARCHAR2(75 BYTE), 
	"COD_SOLI" VARCHAR2(7 BYTE), 
	"OBS" VARCHAR2(1000 BYTE), 
	"REG_ABOGADO" VARCHAR2(7 BYTE), 
	"ESTADO" CHAR(7 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_MIEMBRO
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_MIEMBRO" 
   (	"COD_MIEMBRO" VARCHAR2(7 BYTE), 
	"DESCRIPCION" VARCHAR2(100 BYTE), 
	"CRITERIO" VARCHAR2(7 BYTE), 
	"COD_GRUPO" VARCHAR2(7 BYTE), 
	"ESTUDIO" VARCHAR2(7 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_MULTITABLA
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_MULTITABLA" 
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

  CREATE TABLE "IIVS"."TIIVS_NIVEL" 
   (	"COD_NIV" VARCHAR2(4 BYTE), 
	"DES_NIV" VARCHAR2(45 BYTE), 
	"ESTADO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_OFICINA1
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_OFICINA1" 
   (	"COD_OFI" VARCHAR2(8 BYTE), 
	"DES_OFI" VARCHAR2(45 BYTE), 
	"ACTIVO" CHAR(1 BYTE), 
	"COD_TERR" VARCHAR2(5 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_OPERACION_BANCARIA
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_OPERACION_BANCARIA" 
   (	"COD_OPER_BAN" VARCHAR2(7 BYTE), 
	"DES_OPER_BAN" VARCHAR2(50 BYTE), 
	"ACTIVO" CHAR(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_PERSONA
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_PERSONA" 
   (	"COD_PER" NUMBER(8,0), 
	"COD_CEN" VARCHAR2(7 BYTE), 
	"TIP_DOI" VARCHAR2(4 BYTE), 
	"NUM_DOI" VARCHAR2(13 BYTE), 
	"NOMBRE" VARCHAR2(25 BYTE), 
	"APE_PAT" VARCHAR2(25 BYTE), 
	"APE_MAT" VARCHAR2(25 BYTE), 
	"NUM_CEL" VARCHAR2(11 BYTE), 
	"EMAIL" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_SOLICITUD" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"COD_OFI" VARCHAR2(8 BYTE), 
	"ESTADO" VARCHAR2(7 BYTE), 
	"FECHA" TIMESTAMP (6), 
	"COD_TIPO_SOLIC" VARCHAR2(7 BYTE), 
	"REG_ABOGADO" VARCHAR2(7 BYTE), 
	"OBS" VARCHAR2(1000 BYTE), 
	"REG_USUARIO" VARCHAR2(7 BYTE), 
	"NOM_USUARIO" VARCHAR2(75 BYTE), 
	"IMPORTE" NUMBER(12,2), 
	"FECHA_ENVIO" TIMESTAMP (6), 
	"FECHA_ESTADO" TIMESTAMP (6), 
	"MONEDA" VARCHAR2(7 BYTE), 
	"FECHA_RESPUESTA" TIMESTAMP (6), 
	"COMISION" NUMBER(12,2), 
	"NRO_VOUCHER" VARCHAR2(12 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD_AGRUPACION
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_SOLICITUD_AGRUPACION" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"NUM_GRUPO" NUMBER(8,0), 
	"ACTIVO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD_ESTADO_NIVEL
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_SOLICITUD_ESTADO_NIVEL" 
   (	"COD_SOL" VARCHAR2(7 BYTE), 
	"COD_NIVEL" VARCHAR2(4 BYTE), 
	"COD_ESTADO" VARCHAR2(4 BYTE), 
	"COD_USUARIO" VARCHAR2(10 BYTE), 
	"TIPO" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD_OPERBAN
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_SOLICITUD_OPERBAN" 
   (	"COD_SOLI" VARCHAR2(7 BYTE), 
	"COD_OPER_BAN" VARCHAR2(7 BYTE), 
	"IMPORTE" NUMBER(12,2), 
	"MONEDA" VARCHAR2(20 BYTE), 
	"TIPO_CAMBIO" NUMBER(12,2)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TAREA
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_TAREA" 
   (	"COD_TAREA" VARCHAR2(7 BYTE), 
	"DES_TAREA" VARCHAR2(50 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TAREA_GRUPO
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_TAREA_GRUPO" 
   (	"COD_TAREA" VARCHAR2(7 BYTE), 
	"COD_GRUPO" VARCHAR2(7 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TERRITORIO
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_TERRITORIO" 
   (	"COD_TER" VARCHAR2(8 BYTE), 
	"DES_TER" VARCHAR2(45 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TIPO_SOLIC_DOCUMENTO
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" 
   (	"COD_TIPO_SOLIC" VARCHAR2(7 BYTE), 
	"COD_DOC" VARCHAR2(7 BYTE), 
	"DES_DOC" VARCHAR2(300 BYTE), 
	"ACTIVO" CHAR(1 BYTE), 
	"OBLIGATORIO" CHAR(1 BYTE) DEFAULT '1'
   ) ;
--------------------------------------------------------
--  DDL for Table TIIVS_TIPO_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "IIVS"."TIIVS_TIPO_SOLICITUD" 
   (	"COD_TIP_SOLIC" VARCHAR2(7 BYTE), 
	"DES_TIP_SERVICIO" VARCHAR2(50 BYTE), 
	"ACTIVO" CHAR(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index TIIVS_OPERACION_BANCARIA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."TIIVS_OPERACION_BANCARIA_PK" ON "IIVS"."TIIVS_OPERACION_BANCARIA" ("COD_OPER_BAN") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TIPO_OPERACION
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_TIPO_OPERACION" ON "IIVS"."TIIVS_TIPO_SOLICITUD" ("COD_TIP_SOLIC") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_SOLICITUD" ON "IIVS"."TIIVS_SOLICITUD" ("COD_SOLI") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TAREA
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_TAREA" ON "IIVS"."TIIVS_TAREA" ("COD_TAREA") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_MULTITABLA
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_MULTITABLA" ON "IIVS"."TIIVS_MULTITABLA" ("COD_MULT", "COD_ELEM") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_SOLICITUD_ESTADO_NI_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."TIIVS_SOLICITUD_ESTADO_NI_PK" ON "IIVS"."TIIVS_SOLICITUD_ESTADO_NIVEL" ("COD_SOL") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TERRITORIO
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_TERRITORIO" ON "IIVS"."TIIVS_TERRITORIO" ("COD_TER") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_SOLICITUD_OPERBAN_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."TIIVS_SOLICITUD_OPERBAN_PK" ON "IIVS"."TIIVS_SOLICITUD_OPERBAN" ("COD_SOLI", "COD_OPER_BAN") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_ESTUDIO_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."TIIVS_ESTUDIO_PK" ON "IIVS"."TIIVS_ESTUDIO" ("COD_ESTUDIO") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_ANEXO_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_ANEXO_SOLICITUD" ON "IIVS"."TIIVS_ANEXO_SOLICITUD" ("COD_SOLI", "COD_DOC") 
  ;
--------------------------------------------------------
--  DDL for Index SYS_C0012939
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."SYS_C0012939" ON "IIVS"."TIIVS_NIVEL" ("COD_NIV") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TIPO_DOCUMENTO
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_TIPO_DOCUMENTO" ON "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" ("COD_DOC") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_MIEMBRO
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_MIEMBRO" ON "IIVS"."TIIVS_MIEMBRO" ("COD_MIEMBRO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_OFICINA1_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."TIIVS_OFICINA1_PK" ON "IIVS"."TIIVS_OFICINA1" ("COD_OFI") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_GRUPO
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_GRUPO" ON "IIVS"."TIIVS_GRUPO" ("COD_GRUPO") 
  ;
--------------------------------------------------------
--  DDL for Index TIIVS_SOLICITUD_AGRUPACIO_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."TIIVS_SOLICITUD_AGRUPACIO_PK" ON "IIVS"."TIIVS_SOLICITUD_AGRUPACION" ("NUM_GRUPO") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_TAREA_GRUPO
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_TAREA_GRUPO" ON "IIVS"."TIIVS_TAREA_GRUPO" ("COD_TAREA", "COD_GRUPO") 
  ;
--------------------------------------------------------
--  DDL for Index PK_PERSONA
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."PK_PERSONA" ON "IIVS"."TIIVS_PERSONA" ("COD_PER") 
  ;
--------------------------------------------------------
--  DDL for Index XPKTIIVS_HIST_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "IIVS"."XPKTIIVS_HIST_SOLICITUD" ON "IIVS"."TIIVS_HIST_SOLICITUD" ("COD_SOLI", "MOVIMIENTO") 
  ;
--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD_AGRUPACION
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_SOLICITUD_AGRUPACION" ADD CONSTRAINT "TIIVS_SOLICITUD_AGRUPACIO_PK" PRIMARY KEY ("NUM_GRUPO") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD_AGRUPACION" MODIFY ("NUM_GRUPO" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table TIIVS_PERSONA
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_PERSONA" ADD CONSTRAINT "PK_PERSONA" PRIMARY KEY ("COD_PER") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_PERSONA" MODIFY ("COD_PER" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table TIIVS_HIST_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_HIST_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_HIST_SOLICITUD" PRIMARY KEY ("COD_SOLI", "MOVIMIENTO") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_HIST_SOLICITUD" MODIFY ("ESTADO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_HIST_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_HIST_SOLICITUD" MODIFY ("NOM_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_HIST_SOLICITUD" MODIFY ("REG_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_HIST_SOLICITUD" MODIFY ("FECHA" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_HIST_SOLICITUD" MODIFY ("MOVIMIENTO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_MULTITABLA
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_MULTITABLA" ADD CONSTRAINT "XPKTIIVS_MULTITABLA" PRIMARY KEY ("COD_MULT", "COD_ELEM") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_MULTITABLA" MODIFY ("COD_ELEM" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_MULTITABLA" MODIFY ("COD_MULT" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_OPERACION_BANCARIA
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_OPERACION_BANCARIA" ADD CONSTRAINT "TIIVS_OPERACION_BANCARIA_PK" PRIMARY KEY ("COD_OPER_BAN") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_OPERACION_BANCARIA" MODIFY ("COD_OPER_BAN" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_OFICINA1
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_OFICINA1" ADD CONSTRAINT "TIIVS_OFICINA1_PK" PRIMARY KEY ("COD_OFI") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_OFICINA1" MODIFY ("COD_OFI" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_ANEXO_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_ANEXO_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_ANEXO_SOLICITUD" PRIMARY KEY ("COD_SOLI", "COD_DOC") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_ANEXO_SOLICITUD" MODIFY ("COD_DOC" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_ANEXO_SOLICITUD" MODIFY ("ALIAS_ARCHIVO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_ANEXO_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table TIIVS_TIPO_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_TIPO_SOLICITUD" PRIMARY KEY ("COD_TIP_SOLIC") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLICITUD" MODIFY ("ACTIVO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLICITUD" MODIFY ("DES_TIP_SERVICIO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLICITUD" MODIFY ("COD_TIP_SOLIC" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_ESTUDIO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_ESTUDIO" ADD CONSTRAINT "TIIVS_ESTUDIO_PK" PRIMARY KEY ("COD_ESTUDIO") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_ESTUDIO" MODIFY ("COD_ESTUDIO" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table TIIVS_TERRITORIO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_TERRITORIO" ADD CONSTRAINT "XPKTIIVS_TERRITORIO" PRIMARY KEY ("COD_TER") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_TERRITORIO" MODIFY ("DES_TER" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TERRITORIO" MODIFY ("COD_TER" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_SOLICITUD" PRIMARY KEY ("COD_SOLI") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" MODIFY ("NOM_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" MODIFY ("REG_USUARIO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" MODIFY ("COD_TIPO_SOLIC" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" MODIFY ("FECHA" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" MODIFY ("ESTADO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" MODIFY ("COD_OFI" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_TIPO_SOLIC_DOCUMENTO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("OBLIGATORIO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("ACTIVO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("DES_DOC" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("COD_DOC" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" MODIFY ("COD_TIPO_SOLIC" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" ADD CONSTRAINT "XPKTIIVS_TIPO_DOCUMENTO" PRIMARY KEY ("COD_DOC") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_NIVEL
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_NIVEL" ADD PRIMARY KEY ("COD_NIV") ENABLE;
--------------------------------------------------------
--  Constraints for Table TIIVS_TAREA_GRUPO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_TAREA_GRUPO" ADD CONSTRAINT "XPKTIIVS_TAREA_GRUPO" PRIMARY KEY ("COD_TAREA", "COD_GRUPO") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_TAREA_GRUPO" MODIFY ("COD_GRUPO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TAREA_GRUPO" MODIFY ("COD_TAREA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_GRUPO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_GRUPO" ADD CONSTRAINT "XPKTIIVS_GRUPO" PRIMARY KEY ("COD_GRUPO") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_GRUPO" MODIFY ("DES_GRUPO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_GRUPO" MODIFY ("COD_GRUPO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_TAREA
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_TAREA" ADD CONSTRAINT "XPKTIIVS_TAREA" PRIMARY KEY ("COD_TAREA") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_TAREA" MODIFY ("DES_TAREA" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_TAREA" MODIFY ("COD_TAREA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_MIEMBRO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_MIEMBRO" ADD CONSTRAINT "XPKTIIVS_MIEMBRO" PRIMARY KEY ("COD_MIEMBRO") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_MIEMBRO" MODIFY ("CRITERIO" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_MIEMBRO" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_MIEMBRO" MODIFY ("COD_MIEMBRO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD_OPERBAN
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_SOLICITUD_OPERBAN" ADD CONSTRAINT "TIIVS_SOLICITUD_OPERBAN_PK" PRIMARY KEY ("COD_SOLI", "COD_OPER_BAN") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD_OPERBAN" MODIFY ("COD_OPER_BAN" NOT NULL ENABLE);
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD_OPERBAN" MODIFY ("COD_SOLI" NOT NULL ENABLE);



--------------------------------------------------------
--  Ref Constraints for Table TIIVS_AGRUPACION_PERSONA
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_AGRUPACION_PERSONA" ADD CONSTRAINT "TIIVS_AGRUPACION_PERSONA_SOL" FOREIGN KEY ("COD_SOLI")
	  REFERENCES "IIVS"."TIIVS_SOLICITUD" ("COD_SOLI") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_AGRUPACION_PERSONA" ADD CONSTRAINT "TIIVS_AGRUPACION_PERSONA__PER" FOREIGN KEY ("COD_PER")
	  REFERENCES "IIVS"."TIIVS_PERSONA" ("COD_PER") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_AGRUPACION_PERSONA" ADD CONSTRAINT "TIIVS_AGRUPACION_PERSONA__SOA" FOREIGN KEY ("NUM_GRUPO")
	  REFERENCES "IIVS"."TIIVS_SOLICITUD_AGRUPACION" ("NUM_GRUPO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_ANEXO_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_ANEXO_SOLICITUD" ADD CONSTRAINT "R_11" FOREIGN KEY ("COD_DOC")
	  REFERENCES "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" ("COD_DOC") DISABLE;



--------------------------------------------------------
--  Ref Constraints for Table TIIVS_MIEMBRO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_MIEMBRO" ADD CONSTRAINT "R_12" FOREIGN KEY ("COD_GRUPO")
	  REFERENCES "IIVS"."TIIVS_GRUPO" ("COD_GRUPO") ON DELETE SET NULL ENABLE;





--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_OFICINA" FOREIGN KEY ("COD_OFI")
	  REFERENCES "IIVS"."TIIVS_OFICINA1" ("COD_OFI") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_TIIVS_SOL_FK1" FOREIGN KEY ("COD_SOLI")
	  REFERENCES "IIVS"."TIIVS_SOLICITUD" ("COD_SOLI") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD_AGRUPACION
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_SOLICITUD_AGRUPACION" ADD CONSTRAINT "FK_PARTICI_SOLICITUD" FOREIGN KEY ("COD_SOLI")
	  REFERENCES "IIVS"."TIIVS_SOLICITUD" ("COD_SOLI") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD_OPERBAN
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_SOLICITUD_OPERBAN" ADD CONSTRAINT "TIIVS_SOLICITUD_OPERBAN_T_FK2" FOREIGN KEY ("COD_OPER_BAN")
	  REFERENCES "IIVS"."TIIVS_OPERACION_BANCARIA" ("COD_OPER_BAN") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table TIIVS_TAREA_GRUPO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_TAREA_GRUPO" ADD CONSTRAINT "R_3" FOREIGN KEY ("COD_TAREA")
	  REFERENCES "IIVS"."TIIVS_TAREA" ("COD_TAREA") ENABLE;
  ALTER TABLE "IIVS"."TIIVS_TAREA_GRUPO" ADD CONSTRAINT "R_4" FOREIGN KEY ("COD_GRUPO")
	  REFERENCES "IIVS"."TIIVS_GRUPO" ("COD_GRUPO") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table TIIVS_TIPO_SOLIC_DOCUMENTO
--------------------------------------------------------

  ALTER TABLE "IIVS"."TIIVS_TIPO_SOLIC_DOCUMENTO" ADD CONSTRAINT "R_6" FOREIGN KEY ("COD_TIPO_SOLIC")
	  REFERENCES "IIVS"."TIIVS_TIPO_SOLICITUD" ("COD_TIP_SOLIC") ON DELETE SET NULL ENABLE;


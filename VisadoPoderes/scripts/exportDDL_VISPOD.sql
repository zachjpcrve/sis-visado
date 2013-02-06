--------------------------------------------------------
-- Archivo creado  - miércoles-febrero-06-2013   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence SEQ_ESTUDIO
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_ESTUDIO"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 201 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_FERIADO
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_FERIADO"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 261 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_MOVIMIENTO_NIVEL
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_MOVIMIENTO_NIVEL"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 61 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_OPER_BAN
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_OPER_BAN"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 241 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_PER
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_PER"  MINVALUE 1 MAXVALUE 99999999 INCREMENT BY 1 START WITH 84 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_REV
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_REV"  MINVALUE 1 MAXVALUE 99999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_SOL
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_SOL"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 641 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_SOL_NIVEL
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_SOL_NIVEL"  MINVALUE 1 MAXVALUE 99999999 INCREMENT BY 1 START WITH 33 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_TIPO_DOC
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_TIPO_DOC"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 209 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Sequence SEQ_TIPO_OPER
--------------------------------------------------------

   CREATE SEQUENCE  "SEQ_TIPO_OPER"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 84 CACHE 20 NOORDER  NOCYCLE
/
--------------------------------------------------------
--  DDL for Table TIIVS_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "TIIVS_SOLICITUD" ("COD_SOLI" VARCHAR2(7), "COD_OFI" VARCHAR2(8), "ESTADO" CHAR(4), "FECHA" TIMESTAMP (6), "COD_TIPO_SOLIC" VARCHAR2(7), "REG_ABOGADO" VARCHAR2(7), "OBS" VARCHAR2(1000), "REG_USUARIO" VARCHAR2(7), "NOM_USUARIO" VARCHAR2(75), "IMPORTE" NUMBER(12,2), "FECHA_ENVIO" TIMESTAMP (6), "FECHA_ESTADO" TIMESTAMP (6), "MONEDA" VARCHAR2(7), "FECHA_RESPUESTA" TIMESTAMP (6), "COMISION" NUMBER, "NRO_VOUCHER" VARCHAR2(12), "COD_ESTUDIO" VARCHAR2(7))
/
--------------------------------------------------------
--  DDL for Table TIIVS_HIST_SOLICITUD
--------------------------------------------------------

  CREATE TABLE "TIIVS_HIST_SOLICITUD" ("MOVIMIENTO" VARCHAR2(3), "FECHA" TIMESTAMP (6), "REG_USUARIO" VARCHAR2(7), "NOM_USUARIO" VARCHAR2(75), "COD_SOLI" VARCHAR2(7), "OBS" VARCHAR2(1000), "REG_ABOGADO" VARCHAR2(7), "ESTADO" CHAR(4), "NIVEL" VARCHAR2(4), "NIVEL_ESTADO" VARCHAR2(4), "NIVEL_ROL" VARCHAR2(4))
/
--------------------------------------------------------
--  DDL for Index XPKTIIVS_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "XPKTIIVS_SOLICITUD" ON "TIIVS_SOLICITUD" ("COD_SOLI")
/
--------------------------------------------------------
--  DDL for Index XPKTIIVS_HIST_SOLICITUD
--------------------------------------------------------

  CREATE UNIQUE INDEX "XPKTIIVS_HIST_SOLICITUD" ON "TIIVS_HIST_SOLICITUD" ("COD_SOLI", "MOVIMIENTO")
/
--------------------------------------------------------
--  Constraints for Table TIIVS_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "TIIVS_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_SOLICITUD" MODIFY ("COD_OFI" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_SOLICITUD" MODIFY ("ESTADO" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_SOLICITUD" MODIFY ("FECHA" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_SOLICITUD" MODIFY ("COD_TIPO_SOLIC" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_SOLICITUD" MODIFY ("REG_USUARIO" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_SOLICITUD" MODIFY ("NOM_USUARIO" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_SOLICITUD" PRIMARY KEY ("COD_SOLI") ENABLE
/
--------------------------------------------------------
--  Constraints for Table TIIVS_HIST_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "TIIVS_HIST_SOLICITUD" MODIFY ("MOVIMIENTO" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_HIST_SOLICITUD" MODIFY ("FECHA" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_HIST_SOLICITUD" MODIFY ("REG_USUARIO" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_HIST_SOLICITUD" MODIFY ("NOM_USUARIO" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_HIST_SOLICITUD" MODIFY ("COD_SOLI" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_HIST_SOLICITUD" MODIFY ("ESTADO" NOT NULL ENABLE)
  ALTER TABLE "TIIVS_HIST_SOLICITUD" ADD CONSTRAINT "XPKTIIVS_HIST_SOLICITUD" PRIMARY KEY ("COD_SOLI", "MOVIMIENTO") ENABLE
/
--------------------------------------------------------
--  Ref Constraints for Table TIIVS_SOLICITUD
--------------------------------------------------------

  ALTER TABLE "TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_ESTUDIO_FK" FOREIGN KEY ("COD_ESTUDIO") REFERENCES "TIIVS_ESTUDIO" ("COD_ESTUDIO") ENABLE
  ALTER TABLE "TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_OFICINA" FOREIGN KEY ("COD_OFI") REFERENCES "TIIVS_OFICINA1" ("COD_OFI") ENABLE
  ALTER TABLE "TIIVS_SOLICITUD" ADD CONSTRAINT "TIIVS_SOLICITUD_TIIVS_SOL_FK1" FOREIGN KEY ("COD_TIPO_SOLIC") REFERENCES "TIIVS_TIPO_SOLICITUD" ("COD_TIP_SOLIC") ENABLE
/

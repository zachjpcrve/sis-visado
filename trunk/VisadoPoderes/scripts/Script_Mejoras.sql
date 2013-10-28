/* TIPO DE REGISTRO */
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'Titular', VALOR3 = 'R1' WHERE COD_MULT = 'T12' AND COD_ELEM = '0001';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'Apoderado', VALOR3 = 'R2' WHERE COD_MULT = 'T12' AND COD_ELEM = '0002';
INSERT INTO VISPOD.TIIVS_MULTITABLA (COD_MULT, COD_ELEM, VALOR1, VALOR2, VALOR3) VALUES ('T12', '0003', 'Heredero', '1', 'R2');

/* CLASIFICACION */
--TITULAR
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'FALLECIDO', VALOR2 = '1', VALOR3 = '0001' WHERE COD_MULT = 'T13' AND COD_ELEM = '0001';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'MENOR DE EDAD', VALOR2 = '1', VALOR3 = '0001' WHERE COD_MULT = 'T13' AND COD_ELEM = '0002';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'INCAPAZ', VALOR2 = '1', VALOR3 = '0001' WHERE COD_MULT = 'T13' AND COD_ELEM = '0003';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'PERSONAS JUR�DICAS', VALOR2 = '1', VALOR3 = '0001' WHERE COD_MULT = 'T13' AND COD_ELEM = '0004';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'PERSONA NATURAL (+18 A�OS)', VALOR2 = '1', VALOR3 = '0001' WHERE COD_MULT = 'T13' AND COD_ELEM = '0005';

-- APODERADO
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'TUTOR', VALOR2 = '1', VALOR3 = '0002' WHERE COD_MULT = 'T13' AND COD_ELEM = '0006';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'CURADOR', VALOR2 = '1', VALOR3 = '0002' WHERE COD_MULT = 'T13' AND COD_ELEM = '0007';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'REPRESENTANTE', VALOR2 = '1', VALOR3 = '0002' WHERE COD_MULT = 'T13' AND COD_ELEM = '0008';

-- HEREDERO
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'APODERADO', VALOR2 = '1', VALOR3 = '0003' WHERE COD_MULT = 'T13' AND COD_ELEM = '0009';
UPDATE VISPOD.TIIVS_MULTITABLA SET VALOR1 = 'DERECHO PROPIO', VALOR2 = '1', VALOR3 = '0003' WHERE COD_MULT = 'T13' AND COD_ELEM = '0010';

-- ELIMINACI�N DE REGISTROS INACTIVOS
DELETE FROM VISPOD.TIIVS_MULTITABLA WHERE COD_MULT = 'T13' AND COD_ELEM = '0011';
DELETE FROM VISPOD.TIIVS_MULTITABLA WHERE COD_MULT = 'T13' AND COD_ELEM = '0012';


/* AGREGAR SCRIPT DE OPERACIONES BANCARIAS CON EL EXCEL DE PARAMETRIA */
ALTER TABLE VISPOD.TIIVS_SOLICITUD_OPERBAN DISABLE CONSTRAINT TIIVS_SOLICITUD_OPERBAN_T_FK2;
DELETE FROM VISPOD.TIIVS_OPERACION_BANCARIA;
ALTER TABLE VISPOD.tiivs_operacion_bancaria ADD TIPO VARCHAR2(50) NOT NULL;

Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000001','RESCATE DE FONDOS MUTUOS','1','DINERARIA');
Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000002','PAGO ELECTR�NICO','1','DINERARIA');
Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000003','SUBSIDIOS DE ESSALUD','1','DINERARIA');
Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000004','REGISTRO EN EL SISTEMA DE APODERADOS','1','DINERARIA');
Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000005','CREDITO HIPOTECARIO','1','DINERARIA');
Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000006','SOCIEDAD NOFORMALIZADA (NO INSCRITA)','1','NO DINERARIA');
Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000007','CAMBIO DE NOMBRE','1','NO DINERARIA');
Insert into VISPOD.TIIVS_OPERACION_BANCARIA (COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO) values ('0000008','INFORMACION DE DATOS PERSONALES LEY NRO 27933','1','NO DINERARIA');

COMMIT;

ALTER TABLE VISPOD.TIIVS_SOLICITUD_OPERBAN ENABLE CONSTRAINT TIIVS_SOLICITUD_OPERBAN_T_FK2;


/*** SEGUNDO ENTREGABLE ***/
-- AGREGAR UN NUEVO REGISTRE QUE IDENTIFIQUE LA VISIBILIDAD DEL CAMPO "EXONERA PAGO DE COMISION"
DELETE FROM VISPOD.TIIVS_MULTITABLA WHERE COD_MULT = 'T17';
INSERT INTO VISPOD.TIIVS_MULTITABLA (COD_MULT, COD_ELEM, VALOR1, VALOR2, VALOR3) VALUES ('T17', '0001', 'Exonerado de Pago de comisi�n', '1', 'Esta es una glosa');

-- SOLICITUD
ALTER TABLE VISPOD.TIIVS_SOLICITUD DROP COLUMN EXONERA_COMISION;
ALTER TABLE VISPOD.TIIVS_SOLICITUD ADD EXONERA_COMISION VARCHAR2(1);


/*** TERCER ENTREGABLE ***/
-- CREACION DE NUEVA TABLA: DATOS VOUCHER EN HOST
DROP TABLE VISPOD.TTIVS_HOST_VOUCHER;
CREATE TABLE VISPOD.TTIVS_HOST_VOUCHER(
   NRO_VOUCHER VARCHAR2(12), 
   TIPO_PAGO VARCHAR2(25), 
   CUENTA VARCHAR2(25), 
   MONTO_COMISION VARCHAR2(15), 
   DIVISA VARCHAR2(20),  
   COD_SERVICIO VARCHAR2(100), 
   FECHA_PAGO VARCHAR2(10), 
   HORA_PAGO VARCHAR2(5), 
   CENTRO_PAGO  VARCHAR2(20), 
   TERMINAL_PAGO VARCHAR2(30), 
   USUARIO_PAGO VARCHAR2(8),
   USUARIO_REGISTRO VARCHAR2(7),
   FECHA_REGISTRO TIMESTAMP(6),
   ESTADO VARCHAR2(1)
 ) ;

ALTER TABLE VISPOD.TTIVS_HOST_VOUCHER ADD CONSTRAINT TIIVS_HOST_VOUCHER_PK PRIMARY KEY (NRO_VOUCHER) ENABLE;
ALTER TABLE VISPOD.TTIVS_HOST_VOUCHER MODIFY (NRO_VOUCHER NOT NULL ENABLE);
ALTER TABLE VISPOD.TTIVS_HOST_VOUCHER MODIFY (USUARIO_REGISTRO NOT NULL ENABLE);
ALTER TABLE VISPOD.TTIVS_HOST_VOUCHER MODIFY (FECHA_REGISTRO NOT NULL ENABLE);

-- AGREGAR CAMPOS ALIAS_ARCHIVO PARA GUARDAR EL NOMBRE DE ARCHIVO DE REVOCADOS
ALTER TABLE VISPOD.TIIVS_REVOCADO DROP COLUMN ALIAS_ARCHIVO;
ALTER TABLE VISPOD.TIIVS_REVOCADO ADD ALIAS_ARCHIVO VARCHAR2(50);


-- CONFIGURACION DE VALIDACIONES DE COBRO DE COMISION
DELETE FROM VISPOD.TIIVS_MULTITABLA WHERE COD_MULT = 'T18';
INSERT INTO VISPOD.TIIVS_MULTITABLA (COD_MULT, COD_ELEM, VALOR1, VALOR2) VALUES ('T18', '0001', 'Inactiva', '1');
INSERT INTO VISPOD.TIIVS_MULTITABLA (COD_MULT, COD_ELEM, VALOR1, VALOR2, VALOR3) VALUES ('T18', '0002', 'Activa como advertencia', '0', 'Existe diferencia entre monto Host y valor de la solicitud: ');
INSERT INTO VISPOD.TIIVS_MULTITABLA (COD_MULT, COD_ELEM, VALOR1, VALOR2, VALOR3) VALUES ('T18', '0003', 'Activa como restrictiva', '0', 'Existe diferencia entre monto Host y valor de la solicitud: ');

-- CONFIGURACION DE TABLAS PARAMETRO
DELETE FROM VISPOD.TTIVS_PARAMETROS;
insert into VISPOD.TTIVS_PARAMETROS (ID_PARAM,ID_EMPRESA,ID_SISTEMA,RUTA_FICHEROS_LOCAL,SERVER,LOGIN_SERVER,PASS_SERVER,CARPETA_REMOTA,URL_APP_SCAN,COD_USUARIO,RUTA_ARCHIVO_EXPORTAR_EXCEL) 
values 
('1','1','98','D:\VisadoPoderes\Escaneados','118.180.36.125','ftpvispod','ftpvispod','/mnt/compartido/visado/documentos/','http://tbalanceador/NAEWeb/pages/escaner/InvocaEscaner.xhtml','P014773','D:\VisadoPoderes\');

-- CONFIGURACION DE ESTADOS PARA MOSTRAR EN COMBO DE ESTADOS EN LA BANDEJA DE SOLICITUDES
UPDATE VISPOD.TIIVS_MULTITABLA
SET VALOR3 = 'F'
WHERE COD_MULT = 'T02'
AND COD_ELEM NOT IN ('0014', '0016', '0018', '0019', '0020');

--[23-10] SE AGREGA NUEVO REGISTRO PARA HABILITAR EL DICTAMEN PARA EL ABOGADO CUANDO
--ESTA EN ESTADO 'EN REVISION'.
INSERT INTO VISPOD.TIIVS_MULTITABLA (COD_MULT, COD_ELEM, VALOR1, VALOR2) VALUES ('T19', '0001', 'Habilitar dictaminacion Abogado en estado (En Revision)', '1');
INSERT INTO VISPOD.TIIVS_MULTITABLA (COD_MULT, COD_ELEM, VALOR1, VALOR2) VALUES ('T20', '0001', 'Habilitar Ver Datos Voucher', '1')

--[28-10]Mejora: Se agrega ordenamiento en el combo de Tipos Documentos (valor5)
UPDATE VISPOD.TIIVS_MULTITABLA SET valor5=null WHERE COD_MULT='T04';
COMMIT;
UPDATE VISPOD.TIIVS_MULTITABLA SET valor5=1 WHERE COD_MULT='T04' AND cod_elem='0016';
UPDATE VISPOD.TIIVS_MULTITABLA SET valor5=2 WHERE COD_MULT='T04' AND cod_elem='0003';



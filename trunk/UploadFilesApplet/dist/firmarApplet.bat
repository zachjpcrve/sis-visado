REM ---- Aqui inicializamos algunas variables para la configuracion.
@echo off
REM : Nombre de la llave
set SIGN_NAME=keyfin
REM : Clave keystore
set PASS=javajava
REM : Algunos valores para la descripcion del certificado
set CN=BBVA
set OU=Gestion Demanda
set O=BBVA Continental
set L=Lima
set S=Lima
set C=PE
REM : Periodo de validez certificado (Ejm: 9999 dias)
set VALIDEZ=9999
REM : Nombre de certificado de aplicacion
set NOMB_CERT=certifVisPod.crt
REM : Nombre del JAR de la aplicacion Visado
set NOMB_JAR=uploadFilesApplet.jar


echo ====== INICIAR CMD VISADO PODERES =================
d:
echo ====== Setear el PATH JAVA
path = C:\Archivos de programa\Java\jdk1.6.0_22\bin

pause


echo ====== CONFIGURACION APPLET - VISADO PODERES =================
echo = ----------- GENERACION DE LLAVE --------------
keytool -genkey -alias %SIGN_NAME% -validity %VALIDEZ% -dname "CN=%CN%, OU=%OU%, O=%O%, L=%L%, S=%S%, C=%C%" -v -keystore %PASS% -storepass %PASS% -keypass %PASS%
pause
echo = ------------ EXPORTAR CERTIFICADO ------------
keytool -export -alias %SIGN_NAME% -file %NOMB_CERT% -keystore %PASS% -storepass %PASS%
pause
echo = ------------ FIRMADO DEL ARCHIVO JAR -----------------
jarsigner -keystore %PASS% -storepass %PASS% -keypass %PASS% %NOMB_JAR% %SIGN_NAME% 
pause
echo = ----------- VERIFICACION DE FIRMA ------------
jarsigner -verify -verbose -certs %NOMB_JAR%
echo = -------- El proceso de firmado ha concluido ----
pause

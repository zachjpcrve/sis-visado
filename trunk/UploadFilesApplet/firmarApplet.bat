@echo off
REM --- Para firmar todos los JAR's. Si alguna vez hubiere mas librerias
REM --- agregarlas aqui, teniendo en cuenta la sintaxis adecuada.

REM : Nombre de la llave
set SIGN_NAME=bbva-applet
REM : Clave keystore 
set PASS=javajava

rem set JAVA_PATH=C:\Program Files\Java\jdk1.6.0_32\bin

rem set NOMB_JAR=D:\HDO\NetBeansProjects\firmadoapplet\ConfiguraMA-\uploadFilesApplet.jar
rem set NOMB_JAR=D:\HDO\NetBeansProjects\firmadoapplet\ConfiguraMA-\UploadFileApplet.jar

rem set NOMBRE_LIB=D:\HDO\NetBeansProjects\firmadoapplet\ConfiguraMA-\lib\commons-net-3.2.jar

echo ========================================
echo = FIRMADO DE JARS - VMA
echo ========================================
jarsigner -storepass %PASS% -keypass %PASS% -keystore %PASS% uploadFilesApplet.jar %SIGN_NAME%
rem "%JAVA_PATH%\jarsigner" -storepass %PASS% -keypass %PASS% -keystore %PASS% "%NOMBRE_LIB%" %SIGN_NAME%
echo ============ Proceso de firmado concluido ==========
pause
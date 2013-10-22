/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hildebrando.visado.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.hildebrando.visado.util.Constantes;
import com.hildebrando.visado.util.Utiles;

/**
 *
 * @author Hildebrando
 */
public class ClienteFTP { 

    private String username, password, host, directorio;
    private boolean login;
    FTPClient ftpCliente;

    /*public ClienteFTP(String host, String username, String password) {
        this.username = username;
        this.password = password;
        this.host = host;
        ftpCliente = new FTPClient();

        try {
            ftpCliente.connect(this.host);
            this.login = ftpCliente.login(this.username, this.password);
            if (this.login) {
                System.out.println("Login success...");
            } else {
                System.out.println("Failure success...");
            }
        } catch (IOException e) {
            System.out.println("Error al conectarse al FTP debido a: " + e.getMessage());
            e.printStackTrace();
        }
    }*/
    
    public ClienteFTP(String host, String username, String password) {
        this.username = username;
        this.password = password;
        this.host = host;
        ftpCliente = new FTPClient();        
    }
    
    public ClienteFTP(String host, String username, String password, String directorio) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.directorio = directorio;
        ftpCliente = new FTPClient();        
    }

    public void setDirectorio(String directorio) throws IOException {
        ftpCliente.changeWorkingDirectory(directorio);
    }

    

    public void upLoadFiles(String file, String ruta) {
    	Utiles.escribirEnLog(Constantes.INFO,"== upLoadFiles(f,s):Inicio ==", "");
    	FileInputStream fis = null;
        try {
        	Utiles.escribirEnLog(Constantes.INFO,"\tArchivo:"+file+"  Ruta:"+ruta, "");
        	fis = new FileInputStream(file);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            //ftpCliente.setBufferSize(1024 * 1024);
            
            ftpCliente.enterLocalPassiveMode();
            ftpCliente.storeFile(file, fis);
        } catch (IOException e) {
        	Utiles.escribirEnLog(Constantes.ERROR,Constantes.MSJ_OCURRE_EXCEPCION+"IO en upLoadFiles:"+e, "");
        } catch(Exception exc){
        	Utiles.escribirEnLog(Constantes.ERROR,Constantes.MSJ_OCURRE_EXCEPCION+"en upLoadFiles:"+exc, "");
        } 
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                // ftpCliente.disconnect();
            } catch (IOException e) {
            	Utiles.escribirEnLog(Constantes.ERROR,Constantes.MSJ_OCURRE_EXCEPCION+"IO en upLoadFiles:"+e, "");
            }
        }
        Utiles.escribirEnLog(Constantes.INFO,"== upLoadFiles(f,s):Fin ==", "");
    }
    
  //Metodo que se agrega para subir archivos por FTP
    public String uploadFiles(List<File> listaFile){
    	Utiles.escribirEnLog(Constantes.INFO,"\t===== uploadFiles(l):Inicio =====", "");
    	String archivosSubidos = "";
    	Integer contador = 0;
    	String nombreTemporal = null;
    	try {
            ftpCliente.connect(this.host);
            this.login = ftpCliente.login(this.username, this.password);
            if (this.login) {
            	//Utiles.escribirEnLog(Constantes.DEBUG,"\t[uploadFiles]-Login exitoso.", "");
            	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-directorio: "+directorio, "");
                //04-10-2013 
            	//TEST: El directorio es: '/files/'
            	//PROD: El directorio es: '/documentos/files/'
            	
                boolean resCambDirect = ftpCliente.changeWorkingDirectory(directorio);
                Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-resultado setDirectorio: "+resCambDirect, "");
                Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-ftpCliente.printWorkingDirectory: "+ftpCliente.printWorkingDirectory(), "");
                if(listaFile!=null){
                	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-Tamanhio listaArchivos:"+listaFile.size(), "");
                }
                for(File file:listaFile){
                	contador++;
                	FileInputStream fis = new FileInputStream(file);
                	ftpCliente.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                	nombreTemporal= createNameFile(file);
                	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-nombreTemporal:" + nombreTemporal, "");
                	boolean res = ftpCliente.storeFile(nombreTemporal, fis );
                	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-resultado:" + res, "");
                	fis.close();
                	
                	if(contador<listaFile.size()){
                		archivosSubidos = archivosSubidos + nombreTemporal + Constantes.SEPARADOR;
                	}else{
                		archivosSubidos = archivosSubidos + nombreTemporal;
                	}
                }
            } else {
            	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-"+Constantes.MSJ_OCURRE_EXCEPCION+"al intentar conectarse al FTP", "");
            }
        } catch (IOException e) {
        	Utiles.escribirEnLog(Constantes.ERROR,"\t[uploadFiles]-"+Constantes.MSJ_OCURRE_EXCEPCION+"IO al conectarse al FTP debido a: " + e, "");
        } catch (Exception ex) {
        	Utiles.escribirEnLog(Constantes.ERROR,"\t[uploadFiles]-"+Constantes.MSJ_OCURRE_EXCEPCION+"general al conectarse al FTP debido a: " + ex, "");
        } 
    	finally{
        	try {
        		if(ftpCliente!=null){
        			ftpCliente.logout();
    				ftpCliente.disconnect();	
        		}
			} catch (IOException e) {
				Utiles.escribirEnLog(Constantes.ERROR,"\t[uploadFiles]-Fallo al desconectar FTP (IO):"+e, "");
			} catch(Exception e){
				Utiles.escribirEnLog(Constantes.ERROR,"\t[uploadFiles]-Fallo al desconectar FTP:"+e, "");
			}
        }
    	Utiles.escribirEnLog(Constantes.INFO,"\t===== uploadFiles(l):Fin =====", "");
    	return archivosSubidos;
    }
    
    private String createNameFile(File file){
    	if(file!=null){
    		long fecha = new Date().getTime();
    		String nombreArchivo = file.getName().substring(0, file.getName().indexOf("."));
    		String extension = file.getName().substring(file.getName().lastIndexOf("."));
    		String nombre =  nombreArchivo + "_" + fecha + extension;
    		return nombre;
    	}else{
    		Utiles.escribirEnLog(Constantes.DEBUG, "[createNameFile]-El archivo es nulo", "");
    	}
    	return null;
    }
    
    public void renombrarArchivo(String nuevoNombre, String antiguoNombre) {
    	Utiles.escribirEnLog(Constantes.DEBUG, "=== renombrarArchivo:Inicio ===", "");
    	try {
    		Utiles.escribirEnLog(Constantes.DEBUG, "[Nombre]-Antiguo:"+antiguoNombre + "   Nuevo:"+nuevoNombre, "");
        	ftpCliente.rename(antiguoNombre, nuevoNombre);
            ftpCliente.disconnect();
        } catch (IOException e) {
            Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_EXCEPCION+"IO al renombrarArchivo: "+e, "");
        } catch(Exception exc){
        	Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_EXCEPCION+" al renombrarArchivo: "+exc, "");
        }
    	Utiles.escribirEnLog(Constantes.DEBUG, "== renombrarArchivo:Fin ==", "");
    }
    
    public void upLoadOneFiles(String file, String ruta) {
    	Utiles.escribirEnLog(Constantes.DEBUG, "==== upLoadOneFiles():Inicio ====", "");
    	FileInputStream fis = null;
    	Utiles.escribirEnLog(Constantes.DEBUG, "[upLoadOneFiles]-Ruta: " + ruta, "");
        try {
            fis = new FileInputStream(ruta);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setBufferSize(4096);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpCliente.storeFile(file, fis);
            Utiles.escribirEnLog(Constantes.INFO, "[upLoadOneFiles]-file:"+file, "");
        } catch (IOException e) {
            e.printStackTrace();
            Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_EXCEPCION+"IO en upLoadOneFiles(f,r): "+e, "");
        }catch(Exception ex){
        	ex.printStackTrace();
        	Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_EXCEPCION+" en upLoadOneFiles(f,r): "+ex, "");
        } 
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                //ftpCliente.disconnect();
            } catch (IOException e2) {
                Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_EXCEPCION+" en upLoadOneFiles(f,r) finally: "+e2, "");
            }
        }
        Utiles.escribirEnLog(Constantes.DEBUG, "==== upLoadOneFiles():Fin ====", "");
    }
    
    public void disconnect(){
        if (this.ftpCliente.isConnected()) {
            try {
                this.ftpCliente.logout();
                this.ftpCliente.disconnect();
            } catch (IOException f) {
            	Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_ERROR+"al desconectar el ftpCliente", "");
            }
        }
    }
}

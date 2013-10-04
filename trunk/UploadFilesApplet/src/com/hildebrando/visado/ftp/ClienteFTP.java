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
    	Utiles.escribirEnLog(Constantes.INFO,"== upLoadFiles(f,s):Inicio ==", null);
    	FileInputStream fis = null;
        try {
        	System.out.println("\tArchivo:"+file+"  Ruta:"+ruta);
            fis = new FileInputStream(file);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpCliente.enterLocalPassiveMode();
            ftpCliente.storeFile(file, fis);
        } catch (IOException e) {
        	Utiles.escribirEnLog(Constantes.ERROR,Constantes.MSJ_OCURRE_EXCEPCION+"IO en upLoadFiles:"+e, null);
        } catch(Exception exc){
        	Utiles.escribirEnLog(Constantes.ERROR,Constantes.MSJ_OCURRE_EXCEPCION+"en upLoadFiles:"+exc, null);
        } 
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                // ftpCliente.disconnect();
            } catch (IOException e) {
            	Utiles.escribirEnLog(Constantes.ERROR,Constantes.MSJ_OCURRE_EXCEPCION+"IO en upLoadFiles:"+e, null);
            }
        }
        Utiles.escribirEnLog(Constantes.INFO,"== upLoadFiles(f,s):Fin ==", null);
    }
    
  //Metodo que se agrega para subir archivos por FTP
    public String uploadFiles(List<File> listaFile){
    	Utiles.escribirEnLog(Constantes.INFO,"\t===== uploadFiles(l):Inicio =====", null);
    	String archivosSubidos = "";
    	Integer contador = 0;
    	String nombreTemporal = null;
    	try {
            ftpCliente.connect(this.host);
            this.login = ftpCliente.login(this.username, this.password);
            if (this.login) {
            	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-Login exitoso", null);
            	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-directorio:"+directorio, null);
                
                boolean resCambDirect = ftpCliente.changeWorkingDirectory(directorio);
                Utiles.escribirEnLog(Constantes.INFO,"resCambDirect:"+resCambDirect, null);
                
                if(listaFile!=null){
                	System.out.println("\t[uploadFiles]-listaSize:"+listaFile.size());
                }
                for(File file:listaFile){
                	contador++;
                	FileInputStream fis = new FileInputStream(file);
                	ftpCliente.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                	nombreTemporal= createNameFile(file);
                	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-nombreTemporal:" + nombreTemporal, null);
                	boolean res = ftpCliente.storeFile(nombreTemporal, fis );
                	Utiles.escribirEnLog(Constantes.INFO,"\t[uploadFiles]-resultado:" + res, null);
                	fis.close();
                	
                	if(contador<listaFile.size()){
                		archivosSubidos = archivosSubidos + nombreTemporal + Constantes.SEPARADOR;
                	}else{
                		archivosSubidos = archivosSubidos + nombreTemporal;
                	}
                }
            } else {
                System.out.println("\t[uploadFiles]-No se pudo loguear correctamente");
            }
        } catch (IOException e) {
            System.out.println("\t[uploadFiles]-Error al conectarse al FTP debido a: " + e.getMessage());
            e.printStackTrace();
        } finally{
        	try {
        		if(ftpCliente!=null){
        			ftpCliente.logout();
    				ftpCliente.disconnect();	
        		}
			} catch (IOException e) {
				System.out.println("\t[uploadFiles]-Fallo al desconectar FTP:"+e);
			}
        }
    	System.out.println("\t===== uploadFiles(l):Fin ====="); 
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
    		System.out.println("[createNameFile]-El archivo es nulo");
    	}
    	return null;
    }
    
    public void renombrarArchivo(String nuevoNombre, String antiguoNombre) {
    	System.out.println("=== renombrarArchivo:Inicio ===");
        try {
        	System.out.println("[Nombre]-Antiguo:"+antiguoNombre + "   Nuevo:"+nuevoNombre);
            ftpCliente.rename(antiguoNombre, nuevoNombre);
            ftpCliente.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ha ocurrido una IOException en renombrarArchivo:"+e);
        } catch(Exception exc){
        	exc.printStackTrace();
        	System.out.println("Ha ocurrido una Exception en renombrarArchivo:"+exc);
        }
        System.out.println("== renombrarArchivo:Fin ==");
    }
    
    public void upLoadOneFiles(String file, String ruta) {
    	System.out.println("==== upLoadOneFiles():Inicio ====");
        FileInputStream fis = null;
        System.out.println("[upLoadOneFiles]-Ruta: " + ruta);
        try {
            fis = new FileInputStream(ruta);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setBufferSize(4096);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpCliente.storeFile(file, fis);
            System.out.println("[upLoadOneFiles]-file:"+file);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[upLoadOneFiles]-Ha ocurrido un IOException:"+e);
        }catch(Exception ex){
        	ex.printStackTrace();
        	System.out.println("[upLoadOneFiles]-Ha ocurrido una Exception:"+ex);
        } 
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
//               ftpCliente.disconnect();
            } catch (IOException e2) {
                e2.printStackTrace();
                System.out.println("[upLoadOneFiles]-Ha ocurrido un IOException e2:"+e2);
            }
        }
        System.out.println("==== upLoadOneFiles():Fin ====");
    }
}

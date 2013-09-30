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
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpCliente.enterLocalPassiveMode();
            ftpCliente.storeFile(file, fis);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                // ftpCliente.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //Metodo que se agrega para subir archivos por FTP
    public String uploadFiles(List<File> listaFile){
    	System.out.println("********uploadFiles:Inicio*************"); 
    	String archivosSubidos = "";
    	Integer contador = 0;
    	String nombreTemporal = null;
    	try {
            ftpCliente.connect(this.host);
            this.login = ftpCliente.login(this.username, this.password);
            if (this.login) {
                System.out.println("Login success...");
                ftpCliente.changeWorkingDirectory(directorio);
                for(File file:listaFile){
                	contador++;
                	FileInputStream fis = new FileInputStream(file);
                	ftpCliente.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                	nombreTemporal= createNameFile(file);
                	boolean res = ftpCliente.storeFile(nombreTemporal, fis );
                	System.out.println("Resultado de subir el archivo es " + res);
                	fis.close();
                	
                	if(contador<listaFile.size()){
                		archivosSubidos = archivosSubidos + nombreTemporal + Constantes.SEPARADOR;
                	}else{
                		archivosSubidos = archivosSubidos + nombreTemporal;
                	}
                }
            } else {
                System.out.println("Failure success...");
            }
        } catch (IOException e) {
            System.out.println("Error al conectarse al FTP debido a: " + e.getMessage());
            e.printStackTrace();
        } finally{
        	try {
        		if(ftpCliente!=null){
        			ftpCliente.logout();
    				ftpCliente.disconnect();	
        		}
			} catch (IOException e) {
				System.out.println("+++ Fallo al desconectar FTP");
			}
        }
    	System.out.println("********uploadFiles:Fin*************"); 
    	return archivosSubidos;
    }
    
    private String createNameFile(File file){
    	if(file!=null){
    		long fecha = new Date().getTime();
    		String nombreArchivo = file.getName().substring(0, file.getName().indexOf("."));
    		String extension = file.getName().substring(file.getName().lastIndexOf("."));
    		String nombre =  nombreArchivo + "_" + fecha + extension;
    		return nombre;
    	}
    	return null;
    }
    
    public void renombrarArchivo(String nuevoNombre, String antiguoNombre) {
        try {
            ftpCliente.rename(antiguoNombre, nuevoNombre);
            ftpCliente.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void upLoadOneFiles(String file, String ruta) {
        FileInputStream fis = null;
        System.out.println("Ruta: " + ruta);
        try {
            fis = new FileInputStream(ruta);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setBufferSize(4096);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpCliente.storeFile(file, fis);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
//               ftpCliente.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

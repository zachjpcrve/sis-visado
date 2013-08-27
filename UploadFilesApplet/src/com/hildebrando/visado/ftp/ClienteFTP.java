/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hildebrando.visado.ftp;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * Clase que se encarga de abrir una conexión FTP para el UpLoad de
 * archivos. También contempla renombrar archivos y seteo de directorio
 * de trabajo para los documentos. 
 * @author Hildebrando
 */
public class ClienteFTP {

    private String username, password, host;
    private boolean login;
    FTPClient ftpCliente;

    public ClienteFTP(String host, String username, String password) {
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
    }

    public void setDirectorio(String directorio) throws IOException {
        ftpCliente.changeWorkingDirectory(directorio);
    }

    

    public void upLoadFiles(String file, String ruta) {
    	System.out.println("== upLoadFiles:Inicio ==");
        FileInputStream fis = null;
        try {
        	System.out.println("\tArchivo:"+file+"  Ruta:"+ruta);
            fis = new FileInputStream(file);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpCliente.enterLocalPassiveMode();
            ftpCliente.storeFile(file, fis);

        } catch (IOException e) {
        	System.out.println("Ha ocurrido una IOException en upLoadFiles:"+e);
            e.printStackTrace();
        } catch(Exception exc){
        	System.out.println("Ha ocurrido una Exception en upLoadFiles:"+exc);
        } 
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                // ftpCliente.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ha ocurrido una IOException en upLoadFiles:"+e);
            }
        }
    }
    
    public void renombrarArchivo(String nuevoNombre, String antiguoNombre) {
    	System.out.println("== renombrarArchivo:Inicio ==");
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
        FileInputStream fis = null;
        System.out.println("[UploadFT]-Ruta: " + ruta);
        try {
            fis = new FileInputStream(ruta);
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            ftpCliente.setBufferSize(4096);
            ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpCliente.storeFile(file, fis);
            System.out.println("[UploadFT]-file:"+file);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ha ocurrido un IOException-upLoadOneFiles:"+e);
        }catch(Exception ex){
        	ex.printStackTrace();
        	System.out.println("Ha ocurrido una Exception-upLoadOneFiles:"+ex);
        } 
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
//               ftpCliente.disconnect();
            } catch (IOException e2) {
                e2.printStackTrace();
                System.out.println("Ha ocurrido un IOException e2-upLoadOneFiles:"+e2);
            }
        }
    }
}

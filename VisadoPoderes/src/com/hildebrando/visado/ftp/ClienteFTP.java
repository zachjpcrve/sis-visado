package com.hildebrando.visado.ftp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class ClienteFTP 
{
	private String username, password, host ;
	private boolean login;
	FTPClient ftpCliente;
	
	public ClienteFTP(String host,String username,String password)
	{
        this.username = username;
        this.password = password;
        this.host     = host;
        ftpCliente = new FTPClient();
        
        try {
        		ftpCliente.connect(this.host);
        		this.login=ftpCliente.login(this.username, this.password);
        		if(this.login)
        		{
        			System.out.println("Login success...");
        		}
        		else
        		{
        			System.out.println("Failure success...");
        		}
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        }
	}

	public void setDirectorio(String directorio) throws IOException 
	{
		ftpCliente.changeWorkingDirectory(directorio);
	}


	public List listar() throws IOException
	{
		List lista= new ArrayList(); 
		
		if(this.login)
		{ 			
			FTPFile[] ftpFiles = ftpCliente.listFiles();
		    for (FTPFile ftpFile : ftpFiles) 
		    {
		    	if (ftpFile.getType() == FTPFile.FILE_TYPE) 
		    	{
		    		lista.add(ftpFile.getName()); //agregamos a la lista emlementos
			    } 
                
		    }
		}
		else
		      System.out.println("No logeado...");
		return lista;	
	}
	
	public void upLoadFiles(String file,String ruta)
	{
		FileInputStream fis =null;
		
		try 
		{
			fis = new FileInputStream(ruta+file);
			ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
			ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			ftpCliente.storeFile(file, fis);
			
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
            try 
            {
                if (fis != null) 
                {
                    fis.close();
                }
               // ftpCliente.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void upLoadOneFiles(String file,String ruta)
	{
		FileInputStream fis =null;
		System.out.println("Ruta: " + ruta);
		try 
		{
			fis = new FileInputStream(ruta);
			ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
			ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);			
			ftpCliente.storeFile(file, fis);
			
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
            try 
            {
                if (fis != null) 
                {
                    fis.close();
                }
//               ftpCliente.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void renombrarArchivo(String nuevoNombre, String antiguoNombre)
	{
		try {
			ftpCliente.rename(antiguoNombre, nuevoNombre);
			ftpCliente.disconnect();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
}

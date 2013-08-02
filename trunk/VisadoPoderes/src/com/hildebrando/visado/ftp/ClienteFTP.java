package com.hildebrando.visado.ftp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.bbva.common.util.ConstantesVisado;

public class ClienteFTP 
{
	private String username, password, host ;
	private boolean login;
	FTPClient ftpCliente;
	public static Logger logger = Logger.getLogger(ClienteFTP.class);
	
	/**
	 * Metodo que se encarga de realizar la conexion y login al servidor FTP
	 * @param host Servidor FTP
	 * @param username Usuario de conexion 
	 * @param password Clave
	 * */
	public ClienteFTP(String host,String username,String password)
	{
        this.username = username;
        this.password = password;
        this.host     = host;
        ftpCliente = new FTPClient();
        
        try {
        		ftpCliente.connect(this.host);
        		this.login = ftpCliente.login(this.username, this.password);
        		if(this.login){
        			logger.debug("Login exitoso");
        		}
        		else{
        			logger.debug("Login fallido.");
        		}
        } catch (IOException e) {
        	logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al conectarse al FTP debido a: "+e);
        }
	}

	/**
	 * Metodo que se encarga para establecer el directorio remoto de documentos
	 * @param directorio Carpeta para guardar los documentos escaneados
	 * * */
	public void setDirectorio(String directorio) throws IOException 
	{
		try{
			if(directorio!=null && !directorio.equals("")){
				logger.debug("[FTP]-setDirectorio:"+directorio);
				boolean result = ftpCliente.changeWorkingDirectory(directorio); 
				
				//logger.debug("[FTP]-resultado cambio directorio: "+result);
				if(result==false){
					result = ftpCliente.makeDirectory(directorio);
					logger.debug("[FTP]-Resultado Creacion directorio: "+result);
					ftpCliente.changeWorkingDirectory(directorio);
				}		
			}
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al setDirectorio: "+e);		
		}
	}


	public List listar() throws IOException
	{
		logger.debug("=== listar() ===");
		List lista= new ArrayList(); 
		try{
			if(this.login)
			{ 			
				FTPFile[] ftpFiles = ftpCliente.listFiles();
			    for (FTPFile ftpFile : ftpFiles) 
			    {
			    	if (ftpFile.getType() == FTPFile.FILE_TYPE) 
			    	{
			    		lista.add(ftpFile.getName()); //agregamos a la lista elementos
				    } 
	                
			    }
			}
			else
			{   
				logger.debug("No logeado...");
			}
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al listar(): "+e);
		}		
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
			ftpCliente.enterLocalPassiveMode();
			ftpCliente.storeFile(file, fis);
			
		} catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try 
            {
                if (fis != null) 
                {
                    fis.close();
                }
               // ftpCliente.disconnect();
            } catch (IOException e) {
            	logger.error(e);
                e.printStackTrace();
            }
        }
	}
	
	
  /** Metodo que se encarga de la subida de archivos via FTP al FileServer
	* @param ruta Directorio fuente desde donde se enviará el archivo
	* @param file Nombre de archivo a ser transferido via FTP  **/
	
	public void upLoadOneFiles(String file,String ruta)
	{
		logger.debug("=== iniciando upLoadOneFiles() ===");
		FileInputStream fis =null;
		logger.debug("[upLoad]-Ruta:" + ruta);
		logger.debug("[upLoad]-Archivo: "+file);
		try 
		{
			fis = new FileInputStream(ruta);
			ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
			ftpCliente.setBufferSize(4096);
			ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			ftpCliente.storeFile(file, fis);
			logger.debug("[upLoad]-storeFile");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"(IOException):"+e);
        }catch(Exception e1){
        	logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al subir archivos al ftp:"+e1);
        	e1.printStackTrace();
        } 
		finally {
            try 
            {
                if (fis != null) 
                {
                    fis.close();
                }
                //ftpCliente.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"IOException:"+e);
            }
        }
		logger.debug("=== saliendo de upLoadOneFiles() ===");
	}
	
	/** Metodo que se encarga de renombrar un archivo
	* @param nuevoNombre Nombre renombrado nuevo
	* @param antiguoNombre Nombre a renombrar  **/
	
	public void renombrarArchivo(String nuevoNombre, String antiguoNombre)
	{
		logger.debug("==== renombrarArchivo() ====");		
		try {
			logger.debug("[Renombrar]- nuevoNombre: "+nuevoNombre + "   antiguoNombre:"+antiguoNombre);
			ftpCliente.rename(antiguoNombre, nuevoNombre);
			ftpCliente.disconnect();
		} catch (IOException e) {
            e.printStackTrace();
            logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"IOException al renombrarArchivo:"+e);
        }catch(Exception e1){
        	logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al renombrarArchivo:"+e1);
        } 
	}

	public void deleteFiles(List<String> aliasArchivos) {
		logger.debug("==== deleteFiles() ===");
		try {
			if(aliasArchivos!=null){
				logger.debug("Numero de ficheros a eliminar:"+aliasArchivos.size());
			}
			for(String archivo : aliasArchivos){
				logger.debug("[Delete]-fichero: " + archivo);
				ftpCliente.deleteFile(archivo);
			}
			ftpCliente.disconnect();
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"IOException al deleteFiles:"+e);
		} catch(Exception e1){
        	logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al deleteFiles:"+e1);
        } 
	}

	public void renombrarArchivos(List<String> fromFicheros,
			List<String> toFicheros) {
		try {	
			logger.debug("Numero de ficheros a renombrar:"+fromFicheros.size());
			for(int i=0;i<fromFicheros.size();i++){
				String fromFichero = fromFicheros.get(i);
				String toFichero = toFicheros.get(i);
				logger.debug("Renombrando fichero:" + fromFichero + " a " + toFichero);
				ftpCliente.rename(fromFichero, toFichero);
			}
			ftpCliente.disconnect();
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"IOException al renombrarArchivos:"+e);
        }catch(Exception e1){
        	logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al renombrarArchivos:"+e1);
        } 		
	}
	
	public boolean downloadFile(String rutaLocal, String rutaRemota){
		logger.debug("====== INICIA downloadFile() =======");
		FileOutputStream fos = null;		
		boolean iRet = true;
		try 
		{
			fos = new FileOutputStream(rutaLocal);		
						
			ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
			ftpCliente.setBufferSize(4096);
			ftpCliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			if(rutaLocal!=null){
				logger.debug("[RecuperarArchivo]-rutaLocal: "+rutaLocal);
			}
			if(rutaRemota!=null){
				logger.debug("[RecuperarArchivo]-rutaRemota: "+rutaRemota);
			}
			
			iRet = ftpCliente.retrieveFile(rutaRemota, fos);
			
		} catch (IOException e) {
			iRet = false;
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en downloadFile:"+e);
            e.printStackTrace();
        } catch(Exception e1){
        	iRet = false;
        	logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al retrieveFile:"+e1);
        }
		finally {
            try 
            {
                if (fos != null) 
                {
                	fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"en downloadFile-IOException:"+e);
            }
        }		
		logger.debug("[RecuperarArchivo]-iRet: "+iRet);
		
		logger.debug("====== SALIENDO de downloadFile() =======");
		return iRet;		
	}
}

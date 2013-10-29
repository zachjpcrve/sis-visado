/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hildebrando.visado.applet;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.hildebrando.visado.ftp.ClienteFTP;
import com.hildebrando.visado.http.HttpTransferFiles;
import com.hildebrando.visado.util.Constantes;
import com.hildebrando.visado.util.Utiles;

/**
 *
 * @author Hildebrando
 */
public class AgregarDocumentos extends JApplet {
    
    private static final long serialVersionUID = 1L;
    
    private static String documentosLeidos=""; 
    private static List<File> ficherosLeidos=null;           
    private ClienteFTP clienteFTP = null;
    
    
    public AgregarDocumentos(){
        try {
            initComponents();
        } catch (Exception e){
        	System.out.println(Constantes.MSJ_OCURRE_EXCEPCION+"en initComponents(): "+e);
        	e.printStackTrace();
        }
    }
    
    private void initComponents() {              
        setSize(100, 100);
        add(getCustPanel());
        setVisible(true);              
    }
    
    private JPanel getCustPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, 1));
        addButton("Actualizar", panel);
        return panel;
    }
    
    private void addButton(String text, JPanel container) {
        JButton button = new JButton(text);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actualizar(e);
            }
        });
        button.setPreferredSize(new Dimension(100, 20));
        button.setAlignmentX(0.5F);
        container.add(button);
    }
    
    /**
     * Metodo que es invocado desde el botón "Actualizar" de 
     * la aplicación por medio de Javascript. 
     * @param documento Nombre del documento
     */        
    public void abrirDocumento(String documento){
        System.out.println("********abrirDocumento:Inicio*************");
        System.out.println("Abrir documento:-" + documento+"-");
        if(ficherosLeidos!=null){
            System.out.println("Lista de archivos: " + ficherosLeidos.size());                        
            for(File file : ficherosLeidos){                
                if(file.getName().trim().equalsIgnoreCase(documento.trim())){
                    
                    System.out.println("Abriendo documento: " + file.getName());
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        new RuntimeException(ex);
                    }           
                    
                    /*
                    System.out.println("Confirma abrir documento: " + documento);
                    accion = Accion.ABRIR_DOCUMENTO;
                    ficheroAbrir = file;                    
                    */
                    break;                   
                }
            }
        } else {
            System.out.println("Lista de archivos Nulo ");
        }
        System.out.println("********abrirDocumento:Fin*************");
    }
    
    public void abrirDocumentoConPermisos(final String documento){
        AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                abrirDocumento(documento);
                return null;
            }
        });
    }
    
    
    public void sendToFTP(){        
        System.out.println("==== sendToFTP():Inicio ====");                     
        if(ficherosLeidos!=null){
            System.out.println("[sendToFTP]-Cantidad de ficheros a subir: " + ficherosLeidos.size());         
            for(File file : ficherosLeidos){
                subirArchivoFTP(file);
            }
        } else {
            System.out.println("Lista de archivos nulos. ");
        }
        System.out.println("==== sendToFTP():Fin ====");  
    }
  
    public String subirFTP(List<File> files){     
    	Utiles.escribirEnLog(Constantes.INFO, "==== subirFTP():INICIO =====","");
    	//if(ficherosLeidos!=null){
    	if(files!=null){
    		//Utiles.escribirEnLog(Constantes.INFO, "[SubirFTP]-Cantidad de ficheros a subir: " + ficherosLeidos.size(),"");
    		String host = getParameter("host");
            String user = getParameter("user");
            String pass = getParameter("pass");
            String urlTemporal = getParameter("urlTemporal");
            try {
            	 Utiles.escribirEnLog(Constantes.INFO, "[SubirFTP]-Host:" + host + " \tDatos:"+user+"/"+pass, "");
                 Utiles.escribirEnLog(Constantes.INFO, "[SubirFTP]-RutaTemp:" + urlTemporal, "");
                 clienteFTP = new ClienteFTP(host, user, pass, urlTemporal);
            	 return clienteFTP.uploadFiles(files);
			} catch (Exception e) {
				Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_ERROR+"al conectar via FTP " + e,"");
			}
        } else {
        	Utiles.escribirEnLog(Constantes.DEBUG, Constantes.MSJ_LISTA_ARCHIVOS+"(subirFTP) es nulo.","");
        }
        Utiles.escribirEnLog(Constantes.INFO, "==== subirFTP():FIN =====","");
        return null;
    }
    
    public void actualizar()  {
    	System.out.println("************ actualizar()-Inicio-v2 *********** "); 
    	System.out.println("[actualizar]-Fecha Inicio: "+Utiles.formatoFechaHora(new Date()));
    	String sNombreDocLeidos = "";
    	List<File> lstFicherosLeidos = null;
        try{
        	String sPathCliente = getParameter(Constantes.PATH_CLIENTE);
            String sDocumentosLeer = getParameter(Constantes.DOCUMENTOS_LEER);   
            System.out.println("[actualizar]-sPathCliente:"+sPathCliente);
            System.out.println("[actualizar]-sDocumentosLeer:"+sDocumentosLeer);
            String sDocumentosCargados = "";
            Archivo archivo = new Archivo();                
            if(archivo.obtenerListaFiles(sPathCliente, sDocumentosLeer) == 1){
                sNombreDocLeidos = archivo.getNombreFilesReaded();
                lstFicherosLeidos = archivo.getFilesReaded();
                
                System.out.println("[actualizar]-Documentos obtenidos: " + sNombreDocLeidos);
                if(lstFicherosLeidos!=null){
                	System.out.println("[actualizar]-Cantidad de archivos leidos : " + lstFicherosLeidos.size());	
                }
                
                documentosLeidos = sNombreDocLeidos;   
                ficherosLeidos = lstFicherosLeidos;    
                
                sDocumentosCargados = subirArchivoHTTP(ficherosLeidos);
                
                System.out.println("[actualizar]-Documentos cargados por HTTP:" + sDocumentosCargados);
                
                ejecutarFuncion("actualizarDocumentos", documentosLeidos,sDocumentosCargados);//ojo modificar java script
                System.out.println("[actualizar]-Se terminó de actualizar documentos.");
            }            
        }catch (Exception e) {
			System.out.println("[actualizar]-Ha ocurrido un error al actualizar documentos:"+e);
		}
        System.out.println("[Actualizar]-Fecha Fin: "+Utiles.formatoFechaHora(new Date()));
        System.out.println("************ actualizar()-Final *********** "); 
    }    
    
    public void actualizarFTP()  {   
        Utiles.escribirEnLog(Constantes.INFO, "===== actualizarFTP():INICIO =====", "");
        String sNombreDocLeidos = "";
        List<File> lstFicherosLeidos = null;
        String sPathCliente = getParameter(Constantes.PATH_CLIENTE);
        String sDocumentosLeer = getParameter(Constantes.DOCUMENTOS_LEER);   
        
        Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-[SIEMPRE]-sDocumentosLeer: " + sDocumentosLeer, "");
        
//        Integer contador = 0;
        String sDocumentosCargados = "";
        Archivo archivo = new Archivo();                
        if(archivo.obtenerListaFiles(sPathCliente, sDocumentosLeer) == 1){
        	Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-ListaDocs (App):" + sDocumentosLeer, "");
            sNombreDocLeidos = archivo.getNombreFilesReaded();
            lstFicherosLeidos = archivo.getFilesReaded();
            documentosLeidos = sNombreDocLeidos;   
            ficherosLeidos = lstFicherosLeidos;  
            
            Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-Directorio Fuente: " + sPathCliente, "");
            Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-Documentos obtenidos: "+sNombreDocLeidos, "");
            Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-Cantidad de archivos leidos :"+lstFicherosLeidos.size(), "");
           
            
            //Se valida que haya lstFicherosLeidos para enviar por FTP && lstFicherosLeidos.size()>0
            if(lstFicherosLeidos!=null ){            	 
            	sDocumentosCargados = subirFTP(lstFicherosLeidos);
            	Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-Archivos subidos por FTP: " + sDocumentosCargados, "");
            	/*String[] archivosLeidos = documentosLeidos.split(Constantes.SEPARADOR);
            	String[] archivosCargados = sDocumentosCargados.split(Constantes.SEPARADOR);*/
            	
            	/*for(int i=0; i<ficherosLeidos.size(); i++){
            		Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-archivoLeido:" + archivosLeidos[i], null);
            		Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-archivoCargado:" + archivosCargados[i], null);
            		ejecutarFuncion("actualizarDocumentos", archivosLeidos[i], archivosCargados[i]);//ojo modificar javascript
            	}*/
                ejecutarFuncion("actualizarDocumentos", documentosLeidos,sDocumentosCargados);//ojo modificar javascript
            }else{
            	Utiles.escribirEnLog(Constantes.INFO,"[actualizarFTP]-Es nulo o no hay ficheros:", "");
            }
            
            /*if(lstFicherosLeidos!=null){
            	String[] archivosLeidos = sNombreDocLeidos.split(Constantes.SEPARADOR);
            	Utiles.escribirEnLog(Constantes.INFO, "[SubirFTP]-Cantidad de ficheros a subir: " + ficherosLeidos.size(),"");
            	
				for (File file : lstFicherosLeidos) {
					sDocumentosCargados = subirFTP(obtenerFiles(file));
					Utiles.escribirEnLog(Constantes.INFO, "[actualizarFTP line 247]-Documentos cargados: " + sDocumentosCargados, "");
					if (sDocumentosCargados != null
							&& sDocumentosCargados.compareTo("") != 0) {
						ejecutarFuncion("actualizarDocumentos",
								archivosLeidos[contador], sDocumentosCargados);
						contador++;
						/*
						 * try { Utiles.escribirEnLog(Constantes.INFO,
						 * "[actualizarFTP]-Esperamos 5 segundos: ", "");
						 * Thread.sleep(5000); } catch (InterruptedException e)
						 * { Utiles.escribirEnLog(Constantes.ERROR,
						 * Constantes.MSJ_OCURRE_EXCEPCION
						 * +"al esperar ejecución javascript:"+e, "");
						 * e.printStackTrace(); }
						 */
				/*	}
				}
            }*/
        }else{
        	 Utiles.escribirEnLog(Constantes.INFO,"===== actualizarFTP()-es CERO =====", "");
        }
        Utiles.escribirEnLog(Constantes.INFO,"===== actualizarFTP():FIN =====", "");
    } 
    
    private List<File> obtenerFiles(File file){
    	List<File> files = new ArrayList<File>();
		files.add(file);
		return files;
    }
   
	public void actualizarConPermisos(){
        AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                actualizar();
                return null;
            }
        });
    }
        
	public void actualizarConPermisosFTP(){
        AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                actualizarFTP();
                return null;
            }
        });
    }
    
    public void cargarMultipleFTP(String codigoSolicitud){   
    	Utiles.escribirEnLog(Constantes.INFO, "===== cargarMultipleFTP: Inicio=====", "");
    	Utiles.escribirEnLog(Constantes.INFO, "[cargarMultipleFTP]-CodSolicitud:" + codigoSolicitud, "");
    	
        if (codigoSolicitud != null && !codigoSolicitud.trim().isEmpty()) {
            if (ficherosLeidos != null) {
            	Utiles.escribirEnLog(Constantes.INFO, "[cargarMultipleFTP]-Cantidad ficheros a subir:" + ficherosLeidos.size(), "");
                for (File file : ficherosLeidos) {
                    subirArchivoFTP(codigoSolicitud, file);
                }
            } else {
            	Utiles.escribirEnLog(Constantes.INFO, "[cargarMultipleFTP]-Lista de archivos nulo.", "");
            }
        }
        Utiles.escribirEnLog(Constantes.INFO, "===== cargarMultipleFTP: Fin =====", "");     
    }
    
    /**
     * Metodo que se encarga del envio de archivos leidos (.PDF) de la carpeta 
     * "Escaneados al directorio /files/ del FileServer via HTTP.
     * @param ficherosLeidos2 Lista de ficheros leidos del tipo {@link File}
     * @return tramaDocumentosCargados Contiene la trama de documentos cargados
     * **/   
    private String subirArchivoHTTP(List<File> ficherosLeidos2) {
    	System.out.println("== subirArchivosHTTP:Inicio ==");
    	String tramaDocumentosCargados = "";
    	try{    		
    		if(ficherosLeidos!=null){
    			System.out.println("Se subiran ["+ficherosLeidos.size() +"] archivos al FileServer.");
    			for(File aa: ficherosLeidos){
    				System.out.println("[Fichero]:"+aa.getName() + "\t Path:"+aa.getPath() + "\tAbsPath:"+aa.getAbsolutePath());
    			}
    		}
        	HttpTransferFiles httpTransferFiles = new HttpTransferFiles();
        	httpTransferFiles.setUrl(getParameter(Constantes.URL_SERVER));
        	System.out.println("[Upload]-getUrl:"+httpTransferFiles.getUrl());
        	httpTransferFiles.sendFiles(ficherosLeidos);
        	System.out.println("[Upload]-Se termino el sendFiles.");
        	//tramaDocumentosCargados = Utiles.armaTramaLista(httpTransferFiles.getNameSentFiles());
        	tramaDocumentosCargados = httpTransferFiles.getFilesLoaded();
        	
    		
    	}catch (Exception e) {
			System.out.println("Ha ocurrido una excepcion al subirArchivoHTTP: "+e);
		}
    	System.out.println("\t [Upload]-TramaDocsCargados->" + tramaDocumentosCargados);
    	System.out.println("== subirArchivosHTTP:Fin ==");
    	return tramaDocumentosCargados;
	}    

	public void cargarMultipleFTPConPermisos(final String codigoSolicitud){
        AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                cargarMultipleFTP(codigoSolicitud);
                return null;
            }
        });
    }
    
    public int eliminarDocumento(String codigoDocumento){    
    	Utiles.escribirEnLog(Constantes.INFO, "====== eliminarDocumento:Inicio ======", ""); 
    	Utiles.escribirEnLog(Constantes.INFO, "[eliminarDocumento]-Cod Documento:" + codigoDocumento, ""); 
    	
        if (codigoDocumento != null && !codigoDocumento.trim().isEmpty()) {
            if (ficherosLeidos != null) {
            	Utiles.escribirEnLog(Constantes.INFO, "[eliminarDocumento]-Cantidad de ficheros a recorrer: " + ficherosLeidos.size(), ""); 
                for (File file : ficherosLeidos) {                    
                    //codigoDocumento += ".PDF";
                    if(file.getName().contains(codigoDocumento)){
                    	Utiles.escribirEnLog(Constantes.INFO, "[eliminarDocumento]-Archivo: " + file.getName() + " de " + file.getAbsolutePath(), "");
                        ficherosLeidos.remove(file);
                        break;
                    }
                }
            } else {
            	Utiles.escribirEnLog(Constantes.DEBUG, "[eliminarDocumento]-Lista de archivos Nulo", "");
            }
        }
        Utiles.escribirEnLog(Constantes.INFO, "====== eliminarDocumento:Fin ======", ""); 
        return 0;
    }
    
    public void eliminarDocumentoConPermisos(final String codigoDocumento){        
    	AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
            	eliminarDocumento(codigoDocumento);
                return null;
            }
        });
    }
    
    
	public int eliminarDocumentos() {
		Utiles.escribirEnLog(Constantes.INFO, "====== eliminarDocumentos(): Inicio ======", "");
		String sPathCliente = getParameter("path_cliente");
		Utiles.escribirEnLog(Constantes.DEBUG, "[eliminarDocumentos]-sPatchCliente:"+sPathCliente, "");
		int i = 0;		
		try {
			
			File file = new File(sPathCliente);
			File[] files = file.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".pdf");
				}
			});
						
			for (File f : files) {
				f.delete();
				i++;
			}
		} catch (Exception e) {
			Utiles.escribirEnLog(Constantes.ERROR, Constantes.MSJ_OCURRE_EXCEPCION+"al  eliminar el archivo:"+e, "");
			e.printStackTrace();
		}
		Utiles.escribirEnLog(Constantes.DEBUG, "Numero de archivos eliminados: "+i, "");
		Utiles.escribirEnLog(Constantes.INFO, "====== eliminarDocumentos(): Fin ======", "");
		return 0;
	}

	public void eliminarDocumentosConPermisos() {
		AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				eliminarDocumentos();
				return null;
			}
		});
	}    
    
    /**** Fin Metodos a ser invocado desde java script *************/
    
    
    
    /*
     * Metodos invocados desde el Jpanel del Apllet
     * JApplet
     */
    private void actualizar(ActionEvent e)  {  
    	System.out.println("== actualizar(ActionEvent):INICIO ==");
    	try{
    		String sNombreDocLeidos = "";
            List<File> lstFicherosLeidos = null;
            String sPathCliente = getParameter(Constantes.PATH_CLIENTE);
            String sDocumentosLeer = getParameter(Constantes.DOCUMENTOS_LEER);        
            Archivo archivo = new Archivo();                
            if(archivo.obtenerListaFiles(sPathCliente, sDocumentosLeer) == 1){
                sNombreDocLeidos = archivo.getNombreFilesReaded();
                lstFicherosLeidos = archivo.getFilesReaded();
                System.out.println("[Act-Event]-Documentos obtenidos : " + sNombreDocLeidos);
                if(lstFicherosLeidos!=null){
                	System.out.println("[Act-Event]-Cantidad de archivos leidos : " + lstFicherosLeidos.size());	
                }            
                documentosLeidos = sNombreDocLeidos;   
                ficherosLeidos = lstFicherosLeidos;
                
                ejecutarFuncion("actualizarDocumentos", documentosLeidos);
                System.out.println("Se termino de actualizar los documentos.");
            }
            
    	}catch (Exception ex) {
			System.out.println("Ha ocurrido una excepcion en actualizar (Event):"+ex);
		}
       
    	System.out.println("== actualizar(ActionEvent):FIN ==");
    }    
     /**** Fin Metodos a ser invocado desde java script *************/
    
    
    
    private synchronized void ejecutarFuncion(String funcion, Object... parametros) {        
        try {
        	Utiles.escribirEnLog(Constantes.DEBUG,"[ejecutarFuncion]-nombre:" + funcion, "");
        	StringBuilder builder = new StringBuilder(Constantes.URL_JAVA_SCRIPT);
            builder.append(funcion).append("(");
            int i=0;
            for (Object param : parametros) {
                if (param instanceof String) {
                	if(i>0){
                		builder.append(",");
                	}
                    builder.append("'").append(param).append("'");
                } else {
                    builder.append(param);
                }
                i++;
            }
            builder.append(")");
            String ejecFuncion = builder.toString();
            Utiles.escribirEnLog(Constantes.DEBUG,"[ejecutarFuncion]-final:" + ejecFuncion, "");
            getAppletContext().showDocument(new URL(ejecFuncion));
        } catch (Exception e) {    
        	Utiles.escribirEnLog(Constantes.ERROR,"[ejecutarFuncion]-Ha ocurrido una excepcion al ejecutar la funcion:" + e, "");
        }
    }
   
     private void subirArchivoFTP(File file) {
        System.out.println("===== subirArchivoFTP(f):Inicio ======"); 
        String host = getParameter("host");
        String user = getParameter("user");
        String pass = getParameter("pass");
        String directory = getParameter("directory");
                
        System.out.println("[subirArchivoFTP]-Host:" + host+"Datos:"+ user + " / "+pass);
        System.out.println("[subirArchivoFTP]-Directorio:" + directory);

        clienteFTP = new ClienteFTP(host, user, pass);
        try {
            clienteFTP.setDirectorio(directory);
        } catch (IOException ex) {
        	System.out.println("Ha ocurrido una IOException: "+ex);
            Logger.getLogger(AgregarDocumentos.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Subir " + file.getName() + " de " + file.getAbsolutePath());
        clienteFTP.upLoadOneFiles(file.getName(), file.getAbsolutePath());
        System.out.println("===== subirArchivoFTP(f):Fin ======");
    }
     
    private void subirArchivoFTP(String codigoSolicitud, File file) {
        System.out.println("===== subirArchivoFTP(c,f):Inicio ====="); 
       
        String host = getParameter("host");
        String user = getParameter("user");
        String pass = getParameter("pass");
        String directory = getParameter("directory");
                
        System.out.println("Host:" + host);
        System.out.println("Datos:" + user + " / "+pass);
        System.out.println("Directorio:" + directory);

        clienteFTP = new ClienteFTP(host, user, pass);
        try {
            clienteFTP.setDirectorio(directory);
        } catch (IOException ex) {
            Logger.getLogger(AgregarDocumentos.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Ha ocurrido un error al setDirectorio:"+ex);
        }
        System.out.println("[FTP]-CodSolicitud:"+codigoSolicitud);
        System.out.println("[FTP]-nombreArchivo:"+file.getName());
        System.out.println("[FTP]-Origen:"+file.getAbsolutePath());
        
        try{
        	clienteFTP.upLoadOneFiles(codigoSolicitud + "_" + file.getName(), file.getAbsolutePath());
        }catch (Exception e) {
			System.out.println("Ha ocurrido un error al subirArchivoFTP:"+e);
		}
        
        System.out.println("******** subirArchivoFTP(c,f):Fin *************"); 
    }
    
}

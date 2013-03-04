/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hildebrando.visado.applet;

import com.hildebrando.visado.ftp.ClienteFTP;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;

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
    
    /*
     * Metodos a ser invocado desde java script
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
        System.out.println("********sendToFTP:Inicio*************");                     
        if(ficherosLeidos!=null){
            System.out.println("Cantidad de ficheros a subir: " + ficherosLeidos.size());         
            for(File file : ficherosLeidos){
                subirArchivoFTP(file);
            }
        } else {
            System.out.println("Lista de archivos Nuloooo ");
        }
        System.out.println("********sendToFTP:Fin*************");
    }
    
    
    public void actualizar()  {   
        System.out.println("********actualizar:Inicio*************"); 
        String sNombreDocLeidos = "";
        List<File> lstFicherosLeidos = null;
        String sPathCliente = getParameter(Constantes.PATH_CLIENTE);
        String sDocumentosLeer = getParameter(Constantes.DOCUMENTOS_LEER);        
        Archivo archivo = new Archivo();                
        if(archivo.obtenerListaFiles(sPathCliente, sDocumentosLeer) == 1){
            sNombreDocLeidos = archivo.getNombreFilesReaded();
            lstFicherosLeidos = archivo.getFilesReaded();
            System.out.println("Documentos obtenidos : " + sNombreDocLeidos);
            System.out.println("Cantidad de archivos leidos : " + lstFicherosLeidos.size());
            documentosLeidos = sNombreDocLeidos;   
            ficherosLeidos = lstFicherosLeidos;
            ejecutarFuncion("actualizarDocumentos", documentosLeidos);
        }
        System.out.println("********actualizar:Fin*************");
                
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
        
    
    public void cargarMultipleFTP(String codigoSolicitud){        
        System.out.println("********cargarMultipleFTP:Inicio*************"); 
        System.out.println("Codigo Solicitud " + codigoSolicitud);
        if (codigoSolicitud != null && !codigoSolicitud.trim().isEmpty()) {
            if (ficherosLeidos != null) {
                System.out.println("Cantidad de ficheros a subir: " + ficherosLeidos.size());
                for (File file : ficherosLeidos) {
                    subirArchivoFTP(codigoSolicitud, file);
                }
            } else {
                System.out.println("Lista de archivos Nuloooo ");
            }
        }
        System.out.println("********cargarMultipleFTP:Fin*************");        
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
        System.out.println("********eliminarDocumento:Inicio*************"); 
        System.out.println("Codigo documento " + codigoDocumento);
        if (codigoDocumento != null && !codigoDocumento.trim().isEmpty()) {
            if (ficherosLeidos != null) {
                System.out.println("Cantidad de ficheros a recorrer: " + ficherosLeidos.size());
                for (File file : ficherosLeidos) {                    
                    //codigoDocumento += ".PDF";
                    if(file.getName().contains(codigoDocumento)){
                        System.out.println("Removiendo file: " + file.getName() + " de " + file.getAbsolutePath());
                        ficherosLeidos.remove(file);
                        break;
                    }
                }
            } else {
                System.out.println("Lista de archivos Nulo");
            }
        }
        System.out.println("********eliminarDocumento:Fin*************");
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
    
    /**** Fin Metodos a ser invocado desde java script *************/
    
    
    
    /*
     * Metodos invocados desde el Jpanel del Apllet
     * JApplet
     */
    private void actualizar(ActionEvent e)  {   
        String sNombreDocLeidos = "";
        List<File> lstFicherosLeidos = null;
        String sPathCliente = getParameter(Constantes.PATH_CLIENTE);
        String sDocumentosLeer = getParameter(Constantes.DOCUMENTOS_LEER);        
        Archivo archivo = new Archivo();                
        if(archivo.obtenerListaFiles(sPathCliente, sDocumentosLeer) == 1){
            sNombreDocLeidos = archivo.getNombreFilesReaded();
            lstFicherosLeidos = archivo.getFilesReaded();
            System.out.println("Documentos obtenidos : " + sNombreDocLeidos);
            System.out.println("Cantidad de archivos leidos : " + lstFicherosLeidos.size());
            documentosLeidos = sNombreDocLeidos;   
            ficherosLeidos = lstFicherosLeidos;
            ejecutarFuncion("actualizarDocumentos", documentosLeidos);
        }
    }    
     /**** Fin Metodos a ser invocado desde java script *************/
    
    
    
    private void ejecutarFuncion(String funcion, Object... parametros) {        
        try {
            StringBuilder builder = new StringBuilder(Constantes.URL_JAVA_SCRIPT);
            builder.append(funcion).append("(");
            for (Object param : parametros) {
                if (param instanceof String) {
                    builder.append("'").append(param).append("'");
                } else {
                    builder.append(param);
                }
            }
            builder.append(")");
            String ejecFuncion = builder.toString();
            System.out.println("Ejecutar: " + ejecFuncion);
            getAppletContext().showDocument(new URL(ejecFuncion));
        } catch (Exception e) {                      
            System.out.println("Error al ejecutar la funcion");
            e.printStackTrace();
        }
    }
   
     private void subirArchivoFTP(File file) {
        System.out.println("********subirArchivoFTP:Inicio*************"); 
        String host = getParameter("host");
        String user = getParameter("user");
        String pass = getParameter("pass");
        String directory = getParameter("directory");
                
        System.out.println("host " + host);
        System.out.println("user " + user);
        System.out.println("pass " + pass);
        System.out.println("directory " + directory);

        clienteFTP = new ClienteFTP(host, user, pass);
        try {
            clienteFTP.setDirectorio(directory);
        } catch (IOException ex) {
            Logger.getLogger(AgregarDocumentos.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Subir " + file.getName() + " de " + file.getAbsolutePath());
        clienteFTP.upLoadOneFiles(file.getName(), file.getAbsolutePath());
        System.out.println("********subirArchivoFTP:Fin*************"); 
    }
     
    private void subirArchivoFTP(String codigoSolicitud, File file) {
        System.out.println("********subirArchivoFTP:Inicio*************"); 
        String host = getParameter("host");
        String user = getParameter("user");
        String pass = getParameter("pass");
        String directory = getParameter("directory");
                
        System.out.println("host " + host);
        System.out.println("user " + user);
        System.out.println("pass " + pass);
        System.out.println("directory " + directory);

        clienteFTP = new ClienteFTP(host, user, pass);
        try {
            clienteFTP.setDirectorio(directory);
        } catch (IOException ex) {
            Logger.getLogger(AgregarDocumentos.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Subir " + file.getName() + " de " + file.getAbsolutePath());
        clienteFTP.upLoadOneFiles(codigoSolicitud + "_" + file.getName(), file.getAbsolutePath());
        System.out.println("********subirArchivoFTP:Inicio*************"); 
    }
    
}
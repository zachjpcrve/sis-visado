/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hildebrando.visado.applet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hildebrando
 */
public class Archivo {
    
    private String nombreFilesReaded;
    private List<File> filesReaded;
    
    public Archivo(){
        filesReaded = new ArrayList<File>();
        nombreFilesReaded = "";
    }

    /*
    public List<File> obtenerFiles(String pathCliente, String sDocumentos){
        
        List<File> files = null;        
        try {
            String ubicacion = pathCliente;
            System.out.println("ubicacion:" + ubicacion);
            System.out.println("listaDocumentos:" + sDocumentos);
            
            String []aDocumentos = sDocumentos.split(Constantes.SEPARADOR);
            File file;
            files = new ArrayList<File>();
            for(String documento : aDocumentos){
                file = new File(ubicacion + File.separator + documento);
                if(file.exists()){
                    files.add(file);
                } else {
                    System.out.println("No existe " + documento);
                }
            }
                        
        } catch(Exception e){
            System.out.println("error");            
            e.printStackTrace();
            new RuntimeException(e);
        }          
        return files;
    }
    */
    
    
    public int obtenerListaFiles(String pathCliente, String sDocumentos) {
        int result = 0;
        
        try {
            String ubicacion = pathCliente;
            System.out.println("ubicacion:" + ubicacion);
            System.out.println("listaDocumentos:" + sDocumentos);            
            String []aDocumentos = sDocumentos.split(Constantes.SEPARADOR);
            File file;
            for(String documento : aDocumentos){
                file = new File(ubicacion + File.separator + documento);
                if(file.exists()){
                    this.setNombreFilesReaded(this.getNombreFilesReaded() + file.getName() + Constantes.SEPARADOR);
                    this.getFilesReaded().add(file);
                } else {
                    System.out.println("No existe " + documento);
                }
            }
            result = 1;
        } catch(Exception e){
             System.out.println("error");            
            e.printStackTrace();
            new RuntimeException(e);
            result = 0;
        }          
        return result;
    }

    /**
     * @return the nombreFilesReaded
     */
    public String getNombreFilesReaded() {
        return nombreFilesReaded;
    }

    /**
     * @param nombreFilesReaded the nombreFilesReaded to set
     */
    public void setNombreFilesReaded(String nombreFilesReaded) {
        this.nombreFilesReaded = nombreFilesReaded;
    }

    /**
     * @return the filesReaded
     */
    public List<File> getFilesReaded() {
        return filesReaded;
    }

    /**
     * @param filesReaded the filesReaded to set
     */
    public void setFilesReaded(List<File> filesReaded) {
        this.filesReaded = filesReaded;
    }
    
    
    public static byte[] getFileBytes(File file) throws IOException {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null) {
                    ous.close();
                }
            } catch (IOException e) {
                // swallow, since not that important
            }
            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
                // swallow, since not that important
            }
        }
        return ous.toByteArray();
    }

}

/*
 * Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.hildebrando.visado.applet;

import java.applet.Applet;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
//import netscape.javascript.*; // add plugin.jar to classpath during compilation

public class DataSummaryApplet extends Applet {
    
//    private JSObject window;
//    private List<File> files;
//    
//    public void start() {
//        try {
//            
//            window = JSObject.getWindow(this);
//            //String sListaFicheros = "Ficheros:";
//            //sListaFicheros = sListaFicheros + readFicheros();
//            readFicheros();
//            //window.call("writeSummary", new Object[] {sListaFicheros})   ;
//            /*
//            String sListaFicheros = "Ficheros:";
//            sListaFicheros = sListaFicheros + listarFicheros();
//            
//            String userName = "John Doe";
//
//            // set JavaScript variable
//            window.setMember("userName", userName);
//
//            // invoke JavaScript function
//            Number age = (Number) window.eval("getAge()");
//
//            // get a JavaScript object and retrieve its contents
//            JSObject address = (JSObject) window.eval("new address();");
//            String addressStr = (String) address.getMember("street") + ", " +
//                    (String) address.getMember("city") + ", " +
//                    (String) address.getMember("state");
//
//            // get an array from JavaScript and retrieve its contents
//            JSObject phoneNums = (JSObject) window.eval("getPhoneNums()");
//            String phoneNumStr = (String) phoneNums.getSlot(0) + ", " +
//                    (String) phoneNums.getSlot(1);
//
//            // dynamically change HTML in page; write data summary
//            String summary = userName + " : " + age + " : " +
//                    addressStr + " : " + phoneNumStr;
//            //window.call("writeSummary", new Object[] {summary})   ;
//            window.call("writeSummary", new Object[] {sListaFicheros})   ;
//            * 
//            */
//        } catch (JSException jse) {
//            jse.printStackTrace();
//            new RuntimeException(jse);
//        }
//    }
//
//    public void cambia(){
//        System.out.println("Cambia");
//    }
//    
//    public void listaFicheros(){
//        
//         System.out.println("listaFicheros");
//         
//         String sListaFicheros = "Ficheros:";
//         sListaFicheros = sListaFicheros + files.size();
//         
//         for(File f : files){
//            System.out.println("f:" + f);
//         }
//         if(window!=null){
//             window.call("writeSummary", new Object[] {sListaFicheros})   ;
//         } else {
//             System.out.println("Window nulo");
//         }
//         
//    }
//    
//     private String readFicheros()  {
//        
//        String result = "";
//        
//        try {
//            String ubicacion = getParameter("path");
//            System.out.println("Path:" + ubicacion);
//            File listfile = new File(ubicacion);
//            
//            
//
//
//            if (listfile.exists()){
//                FilenameFilter filterPDF = new FilenameFilter() {
//                    public boolean accept(File dir, String name) {
//                    return name.endsWith(".pdf");
//                    }
//                };
//                File[] lista = listfile.listFiles(filterPDF);
//                files = new ArrayList<File>();
//                for (int i = 0; i < lista.length; i++) {
//                    File file = lista[i];
//                    System.out.println("Nombre: " + file.getName());            
//                    result = result + ";" + file.getName();
//                    files.add(file);
//                }
//            } else {
//                System.out.println("Directorio no existe");            
//            }
//        } catch(Exception e){
//            e.printStackTrace();
//            new RuntimeException(e);
//        }          
//        return result;
//    }


}

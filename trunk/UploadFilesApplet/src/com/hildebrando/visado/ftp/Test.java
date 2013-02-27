/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hildebrando.visado.ftp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hildebrando
 */
public class Test {
    
    public static void main(String []args){
        File file = new File("D:/Escaneados/0000003_.pdf");
        ClienteFTP ftp = new ClienteFTP("10.172.0.4","hilde","$i$tema$2012");
        try {
            ftp.setDirectorio("/VISADO/PDF");
            ftp.upLoadOneFiles(file.getName(), file.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
}

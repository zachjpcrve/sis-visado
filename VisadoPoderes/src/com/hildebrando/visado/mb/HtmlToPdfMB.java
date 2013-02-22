package com.hildebrando.visado.mb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;

/**
 * Clase que se encarga de generar un archivo .PDF en base a una 
 * cadena HTML para el seguimiento de la solicitud.
 * @author 
 * @version 1.0
 */

@ManagedBean(name = "htmlToPdfMB")
@SessionScoped
public class HtmlToPdfMB {
		
	private final Logger logger = Logger.getLogger(HtmlToPdfMB.class);
	private StreamedContent file;  
	private static final String CONTENT_TYPE="application/pdf;charset=UTF-8";
	private String textoDocumento;
	private String nombrePdf;
	
		
	public void generarPdfListener(ActionEvent event){
		logger.info("===== generarPdfListener() ======");	
		File fileTemp = null;
		String nameFile="";
		String estado_obs="";
		
		try {
			
			String texto = (String) event.getComponent().getAttributes().get("texto");	
			 estado_obs=(String) event.getComponent().getAttributes().get("estado_obs");	
			StringBuffer buf = new StringBuffer();
			String sUbicacionTemporal;
			File fDirectory;
			buf.append("<html><head><title>Comentario</title></head><body>");    	       			
//			buf.append("<table>");
//			buf.append("<tr>");
//			buf.append("<td>");
//			buf.append("<img src=\"bbva2.gif\" />");
//			buf.append("</td>");
//			buf.append("<td width=\"500\">");		
//			buf.append("</td>");
//			buf.append("<td>");
//			buf.append("25/05/2012 10:20 AM");
//			buf.append("</td>");
//			buf.append("</tr>");
//			buf.append("</table>");			
	        if(texto!=null){
	        	buf.append(texto);
	        }
	        buf.append("</body></html>");
	        logger.info("[CadenaHTML]:" + buf);
	        
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        ByteArrayInputStream encXML = new ByteArrayInputStream(buf.toString().getBytes("UTF8"));
	        Document doc = builder.parse(encXML);	       
	        
	        sUbicacionTemporal = Utilitarios.getProjectPath() + File.separator + ConstantesVisado.FILES + File.separator;			
			fDirectory = new File(sUbicacionTemporal);
			if(!fDirectory.exists())
				fDirectory.mkdir();
			
			logger.info("[UbicacionTemporal]: " + sUbicacionTemporal);
	        fileTemp = File.createTempFile("tmp", "htmlToPdf.pdf", new File(sUbicacionTemporal));
	        if(fileTemp!=null){
				nameFile = fileTemp.getName().substring(1 + fileTemp.getName().lastIndexOf(File.separator));				
			}
	        logger.info("[NombreArchivo-comentarios]: "+nameFile);
	        	        
	        OutputStream os = new FileOutputStream(fileTemp);	        
			ITextRenderer renderer = new ITextRenderer();			
			renderer.setDocument(doc,null);
			renderer.layout();
			renderer.createPDF(os);
			os.close();
			
		} catch (Exception ex) {
			logger.info(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al generar el PDF: "+ex);
		} finally {
			fileTemp.deleteOnExit();					
		}
		
		InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(File.separator + ConstantesVisado.FILES + File.separator + nameFile);			
        this.file = new DefaultStreamedContent(stream, CONTENT_TYPE, estado_obs+"_Observacion.pdf");        
        logger.info("[Archivo final]: " + file);
        
        logger.info("===== saliendo de generarPdfListener() ======");
	}

	public StreamedContent getFile() {
		return file;
	}

	public String getTextoDocumento() {
		return textoDocumento;
	}

	public void setTextoDocumento(String textoDocumento) {
		this.textoDocumento = textoDocumento;
	}

	public String getNombrePdf() {
		return nombrePdf;
	}

	public void setNombrePdf(String nombrePdf) {
		this.nombrePdf = nombrePdf;
	}	
}

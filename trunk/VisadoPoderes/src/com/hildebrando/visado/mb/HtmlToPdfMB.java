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

@ManagedBean(name = "htmlToPdfMB")
@SessionScoped
public class HtmlToPdfMB {
		
	private final Logger logger = Logger.getLogger(HtmlToPdfMB.class);
	private StreamedContent file;  
	private static final String CONTENT_TYPE="application/pdf;charset=UTF-8";
	private String textoDocumento;
	private String nombrePdf;
		
	
	public HtmlToPdfMB(){
		
	}
	
	public void generarPdfListener(ActionEvent event){
		logger.info("ejecutarListener");
		
	
		File fileTemp = null;
		
		try {
			
			String texto = (String) event.getComponent().getAttributes().get("texto");
			logger.info("texto: " + texto);
			
			StringBuffer buf = new StringBuffer();
			buf.append("<html><head><title>Comentario</title></head><body>");    	       
	        if(texto!=null){
	        	buf.append(texto);
	        }
	        buf.append("</body></html>");
	        logger.info("pagina HTML: " + buf);
	        
	        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();							
			String sUbicacionTemporal = request.getRealPath(File.separator);  //+ File.separator + "files" + File.separator;
			
			logger.info("sUbicacionTemporal:" + sUbicacionTemporal);
			     
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        ByteArrayInputStream encXML = new ByteArrayInputStream(buf.toString().getBytes("UTF8"));
	        Document doc = builder.parse(encXML);	       
	        
	        
	        fileTemp = File.createTempFile("tmp", "htmlToPdf.pdf", new File(sUbicacionTemporal));
	        
//	        OutputStream os = new FileOutputStream(new File(sUbicacionTemporal + nameFile));
	        
	        OutputStream os = new FileOutputStream(fileTemp);
	        
			ITextRenderer renderer = new ITextRenderer();			
			renderer.setDocument(doc,null);
			renderer.layout();
			renderer.createPDF(os);
			os.close();
			
			
			String nameFile = "";
			
			if(fileTemp!=null){
				nameFile = fileTemp.getName().substring(1 + fileTemp.getName().lastIndexOf(File.separator));
				
			}
			logger.info("nameFile: " + nameFile);
			
			this.nombrePdf = nameFile;

			InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(nameFile);			
	        this.file = new DefaultStreamedContent(stream, CONTENT_TYPE, "download_pdf.pdf");        
	        logger.info("File:" + file);	
			
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			if(fileTemp!=null){
				fileTemp.deleteOnExit();
			}			
		}
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

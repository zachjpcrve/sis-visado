package com.hildebrando.visado.mb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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
	
	public HtmlToPdfMB(){
//		InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("xhtmlToPdf.pdf");  		
//        file = new DefaultStreamedContent(stream, CONTENT_TYPE, "download_xhtmlToPdf.pdf");        
//        logger.info("File:" + file);		
	}
	
//	public static void main(String []args){
//		try {
//
//			File inputFile = new File("d:/hdo/holaMundo.xhtml");
//			OutputStream os = new FileOutputStream(new File(
//					"xhtmlToPdf_holaMundo.pdf"));
//
//			ITextRenderer renderer = new ITextRenderer();
//			renderer.setDocument(inputFile);
//			renderer.layout();
//			renderer.createPDF(os);
//
//			os.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//	}	
	
	public void generarPDF(){
		System.out.println("generarPDF");
		
				
		try {
			
			Map<String,String> params =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		    String action = params.get("action");
		    
		    System.out.println("action:" + action);
			
			String sCadena = "<p>A<strong>A<font color=\"#cc33cc\">AAAAA</font>AAA</strong></p><p>AAAAAAAAAA</p><p>aaaaaaaa</p><p>AAAAAAaaaa</p>";
			
			StringBuffer buf = new StringBuffer();
			buf.append("<html>");    
	        buf.append("<head>");
	        buf.append("<title>Comentario</title>");
	        buf.append("</head>");
	        buf.append("<body>"); 
	        buf.append(sCadena);
	        buf.append("</body>");
	        buf.append("</html>");
	        logger.info("pagina HTML: " + buf);
	        

//			File inputFile = new File("d:/hdo/file2.xhtml");
	        
	        //No funciona 
//	        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//	        response.setContentType(CONTENT_TYPE);
	        
	        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();							
			String sUbicacionTemporal = request.getRealPath(File.separator);  //+ File.separator + "files" + File.separator;
			
			logger.info("sUbicacionTemporal:" + sUbicacionTemporal);
			
				        
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = builder.parse(new StringBufferInputStream(buf.toString()));
	        OutputStream os = new FileOutputStream(new File(sUbicacionTemporal + "xhtmlToPdf.pdf"));
//	        OutputStream os = response.getOutputStream();
			ITextRenderer renderer = new ITextRenderer();			
			renderer.setDocument(doc,null);
			renderer.layout();
			renderer.createPDF(os);
			os.close();
			
			
			
			
			 
			
			InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("xhtmlToPdf.pdf");			
	        file = new DefaultStreamedContent(stream, CONTENT_TYPE, "download_xhtmlToPdf.pdf");        
	        logger.info("File:" + file);	
			
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public StreamedContent getFile() {
		return file;
	}
	
	
}

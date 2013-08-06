package com.hildebrando.visado.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbva.common.util.ConstantesVisado;


@Controller
@RequestMapping("/principal")
public class FileTransferController {
	
	private static Logger logger =  Logger.getLogger(FileTransferController.class);
	private static final String FIELD_FILES_LOADED = "FILES_LOADED=";
	private static final String FIELD_END = "#";
	private static final String FIELD_SEPARATOR = ",";
			
	@RequestMapping(value = "/cargar", method = RequestMethod.POST)	
	public @ResponseBody String procesoCarga(HttpServletResponse response, HttpServletRequest request) throws Exception {
		logger.info("============ INICIA procesoCarga ==========");
		String result="";
			FileItemFactory factory = new DiskFileItemFactory();
			
			ServletFileUpload upload = new ServletFileUpload(factory);
			//upload.setSizeMax(1000000);

		    List<FileItem> fileItems = upload.parseRequest(request);
		    if(fileItems!=null){
		    	logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"fileItems es:"+fileItems.size());
		    }
		    // Process the uploaded items 
		    
		    //String path = request.getRealPath(File.separator) + File.separator + ConstantesVisado.FILES;
		    String path = ConstantesVisado.PATH_FILE_SERVER_DOCUMENTOS  + File.separator + ConstantesVisado.FILES + File.separator;
		    //logger.debug("getRealPath: "+request.getRealPath(File.separator));
		    
		    List<String> listaArchivos = new ArrayList<String>();
		    StringBuilder sb = new StringBuilder();
		    sb.append(FIELD_FILES_LOADED);
		    
		    logger.info("[ProcesoCarga]-Carpeta Files : " + path);
		    File filePath = new File(path);
		    if(!filePath.exists()){
		    	logger.debug("Se creara el directorio si no existe");
		    	filePath.mkdir();
		    }
		    	
		    for(int i=0; i<fileItems.size();i++){
		    	FileItem item = fileItems.get(i);
		    	String name = FilenameUtils.getBaseName(item.getName());
		    	String ext = FilenameUtils.getExtension(item.getName()).toLowerCase();
		    	logger.info("[ProcesoCarga]-Archivo recibido " + name + "." + ext);		    	
		    	File file=null;
		    	try {
		    		file = File.createTempFile(name + "_", "." + ext, filePath);
		    		logger.info("[ProcesoCarga]-Archivo copiado a /files/:" + file.getName());
			        item.write(file);
			        listaArchivos.add(file.getName());
			        sb.append(file.getName());
			        if(i == fileItems.size()-1){
			        	sb.append(FIELD_END);
			        } else {
			        	sb.append(FIELD_SEPARATOR);
			        }	     
		    	}catch(Exception e){
		    		logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR + " " + e);
		    	} finally{
		    		if(file!=null){
		    			file.deleteOnExit();
		    		}
		    	}
		    }	   	 	   	    		   
		    result = sb.toString();		
	    
		logger.info("============ SALIENDO de procesoCarga ==========");
		return result;
	}
	
}

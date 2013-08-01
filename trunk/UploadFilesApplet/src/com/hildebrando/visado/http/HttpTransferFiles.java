package com.hildebrando.visado.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpTransferFiles {

	private static final String FIELD_FILES_LOADED = "FILES_LOADED=";
	private static final String FIELD_END = "#";
	private static final String FIELD_SEPARATOR = ",";
	
	private String url;
	private String filesLoaded; 
	
	public HttpTransferFiles() {		
		url = "";
		filesLoaded = "";		
	}
	
	public void sendFiles(List<File> ficherosLeidos) {	
		
		try {
			HttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(url);
	        MultipartEntity entity = new MultipartEntity();
	        
	        if(ficherosLeidos!=null){
	        	System.out.println("Tamaño lista de ficheros: " + ficherosLeidos.size());
	        }
	        
	        System.out.println("Tamaño archivos en trama request - antes: " + entity.getContentLength());
	        
	        for(File file : ficherosLeidos){
	        	entity.addPart("file", new FileBody(file));
	        }
	        
	    	System.out.println("Tamaño archivos en trama request - despues: " + entity.getContentLength());
	        
	        post.setEntity(entity);        

	        HttpResponse response;
	        
			response = client.execute(post);
			
			System.out.println("Despues de ejecutar request: " + response);
        	        			
			this.filesLoaded = leerResponse(response,post,client);
						
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	
	private String leerResponse(HttpResponse response, HttpPost post,HttpClient client) throws Exception {
        
        String archivosSubidos = "";
        
        System.out.println(response.getStatusLine());
 
        HttpEntity entity = response.getEntity();
        
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(instream));
                // do something useful with the response
                String line = reader.readLine();
                System.out.println(line);             
                String field = FIELD_FILES_LOADED;
                if(line.startsWith(field)){
                    //FILES_LOADED=DOIDAPO_251571415801498931.PDF,IMPFOEL_7095448649017491933.PDF# 
                    archivosSubidos = line.substring(line.indexOf(field) + field.length(), line.indexOf(FIELD_END));
                }
                
            } catch (IOException ex) {

                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;
                
            } catch (RuntimeException ex) {

                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                post.abort();
                throw ex;
                
            } catch (Exception ex) {

                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;
             } finally {

                // Closing the input stream will trigger connection release
                instream.close();
                
            }

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            client.getConnectionManager().shutdown();
        }
        
        return archivosSubidos;
    }
	
	public List<String> getNameSentFiles() {
		List<String> nameSentFiles = new ArrayList<String>();
		if(filesLoaded!=null){
			String[] aName = this.filesLoaded.split(FIELD_SEPARATOR);
			for(String nombre : aName){
				nameSentFiles.add(nombre);
			}
		}		
		return nameSentFiles;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilesLoaded() {
		return filesLoaded;
	}

	public void setFilesLoaded(String filesLoaded) {
		this.filesLoaded = filesLoaded;
	}

	

	
}

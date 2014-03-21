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
		System.out.println("=== sendFiles():Inicio -  ===");
		try {
			System.out.println("[Send]-Url:"+url);			
			HttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(url);
	        System.out.println("[Send]-post:"+post);	
	        MultipartEntity entity = new MultipartEntity();
	        System.out.println("[Send]-entity");
	        if(ficherosLeidos!=null){
	        	System.out.println("[Send]-Tamaño lista de ficheros: " + ficherosLeidos.size());
	        	for(File a: ficherosLeidos){
	        		System.out.println("[ARCHIVO]-a.getPath: "+a.getPath() + "\ta.getName:"+a.getName());
	        	}
	        }
	        
	        System.out.println("[Send]-Tamaño archivos en trama request - antes: " + entity.getContentLength());
	        
	        for(File file : ficherosLeidos){
	        	entity.addPart("file", new FileBody(file));
	        }
	        
	    	System.out.println("[Send]-Tamaño archivos en trama request - despues: " + entity.getContentLength());
	        
	        post.setEntity(entity);        

	        HttpResponse response;
	        
			response = client.execute(post);
			
			System.out.println("[Send]-Despues de ejecutar [response]-: " + response);
			System.out.println("[Send]-Despues de ejecutar [client]-: " + client);
			System.out.println("[Send]-Despues de ejecutar [post]-: " + post);
        	        			
			this.filesLoaded = leerResponse(response,post,client);
						
		} catch (ClientProtocolException e1) {
			System.out.println("Ha ocurrido un ClientProtocolException:"+e1);
			e1.printStackTrace();
		} catch (IllegalStateException e2) {
			System.out.println("Ha ocurrido un IllegalStateException:"+e2);
			e2.printStackTrace();
		} catch (IOException e3) {
			System.out.println("Ha ocurrido un IOException:"+e3);
			e3.printStackTrace();
		} catch (Exception e4) {
			System.out.println("[Send]-Ha ocurrido un error al enviar los archivos:"+e4);
			e4.printStackTrace();
		}
		System.out.println("=== sendFiles():Fin ===");
	}	

	
	private String leerResponse(HttpResponse response, HttpPost post,HttpClient client) throws Exception {
        
        String archivosSubidos = "";
        
        System.out.println("\nrsponse.response.getStatusLine()"+response.getStatusLine());
 
        HttpEntity entity = response.getEntity();
        
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                System.out.println("[leerResponse]-inputStream:"+instream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                System.out.println("[leerResponse]-reader:"+reader);
                // do something useful with the response
                String line = reader.readLine();
                System.out.println("[leerResponse]-line: "+line);             
                String field = FIELD_FILES_LOADED;
                System.out.println("[leerResponse]-field:"+field);
                if(line.startsWith(field)){
                    //FILES_LOADED=DOIDAPO_251571415801498931.PDF,IMPFOEL_7095448649017491933.PDF# 
                    if(!line.equalsIgnoreCase(field)){
                    	System.out.println("[leerResponse]-comparando.");
                    	archivosSubidos = line.substring(line.indexOf(field) + field.length(), line.indexOf(FIELD_END));
                    	System.out.println("[leerResponse]-terminando de comparar:"+archivosSubidos);
                    }
                    System.out.println("Son iguales indexOf");
                }
                
            } catch (IOException ex) {
            	System.out.println("Ha ocurrido una IOException:"+ex);
                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;
                
            } catch (RuntimeException ex) {
            	System.out.println("Ha ocurrido una RuntimeException:"+ex);
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                post.abort();
                throw ex;
                
            } catch (Exception ex) {
            	System.out.println("Ha ocurrido una excepcion:"+ex);
                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;
             } finally {
            	 System.out.println("finaalizando leer");
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

	/*public void sendFilesLocal(List<File> ficherosLeidos,String urlNueva) {	
		System.out.println("=== sendFilesLocal():Inicio ===");
		try {
			System.out.println("[Send]-Url:"+urlNueva);			
			HttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urlNueva);
	        System.out.println("[Send]-post:"+post);	
	        MultipartEntity entity = new MultipartEntity();
	       
	        if(ficherosLeidos!=null){
	        	System.out.println("[Send]-Tamaño lista de ficheros: " + ficherosLeidos.size());
	        }
	        
	        System.out.println("[Send]-Tamaño archivos en trama request - antes: " + entity.getContentLength());
	        
	        for(File file : ficherosLeidos){
	        	entity.addPart("file", new FileBody(file));
	        }
	        System.out.println("[Send]-entity-contenType:"+entity.getContentType());
	        System.out.println("[Send]-entity-contentLenght:"+entity.getContentLength());
	        
	    	System.out.println("[Send]-Tamaño archivos en trama request - despues: " + entity.getContentLength());
	        
	        post.setEntity(entity);        

	        HttpResponse response;
	        
			response = client.execute(post);
			
			System.out.println("[Send]-Despues de ejecutar request: " + response);
			System.out.println("[Send]-Despues de ejecutar post: " + post);
			System.out.println("[Send]-Despues de ejecutar client: " + client);
        	        			
			String aa = leerResponse(response,post,client);
			System.out.println("aa: "+aa);
						
		} catch (ClientProtocolException e1) {
			System.out.println("Ha ocurrido un ClientProtocolException:"+e1);
			e1.printStackTrace();
		} catch (IllegalStateException e2) {
			System.out.println("Ha ocurrido un IllegalStateException:"+e2);
			e2.printStackTrace();
		} catch (IOException e3) {
			System.out.println("Ha ocurrido un IOException:"+e3);
			e3.printStackTrace();
		} catch (Exception e4) {
			System.out.println("Ha ocurrido un error al enviar los archivos:"+e4);
			e4.printStackTrace();
		}
		System.out.println("=== sendFilesLocal():Fin ===");
	}*/
	

	
}

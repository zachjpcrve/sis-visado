package com.hildebrando.visado.mb;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.Escaneado;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.ftp.ClienteFTP;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;


@ManagedBean(name = "pdfViewerMB")

@SessionScoped
public class PDFViewerMB {
	
	private List<TiivsParametros> lstParametros;
	private List<Escaneado> lstPDFEscaneados;
	private Escaneado selectEscaneado;
	private String urlPDF ;
	private Boolean mostrarPDF;
	private StreamedContent file;
	private String urlAPPScan;
	private String codUsuario;
	private String valCodSolicitud;
	private String aliasArchivo;
	
	public static Logger logger = Logger.getLogger(PDFViewerMB.class);
	
	public PDFViewerMB() 
	{
		lstPDFEscaneados=new ArrayList<Escaneado>();
		lstParametros=new ArrayList<TiivsParametros>();
		
		
		setValCodSolicitud("S01");
		setCodUsuario("P014773");
		setAliasArchivo("prueba - copia.pdf");
		
		lstPDFEscaneados.clear();
		setMostrarPDF(true);
		
		cargarParametrosBD();
		
		setUrlAPPScan(prepararURLEscaneo(getCodUsuario()));
		
		setMostrarPDF(true);
	}
	
	public String prepararURLEscaneo(String usuario)
	{
		String cadFinal="";
		
		for (TiivsParametros tmp: lstParametros)
		{
			cadFinal=tmp.getUrlAPP()+"?"+"idEmpresa="+tmp.getIdEmpresa()+"&"+"idSistema="+tmp.getIdSistema()+"&"+"txtLogin="+usuario;
		}
		
		System.out.println("URL: " + cadFinal);
		
		return cadFinal;
	}
	
	public void cargarParametrosBD()
	{
		GenericDao<TiivsParametros, Object> paramDAO = (GenericDao<TiivsParametros, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroParam = Busqueda.forClass(TiivsParametros.class);
		filtroParam.add(Restrictions.eq("codUsuario", getCodUsuario()));
		
		try {
			lstParametros = paramDAO.buscarDinamico(filtroParam);

		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de parametros");
		}
		
		System.out.println("Lista de parametros encontrados: " + lstParametros.size());
	}
	
	public boolean validarObligatorioPDF()
	{
		boolean existe=false;
		File Listfile =null;
		File[] lista =null;
		
		for (TiivsParametros tmpLst: lstParametros)
		{
			Listfile = new File(tmpLst.getRutaLocal());
			FilenameFilter filterPDF = new FilenameFilter() 
			{
		      public boolean accept(File dir, String name) 
		      {
		        return name.endsWith(".pdf");
		      }
		    };
		    
		    lista = Listfile.listFiles(filterPDF);
		    
		    for (int i = 0; i < lista.length; i++) 
			{
				if (lista[i].getPath().contains(getAliasArchivo()))
				{
					existe=true;
					System.out.println("Archivo: " + getAliasArchivo()  + " existe en directorio local.");	
					
				}
			}
		}
				
		if (!existe)
		{
			System.out.println("Archivo: " + getAliasArchivo()  + " no existe en directorio local.");	
		}
		return existe; 
	}
	
	public void cargaPDFLocal()
	{
		/*lstPDFEscaneados.clear();
		Utilitario util = new Utilitario();
		String dirServer="\\\\10.172.0.172\\\\compartido";
		String dirLocal="C:\\\\hildebrando";
		util.downloadFile("compartido", dirServer, dirLocal);*/
		lstPDFEscaneados.clear();
		String dirPDF = "C:\\\\hildebrando\\\\compartido";
		//String dirPDF = "ftp://hilde:$i$tema$2012@10.172.0.4/VISADO/PDF/";
		setUrlPDF(dirPDF+"\\\\prueba.pdf");
		
		//obtengo la lista de los archivos a descargar de la carpeta correspondiente al directorio local
		File Listfile = new File(dirPDF);
		
		//create a FilenameFilter and override its accept-method
	    FilenameFilter filterPDF = new FilenameFilter() 
	    {
		    public boolean accept(File dir, String name) 
		    {
		      return name.endsWith(".pdf");
		    }
	    };
		 
		File[] lista = Listfile.listFiles(filterPDF);
		
		for (int i = 0; i < lista.length; i++) 
		{
			Escaneado tmp=new Escaneado();
			
			tmp.setDocumento("Copia "+ i);
			tmp.setNomArchivo(lista[i].getPath());
			System.out.println("Ruta archivo " + i  + ": " + tmp.getNomArchivo());
			tmp.setFormato("Blanco y Negro");
			InputStream stream = this.getClass().getResourceAsStream(lista[i].getPath());
	        file = new DefaultStreamedContent(stream, "application/pdf");  
	        tmp.setScPDF(file);
	        
			lstPDFEscaneados.add(tmp);
		}
		
		System.out.println("Tamanio lista PDF: " + lstPDFEscaneados.size());
	}
	
	public String cargarUnicoPDF(final String nombreArchivo)
	{
		String urlServer="";
		String server="";
		String loginServer="";
		String passServer="";
		String carpetaRemota="";
		String dirLocal="";
		String rutaResultante="";
		
		//String codUsuario="P014773";
		
		//String dirPDF = "ftp://hilde:$i$tema$2012@10.172.0.4/VISADO/PDF/";
		
		if (lstParametros!=null) 
		{
			System.out.println("Tamanio lista parametros: " + lstParametros.size()); 
			
			for (TiivsParametros tmp: lstParametros)
			{
				urlServer=ConstantesVisado.PROTOCOLO_FTP+ConstantesVisado.DOS_PUNTOS.trim()+File.separator+File.separator+
							  tmp.getLoginServer()+ConstantesVisado.DOS_PUNTOS.trim()+
							  tmp.getPassServer()+ConstantesVisado.ARROBA+
							  File.separator+tmp.getCarpetaRemota();
					
				dirLocal=tmp.getRutaLocal();
				server=tmp.getServer();
				loginServer=tmp.getLoginServer();
				passServer=tmp.getPassServer();	
				carpetaRemota=tmp.getCarpetaRemota();
			}
		}		
		
				
		System.out.println("Parametros leidos de BD");
		System.out.println("Dir Server: " + urlServer);
		System.out.println("Dir Local: " + dirLocal);
		System.out.println("Server: " + server);
		System.out.println("Login Server:_" + loginServer);
		System.out.println("Pass Server: " + passServer);
		System.out.println("Carpeta Remota: " + carpetaRemota);
		
				
		ClienteFTP cliente = new ClienteFTP(server, loginServer, passServer);
		try {
			cliente.setDirectorio(carpetaRemota);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (cliente!=null)
		{
			//Transferir archivos PDF de la carpeta local al Servidor
			File Listfile = new File(dirLocal);
			FilenameFilter filterPDF = new FilenameFilter() 
			{
		      public boolean accept(File dir, String name) 
		      {
		        return name.endsWith(nombreArchivo);
		      }
		    };
			 
			File[] lista = Listfile.listFiles(filterPDF);
			
			for (int i = 0; i < lista.length; i++) 
			{
				//String codDocumento = buscarTipoDocByNombre(lista[i].getName());
				String codDocumento="";
				cliente.upLoadFiles(lista[i].getName(),dirLocal);
				if (codDocumento.isEmpty())
				{
					cliente.renombrarArchivo(getValCodSolicitud()+"-"+"00"+(i+1)+".pdf", lista[i].getName());
				}
				else
				{
					cliente.renombrarArchivo(getValCodSolicitud()+"-"+codDocumento+".pdf", lista[i].getName());
				}
				
			}
			
			//Leer ficheros PDF para mostrarlos en la grilla
			List listaTMP=new ArrayList<String>();
			try {
				listaTMP = cliente.listar();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (listaTMP !=null)
			{
				/*int ind=0;
				for (; ind<=listaTMP.size()-1;ind++)
				{
					Escaneado tmp=new Escaneado();
					
					tmp.setDocumento("Copia "+ ind);
					tmp.setNomArchivo(listaTMP.get(ind).toString());
					System.out.println("Ruta archivo " + ind  + ": " + tmp.getNomArchivo());
					tmp.setFormato("Blanco y Negro");
					tmp.setRutaArchivoLarga(urlServer+tmp.getNomArchivo());
					
					lstPDFEscaneados.add(tmp);
				}*/
				rutaResultante=urlServer+nombreArchivo;
			}
			
		}
		
		
		return rutaResultante;
	}
	
	public Boolean cargarPDFRemoto(String sTipoArchivo,String dirLocal)
	{
		boolean cargaExitosa=true;
		String urlServer="";
		String server="";
		String loginServer="";
		String passServer="";
		String carpetaRemota="";
		String rutaArchivoLocal="C:" + File.separator+"instaladores"+File.separator;
		
		//String codUsuario="P014773";
		
		//String dirPDF = "ftp://hilde:$i$tema$2012@10.172.0.4/VISADO/PDF/";
		
		if (sTipoArchivo.equalsIgnoreCase("o"))
		{
			if (lstParametros!=null) 
			{
				System.out.println("Tamanio lista parametros: " + lstParametros.size()); 
				
				for (TiivsParametros tmp: lstParametros)
				{
					urlServer=ConstantesVisado.PROTOCOLO_FTP+ConstantesVisado.DOS_PUNTOS.trim()+File.separator+File.separator+
								  tmp.getLoginServer()+ConstantesVisado.DOS_PUNTOS.trim()+
								  tmp.getPassServer()+ConstantesVisado.ARROBA+
								  File.separator+tmp.getCarpetaRemota();
						
					server=tmp.getServer();
					loginServer=tmp.getLoginServer();
					passServer=tmp.getPassServer();	
					carpetaRemota=tmp.getCarpetaRemota();
				}
				
				//util.downloadFile("compartido", dirLocal,dirServer);
			}
			dirLocal=rutaArchivoLocal;
		}
		else
		{
			if (lstParametros!=null) 
			{
				System.out.println("Tamanio lista parametros: " + lstParametros.size()); 
				
				for (TiivsParametros tmp: lstParametros)
				{
					urlServer=ConstantesVisado.PROTOCOLO_FTP+ConstantesVisado.DOS_PUNTOS.trim()+File.separator+File.separator+
								  tmp.getLoginServer()+ConstantesVisado.DOS_PUNTOS.trim()+
								  tmp.getPassServer()+ConstantesVisado.ARROBA+
								  File.separator+tmp.getCarpetaRemota();
						
					dirLocal=tmp.getRutaLocal();
					server=tmp.getServer();
					loginServer=tmp.getLoginServer();
					passServer=tmp.getPassServer();	
					carpetaRemota=tmp.getCarpetaRemota();
				}
				
				//util.downloadFile("compartido", dirLocal,dirServer);
			}
		}
		
		System.out.println("Parametros leidos de BD");
		System.out.println("Dir Server: " + urlServer);
		System.out.println("Dir Local: " + dirLocal);
		System.out.println("Server: " + server);
		System.out.println("Login Server:_" + loginServer);
		System.out.println("Pass Server: " + passServer);
		System.out.println("Carpeta Remota: " + carpetaRemota);
		
				
		ClienteFTP cliente = new ClienteFTP(server, loginServer, passServer);
		try {
			cliente.setDirectorio(carpetaRemota);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (cliente!=null)
		{
			//Transferir archivos PDF de la carpeta local al Servidor
			File Listfile = new File(dirLocal);
			FilenameFilter filterPDF = new FilenameFilter() 
			{
		      public boolean accept(File dir, String name) 
		      {
		        return name.endsWith(".pdf");
		      }
		    };
			 
			File[] lista = Listfile.listFiles(filterPDF);
			
			for (int i = 0; i < lista.length; i++) 
			{
				//String codDocumento = buscarTipoDocByNombre(lista[i].getName());
				String codDocumento="";
				cliente.upLoadFiles(lista[i].getName(),dirLocal);
				if (codDocumento.isEmpty())
				{
					cliente.renombrarArchivo(getValCodSolicitud()+"-"+"00"+(i+1)+".pdf", lista[i].getName());
				}
				else
				{
					cliente.renombrarArchivo(getValCodSolicitud()+"-"+codDocumento+".pdf", lista[i].getName());
				}
				
			}
			
			//Leer ficheros PDF para mostrarlos en la grilla
			List listaTMP=new ArrayList<String>();
			try {
				listaTMP = cliente.listar();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (listaTMP !=null)
			{
				int ind=0;
				for (; ind<=listaTMP.size()-1;ind++)
				{
					Escaneado tmp=new Escaneado();
					
					tmp.setDocumento("Copia "+ ind);
					tmp.setNomArchivo(listaTMP.get(ind).toString());
					System.out.println("Ruta archivo " + ind  + ": " + tmp.getNomArchivo());
					tmp.setFormato("Blanco y Negro");
					tmp.setRutaArchivoLarga(urlServer+tmp.getNomArchivo());
					
					lstPDFEscaneados.add(tmp);
				}
			}
			else
			{
				cargaExitosa=false;
			}
		}
		return cargaExitosa;
	}
	
	/*public String buscarTipoDocByNombre(String archivo)
	{
		String sinExtesion = archivo.substring(0, archivo.indexOf("."));
		String resultado="";
		
		System.out.println("Documento a evaluar: " + sinExtesion);
		
		GenericDao<TiivsTipoSolicDocumento, Object> tipoDocDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);
		filtroTipoDoc.add(Restrictions.eq("desDoc", sinExtesion));
		
		List<TiivsTipoSolicDocumento> lstTmp= new ArrayList<TiivsTipoSolicDocumento>();
		
		try {
			lstTmp = tipoDocDAO.buscarDinamico(filtroTipoDoc);
		} catch (Exception e1) {
			logger.debug("Error al cargar el listado de tipos de documento");
		}
		
		if (lstTmp!=null)
		{
			if (lstTmp.size()==1)
			{
				resultado=lstTmp.get(0).getCodDoc();
			}
		}
		
		return resultado;
	}*/
	
	public void showPDF()
	{
		String archivoPDF=getSelectEscaneado().getNomArchivo();
		System.out.println("PDF File: " +archivoPDF);
		//setUrlPDF(archivoPDF);
		//setMostrarPDF(true);
		
		InputStream stream = this.getClass().getResourceAsStream(archivoPDF);
        file = new DefaultStreamedContent(stream, "application/pdf", getSelectEscaneado().getDocumento());
 	}

	public List<TiivsParametros> getLstParametros() {
		return lstParametros;
	}

	public void setLstParametros(List<TiivsParametros> lstParametros) {
		this.lstParametros = lstParametros;
	}

	public List<Escaneado> getLstPDFEscaneados() {
		return lstPDFEscaneados;
	}

	public void setLstPDFEscaneados(List<Escaneado> lstPDFEscaneados) {
		this.lstPDFEscaneados = lstPDFEscaneados;
	}

	public Escaneado getSelectEscaneado() {
		return selectEscaneado;
	}

	public void setSelectEscaneado(Escaneado selectEscaneado) {
		this.selectEscaneado = selectEscaneado;
	}

	public String getUrlPDF() {
		return urlPDF;
	}

	public void setUrlPDF(String urlPDF) {
		this.urlPDF = urlPDF;
	}

	public Boolean getMostrarPDF() {
		return mostrarPDF;
	}

	public void setMostrarPDF(Boolean mostrarPDF) {
		this.mostrarPDF = mostrarPDF;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getUrlAPPScan() {
		return urlAPPScan;
	}

	public void setUrlAPPScan(String urlAPPScan) {
		this.urlAPPScan = urlAPPScan;
	}

	public String getCodUsuario() {
		return codUsuario;
	}

	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}

	public String getValCodSolicitud() {
		return valCodSolicitud;
	}

	public void setValCodSolicitud(String valCodSolicitud) {
		this.valCodSolicitud = valCodSolicitud;
	}

	public String getAliasArchivo() {
		return aliasArchivo;
	}

	public void setAliasArchivo(String aliasArchivo) {
		this.aliasArchivo = aliasArchivo;
	}
}

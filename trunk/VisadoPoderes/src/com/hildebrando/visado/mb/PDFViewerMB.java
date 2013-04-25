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
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.Escaneado;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.ftp.ClienteFTP;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;


@ManagedBean(name = "pdfViewerMB")

@SessionScoped
public class PDFViewerMB {
	
//	private List<TiivsParametros> lstParametros;
	private TiivsParametros parametros;
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
//		lstParametros=new ArrayList<TiivsParametros>();
		
		this.urlPDF = "";
		setValCodSolicitud("S01");
		setCodUsuario("P014773");
		setAliasArchivo("prueba - copia.pdf");
		
		lstPDFEscaneados.clear();
		setMostrarPDF(true);
		
		cargarParametrosBD();
		
		setUrlAPPScan(prepararURLEscaneo(getCodUsuario()));
		
		setMostrarPDF(true);
		
	}
	
	public String obtenerURLRemota()
	{
		if(parametros!=null){
			urlPDF = ConstantesVisado.PROTOCOLO_FTP
					+ ConstantesVisado.DOS_PUNTOS.trim() + "//"
					+ parametros.getLoginServer()
					+ ConstantesVisado.DOS_PUNTOS.trim()
					+ parametros.getPassServer() + ConstantesVisado.ARROBA
					+ parametros.getServer() + "/" + parametros.getCarpetaRemota();
		}		
		return urlPDF;
	}
	
	public String prepararURLEscaneo(String usuario)
	{
		String cadFinal="";	
		if(parametros!=null){
//			cadFinal = parametros.getUrlAPP() + "?" + "idEmpresa="
//					+ parametros.getIdEmpresa() + "&amp;" + "idSistema="
//					+ parametros.getIdSistema() + "&amp;" + "txLogin=" +usuario;
			cadFinal = parametros.getUrlAPP() + "?" + "idEmpresa="
					+ parametros.getIdEmpresa() + "&" + "idSistema="
					+ parametros.getIdSistema() + "&" + "txLogin=" +usuario;
		}	
		logger.debug("URL: " + cadFinal);		
		return cadFinal;
	}
	
	public void cargarParametrosBD()
	{
		try {
		IILDPeUsuario usuario = new IILDPeUsuario(); 
		usuario=(IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		
		if (usuario!=null)
		{
			setCodUsuario(usuario.getUID());
		}
		
		GenericDao<TiivsParametros, Object> paramDAO = (GenericDao<TiivsParametros, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroParam = Busqueda.forClass(TiivsParametros.class);
//		filtroParam.add(Restrictions.eq("codUsuario", getCodUsuario()));
				
		List<TiivsParametros> lstParametros = paramDAO.buscarDinamico(filtroParam);
		logger.debug("Lista de parametros encontrados: " + lstParametros.size());
		
		if(lstParametros.size()>0){
			parametros = lstParametros.get(0);
		}

		} catch (Exception e) {
			
			logger.debug("Error al cargar el listado de parametros",e);
		}
		
		
	}
	
	public boolean validarObligatorioPDF()
	{
		boolean existe=false;
		File Listfile =null;
		File[] lista =null;
		
		if(parametros!=null){
			Listfile = new File(parametros.getRutaLocal());
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
					logger.debug("Archivo: " + getAliasArchivo()  + " existe en directorio local.");	
					
				}
			}
		}
				
		if (!existe)
		{
			logger.debug("Archivo: " + getAliasArchivo()  + " no existe en directorio local.");	
		}
		return existe; 
	}
	
	public void cargaPDFLocal()
	{
		lstPDFEscaneados.clear();
		String dirPDF = "C:\\\\hildebrando\\\\compartido";
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
			logger.debug("Ruta archivo " + i  + ": " + tmp.getNomArchivo());
			tmp.setFormato("Blanco y Negro");
			InputStream stream = this.getClass().getResourceAsStream(lista[i].getPath());
	        file = new DefaultStreamedContent(stream, "application/pdf");  
	        tmp.setScPDF(file);
	        
			lstPDFEscaneados.add(tmp);
		}
		
		logger.debug("Tamanio lista PDF: " + lstPDFEscaneados.size());
	}
	
	
	public String cargarUnicoPDF(String ruta)
	{
		String urlServer="";
		String server="";
		String loginServer="";
		String passServer="";
		String carpetaRemota="";
		String dirLocal="";
		String rutaResultante="";
		
	
		if (parametros != null) {

			urlServer = ConstantesVisado.PROTOCOLO_FTP
					+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
					+ File.separator + parametros.getLoginServer()
					+ ConstantesVisado.DOS_PUNTOS.trim()
					+ parametros.getPassServer() + ConstantesVisado.ARROBA
					+ parametros.getServer() + File.separator
					+ parametros.getCarpetaRemota();

			dirLocal = ruta;
			server = parametros.getServer();
			loginServer = parametros.getLoginServer();
			passServer = parametros.getPassServer();
			carpetaRemota = parametros.getCarpetaRemota();

		}
	
		logger.debug("Parametros leidos de BD");
		logger.debug("Dir Server: " + urlServer);
		logger.debug("Dir Local: " + dirLocal);
		logger.debug("Server: " + server);
		logger.debug("Login Server:_" + loginServer);
		logger.debug("Pass Server: " + passServer);
		logger.debug("Carpeta Remota: " + carpetaRemota);
						
		ClienteFTP cliente = new ClienteFTP(server, loginServer, passServer);
		try {
			cliente.setDirectorio(carpetaRemota);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (cliente!=null)
		{
			
			File Listfile = new File(dirLocal);

			cliente.upLoadOneFiles(Listfile.getName(),dirLocal);
			
			rutaResultante=urlServer+Listfile.getName();
			
		}
		
		
		return rutaResultante;
	}
	
	public String cargarUnicoPDF(final String nombreArchivo, String ruta)
	{
		String urlServer="";
		String server="";
		String loginServer="";
		String passServer="";
		String carpetaRemota="";
		String dirLocal="";
		String rutaResultante="";
				
		if (parametros != null) {
			urlServer = ConstantesVisado.PROTOCOLO_FTP
					+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
					+ File.separator + parametros.getLoginServer()
					+ ConstantesVisado.DOS_PUNTOS.trim()
					+ parametros.getPassServer() + ConstantesVisado.ARROBA
					+ parametros.getServer() + File.separator
					+ parametros.getCarpetaRemota();

			dirLocal = ruta;
			server = parametros.getServer();
			loginServer = parametros.getLoginServer();
			passServer = parametros.getPassServer();
			carpetaRemota = parametros.getCarpetaRemota();

		}
		
		logger.debug("Parametros leidos de BD");
		logger.debug("Dir Server: " + urlServer);
		logger.debug("Dir Local: " + dirLocal);
		logger.debug("Server: " + server);
		logger.debug("Login Server:_" + loginServer);
		logger.debug("Pass Server: " + passServer);
		logger.debug("Carpeta Remota: " + carpetaRemota);
		
				
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
			 
			cliente.upLoadOneFiles(nombreArchivo,dirLocal);
			
			rutaResultante=urlServer+nombreArchivo;
			
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
		
				
		if (sTipoArchivo.equalsIgnoreCase("o"))
		{
			if (parametros != null) {

				urlServer = ConstantesVisado.PROTOCOLO_FTP
						+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
						+ File.separator + parametros.getLoginServer()
						+ ConstantesVisado.DOS_PUNTOS.trim()
						+ parametros.getPassServer() + ConstantesVisado.ARROBA
						+ parametros.getServer() + File.separator
						+ parametros.getCarpetaRemota();

				server = parametros.getServer();
				loginServer = parametros.getLoginServer();
				passServer = parametros.getPassServer();
				carpetaRemota = parametros.getCarpetaRemota();
				// util.downloadFile("compartido", dirLocal,dirServer);
			}
			dirLocal = rutaArchivoLocal;
		}
		else
		{
			if (parametros != null) {
				urlServer = ConstantesVisado.PROTOCOLO_FTP
						+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
						+ File.separator + parametros.getLoginServer()
						+ ConstantesVisado.DOS_PUNTOS.trim()
						+ parametros.getPassServer() + ConstantesVisado.ARROBA
						+ File.separator + parametros.getCarpetaRemota();

				dirLocal = parametros.getRutaLocal();
				server = parametros.getServer();
				loginServer = parametros.getLoginServer();
				passServer = parametros.getPassServer();
				carpetaRemota = parametros.getCarpetaRemota();
				// util.downloadFile("compartido", dirLocal,dirServer);
			}
		}
		
		logger.debug("Parametros leidos de BD");
		logger.debug("Dir Server: " + urlServer);
		logger.debug("Dir Local: " + dirLocal);
		logger.debug("Server: " + server);
		logger.debug("Login Server:_" + loginServer);
		logger.debug("Pass Server: " + passServer);
		logger.debug("Carpeta Remota: " + carpetaRemota);
		
				
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
					logger.debug("Ruta archivo " + ind  + ": " + tmp.getNomArchivo());
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
		
	public void showPDF()
	{
		String archivoPDF=getSelectEscaneado().getNomArchivo();
		logger.debug("PDF File: " +archivoPDF);
		//setUrlPDF(archivoPDF);
		//setMostrarPDF(true);
		
		InputStream stream = this.getClass().getResourceAsStream(archivoPDF);
        file = new DefaultStreamedContent(stream, "application/pdf", getSelectEscaneado().getDocumento());
 	}

	

	public void eliminarArchivos(List<String> aliasArchivos) {
		
		
		String urlServer="";
		String server="";
		String loginServer="";
		String passServer="";
		String carpetaRemota="";
		String dirLocal="";
		String rutaResultante="";
		
		//String codUsuario="P014773";
		
		//String dirPDF = "ftp://hilde:$i$tema$2012@10.172.0.4/VISADO/PDF/";
		
		if (parametros != null) {

			urlServer = ConstantesVisado.PROTOCOLO_FTP
					+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
					+ File.separator + parametros.getLoginServer()
					+ ConstantesVisado.DOS_PUNTOS.trim()
					+ parametros.getPassServer() + ConstantesVisado.ARROBA
					+ parametros.getServer() + File.separator
					+ parametros.getCarpetaRemota();

			// dirLocal=ruta;
			server = parametros.getServer();
			loginServer = parametros.getLoginServer();
			passServer = parametros.getPassServer();
			carpetaRemota = parametros.getCarpetaRemota();

		}
				
		logger.debug("Parametros leidos de BD");
		logger.debug("Dir Server: " + urlServer);
		logger.debug("Dir Local: " + dirLocal);
		logger.debug("Server: " + server);
		logger.debug("Login Server:_" + loginServer);
		logger.debug("Pass Server: " + passServer);
		logger.debug("Carpeta Remota: " + carpetaRemota);
		
		
		ClienteFTP cliente = new ClienteFTP(server, loginServer, passServer);
		try {
			cliente.setDirectorio(carpetaRemota);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (cliente!=null)
		{			
			cliente.deleteFiles(aliasArchivos);			
		}
		
	}

	public void renombrarFiles(List<String> fromFicheros, List<String> toFicheros) {
		String urlServer="";
		String server="";
		String loginServer="";
		String passServer="";
		String carpetaRemota="";
		String dirLocal="";
		String rutaResultante="";
		
				
		if (parametros != null) {

			urlServer = ConstantesVisado.PROTOCOLO_FTP
					+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
					+ File.separator + parametros.getLoginServer()
					+ ConstantesVisado.DOS_PUNTOS.trim()
					+ parametros.getPassServer() + ConstantesVisado.ARROBA
					+ parametros.getServer() + File.separator
					+ parametros.getCarpetaRemota();

			// dirLocal=ruta;
			server = parametros.getServer();
			loginServer = parametros.getLoginServer();
			passServer = parametros.getPassServer();
			carpetaRemota = parametros.getCarpetaRemota();

		}
		
				
		logger.debug("Parametros leidos de BD");
		logger.debug("Dir Server: " + urlServer);
		logger.debug("Dir Local: " + dirLocal);
		logger.debug("Server: " + server);
		logger.debug("Login Server:_" + loginServer);
		logger.debug("Pass Server: " + passServer);
		logger.debug("Carpeta Remota: " + carpetaRemota);
		
		
		ClienteFTP cliente = new ClienteFTP(server, loginServer, passServer);
		try {
			cliente.setDirectorio(carpetaRemota);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (cliente!=null)
		{			
			cliente.renombrarArchivos(fromFicheros, toFicheros); 
		}
		
	}

	public boolean descargarArchivo(String ubicacionLocal, String aliasArchivo) {
		
		String urlServer="";
		String server="";
		String loginServer="";
		String passServer="";
		String carpetaRemota="";
		String dirLocal="";
		String rutaResultante="";
		boolean iRet = true; 
		
		
		int iResultCargaParametros = 0;
		if (parametros != null) {

			urlServer = ConstantesVisado.PROTOCOLO_FTP
					+ ConstantesVisado.DOS_PUNTOS.trim() + File.separator
					+ File.separator + parametros.getLoginServer()
					+ ConstantesVisado.DOS_PUNTOS.trim()
					+ parametros.getPassServer() + ConstantesVisado.ARROBA
					+ parametros.getServer() + File.separator
					+ parametros.getCarpetaRemota();

			server = parametros.getServer();
			loginServer = parametros.getLoginServer();
			passServer = parametros.getPassServer();
			carpetaRemota = parametros.getCarpetaRemota();
			iResultCargaParametros = 1;

		}
		
		logger.debug("Parametros leidos de BD");
		logger.debug("Dir Server: " + urlServer);
		logger.debug("Dir Local: " + dirLocal);
		logger.debug("Server: " + server);
		logger.debug("Login Server:_" + loginServer);
		logger.debug("Pass Server: " + passServer);
		logger.debug("Carpeta Remota: " + carpetaRemota);
		
		
		if(iResultCargaParametros == 0){
			logger.debug("No se ha logrado cargar los parámetros de conexión al FTP");
			iRet = false;
		} else {			
			ClienteFTP cliente = null;
			try {
				cliente = new ClienteFTP(server, loginServer, passServer);
				cliente.setDirectorio(carpetaRemota);
			} catch (IOException e1) {
				logger.error("Error al instanciar clienteFTP ",e1);			
			}
			
			if (cliente!=null)
			{			
				iRet = cliente.downloadFile(ubicacionLocal,aliasArchivo);						
			}
			
		}

		return iRet;
		
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

	public TiivsParametros getParametros() {
		return parametros;
	}

	public void setParametros(TiivsParametros parametros) {
		this.parametros = parametros;
	}
	
	
}

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
import java.util.List;
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
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;

/**
 * 
 * @author 
 * @version 1.0
 */

@ManagedBean(name = "visadoDocumentosMB")
@SessionScoped
public class VisadoDocumentosMB {
		
	private final Logger logger = Logger.getLogger(VisadoDocumentosMB.class);
	
	private String pathCliente="";
	private String documentosLeer="";
	private String documentosLeidos="";
	private String documentosCargados="";
	private String host="";
	private String user="";
	private String pass="";
	private String directory="";
	private String carpetaClienteLog=ConstantesVisado.CARPETA_LOG_CLIENTE;
	private String urlCarga="";
	
	public VisadoDocumentosMB(){
		iniciarlizarParametros();
	}

	private void iniciarlizarParametros() {
		List<TiivsParametros> lstParametros = null;
		try {						
			GenericDao<TiivsParametros, Object> paramDAO = (GenericDao<TiivsParametros, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroParam = Busqueda.forClass(TiivsParametros.class);
			filtroParam.add(Restrictions.isNotNull("idParam"));
			lstParametros = paramDAO.buscarDinamico(filtroParam);

		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+"de parametros: " + e);
		}
		
		if(lstParametros!=null){
			logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"de parametros es:[" + lstParametros.size()+"]");	
		}
		
		if(lstParametros!=null && lstParametros.size()>0){
			TiivsParametros param = lstParametros.get(0);
			pathCliente = param.getRutaLocal();
			host = param.getServer();
			user = param.getLoginServer();
			pass = param.getPassServer();
			directory = param.getCarpetaRemota();
			
			logger.info("[PARAMETROS_BD]-pathCliente:" + pathCliente);
			logger.info("[PARAMETROS_BD]-host:" + host);
			logger.info("[PARAMETROS_BD]-user:" + user);
			logger.info("[PARAMETROS_BD]-pass:" + pass);
			logger.info("[PARAMETROS_BD]-directory:" + directory);
			
		}		
		this.urlCarga = armaUrlCarga();
	}
	
	private String armaUrlCarga() {
		String url="";
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();				
			url = request.getScheme()
				      + "://"
				      + request.getServerName()
				      + ":"
				      + request.getServerPort()
				      + request.getContextPath();
				      //+ request.getRequestURI();
			
			url = url + "/principal/cargar.htm";		
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR + " al obtener la ruta de carga de archivos",e);
		}
		logger.debug("urlCarga: "+url);
		return url;
	}

	public static String armaTramaDocumentosALeer (List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp){
		String sResult = "";		
		StringBuilder sb = new StringBuilder(); 
		if(lstDocumentosXTipoSolTemp!=null){
			for(TiivsTipoSolicDocumento doc : lstDocumentosXTipoSolTemp){
				if(sb.length()>1){
					sb.append(",");
				}			
				sb.append(doc.getTiivsDocumento().getNombre());
				sb.append(".");
				sb.append(doc.getTiivsDocumento().getFormato());			
			}
			sResult = sb.toString();
		}		
		return sResult;
	}

	public String getPathCliente() {
		return pathCliente;
	}

	public void setPathCliente(String pathCliente) {
		this.pathCliente = pathCliente;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getDocumentosLeer() {
		return documentosLeer;
	}

	public void setDocumentosLeer(String documentosLeer) {
		this.documentosLeer = documentosLeer;
	}

	public String getDocumentosLeidos() {
		return documentosLeidos;
	}

	public void setDocumentosLeidos(String documentosLeidos) {
		this.documentosLeidos = documentosLeidos;
	}

	public String getCarpetaClienteLog() {
		return carpetaClienteLog;
	}

	public void setCarpetaClienteLog(String carpetaClienteLog) {
		this.carpetaClienteLog = carpetaClienteLog;
	}

	public String getDocumentosCargados() {
		return documentosCargados;
	}

	public void setDocumentosCargados(String documentosCargados) {
		this.documentosCargados = documentosCargados;
	}
	
	public String getUrlCarga() {
		return urlCarga;
	}

	public void setUrlCarga(String urlCarga) {
		this.urlCarga = urlCarga;
	}
	
	

	
	
	
	
	
	
}

package com.hildebrando.visado.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedProperty;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.DocumentosPDF;
import com.hildebrando.visado.dto.Estado;
import com.hildebrando.visado.dto.FormatosDTO;
import com.hildebrando.visado.dto.OperacionesPDF;
import com.hildebrando.visado.dto.PersonaPDF;
import com.hildebrando.visado.dto.SolicitudPDF;
import com.hildebrando.visado.mb.CombosMB;
import com.hildebrando.visado.mb.ConsultarSolicitudMB;
import com.hildebrando.visado.mb.RegistroUtilesMB;
import com.hildebrando.visado.mb.SolicitudRegistroMB;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

@Controller
@RequestMapping("/main")
public class JasperController {
	
	private static Logger log =  Logger.getLogger(JasperController.class);
	
	@ManagedProperty(value = "#{consultarSolicitudMB}")
	private ConsultarSolicitudMB consultarSolicitudMB;
	
	public JasperController() {
		log.info("Estoy en el Constructor JasperController");
	
	}
	
	@RequestMapping(value="/download/pdfReportCartaAtencion.htm", method=RequestMethod.GET)
	public String generarReporteCartaAtencion(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		log.info("generarReporteCartaAtencion : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		
		
		
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
		uno.setNumeroDiasForEjecucion("NO DEFINIDO");
		uno.setInstrucciones(SOLICITUD_TEMP.getObs());
		uno.setOficina(SOLICITUD_TEMP.getTiivsOficina1().getCodOfi()+" - " +SOLICITUD_TEMP.getTiivsOficina1().getDesOfi());
		
		
		
	    //Add lista datasource
		List<TiivsSolicitudOperban> listaOperacionesBancarias=new ArrayList<TiivsSolicitudOperban>();
		listaOperacionesBancarias=SOLICITUD_TEMP.getLstSolicBancarias();
		log.info("getLstSolicBancarias.size : "+SOLICITUD_TEMP.getLstSolicBancarias().size());
	/*	TiivsSolicitudOperban x =new TiivsSolicitudOperban();
		x.setImporte(35.0);
		x.setsDescMoneda("soles");
		x.setTipoCambio(2.0);
		listaOperacionesBancarias.add(x);*/
	     JRDataSource dsOperacion = new JRBeanCollectionDataSource(listaOperacionesBancarias);
	    List<JRDataSource> lstDsSolicitudOperban = new ArrayList<JRDataSource>();
	    lstDsSolicitudOperban.add(dsOperacion);
	    uno.setListaSolicitudOperban(listaOperacionesBancarias);
	    cabecera.add(uno);
	    
	    
    	
		   	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Carta_Atencion.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
              

        
        modelMap.put("dataKey", objCab);
        modelMap.put("SUBREPORT_DIR", "C:\\ARCHIVOS_SAMIRA_HILDE\\WORKSPACES\\VISADO_01\\VisadoPoderes\\resources\\jasper\\");
        modelMap.put("IMG_CABECERA", "C:\\ARCHIVOS_SAMIRA_HILDE\\WORKSPACES\\VISADO_01\\VisadoPoderes\\WebContent\\resources\\images\\bbva2.gif");

        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReportCartaAtencion");
	}
	
	@RequestMapping(value="/download/pdfReportCartaRechazo.htm", method=RequestMethod.GET)
	public String generarReporteCartaRechazo(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
	{
		log.info("generarReporteCartaRechazo : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
		
		if (SOLICITUD_TEMP.getObs()!=null)
		{	
			uno.setInstrucciones(SOLICITUD_TEMP.getObs());
		}
		else
		{
			uno.setInstrucciones("");
		}
		uno.setPoderdantes("VASQUEZ YAIPEN ALICIA MARIA");
		
	    cabecera.add(uno);
    	   	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Carta_Rechazo.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
        
        modelMap.put("dataKey", objCab);
        modelMap.put("SUBREPORT_DIR", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\resources\\jasper\\");
        
        modelMap.put("IMG_CABECERA", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\WebContent\\resources\\images\\bbva2.gif");

        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReportCartaRechazo");
	}
	
	@RequestMapping(value="/download/pdfReportCartaImprocedente.htm", method=RequestMethod.GET)
	public String generarReporteCartaImprocedente(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
	{
		log.info("generarReporteCartaImprocedente : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
		
		if (SOLICITUD_TEMP.getObs()!=null)
		{	
			uno.setInstrucciones(SOLICITUD_TEMP.getObs());
		}
		else
		{
			uno.setInstrucciones("");
		}
		uno.setPoderdantes("VASQUEZ YAIPEN ALICIA MARIA");
		
	    cabecera.add(uno);
    	   	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Carta_Improcedente.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
        
        modelMap.put("dataKey", objCab);
        modelMap.put("SUBREPORT_DIR", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\resources\\jasper\\");
        
        modelMap.put("IMG_CABECERA", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\WebContent\\resources\\images\\bbva2.gif");

        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReportCartaImprocedente");
	}
	
    @RequestMapping(value="/download/pdfReportSolicitudVisado.htm", method=RequestMethod.GET)
    public String generarReporteSolicitudVisado(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) 
    {
    	log.info("generarReporteSolicitudVisado");
    	
    	String sCodSolicitud = "";
    	
    	TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
    	List<DocumentoTipoSolicitudDTO> lstDocumentos = SOLICITUD_TEMP.getLstDocumentos();
    	List<TiivsSolicitudOperban> lstSolicBancarias =  SOLICITUD_TEMP.getLstSolicBancarias();
    	
    	List<AgrupacionSimpleDto> lstAgrupacionSimpleDto = SOLICITUD_TEMP.getLstAgrupacionSimpleDto();    	
    	
    	List<SolicitudPDF> cabecera = new ArrayList<SolicitudPDF>();
    	SolicitudPDF solicitudPDF= new SolicitudPDF();
    	
    	
    	//Cabecera del reporte
    	if(SOLICITUD_TEMP!=null){
    		solicitudPDF.setCodSoli(SOLICITUD_TEMP.getCodSoli());
    		solicitudPDF.setNroVoucher(SOLICITUD_TEMP.getNroVoucher());    		

    		TiivsMultitabla multi = RegistroUtilesMB.getRowFromMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS, SOLICITUD_TEMP.getEstado());
    		String sEstado = multi.getValor1();
    		
    		solicitudPDF.setEstado(sEstado);    		
    		solicitudPDF.setComision(String.valueOf(SOLICITUD_TEMP.getComision()));
    		solicitudPDF.setOficina(SOLICITUD_TEMP.getTiivsOficina1().getDesOfi());
    		solicitudPDF.setImporte(String.valueOf(SOLICITUD_TEMP.getImporte()));    	
    		solicitudPDF.setTipoServicio(SOLICITUD_TEMP.getTiivsTipoSolicitud().getDesTipServicio());
    		
    		solicitudPDF.setLstAgrupacionSimpleDto(lstAgrupacionSimpleDto); //agregar agrupaciones
    		
    		solicitudPDF.setLstDocumentos(lstDocumentos); //agregar documentos
    		
    		List<OperacionesPDF> lstOperaciones = new ArrayList<OperacionesPDF> ();
    		
    		for(TiivsSolicitudOperban op : lstSolicBancarias){    			
    			OperacionesPDF oper = new OperacionesPDF(
    					op.getsItem(), 
    					op.getTiivsOperacionBancaria().getDesOperBan(), 
    					op.getsDescMoneda(), 
    					op.getImporte(),
    					op.getTipoCambio(),
    					op.getImporteSoles());    			
    			lstOperaciones.add(oper);    					    			
    		}
    		
    		solicitudPDF.setLstOperaciones(lstOperaciones); //agregar operaciones
    		
    		
    	} 
    	
    	cabecera.add(solicitudPDF);
    	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"SolVisado.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
              
        modelMap.put("dataKey", objCab);        

        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReport");
    }
    
    
    @RequestMapping(value="/download/reportPDF.htm", method=RequestMethod.GET)
    public String generarReportePDF(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) 
    {
    	log.info("generarReportePDF : ");
    	/*List<Log4jdata> lista = new ArrayList<Log4jdata>();
        HttpSession session=request.getSession(true);
        
	    if(session.getAttribute("lstLogger")!=null){
	    	lista  = (List<Log4jdata>) session.getAttribute("lstLogger");
		}*/
    	
    	//Cabecera del reporte
    	List<SolicitudPDF> cabecera = new ArrayList<SolicitudPDF>();
    	SolicitudPDF tmp= new SolicitudPDF();
    	tmp.setCodSoli("5555555555555");
    	tmp.setNroVoucher("1233-444444444");
    	tmp.setEstado("Registrado");
    	tmp.setComision(String.valueOf(150.00));
    	tmp.setOficina("416-Oficina Larco Mar");
    	tmp.setImporte(String.valueOf(550.00));
    	
    	cabecera.add(tmp);
    	
    	//Seccion Poderdantes / Apoderados
//    	 List<PersonaPDF> listPersonas = new ArrayList<PersonaPDF>();
//	    		    
//	    	listPersonas.add(new PersonaPDF("DNI: 43086804","Beneficiario","Apoderado","999851455","email@email.com","00430844  Vargas Diaz Cynthia/","Activo"));
//	  	    listPersonas.add(new PersonaPDF("DNI: 43082805","Fallecido","Poderdante","","","00430899  Torres Morales Carlos/","Activo"));
//	  	    
//	  	    listPersonas.add(new PersonaPDF("12345678","Beneficiario","Poderdante","1111","e@i","Alberto","Activo"));
//	  	    listPersonas.add(new PersonaPDF("22345678","Beneficiario","Poderdante","1111","e@i","Juan","Activo"));
//	  	    listPersonas.add(new PersonaPDF("32345678","Beneficiario","Poderdante","1111","e@i","Tirza","Activo"));
//	  	    listPersonas.add(new PersonaPDF("42345678","Beneficiario","Poderdante","1111","e@i","Julisa","Activo"));
//	  	    listPersonas.add(new PersonaPDF("52345678","Beneficiario","Poderdante","1111","e@i","Antonia","Activo"));
//	    	
//	    	
//	    List<PersonaPDF> listPersonas2 = new ArrayList<PersonaPDF>();
//	    	listPersonas2.add(new PersonaPDF("10000000","Fallecido","Apoderado","1111","e@i","Alberto","Activo"));
//	  	    listPersonas2.add(new PersonaPDF("20000000","Fallecido","Apoderado","1111","e@i","Juan","Activo"));
//	  	    listPersonas2.add(new PersonaPDF("30000000","Fallecido","Apoderado","1111","e@i","Tirza","Activo"));
//	  	    listPersonas2.add(new PersonaPDF("40000000","Fallecido","Apoderado","1111","e@i","Julisa","Activo"));
//	  	    listPersonas2.add(new PersonaPDF("50000000","Fallecido","Apoderado","1111","e@i","Antonia","Activo"));
//	  	    
//	  	    
//	  	List<PersonaPDF> listPersonas3 = new ArrayList<PersonaPDF>();
//	    	listPersonas3.add(new PersonaPDF("77777771","Fallecido","Apoderado","1111","e@i","Alberto","Activo"));
//	  	    listPersonas3.add(new PersonaPDF("77777772","Fallecido","Apoderado","1111","e@i","Juan","Activo"));
//	  	    listPersonas3.add(new PersonaPDF("77777773","Fallecido","Apoderado","1111","e@i","Tirza","Activo"));
//	  	    listPersonas3.add(new PersonaPDF("77777774","Fallecido","Apoderado","1111","e@i","Julisa","Activo"));
//	  	    listPersonas3.add(new PersonaPDF("77777775","Fallecido","Apoderado","1111","e@i","Antonia","Activo"));   
//	  	    
//	    List<PersonaPDF> listPersonas4 = new ArrayList<PersonaPDF>();
//	    	listPersonas4.add(new PersonaPDF("88888881","Fallecido","Apoderado","1111","e@i","Alberto","Activo"));
//	  	    listPersonas4.add(new PersonaPDF("88888882","Fallecido","Apoderado","1111","e@i","Juan","Activo"));
//	  	    listPersonas4.add(new PersonaPDF("88888883","Fallecido","Apoderado","1111","e@i","Tirza","Activo"));
//	  	    listPersonas4.add(new PersonaPDF("88888884","Fallecido","Apoderado","1111","e@i","Julisa","Activo"));
//	  	    listPersonas4.add(new PersonaPDF("88888885","Fallecido","Apoderado","1111","e@i","Antonia","Activo"));
//	  	    
//	   List<PersonaPDF> listPersonas5 = new ArrayList<PersonaPDF>();
//	    	listPersonas5.add(new PersonaPDF("55555551","Fallecido","Apoderado","1111","e@i","Alberto","Activo"));
//	  	    listPersonas5.add(new PersonaPDF("55555552","Fallecido","Apoderado","1111","e@i","Juan","Activo"));
//	  	    listPersonas5.add(new PersonaPDF("55555553","Fallecido","Apoderado","1111","e@i","Tirza","Activo"));
//	  	    listPersonas5.add(new PersonaPDF("55555554","Fallecido","Apoderado","1111","e@i","Julisa","Activo"));
//	  	    listPersonas5.add(new PersonaPDF("55555555","Fallecido","Apoderado","1111","e@i","Antonia","Activo"));
//	    	
//	  	    
//	    cabecera.get(0).setLstPersonas(listPersonas);
	    
	    //Seccion documentos
//	    List<DocumentosPDF> lstDocumentos = new ArrayList<DocumentosPDF>();	    
//	    lstDocumentos.add(new DocumentosPDF("001","Documento 1"));
//	    lstDocumentos.add(new DocumentosPDF("002","DNI"));
//	    lstDocumentos.add(new DocumentosPDF("004","RUC"));
//	    lstDocumentos.add(new DocumentosPDF("005","Partida de fallecimiento"));
//	    
//	    cabecera.get(0).setLstDocumentos(lstDocumentos);
	    
	    
	    //Secci�n operaciones bancarias
//	    List<OperacionesPDF> lstOperaciones = new ArrayList<OperacionesPDF>();
//	    lstOperaciones.add(new OperacionesPDF("001","Retiro","Soles","260","1","260"));
//	    lstOperaciones.add(new OperacionesPDF("002","Cobro","Dolares","100","2.60","260"));
//	    lstOperaciones.add(new OperacionesPDF("003","Pago","Euros","500","3.50","1725"));
//	    
//	    cabecera.get(0).setLstOperaciones(lstOperaciones);
//	    
	    
	    
	    //Add lista datasource
//	    JRDataSource dsPersona1 = new JRBeanCollectionDataSource(listPersonas);
//	    JRDataSource dsPersona2 = new JRBeanCollectionDataSource(listPersonas2);
//	    JRDataSource dsPersona3 = new JRBeanCollectionDataSource(listPersonas3);
//	    JRDataSource dsPersona4 = new JRBeanCollectionDataSource(listPersonas4);
//	    JRDataSource dsPersona5 = new JRBeanCollectionDataSource(listPersonas5);
	    
//	    List<JRDataSource> lstDsPersonas = new ArrayList<JRDataSource>();
//	    lstDsPersonas.add(dsPersona1);
//	    lstDsPersonas.add(dsPersona2);
//	    lstDsPersonas.add(dsPersona3);
//	    lstDsPersonas.add(dsPersona4);
//	    lstDsPersonas.add(dsPersona5);
	    
//	    cabecera.get(0).setLstDsPersonas(lstDsPersonas);
	    
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"SolVisado.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
              
        modelMap.put("dataKey", objCab);
        modelMap.put("SUBREPORT_DIR", "D:\\WorkSpace\\VisadoPoderes\\resources\\jasper\\");
        

        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReport");
    }

	public ConsultarSolicitudMB getConsultarSolicitudMB() {
		return consultarSolicitudMB;
	}

	public void setConsultarSolicitudMB(ConsultarSolicitudMB consultarSolicitudMB) {
		this.consultarSolicitudMB = consultarSolicitudMB;
	}
    
}

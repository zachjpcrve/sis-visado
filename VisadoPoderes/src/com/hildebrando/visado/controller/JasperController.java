package com.hildebrando.visado.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedProperty;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
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
	
	private static Logger logger =  Logger.getLogger(JasperController.class);
	
	@ManagedProperty(value = "#{consultarSolicitudMB}")
	private ConsultarSolicitudMB consultarSolicitudMB;
	
	public JasperController() {

	}
	
	@RequestMapping(value="/download/pdfReportCartaAtencion.htm", method=RequestMethod.GET)
	public String generarReporteCartaAtencion(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		logger.info("generarReporteCartaAtencion : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		
		if(SOLICITUD_TEMP==null){
			logger.info("Solicitud nula");
			return null;
		}
		
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
		uno.setNumeroDiasForEjecucion("NO DEFINIDO");
		uno.setInstrucciones(SOLICITUD_TEMP.getObs());
		uno.setOficina(SOLICITUD_TEMP.getTiivsOficina1().getCodOfi()+" - " +SOLICITUD_TEMP.getTiivsOficina1().getDesOfi());
		
		
		
	    //Add lista datasource
		List<TiivsSolicitudOperban> listaOperacionesBancarias=new ArrayList<TiivsSolicitudOperban>();
		listaOperacionesBancarias=SOLICITUD_TEMP.getLstSolicBancarias();
		logger.info("getLstSolicBancarias.size : "+SOLICITUD_TEMP.getLstSolicBancarias().size());
	     JRDataSource dsOperacion = new JRBeanCollectionDataSource(listaOperacionesBancarias);
	    List<JRDataSource> lstDsSolicitudOperban = new ArrayList<JRDataSource>();
	    lstDsSolicitudOperban.add(dsOperacion);
	    uno.setListaSolicitudOperban(listaOperacionesBancarias);
	    cabecera.add(uno);
	    
	    
    	
		   	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Carta_Atencion.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
              

        
        modelMap.put("dataKey", objCab);
        //modelMap.put("SUBREPORT_DIR", "C:\\ARCHIVOS_SAMIRA_HILDE\\WORKSPACES\\VISADO_01\\VisadoPoderes\\resources\\jasper\\");
        //modelMap.put("IMG_CABECERA", "C:\\ARCHIVOS_SAMIRA_HILDE\\WORKSPACES\\VISADO_01\\VisadoPoderes\\WebContent\\resources\\images\\bbva2.gif");

        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReportCartaAtencion");
	}
	@RequestMapping(value="/download/pdfReportCartaSolicitudRevision.htm", method=RequestMethod.GET)
	public String generarReporteCartaSolicitudRevision(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		logger.info("generarReporteCartaAtencion : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
		uno.setInstrucciones(SOLICITUD_TEMP.getObs());
	    cabecera.add(uno);
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Solicitud_Revision.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
              

        
        modelMap.put("dataKey", objCab);
        

        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReportCartaSolicitudRevision");
	}
	@RequestMapping(value="/download/pdfReportCartaRechazo.htm", method=RequestMethod.GET)
	public String generarReporteCartaRechazo(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
	{
		logger.info("generarReporteCartaRechazo : ");
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

//        modelMap.put("SUBREPORT_DIR", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\resources\\jasper\\");        
//        modelMap.put("IMG_CABECERA", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\WebContent\\resources\\images\\bbva2.gif");


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
		logger.info("generarReporteCartaImprocedente : ");
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

//        modelMap.put("SUBREPORT_DIR", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\resources\\jasper\\");        
//        modelMap.put("IMG_CABECERA", "C:\\hildebrando\\BBVA\\ws_visado\\VisadoPoderes\\WebContent\\resources\\images\\bbva2.gif");


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
    	logger.info("generarReporteSolicitudVisado");
    	
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
    		solicitudPDF.setComision(SOLICITUD_TEMP.getComision());
    		solicitudPDF.setOficina(SOLICITUD_TEMP.getTiivsOficina1().getDesOfi());
    		solicitudPDF.setTerritorio(SOLICITUD_TEMP.getTiivsOficina1().getTiivsTerritorio().getCodTer() + SOLICITUD_TEMP.getTiivsOficina1().getTiivsTerritorio().getDesTer());
    		solicitudPDF.setImporte(SOLICITUD_TEMP.getImporte());    	
    		solicitudPDF.setTipoServicio(SOLICITUD_TEMP.getTiivsTipoSolicitud().getDesTipServicio());
    		
    		if(SOLICITUD_TEMP.getMoneda().equalsIgnoreCase(ConstantesVisado.MONEDAS.COD_SOLES)){
    			solicitudPDF.setMoneda(ConstantesVisado.MONEDAS.SIMBOLO_SOLES);
    		} else if (SOLICITUD_TEMP.getMoneda().equalsIgnoreCase(ConstantesVisado.MONEDAS.COD_DOLAR)){
    			solicitudPDF.setMoneda(ConstantesVisado.MONEDAS.SIMBOLO_DOLAR);
    		} else if (SOLICITUD_TEMP.getMoneda().equalsIgnoreCase(ConstantesVisado.MONEDAS.COD_EUROS)){
    			solicitudPDF.setMoneda(ConstantesVisado.MONEDAS.SIMBOLO_EURO);
    		}
    		
    		
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
    		cabecera.add(solicitudPDF);
        	        	
    		String nombreSalida = ConstantesVisado.PREFIJO_NOMBRE_SOLICITUD_VISADO
    				+ "_" + SOLICITUD_TEMP.getCodSoli() + "_"
    				+ Utilitarios.formatoFechaHora(new Date()) + ".pdf";
        	
            response.setHeader("Content-type", "application/pdf");
            response.setHeader("Content-Disposition","attachment; filename=\"" + nombreSalida + "\"");
    		
            JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
                  
            modelMap.put("dataKey", objCab);
            
            try {
            	OutputStream os = response.getOutputStream();
            	os.flush();
    		} catch (IOException ioe) {
    			logger.error("Error al generar documento",ioe);
    		}
    		
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

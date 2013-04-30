package com.hildebrando.visado.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ComisionDTO;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.FormatosDTO;
import com.hildebrando.visado.dto.OperacionesPDF;
import com.hildebrando.visado.dto.SolicitudPDF;
import com.hildebrando.visado.mb.RegistroUtilesMB;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

@Controller
@RequestMapping("/main")
public class JasperController {
	
	private static Logger logger =  Logger.getLogger(JasperController.class);
	
	public JasperController() {

	}
	
	@RequestMapping(value="/download/pdfReportCartaAtencion.htm", method=RequestMethod.GET)
	public String generarReporteCartaAtencion(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		logger.info("==== generarReporteCartaAtencion ==== ");
		try {
			TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");

			if (SOLICITUD_TEMP == null) {
				logger.info("La solicitud es nula");
				return null;
			}
			
			GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
			filtroMultitabla.add(Restrictions.eq("id.codMult", ConstantesVisado.DIAS_FOR_EJECUCION.CODIGO_MULTITABLA_DIAS));
			List<TiivsMultitabla> lstMultitabla=new ArrayList<TiivsMultitabla>();
			try {
				lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
				logger.info("lstMultitabla  :::" +lstMultitabla.size());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String diasUtilesForEjecucion="";
			//Se obtiene los dias utiles de la Multitabla
			for (TiivsMultitabla tmp : lstMultitabla) 
			{
				if (tmp.getId().getCodElem().trim().equals(ConstantesVisado.DIAS_FOR_EJECUCION.COD_DIAS_FOR_EJECUCION)) 
				{
					diasUtilesForEjecucion = tmp.getValor2();
					logger.info("diasUtilesForEjecucion :::" +diasUtilesForEjecucion);
					break;
				}
			}

			List<FormatosDTO> cabecera = new ArrayList<FormatosDTO>();
			FormatosDTO uno = new FormatosDTO();
			uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
			uno.setNumeroDiasForEjecucion(diasUtilesForEjecucion);
			
			String sInstr = obtenerInstrucciones(SOLICITUD_TEMP, ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
			if(sInstr.equals("")){
				sInstr = obtenerInstrucciones(SOLICITUD_TEMP, ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02);
			}			
			uno.setInstrucciones(sInstr);
			uno.setOficina(SOLICITUD_TEMP.getTiivsOficina1().getCodOfi()+ " - " + SOLICITUD_TEMP.getTiivsOficina1().getDesOfi());

			// Add lista datasource
			List<OperacionesPDF> lstOperaciones = new ArrayList<OperacionesPDF>();
			for (TiivsSolicitudOperban op : SOLICITUD_TEMP.getLstSolicBancarias()) {
				OperacionesPDF oper = new OperacionesPDF(op.getsItem(), op.getTiivsOperacionBancaria().getDesOperBan(),
						op.getId().getsDescMoneda(), op.getImporte(),
						op.getTipoCambio(), op.getImporteSoles());
				lstOperaciones.add(oper);
			}

			uno.setLstOperaciones(lstOperaciones); // agregar operaciones

			cabecera.add(uno);
			response.setHeader("Content-type", "application/pdf");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"Carta_Atencion.pdf\"");

			JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(
					cabecera, false);
			modelMap.put("dataKey", objCab);

			OutputStream os = response.getOutputStream();
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR+ "al generar el archivo: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR+ "al generar el archivo: " + e);
		}
		return ("pdfReportCartaAtencion");
	}
	
	@RequestMapping(value="/download/pdfReportCartaSolicitudRevision.htm", method=RequestMethod.GET)
	public String generarReporteCartaSolicitudRevision(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		try{
		logger.info("generarReporteCartaAtencion : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());	
		uno.setInstrucciones(obtenerInstrucciones(SOLICITUD_TEMP, ConstantesVisado.ESTADOS.ESTADO_COD_EN_REVISION_T02));
		
	    cabecera.add(uno);
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Solicitud_Revision.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
              

        
        modelMap.put("dataKey", objCab);
        

       
        	OutputStream os = response.getOutputStream();
        	os.flush();
    	} catch (IOException ioe) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+ioe);
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+e);
		}
        return("pdfReportCartaSolicitudRevision");
	}
	
	@RequestMapping(value="/download/pdfReportCartaRechazo.htm", method=RequestMethod.GET)
	public String generarReporteCartaRechazo(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
	{   try{
		logger.info("generarReporteCartaRechazo : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		logger.info("SOLICITUD_TEMP.getLstAgrupacionSimpleDto() "+SOLICITUD_TEMP.getLstAgrupacionSimpleDto().size() );
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
		
		uno.setInstrucciones(obtenerInstrucciones(SOLICITUD_TEMP, ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02));		
		
		String cadenaPoderdantes = obtenerPoderdantesxSolicitud(SOLICITUD_TEMP);
		
		logger.info("lista poderdantes:" + cadenaPoderdantes);
		
		uno.setPoderdantes(cadenaPoderdantes);
		
	    cabecera.add(uno);
    	   	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Carta_Rechazo.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
        
        modelMap.put("dataKey", objCab);

        
        	OutputStream os = response.getOutputStream();
        	os.flush();
    	} catch (IOException ioe) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+ioe);
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+e);
		}
        return("pdfReportCartaRechazo");
	}
	
	@RequestMapping(value="/download/pdfReportCartaImprocedente.htm", method=RequestMethod.GET)
	public String generarReporteCartaImprocedente(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
	{
		try{
		logger.info("generarReporteCartaImprocedente : ");
		TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
		logger.info("SOLICITUD_TEMP.getLstAgrupacionSimpleDto() "+SOLICITUD_TEMP.getLstAgrupacionSimpleDto().size() );
		List<FormatosDTO> cabecera=new ArrayList<FormatosDTO>();
		FormatosDTO uno = new FormatosDTO();
		uno.setNumeroSolicitud(SOLICITUD_TEMP.getCodSoli());
		
		
		uno.setInstrucciones(obtenerInstrucciones(SOLICITUD_TEMP, ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02));
				
		String cadenaPoderdantes = obtenerPoderdantesxSolicitud(SOLICITUD_TEMP);
		
		logger.info("lista poderdantes:" + cadenaPoderdantes);
		
		uno.setPoderdantes(cadenaPoderdantes);
		
	    cabecera.add(uno);
    	   	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"Carta_Improcedente.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
        
        modelMap.put("dataKey", objCab);


      
        	OutputStream os = response.getOutputStream();
        	os.flush();
    	} catch (IOException ioe) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+ioe);
		}catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+e);
		}
        return("pdfReportCartaImprocedente");
	}
	
    @SuppressWarnings("unused")
	@RequestMapping(value="/download/pdfReportSolicitudVisado.htm", method=RequestMethod.GET)
    public String generarReporteSolicitudVisado(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) 
    {
    	logger.info("==== generarReporteSolicitudVisado() ====");
    	
    	TiivsSolicitud SOLICITUD_TEMP = (TiivsSolicitud) request.getSession(true).getAttribute("SOLICITUD_TEMP");
    	List<DocumentoTipoSolicitudDTO> lstDocumentos = SOLICITUD_TEMP.getLstDocumentos();
    	List<TiivsSolicitudOperban> lstSolicBancarias =  SOLICITUD_TEMP.getLstSolicBancarias();
    	
    	List<AgrupacionSimpleDto> lstAgrupacionSimpleDto = SOLICITUD_TEMP.getLstAgrupacionSimpleDto();    	
    	
    	List<SolicitudPDF> cabecera = new ArrayList<SolicitudPDF>();
    	SolicitudPDF solicitudPDF= new SolicitudPDF();
    	
    	
    	//Cabecera del reporte
    	if(SOLICITUD_TEMP!=null){
    		 try {
    		logger.info("Solicitidu-CodSolicitud: "+SOLICITUD_TEMP.getCodSoli());
    		logger.info("Solicitidu-NroVoucher: "+SOLICITUD_TEMP.getNroVoucher());
    		solicitudPDF.setCodSoli(SOLICITUD_TEMP.getCodSoli());
    		solicitudPDF.setNroVoucher(SOLICITUD_TEMP.getNroVoucher());    		

    		TiivsMultitabla multi = RegistroUtilesMB.getRowFromMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS, SOLICITUD_TEMP.getEstado());
    		String sEstado = multi.getValor1();
    		logger.info("Solicitidu-Estado: "+sEstado);
    		solicitudPDF.setEstado(sEstado);    		
    		solicitudPDF.setComision(SOLICITUD_TEMP.getComision());
    		solicitudPDF.setOficina(SOLICITUD_TEMP.getTiivsOficina1().getDesOfi());
    		solicitudPDF.setTerritorio(SOLICITUD_TEMP.getTiivsOficina1().getTiivsTerritorio().getCodTer() + SOLICITUD_TEMP.getTiivsOficina1().getTiivsTerritorio().getDesTer());
    		solicitudPDF.setImporte(SOLICITUD_TEMP.getImporte());    	
    		solicitudPDF.setTipoServicio(SOLICITUD_TEMP.getTiivsTipoSolicitud().getDesTipServicio());
    		logger.info("Solicitidu-Moneda: "+SOLICITUD_TEMP.getMoneda());
    		if(SOLICITUD_TEMP.getMoneda().equalsIgnoreCase(ConstantesVisado.MONEDAS.COD_SOLES)){
    			solicitudPDF.setMoneda(ConstantesVisado.MONEDAS.SIMBOLO_SOLES);
    		} else if (SOLICITUD_TEMP.getMoneda().equalsIgnoreCase(ConstantesVisado.MONEDAS.COD_DOLAR)){
    			solicitudPDF.setMoneda(ConstantesVisado.MONEDAS.SIMBOLO_DOLAR);
    		} else if (SOLICITUD_TEMP.getMoneda().equalsIgnoreCase(ConstantesVisado.MONEDAS.COD_EUROS)){
    			solicitudPDF.setMoneda(ConstantesVisado.MONEDAS.SIMBOLO_EURO);
    		}
    		    		
    		logger.info("Solicitid-Lista Agrupacion-size: "+lstAgrupacionSimpleDto.size());
    		solicitudPDF.setLstAgrupacionSimpleDto(lstAgrupacionSimpleDto); //agregar agrupaciones    		
    		solicitudPDF.setLstDocumentos(lstDocumentos); //agregar documentos    		
    		List<OperacionesPDF> lstOperaciones = new ArrayList<OperacionesPDF> ();    		
    		for(TiivsSolicitudOperban op : lstSolicBancarias){    			
    			OperacionesPDF oper = new OperacionesPDF(
    					op.getsItem(), 
    					op.getTiivsOperacionBancaria().getDesOperBan(), 
    					op.getId().getsDescMoneda(), 
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
        	logger.info("nombreSalida: "+nombreSalida);
        	
            response.setHeader("Content-type", "application/pdf");
            response.setHeader("Content-Disposition","attachment; filename=\"" + nombreSalida + "\"");
    		
            JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
                  
            modelMap.put("dataKey", objCab);
            
           
            	OutputStream os = response.getOutputStream();
            	os.flush();
    		} catch (IOException ioe) {
    			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+ioe);
    		}catch (Exception e) {
    			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al generar el archivo: "+e);
			}
    		
    	}else{
    		logger.info("La solicitud es NULA ");
    	}
    	logger.info("=== saliendo de generarReporteSolicitudVisado() ==");
       
        return("pdfReport");
    }
        
    @SuppressWarnings("unused")
	@RequestMapping(value="/download/pdfReportObsHistorial.htm", method=RequestMethod.GET)
	public String generarReporteObsHistorial(@RequestParam("id") String id, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		logger.info("==== generarReporteObsHistorial ==== ");
		try {

			String codSoli = "";
			String movimiento = "";
			codSoli = id.split(";")[0];
			movimiento  = id.split(";")[1];
			
			logger.info("codSoli   :" + codSoli);
			logger.info("movimiento:" + movimiento);
			
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
			filtroHist.add(Restrictions.eq("id.codSoli", codSoli));
			filtroHist.add(Restrictions.eq("id.movimiento", movimiento));
		
			List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
			lstHist = histDAO.buscarDinamico(filtroHist);
			
			String obs = "";
			Timestamp fechaComentario = null;
			
			String nombrePDF =  "Observacion_" +codSoli+"_" + movimiento + ".pdf";
			if(lstHist.size()>0){
				obs = lstHist.get(0).getObs();
				fechaComentario = lstHist.get(0).getFecha();
			}
			
			List<FormatosDTO> cabecera = new ArrayList<FormatosDTO>();
			FormatosDTO uno = new FormatosDTO();
			
			uno.setObservaciones(obs);	
			uno.setFechaHistorial(fechaComentario);

			cabecera.add(uno);
			response.setHeader("Content-type", "application/pdf");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + nombrePDF + "\"");

			JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
			modelMap.put("dataKey", objCab);

			OutputStream os = response.getOutputStream();
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR
					+ "al generar el archivo: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR
					+ "al generar el archivo: " + e);
		}
		return ("pdfReportObsHistorial");
	}

    
    
    @SuppressWarnings("unused")
	@RequestMapping(value="/download/calculoComision.htm", method=RequestMethod.GET)
	public String generarReporteComision(@RequestParam("param") String param, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
		logger.info("==== generarReporteComision ==== ");
		try {

			String []parametros = param.split(";");
			List<ComisionDTO> cabecera = new ArrayList<ComisionDTO>();			
			ComisionDTO uno = new ComisionDTO();						
			try {			
				uno.setItem1(parametros[0]);
				uno.setItem2(parametros[1]);
				uno.setItem3(parametros[2]);
				uno.setItem4(parametros[3]);
				Double importeLimite = Double.valueOf(parametros[4]);
				Double importe = Double.valueOf(parametros[5]);
				
				uno.setImporte(importe);
				uno.setImporteLimite(importeLimite);
			} catch (Exception e) {
				
			}
			
			cabecera.add(uno);
			response.setHeader("Content-type", "application/pdf");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + "comision" + "\"");

			JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
			modelMap.put("dataKey", objCab);

			OutputStream os = response.getOutputStream();
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR
					+ "al generar el archivo: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(ConstantesVisado.MENSAJE.OCURRE_ERROR
					+ "al generar el archivo: " + e);
		}
		return ("pdfReportComision");
	}

    
    private String obtenerInstrucciones(TiivsSolicitud solicitud, String codigoEstado) {
		
		String sInstrucciones="";
		
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
			
		GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
		filtroHist.add(Restrictions.eq("id.codSoli", solicitud.getCodSoli()));
		filtroHist.add(Restrictions.eq("estado", codigoEstado));
		filtroHist.addOrder(Order.desc("id.movimiento"));
		
		List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
		try {
			lstHist = histDAO.buscarDinamico(filtroHist);
			if(lstHist.size()>0){
				sInstrucciones = lstHist.get(0).getObs();
			}
		} catch (Exception e) {
			logger.debug(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT + "al obtener Historial",e);
		}
		
		return sInstrucciones;
	}

	private String obtenerPoderdantesxSolicitud(TiivsSolicitud SOLICITUD_TEMP)
	{
		String resultado="";
		
		try {
			
			if (SOLICITUD_TEMP!=null)
			{
				String solicitud = SOLICITUD_TEMP.getCodSoli();
				logger.info("Buscando poderdantes en la solicitud: " + solicitud);
				
				List<TiivsAgrupacionPersona> lstTMP = new ArrayList<TiivsAgrupacionPersona>();
				
				GenericDao<TiivsAgrupacionPersona, Object> solicDAO = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				Busqueda filtroAgrp = Busqueda.forClass(TiivsAgrupacionPersona.class);
				filtroAgrp.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD, solicitud));
				filtroAgrp.add(Restrictions.eq(ConstantesVisado.CAMPO_TIPO_PARTIC, ConstantesVisado.PODERDANTE));
				
				try {
					lstTMP = solicDAO.buscarDinamico(filtroAgrp);
					
				} catch (Exception ex) {
					logger.info("Error al buscar las agrupaciones de personas",ex);
				}
				
				for (TiivsAgrupacionPersona tmp: lstTMP)
				{
					if (tmp.getTiivsPersona().getNombre()!=null)
					{
						if (resultado.length()>0)
						{
							
							
							resultado = resultado.concat(",").concat(tmp.getTiivsPersona().getNombre().
									concat(ConstantesVisado.ESPACIO_BLANCO).concat(tmp.getTiivsPersona().getApePat()).
									concat(ConstantesVisado.ESPACIO_BLANCO).concat(tmp.getTiivsPersona().getApeMat())); 
							
							resultado = resultado.concat(",").concat(tmp.getTiivsPersona().getNombre());						
							if(tmp.getTiivsPersona().getApePat()!=null){
								resultado = resultado.concat(ConstantesVisado.ESPACIO_BLANCO).concat(tmp.getTiivsPersona().getApePat());
							}						
							if(tmp.getTiivsPersona().getApeMat()!=null){
								resultado = resultado.concat(ConstantesVisado.ESPACIO_BLANCO).concat(tmp.getTiivsPersona().getApeMat());
							}						
						}
						else
						{
							resultado = tmp.getTiivsPersona().getNombre();						
							if(tmp.getTiivsPersona().getApePat()!=null){
								resultado = resultado.concat(ConstantesVisado.ESPACIO_BLANCO).concat(tmp.getTiivsPersona().getApePat());
							}						
							if(tmp.getTiivsPersona().getApeMat()!=null){
								resultado = resultado.concat(ConstantesVisado.ESPACIO_BLANCO).concat(tmp.getTiivsPersona().getApeMat());
							}																		
						}
					}
				}
			}
			
			
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION + " al obtener Poderdantes", e);
		}
		
		return resultado;
	}

    
}
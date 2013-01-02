package com.hildebrando.visado.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

import com.hildebrando.visado.dto.PersonaPDF;
import com.hildebrando.visado.dto.SolicitudPDF;

@Controller
@RequestMapping("/main")
public class JasperController {
	
	private static Logger log =  Logger.getLogger(JasperController.class);
	
	public JasperController() {
		log.info("Estoy en el Constructor JasperController");
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
    	tmp.setCodSoli("12345678");
    	tmp.setNroVoucher("1233-444444444");
    	tmp.setEstado("Registrado");
    	tmp.setComision(String.valueOf(150.00));
    	tmp.setOficina("416-Oficina Larco Mar");
    	tmp.setImporte(String.valueOf(550.00));
    	cabecera.add(tmp);
    	
    	//Seccion Poderdantes / Apoderados
    	List<PersonaPDF> listPersonas = new ArrayList<PersonaPDF>();
	    	PersonaPDF persona = new PersonaPDF();
	    	persona.setNumDOI("DNI: 43086804");
	    	persona.setClasificacion("Beneficiario");
	    	persona.setTipoRegistro("Apoderado");
	    	persona.setCelular("999851455");
	    	persona.setEmail("email@email.com");
	    	persona.setCliente("00430844  Vargas Diaz Cynthia/");
	    	
	    	PersonaPDF persona2 = new PersonaPDF();
	    	persona2.setNumDOI("DNI: 43082804");
	    	persona2.setClasificacion("Fallecido");
	    	persona2.setTipoRegistro("Poderdante");
	    	persona2.setCelular("");
	    	persona2.setEmail("");
	    	persona2.setCliente("00430899  Torres Morales Carlos/");
    	
	    listPersonas.add(persona);
	    listPersonas.add(persona2);
	    	cabecera.get(0).setLstPersonas(listPersonas);
    	/*//Cabecera del reporte
    	List<TiivsSolicitud> cabecera = new ArrayList<TiivsSolicitud>();
    	TiivsSolicitud tmp= new TiivsSolicitud();
    	tmp.setsUrlSubReporte1("xxxxxxxxxxx");
    	tmp.setCodSoli("12345678");
    	tmp.setNroVoucher("1233-444444444");
    	tmp.setEstado("Registrado");
    	tmp.setComision(BigDecimal.valueOf(150.00));
    	tmp.setOficina("416-Oficina Larco Mar");
    	tmp.setImporte(BigDecimal.valueOf(550.00));
    	tmp.setEstadoApoderado("Activo");
    	cabecera.add(tmp);
    	
    	//Seccion Poderdantes / Apoderados
    	List<TiivsAgrupacionPersona> listPersonas = new ArrayList<TiivsAgrupacionPersona>();
	    	TiivsAgrupacionPersona grupoPersona = new TiivsAgrupacionPersona();
		    	TiivsPersona persona = new TiivsPersona();
		    	persona.setCodPer(1);
		    	persona.setNombre("Cynthia");
		    	persona.setApeMat("Vargas");
		    	persona.setApePat("Diaz");
		    	persona.setEmail("email@email.com");
		    	persona.setNumDoi("43086804");
		    	persona.setsDesctipPartic("Apoderado");
		    	persona.setsDescclasifPer("Beneficiario");
		    	persona.setCliente(persona.getNumDoi()+":"+persona.getApeMat()+" " + persona.getApePat() + " " + persona.getNombre());
		    	
		    	TiivsSolicitudAgrupacion solAgrup = new TiivsSolicitudAgrupacion();
			    TiivsSolicitud tmpSol= new TiivsSolicitud();
			    //tmpSol.setCodSoli("12345678");
			    
		    	solAgrup.setTiivsSolicitud(tmpSol);
		    	solAgrup.setActivo("Activo");
		    	
		    
		    grupoPersona.setTiivsPersona(persona);
		    
		    TiivsAgrupacionPersona grupoPersona2 = new TiivsAgrupacionPersona();
		    	TiivsPersona persona2 = new TiivsPersona();
		    	persona2.setCodPer(2);
		    	persona2.setNombre("Carlos");
		    	persona2.setApeMat("Torres");
		    	persona2.setApePat("Morales");
		    	persona2.setEmail("");
		    	persona2.setNumDoi("");
		    	persona2.setsDesctipPartic("Poderdante");
		    	persona2.setsDescclasifPer("Fallecido");
		    	persona2.setCliente(persona.getNumDoi()+":"+persona.getApeMat()+" " + persona.getApePat() + " " + persona.getNombre());
	    	
		    	TiivsSolicitudAgrupacion solAgrup2 = new TiivsSolicitudAgrupacion();
			    TiivsSolicitud tmpSol1= new TiivsSolicitud();
			    //tmpSol.setCodSoli("12345678");
			    
		    	solAgrup2.setTiivsSolicitud(tmpSol1);
		    	solAgrup2.setActivo("Activo");
	    
		    grupoPersona2.setTiivsPersona(persona2);
		    //grupoPersona2.setTiivsSolicitudAgrupacion(solAgrup2);
		    
		listPersonas.add(grupoPersona);
		listPersonas.add(grupoPersona2);*/
    	
		/*//Seccion Documentos
		List<TiivsTipoSolicDocumento> listTiposDoc = new ArrayList<TiivsTipoSolicDocumento>();
			TiivsTipoSolicDocumento tipoDoc = new TiivsTipoSolicDocumento();
			tipoDoc.setCodDoc("0009");
			tipoDoc.setDesDoc("Carta de solicitud");
			tipoDoc.setObligatorio('S');
			
			TiivsTipoSolicDocumento tipoDoc2 = new TiivsTipoSolicDocumento();
			tipoDoc2.setCodDoc("0150");
			tipoDoc2.setDesDoc("Certificado");
			tipoDoc2.setObligatorio('S');
			
			TiivsTipoSolicDocumento tipoDoc3 = new TiivsTipoSolicDocumento();
			tipoDoc3.setCodDoc("0001");
			tipoDoc3.setDesDoc("DOI del Apoderado");
			tipoDoc3.setObligatorio('S');
			
			TiivsTipoSolicDocumento tipoDoc4 = new TiivsTipoSolicDocumento();
			tipoDoc4.setCodDoc("0002");
			tipoDoc4.setDesDoc("Sirvase Ejecutar");
			tipoDoc4.setObligatorio('S');
			
			TiivsTipoSolicDocumento tipoDoc5 = new TiivsTipoSolicDocumento();
			tipoDoc5.setCodDoc("0003");
			tipoDoc5.setDesDoc("Voucher del cobro de comision o Documento de Exoneracion");
			tipoDoc5.setObligatorio('S');
			
			TiivsTipoSolicDocumento tipoDoc6 = new TiivsTipoSolicDocumento();
			tipoDoc6.setCodDoc("0004");
			tipoDoc6.setDesDoc("Impresion del formulario electronico de revision de poderes");
			tipoDoc6.setObligatorio('S');
			
			TiivsTipoSolicDocumento tipoDoc7 = new TiivsTipoSolicDocumento();
			tipoDoc7.setCodDoc("0001");
			tipoDoc7.setDesDoc("Documento adicional");
			tipoDoc7.setObligatorio('S');
		
		listTiposDoc.add(tipoDoc);
		listTiposDoc.add(tipoDoc2);
		listTiposDoc.add(tipoDoc3);
		listTiposDoc.add(tipoDoc4);
		listTiposDoc.add(tipoDoc5);
		listTiposDoc.add(tipoDoc6);
		listTiposDoc.add(tipoDoc7);*/
		
		/*//Seccion Operaciones Bancarias
		List<TiivsSolicitudOperban> listOpeBan = new ArrayList<TiivsSolicitudOperban>();
			TiivsSolicitudOperban operBan = new TiivsSolicitudOperban();
				TiivsOperacionBancaria tmpOpeBan = new TiivsOperacionBancaria();
				tmpOpeBan.setCodOperBan("0101");
				tmpOpeBan.setDesOperBan("Pago de cheque");
				
			operBan.setTiivsOperacionBancaria(tmpOpeBan);
			operBan.setMoneda("Dolares");
			operBan.setImporte(BigDecimal.valueOf(100.00));
			operBan.setTipoCambio(BigDecimal.valueOf(3.14));
			
			TiivsSolicitudOperban operBan2 = new TiivsSolicitudOperban();
				TiivsOperacionBancaria tmpOpeBan2 = new TiivsOperacionBancaria();
				tmpOpeBan2.setCodOperBan("0122");
				tmpOpeBan2.setDesOperBan("Pago de cheque");
			
			operBan2.setTiivsOperacionBancaria(tmpOpeBan);
			operBan2.setMoneda("Soles");
			operBan2.setImporte(BigDecimal.valueOf(100.00));
			
			TiivsSolicitudOperban operBan3 = new TiivsSolicitudOperban();
				TiivsOperacionBancaria tmpOpeBan3 = new TiivsOperacionBancaria();
				tmpOpeBan3.setCodOperBan("0123");
				tmpOpeBan3.setDesOperBan("Retiros");
		
			operBan3.setTiivsOperacionBancaria(tmpOpeBan);
			operBan3.setMoneda("Soles");
			operBan3.setImporte(BigDecimal.valueOf(100.00));
		
		listOpeBan.add(operBan);
		listOpeBan.add(operBan2);
		listOpeBan.add(operBan3);*/
		   	
        response.setHeader("Content-type", "application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"SolVisado.pdf\"");
		
        JRBeanCollectionDataSource objCab = new JRBeanCollectionDataSource(cabecera, false);
    
        JRDataSource PERSONADS = new JRBeanCollectionDataSource(listPersonas, false);
        
        //JRBeanCollectionDataSource objDoc = new JRBeanCollectionDataSource(listTiposDoc, false);
        //JRBeanCollectionDataSource objOpeBan = new JRBeanCollectionDataSource(listOpeBan, false);
        
        modelMap.put("dataKey", objCab);
      //  modelMap.put("PERSONADS", PERSONADS);
       // modelMap.put("dataKey", PERSONADS);
        //modelMap.put("dataKey", objDoc);
        //modelMap.put("dataKey", objOpeBan);
		//modelMap.put("dataKey", jrbeanobjeto);
		
        try {
        	OutputStream os = response.getOutputStream();
        	os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return("pdfReport");
    }
}

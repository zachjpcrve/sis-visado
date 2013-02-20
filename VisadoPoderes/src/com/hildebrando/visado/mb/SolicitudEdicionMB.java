package com.hildebrando.visado.mb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.UploadedFile;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.consulta.reniec.ObtenerPersonaReniecService;
import com.bbva.consulta.reniec.impl.ObtenerPersonaReniecServiceImpl;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Persona;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.converter.PersonaDataModal;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ApoderadoDTO;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.OperacionBancariaDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitudId;
import com.hildebrando.visado.modelo.TiivsDocumento;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsSolicitudOperbanId;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

@ManagedBean(name = "solEdicionMB")
@SessionScoped
public class SolicitudEdicionMB 
{
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	@ManagedProperty(value = "#{registroUtilesMB}")
	RegistroUtilesMB objRegistroUtilesMB;
	
	public static Logger logger = Logger.getLogger(SolicitudEdicionMB.class);	
	
	private TiivsSolicitud solicitudEdicionT;
	private List<TiivsAnexoSolicitud> lstAnexosSolicitudes;
	private List<TiivsDocumento> lstTiivsDocumentos;
	private List<SeguimientoDTO> lstSeguimientoDTO;
	private TiivsSolicitudOperban objSolicBancaria;
	private List<TiivsSolicitudOperban> lstSolicBancarias;
	private Solicitud solicitudEdicion;
	private List<TiivsMultitabla> lstMultitabla;
	private List<ApoderadoDTO> lstClientes;
	private List<OperacionBancariaDTO> lstOperaciones;
	private List<DocumentoTipoSolicitudDTO> lstdocumentos;
	private List<DocumentoTipoSolicitudDTO> lstdocumentosOpcional;
	private List<SeguimientoDTO> lstSeguimiento;
	private List<TiivsOperacionBancaria> lstTiivsOperacionBancaria;
	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaResultado;
	private TiivsPersona objTiivsPersonaSeleccionado;
	private TiivsPersona objTiivsPersonaCapturado;
	private Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas;
	private Set<TiivsSolicitudAgrupacion> lstTiivsSolicitudAgrupacion;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	private List<TiivsAnexoSolicitud> lstAnexoSolicitud;
	private PersonaDataModal personaDataModal;
	private String iTipoSolicitud = "";
	private TiivsSolicitudOperban objSolicitudOperacionCapturadoOld =new TiivsSolicitudOperban();
	private String sCodDocumento;
	private TiivsTipoSolicDocumento selectedTipoDocumento;
	private DocumentoTipoSolicitudDTO selectedDocumentoDTO;
	private String ubicacionTemporal;
	private List<String> aliasFilesToDelete;
	private IILDPeUsuario usuario;
	private int numGrupo = 0;
	private List<TiivsPersona> lstTiivsPersona;
	private List<TiivsPersona> lstTiivsPersonaResultado;
	private List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos;
	private List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp;
	boolean bBooleanPopup = false;
	boolean bBooleanPopupTipoCambio = true;
	private boolean flagUpdateOperacionSolic = false;
	private boolean flagUpdateOperacionSolcAgrupac = false;
	private boolean flagUpdateOperacionSolcDocumen = false;
	private boolean flagUpdatePersona = false;
	private String sEstadoSolicitud = "";
	private TiivsSolicitudOperban objSolicitudOperacionCapturado;
	private AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado;
	private TiivsTipoSolicDocumento objDocumentoXSolicitudCapturado;
	private int indexUpdateOperacion = 0;
	private int indexUpdateAgrupacionSimpleDto = 0;
	private int indexUpdateSolicDocumentos = 0;
	private int indexUpdatePersona = 0;
	Map<Integer, TiivsSolicitudOperban> mapSolicitudes;
	String cadenaEscanerFinal = "";
	private UploadedFile file;
	private String sMonedaImporteGlobal; 
	
	
	public SolicitudEdicionMB()
	{
		inicializarListas();
		inicializarObjetos();
		//obtenerSolicitud();
	}
	
	public void inicializarListas()
	{
		lstTiivsPersona = new ArrayList<TiivsPersona>();
		lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		lstTiivsAgrupacionPersonas = new HashSet<TiivsAgrupacionPersona>();
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		lstTipoSolicitudDocumentos = new ArrayList<TiivsTipoSolicDocumento>();

		lstDocumentosXTipoSolTemp = new ArrayList<TiivsTipoSolicDocumento>();
		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		
		lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
		lstOperaciones = new ArrayList<OperacionBancariaDTO>();
		
		aliasFilesToDelete = new ArrayList<String>();
	}
	
	public void inicializarObjetos()
	{
		solicitudEdicion = new Solicitud();
		
		personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
		objTiivsPersonaBusqueda = new TiivsPersona();
		objTiivsPersonaResultado = new TiivsPersona();
		objTiivsPersonaSeleccionado = new TiivsPersona();
		
		objSolicBancaria = new TiivsSolicitudOperban();
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		objSolicBancaria.setTiivsOperacionBancaria(new TiivsOperacionBancaria());
		
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
				
		selectedTipoDocumento = new TiivsTipoSolicDocumento();
		mapSolicitudes=new HashMap<Integer, TiivsSolicitudOperban>();
		
		combosMB= new CombosMB();
		combosMB.cargarMultitabla();
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_MONEDA);
		
		//cargarDocumentos();
		
		this.cadenaEscanerFinal = this.prepararURLEscaneo();
	}
	
	public void obtenerAccionAgregarOperacionBancaria()
	{
	    logger.info("**************************** obtenerAccionAgregarOperacionBancaria ****************************");
	    logger.info("********************************************* : "+objSolicBancaria.getImporte());
	}
	
	public boolean validarOperacionBancaria() {
		boolean result = true;
		String sMensaje = "";
		if (objSolicBancaria.getId().getCodOperBan().equals("")) {
			sMensaje = "Ingrese un tipo de Operacion";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (objSolicBancaria.getMoneda().equals("")) {
			sMensaje = "Seleccione una moneda";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (!objSolicBancaria.getMoneda().equals("")
				&& !objSolicBancaria.getMoneda().equals(
						ConstantesVisado.MONEDAS.COD_SOLES)
				&& objSolicBancaria.getTipoCambio() == 0) {
			sMensaje = "Ingrese el Tipo de Cambio";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (objSolicBancaria.getImporte() == 0) {
			sMensaje = "Ingrese el Importe";
			Utilitarios.mensajeInfo("", sMensaje);
			result = false;
		}
		if (!flagUpdateOperacionSolic) {
			for (TiivsSolicitudOperban x : lstSolicBancarias) {
				if (x.getId().getCodOperBan()
						.equals(objSolicBancaria.getId().getCodOperBan())) {
					sMensaje = "Tipo de Operación ya registrado, Ingrese otro Tipo de Operación. ";
					Utilitarios.mensajeInfo("", sMensaje);
					result = false;
					break;
				}
			}
		}
		return result;
	}
	
	public void agregarOperacionBancaria() 
	{
		logger.info(" ************************** agregarOperacionBancaria  ****************************** ");
		
		double valor = 0, valorSoles_C = 0, valorSolesD = 0, valorSolesE = 0, valorEuro = 0,
				valorDolar = 0, valorFinal = 0;
		
		int icontSoles = 0, icontDolares = 0, icontEuros = 0, item = 0;
		
		if (this.validarOperacionBancaria()) 
		{
			for (TiivsOperacionBancaria n : combosMB.getLstOpeBancaria()) 
			{
				if (n.getCodOperBan().equals(objSolicBancaria.getId().getCodOperBan()))
				{
					this.objSolicBancaria.setTiivsOperacionBancaria(n);
					break;
				}
			}
			if (!this.flagUpdateOperacionSolic) 
			{
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
				{
					valorSoles_C += objSolicBancaria.getImporte();
					objSolicBancaria.setImporteSoles(objSolicBancaria.getImporte());
					icontSoles++;
					this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) 
				{
					valor = objSolicBancaria.getTipoCambio()
							* objSolicBancaria.getImporte();
					valorSolesD += valor;
					valorDolar += objSolicBancaria.getImporte();
					objSolicBancaria.setImporteSoles(valor);
					icontDolares++;
					this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
				{
					valor = objSolicBancaria.getTipoCambio()
							* objSolicBancaria.getImporte();
					valorSolesE = valorSolesE + valor;
					valorEuro += objSolicBancaria.getImporte();
					objSolicBancaria.setImporteSoles(valor);
					icontEuros++;
					logger.info("tamanio de euros : " + icontEuros);
					this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
					this.objSolicBancaria
							.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
				}
				if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) 
				{
					valorFinal = valorSoles_C;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_SOLES;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) 
				{
					valorFinal = valorDolar;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_DOLAR;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) 
				{
					valorFinal = valorEuro;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_EUROS;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (icontDolares > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares > 0 && icontEuros == 0
						&& icontSoles > 0 || icontDolares == 0
						&& icontEuros > 0 && icontSoles > 0 || icontDolares > 0
						&& icontEuros > 0 && icontSoles > 0) 
				{
					valorFinal = valorSoles_C + valorSolesD + valorSolesE;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_SOLES;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}

				item++;
				objSolicBancaria.setsItem("00" + item);
				this.lstSolicBancarias.add(objSolicBancaria);
				
			} 
			else 
			{	
				logger.info("objSolicitudOperacionCapturadoOld"+objSolicBancaria.getImporte() + " mapSolicitudes.size() " +mapSolicitudes.size());
				
				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
				{
					logger.info("######################################### "+mapSolicitudes.get(mapSolicitudes.size()).getImporte());
					
					valorSoles_C =valorSoles_C-mapSolicitudes.get(mapSolicitudes.size()).getImporte()+ objSolicitudOperacionCapturado.getImporte();
					icontSoles--;
				}
				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) 
				{
					valor = objSolicitudOperacionCapturado.getTipoCambio()* objSolicitudOperacionCapturado.getImporte();
					valorSolesD -= valor;
					valorDolar -= objSolicitudOperacionCapturado.getImporte();
					icontDolares--;
				}
				if (objSolicitudOperacionCapturado.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
				{
					valor = objSolicitudOperacionCapturado.getTipoCambio()
							* objSolicitudOperacionCapturado.getImporte();
					valorSolesE -= valor;
					valorEuro -= objSolicBancaria.getImporte();
					icontEuros--;
				}

				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_SOLES)) 
				{
					valorSoles_C += objSolicBancaria.getImporte();
					objSolicBancaria.setImporteSoles(objSolicBancaria
							.getImporte());
					icontSoles++;
					this.objSolicBancaria
							.setsDescMoneda(ConstantesVisado.MONEDAS.SOLES);
					this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_DOLAR)) 
				{
					valor = objSolicBancaria.getTipoCambio()
							* objSolicBancaria.getImporte();
					valorSolesD += valor;
					valorDolar += objSolicBancaria.getImporte();
					objSolicBancaria.setImporteSoles(valor);
					icontDolares++;
					this.objSolicBancaria
							.setsDescMoneda(ConstantesVisado.MONEDAS.DOLARES);
					this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (objSolicBancaria.getMoneda().equals(ConstantesVisado.MONEDAS.COD_EUROS)) 
				{
					valor = objSolicBancaria.getTipoCambio()
							* objSolicBancaria.getImporte();
					valorSolesE += valor;
					valorEuro += objSolicBancaria.getImporte();
					objSolicBancaria.setImporteSoles(valor);
					icontEuros++;
					this.objSolicBancaria.setsDescMoneda(ConstantesVisado.MONEDAS.EUROS);
					this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}

				if (icontDolares == 0 && icontEuros == 0 && icontSoles > 0) 
				{
					valorFinal = valorSoles_C;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_SOLES;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}
				if (icontDolares > 0 && icontEuros == 0 && icontSoles == 0) 
				{
					valorFinal = valorDolar;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_DOLAR+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_DOLAR;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_DOLAR);
				}
				if (icontDolares == 0 && icontEuros > 0 && icontSoles == 0) 
				{
					valorFinal = valorEuro;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_EURO+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_EUROS;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_EUROS);
				}
				if (icontDolares > 0 && icontEuros > 0 && icontSoles == 0
						|| icontDolares > 0 && icontEuros == 0
						&& icontSoles > 0 || icontDolares == 0
						&& icontEuros > 0 && icontSoles > 0
						|| icontDolares == 0 && icontEuros > 0
						&& icontSoles > 0 || icontDolares > 0 && icontEuros > 0
						&& icontSoles > 0) 
				{
					valorFinal = valorSoles_C + valorSolesD + valorSolesE;
					this.solicitudEdicionT.setImporte(valorFinal);
					this.solicitudEdicionT.setsImporteMoneda(ConstantesVisado.MONEDAS.PREFIJO_SOLES+valorFinal);
	            	  sMonedaImporteGlobal=ConstantesVisado.MONEDAS.COD_SOLES;
	            	  this.objSolicBancaria.setMoneda(ConstantesVisado.MONEDAS.COD_SOLES);
				}

				this.lstSolicBancarias.set(indexUpdateOperacion,objSolicBancaria);
			}

          objSolicBancaria=new TiivsSolicitudOperban();
          objSolicBancaria.setId(new TiivsSolicitudOperbanId());
          this.flagUpdateOperacionSolic=false;
          this.objSolicitudOperacionCapturado=new TiivsSolicitudOperban();
          this.objSolicitudOperacionCapturado.setId(new TiivsSolicitudOperbanId());
          valorFinal=0;
          this.llamarComision();
		}
	}
	
	public boolean validarAgregarAgrupacion(){
		boolean returno=true;
		logger.info("***************************** validarAgregarAgrupacion ***************************************");
		String mensaje="Por lo menos ingrese un Poderdante o Apoderado";
		if(lstTiivsPersona.size()==0){
			returno=false;
			Utilitarios.mensajeInfo("", mensaje);
		}
		return returno;
	}
	 public void agregarAgrupacion(){
		  if(validarAgregarAgrupacion()){
		  numGrupo=numGrupo+1;
		  logger.info("********************** agregarAgrupacion ********************* " +numGrupo);
		  List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
		  List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
		  TiivsAgrupacionPersona tiivsAgrupacionPersona=null;
		  for (TiivsPersona objTiivsPersonaResultado : lstTiivsPersona) {
			  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
				  lstPoderdantes.add(objTiivsPersonaResultado);}
			  if(objTiivsPersonaResultado.getTipPartic().equals(ConstantesVisado.APODERADO)){
				  lstApoderdantes.add(objTiivsPersonaResultado);}
			  logger.info("objTiivsPersonaResultado.getCodPer() : "+objTiivsPersonaResultado.getCodPer());
			  tiivsAgrupacionPersona =new TiivsAgrupacionPersona();
			  TiivsAgrupacionPersona  tiivsAgrupacionPersonaId =new TiivsAgrupacionPersona();
			  tiivsAgrupacionPersonaId.setNumGrupo(numGrupo);
			  tiivsAgrupacionPersonaId.setCodSoli(solicitudEdicionT.getCodSoli());
			  tiivsAgrupacionPersonaId.setCodPer(objTiivsPersonaResultado.getCodPer());
			  tiivsAgrupacionPersonaId.setClasifPer(objTiivsPersonaResultado.getClasifPer());
			  tiivsAgrupacionPersonaId.setTipPartic(objTiivsPersonaResultado.getTipPartic());
			  tiivsAgrupacionPersona.setTiivsPersona(objTiivsPersonaResultado);
			  //tiivsAgrupacionPersona.setId(tiivsAgrupacionPersonaId);
			  lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
			  lstTiivsAgrupacionPersonas.add(tiivsAgrupacionPersona);
		}  
		  logger.info("lstPoderdantes " +lstPoderdantes.size());
		  logger.info("lstApoderdantes " +lstApoderdantes.size());
		  AgrupacionSimpleDto agrupacionSimpleDto =new AgrupacionSimpleDto();
		   agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudEdicionT.getCodSoli(), numGrupo));
		   agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
		   agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
		   agrupacionSimpleDto.setsEstado("Activo");
		   agrupacionSimpleDto.setiEstado(1);
		   agrupacionSimpleDto.setLstPersonas(this.lstTiivsPersona);
		  lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
		  solicitudEdicionT.setTiivsSolicitudAgrupacions(this.agregarSolicitudArupacion(numGrupo));
		  lstTiivsPersona=new ArrayList<TiivsPersona>();
			logger.info("Tamanio de la lista Solicitud Agrupacion : " +solicitudEdicionT.getTiivsSolicitudAgrupacions().size());
		  this.llamarComision();
		 logger.info("tamanio de lstTiivsAgrupacionPersonas "+lstTiivsAgrupacionPersonas.size());
		  }
	 }
	 public void llamarComision() {
			logger.info("************************** llamar Comision *****************************");
			this.solicitudEdicionT.setComision(objRegistroUtilesMB.calcularComision(this.solicitudEdicionT));
			logger.info("COMISION : " + this.solicitudEdicionT.getComision());

		}
	 public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(int iNumGrupo) {
			logger.info("iNumGrupo : " + iNumGrupo);

			Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion = new HashSet<TiivsSolicitudAgrupacion>();
			lstSolicitudArupacion = this.solicitudEdicionT
					.getTiivsSolicitudAgrupacions();
			TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion = new TiivsSolicitudAgrupacion();
			TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId = new TiivsSolicitudAgrupacionId();
			tiivsSolicitudAgrupacionId.setCodSoli(solicitudEdicionT.getCodSoli());
			tiivsSolicitudAgrupacionId.setNumGrupo(iNumGrupo);
			tiivsSolicitudAgrupacion.setId(tiivsSolicitudAgrupacionId);
			tiivsSolicitudAgrupacion
					.setTiivsAgrupacionPersonas(lstTiivsAgrupacionPersonas);
			tiivsSolicitudAgrupacion.setEstado("1");

			lstSolicitudArupacion.add(tiivsSolicitudAgrupacion);
			logger.info("TAMANIO DE LA SOLICI AGRUPA "
					+ lstSolicitudArupacion.size());
			return lstSolicitudArupacion;
		}

	public boolean validarRegistroSolicitud() throws Exception 
	{
		boolean retorno = true;
		String mensaje = "";
		
		if (solicitudEdicionT.getTiivsSolicitudAgrupacions().size() == 0) {
			mensaje = "Ingrese la sección Apoderado y Poderdante";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if (solicitudEdicionT.getTiivsTipoSolicitud() == (null)) {
			mensaje = "Seleccione el Tipo de Solicitud ";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if (this.lstAnexoSolicitud.size() == 0) {
			mensaje = "Ingrese los documentos Obligatorios";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}

		if (this.lstSolicBancarias.size() == 0) {

			mensaje = "Ingrese al menos una Operación Bancaria";
			retorno = false;
			Utilitarios.mensajeInfo("INFO", mensaje);
		}
		if (!this.sEstadoSolicitud.equals("BORRADOR")) {
		this.validarNroVoucher();
		}
		return retorno;
	}
	
	public void validarNroVoucher() throws Exception
	{	
		String mensaje="Ingrese un Nro de Vourcher no registrado ";
		Busqueda filtroNroVoucher = Busqueda.forClass(TiivsSolicitud.class);
		GenericDao<TiivsSolicitud, String> serviceNroVoucher=(GenericDao<TiivsSolicitud, String>) SpringInit.getApplicationContext().getBean("genericoDao");
		List<TiivsSolicitud> lstSolicitud =new ArrayList<TiivsSolicitud>();
		lstSolicitud=serviceNroVoucher.buscarDinamico(filtroNroVoucher);
		if(lstSolicitud!=null)
		{
			for (TiivsSolicitud a : lstSolicitud) 
			{
				if(a!=null||!a.equals(""))
				{
					if(a.getNroVoucher()!=(null))
					{
						if(a.getNroVoucher().equals(this.solicitudEdicionT.getNroVoucher()))
						{
							Utilitarios.mensajeInfo("INFO", mensaje);
							break;
						}
					}
				}
			}
		}
	}
	
	public boolean cargarArchivosFTP(){
		
		logger.info("************cargarArchivosFTP()*¨**************");
		
		boolean exito = true;
		String sUbicacionTemporal = getUbicacionTemporal();									
		logger.info("ubicacion temporal "+ sUbicacionTemporal);

		for(TiivsAnexoSolicitud anexo : lstAnexoSolicitud){
			
			String ruta = pdfViewerMB.cargarUnicoPDF(anexo.getAliasArchivo(),sUbicacionTemporal + anexo.getAliasTemporal());					
			if (ruta.compareTo("") != 0) {
				logger.debug("subio: " + anexo.getAliasTemporal());
				exito = exito && true;
			} else {
				logger.debug("no subio: " + anexo.getAliasTemporal());
				exito = exito && false;
			}
		}
		return exito;
	}
	
	public void actualizarBorradorSolicitud() {
		String mensaje = "";
		logger.info("*********************** registrarSolicitud ************************");
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
        GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	    TiivsPersona objPersonaRetorno=new TiivsPersona();
	    TiivsAgrupacionPersona objAgruId=new TiivsAgrupacionPersona();
		try {
			logger.info("this.solicitudEdicionT.importe : " +this.solicitudEdicionT.getImporte());
			this.solicitudEdicionT.setFecha(new Date());
			this.solicitudEdicionT.setEstado(this.solicitudEdicionT.getEstado().trim());

			logger.info("usuario.getUID() " + usuario.getUID());
			this.solicitudEdicionT.setRegUsuario(usuario.getUID());
			this.solicitudEdicionT.setNomUsuario(usuario.getNombre());
			logger.info("tiivsOficina1.codOfi ::::::: "+ this.solicitudEdicionT.getTiivsOficina1().getCodOfi());
			for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
				if (tiivsOficina1.getCodOfi().equals(this.solicitudEdicionT.getTiivsOficina1().getCodOfi())) {
					this.solicitudEdicionT.setTiivsOficina1(tiivsOficina1);
				}
			}

			logger.info("solicitudEdicionT.getTiivsSolicitudAgrupacions() : "+ solicitudEdicionT.getTiivsSolicitudAgrupacions().size());
			if (this.validarRegistroSolicitud()) {
				if (!this.sEstadoSolicitud.equals("BORRADOR")) {
					this.enviarSolicitudSSJJ();
				}
				logger.info(solicitudEdicionT.getTiivsEstudio().getCodEstudio());
				TiivsSolicitud objResultado = service.insertar(this.solicitudEdicionT);
				  for (TiivsSolicitudAgrupacion x : this.solicitudEdicionT.getTiivsSolicitudAgrupacions()) {
				  for (TiivsAgrupacionPersona b :x.getTiivsAgrupacionPersonas()) { 
					  logger.info("b.getTiivsPersona() " +b.getCodPer());
					     //b.setId
					  objPersonaRetorno=servicePers.insertarMerge(b.getTiivsPersona());
					  // System.out.println("ccdcdcd : "+objPersonaRetorno.getCodPer());
					     b.setTiivsPersona(null);
					     //objAgruId=b.getId();
					     objAgruId.setCodPer(objPersonaRetorno.getCodPer());
					     //b.setId(objAgruId);
					     serviceAgru.insertar(b);
					     } 
				  
				  }
				  TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
				  objHistorial.setId(new TiivsHistSolicitudId(this.solicitudEdicionT.getCodSoli(),1+""));
				  objHistorial.setEstado(this.solicitudEdicionT.getEstado());
				  objHistorial.setNomUsuario(this.solicitudEdicionT.getNomUsuario());
				  objHistorial.setObs(this.solicitudEdicionT.getObs());
				  objHistorial.setFecha(new Timestamp(new Date().getTime()));
				  objHistorial.setRegUsuario(this.solicitudEdicionT.getRegUsuario());
				  serviceHistorialSolicitud.insertar(objHistorial);
				  //Carga ficheros al FTP
				  boolean bRet = cargarArchivosFTP();
				  logger.info("Resultado de carga de archivos al FTP:" + bRet);
				  //Elimina archivos temporales
				  eliminarArchivosTemporales();
				  
				  for (TiivsAnexoSolicitud n : this.lstAnexoSolicitud) {
					  logger.info("nnnnnnnnnnnnnnnnnnnnnnn "+n.getAliasArchivo());
					  serviceAnexos.insertar(n);
				   }
				 
				for (TiivsSolicitudOperban a : this.lstSolicBancarias) {
					logger.info("a.getId().getCodOperBan() **** "+ a.getId().getCodOperBan());
					a.getId().setCodSoli(this.solicitudEdicionT.getCodSoli());
					logger.info("a.getId().getCodSoli() **** "+ a.getId().getCodSoli());
					 serviceSoli.insertar(a);
				}
				if (objResultado.getCodSoli() != "" || objResultado != null) {
					if (this.sEstadoSolicitud.equals("BORRADOR")) {
						mensaje = "Se registro correctamente la Solicitud con codigo : "+ objResultado.getCodSoli() + " en Borrador";
						Utilitarios.mensajeInfo("INFO", mensaje);
					} else {
						mensaje = "Se envio a SSJJ correctamente la Solicitud con codigo : "+ objResultado.getCodSoli();
						Utilitarios.mensajeInfo("INFO", mensaje);
					}
					
					
					//--this.eliminarArchivosTemporales();
					
				} else {
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
					
					//Elimina archivo temporal
					//this.eliminarArchivosTemporales();
					
				}

				logger.info("objResultado.getCodSoli(); "+ objResultado.getCodSoli());
				logger.info("objResultado.getTiivsSolicitudAgrupacions() "+ objResultado.getTiivsSolicitudAgrupacions().size());
				logger.info("this.solicitudEdicionT.importe : " +this.solicitudEdicionT.getImporte());
				instanciarSolicitudRegistro();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	public void registrarSolicitudBorrador() {
		sEstadoSolicitud = "BORRADOR";
	}

	public void registrarSolicitudEnviado() {
		sEstadoSolicitud = "ENVIADO";
	}
	
	public void obtenerPersonaSeleccionada() {
		logger.info(objTiivsPersonaSeleccionado.getCodPer());
		this.objTiivsPersonaResultado = this.objTiivsPersonaSeleccionado;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public void registrarSolicitud() 
	{
		String mensaje = "";
		logger.info("*********************** registrarSolicitud ************************");
		GenericDao<TiivsAgrupacionPersona, Object> serviceAgru = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsPersona, Object> servicePers = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsSolicitudOperban, Object> serviceSoli = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
        GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsAnexoSolicitud, Object> serviceAnexos = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	    TiivsPersona objPersonaRetorno=new TiivsPersona();
	    TiivsAgrupacionPersona objAgruId=new TiivsAgrupacionPersona();
		try {
			logger.info("this.solicitudRegistrarT.importe : " +this.solicitudEdicionT.getImporte());
			this.solicitudEdicionT.setFecha(new Date());
			this.solicitudEdicionT.setEstado(this.solicitudEdicionT.getEstado().trim());

			logger.info("usuario.getUID() " + usuario.getUID());
			this.solicitudEdicionT.setRegUsuario(usuario.getUID());
			this.solicitudEdicionT.setNomUsuario(usuario.getNombre());
			logger.info("tiivsOficina1.codOfi ::::::: "+ this.solicitudEdicionT.getTiivsOficina1().getCodOfi());
			for (TiivsOficina1 tiivsOficina1 : combosMB.getLstOficina()) {
				if (tiivsOficina1.getCodOfi().equals(this.solicitudEdicionT.getTiivsOficina1().getCodOfi())) {
					this.solicitudEdicionT.setTiivsOficina1(tiivsOficina1);
				}
			}

			logger.info("solicitudRegistrarT.getTiivsSolicitudAgrupacions() : "+ solicitudEdicionT.getTiivsSolicitudAgrupacions().size());
			if (this.validarRegistroSolicitud()) 
			{
				if (!this.sEstadoSolicitud.equals("BORRADOR")) {
					this.enviarSolicitudSSJJ();
					logger.info(solicitudEdicionT.getTiivsEstudio().getCodEstudio());
				}
				
				/*if (this.solicitudEdicionT.getTiivsEstudio().getCodEstudio()==null)
				{
					this.solicitudEdicionT.setTiivsEstudio(new TiivsEstudio());
				}
				*/
				TiivsSolicitud objResultadoTemp = this.solicitudEdicionT;
				for (TiivsSolicitudAgrupacion y : objResultadoTemp.getTiivsSolicitudAgrupacions()) 
				  {
					y.setTiivsAgrupacionPersonas(null);
				  }
				TiivsSolicitud objResultado =service.insertarMerge(objResultadoTemp);
				  for (TiivsSolicitudAgrupacion x : this.solicitudEdicionT.getTiivsSolicitudAgrupacions()) 
				  {
					  for (TiivsAgrupacionPersona b :x.getTiivsAgrupacionPersonas()) 
					  { 
						  logger.info("b.getTiivsPersona() " +b.getCodPer());
						  
						  objPersonaRetorno=servicePers.insertarMerge(b.getTiivsPersona());
						  logger.info("ccdcdcd : "+objPersonaRetorno.getCodPer());
						  b.setTiivsPersona(null);
					      //objAgruId=b.getId();
					     // objAgruId.setCodPer(objPersonaRetorno.getCodPer());
					     // b.setId(objAgruId);
						  serviceAgru.save(b);
					  } 
				  }
				  /*TiivsHistSolicitud objHistorial=new TiivsHistSolicitud();
				  objHistorial.setId(new TiivsHistSolicitudId(this.solicitudEdicionT.getCodSoli(),1+""));
				  objHistorial.setEstado(this.solicitudEdicionT.getEstado());
				  objHistorial.setNomUsuario(this.solicitudEdicionT.getNomUsuario());
				  objHistorial.setObs(this.solicitudEdicionT.getObs());
				  objHistorial.setFecha(new Timestamp(new Date().getDate()));
				  objHistorial.setRegUsuario(this.solicitudEdicionT.getRegUsuario());
				  serviceHistorialSolicitud.insertar(objHistorial);*/
				  //Carga ficheros al FTP
				  boolean bRet = cargarArchivosFTP();
				  logger.info("Resultado de carga de archivos al FTP:" + bRet);
				  //Elimina archivos temporales
				  eliminarArchivosTemporales();
				  
				  for (TiivsAnexoSolicitud n : this.lstAnexoSolicitud) 
				  {
					  logger.info("nnnnnnnnnnnnnnnnnnnnnnn "+n.getAliasArchivo());
					  
					 serviceAnexos.insertarMerge(n);
				  }
				 
				  for (TiivsSolicitudOperban a : this.lstSolicBancarias) 
				  {
						logger.info("a.getId().getCodOperBan() **** "+ a.getId().getCodOperBan());
						a.getId().setCodSoli(this.solicitudEdicionT.getCodSoli());
						logger.info("a.getId().getCodSoli() **** "+ a.getId().getCodSoli());
						serviceSoli.insertarMerge(a);
				  }
				
				  if (objResultado.getCodSoli() != "" || objResultado != null) 
				  {
						if (this.sEstadoSolicitud.equals("BORRADOR")) {
							mensaje = "Se actualizo correctamente la Solicitud con codigo : "+ objResultado.getCodSoli() + " en Borrador";
							Utilitarios.mensajeInfo("INFO", mensaje);
						} else {
							mensaje = "Se envio a SSJJ correctamente la Solicitud con codigo : "+ objResultado.getCodSoli();
							Utilitarios.mensajeInfo("INFO", mensaje);
						}
				  } 
				  else 
				  {
					mensaje = "Error al generar la Solicitud ";
					Utilitarios.mensajeInfo("INFO", mensaje);
				  }

				  logger.info("objResultado.getCodSoli(); "+ objResultado.getCodSoli());
				  logger.info("objResultado.getTiivsSolicitudAgrupacions() "+ objResultado.getTiivsSolicitudAgrupacions().size());
				  logger.info("this.solicitudRegistrarT.importe : " +this.solicitudEdicionT.getImporte());
				//instanciarSolicitudRegistro();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	public List<TiivsPersona> buscarPersonaLocal() throws Exception {
		boolean busco = false;
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);

		if ((objTiivsPersonaBusqueda.getCodCen() == null || objTiivsPersonaBusqueda.getCodCen().equals(""))
				&& (objTiivsPersonaBusqueda.getTipDoi() == null || objTiivsPersonaBusqueda
						.getTipDoi().equals(""))
				&& (objTiivsPersonaBusqueda.getNumDoi() == null || objTiivsPersonaBusqueda
						.getNumDoi().equals(""))) {
			Utilitarios.mensajeInfo("INFO",
					"Ingrese al menos un criterio de busqueda");

		} else if (objTiivsPersonaBusqueda.getNumDoi() == null
				|| objTiivsPersonaBusqueda.getNumDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Número de Doi");
		} else if (objTiivsPersonaBusqueda.getTipDoi() == null
				|| objTiivsPersonaBusqueda.getTipDoi().equals("")) {
			Utilitarios.mensajeInfo("INFO", "Ingrese el Tipo de Doi");
		} else {
			if (objTiivsPersonaBusqueda.getTipDoi() != null
					&& objTiivsPersonaBusqueda.getNumDoi() != null
					&& objTiivsPersonaBusqueda.getTipDoi().compareTo("") != 0
					&& objTiivsPersonaBusqueda.getNumDoi().compareTo("") != 0) {
				filtro.add(Restrictions.eq("tipDoi",
						objTiivsPersonaBusqueda.getTipDoi()));
				filtro.add(Restrictions.eq("numDoi",
						objTiivsPersonaBusqueda.getNumDoi()));
				busco = true;
			}
			if (objTiivsPersonaBusqueda.getCodCen() != null
					&& objTiivsPersonaBusqueda.getCodCen().compareTo("") != 0) {
				filtro.add(Restrictions.eq("codCen",
						objTiivsPersonaBusqueda.getCodCen()));
				busco = true;
			}
			lstTiivsPersona = service.buscarDinamico(filtro);
		
			for (TiivsPersona tiivsPersona : lstTiivsPersona) {
				for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
					if (tiivsPersona.getTipDoi().equals(p.getCodTipoDoc())) {
						tiivsPersona.setsDesctipDoi(p.getDescripcion());
					}
				}
			}

			if (lstTiivsPersona.size() == 0 && busco) {
				//Utilitarios.mensajeInfo("INFO","No se han encontrado resultados para los criterios de busqueda seleccionados");
			}
		}

		return lstTiivsPersona;
	}
	
	public List<TiivsPersona> buscarPersonaReniec() throws Exception {
		logger.debug("==== inicia buscarPersonaReniec() ==== ");
		List<TiivsPersona> lstTiivsPersona = new ArrayList<TiivsPersona>();
		BResult resultado = null;
		TiivsPersona objPersona = null;
		Persona persona = null;
		if (objTiivsPersonaBusqueda.getNumDoi() != null) {
			logger.info("[RENIEC]-Dni:"+ objTiivsPersonaBusqueda.getNumDoi());

			ObtenerPersonaReniecService reniecService = new ObtenerPersonaReniecServiceImpl();
			logger.debug("reniecService="+reniecService);
			//ObtenerPersonaReniecDUMMY reniecService = new ObtenerPersonaReniecDUMMY();
			resultado = reniecService.devolverPersonaReniecDNI("P013371", "0553",objTiivsPersonaBusqueda.getNumDoi());
			logger.debug("[RENIEC]-resultado: "+resultado);
			
			if (resultado.getCode() == 0) {
				
				persona = (Persona) resultado.getObject();
				logger.info("PERSONA : " + persona.getNombreCompleto()
						+ "\nDNI: " + persona.getNumerodocIdentidad());
				objPersona = new TiivsPersona();
				objPersona.setNumDoi(persona.getNumerodocIdentidad());
				objPersona.setNombre(persona.getNombre());
				objPersona.setApePat(persona.getApellidoPaterno());
				objPersona.setApeMat(persona.getApellidoMaterno());
				objPersona.setTipDoi(objTiivsPersonaBusqueda.getTipDoi());
				objPersona.setCodCen(objTiivsPersonaBusqueda.getCodCen());
				lstTiivsPersona.add(objPersona);
			}
		}
		
		logger.debug("==== saliendo de buscarPersonaReniec() ==== ");
		return lstTiivsPersona;
	}
	
	public void buscarPersona() {
		logger.info("******************** buscarPersona **********************");
		logger.info("***objTiivsPersonaBusqueda.getCodCen() "+ objTiivsPersonaBusqueda.getCodCen());
		logger.info("***objTiivsPersonaBusqueda.getTipDoi() "+ objTiivsPersonaBusqueda.getTipDoi());
		logger.info("***objTiivsPersonaBusqueda.getNumDoi() "	+ objTiivsPersonaBusqueda.getNumDoi());
		try {
			List<TiivsPersona> lstTiivsPersonaLocal = new ArrayList<TiivsPersona>();
			lstTiivsPersonaLocal = this.buscarPersonaLocal();
			logger.info("lstTiivsPersonaLocal  "+ lstTiivsPersonaLocal.size());
			List<TiivsPersona> lstTiivsPersonaReniec = new ArrayList<TiivsPersona>();
			if (lstTiivsPersonaLocal.size() == 0) {
				lstTiivsPersonaReniec = this.buscarPersonaReniec();
				if (lstTiivsPersonaReniec.size() == 0) {
					objTiivsPersonaResultado = new TiivsPersona();
					this.bBooleanPopup = false;
					// Utilitarios.mensajeInfo("INFO",
					// "No se encontro resultados para la busqueda.");
				} else if (lstTiivsPersonaReniec.size() == 1) {
					objTiivsPersonaResultado = lstTiivsPersonaReniec.get(0);
					this.bBooleanPopup = false;
				} else if (lstTiivsPersonaReniec.size() > 1) {
					this.bBooleanPopup = true;
					lstTiivsPersonaResultado = lstTiivsPersonaReniec;
				}
			} else if (lstTiivsPersonaLocal.size() == 1) {
				this.bBooleanPopup = false;
				objTiivsPersonaResultado = lstTiivsPersonaLocal.get(0);
			} else if (lstTiivsPersonaLocal.size() > 1) {
				this.bBooleanPopup = true;
				lstTiivsPersonaResultado = lstTiivsPersonaLocal;

				personaDataModal = new PersonaDataModal(
						lstTiivsPersonaResultado);
			} else {
				this.bBooleanPopup = true;
			}
		
		} catch (Exception e) {
			Utilitarios.mensajeError("ERROR", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void enviarSolicitudSSJJ() {
		Timestamp time = new Timestamp(objRegistroUtilesMB.obtenerFechaRespuesta().getTime());
		logger.info("time : " + time);
		String sCodigoEstudio = objRegistroUtilesMB.obtenerEstudioMenorCarga();
		logger.info(" sCodigoEstudio +  " + sCodigoEstudio);
		for (TiivsEstudio x : combosMB.getLstEstudio()) {
			if (x.getCodEstudio().equals(sCodigoEstudio)) {
				this.solicitudEdicionT.setTiivsEstudio(x);
			}
		}
		this.solicitudEdicionT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02);
		this.solicitudEdicionT.setFechaRespuesta(time);
		this.solicitudEdicionT.setFechaEnvio(new Timestamp(new Date().getTime()));
	}
	
	public void instanciarSolicitudRegistro() {
		logger.info("********************** instanciarSolicitudRegistro *********************");
		sEstadoSolicitud = "BORRADOR";
		lstTiivsAgrupacionPersonas = new HashSet<TiivsAgrupacionPersona>();
		lstTiivsSolicitudAgrupacion = new HashSet<TiivsSolicitudAgrupacion>();
		solicitudEdicionT = new TiivsSolicitud();
		solicitudEdicionT.setImporte((double) 0);
		solicitudEdicionT.setTiivsSolicitudAgrupacions(lstTiivsSolicitudAgrupacion);
		solicitudEdicionT.setTiivsOficina1(new TiivsOficina1());
		solicitudEdicionT.setTiivsTipoSolicitud(new TiivsTipoSolicitud());
		String grupoAdm = (String) Utilitarios.getObjectInSession("GRUPO_ADM");
		String grupoOfi = (String) Utilitarios.getObjectInSession("GRUPO_OFI");

		logger.debug("********grupoAdm ****** " + grupoAdm
				+ "  ******* grupoOfi ******** " + grupoOfi);
		// if (grupoAdm == null && grupoOfi!= null) {
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		logger.debug("usuario en session? --> " + usuario.getNombre());
		logger.debug("CodOfi: " + usuario.getBancoOficina().getCodigo().trim());
		logger.debug("DesOfi: "+ usuario.getBancoOficina().getDescripcion().trim());

		TiivsOficina1 oficina = new TiivsOficina1();
		oficina.setCodOfi(usuario.getBancoOficina().getCodigo());

		List<TiivsOficina1> lstOficinas1 = new ArrayList<TiivsOficina1>();
		combosMB = new CombosMB();
		lstOficinas1 = combosMB.getLstOficina();

		if (lstOficinas1 != null && lstOficinas1.size() != 0) {
			for (TiivsOficina1 o : lstOficinas1) {
				if (usuario.getBancoOficina().getCodigo().equals(o.getCodOfi())) {
					this.solicitudEdicionT.setTiivsOficina1(o);
				} else {
					this.solicitudEdicionT.setTiivsOficina1(new TiivsOficina1());
				}
			}
		
		}
		SolicitudDao<TiivsPersona, Object> service = (SolicitudDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		try {
			String sCodigoSol = service.obtenerPKNuevaSolicitud();
			logger.debug(" sCodigoSol " + sCodigoSol);
			this.solicitudEdicionT.setCodSoli(sCodigoSol);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.solicitudEdicionT.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_REGISTRADO_T02);
		this.solicitudEdicionT.setDescEstado(ConstantesVisado.ESTADOS.ESTADO_REGISTRADO_T02);

		lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		personaDataModal = new PersonaDataModal(lstTiivsPersonaResultado);
		objTiivsPersonaBusqueda = new TiivsPersona();
		objTiivsPersonaResultado = new TiivsPersona();
		objTiivsPersonaSeleccionado = new TiivsPersona();
		lstTiivsAgrupacionPersonas = new HashSet<TiivsAgrupacionPersona>();
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		lstTipoSolicitudDocumentos = new ArrayList<TiivsTipoSolicDocumento>();

		lstDocumentosXTipoSolTemp = new ArrayList<TiivsTipoSolicDocumento>();
		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		objSolicBancaria = new TiivsSolicitudOperban();
		objSolicBancaria.setTipoCambio(0.0);
		objSolicBancaria.setId(new TiivsSolicitudOperbanId());
		lstSolicBancarias = new ArrayList<TiivsSolicitudOperban>();
		iTipoSolicitud = "";
		lstTiivsPersona = new ArrayList<TiivsPersona>();

		// }

	}
	
	public void eliminarArchivosTemporales() {	
		logger.info("************eliminarArchivosTemporales()*¨**************");
		logger.info("Archivos a eliminar:" + aliasFilesToDelete.size()); 	
		File fileToDelete = null;
		
		for(String sfile : aliasFilesToDelete){
			logger.debug("borrar archivo: " + this.getUbicacionTemporal() + sfile);
			fileToDelete = new File(this.getUbicacionTemporal() + sfile);
			if(fileToDelete.delete()){
				logger.debug("borro archivo temporal :" + sfile);
			} else {
				logger.debug("no borro archivo temporal :" + sfile);
			}
		}
		
		fileToDelete = null;
		aliasFilesToDelete = new ArrayList<String>();		
	}
	
	private void cargarDocumentos() {
		
		GenericDao<TiivsDocumento, Object> documentoDAO = (GenericDao<TiivsDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroDocumento = Busqueda.forClass(TiivsDocumento.class);
		try {
			lstTiivsDocumentos = documentoDAO.buscarDinamico(filtroDocumento);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de documentos");
		}
		
	}
	
	public String buscarAbrevMoneda(String codigo) {
		int i = 0;
		String descripcion = "";

		for (; i <= combosMB.getLstMoneda().size() - 1; i++) 
		{
			if (combosMB.getLstMoneda().get(i).getCodMoneda().equalsIgnoreCase(codigo)) {
				if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_SOLES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_SOLES;
				} 
				else if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_DOLARES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_DOLARES;
				} 
				else if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_EUROS_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_EUROS;
				} 
				else 
				{
					descripcion = combosMB.getLstMoneda().get(i).getDesMoneda();
				}
				break;
			}
		}

		return descripcion;
	}
	
	public void actualizarVista(TiivsSolicitud solicitud)
	{
	    this.solicitudEdicionT.setCodSoli(solicitud.getCodSoli());
		this.solicitudEdicionT.setNroVoucher(solicitud.getNroVoucher());
		//solicitudEdicionT.getCodSoli()=solicitud.
		
		/*try {
		   lstAgrupacionSimpleDto=new ArrayList<AgrupacionSimpleDto>();
		  
		   //TiivsSolicitud solicitud =new TiivsSolicitud();
		   //solicitud.setCodSoli(codigoSolicitud);
		   SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		   //solicitudEdicionT= solicitudService.obtenerTiivsSolicitud(solicitud);
		   solicitudEdicionT=solicitud;
		   solicitudEdicionT.setDescEstado(Utilitarios.obternerDescripcionEstado(solicitudEdicionT.getEstado()));
		   
		   if (solicitudEdicionT.getMoneda()!=null)
		   {
			   solicitudEdicionT.setsImporteMoneda(solicitudEdicionT.getMoneda() + " " +solicitudEdicionT.getImporte());
		   }
		   else
		   {
			   solicitudEdicionT.setsImporteMoneda(solicitudEdicionT.getImporte().toString());
		   }
		   
		   lstSolicBancarias=solicitudService.obtenerListarOperacionesBancarias(solicitud);
		   int y=0;
		   
		   for (TiivsSolicitudOperban f : lstSolicBancarias) 
		   {
		 	 if (f.getMoneda()!=null)
			 {
		 		 y++;
		 		 f.setsItem(String.format("%03d",y));
		 		 f.setsDescMoneda(Utilitarios.obternerDescripcionMoneda(f.getMoneda()));
			 }
		   }
		   
		   lstAnexosSolicitudes=solicitudService.obtenerListarAnexosSolicitud(solicitud);
		   
		   //descargar anexos
		   //descargarAnexosFileServer();
		   
		   lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();
		   int i=0;
		   
		   for (TiivsAnexoSolicitud v : lstAnexosSolicitudes) 
		   {
			  i++;
			  lstdocumentos.add(new DocumentoTipoSolicitudDTO(String.format("%03d",i) , obtenerDescripcionDocumentos(v.getId().getCodDoc()), v.getAliasTemporal()));
		   }
		   
		   // PODERDANTES Y APODERADOS
		   List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
		   List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
		   AgrupacionSimpleDto agrupacionSimpleDto  =new AgrupacionSimpleDto(); ;
		   List<TiivsPersona>lstPersonas=new ArrayList<TiivsPersona>();
		   TiivsPersona objPersona=new TiivsPersona();
		   
		   for (TiivsSolicitudAgrupacion x : solicitudEdicionT.getTiivsSolicitudAgrupacions()) 
		   {
			   for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) {
				   logger.info("d.getTiivsPersona() "+d.getTiivsPersona().getTipDoi());
				   objPersona=new TiivsPersona();
				   objPersona=d.getTiivsPersona();
				   objPersona.setTipPartic(d.getId().getTipPartic());
				   objPersona.setsDesctipPartic(this.obtenerDescripcionTipoRegistro(d.getId().getTipPartic().trim()));
				   objPersona.setClasifPer(d.getId().getClasifPer());
				   objPersona.setsDescclasifPer(this.obtenerDescripcionClasificacion(d.getId().getClasifPer().trim()));
				   objPersona.setsDesctipDoi(this.obtenerDescripcionDocumentos(d.getTiivsPersona().getTipDoi().trim()));
				  lstPersonas.add(objPersona);
				if(d.getId().getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					lstPoderdantes.add(d.getTiivsPersona());
					}
				else  if(d.getId().getTipPartic().equals(ConstantesVisado.PODERDANTE)){
					lstApoderdantes.add(d.getTiivsPersona());
				}
			}
		    
			agrupacionSimpleDto =new AgrupacionSimpleDto();
		    agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(this.solicitudEdicionT.getCodSoli(), x.getId().getNumGrupo()));
		    agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
		    agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
		    agrupacionSimpleDto.setsEstado(Utilitarios.obternerDescripcionEstado(x.getActivo().trim()) );
		    agrupacionSimpleDto.setLstPersonas(lstPersonas);
		    lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
			   
		   }
		 
		  this.obtenerHistorialSolicitud();	
		 
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	
	
	public String obtenerDescripcionClasificacion(String idTipoClasificacion){
		String descripcion="";
		for (ComboDto z : combosMB.getLstClasificacionPersona()) {
			if(z.getKey().trim().equals(idTipoClasificacion)){
				 descripcion=z.getDescripcion();
				 break;
			}
		}
		return descripcion;
	}
	public String obtenerDescripcionTipoRegistro(String idTipoTipoRegistro){
		String descripcion="";
		for (ComboDto z : combosMB.getLstTipoRegistroPersona()) {
			if(z.getKey().trim().equals(idTipoTipoRegistro)){
				 descripcion=z.getDescripcion();
				 break;
			}
		}
		return descripcion;
	}
	
	public String obtenerDescripcionDocumentos(String idTipoDocumentos){
		String descripcion="";
		for (TipoDocumento z : combosMB.getLstTipoDocumentos()) {
			if(z.getCodTipoDoc().trim().equals(idTipoDocumentos)){
				 descripcion=z.getDescripcion();
				 break;
			}
		}
		return descripcion;
	}
	
	public void obtenerHistorialSolicitud(){
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + solicitudEdicionT.getCodSoli());
		
		String sCodSolicitud=solicitudEdicionT.getCodSoli();
		try {
		GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
		filtroHist.add(Restrictions.eq("id.codSoli",sCodSolicitud));
		filtroHist.addOrder(Order.desc("id.movimiento"));
		
		List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
        lstHist = histDAO.buscarDinamico(filtroHist);
			
		logger.info("Numero de registros encontrados:"+lstHist.size());
		
		if(lstHist!=null && lstHist.size()>0){
			lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
			
			for(TiivsHistSolicitud h : lstHist){
				SeguimientoDTO seg = new SeguimientoDTO();
				String estado = h.getEstado();
				if(estado!=null)
					seg.setEstado(buscarEstadoxCodigo(estado.trim()));
				seg.setNivel("");
				seg.setFecha(h.getFecha());
				seg.setUsuario(h.getNomUsuario());
				seg.setRegUsuario(h.getRegUsuario());
				seg.setObs(h.getObs());
				lstSeguimientoDTO.add(seg);				
			}
		}
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el historial de la solicitud");			
			exp.printStackTrace();
		}
	}
	
	public String buscarEstadoxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstado().size(); i++) {
			if (combosMB.getLstEstado().get(i).getCodEstado().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstado().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}
	public void listarDocumentosXSolicitud(ValueChangeEvent e) {
		//logger.info("ValuechanceEvent :  " + e.getNewValue());
		GenericDao<TiivsTipoSolicDocumento, Object> genTipoSolcDocumDAO = (GenericDao<TiivsTipoSolicDocumento, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSolcDoc = Busqueda.forClass(TiivsTipoSolicDocumento.class);
		filtroTipoSolcDoc.add(Restrictions.eq("tiivsTipoSolicitud.codTipSolic",(String) e.getNewValue()));
		filtroTipoSolcDoc.addOrder(Order.desc("obligatorio"));
		try {
			lstDocumentosXTipoSolTemp = genTipoSolcDocumDAO.buscarDinamico(filtroTipoSolcDoc);			
			lstTipoSolicitudDocumentos = (ArrayList<TiivsTipoSolicDocumento>) ((ArrayList) lstDocumentosXTipoSolTemp).clone();
			
			logger.info("lstDocumentosXTipoSolTemp.size()" + lstDocumentosXTipoSolTemp.size());
			logger.info("lstTipoSolicitudDocumentos.size()" + lstTipoSolicitudDocumentos.size());

			actualizarListadoDocumentos();

			//logger.info(" e.getNewValue()  " + (String) e.getNewValue()+ "  lstTipoSolicitudDocumentos.size : "+ lstTipoSolicitudDocumentos.size());
		} catch (Exception ex) {
			logger.info("Error al cargar el listado de documentos por tipo de soliciitud");
			ex.printStackTrace();
		}
	}
   public void actualizarListadoDocumentos() {
		
		addArchivosTemporalesToDelete();
		
		lstdocumentos = new ArrayList<DocumentoTipoSolicitudDTO>();

		for (TiivsTipoSolicDocumento s : lstTipoSolicitudDocumentos) {
			if (s.getObligatorio()!=null && s.getObligatorio().equals("1") ){
				lstdocumentos.add(new DocumentoTipoSolicitudDTO(s.getId().getCodDoc(),
						s.getTiivsDocumento().getDescripcion(), true + "", "",""));
			} else {				
				lstdocumentos.add(new DocumentoTipoSolicitudDTO(s.getId().getCodDoc(),
						s.getTiivsDocumento().getDescripcion(), false + "", "",""));
			}
		}

		lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
	}
public void addArchivosTemporalesToDelete(){		
	for(TiivsAnexoSolicitud a : lstAnexoSolicitud){	
		aliasFilesToDelete.add(a.getAliasTemporal());
	}				
}

public void agregarDocumentosXTipoSolicitud() {
	logger.info(" ************************** agrearDocumentosXTipoSolicitud  ****************************** ");
	logger.info("iTipoSolicitud  : " + iTipoSolicitud);
	if(selectedTipoDocumento!=null){
		sCodDocumento = selectedTipoDocumento.getId().getCodDoc();
	} else {
		sCodDocumento = null;
	}
	logger.info("codDocumento :  " + sCodDocumento);
	logger.info("lstAnexoSolicitud.size() :  " + lstAnexoSolicitud.size());

	if(sCodDocumento == null || sCodDocumento.isEmpty()){
		Utilitarios.mensajeInfo("", "Debe seleccionar un documento");
		return;
	}

	if (this.ValidarDocumentosDuplicados()) {
											
		if (sCodDocumento.equalsIgnoreCase(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
			sCodDocumento = ConstantesVisado.PREFIJO_OTROS+String.format("%06d", lstdocumentos.size() + 1);
		} 
					
		String sAliasTemporal = cargarUnicoPDF();
								
		if(sAliasTemporal == null || sAliasTemporal.trim() ==""){				
			return;
		}
		
		String sExtension = sAliasTemporal.substring(sAliasTemporal.lastIndexOf("."));
		String sAliasArchivo = this.solicitudEdicionT.getCodSoli() + "_" + sCodDocumento + sExtension;
		
		logger.info("aliasArchivo *** " + sAliasArchivo);
		logger.info("aliasArchivoTemporal *** " + sAliasTemporal);
		
		TiivsAnexoSolicitud objAnexo = new TiivsAnexoSolicitud();
		objAnexo.setId(new TiivsAnexoSolicitudId(this.solicitudEdicionT.getCodSoli(), sCodDocumento));
		objAnexo.setAliasArchivo(sAliasArchivo);
		objAnexo.setAliasTemporal(sAliasTemporal);
		lstAnexoSolicitud.add(objAnexo);
		
		this.actualizarListaDocumentosXTipo(objAnexo);

		for (TiivsTipoSolicitud tipoSoli : combosMB.getLstTipoSolicitud()) {
			if (tipoSoli.getCodTipSolic().equals(iTipoSolicitud)) {
				solicitudEdicionT.setTiivsTipoSolicitud(tipoSoli);
			}
		}
		
	}
	// solicitudEdicionT.getTiivsTipoSolicitud().setTiivsTipoSolicDocumentos(tiivsTipoSolicDocumentos);
}

public void validarTipoCambioDisabled(ValueChangeEvent e){
	if(e.getNewValue()!=null){
	logger.info(" validarTipoCambioDisabled " +e.getNewValue());
	if (e.getNewValue().equals(ConstantesVisado.MONEDAS.COD_SOLES)) {
		bBooleanPopupTipoCambio=true;
	}else{
		bBooleanPopupTipoCambio=false;
	}
	}
}
int y=0;
public void editarOperacionBancaria() {
	logger.info("**************************** editarOperacionBancaria ****************************");
	
	for (int i = 0; i < this.lstSolicBancarias.size(); i++) {
		if (objSolicitudOperacionCapturado.equals(this.lstSolicBancarias.get(i))) {
			indexUpdateOperacion = i;
			break;
		}
	}
	y=y+1;
	logger.info("yyyyyyy : " +y);
//	Map<String, TiivsSolicitudOperban> mapSolicitudes=new HashMap<String, TiivsSolicitudOperban>();
	mapSolicitudes.put(y, objSolicitudOperacionCapturado);
	
	Set set = mapSolicitudes.entrySet(); 
	// Get an iterator 
	Iterator iterate = set.iterator(); 
	// Display elements 
	
	while(iterate.hasNext()) { 
	Map.Entry me = (Map.Entry)iterate.next(); 
	System.out.print(me.getKey() + ": "); 
	logger.info(me.getValue()); 
	} 
	
	logger.info("mapSolicitudes.get(mapSolicitudes.size()).getImporte()"+mapSolicitudes.get(mapSolicitudes.size()).getImporte());
	//if(!flagUpdateOperacionSolic){
	this.objSolicitudOperacionCapturadoOld=this.objSolicitudOperacionCapturado;
	//}
	this.objSolicBancaria = this.objSolicitudOperacionCapturado;
	this.flagUpdateOperacionSolic = true;
}

public void actualizarListaDocumentosXTipo(TiivsAnexoSolicitud objAnexo) {
	logger.info("****************************** actualizarListaDocumentosXTipo *********************************");
	if (objAnexo.getId().getCodDoc().contains(ConstantesVisado.PREFIJO_OTROS)) {
		String sAlias = objAnexo.getAliasArchivo();
		String sAliasTemporal = objAnexo.getAliasTemporal();
		DocumentoTipoSolicitudDTO doc = new DocumentoTipoSolicitudDTO(
				objAnexo.getId().getCodDoc(),
				ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS, false + "",
				sAlias, sAliasTemporal); 
		lstdocumentos.add(doc);
		return;
	}

	for (TiivsTipoSolicDocumento s : lstDocumentosXTipoSolTemp) {

		if (s.getId().getCodDoc().equals(objAnexo.getId().getCodDoc())) {
			this.lstTipoSolicitudDocumentos.remove(s);
			break;
		}
	}

	for (DocumentoTipoSolicitudDTO doc : lstdocumentos) {
		if (doc.getItem().equals(objAnexo.getId().getCodDoc())) {
			doc.setAlias(objAnexo.getAliasArchivo());
			doc.setAliasTemporal(objAnexo.getAliasTemporal());
			break;
		}
	}
}
public String cargarUnicoPDF() {
	
	if(file == null){
		Utilitarios.mensajeInfo("", "No se ha seleccionado ningún archivo");
		return "";
	}
	
	byte fileBytes[] = getFile().getContents();

	File fichTemp = null;
	String sUbicacionTemporal = "";
	String sNombreTemporal = "";
	FileOutputStream canalSalida = null;
	
	try {
		
		//Obteniendo ubicación del proyecto
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();							
		sUbicacionTemporal = request.getRealPath(File.separator)  + File.separator + "files" + File.separator;
		this.setUbicacionTemporal(sUbicacionTemporal);
		
		logger.debug("ubicacion temporal "+ sUbicacionTemporal);
		
		File fDirectory = new File(sUbicacionTemporal);
		fDirectory.mkdirs();	
		
		String extension = file.getFileName().substring(
				getFile().getFileName().lastIndexOf("."));
		
		fichTemp = File.createTempFile("temp", extension, new File(
				sUbicacionTemporal));
		
		sNombreTemporal = fichTemp.getName();
								
		logger.debug("Nombre temporal de archivo:  " + sNombreTemporal);
		
		canalSalida = new FileOutputStream(fichTemp);
		canalSalida.write(fileBytes);
		
		canalSalida.flush();
		return sNombreTemporal;

	} catch (IOException e) {
		logger.debug("error anexo " + e.toString());
		String sMensaje = "Se produjo un error al adjuntar fichero";
		Utilitarios.mensajeInfo("", sMensaje);
		return "";
	} finally {

		fichTemp.deleteOnExit(); // Delete the file when the
		// JVM terminates

		if (canalSalida != null) {
			try {
				canalSalida.close();
			} catch (IOException x) {
				// handle error
			}
		}
	}
}	
public boolean ValidarDocumentosDuplicados() {
	boolean result = true;
	String sMensaje = "";
	for (TiivsAnexoSolicitud a : this.lstAnexoSolicitud) {
		if (a.getId().getCodDoc().equals(sCodDocumento) 
				&& !sCodDocumento.equalsIgnoreCase(ConstantesVisado.VALOR_TIPO_DOCUMENTO_OTROS)) {
			result = false;
			sMensaje = "Documento ya se encuentra en la lista de Documentos";
			Utilitarios.mensajeInfo("INFO ", sMensaje);
		}
	}
	return result;
}
public boolean validarPersona() {
	logger.info("******************************* validarPersona ******************************* "
			+ objTiivsPersonaResultado.getTipPartic());
	boolean bResult = true;
	String sMensaje = "";
	logger.info("objTiivsPersonaResultado.getClasifPer() "
			+ objTiivsPersonaResultado.getClasifPer());
	if (objTiivsPersonaResultado.getTipDoi().equals("")) {
		sMensaje = "Seleccione el Tipo de Documento";
		bResult = false;
		Utilitarios.mensajeInfo("INFO", sMensaje);
	}
	if (objTiivsPersonaResultado.getNumDoi().equals("")) {
		sMensaje = "Ingrese el Número de Doi";
		bResult = false;
		Utilitarios.mensajeInfo("INFO", sMensaje);
	}
	if (objTiivsPersonaResultado.getClasifPer() == null
			|| objTiivsPersonaResultado.getClasifPer().equals("")) {
		sMensaje = "Ingrese el Tipo de Clasificación";
		bResult = false;
		Utilitarios.mensajeInfo("INFO", sMensaje);
	}
	if ((objTiivsPersonaResultado.getTipPartic() == null || objTiivsPersonaResultado
			.getTipPartic().equals(""))) {
		sMensaje = "Ingrese el Tipo de Participacion";
		bResult = false;
		Utilitarios.mensajeInfo("INFO", sMensaje);
	} else if (objTiivsPersonaResultado.getClasifPer().equals("99")
			&& (objTiivsPersonaResultado.getClasifPerOtro().equals("") || objTiivsPersonaResultado
					.getClasifPerOtro() == null)) {
		sMensaje = "Ingrese la descipcion Tipo de Participacion";
		bResult = false;
		Utilitarios.mensajeInfo("INFO", sMensaje);
	} else if (!flagUpdatePersona) {
		for (TiivsPersona x : lstTiivsPersona) {
			if (x.getCodPer() == objTiivsPersonaResultado.getCodPer()) {
				sMensaje = "Persona ya registrada, Ingrese otros datos de persona. ";
				Utilitarios.mensajeInfo("", sMensaje);
				bResult = false;
				break;
			}
		}
	}

	return bResult;

}
public boolean validarRegistroDuplicado() {
	logger.info("******************************* validarRegistroDuplicado ******************************* "
			+ objTiivsPersonaResultado.getNumDoi());
	boolean bResult = true;
	String sMensaje = "";
	for (TipoDocumento c : combosMB.getLstTipoDocumentos()) {
		if (objTiivsPersonaResultado.getTipDoi().equals(c.getCodTipoDoc())) {
			objTiivsPersonaResultado.setsDesctipDoi(c.getDescripcion());
		}
	}
	for (ComboDto c : combosMB.getLstTipoRegistroPersona()) {
		if (objTiivsPersonaResultado.getTipPartic().equals(c.getKey())) {
			objTiivsPersonaResultado.setsDesctipPartic(c.getDescripcion());
		}
	}
	if (!flagUpdatePersona) {
		for (TiivsPersona p : lstTiivsPersona) {
			if (objTiivsPersonaResultado.getNumDoi().equals(p.getNumDoi())) {
				bResult = false;
				sMensaje = "Ya existe una persona con número de Doi :  "
						+ p.getNumDoi() + " y Tipo de Registro : "
						+ objTiivsPersonaResultado.getsDesctipPartic();
				Utilitarios.mensajeInfo("INFO", sMensaje);
			}
		}
		
		//if(objTiivsPersonaResultado.getTipDoi())
	}

	return bResult;
}
public void agregarPersona() {
	logger.info("****************** agregarPersona ********************");
	if (validarPersona()) {
		if (validarRegistroDuplicado()) {

			for (TipoDocumento p : combosMB.getLstTipoDocumentos()) {
				if (objTiivsPersonaResultado.getTipDoi().equals(
						p.getCodTipoDoc())) {
					objTiivsPersonaResultado.setsDesctipDoi(p
							.getDescripcion());
				}
			}
			for (ComboDto p : combosMB.getLstTipoRegistroPersona()) {
				if (objTiivsPersonaResultado.getTipPartic().equals(
						p.getKey())) {
					objTiivsPersonaResultado.setsDesctipPartic(p
							.getDescripcion());
				}
			}
			for (ComboDto p : combosMB.getLstClasificacionPersona()) {
				if (objTiivsPersonaResultado.getClasifPer().equals(
						p.getKey())) {
					objTiivsPersonaResultado.setsDescclasifPer(p
							.getDescripcion());
				}
				if (objTiivsPersonaResultado.getClasifPer().equals("99")) {
					objTiivsPersonaResultado
							.setsDescclasifPer(objTiivsPersonaResultado
									.getClasifPerOtro());
				}
			}

			if (!flagUpdatePersona) {
				lstTiivsPersona.add(objTiivsPersonaResultado);
				objTiivsPersonaResultado = new TiivsPersona();
				objTiivsPersonaBusqueda = new TiivsPersona();
				objTiivsPersonaSeleccionado = new TiivsPersona();
				lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
				personaDataModal = new PersonaDataModal(
						lstTiivsPersonaResultado);
			} else {
				// update
				logger.info("Index Update: " + indexUpdatePersona);

				this.lstTiivsPersona.set(indexUpdatePersona,
						objTiivsPersonaCapturado);
				personaDataModal = new PersonaDataModal(
						lstTiivsPersonaResultado);
				objTiivsPersonaResultado = new TiivsPersona();
				objTiivsPersonaBusqueda = new TiivsPersona();
				objTiivsPersonaSeleccionado = new TiivsPersona();
				flagUpdatePersona = false;
			}
		}

	}
	// return objTiivsPersonaBusqueda;

}

	public String prepararURLEscaneo() {			
		logger.info("***********prepararURLEscaneo***************");		
		String sCadena = "";		
		try{
			//usuario.setUID("P014773");				
			pdfViewerMB = new PDFViewerMB();	
			sCadena = pdfViewerMB.prepararURLEscaneo(usuario.getUID());
		}catch(Exception e){
			logger.error("Error al obtener parámetros de APPLET",e);
		}
		return sCadena;		
	}
	

	public TiivsSolicitud getSolicitudEdicionT() {
		return solicitudEdicionT;
	}

	public void setSolicitudEdicionT(TiivsSolicitud solicitudEdicionT) {
		this.solicitudEdicionT = solicitudEdicionT;
	}

	public List<TiivsSolicitudOperban> getLstSolicBancarias() {
		return lstSolicBancarias;
	}

	public void setLstSolicBancarias(List<TiivsSolicitudOperban> lstSolicBancarias) {
		this.lstSolicBancarias = lstSolicBancarias;
	}

	public List<TiivsAnexoSolicitud> getLstAnexosSolicitudes() {
		return lstAnexosSolicitudes;
	}

	public void setLstAnexosSolicitudes(
			List<TiivsAnexoSolicitud> lstAnexosSolicitudes) {
		this.lstAnexosSolicitudes = lstAnexosSolicitudes;
	}

	public List<DocumentoTipoSolicitudDTO> getLstdocumentos() {
		return lstdocumentos;
	}

	public void setLstdocumentos(List<DocumentoTipoSolicitudDTO> lstdocumentos) {
		this.lstdocumentos = lstdocumentos;
	}

	public List<AgrupacionSimpleDto> getLstAgrupacionSimpleDto() {
		return lstAgrupacionSimpleDto;
	}

	public void setLstAgrupacionSimpleDto(
			List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public List<TiivsDocumento> getLstTiivsDocumentos() {
		return lstTiivsDocumentos;
	}

	public void setLstTiivsDocumentos(List<TiivsDocumento> lstTiivsDocumentos) {
		this.lstTiivsDocumentos = lstTiivsDocumentos;
	}

	public List<SeguimientoDTO> getLstSeguimientoDTO() {
		return lstSeguimientoDTO;
	}

	public void setLstSeguimientoDTO(List<SeguimientoDTO> lstSeguimientoDTO) {
		this.lstSeguimientoDTO = lstSeguimientoDTO;
	}


	public PDFViewerMB getPdfViewerMB() {
		return pdfViewerMB;
	}


	public void setPdfViewerMB(PDFViewerMB pdfViewerMB) {
		this.pdfViewerMB = pdfViewerMB;
	}


	public RegistroUtilesMB getObjRegistroUtilesMB() {
		return objRegistroUtilesMB;
	}


	public void setObjRegistroUtilesMB(RegistroUtilesMB objRegistroUtilesMB) {
		this.objRegistroUtilesMB = objRegistroUtilesMB;
	}


	public TiivsSolicitudOperban getObjSolicBancaria() {
		return objSolicBancaria;
	}


	public void setObjSolicBancaria(TiivsSolicitudOperban objSolicBancaria) {
		this.objSolicBancaria = objSolicBancaria;
	}


	public Solicitud getSolicitudEdicion() {
		return solicitudEdicion;
	}


	public void setSolicitudEdicion(Solicitud solicitudEdicion) {
		this.solicitudEdicion = solicitudEdicion;
	}


	public List<TiivsMultitabla> getLstMultitabla() {
		return lstMultitabla;
	}


	public void setLstMultitabla(List<TiivsMultitabla> lstMultitabla) {
		this.lstMultitabla = lstMultitabla;
	}


	public List<ApoderadoDTO> getLstClientes() {
		return lstClientes;
	}


	public void setLstClientes(List<ApoderadoDTO> lstClientes) {
		this.lstClientes = lstClientes;
	}


	public List<OperacionBancariaDTO> getLstOperaciones() {
		return lstOperaciones;
	}


	public void setLstOperaciones(List<OperacionBancariaDTO> lstOperaciones) {
		this.lstOperaciones = lstOperaciones;
	}


	public List<DocumentoTipoSolicitudDTO> getLstdocumentosOpcional() {
		return lstdocumentosOpcional;
	}


	public void setLstdocumentosOpcional(
			List<DocumentoTipoSolicitudDTO> lstdocumentosOpcional) {
		this.lstdocumentosOpcional = lstdocumentosOpcional;
	}


	public List<SeguimientoDTO> getLstSeguimiento() {
		return lstSeguimiento;
	}


	public void setLstSeguimiento(List<SeguimientoDTO> lstSeguimiento) {
		this.lstSeguimiento = lstSeguimiento;
	}


	public List<TiivsOperacionBancaria> getLstTiivsOperacionBancaria() {
		return lstTiivsOperacionBancaria;
	}


	public void setLstTiivsOperacionBancaria(
			List<TiivsOperacionBancaria> lstTiivsOperacionBancaria) {
		this.lstTiivsOperacionBancaria = lstTiivsOperacionBancaria;
	}


	public TiivsPersona getObjTiivsPersonaBusqueda() {
		return objTiivsPersonaBusqueda;
	}


	public void setObjTiivsPersonaBusqueda(TiivsPersona objTiivsPersonaBusqueda) {
		this.objTiivsPersonaBusqueda = objTiivsPersonaBusqueda;
	}


	public TiivsPersona getObjTiivsPersonaResultado() {
		return objTiivsPersonaResultado;
	}


	public void setObjTiivsPersonaResultado(TiivsPersona objTiivsPersonaResultado) {
		this.objTiivsPersonaResultado = objTiivsPersonaResultado;
	}


	public TiivsPersona getObjTiivsPersonaSeleccionado() {
		return objTiivsPersonaSeleccionado;
	}


	public void setObjTiivsPersonaSeleccionado(
			TiivsPersona objTiivsPersonaSeleccionado) {
		this.objTiivsPersonaSeleccionado = objTiivsPersonaSeleccionado;
	}


	public TiivsPersona getObjTiivsPersonaCapturado() {
		return objTiivsPersonaCapturado;
	}


	public void setObjTiivsPersonaCapturado(TiivsPersona objTiivsPersonaCapturado) {
		this.objTiivsPersonaCapturado = objTiivsPersonaCapturado;
	}


	public Set<TiivsAgrupacionPersona> getLstTiivsAgrupacionPersonas() {
		return lstTiivsAgrupacionPersonas;
	}


	public void setLstTiivsAgrupacionPersonas(
			Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas) {
		this.lstTiivsAgrupacionPersonas = lstTiivsAgrupacionPersonas;
	}


	public Set<TiivsSolicitudAgrupacion> getLstTiivsSolicitudAgrupacion() {
		return lstTiivsSolicitudAgrupacion;
	}


	public void setLstTiivsSolicitudAgrupacion(
			Set<TiivsSolicitudAgrupacion> lstTiivsSolicitudAgrupacion) {
		this.lstTiivsSolicitudAgrupacion = lstTiivsSolicitudAgrupacion;
	}


	public List<TiivsAnexoSolicitud> getLstAnexoSolicitud() {
		return lstAnexoSolicitud;
	}


	public void setLstAnexoSolicitud(List<TiivsAnexoSolicitud> lstAnexoSolicitud) {
		this.lstAnexoSolicitud = lstAnexoSolicitud;
	}


	public PersonaDataModal getPersonaDataModal() {
		return personaDataModal;
	}


	public void setPersonaDataModal(PersonaDataModal personaDataModal) {
		this.personaDataModal = personaDataModal;
	}


	public String getiTipoSolicitud() {
		return iTipoSolicitud;
	}


	public void setiTipoSolicitud(String iTipoSolicitud) {
		this.iTipoSolicitud = iTipoSolicitud;
	}


	public TiivsSolicitudOperban getObjSolicitudOperacionCapturadoOld() {
		return objSolicitudOperacionCapturadoOld;
	}


	public void setObjSolicitudOperacionCapturadoOld(
			TiivsSolicitudOperban objSolicitudOperacionCapturadoOld) {
		this.objSolicitudOperacionCapturadoOld = objSolicitudOperacionCapturadoOld;
	}


	public String getsCodDocumento() {
		return sCodDocumento;
	}


	public void setsCodDocumento(String sCodDocumento) {
		this.sCodDocumento = sCodDocumento;
	}


	public TiivsTipoSolicDocumento getSelectedTipoDocumento() {
		return selectedTipoDocumento;
	}


	public void setSelectedTipoDocumento(
			TiivsTipoSolicDocumento selectedTipoDocumento) {
		this.selectedTipoDocumento = selectedTipoDocumento;
	}


	public DocumentoTipoSolicitudDTO getSelectedDocumentoDTO() {
		return selectedDocumentoDTO;
	}


	public void setSelectedDocumentoDTO(
			DocumentoTipoSolicitudDTO selectedDocumentoDTO) {
		this.selectedDocumentoDTO = selectedDocumentoDTO;
	}


	public String getUbicacionTemporal() {
		return ubicacionTemporal;
	}


	public void setUbicacionTemporal(String ubicacionTemporal) {
		this.ubicacionTemporal = ubicacionTemporal;
	}


	public List<String> getAliasFilesToDelete() {
		return aliasFilesToDelete;
	}


	public void setAliasFilesToDelete(List<String> aliasFilesToDelete) {
		this.aliasFilesToDelete = aliasFilesToDelete;
	}


	public IILDPeUsuario getUsuario() {
		return usuario;
	}


	public void setUsuario(IILDPeUsuario usuario) {
		this.usuario = usuario;
	}


	public int getNumGrupo() {
		return numGrupo;
	}


	public void setNumGrupo(int numGrupo) {
		this.numGrupo = numGrupo;
	}


	public List<TiivsPersona> getLstTiivsPersona() {
		return lstTiivsPersona;
	}


	public void setLstTiivsPersona(List<TiivsPersona> lstTiivsPersona) {
		this.lstTiivsPersona = lstTiivsPersona;
	}


	public List<TiivsPersona> getLstTiivsPersonaResultado() {
		return lstTiivsPersonaResultado;
	}


	public void setLstTiivsPersonaResultado(
			List<TiivsPersona> lstTiivsPersonaResultado) {
		this.lstTiivsPersonaResultado = lstTiivsPersonaResultado;
	}


	public List<TiivsTipoSolicDocumento> getLstTipoSolicitudDocumentos() {
		return lstTipoSolicitudDocumentos;
	}


	public void setLstTipoSolicitudDocumentos(
			List<TiivsTipoSolicDocumento> lstTipoSolicitudDocumentos) {
		this.lstTipoSolicitudDocumentos = lstTipoSolicitudDocumentos;
	}


	public List<TiivsTipoSolicDocumento> getLstDocumentosXTipoSolTemp() {
		return lstDocumentosXTipoSolTemp;
	}


	public void setLstDocumentosXTipoSolTemp(
			List<TiivsTipoSolicDocumento> lstDocumentosXTipoSolTemp) {
		this.lstDocumentosXTipoSolTemp = lstDocumentosXTipoSolTemp;
	}


	public boolean isbBooleanPopup() {
		return bBooleanPopup;
	}


	public void setbBooleanPopup(boolean bBooleanPopup) {
		this.bBooleanPopup = bBooleanPopup;
	}


	public boolean isbBooleanPopupTipoCambio() {
		return bBooleanPopupTipoCambio;
	}


	public void setbBooleanPopupTipoCambio(boolean bBooleanPopupTipoCambio) {
		this.bBooleanPopupTipoCambio = bBooleanPopupTipoCambio;
	}


	public boolean isFlagUpdateOperacionSolic() {
		return flagUpdateOperacionSolic;
	}


	public void setFlagUpdateOperacionSolic(boolean flagUpdateOperacionSolic) {
		this.flagUpdateOperacionSolic = flagUpdateOperacionSolic;
	}


	public boolean isFlagUpdateOperacionSolcAgrupac() {
		return flagUpdateOperacionSolcAgrupac;
	}


	public void setFlagUpdateOperacionSolcAgrupac(
			boolean flagUpdateOperacionSolcAgrupac) {
		this.flagUpdateOperacionSolcAgrupac = flagUpdateOperacionSolcAgrupac;
	}


	public boolean isFlagUpdateOperacionSolcDocumen() {
		return flagUpdateOperacionSolcDocumen;
	}


	public void setFlagUpdateOperacionSolcDocumen(
			boolean flagUpdateOperacionSolcDocumen) {
		this.flagUpdateOperacionSolcDocumen = flagUpdateOperacionSolcDocumen;
	}


	public boolean isFlagUpdatePersona() {
		return flagUpdatePersona;
	}


	public void setFlagUpdatePersona(boolean flagUpdatePersona) {
		this.flagUpdatePersona = flagUpdatePersona;
	}


	public String getsEstadoSolicitud() {
		return sEstadoSolicitud;
	}


	public void setsEstadoSolicitud(String sEstadoSolicitud) {
		this.sEstadoSolicitud = sEstadoSolicitud;
	}


	public TiivsSolicitudOperban getObjSolicitudOperacionCapturado() {
		return objSolicitudOperacionCapturado;
	}


	public void setObjSolicitudOperacionCapturado(
			TiivsSolicitudOperban objSolicitudOperacionCapturado) {
		this.objSolicitudOperacionCapturado = objSolicitudOperacionCapturado;
	}


	public AgrupacionSimpleDto getObjAgrupacionSimpleDtoCapturado() {
		return objAgrupacionSimpleDtoCapturado;
	}


	public void setObjAgrupacionSimpleDtoCapturado(
			AgrupacionSimpleDto objAgrupacionSimpleDtoCapturado) {
		this.objAgrupacionSimpleDtoCapturado = objAgrupacionSimpleDtoCapturado;
	}


	public TiivsTipoSolicDocumento getObjDocumentoXSolicitudCapturado() {
		return objDocumentoXSolicitudCapturado;
	}


	public void setObjDocumentoXSolicitudCapturado(
			TiivsTipoSolicDocumento objDocumentoXSolicitudCapturado) {
		this.objDocumentoXSolicitudCapturado = objDocumentoXSolicitudCapturado;
	}


	public int getIndexUpdateOperacion() {
		return indexUpdateOperacion;
	}


	public void setIndexUpdateOperacion(int indexUpdateOperacion) {
		this.indexUpdateOperacion = indexUpdateOperacion;
	}


	public int getIndexUpdateAgrupacionSimpleDto() {
		return indexUpdateAgrupacionSimpleDto;
	}


	public void setIndexUpdateAgrupacionSimpleDto(int indexUpdateAgrupacionSimpleDto) {
		this.indexUpdateAgrupacionSimpleDto = indexUpdateAgrupacionSimpleDto;
	}


	public int getIndexUpdateSolicDocumentos() {
		return indexUpdateSolicDocumentos;
	}


	public void setIndexUpdateSolicDocumentos(int indexUpdateSolicDocumentos) {
		this.indexUpdateSolicDocumentos = indexUpdateSolicDocumentos;
	}


	public int getIndexUpdatePersona() {
		return indexUpdatePersona;
	}


	public void setIndexUpdatePersona(int indexUpdatePersona) {
		this.indexUpdatePersona = indexUpdatePersona;
	}


	public Map<Integer, TiivsSolicitudOperban> getMapSolicitudes() {
		return mapSolicitudes;
	}


	public void setMapSolicitudes(Map<Integer, TiivsSolicitudOperban> mapSolicitudes) {
		this.mapSolicitudes = mapSolicitudes;
	}


	public String getCadenaEscanerFinal() {
		return cadenaEscanerFinal;
	}


	public void setCadenaEscanerFinal(String cadenaEscanerFinal) {
		this.cadenaEscanerFinal = cadenaEscanerFinal;
	}


	public UploadedFile getFile() {
		return file;
	}


	public void setFile(UploadedFile file) {
		this.file = file;
	}


	public String getsMonedaImporteGlobal() {
		return sMonedaImporteGlobal;
	}


	public void setsMonedaImporteGlobal(String sMonedaImporteGlobal) {
		this.sMonedaImporteGlobal = sMonedaImporteGlobal;
	}	
}

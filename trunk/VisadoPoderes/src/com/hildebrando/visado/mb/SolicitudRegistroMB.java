package com.hildebrando.visado.mb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.ApoderadoDTO;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;
import com.hildebrando.visado.dto.OperacionBancariaDTO;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.dto.UsuarioLDAP2;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersonaId;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;
import com.ibm.ws.batch.xJCL.beans.returnCodeExpression;

@ManagedBean(name = "solicitudRegMB")
@SessionScoped
public class SolicitudRegistroMB {
	private Solicitud solicitudRegistrar;
	private List<TiivsMultitabla> lstMultitabla;
	private List<ApoderadoDTO> lstClientes;
	private List<OperacionBancariaDTO> lstOperaciones;
	private List<DocumentoTipoSolicitudDTO> lstdocumentos;
	private List<DocumentoTipoSolicitudDTO> lstdocumentosOpcional;
	private List<SeguimientoDTO> lstSeguimiento;
	private List<TiivsOperacionBancaria> lstTiivsOperacionBancaria;
	

	private TiivsPersona objTiivsPersona;
	
	
	public static Logger logger = Logger.getLogger(SolicitudRegistroMB.class);
	
	public SolicitudRegistroMB() 
	{
		solicitudRegistrar = new Solicitud();
		inicializarValores();
		listarDataMaqueteado();
		instanciarSolicitudRegistro();
		
	}
	public void listarDataMaqueteado(){
		
		lstClientes=new ArrayList<ApoderadoDTO>();
		lstOperaciones=new ArrayList<OperacionBancariaDTO>();
		lstdocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
		lstSeguimiento=new ArrayList<SeguimientoDTO>();
		lstClientes.add(new ApoderadoDTO("123456", "DNI:43863696 Samira Benazar", "Apoderado", "Beneficiario", "555555555", "samiray.yas@gmail.com"));
		lstClientes.add(new ApoderadoDTO("789654", "DNI:82553665 Diego Clemente", "Poderdante", "Fallecido", "8926358858","diemgo_clemente@hotmail.com"));
		lstOperaciones.add(new OperacionBancariaDTO("001", "DNI:43863696 Samira Benazar", "PEN", "500.00", "0", "500.00"));
		lstOperaciones.add(new OperacionBancariaDTO("001", "DNI:43863696 Diego Clemente", "DOL", "300.00", "3.50", "1050.00"));
	    lstdocumentos.add(new DocumentoTipoSolicitudDTO("001", "Copia de DNI", "Yes"));
	    lstdocumentos.add(new DocumentoTipoSolicitudDTO("001", "Copia Literal", "Nou"));
	    lstSeguimiento.add(new SeguimientoDTO("Registrado", "Nivel 01", "22/11/2012", "P025245 :Samira Benazar", "observacion"));
	    lstSeguimiento.add(new SeguimientoDTO("Enviado", " ", "22/11/2012", "P025245 :Diego Clemente", "observacion"));

	}
	public String redirectDetalleSolicitud(){
		logger.info(" **** redirectDetalleSolicitud ***");
		return "/faces/paginas/detalleSolicitudEstadoEnviado.xhtml";
	}
	
	
	public void inicializarValores(){
		System.out.println(" **********************inicializarValores *********************");
		IILDPeUsuario usuario=(IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		logger.info("usuario en session? --> "+usuario.getNombre());
		System.out.println("usuario en session? --> "+usuario.getNombre());
	}
	public Set<TiivsSolicitudAgrupacion> agregarSolicitudArupacion(){
		  int iNumGrupo=0;
		  Set<TiivsSolicitudAgrupacion> lstSolicitudArupacion=new HashSet<TiivsSolicitudAgrupacion>();
		  TiivsSolicitudAgrupacion tiivsSolicitudAgrupacion=new TiivsSolicitudAgrupacion();
		  TiivsSolicitudAgrupacionId tiivsSolicitudAgrupacionId=new TiivsSolicitudAgrupacionId();
		  tiivsSolicitudAgrupacionId.setCodSoli("");
		  tiivsSolicitudAgrupacionId.setNumGrupo(iNumGrupo+1);
		  tiivsSolicitudAgrupacion.setId(tiivsSolicitudAgrupacionId);
		  tiivsSolicitudAgrupacion.setActivo("1");
		  lstSolicitudArupacion.add(tiivsSolicitudAgrupacion);
		  return lstSolicitudArupacion;
	}
	public TiivsAgrupacionPersona agregarAgrupacionPersona(){
		TiivsAgrupacionPersona objTiivsAgrupacionPersona=new TiivsAgrupacionPersona();
		objTiivsAgrupacionPersona.setTiivsPersona(objTiivsPersona);
		return objTiivsAgrupacionPersona;
	}
  public Set<TiivsAgrupacionPersona> agregarAgrupacion(int iNumeroAgrupacion){
	  
	  Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
	  TiivsAgrupacionPersona tiivsAgrupacionPersona=new TiivsAgrupacionPersona();
	  TiivsAgrupacionPersonaId  tiivsAgrupacionPersonaId =new TiivsAgrupacionPersonaId();
	  tiivsAgrupacionPersonaId.setNumGrupo(iNumeroAgrupacion);
	  tiivsAgrupacionPersonaId.setCodSoli("");
	  tiivsAgrupacionPersonaId.setCodPer(objTiivsPersona.getCodPer());
	  tiivsAgrupacionPersonaId.setClasifPer("CLASIFICACION0");
	  tiivsAgrupacionPersonaId.setTipPartic("TIPO PARTICIPA");
	  tiivsAgrupacionPersona.setTiivsPersona(objTiivsPersona);
	  tiivsAgrupacionPersona.setId(tiivsAgrupacionPersonaId);
	  lstTiivsAgrupacionPersonas.add(tiivsAgrupacionPersona);
	 return lstTiivsAgrupacionPersonas;
 }
	public void instanciarSolicitudRegistro(){
		logger.debug("********************** instanciarSolicitudRegistro *********************");
		Set<TiivsAgrupacionPersona> lstTiivsAgrupacionPersonas=new HashSet<TiivsAgrupacionPersona>();
		TiivsSolicitud objSolicitudRegistro=new TiivsSolicitud();
		objSolicitudRegistro.setTiivsSolicitudAgrupacions(lstTiivsAgrupacionPersonas);
		objSolicitudRegistro.setTiivsOficina1(new TiivsOficina1());
		objSolicitudRegistro.setTiivsTipoSolicitud(new TiivsTipoSolicitud());
		
	}
	
/*	public String btnNuevoAction() {
		try {
			
			logger.info("---btnNuevoAction---");
			String grupoAdm = (String)Utilitarios.getObjectInSession("GRUPO_ADM");
			String grupoOfi = (String)Utilitarios.getObjectInSession("GRUPO_OFI");
			this.solicitudModificar=new Solicitud();
		    //TiivsSolicitud solicitud = new TiivsSolicitud();
			lstClientes=new ArrayList<ApoderadoDTO>();
			lstOperaciones=new ArrayList<OperacionBancariaDTO>();
			lstdocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
			lstdocumentosOpcional=new ArrayList<DocumentoTipoSolicitudDTO>();
			
			if (grupoAdm == null && grupoOfi!= null) {
				
				UsuarioLDAP2 usuario = (UsuarioLDAP2) Utilitarios.getObjectInSession("USUARIO_SESION");
				System.out.println("CodOfi: "+usuario.getCodofi().trim());
				System.out.println("DesOfi: "+usuario.getDesofi().trim());
				
				TiivsOficina1 oficina=new TiivsOficina1();
				oficina.setCodOfi(usuario.getCodofi());
				GenericDao<TiivsOficina1, Object> genericDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				//Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);
				List<TiivsOficina1> lstOficina1=(List<TiivsOficina1>) genericDAO.buscarById(TiivsOficina1.class, usuario.getCodofi());
				ArrayList listaOfi=(ArrayList) this.getSolicitudModel().getOficina1Service().selectDynamicWhere(oficina);
				if(listaOfi!=null)
				{
					oficina=(Oficina1) listaOfi.get(0);
					System.out.println("CodOfi: "+oficina.getCodOfi().trim());
					System.out.println("DesOfi: "+oficina.getDesOfi().trim());
					this.getSolicitudModel().getSolicitudRegistrar().setCodOfi(oficina.getCodOfi());				
					this.getSolicitudModel().getSolicitudRegistrar().setDesOfi(oficina.getDesOfi().trim());
				}
			}		
			
			String codSoli=this.getSolicitudModel().getSolicitudService().selectNextPK();
			System.out.println("codSoli: "+codSoli);
			if (codSoli==null) {
				this.getSolicitudModel().getSolicitudRegistrar().setCodSoli("0000001");	
			}else
			this.getSolicitudModel().getSolicitudRegistrar().setCodSoli(codSoli);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			return null;
		}
			this.getSolicitudModel().setLblError_Text("");
			this.getSolicitudModel().setLblInfo_Text("");
			logger.trace("--nuevo--");
			System.out.println("--nuevo--");
			return "nuevo";
	}
	*/
 
	public void cargarMultitabla(){
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
				
		try {
			lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de multitablas");
		}
	}
	

	//Descripcion: Metodo que se encarga de cargar los combos que se mostraran en la pantalla de Registro de solicitudes 
	//			   de acuerdo a la lista de la multitabla previamente cargada.
	//@Autor: Samira Benazar
	//@Version: 1.0
	//@param: -
	public void cargarCombosFormularioRegistro(String codigo){
		logger.debug("Buscando valores en multitabla con codigo: " + codigo);
		
		
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

	public List<DocumentoTipoSolicitudDTO> getLstdocumentos() {
		return lstdocumentos;
	}

	public void setLstdocumentos(List<DocumentoTipoSolicitudDTO> lstdocumentos) {
		this.lstdocumentos = lstdocumentos;
	}

	public List<SeguimientoDTO> getLstSeguimiento() {
		return lstSeguimiento;
	}

	public void setLstSeguimiento(List<SeguimientoDTO> lstSeguimiento) {
		this.lstSeguimiento = lstSeguimiento;
	}
	public List<DocumentoTipoSolicitudDTO> getLstdocumentosOpcional() {
		return lstdocumentosOpcional;
	}
	public void setLstdocumentosOpcional(
			List<DocumentoTipoSolicitudDTO> lstdocumentosOpcional) {
		this.lstdocumentosOpcional = lstdocumentosOpcional;
	}
	public List<TiivsOperacionBancaria> getLstTiivsOperacionBancaria() {
		return lstTiivsOperacionBancaria;
	}
	public void setLstTiivsOperacionBancaria(List<TiivsOperacionBancaria> lstTiivsOperacionBancaria) {
		this.lstTiivsOperacionBancaria = lstTiivsOperacionBancaria;
	}
	public Solicitud getSolicitudRegistrar() {
		return solicitudRegistrar;
	}
	public void setSolicitudRegistrar(Solicitud solicitudRegistrar) {
		this.solicitudRegistrar = solicitudRegistrar;
	}
		
	
	
	

	

}

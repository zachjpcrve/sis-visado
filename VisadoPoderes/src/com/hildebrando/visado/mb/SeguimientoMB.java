package com.hildebrando.visado.mb;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.BeansException;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsTerritorio;
import com.hildebrando.visado.service.NivelService;

@ManagedBean(name = "seguimientoMB")
@SessionScoped
public class SeguimientoMB 
{
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	private List<TiivsSolicitud> solicitudes;
	private List<TiivsAgrupacionPersona> lstAgrupPer;
	private List<TiivsHistSolicitud> lstHistorial;
	private List<String> lstEstudioSelected;
	private List<String> lstEstadoNivelSelected;
	private List<String> lstTipoSolicitudSelected;
	private List<String> lstEstadoSelected;
	private List<String> lstNivelSelected;
	private List<String> lstSolicitudesSelected;
	private List<String> lstSolicitudesxOpeBan;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	private String codSolicitud;
	private String textoTotalResultados;
	private String idImporte;
	private String idTerr;
	private String idTiposFecha;
	private String idOpeBan;
	private String idCodOfi;
	private String idCodOfi1;
	private String txtCodOfi;
	private String txtNomOfi;
	private String txtNomOficina;
	private String txtCodOficina;
	private String txtNomTerritorio;
	private String txtNomApoderado;
	private String txtNomPoderdante;
	private String nroDOIApoderado;
	private String nroDOIPoderdante;
	private Boolean noHabilitarExportar;
	private Boolean noMostrarFechas;
	private Boolean bRevision=false;
	private Boolean bDelegados=false;
	private Boolean bRevocatoria=false;
	private Boolean mostrarEstudio;
	private Date fechaInicio;
	private Date fechaFin;
	private TiivsOficina1 oficina;
	private Boolean mostrarColumna=true;
	private String nombreArchivoExcel;
	private String rutaArchivoExcel;
	private StreamedContent file;  
	private IILDPeUsuario usuario;
	private String PERFIL_USUARIO ;
	private boolean bloquearOficina=false;
	private NivelService nivelService;
	private TiivsPersona objTiivsPersonaBusquedaNomApod;
	private TiivsPersona objTiivsPersonaBusquedaNomPoder;
	
//	private List<TiivsHistSolicitud> lstHistorial;
	private List<SeguimientoDTO> lstSeguimientoDTO;
	private TiivsSolicitud selectedSolicitud;
	
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	public static Logger logger = Logger.getLogger(SeguimientoMB.class);
	
	public SeguimientoMB()
	{
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");	
		PERFIL_USUARIO=(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		solicitudes = new ArrayList<TiivsSolicitud>();
		lstAgrupPer= new ArrayList<TiivsAgrupacionPersona>();
		lstNivelSelected = new ArrayList<String>();
		lstEstudioSelected = new ArrayList<String>();
		lstTipoSolicitudSelected = new ArrayList<String>();
		lstSolicitudesxOpeBan = new ArrayList<String>();
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
		nivelService = new NivelService();
		oficina= new TiivsOficina1();
		lstSolicitudesSelected = new ArrayList<String>();
		lstHistorial = new ArrayList<TiivsHistSolicitud>();
				
		this.lstTipoSolicitudSelected = new ArrayList<String>();
		this.lstEstadoNivelSelected = new ArrayList<String>();
		this.lstEstadoSelected = new ArrayList<String>();
		
		this.codSolicitud="";
		this.idImporte="";
		this.idTiposFecha = "";
		this.idOpeBan = "";
		this.nroDOIApoderado="";
		this.txtNomApoderado="";
		this.nroDOIPoderdante="";
		this.txtNomPoderdante="";
		
		combosMB= new CombosMB();
		combosMB.cargarMultitabla();
		// Carga combo Rango Importes
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_IMPORTES);
		// Carga combo Estados
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS);
		// Carga combo Estados Nivel
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL);
		// Carga combo Tipos de Fecha
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA);
		// Carga lista de monedas
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_MONEDA);
		// Carga lista de tipos de persona
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA);
		combosMB.cargarCombosNoMultitabla();
		cargarSolicitudes();
		
		if (solicitudes.size() == 0) 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_SIN_REGISTROS,solicitudes.size());
			setNoHabilitarExportar(true);
		} 
		else 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + solicitudes.size() + ConstantesVisado.MSG_REGISTROS,solicitudes.size());
			setNoHabilitarExportar(false);
		}
		
		setearCamposxPerfil();		
		generarNombreArchivo();
		//setNoMostrarFechas(true);
	}
	
	public void setearCamposxPerfil()
	{
		//Seteo de campo Oficina en caso de que el grupo del usuario logueado sea de Perfil Oficina
		String grupoOfi = (String) Utilitarios.getObjectInSession("GRUPO_OFI");
		String codOfi="";
		String desOfi="";
		String textoOficina="";
				
		if (grupoOfi!=null)
		{
			if (grupoOfi.compareTo("")!=0)
			{
				codOfi=usuario.getBancoOficina().getCodigo().trim();
				desOfi=usuario.getBancoOficina().getDescripcion();
				
				TiivsTerritorio terr=buscarTerritorioPorOficina(codOfi);
						
				textoOficina =codOfi + " "  + desOfi+ " (" + terr.getCodTer() + "-" + terr.getDesTer() + ")";
			}
		}
		
		if (textoOficina.compareTo("")!=0)
		{
			logger.info("Texto Oficina a setear: " + textoOficina);
			setTxtNomOficina(textoOficina);
			
			TiivsOficina1 tmpOfi = new TiivsOficina1();
			tmpOfi.setCodOfi(usuario.getBancoOficina().getCodigo().trim());
			tmpOfi.setDesOfi(textoOficina);
			
			setOficina(tmpOfi);
			setBloquearOficina(true);
		}
		
		//Seteo del campo Estudio en caso de que el grupo sea de Servicios Juridicos
		String PERFIL_USUARIO = (String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		
		if (PERFIL_USUARIO!=null)
		{
			if (PERFIL_USUARIO.equals(ConstantesVisado.SSJJ)){
				setMostrarEstudio(true);
			}
			else{
				setMostrarEstudio(false);
			}
		}
	}	
	
	// Descripcion: Metodo que se encarga de cargar las solicitudes en la grilla
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void cargarSolicitudes() 
	{
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		filtroSol.setMaxResults(1000);
		
		if(PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO) )
		{
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_ESTUDIO,	ConstantesVisado.ALIAS_TBL_ESTUDIO);
			filtroSol.add(Restrictions.eq(ConstantesVisado.ALIAS_COD_ESTUDIO, buscarEstudioxAbogado()));
		}
		else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
		{
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,	ConstantesVisado.ALIAS_TBL_OFICINA);
			setBloquearOficina(true);
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA_ALIAS, usuario.getBancoOficina().getCodigo().trim()));
		}
		
		filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
		
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
			if(solicitudes!=null){
				logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"de solicitudes es:"+solicitudes.size());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes");
		}
		
		logger.info("Numero de solicitudes encontradas: " + solicitudes.size());

		actualizarDatosGrilla();
		
		String grupoOfi = (String) Utilitarios.getObjectInSession("GRUPO_OFI");
		
		if (grupoOfi!=null)
		{
			if (grupoOfi.compareTo("")!=0)
			{
				setMostrarColumna(false);
			}
		}
	}
	
	public String buscarEstudioxAbogado()
	{
		String codEstudio ="";
		GenericDao<TiivsMiembro, Object> mDAO = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroM = Busqueda.forClass(TiivsMiembro.class);
		filtroM.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_MIEMBRO, usuario.getUID()));
		List<TiivsMiembro> result = new ArrayList<TiivsMiembro>();
		
		try {
			result = mDAO.buscarDinamico(filtroM);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error el estudio asociado al abogado");
		}
		
		if (result!=null)
		{
			codEstudio = result.get(0).getEstudio();
		}
		
		return codEstudio;
	}
	
	@SuppressWarnings("unchecked")
	public void liberarSolicitud()
	{
		String codigoSolicitud=Utilitarios.capturarParametro("prm_codSoli");
		logger.info("codigoSolicitud : "+codigoSolicitud);
		
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD, codigoSolicitud));
		
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes");
		}
		
		if (solicitudes.size()==1)
		{
			if (solicitudes.get(0).getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02))
			{
				 try {
					  TiivsSolicitud solicitud =new TiivsSolicitud();
					  solicitud.setCodSoli(codigoSolicitud);	  
					  SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
					  solicitud= solicitudService.obtenerTiivsSolicitud(solicitud);
					  solicitud.setEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02);
					  solicitud.setDescEstado(Utilitarios.obternerDescripcionEstado(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02));
					  					  
					  solicitudService.modificar(solicitud);
					  
					  registrarHistorial(solicitud);
				} catch (BeansException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		cargarSolicitudes();
	}
	public void registrarHistorial(TiivsSolicitud solicitud) throws Exception {
		SolicitudDao<String, Object> serviceMaxMovi = (SolicitudDao<String, Object>) SpringInit
				.getApplicationContext().getBean("solicitudEspDao");
		String numeroMovimiento = serviceMaxMovi
				.obtenerMaximoMovimiento(solicitud.getCodSoli());

		int num = 0;
		if (!numeroMovimiento.equals("")) {
			num = Integer.parseInt(numeroMovimiento) + 1;
		} else {
			num = 1;
		}
		numeroMovimiento = num + "";
		logger.info("Numero de Movimiento a registrar para el CodSolicitud : "
				+ solicitud.getCodSoli());
		TiivsHistSolicitud objHistorial = new TiivsHistSolicitud();
		objHistorial.setId(new TiivsHistSolicitudId(solicitud.getCodSoli(),
				numeroMovimiento));
		objHistorial.setEstado(solicitud.getEstado());
		objHistorial.setNomUsuario(usuario.getNombre());
		objHistorial.setObs(solicitud.getObs());
		objHistorial.setFecha(new Timestamp(new Date().getTime()));
		objHistorial.setRegUsuario(usuario.getUID());
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		serviceHistorialSolicitud.insertar(objHistorial);
	}
	public String obtenerDescripcionClasificacion(String idTipoClasificacion) {
		String descripcion = "";
		for (ComboDto z : combosMB.getLstClasificacionPersona()) {
			if (z.getKey().trim().equals(idTipoClasificacion)) {
				descripcion = z.getDescripcion();
				break;
			}
		}
		return descripcion;
	}

	public String obtenerDescripcionTipoRegistro(String idTipoTipoRegistro) {
		String descripcion = "";
		for (ComboDto z : combosMB.getLstTipoRegistroPersona()) {
			if (z.getKey().trim().equals(idTipoTipoRegistro)) {
				descripcion = z.getDescripcion();
				break;
			}
		}
		return descripcion;
	}
	
	public String obtenerDescripcionDocumentos(String idTipoDocumentos) {
		String descripcion = "";
		for (TipoDocumento z : combosMB.getLstTipoDocumentos()) {
			if (z.getCodTipoDoc().trim().equals(idTipoDocumentos)) {
				descripcion = z.getDescripcion();
				break;
			}
		}
		return descripcion;
	}

	
	private void actualizarDatosGrilla() 
	{	
		String cadena="";
		
		// Se obtiene y setea la descripcion del Estado en la grilla
		for (TiivsSolicitud tmpSol : solicitudes) 
		{
			if (tmpSol.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02))
			{
				tmpSol.setbLiberado(true);
			}
			else
			{
				tmpSol.setbLiberado(false);
			}
			
			// Se obtiene y setea la descripcion del Estado en la grilla
			if (tmpSol.getEstado() != null) {
				String estado=buscarEstadoxCodigo(tmpSol.getEstado().trim());
				tmpSol.setTxtEstado(estado);
			}
			
			// Se obtiene la moneda y se coloca las iniciales en la columna Importe Total
			if (tmpSol.getMoneda() != null) 
			{
				String moneda = buscarAbrevMoneda(tmpSol.getMoneda());
				tmpSol.setTxtImporte(moneda.concat(ConstantesVisado.DOS_PUNTOS).concat(String.valueOf(tmpSol.getImporte())));
			} 
			else 
			{
				if (tmpSol.getImporte() != 0) 
				{
					tmpSol.setTxtImporte(String.valueOf(tmpSol.getImporte()));
				}
			}
			
			//Cargar data de poderdantes
		/*	cadena="";
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getCodSoli().equals(tmpSol.getCodSoli()) && tmp.getClasifPer().equals(ConstantesVisado.PODERDANTE))
				{
					if (tmp.getClasifPer().equals(combosMB.getLstTipoRegistroPersona().get(0).getKey()))
					{
						cadena += devolverDesTipoDOI(tmp.getTiivsPersona().getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmp.getTiivsPersona().getNumDoi() +
								  ConstantesVisado.GUION + tmp.getTiivsPersona().getApePat() + " " + tmp.getTiivsPersona().getApeMat() + " " + 
								  tmp.getTiivsPersona().getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
					}
				}
			}
			tmpSol.setTxtPoderdante(cadena);*/
			
			//Cargar data de poderdantes
			List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
			AgrupacionSimpleDto agrupacionSimpleDto  =new AgrupacionSimpleDto(); 
			 List<TiivsPersona>lstPersonas=new ArrayList<TiivsPersona>();
			TiivsPersona objPersona=new TiivsPersona();
			
		    lstPoderdantes = new ArrayList<TiivsPersona>();
		    lstApoderdantes = new ArrayList<TiivsPersona>();
		   
		    for (TiivsSolicitudAgrupacion x : tmpSol.getTiivsSolicitudAgrupacions()) 
		    {
			   for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) 
			   {
				    if (tmpSol.getCodSoli().equals(d.getCodSoli()) && tmpSol.getCodSoli().equals(x.getId().getCodSoli()))
				    {
				    	//logger.info("d.getTiivsPersona() "+d.getTiivsPersona().getTipDoi());
				    	lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
					    objPersona=new TiivsPersona();
					    objPersona=d.getTiivsPersona();
					    objPersona.setTipPartic(d.getTipPartic());
					    objPersona.setsDesctipPartic(this.obtenerDescripcionTipoRegistro(d.getTipPartic().trim()));
					    objPersona.setClasifPer(d.getClasifPer());
					    objPersona.setsDescclasifPer(this.obtenerDescripcionClasificacion(d.getClasifPer().trim()));
					    objPersona.setsDesctipDoi(this.obtenerDescripcionDocumentos(d.getTiivsPersona().getTipDoi().trim()));
					    lstPersonas.add(objPersona);
						  
					    if(d.getTipPartic().trim().equals(ConstantesVisado.PODERDANTE))
					    {
							lstPoderdantes.add(d.getTiivsPersona());
					    }
						else if(d.getTipPartic().trim().equals(ConstantesVisado.APODERADO))
						{
							lstApoderdantes.add(d.getTiivsPersona());
						}
					    
					    agrupacionSimpleDto = new AgrupacionSimpleDto();
						agrupacionSimpleDto.setId(new TiivsSolicitudAgrupacionId(
								tmpSol.getCodSoli(), x.getId()
										.getNumGrupo()));
						agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
						agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
						agrupacionSimpleDto.setsEstado(Utilitarios
								.obternerDescripcionEstado(x.getEstado().trim()));
						agrupacionSimpleDto.setLstPersonas(lstPersonas);
						lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
				    }
			   }
		    }
		    
		    cadena="";
		    for (TiivsPersona tmpPoder: lstPoderdantes)
		    {
		    	cadena += devolverDesTipoDOI(tmpPoder.getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmpPoder.getNumDoi() +
						  ConstantesVisado.GUION + (tmpPoder.getApePat()==null?"":tmpPoder.getApePat()) + " " + (tmpPoder.getApeMat()==null?"":tmpPoder.getApeMat()) + " " + 
						  (tmpPoder.getNombre()==null?"":tmpPoder.getNombre()) + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
		    	
		    }
		    
		    tmpSol.setTxtPoderdante(cadena);
			
			// Cargar data de apoderados
			cadena="";
			/*for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getCodSoli().equals(tmpSol.getCodSoli()) && tmp.getClasifPer().equals(ConstantesVisado.APODERADO))
				{
					if (tmp.getClasifPer().equals(combosMB.getLstTipoRegistroPersona().get(0).getKey()))
					{
						cadena += devolverDesTipoDOI(tmp.getTiivsPersona().getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmp.getTiivsPersona().getNumDoi() +
								  ConstantesVisado.GUION + tmp.getTiivsPersona().getApePat() + " " + tmp.getTiivsPersona().getApeMat() + " " + 
								  tmp.getTiivsPersona().getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
					}
				}
			}*/
			
			for (TiivsPersona tmpApor: lstApoderdantes)
		    {
		    	cadena += devolverDesTipoDOI(tmpApor.getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmpApor.getNumDoi() +
						  ConstantesVisado.GUION + (tmpApor.getApePat()== null ?"":tmpApor.getApePat()) + " " + (tmpApor.getApeMat()==null?"":tmpApor.getApeMat()) + " " + 
						  (tmpApor.getNombre()==null?"": tmpApor.getNombre()) + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
		    }
			
			tmpSol.setTxtApoderado(cadena);
			
			//Carga las operaciones bancarias asociadas a una solicitud
			cadena="";
			for (TiivsSolicitudOperban tmp: combosMB.getLstSolOperBan())
			{
				if (tmp.getId().getCodSoli().equals(tmpSol.getCodSoli()))
				{
					cadena +=  devolverDesOperBan(tmp.getId().getCodOperBan())  + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
				}
			}
			
			tmpSol.setTxtOpeBan(cadena);
			String cadNiveles = "";			
			
			for (Iterator iterator = tmpSol.getTiivsSolicitudNivels().iterator(); iterator.hasNext();) {
				TiivsSolicitudNivel tmp = (TiivsSolicitudNivel) iterator.next();
				String nivel = nivelService.buscarNivelxCodigo(tmp.getCodNiv());
				
				if (cadNiveles.length()>0)
				{
					cadNiveles = cadNiveles.concat(",").concat(nivel);
				}
				else
				{
					cadNiveles = cadNiveles.concat(nivel);
				}
			}
			
			if (cadNiveles.endsWith(","))
			{
				cadNiveles = cadNiveles.substring(0,cadNiveles.length()-1);
			}
						
			
			//logger.info("Niveles encontrados:" + cadNiveles);
			
			tmpSol.setTxtNivel(cadNiveles);
			//Proceso para obtener los niveles de cada solicitud
		/*	if (tmpSol.getImporte() != 0) 
			{
				if (combosMB.getLstNivel().size() > 0) 
				{
					String txtNivelTMP = "";
					String descripcion = buscarDescripcionMoneda(tmpSol.getMoneda());
					//logger.debug("Moneda encontrada: " + descripcion);

					for (TiivsNivel tmp : combosMB.getLstNivel()) 
					{
						if (tmp.getMoneda().equalsIgnoreCase(tmpSol.getMoneda())) 
						{
							if (tmp.getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL1)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni = Double.valueOf(tmp.getRangoInicio());
								rangoFin = Double.valueOf(tmp.getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									txtNivelTMP += ConstantesVisado.CAMPO_NIVEL1;
								}
							}

							if (tmp.getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL2)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni =  Double.valueOf(tmp.getRangoInicio());
								rangoFin =  Double.valueOf(tmp.getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									if (txtNivelTMP.length() > 0) 
									{
										txtNivelTMP += "," + ConstantesVisado.CAMPO_NIVEL2;
									} 
									else 
									{
										txtNivelTMP += ConstantesVisado.CAMPO_NIVEL2;
									}
								}
							}

							if (tmp.getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL3)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni = Double.valueOf(tmp.getRangoInicio());
								rangoFin = Double.valueOf(tmp.getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									if (txtNivelTMP.length() > 0) 
									{
										txtNivelTMP += "," 	+ ConstantesVisado.CAMPO_NIVEL3;
									} 
									else 
									{
										txtNivelTMP += ConstantesVisado.CAMPO_NIVEL3;
									}
								}
							}
							
							if (tmp.getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL4)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni = Double.valueOf(tmp.getRangoInicio());
								rangoFin = Double.valueOf(tmp.getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									if (txtNivelTMP.length() > 0) 
									{
										txtNivelTMP += "," 	+ ConstantesVisado.CAMPO_NIVEL4;
									} 
									else 
									{
										txtNivelTMP += ConstantesVisado.CAMPO_NIVEL4;
									}
								}
							}
						}

					}
					
					tmpSol.setTxtNivel(txtNivelTMP);
				}
				else
				{
					
					 * txtNivelTMP+=ConstantesVisado.CAMPO_NIVEL4;
					 * tmpSol.setTxtNivel(txtNivelTMP);
					 
				}
			}*/
			/*else 
			{
				logger.debug("No se pudo obtener los rangos de los niveles para la solicitud. Verificar base de datos!!");
			}*/
		}
		
	}
	
	public String obtenerGenerador()
	{
		String resultado="";
		
		if (usuario != null)
		{
			resultado = usuario.getUID() + ConstantesVisado.GUION + usuario.getNombre() +  ConstantesVisado.ESPACIO_BLANCO + 
						usuario.getApellido1() + ConstantesVisado.ESPACIO_BLANCO + usuario.getApellido2();
		}
		else
		{
			logger.debug("Error al obtener datos del usuario de session para mostrar en el excel");
		}
		return resultado;
	}
	
	public void exportarExcelPOI()
	{
		crearExcel();
	}
	
	public void generarNombreArchivo() 
	{
		String grupoSSJJ = (String) Utilitarios.getObjectInSession("GRUPO_JRD");
		String grupoADM = (String) Utilitarios.getObjectInSession("GRUPO_ADM");
		String grupoOFI = (String) Utilitarios.getObjectInSession("GRUPO_OFI");
		String rol="";
		
		if (grupoSSJJ!=null)
		{
			rol="SSJJ";
		}
		else if (grupoADM!=null)
		{
			rol="ADM";
		}
		else if (grupoOFI!=null)
		{
			rol="OFI";
		}
		
		setNombreArchivoExcel("Solicitudes_Visado "	+ Utilitarios.obtenerFechaArchivoExcel() + ConstantesVisado.UNDERLINE + rol);
	}

	
	private void crearExcel() 
	{
		try 
		{
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, ConstantesVisado.TITULO_CABECERA_EXCEL,12);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));
			
			//Se crea la leyenda de quien genero el archivo y la hora respectiva
			Row rowG = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			Utilitarios.crearCell(wb, rowG, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerGenerador(),  true, false,true,HSSFColor.GREY_25_PERCENT.index);
			
			Row rowG1 = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG1, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			Utilitarios.crearCell(wb, rowG1, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.obtenerFechaHoraActual(),  true, false,true,HSSFColor.GREY_25_PERCENT.index);
			
			//Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 4);
			
			Utilitarios.crearCell(wb, row2, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_NRO_SOL, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			if (getCodSolicitud()!=null)
			{
				Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getCodSolicitud(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			if (lstEstadoSelected!=null)
			{
				String cadena = "";
								
				int j=0;
				int cont=1;
				
				for (;j<=lstEstadoSelected.size()-1;j++)
				{
					if (lstEstadoSelected.size()>1)
					{
						if (cont==lstEstadoSelected.size())
						{
							cadena=cadena.concat(buscarEstadoxCodigo((lstEstadoSelected.get(j).toString())));
						}
						else
						{
							cadena=cadena.concat(buscarEstadoxCodigo(lstEstadoSelected.get(j).toString()).concat(","));
							cont++;
						}
					}
					else
					{
						cadena = buscarEstadoxCodigo(lstEstadoSelected.get(j).toString());
					}		
				}
				
				Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row2, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_IMPORTE, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) 
			{
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,  ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_1, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
			{
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,  ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_2, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
			{
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,  ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_3, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
			{
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,  ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_4, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row2, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_SOL, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (lstTipoSolicitudSelected!=null)
			{
				String cadena = "";
								
				int j=0;
				int cont=1;
				
				for (;j<=lstTipoSolicitudSelected.size()-1;j++)
				{
					if (lstTipoSolicitudSelected.size()>1)
					{
						if (cont==lstTipoSolicitudSelected.size())
						{
							cadena=cadena.concat(buscarTipoSolxCodigo((lstTipoSolicitudSelected.get(j).toString())));
						}
						else
						{
							cadena=cadena.concat(buscarTipoSolxCodigo(lstTipoSolicitudSelected.get(j).toString()).concat(","));
							cont++;
						}
					}
					else
					{
						cadena = buscarTipoSolxCodigo(lstTipoSolicitudSelected.get(j).toString());
					}		
				}
				
				Utilitarios.crearCell(wb, row2, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Row row3 = sheet.createRow((short) 5);
			Utilitarios.crearCell(wb, row3, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_FECHA, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getIdTiposFecha().compareTo("")!=0)
			{
				String cadena = "";
				cadena= buscarTipoFechaxCodigo(getIdTiposFecha());
								
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getFechaInicio()!=null)
			{
				SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				String sFechaInicio = sf1.format(getFechaInicio());
				
				Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, sFechaInicio, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row3, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getFechaFin()!=null)
			{
				SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				String sFechaFin = sf1.format(getFechaFin());
				
				Utilitarios.crearCell(wb, row3, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,sFechaFin, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row3, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_OPE, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getIdOpeBan().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row3, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarOpeBanxCodigo(getIdOpeBan()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Row row5 = sheet.createRow((short) 7);
			
			Utilitarios.crearCell(wb, row5, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DOI_APODERADO, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getNroDOIApoderado()!=null)
			{
				Utilitarios.crearCell(wb, row5, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getNroDOIApoderado(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row5, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row5, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_APODERADO, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getTxtNomApoderado()!=null)
			{
				Utilitarios.crearCell(wb, row5, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getTxtNomApoderado(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row5, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row5, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DOI_PODERDANTE, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getNroDOIPoderdante()!=null)
			{
				Utilitarios.crearCell(wb, row5, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getNroDOIPoderdante(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row5, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row5, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_PODERDANTE, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getTxtNomPoderdante()!=null)
			{
				Utilitarios.crearCell(wb, row5, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getTxtNomPoderdante(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row5, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Row row6 = sheet.createRow((short) 8);
			Utilitarios.crearCell(wb, row6, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_OFICINA, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getOficina()!=null)
			{
				Utilitarios.crearCell(wb, row6, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getOficina().getDesOfi(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row6, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row6, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_NIVEL, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getLstNivelSelected()!=null)
			{
				String cadena = "";
				
				int j=0;
				int cont=1;
				
				for (;j<=getLstNivelSelected().size()-1;j++)
				{
					if (getLstNivelSelected().size()>1)
					{
						if (cont==getLstNivelSelected().size())
						{
							cadena=cadena.concat(getLstNivelSelected().get(j).toString());
						}
						else
						{
							cadena=cadena.concat(getLstNivelSelected().get(j).toString()).concat(",");
							cont++;
						}
					}
					else
					{
						cadena = getLstNivelSelected().get(j).toString();
					}		
				}
				
				Utilitarios.crearCell(wb, row6, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row6, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row6, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO_NIVEL, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getLstEstadoNivelSelected()!=null)
			{
				String cadena = "";
				
				int j=0;
				int cont=1;
				
				for (;j<=getLstEstadoNivelSelected().size()-1;j++)
				{
					if (getLstEstadoNivelSelected().size()>1)
					{
						if (cont==getLstEstadoNivelSelected().size())
						{
							cadena=cadena.concat(buscarEstNivelxCodigo((getLstEstadoNivelSelected().get(j).toString())));
						}
						else
						{
							cadena=cadena.concat(buscarEstNivelxCodigo(getLstEstadoNivelSelected().get(j).toString()).concat(","));
							cont++;
						}
					}
					else
					{
						cadena = buscarEstNivelxCodigo(getLstEstadoNivelSelected().get(j).toString());
					}		
				}
				
				Utilitarios.crearCell(wb, row6, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row6, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row6, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTUDIO,false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getLstEstudioSelected()!=null)
			{
				String cadena = "";
				
				int j=0;
				int cont=1;
				
				for (;j<=getLstEstudioSelected().size()-1;j++)
				{
					if (getLstEstudioSelected().size()>1)
					{
						if (cont==getLstEstudioSelected().size())
						{
							cadena=cadena.concat(buscarEstudioxCodigo((getLstEstudioSelected().get(j).toString())));
						}
						else
						{
							cadena=cadena.concat(buscarEstudioxCodigo(getLstEstudioSelected().get(j).toString()).concat(","));
							cont++;
						}
					}
					else
					{
						cadena = buscarEstudioxCodigo(getLstEstudioSelected().get(j).toString());
					}		
				}
				
				Utilitarios.crearCell(wb, row6, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row6, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Row row7 = sheet.createRow((short) 9);
			Utilitarios.crearCell(wb, row7, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_REVISION, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (bRevision)
			{
				Utilitarios.crearCell(wb, row7, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row7, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row7, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DELEGADO, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (bDelegados)
			{
				Utilitarios.crearCell(wb, row7, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row7, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			Utilitarios.crearCell(wb, row7, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_REVOCATORIA, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (bRevocatoria)
			{
				Utilitarios.crearCell(wb, row7, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row7, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
		
			if (solicitudes.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un diseño a la celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ITEM, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_NRO_SOLICITUD, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_COD_OFICINA, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_OFICINA, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ESTADO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_IMPORTE, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_PODERDANTE, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 8, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_APODERADO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 9, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TIPO_SOL, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 10, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TIPO_OPE, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 11, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ESTUDIO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 12, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_NIVEL, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 13, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_ENVIO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 14, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_RPTA, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 15, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_ESTADO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 16, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_COMISION, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 17, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_LIBERADO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 18, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_DELEGADO, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 19, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_EN_REVISION, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				Utilitarios.crearCell(wb, rowT, 20, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_REVOCATORIA, true, true,false,HSSFColor.GREY_25_PERCENT.index);
				
				int numReg=13;
				int contador=0;
				for (TiivsSolicitud tmp: solicitudes)
				{
					contador++;
					//Columna Item en Excel
					Row row = sheet.createRow((short) numReg);
					if (contador<=9)
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.TRES_CEROS + contador, true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else if (contador<=99 && contador >9)
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, ConstantesVisado.DOS_CEROS + contador, true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else if(contador>=99 && contador<999)
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.CERO + contador, true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(contador), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					//Columna Nro Solicitud en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCodSoli(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
										
					//Columna Cod Oficina en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsOficina1().getCodOfi()),true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Oficina en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsOficina1().getDesOfi()),true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Territorio en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarDesTerritorio(tmp.getTiivsOficina1().getTiivsTerritorio().getCodTer()),true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Estado en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtEstado(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Importe en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtImporte(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Poderdante en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtPoderdante(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Apoderado en Excel
					Utilitarios.crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtApoderado(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Tipo Solicitud en Excel
					Utilitarios.crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsTipoSolicitud().getDesTipServicio()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Operaciones Bancarias en Excel
					Utilitarios.crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtOpeBan(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Estudio en Excel
					if (tmp.getTiivsEstudio()!=null)
					{
						Utilitarios.crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsEstudio().getDesEstudio()) , true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(null),true,false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					//Columna Nivel en Excel
					Utilitarios.crearCell(wb, row, 12, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtNivel(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
					//Columna Fecha Envio en Excel
					if (tmp.getFechaEnvio()!=null)
					{
						Utilitarios.crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaEnvio()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
							
					//Columna Fecha Rpta en Excel
					if (tmp.getFechaRespuesta()!=null)
					{
						Utilitarios.crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaRespuesta()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					//Columna Fecha Estado en Excel
					if (tmp.getFechaEstado()!=null)
					{
						Utilitarios.crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaEstado()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					//Columna Comision en Excel
					if (tmp.getComision()!=null)
					{
						Utilitarios.crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getComision()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					//Columna Liberado
					if (tmp.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_RESERVADO_T02))
					{
						Utilitarios.crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						if (tmp.getEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02))
						{
							Utilitarios.crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.GREY_25_PERCENT.index);
						}
						else
						{
							Utilitarios.crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
						}
					}
					
					//Columna Delegado
					if (validarSolicitudConDelegacion(tmp.getCodSoli()))
					{
						Utilitarios.crearCell(wb, row, 18, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 18, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					//Columna En Revision
					if (validarSolicitudEnRevision(tmp.getCodSoli()))
					{
						Utilitarios.crearCell(wb, row, 19, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 19, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					//Columna Revocatoria
					if (validarSolicitudRevocada(tmp.getCodSoli()))
					{
						Utilitarios.crearCell(wb, row, 20, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 20, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.GREY_25_PERCENT.index);
					}
					
					numReg++;
				}
			}
			
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.setColumnWidth(3,256*20);
			sheet.setColumnWidth(4,256*20);
			sheet.setColumnWidth(6,256*15);
			sheet.setColumnWidth(7,256*20);
			sheet.setColumnWidth(8,256*20);
			sheet.autoSizeColumn(9);
			sheet.setColumnWidth(10,256*20);
			sheet.setColumnWidth(11,256*12);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			sheet.autoSizeColumn(17);
			sheet.autoSizeColumn(18);
			sheet.autoSizeColumn(19);
			sheet.autoSizeColumn(20);
						
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta="";
			if (obtenerRutaExcel().compareTo("")!=0)
			{
				
				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreArchivoExcel());
				
				strRuta = obtenerRutaExcel() + getNombreArchivoExcel() + ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				logger.debug("Ruta final donde encontrar el archivo excel: " + strRuta);
				
				setRutaArchivoExcel(strRuta);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
			//logger.info("Error al generar el archivo excel debido a: " + e.getStackTrace());
		}	
	}
	
	public void abrirExcel()
	{
		try {
			exportarExcelPOI();
			//Abrir archivo excel
				
			if (rutaArchivoExcel!=null && rutaArchivoExcel.length()>0)
			{
				Desktop.getDesktop().open(new File(rutaArchivoExcel));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("Error al abrir archivo excel debido a: " + e.getMessage());
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	public void descargarArchivo()
	{
		exportarExcelPOI();
		InputStream stream=null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: " + e.getMessage());
		}
		
		if (stream!=null)
		{
			file = new DefaultStreamedContent(stream, "application/excel", nombreArchivoExcel+ConstantesVisado.EXTENSION_XLS);
		}
	}
	
	public String obtenerRutaExcel()
	{
		String res="";
		
		for (TiivsParametros tmp: pdfViewerMB.getLstParametros())
		{
			if (usuario.getUID().equals(tmp.getCodUsuario()))
			{
				res=tmp.getRutaArchivoExcel();
				break;
			}
		}
		
		if (res.compareTo("")==0)
		{
			logger.debug("No se encontro el parametro de ruta para exportar excel para el usuario: " + usuario.getUID());
		}
		else
		{
			logger.debug("Parametro ruta encontrada para el usuario: " + usuario.getUID() + " es: " +  res);
		}
		
		return res;
	}
	
	public void limpiar()
	{
		setCodSolicitud("");
		setLstEstadoSelected(null);
		setIdImporte("");
		setLstTipoSolicitudSelected(null);
		setIdTiposFecha("");
		setFechaInicio(null);
		setFechaFin(null);
		setIdOpeBan("");
		setNroDOIApoderado("");
		setObjTiivsPersonaBusquedaNomApod(null);
		setNroDOIPoderdante("");
		setObjTiivsPersonaBusquedaNomPoder(null);
		setOficina(null);
		setLstNivelSelected(null);
		setLstEstadoNivelSelected(null);
		setLstEstudioSelected(null);
		setbRevision(false);
		setbDelegados(false);
		setbRevocatoria(false);
		
		
	}
		
	// Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a
	// los filtros seleccionados.
	// @Autor: Cesar La Rosa
	// @Version: 4.0
	// @param: -
	// Corregir: nivel, poder, apod, exportacion filtros de combos con mas de un valor
	@SuppressWarnings("unchecked")
	public void busquedaSolicitudes() 
	{
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);

		// solicitudes = new ArrayList<TiivsSolicitud>();

		// 1. Filtro por codigo de solicitud (funciona)
		if (getCodSolicitud().compareTo("") != 0) 
		{
			logger.info("Filtro por codigo de solicitud: " + getCodSolicitud());
			String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE + getCodSolicitud().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_COD_SOLICITUD,"%"+filtroNuevo));
		}

		// 2. Filtro por estado (si funciona, validar que campo estado en BD no tenga espacios en blanco)
		if (lstEstadoSelected.size() > 0) 
		{
			int ind = 0;

			for (; ind <= lstEstadoSelected.size() - 1; ind++) 
			{
				logger.info("Filtro por estados: " + lstEstadoSelected.get(ind));
			}
			
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,lstEstadoSelected));
		}

		// 3. Filtro por importe (funciona)
		if (getIdImporte().compareTo("") != 0) 
		{
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) 
			{
				logger.info("Filtro por importe: " + getIdImporte());
				filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_IMPORTE,(ConstantesVisado.VALOR_RANGO_CINCUENTA)));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
			{
				logger.info("Filtro por importe: " + getIdImporte());
				filtroSol.add(Restrictions.gt(ConstantesVisado.CAMPO_IMPORTE,ConstantesVisado.VALOR_RANGO_CINCUENTA));
				filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_IMPORTE,ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
			{
				logger.info("Filtro por importe: " + getIdImporte());
				filtroSol.add(Restrictions.gt(ConstantesVisado.CAMPO_IMPORTE,(ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE)));
				filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_IMPORTE,(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
			{
				logger.info("Filtro por importe: " + getIdImporte());
				filtroSol.add(Restrictions.gt(ConstantesVisado.CAMPO_IMPORTE,(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}
		}

		// 4. Filtro por tipo de solicitud (funciona)
		if (lstTipoSolicitudSelected.size() > 0) {
			int ind = 0;

			for (; ind <= lstTipoSolicitudSelected.size() - 1; ind++) 
			{
				logger.info("Filtro por tipo de solicitud: " + lstTipoSolicitudSelected.get(ind));
			}
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_TIPO_SOLICITUD,	ConstantesVisado.ALIAS_TBL_TIPO_SOLICITUD);
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_TIPO_SOL_ALIAS,lstTipoSolicitudSelected));
		}

		// 5. Filtro por tipo de fecha (no funciona)
		if (getIdTiposFecha().compareTo("") != 0) 
		{
			if (getIdTiposFecha().equalsIgnoreCase(ConstantesVisado.TIPO_FECHA_ENVIO)) // Es fecha de envio
			{
				if (getFechaFin().before(getFechaInicio()))
				{
					logger.debug("La fecha de inicio debe ser menor a la fecha de fin");
					Utilitarios.mensajeInfo("", "La fecha de inicio debe ser menor a la fecha de fin");
				}
				else
				{
					logger.info("Filtrando por fecha de envio");
					
				 	try {
						SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
						String fecIni = formatter.format(getFechaInicio());
						Date minDate = formatter.parse(fecIni);
						String fecFin = formatter.format(getFechaFin());
						Date maxDate = formatter.parse(fecFin);
						Date rangoFin = new Date(maxDate.getTime() + TimeUnit.DAYS.toMillis(1));
						
						logger.info("Fecha Inicio: " + minDate);
						logger.info("Fecha Fin: " + rangoFin);
						
						filtroSol.add(Restrictions.ge(ConstantesVisado.CAMPO_FECHA_ENVIO, minDate));
						filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_FECHA_ENVIO, rangoFin));
						
						//Verificar que el campo estado no tenga espacios en blanco en BD
						filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,ConstantesVisado.ESTADOS.ESTADO_COD_ENVIADOSSJJ_T02));
						filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
				 	
				 	filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
				}
			}
			if (getIdTiposFecha().equalsIgnoreCase(ConstantesVisado.TIPO_FECHA_RPTA)) // Sino es fecha de respuesta
			{
				if (getFechaFin().before(getFechaInicio()))
				{
					logger.debug("La fecha de inicio debe ser menor a la fecha de fin");
					Utilitarios.mensajeInfo("", "La fecha de inicio debe ser menor a la fecha de fin");
				}
				else
				{
					logger.info("Filtrando por fecha de rpta");
									
					try {
						SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
						String fecIni = formatter.format(getFechaInicio());
						Date minDate = formatter.parse(fecIni);
						String fecFin = formatter.format(getFechaFin());
						Date maxDate = formatter.parse(fecFin);
						Date rangoFin = new Date(maxDate.getTime() + TimeUnit.DAYS.toMillis(1));
						
						logger.info("Fecha Inicio: " + minDate);
						logger.info("Fecha Fin: " + rangoFin);
						
						filtroSol.add(Restrictions.ge(ConstantesVisado.CAMPO_FECHA_RPTA, minDate));
						filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_FECHA_RPTA, rangoFin));
						
						List<String> tmpEstados = new ArrayList<String>();
						tmpEstados.add(ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02);
						tmpEstados.add(ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02);
						tmpEstados.add(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02);
						
						//Verificar que el campo estado no tenga espacios en blanco en BD
						filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,tmpEstados));
						
					} catch (ParseException e) {
						logger.info("Hubo un error al convertir la fecha: ",e);
					}
	
					filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
				}
			}
		}

		// 6. Filtro por operacion bancaria (funciona)
		if (getIdOpeBan().compareTo("") != 0) 
		{
			lstSolicitudesxOpeBan.clear();
			for (TiivsSolicitudOperban opeBanTMP: combosMB.getLstSolOperBan())
			{
				if (opeBanTMP.getId().getCodOperBan().equals(getIdOpeBan()))
				{
					lstSolicitudesxOpeBan.add(opeBanTMP.getId().getCodSoli());
				}
			}
			
			logger.info("Filtro por operacion bancaria");
			
			if (lstSolicitudesxOpeBan.size()>0)
			{
				int ind = 0;

				for (; ind <= lstSolicitudesxOpeBan.size() - 1; ind++) 
				{
					logger.info("Filtro operacion" + "[" + ind + "]" + lstSolicitudesxOpeBan.get(ind));
				}
				
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,lstSolicitudesxOpeBan));
			}
			else
			{
				logger.info("No se selecciono ninguna operacion bancaria");
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,""));
			}			
		}

		// 8. Filtro por nombre de oficina (funciona)
		if (!PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
		{
			if (getOficina() != null) 
			{
				if(getOficina().getDesOfi()!=null){
					logger.info("Filtro Oficina: " + getOficina().getCodOfi());		
					filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,	ConstantesVisado.ALIAS_TBL_OFICINA);
					String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE + getOficina().getDesOfi().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
					filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_NOM_OFICINA_ALIAS, filtroNuevo));
				}			
			}
		}
		// 11. Filtro por numero de documento de apoderado (funciona)
		if (getNroDOIApoderado().compareTo("") != 0) 
		{
			String codSol="";
			int ind=0;
			List<String> lstSolicitudes = new ArrayList<String>();
			
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getTiivsPersona().getNumDoi().equals(getNroDOIApoderado()) && tmp.getTipPartic().equals(ConstantesVisado.APODERADO))
				{
					codSol = tmp.getCodSoli();
					lstSolicitudes.add(codSol);
				}
			}
			
			logger.info("Filtro por numero de documento apoderado: " + getNroDOIApoderado());
			
			for (; ind <= lstSolicitudes.size() - 1; ind++) 
			{
				logger.info("Solicitudes encontradas" + "[" + ind + "]" + lstSolicitudes.get(ind));
			}
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,lstSolicitudes));
		}

		// 12. Filtro por nombre de apoderado (funciona)
		if (objTiivsPersonaBusquedaNomApod!=null) 
		{
			/*String codSol="";
			int ind=0;
			List<String> lstSolicitudes = new ArrayList<String>();
			
			for (TiivsAgrupacionPersona tmp: obtenerSolicitudesxFiltroPersonas())
			{
				if ((tmp.getTiivsPersona().getNombre().toUpperCase().indexOf(getTxtNomApoderado().toUpperCase())!=-1  
					|| tmp.getTiivsPersona().getApeMat().toUpperCase().indexOf(getTxtNomApoderado().toUpperCase())!=-1 
					|| tmp.getTiivsPersona().getApePat().toUpperCase().indexOf(getTxtNomApoderado().toUpperCase())!=-1)
					
					&& tmp.getTipPartic().equals(ConstantesVisado.APODERADO))
				{
					codSol = tmp.getCodSoli();
					lstSolicitudes.add(codSol);
				}
			}
			
			logger.info("Filtro por apoderado: " + getTxtNomApoderado());
			for (; ind <= lstSolicitudes.size() - 1; ind++) 
			{
				logger.info("Solicitudes encontradas" + "[" + ind + "]" + lstSolicitudes.get(ind));
			}*/
			
			logger.info("Filtro por apoderado: " + objTiivsPersonaBusquedaNomApod.getNombreCompletoMayuscula());
			List<String> lstSolicitudes = new ArrayList<String>();
			int ind=0;
			lstSolicitudes = obtenerSolicitudesxFiltroPersonas(objTiivsPersonaBusquedaNomApod,ConstantesVisado.CAMPO_APODERADO);
			
			for (; ind <= lstSolicitudes.size() - 1; ind++) 
			{
				logger.info("Solicitudes encontradas" + "[" + ind + "]" + lstSolicitudes.get(ind));
			}
			
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,lstSolicitudes));
		}
		
		// 13. Filtro por numero de documento de poderdante (funciona)
		if (getNroDOIPoderdante().compareTo("") != 0) 
		{
			String codSol="";
			int ind=0;
			List<String> lstSolicitudes = new ArrayList<String>();
			
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getTiivsPersona().getNumDoi().equals(getNroDOIPoderdante()) && tmp.getTipPartic().equals(ConstantesVisado.PODERDANTE))
				{
					codSol = tmp.getCodSoli();
					lstSolicitudes.add(codSol);
				}
			}
			
			logger.info("Filtro por nro documento poderdante: " + getNroDOIPoderdante());
			for (; ind <= lstSolicitudes.size() - 1; ind++) 
			{
				logger.info("Solicitudes encontradas" + "[" + ind + "]" + lstSolicitudes.get(ind));
			}
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,lstSolicitudes));
		}

		// 14. Filtro por nombre de poderdante (funciona)
		if (objTiivsPersonaBusquedaNomPoder!=null) 
		{
			/*String codSol="";
			int ind=0;
			List<String> lstSolicitudes = new ArrayList<String>();
			
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if ((tmp.getTiivsPersona().getNombre().toUpperCase().indexOf(getTxtNomPoderdante().toUpperCase())!=-1  
					|| tmp.getTiivsPersona().getApeMat().toUpperCase().indexOf(getTxtNomPoderdante().toUpperCase())!=-1 
					|| tmp.getTiivsPersona().getApePat().toUpperCase().indexOf(getTxtNomPoderdante().toUpperCase())!=-1)
					
					&& tmp.getTipPartic().equals(ConstantesVisado.PODERDANTE))
				{
					codSol = tmp.getCodSoli();
					lstSolicitudes.add(codSol);
				}
			}
			
			logger.info("Filtro por poderdante: " + getTxtNomPoderdante());
			for (; ind <= lstSolicitudes.size() - 1; ind++) 
			{
				logger.info("Solicitudes encontradas" + "[" + ind + "]" + lstSolicitudes.get(ind));
			}*/
			
			logger.info("Filtro por poderdante: " + objTiivsPersonaBusquedaNomPoder.getNombreCompletoMayuscula());
			List<String> lstSolicitudes = new ArrayList<String>();
			int ind=0;
			lstSolicitudes = obtenerSolicitudesxFiltroPersonas(objTiivsPersonaBusquedaNomPoder,ConstantesVisado.CAMPO_PODERDANTE);
			
			for (; ind <= lstSolicitudes.size() - 1; ind++) 
			{
				logger.info("Solicitudes encontradas" + "[" + ind + "]" + lstSolicitudes.get(ind));
			}
			
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,lstSolicitudes));
		}

		// 15. Filtro por nivel (funciona)
		if (lstNivelSelected.size() > 0) 
		{
			/*lstSolicitudesSelected.clear();
			for (TiivsSolicitud sol : solicitudes) 
			{
				if (sol.getTxtNivel() != null && sol.getTxtNivel().length() > 0) 
				{
					if (lstNivelSelected.get(0).indexOf(sol.getTxtNivel()) != -1) 
					{
							lstSolicitudesSelected.add(sol.getCodSoli());
					}
				}
			}
			
			int ind = 0;

			for (; ind <= lstNivelSelected.size() - 1; ind++) 
			{
				logger.info("Filtro nivel" + "[" + ind + "]" + lstNivelSelected.get(ind));
			}
			*/
			
			int ind = 0;
			List<String> lstCodNiv = new ArrayList<String>();
			
			for (; ind <= lstNivelSelected.size() - 1; ind++) 
			{
				logger.info("Filtro nivel" + "[" + ind + "]" + lstNivelSelected.get(ind));
				String nivel = nivelService.buscarNivelxDescrip(lstNivelSelected.get(ind));
				
				if (nivel!=null)
				{
					lstCodNiv.add(nivel);
				}
			}
						
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,	obtenerSolicitudesxFiltroNivels(lstCodNiv)));
		}
		
		// 16. Filtro por estado nivel (funciona)
		if (lstEstadoNivelSelected.size() > 0)
		{
			GenericDao<TiivsSolicitudNivel, Object> busqSolNivDAO = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsSolicitudNivel.class);
			List<TiivsSolicitudNivel> lstSolNivel = new ArrayList<TiivsSolicitudNivel>();
			/*for(int i=0;i<lstEstadoNivelSelected.size() ; i++){
				if(lstEstadoNivelSelected.get(i).equals("0001")){
					lstEstadoNivelSelected.add("");
				}
			}*/
			
			filtro.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO_NIVEL, lstEstadoNivelSelected));
			
			try {
				lstSolNivel = busqSolNivDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.info("Error al buscar los estados de los niveles en las solicitudes");
			}
			
			lstSolicitudesSelected.clear();
			
			for (TiivsSolicitudNivel sol : lstSolNivel) 
			{
				lstSolicitudesSelected.add(sol.getTiivsSolicitud().getCodSoli());
			}
			
			int ind = 0;

			for (; ind <= lstEstadoNivelSelected.size() - 1; ind++) 
			{
				logger.info("Filtro estado nivel" + "[" + ind + "]" + lstEstadoNivelSelected.get(ind));
			}
			
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,	lstSolicitudesSelected));
		}
		
		// 17. Filtro por estudio (funciona)
		if (lstEstudioSelected.size() > 0) {
			
			// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTUDIO,
			// getIdEstudio()));
			
			int ind = 0;

			for (; ind <= lstEstudioSelected.size() - 1; ind++) 
			{
				logger.info("Filtro estudio" + "[" + ind + "]" + lstEstudioSelected.get(ind));
			}
			
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_ESTUDIO,	ConstantesVisado.ALIAS_TBL_ESTUDIO);
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_ESTUDIO_ALIAS, lstEstudioSelected));
		}

		// 19. Filtrar solicitudes con Revision (funciona)
		if (getbRevision()) 
		{
			String codigoSolicEnRevision = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_EN_REVISION);
			GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
			filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicEnRevision));

			try {
				lstHistorial = busqHisDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.info("Error al buscar en historial de solicitudes");
			}
			
			lstSolicitudesSelected.clear();
			for (TiivsHistSolicitud tmp: lstHistorial)
			{
				if (lstHistorial!=null && lstHistorial.size()>0)
				{
					lstSolicitudesSelected.add(tmp.getId().getCodSoli());
				}
			}
						
			if (lstSolicitudesSelected.size() > 0) 
			{
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD, lstSolicitudesSelected));
			} 
			else 
			{
				logger.info("No hay solicitudes en el historial con estado En Revision");
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,""));
			}
		}
		
		// 20. Filtrar solicitudes que se hayan Delegado (funciona)
		if (getbDelegados()) 
		{
			String codigoSolicVerA = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A);
			String codigoSolicVerB = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_B);

			GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
			filtro.add(Restrictions.or(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO, codigoSolicVerA),Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicVerB)));
			filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_NIVEL_ROL, ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO));
			filtro.add(Restrictions.or(Restrictions.eq(ConstantesVisado.CAMPO_NIVEL_ESTADO, ConstantesVisado.ESTADOS.ESTADO_COD_Desaprobado_T09),
					   				   Restrictions.eq(ConstantesVisado.CAMPO_NIVEL_ESTADO,ConstantesVisado.ESTADOS.ESTADO_COD_Aprobado_T09)));

			try {
				lstHistorial = busqHisDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.info("Error al buscar en historial de solicitudes");
			}
			
			lstSolicitudesSelected.clear();
			if (lstHistorial!=null) 
			{
				// Colocar aqui la logica para filtrar los niveles aprobados o rechazados
				/*GenericDao<TiivsSolicitudNivel, Object> busqSolNivDAO = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
				Busqueda filtro2 = Busqueda.forClass(TiivsSolicitudNivel.class);
				List<TiivsSolicitudNivel> lstSolNivel = new ArrayList<TiivsSolicitudNivel>();
								
				try {
					lstSolNivel = busqSolNivDAO.buscarDinamico(filtro2);
				} catch (Exception e) {
					logger.info("Error al buscar los estados de los niveles en las solicitudes");
				}
				
				for (TiivsSolicitudNivel tmp: lstSolNivel)
				{
					for (TiivsHistSolicitud hist: lstHistorial)
					{
						if (tmp.getTiivsSolicitud().getCodSoli().equals(hist.getId().getCodSoli()))
						{
							lstSolicitudesSelected.add(hist.getId().getCodSoli());
						}
					}
				}*/
				
				for (TiivsHistSolicitud hist: lstHistorial)
				{
					lstSolicitudesSelected.add(hist.getId().getCodSoli());
				}
				
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,	lstSolicitudesSelected));
			}
			else
			{
				logger.info("No hay solicitudes en el historial con delegacion de niveles");
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,""));
			}
		}

		// 21. Filtrar solicitudes que se hayan Revocado (funciona)
		if (getbRevocatoria()) 
		{
			List<TiivsSolicitudAgrupacion> lstRev = new ArrayList<TiivsSolicitudAgrupacion>();
			GenericDao<TiivsSolicitudAgrupacion, Object> busqSolAgrp = (GenericDao<TiivsSolicitudAgrupacion, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsSolicitudAgrupacion.class);
			filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_3));
			
			try {
				lstRev = busqSolAgrp.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.info("Error al buscar en solicitud agrupacion");
			}
			
			lstSolicitudesSelected.clear();
			for (TiivsSolicitudAgrupacion tmp: lstRev)
			{
				if (lstRev!=null && lstRev.size()>0)
				{
					lstSolicitudesSelected.add(tmp.getId().getCodSoli());
				}
			}
			
			if (lstRev.size()>0)
			{
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD, lstSolicitudesSelected));
			}
			else 
			{
				logger.info("No hay solicitudes con combinaciones revocadas");
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,""));
			}
		}
		
		if(PERFIL_USUARIO.equals(ConstantesVisado.ABOGADO) )
		{
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_ESTUDIO,	ConstantesVisado.ALIAS_TBL_ESTUDIO);
			filtroSol.add(Restrictions.eq(ConstantesVisado.ALIAS_COD_ESTUDIO, buscarEstudioxAbogado()));
		}
		else if (PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))
		{
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA, ConstantesVisado.ALIAS_TBL_OFICINA);
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA_ALIAS_FILTRO, usuario.getBancoOficina().getCodigo().trim()));
		}
		
		filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
		
		// Buscar solicitudes de acuerdo a criterios seleccionados
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes: " + ex.getStackTrace());
		}

		if (solicitudes.size() == 0) 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_SIN_REGISTROS,solicitudes.size());
			setNoHabilitarExportar(true);
		} 
		else 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + solicitudes.size() + ConstantesVisado.MSG_REGISTROS,solicitudes.size());
			actualizarDatosGrilla();
			setNoHabilitarExportar(false);
		}
	}
	
	public List<String> obtenerSolicitudesxFiltroPersonas(TiivsPersona filtroPer, String tipo)
	{
		List<TiivsAgrupacionPersona> lstAgrpPerTMP = new ArrayList<TiivsAgrupacionPersona>();
		
		GenericDao<TiivsAgrupacionPersona, Object> service = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsAgrupacionPersona.class);
		filtro.createAlias("tiivsPersona", "persona");
		
		if(filtroPer != null)
		{
			if(filtroPer.getNombreCompletoMayuscula().compareTo("")!=0)
			{
//				filtro.add(Restrictions.eq("persona.nombre", filtroPer.getNombreCompletoMayuscula().split(" ")[0]));
//				filtro.add(Restrictions.eq("persona.apePat", filtroPer.getNombreCompletoMayuscula().split(" ")[1]));
//				filtro.add(Restrictions.eq("persona.apeMat", filtroPer.getNombreCompletoMayuscula().split(" ")[2]));				
				filtro.add(Restrictions.eq("persona.nombre", filtroPer.getNombre()));
				if(filtroPer.getApePat()!=null)
					filtro.add(Restrictions.eq("persona.apePat", filtroPer.getApePat()));
				if(filtroPer.getApeMat()!=null)
					filtro.add(Restrictions.eq("persona.apeMat", filtroPer.getApeMat()));
				
				
				if (tipo.equals(ConstantesVisado.CAMPO_PODERDANTE))
				{
					filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_TIPO_PARTIC,ConstantesVisado.PODERDANTE));
				}
				else
				{
					filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_TIPO_PARTIC,ConstantesVisado.APODERADO));
				}
			}
		}
		
		try {
			lstAgrpPerTMP = service.buscarDinamico(filtro.addOrder(Order.desc("codSoli")));
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("error al obtener la lista de revocados "+  e.toString());
		}
		
		List<String> tmpAgrupSol = new ArrayList<String>();
		
		for (TiivsAgrupacionPersona tmp: lstAgrpPerTMP)
		{
			if (tmp!=null)
			{
				tmpAgrupSol.add(tmp.getCodSoli());
			}
		}
		
		return tmpAgrupSol;
	}
	
	public List<String> obtenerSolicitudesxFiltroNivels(List<String> lstTMP)
	{
		List<TiivsSolicitudNivel> lstSolNivTMP = new ArrayList<TiivsSolicitudNivel>();
		
		GenericDao<TiivsSolicitudNivel, Object> service = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsSolicitudNivel.class);
		
		if(lstTMP != null)
		{
			filtro.add(Restrictions.in(ConstantesVisado.CAMPO_COD_NIVEL, lstTMP));
		}
		
		try {
			lstSolNivTMP = service.buscarDinamico(filtro);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("error al obtener la lista de solicitudes x nivel "+  e.toString());
		}
		
		List<String> tmpSol = new ArrayList<String>();
		
		for (TiivsSolicitudNivel tmp: lstSolNivTMP)
		{
			if (tmp!=null)
			{
				tmpSol.add(tmp.getTiivsSolicitud().getCodSoli());
			}
		}
		
		return tmpSol;
	}
	
	@SuppressWarnings("unchecked")
	public void busquedaSolicitudxCodigo(String codigo) 
	{
		logger.info("Buscando solicitudes");
		
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD, codigo));
		
		// Actualizar datos de la solicitud de acuerdo al codigo.
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes: " + ex.getStackTrace());
		}

		if (solicitudes.size() == 0) 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_SIN_REGISTROS,solicitudes.size());
			setNoHabilitarExportar(true);
		} 
		else 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + solicitudes.size() + ConstantesVisado.MSG_REGISTROS,solicitudes.size());
			actualizarDatosGrilla();
			setNoHabilitarExportar(false);
		}
	}
	
	public Boolean validarSolicitudConDelegacion(String codSoli)
	{
		boolean bEncontrado=false;
		
		String codigoSolicVerA = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A);
		String codigoSolicVerB = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_B);

		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.or(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO, codigoSolicVerA),Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicVerB)));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}
		
		lstSolicitudesSelected.clear();
		if (lstHistorial.size() > 0) 
		{
			// Colocar aqui la logica para filtrar los niveles aprobados o rechazados
			GenericDao<TiivsSolicitudNivel, Object> busqSolNivDAO = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro2 = Busqueda.forClass(TiivsSolicitudNivel.class);
			List<TiivsSolicitudNivel> lstSolNivel = new ArrayList<TiivsSolicitudNivel>();
							
			try {
				lstSolNivel = busqSolNivDAO.buscarDinamico(filtro2);
			} catch (Exception e) {
				logger.debug("Error al buscar los estados de los niveles en las solicitudes");
			}
			
			for (TiivsSolicitudNivel tmp: lstSolNivel)
			{
				for (TiivsHistSolicitud hist: lstHistorial)
				{
					if (tmp.getTiivsSolicitud().getCodSoli().equals(hist.getId().getCodSoli()))
					{
						lstSolicitudesSelected.add(hist.getId().getCodSoli());
					}
				}
			}
		}
		
		int ind=0;
		
		for (;ind<=lstSolicitudesSelected.size()-1;ind++)
		{
			if (lstSolicitudesSelected.get(ind).equals(codSoli))
			{
				bEncontrado=true;
				break;
			}
		}
		return bEncontrado;
	}
	
	public Boolean validarSolicitudEnRevision(String codSoli)
	{
		boolean bEncontrado=false;
		String codigoSolicEnRevision = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_EN_REVISION);
		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicEnRevision));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}
		
		lstSolicitudesSelected.clear();
		for (TiivsHistSolicitud tmp: lstHistorial)
		{
			if (lstHistorial!=null && lstHistorial.size()>0)
			{
				lstSolicitudesSelected.add(tmp.getId().getCodSoli());
			}
		}
		
		int ind=0;
		
		for (;ind<=lstSolicitudesSelected.size()-1;ind++)
		{
			if (lstSolicitudesSelected.get(ind).equals(codSoli))
			{
				bEncontrado=true;
				break;
			}
		}
		return bEncontrado;
	}
	
	public Boolean validarSolicitudRevocada(String codSoli)
	{
		boolean bEncontrado=false;
		String codigoSolicRevocado = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_REVOCADO);
		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicRevocado));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}
		
		lstSolicitudesSelected.clear();
		for (TiivsHistSolicitud tmp: lstHistorial)
		{
			if (lstHistorial!=null && lstHistorial.size()>0)
			{
				lstSolicitudesSelected.add(tmp.getId().getCodSoli());
			}
		}
		
		int ind=0;
		
		for (;ind<=lstSolicitudesSelected.size()-1;ind++)
		{
			if (lstSolicitudesSelected.get(ind).equals(codSoli))
			{
				bEncontrado=true;
				break;
			}
		}
		return bEncontrado;
	}
	
	public String buscarCodigoEstado(String estado) 
	{
		int i = 0;
		String codigo = "";
		for (; i < combosMB.getLstEstado().size(); i++) {
			if (combosMB.getLstEstado().get(i).getDescripcion().equalsIgnoreCase(estado)) {
				codigo = combosMB.getLstEstado().get(i).getCodEstado();
				break;
			}
		}
		return codigo;
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
	
	public String buscarNivelxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstNivel().size(); i++) {
			if (combosMB.getLstNivel().get(i).getCodNiv().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstNivel().get(i).getDesNiv();
				break;
			}
		}
		return res;
	}
	
	public String buscarNivelxAbrev(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstNivel().size(); i++) {
			if (combosMB.getLstNivel().get(i).getDesNiv().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstNivel().get(i).getCodNiv();
				break;
			}
		}
		return res;
	}
	
	public String buscarEstNivelxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstadoNivel().size(); i++) {
			if (combosMB.getLstEstadoNivel().get(i).getCodigoEstadoNivel().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstadoNivel().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}
	
	public String buscarEstudioxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstudio().size(); i++) {
			if (combosMB.getLstEstudio().get(i).getCodEstudio().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstudio().get(i).getDesEstudio();
				break;
			}
		}
		return res;
	}
	
	public String buscarOpeBanxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstOpeBancaria().size(); i++) {
			if (combosMB.getLstOpeBancaria().get(i).getCodOperBan().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstOpeBancaria().get(i).getDesOperBan();
				break;
			}
		}
		return res;
	}
	
	public String buscarTipoSolxCodigo(String codigo)
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstTipoSolicitud().size(); i++) {
			if (combosMB.getLstTipoSolicitud().get(i).getCodTipSolic().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstTipoSolicitud().get(i).getDesTipServicio();
				break;
			}
		}
		return res;
	}
	
	public String buscarTipoFechaxCodigo(String codigo)
	{
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstTiposFecha().size(); i++) {
			if (combosMB.getLstTiposFecha().get(i).getCodigoTipoFecha().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstTiposFecha().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}
	
	public void buscarOficinaPorTerritorio(ValueChangeEvent e) 
	{
		if (e.getNewValue() != null) 
		{
			logger.debug("Buscando oficina por territorio: " + e.getNewValue());
			
			GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
			filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_TERR_NO_ALIAS, e.getNewValue()));

			List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

			try {
				lstTmp = ofiDAO.buscarDinamico(filtroOfic);
				combosMB.setLstOficina(ofiDAO.buscarDinamico(filtroOfic));
				combosMB.setLstOficina1(ofiDAO.buscarDinamico(filtroOfic));

			} catch (Exception exp) {
				logger.debug("No se pudo encontrar la oficina");
			}

			if (lstTmp.size() == 1) {
				setTxtCodOficina(lstTmp.get(0).getCodOfi());
				setTxtNomOficina(lstTmp.get(0).getDesOfi());
			}
		} 
		else 
		{
			// Carga combo de Territorio
			GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);

			try {
				combosMB.setLstTerritorio(terrDAO.buscarDinamico(filtroTerr));
			} catch (Exception e1) {
				logger.debug("Error al cargar el listado de territorios");
			}

			// Cargar combos de oficina
			GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);

			try {
				combosMB.setLstOficina(ofiDAO.buscarDinamico(filtroOfic));
				combosMB.setLstOficina1(ofiDAO.buscarDinamico(filtroOfic));

			} catch (Exception exp) {
				logger.debug("No se pudo encontrar la oficina");
			}
		}
	}
	
	public void habilitarFechas(ValueChangeEvent e) {
		if (e.getNewValue() != null) {
			setNoMostrarFechas(false);
		} else {
			setNoMostrarFechas(true);
		}
	}
	
	public String devolverDesTipoDOI(String codigo)
	{
		String resultado="";
		if (codigo!= null) {
			for (TipoDocumento tmp: combosMB.getLstTipoDocumentos())
			{
				if (codigo.equalsIgnoreCase(tmp.getCodTipoDoc())) 
				{
					resultado = tmp.getDescripcion();
					break;
				}
			}
		}
		
		return resultado;
	}
	
	public String devolverDesOperBan(String codigo)
	{
		String resultado="";
		if (codigo!= null) {
			for (TiivsOperacionBancaria tmp: combosMB.getLstOpeBancaria())
			{
				if (codigo.equalsIgnoreCase(tmp.getCodOperBan())) 
				{
					resultado = tmp.getDesOperBan();
					break;
				}
			}
		}
		
		return resultado;
	}
	
	private void setearTextoTotalResultados(String cadena, int total) {
		if (total == 1) {
			setTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
					+ total + ConstantesVisado.MSG_REGISTRO);
		} else {
			setTextoTotalResultados(cadena);
		}
	}
	
	public String buscarDescripcionMoneda(String codMoneda) {
		String descripcion = "";
		for (Moneda tmpMoneda : combosMB.getLstMoneda()) {
			if (tmpMoneda.getCodMoneda().equalsIgnoreCase(codMoneda)) {
				descripcion = tmpMoneda.getDesMoneda();
				break;
			}
		}
		return descripcion;
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
	
	@SuppressWarnings("unchecked")
	public List<TiivsOficina1> completeCodOficina(String query) 
	{	
		List<TiivsOficina1> results = new ArrayList<TiivsOficina1>();
		List<TiivsOficina1> oficinas = new ArrayList<TiivsOficina1>();
		GenericDao<TiivsOficina1, Object> oficinaDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);
		try {
			oficinas = oficinaDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (TiivsOficina1 oficina : oficinas) 
		{
			if (oficina.getCodOfi() != null) 
			{
				String texto = oficina.getCodOfi();

				if (texto.contains(query.toUpperCase())) 
				{
					results.add(oficina);
				}

			}
		}

		return results;
	}
	
	public List<TiivsOficina1> completeNomOficina(String query) 
	{	
		List<TiivsOficina1> results = new ArrayList<TiivsOficina1>();

		for (TiivsOficina1 oficina : combosMB.getLstOficina1()) 
		{
			if (oficina.getCodOfi() != null) 
			{
				String texto = oficina.getDesOfi();

				if (texto.contains(query.toUpperCase())) 
				{
					results.add(oficina);
				}
			}
		}

		return results;
	}
	
	public List<TiivsPersona> completePersona(String query) 
	{
		List<TiivsPersona> lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		List<TiivsPersona> lstTiivsPersonaBusqueda = new ArrayList<TiivsPersona>();

		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);
		try {
			lstTiivsPersonaBusqueda = service.buscarDinamico(filtro);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (TiivsPersona pers : lstTiivsPersonaBusqueda) 
		{
			if(pers!= null)
			{	
				if (pers.getApePat()!=null && pers.getApeMat()!=null)
				{
					if (pers.getNombre().toUpperCase() != ""
							&& pers.getApePat().toUpperCase() != ""
							&& pers.getApeMat().toUpperCase() != "") 
					{
						
						String nombreCompletoMayuscula = pers.getNombre().toUpperCase()
								+ " " + pers.getApePat().toUpperCase() + " "
								+ pers.getApeMat().toUpperCase();

						String nombreCompletoMayusculaBusqueda = pers.getApePat()
								.toUpperCase()
								+ " "
								+ pers.getApeMat().toUpperCase()
								+ " " + pers.getNombre().toUpperCase();

						if (nombreCompletoMayusculaBusqueda.contains(query
								.toUpperCase())) {

							pers.setNombreCompletoMayuscula(nombreCompletoMayuscula);

							lstTiivsPersonaResultado.add(pers);
						}
					}
				}
				
			}
		}

		return lstTiivsPersonaResultado;
	}
	
	public void buscarOficinaPorCodigo(ValueChangeEvent e) 
	{
		logger.debug("Buscando oficina por codigo: " + e.getNewValue());
		
		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,
				e.getNewValue()));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}
		
		if (lstTmp.size() == 1) 
		{
			setTxtNomOficina(lstTmp.get(0).getDesOfi());
			setTxtNomTerritorio(buscarDesTerritorio(lstTmp.get(0).getTiivsTerritorio().getCodTer()));
		}
	}
	
	public String buscarDesTerritorio(String codigoTerritorio) 
	{
		String resultado = "";
		//logger.debug("Buscando Territorio por codigo: " + codigoTerritorio);
		// System.out.println("Buscando Territorio por codigo: " +
		// codigoTerritorio);

		GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);
		filtroTerr.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_TERRITORIO,
				codigoTerritorio));

		List<TiivsTerritorio> lstTmp = new ArrayList<TiivsTerritorio>();

		try {
			lstTmp = terrDAO.buscarDinamico(filtroTerr);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre del territorio");
		}

		if (lstTmp.size() == 1) {
			resultado = lstTmp.get(0).getDesTer();
		}
		return resultado;
	}
	
	/*Probar cuando la tabla miembro tenga el registro 0237 (con cualquiera)*/
	public TiivsTerritorio buscarTerritorioPorOficina(String codOficina)
	{   TiivsTerritorio terrTMP = new TiivsTerritorio();
		try {
			
		
		int i=0;
		int j=0;
		String codTerr="";
		String desTerr="";
		
		
		for (;i<combosMB.getLstOficina().size();i++)
		{
			if (combosMB.getLstOficina().get(i).getCodOfi().trim().equals(codOficina))
			{
				codTerr=combosMB.getLstOficina().get(i).getTiivsTerritorio().getCodTer();
				break;
			}
		}
		
		logger.info("Cod Territorio encontrado:" + codTerr);
		
		if (codTerr.length()>0)
		{
			for (;j<combosMB.getLstTerritorio().size();j++)
			{
				if (combosMB.getLstTerritorio().get(j).getCodTer().equals(codTerr))
				{
					desTerr=combosMB.getLstTerritorio().get(j).getDesTer();
					break;
				}
			}
						
			terrTMP.setCodTer(codTerr);
			terrTMP.setDesTer(desTerr);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return terrTMP;
	}
	
	public void buscarOficinaPorNombre(ValueChangeEvent e) 
	{
		logger.debug("Buscando oficina por nombre: " + e.getNewValue());
		
		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,
				e.getNewValue()));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el codigo de la oficina");
		}

		if (lstTmp.size() == 1) {
			setTxtCodOficina(lstTmp.get(0).getCodOfi());
			setTxtNomTerritorio(buscarDesTerritorio(lstTmp.get(0).getTiivsTerritorio().getCodTer()));
		}
	}
	
	public void obtenerHistorialSolicitud(){
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + selectedSolicitud.getCodSoli());
		
		String sCodSolicitud=selectedSolicitud.getCodSoli();
		
		GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
		filtroHist.add(Restrictions.eq("id.codSoli",sCodSolicitud));
		filtroHist.addOrder(Order.desc("fecha"));
		
		List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();

		try {
			lstHist = histDAO.buscarDinamico(filtroHist);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el historial de la solicitud");			
			exp.printStackTrace();
		}
				
		logger.info("Numero de registros encontrados:"+lstHist.size());
		
		if(lstHist!=null && lstHist.size()>0){
			lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
			
			for(TiivsHistSolicitud h : lstHist){
				SeguimientoDTO seg = new SeguimientoDTO();
				String estado = h.getEstado();
				if(estado!=null)
					seg.setEstado(buscarEstadoxCodigo(estado.trim()));
				
				
				String desEstadoNivel = "";
				String desRolNivel = "";
				Integer iCodNivel = 0;
				String descripcionNivel = "";

				if (h.getNivel() != null) {
					if (h.getNivelRol() != null && h.getNivelRol().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE)) {
						desRolNivel = "Responsable";
					}
					if (h.getNivelRol() != null && h.getNivelRol().trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO)) {
						desRolNivel = "Delegado";
					}
					if (h.getNivelEstado() != null && h.getNivelEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_Desaprobado_T09)) {
						desEstadoNivel = ConstantesVisado.ESTADOS.ESTADO_Desaprobado_T09;
					}
					if (h.getNivelEstado() != null && h.getNivelEstado().trim().equals(ConstantesVisado.ESTADOS.ESTADO_COD_Aprobado_T09)) {
						desEstadoNivel = ConstantesVisado.ESTADOS.ESTADO_Aprobado_T09;
					}
					iCodNivel = Integer.parseInt(h.getNivel());
					descripcionNivel = "Nivel " + iCodNivel + " " + desRolNivel + ": " + desEstadoNivel;
				}
				
				seg.setNivel(descripcionNivel);
				seg.setFecha(h.getFecha());
				seg.setUsuario(h.getNomUsuario());
				seg.setRegUsuario(h.getRegUsuario());
				seg.setObs(h.getObs());
				lstSeguimientoDTO.add(seg);				
			}
		}
		
		
	}
	
	
	
	public List<TiivsSolicitud> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(List<TiivsSolicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public List<TiivsAgrupacionPersona> getLstAgrupPer() {
		return lstAgrupPer;
	}

	public void setLstAgrupPer(List<TiivsAgrupacionPersona> lstAgrupPer) {
		this.lstAgrupPer = lstAgrupPer;
	}

	public String getTextoTotalResultados() {
		return textoTotalResultados;
	}

	public void setTextoTotalResultados(String textoTotalResultados) {
		this.textoTotalResultados = textoTotalResultados;
	}

	public Boolean getNoHabilitarExportar() {
		return noHabilitarExportar;
	}

	public void setNoHabilitarExportar(Boolean noHabilitarExportar) {
		this.noHabilitarExportar = noHabilitarExportar;
	}

	public String getCodSolicitud() {
		return codSolicitud;
	}

	public void setCodSolicitud(String codSolicitud) {
		this.codSolicitud = codSolicitud;
	}

	public List<String> getLstEstadoSelected() {
		return lstEstadoSelected;
	}

	public void setLstEstadoSelected(List<String> lstEstadoSelected) {
		this.lstEstadoSelected = lstEstadoSelected;
	}

	public String getIdImporte() {
		return idImporte;
	}

	public void setIdImporte(String idImporte) {
		this.idImporte = idImporte;
	}

	public List<String> getLstTipoSolicitudSelected() {
		return lstTipoSolicitudSelected;
	}

	public void setLstTipoSolicitudSelected(List<String> lstTipoSolicitudSelected) {
		this.lstTipoSolicitudSelected = lstTipoSolicitudSelected;
	}

	public String getIdTiposFecha() {
		return idTiposFecha;
	}

	public void setIdTiposFecha(String idTiposFecha) {
		this.idTiposFecha = idTiposFecha;
	}

	public Boolean getNoMostrarFechas() {
		return noMostrarFechas;
	}

	public void setNoMostrarFechas(Boolean noMostrarFechas) {
		this.noMostrarFechas = noMostrarFechas;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getIdOpeBan() {
		return idOpeBan;
	}

	public void setIdOpeBan(String idOpeBan) {
		this.idOpeBan = idOpeBan;
	}

	public String getTxtCodOfi() {
		return txtCodOfi;
	}

	public void setTxtCodOfi(String txtCodOfi) {
		this.txtCodOfi = txtCodOfi;
	}

	public String getTxtNomOfi() {
		return txtNomOfi;
	}

	public void setTxtNomOfi(String txtNomOfi) {
		this.txtNomOfi = txtNomOfi;
	}

	public String getTxtNomOficina() {
		return txtNomOficina;
	}

	public void setTxtNomOficina(String txtNomOficina) {
		this.txtNomOficina = txtNomOficina;
	}

	public String getTxtNomTerritorio() {
		return txtNomTerritorio;
	}

	public void setTxtNomTerritorio(String txtNomTerritorio) {
		this.txtNomTerritorio = txtNomTerritorio;
	}

	public String getIdCodOfi() {
		return idCodOfi;
	}

	public void setIdCodOfi(String idCodOfi) {
		this.idCodOfi = idCodOfi;
	}

	public String getTxtCodOficina() {
		return txtCodOficina;
	}

	public void setTxtCodOficina(String txtCodOficina) {
		this.txtCodOficina = txtCodOficina;
	}

	public String getIdCodOfi1() {
		return idCodOfi1;
	}

	public void setIdCodOfi1(String idCodOfi1) {
		this.idCodOfi1 = idCodOfi1;
	}

	public String getNroDOIApoderado() {
		return nroDOIApoderado;
	}

	public void setNroDOIApoderado(String nroDOIApoderado) {
		this.nroDOIApoderado = nroDOIApoderado;
	}

	public String getTxtNomApoderado() {
		return txtNomApoderado;
	}

	public void setTxtNomApoderado(String txtNomApoderado) {
		this.txtNomApoderado = txtNomApoderado;
	}

	public String getNroDOIPoderdante() {
		return nroDOIPoderdante;
	}

	public void setNroDOIPoderdante(String nroDOIPoderdante) {
		this.nroDOIPoderdante = nroDOIPoderdante;
	}

	public String getTxtNomPoderdante() {
		return txtNomPoderdante;
	}

	public void setTxtNomPoderdante(String txtNomPoderdante) {
		this.txtNomPoderdante = txtNomPoderdante;
	}

	public List<String> getLstNivelSelected() {
		return lstNivelSelected;
	}

	public void setLstNivelSelected(List<String> lstNivelSelected) {
		this.lstNivelSelected = lstNivelSelected;
	}

	public List<String> getLstEstadoNivelSelected() {
		return lstEstadoNivelSelected;
	}

	public void setLstEstadoNivelSelected(List<String> lstEstadoNivelSelected) {
		this.lstEstadoNivelSelected = lstEstadoNivelSelected;
	}

	public List<String> getLstEstudioSelected() {
		return lstEstudioSelected;
	}

	public void setLstEstudioSelected(List<String> lstEstudioSelected) {
		this.lstEstudioSelected = lstEstudioSelected;
	}

	public String getIdTerr() {
		return idTerr;
	}

	public void setIdTerr(String idTerr) {
		this.idTerr = idTerr;
	}

	public List<String> getLstSolicitudesSelected() {
		return lstSolicitudesSelected;
	}

	public void setLstSolicitudesSelected(List<String> lstSolicitudesSelected) {
		this.lstSolicitudesSelected = lstSolicitudesSelected;
	}

	public Boolean getMostrarEstudio() {
		return mostrarEstudio;
	}

	public void setMostrarEstudio(Boolean mostrarEstudio) {
		this.mostrarEstudio = mostrarEstudio;
	}

	public List<String> getLstSolicitudesxOpeBan() {
		return lstSolicitudesxOpeBan;
	}

	public void setLstSolicitudesxOpeBan(List<String> lstSolicitudesxOpeBan) {
		this.lstSolicitudesxOpeBan = lstSolicitudesxOpeBan;
	}

	public TiivsOficina1 getOficina() {
		return oficina;
	}

	public void setOficina(TiivsOficina1 oficina) {
		this.oficina = oficina;
	}

	public Boolean getMostrarColumna() {
		return mostrarColumna;
	}

	public void setMostrarColumna(Boolean mostrarColumna) {
		this.mostrarColumna = mostrarColumna;
	}

	public String getNombreArchivoExcel() {
		return nombreArchivoExcel;
	}

	public void setNombreArchivoExcel(String nombreArchivoExcel) {
		this.nombreArchivoExcel = nombreArchivoExcel;
	}

	public Boolean getbRevision() {
		return bRevision;
	}

	public void setbRevision(Boolean bRevision) {
		this.bRevision = bRevision;
	}

	public Boolean getbDelegados() {
		return bDelegados;
	}

	public void setbDelegados(Boolean bDelegados) {
		this.bDelegados = bDelegados;
	}

	public Boolean getbRevocatoria() {
		return bRevocatoria;
	}

	public void setbRevocatoria(Boolean bRevocatoria) {
		this.bRevocatoria = bRevocatoria;
	}

	public List<SeguimientoDTO> getLstSeguimientoDTO() {
		return lstSeguimientoDTO;
	}

	public void setLstSeguimientoDTO(List<SeguimientoDTO> lstSeguimientoDTO) {
		this.lstSeguimientoDTO = lstSeguimientoDTO;
	}

	public TiivsSolicitud getSelectedSolicitud() {
		return selectedSolicitud;
	}

	public void setSelectedSolicitud(TiivsSolicitud selectedSolicitud) {
		this.selectedSolicitud = selectedSolicitud;
	}
	

	public List<TiivsHistSolicitud> getLstHistorial() {
		return lstHistorial;
	}

	public void setLstHistorial(List<TiivsHistSolicitud> lstHistorial) {
		this.lstHistorial = lstHistorial;
	}

	public PDFViewerMB getPdfViewerMB() {
		return pdfViewerMB;
	}

	public void setPdfViewerMB(PDFViewerMB pdfViewerMB) {
		this.pdfViewerMB = pdfViewerMB;
	}

	public String rutaArchivoExcel() {
		return rutaArchivoExcel;
	}

	public void setRutaArchivoExcel(String rutaArchivoExcel) {
		this.rutaArchivoExcel = rutaArchivoExcel;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public IILDPeUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(IILDPeUsuario usuario) {
		this.usuario = usuario;
	}

	public String getPERFIL_USUARIO() {
		return PERFIL_USUARIO;
	}

	public void setPERFIL_USUARIO(String pERFIL_USUARIO) {
		PERFIL_USUARIO = pERFIL_USUARIO;
	}

	public boolean isBloquearOficina() {
		return bloquearOficina;
	}

	public void setBloquearOficina(boolean bloquearOficina) {
		this.bloquearOficina = bloquearOficina;
	}

	public List<AgrupacionSimpleDto> getLstAgrupacionSimpleDto() {
		return lstAgrupacionSimpleDto;
	}

	public void setLstAgrupacionSimpleDto(
			List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;
	}

	public NivelService getNivelService() {
		return nivelService;
	}

	public void setNivelService(NivelService nivelService) {
		this.nivelService = nivelService;
	}

	public TiivsPersona getObjTiivsPersonaBusquedaNomApod() {
		return objTiivsPersonaBusquedaNomApod;
	}

	public void setObjTiivsPersonaBusquedaNomApod(
			TiivsPersona objTiivsPersonaBusquedaNomApod) {
		this.objTiivsPersonaBusquedaNomApod = objTiivsPersonaBusquedaNomApod;
	}

	public TiivsPersona getObjTiivsPersonaBusquedaNomPoder() {
		return objTiivsPersonaBusquedaNomPoder;
	}

	public void setObjTiivsPersonaBusquedaNomPoder(
			TiivsPersona objTiivsPersonaBusquedaNomPoder) {
		this.objTiivsPersonaBusquedaNomPoder = objTiivsPersonaBusquedaNomPoder;
	}
}

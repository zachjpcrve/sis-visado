package com.hildebrando.visado.mb;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
import com.hildebrando.visado.modelo.RecaudacionTipoServ;
import com.hildebrando.visado.modelo.SolicitudesOficina;
import com.hildebrando.visado.modelo.SolicitudesTipoServicio;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsTerritorio;

@ManagedBean(name = "reportesMB")
@SessionScoped
public class ReportesMB 
{
	@ManagedProperty(value = "#{pdfViewerMB}")
	private PDFViewerMB pdfViewerMB;
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	
	private List<TiivsSolicitud> solicitudes;
	private List<TiivsAgrupacionPersona> lstAgrupPer;
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	private List<TiivsHistSolicitud> lstHistorial;
	private List<SeguimientoDTO> lstSeguimientoDTO;
	private List<String> lstEstudioSelected;
	private List<String> lstEstadoNivelSelected;
	private List<String> lstTipoSolicitudSelected;
	private List<String> lstEstadoSelected;
	private List<String> lstNivelSelected;
	private List<String> lstSolicitudesSelected;
	private List<String> lstSolicitudesxOpeBan;
	private List<SolicitudesOficina> lstSolicitudesOficina;
	private List<SolicitudesTipoServicio> lstSolicitudesTipoServicio;
	private List<RecaudacionTipoServ> lstRecaudacionTipoServ;
	private List<Moneda> lstMoneda;
	private Date fechaInicio;
	private Date fechaFin;
	private String nombreExtractor;
	private String nombreEstadoSolicitud;
	private String nombreTipoServicio;
	private String nombreRecaudacion;
	private String rutaArchivoExcel;
	private String PERFIL_USUARIO ;
	private String idTerr;
	private String idOfi;
	private String idOfi1;
	private String textoTotalResultados;
	private String txtMsgDialog;
	private String codSolicitud;
	private String idOpeBan;
	private String idImporte;
	private String idMoneda;
	private Double importeIni;
	private Double importeFin;
	private StreamedContent file;  
	private IILDPeUsuario usuario;
	private Boolean noHabilitarExportar;
	private Boolean mostrarBotones=false;
		
	public static Logger logger = Logger.getLogger(ReportesMB.class);
	
	public ReportesMB()
	{
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");	
		PERFIL_USUARIO=(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		lstSolicitudesOficina = new ArrayList<SolicitudesOficina>();
		lstRecaudacionTipoServ = new ArrayList<RecaudacionTipoServ>();
		
		inicializarCampos();
			
		generarNombreArchivoExtractor();
		generarNombreArchivoEstadoSolicitud();
		generarNombreArchivoTipoServicio();
		generarNombreRecaudacion();
		
		setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + lstSolicitudesOficina.size() + ConstantesVisado.MSG_REGISTROS,lstSolicitudesOficina.size());
		if (lstSolicitudesOficina.size()>0)
		{
			setNoHabilitarExportar(false);
		}
		else
		{
			setNoHabilitarExportar(true);
		}
	}
	
	public void inicializarCampos()
	{
		setIdOfi1("");
		setIdOfi1("");
		setIdTerr("");
		setFechaInicio(null);
		setFechaFin(null);
		setCodSolicitud("");
		setIdOpeBan("");
		setIdImporte("");
		setIdMoneda("");
		setImporteIni(0.0);
		setImporteFin(0.0);	
	}
	
	private void setearTextoTotalResultados(String cadena, int total) {
		if (total == 1) {
			setTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
					+ total + ConstantesVisado.MSG_REGISTRO);
		} else {
			setTextoTotalResultados(cadena);
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
	
	public void exportarExcelExtractor()
	{
		rptExtractor();
	}
	
	public void exportarExcelEstadoSolicitud()
	{
		rptEstadoSolicitud();
	}
	
	public void exportarExcelRecaudacion()
	{
		rptRecaudacion();
	}
	
	public void exportarExcelSolicitudTipoServ()
	{
		rptSolicitudTipoServ();
	}
	
	public void buscarRecaudacionxTipoServ()
	{
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		Date fechaIni=null;
		Date fechaFin=null;
		
		if (getFechaInicio()!=null && getFechaFin()!=null)
		{
			fechaIni=getFechaInicio();
			fechaFin=getFechaFin();
		}
		
		TiivsSolicitud tmpSolicitud = new TiivsSolicitud();
		TiivsTerritorio tmpTerr = new TiivsTerritorio();
		TiivsOficina1 tmpOficina = new TiivsOficina1();
		
		if (getIdTerr()!=null && getIdTerr().compareTo("")!=0)
		{
			tmpTerr.setCodTer(getIdTerr());
		}
		
		if (getIdOfi()!=null && getIdOfi().compareTo("")!=0)
		{
			tmpOficina.setCodOfi(getIdOfi());
		}
		
		if (getIdOfi1()!=null && getIdOfi1().compareTo("")!=0)
		{
			tmpOficina.setCodOfi(getIdOfi1());
		}
		
		tmpOficina.setTiivsTerritorio(tmpTerr);
		tmpSolicitud.setTiivsOficina1(tmpOficina);
		
		try {
			this.lstRecaudacionTipoServ = solicitudService.obtenerListarRecaudacionxTipoServicio(tmpSolicitud, fechaIni, fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int iNuevoTotal =lstRecaudacionTipoServ.size()-1;
		
		setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + iNuevoTotal + ConstantesVisado.MSG_REGISTROS,iNuevoTotal);
		if (lstRecaudacionTipoServ.size()>1)
		{
			setNoHabilitarExportar(false);
		}
		else
		{
			setNoHabilitarExportar(true);
		}
	}
	
	public void buscarOficinaPorTerritorio() 
	{
		if (getIdTerr() != null) 
		{
			logger.debug("Buscando oficina por territorio: " + getIdTerr());
			
			GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
			filtroOfic.createAlias(ConstantesVisado.NOM_TBL_TERRITORIO,	ConstantesVisado.ALIAS_TBL_TERRITORIO);
			filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_TERR_ALIAS, getIdTerr()));

			List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

			try {
				lstTmp = ofiDAO.buscarDinamico(filtroOfic);
				combosMB.setLstOficina(ofiDAO.buscarDinamico(filtroOfic));
				combosMB.setLstOficina1(ofiDAO.buscarDinamico(filtroOfic));

			} catch (Exception exp) {
				logger.debug("No se pudo encontrar la oficina");
			}
			
			if (lstTmp.size()>0)
			{
				setIdOfi(lstTmp.get(0).getCodOfi());
				setIdOfi1(lstTmp.get(0).getCodOfi());
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
	
	public void buscarOficinaPorCodigo() 
	{
		logger.debug("Buscando oficina por codigo: " + getIdOfi());
		
		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,getIdOfi()));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}
		
		if (lstTmp.size() > 0) 
		{
			setIdOfi1(lstTmp.get(0).getCodOfi());
			setIdTerr(lstTmp.get(0).getTiivsTerritorio().getCodTer());
		}
	}
	
	public String buscarNomOficinaPorCodigo(String codigo) 
	{
		String resultado="";
		logger.debug("Buscando oficina por codigo: " + codigo);
		
		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,codigo));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}
		
		if (lstTmp.size() ==1) 
		{
			resultado=lstTmp.get(0).getDesOfi();
		}
		
		return resultado;
	}
	
	public String buscarNomTerrPorCodigo(String codigo) 
	{
		String resultado="";
		logger.debug("Buscando oficina por codigo: " + codigo);
		
		GenericDao<TiivsTerritorio, Object> tDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroT = Busqueda.forClass(TiivsTerritorio.class);
		filtroT.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_TERRITORIO,codigo));

		List<TiivsTerritorio> lstTmp = new ArrayList<TiivsTerritorio>();

		try {
			lstTmp = tDAO.buscarDinamico(filtroT);
		} catch (Exception exp) {
			logger.error("No se pudo encontrar el nombre del territorio",exp);
		}
		
		if (lstTmp.size() ==1) 
		{
			resultado=lstTmp.get(0).getDesTer();
		}
		
		return resultado;
	}
	
	public void buscarOficinaPorNombre() 
	{
		logger.debug("Buscando oficina por nombre: " + getIdOfi1());
		
		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,getIdOfi1()));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el codigo de la oficina");
		}

		if (lstTmp.size() == 1) {
			setIdOfi(lstTmp.get(0).getCodOfi());
			setIdTerr(lstTmp.get(0).getTiivsTerritorio().getCodTer());
		}
	}
	
	public void buscarSolicitudesxOficina()
	{
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		Date fechaIni=null;
		Date fechaFin=null;
		
		if (getFechaInicio()!=null && getFechaFin()!=null)
		{
			fechaIni=getFechaInicio();
			fechaFin=getFechaFin();
		}
		
		TiivsSolicitud tmpSolicitud = new TiivsSolicitud();
		TiivsTerritorio tmpTerr = new TiivsTerritorio();
		TiivsOficina1 tmpOficina = new TiivsOficina1();
		
		if (getIdTerr()!=null && getIdTerr().compareTo("")!=0)
		{
			tmpTerr.setCodTer(getIdTerr());
		}
		
		if (getIdOfi()!=null && getIdOfi().compareTo("")!=0)
		{
			tmpOficina.setCodOfi(getIdOfi());
		}
		
		if (getIdOfi1()!=null && getIdOfi1().compareTo("")!=0)
		{
			tmpOficina.setCodOfi(getIdOfi1());
		}
		
		tmpOficina.setTiivsTerritorio(tmpTerr);
		tmpSolicitud.setTiivsOficina1(tmpOficina);
		
		try {
			this.lstSolicitudesOficina = solicitudService.obtenerListarTotalSolicitudesxEstado(tmpSolicitud, fechaIni, fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int iNuevoTotal =lstSolicitudesOficina.size()-1;
		
		setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + iNuevoTotal + ConstantesVisado.MSG_REGISTROS,iNuevoTotal);
		if (lstSolicitudesOficina.size()>1)
		{
			setNoHabilitarExportar(false);
		}
		else
		{
			setNoHabilitarExportar(true);
		}
	}
	
	public void buscarSolicitudesxTipoServicio()
	{
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		Date fechaIni=null;
		Date fechaFin=null;
				
		//Busqueda de solicitudes por rango de fechas
		if (getFechaInicio()!=null && getFechaFin()!=null)
		{
			fechaIni=getFechaInicio();
			fechaFin=getFechaFin();
		}
		
		TiivsSolicitud tmpSolicitud = new TiivsSolicitud();
				
		//Busqueda de solicitudes por codigo
		if (getCodSolicitud()!=null)
		{
			tmpSolicitud.setCodSoli(getCodSolicitud());
		}
		
		//Busqueda de solicitudes por tipo de servicio
		String cadTipoServ = "";
		if (lstTipoSolicitudSelected.size()>0)
		{
			int j=0;
			int cont=1;
			
			for (;j<=lstTipoSolicitudSelected.size()-1;j++)
			{
				if (lstTipoSolicitudSelected.size()>1)
				{
					if (cont==lstTipoSolicitudSelected.size())
					{
						cadTipoServ=cadTipoServ.concat(lstTipoSolicitudSelected.get(j).toString());
					}
					else
					{
						cadTipoServ=cadTipoServ.concat(lstTipoSolicitudSelected.get(j).toString().concat(","));
						cont++;
					}
				}
				else
				{
					cadTipoServ = lstTipoSolicitudSelected.get(j).toString();
				}	
			}
		}	
		
		//Busqueda por rangos de importe (Rango Global)
		String rangoImpG="";
		
		if (getIdImporte().compareTo("") != 0) 
		{
			logger.info("Filtro por importe: " + getIdImporte());

			rangoImpG=getIdImporte();
		}
		

		//Busqueda por rangos de importe (Importe Minimo y Maximo)
		Double rangoIni=0.0;
		Double rangoFin=0.0;
		
		if (getImporteIni()!=0 && getImporteFin()!=0)
		{
			logger.info("Rango de Inicio: " + rangoIni);
			logger.info("Rango de Fin: " + rangoFin);

			rangoIni = getImporteIni();
			rangoFin = getImporteFin();
		}
		
		//Busqueda por estudio
		String cadEstudio = "";
		if (lstEstudioSelected.size()>0)
		{
			int j=0;
			int cont=1;
			
			for (;j<=lstEstudioSelected.size()-1;j++)
			{
				if (lstEstudioSelected.size()>1)
				{
					if (cont==lstEstudioSelected.size())
					{
						cadEstudio=cadTipoServ.concat(lstEstudioSelected.get(j).toString());
					}
					else
					{
						cadEstudio=cadTipoServ.concat(lstEstudioSelected.get(j).toString().concat(","));
						cont++;
					}
				}
				else
				{
					cadEstudio = lstEstudioSelected.get(j).toString();
				}	
			}
		}	
		
		if (getIdMoneda()!=null)
		{
			tmpSolicitud.setMoneda(getIdMoneda());
		}
				
		try {
			this.lstSolicitudesTipoServicio = solicitudService.obtenerSolicitudesxTipoServicio(tmpSolicitud,
							Utilitarios.validarCampoNull(getIdOpeBan()),cadTipoServ,cadEstudio,rangoImpG,rangoIni ,rangoFin,fechaIni, fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int iNuevoTotal =lstSolicitudesTipoServicio.size();
		
		setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + iNuevoTotal + ConstantesVisado.MSG_REGISTROS,iNuevoTotal);
		if (lstSolicitudesTipoServicio.size()>0)
		{
			setNoHabilitarExportar(false);
		}
		else
		{
			setNoHabilitarExportar(true);
		}
	}
	
	// Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a
	// los filtros seleccionados.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	@SuppressWarnings("unchecked")
	public void buscarSolicitudesExtractor() 
	{
		logger.info("Buscando solicitudes a exportar");
		
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		
		//Busqueda por fecha de Registro
		if (getFechaInicio()!=null && getFechaFin()!=null)
		{
			LocalDate newFechaInicio = null;
			LocalDate newFechaFin = null;
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy");
			DateFormat formato = new SimpleDateFormat("dd/MM/yy");
			
			String tmpFecIni = formato.format(getFechaInicio());
			String tmpFecFin = formato.format(getFechaFin());
			
			newFechaInicio=fmt.parseLocalDate(tmpFecIni);
			newFechaFin= fmt.parseLocalDate(tmpFecFin);
			
			Date dFechaIni = newFechaInicio.toDateTimeAtStartOfDay().toDate();
			Date dFechaFin = newFechaFin.toDateTimeAtStartOfDay().toDate();
			
			filtroSol.add(Restrictions.between(ConstantesVisado.CAMPO_FECHA_REGISTRO, dFechaIni,dFechaFin));
		}
				
		//Busqueda por estado de la solicitud
		if (lstEstadoSelected.size() > 0) 
		{
			int ind = 0;

			for (; ind <= lstEstadoSelected.size() - 1; ind++) 
			{
				logger.info("Filtro por estados: " + lstEstadoSelected.get(ind));
			}
			
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,lstEstadoSelected));
		}
		
		filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
		
		// Buscar solicitudes de acuerdo a criterios seleccionados
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes: " + ex.getStackTrace());
		}
		
		if (solicitudes.size()>0)
		{
			actualizarDatosDeBusqueda();
			setTxtMsgDialog("Que desea hacer?");
			mostrarBotones=true;
		}
		else
		{
			//FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_INFO,
				//	"Sin registros para exportar","No se encontraron registros que coinciden con los criterios de b�squeda ingresados"));
			setTxtMsgDialog("No se encontraron registros que coinciden con los criterios de b�squeda ingresados");
			mostrarBotones=false;
		}

	}
	
	public void obtenerHistorialSolicitud(String codSoli) 
	{
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + codSoli);

		try {
			lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroHist = Busqueda.forClass(TiivsHistSolicitud.class);
			filtroHist.add(Restrictions.eq("id.codSoli", codSoli));
			filtroHist.addOrder(Order.desc("fecha"));

			List<TiivsHistSolicitud> lstHist = new ArrayList<TiivsHistSolicitud>();
			lstHist = histDAO.buscarDinamico(filtroHist);

			logger.info("Numero de registros encontrados:" + lstHist.size());

			if (lstHist != null && lstHist.size() > 0) {
				
				for (TiivsHistSolicitud h : lstHist) {
					SeguimientoDTO seg = new SeguimientoDTO();

					String estado = h.getEstado();
					if (estado != null) {
						seg.setEstado(buscarEstadoxCodigo(estado.trim()));
					}

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
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el historial de la solicitud");
			exp.printStackTrace();
		}

	}
	
	private void actualizarDatosDeBusqueda() 
	{	
		String cadena="";
		
		// Se obtiene y setea la descripcion del Estado en la grilla
		for (TiivsSolicitud tmpSol : solicitudes) 
		{
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
				    	logger.info("d.getTiivsPersona() "+d.getTiivsPersona().getTipDoi());
				    	
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
						  ConstantesVisado.GUION + tmpPoder.getApePat() + " " + tmpPoder.getApeMat() + " " + 
						  tmpPoder.getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
		    }
		    
		    tmpSol.setTxtPoderdante(cadena);
			
			// Cargar data de apoderados
			cadena="";
			
			for (TiivsPersona tmpApor: lstApoderdantes)
		    {
		    	cadena += devolverDesTipoDOI(tmpApor.getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmpApor.getNumDoi() +
						  ConstantesVisado.GUION + tmpApor.getApePat() + " " + tmpApor.getApeMat() + " " + 
						  tmpApor.getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
		    }
			
			tmpSol.setTxtApoderado(cadena);		
			
			//logger.info("Tamanio total agrupaciones de Solicitud: " + tmpSol.getCodSoli() + ": " + lstAgrupacionSimpleDto.size());
		}
		
	}
	
	public void generarNombreArchivoExtractor() 
	{
		setNombreExtractor("Extractor_"	+ Utilitarios.obtenerFechaArchivoExcel() + ConstantesVisado.UNDERLINE + Utilitarios.obtenerHoraArchivoExcel());
	}
	
	public void generarNombreArchivoEstadoSolicitud() 
	{
		setNombreEstadoSolicitud("Estados_"	+ Utilitarios.obtenerFechaArchivoExcel() + ConstantesVisado.UNDERLINE + Utilitarios.obtenerHoraArchivoExcel());
	}
	
	public void generarNombreArchivoTipoServicio() 
	{
		setNombreTipoServicio("TipoServicio_"	+ Utilitarios.obtenerFechaArchivoExcel() + ConstantesVisado.UNDERLINE + Utilitarios.obtenerHoraArchivoExcel());
	}
	
	public void generarNombreRecaudacion() 
	{
		setNombreRecaudacion("Recaudacion_"	+ Utilitarios.obtenerFechaArchivoExcel() + ConstantesVisado.UNDERLINE + Utilitarios.obtenerHoraArchivoExcel());
	}
	
	/*public void postProcessXLS(Object document) 
	{
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		CellStyle style = wb.createCellStyle();
		style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
		
		for (Row row : sheet) 
		{
			for (Cell cell : row) 
			{
				cell.setCellValue(cell.getStringCellValue().toUpperCase());
				cell.setCellStyle(style);
			}
		}
	}*/
	
	private void rptSolicitudTipoServ() 
	{
		try{
		// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 2, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, ConstantesVisado.TITULO_REPORTE_RPT_TIPO_SERV,12);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 4));
						
			//Se crea la leyenda de quien genero el archivo y la hora respectiva
			//Row rowG = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, trow, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, trow, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerGenerador(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			Row rowG1 = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG1, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.obtenerFechaHoraActual(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			//Genera celdas con los filtros de busqueda
			
			//Cod Solicitud
			Row row2 = sheet.createRow((short) 4);
			Utilitarios.crearCell(wb, row2, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_COD_SOL, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			if (getCodSolicitud()!=null)
			{
				Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getCodSolicitud(), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			//Tipo de Servicio
			Utilitarios.crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_TIPO_SERV, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
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
							cadena=cadena.concat(buscarTipoSolxCodigo((lstTipoSolicitudSelected.get(j).toString().concat(","))));
							cont++;
						}
					}
					else
					{
						cadena = buscarTipoSolxCodigo(lstTipoSolicitudSelected.get(j).toString());
					}		
				}
				
				Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			//Tipo de Operacion
			Utilitarios.crearCell(wb, row2, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_TIPO_OP, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getIdOpeBan().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarOpeBanxCodigo(getIdOpeBan()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			//Rango Global
			Row row3 = sheet.createRow((short) 6);
			Utilitarios.crearCell(wb, row3, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_RANGO_GLO, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) 
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_1, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,  ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_2, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,  ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_3, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,  ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_4, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			//Importe Operacion Inicio
			Utilitarios.crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_IMP_OP_INI, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(getImporteIni()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
					
			//Importe Operacion Fin
			Utilitarios.crearCell(wb, row3, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_IMP_OP_FIN, false, false,false,HSSFColor.GREY_25_PERCENT.index);
			Utilitarios.crearCell(wb, row3, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(getImporteFin()), true, false,true,HSSFColor.GREY_25_PERCENT.index);
			
			//Estudio
			Row row4 = sheet.createRow((short) 8);
			Utilitarios.crearCell(wb, row4, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_ESTUDIO,false, false,false,HSSFColor.GREY_25_PERCENT.index);
			
			if (lstEstudioSelected!=null)
			{
				String cadena = "";
				/*int ind=0;
				
				for (; ind <= getLstEstudioSelected().size() - 1; ind++) 
				{
					cadena+= buscarEstudioxCodigo(getLstEstudioSelected().get(ind))+",";
				}*/
				
				int j=0;
				int cont=1;
				
				for (;j<=lstEstudioSelected.size()-1;j++)
				{
					if (lstEstudioSelected.size()>1)
					{
						if (cont==lstEstudioSelected.size())
						{
							cadena=cadena.concat(buscarEstudioxCodigo((lstEstudioSelected.get(j).toString())));
						}
						else
						{
							cadena=cadena.concat(buscarEstudioxCodigo((lstEstudioSelected.get(j).toString().concat(","))));
							cont++;
						}
					}
					else
					{
						cadena = buscarEstudioxCodigo(lstEstudioSelected.get(j).toString());
					}		
				}
				
				Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.GREY_25_PERCENT.index);
			}
			
			
			Utilitarios.crearCell(wb, row4, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_FECHA_INICIO, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaInicio()!=null)
			{
				Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaInicio()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Utilitarios.crearCell(wb, row4, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_FILTRO_FECHA_FIN, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaFin()!=null)
			{
				Utilitarios.crearCell(wb, row4, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaFin()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row rowTot = sheet.createRow((short) 10);
			Utilitarios.crearCell(wb, rowTot, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Total de Registros: " + (lstSolicitudesTipoServicio.size()) , false, false,false,HSSFColor.DARK_BLUE.index);
			
		
			if (lstSolicitudesTipoServicio.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un dise�o a la celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_COD_SOL, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_ESTUDIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_TIPO_SERVICIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_TIPO_OPERACION, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_MONEDA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_IMPORTE, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_TIPO_CAMBIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_TIPO_SERV_COLUMNA_IMPORTE_SOLES, true, true,false,HSSFColor.DARK_BLUE.index);
								
				int numReg=13;
				
				for (SolicitudesTipoServicio tmp: lstSolicitudesTipoServicio)
				{
					Row row = sheet.createRow((short) numReg);
					
					//Columna Cod Solicitud en Excel
					Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCodSolicitud(),true, false,true,HSSFColor.DARK_BLUE.index);
										
					//Columna Estudio en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getEstudio(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Tipo de Servicio en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getTipoServicio(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Tipo de Operacion en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTipoOperacion()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Moneda en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getMoneda()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Importe en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getImporte(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Tipo de Cambio en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getTipoCambio(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Importe en Soles en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getImporteSoles(),true, false,true,HSSFColor.DARK_BLUE.index);
						
					numReg++;
				}
			}
			
			//Arregla ancho de columnas
			int pos=0;
			for (;pos<=7;pos++)
			{
				sheet.autoSizeColumn(pos);
			}
						
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta="";
			if (obtenerRutaExcel().compareTo("")!=0)
			{
				
				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreTipoServicio());
				
				strRuta = obtenerRutaExcel() + getNombreTipoServicio() + ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				logger.debug("Ruta final donde encontrar el archivo excel: " + strRuta);
				
				setRutaArchivoExcel(strRuta);
			}
						
		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt SolicitudTipoServ",e);
			//logger.info("Error al generar el archivo excel debido a: " + e.getStackTrace());
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
	
	private void rptRecaudacion() 
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
			Utilitarios.crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, ConstantesVisado.TITULO_REPORTE_RPT_RECAUDACION,12);
						
			//Se crea la leyenda de quien genero el archivo y la hora respectiva
			Row rowG = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerGenerador(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			Row rowG1 = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG1, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.obtenerFechaHoraActual(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			//Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 4);
			
			Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, false, false,false,HSSFColor.DARK_BLUE.index);
			
			if (getIdTerr()!=null)
			{
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarNomTerrPorCodigo(getIdTerr()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row row3 = sheet.createRow((short) 6);
			
			Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_COD_OFICINA, false, false,false,HSSFColor.DARK_BLUE.index);
			
			if (getIdOfi().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getIdOfi(), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_OFICINA, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getIdOfi1().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarNomOficinaPorCodigo(getIdOfi1()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row row4 = sheet.createRow((short) 8);
			
			Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaInicio()!=null)
			{
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaInicio()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaFin()!=null)
			{
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaFin()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row rowTot = sheet.createRow((short) 10);
			Utilitarios.crearCell(wb, rowTot, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Total de Registros: " + (lstRecaudacionTipoServ.size()-1) , false, false,false,HSSFColor.DARK_BLUE.index);
		
			if (lstRecaudacionTipoServ.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un dise�o a la celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_TERRITORIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_CODIGO_OFICINA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_OFICINA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_NATURAL, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_PN, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_JURIDICA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_PJ, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_FALLECIDA_MAYOR_X, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 8, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_FALLECIDA_X, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 9, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_FALLECIDA_MENOR_X, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 10, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_FALLECIDA_X1, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 11, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_RECAUDACION_TOTAL, true, true,false,HSSFColor.DARK_BLUE.index);
				
				int numReg=13;
				
				for (RecaudacionTipoServ tmp: lstRecaudacionTipoServ)
				{
					Row row = sheet.createRow((short) numReg);
					
					//Columna Territorio en Excel
					Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTerritorio(),true, false,true,HSSFColor.DARK_BLUE.index);
										
					//Columna Cod Oficina en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCodOficina(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Oficina en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getOficina(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Persona Natural en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getiContPersonasNaturales()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getsTotalPersonasNat()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Persona Juridica en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getiContPersonasJuridicas()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getsTotalPersonasJurd()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Persona Fallecida > X en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getiContPersonasFallecX()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getsTotalPersonasFallecX()),true, false,true,HSSFColor.DARK_BLUE.index);

					//Columna Persona Fallecida < X en Excel
					Utilitarios.crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getiContPersonasFallecX1()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getsTotalPersonasFallecX1()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getsRecaudacionTotal()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					numReg++;
				}
			}
			
			//Arregla ancho de columnas
			int pos=0;
			for (;pos<=18;pos++)
			{
				sheet.autoSizeColumn(pos);
			}
						
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta="";
			if (obtenerRutaExcel().compareTo("")!=0)
			{
				
				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreRecaudacion());
				
				strRuta = obtenerRutaExcel() + getNombreRecaudacion() + ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				logger.debug("Ruta final donde encontrar el archivo excel: " + strRuta);
				
				setRutaArchivoExcel(strRuta);
			}
						
		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Recaudacion",e);
			//logger.info("Error al generar el archivo excel debido a: " + e.getStackTrace());
		}	
	}
	
	private void rptEstadoSolicitud() 
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
			Utilitarios.crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, ConstantesVisado.TITULO_REPORTE_RPT_SOLICITUD,14);
						
			//Se crea la leyenda de quien genero el archivo y la hora respectiva
			Row rowG = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerGenerador(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			Row rowG1 = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG1, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.obtenerFechaHoraActual(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			//Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 4);
			
			Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, false, false,false,HSSFColor.DARK_BLUE.index);
			
			if (getIdTerr()!=null)
			{
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarNomTerrPorCodigo(getIdTerr()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row row3 = sheet.createRow((short) 6);
			
			Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_COD_OFICINA, false, false,false,HSSFColor.DARK_BLUE.index);
			
			if (getIdOfi().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getIdOfi(), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_OFICINA, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getIdOfi1().compareTo("")!=0)
			{
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarNomOficinaPorCodigo(getIdOfi1()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row row4 = sheet.createRow((short) 8);
			
			Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaInicio()!=null)
			{
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaInicio()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaFin()!=null)
			{
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaFin()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row rowTot = sheet.createRow((short) 10);
			Utilitarios.crearCell(wb, rowTot, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Total de Registros: " + (lstSolicitudesOficina.size()-1) , false, false,false,HSSFColor.DARK_BLUE.index);
			
		
			if (lstSolicitudesOficina.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un dise�o a la celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_COD_OFICINA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_OFICINA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_REGISTRADO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_ENVIADOSSJJ_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_RESERVADO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_EN_VERIFICACION_A_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 8, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 9, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_EN_REVISION_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 10, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_PRE_EJECUTADO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 11, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_EJECUTADO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 12, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 13, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_EN_VERIFICACION_B_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 14, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 15, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_VENCIDO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 16, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ESTADOS.ESTADO_REVOCADO_T02, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 17, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_SOLICITUD_COLUMNA_TOTAL, true, true,false,HSSFColor.DARK_BLUE.index);
				
				int numReg=13;
				
				for (SolicitudesOficina tmp: lstSolicitudesOficina)
				{
					Row row = sheet.createRow((short) numReg);
					
					//Columna Territorio en Excel
					Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTerritorio(),true, false,true,HSSFColor.DARK_BLUE.index);
										
					//Columna Cod Oficina en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCodOficina(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Oficina en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,tmp.getOficina(),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Registrado en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalRegistrados()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Enviado en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalEnviados()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Reservado en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalReservado()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Aceptado en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalAceptado()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna En Verificacion A en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalVerificacionA()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Rechazado en Excel
					Utilitarios.crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalRechazado()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna En Revision en Excel
					Utilitarios.crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalEnRevision()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Pre-Ejecutado en Excel
					Utilitarios.crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalPreEjecutado()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Ejecutado en Excel
					Utilitarios.crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalEjecutado()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Procedente en Excel
					Utilitarios.crearCell(wb, row, 12, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalProcedente()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna En Verificacion B en Excel
					Utilitarios.crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalVerificacionB()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Improcedente en Excel
					Utilitarios.crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalImprocedente()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Vencido en Excel
					Utilitarios.crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalVencido()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Revocado en Excel
					Utilitarios.crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalRevocado()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Total en Excel
					Utilitarios.crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmp.getTotalFila()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					numReg++;
				}
			}
			
			//Arregla ancho de columnas
			int pos=0;
			for (;pos<=18;pos++)
			{
				sheet.autoSizeColumn(pos);
			}
						
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta="";
			if (obtenerRutaExcel().compareTo("")!=0)
			{
				
				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreEstadoSolicitud());
				
				strRuta = obtenerRutaExcel() + getNombreEstadoSolicitud() + ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				logger.debug("Ruta final donde encontrar el archivo excel: " + strRuta);
				
				setRutaArchivoExcel(strRuta);
			}
						
		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Estado de Solicitud",e);
			//logger.info("Error al generar el archivo excel debido a: " + e.getStackTrace());
		}	
	}
	
	private void rptExtractor() 
	{
		try 
		{
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb.createSheet(ConstantesVisado.RPT_NOMBRE_HOJA1);

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
			
			//Se crea la leyenda de quien genero el archivo y la hora respectiva
			Row rowG = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerGenerador(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			Row rowG1 = sheet.createRow((short) 3);
			Utilitarios.crearCell(wb, rowG1, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false, false,false,HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.obtenerFechaHoraActual(),  true, false,true,HSSFColor.DARK_BLUE.index);
			
			//Genera celdas con los filtros de busqueda
			//Row rowFI = sheet.createRow((short) 2);
			
			Utilitarios.crearCell(wb, rowG, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaInicio()!=null)
			{
				Utilitarios.crearCell(wb, rowG, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,Utilitarios.formatoFechaSinHora(getFechaInicio()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, rowG, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			//Row rowFF = sheet.createRow((short) 2);
			
			Utilitarios.crearCell(wb, rowG, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false, false,false,HSSFColor.DARK_BLUE.index);
			if (getFechaFin()!=null)
			{
				Utilitarios.crearCell(wb, rowG, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.formatoFechaSinHora(getFechaFin()), true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, rowG, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			Row rowEs = sheet.createRow((short) 4);
			
			Utilitarios.crearCell(wb, rowEs, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO, false, false,false,HSSFColor.DARK_BLUE.index);
			if (lstEstadoSelected!=null)
			{
				/*String cadena = "";
				int ind=0;
				
				for (; ind <= lstEstadoSelected.size() - 1; ind++) 
				{
					cadena+= buscarEstadoxCodigo(lstEstadoSelected.get(ind))+",";
				}*/
				String cadena="";
				int j=0;
				int cont=1;
				
				for (;j<=lstEstadoSelected.size()-1;j++)
				{
					if (lstEstadoSelected.size()>1)
					{
						if (cont==lstEstadoSelected.size())
						{
							cadena=cadena.concat(buscarEstadoxCodigo(lstEstadoSelected.get(j).toString()));
						}
						else
						{
							cadena=cadena.concat(buscarEstadoxCodigo(lstEstadoSelected.get(j).toString().concat(",")));
							cont++;
						}
					}
					else
					{
						cadena = buscarEstadoxCodigo(lstEstadoSelected.get(j)).toString();
					}		
				}
				
				Utilitarios.crearCell(wb, rowEs, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true,HSSFColor.DARK_BLUE.index);
			}
			else
			{
				Utilitarios.crearCell(wb, rowEs, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
			}
			
			//Generando la estructura de las solicitudes
			if (solicitudes.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 7);

				// Creo las celdas de mi fila, se puede poner un dise�o a la celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ITEM, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_CODIGO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ESTADO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_SOL, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_COMISION, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_COD_OFICINA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_OFICINA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_MONEDA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 8, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_IMPORTE, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 9, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NRO_VOUCHER, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 10, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TERRITORIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 11, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_GRUPO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 12, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_COD_CENTRAL, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 13, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NOMBRES, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 14, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_DOI, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 15, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NRO_DOI, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 16, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_CELULAR, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 17, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_FIJO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 18, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_CLASIFICACION, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 19, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_PARTICIPACION, true, true,false,HSSFColor.DARK_BLUE.index);
				
				Utilitarios.crearCell(wb, rowT, 20, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_COMISION, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 21, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ESTUDIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 22, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_RECLAMO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 23, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NIVELES, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 24, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_DELEGACION, true, true,false,HSSFColor.DARK_BLUE.index);
				
				int numReg=8;
				int contador=0;
				for (TiivsSolicitud tmp: solicitudes)
				{
					contador++;
					//Columna Item en Excel
					Row row = sheet.createRow((short) numReg);
					if (contador<=9)
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.TRES_CEROS + contador, true, false,true,HSSFColor.DARK_BLUE.index);
					}
					else if (contador<=99 && contador >9)
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, ConstantesVisado.DOS_CEROS + contador, true, false,true,HSSFColor.DARK_BLUE.index);
					}
					else if(contador>=99 && contador<999)
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.CERO + contador, true, false,true,HSSFColor.DARK_BLUE.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(contador), true, false,true,HSSFColor.DARK_BLUE.index);
					}
					
					//Columna Codigo en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCodSoli(), true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Estado en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarEstadoxCodigo(tmp.getEstado()), true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Tipo Solicitud en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsTipoSolicitud().getDesTipServicio()), true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Tipo Comision en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
										
					//Columna Cod Oficina en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsOficina1().getCodOfi()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Oficina en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsOficina1().getDesOfi()),true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Moneda en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarAbrevMoneda(Utilitarios.validarCampoNull(tmp.getMoneda())), true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Importe en Excel
					Utilitarios.crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getImporte().toString()), true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Nro Voucher en Excel
					Utilitarios.crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getNroVoucher()), true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Territorio en Excel
					Utilitarios.crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, 
							buscarDesTerritorio(Utilitarios.validarCampoNull(tmp.getTiivsOficina1().getTiivsTerritorio().getCodTer())), true, false,true,HSSFColor.DARK_BLUE.index);
					
					int fila=row.getRowNum();
					int filaTmp=row.getRowNum();
					List<AgrupacionSimpleDto> tmpListaAgrupaciones = buscarAgrupacionesxSolicitud(tmp.getCodSoli());
					
					for (AgrupacionSimpleDto tmpAgrup: tmpListaAgrupaciones)
					{
						if (tmpAgrup.getId().getCodSoli().equals(tmp.getCodSoli()))
						{
							//Columna Cod Central en Excel
							Utilitarios.crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,String.valueOf(tmpAgrup.getId().getNumGrupo()), true, false,true,HSSFColor.DARK_BLUE.index);
							
							for (TiivsPersona tmpPersonaPod: tmpAgrup.getLstPoderdantes())
							{
								//Columna Cod Central en Excel
								Utilitarios.crearCell(wb, row, 12, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaPod.getCodCen(), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Poderdante en Excel
								Utilitarios.crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaPod.getNombre() + " " + tmpPersonaPod.getApePat() + " " + tmpPersonaPod.getApeMat() , true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Tipo DOI en Excel
								Utilitarios.crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerDescripcionDocumentos(tmpPersonaPod.getTipDoi().trim()), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Nro DOI en Excel
								Utilitarios.crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaPod.getNumDoi(), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Celular en Excel
								Utilitarios.crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaPod.getNumCel(), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Nro Fijo en Excel
								Utilitarios.crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Clasificacion en Excel
								Utilitarios.crearCell(wb, row, 18, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_PODERDANTE, true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Particicacion en Excel
								Utilitarios.crearCell(wb, row, 19, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerDescripcionClasificacion(tmpPersonaPod.getTipPartic().trim()), true, false,true,HSSFColor.DARK_BLUE.index);
								
								filaTmp++;
								
								row.setRowNum(filaTmp);
							}
							
							for (TiivsPersona tmpPersonaApod: tmpAgrup.getLstApoderdantes())
							{
								//Columna Cod Central en Excel
								Utilitarios.crearCell(wb, row, 12, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaApod.getCodCen(), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Poderdante en Excel
								Utilitarios.crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaApod.getNombre() + " " + tmpPersonaApod.getApePat() + " " + tmpPersonaApod.getApeMat(), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Tipo DOI en Excel
								Utilitarios.crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerDescripcionDocumentos(tmpPersonaApod.getTipDoi().trim()), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Nro DOI en Excel
								Utilitarios.crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaApod.getNumDoi(), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Celular en Excel
								Utilitarios.crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmpPersonaApod.getNumCel(), true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Nro Fijo en Excel
								Utilitarios.crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Clasificacion en Excel
								Utilitarios.crearCell(wb, row, 18, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_APODERADO, true, false,true,HSSFColor.DARK_BLUE.index);
								
								//Columna Particicacion en Excel
								Utilitarios.crearCell(wb, row, 19, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, obtenerDescripcionClasificacion(tmpPersonaApod.getTipPartic().trim()), true, false,true,HSSFColor.DARK_BLUE.index);
								
								filaTmp++;
								
								row.setRowNum(filaTmp);
							}
						}
					}
					
					row.setRowNum(fila);
					
					//Columna Tipo Comision en Excel
					Utilitarios.crearCell(wb, row, 20, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Estudio en Excel
					if (tmp.getTiivsEstudio()!=null)
					{
						Utilitarios.crearCell(wb, row, 21, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(tmp.getTiivsEstudio().getDesEstudio()) , true, false,true,HSSFColor.DARK_BLUE.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 21, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, Utilitarios.validarCampoNull(null),true,false,true,HSSFColor.DARK_BLUE.index);
					}
					
					//Columna Reclamo en Excel
					Utilitarios.crearCell(wb, row, 22, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Niveles en Excel
					Utilitarios.crearCell(wb, row, 23, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true,HSSFColor.DARK_BLUE.index);
					
					//Columna Delegado
					if (validarSolicitudConDelegacion(tmp.getCodSoli()))
					{
						Utilitarios.crearCell(wb, row, 24, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.DARK_BLUE.index);
					}
					else
					{
						Utilitarios.crearCell(wb, row, 24, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.DARK_BLUE.index);
					}
					
					numReg++;
				}
			}
			
			//Arregla ancho de columnas
			int pos=0;
			for (;pos<=24;pos++)
			{
				sheet.autoSizeColumn(pos);
			}
			
			//Creacion de hoja para el historial de la solicitud
			Sheet sheet2 = wb.createSheet(ConstantesVisado.RPT_NOMBRE_HOJA2);
			
			// quito las lineas del libro para darle un mejor acabado
			sheet2.setDisplayGridlines(false);
			
			//Generando la estructura de las solicitudes
			if (solicitudes.size()==0)
			{
				logger.info("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet2.createRow((short) 1);

				// Creo las celdas de mi fila, se puede poner un dise�o a la celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NRO_SOLICITUD, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ESTADO_HOJA2, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NIVEL, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_DELEGADO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_FECHA, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_USUARIO, true, true,false,HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_OBS, true, true,false,HSSFColor.DARK_BLUE.index);
				
				int numReg=2;
				for (TiivsSolicitud tmp2: solicitudes)
				{
					Row row = sheet2.createRow((short) numReg);
					
					obtenerHistorialSolicitud(tmp2.getCodSoli());
					
					for (SeguimientoDTO seg: lstSeguimientoDTO)
					{
						//Columna Nro Solicitud en Excel
						Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp2.getCodSoli(), true, false,true,HSSFColor.DARK_BLUE.index);
						
						//Columna Estado en Excel
						Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarEstadoxCodigo(seg.getEstado()), true, false,true,HSSFColor.DARK_BLUE.index);
						
						//Columna Nivel en Excel
						Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, seg.getNivel(), true, false,true,HSSFColor.DARK_BLUE.index);
						
						//Columna Delegado en Excel
						if (validarSolicitudConDelegacion(tmp2.getCodSoli()))
						{
							Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true,HSSFColor.DARK_BLUE.index);
						}
						else
						{
							Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true,HSSFColor.DARK_BLUE.index);
						}
						
						//Columna Fecha en Excel
						Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,Utilitarios.formatoFechaSinHora(seg.getFecha()), true, false,true,HSSFColor.DARK_BLUE.index);
						
						//Columna Usuario en Excel
						Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,seg.getUsuario(), true, false,true,HSSFColor.DARK_BLUE.index);
						
						//Columna Observaciones en Excel
						Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,seg.getObs(), true, false,true,HSSFColor.DARK_BLUE.index);
					}
					
				}
				
				//Arregla ancho de columnas
				int posHoj2=0;
				for (;posHoj2<=24;posHoj2++)
				{
					sheet2.autoSizeColumn(posHoj2);
				}
			}
						
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta="";
			if (obtenerRutaExcel().compareTo("")!=0)
			{
				
				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreExtractor());
				
				strRuta = obtenerRutaExcel() + getNombreExtractor() + ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				logger.debug("Ruta final donde encontrar el archivo excel: " + strRuta);
				
				setRutaArchivoExcel(strRuta);
			}
						
		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Extractor",e);
			//logger.info("Error al generar el archivo excel debido a: " + e.getStackTrace());
		}	
	}
	
	public List<AgrupacionSimpleDto> buscarAgrupacionesxSolicitud(String codSoli)
	{
		List<AgrupacionSimpleDto> tmpLista = new ArrayList<AgrupacionSimpleDto>();
		
		for (AgrupacionSimpleDto tmpAgrup: lstAgrupacionSimpleDto)
		{
			if (tmpAgrup.getId().getCodSoli().equals(codSoli))
			{
				tmpLista.add(tmpAgrup);
			}
		}
		
		logger.info("Agrupaciones encontradas de la solicitud: " + codSoli + ": " + tmpLista.size());
		
		return tmpLista;
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
	
	public void abrirExcelEstadoSolicitud()
	{
		try {
			exportarExcelEstadoSolicitud();
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
	
	public void abrirExcelRecaudacion()
	{
		try {
			exportarExcelRecaudacion();
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
	
	public void abrirExcelSolicitudesxTpoServ()
	{
		try {
			exportarExcelSolicitudTipoServ();
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
	
	public void abrirExcelExtractor()
	{
		try {
			exportarExcelExtractor();
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
	
	public void descargarArchivoExtractor()
	{
		exportarExcelExtractor();
		InputStream stream=null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: " + e.getMessage());
		}
		
		if (stream!=null)
		{
			file = new DefaultStreamedContent(stream, "application/excel", nombreExtractor+ConstantesVisado.EXTENSION_XLS);
		}
	}
	
	public void descargarArchivoEstadoSolicitud()
	{
		exportarExcelEstadoSolicitud();
		InputStream stream=null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: " + e.getMessage());
		}
		
		if (stream!=null)
		{
			file = new DefaultStreamedContent(stream, "application/excel", nombreEstadoSolicitud+ConstantesVisado.EXTENSION_XLS);
		}
	}
	
	public void descargarArchivoSolicitudesxTpoServ()
	{
		exportarExcelSolicitudTipoServ();
		InputStream stream=null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: " + e.getMessage());
		}
		
		if (stream!=null)
		{
			file = new DefaultStreamedContent(stream, "application/excel", nombreTipoServicio+ConstantesVisado.EXTENSION_XLS);
		}
	}
	
	public void descargarArchivoRecaudacion()
	{
		exportarExcelRecaudacion();
		InputStream stream=null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: " + e.getMessage());
		}
		
		if (stream!=null)
		{
			file = new DefaultStreamedContent(stream, "application/excel", nombreRecaudacion+ConstantesVisado.EXTENSION_XLS);
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
	
	public Boolean validarSolicitudConDelegacion(String codSoli)
	{
		boolean bEncontrado=false;
		
		/*String codigoSolicVerA = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A);
		String codigoSolicVerB = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_B);

		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.or(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO, codigoSolicVerA),Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicVerB)));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}
		
		//lstSolicitudesSelected.clear();
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
		}*/
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
	
	public String buscarDesTerritorio(String codigoTerritorio) 
	{
		String resultado = "";
		
		if (codigoTerritorio.compareTo("")!=0)
		{
			logger.debug("Buscando Territorio por codigo: " + codigoTerritorio);
			
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
		}
		
		return resultado;
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

	public List<String> getLstEstadoSelected() {
		return lstEstadoSelected;
	}

	public void setLstEstadoSelected(List<String> lstEstadoSelected) {
		this.lstEstadoSelected = lstEstadoSelected;
	}

	public List<String> getLstTipoSolicitudSelected() {
		return lstTipoSolicitudSelected;
	}

	public void setLstTipoSolicitudSelected(List<String> lstTipoSolicitudSelected) {
		this.lstTipoSolicitudSelected = lstTipoSolicitudSelected;
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

	public List<String> getLstSolicitudesSelected() {
		return lstSolicitudesSelected;
	}

	public void setLstSolicitudesSelected(List<String> lstSolicitudesSelected) {
		this.lstSolicitudesSelected = lstSolicitudesSelected;
	}

	public List<String> getLstSolicitudesxOpeBan() {
		return lstSolicitudesxOpeBan;
	}

	public void setLstSolicitudesxOpeBan(List<String> lstSolicitudesxOpeBan) {
		this.lstSolicitudesxOpeBan = lstSolicitudesxOpeBan;
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

	public List<AgrupacionSimpleDto> getLstAgrupacionSimpleDto() {
		return lstAgrupacionSimpleDto;
	}

	public void setLstAgrupacionSimpleDto(
			List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;
	}

	public List<SeguimientoDTO> getLstSeguimientoDTO() {
		return lstSeguimientoDTO;
	}

	public void setLstSeguimientoDTO(List<SeguimientoDTO> lstSeguimientoDTO) {
		this.lstSeguimientoDTO = lstSeguimientoDTO;
	}

	public String getIdTerr() {
		return idTerr;
	}

	public void setIdTerr(String idTerr) {
		this.idTerr = idTerr;
	}

	public List<SolicitudesOficina> getLstSolicitudesOficina() {
		return lstSolicitudesOficina;
	}

	public void setLstSolicitudesOficina(
			List<SolicitudesOficina> lstSolicitudesOficina) {
		this.lstSolicitudesOficina = lstSolicitudesOficina;
	}

	public String getIdOfi() {
		return idOfi;
	}

	public void setIdOfi(String idOfi) {
		this.idOfi = idOfi;
	}

	public String getIdOfi1() {
		return idOfi1;
	}

	public void setIdOfi1(String idOfi1) {
		this.idOfi1 = idOfi1;
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

	public String getNombreExtractor() {
		return nombreExtractor;
	}

	public void setNombreExtractor(String nombreExtractor) {
		this.nombreExtractor = nombreExtractor;
	}

	public String getNombreEstadoSolicitud() {
		return nombreEstadoSolicitud;
	}

	public void setNombreEstadoSolicitud(String nombreEstadoSolicitud) {
		this.nombreEstadoSolicitud = nombreEstadoSolicitud;
	}

	public Boolean getMostrarBotones() {
		return mostrarBotones;
	}

	public void setMostrarBotones(Boolean mostrarBotones) {
		this.mostrarBotones = mostrarBotones;
	}

	public String getTxtMsgDialog() {
		return txtMsgDialog;
	}

	public void setTxtMsgDialog(String txtMsgDialog) {
		this.txtMsgDialog = txtMsgDialog;
	}

	public String getCodSolicitud() {
		return codSolicitud;
	}

	public void setCodSolicitud(String codSolicitud) {
		this.codSolicitud = codSolicitud;
	}

	public String getIdOpeBan() {
		return idOpeBan;
	}

	public void setIdOpeBan(String idOpeBan) {
		this.idOpeBan = idOpeBan;
	}

	public String getIdImporte() {
		return idImporte;
	}

	public void setIdImporte(String idImporte) {
		this.idImporte = idImporte;
	}

	public Double getImporteIni() {
		return importeIni;
	}

	public void setImporteIni(Double importeIni) {
		this.importeIni = importeIni;
	}

	public Double getImporteFin() {
		return importeFin;
	}

	public void setImporteFin(Double importeFin) {
		this.importeFin = importeFin;
	}

	public String getIdMoneda() {
		return idMoneda;
	}

	public void setIdMoneda(String idMoneda) {
		this.idMoneda = idMoneda;
	}

	public List<SolicitudesTipoServicio> getLstSolicitudesTipoServicio() {
		return lstSolicitudesTipoServicio;
	}

	public void setLstSolicitudesTipoServicio(
			List<SolicitudesTipoServicio> lstSolicitudesTipoServicio) {
		this.lstSolicitudesTipoServicio = lstSolicitudesTipoServicio;
	}

	public List<Moneda> getLstMoneda() {
		return lstMoneda;
	}

	public void setLstMoneda(List<Moneda> lstMoneda) {
		this.lstMoneda = lstMoneda;
	}

	public String getNombreTipoServicio() {
		return nombreTipoServicio;
	}

	public void setNombreTipoServicio(String nombreTipoServicio) {
		this.nombreTipoServicio = nombreTipoServicio;
	}

	public List<RecaudacionTipoServ> getLstRecaudacionTipoServ() {
		return lstRecaudacionTipoServ;
	}

	public void setLstRecaudacionTipoServ(
			List<RecaudacionTipoServ> lstRecaudacionTipoServ) {
		this.lstRecaudacionTipoServ = lstRecaudacionTipoServ;
	}

	public String getNombreRecaudacion() {
		return nombreRecaudacion;
	}

	public void setNombreRecaudacion(String nombreRecaudacion) {
		this.nombreRecaudacion = nombreRecaudacion;
	}
}
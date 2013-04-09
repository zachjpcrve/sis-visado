package com.hildebrando.visado.mb;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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
import org.primefaces.component.log.Log;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.AgrupacionPlazoDto;
import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.Liquidacion;
import com.hildebrando.visado.modelo.RecaudacionTipoServ;
import com.hildebrando.visado.modelo.SolicitudesOficina;
import com.hildebrando.visado.modelo.SolicitudesTipoServicio;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacion;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;
import com.hildebrando.visado.modelo.TiivsTerritorio;

@ManagedBean(name = "reportesMB")
@SessionScoped
public class ReportesMB {
	
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
	private List<AgrupacionPlazoDto> lstLiquidacion;
	private List<Moneda> lstMoneda;

	private Date fechaInicio;
	private Date fechaFin;
	private String nombreExtractor;
	private String nombreEstadoSolicitud;
	private String nombreTipoServicio;
	private String nombreRecaudacion;
	private String nombreLiquidacion;
	private String rutaArchivoExcel;
	private String PERFIL_USUARIO;
	private String idTerr;
	private String idOfi;
	private String idOfi1;
	private String textoTotalResultados;
	private String txtMsgDialog;
	private String codSolicitud;
	private String idOpeBan;
	private String idImporte;
	private String idMoneda;
	private String textoAnioMes;
	private Double importeIni;
	private Double importeFin;
	private StreamedContent file;
	private IILDPeUsuario usuario;
	private Boolean noHabilitarExportar;
	private Boolean mostrarBotones = false;
	private int anio;
	private int mes;
	private double impuesto = 0.0;

	public static Logger logger = Logger.getLogger(ReportesMB.class);

	public ReportesMB() {
		usuario = (IILDPeUsuario) Utilitarios
				.getObjectInSession("USUARIO_SESION");
		PERFIL_USUARIO = (String) Utilitarios
				.getObjectInSession("PERFIL_USUARIO");
		lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
		lstSolicitudesOficina = new ArrayList<SolicitudesOficina>();
		lstRecaudacionTipoServ = new ArrayList<RecaudacionTipoServ>();
		lstSolicitudesTipoServicio = new ArrayList<SolicitudesTipoServicio>();
		lstLiquidacion = new ArrayList<AgrupacionPlazoDto>();

		combosMB = new CombosMB();
		combosMB.cargarMultitabla();

		inicializarCampos();

		generarNombreArchivoExtractor();
		generarNombreArchivoEstadoSolicitud();
		generarNombreArchivoTipoServicio();
		generarNombreRecaudacion();
		generarNombreLiquidacion();

		setearTextoTotalResultados(
				ConstantesVisado.MSG_TOTAL_REGISTROS
						+ lstSolicitudesOficina.size()
						+ ConstantesVisado.MSG_REGISTROS,
				lstSolicitudesOficina.size());
		if (lstSolicitudesOficina.size() > 0) {
			setNoHabilitarExportar(false);
		} else {
			setNoHabilitarExportar(true);
		}

		impuesto = obtenerImpuesto();
	}

	public void verRecaudacion(){
		logger.debug("verPagina");
		
	}
	public double obtenerImpuesto() {
		double impuesto = 0.0;

		// Se obtiene los dias utiles de la Multitabla
		for (TiivsMultitabla tmp : combosMB.getLstMultitabla()) {
			if (tmp.getId().getCodMult().trim()
					.equals(ConstantesVisado.CODIGO_MULTITABLA_COMISION)) {
				if (tmp.getId().getCodElem()
						.equals(ConstantesVisado.CODIGO_CAMPO_IMPUESTO)) {
					impuesto = Double.valueOf(tmp.getValor2());
				}
			}
		}

		return impuesto;
	}

	public void actualizarTextoAnioMes() {
		logger.info("Mes seleccionado: " + getMes());
		logger.info("Anio seleccionado: " + getAnio());

		String sAnio = "";
		if (getAnio() == 0) {
			sAnio = "2013";
			setTextoAnioMes(Utilitarios.buscarMesxCodigo(getMes())
					+ ConstantesVisado.ESPACIO_BLANCO + sAnio);
		} else {
			setTextoAnioMes(Utilitarios.buscarMesxCodigo(getMes())
					+ ConstantesVisado.ESPACIO_BLANCO
					+ Utilitarios.buscarAnioxCodigo(getAnio()));
		}

	}

	public void inicializarCampos() {
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

	public String obtenerGenerador() {
		String resultado = "";

		if (usuario != null) {
			resultado = usuario.getUID() + ConstantesVisado.GUION
					+ usuario.getNombre() + ConstantesVisado.ESPACIO_BLANCO
					+ usuario.getApellido1() + ConstantesVisado.ESPACIO_BLANCO
					+ usuario.getApellido2();
		} else {
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

	public String devolverDesTipoDOI(String codigo) {
		String resultado = "";
		if (codigo != null) {
			for (TipoDocumento tmp : combosMB.getLstTipoDocumentos()) {
				if (codigo.equalsIgnoreCase(tmp.getCodTipoDoc())) {
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

	public void exportarExcelExtractor() {
		rptExtractor();
	}

	public void exportarExcelEstadoSolicitud() {
		rptEstadoSolicitud();
	}

	public void exportarExcelRecaudacion() {
		rptRecaudacion();
	}

	public void exportarExcelLiquidacion() {
		rptLiquidacion_2();
	}

	public void exportarExcelSolicitudTipoServ() {
		rptSolicitudTipoServ();
	}

	public void buscarLiquidacion() {
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("solicitudEspDao");

		// Busqueda por estudio
		String cadEstudio = "";

		if (lstEstudioSelected.size() > 0) {
			for (String estado : lstEstudioSelected) {
				cadEstudio += "'" + estado + "',";
			}
			cadEstudio = cadEstudio.substring(0, cadEstudio.lastIndexOf(","));
		}

		// Busqueda por anio
		int pAnio = 0;

		if (getAnio() != 0) {
			switch (getAnio()) {
			case 1:
				pAnio = 2013;
				break;
			case 2:
				pAnio = 2014;
				break;
			case 3:
				pAnio = 2015;
				break;
			case 4:
				pAnio = 2016;
				break;
			default:
				break;
			}
		}

		// Busqueda por mes
		int mes = 0;

		if (getMes() != 0) {
			mes = getMes();
		}

		try {
			this.lstLiquidacion = solicitudService.obtenerLiquidacion_2(
					cadEstudio, pAnio, mes, impuesto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (lstLiquidacion.size() > 1) {
			setNoHabilitarExportar(false);
		} else {
			setNoHabilitarExportar(true);
		}
	}

	public void buscarRecaudacionxTipoServ() {
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("solicitudEspDao");
		Date fechaIni = null;
		Date fechaFin = null;

		if (getFechaInicio() != null && getFechaFin() != null) {
			fechaIni = getFechaInicio();
			fechaFin = getFechaFin();
		}

		TiivsSolicitud tmpSolicitud = new TiivsSolicitud();
		TiivsTerritorio tmpTerr = new TiivsTerritorio();
		TiivsOficina1 tmpOficina = new TiivsOficina1();

		if (getIdTerr() != null && getIdTerr().compareTo("") != 0) {
			tmpTerr.setCodTer(getIdTerr());
		}

		if (getIdOfi() != null && getIdOfi().compareTo("") != 0) {
			tmpOficina.setCodOfi(getIdOfi());
		}

		if (getIdOfi1() != null && getIdOfi1().compareTo("") != 0) {
			tmpOficina.setCodOfi(getIdOfi1());
		}

		tmpOficina.setTiivsTerritorio(tmpTerr);
		tmpSolicitud.setTiivsOficina1(tmpOficina);

		try {
			this.lstRecaudacionTipoServ = solicitudService
					.obtenerListarRecaudacionxTipoServicio(tmpSolicitud,
							fechaIni, fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int iNuevoTotal = lstRecaudacionTipoServ.size() - 1;

		setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
				+ iNuevoTotal + ConstantesVisado.MSG_REGISTROS, iNuevoTotal);
		if (lstRecaudacionTipoServ.size() > 1) {
			setNoHabilitarExportar(false);
		} else {
			setNoHabilitarExportar(true);
		}
	}

	public void buscarOficinaPorTerritorio() {
		if (getIdTerr() != null) {
			logger.debug("Buscando oficina por territorio: " + getIdTerr());

			GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
			filtroOfic.createAlias(ConstantesVisado.NOM_TBL_TERRITORIO,
					ConstantesVisado.ALIAS_TBL_TERRITORIO);
			filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_TERR_ALIAS,
					getIdTerr()));

			List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

			try {
				lstTmp = ofiDAO.buscarDinamico(filtroOfic);
				combosMB.setLstOficina(ofiDAO.buscarDinamico(filtroOfic));
				combosMB.setLstOficina1(ofiDAO.buscarDinamico(filtroOfic));

			} catch (Exception exp) {
				logger.debug("No se pudo encontrar la oficina");
			}

			if (lstTmp.size() > 0) {
				setIdOfi(lstTmp.get(0).getCodOfi());
				setIdOfi1(lstTmp.get(0).getCodOfi());
			}

		} else {
			// Carga combo de Territorio
			GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);

			try {
				combosMB.setLstTerritorio(terrDAO.buscarDinamico(filtroTerr));
			} catch (Exception e1) {
				logger.debug("Error al cargar el listado de territorios");
			}

			// Cargar combos de oficina
			GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);

			try {
				combosMB.setLstOficina(ofiDAO.buscarDinamico(filtroOfic));
				combosMB.setLstOficina1(ofiDAO.buscarDinamico(filtroOfic));

			} catch (Exception exp) {
				logger.debug("No se pudo encontrar la oficina");
			}
		}
	}

	public void buscarOficinaPorCodigo() {
		logger.debug("Buscando oficina por codigo: " + getIdOfi());

		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,
				getIdOfi()));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}

		if (lstTmp.size() > 0) {
			setIdOfi1(lstTmp.get(0).getCodOfi());
			setIdTerr(lstTmp.get(0).getTiivsTerritorio().getCodTer());
		}
	}

	public String buscarNomOficinaPorCodigo(String codigo) {
		String resultado = "";
		logger.debug("Buscando oficina por codigo: " + codigo);

		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,
				codigo));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}

		if (lstTmp.size() == 1) {
			resultado = lstTmp.get(0).getDesOfi();
		}

		return resultado;
	}

	public String buscarNomTerrPorCodigo(String codigo) {
		String resultado = "";
		logger.debug("Buscando oficina por codigo: " + codigo);

		GenericDao<TiivsTerritorio, Object> tDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroT = Busqueda.forClass(TiivsTerritorio.class);
		filtroT.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_TERRITORIO,
				codigo));

		List<TiivsTerritorio> lstTmp = new ArrayList<TiivsTerritorio>();

		try {
			lstTmp = tDAO.buscarDinamico(filtroT);
		} catch (Exception exp) {
			logger.error("No se pudo encontrar el nombre del territorio", exp);
		}

		if (lstTmp.size() == 1) {
			resultado = lstTmp.get(0).getDesTer();
		}

		return resultado;
	}

	public void buscarOficinaPorNombre() {
		logger.debug("Buscando oficina por nombre: " + getIdOfi1());

		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,
				getIdOfi1()));

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

	public void buscarSolicitudesxOficina() {
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("solicitudEspDao");
		Date fechaIni = null;
		Date fechaFin = null;

		if (getFechaInicio() != null && getFechaFin() != null) {
			fechaIni = getFechaInicio();
			fechaFin = getFechaFin();
		}

		TiivsSolicitud tmpSolicitud = new TiivsSolicitud();
		TiivsTerritorio tmpTerr = new TiivsTerritorio();
		TiivsOficina1 tmpOficina = new TiivsOficina1();

		if (getIdTerr() != null && getIdTerr().compareTo("") != 0) {
			tmpTerr.setCodTer(getIdTerr());
		}

		if (getIdOfi() != null && getIdOfi().compareTo("") != 0) {
			tmpOficina.setCodOfi(getIdOfi());
		}

		if (getIdOfi1() != null && getIdOfi1().compareTo("") != 0) {
			tmpOficina.setCodOfi(getIdOfi1());
		}

		tmpOficina.setTiivsTerritorio(tmpTerr);
		tmpSolicitud.setTiivsOficina1(tmpOficina);

		try {
			this.lstSolicitudesOficina = solicitudService
					.obtenerListarTotalSolicitudesxEstado(tmpSolicitud,
							fechaIni, fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int iNuevoTotal = lstSolicitudesOficina.size() - 1;

		setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
				+ iNuevoTotal + ConstantesVisado.MSG_REGISTROS, iNuevoTotal);
		if (lstSolicitudesOficina.size() > 1) {
			setNoHabilitarExportar(false);
		} else {
			setNoHabilitarExportar(true);
		}
	}

	public void buscarSolicitudesxTipoServicio() {
		SolicitudDao<TiivsSolicitud, Object> solicitudService = (SolicitudDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("solicitudEspDao");
		Date fechaIni = null;
		Date fechaFin = null;

		// Busqueda de solicitudes por rango de fechas
		if (getFechaInicio() != null && getFechaFin() != null) {
			fechaIni = getFechaInicio();
			fechaFin = getFechaFin();
		}

		TiivsSolicitud tmpSolicitud = new TiivsSolicitud();

		// Busqueda de solicitudes por codigo
		if (getCodSolicitud() != null) {
			tmpSolicitud.setCodSoli(getCodSolicitud());
		}

		// Busqueda de solicitudes por tipo de servicio
		String cadTipoServ = "";
		if (lstTipoSolicitudSelected.size() > 0) {
			int j = 0;
			int cont = 1;

			for (; j <= lstTipoSolicitudSelected.size() - 1; j++) {
				if (lstTipoSolicitudSelected.size() > 1) {
					if (cont == lstTipoSolicitudSelected.size()) {
						cadTipoServ = cadTipoServ
								.concat(lstTipoSolicitudSelected.get(j)
										.toString());
					} else {
						cadTipoServ = cadTipoServ
								.concat(lstTipoSolicitudSelected.get(j)
										.toString().concat(","));
						cont++;
					}
				} else {
					cadTipoServ = lstTipoSolicitudSelected.get(j).toString();
				}
			}
		}

		// Busqueda por rangos de importe (Rango Global)
		String rangoImpG = "";

		if (getIdImporte().compareTo("") != 0) {
			logger.info("Filtro por importe: " + getIdImporte());

			rangoImpG = getIdImporte();
		}

		// Busqueda por rangos de importe (Importe Minimo y Maximo)
		Double rangoIni = 0.0;
		Double rangoFin = 0.0;

		if (getImporteIni() != 0 && getImporteFin() != 0) {
			logger.info("Rango de Inicio: " + rangoIni);
			logger.info("Rango de Fin: " + rangoFin);

			rangoIni = getImporteIni();
			rangoFin = getImporteFin();
		}

		// Busqueda por estudio
		String cadEstudio = "";
		if (lstEstudioSelected.size() > 0) {
			int j = 0;
			int cont = 1;

			for (; j <= lstEstudioSelected.size() - 1; j++) {
				if (lstEstudioSelected.size() > 1) {
					if (cont == lstEstudioSelected.size()) {
						cadEstudio = cadEstudio.concat(lstEstudioSelected
								.get(j).toString());
					} else {
						cadEstudio = cadEstudio.concat(lstEstudioSelected
								.get(j).toString().concat(","));
						cont++;
					}
				} else {
					cadEstudio = lstEstudioSelected.get(j).toString();
				}
			}
		}

		if (getIdMoneda() != null) {
			tmpSolicitud.setMoneda(getIdMoneda());
		}

		try {
			this.lstSolicitudesTipoServicio = solicitudService
					.obtenerSolicitudesxTipoServicio(tmpSolicitud,
							Utilitarios.validarCampoNull(getIdOpeBan()),
							cadTipoServ, cadEstudio, rangoImpG, rangoIni,
							rangoFin, fechaIni, fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int iNuevoTotal = lstSolicitudesTipoServicio.size();

		setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
				+ iNuevoTotal + ConstantesVisado.MSG_REGISTROS, iNuevoTotal);
		if (lstSolicitudesTipoServicio.size() > 0) {
			setNoHabilitarExportar(false);
		} else {
			setNoHabilitarExportar(true);
		}
	}

	// Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a
	// los filtros seleccionados.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	@SuppressWarnings("unchecked")
	public void buscarSolicitudesExtractor() {
		logger.info("Buscando solicitudes RPT Extractor:");
		/*
		 * LocalDate newFechaInicio = null; LocalDate newFechaFin = null;
		 */
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);

		// Busqueda por fecha de Registro
		if (getFechaInicio() != null && getFechaFin() != null) {
			/*
			 * logger.info("Buscando por fecha de registro");
			 * 
			 * LocalDate newFechaInicio = null; LocalDate newFechaFin = null;
			 * 
			 * DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy");
			 * DateFormat formato = new SimpleDateFormat("dd/MM/yy");
			 * 
			 * String tmpFecIni = formato.format(getFechaInicio()); String
			 * tmpFecFin = formato.format(getFechaFin());
			 * 
			 * newFechaInicio=fmt.parseLocalDate(tmpFecIni); newFechaFin=
			 * fmt.parseLocalDate(tmpFecFin);
			 * 
			 * Date dFechaIni =
			 * newFechaInicio.toDateTimeAtStartOfDay().toDate(); Date dFechaFin
			 * = newFechaFin.toDateTimeAtStartOfDay().toDate();
			 * 
			 * logger.info("Fecha Inicio: " +dFechaIni );
			 * logger.info("Fecha Fin: " +dFechaFin );
			 * 
			 * filtroSol.add(Restrictions.between(ConstantesVisado.
			 * CAMPO_FECHA_REGISTRO, dFechaIni,dFechaFin));
			 */
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				String fecIni = formatter.format(getFechaInicio());
				Date minDate = formatter.parse(fecIni);
				String fecFin = formatter.format(getFechaFin());
				Date maxDate = formatter.parse(fecFin);
				Date rangoFin = new Date(maxDate.getTime()
						+ TimeUnit.DAYS.toMillis(1));

				logger.info("Fecha Inicio: " + minDate);
				logger.info("Fecha Fin: " + rangoFin);

				filtroSol.add(Restrictions.ge(
						ConstantesVisado.CAMPO_FECHA_REGISTRO, minDate));
				filtroSol.add(Restrictions.le(
						ConstantesVisado.CAMPO_FECHA_REGISTRO, rangoFin));
			} catch (ParseException e) {
				logger.info("Hubo un error al convertir la fecha: ", e);
			}
		}

		// Busqueda por estado de la solicitud
		if (lstEstadoSelected.size() > 0) {
			int ind = 0;

			for (; ind <= lstEstadoSelected.size() - 1; ind++) {
				logger.info("Filtro por estado [" + ind + "]"
						+ lstEstadoSelected.get(ind));
			}

			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,
					lstEstadoSelected));
		}

		filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));

		// Buscar solicitudes de acuerdo a criterios seleccionados
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes: "
					+ ex.getStackTrace());
		}

		if (solicitudes.size() > 0) {
			actualizarDatosDeBusqueda();
			setTxtMsgDialog("Que desea hacer?");
			mostrarBotones = true;
		} else {
			// FacesContext.getCurrentInstance().addMessage("growl",new
			// FacesMessage(FacesMessage.SEVERITY_INFO,
			// "Sin registros para exportar","No se encontraron registros que coinciden con los criterios de búsqueda ingresados"));
			setTxtMsgDialog("No se encontraron registros que coinciden con los criterios de búsqueda ingresados");
			mostrarBotones = false;
		}

	}

	public void obtenerHistorialSolicitud(String codSoli) {
		logger.info("Obteniendo Historial ");
		logger.info("Codigo de solicitud : " + codSoli);

		try {
			lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
			GenericDao<TiivsHistSolicitud, Object> histDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
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
						if (h.getNivelRol() != null
								&& h.getNivelRol()
										.trim()
										.equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE)) {
							desRolNivel = "Responsable";
						}
						if (h.getNivelRol() != null
								&& h.getNivelRol()
										.trim()
										.equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO)) {
							desRolNivel = "Delegado";
						}
						if (h.getNivelEstado() != null
								&& h.getNivelEstado()
										.trim()
										.equals(ConstantesVisado.ESTADOS.ESTADO_COD_Desaprobado_T09)) {
							desEstadoNivel = ConstantesVisado.ESTADOS.ESTADO_Desaprobado_T09;
						}
						if (h.getNivelEstado() != null
								&& h.getNivelEstado()
										.trim()
										.equals(ConstantesVisado.ESTADOS.ESTADO_COD_Aprobado_T09)) {
							desEstadoNivel = ConstantesVisado.ESTADOS.ESTADO_Aprobado_T09;
						}
						iCodNivel = Integer.parseInt(h.getNivel());
						descripcionNivel = "Nivel " + iCodNivel + " "
								+ desRolNivel + ": " + desEstadoNivel;
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

	private void actualizarDatosDeBusqueda() {
		String cadena = "";

		// Se obtiene y setea la descripcion del Estado en la grilla
		for (TiivsSolicitud tmpSol : solicitudes) {
			// Cargar data de poderdantes
			List<TiivsPersona> lstPoderdantes = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstApoderdantes = new ArrayList<TiivsPersona>();
			AgrupacionSimpleDto agrupacionSimpleDto = new AgrupacionSimpleDto();
			List<TiivsPersona> lstPersonas = new ArrayList<TiivsPersona>();
			TiivsPersona objPersona = new TiivsPersona();

			lstPoderdantes = new ArrayList<TiivsPersona>();
			lstApoderdantes = new ArrayList<TiivsPersona>();

			for (TiivsSolicitudAgrupacion x : tmpSol
					.getTiivsSolicitudAgrupacions()) {
				for (TiivsAgrupacionPersona d : x.getTiivsAgrupacionPersonas()) {
					if (tmpSol.getCodSoli().equals(d.getCodSoli())
							&& tmpSol.getCodSoli().equals(
									x.getId().getCodSoli())) {
						objPersona = new TiivsPersona();
						objPersona = d.getTiivsPersona();
						objPersona.setTipPartic(d.getTipPartic());
						objPersona.setsDesctipPartic(this
								.obtenerDescripcionTipoRegistro(d
										.getTipPartic().trim()));
						objPersona.setClasifPer(d.getClasifPer());
						objPersona.setsDescclasifPer(this
								.obtenerDescripcionClasificacion(d
										.getClasifPer().trim()));
						objPersona.setsDesctipDoi(this
								.obtenerDescripcionDocumentos(d
										.getTiivsPersona().getTipDoi().trim()));
						lstPersonas.add(objPersona);

						if (d.getTipPartic().trim()
								.equals(ConstantesVisado.PODERDANTE)) {
							lstPoderdantes.add(d.getTiivsPersona());
						} else if (d.getTipPartic().trim()
								.equals(ConstantesVisado.APODERADO)) {
							lstApoderdantes.add(d.getTiivsPersona());
						}

						agrupacionSimpleDto = new AgrupacionSimpleDto();
						agrupacionSimpleDto
								.setId(new TiivsSolicitudAgrupacionId(tmpSol
										.getCodSoli(), x.getId().getNumGrupo()));
						agrupacionSimpleDto.setLstPoderdantes(lstPoderdantes);
						agrupacionSimpleDto.setLstApoderdantes(lstApoderdantes);
						agrupacionSimpleDto
								.setsEstado(Utilitarios
										.obternerDescripcionEstado(x
												.getEstado().trim()));
						agrupacionSimpleDto.setLstPersonas(lstPersonas);
						lstAgrupacionSimpleDto.add(agrupacionSimpleDto);
					}
				}
			}

			cadena = "";
			for (TiivsPersona tmpPoder : lstPoderdantes) {
				cadena += devolverDesTipoDOI(tmpPoder.getTipDoi())
						+ ConstantesVisado.DOS_PUNTOS + tmpPoder.getNumDoi()
						+ ConstantesVisado.GUION + tmpPoder.getApePat() + " "
						+ tmpPoder.getApeMat() + " " + tmpPoder.getNombre()
						+ ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
			}

			tmpSol.setTxtPoderdante(cadena);

			// Cargar data de apoderados
			cadena = "";

			for (TiivsPersona tmpApor : lstApoderdantes) {
				cadena += devolverDesTipoDOI(tmpApor.getTipDoi())
						+ ConstantesVisado.DOS_PUNTOS + tmpApor.getNumDoi()
						+ ConstantesVisado.GUION + tmpApor.getApePat() + " "
						+ tmpApor.getApeMat() + " " + tmpApor.getNombre()
						+ ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
			}

			tmpSol.setTxtApoderado(cadena);

			// logger.info("Tamanio total agrupaciones de Solicitud: " +
			// tmpSol.getCodSoli() + ": " + lstAgrupacionSimpleDto.size());
		}

	}

	public void generarNombreArchivoExtractor() {
		setNombreExtractor("Extractor_"
				+ Utilitarios.obtenerFechaArchivoExcel()
				+ ConstantesVisado.UNDERLINE
				+ Utilitarios.obtenerHoraArchivoExcel());
	}

	public void generarNombreArchivoEstadoSolicitud() {
		setNombreEstadoSolicitud("Estados_"
				+ Utilitarios.obtenerFechaArchivoExcel()
				+ ConstantesVisado.UNDERLINE
				+ Utilitarios.obtenerHoraArchivoExcel());
	}

	public void generarNombreArchivoTipoServicio() {
		setNombreTipoServicio("TipoServicio_"
				+ Utilitarios.obtenerFechaArchivoExcel()
				+ ConstantesVisado.UNDERLINE
				+ Utilitarios.obtenerHoraArchivoExcel());
	}

	public void generarNombreRecaudacion() {
		setNombreRecaudacion("Recaudacion_"
				+ Utilitarios.obtenerFechaArchivoExcel()
				+ ConstantesVisado.UNDERLINE
				+ Utilitarios.obtenerHoraArchivoExcel());
	}

	public void generarNombreLiquidacion() {
		setNombreLiquidacion("Liquidacion_"
				+ Utilitarios.obtenerFechaArchivoExcel()
				+ ConstantesVisado.UNDERLINE
				+ Utilitarios.obtenerHoraArchivoExcel());
	}

	/*
	 * public void postProcessXLS(Object document) { HSSFWorkbook wb =
	 * (HSSFWorkbook) document; HSSFSheet sheet = wb.getSheetAt(0); CellStyle
	 * style = wb.createCellStyle();
	 * style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
	 * 
	 * for (Row row : sheet) { for (Cell cell : row) {
	 * cell.setCellValue(cell.getStringCellValue().toUpperCase());
	 * cell.setCellStyle(style); } } }
	 */

	private void rptSolicitudTipoServ() {
		try {
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb
					.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			// sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 2, CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.TITULO_REPORTE_RPT_TIPO_SERV, 12);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 4));

			// Se crea la leyenda de quien genero el archivo y la hora
			// respectiva
			// Row rowG = sheet.createRow((short) 1);

			Utilitarios.crearCell(wb, trow, 6, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, trow, 7, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, obtenerGenerador(), true, false,
					true, HSSFColor.DARK_BLUE.index);

			Row rowG1 = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG1, 6, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 7, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.obtenerFechaHoraActual(), true, false, true,
					HSSFColor.DARK_BLUE.index);

			// Genera celdas con los filtros de busqueda

			// Cod Solicitud
			Row row2 = sheet.createRow((short) 4);
			Utilitarios.crearCell(wb, row2, 1, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_COD_SOL, false,
					false, false, HSSFColor.GREY_25_PERCENT.index);
			if (getCodSolicitud() != null) {
				Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, getCodSolicitud(), true,
						false, true, HSSFColor.GREY_25_PERCENT.index);
			} else {
				Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			}

			// Tipo de Servicio
			Utilitarios.crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_TIPO_SERV, false,
					false, false, HSSFColor.GREY_25_PERCENT.index);

			if (lstTipoSolicitudSelected != null) {
				String cadena = "";

				int j = 0;
				int cont = 1;

				for (; j <= lstTipoSolicitudSelected.size() - 1; j++) {
					if (lstTipoSolicitudSelected.size() > 1) {
						if (cont == lstTipoSolicitudSelected.size()) {
							cadena = cadena
									.concat(buscarTipoSolxCodigo((lstTipoSolicitudSelected
											.get(j).toString())));
						} else {
							cadena = cadena
									.concat(buscarTipoSolxCodigo((lstTipoSolicitudSelected
											.get(j).toString().concat(","))));
							cont++;
						}
					} else {
						cadena = buscarTipoSolxCodigo(lstTipoSolicitudSelected
								.get(j).toString());
					}
				}

				Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, cadena, true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			} else {
				Utilitarios.crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			}

			// Tipo de Operacion
			Utilitarios.crearCell(wb, row2, 7, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_TIPO_OP, false,
					false, false, HSSFColor.GREY_25_PERCENT.index);

			if (getIdOpeBan().compareTo("") != 0) {
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						buscarOpeBanxCodigo(getIdOpeBan()), true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			} else {
				Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			}

			// Rango Global
			Row row3 = sheet.createRow((short) 6);
			Utilitarios.crearCell(wb, row3, 1, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_RANGO_GLO, false,
					false, false, HSSFColor.GREY_25_PERCENT.index);

			if (getIdImporte().equals(
					ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) {
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_1,
						true, false, true, HSSFColor.GREY_25_PERCENT.index);
			} else if (getIdImporte()
					.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) {
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_2,
						true, false, true, HSSFColor.GREY_25_PERCENT.index);
			} else if (getIdImporte()
					.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) {
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_3,
						true, false, true, HSSFColor.GREY_25_PERCENT.index);
			} else if (getIdImporte()
					.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) {
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RANGOS_IMPORTE.RANGO_IMPORTE_NRO_4,
						true, false, true, HSSFColor.GREY_25_PERCENT.index);
			} else {
				Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			}

			// Importe Operacion Inicio
			Utilitarios.crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_IMP_OP_INI, false,
					false, false, HSSFColor.GREY_25_PERCENT.index);
			Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, String.valueOf(getImporteIni()),
					true, false, true, HSSFColor.GREY_25_PERCENT.index);

			// Importe Operacion Fin
			Utilitarios.crearCell(wb, row3, 7, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_IMP_OP_FIN, false,
					false, false, HSSFColor.GREY_25_PERCENT.index);
			Utilitarios.crearCell(wb, row3, 8, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, String.valueOf(getImporteFin()),
					true, false, true, HSSFColor.GREY_25_PERCENT.index);

			// Estudio
			Row row4 = sheet.createRow((short) 8);
			Utilitarios.crearCell(wb, row4, 1, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_ESTUDIO, false,
					false, false, HSSFColor.GREY_25_PERCENT.index);

			if (lstEstudioSelected != null) {
				String cadena = "";
				/*
				 * int ind=0;
				 * 
				 * for (; ind <= getLstEstudioSelected().size() - 1; ind++) {
				 * cadena+=
				 * buscarEstudioxCodigo(getLstEstudioSelected().get(ind))+","; }
				 */

				int j = 0;
				int cont = 1;

				for (; j <= lstEstudioSelected.size() - 1; j++) {
					if (lstEstudioSelected.size() > 1) {
						if (cont == lstEstudioSelected.size()) {
							cadena = cadena
									.concat(buscarEstudioxCodigo((lstEstudioSelected
											.get(j).toString())));
						} else {
							cadena = cadena
									.concat(buscarEstudioxCodigo((lstEstudioSelected
											.get(j).toString().concat(","))));
							cont++;
						}
					} else {
						cadena = buscarEstudioxCodigo(lstEstudioSelected.get(j)
								.toString());
					}
				}

				Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, cadena, true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			} else {
				Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.GREY_25_PERCENT.index);
			}

			Utilitarios.crearCell(wb, row4, 4, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_FECHA_INICIO, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaInicio() != null) {
				Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaInicio()),
						true, false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Utilitarios.crearCell(wb, row4, 7, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.RPT_TIPO_SERV_FILTRO_FECHA_FIN, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaFin() != null) {
				Utilitarios.crearCell(wb, row4, 8, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaFin()), true,
						false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row4, 8, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row rowTot = sheet.createRow((short) 10);
			Utilitarios.crearCell(wb, rowTot, 0, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, "Total de Registros: "
							+ (lstSolicitudesTipoServicio.size()), false,
					false, false, HSSFColor.DARK_BLUE.index);

			if (lstSolicitudesTipoServicio.size() == 0) {
				logger.info("Sin registros para exportar");
			} else {
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un diseño a la
				// celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_COD_SOL, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_ESTUDIO, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_TIPO_SERVICIO,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_TIPO_OPERACION,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_MONEDA, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_IMPORTE, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_TIPO_CAMBIO,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_TIPO_SERV_COLUMNA_IMPORTE_SOLES,
						true, true, false, HSSFColor.DARK_BLUE.index);

				int numReg = 13;

				for (SolicitudesTipoServicio tmp : lstSolicitudesTipoServicio) {
					Row row = sheet.createRow((short) numReg);

					// Columna Cod Solicitud en Excel
					Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getCodSolicitud(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Estudio en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getEstudio(), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Tipo de Servicio en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getTipoServicio(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Tipo de Operacion en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTipoOperacion()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Moneda en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getMoneda()), true, false, true,
							HSSFColor.DARK_BLUE.index);

					// Columna Importe en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getImporte(), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Tipo de Cambio en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getTipoCambio(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Importe en Soles en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getImporteSoles(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					numReg++;
				}
			}

			// Arregla ancho de columnas
			int pos = 0;
			for (; pos <= 7; pos++) {
				sheet.autoSizeColumn(pos);
			}

			// Se crea el archivo con la informacion y estilos definidos
			// previamente
			String strRuta = "";
			if (obtenerRutaExcel().compareTo("") != 0) {

				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreTipoServicio());

				strRuta = obtenerRutaExcel() + getNombreTipoServicio()
						+ ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);

				fileOut.close();

				logger.debug("Ruta final donde encontrar el archivo excel: "
						+ strRuta);

				setRutaArchivoExcel(strRuta);
			}

		} catch (Exception e) {
			logger.error(
					"Error al exportar datos a excel del Rpt SolicitudTipoServ",
					e);
			// logger.info("Error al generar el archivo excel debido a: " +
			// e.getStackTrace());
		}
	}

	public String buscarEstudioxAbogado() {
		String codEstudio = "";
		GenericDao<TiivsMiembro, Object> mDAO = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroM = Busqueda.forClass(TiivsMiembro.class);
		filtroM.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_MIEMBRO,
				usuario.getUID()));
		List<TiivsMiembro> result = new ArrayList<TiivsMiembro>();

		try {
			result = mDAO.buscarDinamico(filtroM);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error el estudio asociado al abogado");
		}

		if (result != null) {
			codEstudio = result.get(0).getEstudio();
		}

		return codEstudio;
	}

	private void rptLiquidacion() {
		try {
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb
					.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			// sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.TITULO_REPORTE_RPT_RECAUDACION, 12);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 16));

			// Se crea la leyenda de quien genero el archivo y la hora
			// respectiva
			Row rowG = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG, 16, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 17, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, obtenerGenerador(), true, false,
					true, HSSFColor.DARK_BLUE.index);

			Row rowG1 = sheet.createRow((short) 3);
			Utilitarios.crearCell(wb, rowG1, 16, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 17, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.obtenerFechaHoraActual(), true, false, true,
					HSSFColor.DARK_BLUE.index);

			// Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 5);

			Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_ANIO,
					false, false, false, HSSFColor.DARK_BLUE.index);

			// Busqueda por anio
			int pAnio = 0;

			if (getAnio() != 0) {
				switch (getAnio()) {
				case 1:
					pAnio = 2013;
					break;
				case 2:
					pAnio = 2014;
					break;
				case 3:
					pAnio = 2015;
					break;
				case 4:
					pAnio = 2016;
					break;
				default:
					break;
				}
			}

			Utilitarios.crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, String.valueOf(pAnio), true,
					false, true, HSSFColor.DARK_BLUE.index);

			Utilitarios.crearCell(wb, row2, 7, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_ESTUDIO,
					false, false, false, HSSFColor.DARK_BLUE.index);

			// Busqueda por estudio
			String cadEstudio = "";
			if (lstEstudioSelected.size() > 0) {
				int j = 0;
				int cont = 1;

				for (; j <= lstEstudioSelected.size() - 1; j++) {
					if (lstEstudioSelected.size() > 1) {
						if (cont == lstEstudioSelected.size()) {
							cadEstudio = cadEstudio
									.concat(buscarEstudioxCodigo(lstEstudioSelected
											.get(j).toString()));
						} else {
							cadEstudio = cadEstudio
									.concat(buscarEstudioxCodigo(lstEstudioSelected
											.get(j).toString().concat(",")));
							cont++;
						}
					} else {
						cadEstudio = buscarEstudioxCodigo(lstEstudioSelected
								.get(j).toString());
					}
				}
			}

			Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, cadEstudio, true, false, true,
					HSSFColor.DARK_BLUE.index);
			// sheet.addMergedRegion(new CellRangeAddress(5, 5, 8, 11));

			Row row3 = sheet.createRow((short) 7);
			Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_MES,
					false, false, false, HSSFColor.DARK_BLUE.index);
			// Busqueda por mes
			int mes = 0;

			if (getMes() != 0) {
				mes = getMes();
			}
			Utilitarios.crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.buscarMesxCodigo(mes), true, false, true,
					HSSFColor.DARK_BLUE.index);

			Row rowST = sheet.createRow((short) 9);
			Utilitarios.crearCell(
					wb,
					rowST,
					1,
					CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.buscarMesxCodigo(mes)
							+ ConstantesVisado.ESPACIO_BLANCO
							+ String.valueOf(pAnio), false, false, false,
					HSSFColor.DARK_BLUE.index);

			// Generando la estructura de la tabla de resultados
			if (lstLiquidacion.size() == 0) {
				logger.info("Sin registros para exportar");
			} else {
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 10);

				// Creo las celdas de mi fila, se puede poner un diseño a la
				// celda
				Utilitarios
						.crearCell(
								wb,
								rowT,
								1,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_ESTUDIO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								2,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_PLAZO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								3,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_1,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								4,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_2,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								5,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_3,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								6,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_4,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								7,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_5,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								8,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_6,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								9,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_7,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								10,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_8,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								11,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_9,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								12,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_10,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								13,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_11,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								14,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_12,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								15,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_13,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								16,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_14,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								17,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_15,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								18,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_16,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								19,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_17,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								20,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_18,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								21,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_19,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								22,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_20,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								23,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_21,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								24,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_22,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								25,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_23,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								26,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_24,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								27,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_25,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								28,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_26,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								29,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_27,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								30,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_28,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								31,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_29,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								32,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_30,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								33,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_31,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								34,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_TOTAL_MES,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								35,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_COSTO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								36,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_HONORARIOS,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								37,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_IMPUESTO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								38,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_TOTAL,
								true, true, false, HSSFColor.DARK_BLUE.index);

				int numReg = 11;
				String plazo = "";

				CellStyle estilo = Utilitarios.definirSoloEstiloCelda(wb,
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, true, false, true,
						HSSFColor.DARK_BLUE.index);

				for (AgrupacionPlazoDto tmp : lstLiquidacion) {
					Row row = sheet.createRow((short) numReg);

					// Columna Estudio en Excel
					Utilitarios.SetearEstiloCelda(wb, row, 1, tmp.getEstudio(),
							estilo);

					// Utilitarios.crearCell(wb, row, 0,
					// CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,
					// tmp.getEstudio(),true,
					// false,true,HSSFColor.DARK_BLUE.index);

					if (tmp.getLstSolAT() != null) {
						for (Liquidacion liqAT : tmp.getLstSolAT()) {
							plazo = "A tiempo";
							Utilitarios.SetearEstiloCelda(wb, row, 2, plazo,
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 3,
									String.valueOf(liqAT.getTotalDia1()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 4,
									String.valueOf(liqAT.getTotalDia2()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 5,
									String.valueOf(liqAT.getTotalDia3()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 6,
									String.valueOf(liqAT.getTotalDia4()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 7,
									String.valueOf(liqAT.getTotalDia5()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 8,
									String.valueOf(liqAT.getTotalDia6()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 9,
									String.valueOf(liqAT.getTotalDia7()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 10,
									String.valueOf(liqAT.getTotalDia8()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 11,
									String.valueOf(liqAT.getTotalDia9()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 12,
									String.valueOf(liqAT.getTotalDia10()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 13,
									String.valueOf(liqAT.getTotalDia11()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 14,
									String.valueOf(liqAT.getTotalDia12()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 15,
									String.valueOf(liqAT.getTotalDia13()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 16,
									String.valueOf(liqAT.getTotalDia14()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 17,
									String.valueOf(liqAT.getTotalDia15()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 18,
									String.valueOf(liqAT.getTotalDia16()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 19,
									String.valueOf(liqAT.getTotalDia17()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 20,
									String.valueOf(liqAT.getTotalDia18()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 21,
									String.valueOf(liqAT.getTotalDia19()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 22,
									String.valueOf(liqAT.getTotalDia20()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 23,
									String.valueOf(liqAT.getTotalDia21()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 24,
									String.valueOf(liqAT.getTotalDia22()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 25,
									String.valueOf(liqAT.getTotalDia23()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 26,
									String.valueOf(liqAT.getTotalDia24()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 27,
									String.valueOf(liqAT.getTotalDia25()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 28,
									String.valueOf(liqAT.getTotalDia26()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 29,
									String.valueOf(liqAT.getTotalDia27()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 30,
									String.valueOf(liqAT.getTotalDia28()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 31,
									String.valueOf(liqAT.getTotalDia29()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 32,
									String.valueOf(liqAT.getTotalDia30()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 33,
									String.valueOf(liqAT.getTotalDia31()),
									estilo);
							Utilitarios
									.SetearEstiloCelda(
											wb,
											row,
											34,
											String.valueOf(liqAT.getTotalMes()),
											estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 35,
									String.valueOf(liqAT.getCosto()), estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 36,
									String.valueOf(liqAT.getHonorarios()),
									estilo);
							Utilitarios
									.SetearEstiloCelda(
											wb,
											row,
											37,
											String.valueOf(liqAT.getImpuesto()),
											estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 38,
									String.valueOf(liqAT.getTotal()), estilo);
						}
					}

					if (tmp.getLstSolFT() != null) {
						for (Liquidacion liqFT : tmp.getLstSolFT()) {
							plazo = "Retraso";
							Utilitarios.SetearEstiloCelda(wb, row, 2, plazo,
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 3,
									String.valueOf(liqFT.getTotalDia1()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 4,
									String.valueOf(liqFT.getTotalDia2()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 5,
									String.valueOf(liqFT.getTotalDia3()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 6,
									String.valueOf(liqFT.getTotalDia4()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 7,
									String.valueOf(liqFT.getTotalDia5()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 8,
									String.valueOf(liqFT.getTotalDia6()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 9,
									String.valueOf(liqFT.getTotalDia7()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 10,
									String.valueOf(liqFT.getTotalDia8()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 11,
									String.valueOf(liqFT.getTotalDia9()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 12,
									String.valueOf(liqFT.getTotalDia10()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 13,
									String.valueOf(liqFT.getTotalDia11()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 14,
									String.valueOf(liqFT.getTotalDia12()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 15,
									String.valueOf(liqFT.getTotalDia13()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 16,
									String.valueOf(liqFT.getTotalDia14()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 17,
									String.valueOf(liqFT.getTotalDia15()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 18,
									String.valueOf(liqFT.getTotalDia16()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 19,
									String.valueOf(liqFT.getTotalDia17()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 20,
									String.valueOf(liqFT.getTotalDia18()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 21,
									String.valueOf(liqFT.getTotalDia19()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 22,
									String.valueOf(liqFT.getTotalDia20()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 23,
									String.valueOf(liqFT.getTotalDia21()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 24,
									String.valueOf(liqFT.getTotalDia22()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 25,
									String.valueOf(liqFT.getTotalDia23()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 26,
									String.valueOf(liqFT.getTotalDia24()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 27,
									String.valueOf(liqFT.getTotalDia25()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 28,
									String.valueOf(liqFT.getTotalDia26()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 29,
									String.valueOf(liqFT.getTotalDia27()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 30,
									String.valueOf(liqFT.getTotalDia28()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 31,
									String.valueOf(liqFT.getTotalDia29()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 32,
									String.valueOf(liqFT.getTotalDia30()),
									estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 33,
									String.valueOf(liqFT.getTotalDia31()),
									estilo);
							Utilitarios
									.SetearEstiloCelda(
											wb,
											row,
											34,
											String.valueOf(liqFT.getTotalMes()),
											estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 35,
									String.valueOf(liqFT.getCosto()), estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 36,
									String.valueOf(liqFT.getHonorarios()),
									estilo);
							Utilitarios
									.SetearEstiloCelda(
											wb,
											row,
											37,
											String.valueOf(liqFT.getImpuesto()),
											estilo);
							Utilitarios.SetearEstiloCelda(wb, row, 38,
									String.valueOf(liqFT.getTotal()), estilo);
						}
					}

					numReg++;
				}
			}

			// Arregla ancho de columnas
			int pos = 0;
			for (; pos <= 37; pos++) {
				sheet.autoSizeColumn(pos);
			}

			// Se crea el archivo con la informacion y estilos definidos
			// previamente
			String strRuta = "";
			if (obtenerRutaExcel().compareTo("") != 0) {

				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreLiquidacion());

				strRuta = obtenerRutaExcel() + getNombreLiquidacion()
						+ ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);

				fileOut.close();

				logger.debug("Ruta final donde encontrar el archivo excel: "
						+ strRuta);

				setRutaArchivoExcel(strRuta);
			}

		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Liquidacion",
					e);
			// logger.info("Error al generar el archivo excel debido a: " +
			// e.getStackTrace());
		}
	}

	private void rptLiquidacion_2() {
		try {
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb
					.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			// sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.TITULO_REPORTE_RPT_RECAUDACION, 12);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 16));

			// Se crea la leyenda de quien genero el archivo y la hora
			// respectiva
			Row rowG = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG, 16, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 17, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, obtenerGenerador(), true, false,
					true, HSSFColor.DARK_BLUE.index);

			Row rowG1 = sheet.createRow((short) 3);
			Utilitarios.crearCell(wb, rowG1, 16, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 17, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.obtenerFechaHoraActual(), true, false, true,
					HSSFColor.DARK_BLUE.index);

			// Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 5);

			Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_ANIO,
					false, false, false, HSSFColor.DARK_BLUE.index);

			// Busqueda por anio
			int pAnio = 0;

			if (getAnio() != 0) {
				switch (getAnio()) {
				case 1:
					pAnio = 2013;
					break;
				case 2:
					pAnio = 2014;
					break;
				case 3:
					pAnio = 2015;
					break;
				case 4:
					pAnio = 2016;
					break;
				default:
					break;
				}
			}

			Utilitarios.crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, String.valueOf(pAnio), true,
					false, true, HSSFColor.DARK_BLUE.index);

			Utilitarios.crearCell(wb, row2, 7, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_ESTUDIO,
					false, false, false, HSSFColor.DARK_BLUE.index);

			// Busqueda por estudio
			String cadEstudio = "";
			if (lstEstudioSelected.size() > 0) {
				int j = 0;
				int cont = 1;

				for (; j <= lstEstudioSelected.size() - 1; j++) {
					if (lstEstudioSelected.size() > 1) {
						if (cont == lstEstudioSelected.size()) {
							cadEstudio = cadEstudio
									.concat(buscarEstudioxCodigo(lstEstudioSelected
											.get(j).toString()));
						} else {
							cadEstudio = cadEstudio
									.concat(buscarEstudioxCodigo(lstEstudioSelected
											.get(j).toString().concat(",")));
							cont++;
						}
					} else {
						cadEstudio = buscarEstudioxCodigo(lstEstudioSelected
								.get(j).toString());
					}
				}
			}

			Utilitarios.crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, cadEstudio, true, false, true,
					HSSFColor.DARK_BLUE.index);
			// sheet.addMergedRegion(new CellRangeAddress(5, 5, 8, 11));

			Row row3 = sheet.createRow((short) 7);
			Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_MES,
					false, false, false, HSSFColor.DARK_BLUE.index);
			// Busqueda por mes
			int mes = 0;

			if (getMes() != 0) {
				mes = getMes();
			}
			Utilitarios.crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.buscarMesxCodigo(mes), true, false, true,
					HSSFColor.DARK_BLUE.index);

			Row rowST = sheet.createRow((short) 9);
			Utilitarios.crearCell(
					wb,
					rowST,
					1,
					CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.buscarMesxCodigo(mes)
							+ ConstantesVisado.ESPACIO_BLANCO
							+ String.valueOf(pAnio), false, false, false,
					HSSFColor.DARK_BLUE.index);

			// Generando la estructura de la tabla de resultados
			if (lstLiquidacion.size() == 0) {
				logger.info("Sin registros para exportar");
			} else {
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 10);

				// Creo las celdas de mi fila, se puede poner un diseño a la
				// celda
				Utilitarios
						.crearCell(
								wb,
								rowT,
								1,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_ESTUDIO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								2,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_PLAZO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								3,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_1,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								4,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_2,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								5,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_3,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								6,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_4,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								7,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_5,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								8,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_6,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								9,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_7,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								10,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_8,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								11,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_9,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								12,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_10,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								13,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_11,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								14,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_12,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								15,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_13,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								16,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_14,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								17,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_15,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								18,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_16,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								19,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_17,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								20,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_18,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								21,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_19,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								22,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_20,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								23,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_21,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								24,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_22,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								25,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_23,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								26,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_24,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								27,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_25,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								28,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_26,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								29,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_27,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								30,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_28,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								31,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_29,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								32,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_30,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								33,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_NRO_31,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								34,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_TOTAL_MES,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								35,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_COSTO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								36,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_HONORARIOS,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								37,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_IMPUESTO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								38,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_LIQUIDACION.COLUMNA_TOTAL,
								true, true, false, HSSFColor.DARK_BLUE.index);

				int numReg = 11;
				String plazo = "";

				CellStyle estilo = Utilitarios.definirSoloEstiloCelda(wb,
						HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, true, false, true,
						HSSFColor.DARK_BLUE.index);

				for (AgrupacionPlazoDto tmp : lstLiquidacion) {
					Row row = sheet.createRow((short) numReg);

					// Columna Estudio en Excel
					Utilitarios.SetearEstiloCelda(wb, row, 1, tmp.getEstudio(),
							estilo);

					// Utilitarios.crearCell(wb, row, 0,
					// CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,
					// tmp.getEstudio(),true,
					// false,true,HSSFColor.DARK_BLUE.index);
					Utilitarios.SetearEstiloCelda(wb, row, 2, tmp.getPlazo(),
							estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 3,
							String.valueOf(tmp.getSubTotalDia1()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 4,
							String.valueOf(tmp.getSubTotalDia2()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 5,
							String.valueOf(tmp.getSubTotalDia3()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 6,
							String.valueOf(tmp.getSubTotalDia4()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 7,
							String.valueOf(tmp.getSubTotalDia5()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 8,
							String.valueOf(tmp.getSubTotalDia6()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 9,
							String.valueOf(tmp.getSubTotalDia7()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 10,
							String.valueOf(tmp.getSubTotalDia8()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 11,
							String.valueOf(tmp.getSubTotalDia9()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 12,
							String.valueOf(tmp.getSubTotalDia10()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 13,
							String.valueOf(tmp.getSubTotalDia11()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 14,
							String.valueOf(tmp.getSubTotalDia12()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 15,
							String.valueOf(tmp.getSubTotalDia13()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 16,
							String.valueOf(tmp.getSubTotalDia14()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 17,
							String.valueOf(tmp.getSubTotalDia15()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 18,
							String.valueOf(tmp.getSubTotalDia16()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 19,
							String.valueOf(tmp.getSubTotalDia17()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 20,
							String.valueOf(tmp.getSubTotalDia18()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 21,
							String.valueOf(tmp.getSubTotalDia19()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 22,
							String.valueOf(tmp.getSubTotalDia20()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 23,
							String.valueOf(tmp.getSubTotalDia21()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 24,
							String.valueOf(tmp.getSubTotalDia22()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 25,
							String.valueOf(tmp.getSubTotalDia23()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 26,
							String.valueOf(tmp.getSubTotalDia24()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 27,
							String.valueOf(tmp.getSubTotalDia25()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 28,
							String.valueOf(tmp.getSubTotalDia26()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 29,
							String.valueOf(tmp.getSubTotalDia27()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 30,
							String.valueOf(tmp.getSubTotalDia28()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 31,
							String.valueOf(tmp.getSubTotalDia29()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 32,
							String.valueOf(tmp.getSubTotalDia30()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 33,
							String.valueOf(tmp.getSubTotalDia31()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 34,
							String.valueOf(tmp.getSubTotalMes()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 35,
							String.valueOf(tmp.getCosto()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 36,
							String.valueOf(tmp.getHonorarios()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 37,
							String.valueOf(tmp.getImpuesto()), estilo);
					Utilitarios.SetearEstiloCelda(wb, row, 38,
							String.valueOf(tmp.getgTotal()), estilo);

					/*
					 * if (tmp.getLstSolAT()!=null) { for (Liquidacion liqAT:
					 * tmp.getLstSolAT()) { plazo="A tiempo";
					 * Utilitarios.SetearEstiloCelda(wb,row, 2,plazo,estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 3,String.valueOf(liqAT.getTotalDia1()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 4,String.valueOf(liqAT.getTotalDia2()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 5,String.valueOf(liqAT.getTotalDia3()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 6,String.valueOf(liqAT.getTotalDia4()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 7,String.valueOf(liqAT.getTotalDia5()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 8,String.valueOf(liqAT.getTotalDia6()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 9,String.valueOf(liqAT.getTotalDia7()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 10,String.valueOf(liqAT.getTotalDia8()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 11,String.valueOf(liqAT.getTotalDia9()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 12,String.valueOf(liqAT.getTotalDia10()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 13,String.valueOf(liqAT.getTotalDia11()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 14,String.valueOf(liqAT.getTotalDia12()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 15,String.valueOf(liqAT.getTotalDia13()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 16,String.valueOf(liqAT.getTotalDia14()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 17,String.valueOf(liqAT.getTotalDia15()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 18,String.valueOf(liqAT.getTotalDia16()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 19,String.valueOf(liqAT.getTotalDia17()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 20,String.valueOf(liqAT.getTotalDia18()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 21,String.valueOf(liqAT.getTotalDia19()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 22,String.valueOf(liqAT.getTotalDia20()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 23,String.valueOf(liqAT.getTotalDia21()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 24,String.valueOf(liqAT.getTotalDia22()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 25,String.valueOf(liqAT.getTotalDia23()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 26,String.valueOf(liqAT.getTotalDia24()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 27,String.valueOf(liqAT.getTotalDia25()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 28,String.valueOf(liqAT.getTotalDia26()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 29,String.valueOf(liqAT.getTotalDia27()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 30,String.valueOf(liqAT.getTotalDia28()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 31,String.valueOf(liqAT.getTotalDia29()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 32,String.valueOf(liqAT.getTotalDia30()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 33,String.valueOf(liqAT.getTotalDia31()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 34,String.valueOf(liqAT.getTotalMes()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 35,String.valueOf(liqAT.getCosto()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 36,String.valueOf(liqAT.getHonorarios()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 37,String.valueOf(liqAT.getImpuesto()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 38,String.valueOf(liqAT.getTotal()),estilo); } }
					 * 
					 * if (tmp.getLstSolFT()!=null) { for (Liquidacion liqFT:
					 * tmp.getLstSolFT()) { plazo="Retraso";
					 * Utilitarios.SetearEstiloCelda(wb,row, 2,plazo,estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 3,String.valueOf(liqFT.getTotalDia1()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 4,String.valueOf(liqFT.getTotalDia2()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 5,String.valueOf(liqFT.getTotalDia3()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 6,String.valueOf(liqFT.getTotalDia4()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 7,String.valueOf(liqFT.getTotalDia5()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 8,String.valueOf(liqFT.getTotalDia6()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 9,String.valueOf(liqFT.getTotalDia7()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 10,String.valueOf(liqFT.getTotalDia8()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 11,String.valueOf(liqFT.getTotalDia9()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 12,String.valueOf(liqFT.getTotalDia10()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 13,String.valueOf(liqFT.getTotalDia11()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 14,String.valueOf(liqFT.getTotalDia12()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 15,String.valueOf(liqFT.getTotalDia13()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 16,String.valueOf(liqFT.getTotalDia14()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 17,String.valueOf(liqFT.getTotalDia15()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 18,String.valueOf(liqFT.getTotalDia16()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 19,String.valueOf(liqFT.getTotalDia17()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 20,String.valueOf(liqFT.getTotalDia18()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 21,String.valueOf(liqFT.getTotalDia19()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 22,String.valueOf(liqFT.getTotalDia20()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 23,String.valueOf(liqFT.getTotalDia21()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 24,String.valueOf(liqFT.getTotalDia22()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 25,String.valueOf(liqFT.getTotalDia23()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 26,String.valueOf(liqFT.getTotalDia24()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 27,String.valueOf(liqFT.getTotalDia25()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 28,String.valueOf(liqFT.getTotalDia26()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 29,String.valueOf(liqFT.getTotalDia27()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 30,String.valueOf(liqFT.getTotalDia28()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 31,String.valueOf(liqFT.getTotalDia29()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 32,String.valueOf(liqFT.getTotalDia30()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 33,String.valueOf(liqFT.getTotalDia31()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 34,String.valueOf(liqFT.getTotalMes()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 35,String.valueOf(liqFT.getCosto()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 36,String.valueOf(liqFT.getHonorarios()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 37,String.valueOf(liqFT.getImpuesto()),estilo);
					 * Utilitarios.SetearEstiloCelda(wb,row,
					 * 38,String.valueOf(liqFT.getTotal()),estilo); }
					 */

					numReg++;
				}
			}

			// Arregla ancho de columnas
			int pos = 0;
			for (; pos <= 37; pos++) {
				sheet.autoSizeColumn(pos);
			}

			// Se crea el archivo con la informacion y estilos definidos
			// previamente
			String strRuta = "";
			if (obtenerRutaExcel().compareTo("") != 0) {

				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreLiquidacion());

				strRuta = obtenerRutaExcel() + getNombreLiquidacion()
						+ ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);

				fileOut.close();

				logger.debug("Ruta final donde encontrar el archivo excel: "
						+ strRuta);

				setRutaArchivoExcel(strRuta);
			}

		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Liquidacion",
					e);
			// logger.info("Error al generar el archivo excel debido a: " +
			// e.getStackTrace());
		}
	}

	private void rptRecaudacion() {
		try {
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb
					.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			// sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.TITULO_REPORTE_RPT_RECAUDACION, 12);

			// Se crea la leyenda de quien genero el archivo y la hora
			// respectiva
			Row rowG = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG, 9, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 10, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, obtenerGenerador(), true, false,
					true, HSSFColor.DARK_BLUE.index);

			Row rowG1 = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG1, 9, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 10, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.obtenerFechaHoraActual(), true, false, true,
					HSSFColor.DARK_BLUE.index);

			// Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 4);

			Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, false, false,
					false, HSSFColor.DARK_BLUE.index);

			if (getIdTerr() != null) {
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						buscarNomTerrPorCodigo(getIdTerr()), true, false, true,
						HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row row3 = sheet.createRow((short) 6);

			Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_COD_OFICINA, false,
					false, false, HSSFColor.DARK_BLUE.index);

			if (getIdOfi().compareTo("") != 0) {
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, getIdOfi(), true, false,
						true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_OFICINA, false, false,
					false, HSSFColor.DARK_BLUE.index);
			if (getIdOfi1().compareTo("") != 0) {
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						buscarNomOficinaPorCodigo(getIdOfi1()), true, false,
						true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row row4 = sheet.createRow((short) 8);

			Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaInicio() != null) {
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaInicio()),
						true, false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaFin() != null) {
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaFin()), true,
						false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row rowTot = sheet.createRow((short) 10);
			Utilitarios.crearCell(wb, rowTot, 0, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, "Total de Registros: "
							+ (lstRecaudacionTipoServ.size() - 1), false,
					false, false, HSSFColor.DARK_BLUE.index);

			if (lstRecaudacionTipoServ.size() == 0) {
				logger.info("Sin registros para exportar");
			} else {
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un diseño a la
				// celda
				Utilitarios
						.crearCell(
								wb,
								rowT,
								0,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_TERRITORIO,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								1,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_CODIGO_OFICINA,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								2,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_OFICINA,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								3,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_NATURAL,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								4,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_PN,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								5,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_JURIDICA,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								6,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_PJ,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								7,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_FALLECIDA_MAYOR_X,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								8,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_FALLECIDA_X,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								9,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_PERSONA_FALLECIDA_MENOR_X,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								10,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_FALLECIDA_X1,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								11,
								CellStyle.ALIGN_CENTER,
								CellStyle.VERTICAL_CENTER,
								ConstantesVisado.COLUMNAS_RPT_RECAUDACION.COLUMNA_RECAUDACION_RECAUDACION_TOTAL,
								true, true, false, HSSFColor.DARK_BLUE.index);

				int numReg = 13;

				for (RecaudacionTipoServ tmp : lstRecaudacionTipoServ) {
					Row row = sheet.createRow((short) numReg);

					// Columna Territorio en Excel
					Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getTerritorio(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Cod Oficina en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getCodOficina(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Oficina en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getOficina(), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Persona Natural en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getiContPersonasNaturales()),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getsTotalPersonasNat()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Persona Juridica en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getiContPersonasJuridicas()),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getsTotalPersonasJurd()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Persona Fallecida > X en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getiContPersonasFallecX()),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getsTotalPersonasFallecX()),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Persona Fallecida < X en Excel
					Utilitarios.crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getiContPersonasFallecX1()),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getsTotalPersonasFallecX1()),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Recaudacion en Excel
					Utilitarios.crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getsRecaudacionTotal()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					numReg++;
				}
			}

			// Arregla ancho de columnas
			int pos = 0;
			for (; pos <= 18; pos++) {
				sheet.autoSizeColumn(pos);
			}

			// Se crea el archivo con la informacion y estilos definidos
			// previamente
			String strRuta = "";
			if (obtenerRutaExcel().compareTo("") != 0) {

				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreRecaudacion());

				strRuta = obtenerRutaExcel() + getNombreRecaudacion()
						+ ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);

				fileOut.close();

				logger.debug("Ruta final donde encontrar el archivo excel: "
						+ strRuta);

				setRutaArchivoExcel(strRuta);
			}

		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Recaudacion",
					e);
			// logger.info("Error al generar el archivo excel debido a: " +
			// e.getStackTrace());
		}
	}

	private void rptEstadoSolicitud() {
		try {
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb
					.createSheet(Utilitarios.obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			// sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			Utilitarios.crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.TITULO_REPORTE_RPT_SOLICITUD, 14);

			// Se crea la leyenda de quien genero el archivo y la hora
			// respectiva
			Row rowG = sheet.createRow((short) 1);
			Utilitarios.crearCell(wb, rowG, 9, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 10, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, obtenerGenerador(), true, false,
					true, HSSFColor.DARK_BLUE.index);

			Row rowG1 = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG1, 9, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 10, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					Utilitarios.obtenerFechaHoraActual(), true, false, true,
					HSSFColor.DARK_BLUE.index);

			// Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 4);

			Utilitarios.crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, false, false,
					false, HSSFColor.DARK_BLUE.index);

			if (getIdTerr() != null) {
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						buscarNomTerrPorCodigo(getIdTerr()), true, false, true,
						HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row2, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row row3 = sheet.createRow((short) 6);

			Utilitarios.crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_COD_OFICINA, false,
					false, false, HSSFColor.DARK_BLUE.index);

			if (getIdOfi().compareTo("") != 0) {
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, getIdOfi(), true, false,
						true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row3, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Utilitarios.crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_OFICINA, false, false,
					false, HSSFColor.DARK_BLUE.index);
			if (getIdOfi1().compareTo("") != 0) {
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						buscarNomOficinaPorCodigo(getIdOfi1()), true, false,
						true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row3, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row row4 = sheet.createRow((short) 8);

			Utilitarios.crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaInicio() != null) {
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaInicio()),
						true, false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row4, 3, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Utilitarios.crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaFin() != null) {
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaFin()), true,
						false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, row4, 6, CellStyle.ALIGN_LEFT,
						CellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row rowTot = sheet.createRow((short) 10);
			Utilitarios.crearCell(wb, rowTot, 0, CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, "Total de Registros: "
							+ (lstSolicitudesOficina.size() - 1), false, false,
					false, HSSFColor.DARK_BLUE.index);

			if (lstSolicitudesOficina.size() == 0) {
				logger.info("Sin registros para exportar");
			} else {
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un diseño a la
				// celda
				Utilitarios.crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ETIQUETA_COLUMNA_COD_OFICINA, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ETIQUETA_COLUMNA_OFICINA, true, true,
						false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_REGISTRADO_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_ENVIADOSSJJ_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_RESERVADO_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_ACEPTADO_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_EN_VERIFICACION_A_T02,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 8, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_RECHAZADO_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 9, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_EN_REVISION_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 10, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_PRE_EJECUTADO_T02,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 11, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_EJECUTADO_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 12, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_PROCEDENTE_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 13, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_EN_VERIFICACION_B_T02,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 14, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_IMPROCEDENTE_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 15, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_VENCIDO_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 16, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.ESTADOS.ESTADO_REVOCADO_T02, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 17, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_SOLICITUD_COLUMNA_TOTAL, true,
						true, false, HSSFColor.DARK_BLUE.index);

				int numReg = 13;

				for (SolicitudesOficina tmp : lstSolicitudesOficina) {
					Row row = sheet.createRow((short) numReg);

					// Columna Territorio en Excel
					Utilitarios.crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getTerritorio(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Cod Oficina en Excel
					Utilitarios.crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getCodOficina(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Oficina en Excel
					Utilitarios.crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER, tmp.getOficina(), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Registrado en Excel
					Utilitarios.crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalRegistrados()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Enviado en Excel
					Utilitarios.crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalEnviados()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Reservado en Excel
					Utilitarios.crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalReservado()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Aceptado en Excel
					Utilitarios.crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalAceptado()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna En Verificacion A en Excel
					Utilitarios.crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalVerificacionA()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Rechazado en Excel
					Utilitarios.crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalRechazado()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna En Revision en Excel
					Utilitarios.crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalEnRevision()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Pre-Ejecutado en Excel
					Utilitarios.crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalPreEjecutado()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Ejecutado en Excel
					Utilitarios.crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalEjecutado()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Procedente en Excel
					Utilitarios.crearCell(wb, row, 12, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalProcedente()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna En Verificacion B en Excel
					Utilitarios.crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalVerificacionB()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Improcedente en Excel
					Utilitarios.crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalImprocedente()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Vencido en Excel
					Utilitarios.crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalVencido()), true, false,
							true, HSSFColor.DARK_BLUE.index);

					// Columna Revocado en Excel
					Utilitarios.crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalRevocado()), true,
							false, true, HSSFColor.DARK_BLUE.index);

					// Columna Total en Excel
					Utilitarios.crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,
							CellStyle.VERTICAL_CENTER,
							String.valueOf(tmp.getTotalFila()), true, false,
							true, HSSFColor.DARK_BLUE.index);

					numReg++;
				}
			}

			// Arregla ancho de columnas
			int pos = 0;
			for (; pos <= 18; pos++) {
				sheet.autoSizeColumn(pos);
			}

			// Se crea el archivo con la informacion y estilos definidos
			// previamente
			// String strRuta="";
			if (obtenerRutaExcel().compareTo("") != 0) {

				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: "
						+ getNombreEstadoSolicitud());

				/*
				 * strRuta = obtenerRutaExcel() + getNombreEstadoSolicitud() +
				 * ConstantesVisado.EXTENSION_XLS;
				 * logger.info("Nombre strRuta: " + strRuta); FileOutputStream
				 * fileOut = new FileOutputStream(strRuta); wb.write(fileOut);
				 * 
				 * fileOut.close();
				 * 
				 * logger.debug("Ruta final donde encontrar el archivo excel: "
				 * + strRuta);
				 * 
				 * setRutaArchivoExcel(strRuta);
				 */

				try {
					FacesContext context = FacesContext.getCurrentInstance();
					ExternalContext externalContext = context
							.getExternalContext();
					externalContext.responseReset();
					externalContext
							.setResponseContentType("application/vnd.ms-excel");
					externalContext.setResponseHeader("Content-Disposition",
							"attachment;filename=" + getNombreEstadoSolicitud()
									+ ConstantesVisado.EXTENSION_XLS);
					wb.write(externalContext.getResponseOutputStream());
					context.responseComplete(); // Prevent JSF from performing
												// navigation.

					OutputStream os = externalContext.getResponseOutputStream();
					os.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// FacesContext context = FacesContext.getCurrentInstance();
				// writeExcelToResponse(((HttpServletResponse)context.getExternalContext().getResponse()),
				// wb, getNombreEstadoSolicitud());
			}

		} catch (Exception e) {
			logger.error(
					"Error al exportar datos a excel del Rpt Estado de Solicitud",
					e);
			// logger.info("Error al generar el archivo excel debido a: " +
			// e.getStackTrace());
		}
	}

	protected void writeExcelToResponse(HttpServletResponse response,
			HSSFWorkbook generatedExcel, String filename) throws IOException {
		response.reset();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment;filename="
				+ filename + ".xls");

		OutputStream os = response.getOutputStream();
		generatedExcel.write(os);

		os.flush();
	}

	private void rptExtractor() {
		try {
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb.createSheet(ConstantesVisado.RPT_NOMBRE_HOJA1);

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			// sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// Se crea la leyenda de quien genero el archivo y la hora
			// respectiva
			Row rowG = sheet.createRow((short) 2);
			Utilitarios.crearCell(wb, rowG, 9, HSSFCellStyle.ALIGN_LEFT,
					HSSFCellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG, 10, HSSFCellStyle.ALIGN_LEFT,
					HSSFCellStyle.VERTICAL_CENTER, obtenerGenerador(), true,
					false, true, HSSFColor.DARK_BLUE.index);

			Row rowG1 = sheet.createRow((short) 3);
			Utilitarios.crearCell(wb, rowG1, 9, HSSFCellStyle.ALIGN_LEFT,
					HSSFCellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false,
					false, false, HSSFColor.DARK_BLUE.index);
			Utilitarios.crearCell(wb, rowG1, 10, HSSFCellStyle.ALIGN_LEFT,
					HSSFCellStyle.VERTICAL_CENTER,
					Utilitarios.obtenerFechaHoraActual(), true, false, true,
					HSSFColor.DARK_BLUE.index);

			// Genera celdas con los filtros de busqueda
			// Row rowFI = sheet.createRow((short) 2);

			Utilitarios.crearCell(wb, rowG, 1, HSSFCellStyle.ALIGN_LEFT,
					HSSFCellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaInicio() != null) {
				Utilitarios.crearCell(wb, rowG, 2, HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaInicio()),
						true, false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, rowG, 2, HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			// Row rowFF = sheet.createRow((short) 2);

			Utilitarios.crearCell(wb, rowG, 5, HSSFCellStyle.ALIGN_LEFT,
					HSSFCellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false,
					false, false, HSSFColor.DARK_BLUE.index);
			if (getFechaFin() != null) {
				Utilitarios.crearCell(wb, rowG, 6, HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER,
						Utilitarios.formatoFechaSinHora(getFechaFin()), true,
						false, true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, rowG, 6, HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			Row rowEs = sheet.createRow((short) 4);

			Utilitarios.crearCell(wb, rowEs, 1, HSSFCellStyle.ALIGN_LEFT,
					HSSFCellStyle.VERTICAL_CENTER,
					ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO, false, false,
					false, HSSFColor.DARK_BLUE.index);
			if (lstEstadoSelected != null) {
				/*
				 * String cadena = ""; int ind=0;
				 * 
				 * for (; ind <= lstEstadoSelected.size() - 1; ind++) { cadena+=
				 * buscarEstadoxCodigo(lstEstadoSelected.get(ind))+","; }
				 */
				String cadena = "";
				int j = 0;
				int cont = 1;

				for (; j <= lstEstadoSelected.size() - 1; j++) {
					if (lstEstadoSelected.size() > 1) {
						if (cont == lstEstadoSelected.size()) {
							cadena = cadena
									.concat(buscarEstadoxCodigo(lstEstadoSelected
											.get(j).toString()));
						} else {
							cadena = cadena
									.concat(buscarEstadoxCodigo(lstEstadoSelected
											.get(j).toString().concat(",")));
							cont++;
						}
					} else {
						cadena = buscarEstadoxCodigo(lstEstadoSelected.get(j))
								.toString();
					}
				}

				Utilitarios.crearCell(wb, rowEs, 2, HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, cadena, true, false,
						true, HSSFColor.DARK_BLUE.index);
			} else {
				Utilitarios.crearCell(wb, rowEs, 2, HSSFCellStyle.ALIGN_LEFT,
						HSSFCellStyle.VERTICAL_CENTER, "", true, false, true,
						HSSFColor.DARK_BLUE.index);
			}

			// Generando la estructura de las solicitudes
			if (solicitudes.size() == 0) {
				logger.info("Sin registros para exportar");
			} else {
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 7);

				// Creo las celdas de mi fila, se puede poner un diseño a la
				// celda
				Utilitarios.crearCell(wb, rowT, 0,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ITEM, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_CODIGO, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ESTADO, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_SOL,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								4,
								HSSFCellStyle.VERTICAL_CENTER,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_COMISION,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_COD_OFICINA,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_OFICINA,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 7,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_MONEDA, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 8,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_IMPORTE,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 9,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NRO_VOUCHER,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 10,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TERRITORIO,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 11,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_GRUPO, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 12,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_COD_CENTRAL,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 13,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NOMBRES,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 14,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_DOI,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 15,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NRO_DOI,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 16,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_CELULAR,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 17,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_FIJO, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								18,
								HSSFCellStyle.VERTICAL_CENTER,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_CLASIFICACION,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios
						.crearCell(
								wb,
								rowT,
								19,
								HSSFCellStyle.VERTICAL_CENTER,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_PARTICIPACION,
								true, true, false, HSSFColor.DARK_BLUE.index);

				Utilitarios
						.crearCell(
								wb,
								rowT,
								20,
								HSSFCellStyle.VERTICAL_CENTER,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_TIPO_COMISION,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 21,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ESTUDIO,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 22,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_RECLAMO,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 23,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NIVELES,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 24,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_DELEGACION,
						true, true, false, HSSFColor.DARK_BLUE.index);

				int numReg = 8;
				int contador = 0;
				for (TiivsSolicitud tmp : solicitudes) {
					contador++;
					// Columna Item en Excel
					Row row = sheet.createRow((short) numReg);
					if (contador <= 9) {
						Utilitarios.crearCell(wb, row, 0,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.TRES_CEROS + contador, true,
								false, true, HSSFColor.DARK_BLUE.index);
					} else if (contador <= 99 && contador > 9) {
						Utilitarios.crearCell(wb, row, 0,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.DOS_CEROS + contador, true,
								false, true, HSSFColor.DARK_BLUE.index);
					} else if (contador >= 99 && contador < 999) {
						Utilitarios.crearCell(wb, row, 0,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.CERO + contador, true, false,
								true, HSSFColor.DARK_BLUE.index);
					} else {
						Utilitarios.crearCell(wb, row, 0,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER,
								String.valueOf(contador), true, false, true,
								HSSFColor.DARK_BLUE.index);
					}

					// Columna Codigo en Excel
					Utilitarios.crearCell(wb, row, 1, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, tmp.getCodSoli(),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Estado en Excel
					Utilitarios.crearCell(wb, row, 2, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER,
							buscarEstadoxCodigo(tmp.getEstado()), true, false,
							true, HSSFColor.DARK_BLUE.index);

					// Columna Tipo Solicitud en Excel
					Utilitarios.crearCell(wb, row, 3, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, Utilitarios
									.validarCampoNull(tmp
											.getTiivsTipoSolicitud()
											.getDesTipServicio()), true, false,
							true, HSSFColor.DARK_BLUE.index);

					// Columna Tipo Comision en Excel
					Utilitarios.crearCell(wb, row, 4, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, "", true, false,
							true, HSSFColor.DARK_BLUE.index);

					// Columna Cod Oficina en Excel
					Utilitarios.crearCell(wb, row, 5, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, Utilitarios
									.validarCampoNull(tmp.getTiivsOficina1()
											.getCodOfi()), true, false, true,
							HSSFColor.DARK_BLUE.index);

					// Columna Oficina en Excel
					Utilitarios.crearCell(wb, row, 6, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, Utilitarios
									.validarCampoNull(tmp.getTiivsOficina1()
											.getDesOfi()), true, false, true,
							HSSFColor.DARK_BLUE.index);

					// Columna Moneda en Excel
					Utilitarios.crearCell(wb, row, 7, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER,
							buscarAbrevMoneda(Utilitarios.validarCampoNull(tmp
									.getMoneda())), true, false, true,
							HSSFColor.DARK_BLUE.index);

					// Columna Importe en Excel
					Utilitarios.crearCell(wb, row, 8, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, Utilitarios
									.validarCampoNull(tmp.getImporte()
											.toString()), true, false, true,
							HSSFColor.DARK_BLUE.index);

					// Columna Nro Voucher en Excel
					Utilitarios.crearCell(wb, row, 9, HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER,
							Utilitarios.validarCampoNull(tmp.getNroVoucher()),
							true, false, true, HSSFColor.DARK_BLUE.index);

					// Columna Territorio en Excel
					Utilitarios
							.crearCell(wb, row, 10, HSSFCellStyle.ALIGN_LEFT,
									HSSFCellStyle.VERTICAL_CENTER,
									buscarDesTerritorio(Utilitarios
											.validarCampoNull(tmp
													.getTiivsOficina1()
													.getTiivsTerritorio()
													.getCodTer())), true,
									false, true, HSSFColor.DARK_BLUE.index);

					int fila = row.getRowNum();
					int filaTmp = row.getRowNum();
					List<AgrupacionSimpleDto> tmpListaAgrupaciones = buscarAgrupacionesxSolicitud(tmp
							.getCodSoli());

					CellStyle estilo = Utilitarios.definirSoloEstiloCelda(wb,
							HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, true, false, true,
							HSSFColor.DARK_BLUE.index);

					for (AgrupacionSimpleDto tmpAgrup : tmpListaAgrupaciones) {
						if (tmpAgrup.getId().getCodSoli()
								.equals(tmp.getCodSoli())) {
							Utilitarios.SetearEstiloCelda(wb, row, 11, String
									.valueOf(tmpAgrup.getId().getNumGrupo()),
									estilo);

							for (TiivsPersona tmpPersonaPod : tmpAgrup
									.getLstPoderdantes()) {
								// Columna Cod Central en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 12,
										tmpPersonaPod.getCodCen(), estilo);

								// Columna Poderdante en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 13,
										tmpPersonaPod.getNombre(), estilo);

								// Columna Tipo DOI en Excel
								Utilitarios
										.SetearEstiloCelda(
												wb,
												row,
												14,
												obtenerDescripcionDocumentos(tmpPersonaPod
														.getTipDoi().trim()),
												estilo);

								// Columna Nro DOI en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 15,
										tmpPersonaPod.getNumDoi(), estilo);
								// Utilitarios.crearCell(wb, row, 15,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// tmpPersonaPod.getNumDoi(), true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Celular en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 16,
										tmpPersonaPod.getNumCel(), estilo);
								// Utilitarios.crearCell(wb, row, 16,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// tmpPersonaPod.getNumCel(), true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Nro Fijo en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 17, "",
										estilo);
								// Utilitarios.crearCell(wb, row, 17,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// "", true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Clasificacion en Excel
								Utilitarios
										.SetearEstiloCelda(
												wb,
												row,
												18,
												ConstantesVisado.ETIQUETA_COLUMNA_PODERDANTE,
												estilo);
								// Utilitarios.crearCell(wb, row, 18,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// ConstantesVisado.ETIQUETA_COLUMNA_PODERDANTE,
								// true, false,true,HSSFColor.DARK_BLUE.index);

								// Columna Particicacion en Excel
								Utilitarios
										.SetearEstiloCelda(
												wb,
												row,
												19,
												obtenerDescripcionClasificacion(tmpPersonaPod
														.getTipPartic().trim()),
												estilo);
								// Utilitarios.crearCell(wb, row, 19,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// obtenerDescripcionClasificacion(tmpPersonaPod.getTipPartic().trim()),
								// true, false,true,HSSFColor.DARK_BLUE.index);

								filaTmp++;

								row.setRowNum(filaTmp);
							}

							for (TiivsPersona tmpPersonaApod : tmpAgrup
									.getLstApoderdantes()) {
								// Columna Cod Central en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 12,
										tmpPersonaApod.getCodCen(), estilo);
								// Utilitarios.crearCell(wb, row, 12,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// tmpPersonaApod.getCodCen(), true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Poderdante en Excel
								Utilitarios.SetearEstiloCelda(
										wb,
										row,
										13,
										tmpPersonaApod.getNombre() + " "
												+ tmpPersonaApod.getApePat()
												+ " "
												+ tmpPersonaApod.getApeMat(),
										estilo);
								// Utilitarios.crearCell(wb, row, 13,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// tmpPersonaApod.getNombre() + " " +
								// tmpPersonaApod.getApePat() + " " +
								// tmpPersonaApod.getApeMat(), true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Tipo DOI en Excel
								Utilitarios
										.SetearEstiloCelda(
												wb,
												row,
												14,
												obtenerDescripcionDocumentos(tmpPersonaApod
														.getTipDoi().trim()),
												estilo);
								// Utilitarios.crearCell(wb, row, 14,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// obtenerDescripcionDocumentos(tmpPersonaApod.getTipDoi().trim()),
								// true, false,true,HSSFColor.DARK_BLUE.index);

								// Columna Nro DOI en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 15,
										tmpPersonaApod.getNumDoi(), estilo);
								// Utilitarios.crearCell(wb, row, 15,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// tmpPersonaApod.getNumDoi(), true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Celular en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 16,
										tmpPersonaApod.getNumCel(), estilo);
								// Utilitarios.crearCell(wb, row, 16,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// tmpPersonaApod.getNumCel(), true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Nro Fijo en Excel
								Utilitarios.SetearEstiloCelda(wb, row, 17, "",
										estilo);
								// Utilitarios.crearCell(wb, row, 17,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// "", true,
								// false,true,HSSFColor.DARK_BLUE.index);

								// Columna Clasificacion en Excel
								Utilitarios
										.SetearEstiloCelda(
												wb,
												row,
												18,
												ConstantesVisado.ETIQUETA_COLUMNA_APODERADO,
												estilo);
								// Utilitarios.crearCell(wb, row, 18,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// ConstantesVisado.ETIQUETA_COLUMNA_APODERADO,
								// true, false,true,HSSFColor.DARK_BLUE.index);

								// Columna Particicacion en Excel
								Utilitarios
										.SetearEstiloCelda(
												wb,
												row,
												19,
												obtenerDescripcionClasificacion(tmpPersonaApod
														.getTipPartic().trim()),
												estilo);
								// Utilitarios.crearCell(wb, row, 19,
								// HSSFCellStyle.ALIGN_LEFT,HSSFCellStyle.VERTICAL_CENTER,
								// obtenerDescripcionClasificacion(tmpPersonaApod.getTipPartic().trim()),
								// true, false,true,HSSFColor.DARK_BLUE.index);

								filaTmp++;

								row.setRowNum(filaTmp);
							}
						}
					}

					row.setRowNum(fila);

					// Columna Tipo Comision en Excel
					Utilitarios.crearCell(wb, row, 20,
							HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, "", true, false,
							true, HSSFColor.DARK_BLUE.index);

					// Columna Estudio en Excel
					if (tmp.getTiivsEstudio() != null) {
						Utilitarios.crearCell(wb, row, 21,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER, Utilitarios
										.validarCampoNull(tmp.getTiivsEstudio()
												.getDesEstudio()), true, false,
								true, HSSFColor.DARK_BLUE.index);
					} else {
						Utilitarios.crearCell(wb, row, 21,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER,
								Utilitarios.validarCampoNull(null), true,
								false, true, HSSFColor.DARK_BLUE.index);
					}

					// Columna Reclamo en Excel
					Utilitarios.crearCell(wb, row, 22,
							HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, "", true, false,
							true, HSSFColor.DARK_BLUE.index);

					// Columna Niveles en Excel
					Utilitarios.crearCell(wb, row, 23,
							HSSFCellStyle.ALIGN_LEFT,
							HSSFCellStyle.VERTICAL_CENTER, "", true, false,
							true, HSSFColor.DARK_BLUE.index);

					// Columna Delegado
					if (validarSolicitudConDelegacion(tmp.getCodSoli())) {
						Utilitarios.crearCell(wb, row, 24,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER, "Si", true,
								false, true, HSSFColor.DARK_BLUE.index);
					} else {
						Utilitarios.crearCell(wb, row, 24,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER, "No", true,
								false, true, HSSFColor.DARK_BLUE.index);
					}

					numReg++;
				}
			}

			// Arregla ancho de columnas
			int pos = 0;
			for (; pos <= 24; pos++) {
				sheet.autoSizeColumn(pos);
			}

			// Creacion de hoja para el historial de la solicitud
			Sheet sheet2 = wb.createSheet(ConstantesVisado.RPT_NOMBRE_HOJA2);

			// quito las lineas del libro para darle un mejor acabado
			sheet2.setDisplayGridlines(false);

			// Generando la estructura de las solicitudes
			if (solicitudes.size() == 0) {
				logger.info("Sin registros para exportar");
			} else {
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet2.createRow((short) 1);

				// Creo las celdas de mi fila, se puede poner un diseño a la
				// celda
				Utilitarios
						.crearCell(
								wb,
								rowT,
								0,
								HSSFCellStyle.VERTICAL_CENTER,
								HSSFCellStyle.VERTICAL_CENTER,
								ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NRO_SOLICITUD,
								true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 1,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_ESTADO_HOJA2,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 2,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_NIVEL, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 3,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_DELEGADO,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 4,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_FECHA, true,
						true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 5,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_USUARIO,
						true, true, false, HSSFColor.DARK_BLUE.index);
				Utilitarios.crearCell(wb, rowT, 6,
						HSSFCellStyle.VERTICAL_CENTER,
						HSSFCellStyle.VERTICAL_CENTER,
						ConstantesVisado.RPT_EXT_ETIQUETA_COLUMNA_OBS, true,
						true, false, HSSFColor.DARK_BLUE.index);

				int numReg = 2;
				for (TiivsSolicitud tmp2 : solicitudes) {
					Row row = sheet2.createRow((short) numReg);

					obtenerHistorialSolicitud(tmp2.getCodSoli());

					for (SeguimientoDTO seg : lstSeguimientoDTO) {
						// Columna Nro Solicitud en Excel
						Utilitarios.crearCell(wb, row, 0,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER,
								tmp2.getCodSoli(), true, false, true,
								HSSFColor.DARK_BLUE.index);

						// Columna Estado en Excel
						Utilitarios.crearCell(wb, row, 1,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER, seg.getEstado(),
								true, false, true, HSSFColor.DARK_BLUE.index);

						// Columna Nivel en Excel
						Utilitarios.crearCell(wb, row, 2,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER, seg.getNivel(),
								true, false, true, HSSFColor.DARK_BLUE.index);

						// Columna Delegado en Excel
						if (validarSolicitudConDelegacion(tmp2.getCodSoli())) {
							Utilitarios.crearCell(wb, row, 3,
									HSSFCellStyle.ALIGN_LEFT,
									HSSFCellStyle.VERTICAL_CENTER, "Si", true,
									false, true, HSSFColor.DARK_BLUE.index);
						} else {
							Utilitarios.crearCell(wb, row, 3,
									HSSFCellStyle.ALIGN_LEFT,
									HSSFCellStyle.VERTICAL_CENTER, "No", true,
									false, true, HSSFColor.DARK_BLUE.index);
						}

						// Columna Fecha en Excel
						Utilitarios
								.crearCell(wb, row, 4,
										HSSFCellStyle.ALIGN_LEFT,
										HSSFCellStyle.VERTICAL_CENTER,
										Utilitarios.formatoFechaSinHora(seg
												.getFecha()), true, false,
										true, HSSFColor.DARK_BLUE.index);

						// Columna Usuario en Excel
						Utilitarios.crearCell(wb, row, 5,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER,
								seg.getUsuario(), true, false, true,
								HSSFColor.DARK_BLUE.index);

						// Columna Observaciones en Excel
						Utilitarios.crearCell(wb, row, 6,
								HSSFCellStyle.ALIGN_LEFT,
								HSSFCellStyle.VERTICAL_CENTER, seg.getObs(),
								true, false, true, HSSFColor.DARK_BLUE.index);
					}
					numReg++;
				}

				// Arregla ancho de columnas
				int posHoj2 = 0;
				for (; posHoj2 <= 24; posHoj2++) {
					sheet2.autoSizeColumn(posHoj2);
				}
			}

			// Se crea el archivo con la informacion y estilos definidos
			// previamente
			String strRuta = "";
			if (obtenerRutaExcel().compareTo("") != 0) {

				logger.info("Parametros recogidos para exportar");
				logger.info("Ruta: " + obtenerRutaExcel());
				logger.info("Nombre Archivo Excel: " + getNombreExtractor());

				strRuta = obtenerRutaExcel() + getNombreExtractor()
						+ ConstantesVisado.EXTENSION_XLS;
				logger.info("Nombre strRuta: " + strRuta);
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);

				fileOut.close();

				logger.debug("Ruta final donde encontrar el archivo excel: "
						+ strRuta);

				setRutaArchivoExcel(strRuta);
			}

		} catch (Exception e) {
			logger.error("Error al exportar datos a excel del Rpt Extractor", e);
			// logger.info("Error al generar el archivo excel debido a: " +
			// e.getStackTrace());
		}
	}

	public List<AgrupacionSimpleDto> buscarAgrupacionesxSolicitud(String codSoli) {
		List<AgrupacionSimpleDto> tmpLista = new ArrayList<AgrupacionSimpleDto>();

		for (AgrupacionSimpleDto tmpAgrup : lstAgrupacionSimpleDto) {
			if (tmpAgrup.getId().getCodSoli().equals(codSoli)) {
				tmpLista.add(tmpAgrup);
			}
		}

		logger.info("Agrupaciones encontradas de la solicitud: " + codSoli
				+ ": " + tmpLista.size());

		return tmpLista;
	}

	public String buscarAbrevMoneda(String codigo) {
		int i = 0;
		String descripcion = "";

		for (; i <= combosMB.getLstMoneda().size() - 1; i++) {
			if (combosMB.getLstMoneda().get(i).getCodMoneda()
					.equalsIgnoreCase(codigo)) {
				if (combosMB
						.getLstMoneda()
						.get(i)
						.getDesMoneda()
						.equalsIgnoreCase(
								ConstantesVisado.CAMPO_SOLES_TBL_MONEDA)) {
					descripcion = ConstantesVisado.CAMPO_ABREV_SOLES;
				} else if (combosMB
						.getLstMoneda()
						.get(i)
						.getDesMoneda()
						.equalsIgnoreCase(
								ConstantesVisado.CAMPO_DOLARES_TBL_MONEDA)) {
					descripcion = ConstantesVisado.CAMPO_ABREV_DOLARES;
				} else if (combosMB
						.getLstMoneda()
						.get(i)
						.getDesMoneda()
						.equalsIgnoreCase(
								ConstantesVisado.CAMPO_EUROS_TBL_MONEDA)) {
					descripcion = ConstantesVisado.CAMPO_ABREV_EUROS;
				} else {
					descripcion = combosMB.getLstMoneda().get(i).getDesMoneda();
				}
				break;
			}
		}

		return descripcion;
	}

	public void abrirExcelEstadoSolicitud() {
		exportarExcelEstadoSolicitud();
	}

	public void abrirExcelLiquidacion() {
		try {
			exportarExcelLiquidacion();
			// Abrir archivo excel

			if (rutaArchivoExcel != null && rutaArchivoExcel.length() > 0) {
				Desktop.getDesktop().open(new File(rutaArchivoExcel));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("Error al abrir archivo excel debido a: "
					+ e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void abrirExcelRecaudacion() {
		try {
			exportarExcelRecaudacion();
			// Abrir archivo excel

			if (rutaArchivoExcel != null && rutaArchivoExcel.length() > 0) {
				Desktop.getDesktop().open(new File(rutaArchivoExcel));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("Error al abrir archivo excel debido a: "
					+ e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void abrirExcelSolicitudesxTpoServ() {
		try {
			exportarExcelSolicitudTipoServ();
			// Abrir archivo excel

			if (rutaArchivoExcel != null && rutaArchivoExcel.length() > 0) {
				Desktop.getDesktop().open(new File(rutaArchivoExcel));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("Error al abrir archivo excel debido a: "
					+ e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void abrirExcelExtractor() {
		try {
			exportarExcelExtractor();
			// Abrir archivo excel

			if (rutaArchivoExcel != null && rutaArchivoExcel.length() > 0) {
				Desktop.getDesktop().open(new File(rutaArchivoExcel));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("Error al abrir archivo excel debido a: "
					+ e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void descargarArchivoExtractor() {
		buscarSolicitudesExtractor();
		exportarExcelExtractor();
		InputStream stream = null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: "
					+ e.getMessage());
		}

		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/excel",
					nombreExtractor + ConstantesVisado.EXTENSION_XLS);
		}
	}

	public void descargarArchivoEstadoSolicitud() {
		exportarExcelEstadoSolicitud();
		InputStream stream = null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: "
					+ e.getMessage());
		}

		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/excel",
					nombreEstadoSolicitud + ConstantesVisado.EXTENSION_XLS);
		}
	}

	public void descargarArchivoLiquidacion() {
		logger.debug("descargarArchivoLiquidacion");
		exportarExcelLiquidacion();
		InputStream stream = null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.error("Error al obtener archivo excel debido a: "
					+ e.getMessage());
		}

		if (stream != null) {
			logger.debug("if (stream != null) {");
			try {
				file = new DefaultStreamedContent(stream, "application/excel",
						nombreLiquidacion + ConstantesVisado.EXTENSION_XLS);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

	public void descargarArchivoSolicitudesxTpoServ() {
		exportarExcelSolicitudTipoServ();
		InputStream stream = null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.debug("Error al obtener archivo excel debido a: "
					+ e.getMessage());
		}

		if (stream != null) {
			file = new DefaultStreamedContent(stream, "application/excel",
					nombreTipoServicio + ConstantesVisado.EXTENSION_XLS);
		}
	}

	public void descargarArchivoRecaudacion() {
		exportarExcelRecaudacion();
		InputStream stream = null;
		try {
			stream = new FileInputStream(rutaArchivoExcel);
		} catch (FileNotFoundException e) {
			logger.error("Error al obtener archivo excel debido a: "
					+ e.getMessage());
		}

		if (stream != null) {
			logger.debug("if (stream != null) {");
			file = new DefaultStreamedContent(stream, "application/excel",
					nombreRecaudacion + ConstantesVisado.EXTENSION_XLS);
		}
	}

	public String obtenerRutaExcel() {
		String res = "";

		for (TiivsParametros tmp : pdfViewerMB.getLstParametros()) {
			if (usuario.getUID().equals(tmp.getCodUsuario())) {
				res = tmp.getRutaArchivoExcel();
				break;
			}
		}

		if (res.compareTo("") == 0) {
			logger.debug("No se encontro el parametro de ruta para exportar excel para el usuario: "
					+ usuario.getUID());
		} else {
			logger.debug("Parametro ruta encontrada para el usuario: "
					+ usuario.getUID() + " es: " + res);
		}

		return res;
	}

	public Boolean validarSolicitudConDelegacion(String codSoli) {
		boolean bEncontrado = false;

		/*
		 * String codigoSolicVerA =
		 * buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A);
		 * String codigoSolicVerB =
		 * buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_B);
		 * 
		 * GenericDao<TiivsHistSolicitud, Object> busqHisDAO =
		 * (GenericDao<TiivsHistSolicitud, Object>)
		 * SpringInit.getApplicationContext().getBean("genericoDao"); Busqueda
		 * filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		 * filtro.add(Restrictions
		 * .or(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
		 * codigoSolicVerA),Restrictions
		 * .eq(ConstantesVisado.CAMPO_ESTADO,codigoSolicVerB)));
		 * 
		 * try { lstHistorial = busqHisDAO.buscarDinamico(filtro); } catch
		 * (Exception e) {
		 * logger.debug("Error al buscar en historial de solicitudes"); }
		 * 
		 * //lstSolicitudesSelected.clear(); if (lstHistorial.size() > 0) { //
		 * Colocar aqui la logica para filtrar los niveles aprobados o
		 * rechazados GenericDao<TiivsSolicitudNivel, Object> busqSolNivDAO =
		 * (GenericDao<TiivsSolicitudNivel, Object>)
		 * SpringInit.getApplicationContext().getBean("genericoDao"); Busqueda
		 * filtro2 = Busqueda.forClass(TiivsSolicitudNivel.class);
		 * List<TiivsSolicitudNivel> lstSolNivel = new
		 * ArrayList<TiivsSolicitudNivel>();
		 * 
		 * try { lstSolNivel = busqSolNivDAO.buscarDinamico(filtro2); } catch
		 * (Exception e) { logger.debug(
		 * "Error al buscar los estados de los niveles en las solicitudes"); }
		 * 
		 * for (TiivsSolicitudNivel tmp: lstSolNivel) { for (TiivsHistSolicitud
		 * hist: lstHistorial) { if
		 * (tmp.getTiivsSolicitud().getCodSoli().equals(
		 * hist.getId().getCodSoli())) {
		 * lstSolicitudesSelected.add(hist.getId().getCodSoli()); } } } }
		 * 
		 * int ind=0;
		 * 
		 * for (;ind<=lstSolicitudesSelected.size()-1;ind++) { if
		 * (lstSolicitudesSelected.get(ind).equals(codSoli)) { bEncontrado=true;
		 * break; } }
		 */
		return bEncontrado;
	}

	public Boolean validarSolicitudEnRevision(String codSoli) {
		boolean bEncontrado = false;
		String codigoSolicEnRevision = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_EN_REVISION);
		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
				codigoSolicEnRevision));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}

		lstSolicitudesSelected.clear();
		for (TiivsHistSolicitud tmp : lstHistorial) {
			if (lstHistorial != null && lstHistorial.size() > 0) {
				lstSolicitudesSelected.add(tmp.getId().getCodSoli());
			}
		}

		int ind = 0;

		for (; ind <= lstSolicitudesSelected.size() - 1; ind++) {
			if (lstSolicitudesSelected.get(ind).equals(codSoli)) {
				bEncontrado = true;
				break;
			}
		}
		return bEncontrado;
	}

	public Boolean validarSolicitudRevocada(String codSoli) {
		boolean bEncontrado = false;
		String codigoSolicRevocado = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_REVOCADO);
		GenericDao<TiivsHistSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsHistSolicitud.class);
		filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
				codigoSolicRevocado));

		try {
			lstHistorial = busqHisDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.debug("Error al buscar en historial de solicitudes");
		}

		lstSolicitudesSelected.clear();
		for (TiivsHistSolicitud tmp : lstHistorial) {
			if (lstHistorial != null && lstHistorial.size() > 0) {
				lstSolicitudesSelected.add(tmp.getId().getCodSoli());
			}
		}

		int ind = 0;

		for (; ind <= lstSolicitudesSelected.size() - 1; ind++) {
			if (lstSolicitudesSelected.get(ind).equals(codSoli)) {
				bEncontrado = true;
				break;
			}
		}
		return bEncontrado;
	}

	public String buscarCodigoEstado(String estado) {
		int i = 0;
		String codigo = "";
		for (; i < combosMB.getLstEstado().size(); i++) {
			if (combosMB.getLstEstado().get(i).getDescripcion()
					.equalsIgnoreCase(estado)) {
				codigo = combosMB.getLstEstado().get(i).getCodEstado();
				break;
			}
		}
		return codigo;
	}

	public String buscarEstadoxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstado().size(); i++) {
			if (combosMB.getLstEstado().get(i).getCodEstado()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstado().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}

	public String buscarNivelxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstNivel().size(); i++) {
			if (combosMB.getLstNivel().get(i).getCodNiv()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstNivel().get(i).getDesNiv();
				break;
			}
		}
		return res;
	}

	public String buscarEstNivelxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstadoNivel().size(); i++) {
			if (combosMB.getLstEstadoNivel().get(i).getCodigoEstadoNivel()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstadoNivel().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}

	public String buscarEstudioxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstEstudio().size(); i++) {
			if (combosMB.getLstEstudio().get(i).getCodEstudio()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstEstudio().get(i).getDesEstudio();
				break;
			}
		}
		return res;
	}

	public String buscarOpeBanxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstOpeBancaria().size(); i++) {
			if (combosMB.getLstOpeBancaria().get(i).getCodOperBan()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstOpeBancaria().get(i).getDesOperBan();
				break;
			}
		}
		return res;
	}

	public String buscarTipoSolxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstTipoSolicitud().size(); i++) {
			if (combosMB.getLstTipoSolicitud().get(i).getCodTipSolic()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstTipoSolicitud().get(i).getDesTipServicio();
				break;
			}
		}
		return res;
	}

	public String buscarTipoFechaxCodigo(String codigo) {
		int i = 0;
		String res = "";
		for (; i < combosMB.getLstTiposFecha().size(); i++) {
			if (combosMB.getLstTiposFecha().get(i).getCodigoTipoFecha()
					.equalsIgnoreCase(codigo)) {
				res = combosMB.getLstTiposFecha().get(i).getDescripcion();
				break;
			}
		}
		return res;
	}

	public String buscarDesTerritorio(String codigoTerritorio) {
		String resultado = "";

		if (codigoTerritorio.compareTo("") != 0) {
			logger.debug("Buscando Territorio por codigo: " + codigoTerritorio);

			GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);
			filtroTerr.add(Restrictions.eq(
					ConstantesVisado.CAMPO_COD_TERRITORIO, codigoTerritorio));

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

	public void setLstTipoSolicitudSelected(
			List<String> lstTipoSolicitudSelected) {
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
		return (textoTotalResultados.replace("-1", "0"));
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

	public String getTextoAnioMes() {
		return textoAnioMes;
	}

	public void setTextoAnioMes(String textoAnioMes) {
		this.textoAnioMes = textoAnioMes;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public List<AgrupacionPlazoDto> getLstLiquidacion() {
		return lstLiquidacion;
	}

	public void setLstLiquidacion(List<AgrupacionPlazoDto> lstLiquidacion) {
		this.lstLiquidacion = lstLiquidacion;
	}

	public String getRutaArchivoExcel() {
		return rutaArchivoExcel;
	}

	public double getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(double impuesto) {
		this.impuesto = impuesto;
	}

	public String getNombreLiquidacion() {
		return nombreLiquidacion;
	}

	public void setNombreLiquidacion(String nombreLiquidacion) {
		this.nombreLiquidacion = nombreLiquidacion;
	}

	// lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
	// lstSolicitudesOficina = new ArrayList<SolicitudesOficina>();
	// lstRecaudacionTipoServ = new ArrayList<RecaudacionTipoServ>();
	// lstSolicitudesTipoServicio = new ArrayList<SolicitudesTipoServicio>();
	// lstLiquidacion = new ArrayList<AgrupacionPlazoDto>();
}
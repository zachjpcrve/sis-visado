package com.hildebrando.visado.mb;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.Estado;
import com.hildebrando.visado.dto.EstadosNivel;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.RangosImporte;
import com.hildebrando.visado.dto.Solicitud;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.dto.TiposFecha;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsHistorialDeSolicitud;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsTerritorio;
import com.hildebrando.visado.modelo.TiivsTipoServicio;

@ManagedBean(name = "solicitudMB")
@SessionScoped
public class SolicitudMB {
	Solicitud solicitudModificar;
	Solicitud bandeja;
	private List<TiivsSolicitud> solicitudes;
	private String textoTotalResultados;
	private TiivsMultitabla multitabla;
	private List<TiivsMultitabla> lstMultitabla;
	private List<RangosImporte> lstRangosImporte;
	private List<Estado> lstEstado;
	private List<String> lstEstadoSelected;
	private List<EstadosNivel> lstEstadoNivel;
	private List<String> lstEstadoNivelSelected;
	private List<TiposFecha> lstTiposFecha;
	private List<TiivsEstudio> lstEstudio;
	private List<String> lstEstudioSelected;
	private List<TiivsNivel> lstNivel;
	private List<String> lstNivelSelected;
	private List<String> lstSolicitudesSelected;
	private List<TiivsOperacionBancaria> lstOpeBancaria;
	private List<TiivsTipoServicio> lstTipoSolicitud;
	private List<TiivsHistorialDeSolicitud> lstHistorial;
	private List<String> lstTipoSolicitudSelected;
	private List<TiivsTerritorio> lstTerritorio;
	private List<TiivsOficina1> lstOficina;
	private List<TiivsOficina1> lstOficina1;
	private List<Moneda> lstMoneda;
	private List<TiivsMiembro> lstMiembros;
	private List<TipoDocumento> lstTipoDocumentos;
	private String idCodOfi;
	private String idCodOfi1;
	private String txtCodOfi;
	private String txtNomOfi;
	private String idTerr;
	private String idTipoSol;
	private String idOpeBan;
	private String idImporte;
	private String idEstudio;
	private String idEstado;
	private String idTiposFecha;
	private String idNivel;
	private String idEstNivel;
	private String poderdante;
	private String codSolicitud;
	private String nroDOIApoderado;
	private String nroDOIPoderdante;
	private String txtNomApoderado;
	private String txtNomPoderdante;
	private Date fechaInicio;
	private Date fechaFin;
	private Boolean noMostrarFechas;
	private String txtValTipoFecha;
	private String txtNomOficina;
	private String txtNomTerritorio;
	private String txtCodOficina;
	private Boolean conRevision;
	private Boolean conDelegados;
	private Boolean conExonerados;
	private Map<String, String> niveles;
	private Map<String, String> tiposSolicitud;
	private Map<String, String> estados;
	private Map<String, String> estadosNivel;
	private Map<String, String> estudios;
	private Boolean noHabilitarExportar;
	private String nombreArchivoExcel;

	public static Logger logger = Logger.getLogger(SolicitudMB.class);

	public SolicitudMB() {
		solicitudModificar = new Solicitud();
		solicitudes = new ArrayList<TiivsSolicitud>();
		lstMultitabla = new ArrayList<TiivsMultitabla>();
		lstRangosImporte = new ArrayList<RangosImporte>();
		lstEstado = new ArrayList<Estado>();
		lstEstadoNivel = new ArrayList<EstadosNivel>();
		lstTiposFecha = new ArrayList<TiposFecha>();
		lstEstudio = new ArrayList<TiivsEstudio>();
		lstNivel = new ArrayList<TiivsNivel>();
		lstOpeBancaria = new ArrayList<TiivsOperacionBancaria>();
		lstTipoSolicitud = new ArrayList<TiivsTipoServicio>();
		lstTerritorio = new ArrayList<TiivsTerritorio>();
		lstOficina = new ArrayList<TiivsOficina1>();
		lstOficina1 = new ArrayList<TiivsOficina1>();
		lstMoneda = new ArrayList<Moneda>();
		lstTipoDocumentos = new ArrayList<TipoDocumento>();
		niveles = new HashMap<String, String>();
		tiposSolicitud = new HashMap<String, String>();
		estados = new HashMap<String, String>();
		estadosNivel = new HashMap<String, String>();
		estudios = new HashMap<String, String>();
		lstSolicitudesSelected = new ArrayList<String>();

		cargarMultitabla();
		// Carga combo Rango Importes
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_IMPORTES);
		// Carga combo Estados
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS);
		// Carga combo Estados Nivel
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL);
		// Carga combo Tipos de Fecha
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA);
		// Carga lista de monedas
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_MONEDA);
		cargarMiembros();

		cargaCombosNoMultitabla();
		// LLena la grilla de solicitudes
		cargarSolicitudes();

		if (solicitudes.size() == 0) {
			setearTextoTotalResultados(
					ConstantesVisado.MSG_TOTAL_SIN_REGISTROS,
					solicitudes.size());
			setNoHabilitarExportar(true);
		} else {
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
					+ solicitudes.size() + ConstantesVisado.MSG_REGISTROS,
					solicitudes.size());
			setNoHabilitarExportar(false);
		}
		setNoMostrarFechas(true);

		String cadena = ConstantesVisado.TOOLTIP_TIPO_FECHA_ENVIO;
		String cadena2 = ConstantesVisado.TOOLTIP_TIPO_FECHA_RPTA;

		String cadFinal = cadena.concat(System.getProperty("line.separator"))
				.concat(cadena2);
		setTxtValTipoFecha(cadFinal);
		setTxtNomOficina(ConstantesVisado.MSG_TODOS);
		setTxtNomTerritorio(ConstantesVisado.MSG_TODOS);
		setTxtCodOficina(ConstantesVisado.MSG_TODOS);
		generarNombreArchivo();

	}

	private void setearTextoTotalResultados(String cadena, int total) {
		if (total == 1) {
			setTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
					+ total + ConstantesVisado.MSG_REGISTRO);
		} else {
			setTextoTotalResultados(cadena);
		}
	}

	// Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a
	// los filtros seleccionados.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void busquedaSolicitudes() {
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);

		// solicitudes = new ArrayList<TiivsSolicitud>();

		// 1. Filtro por codigo de solicitud
		if (getCodSolicitud().compareTo("") != 0) {
			System.out.println("Entro 1");
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,
					getCodSolicitud()));
		}

		// 2. Filtro por estado
		if (lstEstadoSelected.size() > 0) {
			// String codEstado=getIdEstado().trim();
			// System.out.println("Filtro estado: " + codEstado);
			// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
			// codEstado));
			System.out.println("Entro 2");
			int ind = 0;

			for (; ind <= lstEstadoSelected.size() - 1; ind++) {
				System.out.println("Estados: " + lstEstadoSelected.get(ind));
				// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
				// lstEstadoSelected.get(ind)));
			}

			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,
					lstEstadoSelected));
		}

		// 3. Filtro por importe
		if (getIdImporte().compareTo("") != 0) {
			System.out.println("Entro 3");
			if (getIdImporte().equals(
					ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) {
				filtroSol
						.add(Restrictions.le(
								ConstantesVisado.CAMPO_IMPORTE,
								BigDecimal
										.valueOf(ConstantesVisado.VALOR_RANGO_CINCUENTA)));
			}

			if (getIdImporte()
					.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) {
				filtroSol
						.add(Restrictions.gt(
								ConstantesVisado.CAMPO_IMPORTE,
								BigDecimal
										.valueOf(ConstantesVisado.VALOR_RANGO_CINCUENTA)));
				filtroSol
						.add(Restrictions.le(
								ConstantesVisado.CAMPO_IMPORTE,
								BigDecimal
										.valueOf(ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE)));
			}

			if (getIdImporte()
					.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) {
				filtroSol
						.add(Restrictions.gt(
								ConstantesVisado.CAMPO_IMPORTE,
								BigDecimal
										.valueOf(ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE)));
				filtroSol
						.add(Restrictions.le(
								ConstantesVisado.CAMPO_IMPORTE,
								BigDecimal
										.valueOf(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}

			if (getIdImporte()
					.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) {
				filtroSol
						.add(Restrictions.gt(
								ConstantesVisado.CAMPO_IMPORTE,
								BigDecimal
										.valueOf(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}
		}

		// 4. Filtro por tipo de solicitud
		if (lstTipoSolicitudSelected.size() > 0) {
			System.out.println("Entro 4");
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_TIPO_SERVICIO,
					ConstantesVisado.ALIAS_TBL_TIPO_SERVICIO);
			// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OPE_ALIAS,
			// getIdTipoSol()));
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_OPE_ALIAS,
					lstTipoSolicitudSelected));
		}

		// 5. Filtro por tipo de fecha
		if (getIdTiposFecha().compareTo("") != 0) {
			System.out.println("Entro 5");
			if (getIdTiposFecha().equalsIgnoreCase(
					ConstantesVisado.TIPO_FECHA_ENVIO)) // Es fecha de envio
			{
				filtroSol.add(Restrictions.between(
						ConstantesVisado.CAMPO_FECHA_ENVIO, getFechaInicio(),
						getFechaFin()));
				filtroSol
						.add(Restrictions
								.eq(ConstantesVisado.CAMPO_ESTADO,
										buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_ENVIADO)));
			}
			if (getIdTiposFecha().equalsIgnoreCase(
					ConstantesVisado.TIPO_FECHA_RPTA)) // Sino es fecha de
														// respuesta
			{
				filtroSol.add(Restrictions.between(
						ConstantesVisado.CAMPO_FECHA_RPTA, getFechaInicio(),
						getFechaFin()));

				Collection<String> tmpEstados = null;
				tmpEstados
						.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_ACEPTADO));
				tmpEstados
						.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_RECHAZADO));
				tmpEstados
						.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A));

				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,
						tmpEstados));
			}
		}

		// 6. Filtro por operacion bancaria
		if (getIdOpeBan().compareTo("") != 0) {
			System.out.println("Entro 6");
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_OPE_BANCARIAS,
					getIdOpeBan()));
		}

		// 7. Filtro por codigo de oficina
		if (getTxtCodOfi().compareTo("") != 0) {
			System.out.println("Entro 7");
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,
					ConstantesVisado.ALIAS_TBL_OFICINA);
			filtroSol.add(Restrictions.eq(
					ConstantesVisado.CAMPO_COD_OFICINA_ALIAS, getTxtCodOfi()));
		}

		// 8. Filtro por nombre de oficina
		if (getTxtNomOfi().compareTo("") != 0) {
			System.out.println("Entro 8");
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,
					ConstantesVisado.ALIAS_TBL_OFICINA);
			String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE
					+ getTxtNomOfi()
							.concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like(
					ConstantesVisado.CAMPO_NOM_OFICINA_ALIAS, filtroNuevo));
		}

		// 9. Filtro por combo de codigos de oficina
		if (getIdCodOfi().compareTo("") != 0) {
			System.out.println("Entro 9");
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,
					ConstantesVisado.ALIAS_TBL_OFICINA);
			filtroSol.add(Restrictions.eq(
					ConstantesVisado.CAMPO_COD_OFICINA_ALIAS, getIdCodOfi()));
		}

		// 10. Filtro por combo de oficinas
		if (getIdCodOfi1().compareTo("") != 0) {
			System.out.println("Entro 10");
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,
					ConstantesVisado.ALIAS_TBL_OFICINA);
			filtroSol.add(Restrictions.eq(
					ConstantesVisado.CAMPO_COD_OFICINA_ALIAS, getIdCodOfi1()));
		}

		// 11. Filtro por numero de documento de apoderado
		if (getNroDOIApoderado().compareTo("") != 0) {
			System.out.println("Entro 11");
			filtroSol.add(Restrictions.eq(
					ConstantesVisado.CAMPO_NUMDOC_APODERADO,
					getNroDOIApoderado()));
		}

		// 12. Filtro por nombre de apoderado
		if (getTxtNomApoderado().compareTo("") != 0) {
			System.out.println("Entro 12");
			String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE
					+ getTxtNomApoderado().concat(
							ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_APODERADO,
					filtroNuevo));
		}

		// 13. Filtro por numero de documento de poderdante
		if (getNroDOIPoderdante().compareTo("") != 0) {
			System.out.println("Entro 13");
			filtroSol.add(Restrictions.eq(
					ConstantesVisado.CAMPO_NUMDOC_PODERDANTE,
					getNroDOIPoderdante()));
		}

		// 14. Filtro por nombre de poderdante
		if (getTxtNomPoderdante().compareTo("") != 0) {
			System.out.println("Entro 14");
			String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE
					+ getTxtNomPoderdante().concat(
							ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_PODERDANTE,
					filtroNuevo));
		}

		// 15. Filtro por nivel
		if (lstNivelSelected.size() > 0) {
			System.out.println("Entro 15");

			for (TiivsSolicitud sol : solicitudes) {
				if (sol.getTxtNivel() != null && sol.getTxtNivel().length() > 0) {
					if (lstNivelSelected.get(0).indexOf(sol.getTxtNivel()) != -1) {
						lstSolicitudesSelected.add(sol.getCodSoli());
					}
				}
			}
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,
					lstSolicitudesSelected));
		}

		// 16. Filtro por estado de nivel
		if (lstEstadoNivelSelected.size() > 0) {
			System.out.println("Entro 16");
		}

		// 17. Filtro por estudio
		if (lstEstudioSelected.size() > 0) {
			System.out.println("Entro 17");
			// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTUDIO,
			// getIdEstudio()));
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTUDIO,
					lstEstudioSelected));
		}

		// 18. Filtro por territorio
		if (getIdTerr().compareTo("") != 0) {
			System.out.println("Entro 18");
			String codTerr = getIdTerr().trim();
			// System.out.println("Buscando territorio: " + codTerr);
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,
					ConstantesVisado.ALIAS_TBL_OFICINA);
			filtroSol.add(Restrictions.eq(
					ConstantesVisado.CAMPO_COD_TERR_ALIAS, codTerr));
		}

		// 19. Filtrar solicitudes con revision
		if (getConRevision()) {
			String codigoSolicEnRevision = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_EN_REVISION);
			GenericDao<TiivsHistorialDeSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistorialDeSolicitud, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda
					.forClass(TiivsHistorialDeSolicitud.class);
			filtro.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
					codigoSolicEnRevision));

			try {
				lstHistorial = busqHisDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.debug("Error al buscar en historial de solicitudes");
			}

			if (lstHistorial.size() > 0) {
				filtroSol.add(Restrictions.in(
						ConstantesVisado.CAMPO_COD_SOLICITUD, lstHistorial));
			} else {
				System.out
						.println("No hay solicitudes en el historial con estado En Revision");
			}

		}

		// 20. Filtrar solicitudes que se hayan delegado
		if (getConDelegados()) {
			String codigoSolicVerA = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A);
			String codigoSolicVerB = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_B);

			GenericDao<TiivsHistorialDeSolicitud, Object> busqHisDAO = (GenericDao<TiivsHistorialDeSolicitud, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda
					.forClass(TiivsHistorialDeSolicitud.class);
			filtro.add(Restrictions.or(Restrictions.eq(
					ConstantesVisado.CAMPO_ESTADO, codigoSolicVerA),
					Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
							codigoSolicVerB)));

			try {
				lstHistorial = busqHisDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.debug("Error al buscar en historial de solicitudes");
			}

			if (lstHistorial.size() > 0) {
				// Colocar aqui la logica para filtrar los niveles aprobados o
				// rechazados
			}
		}

		// 21. Filtrar solicitudes que se hayan exonerado (no hay definicion
		// funcional)
		if (getConExonerados()) {

		}

		// Buscar solicitudes de acuerdo a criterios seleccionados
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes");
		}

		if (solicitudes.size() == 0) {
			setearTextoTotalResultados(
					ConstantesVisado.MSG_TOTAL_SIN_REGISTROS,
					solicitudes.size());
			setNoHabilitarExportar(true);
		} else {
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
					+ solicitudes.size() + ConstantesVisado.MSG_REGISTROS,
					solicitudes.size());
			actualizarDatosGrilla();
			setNoHabilitarExportar(false);
		}
	}

	public String buscarCodigoEstado(String estado) {
		int i = 0;
		String codigo = "";
		for (; i <= lstEstado.size(); i++) {
			if (lstEstado.get(i).getDescripcion().equalsIgnoreCase(estado)) {
				codigo = lstEstado.get(i).getCodEstado();
				break;
			}
		}

		return codigo;
	}

	private void actualizarDatosGrilla() {
		String nomTipoDocPoder = "";
		String nomTipoDocApod = "";

		for (TiivsSolicitud tmpSol : solicitudes) {
			// Se setea la descripcion de la columna moneda
			if (tmpSol.getMoneda() != null) {
				tmpSol.setDesMoneda(buscarDescripcionMoneda(tmpSol.getMoneda()));
			}

			// Se setea la columna estudio
			if (tmpSol.getRegAbogado() != null) {
				tmpSol.setEstudio(buscarDescripcionEstudio(tmpSol
						.getRegAbogado()));
			}

			// Se obtiene la descripcion del documento del Poderdante
			if (tmpSol.getNumDocPoder() != null) {
				if (tmpSol.getTipDocPoder().equalsIgnoreCase(
						lstTipoDocumentos.get(0).getCodTipoDoc())) {
					nomTipoDocPoder = lstTipoDocumentos.get(0).getDescripcion();
				}
			}

			// Se obtiene la moneda y se coloca las iniciales en la columna
			// Importe Total
			if (tmpSol.getMoneda() != null) {
				String moneda = buscarAbrevMoneda(tmpSol.getMoneda());
				tmpSol.setTxtImporte(moneda.concat(ConstantesVisado.DOS_PUNTOS)
						.concat(String.valueOf(tmpSol.getImporte())));
			} else {
				if (tmpSol.getImporte() != null) {
					tmpSol.setTxtImporte(String.valueOf(tmpSol.getImporte()));
				}
			}

			// Seteo de la columna Poderdante
			if (nomTipoDocPoder.compareTo("") == 0) {
				tmpSol.setTxtPoderdante(tmpSol.getPoderante());
			} else {
				if (tmpSol.getCodCentral() != null) {
					if (tmpSol.getCodCentral().compareTo("") == 0) {
						tmpSol.setTxtPoderdante(nomTipoDocPoder
								+ ConstantesVisado.DOS_PUNTOS
								+ tmpSol.getNumDocPoder()
								+ ConstantesVisado.GUION
								+ tmpSol.getPoderante());
					} else {
						tmpSol.setTxtPoderdante(ConstantesVisado.ETIQUETA_COD_CENTRAL
								+ tmpSol.getCodCentral()
								+ ConstantesVisado.DOS_PUNTOS
								+ nomTipoDocPoder
								+ ConstantesVisado.DOS_PUNTOS
								+ tmpSol.getNumDocPoder()
								+ ConstantesVisado.GUION
								+ tmpSol.getPoderante());
					}
				} else {
					tmpSol.setTxtPoderdante(nomTipoDocPoder
							+ ConstantesVisado.DOS_PUNTOS
							+ tmpSol.getNumDocPoder() + ConstantesVisado.GUION
							+ tmpSol.getPoderante());
				}
			}

			// Se obtiene la descripcion del documento del Apoderado
			if (tmpSol.getNumDocApoder() != null) {
				if (tmpSol.getTipDocApoder().equalsIgnoreCase(
						lstTipoDocumentos.get(0).getCodTipoDoc())) {
					nomTipoDocApod = lstTipoDocumentos.get(0).getDescripcion();
				}
			}

			// Seteo de la columna Apoderado
			if (nomTipoDocApod.compareTo("") == 0) {
				tmpSol.setTxtApoderado(tmpSol.getApoderado());
			} else {
				if (tmpSol.getCodCentral() != null) {
					if (tmpSol.getCodCentral().compareTo("") == 0) {
						tmpSol.setTxtApoderado(nomTipoDocApod
								+ ConstantesVisado.DOS_PUNTOS
								+ tmpSol.getNumDocApoder()
								+ ConstantesVisado.GUION
								+ tmpSol.getApoderado());
					} else {
						tmpSol.setTxtApoderado(ConstantesVisado.ETIQUETA_COD_CENTRAL
								+ tmpSol.getCodCentral()
								+ ConstantesVisado.DOS_PUNTOS
								+ nomTipoDocApod
								+ ConstantesVisado.DOS_PUNTOS
								+ tmpSol.getNumDocApoder()
								+ ConstantesVisado.GUION
								+ tmpSol.getApoderado());
					}
				} else {
					tmpSol.setTxtApoderado(nomTipoDocApod
							+ ConstantesVisado.DOS_PUNTOS
							+ tmpSol.getNumDocApoder() + ConstantesVisado.GUION
							+ tmpSol.getApoderado());
				}
			}

			// Se obtiene y setea la descripcion del Estado en la grilla
			if (tmpSol.getEstado() != null) {
				if (tmpSol.getEstado().trim()
						.equalsIgnoreCase(lstEstado.get(0).getCodEstado())) {
					tmpSol.setTxtEstado(lstEstado.get(0).getDescripcion());
				}
			}

			// Proceso para obtener los niveles de cada solicitud
			if (tmpSol.getImporte() != null) {
				if (lstNivel.size() > 0) {
					String txtNivelTMP = "";
					String descripcion = buscarDescMoneda(tmpSol.getMoneda());
					System.out.println("Moneda encontrada: " + descripcion);

					for (TiivsNivel tmp : lstNivel) {
						if (tmp.getMoneda().equalsIgnoreCase(descripcion)) {
							if (tmp.getDesNiv().equalsIgnoreCase(
									ConstantesVisado.CAMPO_NIVEL1)) {
								BigDecimal importeTMP = tmpSol.getImporte();
								BigDecimal rangoIni = BigDecimal.valueOf(tmp
										.getRangoInicio());
								BigDecimal rangoFin = BigDecimal.valueOf(tmp
										.getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0
										&& importeTMP.compareTo(rangoFin) <= 0) {
									txtNivelTMP += ConstantesVisado.CAMPO_NIVEL1;
								}
							}

							if (tmp.getDesNiv().equalsIgnoreCase(
									ConstantesVisado.CAMPO_NIVEL2)) {
								BigDecimal importeTMP = tmpSol.getImporte();
								BigDecimal rangoIni = BigDecimal.valueOf(tmp
										.getRangoInicio());
								BigDecimal rangoFin = BigDecimal.valueOf(tmp
										.getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0
										&& importeTMP.compareTo(rangoFin) <= 0) {
									if (txtNivelTMP.length() > 0) {
										txtNivelTMP += ","
												+ ConstantesVisado.CAMPO_NIVEL2;
									} else {
										txtNivelTMP += ConstantesVisado.CAMPO_NIVEL2;
									}
								}
							}

							if (tmp.getDesNiv().equalsIgnoreCase(
									ConstantesVisado.CAMPO_NIVEL3)) {
								BigDecimal importeTMP = tmpSol.getImporte();
								BigDecimal rangoIni = BigDecimal.valueOf(tmp
										.getRangoInicio());
								BigDecimal rangoFin = BigDecimal.valueOf(tmp
										.getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0
										&& importeTMP.compareTo(rangoFin) <= 0) {
									if (txtNivelTMP.length() > 0) {
										txtNivelTMP += ","
												+ ConstantesVisado.CAMPO_NIVEL3;
									} else {
										txtNivelTMP += ConstantesVisado.CAMPO_NIVEL3;
									}
								}
							}
						}

					}
					tmpSol.setTxtNivel(txtNivelTMP);
					// }
					// else
					// {
					/*
					 * txtNivelTMP+=ConstantesVisado.CAMPO_NIVEL4;
					 * tmpSol.setTxtNivel(txtNivelTMP);
					 */
					// }
				} else {
					System.out
							.println("No se pudo obtener los rangos de los niveles para la solicitud. Verificar base de datos!!");
				}
			} else {
				System.out
						.println("No se pudo determinar importe valido. Verificar registro de solicitud!!");
			}
		}
	}

	public void generarNombreArchivo() 
	{
		setNombreArchivoExcel("Solicitudes_Visado "	+ obtenerFechaArchivoExcel() + "_XXXX");
	}

	public String obtenerFechaArchivoExcel() 
	{
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"dd/MM/yyyy");
		String fecha = sdf.format(date);
		String nuevaFecha = fecha.substring(0, 2) + "" + fecha.substring(3, 5)
				+ "" + fecha.substring(6, fecha.length());

		return nuevaFecha;
	}

	public void exportarExcelPOI()
	{
		crearExcel();
	}

	private void crearExcel() 
	{
		try 
		{
			// Defino el Libro de Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// Creo la Hoja en Excel
			Sheet sheet = wb.createSheet(obtenerFechaArchivoExcel());

			// quito las lineas del libro para darle un mejor acabado
			sheet.setDisplayGridlines(false);
			//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

			// creo una nueva fila
			Row trow = sheet.createRow((short) 0);
			crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER, ConstantesVisado.TITULO_CABECERA_EXCEL);
			
			//Se crea la leyenda de quien genero el archivo y la hora respectiva
			Row rowG = sheet.createRow((short) 1);
			crearCell(wb, rowG, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_GENERADOR, false, false,false);
			crearCell(wb, rowG, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "",  true, false,true);
			
			Row rowG1 = sheet.createRow((short) 2);
			crearCell(wb, rowG1, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_HORA, false, false,false);
			crearCell(wb, rowG1, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "",  true, false,true);
			
			
			//Genera celdas con los filtros de busqueda
			Row row2 = sheet.createRow((short) 4);
			crearCell(wb, row2, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_NRO_SOL, false, false,false);
			crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO, false, false,false);
			crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row2, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_IMPORTE, false, false,false);
			crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row2, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_SOL, false, false,false);
			crearCell(wb, row2, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			
			Row row3 = sheet.createRow((short) 5);
			crearCell(wb, row3, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_FECHA, false, false,false);
			crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false, false,false);
			crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row3, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false, false,false);
			crearCell(wb, row3, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row3, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_OPE, false, false,false);
			crearCell(wb, row3, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			
			Row row4 = sheet.createRow((short) 6);
			crearCell(wb, row4, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_COD_OFICINA, false, false,false);
			crearCell(wb, row4, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row4, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_OFICINA, false, false,false);
			crearCell(wb, row4, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row4, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_COMB_COD_OFICINA, false, false,false);
			crearCell(wb, row4, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row4, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_COMB_OFICINA, false, false,false);
			crearCell(wb, row4, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			
			Row row5 = sheet.createRow((short) 7);
			crearCell(wb, row5, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DOI_APODERADO, false, false,false);
			crearCell(wb, row5, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row5, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_APODERADO, false, false,false);
			crearCell(wb, row5, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row5, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DOI_PODERDANTE, false, false,false);
			crearCell(wb, row5, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row5, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_PODERDANTE, false, false,false);
			crearCell(wb, row5, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			
			Row row6 = sheet.createRow((short) 8);
			crearCell(wb, row6, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row6, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row6, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row6, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TERRITORIO,false, false,false);
			crearCell(wb, row6, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			
			Row row7 = sheet.createRow((short) 9);
			crearCell(wb, row7, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_RECLAMO, false, false,false);
			crearCell(wb, row7, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row7, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DELEGADO, false, false,false);
			crearCell(wb, row7, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			crearCell(wb, row7, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_NIVEL, false, false,false);
			crearCell(wb, row7, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
		
			
			if (solicitudes.size()==0)
			{
				System.out.println("Sin registros para exportar");
			}
			else
			{
				// Se crea la cabecera de la tabla de resultados
				Row rowT = sheet.createRow((short) 12);

				// Creo las celdas de mi fila, se puede poner un diseño a la celda
				crearCell(wb, rowT, 0, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ITEM, true, true,false);
				crearCell(wb, rowT, 1, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_NRO_SOLICITUD, true, true,false);
				crearCell(wb, rowT, 2, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_COD_OFICINA, true, true,false);
				crearCell(wb, rowT, 3, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_OFICINA, true, true,false);
				crearCell(wb, rowT, 4, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TERRITORIO, true, true,false);
				crearCell(wb, rowT, 5, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ESTADO, true, true,false);
				crearCell(wb, rowT, 6, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_IMPORTE, true, true,false);
				crearCell(wb, rowT, 7, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_PODERDANTE, true, true,false);
				crearCell(wb, rowT, 8, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_APODERADO, true, true,false);
				crearCell(wb, rowT, 9, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TIPO_SOL, true, true,false);
				crearCell(wb, rowT, 10, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_TIPO_OPE, true, true,false);
				crearCell(wb, rowT, 11, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_ESTUDIO, true, true,false);
				crearCell(wb, rowT, 12, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_NIVEL, true, true,false);
				crearCell(wb, rowT, 13, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_ENVIO, true, true,false);
				crearCell(wb, rowT, 14, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_RPTA, true, true,false);
				crearCell(wb, rowT, 15, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_FECHA_ESTADO, true, true,false);
				
				int numReg=13;
				int contador=0;
				for (TiivsSolicitud tmp: solicitudes)
				{
					contador++;
					//Columna Item en Excel
					Row row = sheet.createRow((short) numReg);
					if (contador<=9)
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.TRES_CEROS + contador, true, false,true);
					}
					else if (contador<=99 && contador >9)
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, ConstantesVisado.DOS_CEROS + contador, true, false,true);
					}
					else if(contador>=99 && contador<999)
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.CERO + contador, true, false,true);
					}
					else
					{
						crearCell(wb, row, 0, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(contador), true, false,true);
					}
					
					//Columna Nro Solicitud en Excel
					crearCell(wb, row, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getCodSoli(), true, false,true);
										
					//Columna Cod Oficina en Excel
					crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTiivsOficina1().getCodOfi(),true, false,true);
					
					//Columna Oficina en Excel
					crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTiivsOficina1().getDesOfi(),true, false,true);
					
					//Columna Territorio en Excel
					crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarDesTerritorio(tmp.getTiivsOficina1().getCodTerr()),true, false,true);
					
					//Columna Estado en Excel
					crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtEstado(), true, false,true);
					
					//Columna Importe en Excel
					crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtImporte(), true, false,true);
					
					//Columna Poderdante en Excel
					crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtPoderdante(), true, false,true);
					
					//Columna Apoderado en Excel
					crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtApoderado(), true, false,true);
					
					//Columna Tipo Solicitud en Excel
					crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTiivsTipoServicio().getDesOper(), true, false,true);
					
					//Columna Operaciones Bancarias en Excel
					crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getOperacionesBancarias(), true, false,true);
					
					//Columna Estudio en Excel
					crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getEstudio(), true, false,true);
					
					//Columna Nivel en Excel
					crearCell(wb, row, 12, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtNivel(), true, false,true);
					
					//Columna Fecha Envio en Excel
					if (tmp.getFechaEnvio()!=null)
					{
						crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaEnvio()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 13, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					}
							
					//Columna Fecha Rpta en Excel
					if (tmp.getFechaRespuesta()!=null)
					{
						crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaRespuesta()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 14, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					}
					
					//Columna Fecha Estado en Excel
					if (tmp.getFechaEstado()!=null)
					{
						crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getFechaEstado()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 15, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
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
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			
			//Se crea el archivo con la informacion y estilos definidos previamente
			String strRuta = "C:/hildebrando/" + getNombreArchivoExcel() + ".xls";
			FileOutputStream fileOut = new FileOutputStream(strRuta);
			wb.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}	}

	private static void crearTituloCell(HSSFWorkbook wb, Row row, int column, short halign, short valign, String strContenido) 
	{
		CreationHelper ch = wb.getCreationHelper();
		Cell cell = row.createCell(column);
		cell.setCellValue(ch.createRichTextString(strContenido));

		HSSFFont cellFont = wb.createFont();
		cellFont.setFontHeightInPoints((short) 10);
		cellFont.setFontName(HSSFFont.FONT_ARIAL);
		cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFont.setUnderline((byte) 1);

		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		cellStyle.setFont(cellFont);
		cell.setCellStyle(cellStyle);
	}

	private static void crearCell(Workbook wb, Row row, int column, short halign, short valign, String strContenido, boolean booBorde,
			boolean booCabecera, boolean booFiltrosBus) 
	{
		CreationHelper ch = wb.getCreationHelper();
		Cell cell = row.createCell(column);
		cell.setCellValue(ch.createRichTextString(strContenido));
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		
		if (booBorde) 
		{
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setBottomBorderColor((short) 8);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setLeftBorderColor((short) 8);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setRightBorderColor((short) 8);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_DOTTED);
			cellStyle.setTopBorderColor((short) 8);
		}
		
		if (booCabecera) 
		{
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setBottomBorderColor((short) 8);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setLeftBorderColor((short) 8);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setRightBorderColor((short) 8);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setTopBorderColor((short) 8);

			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}
		
		if (booFiltrosBus) 
		{
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setBottomBorderColor((short) 8);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setLeftBorderColor((short) 8);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setRightBorderColor((short) 8);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			cellStyle.setTopBorderColor((short) 8);
			//cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}
		
		cell.setCellStyle(cellStyle);
	}

	public String buscarDescMoneda(String codigo) 
	{
		int i = 0;
		String descripcion = "";

		for (; i <= lstMoneda.size() - 1; i++) 
		{
			if (lstMoneda.get(i).getCodMoneda().equalsIgnoreCase(codigo)) 
			{
				if (lstMoneda.get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_SOLES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_SOLES;
				} 
				else if (lstMoneda.get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_DOLARES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_DOLARES;
				} 
				else if (lstMoneda.get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_EUROS_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_EUROS;
				} 
				else {
					descripcion = lstMoneda.get(i).getDesMoneda();
				}
				break;
			}
		}

		return descripcion;
	}

	public String buscarAbrevMoneda(String codigo) {
		int i = 0;
		String descripcion = "";

		for (; i <= lstMoneda.size() - 1; i++) {
			if (lstMoneda.get(i).getCodMoneda().equalsIgnoreCase(codigo)) {
				if (lstMoneda
						.get(i)
						.getDesMoneda()
						.equalsIgnoreCase(
								ConstantesVisado.CAMPO_SOLES_TBL_MONEDA)) {
					descripcion = ConstantesVisado.CAMPO_ABREV_SOLES;
				} else if (lstMoneda
						.get(i)
						.getDesMoneda()
						.equalsIgnoreCase(
								ConstantesVisado.CAMPO_DOLARES_TBL_MONEDA)) {
					descripcion = ConstantesVisado.CAMPO_ABREV_DOLARES;
				} else if (lstMoneda
						.get(i)
						.getDesMoneda()
						.equalsIgnoreCase(
								ConstantesVisado.CAMPO_EUROS_TBL_MONEDA)) {
					descripcion = ConstantesVisado.CAMPO_ABREV_EUROS;
				} else {
					descripcion = lstMoneda.get(i).getDesMoneda();
				}
				break;
			}
		}

		return descripcion;
	}

	// Descripcion: Metodo que se encarga de cargar las solicitudes en la grilla
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void cargarSolicitudes() {
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);

		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
			// System.out.println("Tamanio inicial de solicitudes: " +
			// solicitudes.size());
		} catch (Exception ex) {
			logger.debug("Error al buscar las solicitudes");
		}

		actualizarDatosGrilla();
	}

	public String buscarDescripcionMoneda(String codMoneda) {
		String descripcion = "";
		for (Moneda tmpMoneda : lstMoneda) {
			if (tmpMoneda.getCodMoneda().equalsIgnoreCase(codMoneda)) {
				descripcion = tmpMoneda.getDesMoneda();
				break;
			}
		}
		return descripcion;
	}

	public String buscarDescripcionEstudio(String codEstudio) {
		String descripcion = "";
		for (TiivsMiembro tmpMiembros : lstMiembros) {
			if (tmpMiembros.getCodMiembro().equalsIgnoreCase(codEstudio)) {
				descripcion = tmpMiembros.getDescripcion();
				break;
			}
		}
		return descripcion;
	}

	public void cargarMiembros() {
		GenericDao<TiivsMiembro, Object> miemDAO = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMiem = Busqueda.forClass(TiivsMiembro.class);

		try {
			lstMiembros = miemDAO.buscarDinamico(filtroMiem);

		} catch (Exception ex) {
			logger.debug("Error al buscar registros de la tabla miembro");
		}
	}

	// Descripcion: Metodo que se encarga de cargar los datos que se encuentran
	// en la multitabla. Este metodo se llamara en el constructor
	// de la clase para que este disponible al inicio.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void cargarMultitabla() {
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);

		try {
			lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de multitablas");
		}
	}

	public void buscarOficinaPorCodigo(ValueChangeEvent e) {
		logger.debug("Buscando oficina por codigo: " + e.getNewValue());
		// System.out.println("Buscando oficina por codigo: " +
		// e.getNewValue());

		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_OFICINA,
				e.getNewValue()));

		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}

		if (lstTmp.size() == 1) {
			setTxtNomOficina(lstTmp.get(0).getDesOfi());
			setTxtNomTerritorio(buscarDesTerritorio(lstTmp.get(0).getCodTerr()));
		}
	}

	public void buscarOficinaPorNombre(ValueChangeEvent e) {
		logger.debug("Buscando oficina por nombre: " + e.getNewValue());
		// System.out.println("Buscando oficina por nombre: " +
		// e.getNewValue());

		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
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
			setTxtNomTerritorio(buscarDesTerritorio(lstTmp.get(0).getCodTerr()));
		}
	}

	public void buscarOficinaPorTerritorio(ValueChangeEvent e) {
		if (e.getNewValue() != null) {
			logger.debug("Buscando oficina por territorio: " + e.getNewValue());
			System.out.println("Buscando oficina por territorio: "
					+ e.getNewValue());

			GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
			filtroOfic.add(Restrictions.eq(
					ConstantesVisado.CAMPO_COD_TERR_NO_ALIAS, e.getNewValue()));

			List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();

			try {
				lstTmp = ofiDAO.buscarDinamico(filtroOfic);
				lstOficina = ofiDAO.buscarDinamico(filtroOfic);
				lstOficina1 = ofiDAO.buscarDinamico(filtroOfic);

			} catch (Exception exp) {
				logger.debug("No se pudo encontrar la oficina");
			}

			if (lstTmp.size() == 1) {
				setTxtCodOficina(lstTmp.get(0).getCodOfi());
				setTxtNomOficina(lstTmp.get(0).getDesOfi());
			}
		} else {
			// Carga combo de Territorio
			GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);

			try {
				lstTerritorio = terrDAO.buscarDinamico(filtroTerr);
			} catch (Exception e1) {
				logger.debug("Error al cargar el listado de territorios");
			}

			// Cargar combos de oficina
			GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
					.getApplicationContext().getBean("genericoDao");
			Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);

			try {
				lstOficina = ofiDAO.buscarDinamico(filtroOfic);
				lstOficina1 = ofiDAO.buscarDinamico(filtroOfic);

			} catch (Exception exp) {
				logger.debug("No se pudo encontrar la oficina");
			}
		}
	}

	public List<TiivsOficina1> completeCodOficina(String query) {

		List<TiivsOficina1> results = new ArrayList<TiivsOficina1>();

		List<TiivsOficina1> oficinas = new ArrayList<TiivsOficina1>();
		GenericDao<TiivsOficina1, Object> oficinaDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);
		try {
			oficinas = oficinaDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (TiivsOficina1 oficina : oficinas) {

			if (oficina.getCodOfi() != null) {

				String texto = oficina.getCodOfi();

				if (texto.contains(query.toUpperCase())) {
					results.add(oficina);
				}

			}

		}

		return results;
	}

	public List<TiivsOficina1> completeNomOficina(String query) {

		List<TiivsOficina1> results = new ArrayList<TiivsOficina1>();

		List<TiivsOficina1> oficinas = new ArrayList<TiivsOficina1>();
		GenericDao<TiivsOficina1, Object> oficinaDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);
		try {
			oficinas = oficinaDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (TiivsOficina1 oficina : oficinas) {

			if (oficina.getCodOfi() != null) {

				String texto = oficina.getDesOfi();

				if (texto.contains(query.toUpperCase())) {
					results.add(oficina);
				}

			}

		}

		return results;
	}

	public String buscarDesTerritorio(String codigoTerritorio) {
		String resultado = "";
		logger.debug("Buscando Territorio por codigo: " + codigoTerritorio);
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

	public void habilitarFechas(ValueChangeEvent e) {
		if (e.getNewValue() != null) {
			setNoMostrarFechas(false);
		} else {
			setNoMostrarFechas(true);
		}
	}

	// Descripcion: Metodo que se encarga de cargar los combos que se mostraran
	// en la pantalla de Bandeja de solicitudes
	// de acuerdo a la lista de la multitabla previamente cargada.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void cargarCombosMultitabla(String codigo) {
		logger.debug("Buscando valores en multitabla con codigo: " + codigo);
		lstRangosImporte.clear();
		lstEstado.clear();
		lstEstadoNivel.clear();
		lstTiposFecha.clear();
		lstEstudio.clear();
		lstNivel.clear();
		lstOpeBancaria.clear();
		lstTipoSolicitud.clear();
		lstTerritorio.clear();
		lstOficina.clear();

		for (TiivsMultitabla res : lstMultitabla) {
			// Carga combo importes
			if (res.getId()
					.getCodMult()
					.equalsIgnoreCase(
							ConstantesVisado.CODIGO_MULTITABLA_IMPORTES)) {
				RangosImporte tmpRangos = new RangosImporte();
				tmpRangos.setCodigoRango(res.getId().getCodElem());
				tmpRangos.setDescripcion(res.getValor1());
				lstRangosImporte.add(tmpRangos);

				logger.debug("Tamanio lista de importes: "
						+ lstRangosImporte.size());
			}

			// Carga combo estados
			if (res.getId()
					.getCodMult()
					.equalsIgnoreCase(
							ConstantesVisado.CODIGO_MULTITABLA_ESTADOS)) {
				Estado tmpEstado = new Estado();
				tmpEstado.setCodEstado(res.getId().getCodElem());
				tmpEstado.setDescripcion(res.getValor1());
				lstEstado.add(tmpEstado);

				int j = 0;

				for (; j <= lstEstado.size() - 1; j++) {
					estados.put(lstEstado.get(j).getDescripcion(), lstEstado
							.get(j).getCodEstado());
				}

				logger.debug("Tamanio lista de estados: " + lstEstado.size());
			}

			// Carga combo estados Nivel
			if (res.getId()
					.getCodMult()
					.equalsIgnoreCase(
							ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL)) {
				EstadosNivel tmpEstadoNivel = new EstadosNivel();
				tmpEstadoNivel.setCodigoEstadoNivel(res.getId().getCodElem());
				tmpEstadoNivel.setDescripcion(res.getValor1());
				lstEstadoNivel.add(tmpEstadoNivel);

				logger.debug("Tamanio lista de estados nivel: "
						+ lstEstadoNivel.size());

				int j = 0;

				for (; j <= lstEstadoNivel.size() - 1; j++) {
					estadosNivel.put(lstEstadoNivel.get(j).getDescripcion(),
							lstEstadoNivel.get(j).getCodigoEstadoNivel());
				}

				logger.debug("Tamanio lista de estados: " + lstEstado.size());
			}

			// Carga combo Tipos de fecha
			if (res.getId()
					.getCodMult()
					.equalsIgnoreCase(
							ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA)) {
				TiposFecha tmpTiposFecha = new TiposFecha();
				tmpTiposFecha.setCodigoTipoFecha(res.getId().getCodElem());
				tmpTiposFecha.setDescripcion(res.getValor1());
				lstTiposFecha.add(tmpTiposFecha);

				logger.debug("Tamanio lista de tipos de fecha: "
						+ lstTiposFecha.size());
			}

			// Carga lista de monedas
			if (res.getId()
					.getCodMult()
					.equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_MONEDA)) {
				Moneda tmpMoneda = new Moneda();
				tmpMoneda.setCodMoneda(res.getId().getCodElem());
				tmpMoneda.setDesMoneda(res.getValor1());
				lstMoneda.add(tmpMoneda);

				logger.debug("Tamanio lista de monedas: " + lstMoneda.size());
			}

			// Carga combo Tipos de documento
			if (res.getId()
					.getCodMult()
					.equalsIgnoreCase(
							ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC)) {
				TipoDocumento tmpTipoDoc = new TipoDocumento();
				tmpTipoDoc.setCodTipoDoc(res.getId().getCodElem());
				tmpTipoDoc.setDescripcion(res.getValor1());
				lstTipoDocumentos.add(tmpTipoDoc);

				logger.debug("Tamanio lista de tipos de documento: "
						+ lstTipoDocumentos.size());
			}
		}
	}

	public void cargaCombosNoMultitabla() {
		// Carga combo Nivel
		GenericDao<TiivsNivel, Object> nivelDAO = (GenericDao<TiivsNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroNivel = Busqueda.forClass(TiivsNivel.class);

		try {
			lstNivel = nivelDAO.buscarDinamico(filtroNivel);

		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de niveles");
		}

		int w = 0;
		String tmp = "";

		for (; w <= lstNivel.size() - 1; w++) {
			if (tmp.compareTo(lstNivel.get(w).getDesNiv()) != 0) {
				niveles.put(lstNivel.get(w).getDesNiv(), lstNivel.get(w)
						.getDesNiv());
				tmp = lstNivel.get(w).getCodNiv();
			}
		}

		// Carga combo de Estudios
		GenericDao<TiivsEstudio, Object> estudioDAO = (GenericDao<TiivsEstudio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroEstudio = Busqueda.forClass(TiivsEstudio.class);

		try {
			lstEstudio = estudioDAO.buscarDinamico(filtroEstudio);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de estudios");
		}

		int j = 0;

		for (; j <= lstEstudio.size() - 1; j++) {
			estudios.put(lstEstudio.get(j).getDesEstudio(), lstEstudio.get(j)
					.getCodEstudio());
		}

		// Carga combo de Operacion Bancaria
		GenericDao<TiivsOperacionBancaria, Object> openBanDAO = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOpenBan = Busqueda
				.forClass(TiivsOperacionBancaria.class);

		try {
			lstOpeBancaria = openBanDAO.buscarDinamico(filtroOpenBan);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de operaciones bancarias");
		}

		// Carga combo de Tipo de Solicitud
		String hql = "select distinct tsol.cod_oper, ts.des_oper,ts.activo "
				+ " from tiivs_tipo_servicio ts inner join tiivs_solicitud tsol on ts.cod_oper=tsol.cod_oper";

		// System.out.println("Query Busqueda: " + hql);

		logger.debug("Query Tipo Solicitud: " + hql);

		Query query = SpringInit.devolverSession().createSQLQuery(hql)
				.addEntity(TiivsTipoServicio.class);

		lstTipoSolicitud = query.list();

		int x = 0;
		for (; x <= lstTipoSolicitud.size() - 1; x++) {
			tiposSolicitud.put(lstTipoSolicitud.get(x).getDesOper(),
					lstTipoSolicitud.get(x).getCodOper());
		}

		// Carga combo de Territorio
		GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);

		try {
			lstTerritorio = terrDAO.buscarDinamico(filtroTerr);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de territorios");
		}

		// Carga combo de Oficinas
		GenericDao<TiivsOficina1, Object> oficDAO = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfi = Busqueda.forClass(TiivsOficina1.class);
		filtroOfi.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_OFICINA));
		try {
			lstOficina = oficDAO.buscarDinamico(filtroOfi);
			lstOficina1 = oficDAO.buscarDinamico(filtroOfi);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de oficinas");
		}
	}

	public Solicitud getSolicitudModificar() {
		return solicitudModificar;
	}

	public void setSolicitudModificar(Solicitud solicitudModificar) {
		this.solicitudModificar = solicitudModificar;
	}

	public Solicitud getBandeja() {
		return bandeja;
	}

	public void setBandeja(Solicitud bandeja) {
		this.bandeja = bandeja;
	}

	public List<TiivsSolicitud> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(List<TiivsSolicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

	public String getTextoTotalResultados() {
		return textoTotalResultados;
	}

	public void setTextoTotalResultados(String textoTotalResultados) {
		this.textoTotalResultados = textoTotalResultados;
	}

	public TiivsMultitabla getMultitabla() {
		return multitabla;
	}

	public void setMultitabla(TiivsMultitabla multitabla) {
		this.multitabla = multitabla;
	}

	public List<TiivsMultitabla> getLstMultitabla() {
		return lstMultitabla;
	}

	public void setLstMultitabla(List<TiivsMultitabla> lstMultitabla) {
		this.lstMultitabla = lstMultitabla;
	}

	public List<RangosImporte> getLstRangosImporte() {
		return lstRangosImporte;
	}

	public void setLstRangosImporte(List<RangosImporte> lstRangosImporte) {
		this.lstRangosImporte = lstRangosImporte;
	}

	public List<Estado> getLstEstado() {
		return lstEstado;
	}

	public void setLstEstado(List<Estado> lstEstado) {
		this.lstEstado = lstEstado;
	}

	public List<EstadosNivel> getLstEstadoNivel() {
		return lstEstadoNivel;
	}

	public void setLstEstadoNivel(List<EstadosNivel> lstEstadoNivel) {
		this.lstEstadoNivel = lstEstadoNivel;
	}

	public List<TiposFecha> getLstTiposFecha() {
		return lstTiposFecha;
	}

	public void setLstTiposFecha(List<TiposFecha> lstTiposFecha) {
		this.lstTiposFecha = lstTiposFecha;
	}

	public List<TiivsEstudio> getLstEstudio() {
		return lstEstudio;
	}

	public void setLstEstudio(List<TiivsEstudio> lstEstudio) {
		this.lstEstudio = lstEstudio;
	}

	public List<TiivsNivel> getLstNivel() {
		return lstNivel;
	}

	public void setLstNivel(List<TiivsNivel> lstNivel) {
		this.lstNivel = lstNivel;
	}

	public List<TiivsOperacionBancaria> getLstOpeBancaria() {
		return lstOpeBancaria;
	}

	public void setLstOpeBancaria(List<TiivsOperacionBancaria> lstOpeBancaria) {
		this.lstOpeBancaria = lstOpeBancaria;
	}

	public List<TiivsTipoServicio> getLstTipoSolicitud() {
		return lstTipoSolicitud;
	}

	public void setLstTipoSolicitud(List<TiivsTipoServicio> lstTipoSolicitud) {
		this.lstTipoSolicitud = lstTipoSolicitud;
	}

	public List<TiivsTerritorio> getLstTerritorio() {
		return lstTerritorio;
	}

	public void setLstTerritorio(List<TiivsTerritorio> lstTerritorio) {
		this.lstTerritorio = lstTerritorio;
	}

	public List<TiivsOficina1> getLstOficina() {
		return lstOficina;
	}

	public void setLstOficina(List<TiivsOficina1> lstOficina) {
		this.lstOficina = lstOficina;
	}

	public String getIdCodOfi() {
		return idCodOfi;
	}

	public void setIdCodOfi(String idCodOfi) {
		this.idCodOfi = idCodOfi;
	}

	public String getIdCodOfi1() {
		return idCodOfi1;
	}

	public void setIdCodOfi1(String idCodOfi1) {
		this.idCodOfi1 = idCodOfi1;
	}

	public String getIdTerr() {
		return idTerr;
	}

	public void setIdTerr(String idTerr) {
		this.idTerr = idTerr;
	}

	public String getIdTipoSol() {
		return idTipoSol;
	}

	public void setIdTipoSol(String idTipoSol) {
		this.idTipoSol = idTipoSol;
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

	public String getIdEstudio() {
		return idEstudio;
	}

	public void setIdEstudio(String idEstudio) {
		this.idEstudio = idEstudio;
	}

	public String getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(String idEstado) {
		this.idEstado = idEstado;
	}

	public String getIdTiposFecha() {
		return idTiposFecha;
	}

	public void setIdTiposFecha(String idTiposFecha) {
		this.idTiposFecha = idTiposFecha;
	}

	public String getIdNivel() {
		return idNivel;
	}

	public void setIdNivel(String idNivel) {
		this.idNivel = idNivel;
	}

	public String getIdEstNivel() {
		return idEstNivel;
	}

	public void setIdEstNivel(String idEstNivel) {
		this.idEstNivel = idEstNivel;
	}

	public List<TiivsOficina1> getLstOficina1() {
		return lstOficina1;
	}

	public void setLstOficina1(List<TiivsOficina1> lstOficina1) {
		this.lstOficina1 = lstOficina1;
	}

	public String getPoderdante() {
		return poderdante;
	}

	public void setPoderdante(String poderdante) {
		this.poderdante = poderdante;
	}

	public String getCodSolicitud() {
		return codSolicitud;
	}

	public void setCodSolicitud(String codSolicitud) {
		this.codSolicitud = codSolicitud;
	}

	public List<Moneda> getLstMoneda() {
		return lstMoneda;
	}

	public void setLstMoneda(List<Moneda> lstMoneda) {
		this.lstMoneda = lstMoneda;
	}

	public List<TiivsMiembro> getLstMiembros() {
		return lstMiembros;
	}

	public void setLstMiembros(List<TiivsMiembro> lstMiembros) {
		this.lstMiembros = lstMiembros;
	}

	public List<TipoDocumento> getLstTipoDocumentos() {
		return lstTipoDocumentos;
	}

	public void setLstTipoDocumentos(List<TipoDocumento> lstTipoDocumentos) {
		this.lstTipoDocumentos = lstTipoDocumentos;
	}

	public String getNroDOIApoderado() {
		return nroDOIApoderado;
	}

	public void setNroDOIApoderado(String nroDOIApoderado) {
		this.nroDOIApoderado = nroDOIApoderado;
	}

	public String getNroDOIPoderdante() {
		return nroDOIPoderdante;
	}

	public void setNroDOIPoderdante(String nroDOIPoderdante) {
		this.nroDOIPoderdante = nroDOIPoderdante;
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

	public String getTxtNomApoderado() {
		return txtNomApoderado;
	}

	public void setTxtNomApoderado(String txtNomApoderado) {
		this.txtNomApoderado = txtNomApoderado;
	}

	public String getTxtNomPoderdante() {
		return txtNomPoderdante;
	}

	public void setTxtNomPoderdante(String txtNomPoderdante) {
		this.txtNomPoderdante = txtNomPoderdante;
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

	public Boolean getNoMostrarFechas() {
		return noMostrarFechas;
	}

	public void setNoMostrarFechas(Boolean noMostrarFechas) {
		this.noMostrarFechas = noMostrarFechas;
	}

	public String getTxtValTipoFecha() {
		return txtValTipoFecha;
	}

	public void setTxtValTipoFecha(String txtValTipoFecha) {
		this.txtValTipoFecha = txtValTipoFecha;
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

	public String getTxtCodOficina() {
		return txtCodOficina;
	}

	public void setTxtCodOficina(String txtCodOficina) {
		this.txtCodOficina = txtCodOficina;
	}

	public Boolean getConRevision() {
		return conRevision;
	}

	public void setConRevision(Boolean conRevision) {
		this.conRevision = conRevision;
	}

	public Boolean getConDelegados() {
		return conDelegados;
	}

	public void setConDelegados(Boolean conDelegados) {
		this.conDelegados = conDelegados;
	}

	public Boolean getConExonerados() {
		return conExonerados;
	}

	public void setConExonerados(Boolean conExonerados) {
		this.conExonerados = conExonerados;
	}

	public List<String> getLstNivelSelected() {
		return lstNivelSelected;
	}

	public void setLstNivelSelected(List<String> lstNivelSelected) {
		this.lstNivelSelected = lstNivelSelected;
	}

	public Map<String, String> getNiveles() {
		return niveles;
	}

	public List<String> getLstTipoSolicitudSelected() {
		return lstTipoSolicitudSelected;
	}

	public void setLstTipoSolicitudSelected(
			List<String> lstTipoSolicitudSelected) {
		this.lstTipoSolicitudSelected = lstTipoSolicitudSelected;
	}

	public Map<String, String> getTiposSolicitud() {
		return tiposSolicitud;
	}

	public Map<String, String> getEstados() {
		return estados;
	}

	public Map<String, String> getEstadosNivel() {
		return estadosNivel;
	}

	public Map<String, String> getEstudios() {
		return estudios;
	}

	public List<String> getLstEstudioSelected() {
		return lstEstudioSelected;
	}

	public void setLstEstudioSelected(List<String> lstEstudioSelected) {
		this.lstEstudioSelected = lstEstudioSelected;
	}

	public List<String> getLstEstadoNivelSelected() {
		return lstEstadoNivelSelected;
	}

	public void setLstEstadoNivelSelected(List<String> lstEstadoNivelSelected) {
		this.lstEstadoNivelSelected = lstEstadoNivelSelected;
	}

	public List<String> getLstEstadoSelected() {
		return lstEstadoSelected;
	}

	public void setLstEstadoSelected(List<String> lstEstadoSelected) {
		this.lstEstadoSelected = lstEstadoSelected;
	}

	public Boolean getNoHabilitarExportar() {
		return noHabilitarExportar;
	}

	public void setNoHabilitarExportar(Boolean noHabilitarExportar) {
		this.noHabilitarExportar = noHabilitarExportar;
	}

	public List<TiivsHistorialDeSolicitud> getLstHistorial() {
		return lstHistorial;
	}

	public void setLstHistorial(List<TiivsHistorialDeSolicitud> lstHistorial) {
		this.lstHistorial = lstHistorial;
	}

	public List<String> getLstSolicitudesSelected() {
		return lstSolicitudesSelected;
	}

	public void setLstSolicitudesSelected(List<String> lstSolicitudesSelected) {
		this.lstSolicitudesSelected = lstSolicitudesSelected;
	}

	public String getNombreArchivoExcel() {
		return nombreArchivoExcel;
	}

	public void setNombreArchivoExcel(String nombreArchivoExcel) {
		this.nombreArchivoExcel = nombreArchivoExcel;
	}
}

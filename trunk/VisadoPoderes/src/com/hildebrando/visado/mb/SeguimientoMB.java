package com.hildebrando.visado.mb;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.SeguimientoDTO;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsParametros;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsTerritorio;

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
	private Boolean bRevision;
	private Boolean bDelegados;
	private Boolean bRevocatoria;
	private Boolean ocultarControl;
	private Date fechaInicio;
	private Date fechaFin;
	private TiivsOficina1 oficina;
	private Boolean mostrarColumna=true;
	private String nombreArchivoExcel;
	
//	private List<TiivsHistSolicitud> lstHistorial;
	private List<SeguimientoDTO> lstSeguimientoDTO;
	private TiivsSolicitud selectedSolicitud;
	
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	public static Logger logger = Logger.getLogger(SeguimientoMB.class);
	
	public SeguimientoMB()
	{
		solicitudes = new ArrayList<TiivsSolicitud>();
		lstAgrupPer= new ArrayList<TiivsAgrupacionPersona>();
		lstNivelSelected = new ArrayList<String>();
		lstEstudioSelected = new ArrayList<String>();
		lstTipoSolicitudSelected = new ArrayList<String>();
		lstSolicitudesxOpeBan = new ArrayList<String>();

		lstSeguimientoDTO = new ArrayList<SeguimientoDTO>();
				
		oficina= new TiivsOficina1();
		lstSolicitudesSelected = new ArrayList<String>();
		lstHistorial = new ArrayList<TiivsHistSolicitud>();
		
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
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");	
		
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
		}
		
		//Seteo del campo Estudio en caso de que el grupo sea de Servicios Juridicos
		String grupoSSJJ = (String) Utilitarios.getObjectInSession("GRUPO_JRD");
		
		if (grupoSSJJ!=null)
		{
			if (grupoSSJJ.compareTo("")!=0)
			{
				setOcultarControl(true);
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
		filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
		
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes");
		}

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

	private void actualizarDatosGrilla() 
	{	
		String cadena="";
		Double importeTMP=0.0;
		Double rangoIni=0.0;
		Double rangoFin=0.0;
		
		// Se obtiene y setea la descripcion del Estado en la grilla
		for (TiivsSolicitud tmpSol : solicitudes) 
		{
			// Se obtiene y setea la descripcion del Estado en la grilla
			if (tmpSol.getEstado() != null) {
				if (tmpSol.getEstado().trim().equalsIgnoreCase(combosMB.getLstEstado().get(0).getCodEstado())) {
					tmpSol.setTxtEstado(combosMB.getLstEstado().get(0).getDescripcion());
				}
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
			cadena="";
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getId().getCodSoli().equals(tmpSol.getCodSoli()) && tmp.getId().getClasifPer().equals(ConstantesVisado.PODERDANTE))
				{
					if (tmp.getId().getClasifPer().equals(combosMB.getLstTipoRegistroPersona().get(0).getKey()))
					{
						cadena += devolverDesTipoDOI(tmp.getTiivsPersona().getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmp.getTiivsPersona().getNumDoi() +
								  ConstantesVisado.GUION + tmp.getTiivsPersona().getApePat() + " " + tmp.getTiivsPersona().getApeMat() + " " + 
								  tmp.getTiivsPersona().getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
					}
				}
			}
			tmpSol.setTxtPoderdante(cadena);
			
			// Cargar data de apoderados
			cadena="";
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getId().getCodSoli().equals(tmpSol.getCodSoli()) && tmp.getId().getClasifPer().equals(ConstantesVisado.APODERADO))
				{
					if (tmp.getId().getClasifPer().equals(combosMB.getLstTipoRegistroPersona().get(0).getKey()))
					{
						cadena += devolverDesTipoDOI(tmp.getTiivsPersona().getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmp.getTiivsPersona().getNumDoi() +
								  ConstantesVisado.GUION + tmp.getTiivsPersona().getApePat() + " " + tmp.getTiivsPersona().getApeMat() + " " + 
								  tmp.getTiivsPersona().getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
					}
				}
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
			
			//Proceso para obtener los niveles de cada solicitud
			if (tmpSol.getImporte() != 0) 
			{
				if (combosMB.getLstNivel().size() > 0) 
				{
					String txtNivelTMP = "";
					String descripcion = buscarDescripcionMoneda(tmpSol.getMoneda());
					logger.debug("Moneda encontrada: " + descripcion);

					for (TiivsNivel tmp : combosMB.getLstNivel()) 
					{
						if (tmp.getId().getMoneda().equalsIgnoreCase(tmpSol.getMoneda())) 
						{
							if (tmp.getId().getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL1)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni = Double.valueOf(tmp.getId().getRangoInicio());
								rangoFin = Double.valueOf(tmp.getId().getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									txtNivelTMP += ConstantesVisado.CAMPO_NIVEL1;
								}
							}

							if (tmp.getId().getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL2)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni =  Double.valueOf(tmp.getId().getRangoInicio());
								rangoFin =  Double.valueOf(tmp.getId().getRangoFin());

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

							if (tmp.getId().getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL3)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni = Double.valueOf(tmp.getId().getRangoInicio());
								rangoFin = Double.valueOf(tmp.getId().getRangoFin());

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
						}

					}
					
					tmpSol.setTxtNivel(txtNivelTMP);
				}
				else
				{
					/*
					 * txtNivelTMP+=ConstantesVisado.CAMPO_NIVEL4;
					 * tmpSol.setTxtNivel(txtNivelTMP);
					 */
				}
			}
			else 
			{
				logger.debug("No se pudo obtener los rangos de los niveles para la solicitud. Verificar base de datos!!");
			}
		}
		
	}
	
	public void exportarExcelPOI()
	{
		crearExcel();
	}
	
	public void generarNombreArchivo() 
	{
		setNombreArchivoExcel("Solicitudes_Visado "	+ obtenerFechaArchivoExcel() + "_XXXX");
	}

	public String obtenerFechaArchivoExcel() 
	{
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		String nuevaFecha = fecha.substring(0, 2) + "" + fecha.substring(3, 5) + "" + fecha.substring(6, fecha.length());

		return nuevaFecha;
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
			crearTituloCell(wb, trow, 4, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, ConstantesVisado.TITULO_CABECERA_EXCEL);
			
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
			if (getCodSolicitud()!=null)
			{
				crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getCodSolicitud(), true, false,true);
			}
			else
			{
				crearCell(wb, row2, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row2, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO, false, false,false);
			if (lstEstadoSelected!=null)
			{
				String cadena = "";
				int ind=0;
				
				for (; ind <= lstEstadoSelected.size() - 1; ind++) 
				{
					cadena+= buscarEstadoxCodigo(lstEstadoSelected.get(ind))+",";
				}
				
				crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true);
			}
			else
			{
				crearCell(wb, row2, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row2, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_IMPORTE, false, false,false);
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) 
			{
				crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "<50", true, false,true);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
			{
				crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ">50 y <120", true, false,true);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
			{
				crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ">120 y <250", true, false,true);
			}
			else if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
			{
				crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ">250", true, false,true);
			}
			else
			{
				crearCell(wb, row2, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row2, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_SOL, false, false,false);
			
			if (lstTipoSolicitudSelected!=null)
			{
				String cadena = "";
				int ind=0;
				
				for (; ind <= lstTipoSolicitudSelected.size() - 1; ind++) 
				{
					cadena+= buscarTipoSolxCodigo(lstTipoSolicitudSelected.get(ind))+",";
				}
				
				crearCell(wb, row2, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true);
			}
			else
			{
				crearCell(wb, row2, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			Row row3 = sheet.createRow((short) 5);
			crearCell(wb, row3, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_FECHA, false, false,false);
			
			if (getIdTiposFecha().compareTo("")!=0)
			{
				String cadena = "";
				cadena= buscarTipoFechaxCodigo(getIdTiposFecha());
								
				crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true);
			}
			else
			{
				crearCell(wb, row3, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row3, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_INICIO, false, false,false);
			
			if (getFechaInicio()!=null)
			{
				SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				String sFechaInicio = sf1.format(getFechaInicio());
				
				crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, sFechaInicio, true, false,true);
			}
			else
			{
				crearCell(wb, row3, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row3, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_FECHA_FIN, false, false,false);
			
			if (getFechaFin()!=null)
			{
				SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				String sFechaFin = sf1.format(getFechaFin());
				
				crearCell(wb, row3, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER,sFechaFin, true, false,true);
			}
			else
			{
				crearCell(wb, row3, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row3, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_TIPO_OPE, false, false,false);
			
			if (getIdOpeBan().compareTo("")!=0)
			{
				crearCell(wb, row3, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarOpeBanxCodigo(getIdOpeBan()), true, false,true);
			}
			else
			{
				crearCell(wb, row3, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			Row row5 = sheet.createRow((short) 7);
			
			crearCell(wb, row5, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DOI_APODERADO, false, false,false);
			
			if (getNroDOIApoderado()!=null)
			{
				crearCell(wb, row5, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getNroDOIApoderado(), true, false,true);
			}
			else
			{
				crearCell(wb, row5, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row5, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_APODERADO, false, false,false);
			
			if (getTxtNomApoderado()!=null)
			{
				crearCell(wb, row5, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getTxtNomApoderado(), true, false,true);
			}
			else
			{
				crearCell(wb, row5, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row5, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DOI_PODERDANTE, false, false,false);
			
			if (getNroDOIPoderdante()!=null)
			{
				crearCell(wb, row5, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getNroDOIPoderdante(), true, false,true);
			}
			else
			{
				crearCell(wb, row5, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row5, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_PODERDANTE, false, false,false);
			
			if (getTxtNomPoderdante()!=null)
			{
				crearCell(wb, row5, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getTxtNomPoderdante(), true, false,true);
			}
			else
			{
				crearCell(wb, row5, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			Row row6 = sheet.createRow((short) 8);
			crearCell(wb, row6, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_OFICINA, false, false,false);
			
			if (getOficina()!=null)
			{
				crearCell(wb, row6, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, getOficina().getDesOfi(), true, false,true);
			}
			else
			{
				crearCell(wb, row6, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row6, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_NIVEL, false, false,false);
			
			if (getLstNivelSelected()!=null)
			{
				String cadena = "";
				int ind=0;
				
				for (; ind <= getLstNivelSelected().size() - 1; ind++) 
				{
					cadena+= buscarNivelxCodigo(getLstNivelSelected().get(ind))+",";
				}
				
				crearCell(wb, row6, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true);
			}
			else
			{
				crearCell(wb, row6, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row6, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTADO_NIVEL, false, false,false);
			
			if (getLstEstadoNivelSelected()!=null)
			{
				String cadena = "";
				int ind=0;
				
				for (; ind <= getLstEstadoNivelSelected().size() - 1; ind++) 
				{
					cadena+= buscarEstNivelxCodigo(getLstEstadoNivelSelected().get(ind))+",";
				}
				
				crearCell(wb, row6, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true);
			}
			else
			{
				crearCell(wb, row6, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			crearCell(wb, row6, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_ESTUDIO,false, false,false);
			
			if (getLstEstudioSelected()!=null)
			{
				String cadena = "";
				int ind=0;
				
				for (; ind <= getLstEstudioSelected().size() - 1; ind++) 
				{
					cadena+= buscarEstudioxCodigo(getLstEstudioSelected().get(ind))+",";
				}
				
				crearCell(wb, row6, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, cadena, true, false,true);
			}
			else
			{
				crearCell(wb, row6, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
			}
			
			Row row7 = sheet.createRow((short) 9);
			crearCell(wb, row7, 1, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_REVISION, false, false,false);
			
			if (bRevision)
			{
				crearCell(wb, row7, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true);
			}
			else
			{
				crearCell(wb, row7, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
			}
			
			crearCell(wb, row7, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_DELEGADO, false, false,false);
			
			if (bDelegados)
			{
				crearCell(wb, row7, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true);
			}
			else
			{
				crearCell(wb, row7, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
			}
			
			crearCell(wb, row7, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_FILTRO_BUS_REVOCATORIA, false, false,false);
			
			if (bRevocatoria)
			{
				crearCell(wb, row7, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "Si", true, false,true);
			}
			else
			{
				crearCell(wb, row7, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "No", true, false,true);
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
				crearCell(wb, rowT, 16, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_COMISION, true, true,false);
				crearCell(wb, rowT, 17, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_LIBERADO, true, true,false);
				crearCell(wb, rowT, 18, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_DELEGADO, true, true,false);
				crearCell(wb, rowT, 19, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_EN_REVISION, true, true,false);
				crearCell(wb, rowT, 20, CellStyle.ALIGN_CENTER,
						CellStyle.VERTICAL_CENTER, ConstantesVisado.ETIQUETA_COLUMNA_REVOCATORIA, true, true,false);
				
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
					crearCell(wb, row, 2, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsOficina1().getCodOfi()),true, false,true);
					
					//Columna Oficina en Excel
					crearCell(wb, row, 3, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsOficina1().getDesOfi()),true, false,true);
					
					//Columna Territorio en Excel
					crearCell(wb, row, 4, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, buscarDesTerritorio(tmp.getTiivsOficina1().getTiivsTerritorio().getCodTer()),true, false,true);
					
					//Columna Estado en Excel
					crearCell(wb, row, 5, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtEstado(), true, false,true);
					
					//Columna Importe en Excel
					crearCell(wb, row, 6, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtImporte(), true, false,true);
					
					//Columna Poderdante en Excel
					crearCell(wb, row, 7, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtPoderdante(), true, false,true);
					
					//Columna Apoderado en Excel
					crearCell(wb, row, 8, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtApoderado(), true, false,true);
					
					//Columna Tipo Solicitud en Excel
					crearCell(wb, row, 9, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsTipoSolicitud().getDesTipServicio()), true, false,true);
					
					//Columna Operaciones Bancarias en Excel
					crearCell(wb, row, 10, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, tmp.getTxtOpeBan(), true, false,true);
					
					//Columna Estudio en Excel
					if (tmp.getTiivsEstudio()!=null)
					{
						crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(tmp.getTiivsEstudio().getDesEstudio()) , true, false,true);
					}
					else
					{
						crearCell(wb, row, 11, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, validarCampoNull(null),true,false,true);
					}
					
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
					
					//Columna Comision en Excel
					if (tmp.getComision()!=null)
					{
						crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, String.valueOf(tmp.getComision()), true, false,true);
					}
					else
					{
						crearCell(wb, row, 16, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					}
					
					//Columna Liberado
					crearCell(wb, row, 17, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					
					//Columna Delegado
					crearCell(wb, row, 18, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					
					//Columna En Revision
					crearCell(wb, row, 19, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					
					//Columna Revocatoria
					crearCell(wb, row, 20, CellStyle.ALIGN_LEFT,CellStyle.VERTICAL_CENTER, "", true, false,true);
					
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
				strRuta = obtenerRutaExcel() + getNombreArchivoExcel() + ConstantesVisado.EXTENSION_XLS;
				FileOutputStream fileOut = new FileOutputStream(strRuta);
				wb.write(fileOut);
				
				fileOut.close();
				
				//Abrir archivo excel
				Desktop.getDesktop().open(new File(strRuta));  
			}
			else
			{
				logger.info("No se pudo encontrar la ruta para exportar el archivo de excel.");
			}
			
		} catch (Exception e) {
			logger.info("Error al generar el archivo excel debido a: " +e.getMessage());
		}	
	}
	
	public String obtenerRutaExcel()
	{
		String res="";
		
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		
		for (TiivsParametros tmp: pdfViewerMB.getLstParametros())
		{
			if (usuario.getUID().equals(tmp.getCodUsuario()))
			{
				res=tmp.getRutaArchivoExcel();
				break;
			}
		}
		
		return res;
	}
	
	private String validarCampoNull(String campo)
	{
		String resultado="";
		if (campo==null)
		{
			resultado="";
		}
		else
		{
			resultado=campo;
		}
		return resultado;
	}
	
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

	
	// Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a
	// los filtros seleccionados.
	// @Autor: Cesar La Rosa
	// @Version: 3.0
	// @param: -
	@SuppressWarnings("unchecked")
	public void busquedaSolicitudes() 
	{
		logger.info("Buscando solicitudes");
		
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		LocalDate newFechaInicio = null;
		LocalDate newFechaFin = null;

		// solicitudes = new ArrayList<TiivsSolicitud>();

		// 1. Filtro por codigo de solicitud (funciona)
		if (getCodSolicitud().compareTo("") != 0) 
		{
			logger.debug("Filtro por codigo de solicitud: " + getCodSolicitud());
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,	getCodSolicitud()));
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
				logger.debug("Filtro por importe: " + getIdImporte());
				filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_IMPORTE,(ConstantesVisado.VALOR_RANGO_CINCUENTA)));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
			{
				logger.debug("Filtro por importe: " + getIdImporte());
				filtroSol.add(Restrictions.gt(ConstantesVisado.CAMPO_IMPORTE,ConstantesVisado.VALOR_RANGO_CINCUENTA));
				filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_IMPORTE,ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
			{
				logger.debug("Filtro por importe: " + getIdImporte());
				filtroSol.add(Restrictions.gt(ConstantesVisado.CAMPO_IMPORTE,(ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE)));
				filtroSol.add(Restrictions.le(ConstantesVisado.CAMPO_IMPORTE,(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
			{
				logger.debug("Filtro por importe: " + getIdImporte());
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
				DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy HH:mm:ss");
				DateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				
				String tmpFecIni = formato.format(getFechaInicio());
				String tmpFecFin = formato.format(getFechaFin());
				
				newFechaInicio=fmt.parseLocalDate(tmpFecIni);
				newFechaFin= fmt.parseLocalDate(tmpFecFin);
				
				Date dateIni = newFechaInicio.toDateTimeAtStartOfDay().toDate();
				Date dateFin = newFechaFin.toDateTimeAtStartOfDay().toDate();
				
				logger.debug("Filtro por fecha de envio...");
				logger.info("Fecha Inicio: " + dateIni);
				logger.info("Fecha Fin: " + dateFin);
				
				filtroSol.add(Restrictions.between(ConstantesVisado.CAMPO_FECHA_ENVIO, dateIni,dateFin));
				
				String codEstado = buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_ENVIADO);
				
				logger.info("Estado enviado: " + codEstado);
				
				//Verificar que el campo estado no tenga espacios en blanco en BD
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codEstado));
				//filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,codEstado+ "   "));
				filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
			}
			if (getIdTiposFecha().equalsIgnoreCase(ConstantesVisado.TIPO_FECHA_RPTA)) // Sino es fecha de respuesta
			{
				DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy");
				DateFormat formato = new SimpleDateFormat("dd/MM/yy");
				
				String tmpFecIni = formato.format(getFechaInicio());
				String tmpFecFin = formato.format(getFechaFin());
				
				newFechaInicio=fmt.parseLocalDate(tmpFecIni);
				newFechaFin= fmt.parseLocalDate(tmpFecFin);
				
				Date dateIni = newFechaInicio.toDateTimeAtStartOfDay().toDate();
				Date dateFin = newFechaFin.toDateTimeAtStartOfDay().toDate();
				
				logger.debug("Filtro por fecha de rpta...");
				logger.info("Fecha Inicio: " + dateIni);
				logger.info("Fecha Fin: " + dateFin);
				
				filtroSol.add(Restrictions.between(ConstantesVisado.CAMPO_FECHA_RPTA, dateIni,	dateFin));

				Collection<String> tmpEstados = null;
				tmpEstados.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_ACEPTADO));
				tmpEstados.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_RECHAZADO));
				tmpEstados.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A));
				
				//Verificar que el campo estado no tenga espacios en blanco en BD
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,tmpEstados));
				
				filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
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
			
			logger.debug("Filtro por operacion bancaria");
			
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
				logger.debug("No se selecciono ninguna operacion bancaria");
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,""));
			}
			
			//filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_OPE_BANCARIAS,	getIdOpeBan()));
		}

		// 8. Filtro por nombre de oficina (funciona)
		if (getOficina() != null) 
		{
			logger.debug("Filtro Oficina: " + getOficina());
			
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,	ConstantesVisado.ALIAS_TBL_OFICINA);
			String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE + getOficina().getDesOfi().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_NOM_OFICINA_ALIAS, filtroNuevo));
		}

		// 11. Filtro por numero de documento de apoderado (funciona)
		if (getNroDOIApoderado().compareTo("") != 0) 
		{
			String codSol="";
			
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getTiivsPersona().getNumDoi().equals(getNroDOIApoderado()) && tmp.getId().getClasifPer().equals(ConstantesVisado.APODERADO))
				{
					codSol = tmp.getId().getCodSoli();
					break;
				}
			}
			
			logger.debug("Filtro por numero de documento apoderado: " + getNroDOIApoderado());
			
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}

		// 12. Filtro por nombre de apoderado (funciona)
		if (getTxtNomApoderado().compareTo("") != 0) 
		{
			//String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE + getTxtNomApoderado().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			//filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_APODERADO, filtroNuevo));
			
			String codSol="";
			
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if ((tmp.getTiivsPersona().getNombre().indexOf(getTxtNomApoderado().toUpperCase())!=-1  
					|| tmp.getTiivsPersona().getApeMat().indexOf(getTxtNomApoderado().toUpperCase())!=-1 
					|| tmp.getTiivsPersona().getApePat().indexOf(getTxtNomApoderado().toUpperCase())!=-1)
					
					&& tmp.getId().getClasifPer().equals(ConstantesVisado.APODERADO))
				{
					codSol = tmp.getId().getCodSoli();
					break;
				}
			}
			
			logger.debug("Filtro por apoderado: " + getTxtNomApoderado());
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}
		
		// 13. Filtro por numero de documento de poderdante (funciona)
		if (getNroDOIPoderdante().compareTo("") != 0) 
		{
			//filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_NUMDOC_PODERDANTE,	getNroDOIPoderdante()));
			
			String codSol="";
			
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getTiivsPersona().getNumDoi().equals(getNroDOIPoderdante()) && tmp.getId().getClasifPer().equals(ConstantesVisado.PODERDANTE))
				{
					codSol = tmp.getId().getCodSoli();
					break;
				}
			}
			
			logger.debug("Filtro por nro documento poderdante: " + getNroDOIPoderdante());
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}

		// 14. Filtro por nombre de poderdante (funciona)
		if (getTxtNomPoderdante().compareTo("") != 0) 
		{
			/*String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE + getTxtNomPoderdante().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_PODERDANTE,filtroNuevo));*/
			
			String codSol="";
			
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if ((tmp.getTiivsPersona().getNombre().indexOf(getTxtNomPoderdante().toUpperCase())!=-1  
					|| tmp.getTiivsPersona().getApeMat().indexOf(getTxtNomPoderdante().toUpperCase())!=-1 
					|| tmp.getTiivsPersona().getApePat().indexOf(getTxtNomPoderdante().toUpperCase())!=-1)
					
					&& tmp.getId().getClasifPer().equals(ConstantesVisado.PODERDANTE))
				{
					codSol = tmp.getId().getCodSoli();
					break;
				}
			}
			
			logger.debug("Filtro por poderdante: " + getTxtNomPoderdante());
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}

		// 15. Filtro por nivel (funciona)
		if (lstNivelSelected.size() > 0) 
		{
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
			
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,	lstSolicitudesSelected));
		}
		
		// 16. Filtro por estado nivel (funciona)
		if (lstEstadoNivelSelected.size() > 0)
		{
			GenericDao<TiivsSolicitudNivel, Object> busqSolNivDAO = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsSolicitudNivel.class);
			List<TiivsSolicitudNivel> lstSolNivel = new ArrayList<TiivsSolicitudNivel>();
			filtro.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO, lstEstadoNivelSelected));
			
			try {
				lstSolNivel = busqSolNivDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.debug("Error al buscar los estados de los niveles en las solicitudes");
			}
			
			lstSolicitudesSelected.clear();
			
			for (TiivsSolicitudNivel sol : lstSolNivel) 
			{
				for (TiivsSolicitud soli: solicitudes)
				{	
					if (sol.getId().getCodSoli().equals(soli.getCodSoli()) && soli.getTxtNivel().length()>0)
					{
						lstSolicitudesSelected.add(sol.getId().getCodSoli());
					}
				}
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

			try {
				lstHistorial = busqHisDAO.buscarDinamico(filtro);
			} catch (Exception e) {
				logger.debug("Error al buscar en historial de solicitudes");
			}

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
						if (tmp.getId().getCodSoli().equals(hist.getId().getCodSoli()))
						{
							lstSolicitudesSelected.add(hist.getId().getCodSoli());
						}
					}
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

			if (lstSolicitudesSelected.size() > 0) 
			{
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD, lstSolicitudesSelected));
			} 
			else 
			{
				logger.info("No hay solicitudes en el historial con estado Revocado");
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,""));
			}
		}
		
		// Buscar solicitudes de acuerdo a criterios seleccionados
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes");
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
	
	public String buscarCodigoEstado(String estado) 
	{
		int i = 0;
		String codigo = "";
		for (; i <= combosMB.getLstEstado().size(); i++) {
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
		for (; i <= combosMB.getLstNivel().size(); i++) {
			if (combosMB.getLstNivel().get(i).getId().getCodNiv().equalsIgnoreCase(codigo)) {
				res = combosMB.getLstNivel().get(i).getId().getDesNiv();
				break;
			}
		}
		return res;
	}
	
	public String buscarEstNivelxCodigo(String codigo) 
	{
		int i = 0;
		String res = "";
		for (; i <= combosMB.getLstEstadoNivel().size(); i++) {
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
		for (; i <= combosMB.getLstEstudio().size(); i++) {
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
		for (; i <= combosMB.getLstOpeBancaria().size(); i++) {
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
		for (; i <= combosMB.getLstTipoSolicitud().size(); i++) {
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
		for (; i <= combosMB.getLstTiposFecha().size(); i++) {
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
	
	public void buscarOficinaPorCodigo(ValueChangeEvent e) 
	{
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
		
		if (lstTmp.size() == 1) 
		{
			setTxtNomOficina(lstTmp.get(0).getDesOfi());
			setTxtNomTerritorio(buscarDesTerritorio(lstTmp.get(0).getTiivsTerritorio().getCodTer()));
		}
	}
	
	public String buscarDesTerritorio(String codigoTerritorio) 
	{
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
	
	public TiivsTerritorio buscarTerritorioPorOficina(String codOficina)
	{
		int i=0;
		int j=0;
		String codTerr="";
		String desTerr="";
		for (;i<=combosMB.getLstOficina().size();i++)
		{
			if (combosMB.getLstOficina().get(i).getCodOfi().equals(codOficina))
			{
				codTerr=combosMB.getLstOficina().get(i).getTiivsTerritorio().getCodTer();
				break;
			}
		}
		
		if (codTerr!="")
		{
			for (;j<=combosMB.getLstTerritorio().size();j++)
			{
				if (combosMB.getLstTerritorio().get(j).getCodTer().equals(codTerr))
				{
					desTerr=combosMB.getLstTerritorio().get(j).getDesTer();
					break;
				}
			}
		}
		
		TiivsTerritorio terrTMP = new TiivsTerritorio();
		terrTMP.setCodTer(codTerr);
		terrTMP.setDesTer(desTerr);
		
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
				seg.setNivel("");
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

	public Boolean getOcultarControl() {
		return ocultarControl;
	}

	public void setOcultarControl(Boolean ocultarControl) {
		this.ocultarControl = ocultarControl;
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

}

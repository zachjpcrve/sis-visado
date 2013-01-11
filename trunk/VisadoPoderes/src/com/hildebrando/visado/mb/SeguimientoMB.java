package com.hildebrando.visado.mb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsTerritorio;

@ManagedBean(name = "seguimientoMB")
@SessionScoped
public class SeguimientoMB 
{
	private List<TiivsSolicitud> solicitudes;
	private List<TiivsAgrupacionPersona> lstAgrupPer;
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
	private String txtValTipoFecha;
	private Boolean noHabilitarExportar;
	private Boolean noMostrarFechas;
	private Boolean conRevision;
	private Boolean conDelegados;
	private Boolean conExonerados;
	private Boolean ocultarControl;
	private Date fechaInicio;
	private Date fechaFin;
	private TiivsOficina1 oficina;
	
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
		oficina= new TiivsOficina1();
		
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
						
				textoOficina =codOfi + " "
						+ desOfi+ " (" + terr.getCodTer() + "-" + terr.getDesTer() + ")";
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
		//crearExcel();
	}
	
	// Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a
	// los filtros seleccionados.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void busquedaSolicitudes() 
	{
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		Date newFechaInicio = null;
		Date newFechaFin = null;

		// solicitudes = new ArrayList<TiivsSolicitud>();

		// 1. Filtro por codigo de solicitud		
		if (getCodSolicitud().compareTo("") != 0) 
		{
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,	getCodSolicitud()));
		}

		// 2. Filtro por estado
		if (lstEstadoSelected.size() > 0) 
		{
			// String codEstado=getIdEstado().trim();
			// System.out.println("Filtro estado: " + codEstado);
			// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
			// codEstado));
			int ind = 0;

			for (; ind <= lstEstadoSelected.size() - 1; ind++) 
			{
				logger.info("Estados: " + lstEstadoSelected.get(ind));
				// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,
				// lstEstadoSelected.get(ind)));
			}

			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,lstEstadoSelected));
		}

		// 3. Filtro por importe
		if (getIdImporte().compareTo("") != 0) 
		{
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) 
			{
				filtroSol.add(Restrictions.le(
					ConstantesVisado.CAMPO_IMPORTE,
					(ConstantesVisado.VALOR_RANGO_CINCUENTA)));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
			{
				filtroSol.add(Restrictions.gt(
								ConstantesVisado.CAMPO_IMPORTE,
								ConstantesVisado.VALOR_RANGO_CINCUENTA));
				filtroSol.add(Restrictions.le(
								ConstantesVisado.CAMPO_IMPORTE,
								ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
			{
				filtroSol.add(Restrictions.gt(
								ConstantesVisado.CAMPO_IMPORTE,
								(ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE)));
				filtroSol
						.add(Restrictions.le(
								ConstantesVisado.CAMPO_IMPORTE,
								(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}

			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
			{
				filtroSol.add(Restrictions.gt(
								ConstantesVisado.CAMPO_IMPORTE,
								(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}
		}

		// 4. Filtro por tipo de solicitud
		if (lstTipoSolicitudSelected.size() > 0) {
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_TIPO_SOLICITUD,	ConstantesVisado.ALIAS_TBL_TIPO_SOLICITUD);
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_TIPO_SOL_ALIAS,lstTipoSolicitudSelected));
		}

		// 5. Filtro por tipo de fecha
		if (getIdTiposFecha().compareTo("") != 0) 
		{
			if (getIdTiposFecha().equalsIgnoreCase(ConstantesVisado.TIPO_FECHA_ENVIO)) // Es fecha de envio
			{
				SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				sf1.setTimeZone(TimeZone.getTimeZone("America/Bogota"));

				String sFechaInicio=sf1.format(getFechaInicio());
				String sFechaFin=sf1.format(getFechaFin());
				
				try {
					newFechaInicio = sf1.parse(sFechaInicio);
					newFechaFin = sf1.parse(sFechaFin);
				} catch (ParseException e) {
					logger.debug("Error al convertir la fecha de String a Date");
				}
				
				Calendar calFecIni = Calendar.getInstance();
				calFecIni.setTime(newFechaInicio);
				
				Calendar calFecFin = Calendar.getInstance();
				calFecFin.setTime(newFechaFin);
				
				logger.info("newFechaInicio: " + calFecIni.getTime());
				logger.info("newFechaFin: " + calFecFin.getTime());
				
				filtroSol.add(Restrictions.between(ConstantesVisado.CAMPO_FECHA_ENVIO, newFechaInicio,newFechaFin));
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTADO,buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_ENVIADO)));
			}
			if (getIdTiposFecha().equalsIgnoreCase(ConstantesVisado.TIPO_FECHA_RPTA)) // Sino es fecha de respuesta
			{
				filtroSol.add(Restrictions.between(ConstantesVisado.CAMPO_FECHA_RPTA, getFechaInicio(),	getFechaFin()));

				Collection<String> tmpEstados = null;
				tmpEstados.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_ACEPTADO));
				tmpEstados.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_RECHAZADO));
				tmpEstados.add(buscarCodigoEstado(ConstantesVisado.CAMPO_ESTADO_VERIFICACION_A));

				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTADO,tmpEstados));
			}
		}

		// 6. Filtro por operacion bancaria
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
			
			if (lstSolicitudesxOpeBan.size()>0)
			{
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,lstSolicitudesxOpeBan));
			}
			else
			{
				filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,""));
			}
			
			//filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_OPE_BANCARIAS,	getIdOpeBan()));
		}

		// 8. Filtro por nombre de oficina
		if (getOficina() != null) 
		{
			filtroSol.createAlias(ConstantesVisado.NOM_TBL_OFICINA,	ConstantesVisado.ALIAS_TBL_OFICINA);
			String filtroNuevo = ConstantesVisado.SIMBOLO_PORCENTAJE + getOficina().getDesOfi().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like(ConstantesVisado.CAMPO_NOM_OFICINA_ALIAS, filtroNuevo));
		}

		// 11. Filtro por numero de documento de apoderado
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
			
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}

		// 12. Filtro por nombre de apoderado
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
			
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}
		
		// 13. Filtro por numero de documento de poderdante
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
			
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}

		// 14. Filtro por nombre de poderdante
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
			
			filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_SOLICITUD,codSol));
		}

		// 15. Filtro por nivel
		if (lstNivelSelected.size() > 0) {
			
			for (TiivsSolicitud sol : solicitudes) {
				if (sol.getTxtNivel() != null && sol.getTxtNivel().length() > 0) {
					if (lstNivelSelected.get(0).indexOf(sol.getTxtNivel()) != -1) {
						lstSolicitudesSelected.add(sol.getCodSoli());
					}
				}
			}
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD,	lstSolicitudesSelected));
		}

		// 17. Filtro por estudio
		if (lstEstudioSelected.size() > 0) {
			
			// filtroSol.add(Restrictions.eq(ConstantesVisado.CAMPO_ESTUDIO,
			// getIdEstudio()));
			filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_ESTUDIO,lstEstudioSelected));
		}

		// 19. Filtrar solicitudes con revision
		/*if (getConRevision()) 
		{
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
				filtroSol.add(Restrictions.in(ConstantesVisado.CAMPO_COD_SOLICITUD, lstHistorial));
			} else {
				System.out.println("No hay solicitudes en el historial con estado En Revision");
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
		}*/

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

	public String getTxtValTipoFecha() {
		return txtValTipoFecha;
	}

	public void setTxtValTipoFecha(String txtValTipoFecha) {
		this.txtValTipoFecha = txtValTipoFecha;
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
}

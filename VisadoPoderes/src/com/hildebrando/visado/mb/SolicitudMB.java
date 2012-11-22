package com.hildebrando.visado.mb;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
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
public class SolicitudMB 
{
	Solicitud solicitudModificar;
	Solicitud bandeja;
	private List<TiivsSolicitud> solicitudes;
	private String textoTotalResultados;
	private TiivsMultitabla multitabla;
	private List<TiivsMultitabla> lstMultitabla;
	private List<RangosImporte> lstRangosImporte;
	private List<Estado> lstEstado;
	private List<EstadosNivel> lstEstadoNivel;
	private List<TiposFecha> lstTiposFecha;
	private List<TiivsEstudio> lstEstudio;
	private List<TiivsNivel> lstNivel;
	private List<TiivsOperacionBancaria> lstOpeBancaria;
	private List<TiivsTipoServicio> lstTipoSolicitud;
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
	
	public static Logger logger = Logger.getLogger(SolicitudMB.class);
	
	public SolicitudMB() 
	{
		solicitudModificar = new Solicitud();
		solicitudes= new ArrayList<TiivsSolicitud>();
		lstMultitabla=new ArrayList<TiivsMultitabla>();
		lstRangosImporte=new ArrayList<RangosImporte>();
		lstEstado = new ArrayList<Estado>();
		lstEstadoNivel=new ArrayList<EstadosNivel>();
		lstTiposFecha= new ArrayList<TiposFecha>();
		lstEstudio= new ArrayList<TiivsEstudio>();
		lstNivel = new ArrayList<TiivsNivel>();
		lstOpeBancaria=new ArrayList<TiivsOperacionBancaria>();
		lstTipoSolicitud=new ArrayList<TiivsTipoServicio>();
		lstTerritorio=new ArrayList<TiivsTerritorio>();
		lstOficina=new ArrayList<TiivsOficina1>();
		lstOficina1=new ArrayList<TiivsOficina1>();
		lstMoneda = new ArrayList<Moneda>();
		lstTipoDocumentos= new ArrayList<TipoDocumento>();
		cargarMultitabla();		
		//Carga combo Rango Importes
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_IMPORTES);
		//Carga combo Estados
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS);
		//Carga combo Estados Nivel
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL);
		//Carga combo Tipos de Fecha
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA);
		//Carga lista de monedas
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_MONEDA);
		cargarMiembros();
		//LLena la grilla de solicitudes
		cargarSolicitudes();
		cargaCombosNoMultitabla();
		if (solicitudes.size()==0)
		{
			setearTextoTotalResultados("No se encontraron coincidencias con los criterios ingresados",solicitudes.size());
		}
		else
		{
			setearTextoTotalResultados("Total de registros: "+ solicitudes.size() + " registros",solicitudes.size());
		}
		setNoMostrarFechas(true);
	}
	
	private void setearTextoTotalResultados(String cadena, int total)
	{
		if (total==1)
		{
			setTextoTotalResultados("Total de registros: "+ total + " registro");
		}
		else
		{
			setTextoTotalResultados(cadena);
		}
	}
	
	//Descripcion: Metodo que se encarga de buscar las solicitudes de acuerdo a los filtros seleccionados.
	//@Autor: Cesar La Rosa
	//@Version: 1.0
	//@param: -
	public void busquedaSolicitudes()
	{
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol= Busqueda.forClass(TiivsSolicitud.class);
		
		solicitudes = new ArrayList<TiivsSolicitud>();
		
		//Filtro por codigo de solicitud
		if (getCodSolicitud().compareTo("")!=0)
		{
			filtroSol.add(Restrictions.eq("codSoli", getCodSolicitud()));
		}
		
		//Filtro por combo de codigos de oficina
		if (getIdCodOfi().compareTo("")!=0)
		{
			filtroSol.createAlias("tiivsOficina1", "ofic");
			filtroSol.add(Restrictions.eq("ofic.codOfi", getIdCodOfi()));
		}
		
		//Filtro por combo de oficinas
		if (getIdCodOfi1().compareTo("")!=0)
		{
			filtroSol.createAlias("tiivsOficina1", "ofic");
			filtroSol.add(Restrictions.eq("ofic.codOfi", getIdCodOfi1()));
		}
		
		//Filtro por operacion bancaria
		if (getIdOpeBan().compareTo("")!=0)
		{
			filtroSol.add(Restrictions.eq("operacionesBancarias", getIdOpeBan()));
		}
		
		//Filtro por importe
		if (getIdImporte().compareTo("")!=0)
		{
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA))
			{
				filtroSol.add(Restrictions.le("importe", BigDecimal.valueOf(ConstantesVisado.VALOR_RANGO_CINCUENTA)));
			}
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE))
			{
				filtroSol.add(Restrictions.gt("importe", BigDecimal.valueOf(ConstantesVisado.VALOR_RANGO_CINCUENTA)));
				filtroSol.add(Restrictions.le("importe", BigDecimal.valueOf(ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE)));
			}
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA))
			{
				filtroSol.add(Restrictions.gt("importe", BigDecimal.valueOf(ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE)));
				filtroSol.add(Restrictions.le("importe", BigDecimal.valueOf(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA))
			{
				filtroSol.add(Restrictions.gt("importe", BigDecimal.valueOf(ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA)));
			}
		}
		
		//Filtro por codigo de oficina
		if (getTxtCodOfi().compareTo("")!=0)
		{
			filtroSol.createAlias("tiivsOficina1", "ofic");
			filtroSol.add(Restrictions.eq("ofic.codOfi", getTxtCodOfi()));
		}
		
		//Filtro por nombre de oficina
		if (getTxtNomOfi().compareTo("")!=0)
		{
			filtroSol.createAlias("tiivsOficina1", "ofic");
			String filtroNuevo=ConstantesVisado.SIMBOLO_PORCENTAJE + getTxtNomOfi().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like("ofic.desOfi", filtroNuevo));
		}
		
		//Filtro por nombre de poderdante
		if (getTxtNomPoderdante().compareTo("")!=0)
		{
			String filtroNuevo=ConstantesVisado.SIMBOLO_PORCENTAJE + getTxtNomPoderdante().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like("poderante", filtroNuevo));
		}
		
		//Filtro por nombre de apoderado
		if (getTxtNomApoderado().compareTo("")!=0)
		{
			String filtroNuevo=ConstantesVisado.SIMBOLO_PORCENTAJE + getTxtNomApoderado().concat(ConstantesVisado.SIMBOLO_PORCENTAJE);
			filtroSol.add(Restrictions.like("apoderado", filtroNuevo));
		}
		
		//Filtro por estado
		if (getIdEstado().compareTo("")!=0)
		{
			String codEstado=getIdEstado().trim();
			System.out.println("Filtro estado: " + codEstado);
			filtroSol.add(Restrictions.eq("estado", codEstado));
		}
		
		//Filtro por numero de documento de poderdante
		if (getNroDOIPoderdante().compareTo("")!=0)
		{
			filtroSol.add(Restrictions.eq("numDocPoder", getNroDOIPoderdante()));
		}
		
		//Filtro por numero de documento de apoderado
		if (getNroDOIApoderado().compareTo("")!=0)
		{
			filtroSol.add(Restrictions.eq("numDocApoder", getNroDOIApoderado()));
		}
		
		//Filtro por estudio
		if (getIdEstudio().compareTo("")!=0)
		{
			filtroSol.add(Restrictions.eq("regAbogado", getIdEstudio()));
		}
		
		//Filtro por tipo de fecha
		if (getIdTiposFecha().compareTo("")!=0)
		{
			if (getIdTiposFecha().equalsIgnoreCase(ConstantesVisado.TIPO_FECHA_ENVIO)) // Es fecha de envio
			{
				filtroSol.add(Restrictions.between("fechaEnvio", getFechaInicio(),getFechaFin()));
			}
			if (getIdTiposFecha().equalsIgnoreCase(ConstantesVisado.TIPO_FECHA_RPTA)) //Sino es fecha de respuesta
			{
				filtroSol.add(Restrictions.between("fechaRespuesta", getFechaInicio(),getFechaFin()));
			}
		}
		
		//Filtro por tipo de solicitud
		if (getIdTipoSol().compareTo("")!=0)
		{
			filtroSol.createAlias("tiivsTipoServicio", "tipoSer");
			filtroSol.add(Restrictions.eq("tipoServ.codOper", getIdTipoSol()));
		}
		
		//Filtro por territorio
		if (getIdTerr().compareTo("")!=0)
		{
			String codTerr=getIdTerr().trim();
			System.out.println("Buscando territorio: " + codTerr);
			filtroSol.createAlias("tiivsOficina1", "ofic");
			filtroSol.add(Restrictions.eq("ofic.codTerr", codTerr));
		}
		
		//Buscar solicitudes de acuerdo a criterios seleccionados
		try
		{
			solicitudes =  solicDAO.buscarDinamico(filtroSol);
		}
		catch (Exception ex)
		{
			logger.debug("Error al buscar las solicitudes");
		}
		
		if (solicitudes.size()==0)
		{
			setearTextoTotalResultados("No se encontraron coincidencias con los criterios ingresados",solicitudes.size());
		}
		else
		{
			setearTextoTotalResultados("Total de registros: "+ solicitudes.size() + " registros",solicitudes.size());
			actualizarDatosGrilla();
		}
	}
	
	private void actualizarDatosGrilla()
	{
		String nomTipoDocPoder="";
		String nomTipoDocApod="";
		
		for (TiivsSolicitud tmpSol: solicitudes)
		{
			//Se setea la descripcion de la columna moneda
			if (tmpSol.getMoneda()!=null)
			{
				tmpSol.setDesMoneda(buscarDescripcionMoneda(tmpSol.getMoneda()));
			}
			
			//Se setea la columna estudio
			if (tmpSol.getRegAbogado()!=null)
			{
				tmpSol.setEstudio(buscarDescripcionEstudio(tmpSol.getRegAbogado()));
			}
			
			//Se obtiene la descripcion del documento del Poderdante
			if (tmpSol.getNumDocPoder()!=null)
			{
				if (tmpSol.getTipDocPoder().equalsIgnoreCase(lstTipoDocumentos.get(0).getCodTipoDoc()))
				{
					nomTipoDocPoder=lstTipoDocumentos.get(0).getDescripcion();
				}
			}
			
			//Seteo de la columna Poderdante
			if (nomTipoDocPoder.compareTo("")==0)
			{
				tmpSol.setTxtPoderdante(tmpSol.getPoderante());
			}
			else
			{
				tmpSol.setTxtPoderdante(nomTipoDocPoder + ": " + tmpSol.getNumDocPoder() + " - " + tmpSol.getPoderante());
			}
			
			//Se obtiene la descripcion del documento del Apoderado
			if (tmpSol.getNumDocApoder()!=null)
			{
				if (tmpSol.getTipDocApoder().equalsIgnoreCase(lstTipoDocumentos.get(0).getCodTipoDoc()))
				{
					nomTipoDocApod=lstTipoDocumentos.get(0).getDescripcion();
				}
			}
			
			//Seteo de la columna Apoderado
			if (nomTipoDocApod.compareTo("")==0)
			{
				tmpSol.setTxtApoderado(tmpSol.getApoderado());
			}
			else
			{
				tmpSol.setTxtApoderado(nomTipoDocApod + ": " + tmpSol.getNumDocApoder() + " - " + tmpSol.getApoderado());
			}
			
			//Se obtiene y setea la descripcion del Estado en la grilla
			if (tmpSol.getEstado()!=null)
			{
				if (tmpSol.getEstado().trim().equalsIgnoreCase(lstEstado.get(0).getCodEstado()))
				{
					tmpSol.setTxtEstado(lstEstado.get(0).getDescripcion());
				}
			}
		}
	}
	
	//Descripcion: Metodo que se encarga de cargar las solicitudes en la grilla
	//@Autor: Cesar La Rosa
	//@Version: 1.0
	//@param: -
	public void cargarSolicitudes()
	{
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol= Busqueda.forClass(TiivsSolicitud.class);
				
		try {
			solicitudes =  solicDAO.buscarDinamico(filtroSol);
			//System.out.println("Tamanio inicial de solicitudes: " + solicitudes.size());
		} catch (Exception ex) {
			logger.debug("Error al buscar las solicitudes");
		}
		
		actualizarDatosGrilla();
	}
	
	public String buscarDescripcionMoneda(String codMoneda)
	{
		String descripcion="";
		for (Moneda tmpMoneda: lstMoneda)
		{
			if (tmpMoneda.getCodMoneda().equalsIgnoreCase(codMoneda))
			{
				descripcion = tmpMoneda.getDesMoneda();
				break;
			}
		}
		return descripcion;
	}
	
	public String buscarDescripcionEstudio(String codEstudio)
	{
		String descripcion="";
		for (TiivsMiembro tmpMiembros: lstMiembros)
		{
			if (tmpMiembros.getCodMiembro().equalsIgnoreCase(codEstudio))
			{
				descripcion = tmpMiembros.getDescripcion();
				break;
			}
		}
		return descripcion;
	}
	
	public void cargarMiembros()
	{
		GenericDao<TiivsMiembro, Object> miemDAO = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMiem= Busqueda.forClass(TiivsMiembro.class);
		
		try {
			lstMiembros =  miemDAO.buscarDinamico(filtroMiem);
			
		} catch (Exception ex) {
			logger.debug("Error al buscar registros de la tabla miembro");
		}
	}
	
	//Descripcion: Metodo que se encarga de cargar los datos que se encuentran en la multitabla. Este metodo se llamara en el constructor 
	//			   de la clase para que este disponible al inicio.
	//@Autor: Cesar La Rosa
	//@Version: 1.0
	//@param: -
	public void cargarMultitabla()
	{
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);
				
		try {
			lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de multitablas");
		}
	}
	
	public TiivsOficina1 buscarOficinaPorCodigo(ValueChangeEvent e) {
		logger.debug("Buscando oficina por codigo: " + e.getNewValue());
		System.out.println("Buscando oficina por codigo: " + e.getNewValue());
		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq("codOfi", e.getNewValue()));
		TiivsOficina1 tmpOfic = new TiivsOficina1();
		List<TiivsOficina1> lstTmp = new ArrayList<TiivsOficina1>();
		
		try {
			lstTmp = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}

		if (lstTmp.size()==1)
		{
			tmpOfic.setCodOfi(lstTmp.get(0).getCodOfi());
			tmpOfic.setDesOfi(lstTmp.get(0).getDesOfi());
		}
		return tmpOfic;
	}
	
	public void habilitarFechas(ValueChangeEvent e) 
	{
		if (e.getNewValue()!=null)
		{
			setNoMostrarFechas(false);
		}
		else
		{
			setNoMostrarFechas(true);
		}
	}
	
	//Descripcion: Metodo que se encarga de cargar los combos que se mostraran en la pantalla de Bandeja de solicitudes 
	//			   de acuerdo a la lista de la multitabla previamente cargada.
	//@Autor: Cesar La Rosa
	//@Version: 1.0
	//@param: -
	public void cargarCombosMultitabla(String codigo)
	{
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
		
		for (TiivsMultitabla res: lstMultitabla)
 		{
			//Carga combo importes
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_IMPORTES))
			{
				RangosImporte tmpRangos = new RangosImporte();
				tmpRangos.setCodigoRango(res.getId().getCodElem());
				tmpRangos.setDescripcion(res.getValor1());
				lstRangosImporte.add(tmpRangos);
				
				logger.debug("Tamanio lista de importes: " + lstRangosImporte.size());
			}
			
			//Carga combo estados
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS))
			{
				Estado tmpEstado = new Estado();
				tmpEstado.setCodEstado(res.getId().getCodElem());
				tmpEstado.setDescripcion(res.getValor1());
				lstEstado.add(tmpEstado);
				
				logger.debug("Tamanio lista de estados: " + lstEstado.size());
			}
			
			//Carga combo estados Nivel
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL))
			{
				EstadosNivel tmpEstadoNivel = new EstadosNivel();
				tmpEstadoNivel.setCodigoEstadoNivel(res.getId().getCodElem());
				tmpEstadoNivel.setDescripcion(res.getValor1());
				lstEstadoNivel.add(tmpEstadoNivel);
				
				logger.debug("Tamanio lista de estados nivel: " + lstEstadoNivel.size());
			}
			
			//Carga combo Tipos de fecha
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA))
			{
				TiposFecha tmpTiposFecha = new TiposFecha();
				tmpTiposFecha.setCodigoTipoFecha(res.getId().getCodElem());
				tmpTiposFecha.setDescripcion(res.getValor1());
				lstTiposFecha.add(tmpTiposFecha);
				
				logger.debug("Tamanio lista de tipos de fecha: " + lstTiposFecha.size());
			}
			
			//Carga lista de monedas
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_MONEDA))
			{
				Moneda tmpMoneda = new Moneda();
				tmpMoneda.setCodMoneda(res.getId().getCodElem());
				tmpMoneda.setDesMoneda(res.getValor1());
				lstMoneda.add(tmpMoneda);
				
				logger.debug("Tamanio lista de monedas: " + lstMoneda.size());
			}
			
			//Carga combo Tipos de documento
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC))
			{
				TipoDocumento tmpTipoDoc = new TipoDocumento();
				tmpTipoDoc.setCodTipoDoc(res.getId().getCodElem());
				tmpTipoDoc.setDescripcion(res.getValor1());
				lstTipoDocumentos.add(tmpTipoDoc);
				
				logger.debug("Tamanio lista de tipos de documento: " + lstTipoDocumentos.size());
			}
		}
	}
	
	public void cargaCombosNoMultitabla()
	{
		//Carga combo de Estudios
		GenericDao<TiivsEstudio, Object> estudioDAO = (GenericDao<TiivsEstudio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroEstudio = Busqueda.forClass(TiivsEstudio.class);
		
		try {
			lstEstudio = estudioDAO.buscarDinamico(filtroEstudio);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de estudios");
		}
		
		//Carga combo de Nivel
		GenericDao<TiivsNivel, Object> nivelDAO = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroNivel = Busqueda.forClass(TiivsNivel.class);
		
		try {
			lstNivel = nivelDAO.buscarDinamico(filtroNivel);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de niveles");
		}
		
		//Carga combo de Operacion Bancaria
		GenericDao<TiivsOperacionBancaria, Object> openBanDAO = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOpenBan = Busqueda.forClass(TiivsOperacionBancaria.class);
		
		try {
			lstOpeBancaria = openBanDAO.buscarDinamico(filtroOpenBan);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de operaciones bancarias");
		}
		
		//Carga combo de Tipo de Solicitud
		String hql = "select distinct tsol.cod_oper, ts.des_oper,ts.activo " +
				" from tiivs_tipo_servicio ts inner join tiivs_solicitud tsol on ts.cod_oper=tsol.cod_oper";
		
		System.out.println("Query Busqueda: " + hql);
		
		logger.debug("Query Tipo Solicitud: " + hql);

		Query query = SpringInit.devolverSession().createSQLQuery(hql)
				.addEntity(TiivsTipoServicio.class);
		
		lstTipoSolicitud = query.list();		
		
		//Carga combo de Territorio
		GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);
		
		try {
			lstTerritorio = terrDAO.buscarDinamico(filtroTerr);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de territorios");
		}
		
		//Carga combo de Oficinas
		GenericDao<TiivsOficina1, Object> oficDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfi = Busqueda.forClass(TiivsOficina1.class);
		filtroOfi.addOrder(Order.asc("codOfi"));
		try {
			lstOficina = oficDAO.buscarDinamico(filtroOfi);
			lstOficina1=oficDAO.buscarDinamico(filtroOfi);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de oficinas");
		}
	}
	
	public Solicitud getSolicitudModificar() 
	{
		return solicitudModificar;
	}

	public void setSolicitudModificar(Solicitud solicitudModificar) 
	{
		this.solicitudModificar = solicitudModificar;
	}

	public Solicitud getBandeja() 
	{
		return bandeja;
	}

	public void setBandeja(Solicitud bandeja) 
	{
		this.bandeja = bandeja;
	}

	public List<TiivsSolicitud> getSolicitudes() 
	{
		return solicitudes;
	}

	public void setSolicitudes(List<TiivsSolicitud> solicitudes) 
	{
		this.solicitudes = solicitudes;
	}

	public String getTextoTotalResultados() 
	{
		return textoTotalResultados;
	}

	public void setTextoTotalResultados(String textoTotalResultados) 
	{
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
}

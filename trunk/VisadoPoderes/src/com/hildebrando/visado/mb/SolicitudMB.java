package com.hildebrando.visado.mb;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
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
	private String idCodOfi;
	private String idCodOfi1;
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
		cargarMultitabla();		
		//Carga combo Rango Importes
		cargarCombosBandejaSeguimiento(ConstantesVisado.CODIGO_MULTITABLA_IMPORTES);
		//Carga combo Estados
		cargarCombosBandejaSeguimiento(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS);
		//Carga combo Estados Nivel
		cargarCombosBandejaSeguimiento(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL);
		//Carga combo Tipos de Fecha
		cargarCombosBandejaSeguimiento(ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA);
		//Carga lista de monedas
		cargarCombosBandejaSeguimiento(ConstantesVisado.CODIGO_MULTITABLA_MONEDA);
		cargarMiembros();
		//LLena la grilla de solicitudes
		cargarSolicitudes();
		
		if (solicitudes.size()==0)
		{
			textoTotalResultados="No se encontraron coincidencias con los criterios ingresados";
		}
		else
		{
			textoTotalResultados="Total de registros: "+ solicitudes.size() + " registros";
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
		
		if (getCodSolicitud().compareTo("")!=0)
		{
			filtroSol.add(Restrictions.eq("codSoli", getCodSolicitud()));
		}
		
		if (getIdCodOfi().compareTo("")!=0)
		{
			System.out.println("Buscando por codigo oficina: " + getIdCodOfi());
			filtroSol.createAlias("tiivsOficina1", "ofic");
			filtroSol.add(Restrictions.eq("ofic.codOfi", getIdCodOfi()));
		}
		
		if (getIdCodOfi1().compareTo("")!=0)
		{
			filtroSol.createAlias("tiivsOficina1", "ofic");
			filtroSol.add(Restrictions.eq("ofic.codOfi", getIdCodOfi1()));
		}
		
		if (getIdOpeBan().compareTo("")!=0)
		{
			filtroSol.add(Restrictions.eq("operacionesBancarias", getIdOpeBan()));
		}
		
		if (getIdImporte().compareTo("")!=0)
		{
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA))
			{
				filtroSol.add(Restrictions.le("importe", ConstantesVisado.VALOR_RANGO_CINCUENTA));
			}
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE))
			{
				filtroSol.add(Restrictions.gt("importe", ConstantesVisado.VALOR_RANGO_CINCUENTA));
				filtroSol.add(Restrictions.le("importe", ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE));
			}
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA))
			{
				filtroSol.add(Restrictions.gt("importe", ConstantesVisado.VALOR_RANGO_CIENTO_VEINTE));
				filtroSol.add(Restrictions.le("importe", ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA));
			}
			
			if (getIdImporte().equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA))
			{
				filtroSol.add(Restrictions.gt("importe", ConstantesVisado.VALOR_RANGO_DOSCIENTOS_CINCUENTA));
			}
		}
		
		try {
			solicitudes =  solicDAO.buscarDinamico(filtroSol);
		} catch (Exception ex) {
			logger.debug("Error al buscar las solicitudes");
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
			System.out.println("Tamanio inicial de solicitudes: " + solicitudes.size());
		} catch (Exception ex) {
			logger.debug("Error al buscar las solicitudes");
		}
		
		for (TiivsSolicitud tmpSol: solicitudes)
		{
			if (tmpSol.getMoneda()!=null)
			{
				if (tmpSol.getMoneda().equalsIgnoreCase(lstMoneda.get(0).getCodMoneda()))
				{
					tmpSol.setDesMoneda(lstMoneda.get(0).getDesMoneda());
				}
			}
			if (tmpSol.getRegAbogado()!=null)
			{
				if (tmpSol.getRegAbogado().equals(lstMiembros.get(0).getCodMiembro()))
				{
					tmpSol.setEstudio(lstMiembros.get(0).getEstudio());
				}
			}
		}
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
		GenericDao<TiivsOficina1, Object> ofiDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfic = Busqueda.forClass(TiivsOficina1.class);
		filtroOfic.add(Restrictions.eq("codOfi", e.getNewValue()));
		TiivsOficina1 tmpOfic = new TiivsOficina1();
		
		try {
			lstOficina1 = ofiDAO.buscarDinamico(filtroOfic);
		} catch (Exception exp) {
			logger.debug("No se pudo encontrar el nombre de la oficina");
		}

		/*for (TiivsOficina1 of : lstOficina) {
			if (lstOficina.size() == 1) {
				tmpOfic.setCodOfi(of.getCodOfi());
				tmpOfic.setDesOfi(of.getDesOfi());
			}
		}*/
		
		return tmpOfic;
	}
	
	//Descripcion: Metodo que se encarga de cargar los combos que se mostraran en la pantalla de Bandeja de solicitudes 
	//			   de acuerdo a la lista de la multitabla previamente cargada.
	//@Autor: Cesar La Rosa
	//@Version: 1.0
	//@param: -
	public void cargarCombosBandejaSeguimiento(String codigo)
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
		}
		
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
		GenericDao<TiivsTipoServicio, Object> tipoSolDAO = (GenericDao<TiivsTipoServicio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSol = Busqueda.forClass(TiivsTipoServicio.class);
		
		try {
			lstTipoSolicitud = tipoSolDAO.buscarDinamico(filtroTipoSol);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de tipos de solicitudes");
		}
		
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
}

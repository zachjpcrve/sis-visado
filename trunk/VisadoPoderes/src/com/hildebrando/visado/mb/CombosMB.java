package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.Estado;
import com.hildebrando.visado.dto.EstadosNivel;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.RangosImporte;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.dto.TiposFecha;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsTerritorio;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

//@Autor Samira Benazar
@ManagedBean(name = "combosMB")
@SessionScoped
public class CombosMB {
 	public static Logger logger = Logger.getLogger(CombosMB.class);
	
	
	private List<TiivsMultitabla> lstMultitabla;
	private List<RangosImporte> lstRangosImporte;
	private List<Estado> lstEstado;
	private List<EstadosNivel> lstEstadoNivel;
	private List<TiposFecha> lstTiposFecha;
	private List<TiivsEstudio> lstEstudio;
	private List<TiivsNivel> lstNivel;
	private List<TiivsOperacionBancaria> lstOpeBancaria;
	private List<TiivsTerritorio> lstTerritorio;
	private List<TiivsOficina1> lstOficina;
	private List<TiivsOficina1> lstOficina1;
	private List<Moneda> lstMoneda;
	private List<TipoDocumento> lstTipoDocumentos;
	private List<TiivsAgrupacionPersona> lstTiposPersona;
	private List<TiivsSolicitudOperban> lstSolOperBan;
	private Map<String, String> estados;
	private Map<String, String> niveles;
	private Map<String, String> estadosNivel;
	private Map<String, String> estudios;
	private Map<String, String> tiposSolicitud;
	private List<ComboDto> lstClasificacionPersona;
	private List<ComboDto> lstTipoRegistroPersona;
	private List<TiivsMiembro> lstAbogados;

	private List<TiivsTipoSolicitud> lstTipoSolicitud;
	
	public CombosMB() {
		lstMultitabla = new ArrayList<TiivsMultitabla>();
		lstRangosImporte = new ArrayList<RangosImporte>();
		lstTipoDocumentos=new ArrayList<TipoDocumento>();
		lstEstado = new ArrayList<Estado>();
		lstEstadoNivel = new ArrayList<EstadosNivel>();
		lstTiposFecha = new ArrayList<TiposFecha>();
		lstEstudio = new ArrayList<TiivsEstudio>();
		lstNivel = new ArrayList<TiivsNivel>();
		lstOpeBancaria = new ArrayList<TiivsOperacionBancaria>();
		lstTerritorio = new ArrayList<TiivsTerritorio>();
		lstOficina = new ArrayList<TiivsOficina1>();
		lstOficina1 = new ArrayList<TiivsOficina1>();
		lstMoneda = new ArrayList<Moneda>();
		estados = new HashMap<String, String>();
		niveles = new HashMap<String, String>();
		tiposSolicitud = new HashMap<String, String>();
		estudios = new HashMap<String, String>();
		estadosNivel = new HashMap<String, String>();
		lstClasificacionPersona=new ArrayList<ComboDto>();
		lstTipoRegistroPersona=new ArrayList<ComboDto>();
		lstTipoSolicitud=new ArrayList<TiivsTipoSolicitud>();
		lstSolOperBan= new ArrayList<TiivsSolicitudOperban>();
		lstAbogados=new ArrayList<TiivsMiembro>();
		
		cargarMultitabla();
		cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA);
		cargarCombosNoMultitabla();
	}
	
	public List<TipoDocumento> getLstTipoDocumentos() {
		return lstTipoDocumentos;
	}

	public void setLstTipoDocumentos(List<TipoDocumento> lstTipoDocumentos) {
		this.lstTipoDocumentos = lstTipoDocumentos;
	}

	// Descripcion: Metodo que se encarga de cargar los datos que se encuentran
	// en la multitabla. Este metodo se llamara en el constructor
	// de la clase para que este disponible al inicio.
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void cargarMultitabla() {
		GenericDao<TiivsMultitabla, Object> multiDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultitabla = Busqueda.forClass(TiivsMultitabla.class);

		try {
			lstMultitabla = multiDAO.buscarDinamico(filtroMultitabla);
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de multitablas");
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
		lstTerritorio.clear();
		lstOficina.clear();
		lstClasificacionPersona.clear();
		lstTipoRegistroPersona.clear();

		for (TiivsMultitabla res : lstMultitabla) { 
			//@Autor Samira
			// Carga combo Clasificacion Persona
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_CLASIFICACION_PERSONA)) {
				ComboDto tmpComboClasi = new ComboDto();
				tmpComboClasi.setKey(res.getId().getCodElem());
				tmpComboClasi.setDescripcion(res.getValor1());
				lstClasificacionPersona.add(tmpComboClasi);
			}
			
			//@Autor Samira
			// Carga combo Tipo Registro Persona
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA)) {
				ComboDto tmpCombo = new ComboDto();
				tmpCombo.setKey(res.getId().getCodElem());
				tmpCombo.setDescripcion(res.getValor1());
				lstTipoRegistroPersona.add(tmpCombo);
			}
			
			// Carga combo importes
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_IMPORTES)) {
				RangosImporte tmpRangos = new RangosImporte();
				tmpRangos.setCodigoRango(res.getId().getCodElem());
				tmpRangos.setDescripcion(res.getValor1());
				lstRangosImporte.add(tmpRangos);

				
			}

			// Carga combo estados
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS)) {
				Estado tmpEstado = new Estado();
				tmpEstado.setCodEstado(res.getId().getCodElem());
				tmpEstado.setDescripcion(res.getValor1());
				lstEstado.add(tmpEstado);

				int j = 0;

				for (; j <= lstEstado.size() - 1; j++) {
					estados.put(lstEstado.get(j).getDescripcion(), lstEstado.get(j).getCodEstado());
				}

				
			}

			// Carga combo estados Nivel
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL)) {
				EstadosNivel tmpEstadoNivel = new EstadosNivel();
				tmpEstadoNivel.setCodigoEstadoNivel(res.getId().getCodElem());
				tmpEstadoNivel.setDescripcion(res.getValor1());
				lstEstadoNivel.add(tmpEstadoNivel);


				int j = 0;

				for (; j <= lstEstadoNivel.size() - 1; j++) {
					estadosNivel.put(lstEstadoNivel.get(j).getDescripcion(),
							lstEstadoNivel.get(j).getCodigoEstadoNivel());
				}

				
			}

			// Carga combo Tipos de fecha
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA)) {
				TiposFecha tmpTiposFecha = new TiposFecha();
				tmpTiposFecha.setCodigoTipoFecha(res.getId().getCodElem());
				tmpTiposFecha.setDescripcion(res.getValor1());
				lstTiposFecha.add(tmpTiposFecha);

				
			}

			// Carga lista de monedas
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_MONEDA)) {
				Moneda tmpMoneda = new Moneda();
				tmpMoneda.setCodMoneda(res.getId().getCodElem());
				tmpMoneda.setDesMoneda(res.getValor1());
				lstMoneda.add(tmpMoneda);

				
			}

			// Carga combo Tipos de documento
			if (res.getId().getCodMult().equalsIgnoreCase(ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC)) {
				TipoDocumento tmpTipoDoc = new TipoDocumento();
				tmpTipoDoc.setCodTipoDoc(res.getId().getCodElem());
				tmpTipoDoc.setDescripcion(res.getValor1());
				lstTipoDocumentos.add(tmpTipoDoc);

				logger.debug("Tamanio lista de tipos de documento: "+ lstTipoDocumentos.size());
			}
		}
		
		logger.debug("Tamanio lista de Clasificacion de Personas: "+ lstClasificacionPersona.size());
		logger.debug("Tamanio lista de Tipo de Registro de Personas: "+ lstTipoRegistroPersona.size());
		logger.debug("Tamanio lista de importes: "+ lstRangosImporte.size());
		logger.debug("Tamanio lista de estados: " + lstEstado.size());
		logger.debug("Tamanio lista de estados nivel: "+ lstEstadoNivel.size());
		logger.debug("Tamanio lista de estados: " + lstEstado.size());
		logger.debug("Tamanio lista de tipos de fecha: "+ lstTiposFecha.size());
		logger.debug("Tamanio lista de monedas: " + lstMoneda.size());

	}
	
	
	public void cargarCombosNoMultitabla(){
		try {
		GenericDao<TiivsMiembro, Object> serviceMiembro = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMiembro = Busqueda.forClass(TiivsMiembro.class);
		filtroMiembro.add(Restrictions.eq("tiivsGrupo.codGrupo", ConstantesVisado.CODIGO_GRUPO_ABOGADOS));
		lstAbogados=serviceMiembro.buscarDinamico(filtroMiembro);
		}catch (Exception e) {
			logger.debug("Error al cargar el listado de abogados - miembro");
		}
		
		// Carga combo de Operacion Bancaria
		GenericDao<TiivsOperacionBancaria, Object> openBanDAO = (GenericDao<TiivsOperacionBancaria, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOpenBan = Busqueda.forClass(TiivsOperacionBancaria.class);
       try {
			lstOpeBancaria = openBanDAO.buscarDinamico(filtroOpenBan);
			logger.debug("lstOpeBancaria size " + lstOpeBancaria.size());
		} catch (Exception e) {
			logger.debug("Error al cargar el listado de operaciones bancarias");
		}
       
        // Carga combo de Tipo de Solicitud
		GenericDao<TiivsTipoSolicitud, Object> genTipoSolcDAO = (GenericDao<TiivsTipoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTipoSolc = Busqueda.forClass(TiivsTipoSolicitud.class);

		try {
			lstTipoSolicitud = genTipoSolcDAO.buscarDinamico(filtroTipoSolc);
			logger.debug(" lstTipoSolicitud.size() " +lstTipoSolicitud.size());
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de tipos de solicitud ");
		}
		
		int x = 0;
		for (; x <= lstTipoSolicitud.size() - 1; x++) 
		{
			tiposSolicitud.put(lstTipoSolicitud.get(x).getDesTipServicio(),	lstTipoSolicitud.get(x).getCodTipSolic());
		}
		
		// Carga combo de Territorio
		GenericDao<TiivsTerritorio, Object> terrDAO = (GenericDao<TiivsTerritorio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTerr = Busqueda.forClass(TiivsTerritorio.class);

		try {
			lstTerritorio = terrDAO.buscarDinamico(filtroTerr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de territorios");
		}

		// Carga combo de Oficinas
		GenericDao<TiivsOficina1, Object> oficDAO = (GenericDao<TiivsOficina1, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOfi = Busqueda.forClass(TiivsOficina1.class);
		filtroOfi.add(Restrictions.isNotNull("tiivsTerritorio.codTer"));
		filtroOfi.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_OFICINA));
		try {
			lstOficina = oficDAO.buscarDinamico(filtroOfi);
			lstOficina1 = oficDAO.buscarDinamico(filtroOfi);
			logger.info("TAMANIOO DE LA LSTOFICIN EN EL COMBOMB " +lstOficina.size());
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de oficinas");
		}
		
		// Carga data de Agrupacion de Personas
		GenericDao<TiivsAgrupacionPersona, Object> agrupPerDAO = (GenericDao<TiivsAgrupacionPersona, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroPer = Busqueda.forClass(TiivsAgrupacionPersona.class);
		filtroPer.addOrder(Order.asc(ConstantesVisado.CAMPO_ID_CODIGO_SOLICITUD_ALIAS));
		
		try {
			lstTiposPersona = agrupPerDAO.buscarDinamico(filtroPer);
			logger.debug("TAMANIOO DE LOS TIPOS DE PERSONA EN EL COMBOMB " +lstTiposPersona.size());
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de tipos de persona");
		}
		
		// Carga data de Operaciones Bancarias por Solicitud
		GenericDao<TiivsSolicitudOperban, Object> operBanDAO = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroOperBan = Busqueda.forClass(TiivsSolicitudOperban.class);
		filtroOperBan.addOrder(Order.asc(ConstantesVisado.CAMPO_ID_CODIGO_SOLICITUD_ALIAS));
		
		try {
			lstSolOperBan = operBanDAO.buscarDinamico(filtroOperBan);
			logger.debug("TAMANIOO DE LAS OPERACIONES BANCARIAS POR SOLICITUD EN EL COMBOMB " +lstSolOperBan.size());
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error al cargar el listado de tipos de persona");
		}
		
		// Carga combo Nivel
		GenericDao<TiivsNivel, Object> nivelDAO = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
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
			if (tmp.compareTo(lstNivel.get(w).getId().getDesNiv()) != 0) {
				niveles.put(lstNivel.get(w).getId().getDesNiv(), lstNivel.get(w).getId().getDesNiv());
				tmp = lstNivel.get(w).getId().getCodNiv();
			}
		}
		
		// Carga combo de Estudios
		GenericDao<TiivsEstudio, Object> estudioDAO = (GenericDao<TiivsEstudio, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
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


	public List<Moneda> getLstMoneda() {
		return lstMoneda;
	}


	public void setLstMoneda(List<Moneda> lstMoneda) {
		this.lstMoneda = lstMoneda;
	}


	public Map<String, String> getEstados() {
		return estados;
	}


	public void setEstados(Map<String, String> estados) {
		this.estados = estados;
	}


	public Map<String, String> getEstadosNivel() {
		return estadosNivel;
	}


	public void setEstadosNivel(Map<String, String> estadosNivel) {
		this.estadosNivel = estadosNivel;
	}


	public List<ComboDto> getLstClasificacionPersona() {
		return lstClasificacionPersona;
	}


	public void setLstClasificacionPersona(List<ComboDto> lstClasificacionPersona) {
		this.lstClasificacionPersona = lstClasificacionPersona;
	}


	public List<ComboDto> getLstTipoRegistroPersona() {
		return lstTipoRegistroPersona;
	}


	public void setLstTipoRegistroPersona(List<ComboDto> lstTipoRegistroPersona) {
		this.lstTipoRegistroPersona = lstTipoRegistroPersona;
	}

	

	public List<TiivsTipoSolicitud> getLstTipoSolicitud() {
		return lstTipoSolicitud;
	}

	public void setLstTipoSolicitud(List<TiivsTipoSolicitud> lstTipoSolicitud) {
		this.lstTipoSolicitud = lstTipoSolicitud;
	}

	public List<TiivsAgrupacionPersona> getLstTiposPersona() {
		return lstTiposPersona;
	}

	public void setLstTiposPersona(List<TiivsAgrupacionPersona> lstTiposPersona) {
		this.lstTiposPersona = lstTiposPersona;
	}

	public List<TiivsSolicitudOperban> getLstSolOperBan() {
		return lstSolOperBan;
	}

	public void setLstSolOperBan(List<TiivsSolicitudOperban> lstSolOperBan) {
		this.lstSolOperBan = lstSolOperBan;
	}

	public Map<String, String> getTiposSolicitud() {
		return tiposSolicitud;
	}

	public void setTiposSolicitud(Map<String, String> tiposSolicitud) {
		this.tiposSolicitud = tiposSolicitud;
	}

	public List<TiivsOficina1> getLstOficina1() {
		return lstOficina1;
	}

	public void setLstOficina1(List<TiivsOficina1> lstOficina1) {
		this.lstOficina1 = lstOficina1;
	}

	public Map<String, String> getNiveles() {
		return niveles;
	}

	public void setNiveles(Map<String, String> niveles) {
		this.niveles = niveles;
	}

	public Map<String, String> getEstudios() {
		return estudios;
	}

	public void setEstudios(Map<String, String> estudios) {
		this.estudios = estudios;
	}

	public List<TiivsMiembro> getLstAbogados() {
		return lstAbogados;
	}

	public void setLstAbogados(List<TiivsMiembro> lstAbogados) {
		this.lstAbogados = lstAbogados;
	}

	public List<TiivsMultitabla> getLstMultitabla() {
		return lstMultitabla;
	}	
}

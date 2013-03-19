package com.hildebrando.visado.mb;

import java.nio.charset.CodingErrorAction;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.AgrupacionDelegadosDto;
import com.hildebrando.visado.dto.AgrupacionNivelDelegadoDto;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.GrupoDto;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.service.DelegadosService;
import com.hildebrando.visado.service.NivelService;

@ManagedBean(name = "delegadosMB")
@SessionScoped
public class DelegadosMB {

	public static Logger logger = Logger.getLogger(DelegadosMB.class);
	private SimpleDateFormat formatear = new SimpleDateFormat("dd/MM/yyyy");
	private DateFormat df = DateFormat.getDateInstance();
	private List<AgrupacionNivelDelegadoDto> lstListaAgrupacionesNivelesDelegados;
	private TiivsMiembroNivel miembroNivel;
	private TiivsMiembroNivel miembroNivelEditar;
	private List<TiivsNivel> listaNiveles;
	private DelegadosService delegadosService;
	private IILDPeUsuario usuario;
	private List<TiivsMiembro> miembros;
	private List<TiivsMiembroNivel> listaDelegados;
	private List<TiivsMiembroNivel> listaDelegadosEditar;
	private List<TiivsMiembroNivel> listaDelegadosEditarCopia;
	private List<TiivsMiembroNivel> listaDelegadosEditarEliminar;
	private List<TiivsMiembroNivel> listaDelegadosEditarEstado;
	private TiivsMiembroNivel delegado;
	private boolean validarCodRegistro;
	private boolean validarCodRegistroEditar;
	private String codRegistro;
	private String desRegistro;
	private String perfilRegistro;
	private String criterioRegistro;

	private String codRegistroEditar;
	private String desRegistroEditar;
	private String perfilRegistroEditar;
	private String criterioRegistroEditar;

	private int iDelegadoGrupo;
	private String sDelegadoEstado;
	//boolean esDelegadoEditar=false;
	boolean esDelegado=false;
	public DelegadosMB() {
		criterioRegistro = "";
		codRegistro = "";
		desRegistro = "";
		perfilRegistro = "";
		codRegistroEditar = "";
		desRegistroEditar = "";
		perfilRegistroEditar = "";
		criterioRegistroEditar = "";

		usuario = (IILDPeUsuario) Utilitarios
				.getObjectInSession("USUARIO_SESION");
		validarCodRegistro = false;
		validarCodRegistroEditar = false;
		delegado = new TiivsMiembroNivel();
		listaDelegadosEditar = new ArrayList<TiivsMiembroNivel>();
		listaDelegados = new ArrayList<TiivsMiembroNivel>();
		listaDelegadosEditarCopia = new ArrayList<TiivsMiembroNivel>();
		delegadosService = new DelegadosService();
		lstListaAgrupacionesNivelesDelegados = new ArrayList<AgrupacionNivelDelegadoDto>();
		listarAgrupacionesDelegados();
		miembroNivel = new TiivsMiembroNivel();
		miembroNivelEditar = new TiivsMiembroNivel();
		listarNiveles();
		listaDelegadosEditarEliminar = new ArrayList<TiivsMiembroNivel>();
		listaDelegadosEditarEstado = new ArrayList<TiivsMiembroNivel>();
		
		iDelegadoGrupo = 0;
		sDelegadoEstado = null;
	}

	public void listarNiveles() {
		logger.info("DelegadosMB : listarNiveles");

		try {
			listaNiveles = delegadosService.listarNiveles();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : listarNiveles"
					+ ex.getLocalizedMessage());
		}
	}

	public void obtenerDatosMiembro() {
		logger.info("DelegadosMB : obtenerDatosMiembro");
		try {
			if (!codRegistro.equals("")) {
				if (miembroNivel.getCodNiv()!=null) {
				miembros = delegadosService.obtenerDatosMiembro(codRegistro.toUpperCase());
				 esDelegado=false;
				if (miembros.size() > 0) {
					for (TiivsMiembroNivel x : miembros.get(0).getTiivsMiembroNivels()) {
						if(x.getTipoRol().equals("R")&&x.getCodNiv().equals(miembroNivel.getCodNiv())){
							esDelegado=true;
							break;
						}else{
							esDelegado=false;
						}
					}
					if(esDelegado){
					Utilitarios.mensajeInfo("Info", "La Persona ya tiene rol de Responsable, no puede ser Delegado del mismo Nivel");
					desRegistro = miembros.get(0).getDescripcion();
					perfilRegistro = miembros.get(0).getTiivsGrupo().getDesGrupo();
					}else{
						desRegistro = miembros.get(0).getDescripcion();
						perfilRegistro = miembros.get(0).getTiivsGrupo().getDesGrupo();
						criterioRegistro = miembros.get(0).getCriterio();
						validarCodRegistro = true;
						esDelegado=false;
					}
				   }else {
						Utilitarios
						.mensajeError("Error","No se encuentra Registrado el codigo del Delegado");
				    desRegistro = "";
				    perfilRegistro = "";
				    criterioRegistro = "";
				    validarCodRegistro = false;
			        }
				}else{
					Utilitarios.mensajeInfo("Info", "Seleccione un Nivel");
				}		
				} else {
					Utilitarios.mensajeError("Info","Ingrese el código del delegado");
					desRegistro = "";
					perfilRegistro = "";
					criterioRegistro = "";
					validarCodRegistro = false;
				}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : obtenerDatosMiembro"
					+ ex.getLocalizedMessage());
		}
	}

	public void obtenerDatosMiembroEditar() {
		logger.info("DelegadosMB : obtenerDatosMiembroEditar");
		try {
			if (!codRegistroEditar.equals("")) {
				if (miembroNivelEditar.getCodNiv()!=null) {
				miembros = delegadosService.obtenerDatosMiembro(codRegistroEditar.toUpperCase());
				
				if (miembros.size() > 0) {
					for (TiivsMiembroNivel x : miembros.get(0).getTiivsMiembroNivels()) {
						if(x.getTipoRol().equals("R")&&x.getCodNiv().equals(miembroNivelEditar.getCodNiv())){
							esDelegado=true;
							break;
						}else{
							esDelegado=false;
						}
					}
					if(esDelegado){
					Utilitarios.mensajeInfo("Info", "La Persona ya tiene rol de Responsable, no puede ser Delegado del mismo Nivel");
					desRegistroEditar = miembros.get(0).getDescripcion();
					perfilRegistroEditar = miembros.get(0).getTiivsGrupo().getDesGrupo();
					}else{
						desRegistroEditar = miembros.get(0).getDescripcion();
						perfilRegistroEditar = miembros.get(0).getTiivsGrupo().getDesGrupo();
						criterioRegistroEditar = miembros.get(0).getCriterio();
						validarCodRegistroEditar = true;
						esDelegado=false;
					}
				   }else {
						Utilitarios
						.mensajeError("Error","No se encuentra Registrado el codigo del Delegado");
				    desRegistroEditar = "";
				    perfilRegistroEditar = "";
				    criterioRegistroEditar = "";
				    validarCodRegistroEditar = false;
			        }
				}else{
					Utilitarios.mensajeInfo("Info", "Seleccione un Nivel");
				}		
				} else {
					Utilitarios.mensajeError("Info","Ingrese el código del delegado");
					desRegistroEditar = "";
					perfilRegistroEditar = "";
					criterioRegistroEditar = "";
					validarCodRegistroEditar = false;
				}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : obtenerDatosMiembroEditar"
					+ ex.getLocalizedMessage());
		}
	}

	public void editarAgrupacion() {
		logger.info("DelegadosMB : editarAgrupacion");
		String codigoGrupo = null;
		String desNivel = null;
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		codigoGrupo = params.get("codGrupo");
		desNivel = params.get("desNivel");
		logger.info("desNivel "+desNivel);
		try {
			listaDelegadosEditar = delegadosService.editarAgrupacion(
					codigoGrupo, desNivel);
			
			//Agregar descripcion
			NivelService nivelService = new NivelService();
			for(TiivsMiembroNivel miembroNivel: listaDelegadosEditar){
				miembroNivel.setDescNiv(nivelService.buscarNivelxCodigo(miembroNivel.getCodNiv()));
			}
			
			sDelegadoEstado = listaDelegadosEditar.get(0).getEstado();
			iDelegadoGrupo = listaDelegadosEditar.get(0).getGrupo();
			
			listaDelegadosEditarCopia = listaDelegadosEditar;
			
			listaDelegadosEditarEliminar = new ArrayList<TiivsMiembroNivel>();//er
			/** Samira */
			codRegistroEditar = "";
			desRegistroEditar = "";
			perfilRegistroEditar = "";
			criterioRegistroEditar = "";
			esDelegado=false;
			miembroNivelEditar.setCodNiv(listaDelegadosEditar.get(0).getCodNiv());
			validarCodRegistroEditar = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : editarAgrupacion"
					+ ex.getLocalizedMessage());
		}

	}

	public void agregarDelegado() {
		if(!esDelegado){
		logger.info("DelegadosMB : agregarDelegado");
		boolean codigoRepetido = false;
		boolean nivelDiferente = false;
		if (isValidarCodRegistro() == true) {
			if (listaDelegados.size() < 5) {
				delegado = new TiivsMiembroNivel();
				TiivsMiembro miembroDelegado = new TiivsMiembro();
				miembroDelegado.setCodMiembro(codRegistro);
				miembroDelegado.setDescripcion(desRegistro);
				// delegado.setTiivsMiembro(miembroDelegado);
				delegado.setTiivsMiembro(miembros.get(0));
				for (int i = 0; i < listaDelegados.size(); i++) {
					if (listaDelegados.size() > 0
							&& codRegistro.toUpperCase().equals(
									listaDelegados.get(i).getTiivsMiembro()
											.getCodMiembro().toUpperCase())) {
						codigoRepetido = true;
						break;
					} else {
						codigoRepetido = false;
					}
				}
				for (int j = 0; j < listaDelegados.size(); j++) {
					if (listaDelegados.size() > 0
							&& miembroNivel.getCodNiv().equals(
									listaDelegados.get(j).getCodNiv())) {
						nivelDiferente = false;

					} else {
						nivelDiferente = true;
						break;
					}
				}
				delegado.setCodNiv(miembroNivel.getCodNiv());
				NivelService nivelservice = new NivelService(); 
				delegado.setDescNiv(nivelservice.buscarNivelxCodigo(miembroNivel.getCodNiv()));
				delegado.setEstadoMiembro("1");
				delegado.setTipoRol("D");
				if (codigoRepetido == false) {
					if (nivelDiferente == false) {
						listaDelegados.add(delegado);
						esDelegado=false;
					} else {
						Utilitarios
								.mensajeError("Error",
										"Debe seleccionar el mismo nivel para los delegados a agregar");
					}

				} else {
					Utilitarios
							.mensajeError("Error",
									"El delegado ya ha sido seleccionado para este nivel");
				}

				//
				codRegistro = "";
				desRegistro = "";
				perfilRegistro = "";
				criterioRegistro = "";
				validarCodRegistro = false;
			} else {
				Utilitarios.mensajeError("Error",
						"Un Nivel no puede tener mas de 5 delegados");
				//
				codRegistro = "";
				desRegistro = "";
				perfilRegistro = "";
				criterioRegistro = "";
				validarCodRegistro = false;
			}
			//
		} else {
			//
			codRegistro = "";
			desRegistro = "";
			perfilRegistro = "";
			criterioRegistro = "";
			validarCodRegistro = false;
			//
		}
		}else{
			Utilitarios.mensajeInfo("Info", "La Persona ya tiene rol de Responsable, no puede ser Delegado del mismo Nivel");
		}
	}

	public void estadoAgrupacion() {
		logger.info("DelegadosMB : estadoAgrupacion");
		String grupo = null;
		String nivel = null;
		listaDelegadosEditarEstado = new ArrayList<TiivsMiembroNivel>();
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		grupo = params.get("grupo");
		nivel = params.get("nivel");
		try {
			listaDelegadosEditarEstado = delegadosService.estadoAgrupacion(
					grupo, nivel);

			if (listaDelegadosEditarEstado.get(0).getEstado() == null) {
				for (int i = 0; i < listaDelegadosEditarEstado.size(); i++) {
					listaDelegadosEditarEstado.get(i).setEstado("1");
					delegadosService.actualizarAgrupacion(listaDelegadosEditarEstado.get(i));	
				}
			} else {
				if (listaDelegadosEditarEstado.get(0).getEstado().equals("1")) {
					for (int i = 0; i < listaDelegadosEditarEstado.size(); i++) {
						listaDelegadosEditarEstado.get(i).setEstado("0");
						delegadosService.actualizarAgrupacion(listaDelegadosEditarEstado.get(i));	
					}
				} else {
					for (int i = 0; i < listaDelegadosEditarEstado.size(); i++) {
						listaDelegadosEditarEstado.get(i).setEstado("1");
						delegadosService.actualizarAgrupacion(listaDelegadosEditarEstado.get(i));	
					}
				}
			}
			limpiarListaAgrupaciones();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : estadoAgrupacion"
					+ ex.getLocalizedMessage());
		}

	}

	public void agregarDelegadoEditar() {
		logger.info("DelegadosMB : agregarDelegadoEditar");
		if(!esDelegado){
		boolean codigoRepetido = false;
		boolean nivelDiferente = false;
		if (isValidarCodRegistroEditar() == true) {
			if (listaDelegadosEditar.size() < 5) {
				delegado = new TiivsMiembroNivel();
				TiivsMiembro miembroDelegado = new TiivsMiembro();
				miembroDelegado.setCodMiembro(codRegistro);
				miembroDelegado.setDescripcion(desRegistro);
				// delegado.setTiivsMiembro(miembroDelegado);
				delegado.setTiivsMiembro(miembros.get(0));
				for (int i = 0; i < listaDelegadosEditar.size(); i++) {
					if (listaDelegadosEditar.size() > 0
							&& codRegistroEditar.toUpperCase().equals(listaDelegadosEditar.get(i).getTiivsMiembro().getCodMiembro().toUpperCase())) {
						codigoRepetido = true;
						break;
					} else {
						codigoRepetido = false;
					}
				}
				for (int j = 0; j < listaDelegadosEditar.size(); j++) {
					if (listaDelegadosEditar.size() > 0
							&& miembroNivelEditar.getCodNiv().equals(
									listaDelegadosEditar.get(j).getCodNiv())) {
						nivelDiferente = false;

					} else {
						nivelDiferente = true;
						break;
					}
				}
				delegado.setCodNiv(miembroNivelEditar.getCodNiv());
				NivelService nivelService = new NivelService(); 
				delegado.setDescNiv(nivelService.buscarNivelxCodigo(miembroNivelEditar.getCodNiv()));
				delegado.setEstadoMiembro("1");
				delegado.setTipoRol("D");
				delegado.setGrupo(iDelegadoGrupo);
				if (codigoRepetido == false) {
					if (nivelDiferente == false) {
						listaDelegadosEditar.add(delegado);
						codRegistroEditar = "";
						desRegistroEditar = "";
						perfilRegistroEditar = "";
						criterioRegistroEditar = "";
					} else {
						Utilitarios
								.mensajeError("Error",
										"Debe seleccionar el mismo nivel para los delegados a agregar");
					}

				} else {
					Utilitarios
							.mensajeError("Error",
									"El delegado ya ha sido seleccionado para este nivel");
				}

				//
				/*codRegistroEditar = "";
				desRegistroEditar = "";
				perfilRegistroEditar = "";
				criterioRegistroEditar = "";*/
				validarCodRegistroEditar = false;
			} else {
				Utilitarios.mensajeError("Error",
						"Un Nivel no puede tener más de 5 delegados");
				//
				codRegistroEditar = "";
				desRegistroEditar = "";
				perfilRegistroEditar = "";
				criterioRegistroEditar = "";
				validarCodRegistroEditar = false;
			}
			//
		} else {
			//
			codRegistroEditar = "";
			desRegistroEditar = "";
			perfilRegistroEditar = "";
			criterioRegistroEditar = "";
			validarCodRegistroEditar = false;
			//
		}
		}else{
			Utilitarios.mensajeInfo("Info", "La Persona ya tiene rol de Responsable, no puede ser Delegado del mismo Nivel");
		}
	}

	public void eliminarDelegado() {
		logger.info("DelegadosMB : eliminarDelegado");
		String id;
		int codigo;
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		id = params.get("id");
		codigo = Integer.parseInt(id);
		listaDelegados.remove(codigo);

	}

	public void eliminarDelegadoEditar() {
		logger.info("DelegadosMB : eliminarDelegadoEditar");
		//listaDelegadosEditarEliminar = new ArrayList<TiivsMiembroNivel>();
		String idEl;
		String codRegistro;
		int codigo;
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		idEl = params.get("idEl");
		codRegistro = params.get("codMiembroEditar");
		codigo = Integer.parseInt(idEl);
		for (int i = 0; i < listaDelegadosEditarCopia.size(); i++) {
			if (listaDelegadosEditar.get(codigo).equals(
					listaDelegadosEditarCopia.get(i)) == true) {
				listaDelegadosEditar.get(i).setEstadoMiembro("0");
				listaDelegadosEditarEliminar.add(listaDelegadosEditar.get(i));
			}
		}

		listaDelegadosEditar.remove(codigo);

	}

	public void limpiarNuevo() {
		logger.info("DelegadosMB : limpiarNuevo");
		listaDelegados = new ArrayList<TiivsMiembroNivel>();
		codRegistro = "";
		desRegistro = "";
		perfilRegistro = "";
		criterioRegistro = "";
		validarCodRegistro = false;
	}

	public void actualizarAgrupacion() throws Exception {
		logger.info("DelegadosMB : actualizarAgrupacion");
		
		Date sysDate = new Date();
		Timestamp utilDateDate = new Timestamp(sysDate.getTime());
		for (int i = 0; i < listaDelegadosEditarEliminar.size(); i++) {

			delegadosService.actualizarAgrupacion(listaDelegadosEditarEliminar
					.get(i));
		}
		for (int j = 0; j < listaDelegadosEditar.size(); j++) {
			listaDelegadosEditar.get(j).setEstado(sDelegadoEstado);
			listaDelegadosEditar.get(j).setGrupo(iDelegadoGrupo);
			listaDelegadosEditar.get(j).setFechaRegistro(utilDateDate);
			listaDelegadosEditar.get(j).setUsuarioRegistro(usuario.getUID());

			delegadosService.actualizarAgrupacion(listaDelegadosEditar.get(j));
		}
		limpiarListaAgrupaciones();
		sDelegadoEstado = null;
		iDelegadoGrupo = 0;
	}

	public void registrarAgrupacion() throws Exception {
		logger.info("DelegadosMB : registrarAgrupacion");
		int grupo = 0;
		int id = 0;
		if (listaDelegados.size() > 0) {
			if(listaDelegados.size()>5){
				Utilitarios.mensajeInfo("Info", "Solo se pueden registrar 5 delegados por combinación");
			}else{
			Date sysDate = new Date();/*
									 * String utilDateString =
									 * formatear.format(sysDate);
									 */
			Timestamp utilDateDate = new Timestamp(sysDate.getTime());
			grupo = obtenerGrupo(miembroNivel.getCodNiv());
			for (int i = 0; i < listaDelegados.size(); i++) {
				id = id + i;
				listaDelegados.get(i).setGrupo(grupo);
				listaDelegados.get(i).setFechaRegistro(utilDateDate);
				listaDelegados.get(i).setUsuarioRegistro(usuario.getUID());

				delegadosService.registrarAgrupacion(listaDelegados.get(i));
			}

			limpiarListaAgrupaciones();
			}
		}
		 else {
			Utilitarios.mensajeError("Error","Debe asignar delegados a un nivel");
			limpiarListaAgrupaciones();
		}
	}

	private int obtenerId() {
		logger.info("DelegadosMB : obtenerId");
		int id = 0;
		try {
			id = delegadosService.obtenerId();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : obtenerId" + ex.getLocalizedMessage());
		}
		return id;
	}

	private int obtenerGrupo(String codNiv) {
		logger.info("DelegadosMB : obtenerGrupo");
		int grupo = 0;
		try {
			grupo = delegadosService.obtenerGrupo(codNiv);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : obtenerDatosMiembro"
					+ ex.getLocalizedMessage());
		}
		return grupo;
	}

	public void limpiarListaAgrupaciones() {
		logger.info("DelegadosMB : listarAgrupacionesDelegados");
		lstListaAgrupacionesNivelesDelegados = new ArrayList<AgrupacionNivelDelegadoDto>();

	}

	public void listarAgrupacionesDelegados() {
		logger.info("DelegadosMB : listarAgrupacionesDelegados");
		try {
			SolicitudDao<TiivsMiembroNivel, Object> service = (SolicitudDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
			List<AgrupacionDelegadosDto> lstDele = new ArrayList<AgrupacionDelegadosDto>();
			List<AgrupacionDelegadosDto> lstDelegadosPK = new ArrayList<AgrupacionDelegadosDto>();
			/* List<AgrupacionDelegadosDto> */lstDele = service.obtenerDelegados();
			/* List<AgrupacionDelegadosDto> */lstDelegadosPK = service.obtenerPKDelegados();
			List<ComboDto> lstDuos = null;
			ComboDto duo = null;
			AgrupacionNivelDelegadoDto agrupacionNivelDelegadoDto = null;

			for (AgrupacionDelegadosDto a : lstDelegadosPK) {
				lstDuos = new ArrayList<ComboDto>();
				agrupacionNivelDelegadoDto = new AgrupacionNivelDelegadoDto();
				agrupacionNivelDelegadoDto.setNivel(a.getDes_niv());
				agrupacionNivelDelegadoDto.setCodGrupo(a.getGrupo());
				if (a.getEstado().equals("")) {
					agrupacionNivelDelegadoDto.setEstado("Pendiente");
					agrupacionNivelDelegadoDto.setEstadoEtiqueta("Activar");
				} else {
					if (a.getEstado().equals("1")) {
						agrupacionNivelDelegadoDto.setEstado("Activo");
						agrupacionNivelDelegadoDto.setEstadoEtiqueta("Inactivar");

					} else {
						agrupacionNivelDelegadoDto.setEstado("Inactivo");
						agrupacionNivelDelegadoDto.setEstadoEtiqueta("Activar");
					}
				}

				agrupacionNivelDelegadoDto.setLstDelegados(lstDuos);
				for (AgrupacionDelegadosDto b : lstDele) {
					if (a.getDes_niv().equals(b.getDes_niv())&& a.getGrupo().equals(b.getGrupo())) {
						duo = new ComboDto();
						duo.setKey(b.getCod_miembro());
						duo.setDescripcion(b.getDescripcion());
						lstDuos.add(duo);
					}
				}
				lstListaAgrupacionesNivelesDelegados.add(agrupacionNivelDelegadoDto);
			}

			for (AgrupacionNivelDelegadoDto c : lstListaAgrupacionesNivelesDelegados) {

				if (c.getLstDelegados().size() == 1) {
					c.setCod_delegado_A(c.getLstDelegados().get(0) == null ? "": c.getLstDelegados().get(0).getKey());
					c.setCod_nombre_delegado_A(c.getLstDelegados().get(0) == null ? ""
							: c.getLstDelegados().get(0).getDescripcion());
				} else {
					if (c.getLstDelegados().size() == 2) {
						c.setCod_delegado_A(c.getLstDelegados().get(0) == null ? ""
								: c.getLstDelegados().get(0).getKey());
						c.setCod_nombre_delegado_A(c.getLstDelegados().get(0) == null ? ""
								: c.getLstDelegados().get(0).getDescripcion());
						c.setCod_delegado_B(c.getLstDelegados().get(1) == null ? ""
								: c.getLstDelegados().get(1).getKey());
						c.setCod_nombre_delegado_B(c.getLstDelegados().get(1) == null ? ""
								: c.getLstDelegados().get(1).getDescripcion());
					} else {
						if (c.getLstDelegados().size() == 3) {
							c.setCod_delegado_A(c.getLstDelegados().get(0) == null ? ""
									: c.getLstDelegados().get(0).getKey());
							c.setCod_nombre_delegado_A(c.getLstDelegados().get(
									0) == null ? "" : c.getLstDelegados()
									.get(0).getDescripcion());
							c.setCod_delegado_B(c.getLstDelegados().get(1) == null ? ""
									: c.getLstDelegados().get(1).getKey());
							c.setCod_nombre_delegado_B(c.getLstDelegados().get(
									1) == null ? "" : c.getLstDelegados()
									.get(1).getDescripcion());
							c.setCod_delegado_C(c.getLstDelegados().get(2) == null ? ""
									: c.getLstDelegados().get(2).getKey());
							c.setCod_nombre_delegado_C(c.getLstDelegados().get(
									2) == null ? "" : c.getLstDelegados()
									.get(2).getDescripcion());
						} else {
							if (c.getLstDelegados().size() == 4) {
								c.setCod_delegado_A(c.getLstDelegados().get(0) == null ? ""
										: c.getLstDelegados().get(0).getKey());
								c.setCod_nombre_delegado_A(c.getLstDelegados()
										.get(0) == null ? "" : c
										.getLstDelegados().get(0)
										.getDescripcion());
								c.setCod_delegado_B(c.getLstDelegados().get(1) == null ? ""
										: c.getLstDelegados().get(1).getKey());
								c.setCod_nombre_delegado_B(c.getLstDelegados()
										.get(1) == null ? "" : c
										.getLstDelegados().get(1)
										.getDescripcion());
								c.setCod_delegado_C(c.getLstDelegados().get(2) == null ? ""
										: c.getLstDelegados().get(2).getKey());
								c.setCod_nombre_delegado_C(c.getLstDelegados()
										.get(2) == null ? "" : c
										.getLstDelegados().get(2)
										.getDescripcion());
								c.setCod_delegado_D(c.getLstDelegados().get(3) == null ? ""
										: c.getLstDelegados().get(3).getKey());
								c.setCod_nombre_delegado_D(c.getLstDelegados()
										.get(3) == null ? "" : c
										.getLstDelegados().get(3)
										.getDescripcion());

							} else {
								if (c.getLstDelegados().size() == 5) {
									c.setCod_delegado_A(c.getLstDelegados()
											.get(0) == null ? "" : c
											.getLstDelegados().get(0).getKey());
									c.setCod_nombre_delegado_A(c
											.getLstDelegados().get(0) == null ? ""
											: c.getLstDelegados().get(0)
													.getDescripcion());
									c.setCod_delegado_B(c.getLstDelegados()
											.get(1) == null ? "" : c
											.getLstDelegados().get(1).getKey());
									c.setCod_nombre_delegado_B(c
											.getLstDelegados().get(1) == null ? ""
											: c.getLstDelegados().get(1)
													.getDescripcion());
									c.setCod_delegado_C(c.getLstDelegados()
											.get(2) == null ? "" : c
											.getLstDelegados().get(2).getKey());
									c.setCod_nombre_delegado_C(c
											.getLstDelegados().get(2) == null ? ""
											: c.getLstDelegados().get(2)
													.getDescripcion());
									c.setCod_delegado_D(c.getLstDelegados()
											.get(3) == null ? "" : c
											.getLstDelegados().get(3).getKey());
									c.setCod_nombre_delegado_D(c
											.getLstDelegados().get(3) == null ? ""
											: c.getLstDelegados().get(3)
													.getDescripcion());
									c.setCod_delegado_E(c.getLstDelegados()
											.get(4) == null ? "" : c
											.getLstDelegados().get(4).getKey());
									c.setCod_nombre_delegado_E(c
											.getLstDelegados().get(4) == null ? ""
											: c.getLstDelegados().get(4)
													.getDescripcion());
								} else {

									break;
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<AgrupacionNivelDelegadoDto> getLstListaAgrupacionesNivelesDelegados() {
		return this.lstListaAgrupacionesNivelesDelegados;
	}

	public void setLstListaAgrupacionesNivelesDelegados(
			List<AgrupacionNivelDelegadoDto> lstListaAgrupacionesNivelesDelegados) {
		this.lstListaAgrupacionesNivelesDelegados = lstListaAgrupacionesNivelesDelegados;
	}

	public TiivsMiembroNivel getMiembroNivel() {
		return miembroNivel;
	}

	public void setMiembroNivel(TiivsMiembroNivel miembroNivel) {
		this.miembroNivel = miembroNivel;
	}

	public List<TiivsNivel> getListaNiveles() {
		return listaNiveles;
	}

	public void setListaNiveles(List<TiivsNivel> listaNiveles) {
		this.listaNiveles = listaNiveles;
	}

	public String getCodRegistro() {
		return codRegistro;
	}

	public void setCodRegistro(String codRegistro) {
		this.codRegistro = codRegistro;
	}

	public String getDesRegistro() {
		return desRegistro;
	}

	public void setDesRegistro(String desRegistro) {
		this.desRegistro = desRegistro;
	}

	public String getPerfilRegistro() {
		return perfilRegistro;
	}

	public void setPerfilRegistro(String perfilRegistro) {
		this.perfilRegistro = perfilRegistro;
	}

	public List<TiivsMiembro> getMiembros() {
		return miembros;
	}

	public void setMiembros(List<TiivsMiembro> miembros) {
		this.miembros = miembros;
	}

	public List<TiivsMiembroNivel> getListaDelegados() {
		return listaDelegados;
	}

	public void setListaDelegados(List<TiivsMiembroNivel> listaDelegados) {
		this.listaDelegados = listaDelegados;
	}

	public boolean isValidarCodRegistro() {
		return validarCodRegistro;
	}

	public void setValidarCodRegistro(boolean validarCodRegistro) {
		this.validarCodRegistro = validarCodRegistro;
	}

	public TiivsMiembroNivel getDelegado() {
		return delegado;
	}

	public void setDelegado(TiivsMiembroNivel delegado) {
		this.delegado = delegado;
	}

	public String getCriterioRegistro() {
		return criterioRegistro;
	}

	public void setCriterioRegistro(String criterioRegistro) {
		this.criterioRegistro = criterioRegistro;
	}

	public List<TiivsMiembroNivel> getListaDelegadosEditar() {
		return listaDelegadosEditar;
	}

	public void setListaDelegadosEditar(
			List<TiivsMiembroNivel> listaDelegadosEditar) {
		this.listaDelegadosEditar = listaDelegadosEditar;
	}

	public String getCodRegistroEditar() {
		return codRegistroEditar;
	}

	public void setCodRegistroEditar(String codRegistroEditar) {
		this.codRegistroEditar = codRegistroEditar;
	}

	public String getDesRegistroEditar() {
		return desRegistroEditar;
	}

	public void setDesRegistroEditar(String desRegistroEditar) {
		this.desRegistroEditar = desRegistroEditar;
	}

	public String getPerfilRegistroEditar() {
		return perfilRegistroEditar;
	}

	public void setPerfilRegistroEditar(String perfilRegistroEditar) {
		this.perfilRegistroEditar = perfilRegistroEditar;
	}

	public String getCriterioRegistroEditar() {
		return criterioRegistroEditar;
	}

	public void setCriterioRegistroEditar(String criterioRegistroEditar) {
		this.criterioRegistroEditar = criterioRegistroEditar;
	}

	public TiivsMiembroNivel getMiembroNivelEditar() {
		return miembroNivelEditar;
	}

	public void setMiembroNivelEditar(TiivsMiembroNivel miembroNivelEditar) {
		this.miembroNivelEditar = miembroNivelEditar;
	}

	public List<TiivsMiembroNivel> getListaDelegadosEditarCopia() {
		return listaDelegadosEditarCopia;
	}

	public void setListaDelegadosEditarCopia(
			List<TiivsMiembroNivel> listaDelegadosEditarCopia) {
		this.listaDelegadosEditarCopia = listaDelegadosEditarCopia;
	}

	public List<TiivsMiembroNivel> getListaDelegadosEditarEliminar() {
		return listaDelegadosEditarEliminar;
	}

	public void setListaDelegadosEditarEliminar(
			List<TiivsMiembroNivel> listaDelegadosEditarEliminar) {
		this.listaDelegadosEditarEliminar = listaDelegadosEditarEliminar;
	}

	public boolean isValidarCodRegistroEditar() {
		return validarCodRegistroEditar;
	}

	public void setValidarCodRegistroEditar(boolean validarCodRegistroEditar) {
		this.validarCodRegistroEditar = validarCodRegistroEditar;
	}

	public List<TiivsMiembroNivel> getListaDelegadosEditarEstado() {
		return listaDelegadosEditarEstado;
	}

	public void setListaDelegadosEditarEstado(
			List<TiivsMiembroNivel> listaDelegadosEditarEstado) {
		this.listaDelegadosEditarEstado = listaDelegadosEditarEstado;
	}

	
	public int getiDelegadoGrupo() {
		return iDelegadoGrupo;
	}

	public void setiDelegadoGrupo(int iDelegadoGrupo) {
		this.iDelegadoGrupo = iDelegadoGrupo;
	}

	public String getsDelegadoEstado() {
		return sDelegadoEstado;
	}

	public void setsDelegadoEstado(String sDelegadoEstado) {
		this.sDelegadoEstado = sDelegadoEstado;
	}
}

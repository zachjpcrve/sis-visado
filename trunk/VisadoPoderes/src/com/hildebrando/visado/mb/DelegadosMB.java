package com.hildebrando.visado.mb;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.service.DelegadosService;

@ManagedBean(name = "delegadosMB")
@SessionScoped
public class DelegadosMB {

	public static Logger logger = Logger.getLogger(DelegadosMB.class);
	private SimpleDateFormat formatear = new SimpleDateFormat("dd/MM/yyyy");
	private DateFormat df = DateFormat.getDateInstance();
	private List<AgrupacionNivelDelegadoDto> lstListaAgrupacionesNivelesDelegados;
	private TiivsMiembroNivel miembroNivel;
	private List<TiivsNivel> listaNiveles;
	private DelegadosService delegadosService;
	private IILDPeUsuario usuario;
	private List<TiivsMiembro> miembros;
	private List<TiivsMiembroNivel> listaDelegados;
	private TiivsMiembroNivel delegado;
	private boolean validarCodRegistro;

	private String codRegistro;
	private String desRegistro;
	private String perfilRegistro;
	private String criterioRegistro;

	public DelegadosMB() {
		criterioRegistro = "";
		codRegistro = "";
		desRegistro = "";
		perfilRegistro = "";
		usuario = (IILDPeUsuario) Utilitarios
				.getObjectInSession("USUARIO_SESION");
		validarCodRegistro = false;
		delegado = new TiivsMiembroNivel();
		listaDelegados = new ArrayList<TiivsMiembroNivel>();
		delegadosService = new DelegadosService();
		lstListaAgrupacionesNivelesDelegados = new ArrayList<AgrupacionNivelDelegadoDto>();
		listarAgrupacionesDelegados();
		miembroNivel = new TiivsMiembroNivel();
		listarNiveles();

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
				miembros = delegadosService.obtenerDatosMiembro(codRegistro
						.toUpperCase());
				if (miembros.size() > 0) {

					desRegistro = miembros.get(0).getDescripcion();
					perfilRegistro = miembros.get(0).getTiivsGrupo()
							.getDesGrupo();
					criterioRegistro = miembros.get(0).getCriterio();
					validarCodRegistro = true;
				} else {
					Utilitarios
							.mensajeError("Error",
									"No se encuentra Registrado el codigo del Delegado");
					desRegistro = "";
					perfilRegistro = "";
					criterioRegistro = "";
					validarCodRegistro = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DelegadosMB : obtenerDatosMiembro"
					+ ex.getLocalizedMessage());
		}
	}

	public void agregarDelegado() {
		logger.info("DelegadosMB : agregarDelegado");
		boolean codigoRepetido = false;
		if (isValidarCodRegistro() == true) {
			if(listaDelegados.size()< 5){
				delegado = new TiivsMiembroNivel();
				TiivsMiembro miembroDelegado = new TiivsMiembro();
				miembroDelegado.setCodMiembro(codRegistro);
				miembroDelegado.setDescripcion(desRegistro);
				//delegado.setTiivsMiembro(miembroDelegado);
				delegado.setTiivsMiembro(miembros.get(0));
				for(int i = 0; i <listaDelegados.size(); i++){
					if(listaDelegados.size()> 0 && codRegistro.toUpperCase().equals(listaDelegados.get(i).getTiivsMiembro().getCodMiembro().toUpperCase())){
						codigoRepetido = true;
						break;
					}else{
						codigoRepetido = false;
					}
				}
				delegado.setCodNiv(miembroNivel.getCodNiv());
				delegado.setEstado("1");
				delegado.setTipoRol("D");
				if(codigoRepetido == false){
					listaDelegados.add(delegado);

				}else{
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
			}else{
				Utilitarios
				.mensajeError("Error",
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
	
	public void registrarAgrupacion() throws Exception{
		logger.info("DelegadosMB : registrarAgrupacion");
		int grupo = 0;
		int id = 0;
		if(listaDelegados.size()>0){
			Date sysDate = new Date();/*
			String utilDateString = formatear.format(sysDate);*/
			Timestamp utilDateDate = new Timestamp(sysDate.getTime());
			grupo = obtenerGrupo();
			for(int i = 0; i<listaDelegados.size(); i ++){
				id= id + i;
				listaDelegados.get(i).setGrupo(grupo);
				listaDelegados.get(i).setFechaRegistro(utilDateDate);
				listaDelegados.get(i).setUsuarioRegistro(usuario.getUID());
				
				delegadosService.registrarAgrupacion(listaDelegados.get(i));
			}
		}else{
			Utilitarios
			.mensajeError("Error",
					"Debe asignar delegados a un nivel");	
		}
	}


	private int obtenerGrupo() {
		logger.info("DelegadosMB : obtenerGrupo");
		int grupo = 0;
		try{
			grupo = delegadosService.obtenerGrupo();
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("DelegadosMB : obtenerDatosMiembro"
					+ ex.getLocalizedMessage());
		}
		return grupo;
	}

	public void listarAgrupacionesDelegados() {

		logger.info("DelegadosMB : listarAgrupacionesDelegados");
		try {
			SolicitudDao<TiivsMiembroNivel, Object> service = (SolicitudDao<TiivsMiembroNivel, Object>) SpringInit
					.getApplicationContext().getBean("solicitudEspDao");
			List<AgrupacionDelegadosDto> lstDele = service.obtenerDelegados();
			List<AgrupacionDelegadosDto> lstDelegadosPK = service
					.obtenerPKDelegados();
			List<ComboDto> lstDuos = null;
			ComboDto duo = null;
			AgrupacionNivelDelegadoDto agrupacionNivelDelegadoDto = null;

			for (AgrupacionDelegadosDto a : lstDelegadosPK) {
				lstDuos = new ArrayList<ComboDto>();
				agrupacionNivelDelegadoDto = new AgrupacionNivelDelegadoDto();
				agrupacionNivelDelegadoDto.setNivel(a.getDes_niv());
				agrupacionNivelDelegadoDto.setCodGrupo(a.getGrupo());
				agrupacionNivelDelegadoDto.setLstDelegados(lstDuos);

				for (AgrupacionDelegadosDto b : lstDele) {
					if (a.getDes_niv().equals(b.getDes_niv())
							&& a.getGrupo().equals(b.getGrupo())) {
						duo = new ComboDto();
						duo.setKey(b.getCod_miembro());
						duo.setDescripcion(b.getDescripcion());
						lstDuos.add(duo);
					}
				}
				lstListaAgrupacionesNivelesDelegados
						.add(agrupacionNivelDelegadoDto);
			}

			for (AgrupacionNivelDelegadoDto c : lstListaAgrupacionesNivelesDelegados) {

				if (c.getLstDelegados().size() == 1) {
					c.setCod_delegado_A(c.getLstDelegados().get(0) == null ? ""
							: c.getLstDelegados().get(0).getKey());
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
							c.setCod_delegado_D(c.getLstDelegados().get(3) == null ? ""
									: c.getLstDelegados().get(3).getKey());
							c.setCod_nombre_delegado_D(c.getLstDelegados().get(
									3) == null ? "" : c.getLstDelegados()
									.get(3).getDescripcion());
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
								c.setCod_delegado_E(c.getLstDelegados().get(4) == null ? ""
										: c.getLstDelegados().get(4).getKey());
								c.setCod_nombre_delegado_E(c.getLstDelegados()
										.get(4) == null ? "" : c
										.getLstDelegados().get(4)
										.getDescripcion());
							} else {
								break;
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

}

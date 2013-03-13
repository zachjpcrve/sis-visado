package com.hildebrando.visado.mb;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.event.RowEditEvent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.consulta.reniec.util.Constantes;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.MiembroNivelDTO;
import com.hildebrando.visado.dto.NivelDto;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.service.DelegadosService;
import com.hildebrando.visado.service.RespNivelAprobacionService;

/**
 * Clase que se encarga de manejar la edición y actualizacion de los
 * responsables por nivel de aprobacion
 * 
 * @author
 * 
 * **/

@ManagedBean(name = "editRespNivel")
@SessionScoped
public class EditRespNivelAprobacionMB {

	public static Logger logger = Logger.getLogger(EditRespNivelAprobacionMB.class);
	
	private DelegadosService delegadosService;
	private RespNivelAprobacionService respNivelAprobacionService;
	private MiembroNivelDTO miembroNivelDto;
	private List<NivelDto> niveles;
	private List<MiembroNivelDTO> respNiveles;
	private boolean flagVisible;
	private String codNivel;

	private List<TiivsMiembroNivel> list;
	private String codRegistro;
	private List<TiivsMiembro> miembros;

	private boolean validarCodRegistro;
	private String iniciar;
	private TiivsMiembroNivel miembroNivel;
	private GenericDao<TiivsNivel, Object> serviceTiivsNivel;
	private String[] estados;
	private boolean bEditar;

	public EditRespNivelAprobacionMB() 
	{
		iniciar = "";
		delegadosService = new DelegadosService();
		respNivelAprobacionService = new RespNivelAprobacionService();
		setFlagVisible(true);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession();
		miembroNivel = (TiivsMiembroNivel) session.getAttribute("miembroNivel");
		
		RespNivelAprobacionMB temp = (RespNivelAprobacionMB) session.getAttribute("respNivel");

		serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

		if (temp.isLimpiar()) {
			this.limpiarCampos();
			temp.setLimpiar(false);
			session.setAttribute("respNivel", temp);
		} else {
			this.editar();
		}
		
		estados=  new String[2];
		estados[0] = "Activo";
		estados[1] = "Inactivo";
	}

	private void editar() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession();
		setbEditar(false);
		miembroNivel = (TiivsMiembroNivel) session.getAttribute("miembroNivel");

		if (miembroNivel.getTiivsMiembro() != null) 
		{
			miembroNivelDto = new MiembroNivelDTO();
			miembroNivelDto.setRegistro(miembroNivel.getTiivsMiembro().getCodMiembro());
			miembroNivelDto.setDesGrupo(miembroNivel.getTiivsMiembro().getTiivsGrupo().getDesGrupo());
			miembroNivelDto.setDescripcion(miembroNivel.getTiivsMiembro().getDescripcion());
			miembroNivelDto.setEstado(miembroNivel.getEstado());
			miembroNivelDto.setCodNivel(miembroNivel.getCodNiv());

			GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTiivsMiembroNivel = Busqueda
					.forClass(TiivsMiembroNivel.class);
			list = new ArrayList<TiivsMiembroNivel>();

			try {
				list = serviceTiivsMiembroNivel
						.buscarDinamico(filtroTiivsMiembroNivel
								.add(Restrictions.eq("tiivsMiembro.codMiembro",
										miembroNivel.getTiivsMiembro().getCodMiembro())));
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
						+ "al obtener ListaRespNivel: " + e);
			}

			respNiveles = new ArrayList<MiembroNivelDTO>();

			for (TiivsMiembroNivel e : list) {
				String descEstado = "";
				String desNivel = "";

				descEstado = respNivelAprobacionService.obtenerDesEstado(e
						.getCodNiv());
				desNivel = respNivelAprobacionService.obtenerDesNivel(e
						.getCodNiv());

				List<TiivsNivel> tiivsNivels = new ArrayList<TiivsNivel>();
				Busqueda filtroTiivsNivelMoneda = Busqueda
						.forClass(TiivsNivel.class);
				try {
					tiivsNivels = serviceTiivsNivel
							.buscarDinamico(filtroTiivsNivelMoneda
									.add(Restrictions.eq("codNiv",
											e.getCodNiv())));
				} catch (Exception e1) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
							+ "al obtener monedas: " + e);
				}

				int ris = 0;
				int rfs = 0;

				int rid = 0;
				int rfd = 0;

				int rie = 0;
				int rfe = 0;

				for (TiivsNivel nivel : tiivsNivels) {

					if (nivel.getMoneda().compareTo(
							ConstantesVisado.MONEDAS.COD_SOLES) == 0) {
						ris = nivel.getRangoInicio();
						rfs = nivel.getRangoFin();
					}

					if (nivel.getMoneda().compareTo(
							ConstantesVisado.MONEDAS.COD_DOLAR) == 0) {
						rid = nivel.getRangoInicio();
						rfd = nivel.getRangoFin();
					}

					if (nivel.getMoneda().compareTo(
							ConstantesVisado.MONEDAS.COD_EUROS) == 0) {
						rie = nivel.getRangoInicio();
						rfe = nivel.getRangoFin();
					}

				}

				respNiveles.add(new MiembroNivelDTO(0, e.getId(),
						e.getCodNiv(), desNivel, e.getTiivsMiembro()
								.getCodMiembro(), e.getTiivsMiembro()
								.getDescripcion(), e.getTiivsMiembro()
								.getTiivsGrupo().getCodGrupo(), e
								.getTiivsMiembro().getTiivsGrupo()
								.getDesGrupo(),
						e.getFechaRegistro().toString(),
						e.getUsuarioRegistro(), e.getEstado(), descEstado, ris,
						rfs, rid, rfd, rie, rfe));
			}

			setFlagVisible(true);

		} else {

			miembroNivelDto = new MiembroNivelDTO();
			miembroNivelDto.setEstado(miembroNivel.getEstado());

			if (respNiveles == null)
				respNiveles = new ArrayList<MiembroNivelDTO>();

			setFlagVisible(false);
		}

		niveles = new ArrayList<NivelDto>();

		Busqueda filtroTiivsNivel = Busqueda.forClass(TiivsNivel.class)
				.setProjection(
						Projections.distinct(Projections.property("codNiv")));

		try {
			List<String> tiivsNivels = serviceTiivsNivel
					.buscarDinamicoString(filtroTiivsNivel);
			for (String s : tiivsNivels) {
				Busqueda filtroTiivsNivel2 = Busqueda
						.forClass(TiivsNivel.class);
				List<TiivsNivel> list = serviceTiivsNivel
						.buscarDinamico(filtroTiivsNivel2.add(Restrictions.eq(
								"codNiv", s)));
				String des = list.get(0).getDesNiv();
				niveles.add(new NivelDto(s, des));
			}
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR + "al obtener la lista CodAgrup: " + e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void editarNivAprobacion(RowEditEvent event)
	{
		List<TiivsMiembroNivel> lstTmp = new ArrayList<TiivsMiembroNivel>();
		
		MiembroNivelDTO mNivel = ((MiembroNivelDTO) event.getObject());
		logger.debug("modificando nivel con codigo:" + mNivel.getId());
		logger.debug("estado modificado:" + mNivel.getCodEstado());
		
		GenericDao<TiivsMiembroNivel, Object> mNivelDAO = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMiem = Busqueda.forClass(TiivsMiembroNivel.class);
		filtroMiem.add(Restrictions.eq(ConstantesVisado.CAMPO_ALIAS_COD_MIEMBRO,mNivel.getUsuarioRegistro()));
		filtroMiem.add(Restrictions.eq(ConstantesVisado.CAMPO_TIPO_ROL, "R"));
		filtroMiem.add(Restrictions.eq(ConstantesVisado.CAMPO_ID, mNivel.getId()));
		filtroMiem.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_NIVEL,mNivel.getCodNivel()));
		
		try {
			lstTmp = mNivelDAO.buscarDinamico(filtroMiem);
		} catch (Exception e) {
			logger.debug("No se pudo encontrar los datos de miembro nivel",e);
		}
		
		if (lstTmp.size()==1)
		{
			for (TiivsMiembroNivel tmp: lstTmp )
			{
				tmp.setEstado(mNivel.getCodEstado());
				tmp.setUsuarioAct(tmp.getUsuarioRegistro());
				
				java.util.Date date= new java.util.Date();
				tmp.setFechaAct(new Timestamp(date.getTime()));
				
				try {
					mNivelDAO.modificar(tmp);
					logger.info("Se modifico correctamente el estado de miembro nivel");
				} catch (Exception e) {
					logger.info("Error al actualizar la informacion de miembro nivel",e);
				}
			}
		}
	}
	
	public String devuelveDatosUsuario(String usuario, String filtro)
	{
		String resultado="";
		
		GenericDao<TiivsMiembro, Object> mNivelDAO = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMiem = Busqueda.forClass(TiivsMiembro.class);
		filtroMiem.add(Restrictions.eq(ConstantesVisado.CAMPO_COD_MIEMBRO,usuario));
		
		List<TiivsMiembro> lstTmp = new ArrayList<TiivsMiembro>();

		try {
			lstTmp = mNivelDAO.buscarDinamico(filtroMiem);
		} catch (Exception e) {
			logger.debug("No se pudo encontrar los datos de usuario",e);
		}
		
		if (lstTmp.size()==1)
		{
			if (filtro.trim().equals("criterio"))
			{
				resultado=lstTmp.get(0).getCriterio();
			}
		}
		
		return resultado;
	}

	public String getIniciar() 
	{
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession();
		RespNivelAprobacionMB temp = (RespNivelAprobacionMB) session.getAttribute("respNivel");
		
		if (temp.isLimpiar()) 
		{
			this.limpiarCampos();
			respNiveles.clear();
			temp.setLimpiar(false);
			session.setAttribute("respNivel", temp);
		} 
		else 
		{
			this.editar();

		}
		
		setFlagVisible(false);
		return iniciar;
	}

	public void agregarlistarRespxNivel() 
	{
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession();
		String editar = "";
		editar = (String) session.getAttribute("editar");
		
		if (editar == null) {
			setbEditar(false);
		} else {
			setbEditar(true);
		}
		
		if (miembroNivelDto.getEstado().equals("-1")) {
			Utilitarios.mensajeError("Error", "Debe seleccionar el estado");
		} else {
			if (codNivel.equals("-1")) {
				Utilitarios.mensajeError("Error", "Debe seleccionar un nivel");
			} else {

				GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
						.getApplicationContext().getBean("genericoDao");
				Busqueda filtroTiivsMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);

				List<TiivsMiembroNivel> miembroNivels = new ArrayList<TiivsMiembroNivel>();

				try {
					// Cambio Editar
					miembroNivels = serviceTiivsMiembroNivel
							.buscarDinamico(filtroTiivsMiembroNivel
									.add(Restrictions.eq(
											"tiivsMiembro.codMiembro",
											miembroNivelDto.getRegistro()))
									.add(Restrictions.eq("codNiv", codNivel))
									.add(Restrictions.eq("tipoRol", "R")));

					// Cambio Nuevo.

				} catch (Exception e) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
							+ "al consultar miembroNivels: " + e);
				}
				if (isbEditar()) {
					if (miembroNivels.size() == 0) {
						TiivsMiembroNivel miembroNivel = new TiivsMiembroNivel();

						TiivsMiembro miembro = new TiivsMiembro();
						miembro.setCodMiembro(miembroNivelDto.getRegistro());
						miembro.setDescripcion(miembroNivelDto.getDescripcion());
						miembroNivel.setTiivsMiembro(miembro);

						miembroNivel.setCodNiv(getCodNivel());
						miembroNivel.setTipoRol("R");
						miembroNivel.setEstado(miembroNivelDto.getEstado());

						if (logger.isDebugEnabled()) {
							logger.debug("miembroNivelDto.getRegistro()->"
									+ miembroNivelDto.getRegistro());
							logger.debug("miembroNivelDto.getDescripcion()->"
									+ miembroNivelDto.getDescripcion());
							logger.debug("miembroNivelDto.getEstado()->"
									+ miembroNivelDto.getEstado());
						}

						GenericDao<TiivsNivel, Object> serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

						String descEstado = "";
						String desNivel = "";

						descEstado = respNivelAprobacionService
								.obtenerDesEstado(miembroNivel.getCodNiv());
						desNivel = respNivelAprobacionService
								.obtenerDesNivel(miembroNivel.getCodNiv());

						List<TiivsNivel> tiivsNivels = new ArrayList<TiivsNivel>();
						Busqueda filtroTiivsNivelMoneda = Busqueda
								.forClass(TiivsNivel.class);
						try {
							tiivsNivels = serviceTiivsNivel
									.buscarDinamico(filtroTiivsNivelMoneda
											.add(Restrictions.eq("codNiv",
													miembroNivel.getCodNiv())));
						} catch (Exception e1) {
							logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
									+ "al obtener nivelesMonedas: " + e1);
						}

						int ris = 0;
						int rfs = 0;

						int rid = 0;
						int rfd = 0;

						int rie = 0;
						int rfe = 0;

						if (tiivsNivels != null) {
							logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA
									+ "tiivsNivels es: ["
									+ tiivsNivels.size()
									+ "]");
						}
						for (TiivsNivel nivel : tiivsNivels) {

							if (nivel.getMoneda().compareTo(
									ConstantesVisado.MONEDAS.COD_SOLES) == 0) {
								ris = nivel.getRangoInicio();
								rfs = nivel.getRangoFin();
							}

							if (nivel.getMoneda().compareTo(
									ConstantesVisado.MONEDAS.COD_DOLAR) == 0) {
								rid = nivel.getRangoInicio();
								rfd = nivel.getRangoFin();
							}

							if (nivel.getMoneda().compareTo(
									ConstantesVisado.MONEDAS.COD_EUROS) == 0) {
								rie = nivel.getRangoInicio();
								rfe = nivel.getRangoFin();
							}

						}

						IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
						logger.debug("usuario.getUID(): " + usuario.getUID());

						if (respNiveles.size() > 0) {
							for (int i = 0; i < respNiveles.size(); i++) {
								if (respNiveles.get(i).getDesNivel()
										.equals(desNivel)) {

								}
							}
						} else {

						}
						respNiveles.add(new MiembroNivelDTO(1, miembroNivel
								.getTiivsMiembro().getDescripcion(),
								miembroNivel.getCodNiv(), desNivel,
								miembroNivel.getTiivsMiembro().getCodMiembro(),
								(new Date()).toString(), usuario.getUID(),
								miembroNivel.getEstado(), descEstado, ris, rfs,
								rid, rfd, rie, rfe));

						logger.debug("== despues de agregar.. ===");

					} else {

						Utilitarios.mensajeInfo("Mensaje","Ya existe un miembro nivel");
						logger.debug("ya existe un miembro nivel");
					}

				} else {
					if (miembroNivels.size() == 0) {
						TiivsMiembroNivel miembroNivel = new TiivsMiembroNivel();

						TiivsMiembro miembro = new TiivsMiembro();
						miembro.setCodMiembro(miembroNivelDto.getRegistro());
						miembro.setDescripcion(miembroNivelDto.getDescripcion());
						miembroNivel.setTiivsMiembro(miembro);

						miembroNivel.setCodNiv(getCodNivel());
						miembroNivel.setTipoRol("R");
						miembroNivel.setEstado(miembroNivelDto.getEstado());
	
						GenericDao<TiivsNivel, Object> serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

						String descEstado = "";
						String desNivel = "";

						descEstado = respNivelAprobacionService.obtenerDesEstado(miembroNivel.getCodNiv());
						desNivel = respNivelAprobacionService.obtenerDesNivel(miembroNivel.getCodNiv());

						List<TiivsNivel> tiivsNivels = new ArrayList<TiivsNivel>();
						Busqueda filtroTiivsNivelMoneda = Busqueda
								.forClass(TiivsNivel.class);
						try {
							tiivsNivels = serviceTiivsNivel
									.buscarDinamico(filtroTiivsNivelMoneda
											.add(Restrictions.eq("codNiv",
													miembroNivel.getCodNiv())));
						} catch (Exception e1) {
							logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
									+ "al obtener nivelesMonedas: " + e1);
						}

						int ris = 0;
						int rfs = 0;

						int rid = 0;
						int rfd = 0;

						int rie = 0;
						int rfe = 0;

						if (tiivsNivels != null) {
							logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA
									+ "tiivsNivels es: ["
									+ tiivsNivels.size()
									+ "]");
						}
						for (TiivsNivel nivel : tiivsNivels) {

							if (nivel.getMoneda().compareTo(
									ConstantesVisado.MONEDAS.COD_SOLES) == 0) {
								ris = nivel.getRangoInicio();
								rfs = nivel.getRangoFin();
							}

							if (nivel.getMoneda().compareTo(
									ConstantesVisado.MONEDAS.COD_DOLAR) == 0) {
								rid = nivel.getRangoInicio();
								rfd = nivel.getRangoFin();
							}

							if (nivel.getMoneda().compareTo(
									ConstantesVisado.MONEDAS.COD_EUROS) == 0) {
								rie = nivel.getRangoInicio();
								rfe = nivel.getRangoFin();
							}

						}

						IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
						logger.debug("usuario.getUID(): " + usuario.getUID());

						if (respNiveles.size() > 0) {
							for (int i = 0; i < respNiveles.size(); i++) {
								if (respNiveles.get(i).getDesNivel()
										.equals(desNivel)) {

								}
							}
						} else {

						}
						respNiveles.add(new MiembroNivelDTO(1, miembroNivel
								.getTiivsMiembro().getDescripcion(),
								miembroNivel.getCodNiv(), desNivel,
								miembroNivel.getTiivsMiembro().getCodMiembro(),
								(new Date()).toString(), usuario.getUID(),
								miembroNivel.getEstado(), descEstado, ris, rfs,
								rid, rfd, rie, rfe));

						logger.debug("== despues de agregar.. ===");

					} else {
						Utilitarios.mensajeInfo("Mensaje","Ya existe un miembro nivel");
						logger.debug("ya existe un miembro nivel");
					}

				}
			}
		}

	}

	public void confirmarCambios() {

		logger.info("=== inicia confirmarCambios() ===");
		List<TiivsMiembro> miembroBuscar = new ArrayList<TiivsMiembro>();
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsMiembro, Object> serviceTiivsMiembro = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		if (respNiveles != null) {
			logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA
					+ "respNiveles es: [" + respNiveles.size() + "]");
		}

		for (MiembroNivelDTO miembroNivelDTO : respNiveles) {

			logger.debug("miembroNivelDTO.getNuevo(): "
					+ miembroNivelDTO.getNuevo());
			if (miembroNivelDTO.getNuevo() == 1) {

				TiivsMiembro miembro = new TiivsMiembro();
				miembro.setCodMiembro(miembroNivelDTO.getRegistro());
				miembro.setDescripcion(miembroNivelDTO.getDescripcion());
				miembroBuscar = obtenerMiembro(miembro.getCodMiembro());
				
				TiivsGrupo tiivsGrupo = new TiivsGrupo();
				tiivsGrupo.setCodGrupo(miembroBuscar.get(0).getTiivsGrupo().getCodGrupo());	
				miembro.setTiivsGrupo(tiivsGrupo);
				miembro.setCriterio(miembroBuscar.get(0).getCriterio());
				miembro.setActivo(miembroBuscar.get(0).getActivo());
				/*
				 * try { miembro = serviceTiivsMiembro.save(miembro); } catch
				 * (Exception e1) { e1.printStackTrace(); }
				 */

				TiivsMiembroNivel miembroNivel = new TiivsMiembroNivel();
				miembroNivel.setTiivsMiembro(miembro);
				miembroNivel.setCodNiv(miembroNivelDTO.getCodNivel());
				miembroNivel.setTipoRol("R");
				miembroNivel.setEstado(miembroNivelDTO.getCodEstado());

				Date date = new Date();
				miembroNivel.setFechaRegistro(new Timestamp(date.getTime()));
				miembroNivel.setFechaAct(new Timestamp(date.getTime()));

				IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios
						.getObjectInSession("USUARIO_SESION");

				miembroNivel.setUsuarioRegistro(usuario.getUID());
				miembroNivel.setUsuarioAct(usuario.getUID());

				if (logger.isDebugEnabled()) {
					logger.debug("== Datos miembroNivel ==");
					logger.debug("[miemb-usuReg]:"
							+ miembroNivel.getUsuarioRegistro());
					logger.debug("[miemb-fechaReg]:"
							+ miembroNivel.getFechaRegistro());
					logger.debug("[miemb-codNivel]:" + miembroNivel.getCodNiv());
					logger.debug("[miemb-estado]:" + miembroNivel.getEstado());
					logger.debug("[miemb-tipoRol]:" + miembroNivel.getTipoRol());
				}
				try {
					serviceTiivsMiembroNivel.save(miembroNivel);
					logger.info(ConstantesVisado.MENSAJE.REGISTRO_OK + " el: "
							+ miembroNivelDTO.getRegistro());
				} catch (Exception e) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
							+ "al guardar MiembroNivel :"
							+ miembroNivelDTO.getRegistro() + " - " + e);
				}
			}

		}
		logger.info("=== saliendo de confirmarCambios() ===");
		try {
			
			RespNivelAprobacionMB instancia = new RespNivelAprobacionMB();
			instancia.listarRespxNivel();
			FacesContext.getCurrentInstance().getExternalContext().redirect("/VisadoPoderes/faces/paginas/respNivel.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private List<TiivsMiembro> obtenerMiembro(String codMiembro) {
		logger.info("EditRespNivelAprobacionMB : obtenerMiembro");
		List<TiivsMiembro> miembro = new ArrayList<TiivsMiembro>();
		try{
			miembro = respNivelAprobacionService.obtenerMiembro(codMiembro);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return miembro;
	}

	public void obtenerDatosMiembro() {
		logger.info("EditRespNivelAprobacionMB : obtenerDatosMiembro");
		try {
			if (!miembroNivelDto.getRegistro().equals("")) {
				miembros = delegadosService.obtenerDatosMiembro(miembroNivelDto
						.getRegistro().toUpperCase());
				if (miembros.size() > 0) {

					miembroNivelDto.setDescripcion(miembros.get(0)
							.getDescripcion());
					miembroNivelDto.setDesGrupo(miembros.get(0).getTiivsGrupo()
							.getDesGrupo());
					// criterioRegistro = miembros.get(0).getCriterio();
					validarCodRegistro = true;
				} else {
					Utilitarios
							.mensajeError("Error",
									"No se encuentra Registrado el codigo del Delegado");
					/*
					 * desRegistro = ""; perfilRegistro = ""; criterioRegistro =
					 * "";
					 */
					miembroNivelDto.setDescripcion("");
					miembroNivelDto.setDesGrupo("");
					validarCodRegistro = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("EditRespNivelAprobacionMB : obtenerDatosMiembro"
					+ ex.getLocalizedMessage());
		}
	}

	public void limpiarCampos() {
		if (miembroNivelDto != null) {
			miembroNivelDto.setRegistro("");
			miembroNivelDto.setDescripcion("");
			miembroNivelDto.setDesGrupo("");
			miembroNivelDto.setEstado("");
			miembroNivelDto.setCodNivel("-1");
			validarCodRegistro = false;
		} else {
			miembroNivelDto = new MiembroNivelDTO();
		}
	}

	public MiembroNivelDTO getMiembroNivelDto() {
		return miembroNivelDto;
	}

	public void setMiembroNivelDto(MiembroNivelDTO miembroNivelDto) {
		this.miembroNivelDto = miembroNivelDto;
	}

	public List<NivelDto> getNiveles() {
		return niveles;
	}

	public void setNiveles(List<NivelDto> niveles) {
		this.niveles = niveles;
	}

	public List<MiembroNivelDTO> getRespNiveles() {
		return respNiveles;
	}

	public void setRespNiveles(List<MiembroNivelDTO> respNiveles) {
		this.respNiveles = respNiveles;
	}

	public boolean isFlagVisible() {
		return flagVisible;
	}

	public void setFlagVisible(boolean flagVisible) {
		this.flagVisible = flagVisible;
	}

	public String getCodNivel() {
		return codNivel;
	}

	public void setCodNivel(String codNivel) {
		this.codNivel = codNivel;
	}

	public String getCodRegistro() {
		return codRegistro;
	}

	public void setCodRegistro(String codRegistro) {
		this.codRegistro = codRegistro;
	}

	public boolean isValidarCodRegistro() {
		return validarCodRegistro;
	}

	public void setValidarCodRegistro(boolean validarCodRegistro) {
		this.validarCodRegistro = validarCodRegistro;
	}

	public void setIniciar(String iniciar) {
		this.iniciar = iniciar;
	}

	public boolean isbEditar() {
		return bEditar;
	}

	public void setbEditar(boolean bEditar) {
		this.bEditar = bEditar;
	}

	public String[] getEstados() {
		return estados;
	}

	public void setEstados(String[] estados) {
		this.estados = estados;
	}
}

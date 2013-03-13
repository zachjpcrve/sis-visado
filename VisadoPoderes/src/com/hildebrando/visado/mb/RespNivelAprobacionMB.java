package com.hildebrando.visado.mb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.GrupoDto;
import com.hildebrando.visado.dto.MiembroNivelDTO;
import com.hildebrando.visado.dto.NivelDto;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.service.RespNivelAprobacionService;

/**
 * Clase que maneja el mantenimiento de Responsable Nivel Aprobacion, contiene la 
 * busqueda, edidion, etc.
 * @author jsaldana
 * **/

@ManagedBean(name = "respNivel")
@SessionScoped
public class RespNivelAprobacionMB {
	
	public static Logger logger = Logger.getLogger(RespNivelAprobacionMB.class);
	
	private MiembroNivelDTO miembroNivelDto;
	private String codNivel;
	public String getCodNivel() {
		return this.codNivel;
	}

	public void setCodNivel(String codNivel) {
		this.codNivel = codNivel;
	}

	private List<GrupoDto> grupos;
	private List<NivelDto> niveles;
	private boolean limpiar;
	private List<MiembroNivelDTO> respNiveles;
		
	private RespNivelAprobacionService respNivelAprobacionService;
	public RespNivelAprobacionMB(){
		
		respNivelAprobacionService = new RespNivelAprobacionService();
		miembroNivelDto= new MiembroNivelDTO();
		grupos = new ArrayList<GrupoDto>();
		niveles = new ArrayList<NivelDto>();
		respNiveles = new ArrayList<MiembroNivelDTO>();
		cargarCombos();
		limpiar = false;	
	}
	
	private void cargarCombos(){
		//Grupos
		GenericDao<TiivsGrupo, Object> serviceTiivsGrupo = (GenericDao<TiivsGrupo, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsGrupo = Busqueda.forClass(TiivsGrupo.class);		
		try {
			List<TiivsGrupo> tiivsGrupos = serviceTiivsGrupo.buscarDinamico(filtroTiivsGrupo);
			for(TiivsGrupo tiivsGrupo: tiivsGrupos){				
				grupos.add(new GrupoDto(tiivsGrupo.getCodGrupo(),tiivsGrupo.getDesGrupo()));
			}			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+"de Grupos: "+e);
		}
		
		//Niveles
		GenericDao<TiivsNivel, Object> serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsNivel = Busqueda.forClass(TiivsNivel.class).setProjection(Projections.distinct(Projections.property("codNiv")));
		try {
			List<String> tiivsNivels = serviceTiivsNivel.buscarDinamicoString(filtroTiivsNivel);
			for(String s:tiivsNivels){
				Busqueda filtroTiivsNivel2 = Busqueda.forClass(TiivsNivel.class);
				List<TiivsNivel>  list= serviceTiivsNivel.buscarDinamico(filtroTiivsNivel2.add(Restrictions.eq("codNiv", s)));
				String des= list.get(0).getDesNiv();
				
				niveles.add(new NivelDto(s,des));
			}			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+"de niveles: "+e);
		}
	}
	
/*	public void editarRespNivelAprob(){
		logger.debug("=== inicia editarRespNivelAprob() ====");
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		String idResp;
		String editar;
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		idResp = params.get("idResp");
		editar = params.get("editar");
		TiivsMiembroNivel miembroNivel= new TiivsMiembroNivel();
		Busqueda filtroMiembro= Busqueda.forClass(TiivsMiembroNivel.class);
		filtroMiembro.add(Restrictions.eq("tiivsMiembro.codMiembro", idResp));
		filtroMiembro.add(Restrictions.eq("tipoRol", "R"));
		try {
			miembroNivel= serviceTiivsMiembroNivel.buscarById(TiivsMiembroNivel.class, Integer.parseInt(idResp));
			logger.info("miembroNivel.toString() :::: "+miembroNivel.toString());
		} catch (NumberFormatException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al formatear: "+e);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar miembroNivel: "+e);
		}
		
		ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
		ec.getSessionMap().put("miembroNivel", miembroNivel);
		 ec.getSessionMap().put("editar", editar);
		try {
			ec.redirect("newEditRespNivel.xhtml?faces-redirect=true");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel: "+e);
		}
		logger.debug("=== saliendo de editarRespNivelAprob() ====");
		//setLimpiar(false);
	}
	*/
	public void nuevoRespxNivel(){
		logger.debug("=== inicia nuevoRespxNivel() ====");
		TiivsMiembroNivel miembroNivel= new TiivsMiembroNivel();
		ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
		ec.getSessionMap().put("miembroNivel", miembroNivel);
		 
		try{
			this.setLimpiar(true);
			ec.redirect("newEditRespNivel.xhtml?faces-redirect=true");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel.xhtml: "+e);
		}
		logger.debug("=== saliendo nuevoRespxNivel() ====");
	}
	
	public void listarRespxNivel(){
		logger.debug("=== inicia listarRespxNivel() ====");
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsMiembro, Object> serviceTiivsMiembro = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsGrupo, Object> serviceTiivsGrupo = (GenericDao<TiivsGrupo, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		Busqueda filtroTiivsMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);
		//Se consulta los responsables por nivel en base al filtroTiivsMiembroNivel 
		filtroTiivsMiembroNivel.add(Restrictions.eq("tipoRol", "R"));
		filtroTiivsMiembroNivel.createAlias("tiivsMiembro", "miemb");
		
		List<TiivsMiembroNivel> list= new ArrayList<TiivsMiembroNivel>();
		if(miembroNivelDto!=null&&miembroNivelDto.getCodGrupo()!=null&&miembroNivelDto.getEstado()!=null){
			
		
		if(miembroNivelDto.getRegistro() != ""){
			logger.debug("[BUSQ]-REGISTRO: "+miembroNivelDto.getRegistro());
			filtroTiivsMiembroNivel.add(Restrictions.eq("miemb.codMiembro", miembroNivelDto.getRegistro()));
		}
		
		if(miembroNivelDto.getDescripcion() != ""){
			logger.debug("[BUSQ]-DESCRIPCION: "+miembroNivelDto.getDescripcion());
			filtroTiivsMiembroNivel.add(Restrictions.eq("miemb.descripcion", miembroNivelDto.getDescripcion()));
		}
		
		if(miembroNivelDto.getEstado() != "" && miembroNivelDto.getEstado().compareTo("-1") != 0 ){
			logger.debug("[BUSQ]-ESTADO: "+miembroNivelDto.getEstado());
			filtroTiivsMiembroNivel.add(Restrictions.eq("estado", miembroNivelDto.getEstado()));
		}
		

		if(miembroNivelDto.getCodGrupo() != "" && miembroNivelDto.getCodGrupo().compareTo("-1") != 0 ){
			logger.debug("[BUSQ]-CODGRUPO: "+miembroNivelDto.getCodGrupo());
			Busqueda filtroTiivsMiembro= Busqueda.forClass(TiivsMiembro.class);
			filtroTiivsMiembro.createAlias("tiivsGrupo", "grupo");
			filtroTiivsMiembro.add(Restrictions.eq("grupo.codGrupo", miembroNivelDto.getCodGrupo()));
			List<TiivsMiembro> miembros= new ArrayList<TiivsMiembro>();
			try {
				miembros = serviceTiivsMiembro.buscarDinamico(filtroTiivsMiembro);
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener miembros: "+e);
			}
			
			List<String> codigos= new ArrayList<String>();
			for(TiivsMiembro tiivsMiembro: miembros){
				codigos.add(tiivsMiembro.getCodMiembro());
			logger.info("codigos filtrar" + codigos);
			}
			filtroTiivsMiembroNivel.add(Restrictions.in("miemb.codMiembro", codigos));
			
		}
		
		if(miembroNivelDto.getCodNivel() != "" && miembroNivelDto.getCodNivel().compareTo("-1") != 0 ){
			logger.debug("[BUSQ]-CODNIVEL: "+miembroNivelDto.getCodNivel());
			filtroTiivsMiembroNivel.add(Restrictions.eq("codNiv", miembroNivelDto.getCodNivel()));
		}
		
		}
	
		
		try {
			list = serviceTiivsMiembroNivel.buscarDinamico(filtroTiivsMiembroNivel);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar Responsables por Nivel: "+e);
		}
		
		respNiveles = new ArrayList<MiembroNivelDTO>();
		
		if(list!=null){
			logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"Responsables por Nivel es ["+list.size()+"]  ." );
		}
		
		for(TiivsMiembroNivel  e:list){
			TiivsGrupo grupo= new TiivsGrupo();
			//TiivsMiembro miembro= new TiivsMiembro();
			try {
				grupo = serviceTiivsGrupo.buscarById(TiivsGrupo.class, e.getTiivsMiembro().getTiivsGrupo().getCodGrupo());
				//miembro = serviceTiivsMiembro.buscarById(TiivsMiembro.class, e.getTiivsMiembro().getCodMiembro());
			} catch (Exception ex) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener Grupo/Miembro: "+ex);
			}
			
			String descEstado="" ;
			String desNivel="";
			
			descEstado = respNivelAprobacionService.obtenerDesEstado(e.getCodNiv());
			desNivel = respNivelAprobacionService.obtenerDesNivel(e.getCodNiv());
				
			respNiveles.add(new MiembroNivelDTO(e.getId(), e.getCodNiv(),desNivel,e.getTiivsMiembro().getCodMiembro(),e.getTiivsMiembro().getDescripcion(),e.getTiivsMiembro().getTiivsGrupo().getCodGrupo(),
					grupo.getDesGrupo(),e.getFechaRegistro().toString(),e.getUsuarioRegistro(),e.getEstado(),descEstado));
		}
		if(respNiveles!=null && respNiveles.size()>0){
			for(int i=0;i <=respNiveles.size(); i++ ){
				if(logger.isDebugEnabled()){
					//logger.debug("==>  ID:"+respNiveles.get(i).getId()+"  Registro:"+respNiveles.get(i).getRegistro());
				}
			}	
		}
		
		
		logger.debug("=== saliendo de listarRespxNivel() ===");
	}
	public List<TiivsNivel> listarNivelesXusuario(String codNiv){
		GenericDao<TiivsNivel, Object> serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

		List<TiivsNivel> tiivsNivels = new ArrayList<TiivsNivel>();
		Busqueda filtroTiivsNivelMoneda = Busqueda.forClass(TiivsNivel.class);
		try {
			tiivsNivels = serviceTiivsNivel.buscarDinamico(filtroTiivsNivelMoneda.add(Restrictions.eq("codNiv",codNiv)));
		} catch (Exception e1) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+ "al obtener nivelesMonedas: " + e1);
		}
    return tiivsNivels;
	}
	//@Autor Samira
	public void listarNivelesPorResponsable()  throws Exception {
		    GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			String idResp=Utilitarios.capturarParametro("idResp");
			/*Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			idResp = params.get("idResp");*/
			List<TiivsMiembroNivel> listaDeMiembrosNivel= new ArrayList<TiivsMiembroNivel>();
			Busqueda filtroMiembro= Busqueda.forClass(TiivsMiembroNivel.class);
			filtroMiembro.add(Restrictions.eq("tiivsMiembro.codMiembro", idResp));
			filtroMiembro.add(Restrictions.eq("tipoRol", "R"));
			listaDeMiembrosNivel=serviceTiivsMiembroNivel.buscarDinamico(filtroMiembro);
			logger.info("Tamanio de la lista de miembro " +listaDeMiembrosNivel);
			
	}
	private boolean  validarRegistroResponsableNivel(){
		boolean retorno=true;
		if (miembroNivelDto.getEstado().equals("-1")) {
			retorno=false;
			Utilitarios.mensajeError("Info", "Debe seleccionar el estado");
		} else {
			if (codNivel.equals("-1")) {
				retorno=false;
				Utilitarios.mensajeError("Info", "Debe seleccionar un nivel");
			}
		}
		return retorno;
	}
	private void nuevoResponsable(){
		IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		logger.info("Creando nuevo Responsable");
		int ris = 0, rfs = 0;
		int rid = 0, rfd = 0;
		int rie = 0, rfe = 0;
		TiivsMiembroNivel miembroNivel = new TiivsMiembroNivel();
		TiivsMiembro miembro = new TiivsMiembro();
		miembro.setCodMiembro(miembroNivelDto.getRegistro());
		miembro.setDescripcion(miembroNivelDto.getDescripcion());
		miembroNivel.setTiivsMiembro(miembro);
		miembroNivel.setCodNiv(getCodNivel());
		miembroNivel.setTipoRol("R");
		miembroNivel.setEstado(miembroNivelDto.getEstado());
		miembroNivel.setDescNiv(respNivelAprobacionService.obtenerDesNivel(miembroNivel.getCodNiv()));
		miembroNivel.setDescEstado(respNivelAprobacionService.obtenerDesEstado(miembroNivel.getCodNiv()));
		List<TiivsNivel> tiivsNivels=this.listarNivelesXusuario(miembroNivel.getCodNiv());
		miembroNivel.setListaNiveles(tiivsNivels);
		for (TiivsNivel nivel : tiivsNivels) {
			if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_SOLES) == 0) {
				ris = nivel.getRangoInicio();rfs = nivel.getRangoFin();}
			if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_DOLAR) == 0) {
				rid = nivel.getRangoInicio();rfd = nivel.getRangoFin();}
			if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_EUROS) == 0) {
				rie = nivel.getRangoInicio();rfe = nivel.getRangoFin();}
		}
		respNiveles.add(new MiembroNivelDTO(1, miembroNivel.getTiivsMiembro().getDescripcion(),
											   miembroNivel.getCodNiv(), miembroNivel.getDescNiv(),
											   miembroNivel.getTiivsMiembro().getCodMiembro(),
											   (new Date()).toString(), usuario.getUID(),
											   miembroNivel.getEstado(), miembroNivel.getDescEstado(), ris, rfs, rid, rfd, rie, rfe));
												}
	//@Autor Samira
	private void agregarlistarRespxNivel() throws Exception {
		logger.debug("=== inicia agregarlistarRespxNivel() ====");
		if(validarRegistroResponsableNivel()){
			nuevoResponsable();
			listarNivelesPorResponsable();
			respNiveles.add(miembroNivelDto);
		}
		
		
	}
	//@Autor Samira
	public void editarRespNivelAprob(){
		logger.debug("=== inicia editarRespNivelAprob() ====");
		try {
		listarNivelesPorResponsable();
		ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
			ec.redirect("newEditRespNivel.xhtml?faces-redirect=true");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel: "+e);
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel: "+e);
		}
		logger.debug("=== saliendo de editarRespNivelAprob() ====");
		//setLimpiar(false);
	}
	
	
	
	public MiembroNivelDTO getMiembroNivelDto() {
		return miembroNivelDto;
	}


	public void setMiembroNivelDto(MiembroNivelDTO miembroNivelDto) {
		this.miembroNivelDto = miembroNivelDto;
	}

	public List<GrupoDto> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoDto> grupos) {
		this.grupos = grupos;
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

	public boolean isLimpiar() {
		return limpiar;
	}

	public void setLimpiar(boolean limpiar) {
		this.limpiar = limpiar;
	}
}

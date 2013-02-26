package com.hildebrando.visado.mb;

import java.io.IOException;
import java.util.ArrayList;
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
import com.hildebrando.visado.dto.GrupoDto;
import com.hildebrando.visado.dto.MiembroNivelDTO;
import com.hildebrando.visado.dto.NivelDto;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;

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
	private List<GrupoDto> grupos;
	private List<NivelDto> niveles;
	
	private List<MiembroNivelDTO> respNiveles;
	
	
	public RespNivelAprobacionMB(){
		
		miembroNivelDto= new MiembroNivelDTO();
		grupos = new ArrayList<GrupoDto>();
		niveles = new ArrayList<NivelDto>();
		respNiveles = new ArrayList<MiembroNivelDTO>();
		cargarCombos();
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
	
	public void editarRespNivelAprob(){
		logger.debug("=== inicia editarRespNivelAprob() ====");
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		String idResp;
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		idResp = params.get("idResp");
		
		TiivsMiembroNivel miembroNivel= new TiivsMiembroNivel();
		
		try {
			miembroNivel= serviceTiivsMiembroNivel.buscarById(TiivsMiembroNivel.class, Integer.parseInt(idResp));
		} catch (NumberFormatException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al formatear: "+e);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar miembroNivel: "+e);
		}
		
		ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
		ec.getSessionMap().put("miembroNivel", miembroNivel);
		 
		try {
			ec.redirect("newEditRespNivel.xhtml?faces-redirect=true");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel: "+e);
		}
		logger.debug("=== saliendo de editarRespNivelAprob() ====");
	}
	
	public void nuevoRespxNivel(){
		logger.debug("=== inicia nuevoRespxNivel() ====");
		TiivsMiembroNivel miembroNivel= new TiivsMiembroNivel();
		ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
		ec.getSessionMap().put("miembroNivel", miembroNivel);
		 
		try{
			ec.redirect("newEditRespNivel.xhtml?faces-redirect=true");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel.xhtml: "+e);
		}
		logger.debug("=== saliendo nuevoRespxNivel() ====");
	}
	
	public void listarRespxNivel(){
		logger.debug("=== inicia listarRespxNivel() ====");
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		Busqueda filtroTiivsMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);
		List<TiivsMiembroNivel> list= new ArrayList<TiivsMiembroNivel>();
		
		GenericDao<TiivsMiembro, Object> serviceTiivsMiembro = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		GenericDao<TiivsGrupo, Object> serviceTiivsGrupo = (GenericDao<TiivsGrupo, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		filtroTiivsMiembroNivel.createAlias("tiivsMiembro", "miemb");
		
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
			}
			filtroTiivsMiembroNivel.add(Restrictions.in("miemb.codMiembro", codigos));
		}
		
		if(miembroNivelDto.getCodNivel() != "" && miembroNivelDto.getCodNivel().compareTo("-1") != 0 ){
			logger.debug("[BUSQ]-CODNIVEL: "+miembroNivelDto.getCodNivel());
			filtroTiivsMiembroNivel.add(Restrictions.eq("codNiv", miembroNivelDto.getCodNivel()));
		}
		//Se consulta los responsables por nivel en base al filtroTiivsMiembroNivel 
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
			TiivsMiembro miembro= new TiivsMiembro();
			try {
				grupo = serviceTiivsGrupo.buscarById(TiivsGrupo.class, e.getTiivsMiembro().getTiivsGrupo().getCodGrupo());
				miembro = serviceTiivsMiembro.buscarById(TiivsMiembro.class, e.getTiivsMiembro().getCodMiembro());
			} catch (Exception ex) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener Grupo/Miembro: "+ex);
			}
			
			String descEstado="" ;
			String desNivel="";
			
			if(e.getEstado().compareTo(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO)==0)
				descEstado= ConstantesVisado.ESTADOS.ESTADO_ACTIVO;
			
			if(e.getEstado().compareTo(ConstantesVisado.ESTADOS.ESTADO_COD_DESACTIVO)==0)
				descEstado = ConstantesVisado.ESTADOS.ESTADO_DESACTIVO;
			
			if(e.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL1)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL1;
				
			if(e.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL2)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL2;
			
			if(e.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL3)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL3;
			
			if(e.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL4)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL4;
				
			respNiveles.add(new MiembroNivelDTO(e.getId(), e.getCodNiv(),desNivel,e.getTiivsMiembro().getCodMiembro(),e.getTiivsMiembro().getDescripcion(),e.getTiivsMiembro().getTiivsGrupo().getCodGrupo(),
					grupo.getDesGrupo(),e.getFechaRegistro().toString(),e.getUsuarioRegistro(),e.getEstado(),descEstado));
		}
		
		for(int i=0;i <=respNiveles.size(); i++ ){
			if(logger.isDebugEnabled()){
				logger.debug("==>  ID:"+respNiveles.get(i).getId()+"  Registro:"+respNiveles.get(i).getRegistro());
			}
		}
		
		logger.debug("=== saliendo de listarRespxNivel() ===");
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

	
	
	
}

package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.MiembroNivelDTO;
import com.hildebrando.visado.dto.NivelDto;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;


@ManagedBean(name = "editRespNivel")
@RequestScoped
public class EditRespNivelAprobacionMB {
	
	public static Logger logger = Logger.getLogger(EditRespNivelAprobacionMB.class);
	
	private MiembroNivelDTO miembroNivelDto;
	private List<NivelDto> niveles;
	private List<MiembroNivelDTO> respNiveles;
	private boolean flagVisible;
	
	private String codNivel;
	
	public EditRespNivelAprobacionMB(){
		
		respNiveles = new ArrayList<MiembroNivelDTO>();
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession();
		TiivsMiembroNivel miembroNivel = (TiivsMiembroNivel) session.getAttribute("miembroNivel");
		//session.removeAttribute("miembroNivel");
		
		if(miembroNivel.getTiivsMiembro()!=null){
			miembroNivelDto= new MiembroNivelDTO();
			miembroNivelDto.setRegistro(miembroNivel.getTiivsMiembro().getCodMiembro());
			miembroNivelDto.setDesGrupo(miembroNivel.getTiivsMiembro().getTiivsGrupo().getDesGrupo());
			miembroNivelDto.setDescripcion(miembroNivel.getTiivsMiembro().getDescripcion());
			miembroNivelDto.setEstado(miembroNivel.getEstado());
			miembroNivelDto.setCodNivel(miembroNivel.getCodNiv());
			
			setFlagVisible(true);
			
		}else{
			
			miembroNivelDto= new MiembroNivelDTO();
			miembroNivelDto.setEstado(miembroNivel.getEstado());
			
			setFlagVisible(false);
		}
		
		niveles = new ArrayList<NivelDto>();
		
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
			
			logger.error("error al obtener la lista de cod de agrupacion "+  e.toString());
		}
		
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);
		List<TiivsMiembroNivel> list= new ArrayList<TiivsMiembroNivel>();
		
		try {
			list = serviceTiivsMiembroNivel.buscarDinamico(filtroTiivsMiembroNivel.add(Restrictions.eq("tiivsMiembro.codMiembro", miembroNivel.getTiivsMiembro().getCodMiembro())));
			
		} catch (Exception e) {
			
			logger.error("error al obtener la lista de resp x nivel "+ e.getMessage());
		}
		
		
		respNiveles = new ArrayList<MiembroNivelDTO>();
		
		for(TiivsMiembroNivel  e:list){
			
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
			
			List<TiivsNivel>  tiivsNivels= new ArrayList<TiivsNivel>();
			Busqueda filtroTiivsNivelMoneda = Busqueda.forClass(TiivsNivel.class);
			try {
				tiivsNivels= serviceTiivsNivel.buscarDinamico(filtroTiivsNivelMoneda.add(Restrictions.eq("codNiv", e.getCodNiv())));
			} catch (Exception e1) {
				logger.error("error al obtener niveles monedas "+  e1.getMessage());
			}
			
			int ris=0;
			int rfs=0;
			
			int rid=0;
			int rfd=0;
			
			int rie=0;
			int rfe=0;
			
			for(TiivsNivel  nivel:tiivsNivels){
				
				if(nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_SOLES)==0){
					ris= nivel.getRangoInicio();
					rfs= nivel.getRangoFin();
				}
				
				if(nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_DOLAR)==0){
					rid= nivel.getRangoInicio();
					rfd= nivel.getRangoFin();
				}
				
				if(nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_EUROS)==0){
					rie= nivel.getRangoInicio();
					rfe= nivel.getRangoFin();
				}
					
			}
			
				
			respNiveles.add(new MiembroNivelDTO(e.getId(), e.getCodNiv(),desNivel,e.getTiivsMiembro().getCodMiembro(),e.getTiivsMiembro().getDescripcion(),e.getTiivsMiembro().getTiivsGrupo().getCodGrupo(),
					e.getTiivsMiembro().getTiivsGrupo().getDesGrupo(),e.getFechaRegistro().toString(),e.getUsuarioRegistro(),e.getEstado(),descEstado,
					ris, rfs, rid, rfd, rie,rfe ));
		}
		
	}
	
	public void agregarlistarRespxNivel(){
		
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);
		
		List<TiivsMiembroNivel>  miembroNivels= new ArrayList<TiivsMiembroNivel>();
		
		try {
			miembroNivels = serviceTiivsMiembroNivel.buscarDinamico(filtroTiivsMiembroNivel.add(Restrictions.eq("tiivsMiembro.codMiembro", miembroNivelDto.getRegistro())).add(Restrictions.eq("codNiv", getCodNivel())));
		} catch (Exception e) {
			logger.error("error al acceder "+ e.getMessage());
		}
		
		
		if(miembroNivels.size() == 0){
			TiivsMiembroNivel miembroNivel= new TiivsMiembroNivel();
			
			TiivsMiembro miembro= new TiivsMiembro();
			miembro.setCodMiembro(miembroNivelDto.getRegistro());
			miembroNivel.setTiivsMiembro(miembro);
			
			miembroNivel.setCodNiv(getCodNivel());
			miembroNivel.setTipoRol("R");
			miembroNivel.setEstado(miembroNivelDto.getEstado());
			
			
			GenericDao<TiivsNivel, Object> serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			
			
			String descEstado="" ;
			String desNivel="";
			
			if(miembroNivel.getEstado().compareTo(ConstantesVisado.ESTADOS.ESTADO_COD_ACTIVO)==0)
				descEstado= ConstantesVisado.ESTADOS.ESTADO_ACTIVO;
			
			if(miembroNivel.getEstado().compareTo(ConstantesVisado.ESTADOS.ESTADO_COD_DESACTIVO)==0)
				descEstado = ConstantesVisado.ESTADOS.ESTADO_DESACTIVO;
			
			if(miembroNivel.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL1)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL1;
				
			if(miembroNivel.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL2)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL2;
			
			if(miembroNivel.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL3)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL3;
			
			if(miembroNivel.getCodNiv().compareTo(ConstantesVisado.COD_NIVEL4)==0)
				desNivel = ConstantesVisado.CAMPO_NIVEL4;
			
			List<TiivsNivel>  tiivsNivels= new ArrayList<TiivsNivel>();
			Busqueda filtroTiivsNivelMoneda = Busqueda.forClass(TiivsNivel.class);
			try {
				tiivsNivels= serviceTiivsNivel.buscarDinamico(filtroTiivsNivelMoneda.add(Restrictions.eq("codNiv", miembroNivel.getCodNiv())));
			} catch (Exception e1) {
				logger.error("error al obtener niveles monedas "+  e1.getMessage());
			}
			
			int ris=0;
			int rfs=0;
			
			int rid=0;
			int rfd=0;
			
			int rie=0;
			int rfe=0;
			
			for(TiivsNivel  nivel:tiivsNivels){
				
				if(nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_SOLES)==0){
					ris= nivel.getRangoInicio();
					rfs= nivel.getRangoFin();
				}
				
				if(nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_DOLAR)==0){
					rid= nivel.getRangoInicio();
					rfd= nivel.getRangoFin();
				}
				
				if(nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_EUROS)==0){
					rie= nivel.getRangoInicio();
					rfe= nivel.getRangoFin();
				}
					
			}
			
			respNiveles.add(new MiembroNivelDTO( miembroNivel.getCodNiv(),desNivel,miembroNivel.getTiivsMiembro().getCodMiembro()
					,(new Date()).toString(),"usuario",miembroNivel.getEstado(),descEstado,
					ris, rfs, rid, rfd, rie,rfe ));
			
			
		}else{
		
			logger.debug("ya existe un miembro nivel");
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

	

}

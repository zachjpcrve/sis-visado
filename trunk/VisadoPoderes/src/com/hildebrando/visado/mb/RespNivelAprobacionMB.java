package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.GrupoDto;
import com.hildebrando.visado.dto.MiembroNivelDTO;
import com.hildebrando.visado.dto.NivelDto;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsRevocado;


@ManagedBean(name = "respNivel")
@SessionScoped
public class RespNivelAprobacionMB {
	
	public static Logger logger = Logger.getLogger(RespNivelAprobacionMB.class);
	
	private MiembroNivelDTO miembroNivelDto;
	private List<GrupoDto> grupos;
	private List<NivelDto> niveles;
	
	private List<MiembroNivelDTO> respNiveles;
	
	
	public RespNivelAprobacionMB(){
		
		grupos = new ArrayList<GrupoDto>();
		niveles = new ArrayList<NivelDto>();
		cargarCombos();
	}
	
	private void cargarCombos(){
		
		
		GenericDao<TiivsGrupo, Object> serviceTiivsGrupo = (GenericDao<TiivsGrupo, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsGrupo = Busqueda.forClass(TiivsGrupo.class);
		
		try {
			List<TiivsGrupo> tiivsGrupos = serviceTiivsGrupo.buscarDinamico(filtroTiivsGrupo);
			for(TiivsGrupo tiivsGrupo: tiivsGrupos){
				
				grupos.add(new GrupoDto(tiivsGrupo.getCodGrupo(),tiivsGrupo.getDesGrupo()));
			}
			
		} catch (Exception e) {
			
			logger.error("error al obtener la lista de tiivsGrupos "+  e.toString());
		}
		
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
	} 
	
	public void listarRespxNivel(){
		
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

package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.hildebrando.visado.dto.AgrupacionDelegadosDto;
import com.hildebrando.visado.dto.AgrupacionNivelDelegadoDto;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;

@ManagedBean(name = "delegadosMB")
@SessionScoped
public class DelegadosMB {

	public static Logger logger = Logger.getLogger(DelegadosMB.class);
	private List<AgrupacionNivelDelegadoDto>  lstListaAgrupacionesNivelesDelegados;
	
	public DelegadosMB(){
		lstListaAgrupacionesNivelesDelegados=new ArrayList<AgrupacionNivelDelegadoDto>();
		listarAgrupacionesDelegados();
	}
	
	
	public void listarAgrupacionesDelegados() {
	try {
	  SolicitudDao<TiivsMiembroNivel, Object> service =(SolicitudDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
	  List<AgrupacionDelegadosDto>  lstDele= service.obtenerDelegados();
	  List<AgrupacionDelegadosDto>  lstDelegadosPK=service.obtenerPKDelegados();
	  List<ComboDto>         lstDuos=null;   
	  ComboDto duo=null;
	  AgrupacionNivelDelegadoDto agrupacionNivelDelegadoDto =null;
	
	 
	  for (AgrupacionDelegadosDto a : lstDelegadosPK) {
		  lstDuos=new ArrayList<ComboDto>();
		  agrupacionNivelDelegadoDto=new AgrupacionNivelDelegadoDto();
		  agrupacionNivelDelegadoDto.setNivel(a.getDes_niv());
		  agrupacionNivelDelegadoDto.setCodGrupo(a.getGrupo());
		  agrupacionNivelDelegadoDto.setLstDelegados(lstDuos);
		  
		    for (AgrupacionDelegadosDto b : lstDele) {
			  if(a.getDes_niv().equals(b.getDes_niv()) && a.getGrupo().equals(b.getGrupo())){
				  duo =new ComboDto();
				  duo.setKey(b.getCod_miembro());
				  duo.setDescripcion(b.getDescripcion());
				  lstDuos.add(duo);
			  }
		     }
		    lstListaAgrupacionesNivelesDelegados.add(agrupacionNivelDelegadoDto);
	    }
	  
	  for (AgrupacionNivelDelegadoDto c : lstListaAgrupacionesNivelesDelegados){
		    c.setCod_delegado_A(c.getLstDelegados().get(0)==null?"":c.getLstDelegados().get(0).getKey());
		    c.setCod_nombre_delegado_A(c.getLstDelegados().get(0)==null?"":c.getLstDelegados().get(0).getDescripcion());
			c.setCod_delegado_B(c.getLstDelegados().get(1)==null?"":c.getLstDelegados().get(1).getKey());
			c.setCod_nombre_delegado_B(c.getLstDelegados().get(1)==null?"":c.getLstDelegados().get(1).getDescripcion());
			/*c.setCod_delegado_C(c.getLstDelegados().get(2)==null?"":c.getLstDelegados().get(2).getKey());
			c.setCod_nombre_delegado_C(c.getLstDelegados().get(2)==null?"":c.getLstDelegados().get(3).getDescripcion());
			c.setCod_delegado_D(c.getLstDelegados().get(3)==null?"":c.getLstDelegados().get(3).getKey());
			c.setCod_nombre_delegado_D(c.getLstDelegados().get(3)==null?"":c.getLstDelegados().get(3).getDescripcion());
			c.setCod_delegado_E(c.getLstDelegados().get(4)==null?"":c.getLstDelegados().get(4).getKey());
			c.setCod_nombre_delegado_E(c.getLstDelegados().get(4)==null?"":c.getLstDelegados().get(4).getDescripcion());*/
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
	
	
	
}

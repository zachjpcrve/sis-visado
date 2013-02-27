package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
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
		  for (int i = 0; i < c.getLstDelegados().size(); i++) {
			  System.out.println("i " + i + " dd " +c.getLstDelegados().get(i).getDescripcion());
			    c.setCod_delegado_A(i==0?c.getLstDelegados().get(i).getKey():"");
			    c.setCod_nombre_delegado_A(c.getLstDelegados().get(i).getDescripcion());
				c.setCod_delegado_B(i==1?c.getLstDelegados().get(i).getKey():"");
				c.setCod_nombre_delegado_B(c.getLstDelegados().get(i).getDescripcion());
				c.setCod_delegado_C(i==2?c.getLstDelegados().get(i).getKey():"");
				c.setCod_nombre_delegado_C(i==2?c.getLstDelegados().get(i).getDescripcion():"");
				c.setCod_delegado_D(i==3?c.getLstDelegados().get(i).getKey():"");
				c.setCod_nombre_delegado_D(i==3?c.getLstDelegados().get(i).getDescripcion():"");
				c.setCod_delegado_E(i==4?c.getLstDelegados().get(i).getKey():"");
				c.setCod_nombre_delegado_E(i==4?c.getLstDelegados().get(i).getDescripcion():"");
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
	
	
	
}

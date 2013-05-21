package com.hildebrando.visado.mb;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsTerritorio;
import com.hildebrando.visado.service.OficinaService;

@ManagedBean(name = "oficinaMB")
@SessionScoped
public class OficinaMB {
	public static Logger logger = Logger.getLogger(OficinaMB.class);
	private OficinaService oficinaService;
	private TiivsOficina1 oficina;
	private TiivsTerritorio territorio;
	private List<TiivsOficina1> oficinas;
	private List<TiivsOficina1> comboOficinas;
	private List<TiivsTerritorio> comboTerritorios;
	private List<TiivsOficina1> oficinaEditar;
	private String estado;
	
	public OficinaMB(){
		estado="";
		oficina = new TiivsOficina1();
		territorio = new TiivsTerritorio();
		oficinaService = new OficinaService();
		listarOficinas();
		cargarComboOficina();
		cargarComboTerritorio();
		
	}
	
	public void listarOficinas() {
		logger.info("OficinaMB: listarClasificaciones");
		try {
			oficinas = oficinaService.listarOficinas();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("OficinaMB : listarClasificaciones : " + e.getLocalizedMessage());
		}

	}
	
	public void cargarComboTerritorio(){
		logger.info("OficinaMB: cargarComboTerritorio");
		try {
			comboTerritorios = oficinaService.cargarComboTerritorio();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("OficinaMB : cargarComboTerritorio : " + e.getLocalizedMessage());
		}
		
	}
	
	public void cargarComboOficina(){
		logger.info("OficinaMB: cargarComboOficina");
		try {
			comboOficinas = oficinaService.cargarComboOficina();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("OficinaMB : cargarComboOficina : " + e.getLocalizedMessage());
		}
	}
	
	public void editarOficina(){
		logger.info("OficinaMB: editarOficina");
		String codOficina;
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		
		codOficina = params.get("codOficina");
		try{
			oficinaEditar = oficinaService.editarOficina(codOficina);
			for (int i = 0; i < oficinaEditar.size(); i++) {
				oficina.setCodOfi(oficinaEditar.get(i).getCodOfi());
				oficina.setTiivsTerritorio(oficinaEditar.get(i).getTiivsTerritorio());
				oficina.setDesOfi(oficinaEditar.get(i).getDesOfi());
				oficina.setActivo(oficinaEditar.get(i).getActivo());
				oficina.setTiivsSolicituds(oficinaEditar.get(i).getTiivsSolicituds());
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("OficinaMB : editarOficina : " + e.getLocalizedMessage());
		}
		
	}
	public void cambiarEstado(){
		logger.info("OficinaMB: cambiarEstado");

		try{
			oficinaService.cambiarEstado(oficina);
			listarOficinas();
			Utilitarios.mensajeInfo("NIVEL", "Se actualizó correctamente");
		}catch(Exception e){
			e.printStackTrace();
			logger.error("OficinaMB : cambiarEstado : " + e.getLocalizedMessage());
			Utilitarios.mensajeError("Error", "Error al actualizar la oficina");
		}
		
	}
	
	public void selecTerritorioOficina(){
		logger.info("OficinaMB: cambiarEstado");
		try{
			comboOficinas = oficinaService.selecTerritorioOficina(territorio);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("OficinaMB : cambiarEstado : " + e.getLocalizedMessage());
		}
	}
	public void listarOficinasCombo(){
		logger.info("OficinaMB: listarOficinasCombo");
		try{
			oficinas= oficinaService.listarOficinasCombo(territorio, oficina, estado);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("OficinaMB : listarOficinasCombo : " + e.getLocalizedMessage());
		}
	}
	
	public TiivsOficina1 getOficina() {
		return oficina;
	}
	public void setOficina(TiivsOficina1 oficina) {
		this.oficina = oficina;
	}
	public List<TiivsOficina1> getOficinas() {
		return oficinas;
	}
	public void setOficinas(List<TiivsOficina1> oficinas) {
		this.oficinas = oficinas;
	}

	public List<TiivsOficina1> getOficinaEditar() {
		return oficinaEditar;
	}

	public void setOficinaEditar(List<TiivsOficina1> oficinaEditar) {
		this.oficinaEditar = oficinaEditar;
	}

	public List<TiivsOficina1> getComboOficinas() {
		return comboOficinas;
	}

	public void setComboOficinas(List<TiivsOficina1> comboOficinas) {
		this.comboOficinas = comboOficinas;
	}

	public List<TiivsTerritorio> getComboTerritorios() {
		return comboTerritorios;
	}

	public void setComboTerritorios(List<TiivsTerritorio> comboTerritorios) {
		this.comboTerritorios = comboTerritorios;
	}

	public TiivsTerritorio getTerritorio() {
		return territorio;
	}

	public void setTerritorio(TiivsTerritorio territorio) {
		this.territorio = territorio;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}

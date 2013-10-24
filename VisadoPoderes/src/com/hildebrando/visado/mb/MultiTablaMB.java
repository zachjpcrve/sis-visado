package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.service.AbogadoService;
import com.hildebrando.visado.service.MultiTablaService;

@ManagedBean(name = "multiMB")
@SessionScoped
public class MultiTablaMB {
	public static Logger logger = Logger.getLogger(AbogadoMB.class);


	private TiivsMultitabla multiTabla;
	private TiivsMultitabla multiTablaLista;
	private TiivsMultitabla multiTablaEliminar;
	private MultiTablaService multiTablaService;
	private String codigoMultiTabla = "";
	private String codigoElemento = "";
	private List<String> filtroTablas;
	private boolean flagEditar=false;
	
	
	private List<TiivsMultitabla> multiTablas = new ArrayList<TiivsMultitabla>();
	
	public MultiTablaMB() {
		
		multiTablaService = new MultiTablaService();
		multiTabla = new TiivsMultitabla();
		multiTablaLista = new TiivsMultitabla();
		multiTablaEliminar = new TiivsMultitabla();
		cargarFiltroTablas();
		
	}

	private void cargarFiltroTablas() {
		filtroTablas = this.multiTablaService.listarCodigoMultiTabla();		
	}

	
	public void nuevaMultiTabla(){
		multiTabla = new TiivsMultitabla();
		multiTabla.setId(new TiivsMultitablaId("", ""));
		this.setFlagEditar(false);
	}
	
	
	public void registrar(){
		
		if(this.flagEditar == false){
			if(validarRegistroDuplicado(multiTabla)){
				Utilitarios.mensajeInfo("ERROR", "Código de tabla o elemento ya utilizado, no se realizarán cambios");
				return;
			}
		}
		
		if(multiTablaService.registrar(multiTabla)){			
			multiTablas = multiTablaService.listarMultiTabla(this.codigoMultiTabla, "");		
			multiTabla = new TiivsMultitabla();
			this.cargarFiltroTablas();	
			Utilitarios.mensajeInfo("NIVEL", "Se actualizó Multi-Tabla correctamente");
		} else {
			Utilitarios.mensajeError("ERROR", "No se actualizó Multi-Tabla");
		}
	}
	
	private boolean validarRegistroDuplicado(TiivsMultitabla multiBuscar) {
		for(TiivsMultitabla mult : multiTablas){
			if(mult.getId().equals(multiBuscar.getId())){
				return true;
			}
		}
		return false;
	}
	
	
	public void editarMultiTabla(){
				
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codigoMulti = params.get("codMult");
		String codigoElem = params.get("codElem");
		
		TiivsMultitablaId id = new TiivsMultitablaId(codigoMulti,codigoElem);
		
		for(TiivsMultitabla mult : this.multiTablas){
			if(mult.getId().equals(id)){
				multiTabla = mult;
				break;
			}
		}
		
		this.setFlagEditar(true);

	}


	public void listarMultiTabla() {
		
		logger.info("====== listarMultiTabla ======");
		
		String codigoSubTabla = codigoMultiTabla;
		String codigoElemento = "";
		
		try {
			multiTablas = multiTablaService.listarMultiTabla(codigoSubTabla, codigoElemento);
		} catch (Exception e) {
			logger.error("multiTablaMB : listarMultiTabla : "
					+ e.getLocalizedMessage());
		}
	}

	
	public void eliminarMultiTabla()
	{
		
		if(multiTablaService.eliminarMultiTabla(multiTabla)){
			Utilitarios.mensajeInfo("NIVEL", "Se eliminó correctamente");
			multiTablas = multiTablaService.listarMultiTabla(this.codigoMultiTabla, "");				
			this.cargarFiltroTablas();		
		} else {
			Utilitarios.mensajeError("ERROR", "No se eliminó Multi-Tabla");
		}

	}
	
	public void seleccionarMultiTabla()
	{

		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codigoMulti = params.get("codMult");
		String codigoElem = params.get("codElem");
		
		TiivsMultitablaId id = new TiivsMultitablaId(codigoMulti,codigoElem);
		
		for(TiivsMultitabla mult : this.multiTablas){
			if(mult.getId().equals(id)){
				multiTabla = mult;
				break;
			}
		}				

	}


	public TiivsMultitabla getMultiTabla() {
		return multiTabla;
	}

	public void setMultiTabla(TiivsMultitabla multiTabla) {
		this.multiTabla = multiTabla;
	}

	public TiivsMultitabla getMultiTablaLista() {
		return multiTablaLista;
	}

	public void setMultiTablaLista(TiivsMultitabla multiTablaLista) {
		this.multiTablaLista = multiTablaLista;
	}

	public TiivsMultitabla getMultiTablaEliminar() {
		return multiTablaEliminar;
	}

	public void setMultiTablaEliminar(TiivsMultitabla multiTablaEliminar) {
		this.multiTablaEliminar = multiTablaEliminar;
	}

	public List<TiivsMultitabla> getMultiTablas() {
		return multiTablas;
	}

	public void setMultiTablas(List<TiivsMultitabla> multiTablas) {
		this.multiTablas = multiTablas;
	}

	public String getCodigoMultiTabla() {
		return codigoMultiTabla;
	}

	public void setCodigoMultiTabla(String codigoMultiTabla) {
		this.codigoMultiTabla = codigoMultiTabla;
	}

	public String getCodigoElemento() {
		return codigoElemento;
	}

	public void setCodigoElemento(String codigoElemento) {
		this.codigoElemento = codigoElemento;
	}

	public List<String> getFiltroTablas() {
		return filtroTablas;
	}

	public void setFiltroTablas(List<String> filtroTablas) {
		this.filtroTablas = filtroTablas;
	}

	public boolean isFlagEditar() {
		return flagEditar;
	}

	public void setFlagEditar(boolean flagEditar) {
		this.flagEditar = flagEditar;
	}


}

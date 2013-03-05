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
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.service.AbogadoService;

@ManagedBean(name = "abogadoMB")
@SessionScoped
public class AbogadoMB {
	public static Logger logger = Logger.getLogger(AbogadoMB.class);

	private TiivsMiembro abogado = new TiivsMiembro();
	private TiivsMiembro abogadoLista = new TiivsMiembro();
	private TiivsMiembro abogadoEliminar = new TiivsMiembro();
	private List<TiivsEstudio> estudios = new ArrayList<TiivsEstudio>();
	private List<TiivsEstudio> estudiosFiltro = new ArrayList<TiivsEstudio>();
	private TiivsEstudio estudioFiltro = new TiivsEstudio();
	private List<TiivsGrupo> grupos = new ArrayList<TiivsGrupo>();
	private List<TiivsGrupo> gruposFiltro = new ArrayList<TiivsGrupo>();
	private TiivsGrupo grupoFiltro = new TiivsGrupo();
	private TiivsGrupo grupoFiltro2 = new TiivsGrupo();
	private List<TiivsMultitabla> criterios = new ArrayList<TiivsMultitabla>();
	private List<TiivsMiembro> abogados = new ArrayList<TiivsMiembro>();
	private List<TiivsMiembro> abogadoEditar = new ArrayList<TiivsMiembro>();
	private AbogadoService abogadoService;
	private boolean mostrarComboEstudio;
	private boolean mostrarComboEstudioGuardar;
	private boolean editarAbogado;
	private boolean mostrarCodigoEditar;
	
	public AbogadoMB() {
		abogadoService = new AbogadoService();
		abogado = new TiivsMiembro();
		grupoFiltro = new TiivsGrupo();
		estudioFiltro = new TiivsEstudio();
		abogado.setTiivsGrupo(grupoFiltro);
		abogadoEliminar= new TiivsMiembro();
		abogadoEliminar.setTiivsGrupo(grupoFiltro);
		abogadoLista = new TiivsMiembro();
		abogadoLista.setTiivsGrupo(grupoFiltro2);
		mostrarComboEstudio = false;
		mostrarComboEstudioGuardar = false;
		editarAbogado = false;
		mostrarCodigoEditar = false;
		listarEstudios();
		listarCriterios();
		listarGrupos();
		cargarCombosFiltro();
		listarAbogados();
		
	}

	public void registrar(){
		logger.info("AbogadoMB : registrar");
		
		String contador ="";
		abogado.setActivo("1");
		if(abogado.getEstudio()== null || abogado.getEstudio().equals("-1")){		
			abogado.setEstudio("");
		}
		try{
		if(abogado.getCodMiembro().isEmpty()==false){
			if(abogado.getDescripcion().isEmpty()==false){
				if(isEditarAbogado()==true){;
					if(	!abogado.getTiivsGrupo().getCodGrupo().equals(ConstantesVisado.COD_GRUPO_JRD)){
						abogado.setEstudio(null);						
					}
					abogadoService.registrar(abogado);
					Utilitarios.mensajeInfo("NIVEL", "Se actualizo correctamente");
				}else{
					contador = abogadoService.validarCodigo(abogado);
					if(contador.equals("0")){
						abogadoService.registrar(abogado);
						Utilitarios.mensajeInfo("NIVEL", "Se registro correctamente");
					}else{
						Utilitarios.mensajeError("ERROR", "El codigo ya ha sido registrado");
					}
				}
			}else{
				Utilitarios.mensajeError("ERROR", "La descripcion es obligatoria");
			}
		}else{
			Utilitarios.mensajeError("ERROR", "El codigo es obligatorio");
		}
		
			
			
			abogado = new TiivsMiembro();
			grupoFiltro = new TiivsGrupo();
			abogado.setTiivsGrupo(grupoFiltro);
			mostrarComboEstudioGuardar = false;
			mostrarCodigoEditar = false;
			editarAbogado=false;
		}catch(Exception e){
			e.printStackTrace();
			logger.error("AbogadoMB : registrar : "
					+ e.getLocalizedMessage());
			Utilitarios.mensajeError("ERROR", "No se pudo Registrar");
		}
		
	}
	
	private void cargarCombosFiltro() {
		logger.info("AbogadoMB : cargarCombosFiltro");
		comboGrupos();
		comboEstudio();
	}
	
	
	private void comboEstudio() {
		logger.info("AbogadoMB : comboEstudio");
		try {
			estudiosFiltro = abogadoService.listarEstudios();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AbogadoMB : comboEstudio : "
					+ e.getLocalizedMessage());
		}
		
	}
	
	public void selecPerfilEstudio(){
		logger.info("AbogadoMB : selecPerfilEstudio");
		if(abogado.getTiivsGrupo().getCodGrupo().equals("0000002")){
			mostrarComboEstudio = true;
		}else{
			mostrarComboEstudio = false;
		}
	}
	
	public void selecPerfilEstudioGuardar(){
		logger.info("AbogadoMB : selecPerfilEstudioGuardar");
		if(abogado.getTiivsGrupo().getCodGrupo().equals("0000002")){
			mostrarComboEstudioGuardar = true;
		}else{
			mostrarComboEstudioGuardar = false;
		}
	}

	private void comboGrupos() {
		logger.info("AbogadoMB : comboGrupos");
		try {
			gruposFiltro = abogadoService.listarGrupos();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AbogadoMB : listarGrupos : "
					+ e.getLocalizedMessage());
		}
		
	}
	
	public void editarAbogado(){
		logger.info("AbogadoMB : editarAbogado");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codAbogado;
		codAbogado = params.get("codAbogado");
		try {
			abogadoEditar = abogadoService.editarAbogado(codAbogado);
			for (int i = 0; i < abogadoEditar.size(); i++) {
				abogado.setCodMiembro(abogadoEditar.get(i).getCodMiembro());
				abogado.setDescripcion(abogadoEditar.get(i).getDescripcion());
				abogado.setTiivsGrupo(abogadoEditar.get(i).getTiivsGrupo());
				abogado.setCriterio(abogadoEditar.get(i).getCriterio());
				abogado.setEstudio(abogadoEditar.get(i).getEstudio());
				abogado.setTiivsMiembroNivels(abogadoEditar.get(i).getTiivsMiembroNivels());
				abogado.setActivo(abogadoEditar.get(i).getActivo());
			}
			
			editarAbogado = true;
			mostrarCodigoEditar = true;
			if(abogado.getTiivsGrupo().getCodGrupo().equals("0000002")){
				mostrarComboEstudioGuardar = true;
			}else{
				mostrarComboEstudioGuardar = false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoMB : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
	}

	private void listarGrupos() {
		logger.info("AbogadoMB : listarGrupos");
		try {
			grupos = abogadoService.listarGrupos();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AbogadoMB : listarGrupos : "
					+ e.getLocalizedMessage());
		}
		
	}

	private void listarCriterios() {
		logger.info("AbogadoMB : listarCriterios");
		try {
			criterios = abogadoService.listarCriterios();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AbogadoMB : listarCriterios : "
					+ e.getLocalizedMessage());
		}

	}
	
	private void listarEstudios() {
		logger.info("AbogadoMB : listarEstudios");
		try {
			estudios = abogadoService.listarEstudios();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AbogadoMB : listarEstudios : "
					+ e.getLocalizedMessage());
		}
	}

	//
	public void listarAbogados() {
		logger.info("AbogadoMB : listarAbogados");
		try {
			abogados = abogadoService.listarAbogados(estudios, criterios);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AbogadoMB : listarAbogados : "
					+ e.getLocalizedMessage());
		}

	}
	
	public void listarAbogadosCombo(){
		logger.info("OficinaMB: listarOficinasCombo");
		try{
			abogados= abogadoService.listarAbogadosCombo(grupoFiltro2, criterios, estudios, abogadoLista);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("OficinaMB : listarOficinasCombo : " + e.getLocalizedMessage());
		}
	}
	public void limpiarCampos(){
		logger.info("OficinaMB: limpiarCampos");
		abogado = new TiivsMiembro();
		grupoFiltro = new TiivsGrupo();
		abogado.setTiivsGrupo(grupoFiltro);
		mostrarComboEstudioGuardar = false;
		mostrarCodigoEditar = false;
		editarAbogado=false;
	}
	
	public void eliminarAbogado(){
		logger.info("OficinaMB: eliminarAbogado");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codAbogado;
		codAbogado = params.get("codAbogado");
		try {
			abogadoEditar = abogadoService.editarAbogado(codAbogado);
			for (int i = 0; i < abogadoEditar.size(); i++) {
				abogadoEliminar.setCodMiembro(abogadoEditar.get(i).getCodMiembro());
				abogadoEliminar.setDescripcion(abogadoEditar.get(i).getDescripcion());
				abogadoEliminar.setTiivsGrupo(abogadoEditar.get(i).getTiivsGrupo());
				abogadoEliminar.setCriterio(abogadoEditar.get(i).getCriterio());
				abogadoEliminar.setEstudio(abogadoEditar.get(i).getEstudio());
				abogadoEliminar.setTiivsMiembroNivels(abogadoEditar.get(i).getTiivsMiembroNivels());
				abogadoEliminar.setActivo("0");
			}
			
			abogadoService.eliminarAbogado(abogadoEliminar);
			Utilitarios.mensajeInfo("NIVEL", "Se elimino correctamente");
			editarAbogado = false;
			mostrarCodigoEditar = false;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoMB : eliminarAbogado: "
					+ ex.getLocalizedMessage());
			Utilitarios.mensajeError("ERROR", "No se pudo eliminar el registro");
		}
	}
	//
	public TiivsMiembro getAbogado() {
		return abogado;
	}

	public void setAbogado(TiivsMiembro abogado) {
		this.abogado = abogado;
	}

	public List<TiivsMultitabla> getCriterios() {
		return criterios;
	}

	public void setCriterios(List<TiivsMultitabla> criterios) {
		this.criterios = criterios;
	}

	public List<TiivsMiembro> getAbogados() {
		return abogados;
	}

	public void setAbogados(List<TiivsMiembro> abogados) {
		this.abogados = abogados;
	}

	public List<TiivsEstudio> getEstudios() {
		return estudios;
	}

	public void setEstudios(List<TiivsEstudio> estudios) {
		this.estudios = estudios;
	}

	public List<TiivsGrupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<TiivsGrupo> grupos) {
		this.grupos = grupos;
	}

	public List<TiivsEstudio> getEstudiosFiltro() {
		return estudiosFiltro;
	}

	public void setEstudiosFiltro(List<TiivsEstudio> estudiosFiltro) {
		this.estudiosFiltro = estudiosFiltro;
	}

	public List<TiivsGrupo> getGruposFiltro() {
		return gruposFiltro;
	}

	public void setGruposFiltro(List<TiivsGrupo> gruposFiltro) {
		this.gruposFiltro = gruposFiltro;
	}

	public TiivsGrupo getGrupoFiltro() {
		return grupoFiltro;
	}

	public void setGrupoFiltro(TiivsGrupo grupoFiltro) {
		this.grupoFiltro = grupoFiltro;
	}

	public boolean isMostrarComboEstudio() {
		return mostrarComboEstudio;
	}

	public void setMostrarComboEstudio(boolean mostrarComboEstudio) {
		this.mostrarComboEstudio = mostrarComboEstudio;
	}

	public TiivsEstudio getEstudioFiltro() {
		return estudioFiltro;
	}

	public void setEstudioFiltro(TiivsEstudio estudioFiltro) {
		this.estudioFiltro = estudioFiltro;
	}

	public boolean isMostrarComboEstudioGuardar() {
		return mostrarComboEstudioGuardar;
	}

	public void setMostrarComboEstudioGuardar(boolean mostrarComboEstudioGuardar) {
		this.mostrarComboEstudioGuardar = mostrarComboEstudioGuardar;
	}

	public List<TiivsMiembro> getAbogadoEditar() {
		return abogadoEditar;
	}

	public void setAbogadoEditar(List<TiivsMiembro> abogadoEditar) {
		this.abogadoEditar = abogadoEditar;
	}

	public boolean isEditarAbogado() {
		return editarAbogado;
	}

	public void setEditarAbogado(boolean editarAbogado) {
		this.editarAbogado = editarAbogado;
	}

	public boolean isMostrarCodigoEditar() {
		return mostrarCodigoEditar;
	}

	public void setMostrarCodigoEditar(boolean mostrarCodigoEditar) {
		this.mostrarCodigoEditar = mostrarCodigoEditar;
	}

	public TiivsMiembro getAbogadoEliminar() {
		return abogadoEliminar;
	}

	public void setAbogadoEliminar(TiivsMiembro abogadoEliminar) {
		this.abogadoEliminar = abogadoEliminar;
	}

	public TiivsMiembro getAbogadoLista() {
		return abogadoLista;
	}

	public void setAbogadoLista(TiivsMiembro abogadoLista) {
		this.abogadoLista = abogadoLista;
	}

	public TiivsGrupo getGrupoFiltro2() {
		return grupoFiltro2;
	}

	public void setGrupoFiltro2(TiivsGrupo grupoFiltro2) {
		this.grupoFiltro2 = grupoFiltro2;
	}
	
	
	
}

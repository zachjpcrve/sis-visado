package com.hildebrando.visado.mb;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.service.EstudioService;

@ManagedBean(name = "estudioMB")
@SessionScoped
public class EstudioMB {
	public static Logger logger = Logger.getLogger(EstudioMB.class);
	String estadoString = "1";
	char estadoChar = estadoString.charAt(0);
	Character estadoSi = Character.valueOf(estadoChar);
	
	private TiivsEstudio estudio;
	private List<TiivsEstudio> estudios;
	private List<TiivsEstudio> estudioEditar;

	private EstudioService estudioService;
	private boolean bMostrarCodigo;
	private boolean bEditar;

	public EstudioMB() {
		estudio = new TiivsEstudio();
		estudioService = new EstudioService();
		bMostrarCodigo = true;
		bEditar = false;
		listarEstudios();
		obtenerMaximo();
	}

	/***********/
	public void listarEstudios() {
		logger.info("EstudioMB : listarEstudios");
		try {
			estudios = estudioService.listarEstudios();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("EstudioMB : listarEstudios : "
					+ e.getLocalizedMessage());
		}
	}

	public void registrar() {
		logger.info("EstudioMB : registrar");
		try {
			if (estudio.getDesEstudio().isEmpty() == false) {
				if(estudio.getCosto() >= 0.0){
					//if(estudio.getCosto().intValue())
					//System.out.println(" estudio.getCosto().intValue() " + estudio.getCosto().intValue());
					if(isbEditar() == false){
						if(isNombreRegistrado(estudio) == false){
							estudioService.registrar(estudio);
							Utilitarios.mensajeInfo("NIVEL", "Se registró correctamente");
						} else {
							Utilitarios.mensajeError("NIVEL", "Descripción de estudio ya utilizado, no se actualizará");
						}
						
					}else{
						if(isNombreRegistrado(estudio) == false){
							estudioService.registrar(estudio);
							Utilitarios.mensajeInfo("NIVEL", "Se actualizó correctamente");
						} else {
							Utilitarios.mensajeError("NIVEL", "Descripción de estudio ya utilizado, no se actualizará");
						}
						
					}	
					this.listarEstudios();
				}else{
					Utilitarios.mensajeError("Error",
							"El costo no puede ser negativo");
				}
				
			} else {
				Utilitarios.mensajeError("Error",
						"El campo Descripción es obligatorio");
			}
			
			estudio = new TiivsEstudio();
			obtenerMaximo();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("EstudioMB : registrar " + e.getLocalizedMessage());
			Utilitarios.mensajeError("Error",
					"Error al registrar el Tipo de Clasificación");
		}
	}
	
	private boolean isNombreRegistrado(TiivsEstudio tiivsEstudio){
		List<TiivsEstudio> lstEstudios = estudioService.listarEstudios();
		for(TiivsEstudio est : lstEstudios){
			if(!est.getCodEstudio().equals(tiivsEstudio.getCodEstudio())){
				if(est.getDesEstudio().trim().equals(tiivsEstudio.getDesEstudio().trim())){
					return true;
				}
			}			
		}
		return false;
	}
	
	public void limpiarCampos(){
		logger.info("EstudioMB : limpiarCampos");
		estudio = new TiivsEstudio();
		obtenerMaximo();
	}
	
	public void editarEstudio(){
		logger.info("EstudioMB : editarEstudio");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codEstudio;
		codEstudio = params.get("codEstudio");
		try {
			estudioEditar = estudioService.editarEstudio(codEstudio);
			for (int i = 0; i < estudioEditar.size(); i++) {
				estudio.setCodEstudio(codEstudio);
				estudio.setDesEstudio(estudioEditar.get(i).getDesEstudio());
				estudio.setActivo(estudioEditar.get(i).getActivo());
				estudio.setCosto(estudioEditar.get(i).getCosto());
				estudio.setTiivsSolicituds(estudioEditar.get(i).getTiivsSolicituds());
				if(estudio.getActivo()== estadoSi){
					estudio.setDesActivo("1");
				}else{
					estudio.setDesActivo("0");
				}
			}
			
			bEditar = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoMB : listarDocumentos: "
					+ ex.getLocalizedMessage());
		}
		
	}
	public void obtenerMaximo() {
		logger.info("EstudioMB : obtenerMaximo");
		String secuencial = null;
		int parseSecuencial = 0;
		try {

			secuencial = estudioService.obtenerMaximo();
			parseSecuencial = Integer.parseInt(secuencial) + 1;
			secuencial = String.valueOf(parseSecuencial);

			if (secuencial.length() == 1) {
				secuencial = "000000" + secuencial;
			} else {
				if (secuencial.length() == 2) {
					secuencial = "00000" + secuencial;
				} else {
					if (secuencial.length() == 3) {
						secuencial = "0000" + secuencial;
					} else {
						if (secuencial.length() == 4) {
							secuencial = "000" + secuencial;
						} else {
							if (secuencial.length() == 5) {
								secuencial = "00" + secuencial;
							} else {
								if (secuencial.length() == 6) {
									secuencial = "0" + secuencial;
								} else {
									secuencial = secuencial;
								}
							}
						}
					}
				}
			}

			estudio.setCodEstudio(secuencial);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("EstudioMB : obtenerMaximo " + e.getLocalizedMessage());
		}
	}

	public TiivsEstudio getEstudio() {
		return estudio;
	}

	public void setEstudio(TiivsEstudio estudio) {
		this.estudio = estudio;
	}

	public List<TiivsEstudio> getEstudios() {
		return estudios;
	}

	public void setEstudios(List<TiivsEstudio> estudios) {
		this.estudios = estudios;
	}

	public boolean isbMostrarCodigo() {
		return bMostrarCodigo;
	}

	public void setbMostrarCodigo(boolean bMostrarCodigo) {
		this.bMostrarCodigo = bMostrarCodigo;
	}

	public List<TiivsEstudio> getEstudioEditar() {
		return estudioEditar;
	}

	public void setEstudioEditar(List<TiivsEstudio> estudioEditar) {
		this.estudioEditar = estudioEditar;
	}

	public boolean isbEditar() {
		return bEditar;
	}

	public void setbEditar(boolean bEditar) {
		this.bEditar = bEditar;
	}
	
	
	
}

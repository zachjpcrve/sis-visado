package com.hildebrando.visado.mb;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.service.ComisionService;

@ManagedBean(name = "comisionMB")
@SessionScoped
public class ComisionMB {
	public static Logger logger = Logger.getLogger(ComisionMB.class);

	private TiivsMultitabla comision;
	private List<TiivsMultitabla> comisiones;
	private ComisionService comisionService;

	public ComisionMB() {
		comision = new TiivsMultitabla();
		TiivsMultitablaId comisionId = new TiivsMultitablaId();
		comision.setId(comisionId);
		comisionService = new ComisionService();
		listarComisiones();
	}

	public void listarComisiones() {
		logger.info("ComisionMB : listarComisiones");
		try {
			comisiones = comisionService.listarComisiones();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ComisionMB : listarComisiones :"
					+ e.getLocalizedMessage());
		}
	}

	public void actualizar() {
		logger.info("ComisionMB : actualizar");
		String valor = "0";
		try {
			for (int j = 0; j < comisiones.size(); j++) {
				if (comisiones.get(j).getValor2().isEmpty() == false) {
					valor = "0";
				} else {
					valor = "1";
					break;
				}
			}
			if (valor.equals("0")) {
				for (int i = 0; i < comisiones.size(); i++) {

					comisionService.Actualizar(comisiones.get(i));

				}
				Utilitarios.mensajeInfo("NIVEL", "Se actualizó correctamente");
			} else {
				Utilitarios.mensajeError("Error", "El campo es Obligatorio");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ComisionMB : actualizar :" + e.getLocalizedMessage());
			Utilitarios.mensajeError("Error", "No se pudo actualizar");
		}

	}

	public TiivsMultitabla getComision() {
		return comision;
	}

	public void setComision(TiivsMultitabla comision) {
		this.comision = comision;
	}

	public List<TiivsMultitabla> getComisiones() {
		return comisiones;
	}

	public void setComisiones(List<TiivsMultitabla> comisiones) {
		this.comisiones = comisiones;
	}
}

package com.hildebrando.visado.mb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.service.NivelService;

@ManagedBean(name = "nivelMB")
@SessionScoped
public class NivelMB {
	public static Logger logger = Logger.getLogger(NivelMB.class);
	private SimpleDateFormat formatear = new SimpleDateFormat("dd/MM/yyyy");
	
	private TiivsNivel nivel;
	private List<TiivsMultitabla> moneda;
	private List<TiivsNivel> niveles;
	private List<TiivsNivel> nivelesMant;
	private NivelService nivelService;

	public NivelMB() {
		nivelService = new NivelService();
		nivelesMant = new ArrayList<TiivsNivel>();
		nivel = new TiivsNivel();
		obtenerMaximo();
		listarMonedas();
		listarNiveles();

	}
	
	public void obtenerMaximo() {
		logger.info("NivelMB : obtenerMaximo");
		String secuencial = null;
		int parseSecuencial = 0;
		try {

			secuencial = nivelService.obtenerMaximo();
			parseSecuencial = Integer.parseInt(secuencial) + 1;
			secuencial = String.valueOf(parseSecuencial);

			if (secuencial.length() == 1) {
				secuencial = "000" + secuencial;
			} else {
				if (secuencial.length() == 2) {
					secuencial = "00" + secuencial;
				} else {
					if (secuencial.length() == 3) {
						secuencial = "0" + secuencial;
					} else {
						secuencial = secuencial;
					}
				}

			}
			logger.info("NivelMB : secuencial" + " " + secuencial);
			
			for (int i = 0 ; i< ConstantesVisado.NUMERO_DE_MONEDAS_POR_NIVEL; i++){
				//nivelesMant.add(i, new TiivsNivel(new TiivsNivelId()));
				nivelesMant.get(i).setCodNiv(secuencial);
				/*nivelesMant.get(i).getId().setRangoInicio(12);
				nivelesMant.get(i).getId().setRangoFin(20);*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("NivelMB : secuencial :" + e.getLocalizedMessage());
		}
	}
	
	public void registrar() throws Exception{
		logger.info("NivelMB : registrar");
		Date sysDate = new Date();
		String utilDateString = formatear.format(sysDate);
		Date utilDateDate = formatear.parse(utilDateString);
		
		String valor = "0";
		try {
			for (int j = 0; j < nivelesMant.size(); j++) {
				if (nivelesMant.get(j).getRangoInicio() >= 0) {
					if (nivelesMant.get(j).getRangoFin() >= 0) {
						valor = "0";
					}else{
						valor = "1";
						break;
					}
				} else {
					valor = "1";
					break;
				}
			}
		//	nivelesMant.add(0, new TiivsNivel().getId().setMoneda("0001"));		
			nivelesMant.get(0).setMoneda("0001");
			nivelesMant.get(1).setMoneda("0002");
			nivelesMant.get(2).setMoneda("0003");
			if (valor.equals("0")) {
				for (int i = 0; i < nivelesMant.size(); i++) {
					nivelesMant.get(i).setDesNiv(nivelesMant.get(0).getDesNiv().toUpperCase());
					nivelesMant.get(i).setFechaReg(utilDateDate);
					nivelesMant.get(i).setUsuarioReg(Utilitarios.getObjectInSession("USUARIO_SESION").toString());
				}
				Utilitarios.mensajeInfo("NIVEL", "Se actualizo correctamente");
			} else {
				Utilitarios.mensajeError("Error", "El campo es Obligatorio");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ComisionMB : actualizar :" + e.getLocalizedMessage());
			Utilitarios.mensajeError("Error", "No se pudo actualizar");
		}
	}
	public void listarNiveles() {
		logger.info("NivelMB: listarNiveles");
		try {
			niveles = nivelService.listarNiveles(moneda);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("NivelMB: listarNiveles : " + e.getLocalizedMessage());
		}
	}

	public void listarMonedas() {
		logger.info("NivelMB: listarMonedas");
		try {
			moneda = nivelService.listarMonedas();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("NivelMB: listarMonedas : " + e.getLocalizedMessage());
		}
	}

	public TiivsNivel getNivel() {
		return nivel;
	}

	public void setNivel(TiivsNivel nivel) {
		this.nivel = nivel;
	}

	public List<TiivsNivel> getNiveles() {
		return niveles;
	}

	public void setNiveles(List<TiivsNivel> niveles) {
		this.niveles = niveles;
	}

	public List<TiivsMultitabla> getMoneda() {
		return moneda;
	}

	public void setMoneda(List<TiivsMultitabla> moneda) {
		this.moneda = moneda;
	}

	public List<TiivsNivel> getNivelesMant() {
		return nivelesMant;
	}

	public void setNivelesMant(List<TiivsNivel> nivelesMant) {
		this.nivelesMant = nivelesMant;
	}
	
	
}

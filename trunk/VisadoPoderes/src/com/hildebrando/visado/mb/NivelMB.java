package com.hildebrando.visado.mb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.service.NivelService;

@ManagedBean(name = "nivelMB")
@SessionScoped
public class NivelMB {
	public static Logger logger = Logger.getLogger(NivelMB.class);
	private SimpleDateFormat formatear = new SimpleDateFormat("dd/MM/yy");
	private TiivsNivel nivel;
	private List<TiivsMultitabla> moneda;
	private List<TiivsNivel> niveles;
	private List<TiivsNivel> nivelesMant;
	private NivelService nivelService;
	private IILDPeUsuario usuario;
	private boolean bEditar;
	private String descripcion;

	public NivelMB() {
		nivelService = new NivelService();
		nivelesMant = new ArrayList<TiivsNivel>();
		nivel = new TiivsNivel();
		obtenerMaximo();
		listarMonedas();
		listarNiveles();
		usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		bEditar = false;
		descripcion = "";
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
			for (int i = 0; i < ConstantesVisado.NUMERO_DE_MONEDAS_POR_NIVEL; i++) {
				nivelesMant.add(i, new TiivsNivel());
				nivelesMant.get(i).setCodNiv(secuencial);
				/*
				 * nivelesMant.get(i).getId().setRangoInicio(12);
				 * nivelesMant.get(i).getId().setRangoFin(20);
				 */
			}
		} catch (Exception e) {
			logger.error("NivelMB : secuencial :" ,e);
		}
	}

	public void editarNivel() {
		logger.info("NivelMB : editarNivel");
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String codNiv;
		codNiv = params.get("codNiv");

		try {
			nivelesMant = nivelService.editarNivel(codNiv);
			for (int i = 0; i < nivelesMant.size(); i++) {
				if (nivelesMant.get(i).getEstado() ==1) {
					nivelesMant.get(i).setDesEstado("1");
				} else {
					nivelesMant.get(i).setDesEstado("0");
				}
			}
			descripcion = nivelesMant.get(0).getDesNiv().toUpperCase();
			bEditar = true;
		} catch (Exception e) {
			logger.error("NivelMB : editarNivel: " ,e);
		}
	}

	public void registrar() throws Exception {
		logger.info("NivelMB : registrar");
		Date sysDate = new Date();
		int seq;
		String utilDateString = formatear.format(sysDate);
		/*Date utilDateDate = df.parse(utilDateString);
		String validacionRangoInicio = "0";*/
		int compararDescripcion = 0;
		String validacionDescripcion = "0";
		String validacionCampoDesNulo = "0";
		String valor = "0";
		try {
			for (int j = 0; j < nivelesMant.size(); j++) {
				if (nivelesMant.get(j).getRangoInicio() >= 0) {
					if (nivelesMant.get(j).getRangoFin() >= 0) {
						valor = "0";
					} else {
						valor = "1";
						break;
					}
				} else {
					valor = "1";
					break;
				}
			}

			if (nivelesMant.get(0).getDesNiv().isEmpty() == false) {
				validacionCampoDesNulo = "0";

				if (bEditar == true
						&& nivelesMant.get(0).getDesNiv().toUpperCase()
								.equals(descripcion)) {
					validacionDescripcion = "0";
				} else {
					compararDescripcion = compararDesNivel(nivelesMant.get(0)
							.getDesNiv().toUpperCase());
					if (compararDescripcion == 0) {
						validacionDescripcion = "0";
					} else {
						validacionDescripcion = "1";
					}
				}

			} else {
				validacionCampoDesNulo = "1";
			}
			if(bEditar == false){
				for (int i = 0; i < nivelesMant.size(); i++) {
					nivelesMant.get(i)
							.setMoneda(moneda.get(i).getId().getCodElem());
					logger.info("Niveles Mantenimiento  : " + " " + i + " "
							+ nivelesMant.get(i).getRangoInicio());
					/*
					 * int rec =
					 * obtenerRangoInicio(moneda.get(i).getId().getCodElem());
					 * logger.info("Niveles Mantenimiento  : " +
					 * "obtenerRangoInicio " + rec); if
					 * (nivelesMant.get(i).getRangoInicio() == rec) {
					 * validacionRangoInicio = "0"; } else { validacionRangoInicio =
					 * "1"; break; }
					 */
				}
			}
			
			if (validacionCampoDesNulo.equals("0")) {
				if (valor.equals("0")) {
					if (validacionDescripcion.equals("0")) {
						if (this.validarRegistroNivel()) { 
						for (int i = 0; i < nivelesMant.size(); i++) {
							/*if (nivelesMant.get(i).getRangoInicio() < nivelesMant
									.get(i).getRangoFin()) {*/
								if(bEditar == false){
									seq = nivelService.obtenerSecuencialNivel();
									nivelesMant.get(i).setId(seq);
									nivelesMant.get(i).setFechaReg(new Date());
									if(usuario.getUID()!=null){
									nivelesMant.get(i).setUsuarioReg(usuario.getUID());
									nivelesMant.get(i).setUsuarioAct(usuario.getUID());
									}
								}else{
									nivelesMant.get(i).setFechaAct(new Date());
									if(usuario.getUID()!=null){
										nivelesMant.get(i).setUsuarioReg(usuario.getUID());
										nivelesMant.get(i).setUsuarioAct(usuario.getUID());
									}
								}
								nivelesMant.get(i).setDesNiv(nivelesMant.get(0).getDesNiv().toUpperCase());								
								nivelesMant.get(i).setDesEstado(nivelesMant.get(0).getDesEstado());

								if (nivelesMant.get(i).getDesEstado().equals("1")) {
									nivelesMant.get(i).setEstado(1);
								} else {
									nivelesMant.get(i).setEstado(0);
								}
								logger.info("Niveles Mantenimiento  : " + " " + i + " " + nivelesMant.get(i).getId());
								logger.info("Niveles Mantenimiento  : " + " " + i + " " + nivelesMant.get(i).getRangoInicio());
								logger.info("Niveles Mantenimiento  : " + " " + i + " " + nivelesMant.get(i).getRangoFin());
								logger.info("Niveles Mantenimiento  : " + " " + i + " " + nivelesMant.get(i).getMoneda());
								logger.info("Niveles Mantenimiento  : " + " " + i + " " + nivelesMant.get(i).getEstado());
								logger.info("Niveles Mantenimiento  : " + " " + i + " "	+ nivelesMant.get(i).getFechaReg());
								logger.info("Niveles Mantenimiento  : " + " " + i + " " + nivelesMant.get(i).getUsuarioReg());

								nivelService.registrar(nivelesMant.get(i));
								
							
							/*} else {
								//Utilitarios.mensajeError("Error","El rango inicio no puede ser mayor que el rango fin de cada nivel");
								break;
							}*/

						}
						
						if(bEditar == false){
							Utilitarios.mensajeInfo("NIVEL","Se registró correctamente");
					     }else{
							Utilitarios.mensajeInfo("NIVEL","Se actualizó correctamente");
					    }
						this.limpiarNiveles();
						this.listarNiveles();
						
						}
						
						
						
						
						descripcion = "";
						bEditar = false;
					} /*
					 * else { Utilitarios .mensajeError( "Error",
					 * "Los Valores del Rango Inicio debe ser el correlativo del rango fin del ultimo nivel"
					 * );
					 * 
					 * } }
					 */else {
						Utilitarios
								.mensajeError("Error",
										"La descripción del nivel ya se encuentra registrada");
					}

				} else {
					Utilitarios
							.mensajeError("Error", "El campo es Obligatorio");
				}
			} else {
				Utilitarios.mensajeError("Error",
						"El campo Descripción es Obligatorio");
			}
			
			/*nivelesMant = new ArrayList<TiivsNivel>();
			obtenerMaximo();
			bEditar = false;
*/  
		} catch (Exception e) {
			logger.error("ComisionMB : actualizar :",e);
			Utilitarios.mensajeError("Error", "No se pudo actualizar");
		}
	}
	
	public boolean validarRegistroNivel(){
		boolean retorno=true;
		for (int i = 0; i < nivelesMant.size(); i++) {
			if(nivelesMant.get(i).getRangoFin()!=0){
			if (nivelesMant.get(i).getRangoInicio() < nivelesMant.get(i).getRangoFin()) {
				retorno=true;
			}
			else{
				retorno=false;
				Utilitarios.mensajeError("Error","El rango inicio no puede ser mayor que el rango fin de cada nivel");
				break;
			}
		
			}else{
			retorno=false;
			Utilitarios.mensajeError("Error","El rango fin no puede ser 0, ingrese rangos válidos");
			break;
		     }
		}
		return retorno;
	}
		
	
	public void limpiarNiveles(){
		logger.info("NivelMB: limpiarNiveles");	
		nivelesMant = new ArrayList<TiivsNivel>();
		obtenerMaximo();
		bEditar = false;
		
	}
	
	private Integer compararDesNivel(String desNivel) {
		logger.info("NivelMB: compararDesNivel");
		int compararDes = 0;
		try {
			compararDes = nivelService.compararDesNivel(desNivel);
		} catch (Exception e) {
			logger.error("NivelMB: compararDesNivel : ",e);
		}
		return compararDes;
	}

	private Integer obtenerRangoInicio(String codElem) {
		logger.info("NivelMB: obtenerRangoInicio");
		int rangoInicio = 0;
		try {
			rangoInicio = nivelService.obtenerRangoInicio(codElem);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("NivelMB: listarNiveles : " + e.getLocalizedMessage());
		}
		return rangoInicio;
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

	public boolean isbEditar() {
		return bEditar;
	}

	public void setbEditar(boolean bEditar) {
		this.bEditar = bEditar;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}

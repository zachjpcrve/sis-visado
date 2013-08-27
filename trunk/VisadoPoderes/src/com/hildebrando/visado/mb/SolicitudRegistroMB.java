package com.hildebrando.legal.mb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.legal.modelo.Abogado;
import com.hildebrando.legal.modelo.AbogadoEstudio;
import com.hildebrando.legal.modelo.AbogadoEstudioId;
import com.hildebrando.legal.modelo.Actividad;
import com.hildebrando.legal.modelo.ActividadProcesal;
import com.hildebrando.legal.modelo.ActividadProcesalMan;
import com.hildebrando.legal.modelo.Anexo;
import com.hildebrando.legal.modelo.Calificacion;
import com.hildebrando.legal.modelo.Clase;
import com.hildebrando.legal.modelo.ContraCautela;
import com.hildebrando.legal.modelo.Cuantia;
import com.hildebrando.legal.modelo.Cuota;
import com.hildebrando.legal.modelo.Entidad;
import com.hildebrando.legal.modelo.EstadoCautelar;
import com.hildebrando.legal.modelo.EstadoExpediente;
import com.hildebrando.legal.modelo.Estudio;
import com.hildebrando.legal.modelo.Etapa;
import com.hildebrando.legal.modelo.Expediente;
import com.hildebrando.legal.modelo.Feriado;
import com.hildebrando.legal.modelo.Honorario;
import com.hildebrando.legal.modelo.Inculpado;
import com.hildebrando.legal.modelo.Instancia;
import com.hildebrando.legal.modelo.Involucrado;
import com.hildebrando.legal.modelo.Materia;
import com.hildebrando.legal.modelo.Moneda;
import com.hildebrando.legal.modelo.Oficina;
import com.hildebrando.legal.modelo.Organo;
import com.hildebrando.legal.modelo.Persona;
import com.hildebrando.legal.modelo.Proceso;
import com.hildebrando.legal.modelo.Recurrencia;
import com.hildebrando.legal.modelo.Resumen;
import com.hildebrando.legal.modelo.Riesgo;
import com.hildebrando.legal.modelo.RolInvolucrado;
import com.hildebrando.legal.modelo.SituacionActProc;
import com.hildebrando.legal.modelo.SituacionCuota;
import com.hildebrando.legal.modelo.SituacionHonorario;
import com.hildebrando.legal.modelo.SituacionInculpado;
import com.hildebrando.legal.modelo.TipoCautelar;
import com.hildebrando.legal.modelo.TipoDocumento;
import com.hildebrando.legal.modelo.TipoExpediente;
import com.hildebrando.legal.modelo.TipoHonorario;
import com.hildebrando.legal.modelo.TipoInvolucrado;
import com.hildebrando.legal.modelo.Ubigeo;
import com.hildebrando.legal.modelo.Usuario;
import com.hildebrando.legal.modelo.Via;
import com.hildebrando.legal.service.AbogadoService;
import com.hildebrando.legal.service.ConsultaService;
import com.hildebrando.legal.service.OrganoService;
import com.hildebrando.legal.service.PersonaService;
import com.hildebrando.legal.util.SglConstantes;
import com.hildebrando.legal.util.Util;
import com.hildebrando.legal.util.Utilitarios;
import com.hildebrando.legal.view.AbogadoDataModel;
import com.hildebrando.legal.view.CuantiaDataModel;
import com.hildebrando.legal.view.InvolucradoDataModel;
import com.hildebrando.legal.view.OrganoDataModel;
import com.hildebrando.legal.view.PersonaDataModel;

/**
 * Clase encargada de realizar el registro de un expediente 
 * a través del formulario de registro. Algunas secciones contempladas son 
 * Cabecera, Cuantia, Abogado, Resumen, Actividades Procesales, Etc.
 * Implementa {@link Serializable}
 * @author hildebrando
 * @version 1.0
 */
public class RegistroExpedienteMB implements Serializable {

	private static final long serialVersionUID = -1963075122904356898L;
	/**
	 * Escribe los logs en un archivo externo seg&uacute;n la configuraci&oacute;n
	 * del <code>log4j.properties</code>.
	 */
	public static Logger logger = Logger.getLogger(RegistroExpedienteMB.class);

	private int proceso;
	private List<Proceso> procesos;
	private int via;
	private List<Via> vias;
	private int instancia;
	private List<Instancia> instancias;
	private Usuario responsable;
	private String nroExpeOficial;
	private Date inicioProceso;
	private int estado;
	private List<EstadoExpediente> estados;
	private Oficina oficina;
	private int tipo;
	private Organo organo1;
	private List<TipoExpediente> tipos;
	private List<Entidad> entidades;
	private String secretario;
	private int calificacion;
	private List<Calificacion> calificaciones;
	private Recurrencia recurrencia;
	private Abogado abogado;
	private Estudio estudio;
	private AbogadoDataModel abogadoDataModel;
	private List<TipoHonorario> tipoHonorarios;
	private List<String> tipoHonorariosString;
	private Honorario honorario;
	private int contadorHonorario = 0;
	private int contadorInvolucrado = 0;
	private int contadorInculpado = 0;
	private int contadorCuantia = 0;
	private int contadorResumen = 0;
	private List<Cuota> cuotas;
	private List<Moneda> monedas;
	private List<String> monedasString;
	private List<SituacionHonorario> situacionHonorarios;
	private List<String> situacionHonorariosString;
	private List<SituacionCuota> situacionCuotas;
	private List<String> situacionCuotasString;
	private Involucrado involucrado;
	private Persona persona;
	private List<RolInvolucrado> rolInvolucrados;
	private List<String> rolInvolucradosString;
	private List<TipoInvolucrado> tipoInvolucrados;
	private List<String> tipoInvolucradosString;
	private InvolucradoDataModel involucradoDataModel;
	private Involucrado selectedInvolucrado;
	private List<Clase> clases;
	private List<TipoDocumento> tipoDocumentos;
	private PersonaDataModel personaDataModelBusq;
	private Cuantia cuantia;
	private Cuantia selectedCuantia;
	private CuantiaDataModel cuantiaDataModel;
	private List<SituacionInculpado> situacionInculpados;
	private List<String> situacionInculpadosString;
	private Inculpado inculpado;
	private Inculpado selectedInculpado;
	private List<Inculpado> inculpados;
	private int moneda;
	private double montoCautelar;
	private int tipoCautelar;
	private List<TipoCautelar> tipoCautelares;
	private String descripcionCautelar;
	private int contraCautela;
	private double importeCautelar;
	private int estadoCautelar;
	private List<EstadoCautelar> estadosCautelares;
	private int riesgo;
	private List<Riesgo> riesgos;

	private UploadedFile file;
	private Anexo anexo;
	private List<Anexo> anexos;
	private Anexo selectedAnexo;

	private String todoResumen;
	private String resumen;
	private Date fechaResumen;
	private List<Resumen> resumens;
	private Resumen selectedResumen;

	private Organo organo;
	private OrganoDataModel organoDataModel;
	private Organo selectedOrgano;

	private List<Honorario> honorarios;
	private Honorario selectedHonorario;
	private Persona selectPersona;
	private Persona selectInvolucrado;
	private List<ContraCautela> contraCautelas;

	private List<Ubigeo> ubigeos;

	private boolean tabAsigEstExt;
	private boolean tabCaucion;
	private boolean tabCuanMat;

	private Abogado selectedAbogado;

	private boolean reqPenal;
	private boolean reqCabecera;

	private ConsultaService consultaService;

	private AbogadoService abogadoService;

	private PersonaService personaService;

	private OrganoService organoService;

	private boolean flagDeshabilitadoGeneral;
	private boolean flagColumnGeneralHonorario;
	private boolean flagColumnGeneral;
	private boolean flagColumnsBtnHonorario;

	private boolean flagLectResp;
	private File archivo;
	private String txtOrgano;
	private int idEntidad;
	private int idUbigeo;
	private String txtRegistroCA;
	private Integer DNI;
	private String txtNombre;
	private String txtApePat;
	private String txtApeMat;
	private String txtTel;
	private String txtCorreo;
	private String txtTitulo;
	private String txtComentario;
	private Date fechaInicio;
	private Ubigeo ubigeo;
	// Para mantenimiento de personas
	private int idClase;
	private Integer codCliente;
	private int idTipoDocumento;
	private Long numeroDocumento;
	private String txtNombres;
	private String txtApellidoPaterno;
	private String txtApellidoMaterno;
	// Para mantenimiento de inculpados
	private int idClase_inclp;
	private Integer codCliente_inclp;
	private int idTipoDocumento_inclp;
	private Long numeroDocumento_inclp;
	private String txtNombres_inclp;
	private String txtApellidoPaterno_inclp;
	private String txtApellidoMaterno_inclp;
	private String pretendidoMostrar;

	public void verAnexo() {
		logger.debug("=== inicio verAnexo()====");
		logger.debug("[ANEXO]-Ubicacion Temporal:"
				+ getSelectedAnexo().getUbicacionTemporal());
		/*
		 * return
		 * getSelectedAnexo().getUbicacionTemporal()+"?faces-redirect=true";
		 * File file = new File(getSelectedAnexo().getUbicacionTemporal()); try
		 * { Desktop.getDesktop().open(file); } catch (IOException e) {
		 * logger.debug("erro al abrir "+ e.toString()); }
		 */
	}

	/**
	 * Metodo que se encarga de "Agregar" un Comentario en la grilla
	 * de Resumen del formulario de registro de expediente
	 * @param e ActionEvent
	 * */
	public void agregarTodoResumen(ActionEvent e) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if (getFechaResumen() == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Fecha de Resumen Requerido", "Fecha de Resumen Requerido");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			if (getResumen() == "") {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Resumen Requerido","Resumen Requerido");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else {
				Resumen resumen = new Resumen();
				resumen.setUsuario(getResponsable());
				resumen.setTexto(getResumen());
				resumen.setFecha(getFechaResumen());

				contadorResumen++;
				resumen.setNumero(contadorResumen);
				getResumens().add(resumen);

				// Se limpian los campos del formulario
				setResumen("");
				setFechaResumen(null);
			}
		}
	}

	public void deleteHonorario() {
		getHonorarios().remove(selectedHonorario);
	}

	public void deleteAnexo() {
		getAnexos().remove(selectedAnexo);
	}

	public void deleteInvolucrado() {
		List<Involucrado> involucrados = (List<Involucrado>) getInvolucradoDataModel().getWrappedData();
		involucrados.remove(getSelectedInvolucrado());
		involucradoDataModel = new InvolucradoDataModel(involucrados);
	}

	public void deleteCuantia() {
		List<Cuantia> cuantias = (List<Cuantia>) getCuantiaDataModel().getWrappedData();
		cuantias.remove(getSelectedCuantia());
		cuantiaDataModel = new CuantiaDataModel(cuantias);
	}

	public void deleteInculpado() {
		inculpados.remove(getSelectedInculpado());
	}

	public void deleteResumen() {
		resumens.remove(getSelectedResumen());
	}

	/**
	 * Metodo que se encarga de consultar y recuperar una lista de abogados. 
	 * También se realizan validaciones para realizar la búsqueda.
	 * @param e ActionEvent
	 * */
	@SuppressWarnings("unchecked")
	public void buscarAbogado(ActionEvent e) {
		logger.debug("== inicia buscarAbogado() ===");
		try {
			Abogado abg = new Abogado();
			List<Abogado> results = new ArrayList<Abogado>();
			if (getTxtRegistroCA() != null) {
				abg.setRegistroca(getTxtRegistroCA());
			}
			if (getDNI() != null) {
				abg.setDni(getDNI());
			}
			if (getTxtNombre() != null) {
				abg.setNombres(getTxtNombre());
			}
			if (getTxtApePat() != null) {
				abg.setApellidoPaterno(getTxtApePat());
			}
			if (getTxtApeMat() != null) {
				abg.setApellidoMaterno(getTxtApeMat());
			}
			if (getTxtTel() != null) {
				abg.setTelefono(getTxtTel());
			}
			if (getTxtCorreo() != null) {
				abg.setCorreo(getTxtCorreo());
			}

			results = consultaService.getAbogadosByAbogadoEstudio(abg,getEstudio());
			if (results != null) {
				logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA+ "abogados POPUP es:[" + results.size() + "]");
			}
			abogadoDataModel = new AbogadoDataModel(results);
		} catch (Exception e2) {
			logger.error(SglConstantes.MSJ_ERROR_CONSULTAR + "abogados POPUP:"+ e2);
		}
		logger.debug("== saliendo de buscarAbogado() ===");
	}

	public void agregarHonorario(ActionEvent e2) {
		logger.debug("=== inicia agregarHonorario() ===");

		if (honorario.getAbogado() == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Abogado Requerido", "Abogado Requerido");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			if (honorario.getTipoHonorario().getDescripcion() == "") {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Honorario Requerido",
						"Honorario Requerido");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else {
				if (honorario.getCantidad() == 0) {
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Cuotas Requerido",
							"Cuotas Requerido");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				} else {
					if (honorario.getMoneda().getSimbolo() == "") {
						FacesMessage msg = new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Moneda Requerido", "Moneda Requerido");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					} else {
						if (honorario.getMonto() == 0.0) {
							FacesMessage msg = new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Monto Requerido", "Monto Requerido");
							FacesContext.getCurrentInstance().addMessage(null,
									msg);
						} else {
							if (honorario.getSituacionHonorario()
									.getDescripcion() == "") {
								FacesMessage msg = new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Situación Requerido",
										"Situación Requerido");
								FacesContext.getCurrentInstance().addMessage(
										null, msg);
							} else {
								// TipoHonorario
								for (TipoHonorario tipo : getTipoHonorarios()) {
									if (tipo.getDescripcion().compareTo(
											honorario.getTipoHonorario()
													.getDescripcion()) == 0) {
										honorario.setTipoHonorario(tipo);
										break;
									}
								}
								// Moneda
								for (Moneda moneda : getMonedas()) {
									if (moneda.getSimbolo().compareTo(
											honorario.getMoneda().getSimbolo()) == 0) {
										honorario.setMoneda(moneda);
										break;
									}
								}
								// Situacion honorario
								for (SituacionHonorario situacionHonorario : getSituacionHonorarios()) {
									if (situacionHonorario.getDescripcion().compareTo(honorario.getSituacionHonorario().getDescripcion()) == 0) {
										honorario.setSituacionHonorario(situacionHonorario);
										break;
									}
								}
								
								if (getHonorario().getAbogado()!=null)
								{
									logger.debug("Abogado seleccionado: " + getHonorario().getAbogado().getNombreCompleto());
								}
								
								// Abogado Estudio
								List<AbogadoEstudio> abogadoEstudios = consultaService.getAbogadoEstudioByAbogado(getHonorario().getAbogado());
								if (abogadoEstudios != null) {
									if (abogadoEstudios.size() != 0) {
										honorario.setEstudio(abogadoEstudios
												.get(0).getEstudio()
												.getNombre());
									}
								}

								// Situacion pendiente
								if (honorario.getSituacionHonorario().getIdSituacionHonorario() == 1) {

									double importe = getHonorario().getMonto() / getHonorario().getCantidad().intValue();
									importe = Math.rint(importe * 100) / 100;
									
									//Busqueda de situacion cuota (reconfirmacion)
									List<SituacionCuota> situacionCuotaTMP = new ArrayList<SituacionCuota>();
									GenericDao<SituacionCuota, Object> situacionCuotaDAO = (GenericDao<SituacionCuota, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
									Busqueda filtroSC = Busqueda.forClass(SituacionCuota.class);
									filtroSC.add(Restrictions.eq("descripcion", SglConstantes.SITUACION_CUOTA_PENDIENTE));
									
									try {
										situacionCuotaTMP = situacionCuotaDAO.buscarDinamico(filtroSC);
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									setFlagColumnsBtnHonorario(true);
									//setFlagColumnGeneral(false);
									setFlagColumnGeneralHonorario(false);
																	
									honorario.setMontoPagado(0.0);
									honorario.setCuotas(new ArrayList<Cuota>());

									Calendar cal = Calendar.getInstance();
									for (int i = 1; i <= getHonorario()
											.getCantidad().intValue(); i++) {
										Cuota cuota = new Cuota();
										cuota.setNumero(i);
										cuota.setMoneda(honorario.getMoneda()
												.getSimbolo());
										cuota.setNroRecibo("000" + i);
										cuota.setImporte(importe);
										cal.add(Calendar.MONTH, 1);
										Date date = cal.getTime();
										cuota.setFechaPago(date);
										
										if (situacionCuotaTMP!=null)
										{
											if (situacionCuotaTMP.size()>0)
											{
												cuota.setSituacionCuota(new SituacionCuota());
												cuota.getSituacionCuota()
														.setIdSituacionCuota(
																situacionCuotaTMP.get(0)
																		.getIdSituacionCuota());
												cuota.getSituacionCuota()
														.setDescripcion(
																situacionCuotaTMP.get(0)
																		.getDescripcion());
											}
										}
										
										
										cuota.setFlagPendiente(true);

										honorario.addCuota(cuota);
									}
									honorario.setFlagPendiente(true);
								} else {
									logger.debug("La situación del honorario no es PENDIENTE ");
									honorario.setMontoPagado(honorario.getMonto());
									honorario.setFlagPendiente(false);
								}

								contadorHonorario++;
								honorario.setNumero(contadorHonorario);

								honorarios.add(honorario);

								honorario = new Honorario();
								honorario.setCantidad(0);
								honorario.setMonto(0.0);

							}
						}
					}
				}
			}
		}
		logger.debug("=== saliendo de agregarHonorario() ===");
	}
	
	/**
	 * Metodo que se encarga de adjuntar/cargar un archivo en la sección Anexo
	 * del formulario de registro de expediente
	 * @param e Representa el evento del tipo {@link ActionEvent}
	 * */
	public void agregarAnexo(ActionEvent en) {
		if (file == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Cargar Archivo", "Cargar Archivo");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			if (getTxtTitulo() == "") {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Título Requerido","Título Requerido");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else {
				if (getTxtComentario() == "") {
					FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,"Comentario Requerido", "Comentario Requerido");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				} else {
					if (getFechaInicio() == null) {
						FacesMessage msg = new FacesMessage(
								FacesMessage.SEVERITY_ERROR,"Fecha Inicio Requerido","Fecha Inicio Requerido");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					} else {
						/*
						 * String ubicacionTemporal=
						 * Util.getMessage("ruta_documento") + File.separator +
						 * "documento" ;
						 * 
						 * File fichSalida= new File(ubicacionTemporal);
						 * fichSalida.mkdirs();
						 * 
						 * byte fileBytes[]= getFile().getContents(); String
						 * ubicacionTemporal2=ubicacionTemporal + File.separator
						 * + getFile().getFileName(); File fichSalida2 = new
						 * File(ubicacionTemporal2);
						 * 
						 * 
						 * try { FileOutputStream canalSalida = new
						 * FileOutputStream(fichSalida2);
						 * canalSalida.write(fileBytes); canalSalida.close(); }
						 * catch (IOException e) { e.printStackTrace(); }
						 */

						byte fileBytes[] = getFile().getContents();

						File fichTemp = null;
						String ubicacionTemporal2 = "";
						String sfileName = "";
						FileOutputStream canalSalida = null;

						try {
							HttpServletRequest request = (HttpServletRequest) FacesContext
									.getCurrentInstance().getExternalContext().getRequest();
							ubicacionTemporal2 = request.getRealPath(File.separator) 
									+ File.separator + "files" + File.separator;
							logger.debug("ubicacion temporal " + ubicacionTemporal2);

							File fDirectory = new File(ubicacionTemporal2);
							fDirectory.mkdirs();

							fichTemp = File.createTempFile(
								"temp",	getFile().getFileName().substring(
								getFile().getFileName().lastIndexOf(".")),
							
							new File(ubicacionTemporal2));

							canalSalida = new FileOutputStream(fichTemp);
							canalSalida.write(fileBytes);
							canalSalida.flush();
							sfileName = fichTemp.getName();
							logger.debug("sfileName " + sfileName);

						} catch (IOException e) {
							logger.debug("error anexo " + e.toString());
						} finally {
							// Delete the file when the JVM terminates
							fichTemp.deleteOnExit(); 
							if (canalSalida != null) {
								try {
									canalSalida.close();
								} catch (IOException x) {
									// handle error
								}
							}
						}

						// Blob b = Hibernate.createBlob(fileBytes);
						getAnexo().setBytes(fileBytes);
						getAnexo().setUbicacionTemporal(sfileName);

						getAnexo().setUbicacion(
								getFile().getFileName().substring(
										1 + getFile().getFileName()
												.lastIndexOf(File.separator)));
						getAnexo().setFormato(
								getFile()
										.getFileName()
										.substring(
												getFile().getFileName()
														.lastIndexOf("."))
										.toUpperCase());
						getAnexos().add(getAnexo());

						setAnexo(new Anexo());
						getAnexo().setFechaInicio(new Date());
						setFile(null);

					}

				}
			}
		}

	}

	public void handleFileUpload(FileUploadEvent event) {
		FacesMessage msg = new FacesMessage("Archivo ", event.getFile()
				.getFileName() + " almacenado correctamente.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		// FacesUtils.getRequestContext().execute("clearInvalidFileMsg()");
		// .getRequestContext().execute("clearInvalidFileMsg()");
		setFile(event.getFile());
	}

	public void handleFileUpload2() {
		FacesMessage msg = new FacesMessage("Archivo ", this.archivo.getName()
				+ " almacenado correctamente.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		setFile((UploadedFile) archivo);
	}

	public void agregarAbogado(ActionEvent e2) {
		logger.info("=== agregarAbogado() ====");
		List<Abogado> abogadosBD = new ArrayList<Abogado>();
		if (getDNI() == null || getTxtNombre() == "" || getTxtApeMat() == ""
				|| getTxtApePat() == "" || getEstudio()==null) 
		{
			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"Datos Requeridos: Nro Documento, Nombres, Apellido Paterno, Apellido Materno, Estudio",
					"Datos Requeridos");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			setDNI(null);
		} else {
			Abogado abg = new Abogado();
			AbogadoEstudio abgEs = new AbogadoEstudio();
			if (getTxtRegistroCA() != null) {
				abg.setRegistroca(getTxtRegistroCA());
			}
			abg.setDni(getDNI());
			abg.setNombres(getTxtNombre());
			abg.setApellidoPaterno(getTxtApePat());
			abg.setApellidoMaterno(getTxtApeMat());
			
			String nombreCompleto= abg.getNombres() + " " + abg.getApellidoPaterno() + " " + abg.getApellidoMaterno();
			abg.setNombreCompleto(nombreCompleto);

			if (getTxtTel() != null) {
				abg.setTelefono(getTxtTel());
			}
			if (getTxtCorreo() != null) {
				abg.setCorreo(getTxtCorreo());
			}

			abogadosBD = consultaService.getAbogadosByAbogado(abg);

			Abogado abogadobd = new Abogado();
			AbogadoEstudio abogadoEsBD = new AbogadoEstudio();

			if (abogadosBD.size() == 0) {
				try {
					getAbogado().setNombreCompleto(
							getAbogado().getNombres() + " "
									+ getAbogado().getApellidoPaterno() + " "
									+ getAbogado().getApellidoMaterno());
					
					logger.debug("[ADD_ABOG]-Nombre:" + getAbogado().getNombreCompleto());
					abogadobd = abogadoService.registrar(abg);
					
					logger.debug(SglConstantes.MSJ_EXITO_REGISTRO+"el Abogado-Id:[" + abogadobd.getIdAbogado() + "].");
					
					//Seteo del abogado estudio
					abgEs.setAbogado(abogadobd);
					abgEs.setEstado('A');
					abgEs.setEstudio(getEstudio());
					
					AbogadoEstudioId id = new AbogadoEstudioId();
					id.setIdAbogado(abogadobd.getIdAbogado());
					id.setIdEstudio(getEstudio().getIdEstudio());
					
					abgEs.setId(id);					
					
					logger.debug("Se registra el abogado con ID: " + abogadobd.getIdAbogado() + " en la tabla Abogado-Estudio");
					abogadoEsBD = abogadoService.registrarAbogadoEstudio(abgEs);
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Abogado agregado",	"Abogado agregado");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(SglConstantes.MSJ_ERROR_REGISTR + "el Abogado:" + e);
				}

			} else {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Abogado Existente", "Abogado Existente");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

			List<Abogado> abogados = new ArrayList<Abogado>();
			abogados.add(abogadobd);
			abogadoDataModel = new AbogadoDataModel(abogados);

			// Limpiar datos
			setTxtRegistroCA("");
			setTxtNombre("");
			setDNI(null);
			setTxtApePat("");
			setTxtApeMat("");
			setTxtCorreo("");
			setTxtTel("");
			setEstudio(new Estudio());
		}
	}

	public void buscarPersona(ActionEvent e) {
		logger.debug("=== buscarPersona() ===");
		try {
			if (getIdClase() != -1 || getCodCliente() != null
					|| getIdTipoDocumento() != -1 || getNumeroDocumento() != 0
					|| getTxtNombres() != "" || getTxtApellidoMaterno() != ""
					|| getTxtApellidoPaterno() != "") {
				Persona per = new Persona();
				Clase cls = new Clase();
				cls.setIdClase(getIdClase());
				TipoDocumento tdoc = new TipoDocumento();
				tdoc.setIdTipoDocumento(getIdTipoDocumento());

				per.setCodCliente(getCodCliente());
				per.setNumeroDocumento(getNumeroDocumento());
				per.setNombres(getTxtNombres());
				per.setApellidoMaterno(getTxtApellidoMaterno());
				per.setApellidoPaterno(getTxtApellidoPaterno());
				per.setClase(cls);
				per.setTipoDocumento(tdoc);

				List<Persona> personas = consultaService.getPersonasByPersona(per);

				if(personas!=null){
					logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA+"de Personas es: ["+personas.size()+"]");
				}
				personaDataModelBusq = new PersonaDataModel(personas);

				// Limpiar datos de persona
				setIdClase(-1);
				setCodCliente(null);
				setIdTipoDocumento(-1);
				setNumeroDocumento(null);
				setTxtNombres("");
				setTxtApellidoMaterno("");
				setTxtApellidoPaterno("");
			} else {				
				Persona per = new Persona();
				Clase cls = new Clase();
				TipoDocumento tdoc = new TipoDocumento();
				per.setClase(cls);
				per.setTipoDocumento(tdoc);

				List<Persona> personas = consultaService.getPersonasByPersona(per);
				
				if(personas!=null){
					logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA+"de Personas es: ["+personas.size()+"]");
				}
				personaDataModelBusq = new PersonaDataModel(personas);
			}
		} catch (Exception e1) {
			logger.error(SglConstantes.MSJ_ERROR_EXCEPTION+"al consultar Personas:"+e1);
		}
	}

	public void buscarInculpado(ActionEvent e) {
		logger.debug("=== buscarInculpado()===");
		if (getIdClase_inclp() != -1 || getCodCliente_inclp() != null
				|| getIdTipoDocumento_inclp() != -1
				|| getNumeroDocumento_inclp() != 0
				|| getTxtNombres_inclp() != ""
				|| getTxtApellidoMaterno_inclp() != ""
				|| getTxtApellidoPaterno_inclp() != "") {
			Persona per = new Persona();
			Clase cls = new Clase();
			TipoDocumento tdoc = new TipoDocumento();
			tdoc.setIdTipoDocumento(getIdTipoDocumento_inclp());
			cls.setIdClase(getIdClase_inclp());

			per.setCodCliente(getCodCliente_inclp());
			per.setNumeroDocumento(getNumeroDocumento_inclp());
			per.setNombres(getTxtNombres_inclp());
			per.setApellidoMaterno(getTxtApellidoMaterno_inclp());
			per.setApellidoPaterno(getTxtApellidoPaterno_inclp());
			per.setTipoDocumento(tdoc);
			per.setClase(cls);

			List<Persona> personas = consultaService.getPersonasByPersona(per);

			if(personas!=null){
				logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA+"de Personas es: ["+personas.size()+"]");
			}

			personaDataModelBusq = new PersonaDataModel(personas);

			// Limpiar datos de inculpado
			setIdClase_inclp(-1);
			setCodCliente_inclp(null);
			setIdTipoDocumento_inclp(-1);
			setNumeroDocumento_inclp(null);
			setTxtNombres_inclp("");
			setTxtApellidoMaterno_inclp("");
			setTxtApellidoPaterno_inclp("");
		} else {
			Persona per = new Persona();
			Clase cls = new Clase();
			TipoDocumento tdoc = new TipoDocumento();
			per.setClase(cls);
			per.setTipoDocumento(tdoc);

			List<Persona> personas = consultaService.getPersonasByPersona(per);

			if(personas!=null){
				logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA+"de Personas es: ["+personas.size()+"]");
			}
			personaDataModelBusq = new PersonaDataModel(personas);
		}
	}

	/**
	 * Metodo que se encarga de buscar organos en el popup
	 * "Mantenimiento Organo"
	 * @param e ActionEvent
	 * **/
	public void buscarOrganos(ActionEvent e) {
		logger.debug("=== buscarOrganos() ===");
		try {
			if (getTxtOrgano() != null || getIdEntidad() != 0
					|| getUbigeo() != null) {
				logger.debug("[BUSQ_ORG]-txtOrgano():" + getTxtOrgano());
				Organo tmp = new Organo();
				Entidad ent = new Entidad();

				if (getTxtOrgano() != null) {
					tmp.setNombre(getTxtOrgano());
				}
				if (getIdEntidad() != 0) {
					ent.setIdEntidad(getIdEntidad());
					tmp.setEntidad(ent);
				} else {
					tmp.setEntidad(ent);
				}

				if (getUbigeo() != null) {
					tmp.setUbigeo(getUbigeo());
				}

				List<Organo> organos = consultaService.getOrganosByOrgano(tmp);
				if (organos != null) {
					logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA
							+ "organos POPUP es:[" + organos.size() + "]");
				} else {
					logger.debug("La consulta de organos devuelve NULL");
				}

				organoDataModel = new OrganoDataModel(organos);

			} else {
				logger.debug("Buscando sin filtros en el Mantenimiento de Organos");

				Organo tmp = new Organo();
				Entidad ent = new Entidad();
				tmp.setEntidad(ent);

				List<Organo> organos = consultaService.getOrganosByOrgano(tmp);
				if (organos != null) {
					logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA
							+ "organos POPUP es:[" + organos.size() + "]");
				} else {
					logger.debug("La consulta de organos devuelve NULL");
				}

				organoDataModel = new OrganoDataModel(organos);
			}
			// Limpiar datos
			setIdEntidad(0);
			setTxtOrgano("");
			Organo org = new Organo();
			Ubigeo ub = new Ubigeo();
			org.setUbigeo(ub);

		} catch (Exception e1) {
			logger.error(SglConstantes.MSJ_ERROR_CONSULTAR + "organos popup:"+ e1);
		}
		logger.debug("=== saliendo de buscarOrganos() ===");
	}

	public void agregarOrgano(ActionEvent e2) {
		List<Organo> organos = new ArrayList<Organo>();

		if (getTxtOrgano() == null || getIdEntidad() == 0
				|| getOrgano() == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Datos Requeridos: ", "Entidad, Órgano, Distrito");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			logger.debug("==Datos a grabar==");
			logger.debug("[ADD_ORG]-Nombre: " + getTxtOrgano());
			logger.debug("[ADD_ORG]-CodEntidad: " + getIdEntidad());

			Organo tmp = new Organo();
			Entidad ent = new Entidad();

			if (getTxtOrgano() != null) {
				tmp.setNombre(getTxtOrgano());
			}
			if (getIdEntidad() != 0) {
				ent.setIdEntidad(getIdEntidad());
				tmp.setEntidad(ent);
			} else {
				tmp.setEntidad(ent);
			}
			if (getUbigeo() != null) {
				tmp.setUbigeo(getUbigeo());
			}

			organos = consultaService.getOrganosByOrganoEstricto(tmp);

			Organo organobd = new Organo();

			if (organos.size() == 0) {
				try {
					organobd = organoService.registrar(tmp);
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Exito: ", "Órgano Agregado"));
					// TODO Limpiar los datos ingresados
				} catch (Exception e) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"No Exitoso: ", "Órgano No Agregado"));
					logger.error(SglConstantes.MSJ_ERROR_REGISTR + "el Organo:"	+ e);
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"No Exitoso: ", "Órgano Existente"));
			}

			List<Organo> organos2 = new ArrayList<Organo>();
			organos2.add(organobd);
			organoDataModel = new OrganoDataModel(organos2);

			// Limpiar datos
			logger.debug("= Limpiando datos despues de Agregar =");
			setIdEntidad(0);
			setTxtOrgano("");
			setUbigeo(new Ubigeo());
		}
	}

	public void agregarCuantia(ActionEvent e) {

		if (cuantia.getMoneda().getSimbolo() == "") {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Moneda Requerido", "Moneda Requerido");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		} else {

			if (cuantia.getPretendido() == 0.0) {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Pretendido Requerido",
						"Pretendido Requerido");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else {
				for (Moneda m : getMonedas()) {
					if (m.getSimbolo().equals(
							getCuantia().getMoneda().getSimbolo())) {
						cuantia.setMoneda(m);
						break;
					}
				}

				List<Cuantia> cuantias;
				if (cuantiaDataModel == null) {
					cuantias = new ArrayList<Cuantia>();
				} else {
					cuantias = (List<Cuantia>) cuantiaDataModel.getWrappedData();
				}
				contadorCuantia++;
				getCuantia().setNumero(contadorCuantia);
				cuantias.add(getCuantia());

				cuantiaDataModel = new CuantiaDataModel(cuantias);

				cuantia = new Cuantia();

			}
		}
	}

	public void agregarInvolucrado(ActionEvent e) {
		if (involucrado.getPersona() == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Nombre Requerido", "Nombre Requerido");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			if (involucrado.getRolInvolucrado().getNombre() == "") {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Rol Requerido",
						"Abogado Requerido");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else {
				for (RolInvolucrado rol : getRolInvolucrados()) {
					if (rol.getNombre() == getInvolucrado().getRolInvolucrado()
							.getNombre()) {
						involucrado.setRolInvolucrado(rol);
						break;
					}
				}

				for (TipoInvolucrado tipo : getTipoInvolucrados()) {
					if (tipo.getNombre() == getInvolucrado()
							.getTipoInvolucrado().getNombre()) {
						involucrado.setTipoInvolucrado(tipo);
						break;
					}
				}

				List<Involucrado> involucrados;
				if (involucradoDataModel == null) {
					involucrados = new ArrayList<Involucrado>();
				} else {
					involucrados = (List<Involucrado>) involucradoDataModel
							.getWrappedData();
				}

				contadorInvolucrado++;
				getInvolucrado().setNumero(contadorInvolucrado);

				involucrados.add(getInvolucrado());
				involucradoDataModel = new InvolucradoDataModel(involucrados);

				involucrado = new Involucrado();
			}
		}
	}

	public void agregarInculpado(ActionEvent e) {
		if (inculpado.getPersona() == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Inculpado Requerido", "Inculpado Requerido");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			if (inculpado.getFecha() == null) {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Fecha Requerido",
						"Fecha Requerido");
				FacesContext.getCurrentInstance().addMessage(null, msg);

			} else {
				if (inculpado.getMoneda().getSimbolo() == "") {
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Moneda Requerido",
							"Materia Requerido");
					FacesContext.getCurrentInstance().addMessage(null, msg);

				} else {
					if (inculpado.getMonto() == 0.0) {
						FacesMessage msg = new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Monto Requerido",
								"Monto Requerido");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					} else {
						if (inculpado.getNrocupon() == 0) {

							FacesMessage msg = new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Numero Cupón Requerido",
									"Numero Cupón Requerido");
							FacesContext.getCurrentInstance().addMessage(null,
									msg);

						} else {

							if (inculpado.getSituacionInculpado().getNombre() == "") {

								FacesMessage msg = new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Situación Requerido",
										"Situación Requerido");
								FacesContext.getCurrentInstance().addMessage(
										null, msg);

							} else {

								for (Moneda moneda : getMonedas()) {
									if (moneda.getSimbolo().equals(
											getInculpado().getMoneda()
													.getSimbolo()))
										inculpado.setMoneda(moneda);
								}

								for (SituacionInculpado situac : getSituacionInculpados()) {
									if (situac.getNombre().equals(
											getInculpado()
													.getSituacionInculpado()
													.getNombre()))
										inculpado.setSituacionInculpado(situac);
								}

								if (inculpados == null) {

									inculpados = new ArrayList<Inculpado>();
								}

								contadorInculpado++;
								getInculpado().setNumero(contadorInculpado);
								inculpados.add(getInculpado());

								inculpado = new Inculpado();

							}

						}

					}
				}

			}

		}

	}

	public void agregarPersona(ActionEvent e) {

		logger.info("Ingreso a agregarDetallePersona..");

		if (getIdClase() == -1 || getIdTipoDocumento() == -1
				|| getNumeroDocumento() == 0 || getTxtNombres() == ""
				|| getTxtApellidoMaterno() == ""
				|| getTxtApellidoPaterno() == "") {

			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"Datos Requeridos: Clase, Tipo Doc, Nro Documento, Nombre, Apellido Paterno, Apellido Materno",
					"");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
			setIdClase(-1);
			setIdTipoDocumento(-1);

		} else {
			Persona per = new Persona();
			Clase cls = new Clase();
			cls.setIdClase(getIdClase());
			per.setCodCliente(getCodCliente());
			TipoDocumento tdoc = new TipoDocumento();
			tdoc.setIdTipoDocumento(getIdTipoDocumento());
			per.setNumeroDocumento(getNumeroDocumento());
			per.setNombres(getTxtNombres());
			per.setApellidoMaterno(getTxtApellidoMaterno());
			per.setApellidoPaterno(getTxtApellidoPaterno());
			per.setClase(cls);
			per.setTipoDocumento(tdoc);

			List<Persona> personas = new ArrayList<Persona>();

			personas = consultaService.getPersonasByPersona(per);

			Persona personabd = new Persona();

			if (personas.size() == 0) {

				try {
					per.setNombreCompleto(per.getNombres() + " "
							+ per.getApellidoPaterno() + " "
							+ per.getApellidoMaterno());
					personabd = personaService.registrar(per);
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Persona agregada",
							"Persona agregada");
					FacesContext.getCurrentInstance().addMessage(null, msg);

				} catch (Exception e2) {
					e2.printStackTrace();
				}

			} else {

				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Persona Existente", "Persona Existente");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

			/*List<Persona> personas2 = new ArrayList<Persona>();
			personas2.add(personabd);
			personaDataModelBusq = new PersonaDataModel(personas2);*/
			
			buscarPersona(e);
			
			// Limpiar datos de persona
			setIdClase(-1);
			setCodCliente(null);
			setIdTipoDocumento(-1);
			setNumeroDocumento(null);
			setTxtNombres("");
			setTxtApellidoMaterno("");
			setTxtApellidoPaterno("");
			
			

		}

	}

	public void agregar_Inculpado(ActionEvent e) {
		if (getIdClase_inclp() == -1 || getIdTipoDocumento_inclp() == -1
				|| getNumeroDocumento_inclp() == 0
				|| getTxtNombres_inclp() == ""
				|| getTxtApellidoMaterno_inclp() == ""
				|| getTxtApellidoPaterno_inclp() == "") {

			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"Datos Requeridos: Clase, Tipo Doc, Nro Documento, Nombre, Apellido Paterno, Apellido Materno",
					"");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			Persona per = new Persona();
			Clase cls = new Clase();
			cls.setIdClase(getIdClase_inclp());
			per.setCodCliente(getCodCliente_inclp());
			TipoDocumento tdoc = new TipoDocumento();
			tdoc.setIdTipoDocumento(getIdTipoDocumento_inclp());
			per.setNumeroDocumento(getNumeroDocumento_inclp());
			per.setNombres(getTxtNombres_inclp());
			per.setApellidoMaterno(getTxtApellidoMaterno_inclp());
			per.setApellidoPaterno(getTxtApellidoPaterno_inclp());

			List<Persona> personas = new ArrayList<Persona>();

			personas = consultaService.getPersonasByPersona(per);

			Persona personabd = new Persona();

			if (personas.size() == 0) {
				try {
					per.setNombreCompleto(per.getNombres() + " "
							+ per.getApellidoPaterno() + " "
							+ per.getApellidoMaterno());
					personabd = personaService.registrar(per);
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Persona agregada",
							"Persona agregada");
					FacesContext.getCurrentInstance().addMessage(null, msg);

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Persona Existente", "Persona Existente");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

			List<Persona> personas2 = new ArrayList<Persona>();
			personas2.add(personabd);
			personaDataModelBusq = new PersonaDataModel(personas2);

			// Limpiar datos de inculpado
			setIdClase_inclp(-1);
			setCodCliente_inclp(null);
			setIdTipoDocumento_inclp(-1);
			setNumeroDocumento_inclp(null);
			setTxtNombres_inclp("");
			setTxtApellidoMaterno_inclp("");
			setTxtApellidoPaterno_inclp("");
		}
	}

	public String agregarDetalleInculpado(ActionEvent e) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Inculpado Agregado", "Inculpado Agregado");
		
		FacesContext.getCurrentInstance().addMessage(null, msg);
		List<Persona> personas = new ArrayList<Persona>();
		personaDataModelBusq = new PersonaDataModel(personas);
		return null;
	}

	public void seleccionarOrgano() {
		try {
			logger.debug("Organo seleccionado:");
			logger.debug("Nombre: " + getSelectedOrgano().getNombre());
			logger.debug("Distrito: "
					+ getSelectedOrgano().getUbigeo().getDistrito());
			logger.debug("Provincia: "
					+ getSelectedOrgano().getUbigeo().getProvincia());
			logger.debug("Departamento: "
					+ getSelectedOrgano().getUbigeo().getDepartamento());

			if (getSelectedOrgano().getUbigeo().getDistrito() != null
					&& getSelectedOrgano().getUbigeo().getProvincia() != null
					&& getSelectedOrgano().getUbigeo().getDepartamento() != null) {
				String descripcion = getSelectedOrgano()
						.getNombre()
						.toUpperCase()
						.concat("(")
						.concat(getSelectedOrgano().getUbigeo().getDistrito()
								.toUpperCase())
						.concat(", ")
						.concat(getSelectedOrgano().getUbigeo().getProvincia()
								.toUpperCase())
						.concat(", ")
						.concat(getSelectedOrgano().getUbigeo()
								.getDepartamento().toUpperCase()).concat(")");

				logger.debug("Descripcion seleccionada: " + descripcion);

				getSelectedOrgano().setNombreDetallado(descripcion);

				organo1 = getSelectedOrgano();
			} else {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						"Debe seleccionar un órgano con distrito diferente a vacío o nulo",
						"");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		} catch (Exception e) {
			logger.debug("Error: ", e);
		}
	}

	public void seleccionarAbogado() {
		getSelectedAbogado().setNombreCompletoMayuscula(
				getSelectedAbogado().getNombres().toUpperCase()	+ " "
				+ getSelectedAbogado().getApellidoPaterno().toUpperCase()+ " "
				+ getSelectedAbogado().getApellidoMaterno().toUpperCase());
		
		getHonorario().setAbogado(getSelectedAbogado());
	}

	public void seleccionarPersona() {
		getSelectPersona().setNombreCompletoMayuscula(
				getSelectPersona().getNombres().toUpperCase()+ " "
				+ getSelectPersona().getApellidoPaterno().toUpperCase()+ " "
				+ getSelectPersona().getApellidoMaterno().toUpperCase());
		
		getInvolucrado().setPersona(getSelectPersona());
	}

	public void seleccionarInvolucrado() {
		getSelectInvolucrado().setNombreCompletoMayuscula(
			getSelectInvolucrado().getNombres().toUpperCase()+ " "
			+ getSelectInvolucrado().getApellidoPaterno().toUpperCase()+ " "
			+ getSelectInvolucrado().getApellidoMaterno().toUpperCase());

		getInculpado().setPersona(getSelectInvolucrado());
	}

	public void limpiarAnexo(ActionEvent e) {
		setTxtComentario("");
		setTxtTitulo("");
		setAnexo(new Anexo());
	}

	public void limpiarOrgano(CloseEvent event) {
		/*
		 * setOrgano(new Organo()); getOrgano().setEntidad(new Entidad());
		 * getOrgano().setUbigeo(new Ubigeo());
		 * organoDataModel = new OrganoDataModel(new ArrayList<Organo>());
		 */
		// Limpiar datos
		setIdEntidad(0);
		setTxtOrgano("");
		setUbigeo(new Ubigeo());
	}

	public void limpiarOrgano(ActionEvent event) {
		/* setOrgano(new Organo()); getOrgano().setEntidad(new Entidad());
		 * getOrgano().setUbigeo(new Ubigeo());
		 * organoDataModel = new OrganoDataModel(new ArrayList<Organo>());
		 */
		// Limpiar datos
		setIdEntidad(0);
		setTxtOrgano("");
		setUbigeo(new Ubigeo());
	}

	public void limpiarAbogado(CloseEvent event) {
		/*setAbogado(new Abogado()); getAbogado().setDni(null);
		 * setEstudio(new Estudio());
		 * abogadoDataModel = new AbogadoDataModel(new ArrayList<Abogado>());
		 */

		setTxtRegistroCA("");
		setTxtNombre("");
		setDNI(null);
		setTxtApePat("");
		setTxtApeMat("");
		setTxtCorreo("");
		setTxtTel("");
		setEstudio(new Estudio());
	}

	public void limpiarAbogado(ActionEvent event) {
		/*
		 * setAbogado(new Abogado()); getAbogado().setDni(null);
		 * setEstudio(new Estudio());
		 * 
		 * abogadoDataModel = new AbogadoDataModel(new ArrayList<Abogado>());
		 */

		setTxtRegistroCA("");
		setTxtNombre("");
		setDNI(null);
		setTxtApePat("");
		setTxtApeMat("");
		setTxtCorreo("");
		setTxtTel("");
		setEstudio(new Estudio());
	}

	public void limpiarPersona(CloseEvent event) {

		/*
		 * setPersona(new Persona()); getPersona().setClase(new Clase());
		 * getPersona().setCodCliente(null); getPersona().setTipoDocumento(new
		 * TipoDocumento()); getPersona().setNumeroDocumento(null);
		 * 
		 * personaDataModelBusq = new PersonaDataModel(new
		 * ArrayList<Persona>());
		 */

		setIdClase(-1);
		setCodCliente(null);
		setIdTipoDocumento(-1);
		setNumeroDocumento(null);
		setTxtNombres("");
		setTxtApellidoMaterno("");
		setTxtApellidoPaterno("");
	}

	public void limpiarPersona(ActionEvent event) {

		/*
		 * setPersona(new Persona()); getPersona().setClase(new Clase());
		 * getPersona().setCodCliente(null); getPersona().setTipoDocumento(new
		 * TipoDocumento()); getPersona().setNumeroDocumento(null);
		 * 
		 * personaDataModelBusq = new PersonaDataModel(new
		 * ArrayList<Persona>());
		 */

		setIdClase(-1);
		setCodCliente(null);
		setIdTipoDocumento(-1);
		setNumeroDocumento(null);
		setTxtNombres("");
		setTxtApellidoMaterno("");
		setTxtApellidoPaterno("");
	}

	public void limpiarInculpado(ActionEvent event) {

		/*
		 * setPersona(new Persona()); getPersona().setClase(new Clase());
		 * getPersona().setCodCliente(null); getPersona().setTipoDocumento(new
		 * TipoDocumento()); getPersona().setNumeroDocumento(null);
		 * 
		 * personaDataModelBusq = new PersonaDataModel(new
		 * ArrayList<Persona>());
		 */

		setIdClase_inclp(-1);
		setCodCliente_inclp(null);
		setIdTipoDocumento_inclp(-1);
		setNumeroDocumento_inclp(null);
		setTxtNombres_inclp("");
		setTxtApellidoMaterno_inclp("");
		setTxtApellidoPaterno_inclp("");
	}

	public void limpiarInculpado(CloseEvent event) {
		setIdClase_inclp(-1);
		setCodCliente_inclp(null);
		setIdTipoDocumento_inclp(-1);
		setNumeroDocumento_inclp(null);
		setTxtNombres_inclp("");
		setTxtApellidoMaterno_inclp("");
		setTxtApellidoPaterno_inclp("");
	}

	public void limpiar(ActionEvent e) {
		logger.debug("limpiando los valores de la pantalla principal del expediente");
		Calendar calendar = Calendar.getInstance();

		setNroExpeOficial("");
		setInicioProceso(null);
		setEstado(0);
		setProceso(0);
		setVia(0);
		setInstancia(0);
		setResponsable(new Usuario());
		setOficina(new Oficina());
		setTipo(0);
		setOrgano1(new Organo());
		setSecretario("");
		setCalificacion(0);
		setRecurrencia(new Recurrencia());

		setHonorario(new Honorario());
		setHonorarios(new ArrayList<Honorario>());

		setInvolucrado(new Involucrado());
		setInvolucradoDataModel(new InvolucradoDataModel());

		setCuantia(new Cuantia());
		setCuantiaDataModel(new CuantiaDataModel());

		setInculpado(new Inculpado());
		setInculpados(new ArrayList<Inculpado>());

		setMoneda(0);
		setMontoCautelar(0.0);
		setTipoCautelar(0);
		setDescripcionCautelar("");
		setContraCautela(0);
		setImporteCautelar(0.0);
		setEstadoCautelar(0);

		setFechaResumen(null);
		setResumen("");
		setTodoResumen("");
		setResumens(new ArrayList<Resumen>());

		setAnexo(new Anexo());
		setAnexos(new ArrayList<Anexo>());

		setRiesgo(0);

		/*
		 * FacesContext fc = FacesContext.getCurrentInstance(); ExternalContext
		 * exc = fc.getExternalContext(); HttpSession session1 = (HttpSession)
		 * exc.getSession(true);
		 * 
		 * com.grupobbva.seguridad.client.domain.Usuario usuarioAux=
		 * (com.grupobbva.seguridad.client.domain.Usuario)
		 * session1.getAttribute("usuario");
		 * 
		 * FacesContext.getCurrentInstance().getExternalContext().invalidateSession
		 * ();
		 * 
		 * ExternalContext context =
		 * FacesContext.getCurrentInstance().getExternalContext(); HttpSession
		 * session = (HttpSession) context.getSession(true);
		 * session.setAttribute("usuario", usuarioAux);
		 */

	}

	public String home() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext exc = fc.getExternalContext();
		HttpSession session1 = (HttpSession) exc.getSession(true);

		com.grupobbva.seguridad.client.domain.Usuario usuarioAux = (com.grupobbva.seguridad.client.domain.Usuario) session1
				.getAttribute("usuario");
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) context.getSession(true);
		session.setAttribute("usuario", usuarioAux);

		return "consultaExpediente.xhtml?faces-redirect=true";
	}

	@SuppressWarnings("unchecked")
	public void guardar(ActionEvent event) 
	{
		logger.debug("==== guardar Expediente() ====");
		/**/
		if (getProceso() > 0) {
			if (getVia() > 0) {
				if (getInstancia() > 0) {
					if (getNroExpeOficial().length() > 0) {
						if (getEstado() > 0) {
							if (getOficina() != null) {
								if (getCalificacion() > 0) {
									if (getTipo() > 0) {
										if (getOrgano1() != null) {
											/**/
											GenericDao<Expediente, Object> expedienteDAO = (GenericDao<Expediente, Object>) SpringInit
													.getApplicationContext().getBean("genericoDao");

											Busqueda filtro = Busqueda.forClass(Expediente.class);
											List<Expediente> expedientes = consultaService.getExpedienteByNroExpediente(getNroExpeOficial());

											if (expedientes.size() == 0) {
												logger.debug("No existe expediente con Nro: "+ getNroExpeOficial());

												Expediente expediente = new Expediente();
												// expedienteService.registrar(expediente);

												GenericDao<EstadoExpediente, Object> estadoExpedienteDAO = (GenericDao<EstadoExpediente, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<Proceso, Object> procesoDAO = (GenericDao<Proceso, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<Via, Object> viaDAO = (GenericDao<Via, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<Instancia, Object> instanciaDAO = (GenericDao<Instancia, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<TipoExpediente, Object> tipoExpedienteDAO = (GenericDao<TipoExpediente, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<Calificacion, Object> calificacionDAO = (GenericDao<Calificacion, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");

												EstadoExpediente estadoExpedientebd = new EstadoExpediente();
												Proceso procesobd = new Proceso();
												Via viabd = new Via();
												Instancia instanciabd = new Instancia();
												TipoExpediente tipoExpedientebd = new TipoExpediente();
												Calificacion calificacionbd = new Calificacion();
												try {
													estadoExpedientebd = estadoExpedienteDAO.buscarById(EstadoExpediente.class,	getEstado());
													procesobd = procesoDAO.buscarById(Proceso.class,getProceso());
													viabd = viaDAO.buscarById(Via.class,getVia());
													instanciabd = instanciaDAO.buscarById(Instancia.class,getInstancia());
													tipoExpedientebd = tipoExpedienteDAO.buscarById(TipoExpediente.class,getTipo());
													calificacionbd = calificacionDAO.buscarById(Calificacion.class,	getCalificacion());
												} catch (Exception e1) {
													logger.error(SglConstantes.MSJ_ERROR_EXCEPTION+" :"+e1);
												}

												expediente.setNumeroExpediente(getNroExpeOficial());
												expediente.setFechaInicioProceso(getInicioProceso());
												expediente.setEstadoExpediente(estadoExpedientebd);
												expediente.setProceso(procesobd);
												expediente.setVia(viabd);
												expediente.setInstancia(instanciabd);
												expediente.setUsuario(getResponsable());
												expediente.setOficina(getOficina());
												expediente.setTipoExpediente(tipoExpedientebd);
												expediente.setOrgano(getOrgano1());
												expediente.setSecretario(getSecretario());
												expediente.setCalificacion(calificacionbd);
												expediente.setRecurrencia(getRecurrencia());

												
												//Honorarios
												List<Honorario> honorarios = getHonorarios();
												expediente.setHonorarios(new ArrayList<Honorario>());
												
												for (Honorario honorario : honorarios) {
													if (honorario != null) {
														for (TipoHonorario tipo : getTipoHonorarios()) {
															if (honorario.getTipoHonorario().getDescripcion().equals(tipo.getDescripcion())) {
																honorario.setTipoHonorario(tipo);
																break;
															}
														}
														
														for (Moneda moneda : getMonedas()) {
															if (honorario.getMoneda().getSimbolo().equals(moneda.getSimbolo())) {
																honorario.setMoneda(moneda);
																break;
															}
														}

														for (SituacionHonorario situacionHonorario : getSituacionHonorarios()) {
															if (honorario.getSituacionHonorario().getDescripcion().equals(situacionHonorario.getDescripcion())) {
																honorario.setSituacionHonorario(situacionHonorario);
																break;
															}
														}

														expediente.addHonorario(honorario);
													}
												}
												
												//Involucrados
												List<Involucrado> involucrados = (List<Involucrado>) getInvolucradoDataModel().getWrappedData();
												expediente.setInvolucrados(new ArrayList<Involucrado>());
												
												for (Involucrado involucrado : involucrados) {
													if (involucrado != null) {
														for (RolInvolucrado rol : getRolInvolucrados()) {
															if (rol.getNombre().equals(involucrado.getRolInvolucrado().getNombre())) {
																involucrado.setRolInvolucrado(rol);
																break;
															}
														}

														for (TipoInvolucrado tipo : getTipoInvolucrados()) {
															if (tipo.getNombre().equals(involucrado.getTipoInvolucrado().getNombre())) {
																involucrado.setTipoInvolucrado(tipo);
																break;
															}
														}

														expediente.addInvolucrado(involucrado);
													}
												}

												//Cuantias
												List<Cuantia> cuantias = (List<Cuantia>) getCuantiaDataModel().getWrappedData();
												expediente.setCuantias(new ArrayList<Cuantia>());
												for (Cuantia cuantia : cuantias) {
													if (cuantia != null) {
														for (Moneda m : getMonedas()) {
															if (m.getSimbolo().equals(cuantia.getMoneda().getSimbolo())) {
																cuantia.setMoneda(m);
																break;
															}
														}
														
														expediente.addCuantia(cuantia);
													}
												}

												//Inculpados
												List<Inculpado> inculpados = getInculpados();
												expediente.setInculpados(new ArrayList<Inculpado>());
												
												for (Inculpado inculpado : inculpados) {
													if (inculpado != null) {
														for (Moneda moneda : getMonedas()) {
															if (moneda.getSimbolo().equals(inculpado.getMoneda().getSimbolo())) {
																inculpado.setMoneda(moneda);
																break;
															}
														}

														for (SituacionInculpado s : getSituacionInculpados()) {
															if (s.getNombre().equals(inculpado.getSituacionInculpado().getNombre())) {
																inculpado.setSituacionInculpado(s);
																break;
															}
														}

														expediente.addInculpado(inculpado);
													}
												}

												GenericDao<Moneda, Object> monedaDAO = (GenericDao<Moneda, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<TipoCautelar, Object> tipoCautelarDAO = (GenericDao<TipoCautelar, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<ContraCautela, Object> contraCautelaDAO = (GenericDao<ContraCautela, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<EstadoCautelar, Object> estadoCautelarDAO = (GenericDao<EstadoCautelar, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");

												Moneda monedabd = new Moneda();
												TipoCautelar tipoCautelarbd = new TipoCautelar();
												ContraCautela contraCautelabd = new ContraCautela();
												EstadoCautelar estadoCautelarbd = new EstadoCautelar();

												try {
													monedabd = monedaDAO.buscarById(Moneda.class,getMoneda());
													tipoCautelarbd = tipoCautelarDAO.buscarById(TipoCautelar.class,	getTipoCautelar());
													contraCautelabd = contraCautelaDAO.buscarById(ContraCautela.class,getContraCautela());
													estadoCautelarbd = estadoCautelarDAO.buscarById(EstadoCautelar.class,getEstadoCautelar());
												} catch (Exception e) {
													logger.error(SglConstantes.MSJ_ERROR_EXCEPTION+e);
												}

												expediente.setMoneda(monedabd);
												expediente.setMontoCautelar(getMontoCautelar());
												expediente.setTipoCautelar(tipoCautelarbd);
												expediente.setDescripcionCautelar(getDescripcionCautelar());
												expediente.setContraCautela(contraCautelabd);
												expediente.setImporteCautelar(getImporteCautelar());
												expediente.setEstadoCautelar(estadoCautelarbd);

												//Resumen
												List<Resumen> resumens = getResumens();
												expediente.setResumens(new ArrayList<Resumen>());

												for (Resumen resumen : resumens)
													if (resumen != null){
														expediente.addResumen(resumen);
													}

												//Anexos
												List<Anexo> anexos = getAnexos();
												expediente.setAnexos(new ArrayList<Anexo>());

												if (anexos != null) {
													if (anexos.size() > 0) {

														File fichUbicacion;
														String ubicacion = "";

														if (expediente.getInstancia() == null) {
															ubicacion = Util.getMessage("ruta_documento")+ File.separator
																	+ expediente.getNumeroExpediente()+ File.separator
																	+ "sin-instancia";
														} else {
															ubicacion = Util.getMessage("ruta_documento")+ File.separator
																	+ expediente.getNumeroExpediente()+ File.separator
																	+ expediente.getInstancia().getNombre();
														}

														fichUbicacion = new File(ubicacion);
														fichUbicacion.mkdirs();

														for (Anexo anexo : anexos)
															if (anexo != null) {
																anexo.setUbicacion(ubicacion+ File.separator
																		+ anexo.getUbicacion());

																byte b[] = anexo.getBytes();
																File fichSalida = new File(anexo.getUbicacion());
																try {
																	FileOutputStream canalSalida = new FileOutputStream(fichSalida);
																	canalSalida.write(b);
																	canalSalida.close();
																}
																catch (IOException e) {
																	logger.error(SglConstantes.MSJ_ERROR_EXCEPTION+"IOException en Anexos:"+e);
																}catch(Exception e1){
																	logger.error(SglConstantes.MSJ_ERROR_EXCEPTION+"en Anexos:"+e1);
																}
																
																expediente.addAnexo(anexo);
															}
													}
												}

												GenericDao<Riesgo, Object> riesgoDAO = (GenericDao<Riesgo, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<Actividad, Object> actividadDAO = (GenericDao<Actividad, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<SituacionActProc, Object> situacionActProcDAO = (GenericDao<SituacionActProc, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");
												GenericDao<Etapa, Object> etapaDAO = (GenericDao<Etapa, Object>) SpringInit
														.getApplicationContext().getBean("genericoDao");

												filtro = Busqueda.forClass(Actividad.class);

												Riesgo riesgobd = new Riesgo();
												List<Actividad> actividades = new ArrayList<Actividad>();
												SituacionActProc situacionActProc = new SituacionActProc();
												Etapa etapabd = new Etapa();

												try {
													riesgobd = riesgoDAO.buscarById(Riesgo.class,getRiesgo());
													actividades = actividadDAO.buscarDinamico(filtro);
													situacionActProc = situacionActProcDAO.buscarById(SituacionActProc.class,1);
													etapabd = etapaDAO.buscarById(Etapa.class,1);

												} catch (Exception e) {
													logger.error(SglConstantes.MSJ_ERROR_CONSULTAR+"risgos, actividades, sitActPro y Etapas:"+e);
												}

												expediente.setRiesgo(riesgobd);
												expediente.setFlagRevertir(SglConstantes.COD_NO_REVERTIR);
												expediente.setActividadProcesals(new ArrayList<ActividadProcesal>());

												SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

												//Date date = new Date();
												Date date = expediente.getFechaInicioProceso();
												logger.debug("[EXP]-FechaInicioProceso:"+expediente.getFechaInicioProceso());
												try {
													//String dates = format.format(new Date());
													String dates = format.format(expediente.getFechaInicioProceso());
													date = format.parse(dates);
													logger.debug("[EXP]-date.parse:"+date);
												} catch (ParseException e) {
													logger.error(SglConstantes.MSJ_ERROR_EXCEPTION+"ParseException:"+e);
												}catch(Exception e1){
													logger.error(SglConstantes.MSJ_ERROR_EXCEPTION+e1);
												}
												/** Actividades Procesales 08/08/2013*/
												GenericDao<ActividadProcesalMan, Object> actividadProcesalDAO = 
														(GenericDao<ActividadProcesalMan, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
												Busqueda filtroActProcesal = Busqueda.forClass(ActividadProcesalMan.class);
												/** Solo Civiles*/
												filtroActProcesal.add(Restrictions.eq("proceso.idProceso", 1));
												//filtroActProcesal.add(Restrictions.eq("via.idVia", viabd.getIdVia()));
												List<ActividadProcesalMan> lstListado=new ArrayList<ActividadProcesalMan>();
											    try {
													lstListado=actividadProcesalDAO.buscarDinamico(filtroActProcesal);
												} catch (Exception e1) {
													logger.error(SglConstantes.MSJ_ERROR_CONSULTAR+"las ActividadProcesalMan:"+e1);
												}
											    if(lstListado!=null){
											    	logger.info(SglConstantes.MSJ_TAMANHIO_LISTA+"Actividades Procesales-CIVIL ok es: "+lstListado.size());
											    }

												//TODO - Verificar
												// Si es un proceso CIVIL
												if (procesobd != null) {
													if (procesobd.getIdProceso() == 1) {
														logger.debug("[EXP]-Proceso:"+procesobd.getIdProceso() +"\t"+ procesobd.getNombre());
														logger.debug("[EXP]-Via:"+viabd.getIdVia() +"\t"+ viabd.getNombre());

														if (actividades != null) {

															for (Actividad actividad : actividades) { 

																ActividadProcesal actividadProcesal = new ActividadProcesal();
																actividadProcesal.setSituacionActProc(situacionActProc);
																actividadProcesal.setEtapa(etapabd);
																actividadProcesal.setFechaActividad(new Timestamp(date.getTime()));
																//logger.debug("actividadProcesal-fechaActividad:"+actividadProcesal.getFechaActividad());

																for (ActividadProcesalMan x : lstListado) {  // Inicio del For
																	//Para todos
																	if (actividad.getIdActividad() == x.getActividad().getIdActividad()) { 
																		actividadProcesal.setPlazoLey(x.getPlazo()+"");

																		Date fechaVencimiento = calcularFechaVencimiento(date,x.getPlazo());
																		actividadProcesal.setFechaVencimiento(new Timestamp(fechaVencimiento.getTime()));
																		actividadProcesal.setActividad(actividad);
																		expediente.addActividadProcesal(actividadProcesal);
																	}
														//1	OPOSICIONES Y TACHAS
														
																	
																/*if (actividad.getIdActividad() == 1) { 
																	
                                                                   
																	if(x.getActividad().getIdActividad()==1)
																	{
																		actividadProcesal.setPlazoLey(x.getPlazo()+"");

																		Date fechaVencimiento = calcularFechaVencimiento(
																			date,Integer.parseInt(Util.getMessage(x.getPlazo()+"")));
																		actividadProcesal.setFechaVencimiento(new Timestamp(fechaVencimiento.getTime()));
																	}
															    	
																	actividadProcesal.setActividad(actividad);
																	expediente.addActividadProcesal(actividadProcesal);
																}
																//2	EXCEPCIONES
															if (actividad.getIdActividad() == 2) {

																	actividadProcesal.setActividad(actividad);
																	if(x.getActividad().getIdActividad()==2){
																	actividadProcesal.setPlazoLey(Util.getMessage("diasActividad2"));

																	Date fechaVencimiento = calcularFechaVencimiento(
																	   date,Integer.parseInt(Util.getMessage("diasActividad2")));
																	actividadProcesal.setFechaVencimiento(new Timestamp(fechaVencimiento.getTime()));
																	}

																	
																		
																	expediente.addActividadProcesal(actividadProcesal);
																}
																//4	CONTESTACIÓN DE LA DEMANDA
																if (actividad.getIdActividad() == 4) {

																	actividadProcesal.setActividad(actividad);
																	if(x.getActividad().getIdActividad()==4){
																	actividadProcesal.setPlazoLey(Util.getMessage("diasActividad3"));

																	Date fechaVencimiento = calcularFechaVencimiento(
																		date,Integer.parseInt(Util.getMessage("diasActividad3")));

																	actividadProcesal.setFechaVencimiento(new Timestamp(fechaVencimiento.getTime()));
																	}
																	expediente.addActividadProcesal(actividadProcesal);
																}
															*/
																
																} // Fin del For

															}
														}

													}

													try {
														expedienteDAO.save(expediente);
														FacesContext.getCurrentInstance().addMessage("growl",
															new FacesMessage(FacesMessage.SEVERITY_INFO,"Exitoso","Se registró el expediente"));
														logger.debug(SglConstantes.MSJ_EXITO_REGISTRO+"el expediente.");

														setFlagColumnGeneral(false);
														setFlagDeshabilitadoGeneral(true);

													} catch (Exception e) {
														FacesContext.getCurrentInstance().addMessage("growl",
															new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Exitoso","No se registró el expediente "));
														logger.debug(SglConstantes.MSJ_ERROR_REGISTR+"el expediente:"+ e);

														setFlagColumnGeneral(true);
														setFlagDeshabilitadoGeneral(false);
													}
												} else {
													FacesContext.getCurrentInstance().addMessage("growl",
														new FacesMessage(FacesMessage.SEVERITY_ERROR,"Campos requeridos","No se registró el expediente "));
													logger.debug(SglConstantes.MSJ_ERROR_REGISTR+"el expediente.");
												}

											} else {

												FacesContext.getCurrentInstance().addMessage("growl",
														new FacesMessage(FacesMessage.SEVERITY_ERROR,"Existe expediente","El número de expediente ya existe"));
													logger.debug("El numero de expediente ["+ getNroExpeOficial()+"] ya existe.");

												setFlagColumnGeneral(true);
												setFlagDeshabilitadoGeneral(false);

											}

											/**/
										} else {
											FacesMessage msg = new FacesMessage(
												FacesMessage.SEVERITY_ERROR,"Órgano Requerido","Órgano Requerido");
											FacesContext.getCurrentInstance().addMessage("growl_cab",msg);
										}
									} else {
										FacesMessage msg = new FacesMessage(
												FacesMessage.SEVERITY_ERROR,"Tipo Requerido","Tipo Requerido");
										FacesContext.getCurrentInstance().addMessage("growl_cab", msg);
									}
								} else {
									FacesMessage msg = new FacesMessage(
											FacesMessage.SEVERITY_ERROR,"Calificación Requerido","Calificación Requerido");
									FacesContext.getCurrentInstance().addMessage("growl_cab", msg);
								}
							} else {
								FacesMessage msg = new FacesMessage(
										FacesMessage.SEVERITY_ERROR,"Oficina Requerido","Oficina Requerido");
								FacesContext.getCurrentInstance().addMessage("growl_cab", msg);
							}
						} else {
							FacesMessage msg = new FacesMessage(
									FacesMessage.SEVERITY_ERROR,"Estado Requerido", "Estado Requerido");
							FacesContext.getCurrentInstance().addMessage("growl_cab", msg);
						}

					} else {
						FacesMessage msg = new FacesMessage(
								FacesMessage.SEVERITY_ERROR,"Número Expediente Oficial Requerido","Número Expediente Oficial Requerido");
						FacesContext.getCurrentInstance().addMessage("growl_cab", msg);
					}

				} else {
					FacesMessage msg = new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Instancia Requerido","Instancia Requerido");
					FacesContext.getCurrentInstance().addMessage("growl_cab",msg);
				}

			} else {
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Via Requerido","Via Requerido");
				FacesContext.getCurrentInstance().addMessage("growl_cab", msg);
			}
		} else {
			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,"Proceso Requerido", "Proceso Requerido");
			FacesContext.getCurrentInstance().addMessage("growl_cab", msg);
		}
		/**/

	}

	public Date sumaDias(Date fechaOriginal, int dias) {

		if (dias > 0) {

			Date fechaFin = sumaTiempo(fechaOriginal, Calendar.DAY_OF_MONTH, dias);

			int diasNL = getDiasNoLaborables(fechaOriginal, fechaFin);

			return sumaTiempo(fechaOriginal, Calendar.DAY_OF_MONTH, dias + diasNL);

		} else {

			Date fechaFin = sumaTiempo(fechaOriginal, Calendar.DAY_OF_MONTH, 0);

			return fechaFin;
		}

	}

	public int getDomingos(Calendar fechaInicial, Calendar fechaFinal) {

		int dias = 0;

		// mientras la fecha inicial sea menor o igual que la fecha final se
		// cuentan los dias
		while (fechaInicial.before(fechaFinal)
				|| fechaInicial.equals(fechaFinal)) {

			// si el dia de la semana de la fecha minima es diferente de sabado
			// o domingo
			if (fechaInicial.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				// se aumentan los dias de diferencia entre min y max
				dias++;
			}
			// se suma 1 dia para hacer la validacion del siguiente dia.
			fechaInicial.add(Calendar.DATE, 1);

		}

		return dias;

	}

	public int getDiasNoLaborables(Date fechaInicio, Date FechaFin) 
	{	
		logger.debug("-----------------En el metodo getDiasNoLaborales-----------------------");
		
		logger.debug("Se obtiene los campos sabado y domingo del properties para validar si se toman en cuenta en calculos");
		
		boolean validarSabado = Boolean.valueOf(Util.getMessage("sabado"));
		boolean validarDomingo = Boolean.valueOf(Util.getMessage("domingo"));
		
		List<Feriado> resultadofn = new ArrayList<Feriado>();
		List<Feriado> resultadoflo = new ArrayList<Feriado>();

		int sumaFeriadosNacionales = 0;
		int sumaFeriadosOrgano = 0;
		int sumaDNL = 0;
		int sumaSabados =0;
		int sumaDomingos = 0;

		Calendar calendarInicial = Calendar.getInstance();
		calendarInicial.setTime(fechaInicio);
		
		logger.debug("Parametros de Fecha Inicio (antes): " + fechaInicio);
		logger.debug("Parametros de Fecha Inicio (despues): " + calendarInicial);

		Calendar calendarFinal = Calendar.getInstance();
		calendarFinal.setTime(FechaFin);
		
		logger.debug("Parametros de Fecha Fin (antes): " + FechaFin);
		logger.debug("Parametros de Fecha Fin (despues): " + calendarFinal);
		
		//Si el flag sabado es true entonces sumar los sabados como no laborales
		if (!validarSabado)
		{
			sumaSabados = Utilitarios.getSabados(calendarInicial, calendarFinal);
		}
		
		logger.debug("Resultado sumaSabados: " + sumaSabados);
		
		calendarInicial.setTime(fechaInicio);
		calendarFinal.setTime(FechaFin);
		
		//Si el flag domingo es true entonces sumar los domingos como no laborales
		if (!validarDomingo)
		{
			sumaDomingos = Utilitarios.getDomingos(calendarInicial, calendarFinal);
		}
		
		logger.debug("Resultado sumaDomingos: " + sumaDomingos);

		//sumaDomingos = getDomingos(calendarInicial, calendarFinal);

		GenericDao<Feriado, Object> feriadoDAO = (GenericDao<Feriado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

		Busqueda filtroNac = Busqueda.forClass(Feriado.class);
		filtroNac.add(Restrictions.between("fecha", fechaInicio, FechaFin));
		filtroNac.add(Restrictions.eq("indicador", 'N'));
		filtroNac.add(Restrictions.eq("estado", 'A'));

		try {
			resultadofn = feriadoDAO.buscarDinamico(filtroNac);
		} catch (Exception e1) {
			logger.error(SglConstantes.MSJ_ERROR_CONSULTAR+"feriados Nacionales:"+e1);
		}

		logger.debug("Valida si se tiene que restar los sabados para el calculo de dias no laborales: " + validarSabado);
		//Valida si se tiene que restar los sabados para el calculo de dias no laborales 
		if (validarSabado)
		{
			resultadofn = restarSabados(resultadofn);
		}		
		
		logger.debug("Valida si se tiene que restar los domingos para el calculo de dias no laborales: " + validarDomingo);
		//Valida si se tiene que restar los domingos para el calculo de dias no laborales 
		if (validarDomingo)
		{
			resultadofn = restarDomingos(resultadofn);
		}

		sumaFeriadosNacionales = resultadofn.size();

		Busqueda filtroOrg = Busqueda.forClass(Feriado.class);

		if (getOrgano1() != null) 
		{
			filtroOrg.add(Restrictions.eq("organo.idOrgano", getOrgano1().getIdOrgano()));
			filtroOrg.add(Restrictions.eq("tipo", 'O'));
			filtroOrg.add(Restrictions.eq("indicador", 'L'));
			filtroOrg.add(Restrictions.eq("estado", 'A'));
			filtroOrg.add(Restrictions.between("fecha", fechaInicio, FechaFin));

			try {
				resultadoflo = feriadoDAO.buscarDinamico(filtroOrg);
			} catch (Exception e1) {
				logger.error(SglConstantes.MSJ_ERROR_CONSULTAR+"feriados Locales:"+e1);
			}

			resultadoflo = restarDomingos(resultadoflo);

			sumaFeriadosOrgano = resultadoflo.size();
		}

		sumaDNL = sumaFeriadosNacionales + sumaFeriadosOrgano + sumaDomingos + sumaSabados;
		
		logger.debug("Dias no laborales: " + sumaDNL);

		return sumaDNL;
	}

	public List<Feriado> restarDomingos(List<Feriado> feriados) {

		List<Feriado> feri = new ArrayList<Feriado>();

		for (Feriado fer : feriados) {

			Calendar calendarInicial = Calendar.getInstance();
			calendarInicial.setTime(fer.getFecha());

			if (!esDomingo(calendarInicial)) {
				feri.add(fer);
			}
		}

		return feri;
	}
	
	public List<Feriado> restarSabados(List<Feriado> feriados) {

		List<Feriado> feri = new ArrayList<Feriado>();

		for (Feriado fer : feriados) {

			Calendar calendarInicial = Calendar.getInstance();
			calendarInicial.setTime(fer.getFecha());

			if (!Utilitarios.esSabado(calendarInicial)) {
				feri.add(fer);
			}
		}

		return feri;
	}

	public Date calcularFechaVencimiento(Date fechaOriginal, int dias) 
	{	
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date fechaTMP = sumaDias(fechaOriginal, dias);

		if (esValido(fechaTMP)) 
		{	
			boolean validarSabado = Boolean.valueOf(Util.getMessage("sabado"));
			boolean validarDomingo = Boolean.valueOf(Util.getMessage("domingo"));
			int totalSuma=0;
			
			Calendar tmpFecha = Calendar.getInstance();
			tmpFecha.setTime(fechaTMP);
			
			if (esSabado(tmpFecha))
			{
				if (!validarSabado)
				{
					if (!validarDomingo)
					{
						totalSuma+=2;
					}
					else
					{
						totalSuma+=1;
					}
				}
			}
			else if (esDomingo(tmpFecha))
			{
				if (!validarDomingo)
				{
					totalSuma+=1;
				}
			}
			else
			{
				totalSuma=0;
			}
			
			
			tmpFecha.add(Calendar.DAY_OF_MONTH, totalSuma);
			Date fechaResultante = new Date(tmpFecha.getTimeInMillis());
			
			Calendar calendario = Calendar.getInstance();
			calendario.setTimeInMillis(fechaTMP.getTime());

			int diasTotales = Utilitarios.diferenciaTiempo(fechaOriginal, fechaResultante);
			
			int diasNL = getDiasNoLaborables(fechaOriginal, fechaTMP);
			
			int diferenciaTMP = diasTotales-diasNL;
			
			logger.debug("diasTotales: " + diasTotales);
			logger.debug("dias no laborales" + diasNL);
			
			if (diferenciaTMP!=dias)
			{
				int diferenciaTMP2 = dias-diferenciaTMP;
				
				logger.debug("Dias de diferencia: " + diferenciaTMP2);
				logger.debug("Fecha antes de sumar dias para cumplir plazo: " + fechaTMP);
				
				if (diferenciaTMP2>0)
				{
					fechaTMP = sumaTiempo(fechaTMP, Calendar.DAY_OF_MONTH, diferenciaTMP2);	
				}
				
				while (!esValido(fechaTMP)) 
				{
					fechaTMP = sumaTiempo(fechaTMP, Calendar.DAY_OF_MONTH, 1);	
				}
			}
			
			logger.debug("Fecha de fin luego de validacion: " + fechaTMP);
			
			Date newDate = fechaTMP;
			Date date2 = new Date();
			
			if (newDate!=null)
			{
				String format = dateFormat.format(newDate);

				try {
					date2 = dateFormat.parse(format);
				} catch (ParseException e1) {
					logger.error(SglConstantes.MSJ_ERROR_CONVERTIR+"la Fecha Vencimiento:"+e1);
				}	
			}
				
			return date2;

		} else {
			
			while (!esValido(fechaTMP)) 
			{
				fechaTMP = sumaTiempo(fechaTMP, Calendar.DAY_OF_MONTH, 1);	
			}
			
			Calendar calendario = Calendar.getInstance();
			calendario.setTimeInMillis(fechaTMP.getTime());

			int diasTotales = Utilitarios.diferenciaTiempo(fechaOriginal, fechaTMP);
			
			int diasNL = getDiasNoLaborables(fechaOriginal, fechaTMP);
			
			int diferenciaTMP = diasTotales-diasNL;
			
			logger.debug("diasTotales: " + diasTotales);
			logger.debug("dias no laborales" + diasNL);
			
			if (diferenciaTMP!=dias)
			{
				int diferenciaTMP2 = dias-diferenciaTMP;
				
				logger.debug("Dias de diferencia: " + diferenciaTMP2);
				logger.debug("Fecha antes de sumar dias para cumplir plazo: " + fechaTMP);
				
				if (diferenciaTMP2>0)
				{
					fechaTMP = sumaTiempo(fechaTMP, Calendar.DAY_OF_MONTH, diferenciaTMP2);	
				}
				
				while (!esValido(fechaTMP)) 
				{
					fechaTMP = sumaTiempo(fechaTMP, Calendar.DAY_OF_MONTH, 1);	
				}
			}	
			
			logger.debug("Fecha de fin luego de validacion: " + fechaTMP);
				
			String format = dateFormat.format(fechaTMP);
			Date date2 = new Date();
			
			try {
				date2 = dateFormat.parse(format);
			} catch (ParseException e1) {
				logger.error(SglConstantes.MSJ_ERROR_CONVERTIR+"la Fecha Vencimiento:"+e1);
			}

			return date2;

		}
	}

	public static boolean esDomingo(Calendar fecha) {

		if (fecha.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

			return true;
		} else {

			return false;
		}
	}
	
	public static boolean esSabado(Calendar fecha) {

		if (fecha.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {

			return true;
		} else {

			return false;
		}
	}

	public boolean esFeriado(Date fecha) {

		int sumaFeriadosNacionales = 0;
		int sumaFeriadosOrgano = 0;
		int sumaDF = 0;

		List<Feriado> resultadofn = new ArrayList<Feriado>();
		List<Feriado> resultadofo = new ArrayList<Feriado>();

		GenericDao<Feriado, Object> feriadoDAO = (GenericDao<Feriado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

		Busqueda filtroNac = Busqueda.forClass(Feriado.class);
		filtroNac.add(Restrictions.eq("fecha", fecha));
		filtroNac.add(Restrictions.eq("indicador", 'N'));
		filtroNac.add(Restrictions.eq("estado", 'A'));

		try {
			resultadofn = feriadoDAO.buscarDinamico(filtroNac);

		} catch (Exception e1) {
			logger.error(SglConstantes.MSJ_ERROR_CONSULTAR+"feriados Nacionales:"+e1);
		}

		sumaFeriadosNacionales = resultadofn.size();

		Busqueda filtroOrg = Busqueda.forClass(Feriado.class);

		if (getOrgano1() != null) {

			filtroOrg.add(Restrictions.eq("organo.idOrgano", getOrgano1()
					.getIdOrgano()));
			filtroOrg.add(Restrictions.eq("tipo", 'O'));
			filtroOrg.add(Restrictions.eq("indicador", 'L'));
			filtroOrg.add(Restrictions.eq("estado", 'A'));
			filtroOrg.add(Restrictions.eq("fecha", fecha));

			try {

				resultadofo = feriadoDAO.buscarDinamico(filtroOrg);

			} catch (Exception e1) {
				logger.error(SglConstantes.MSJ_ERROR_CONSULTAR+"feriados Organo:"+e1);
			}

			sumaFeriadosOrgano = resultadofo.size();

		}

		sumaDF = sumaFeriadosNacionales + sumaFeriadosOrgano;

		if (sumaDF > 0) {

			return true;
		} else {

			return false;
		}

	}

	public boolean esValido(Date date) {

		Calendar calendarInicial = Calendar.getInstance();
		calendarInicial.setTime(date);

		boolean flagDomingo = esDomingo(calendarInicial);
		boolean flagFeriado = esFeriado(date);

		if (flagDomingo == true || flagFeriado == true) {

			return false;

		} else {

			return true;
		}

	}

	private static Date sumaTiempo(Date fechaOriginal, int field, int amount) {
		int cantSabados=0;
		int cantDomingos=0;
		
		boolean validarSabado = Boolean.valueOf(Util.getMessage("sabado"));
		boolean validarDomingo = Boolean.valueOf(Util.getMessage("domingo"));
		
		Calendar calendario = Calendar.getInstance();
		calendario.setTimeInMillis(fechaOriginal.getTime());
		
		calendario.add(field, amount);
		
		if (esSabado(calendario))
		{
			if (!validarSabado)
			{
				cantSabados++;
			}
		}
		if (esDomingo(calendario))
		{
			if (!validarDomingo)
			{
				cantDomingos++;
			}
		}
		
		calendario.add(field, cantSabados+cantDomingos);
		Date fechaResultante = new Date(calendario.getTimeInMillis());

		return fechaResultante;
	}

	
	public List<Recurrencia> completeRecurrencia(String query) {
		List<Recurrencia> recurrencias = consultaService.getRecurrencias();
		if (recurrencias != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "recurrencias es:["+ recurrencias.size() + "]. ");
		}
		List<Recurrencia> results = new ArrayList<Recurrencia>();
		for (Recurrencia rec : recurrencias) {
			if (rec.getNombre() != null) {
				if (rec.getNombre().toUpperCase().contains(query.toUpperCase())) {
					results.add(rec);
				}
			}
		}
		return results;
	}

	public List<Materia> completeMaterias(String query) {
		List<Materia> results = new ArrayList<Materia>();
		List<Materia> materias = consultaService.getMaterias();
		if (materias != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "materias es:["+ materias.size() + "]. ");
		}
		for (Materia mat : materias) {
			String descripcion = "".concat(
				mat.getDescripcion() != null ? mat.getDescripcion().toLowerCase() : "").concat(" ");
			if (descripcion.contains(query.toLowerCase())) {
				results.add(mat);
			}
		}
		return results;
	}

	public List<Persona> completePersona(String query) {
		logger.debug("=== completePersona ===");
		List<Persona> results = new ArrayList<Persona>();
		List<Persona> personas = consultaService.getPersonas();
		if (personas != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "personas es:["+ personas.size() + "]. ");
		}

		for (Persona pers : personas) {
			String nombreCompletoMayuscula = ""
					.concat(pers.getNombres() != null ? pers.getNombres().toUpperCase() : "").concat(" ")
					.concat(pers.getApellidoPaterno() != null ? pers.getApellidoPaterno().toUpperCase() : "")
					.concat(" ").concat(pers.getApellidoMaterno() != null ? pers.getApellidoMaterno().toUpperCase() : "");

			if (nombreCompletoMayuscula.contains(query.toUpperCase())) {
				pers.setNombreCompletoMayuscula(nombreCompletoMayuscula);
				results.add(pers);
			}
		}
		return results;
	}

	public List<Oficina> completeOficina(String query) {
		logger.debug("=== completeOficina ===");

		List<Oficina> results = new ArrayList<Oficina>();
		List<Oficina> oficinas = consultaService.getOficinas();

		if (oficinas != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "oficinas es:["+ oficinas.size() + "]. ");
		}

		for (Oficina oficina : oficinas) {
			if (oficina.getTerritorio() != null) {
				String texto = oficina.getCodigo()
						.concat(" ").concat(oficina.getNombre() != null ? oficina.getNombre().toUpperCase() : "").concat(" (")
						.concat(oficina.getTerritorio().getDescripcion() != null ? oficina.getTerritorio().getDescripcion().toUpperCase(): "").concat(")");
			
				if (texto.contains(query.toUpperCase())) {
					oficina.setNombreDetallado(texto);
					results.add(oficina);
				}
			}
		}
		return results;
	}

	public List<Estudio> completeEstudio(String query) {
		List<Estudio> estudios = consultaService.getEstudios();
		List<Estudio> results = new ArrayList<Estudio>();
		if (estudios != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "estudios es:["+ estudios.size() + "]. ");
		}
		for (Estudio est : estudios) {
			if (est.getNombre() != null) {
				if (est.getNombre().toUpperCase().contains(query.toUpperCase())) {
					results.add(est);
				}
			}
		}

		return results;
	}

	public List<Abogado> completeAbogado(String query) {
		logger.debug("=== completeAbogado ===");
		List<Abogado> abogados = consultaService.getAbogados();
		List<Abogado> results = new ArrayList<Abogado>();

		if (abogados != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "abogados es:["+ abogados.size() + "]. ");
		}
		
		for (Abogado abog : abogados) {
			String nombreCompletoMayuscula = ""	.concat(abog.getNombres() != null ? 
					abog.getNombres().toUpperCase() : "").concat(" ").concat(abog.getApellidoPaterno() != null ? abog
					.getApellidoPaterno().toUpperCase() : "").concat(" ").concat(abog.getApellidoMaterno() != null ? abog
					.getApellidoMaterno().toUpperCase() : "");
			if (nombreCompletoMayuscula.contains(query.toUpperCase())) {
				abog.setNombreCompletoMayuscula(nombreCompletoMayuscula);
				results.add(abog);
			}
		}
		return results;
	}

	public List<Organo> completeOrgano(String query) {
		logger.debug("=== completeOrgano()=== ");
		List<Organo> results = new ArrayList<Organo>();
		List<Organo> organos = consultaService.getOrganos();
		String descripcion = "";

		if (organos != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "organos es:["+ organos.size() + "]. ");
		}

		for (Organo organo : organos) {
			if (organo.getUbigeo() != null) {
				descripcion = "".concat(organo.getNombre() != null ? organo.getNombre().toUpperCase() : "")
						.concat("(").concat(organo.getUbigeo().getDistrito() != null ? organo
						.getUbigeo().getDistrito().toUpperCase() : "")
						.concat(", ").concat(organo.getUbigeo().getProvincia() != null ? organo
						.getUbigeo().getProvincia().toUpperCase() : "")
						.concat(", ").concat(organo.getUbigeo().getDepartamento() != null ? organo
						.getUbigeo().getDepartamento().toUpperCase() : "").concat(")");
			}

			if (descripcion.toUpperCase().contains(query.toUpperCase())) {
				if (descripcion.compareTo("") != 0) {
					organo.setNombreDetallado(descripcion);
					results.add(organo);
				}
			}
		}
		return results;
	}
	
	/**
	 * Metodo usado para mostrar un filtro autocompletable de Distrito
	 * @param query Representa el query
	 * @return List<Ubigeo> Representa la lista de Ubigeos
	 * **/
	public List<Ubigeo> completeDistrito(String query) 
	{
		List<Ubigeo> results = new ArrayList<Ubigeo>();
		List<Ubigeo> ubigeos = consultaService.getUbigeos();

		if (ubigeos != null){
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "ubigeos es:[" + ubigeos.size() + "]. ");
		}

		for (Ubigeo ubig : ubigeos){
			String descripcion = ubig.getCodDist().concat(" - ")
					.concat(ubig.getDistrito() != null ? ubig.getDistrito().toUpperCase() : "").concat(",")
					.concat(ubig.getProvincia() != null ? ubig.getProvincia().toUpperCase() : "").concat(",")
					.concat(ubig.getDepartamento() != null ? ubig.getDepartamento().toUpperCase() : "").concat(" ");
			
			//logger.debug("Validacion para mostrar un solo registro de ubigeo de distrito");
			
			if (descripcion.toUpperCase().contains(query.toUpperCase()) && ubig.getCodDist().compareTo(ubig.getCodProv())!=0) 
			{
				ubig.setDescripcionDistrito(descripcion);
				results.add(ubig);
			}
		}
		return results;
	}

	/**
	 * Metodo usado para mostrar un filtro autocompletable de Responsable
	 * @param query Representa el query
	 * @return List Representa la lista de responsable
	 * **/
	public List<Usuario> completeResponsable(String query) {
		List<Usuario> results = new ArrayList<Usuario>();

		List<Usuario> usuarios = consultaService.getUsuarios();

		if (usuarios != null) {
			logger.debug(SglConstantes.MSJ_TAMANHIO_LISTA + "usuarios es:["+ usuarios.size() + "]. ");
		}

		for (Usuario usuario : usuarios) {

			if (usuario.getNombres().toUpperCase()
					.contains(query.toUpperCase())
					|| usuario.getApellidoPaterno().toUpperCase()
							.contains(query.toUpperCase())
					|| usuario.getApellidoMaterno().toUpperCase()
							.contains(query.toUpperCase())
					|| usuario.getCodigo().toUpperCase()
							.contains(query.toUpperCase())) {

				usuario.setNombreDescripcion(usuario.getCodigo() + " - "
						+ usuario.getNombres() + " "
						+ usuario.getApellidoPaterno() + " "
						+ usuario.getApellidoMaterno());

				results.add(usuario);
			}
		}
		return results;
	}

	/**
	 * Listener que se ejecuta cada vez que se modifica el proceso 
	 * en los combos en el formulario de registro de expediente
	 * */
	public void cambioProceso() {
		setTabAsigEstExt(false);
		setTabCuanMat(false);
		setTabCaucion(false);

		if (getProceso() != 0) {
			//1	Civil
			if (getProceso() == 1 || getProceso() == 3) {
				setTabCaucion(true);
				setReqPenal(true);
				setReqCabecera(true);
			}
			//2	Penal
			if (getProceso() == 2) {
				setTabAsigEstExt(true);
				setTabCuanMat(true);
				setReqPenal(true);
				setReqCabecera(true);
			}

			vias = consultaService.getViasByProceso(getProceso());
			instancias = new ArrayList<Instancia>();

		} else {
			vias = new ArrayList<Via>();
		}
	}

	/**
	 * Listener que se ejecuta cada vez que se modifica la via 
	 * en los combos en el formulario de registro de expediente
	 * */
	public void cambioVia() {
		if (getVia() != 0) {
			instancias = consultaService.getInstanciasByVia(getVia());
			setInstancia(instancias.get(0).getIdInstancia());
		} else {
			instancias = new ArrayList<Instancia>();
		}
	}

	public RegistroExpedienteMB() {
		// TODO Limpiar datos
	}

	@PostConstruct
	@SuppressWarnings("unchecked")
	private void cargarCombos() {

		logger.debug("=== Inicializando valores cargarCombos() ===");

		Calendar cal = Calendar.getInstance();
		inicioProceso = cal.getTime();
		fechaResumen = cal.getTime();

		selectedOrgano = new Organo();

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext exc = fc.getExternalContext();
		HttpSession session1 = (HttpSession) exc.getSession(true);

		com.grupobbva.seguridad.client.domain.Usuario usuario = (com.grupobbva.seguridad.client.domain.Usuario) session1
				.getAttribute("usuario");
		
		//com.grupobbva.seguridad.client.domain.Usuario usuario= new com.grupobbva.seguridad.client.domain.Usuario();
		//usuario.setUsuarioId("P015740");
		if (usuario.getUsuarioId() != null) {
			logger.debug("Recuperando usuario sesion: "	+ usuario.getUsuarioId());
		}

		GenericDao<Usuario, Object> usuarioDAO = (GenericDao<Usuario, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(Usuario.class);
		filtro.add(Restrictions.eq("codigo", usuario.getUsuarioId()));
		List<Usuario> usuarios = new ArrayList<Usuario>();

		try {
			usuarios = usuarioDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.error(SglConstantes.MSJ_ERROR_OBTENER + "el usuario:" + e);
		}

		if (usuarios != null) {

			if (usuarios.size() != 0) {

				usuarios.get(0).setNombreDescripcion(
						usuarios.get(0).getCodigo() + " - "
								+ usuarios.get(0).getNombres() + " "
								+ usuarios.get(0).getApellidoPaterno() + " "
								+ usuarios.get(0).getApellidoMaterno());

				setResponsable(usuarios.get(0));
			}

		}

		oficina = new Oficina();

		honorario = new Honorario();
		honorario.setCantidad(0);
		honorario.setMonto(0.0);
		setHonorarios(new ArrayList<Honorario>());

		involucrado = new Involucrado();
		involucradoDataModel = new InvolucradoDataModel(new ArrayList<Involucrado>());

		cuantia = new Cuantia();
		cuantia.setPretendido(0.0);
		cuantiaDataModel = new CuantiaDataModel(new ArrayList<Cuantia>());

		inculpado = new Inculpado();
		inculpado.setFecha(cal.getTime());
		inculpado.setMonto(0.0);
		inculpado.setNrocupon(0000);
		inculpados = new ArrayList<Inculpado>();

		resumens = new ArrayList<Resumen>();

		anexo = new Anexo();
		anexo.setFechaInicio(cal.getTime());
		anexos = new ArrayList<Anexo>();

		organo = new Organo();
		organo.setEntidad(new Entidad());
		organo.setUbigeo(new Ubigeo());

		organo1 = new Organo();
		organo1.setEntidad(new Entidad());
		organo1.setUbigeo(new Ubigeo());

		ubigeo = new Ubigeo();

		organoDataModel = new OrganoDataModel(new ArrayList<Organo>());

		persona = new Persona();
		persona.setClase(new Clase());
		persona.setCodCliente(null);
		persona.setTipoDocumento(new TipoDocumento());
		persona.setNumeroDocumento(null);

		selectPersona = new Persona();

		abogado = new Abogado();
		abogado.setDni(null);

		estudio = new Estudio();
		abogadoDataModel = new AbogadoDataModel(new ArrayList<Abogado>());

		personaDataModelBusq = new PersonaDataModel(new ArrayList<Persona>());

		setReqPenal(false);
		setReqCabecera(false);

		setFlagColumnGeneral(true);
		setFlagDeshabilitadoGeneral(false);

		logger.debug("Cargando combos para registro expediente");

		estados = consultaService.getEstadoExpedientes();

		procesos = consultaService.getProcesos();

		if (usuarios.get(0).getRol().getIdRol() == 1) {

			setFlagLectResp(false);

		} else {

			setFlagLectResp(true);
		}

		tipos = consultaService.getTipoExpedientes();
		entidades = consultaService.getEntidads();
		calificaciones = consultaService.getCalificacions();
		tipoHonorarios = consultaService.getTipoHonorarios();
		monedas = consultaService.getMonedas();
		situacionHonorarios = consultaService.getSituacionHonorarios();
		situacionCuotas = consultaService.getSituacionCuota();
		situacionInculpados = consultaService.getSituacionInculpados();
		rolInvolucrados = consultaService.getRolInvolucrados();
		tipoInvolucrados = consultaService.getTipoInvolucrados();
		clases = consultaService.getClases();
		tipoDocumentos = consultaService.getTipoDocumentos();
		tipoCautelares = consultaService.getTipoCautelars();
		contraCautelas = consultaService.getContraCautelas();
		estadosCautelares = consultaService.getEstadoCautelars();
		riesgos = consultaService.getRiesgos();

		vias = new ArrayList<Via>();
		instancias = new ArrayList<Instancia>();

		tipoHonorariosString = new ArrayList<String>();
		for (TipoHonorario t : tipoHonorarios)
			tipoHonorariosString.add(t.getDescripcion());

		monedasString = new ArrayList<String>();
		for (Moneda m : monedas)
			monedasString.add(m.getSimbolo());

		situacionHonorariosString = new ArrayList<String>();
		for (SituacionHonorario s : situacionHonorarios)
			situacionHonorariosString.add(s.getDescripcion());

		situacionCuotasString = new ArrayList<String>();
		for (SituacionCuota s : situacionCuotas)
			situacionCuotasString.add(s.getDescripcion());

		situacionInculpadosString = new ArrayList<String>();
		for (SituacionInculpado s : situacionInculpados)
			situacionInculpadosString.add(s.getNombre());

		rolInvolucradosString = new ArrayList<String>();
		for (RolInvolucrado r : rolInvolucrados)
			rolInvolucradosString.add(r.getNombre());

		tipoInvolucradosString = new ArrayList<String>();
		for (TipoInvolucrado t : tipoInvolucrados)
			tipoInvolucradosString.add(t.getNombre());

	}

	public void editHonor(RowEditEvent event) {
		logger.debug("=== inicia editHonor() ===");
		GenericDao<SituacionCuota, Object> situacionCuotasDAO = (GenericDao<SituacionCuota, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

		Honorario honorarioModif = ((Honorario) event.getObject());
		if (honorarioModif != null) {
			logger.debug("[EDIT_HONORAR]-Numero:" + honorarioModif.getNumero());
			logger.debug("[EDIT_HONORAR]-Monto:" + honorarioModif.getMonto());
		}

		for (Honorario honorario : honorarios) {

			// Valida si el honorario selecionado coincide con la lista
			if (honorarioModif.getNumero() == honorario.getNumero()) {

				// Situacion "Pendiente"
				//if (honorario.getSituacionHonorario().getIdSituacionHonorario() == 1) {
				if(honorarioModif.getSituacionHonorario().getDescripcion().compareTo(SglConstantes.SITUACION_HONORARIO_PENDIENTE)==0)
				{	
					double importe = honorarioModif.getMonto()
							/ honorarioModif.getCantidad().intValue();

					importe = Math.rint(importe * 100) / 100;

					List<SituacionCuota> situacionCuotas = new ArrayList<SituacionCuota>();
					
					Busqueda filtro = Busqueda.forClass(SituacionCuota.class);
					filtro.add(Restrictions.eq("descripcion", SglConstantes.SITUACION_CUOTA_PENDIENTE));
					
					try {
						situacionCuotas = situacionCuotasDAO.buscarDinamico(filtro);
					} catch (Exception e) {
						logger.error(SglConstantes.MSJ_ERROR_CONSULTAR+"situacionCuotas: "+e);
					}
					SituacionCuota situacionCuota = situacionCuotas.get(0);

					// honorario.setMontoPagado(0.0);
					honorario.setCuotas(new ArrayList<Cuota>());

					Calendar cal = Calendar.getInstance();

					for (int i = 1; i <= honorarioModif.getCantidad()
							.intValue(); i++) {
						Cuota cuota = new Cuota();
						cuota.setNumero(i);
						cuota.setMoneda(honorarioModif.getMoneda().getSimbolo());
						cuota.setNroRecibo("000" + i);
						cuota.setImporte(importe);
						cal.add(Calendar.MONTH, 1);
						Date date = cal.getTime();
						cuota.setFechaPago(date);

						cuota.setSituacionCuota(new SituacionCuota());
						cuota.getSituacionCuota().setIdSituacionCuota(
								situacionCuota.getIdSituacionCuota());
						cuota.getSituacionCuota().setDescripcion(
								situacionCuota.getDescripcion());
						cuota.setFlagPendiente(true);

						honorario.addCuota(cuota);

					}

				}

			}
		}

		FacesMessage msg = new FacesMessage("Honorario Editado",
				"Honorario Editado al modificar algunos campos");
		FacesContext.getCurrentInstance().addMessage("growl", msg);

		logger.debug("=== saliendo de editHonor() ===");

	}

	public void editDetHonor(RowEditEvent event) {

		logger.debug("=== inicia editDetHonor() ===");
		Cuota cuotaModif = ((Cuota) event.getObject());
		if (cuotaModif != null) {
			logger.debug("cuotaModif.getImporte():" + cuotaModif.getImporte());
			logger.debug("cuotaModif.getMonto():"
					+ cuotaModif.getHonorario().getMonto());
		}
		double importe = cuotaModif.getImporte();
		double importeRestante = cuotaModif.getHonorario().getMonto() - importe;

		double importeNuevo = 0.0;

		if (cuotaModif.getHonorario().getCantidad().intValue() > 1) {

			importeNuevo = importeRestante
					/ (cuotaModif.getHonorario().getCantidad().intValue() - 1);
			importeNuevo = Math.rint(importeNuevo * 100) / 100;

		} else {

			importeNuevo = importe;
		}

		for (Honorario honorario : honorarios) {

			if (cuotaModif.getHonorario().getNumero() == honorario.getNumero()) {

				for (Cuota cuota : cuotas) {

					if (cuota.getNumero() == cuotaModif.getNumero()) {

						logger.debug("cuotaModif.getSituacionCuota().getDescripcion():"
								+ cuotaModif.getSituacionCuota()
										.getDescripcion());

						if (cuotaModif.getSituacionCuota().getDescripcion()
								.equals("Pagado")
								|| cuotaModif.getSituacionCuota()
										.getDescripcion().equals("Baja")) {

							// honorario.setMonto(importeRestante);
							honorario.setMontoPagado(honorario.getMontoPagado()
									+ importe);

							if (honorario.getMonto().compareTo(
									honorario.getMontoPagado()) == 0) {
								SituacionHonorario situacionHonorario = getSituacionHonorarios()
										.get(1);
								honorario
										.setSituacionHonorario(situacionHonorario);
								honorario.setFlagPendiente(false);
							}
							cuota.setFlagPendiente(false);
						}
						cuota.setImporte(importe);

					} else {

						cuota.setImporte(importeNuevo);
					}

				}

				honorario.setCuotas(cuotas);
				break;
			}

		}

		/*
		 * for(Honorario honorario: honorarios){
		 * 
		 * if (cuotaModif.getHonorario().getNumero() == honorario.getNumero()) {
		 * 
		 * 
		 * 
		 * double importe = cuotaModif.getImporte(); double importeRestante =
		 * honorario.getMonto() - importe; double importeNuevo = importeRestante
		 * / honorario.getCantidad().intValue(); importeNuevo =
		 * Math.rint(importeNuevo*100)/100;
		 * 
		 * honorario.setMonto(importeRestante);
		 * honorario.setMontoPagado(honorario.getMontoPagado() + importe);
		 * 
		 * 
		 * SituacionCuota situacionCuota = getSituacionCuotas().get(0);
		 * 
		 * honorario.setMontoPagado(0.0); honorario.setCuotas(new
		 * ArrayList<Cuota>());
		 * 
		 * Calendar cal = Calendar.getInstance();
		 * 
		 * for (int i = 1; i <= honorario.getCantidad().intValue(); i++) { Cuota
		 * cuota = new Cuota(); cuota.setNumero(i);
		 * cuota.setMoneda(honorario.getMoneda().getSimbolo());
		 * cuota.setNroRecibo("000" + i); cuota.setImporte(importe);
		 * cal.add(Calendar.MONTH, 1); Date date = cal.getTime();
		 * cuota.setFechaPago(date);
		 * 
		 * cuota.setSituacionCuota(new SituacionCuota());
		 * cuota.getSituacionCuota
		 * ().setIdSituacionCuota(situacionCuota.getIdSituacionCuota());
		 * cuota.getSituacionCuota
		 * ().setDescripcion(situacionCuota.getDescripcion());
		 * 
		 * honorario.addCuota(cuota);
		 * 
		 * }
		 * 
		 * break; }
		 * 
		 * }
		 */

		logger.debug("== saliendo de editDetHonor() === ");
		FacesMessage msg = new FacesMessage("Cuota Editada", "Cuota Editada");
		FacesContext.getCurrentInstance().addMessage("growl", msg);
	}

	public void editInv(RowEditEvent event) {

	}

	public void editCua(RowEditEvent event) {

	}

	public void editIncul(RowEditEvent event) {

	}

	public void editRes(RowEditEvent event) {

	}

	public void editAnex(RowEditEvent event) {

	}

	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Honorario Cancelado","Honorario Cancelado");

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public List<Inculpado> getInculpados() {
		return inculpados;
	}

	public void setInculpados(List<Inculpado> inculpados) {
		this.inculpados = inculpados;
	}

	public CuantiaDataModel getCuantiaDataModel() {
		return cuantiaDataModel;
	}

	public void setCuantiaDataModel(CuantiaDataModel cuantiaDataModel) {
		this.cuantiaDataModel = cuantiaDataModel;
	}

	public String getNroExpeOficial() {
		return nroExpeOficial;
	}

	public void setNroExpeOficial(String nroExpeOficial) {
		this.nroExpeOficial = nroExpeOficial;
	}

	public Organo getSelectedOrgano() {
		return selectedOrgano;
	}

	public void setSelectedOrgano(Organo selectedOrgano) {
		this.selectedOrgano = selectedOrgano;
	}

	public Oficina getOficina() {
		return oficina;
	}

	public void setOficina(Oficina oficina) {
		this.oficina = oficina;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Persona getSelectPersona() {
		return selectPersona;
	}

	public void setSelectPersona(Persona selectPersona) {
		this.selectPersona = selectPersona;
	}

	public Inculpado getInculpado() {
		return inculpado;
	}

	public void setInculpado(Inculpado inculpado) {
		this.inculpado = inculpado;
	}

	public Abogado getAbogado() {
		return abogado;
	}

	public void setAbogado(Abogado abogado) {
		this.abogado = abogado;
	}

	public AbogadoDataModel getAbogadoDataModel() {
		return abogadoDataModel;
	}

	public void setAbogadoDataModel(AbogadoDataModel abogadoDataModel) {
		this.abogadoDataModel = abogadoDataModel;
	}

	public boolean isTabAsigEstExt() {
		return tabAsigEstExt;
	}

	public void setTabAsigEstExt(boolean tabAsigEstExt) {
		this.tabAsigEstExt = tabAsigEstExt;
	}

	public boolean isTabCaucion() {
		return tabCaucion;
	}

	public void setTabCaucion(boolean tabCaucion) {
		this.tabCaucion = tabCaucion;
	}

	public boolean isTabCuanMat() {
		return tabCuanMat;
	}

	public void setTabCuanMat(boolean tabCuanMat) {
		this.tabCuanMat = tabCuanMat;
	}

	public String getSecretario() {
		return secretario;
	}

	public void setSecretario(String secretario) {
		this.secretario = secretario;
	}

	public Cuantia getCuantia() {
		return cuantia;
	}

	public void setCuantia(Cuantia cuantia) {
		this.cuantia = cuantia;
	}

	public Organo getOrgano() {
		return organo;
	}

	public void setOrgano(Organo organo) {
		this.organo = organo;
	}

	public OrganoDataModel getOrganoDataModel() {
		return organoDataModel;
	}

	public void setOrganoDataModel(OrganoDataModel organoDataModel) {
		this.organoDataModel = organoDataModel;
	}

	public PersonaDataModel getPersonaDataModelBusq() {
		return personaDataModelBusq;
	}

	public void setPersonaDataModelBusq(PersonaDataModel personaDataModelBusq) {
		this.personaDataModelBusq = personaDataModelBusq;
	}

	public Cuantia getSelectedCuantia() {
		return selectedCuantia;
	}

	public void setSelectedCuantia(Cuantia selectedCuantia) {
		this.selectedCuantia = selectedCuantia;
	}

	public Inculpado getSelectedInculpado() {
		return selectedInculpado;
	}

	public void setSelectedInculpado(Inculpado selectedInculpado) {
		this.selectedInculpado = selectedInculpado;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public List<EstadoExpediente> getEstados() {
		return estados;
	}

	public void setEstados(List<EstadoExpediente> estados) {
		this.estados = estados;
	}

	public List<TipoExpediente> getTipos() {
		return tipos;
	}

	public void setTipos(List<TipoExpediente> tipos) {
		this.tipos = tipos;
	}

	public List<Moneda> getMonedas() {
		return monedas;
	}

	public void setMonedas(List<Moneda> monedas) {
		this.monedas = monedas;
	}

	public List<Calificacion> getCalificaciones() {
		return calificaciones;
	}

	public void setCalificaciones(List<Calificacion> calificaciones) {
		this.calificaciones = calificaciones;
	}

	public List<Via> getVias() {
		return vias;
	}

	public void setVias(List<Via> vias) {
		this.vias = vias;
	}

	public List<Instancia> getInstancias() {
		return instancias;
	}

	public void setInstancias(List<Instancia> instancias) {
		this.instancias = instancias;
	}

	public int getProceso() {
		return proceso;
	}

	public void setProceso(int proceso) {
		this.proceso = proceso;
	}

	public Usuario getResponsable() {
		return responsable;
	}

	public void setResponsable(Usuario responsable) {
		this.responsable = responsable;
	}

	public List<Entidad> getEntidades() {
		return entidades;
	}

	public void setEntidades(List<Entidad> entidades) {
		this.entidades = entidades;
	}

	public Organo getOrgano1() {
		return organo1;
	}

	public void setOrgano1(Organo organo1) {
		this.organo1 = organo1;
	}

	public Recurrencia getRecurrencia() {
		return recurrencia;
	}

	public void setRecurrencia(Recurrencia recurrencia) {
		this.recurrencia = recurrencia;
	}

	public List<TipoHonorario> getTipoHonorarios() {
		return tipoHonorarios;
	}

	public void setTipoHonorarios(List<TipoHonorario> tipoHonorarios) {
		this.tipoHonorarios = tipoHonorarios;
	}

	public Honorario getHonorario() {
		return honorario;
	}

	public void setHonorario(Honorario honorario) {
		this.honorario = honorario;
	}

	public Honorario getSelectedHonorario() {
		return selectedHonorario;
	}

	public void setSelectedHonorario(Honorario selectedHonorario) {
		this.selectedHonorario = selectedHonorario;
	}

	public List<Honorario> getHonorarios() {
		return honorarios;
	}

	public void setHonorarios(List<Honorario> honorarios) {
		this.honorarios = honorarios;
	}

	public List<SituacionCuota> getSituacionCuotas() {
		return situacionCuotas;
	}

	public void setSituacionCuotas(List<SituacionCuota> situacionCuotas) {
		this.situacionCuotas = situacionCuotas;
	}

	public List<SituacionInculpado> getSituacionInculpados() {
		return situacionInculpados;
	}

	public void setSituacionInculpados(
			List<SituacionInculpado> situacionInculpados) {
		this.situacionInculpados = situacionInculpados;
	}

	public List<Cuota> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<Cuota> cuotas) {
		this.cuotas = cuotas;
	}

	public Involucrado getInvolucrado() {
		return involucrado;
	}

	public void setInvolucrado(Involucrado involucrado) {
		this.involucrado = involucrado;
	}

	public List<RolInvolucrado> getRolInvolucrados() {
		return rolInvolucrados;
	}

	public void setRolInvolucrados(List<RolInvolucrado> rolInvolucrados) {
		this.rolInvolucrados = rolInvolucrados;
	}

	public InvolucradoDataModel getInvolucradoDataModel() {
		return involucradoDataModel;
	}

	public void setInvolucradoDataModel(
			InvolucradoDataModel involucradoDataModel) {
		this.involucradoDataModel = involucradoDataModel;
	}

	public Involucrado getSelectedInvolucrado() {
		return selectedInvolucrado;
	}

	public void setSelectedInvolucrado(Involucrado selectedInvolucrado) {
		this.selectedInvolucrado = selectedInvolucrado;
	}

	public List<TipoInvolucrado> getTipoInvolucrados() {
		return tipoInvolucrados;
	}

	public void setTipoInvolucrados(List<TipoInvolucrado> tipoInvolucrados) {
		this.tipoInvolucrados = tipoInvolucrados;
	}

	public List<Clase> getClases() {
		return clases;
	}

	public void setClases(List<Clase> clases) {
		this.clases = clases;
	}

	public List<TipoDocumento> getTipoDocumentos() {
		return tipoDocumentos;
	}

	public void setTipoDocumentos(List<TipoDocumento> tipoDocumentos) {
		this.tipoDocumentos = tipoDocumentos;
	}

	public List<Riesgo> getRiesgos() {
		return riesgos;
	}

	public void setRiesgos(List<Riesgo> riesgos) {
		this.riesgos = riesgos;
	}

	public List<EstadoCautelar> getEstadosCautelares() {
		return estadosCautelares;
	}

	public void setEstadosCautelares(List<EstadoCautelar> estadosCautelares) {
		this.estadosCautelares = estadosCautelares;
	}

	public int getVia() {
		return via;
	}

	public void setVia(int via) {
		this.via = via;
	}

	public int getInstancia() {
		return instancia;
	}

	public void setInstancia(int instancia) {
		this.instancia = instancia;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(int calificacion) {
		this.calificacion = calificacion;
	}

	public Date getInicioProceso() {
		return inicioProceso;
	}

	public void setInicioProceso(Date inicioProceso) {
		this.inicioProceso = inicioProceso;
	}

	public int getMoneda() {
		return moneda;
	}

	public void setMoneda(int moneda) {
		this.moneda = moneda;
	}

	public double getMontoCautelar() {
		return montoCautelar;
	}

	public void setMontoCautelar(double montoCautelar) {
		this.montoCautelar = montoCautelar;
	}

	public double getImporteCautelar() {
		return importeCautelar;
	}

	public void setImporteCautelar(double importeCautelar) {
		this.importeCautelar = importeCautelar;
	}

	public String getDescripcionCautelar() {
		return descripcionCautelar;
	}

	public void setDescripcionCautelar(String descripcionCautelar) {
		this.descripcionCautelar = descripcionCautelar;
	}

	public List<TipoCautelar> getTipoCautelares() {
		return tipoCautelares;
	}

	public void setTipoCautelares(List<TipoCautelar> tipoCautelares) {
		this.tipoCautelares = tipoCautelares;
	}

	public int getTipoCautelar() {
		return tipoCautelar;
	}

	public void setTipoCautelar(int tipoCautelar) {
		this.tipoCautelar = tipoCautelar;
	}

	public List<ContraCautela> getContraCautelas() {
		return contraCautelas;
	}

	public void setContraCautelas(List<ContraCautela> contraCautelas) {
		this.contraCautelas = contraCautelas;
	}

	public int getRiesgo() {
		return riesgo;
	}

	public void setRiesgo(int riesgo) {
		this.riesgo = riesgo;
	}

	public int getContraCautela() {
		return contraCautela;
	}

	public void setContraCautela(int contraCautela) {
		this.contraCautela = contraCautela;
	}

	public Estudio getEstudio() {
		return estudio;
	}

	public void setEstudio(Estudio estudio) {
		this.estudio = estudio;
	}

	public int getEstadoCautelar() {
		return estadoCautelar;
	}

	public void setEstadoCautelar(int estadoCautelar) {
		this.estadoCautelar = estadoCautelar;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public Anexo getAnexo() {
		return anexo;
	}

	public void setAnexo(Anexo anexo) {
		this.anexo = anexo;
	}

	public List<Anexo> getAnexos() {
		return anexos;
	}

	public void setAnexos(List<Anexo> anexos) {
		this.anexos = anexos;
	}

	public Anexo getSelectedAnexo() {
		return selectedAnexo;
	}

	public void setSelectedAnexo(Anexo selectedAnexo) {
		this.selectedAnexo = selectedAnexo;
	}

	public List<SituacionHonorario> getSituacionHonorarios() {
		return situacionHonorarios;
	}

	public void setSituacionHonorarios(
			List<SituacionHonorario> situacionHonorarios) {
		this.situacionHonorarios = situacionHonorarios;
	}

	public List<String> getSituacionHonorariosString() {
		return situacionHonorariosString;
	}

	public void setSituacionHonorariosString(
			List<String> situacionHonorariosString) {
		this.situacionHonorariosString = situacionHonorariosString;
	}

	public List<String> getMonedasString() {
		return monedasString;
	}

	public void setMonedasString(List<String> monedasString) {
		this.monedasString = monedasString;
	}

	public List<String> getTipoHonorariosString() {
		return tipoHonorariosString;
	}

	public void setTipoHonorariosString(List<String> tipoHonorariosString) {
		this.tipoHonorariosString = tipoHonorariosString;
	}

	public List<String> getSituacionCuotasString() {
		return situacionCuotasString;
	}

	public void setSituacionCuotasString(List<String> situacionCuotasString) {
		this.situacionCuotasString = situacionCuotasString;
	}

	public List<String> getRolInvolucradosString() {
		return rolInvolucradosString;
	}

	public void setRolInvolucradosString(List<String> rolInvolucradosString) {
		this.rolInvolucradosString = rolInvolucradosString;
	}

	public List<String> getTipoInvolucradosString() {
		return tipoInvolucradosString;
	}

	public void setTipoInvolucradosString(List<String> tipoInvolucradosString) {
		this.tipoInvolucradosString = tipoInvolucradosString;
	}

	public List<String> getSituacionInculpadosString() {
		return situacionInculpadosString;
	}

	public void setSituacionInculpadosString(
			List<String> situacionInculpadosString) {
		this.situacionInculpadosString = situacionInculpadosString;
	}

	public boolean isReqPenal() {
		return reqPenal;
	}

	public void setReqPenal(boolean reqPenal) {
		this.reqPenal = reqPenal;
	}

	public boolean isReqCabecera() {
		return reqCabecera;
	}

	public void setReqCabecera(boolean reqCabecera) {
		this.reqCabecera = reqCabecera;
	}

	public List<Resumen> getResumens() {
		return resumens;
	}

	public void setResumens(List<Resumen> resumens) {
		this.resumens = resumens;
	}

	public Date getFechaResumen() {
		return fechaResumen;
	}

	public void setFechaResumen(Date fechaResumen) {
		this.fechaResumen = fechaResumen;
	}

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public String getTodoResumen() {
		return todoResumen;
	}

	public void setTodoResumen(String todoResumen) {
		this.todoResumen = todoResumen;
	}

	public Abogado getSelectedAbogado() {
		return selectedAbogado;
	}

	public void setSelectedAbogado(Abogado selectedAbogado) {
		this.selectedAbogado = selectedAbogado;
	}

	public Persona getSelectInvolucrado() {
		return selectInvolucrado;
	}

	public void setSelectInvolucrado(Persona selectInvolucrado) {
		this.selectInvolucrado = selectInvolucrado;
	}

	public int getContadorHonorario() {
		return contadorHonorario;
	}

	public void setContadorHonorario(int contadorHonorario) {
		this.contadorHonorario = contadorHonorario;
	}

	public void setConsultaService(ConsultaService consultaService) {
		this.consultaService = consultaService;
	}

	public void setAbogadoService(AbogadoService abogadoService) {
		this.abogadoService = abogadoService;
	}

	public void setPersonaService(PersonaService personaService) {
		this.personaService = personaService;
	}

	public void setOrganoService(OrganoService organoService) {
		this.organoService = organoService;
	}

	public Resumen getSelectedResumen() {
		return selectedResumen;
	}

	public void setSelectedResumen(Resumen selectedResumen) {
		this.selectedResumen = selectedResumen;
	}

	public List<Ubigeo> getUbigeos() {
		return ubigeos;
	}

	public void setUbigeos(List<Ubigeo> ubigeos) {
		this.ubigeos = ubigeos;
	}

	public int getContadorInvolucrado() {
		return contadorInvolucrado;
	}

	public void setContadorInvolucrado(int contadorInvolucrado) {
		this.contadorInvolucrado = contadorInvolucrado;
	}

	public int getContadorInculpado() {
		return contadorInculpado;
	}

	public void setContadorInculpado(int contadorInculpado) {
		this.contadorInculpado = contadorInculpado;
	}

	public int getContadorCuantia() {
		return contadorCuantia;
	}

	public void setContadorCuantia(int contadorCuantia) {
		this.contadorCuantia = contadorCuantia;
	}

	public int getContadorResumen() {
		return contadorResumen;
	}

	public void setContadorResumen(int contadorResumen) {
		this.contadorResumen = contadorResumen;
	}

	public boolean isFlagDeshabilitadoGeneral() {
		return flagDeshabilitadoGeneral;
	}

	public void setFlagDeshabilitadoGeneral(boolean flagDeshabilitadoGeneral) {
		this.flagDeshabilitadoGeneral = flagDeshabilitadoGeneral;
	}

	public boolean isFlagColumnGeneral() {
		return flagColumnGeneral;
	}

	public void setFlagColumnGeneral(boolean flagColumnGeneral) {
		this.flagColumnGeneral = flagColumnGeneral;
	}

	public boolean isFlagLectResp() {
		return flagLectResp;
	}

	public void setFlagLectResp(boolean flagLectResp) {
		this.flagLectResp = flagLectResp;
	}

	public File getArchivo() {
		return archivo;
	}

	public void setArchivo(File archivo) {
		this.archivo = archivo;
	}

	public String getTxtOrgano() {
		return txtOrgano;
	}

	public void setTxtOrgano(String txtOrgano) {
		this.txtOrgano = txtOrgano;
	}

	public int getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(int idEntidad) {
		this.idEntidad = idEntidad;
	}

	public int getIdUbigeo() {
		return idUbigeo;
	}

	public void setIdUbigeo(int idUbigeo) {
		this.idUbigeo = idUbigeo;
	}

	public String getTxtRegistroCA() {
		return txtRegistroCA;
	}

	public void setTxtRegistroCA(String txtRegistroCA) {
		this.txtRegistroCA = txtRegistroCA;
	}

	public Integer getDNI() {
		return DNI;
	}

	public void setDNI(Integer dNI) {
		DNI = dNI;
	}

	public String getTxtNombre() {
		return txtNombre;
	}

	public void setTxtNombre(String txtNombre) {
		this.txtNombre = txtNombre;
	}

	public String getTxtApePat() {
		return txtApePat;
	}

	public void setTxtApePat(String txtApePat) {
		this.txtApePat = txtApePat;
	}

	public String getTxtApeMat() {
		return txtApeMat;
	}

	public void setTxtApeMat(String txtApeMat) {
		this.txtApeMat = txtApeMat;
	}

	public String getTxtTel() {
		return txtTel;
	}

	public void setTxtTel(String txtTel) {
		this.txtTel = txtTel;
	}

	public String getTxtCorreo() {
		return txtCorreo;
	}

	public void setTxtCorreo(String txtCorreo) {
		this.txtCorreo = txtCorreo;
	}

	public String getTxtTitulo() {
		return txtTitulo;
	}

	public void setTxtTitulo(String txtTitulo) {
		this.txtTitulo = txtTitulo;
	}

	public String getTxtComentario() {
		return txtComentario;
	}

	public void setTxtComentario(String txtComentario) {
		this.txtComentario = txtComentario;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Ubigeo getUbigeo() {
		return ubigeo;
	}

	public void setUbigeo(Ubigeo ubigeo) {
		this.ubigeo = ubigeo;
	}

	public int getIdClase() {
		return idClase;
	}

	public void setIdClase(int idClase) {
		this.idClase = idClase;
	}

	public int getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(int idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public Long getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Long numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public int getIdClase_inclp() {
		return idClase_inclp;
	}

	public void setIdClase_inclp(int idClase_inclp) {
		this.idClase_inclp = idClase_inclp;
	}

	public int getIdTipoDocumento_inclp() {
		return idTipoDocumento_inclp;
	}

	public void setIdTipoDocumento_inclp(int idTipoDocumento_inclp) {
		this.idTipoDocumento_inclp = idTipoDocumento_inclp;
	}

	public Long getNumeroDocumento_inclp() {
		return numeroDocumento_inclp;
	}

	public void setNumeroDocumento_inclp(Long numeroDocumento_inclp) {
		this.numeroDocumento_inclp = numeroDocumento_inclp;
	}

	public String getTxtNombres() {
		return txtNombres;
	}

	public void setTxtNombres(String txtNombres) {
		this.txtNombres = txtNombres;
	}

	public String getTxtNombres_inclp() {
		return txtNombres_inclp;
	}

	public void setTxtNombres_inclp(String txtNombres_inclp) {
		this.txtNombres_inclp = txtNombres_inclp;
	}

	public String getTxtApellidoPaterno_inclp() {
		return txtApellidoPaterno_inclp;
	}

	public void setTxtApellidoPaterno_inclp(String txtApellidoPaterno_inclp) {
		this.txtApellidoPaterno_inclp = txtApellidoPaterno_inclp;
	}

	public String getTxtApellidoMaterno_inclp() {
		return txtApellidoMaterno_inclp;
	}

	public void setTxtApellidoMaterno_inclp(String txtApellidoMaterno_inclp) {
		this.txtApellidoMaterno_inclp = txtApellidoMaterno_inclp;
	}

	public String getTxtApellidoPaterno() {
		return txtApellidoPaterno;
	}

	public void setTxtApellidoPaterno(String txtApellidoPaterno) {
		this.txtApellidoPaterno = txtApellidoPaterno;
	}

	public String getTxtApellidoMaterno() {
		return txtApellidoMaterno;
	}

	public void setTxtApellidoMaterno(String txtApellidoMaterno) {
		this.txtApellidoMaterno = txtApellidoMaterno;
	}

	public String getPretendidoMostrar() {
		return pretendidoMostrar;
	}

	public void setPretendidoMostrar(String pretendidoMostrar) {
		this.pretendidoMostrar = pretendidoMostrar;
	}

	public Integer getCodCliente() {
		return codCliente;
	}

	public void setCodCliente(Integer codCliente) {
		this.codCliente = codCliente;
	}

	public Integer getCodCliente_inclp() {
		return codCliente_inclp;
	}

	public void setCodCliente_inclp(Integer codCliente_inclp) {
		this.codCliente_inclp = codCliente_inclp;
	}

	public boolean isFlagColumnsBtnHonorario() {
		return flagColumnsBtnHonorario;
	}

	public void setFlagColumnsBtnHonorario(boolean flagColumnsBtnHonorario) {
		this.flagColumnsBtnHonorario = flagColumnsBtnHonorario;
	}

	public boolean isFlagColumnGeneralHonorario() {
		return flagColumnGeneralHonorario;
	}

	public void setFlagColumnGeneralHonorario(boolean flagColumnGeneralHonorario) {
		this.flagColumnGeneralHonorario = flagColumnGeneralHonorario;
	}
}

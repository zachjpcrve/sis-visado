package com.hildebrando.visado.mb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsHistSolicitudId;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsMovimientoNivel;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;

/**
 * Clase que se encarga de evaluar los niveles .. contiene metodos de registro, 
 * verificacion de niveles, obtención de datos de delegados, etc
 * @author 
 * */

public class EvaluacionNivelesMB {
	public static Logger logger = Logger.getLogger(EvaluacionNivelesMB.class);
	
	private TiivsSolicitud solicitudRegistrarT;
	private String PERFIL_USUARIO;
	private IILDPeUsuario usuario;	
	private Integer iGrupoDelegados;
	private List<TiivsSolicitudNivel> lstSolicitudNivel;
	private List<TiivsMiembroNivel> lstResponsables;
	private List<TiivsMiembroNivel> lstDelegados;
	
	private String registroUsuario;
	
	public EvaluacionNivelesMB(TiivsSolicitud solicitud){
		this.solicitudRegistrarT = solicitud;
		this.lstResponsables = new ArrayList<TiivsMiembroNivel>();
		this.lstDelegados = new ArrayList<TiivsMiembroNivel>();
		this.lstSolicitudNivel = new ArrayList<TiivsSolicitudNivel>();
		this.usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
		this.PERFIL_USUARIO =(String) Utilitarios.getObjectInSession("PERFIL_USUARIO");
	}
		
	public void registrarEvaluacionNivel(String sCodigoEstadoNivel){
		
		logger.info("**************registrarEvaluacionNivel()*********************");
			
		String sCodUsuario = (String) usuario.getUID();
		TiivsSolicitudNivel solicitudNivel;
		
		String sCodigoEstadoActual = this.solicitudRegistrarT.getEstado().trim();
		String sCodigoEstadoNuevo = sCodigoEstadoNivel;
		String sCodigoSolicitud= this.solicitudRegistrarT.getCodSoli();									
		String sCodNivel ="";
		
		if(!sCodigoEstadoNivel.equals(ConstantesVisado.ESTADOS.ESTADO_COD_APROBADO_T09) && 
				!sCodigoEstadoNivel.equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESAPROBADO_T09)){
			Utilitarios.mensajeInfo("INFO", ConstantesVisado.NIVELES.SELEC_OPCION_CORRECTA);
			logger.info(ConstantesVisado.NIVELES.SELEC_OPCION_CORRECTA+"==> " + sCodigoEstadoNivel);
			return;
		}
		if(!sCodigoEstadoActual.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02) &&
				!sCodigoEstadoActual.equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02)){
			Utilitarios.mensajeInfo("INFO", ConstantesVisado.NIVELES.CAMBIO_EST_NO_PERMITIDO+"de la solicitud.");
			logger.info(ConstantesVisado.NIVELES.CAMBIO_EST_NO_PERMITIDO+"de la solicitud ==>" + sCodigoEstadoActual);		
			return;
		}	
		
		if(!(PERFIL_USUARIO.equals(ConstantesVisado.SSJJ) || PERFIL_USUARIO.equals(ConstantesVisado.OFICINA))){ //confirmar que solo los usuarios de SSJJ pueden ser d|r
			Utilitarios.mensajeInfo("INFO", ConstantesVisado.NIVELES.CAMBIO_EST_NO_PERMITIDO+"para el perfil");
			logger.info(ConstantesVisado.NIVELES.CAMBIO_EST_NO_PERMITIDO+"para el perfil");		
			return;
		}	
		
		//llena variable: lstSolicitudNivel
		solicitudNivel = obtenerNivelSolicitud();
		if(solicitudNivel!=null){
			sCodNivel = solicitudNivel.getCodNiv().trim();
		}

		logger.info("Nivel: " + sCodNivel);
							
		if(sCodNivel==null || sCodNivel.equals("")){
			Utilitarios.mensajeInfo("INFO", ConstantesVisado.NIVELES.SOLICIT_SIN_NIVEL+"de evaluación");
			logger.info(ConstantesVisado.NIVELES.SOLICIT_SIN_NIVEL+"de evaluación, no se permite realizar cambios");
			return;
		}	
		
		//llena variables: lstResponsables,lstDelegados
		obtenerDatosDelegadosResponsables(solicitudNivel);
		List<Integer> lstGrupo = obtenerGrupoEvaluador(solicitudNivel);
		
		StringBuilder sb = new StringBuilder();
		for(Integer iGrupo : lstGrupo){
			sb.append(iGrupo + ",");
		}
		
		if(logger.isInfoEnabled()){
			logger.info("Solicitud: " + sCodigoSolicitud);			
			logger.info("lstGrupo: " + lstGrupo.size());
			logger.info("Grupo evaluador: " + sb);
			logger.info("Responsables: " + lstResponsables.size());
			logger.info("Delegados: " + lstDelegados.size());
			logger.info("sCodUsuario: " + sCodUsuario);
		}		
		
		boolean bRegistro = false;
		
		try{
			if (usuarioEsMiembroDe(sCodUsuario, lstResponsables, null)) {
				modificarEstadoSolicitudNivel(solicitudNivel,sCodigoEstadoNuevo, sCodUsuario, ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE);
				bRegistro = true;
			} else if (usuarioEsMiembroDe(sCodUsuario, lstDelegados,lstGrupo)) {
				registrarEstadoMovimientoNivel(solicitudNivel,sCodigoEstadoNuevo, sCodUsuario);
				//Para obtener el grupo evaluador una vez se registre el estado nivel
				if(lstGrupo.size()==0){
					lstGrupo = obtenerGrupoEvaluador(solicitudNivel);
				}
				if (verificarCalificacionPorDelegados(solicitudNivel,sCodigoEstadoNuevo, lstGrupo)) {
					String registro = iGrupoDelegados != null ? iGrupoDelegados.toString() : null;
					logger.info("Registro Estado Solicitud Nivel: "+ registro);
					modificarEstadoSolicitudNivel(solicitudNivel,sCodigoEstadoNuevo, registro, ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO);
				}
				bRegistro = true;
			} else {
				bRegistro = false;
				logger.info(ConstantesVisado.NIVELES.YA_EXISTE_GRUP_EVAL_ACCION_NO_PERMIT);
			}
													
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION+"al registrar: ", e);
		}
		
		if(bRegistro){
			Utilitarios.mensajeInfo("INFO", ConstantesVisado.NIVELES.EVALUAC_REGISTRAD_OK);
		} else {
			Utilitarios.mensajeInfo("INFO", ConstantesVisado.NIVELES.ACCION_NO_PERMITIDA+": " + this.usuario.getUID());
		}
		logger.info("***********registrarEvaluacionNivel():FIN ****************");		
	}
	
	public TiivsSolicitudNivel obtenerNivelSolicitud(){
		
		this.lstSolicitudNivel = new ArrayList<TiivsSolicitudNivel>();
		TiivsSolicitudNivel solNivel = null;
		String sCodEstadoSolicitud = null;
		try {
			sCodEstadoSolicitud = this.solicitudRegistrarT.getEstado().trim();
			GenericDao<TiivsSolicitudNivel, Object> soliNivelDAO = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroNivel = Busqueda.forClass(TiivsSolicitudNivel.class);
			filtroNivel.add(Restrictions.eq("tiivsSolicitud", this.solicitudRegistrarT));
			filtroNivel.add(Restrictions.eq("estadoSolicitud", sCodEstadoSolicitud));
			filtroNivel.addOrder(Order.asc("codNiv"));
			this.lstSolicitudNivel = soliNivelDAO.buscarDinamico(filtroNivel);
			
			for(TiivsSolicitudNivel n : this.lstSolicitudNivel){
				if(n.getEstadoNivel()==null || 
						n.getEstadoNivel().trim().equals("") || 
						n.getEstadoNivel().equals(ConstantesVisado.ESTADOS.ESTADO_COD_Pendiente_T09)){
					solNivel = n;				
					break;
				}					
			} 
		} catch (Exception e) {
			this.lstSolicitudNivel = null;
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener datos de nivel: "+e);
		}        
		return solNivel;
	}
	
	private void obtenerDatosDelegadosResponsables(TiivsSolicitudNivel solicitudNivel){
		
		List<TiivsMiembroNivel> lstMiembroNivel;
		lstMiembroNivel = new ArrayList<TiivsMiembroNivel>();
		this.lstResponsables = new ArrayList<TiivsMiembroNivel>();
		this.lstDelegados = new ArrayList<TiivsMiembroNivel>();
		String sCodNivel=null;
		if(solicitudNivel!=null){
			sCodNivel = solicitudNivel.getCodNiv();
		}
		
		try {
			GenericDao<TiivsMiembroNivel, Object> miembroNivelDAO = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);
			filtroMiembroNivel.add(Restrictions.eq("codNiv", sCodNivel));
			filtroMiembroNivel.add(Restrictions.eq("estado", "1"));
			filtroMiembroNivel.add(Restrictions.eq("estadoMiembro", "1"));
			lstMiembroNivel = miembroNivelDAO.buscarDinamico(filtroMiembroNivel);
		} catch (Exception e) {
			//lstMiembroNivel = null;
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener datos de los Responsables:" + e);
		}        
		
		for(TiivsMiembroNivel m : lstMiembroNivel){
			if(m.getTipoRol().equalsIgnoreCase(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE)){
				this.lstResponsables.add(m);
			}
			if(m.getTipoRol().equalsIgnoreCase(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO)){
				this.lstDelegados.add(m);
			}
				
		}        
	}

	private List<Integer> obtenerGrupoEvaluador(TiivsSolicitudNivel tiivsSolicitudNivel) {
		List<TiivsMovimientoNivel> lstMovimientoNivel = new ArrayList<TiivsMovimientoNivel>();
		List<Integer> lstGrupo = new ArrayList<Integer>();
		try {
			GenericDao<TiivsMovimientoNivel, Object> moviNivelDAO = (GenericDao<TiivsMovimientoNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroNivel = Busqueda.forClass(TiivsMovimientoNivel.class);
			filtroNivel.add(Restrictions.eq("tiivsSolicitudNivel", tiivsSolicitudNivel));
			lstMovimientoNivel = moviNivelDAO.buscarDinamico(filtroNivel);			
			int iGrupo=0;
			for(TiivsMovimientoNivel m : lstMovimientoNivel){
				if(iGrupo!=m.getGrupo()){
					iGrupo = m.getGrupo();
					lstGrupo.add(iGrupo);
				}
			}						
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener obtenerGrupoEvaluador: ",e);
		}
		return lstGrupo;
	}
	
	private boolean usuarioEsMiembroDe(String sCodUsuario, List<TiivsMiembroNivel> lstMiembros, List<Integer> lstGrupo){
		if(sCodUsuario == null){
			return false;
		}
		for(TiivsMiembroNivel res : lstMiembros){
			if(res.getTiivsMiembro()!=null && res.getTiivsMiembro().getCodMiembro().equals(sCodUsuario.trim())){
				if(lstGrupo==null || lstGrupo.size()==0){
					return true;
				} else {		
					for(Integer iGrupo : lstGrupo){
						if(res.getGrupo().equals(iGrupo)){
							return true;
						} 
					}	
				}				
			}
		}
		return false;
	}
	
	
	private void modificarEstadoSolicitudNivel(TiivsSolicitudNivel tiivsSolicitudNivel,String sCodigoEstado,String sRegistro, String sRolNivel) throws Exception{
		
		logger.info("*********************** modificarEstadoSolicitudNivel **************************");

		tiivsSolicitudNivel.setEstadoNivel(sCodigoEstado);
		tiivsSolicitudNivel.setUsuarioRegistro(sRegistro);
		tiivsSolicitudNivel.setFechaRegistro(new Timestamp((new Date().getTime())));
		
		GenericDao<TiivsSolicitudNivel, Object> service = (GenericDao<TiivsSolicitudNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		service.modificar(tiivsSolicitudNivel);
				
		if(sRolNivel.trim().equals(ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_RESPONSABLE)){
			registrarHistorialSolicitud(this.solicitudRegistrarT, tiivsSolicitudNivel.getCodNiv(), sCodigoEstado, sRolNivel);//Registro de Historial
		}

		if(sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESAPROBADO_T09)){
			modificarEstadoSolicitud(this.solicitudRegistrarT,sCodigoEstado);
		} else if(sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_APROBADO_T09)){						
			if(verificarCalificacionPorNiveles(sCodigoEstado)){
				modificarEstadoSolicitud(this.solicitudRegistrarT,sCodigoEstado);
			}		
		}		
		
	}
	
	private void registrarEstadoMovimientoNivel(TiivsSolicitudNivel tiivsSolicitudNivel, String sCodigoEstado,String sCodUsuario){
		
		List<TiivsMovimientoNivel> lstMovimientoNivel = new ArrayList<TiivsMovimientoNivel>();
		TiivsMovimientoNivel movimientoNivel;
		
		try {
			GenericDao<TiivsMovimientoNivel, Object> moviNivelDAO = (GenericDao<TiivsMovimientoNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroNivel = Busqueda.forClass(TiivsMovimientoNivel.class);
			filtroNivel.add(Restrictions.eq("tiivsSolicitudNivel", tiivsSolicitudNivel));
			filtroNivel.add(Restrictions.eq("usuarioRegistro", sCodUsuario));
			lstMovimientoNivel = moviNivelDAO.buscarDinamico(filtroNivel);
			
			if(lstMovimientoNivel.size()>0){	//...Si ya existe movimiento modificar							
				for(TiivsMovimientoNivel m : lstMovimientoNivel){
					m.setEstado(sCodigoEstado);
					m.setFechaRegistro(new Timestamp(new Date().getTime()));
					moviNivelDAO.modificar(m);					
				}				
			} else {							//.. Si no insertar nuevo movimiento
				for(TiivsMiembroNivel m : this.lstDelegados){ //Insertar tantos movimientos como facultades tenga el usuario
					if(m.getTiivsMiembro().getCodMiembro().equals(sCodUsuario)){						
						movimientoNivel = new TiivsMovimientoNivel();
						movimientoNivel.setTiivsSolicitudNivel(tiivsSolicitudNivel);
						movimientoNivel.setEstado(sCodigoEstado);
						movimientoNivel.setGrupo(m.getGrupo());
						movimientoNivel.setUsuarioRegistro(m.getTiivsMiembro().getCodMiembro());
						movimientoNivel.setFechaRegistro(new Timestamp((new Date()).getTime()));
						moviNivelDAO.insertar(movimientoNivel);
					}
				}							
			}	
			
			this.registrarHistorialSolicitud(this.solicitudRegistrarT, tiivsSolicitudNivel.getCodNiv(), sCodigoEstado, ConstantesVisado.CODIGO_CAMPO_TIPO_ROL_DELEGADO);
			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al registrarEstadoMovimientoNivel: " + e);
		}
	}
	
	private boolean verificarCalificacionPorDelegados(TiivsSolicitudNivel tiivsSolicitudNivel, String sCodigoEstado, List<Integer> lstGrupo){				
		List<TiivsMovimientoNivel> lstMovimientoNivel = new ArrayList<TiivsMovimientoNivel>();
		try {
			GenericDao<TiivsMovimientoNivel, Object> movimientoNivelDAO = (GenericDao<TiivsMovimientoNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsMovimientoNivel.class);
			filtro.add(Restrictions.eq("tiivsSolicitudNivel", tiivsSolicitudNivel));
			lstMovimientoNivel = movimientoNivelDAO.buscarDinamico(filtro);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener datos de MovimientosPorNivel :"+ e);
		}
		
		int cont;
		int nroDelegados;
		
		for(Integer iGrupo : lstGrupo){
			cont=0;
			for(TiivsMovimientoNivel movNivel : lstMovimientoNivel){
				if(movNivel.getGrupo().equals(iGrupo) && movNivel.getEstado().equals(sCodigoEstado)){ //movimiento nivel x grupo
					cont++;
				}
			}
			nroDelegados=obtenerNroDelegados(lstDelegados,iGrupo);
			if(cont==nroDelegados && nroDelegados > 0){
				this.iGrupoDelegados = iGrupo;
				return true;				
			}
		}
		return false;
	}

	public void registrarHistorialSolicitud(TiivsSolicitud solicitud, String sCodNivel, String sCodEstadoNivel, String sCodRolNivel) throws Exception{
		logger.info("******************registrarHistorialSolicitud()*****************************");
		Timestamp tsFechaRegistro = new Timestamp(new Date().getTime());
		String sRegUsuario = this.usuario.getUID();
		String sNomUsuario = this.usuario.getNombre() + ' ' + this.usuario.getApellido1() + ' '	+ this.usuario.getApellido2();
		String sCodSoli = solicitud.getCodSoli();
		String sEstado = solicitud.getEstado();		
		String sNivel = sCodNivel;
		String sEstadoNivel = sCodEstadoNivel;
		String sRolNivel = sCodRolNivel;
		String sObs = solicitud.getObs();
		
		if(logger.isInfoEnabled()){
			logger.info("Registro:" + sRegUsuario);
			logger.info("sNomUsuario:" + sNomUsuario);
			logger.info("sCodSoli:" + sCodSoli);
			logger.info("sEstado:" + sEstado);
			logger.info("sNivel:" + sNivel);
			logger.info("sEstadoNivel:" + sEstadoNivel);
			logger.info("sRolNivel:" + sRolNivel);
			logger.info("tsFechaRegistro:" + tsFechaRegistro);
			logger.info("sObs:" + sObs);
			logger.info("******************registrarHistorialSolicitud()*****************************");
		}
		
		SolicitudDao<String, Object> serviceMaxMovi = (SolicitudDao<String, Object>) SpringInit.getApplicationContext().getBean("solicitudEspDao");
		String numeroMovimiento = serviceMaxMovi.obtenerMaximoMovimiento(solicitud.getCodSoli());
		 
		int num = 0;
		if (!numeroMovimiento.equals("")) {
			num = Integer.parseInt(numeroMovimiento) + 1;
		} else {
			num = 1;
		}
		numeroMovimiento = num + "";
		logger.info("Numero de Movimiento a registrar para el CodSolicitud : " + solicitud.getCodSoli() + " is :" + numeroMovimiento);
	 
		TiivsHistSolicitud objHistorial = new TiivsHistSolicitud();
		objHistorial.setId(new TiivsHistSolicitudId(sCodSoli, numeroMovimiento));
		objHistorial.setEstado(sEstado);
		objHistorial.setNomUsuario(sNomUsuario);
		objHistorial.setRegUsuario(sRegUsuario);
		objHistorial.setFecha(tsFechaRegistro);
		objHistorial.setNivel(sNivel);
		objHistorial.setNivelEstado(sEstadoNivel);
		objHistorial.setNivelRol(sRolNivel);
		objHistorial.setObs(sObs);
		GenericDao<TiivsHistSolicitud, Object> serviceHistorialSolicitud = (GenericDao<TiivsHistSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		serviceHistorialSolicitud.insertar(objHistorial);
	}
	
	private void modificarEstadoSolicitud(TiivsSolicitud tiivsSolicitud, String sCodigoEstado) throws Exception {
				
		if(sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_APROBADO_T09)){
			if(tiivsSolicitud.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){
				sCodigoEstado = ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02;
			} else if(tiivsSolicitud.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02)){
				sCodigoEstado = ConstantesVisado.ESTADOS.ESTADO_COD_PROCEDENTE_T02;
			}
			
		} else if (sCodigoEstado.equals(ConstantesVisado.ESTADOS.ESTADO_COD_DESAPROBADO_T09)){
			if(tiivsSolicitud.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02)){
				sCodigoEstado = ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02;
			} else if(tiivsSolicitud.getEstado().equals(ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_B_T02)){
				sCodigoEstado = ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02;
			}
		}					
		
		String sCodUsuario = usuario.getUID();		
		tiivsSolicitud.setEstado(sCodigoEstado);
		tiivsSolicitud.setFechaEstado(new Timestamp((new Date().getTime())));
		tiivsSolicitud.setRegUsuario(sCodUsuario);//validar con diego		
		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		service.modificar(tiivsSolicitud);
		
		//registrarHistorial(tiivsSolicitud);		
		this.registrarHistorialSolicitud(this.solicitudRegistrarT, null, null, null);
		
	}	
	
	private boolean verificarCalificacionPorNiveles(String sEstado){
		boolean iRet = false;
		int i=0;
		for(TiivsSolicitudNivel sn : lstSolicitudNivel){
			if(sn.getEstadoNivel()!=null && sn.getEstadoNivel().equals(sEstado)){
				i++;
			}
		}
		if(i==lstSolicitudNivel.size()){
			iRet = true;
		} 
		return iRet;
	}
	
	private int obtenerNroDelegados(List<TiivsMiembroNivel> lstDelegados, Integer iGrupo) {
		int cont=0;
		for(TiivsMiembroNivel m : lstDelegados){
			if(m.getGrupo().equals(iGrupo)){
				cont++;
			}
		}
		return cont;
	}
	
	public String getRegistroUsuario() {
		return registroUsuario;
	}

	public void setRegistroUsuario(String registroUsuario) {
		this.registroUsuario = registroUsuario;
	}
	
	
}

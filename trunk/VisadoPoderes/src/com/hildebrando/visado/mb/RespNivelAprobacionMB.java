package com.hildebrando.visado.mb;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.event.RowEditEvent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.GrupoDto;
import com.hildebrando.visado.dto.MiembroNivelDTO;
import com.hildebrando.visado.dto.NivelDto;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMiembroNivel;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.service.DelegadosService;
import com.hildebrando.visado.service.RespNivelAprobacionService;

/**
 * Clase que maneja el mantenimiento de Responsable Nivel Aprobacion, contiene la 
 * busqueda, edidion, etc.
 * @author jsaldana
 * **/

@ManagedBean(name = "respNivel")
@SessionScoped
public class RespNivelAprobacionMB {
	
	public static Logger logger = Logger.getLogger(RespNivelAprobacionMB.class);
	private DelegadosService delegadosService;
	private MiembroNivelDTO miembroNivelDto;
	private String codNivel;
	private RespNivelAprobacionService respNivelAprobacionService;
	private List<MiembroNivelDTO> respNiveles;
	private boolean flagVisible;
    private MiembroNivelDTO miembroCapturado;
    private MiembroNivelDTO  miembroCapturado_Edit;
	private List<TiivsMiembroNivel> list;
	private String codRegistro;
	private List<TiivsMiembro> miembros;

	private boolean validarCodRegistro;
	private String iniciar;
	private TiivsMiembroNivel miembroNivel;
	private GenericDao<TiivsNivel, Object> serviceTiivsNivel;
	private String[] estados;
	private boolean bEditar=true;
	private boolean bFlagDisabled;

	private List<GrupoDto> grupos;
	public List<GrupoDto> getGrupos() {
		return this.grupos;
	}

	public void setGrupos(List<GrupoDto> grupos) {
		this.grupos = grupos;
	}

	private List<NivelDto> niveles;
	private boolean limpiar;
		
	public RespNivelAprobacionMB(){
		delegadosService = new DelegadosService();
		respNivelAprobacionService = new RespNivelAprobacionService();
		miembroNivelDto= new MiembroNivelDTO();
		miembroNivel = new TiivsMiembroNivel();
		grupos = new ArrayList<GrupoDto>();
		niveles = new ArrayList<NivelDto>();
		respNiveles = new ArrayList<MiembroNivelDTO>();
		cargarCombos();
		limpiar = false;	
	}
	
	private void cargarCombos(){
		//Grupos
		GenericDao<TiivsGrupo, Object> serviceTiivsGrupo = (GenericDao<TiivsGrupo, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsGrupo = Busqueda.forClass(TiivsGrupo.class);		
		try {
			List<TiivsGrupo> tiivsGrupos = serviceTiivsGrupo.buscarDinamico(filtroTiivsGrupo);
			for(TiivsGrupo tiivsGrupo: tiivsGrupos){				
				grupos.add(new GrupoDto(tiivsGrupo.getCodGrupo(),tiivsGrupo.getDesGrupo()));
			}			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+"de Grupos: "+e);
		}
		
		//Niveles
		GenericDao<TiivsNivel, Object> serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroTiivsNivel = Busqueda.forClass(TiivsNivel.class).setProjection(Projections.distinct(Projections.property("codNiv")));
		try {
			List<String> tiivsNivels = serviceTiivsNivel.buscarDinamicoString(filtroTiivsNivel);
			for(String s:tiivsNivels){
				Busqueda filtroTiivsNivel2 = Busqueda.forClass(TiivsNivel.class);
				List<TiivsNivel>  list= serviceTiivsNivel.buscarDinamico(filtroTiivsNivel2.add(Restrictions.eq("codNiv", s)));
				String des= list.get(0).getDesNiv();
				
				niveles.add(new NivelDto(s,des));
			}			
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+"de niveles: "+e);
		}
	}
	
/*	public void editarRespNivelAprob(){
		logger.debug("=== inicia editarRespNivelAprob() ====");
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		String idResp;
		String editar;
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		idResp = params.get("idResp");
		editar = params.get("editar");
		TiivsMiembroNivel miembroNivel= new TiivsMiembroNivel();
		Busqueda filtroMiembro= Busqueda.forClass(TiivsMiembroNivel.class);
		filtroMiembro.add(Restrictions.eq("tiivsMiembro.codMiembro", idResp));
		filtroMiembro.add(Restrictions.eq("tipoRol", "R"));
		try {
			miembroNivel= serviceTiivsMiembroNivel.buscarById(TiivsMiembroNivel.class, Integer.parseInt(idResp));
			logger.info("miembroNivel.toString() :::: "+miembroNivel.toString());
		} catch (NumberFormatException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al formatear: "+e);
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar miembroNivel: "+e);
		}
		
		ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
		ec.getSessionMap().put("miembroNivel", miembroNivel);
		 ec.getSessionMap().put("editar", editar);
		try {
			ec.redirect("newEditRespNivel.xhtml?faces-redirect=true");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel: "+e);
		}
		logger.debug("=== saliendo de editarRespNivelAprob() ====");
		//setLimpiar(false);
	}
	*/
	public void nuevoRespxNivel(){
		logger.debug("=== inicia nuevoRespxNivel() ====");
		//TiivsMiembroNivel miembroNivel= new TiivsMiembroNivel();
		
		miembroNivel= new TiivsMiembroNivel();
		bEditar=false;
		/*ec.getSessionMap().put("miembroNivel", miembroNivel);*/
		 
		try{
			this.setLimpiar(true);
			ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("paginas/newEditRespNivel.xhtml?faces-redirect=true");
			logger.debug("=== bEditar ==== ::: "+bEditar);
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel.xhtml: "+e);
		}
		logger.debug("=== saliendo nuevoRespxNivel() ====");
	}
	
	public void listarRespxNivel(){
		logger.debug("=== inicia listarRespxNivel() ====");
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsMiembro, Object> serviceTiivsMiembro = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		Busqueda filtroTiivsMiembroNivel = Busqueda.forClass(TiivsMiembroNivel.class);
		//Se consulta los responsables por nivel en base al filtroTiivsMiembroNivel 
		filtroTiivsMiembroNivel.add(Restrictions.eq("tipoRol", "R"));
		filtroTiivsMiembroNivel.createAlias("tiivsMiembro", "miemb");
		
		List<TiivsMiembroNivel> list= new ArrayList<TiivsMiembroNivel>();
		if(miembroNivelDto!=null&&miembroNivelDto.getCodGrupo()!=null&&miembroNivelDto.getEstado()!=null){
			
		
		if(!miembroNivelDto.getRegistro().equals("")){
			logger.debug("[BUSQ]-REGISTRO: "+miembroNivelDto.getRegistro().toUpperCase());
			filtroTiivsMiembroNivel.add(Restrictions.eq("miemb.codMiembro", miembroNivelDto.getRegistro().toUpperCase()));
		}
		
		if(!miembroNivelDto.getDescripcion().equals("")){
			logger.debug("[BUSQ]-DESCRIPCION: "+miembroNivelDto.getDescripcion());
			filtroTiivsMiembroNivel.add(Restrictions.like("miemb.descripcion", "%"+miembroNivelDto.getDescripcion().toUpperCase()+"%"));
			//filtroTiivsMiembroNivel.add(Restrictions. like("miemb.descripcion", miembroNivelDto.getDescripcion()));
		}
		
		if((!miembroNivelDto.getEstado().equals("")) && miembroNivelDto.getEstado().compareTo("-1") != 0 ){
			logger.debug("[BUSQ]-ESTADO: "+miembroNivelDto.getEstado());
			filtroTiivsMiembroNivel.add(Restrictions.eq("estado", miembroNivelDto.getEstado()));
		}
		

	    if(miembroNivelDto.getCodGrupo() != "" && miembroNivelDto.getCodGrupo().compareTo("-1") != 0 ){
			logger.debug("[BUSQ]-CODGRUPO: "+miembroNivelDto.getCodGrupo());
			Busqueda filtroTiivsMiembro= Busqueda.forClass(TiivsMiembro.class);
			filtroTiivsMiembro.createAlias("tiivsGrupo", "grupo");
			filtroTiivsMiembro.add(Restrictions.eq("grupo.codGrupo", miembroNivelDto.getCodGrupo()));
			List<TiivsMiembro> miembros= new ArrayList<TiivsMiembro>();
			try {
				miembros = serviceTiivsMiembro.buscarDinamico(filtroTiivsMiembro);
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener miembros: "+e);
			}
			
			List<String> codigos= new ArrayList<String>();
			for(TiivsMiembro tiivsMiembro: miembros){
				codigos.add(tiivsMiembro.getCodMiembro());
			logger.info("codigos filtrar" + codigos);
			}
			filtroTiivsMiembroNivel.add(Restrictions.in("miemb.codMiembro", codigos));
			
		}
		
		if(miembroNivelDto.getCodNivel() != "" && miembroNivelDto.getCodNivel().compareTo("-1") != 0 ){
			logger.debug("[BUSQ]-CODNIVEL: "+miembroNivelDto.getCodNivel());
			filtroTiivsMiembroNivel.add(Restrictions.eq("codNiv", miembroNivelDto.getCodNivel()));
		}
		
		}
	
		
		try {
			list = serviceTiivsMiembroNivel.buscarDinamico(filtroTiivsMiembroNivel);
		
		
		respNiveles = new ArrayList<MiembroNivelDTO>();
		
		if(list!=null){
			logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"Responsables por Nivel es ["+list.size()+"]  ." );
		}
		
		for(TiivsMiembroNivel  e:list){
			String descEstado="" ;
			String desNivel="";
			
			descEstado = Utilitarios.obternerDescripcionEstado(e.getEstado());
			desNivel = respNivelAprobacionService.obtenerDesNivel(e.getCodNiv());
			
			logger.info("Data a mostrar :: " +e.toString());
			
			respNiveles.add(new MiembroNivelDTO(e.getId(), e.getCodNiv(),desNivel,e.getTiivsMiembro().getCodMiembro(),e.getTiivsMiembro().getDescripcion(),e.getTiivsMiembro().getTiivsGrupo().getCodGrupo(),
					e.getTiivsMiembro().getTiivsGrupo().getDesGrupo(),e.getFechaRegistro().toString(),e.getUsuarioRegistro(),(e.getEstado()==null?"":e.getEstado()),descEstado));
		}
		if(respNiveles!=null && respNiveles.size()>0){
			for(int i=0;i <=respNiveles.size(); i++ ){
				if(logger.isDebugEnabled()){
					//logger.debug("==>  ID:"+respNiveles.get(i).getId()+"  Registro:"+respNiveles.get(i).getRegistro());
				}
			}	
		}
		
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al consultar Responsables por Nivel: "+e);
		}
		logger.debug("=== saliendo de listarRespxNivel() ===");
	}
	public List<TiivsNivel> listarNivelesXusuario(String codNiv){
		GenericDao<TiivsNivel, Object> serviceTiivsNivel = (GenericDao<TiivsNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");

		List<TiivsNivel> tiivsNivels = new ArrayList<TiivsNivel>();
		Busqueda filtroTiivsNivelMoneda = Busqueda.forClass(TiivsNivel.class);
		try {
			tiivsNivels = serviceTiivsNivel.buscarDinamico(filtroTiivsNivelMoneda.add(Restrictions.eq("codNiv",codNiv)));
		} catch (Exception e1) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+ "al obtener nivelesMonedas: " + e1);
		}
    return tiivsNivels;
	}
	//@Autor Samira
	public void listarNivelesPorResponsable(String idResp)  throws Exception {
		logger.info("*********************** listarNivelesPorResponsable ********************** ");
		    GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		logger.info("idResp " +idResp);
			List<TiivsMiembroNivel> listaDeMiembrosNivel= new ArrayList<TiivsMiembroNivel>();
			Busqueda filtroMiembro= Busqueda.forClass(TiivsMiembroNivel.class);
			if(idResp!=""||idResp!=null){
			filtroMiembro.add(Restrictions.eq("tiivsMiembro.codMiembro", idResp));
			}
			filtroMiembro.add(Restrictions.eq("tipoRol", "R"));
			listaDeMiembrosNivel=serviceTiivsMiembroNivel.buscarDinamico(filtroMiembro);
			logger.info("Tamanio de la lista de miembro " +listaDeMiembrosNivel.size());
			
			int ris = 0, rfs = 0;
			int rid = 0, rfd = 0;
			int rie = 0, rfe = 0;
			
			respNiveles=new ArrayList<MiembroNivelDTO>();
			for(TiivsMiembroNivel  e:listaDeMiembrosNivel){
				String descEstado="" ;
				String desNivel="";
				
				descEstado = Utilitarios.obternerDescripcionEstado(e.getEstado());
				desNivel = respNivelAprobacionService.obtenerDesNivel(e.getCodNiv());
					
				List<TiivsNivel> listaNiveles =listarNivelesXusuario(e.getCodNiv());
				
				for (TiivsNivel nivel : listaNiveles) {
					if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_SOLES) == 0) {
						ris = nivel.getRangoInicio();rfs = nivel.getRangoFin();}
					if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_DOLAR) == 0) {
						rid = nivel.getRangoInicio();rfd = nivel.getRangoFin();}
					if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_EUROS) == 0) {
						rie = nivel.getRangoInicio();rfe = nivel.getRangoFin();}
				}
				if(e.getEstado().equals("1")){
					e.setLabelAccion("Inactivar");
				}else if(e.getEstado().equals("0")){
					e.setLabelAccion("Activar");
				}
				respNiveles.add(new MiembroNivelDTO(0, e.getId(),
						e.getCodNiv(), desNivel, e.getTiivsMiembro().getCodMiembro(),
						e.getTiivsMiembro().getDescripcion(), e.getTiivsMiembro().getTiivsGrupo().getCodGrupo(), 
						e.getTiivsMiembro().getTiivsGrupo().getDesGrupo(),
						e.getFechaRegistro().toString(),
						e.getUsuarioRegistro(), e.getEstado(), descEstado, ris,rfs, rid, rfd, rie, rfe,e.getLabelAccion()));
			}
			
			
	}
	private boolean  validarRegistroResponsableNivel(){
		boolean retorno=true;
		if(miembroNivelDto.getRegistro().equals("")||miembroNivelDto.getRegistro()==null){
			retorno=false;
			Utilitarios.mensajeError("Info", "No se encontro al usuario, ingrese uno nuevo");
		}
		else if (miembroNivelDto.getEstado().equals("-1")) {
			retorno=false;
			Utilitarios.mensajeError("Info", "Debe seleccionar el estado");
		} else {
			if (codNivel.equals("-1")) {
				retorno=false;
				Utilitarios.mensajeError("Info", "Debe seleccionar un nivel");
			}
		}
		return retorno;
	}
	private boolean validarNivelPorPersona(String codNiv){
		boolean retorno=true;
		for (MiembroNivelDTO x : respNiveles) {
			if(codNiv.equals(x.getCodNivel())){
				Utilitarios.mensajeInfo("Info", "Nivel ya se encuentra en la lista, agregue uno nuevo");
				retorno=false;
				break;
			}
			
		}
	 return	retorno;
	}
	private  void nuevoResponsable(){
		if(validarNivelPorPersona(getCodNivel())){
			IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios.getObjectInSession("USUARIO_SESION");
			logger.info(" ************** Creando nuevo Responsable ************"  );
			int ris = 0, rfs = 0;
			int rid = 0, rfd = 0;
			int rie = 0, rfe = 0;
			TiivsMiembroNivel miembroNivel = new TiivsMiembroNivel();
			TiivsMiembro miembro = new TiivsMiembro();
			logger.info("miembroNivelDto.getRegistro() " +miembroNivelDto.getRegistro());
			miembro.setCodMiembro(miembroNivelDto.getRegistro());
			miembro.setDescripcion(miembroNivelDto.getDescripcion());
			miembroNivel.setTiivsMiembro(miembro);
			miembroNivel.setCodNiv(getCodNivel());
			miembroNivel.setTipoRol("R");
			miembroNivel.setEstado(miembroNivelDto.getEstado());
			miembroNivel.setDescNiv(respNivelAprobacionService.obtenerDesNivel(miembroNivel.getCodNiv()));
			miembroNivel.setDescEstado(Utilitarios.obternerDescripcionEstado(miembroNivel.getEstado()));
			List<TiivsNivel> tiivsNivels=this.listarNivelesXusuario(miembroNivel.getCodNiv());
			miembroNivel.setListaNiveles(tiivsNivels);
			for (TiivsNivel nivel : tiivsNivels) {
				if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_SOLES) == 0) {
					ris = nivel.getRangoInicio();rfs = nivel.getRangoFin();}
				if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_DOLAR) == 0) {
					rid = nivel.getRangoInicio();rfd = nivel.getRangoFin();}
				if (nivel.getMoneda().compareTo(ConstantesVisado.MONEDAS.COD_EUROS) == 0) {
					rie = nivel.getRangoInicio();rfe = nivel.getRangoFin();}
			}
			respNiveles.add(new MiembroNivelDTO(1, miembroNivel.getTiivsMiembro().getDescripcion(),
					   miembroNivel.getCodNiv(), miembroNivel.getDescNiv(),
					   miembroNivel.getTiivsMiembro().getCodMiembro(),
					   Utilitarios.formatoFecha(new Date()) ,  usuario.getUID(),
					   miembroNivel.getEstado(), miembroNivel.getDescEstado(), ris, rfs, rid, rfd, rie, rfe));
		}
		
		//return miembroNivel;
	}
	//@Autor Samira
	public void agregarlistarRespxNivel()  {
		logger.debug("=== inicia agregarlistarRespxNivel() ====");
		if(validarRegistroResponsableNivel()){
			try {
				nuevoResponsable();
			//	listarNivelesPorResponsable(miembroNivel.getTiivsMiembro().getCodMiembro());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//@Autor Samira
	public void editarRespNivelAprob(){
		logger.debug("=== inicia editarRespNivelAprob() ====");
		try {
	/*	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String idResp = params.get("idResp");
		//listarNivelesPorResponsable(idResp);
*/		ExternalContext ec=  FacesContext.getCurrentInstance().getExternalContext();
			            ec.redirect("newEditRespNivel.xhtml?faces-redirect=true");
		} catch (IOException e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel: "+e);
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al redireccionar newEditRespNivel: "+e);
		}
		logger.debug("=== saliendo de editarRespNivelAprob() ====");
	}
	
	public void obtenerDatosMiembro() {
		logger.info("EditRespNivelAprobacionMB : obtenerDatosMiembro");
		try {
			if (!miembroNivelDto.getRegistro().equals("")) {
				logger.info("----------------- Entro con Registro no vacio");
				miembros = delegadosService.obtenerDatosMiembro(miembroNivelDto.getRegistro().toUpperCase());
				if (miembros.size() > 0) {

					miembroNivelDto.setDescripcion(miembros.get(0).getDescripcion());
					miembroNivelDto.setDesGrupo(miembros.get(0).getTiivsGrupo().getDesGrupo());
					// criterioRegistro = miembros.get(0).getCriterio();
					validarCodRegistro = true;
					listarNivelesPorResponsable(miembroNivelDto.getRegistro().toUpperCase());
					//listarRespxNivel();
				} else {
					Utilitarios.mensajeInfo("Info","No se encuentra Registrado el código del Responsable");
					/*
					 * desRegistro = ""; perfilRegistro = ""; criterioRegistro =
					 * "";
					 */
					miembroNivelDto.setDescripcion("");
					miembroNivelDto.setDesGrupo("");
					respNiveles=new ArrayList<MiembroNivelDTO>();
					validarCodRegistro = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("EditRespNivelAprobacionMB : obtenerDatosMiembro:: Error ::: "+ ex.getLocalizedMessage()+" - Cause " +ex.getCause()+ " - " +ex.getMessage());
		}
	}
	
	//Metodos del OTRO MB
	public void limpiarCampos() {
		if (miembroNivelDto != null) {
			miembroNivelDto.setRegistro("");
			miembroNivelDto.setDescripcion("");
			miembroNivelDto.setDesGrupo("");
			miembroNivelDto.setEstado("");
			miembroNivelDto.setCodNivel("-1");
			validarCodRegistro = false;
		} else {
			miembroNivelDto = new MiembroNivelDTO();
		}
		respNiveles= new ArrayList<MiembroNivelDTO>();
		bEditar=true;
	}
	public String getIniciar() 
	{
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession();
		RespNivelAprobacionMB temp = (RespNivelAprobacionMB) session.getAttribute("respNivel");
		
		if (isLimpiar()) 
		{
			this.limpiarCampos();
			respNiveles.clear();
			setLimpiar(false);
			bEditar=false;
		}
		else 
		{
			this.editar();

		}
		
		setFlagVisible(false);
		return iniciar;
	}
	
	private void editar(){
		logger.info("Here here .... editar ");
		bEditar=true;
		miembroNivelDto.setRegistro(miembroCapturado.getRegistro());
		this.obtenerDatosMiembro();
		logger.info("bEditar :: :: "+bEditar);
		/*
		miembroNivelDto.setCodEstado(miembroCapturado.getCodEstado());
		miembroNivelDto.setCodNivel(miembroCapturado.getCodNivel());
		miembroNivelDto.setDescripcion(miembroCapturado.getDescripcion());
		miembroNivelDto.set*/
	}
	/*private void editar() {
		setbEditar(false);

		if (miembroNivel.getTiivsMiembro() != null) 
		{
			miembroNivelDto = new MiembroNivelDTO();
			miembroNivelDto.setRegistro(miembroNivel.getTiivsMiembro().getCodMiembro());
			miembroNivelDto.setDesGrupo(miembroNivel.getTiivsMiembro().getTiivsGrupo().getDesGrupo());
			miembroNivelDto.setDescripcion(miembroNivel.getTiivsMiembro().getDescripcion());
			miembroNivelDto.setEstado(miembroNivel.getEstado());
			miembroNivelDto.setCodNivel(miembroNivel.getCodNiv());

			GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtroTiivsMiembroNivel = Busqueda
					.forClass(TiivsMiembroNivel.class);
			list = new ArrayList<TiivsMiembroNivel>();

			try {
				list = serviceTiivsMiembroNivel
						.buscarDinamico(filtroTiivsMiembroNivel
								.add(Restrictions.eq("tiivsMiembro.codMiembro",
										miembroNivel.getTiivsMiembro().getCodMiembro())));
			} catch (Exception e) {
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
						+ "al obtener ListaRespNivel: " + e);
			}

			respNiveles = new ArrayList<MiembroNivelDTO>();

			for (TiivsMiembroNivel e : list) {
				String descEstado = "";
				String desNivel = "";

				descEstado = Utilitarios.obternerDescripcionEstado(e.getEstado());
				desNivel = respNivelAprobacionService.obtenerDesNivel(e
						.getCodNiv());

				List<TiivsNivel> tiivsNivels = new ArrayList<TiivsNivel>();
				Busqueda filtroTiivsNivelMoneda = Busqueda
						.forClass(TiivsNivel.class);
				try {
					tiivsNivels = serviceTiivsNivel
							.buscarDinamico(filtroTiivsNivelMoneda
									.add(Restrictions.eq("codNiv",
											e.getCodNiv())));
				} catch (Exception e1) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
							+ "al obtener monedas: " + e);
				}

				int ris = 0;
				int rfs = 0;

				int rid = 0;
				int rfd = 0;

				int rie = 0;
				int rfe = 0;

				for (TiivsNivel nivel : tiivsNivels) {

					if (nivel.getMoneda().compareTo(
							ConstantesVisado.MONEDAS.COD_SOLES) == 0) {
						ris = nivel.getRangoInicio();
						rfs = nivel.getRangoFin();
					}

					if (nivel.getMoneda().compareTo(
							ConstantesVisado.MONEDAS.COD_DOLAR) == 0) {
						rid = nivel.getRangoInicio();
						rfd = nivel.getRangoFin();
					}

					if (nivel.getMoneda().compareTo(
							ConstantesVisado.MONEDAS.COD_EUROS) == 0) {
						rie = nivel.getRangoInicio();
						rfe = nivel.getRangoFin();
					}

				}

				respNiveles.add(new MiembroNivelDTO(0, e.getId(),
						e.getCodNiv(), desNivel, e.getTiivsMiembro()
								.getCodMiembro(), e.getTiivsMiembro()
								.getDescripcion(), e.getTiivsMiembro()
								.getTiivsGrupo().getCodGrupo(), e
								.getTiivsMiembro().getTiivsGrupo()
								.getDesGrupo(),
						e.getFechaRegistro().toString(),
						e.getUsuarioRegistro(), e.getEstado(), descEstado, ris,
						rfs, rid, rfd, rie, rfe));
			}

			setFlagVisible(true);

		} else {

			miembroNivelDto = new MiembroNivelDTO();
			miembroNivelDto.setEstado(miembroNivel.getEstado());

			if (respNiveles == null)
				respNiveles = new ArrayList<MiembroNivelDTO>();

			setFlagVisible(false);
		}

		niveles = new ArrayList<NivelDto>();

		Busqueda filtroTiivsNivel = Busqueda.forClass(TiivsNivel.class).setProjection(Projections.distinct(Projections.property("codNiv")));

		try {
			List<String> tiivsNivels = serviceTiivsNivel.buscarDinamicoString(filtroTiivsNivel);
			for (String s : tiivsNivels) {
				Busqueda filtroTiivsNivel2 = Busqueda.forClass(TiivsNivel.class);
				List<TiivsNivel> list = serviceTiivsNivel.buscarDinamico(filtroTiivsNivel2.add(Restrictions.eq("codNiv", s)));
				String des = list.get(0).getDesNiv();
				niveles.add(new NivelDto(s, des));
			}
		} catch (Exception e) {
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR + "al obtener la lista CodAgrup: " + e);
		}
	}
	*/
	//@Autor Cesar
	@SuppressWarnings("unchecked")
	public void editarNivAprobacion()
	{
		List<TiivsMiembroNivel> lstTmp = new ArrayList<TiivsMiembroNivel>();
		
		/*MiembroNivelDTO mNivel = ((MiembroNivelDTO) event.getObject());
		logger.debug("modificando nivel con codigo:" + mNivel.getId());*/
		
		MiembroNivelDTO mNivel=miembroCapturado_Edit;
		logger.debug("estado a modificar:" + mNivel.getCodEstado());
		GenericDao<TiivsMiembroNivel, Object> mNivelDAO = (GenericDao<TiivsMiembroNivel, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMiem = Busqueda.forClass(TiivsMiembroNivel.class);
		filtroMiem.add(Restrictions.eq(ConstantesVisado.CAMPO_ID, mNivel.getId()));
		
		
		try {
			lstTmp = mNivelDAO.buscarDinamico(filtroMiem);
			logger.info(" lstTmp " +lstTmp.size());
		} catch (Exception e) {
			logger.debug("No se pudo encontrar los datos de miembro nivel",e);
		}
		
		if (lstTmp.size()==1)
		{
			for (TiivsMiembroNivel tmp: lstTmp )
			{
				//tmp.setEstado(mNivel.getCodEstado());
				try {
					
					if(tmp.getEstado().equals("1")){
						tmp.setEstado("0");
						tmp.setLabelAccion("Inactivar");
					}else if(tmp.getEstado().equals("0")){
						tmp.setEstado("1");
						tmp.setLabelAccion("Activar");
					}
					
					tmp.setUsuarioAct(tmp.getUsuarioRegistro());
					
					java.util.Date date= new java.util.Date();
					tmp.setFechaAct(new Timestamp(date.getTime()));
					tmp.setEstadoMiembro(tmp.getEstado());
					mNivelDAO.modificar(tmp);
					logger.info("Se modifico correctamente el estado de miembro nivel");
					/// LISTAR TODO DE NUEVO
					miembroNivelDto.setRegistro(tmp.getTiivsMiembro().getCodMiembro());
					/*miembroNivelDto.setDescripcion("");*/
					obtenerDatosMiembro();
				//	listarRespxNivel();
				} catch (Exception e) {
					logger.info("Error al actualizar la informacion de miembro nivel",e);
				}
			}
		}
	}
	
	public void confirmarCambios() {

		logger.info("=== inicia confirmarCambios() ===");
		List<TiivsMiembro> miembroBuscar = new ArrayList<TiivsMiembro>();
		GenericDao<TiivsMiembroNivel, Object> serviceTiivsMiembroNivel = (GenericDao<TiivsMiembroNivel, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		GenericDao<TiivsMiembro, Object> serviceTiivsMiembro = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");

		if (respNiveles != null) {
			logger.debug(ConstantesVisado.MENSAJE.TAMANHIO_LISTA
					+ "respNiveles es: [" + respNiveles.size() + "]");
		}

		for (MiembroNivelDTO miembroNivelDTO : respNiveles) {

			logger.debug("miembroNivelDTO.getNuevo(): "+ miembroNivelDTO.getNuevo());
			if (miembroNivelDTO.getNuevo() == 1) {

				TiivsMiembro miembro = new TiivsMiembro();
				miembro.setCodMiembro(miembroNivelDTO.getRegistro());
				miembro.setDescripcion(miembroNivelDTO.getDescripcion());
				miembroBuscar = obtenerMiembro(miembro.getCodMiembro());
				
				TiivsGrupo tiivsGrupo = new TiivsGrupo();
				tiivsGrupo.setCodGrupo(miembroBuscar.get(0).getTiivsGrupo().getCodGrupo());	
				miembro.setTiivsGrupo(tiivsGrupo);
				miembro.setCriterio(miembroBuscar.get(0).getCriterio());
				miembro.setActivo(miembroBuscar.get(0).getActivo());
				/*
				 * try { miembro = serviceTiivsMiembro.save(miembro); } catch
				 * (Exception e1) { e1.printStackTrace(); }
				 */

				TiivsMiembroNivel miembroNivel = new TiivsMiembroNivel();
				miembroNivel.setTiivsMiembro(miembro);
				miembroNivel.setCodNiv(miembroNivelDTO.getCodNivel());
				miembroNivel.setTipoRol("R");
				miembroNivel.setEstado(miembroNivelDTO.getCodEstado());
				miembroNivel.setEstadoMiembro(miembroNivelDTO.getCodEstado());

				Date date = new Date();
				miembroNivel.setFechaRegistro(new Timestamp(date.getTime()));
				miembroNivel.setFechaAct(new Timestamp(date.getTime()));

				IILDPeUsuario usuario = (IILDPeUsuario) Utilitarios
						.getObjectInSession("USUARIO_SESION");

				miembroNivel.setUsuarioRegistro(usuario.getUID());
				miembroNivel.setUsuarioAct(usuario.getUID());

				if (logger.isDebugEnabled()) {
					logger.debug("== Datos miembroNivel ==");
					logger.debug("[miemb-usuReg]:"
							+ miembroNivel.getUsuarioRegistro());
					logger.debug("[miemb-fechaReg]:"
							+ miembroNivel.getFechaRegistro());
					logger.debug("[miemb-codNivel]:" + miembroNivel.getCodNiv());
					logger.debug("[miemb-estado]:" + miembroNivel.getEstado());
					logger.debug("[miemb-tipoRol]:" + miembroNivel.getTipoRol());
				}
				try {
					serviceTiivsMiembroNivel.save(miembroNivel);
					logger.info(ConstantesVisado.MENSAJE.REGISTRO_OK + " el: "
							+ miembroNivelDTO.getRegistro());
					Utilitarios.mensajeInfo("Info", ConstantesVisado.MENSAJE.REGISTRO_OK + " el: "+ miembroNivelDTO.getRegistro());
				} catch (Exception e) {
					logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR
							+ "al guardar MiembroNivel :"
							+ miembroNivelDTO.getRegistro() + " - " + e);
				}
			}

		}
		logger.info("=== saliendo de confirmarCambios() ===");
		try {
			//limpiarCampos();
			miembroNivelDto.setEstado("");
			listarRespxNivel();
			FacesContext.getCurrentInstance().getExternalContext().redirect("/VisadoPoderes/faces/paginas/respNivel.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private List<TiivsMiembro> obtenerMiembro(String codMiembro) {
		logger.info("EditRespNivelAprobacionMB : obtenerMiembro");
		List<TiivsMiembro> miembro = new ArrayList<TiivsMiembro>();
		try{
			miembro = respNivelAprobacionService.obtenerMiembro(codMiembro);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return miembro;
	}
	public MiembroNivelDTO getMiembroNivelDto() {
		return miembroNivelDto;
	}

	public void setMiembroNivelDto(MiembroNivelDTO miembroNivelDto) {
		this.miembroNivelDto = miembroNivelDto;
	}

	public List<NivelDto> getNiveles() {
		return niveles;
	}

	public void setNiveles(List<NivelDto> niveles) {
		this.niveles = niveles;
	}

	public List<MiembroNivelDTO> getRespNiveles() {
		return respNiveles;
	}

	public void setRespNiveles(List<MiembroNivelDTO> respNiveles) {
		this.respNiveles = respNiveles;
	}

	public boolean isFlagVisible() {
		return flagVisible;
	}

	public void setFlagVisible(boolean flagVisible) {
		this.flagVisible = flagVisible;
	}

	public String getCodNivel() {
		return codNivel;
	}

	public void setCodNivel(String codNivel) {
		this.codNivel = codNivel;
	}

	public String getCodRegistro() {
		return codRegistro;
	}

	public void setCodRegistro(String codRegistro) {
		this.codRegistro = codRegistro;
	}

	public boolean isValidarCodRegistro() {
		return validarCodRegistro;
	}

	public void setValidarCodRegistro(boolean validarCodRegistro) {
		this.validarCodRegistro = validarCodRegistro;
	}

	public void setIniciar(String iniciar) {
		this.iniciar = iniciar;
	}

	public boolean isbEditar() {
		return bEditar;
	}

	public void setbEditar(boolean bEditar) {
		this.bEditar = bEditar;
	}

	public String[] getEstados() {
		return estados;
	}

	public void setEstados(String[] estados) {
		this.estados = estados;
	}
	public boolean isLimpiar() {
		return limpiar;
	}

	public void setLimpiar(boolean limpiar) {
		this.limpiar = limpiar;
	}

	public MiembroNivelDTO getMiembroCapturado() {
		return this.miembroCapturado;
	}

	public void setMiembroCapturado(MiembroNivelDTO miembroCapturado) {
		this.miembroCapturado = miembroCapturado;
	}

	public MiembroNivelDTO getMiembroCapturado_Edit() {
		return this.miembroCapturado_Edit;
	}

	public void setMiembroCapturado_Edit(MiembroNivelDTO miembroCapturado_Edit) {
		this.miembroCapturado_Edit = miembroCapturado_Edit;
	}

	
	
	
}

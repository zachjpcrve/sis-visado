package com.hildebrando.visado.mb;

import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.SeguridadDao;
import com.bbva.persistencia.generica.util.Sesion;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.conexion.__Conexion2;
import com.grupobbva.bc.per.tele.seguridad.ServiciosSeguridadBbva;
import com.hildebrando.visado.dto.MiembroDto;
import com.hildebrando.visado.modelo.Ldapperu2;

/**
 * Clase que se encarga de manejar la seguridad de la aplicacion, 
 * comprende metodos como el logueo, cerrar sesion, etc.
 * @author 
 * */

@ManagedBean(name = "seguridadMB")
@SessionScoped
public class SeguridadMB {
	public static Logger logger = Logger.getLogger(SeguridadMB.class);
	HttpServletRequest request;
	HttpServletResponse response;
//	private String sCodUsuarioBBVA="P015740";
	private String sCodUsuarioBBVA="P017433";
	private String idSesion = "";
	private String usuarioCodigo = "";
	private boolean bNoLogeado = true;
	public boolean isbNoLogeado() {
		return bNoLogeado;
	}
	public void setbNoLogeado(boolean bNoLogeado) {
		this.bNoLogeado = bNoLogeado;
	}
	private String password="iivs";
	
	public SeguridadMB() {
	}
	public String getIdSesion() {
		return idSesion;
	}
	public void setIdSesion(String idSesion) {
		this.idSesion = idSesion;
	}
	public String getUsuarioCodigo() {
		return usuarioCodigo;
	}
	public void setUsuarioCodigo(String usuarioCodigo) {
		this.usuarioCodigo = usuarioCodigo;
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	public void iniciarSession(){
	String explorador="";
	logger.info("===== entrando a iniciarSession() ====");				
	ServiciosSeguridadBbva objSeguridad;
	//IILDPeUsuario usuarioIILD = null;
	com.grupobbva.seguridad.client.domain.Usuario usuarioIILD=null;
	//Ldapperu2 usuarioIILD = new Ldapperu2();;
    request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
   
    
    
    String reg = sCodUsuarioBBVA;
    logger.info("************************** reg *************** "+ reg);
    try {
        	__Conexion2 con = new __Conexion2();
            String codigoUsuario = sCodUsuarioBBVA;                                
          //  usuarioIILD = con.recuperarUsuario(codigoUsuario);	
        
        //Valida explorador
        String agente = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestHeaderMap()
                    .get("User-Agent");
        
        if (agente.contains("MSIE 7.0")) {
            explorador = "IE7";
	    }
        if (agente.contains("MSIE 9.0")) {
            explorador = "IE9";
	    }
        if (agente.contains("MSIE 8.0")) {
            explorador = "IE8";
	    }
	    if (agente.contains("Opera")) {
	        explorador = "Opera";
	    }
	    if (agente.contains("Firefox")) {
	        explorador = "Firefox";
	    }
	    if (agente.contains("Safari")) {
	        explorador = "Safari";
	    }
	    if (agente.contains("Chrome")) {
	        explorador = "Chrome";
	    }
	    
	    logger.info("Navegador detectado: " + explorador);
	    
	    if (explorador.equals("IE7"))
	    {
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET", "350%");
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET_PODER", "95%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_PODER", "850");
	    	request.getSession(true).setAttribute("ANCHO_REVOC_PODER", "100%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_REVOC_PODER", "850");
	    	request.getSession(true).setAttribute("ALTO_POPUP_REVOC_PODER", "300");
	    }
	    if (explorador.equals("IE9"))
	    {
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET", "105%");
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET_PODER", "105%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_PODER", "850");
	    	request.getSession(true).setAttribute("ANCHO_REVOC_PODER", "150%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_REVOC_PODER", "800");
	    	request.getSession(true).setAttribute("ALTO_POPUP_REVOC_PODER", "400");
	    }
	    
	    if (explorador.equals("IE8"))
	    {
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET", "105%");
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET_PODER", "105%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_PODER", "850");
	    	request.getSession(true).setAttribute("ANCHO_REVOC_PODER", "150%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_REVOC_PODER", "800");
	    	request.getSession(true).setAttribute("ALTO_POPUP_REVOC_PODER", "400");
	    }
	    if (explorador.equals("Firefox"))
	    {
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET", "105%");
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET_PODER", "105%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_PODER", "950");
	    	request.getSession(true).setAttribute("ANCHO_REVOC_PODER", "150%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_REVOC_PODER", "930");
	    	request.getSession(true).setAttribute("ALTO_POPUP_REVOC_PODER", "400");
	    }
	    
	    if (explorador.equals("Chrome"))
	    {
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET", "105%");
	    	request.getSession(true).setAttribute("ANCHO_FIELDSET_PODER", "105%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_PODER", "950");
	    	request.getSession(true).setAttribute("ANCHO_REVOC_PODER", "150%");
	    	request.getSession(true).setAttribute("ANCHO_POPUP_REVOC_PODER", "930");
	    	request.getSession(true).setAttribute("ALTO_POPUP_REVOC_PODER", "400");
	    }
          
		//PARTE ESPACIOsc
		if(password!=null){
			logger.info("IIVP : Desarrollo = true");
    		reg = reg.toUpperCase();
		}
		else{
			logger.info("IIVP : Desarrollo = false");
			objSeguridad = new ServiciosSeguridadBbva(request);
			objSeguridad.obtener_ID();
			reg = objSeguridad.getUsuario().toUpperCase();
		}
		
		//PARTE LDAP
		logger.info("IIVP Usuario:" + reg);    		
//		usuario = (Ldapperu2) usuService.buscarDinamico(Busqueda.forClass(Ldapperu2.class));
		
		
		
		if(usuarioIILD==null){
			logger.info("==== El [USU_LDAP] es NULO === ");
			request.getSession(true).setAttribute("strMensaje","Usuario no Encontrado");
			response.sendRedirect("./sinAcceso.jsp");
		}else{
			if(logger.isInfoEnabled()){
				logger.info("==== [USU_LDAP] encontrado === ");
				/*logger.info("[USU_LDAP]-Codcargo: "+usuarioIILD.getCargo().getCodigo());
				logger.info("[USU_LDAP]-Codofi: "+usuarioIILD.getBancoOficina().getCodigo());
				logger.info("[USU_LDAP]-Codusu: "+usuarioIILD.getUID());
				logger.info("[USU_LDAP]-Nombres: "+usuarioIILD.getNombre()+" "+usuarioIILD.getApellido1());*/
			}
			
			this.bNoLogeado = false;
			
			if(idSesion.equals("")){
				int numIdSesion = Sesion.crearNuevaSesion();
				idSesion = String.valueOf(numIdSesion);
				logger.info("idSesion :: " + idSesion);
				
			}
			
			
			/*
			System.out.println("Codcargo: "+usuarioIILD.getId().getCodcargo());
			System.out.println("Codofi: "+usuarioIILD.getId().getCodofi());
			System.out.println("Codusu: "+usuarioIILD.getId().getCodusu());
		*/	
        /*	request.getSession(true).setAttribute("USUARIO_SESION", usuario);
        	request.getSession(true).setAttribute("nombreUsuario", usuario.getNombre());
        	request.getSession(true).setAttribute("codigoUsuario", usuario.getCodusu());
       */
      	
        	request.getSession(true).setAttribute("GRUPO_ADM", null);
			request.getSession(true).setAttribute("GRUPO_JRD", null);
			request.getSession(true).setAttribute("GRUPO_OFI", null);
			request.getSession(true).setAttribute("DES_GRUPO", null);			

			SeguridadDao<MiembroDto, Object> miembroService = (SeguridadDao<MiembroDto, Object>) SpringInit.getApplicationContext().getBean("miembroEspDao");
			logger.info("**************** miembroService : "+miembroService);
			if(miembroService==null){
				logger.info("miembroService - Nullo");
			}else{
				logger.info("miembroService - No Nullo");
			}
			
			 List<MiembroDto> ListaMiembros= miembroService.buscarMiembroSql(usuarioIILD);
				
			 logger.info(" ***************** ListaMiembros **************");
			 if(ListaMiembros!=null){
				logger.debug("Hay ["+ListaMiembros.size()+"] miembro(s) en la BD.");
			 }
        	for (Iterator iterator = ListaMiembros.iterator(); iterator.hasNext();) {
        		MiembroDto object = (MiembroDto) iterator.next();
				logger.info("Grupo: " + object.getCOD_GRUPO()+", criterio: "+ object.getCRITERIO()+", miembro: "+object.getCOD_MIEMBRO() );
				if (ConstantesVisado.COD_GRUPO_ADM.equals(object.getCOD_GRUPO())){
					logger.info("[GRUPO_ADMIN]:"+object.getDESCRIPCION());
					request.getSession(true).setAttribute("GRUPO_ADM", ConstantesVisado.COD_GRUPO_ADM);
					request.getSession(true).setAttribute("DES_GRUPO", ConstantesVisado.DES_GRUPO_ADM);
                }else 
				if (ConstantesVisado.COD_GRUPO_JRD.equals(object.getCOD_GRUPO())){
					logger.info("[GRUPO_JRD]:"+object.getDESCRIPCION());
					request.getSession(true).setAttribute("GRUPO_JRD", ConstantesVisado.COD_GRUPO_JRD);	
					request.getSession(true).setAttribute("DES_GRUPO", ConstantesVisado.DES_GRUPO_JRD);
                }else
				if (ConstantesVisado.COD_GRUPO_OFI.equals(object.getCOD_GRUPO())){
					logger.info("[GRUPO_OFI]:"+object.getDESCRIPCION());
					request.getSession(true).setAttribute("GRUPO_OFI", ConstantesVisado.COD_GRUPO_OFI);
					request.getSession(true).setAttribute("DES_GRUPO", ConstantesVisado.DES_GRUPO_OFI);
                  }
			}
/*        	request.getSession(true).setAttribute("USUARIO_SESION", usuarioIILD);
        	request.getSession(true).setAttribute(ConstantesVisado.USUARIO_ID, usuarioIILD.getUID());
        	request.getSession(true).setAttribute(ConstantesVisado.USUARIO_NOMBRE, usuarioIILD.getNombre()
        			                                                              + " " + usuarioIILD.getApellido1() );*/
        	
        	String grupoAdm = (String) Utilitarios.getObjectInSession("GRUPO_ADM");
    		String grupoOfi = (String) Utilitarios.getObjectInSession("GRUPO_OFI");
    		String grupoJrd = (String) Utilitarios.getObjectInSession("GRUPO_JRD");
    		
    		// DES_GRUPO_ADM= "SERVICIOS JURIDICOS";	
    		// DES_GRUPO_JRD= "ABOGADO";
    		// DES_GRUPO_OFI= "OFICINA";<
        	if(grupoAdm ==null && grupoJrd==null ){
        		logger.info("ROL OFICINA");
        		request.getSession(true).setAttribute("PERFIL_USUARIO", ConstantesVisado.OFICINA);
        	}else{
        		 if (grupoAdm ==null) {
        			 logger.info("ROL ABOGADO");
        			 request.getSession(true).setAttribute("PERFIL_USUARIO", ConstantesVisado.ABOGADO);
        		 }else{	
        			 logger.info("ROL SSJJ");
            		 request.getSession(true).setAttribute("PERFIL_USUARIO", ConstantesVisado.SSJJ);
        		 }
        	}
        	
        	
        	
        	if(ListaMiembros.size()>0){
        		logger.debug(ConstantesVisado.SEGURIDAD.USER_ACCESO_OK);
        		response.sendRedirect("./paginas/bienvenido.xhtml");
        	}else{
            	request.getSession().setAttribute("strMensaje",ConstantesVisado.SEGURIDAD.USER_NO_REGISTR);
            	response.sendRedirect("./sinAcceso.jsp");
        	}
        	
    	}
    	logger.info("IIVP --fin de autenticación---");

    } catch (Exception e) {
    	logger.error(e.getMessage(),e);
    	request.getSession().setAttribute("strMensaje","Excepcion al procesar la validacion:" + e.getMessage());
    }   
    logger.debug("===== saliendo de iniciarSession() ====");	

	}
	
	
	public String cerrarSesion() {
		logger.info(" === cerrarSesion() ==> " +idSesion);
		this.idSesion = "";
		this.usuarioCodigo = "";
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			Object session = externalContext.getSession(false);
			HttpSession httpSession = (HttpSession) session;
			httpSession.invalidate();
		} catch (Exception e) {
			logger.error(ConstantesVisado.SEGURIDAD.ERROR_CERRAR_SESION +": "+e);
		}
		return "/faces/paginas/bienvenido.xhtml";
	}
	
	public String getsCodUsuarioBBVA() {
		return sCodUsuarioBBVA;
	}
	public void setsCodUsuarioBBVA(String sCodUsuarioBBVA) {
		this.sCodUsuarioBBVA = sCodUsuarioBBVA;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	
}

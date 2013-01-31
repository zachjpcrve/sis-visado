package com.hildebrando.visado.mb;

import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.SeguridadDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.conexion.__Conexion2;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.grupobbva.bc.per.tele.seguridad.ServiciosSeguridadBbva;
import com.hildebrando.visado.dto.MiembroDto;


@ManagedBean(name = "seguridadMB")
@SessionScoped
public class SeguridadMB {
	public static Logger logger = Logger.getLogger(SeguridadMB.class);
	HttpServletRequest request;
	HttpServletResponse response;
	private String sCodUsuarioBBVA="P015740";
	//private String sCodUsuarioBBVA="P017433";
	
	private String password="iivs";
	
	public SeguridadMB() {
	}
	@SuppressWarnings("rawtypes")
	public void iniciarSession(){
	logger.info("===== entrando a iniciarSession() ====");				
	ServiciosSeguridadBbva objSeguridad;
	IILDPeUsuario usuarioIILD = null;
	//Ldapperu2 usuarioIILD = new Ldapperu2();;
    request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
   
    
    
    String reg = sCodUsuarioBBVA;
    logger.info("************************** reg *************** "+ reg);
    try {
        	__Conexion2 con = new __Conexion2();
            String codigoUsuario = sCodUsuarioBBVA;                                
            usuarioIILD = con.recuperarUsuario(codigoUsuario);	

           
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
		//usuario = (Ldapperu2) usuService.buscarDinamico(Busqueda.forClass(Ldapperu2.class));
		
		
		
		if(usuarioIILD==null){
			logger.info("==== El [USU_LDAP] es NULO === ");
			request.getSession(true).setAttribute("strMensaje","Usuario no Encontrado");
			response.sendRedirect("./sinAcceso.jsp");
		}else{
			logger.info("==== [USU_LDAP] encontrado === ");
			logger.info("[USU_LDAP]-Codcargo: "+usuarioIILD.getCargo().getCodigo());
			logger.info("[USU_LDAP]-Codofi: "+usuarioIILD.getBancoOficina().getCodigo());
			logger.info("[USU_LDAP]-Codusu: "+usuarioIILD.getUID());
			logger.info("[USU_LDAP]-Nombres: "+usuarioIILD.getNombre()+" "+usuarioIILD.getApellido1());
			
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
			request.getSession(true).setAttribute("TAREA_CONSULTA_SOLICITUDES", null);
			request.getSession(true).setAttribute("TAREA_MANTENIMIENTOS", null);
			request.getSession(true).setAttribute("TAREA_ADMINISTRAR_GRUPOS", null);
			request.getSession(true).setAttribute("TAREA_ADMINISTRAR_TAREAS", null);
			request.getSession(true).setAttribute("TAREA_APROBAR_RECHAZAR_SOLICITUD", null);
			request.getSession(true).setAttribute("TAREA_MODIFICAR_SOLICITUD", null);
			request.getSession(true).setAttribute("TAREA_REGISTRO_SOLICITUDES", null);		
			
			SeguridadDao<MiembroDto, Object> miembroService = (SeguridadDao<MiembroDto, Object>) SpringInit.getApplicationContext().getBean("miembroEspDao");
			logger.info("**************** miembroService : "+miembroService);
			if(miembroService==null){
				logger.info("Nullo");
			}else{
				logger.info("No Nullo");
			}
			
			 List<MiembroDto> ListaMiembros= miembroService.buscarMiembroSql(usuarioIILD);
				
			 logger.info(" ***************** ListaMiembros **************" +ListaMiembros.size());
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
        	request.getSession(true).setAttribute("USUARIO_SESION", usuarioIILD);
        	String grupoAdm = (String) Utilitarios.getObjectInSession("GRUPO_ADM");
    		String grupoOfi = (String) Utilitarios.getObjectInSession("GRUPO_OFI");
    		String grupoJrd = (String) Utilitarios.getObjectInSession("GRUPO_JRD");
    		
    		// DES_GRUPO_ADM= "SERVICIOS JURIDICOS";	
    		// DES_GRUPO_JRD= "ABOGADO";
    		// DES_GRUPO_OFI= "OFICINA";
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
        		response.sendRedirect("./bienvenido.xhtml");
        	}else{
            	request.getSession().setAttribute("strMensaje","Usuario no se encuentra registrado en el sistema.");
            	response.sendRedirect("./sinAcceso.jsp");
        	}
        	
    	}
    	logger.info("IIVP --fin de autenticación---");

    } catch (Exception e) {
    	e.printStackTrace();
    	logger.error(e.getMessage(),e);
    	request.getSession().setAttribute("strMensaje","Excepcion al procesar la validacion:" + e.getMessage());
    }   
    logger.debug("===== saliendo de iniciarSession() ====");	

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

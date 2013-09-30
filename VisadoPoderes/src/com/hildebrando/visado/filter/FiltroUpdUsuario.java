package com.hildebrando.visado.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.SeguridadDao;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeCargo;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeOficina;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.MiembroDto;


/**
 * Servlet Filter implementation class UpdUsuario
 */
//@WebFilter("/UpdUsuario")
public class FiltroUpdUsuario implements Filter {
	public static Logger logger = Logger.getLogger(FiltroUpdUsuario.class);
    /**
     * Default constructor. 
     */
    public FiltroUpdUsuario() {
       
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,  FilterChain chain) throws IOException, ServletException {
		HttpSession sesion = ((HttpServletRequest)request).getSession();
		String explorador="";
		try {
			if(sesion.getAttribute("usuario") != null){				
				com.grupobbva.seguridad.client.domain.Usuario usuario = (com.grupobbva.seguridad.client.domain.Usuario) sesion.getAttribute("usuario");
			
				
				IILDPeUsuario usuarioDto=new IILDPeUsuario();
				usuarioDto.setUID(usuario.getUsuarioId());
				usuarioDto.setNombre(usuario.getNombre());
				usuarioDto.setApellido1(usuario.getApePat());
				usuarioDto.setApellido2(usuario.getApeMat());
				usuarioDto.setBancoOficina(new IILDPeOficina(usuario.getOficinaId(), usuario.getOficinaNombre(), "", "", ""));
				usuarioDto.setCargo(new IILDPeCargo(usuario.getPuestoId(), usuario.getPuesto().getNombre()));				
				
				String grupoAdm = null;
	    		String grupoOfi = null;
	    		String grupoJrd = null;
				if(usuario != null){
					
					SeguridadDao<MiembroDto, Object> miembroService = (SeguridadDao<MiembroDto, Object>) SpringInit.getApplicationContext().getBean("miembroEspDao");
					//logger.info("**************** miembroService : "+miembroService);
					/*if(miembroService==null){
						logger.info("miembroService - Nullo");
					}else{
						logger.info("miembroService - No Nullo");
					}*/
					
					 List<MiembroDto> ListaMiembros= miembroService.buscarMiembroSql(usuario);
						
					 //logger.info(" ***************** ListaMiembros **************");
					 if(ListaMiembros!=null){
						//logger.debug("Hay ["+ListaMiembros.size()+"] miembro(s) en la BD.");
					 }
		        	for (Iterator iterator = ListaMiembros.iterator(); iterator.hasNext();) {
		        		MiembroDto object = (MiembroDto) iterator.next();
						//logger.info("Grupo: " + object.getCOD_GRUPO()+", criterio: "+ object.getCRITERIO()+", miembro: "+object.getCOD_MIEMBRO() );
						if (ConstantesVisado.COD_GRUPO_ADM.equals(object.getCOD_GRUPO())){
						//	logger.info("[GRUPO_ADMIN]:"+object.getDESCRIPCION());
							grupoAdm=ConstantesVisado.COD_GRUPO_ADM;
							//sesion.setAttribute("GRUPO_ADM", ConstantesVisado.COD_GRUPO_ADM);
		                }else 
						if (ConstantesVisado.COD_GRUPO_JRD.equals(object.getCOD_GRUPO())){
						//	logger.info("[GRUPO_JRD]:"+object.getDESCRIPCION());
							grupoJrd=ConstantesVisado.COD_GRUPO_JRD;
							//sesion.setAttribute("GRUPO_JRD", ConstantesVisado.COD_GRUPO_JRD);	
		                }else
						if (ConstantesVisado.COD_GRUPO_OFI.equals(object.getCOD_GRUPO())){
						//	logger.info("[GRUPO_OFI]:"+object.getDESCRIPCION());
							grupoOfi=ConstantesVisado.COD_GRUPO_OFI;
							//sesion.setAttribute("GRUPO_OFI", ConstantesVisado.COD_GRUPO_OFI);
		                  }
					}
		        	sesion.setAttribute("USUARIO_SESION", usuarioDto);
		        	sesion.setAttribute(ConstantesVisado.USUARIO_ID, usuario.getUsuarioId());
		        	sesion.setAttribute(ConstantesVisado.USUARIO_NOMBRE, usuario.getNombre()  + " " + usuario.getApePat());
		      
		        	
		        	if(grupoAdm ==null && grupoJrd==null ){
		        		//logger.info("ROL OFICINA");
		        		sesion.setAttribute("PERFIL_USUARIO", ConstantesVisado.OFICINA);
		        	}else{
		        		 if (grupoAdm ==null) {
		        			// logger.info("ROL ABOGADO");
		        			 sesion.setAttribute("PERFIL_USUARIO", ConstantesVisado.ABOGADO);
		        		 }else{	
		        			 //logger.info("ROL SSJJ");
		        			 sesion.setAttribute("PERFIL_USUARIO", ConstantesVisado.SSJJ);
		        		 }
		        	}
		        	
		        	if(ListaMiembros.size()>0){
		        		//logger.debug(ConstantesVisado.SEGURIDAD.USER_ACCESO_OK);
		        		
		        		
		        		//Valida explorador
		          
		        		
		        		
		        		//response. sendRedirect("./bienvenido.xhtml");
		        	}else{
		        		
		            	sesion.setAttribute("strMensaje",ConstantesVisado.SEGURIDAD.USER_NO_REGISTR);
		            	//response.sendRedirect("./sinAcceso.jsp");
		            	 response.setContentType("text/html");
		        	     PrintWriter out = response.getWriter();
		        	     out.write("<center>La validacion del usuario no fue satisfactoria por el siguiente motivo: " + ConstantesVisado.SEGURIDAD.USER_NO_REGISTR + "</center>");
		        	     out.close();
		        	}
		        	
		    	}
		    	//logger.info("IIVP --fin de autenticación---");
					
			}
					
			
	
		} catch (Exception e) {
			logger.error(e);			
		}				
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

	
}

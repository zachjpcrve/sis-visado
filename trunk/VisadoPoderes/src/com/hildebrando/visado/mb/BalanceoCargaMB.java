package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.EstudioxSolicitud;
import com.hildebrando.visado.dto.MiembroDto;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.modelo.TiivsSolicitud;


@ManagedBean(name = "balanceoCargaMB")
@SessionScoped
public class BalanceoCargaMB {
	
	

	//Metodo para la asignacion de un abogado a una Solicitud
	public TiivsSolicitud asignarSolicitud(TiivsSolicitud solicitud) {
		
		String abogadoAsignado = getAbogadoPorSolicitudes();		
		System.out.println("Abogado Asignado:" + abogadoAsignado);
		solicitud.setRegAbogado(abogadoAsignado);
		return solicitud;
	}
	
	public void asignarSolicitud() {
		
		String abogadoAsignado = getAbogadoPorSolicitudes();		
		System.out.println("Abogado Asignado:" + abogadoAsignado);
	}


	private String getAbogadoPorSolicitudes() {
					
		String codigoEstado = ConstantesVisado.CODIGO_ESTADO_ENVIADO;		
		String sHoraCorte = getHoraCorte();  //Para obtener hora de corte (parámetro de sistema)
		String resultEstudio = null;
		String resultAbogado = null;
		
		//Query para obtener estudio con menos solicitudes pendientes
		String hql = " SELECT "+ 
			 " EST.COD_ESTUDIO,"+
			 " COUNT(SOL.COD_SOLI) NRO_SOLICITUDES" +
			 " FROM TIIVS_SOLICITUD SOL" +
			 " INNER JOIN TIIVS_MIEMBRO ABO ON SOL.REG_ABOGADO = ABO.COD_MIEMBRO" +
			 " INNER JOIN TIIVS_ESTUDIO EST ON EST.COD_ESTUDIO = ABO.ESTUDIO" +
			 " WHERE" +
			 " TRIM(SOL.ESTADO) = '"+ codigoEstado +"'" + 
			 " AND EST.ACTIVO = 1" + 			 
			 " AND (" +
			 " EXTRACT(YEAR FROM SOL.FECHA) = EXTRACT(YEAR FROM SYSDATE) AND"+
			 " EXTRACT(MONTH FROM SOL.FECHA) = EXTRACT(MONTH FROM SYSDATE) AND"+
			 " EXTRACT(DAY FROM SOL.FECHA) = EXTRACT(DAY FROM SYSDATE) AND"+
			 " EXTRACT(HOUR FROM SOL.FECHA) <= "+sHoraCorte+")"+		 
			 " GROUP BY" +
			 " EST.COD_ESTUDIO" +
			 " ORDER BY NRO_SOLICITUDES ASC";
		 
		//System.out.println("SQL2 : "+hql);
		

		try {			
			Query query = SpringInit.devolverSession().createSQLQuery(hql);									
			List<Object[]> lstEstudios = query.list();			
			//System.out.println("Tamaño result:" + lstEstudios.size());	
			if(lstEstudios.size()>0){
				resultEstudio = (String) lstEstudios.get(0)[0];
			} else { //asignación automática
				System.out.println("Estudios sin solicitudes pendientes");
				System.out.println("Asignación aleatoria");
				resultAbogado = getRandomAbogado();
			}
		} catch (Exception e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
		
		
		if(resultEstudio!=null){
			
			//Query para obtener abogado con menos solicitudes pendientes
			hql = " SELECT "+   
			  	" SOL.REG_ABOGADO,"+
			  	" COUNT(SOL.COD_SOLI) NRO_SOLICITUDES"+
			  	" FROM TIIVS_SOLICITUD SOL"+ 
			  	" INNER JOIN TIIVS_MIEMBRO ABO ON SOL.REG_ABOGADO = ABO.COD_MIEMBRO"+
			  	" WHERE"+
			  	" TRIM(SOL.ESTADO) = '"+codigoEstado+"'"+ 
			  	" AND TRIM(ABO.ESTUDIO) = '"+resultEstudio+"'"+
			  	" AND (" +
				" EXTRACT(YEAR FROM SOL.FECHA) = EXTRACT(YEAR FROM SYSDATE) AND"+
				" EXTRACT(MONTH FROM SOL.FECHA) = EXTRACT(MONTH FROM SYSDATE) AND"+
				" EXTRACT(DAY FROM SOL.FECHA) = EXTRACT(DAY FROM SYSDATE) AND"+
				" EXTRACT(HOUR FROM SOL.FECHA) <= "+sHoraCorte+")"+		
			  	" GROUP BY"+ 
			  	" SOL.REG_ABOGADO"+
			  	" ORDER BY NRO_SOLICITUDES ASC";
			
			try {			
				Query query = SpringInit.devolverSession().createSQLQuery(hql);									
				List<Object[]> lstEstudios = query.list();			
				System.out.println("Tamaño result:" + lstEstudios.size());
				if(lstEstudios.size()>0){
					resultAbogado = (String) lstEstudios.get(0)[0];
				}
			} catch (Exception e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}
		} 
		
		return resultAbogado;
		
	}

	private String getRandomAbogado() {
		
		String regAbogado = null;
		GenericDao<TiivsMiembro, Object> abogadoDAO = (GenericDao<TiivsMiembro, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);			
		TiivsGrupo grupoJuridico = new TiivsGrupo();
		grupoJuridico.setCodGrupo(ConstantesVisado.COD_GRUPO_JRD);
		filtro.add(Restrictions.eq("tiivsGrupo", grupoJuridico));
		List<TiivsMiembro> lstAbogados= new ArrayList<TiivsMiembro>();				
		try {
			lstAbogados = abogadoDAO.buscarDinamico(filtro);
			regAbogado = lstAbogados.get( 0 + (int)(Math.random()*lstAbogados.size())).getCodMiembro();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return regAbogado;
	}

	//Obtiene hora de corte de la BD
	private String getHoraCorte() {
		
		String sHoraCorte = null;
		GenericDao<TiivsMultitabla, Object> multiTablaDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);		
		TiivsMultitablaId multId = new TiivsMultitablaId(ConstantesVisado.CODIGO_TABLA_HORA_CORTE, 
				ConstantesVisado.CODIGO_CAMPO_HORA_CORTE);		
		filtro.add(Restrictions.eq("id", multId));
		List<TiivsMultitabla> multitabla= new ArrayList<TiivsMultitabla>();				
		try {
			multitabla = multiTablaDAO.buscarDinamico(filtro);
			sHoraCorte = multitabla.get(0).getValor1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sHoraCorte;
	}	
}

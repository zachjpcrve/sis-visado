package com.bbva.persistencia.generica.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.MiembroDto;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

public abstract  class SolicitudDaoImpl <K, T extends Serializable> 
                              extends GenericDaoImpl<K, Serializable> implements SolicitudDao<K,Serializable>{

	
	
	public SolicitudDaoImpl() {
	}
	
	
	
	@SuppressWarnings("unchecked")
	public  String obtenerPKNuevaSolicitud() throws Exception{
		
		final String  sql ="select LPAD(nvl(MAX(COD_SOLI+1),1),7,'0')COD_SOLI from TIIVS_SOLICITUD";
		
		String codigoSol="";
		List ResultList = (List)getHibernateTemplate().execute(new HibernateCallback() {
				public List doInHibernate(Session session) throws HibernateException {
				SQLQuery sq =session.createSQLQuery(sql);
				return sq.list();
				}});

				if(ResultList.size()>0){
				for(int i=0;i<=ResultList.size()-1;i++){
					codigoSol=  (String) ResultList.get(i);
				
				}
	           }
		return codigoSol;
				
	}
	
	public String obtenerMaximoMovimiento(String codSolicitud) throws Exception{
     
		final String  sql ="select max(cast(movimiento as number))movimiento from tiivs_hist_solicitud where cod_soli='"+codSolicitud+"' group by cod_soli";
		
		String movimiento="";
		BigDecimal nMovimiento=null;
		List ResultList = (List)getHibernateTemplate().execute(new HibernateCallback() {
				public List doInHibernate(Session session) throws HibernateException {
				SQLQuery sq =session.createSQLQuery(sql);
				return sq.list();
				}});

				if(ResultList.size()>0){
				for(int i=0;i<=ResultList.size()-1;i++){
					nMovimiento =  (BigDecimal) ResultList.get(i);
				
				}
	           }
		if(nMovimiento!=null){
			movimiento = nMovimiento.toString();
		}
		return movimiento;


	}
	public TiivsSolicitud obtenerTiivsSolicitud(TiivsSolicitud solicitud) throws Exception{
		GenericDao<TiivsSolicitud, Object>service=(GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
	    solicitud= service.buscarById(TiivsSolicitud.class, solicitud.getCodSoli());
	return solicitud;
	}
	
	public List<TiivsSolicitudOperban> obtenerListarOperacionesBancarias(TiivsSolicitud solicitud) throws Exception{
		List<TiivsSolicitudOperban> lstOperacionesBancarias=new ArrayList<TiivsSolicitudOperban>();
		GenericDao<TiivsSolicitudOperban, Object>service=(GenericDao<TiivsSolicitudOperban, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsSolicitudOperban.class);
		filtro.add(Restrictions.eq("id.codSoli", solicitud.getCodSoli()));
		return lstOperacionesBancarias=service.buscarDinamico(filtro);
	}
	public List<TiivsAnexoSolicitud> obtenerListarAnexosSolicitud(TiivsSolicitud solicitud) throws Exception{
		List<TiivsAnexoSolicitud> lstAnexoSolicitud=new ArrayList<TiivsAnexoSolicitud>();
		GenericDao<TiivsAnexoSolicitud, Object>service=(GenericDao<TiivsAnexoSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsAnexoSolicitud.class);
		filtro.add(Restrictions.eq("id.codSoli", solicitud.getCodSoli()));
		return lstAnexoSolicitud=service.buscarDinamico(filtro);
	}
}

package com.bbva.persistencia.generica.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.MiembroDto;

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
	
}

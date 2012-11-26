package com.bbva.persistencia.generica.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;


public abstract class GenericDaoImpl<K, T extends Serializable> 
									extends HibernateDaoSupport  implements GenericDao<K, T> {
	
	public GenericDaoImpl() {
		super();
	}
	
	public Connection getConnection() throws Exception {
		Connection cnx = null;
		Session hibernateSession = null;
		try {
			hibernateSession = (Session) getHibernateTemplate().getSessionFactory().getCurrentSession();
			cnx = hibernateSession.getSessionFactory().openStatelessSession().connection();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			throw new Exception(e);
		}
		return cnx;
	}
	
  
	public  void eliminar(K objeto)   throws Exception{
		try {
			getHibernateTemplate().delete(getHibernateTemplate().contains(objeto) ? objeto : getHibernateTemplate().merge(objeto));
		}
		catch (Exception e) {
			throw new Exception(e);
		}
		
	}
	
	public  K insertar(K objeto) throws Exception{
		getHibernateTemplate().persist(objeto);
		
		return objeto;
	}
	
	public  K save(K objeto) throws Exception{
		getHibernateTemplate().save(objeto);
		
		return objeto;
	}

	public  K modificar(K objeto) throws Exception{
		getHibernateTemplate().update(objeto);
		return objeto;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  List<K> buscarDinamico(final Busqueda filtro) throws Exception{
		   return (List<K>)this.getHibernateTemplate().execute(new HibernateCallback() {
	           
	            public Object  doInHibernate(Session session) throws HibernateException, SQLException {
	                Criteria busqueda = filtro.getExecutableCriteria(session);
	                busqueda.setFirstResult(((Busqueda)filtro).getFirstResult());
	                if (filtro.getMaxResults() > 0)
	                    busqueda.setMaxResults(((Busqueda)filtro).getMaxResults());
	                return (List<K>)busqueda.list(); 
	            }
	        });
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  K buscarById(Class clazz, Serializable id) throws Exception{
		return (K)getHibernateTemplate().get( clazz,  id);
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  List<K> buscarSQLNativo(final String sql) throws Exception{
		/*Session sesion=getHibernateTemplate().getSessionFactory().getCurrentSession();
	return 	(ArrayList<K>) sesion.createSQLQuery(sql);*/
		List ResultList = (List)getHibernateTemplate().execute(
				new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
				SQLQuery sq =session.createSQLQuery(sql);
				/*sq.addScalar("TEST_TABLE_ID", Hibernate.INTEGER);
				sq.addScalar("NAME", Hibernate.STRING);
				sq.addScalar("TEST_DATE", Hibernate.DATE);*/
				return sq.list();
				}});

				if(ResultList.size()>0){
					System.out.println("ResultList.size "+ResultList.size());
				for(int i=0;i<=ResultList.size();i++){
				Object[] row = (Object[]) ResultList.get(i);
				int id = (Integer)row[0];
				String name = (String)row[1];
				Date date = (Date)row[2];
				}
	}
				return ResultList;
				
				
				
	}
}



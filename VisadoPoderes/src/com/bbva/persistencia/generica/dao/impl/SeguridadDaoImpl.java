package com.bbva.persistencia.generica.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.bbva.persistencia.generica.dao.SeguridadDao;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.MiembroDto;


public abstract class SeguridadDaoImpl<K, T extends Serializable> 
									extends GenericDaoImpl<K, Serializable> implements SeguridadDao<K, Serializable>{
	
	public SeguridadDaoImpl() {
		super();
	}
	

	@SuppressWarnings("unchecked")
	public  List<MiembroDto> buscarMiembroSql(IILDPeUsuario usuarioIILD) throws Exception{
		
		final String  sql ="select M.COD_MIEMBRO, M.DESCRIPCION, M.CRITERIO, M.COD_GRUPO, MT.VALOR1 AS DES_CRITERIO, M.ESTUDIO" +
 		" from IIVS.TIIVS_MIEMBRO M " +
 		" left join IIVS.TIIVS_MULTITABLA MT on M.CRITERIO = MT.COD_MULT || MT.COD_ELEM " +
 		" where (trim(m.cod_miembro) = '"+usuarioIILD.getUID().trim()+"' and trim(m.criterio) = 'T030001') " +
 		" OR (trim(m.cod_miembro)= '"+usuarioIILD.getCargo().getCodigo().trim()+"' and trim(m.criterio) = 'T030002') " +
 		" OR (trim(m.cod_miembro)= '"+usuarioIILD.getBancoOficina().getCodigo().trim()+"' and trim(m.criterio) = 'T030003')"	;
       System.out.println("SQL : "+sql);
		
		List<MiembroDto> listaMiembro=new ArrayList<MiembroDto>();
		MiembroDto nuevo;
		List ResultList = (ArrayList<MiembroDto>)getHibernateTemplate().execute(new HibernateCallback() {
				public List<Object> doInHibernate(Session session) throws HibernateException {
				SQLQuery sq =session.createSQLQuery(sql);
				return sq.list();
				}});

				if(ResultList.size()>0){
				System.out.println("ResultList.size "+ResultList.size());
				for(int i=0;i<=ResultList.size()-1;i++){
				  Object[] row =  (Object[]) ResultList.get(i);
				   nuevo = new MiembroDto();
				nuevo.setCOD_MIEMBRO(row[0].toString());
				nuevo.setDESCRIPCION(row[1].toString());
				nuevo.setCRITERIO(row[2].toString());
				nuevo.setCOD_GRUPO(row[3].toString());
				nuevo.setDES_CRITERIO(row[4].toString());
				nuevo.setESTUDIO(row[5].toString());
				listaMiembro.add(nuevo);
				}
	           }
		return listaMiembro;
				
	}
}



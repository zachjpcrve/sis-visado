package com.bbva.persistencia.generica.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.bbva.persistencia.generica.dao.SeguridadDao;
import com.hildebrando.visado.dto.MiembroDto;

/**
 * Clase encargada de manejar la seguridad y validar el usuario  que accede 
 * a la aplicación contra las tablas de BD (VISPOD). Implementa
 * la interface {@link SeguridadDao} y extiende la clase generica {@link GenericDaoImpl}
 * @author 
 * @version 1.0
 */

public abstract class SeguridadDaoImpl<K, T extends Serializable> 
									extends GenericDaoImpl<K, Serializable> implements SeguridadDao<K, Serializable>{
	
	private static final Logger logger = Logger.getLogger(SeguridadDaoImpl.class);
	
	public SeguridadDaoImpl() {
		super();
	}
	
	//public  List<MiembroDto> buscarMiembroSql(IILDPeUsuario usuarioIILD) throws Exception;
	//public  List<MiembroDto> buscarMiembroSql(Ldapperu2 usuarioIILD) throws Exception;
	
	/**
	 * Metodo encargado de validar los datos del usuario de la siguiente por 3 criterios, 
	 * T030003 Por Oficina, T030002	Por Categoria y T030001	Por Registro.
	 * @param usuario Representa el usuario a consultar del tipo {@link Usuario}
	 * @return listaMiembro Lista con el usuario recuperado del tipo {@link MiembroDto}
	 * */
	@SuppressWarnings("unchecked")
	public  List<MiembroDto> buscarMiembroSql(com.grupobbva.seguridad.client.domain.Usuario usuario) throws Exception{
		//logger.info(" *************** En el buscarMiembroSql **************************: ");
		String usu="";
		String puesto ="";
		String oficinaId="";
		
		if(usuario.getUsuarioId()!=null){
			usu=usuario.getUsuarioId().trim();
		}
		if(usuario.getPuestoId()!=null){
			puesto=usuario.getPuestoId().trim();
		}
		if(usuario.getOficinaId()!=null){
			oficinaId= usuario.getOficinaId().trim();
		}
		
		final String  sql ="SELECT M.COD_MIEMBRO, M.DESCRIPCION, M.CRITERIO, M.COD_GRUPO, MT.VALOR1 AS DES_CRITERIO, M.ESTUDIO" +
		 		" FROM VISPOD.TIIVS_MIEMBRO M " +
		 		" LEFT JOIN VISPOD.TIIVS_MULTITABLA MT on M.CRITERIO = MT.COD_MULT || MT.COD_ELEM " +
		 		" WHERE (TRIM(m.cod_miembro) = '"+  usu +"' AND TRIM(m.criterio) = 'T030001') " +
		 		" OR (TRIM(m.cod_miembro)= '"+puesto +"' " +
		 		" AND TRIM(m.criterio) = 'T030002') " +
		 		" OR (TRIM(m.cod_miembro)= '"+oficinaId+"' AND TRIM(m.criterio) = 'T030003')"	+
		 		" AND ACTIVO = 1 ";
		
		logger.info("Sql: "+sql);		
		
		List<MiembroDto> listaMiembro=new ArrayList<MiembroDto>();
		MiembroDto nuevo;
		List listResult = (ArrayList<MiembroDto>)getHibernateTemplate().execute(new HibernateCallback() {
				public List<Object> doInHibernate(Session session) throws HibernateException {
					SQLQuery sq =session.createSQLQuery(sql);
					return sq.list();
				}});

				if(listResult.size()>0){
					logger.info("Se ha encontrado: ["+listResult.size()+"] resultados.");
					for(int i=0;i<=listResult.size()-1;i++){
						Object[] row =  (Object[]) listResult.get(i);
						nuevo = new MiembroDto();
						nuevo.setCOD_MIEMBRO(row[0]==null?null:row[0].toString());
						nuevo.setDESCRIPCION(row[1]==null?null:row[1].toString());
						nuevo.setCRITERIO(row[2]==null?null:row[2].toString());
						nuevo.setCOD_GRUPO(row[3]==null?null:row[3].toString());
						nuevo.setDES_CRITERIO(row[4]==null?null:row[4].toString());
						nuevo.setESTUDIO(row[5]==null?null:row[5].toString());
						
						listaMiembro.add(nuevo);
					}
	           }
		return listaMiembro;				
	}
}



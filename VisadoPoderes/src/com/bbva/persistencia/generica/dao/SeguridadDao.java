package com.bbva.persistencia.generica.dao;

import java.util.List;

import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.MiembroDto;
import com.hildebrando.visado.modelo.Ldapperu2;

public interface SeguridadDao<K, T> extends GenericDao<K, T>{
	

	public  List<MiembroDto> buscarMiembroSql(IILDPeUsuario usuarioIILD) throws Exception;
	//public  List<MiembroDto> buscarMiembroSql(Ldapperu2 usuarioIILD) throws Exception;
	
}

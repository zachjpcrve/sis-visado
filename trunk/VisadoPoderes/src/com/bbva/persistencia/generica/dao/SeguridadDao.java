package com.bbva.persistencia.generica.dao;

import java.util.List;

import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.hildebrando.visado.dto.MiembroDto;

public interface SeguridadDao<K, T> extends GenericDao<K, T>{
	

	public  List<MiembroDto> buscarMiembroSql(IILDPeUsuario usuarioIILD) throws Exception;
}

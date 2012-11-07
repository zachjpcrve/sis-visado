package com.bbva.persistencia.generica.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

public interface GenericDao<K, T> {
	
	public abstract Connection getConnection() throws Exception;
	
	public abstract void eliminar(K objeto)  throws Exception;

	public abstract K insertar(K objeto) throws Exception;
	
	public abstract K save(K objeto) throws Exception;

	public abstract K modificar(K objeto) throws Exception;
	
	public abstract List<K> buscarDinamico(final Busqueda filtro) throws Exception;

	@SuppressWarnings("unchecked")
	public abstract K buscarById(Class clazz, Serializable id) throws Exception;

	
}

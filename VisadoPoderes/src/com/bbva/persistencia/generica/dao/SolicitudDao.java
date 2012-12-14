package com.bbva.persistencia.generica.dao;

public interface SolicitudDao<K, T> extends GenericDao<K, T>{ 

	public  String obtenerPKNuevaSolicitud() throws Exception;
}

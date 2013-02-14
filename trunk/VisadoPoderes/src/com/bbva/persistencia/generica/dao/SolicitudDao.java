package com.bbva.persistencia.generica.dao;

import java.util.Date;
import java.util.List;

import com.hildebrando.visado.modelo.SolicitudesOficina;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

public interface SolicitudDao<K, T> extends GenericDao<K, T>{ 

	public  String obtenerPKNuevaSolicitud() throws Exception;
	public String obtenerMaximoMovimiento(String codSolicitud) throws Exception;
	public TiivsSolicitud obtenerTiivsSolicitud(TiivsSolicitud solicitud) throws Exception;
	public List<TiivsSolicitudOperban> obtenerListarOperacionesBancarias(TiivsSolicitud solicitud) throws Exception;
	public List<TiivsAnexoSolicitud> obtenerListarAnexosSolicitud(TiivsSolicitud solicitud) throws Exception;
	public List<SolicitudesOficina> obtenerListarTotalSolicitudesxEstado(TiivsSolicitud solicitud,Date dFechaInicio, Date dFechaFin) throws Exception;
}

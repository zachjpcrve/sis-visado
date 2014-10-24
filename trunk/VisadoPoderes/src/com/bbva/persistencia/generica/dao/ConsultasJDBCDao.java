package com.bbva.persistencia.generica.dao;

import java.sql.Connection;
import java.util.List;

import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsTerritorio;
import com.hildebrando.visado.modelo.TiivsTiempo;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

	/* CAMBIO HVB 25/07/2014
	 * SE CREA LA CLASE PARA REALIZAR CONSULTAS POR MEDIO DE JDBC 
	 * PARA OPTIMIZAR TIEMPOS
	 */
public interface ConsultasJDBCDao 
{
	public abstract Connection getConnection();
	public List<TiivsSolicitud> consultarSolicitud(int maxResultados,String filtros);
//	public List<TiivsSolicitud> consultarSolicitud();
	public List<TiivsMiembro> obtenerAbogados();
	public List<TiivsOperacionBancaria>obtenerOperacionesBancarias();
	public List<TiivsTipoSolicitud>obtenerTiposSolicitud();
	public List<TiivsTerritorio>obtenerTerritorios();
	public List<TiivsOficina1>obtenerOficinas(int pMaxResultados);
	public List<TiivsSolicitudOperban>obtenerSolicitudesxOperacionesBancarias();
	public List<TiivsNivel>obtenerNiveles();
	public List<TiivsEstudio>obtenerEstudios();
	public List<TiivsTiempo>obtenerTiempos();
	public List<TiivsSolicitudNivel>obtenerSolicitudesxNiveles();
}

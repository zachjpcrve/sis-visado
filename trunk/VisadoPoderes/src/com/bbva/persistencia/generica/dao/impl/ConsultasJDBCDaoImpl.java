package com.bbva.persistencia.generica.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.ConsultasJDBCDao;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudNivel;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsSolicitudOperbanId;
import com.hildebrando.visado.modelo.TiivsTerritorio;
import com.hildebrando.visado.modelo.TiivsTiempo;
import com.hildebrando.visado.modelo.TiivsTipoSolicitud;

public class ConsultasJDBCDaoImpl implements ConsultasJDBCDao{

	public static Logger logger = Logger.getLogger(ConsultasJDBCDaoImpl.class);
	private Connection conexionVisado;
	
	/*
	 * CAMBIO 25/07/2014 HVB
	 * SE IMPLEMENTAN CONSULTAS JDBC PARA OPTIMIZAR TIEMPO
	 */
	
	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		  Context initialContext;
		  Connection con = null;
		try 
		{
			initialContext = new InitialContext();
			//DataSource datasource = (DataSource)initialContext.lookup("java:comp/env/jdbc/VISPOD");
			DataSource datasource = (DataSource)initialContext.lookup("jdbc/VISPOD");
			con = datasource.getConnection();
			if(con != null)
				con.setAutoCommit(false);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			logger.info("Error al crear la cadena de conexion JBDC en VISADO"+e);
		}
		return con;
	}

	@Override
	public List<TiivsMiembro> obtenerAbogados() {
		// TODO Auto-generated method stub
		
		String query = "SELECT COD_MIEMBRO,DESCRIPCION,CRITERIO,COD_GRUPO,ESTUDIO,ACTIVO" +
				" FROM VISPOD.TIIVS_MIEMBRO" +
				" WHERE COD_GRUPO = ?";
		List<TiivsMiembro>lstAbogados = new ArrayList<TiivsMiembro>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			ps.setString(1,ConstantesVisado.CODIGO_GRUPO_ABOGADOS);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsMiembro abogado = new TiivsMiembro();
				abogado.setActivo(rs.getString("ACTIVO"));
				abogado.setCodMiembro(rs.getString("COD_MIEMBRO"));
				abogado.setDescripcion(rs.getString("DESCRIPCION"));
				abogado.setEstudio(rs.getString("ESTUDIO"));
				abogado.setCriterio(rs.getString("CRITERIO"));
				//abogado.setTiivsGrupo(rs.getString("COD_MIEMBRO"))
				lstAbogados.add(abogado);
			}

		} 

		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener abogados x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstAbogados;
	}

	@Override
	public List<TiivsOperacionBancaria> obtenerOperacionesBancarias() {
		// TODO Auto-generated method stub
		String query = "SELECT COD_OPER_BAN,DES_OPER_BAN,ACTIVO,TIPO" +
		" FROM VISPOD.TIIVS_OPERACION_BANCARIA";
		List<TiivsOperacionBancaria>lstOperacionesBancarias = new ArrayList<TiivsOperacionBancaria>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsOperacionBancaria opBancaria = new TiivsOperacionBancaria();
				opBancaria.setActivo(rs.getString("ACTIVO"));
				opBancaria.setCodOperBan(rs.getString("COD_OPER_BAN"));
				opBancaria.setDesOperBan(rs.getString("DES_OPER_BAN").toUpperCase());
				opBancaria.setTipo(rs.getString("TIPO"));
				lstOperacionesBancarias.add(opBancaria);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener operacionesBancarias x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstOperacionesBancarias;

	}

	@Override
	public List<TiivsTipoSolicitud> obtenerTiposSolicitud() {
		// TODO Auto-generated method stub
		
		String query = "SELECT COD_TIP_SOLIC,DES_TIP_SERVICIO,ACTIVO" +
		" FROM VISPOD.TIIVS_TIPO_SOLICITUD"+
		" WHERE ACTIVO = ?";
		List<TiivsTipoSolicitud> lstTiposSolicitud = new ArrayList<TiivsTipoSolicitud>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			ps.setString(1,"1");
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsTipoSolicitud tipoSolicitud = new TiivsTipoSolicitud();
				tipoSolicitud.setActivo(rs.getString("ACTIVO").charAt(0));
				tipoSolicitud.setCodTipSolic(rs.getString("COD_TIP_SOLIC"));
				tipoSolicitud.setDesTipServicio(rs.getString("DES_TIP_SERVICIO").toUpperCase());
				lstTiposSolicitud.add(tipoSolicitud);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener tiposSolicitud x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstTiposSolicitud;
	}

	@Override
	public List<TiivsTerritorio> obtenerTerritorios() {
		// TODO Auto-generated method stub
		
		String query = "SELECT COD_TER,DES_TER" +
		" FROM VISPOD.TIIVS_TERRITORIO";
		List<TiivsTerritorio>lstTerritorios = new ArrayList<TiivsTerritorio>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsTerritorio territorio = new TiivsTerritorio();
				territorio.setCodTer(rs.getString("COD_TER"));
				territorio.setDesTer(rs.getString("DES_TER").toUpperCase());
				lstTerritorios.add(territorio);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener territorios x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstTerritorios;

	}

	@Override
	public List<TiivsOficina1> obtenerOficinas(int pMaxResultados) {
		// TODO Auto-generated method stub
		
		String query = "SELECT COD_OFI,DES_OFI,ACTIVO,COD_TERR,DES_TER" +
		" FROM VISPOD.TIIVS_OFICINA1 O JOIN VISPOD.TIIVS_TERRITORIO T ON O.COD_TERR = T.COD_TER"+
		" WHERE O.COD_TERR IS NOT NULL AND O.ACTIVO=?" +
		" ORDER BY COD_OFI ASC";
		List<TiivsOficina1>lstOficinas = new ArrayList<TiivsOficina1>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			ps.setMaxRows(pMaxResultados);
			ps.setString(1,"1");
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsOficina1 oficina = new TiivsOficina1();
				oficina.setActivo(rs.getString("ACTIVO").charAt(0));
				oficina.setCodOfi(rs.getString("COD_OFI"));
				oficina.setDesOfi(rs.getString("DES_OFI").toUpperCase());
				oficina.setTiivsTerritorio(new TiivsTerritorio(rs.getString("COD_TERR"), rs.getString("DES_TER")));
//				oficina.set
				lstOficinas.add(oficina);
			}
		//	logger.info("[DAO]-lstOficinas: "+lstOficinas.size());
		
		} 
		
		catch (Exception e) 
		{
			logger.error("Error al obtener oficinas x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstOficinas;
	}

	@Override
	public List<TiivsSolicitudOperban> obtenerSolicitudesxOperacionesBancarias() {
		// TODO Auto-generated method stub
		String query = "SELECT COD_SOLI,COD_OPER_BAN,IMPORTE,MONEDA,TIPO_CAMBIO" +
		" FROM VISPOD.TIIVS_SOLICITUD_OPERBAN" +
		" ORDER BY COD_SOLI ASC";
		
		List<TiivsSolicitudOperban>lstSolicitudesxOperaciones = new ArrayList<TiivsSolicitudOperban>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsSolicitudOperban solxOper = new TiivsSolicitudOperban();
				TiivsSolicitudOperbanId id = new TiivsSolicitudOperbanId();
				id.setCodOperBan(rs.getString("COD_OPER_BAN"));
				id.setCodSoli(rs.getString("COD_SOLI"));
				id.setMoneda(rs.getString("MONEDA"));
				solxOper.setId(id);
				solxOper.setImporte(rs.getDouble("IMPORTE"));
				solxOper.setTipoCambio(rs.getDouble("TIPO_CAMBIO"));
				lstSolicitudesxOperaciones.add(solxOper);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener solxoperaciones x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstSolicitudesxOperaciones;
	}

	@Override
	public List<TiivsNivel> obtenerNiveles() {
		// TODO Auto-generated method stub
		String query = "SELECT ID,COD_NIV,DES_NIV,RANGO_INICIO,RANGO_FIN,MONEDA,FECHA_REG,USUARIO_REG,FECHA_ACT,USUARIO_ACT,ESTADO" +
		" FROM VISPOD.TIIVS_NIVEL";
		List<TiivsNivel>lstNivel = new ArrayList<TiivsNivel>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsNivel nivel = new TiivsNivel();
				nivel.setId(rs.getInt("ID"));
				nivel.setCodNiv(rs.getString("COD_NIV"));
				nivel.setDesNiv(rs.getString("DES_NIV").toUpperCase());
				nivel.setRangoInicio(rs.getInt("RANGO_INICIO"));
				nivel.setRangoFin(rs.getInt("RANGO_FIN"));
				nivel.setMoneda(rs.getString("MONEDA"));
				nivel.setFechaReg(rs.getDate("FECHA_REG"));
				nivel.setUsuarioReg(rs.getString("USUARIO_REG"));
				nivel.setFechaAct(rs.getDate("FECHA_ACT"));
				nivel.setUsuarioAct(rs.getString("USUARIO_ACT"));
				nivel.setEstado(rs.getInt("ESTADO"));
				lstNivel.add(nivel);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener niveles x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstNivel;
	}

	@Override
	public List<TiivsEstudio> obtenerEstudios() {
		// TODO Auto-generated method stub
		String query = "SELECT COD_ESTUDIO,DES_ESTUDIO,ACTIVO,COSTO" +
		" FROM VISPOD.TIIVS_ESTUDIO" +
		" WHERE ACTIVO = ?";
		List<TiivsEstudio>lstEstudios = new ArrayList<TiivsEstudio>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			ps.setString(1,"1");
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsEstudio estudio = new TiivsEstudio();
				estudio.setActivo(rs.getString("ACTIVO").charAt(0));
				estudio.setCodEstudio(rs.getString("COD_ESTUDIO"));
				estudio.setDesEstudio(rs.getString("DES_ESTUDIO").toUpperCase());
				estudio.setCosto(rs.getDouble("COSTO"));
				lstEstudios.add(estudio);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener estudio x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstEstudios;

	}

	@Override
	public List<TiivsTiempo> obtenerTiempos() {
		// TODO Auto-generated method stub
		String query = "SELECT TIEMPO_ID,NUMERO_DIA,NUMERO_MES,NOMBRE_MES,ANIO,FECHA_FORMATO" +
		" FROM VISPOD.TIIVS_TIEMPO";
		List<TiivsTiempo>lstTiempo = new ArrayList<TiivsTiempo>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsTiempo tiempo = new TiivsTiempo();
				tiempo.setTiempoId(rs.getInt("TIEMPO_ID"));
				tiempo.setNumeroDia(rs.getInt("TIEMPO_ID"));
				tiempo.setNumeroMes(rs.getInt("TIEMPO_ID"));
				tiempo.setNombreMes(rs.getString("NOMBRE_MES"));
				tiempo.setAnio(rs.getInt("ANIO"));
				tiempo.setFechaFormato(rs.getDate("FECHA_FORMATO"));
				lstTiempo.add(tiempo);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener tiempos x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstTiempo;
	}

	@Override
	public List<TiivsSolicitud> consultarSolicitud(int maxResultados,String filtros) 
	{
		// TODO Auto-generated method stub
		/* ERROR: GD-incidencias
		 * String query = "SELECT S.COD_SOLI,S.COD_OFI,S.ESTADO,S.IMPORTE,S.MONEDA,S.CADENA_TITULARES,S.CADENA_APODERADOS_HEREDEROS,S.CADENA_OPERACIONES" +
				",S.COD_ESTUDIO,E.DES_ESTUDIO,S.FECHA_ENVIO,S.FECHA_RESPUESTA,S.FECHA_ESTADO" +
				" FROM VISPOD.TIIVS_SOLICITUD S LEFT JOIN VISPOD.TIIVS_ESTUDIO E ON S.COD_ESTUDIO = E.COD_ESTUDIO" +
				" LEFT JOIN VISPOD.TIIVS_OFICINA1 O ON S.COD_OFI=O.COD_OFI";
		 * 
		 * */
		
		
		//CAMBIO HVB 14082014 SE AGREGAN CAMPOS AL QUERY S.COD_TIPO_SOLIC,TS.DES_TIP_SERVICIO,O.DES_OFI,O.COD_TERR,S.COMISION Y SE AGREGA LEFT CON LA TABLA TIPO SOLICITUD
		String query = "SELECT S.COD_SOLI,S.COD_TIPO_SOLIC,TS.DES_TIP_SERVICIO,S.COD_OFI,O.DES_OFI,O.COD_TERR,S.ESTADO,S.IMPORTE,S.MONEDA,S.CADENA_TITULARES,S.CADENA_APODERADOS_HEREDEROS,S.CADENA_OPERACIONES" +
				",S.COD_ESTUDIO,E.DES_ESTUDIO,S.FECHA_ENVIO,S.FECHA_RESPUESTA,S.FECHA_ESTADO,S.COMISION" +
				" FROM VISPOD.TIIVS_SOLICITUD S LEFT JOIN VISPOD.TIIVS_ESTUDIO E ON S.COD_ESTUDIO = E.COD_ESTUDIO" +
				" LEFT JOIN VISPOD.TIIVS_OFICINA1 O ON S.COD_OFI=O.COD_OFI" +
				" LEFT JOIN VISPOD.TIIVS_TIPO_SOLICITUD TS ON S.COD_TIPO_SOLIC = TS.COD_TIP_SOLIC";
			
				//SI EXISTEN FILTROS DE BUSQUEDA
				if(!filtros.isEmpty()) 
				{
					query+=" WHERE "+filtros;
				}
				
				query+=" ORDER BY S.COD_SOLI ASC";
		
		logger.info("QUERY: "+query);	
		List<TiivsSolicitud>lstSolicitudes = new ArrayList<TiivsSolicitud>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			if(maxResultados != -1) // si no se desea un maximo numero de registros se envia -1
			ps.setMaxRows(maxResultados);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsSolicitud solicitud = new TiivsSolicitud();
				solicitud.setCodSoli(rs.getString("COD_SOLI"));
				
				/*
				 * CAMBIO 14082014 HVB INICIO 
				 * SE AGREGA DESCRIPCION DE OFICINA,CODIGO DE TERRITORIO,DATOS DE TIPO
				 * DE SOLICITUD Y COMISION
				 */
				TiivsTipoSolicitud tipoSolicitud = new TiivsTipoSolicitud();
				tipoSolicitud.setCodTipSolic(rs.getString("COD_TIPO_SOLIC"));
				tipoSolicitud.setDesTipServicio(rs.getString("DES_TIP_SERVICIO"));
				solicitud.setTiivsTipoSolicitud(tipoSolicitud);
				
				TiivsOficina1 datosOficina = new TiivsOficina1();
				datosOficina.setCodOfi(rs.getString("COD_OFI"));
				datosOficina.setDesOfi(rs.getString("DES_OFI"));
				TiivsTerritorio datosTerritorio = new TiivsTerritorio();
				datosTerritorio.setCodTer(rs.getString("COD_TERR"));
				datosOficina.setTiivsTerritorio(datosTerritorio);
				solicitud.setTiivsOficina1(datosOficina);
				solicitud.setComision(rs.getDouble("COMISION"));
				/*
				 * CAMBIO 14082014 HVB FIN
				 *
				 */
				
				solicitud.setEstado(rs.getString("ESTADO"));
				solicitud.setImporte(rs.getDouble("IMPORTE"));
				solicitud.setMoneda(rs.getString("MONEDA"));
				solicitud.setTxtPoderdante(rs.getString("CADENA_TITULARES"));
				solicitud.setTxtApoderado(rs.getString("CADENA_APODERADOS_HEREDEROS"));
				solicitud.setTxtOpeBan(rs.getString("CADENA_OPERACIONES"));
				TiivsEstudio estudio =  new TiivsEstudio();
				estudio.setCodEstudio(rs.getString("COD_ESTUDIO"));
				estudio.setDesEstudio(rs.getString("DES_ESTUDIO"));
				solicitud.setTiivsEstudio(estudio);
				solicitud.setFechaEnvio(rs.getTimestamp("FECHA_ENVIO"));
				solicitud.setFechaRespuesta(rs.getTimestamp("FECHA_RESPUESTA"));
				solicitud.setFechaEstado(rs.getTimestamp("FECHA_ESTADO"));
				lstSolicitudes.add(solicitud);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener solicitudes x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstSolicitudes;
	}

	@Override
	public List<TiivsSolicitudNivel> obtenerSolicitudesxNiveles() 
	{
		// TODO Auto-generated method stub
		String query = "SELECT COD_SOLI,COD_NIV,ESTADO_NIVEL,USUARIO_REGISTRO,FECHA_REGISTRO,ESTADO_SOLICITUD,ID" +
		" FROM VISPOD.TIIVS_SOLICITUD_NIVEL" +
		" ORDER BY COD_SOLI ASC";
		
		List<TiivsSolicitudNivel>lstSolicitudesxNiveles = new ArrayList<TiivsSolicitudNivel>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try 
		{
			conexionVisado = getConnection();
			ps = conexionVisado.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				TiivsSolicitudNivel solxNiveles = new TiivsSolicitudNivel();
				solxNiveles.setTiivsSolicitud(new TiivsSolicitud(rs.getString("COD_SOLI")));
				solxNiveles.setCodNiv(rs.getString("COD_NIV"));
				solxNiveles.setEstadoNivel(rs.getString("ESTADO_NIVEL"));
				solxNiveles.setUsuarioRegistro(rs.getString("USUARIO_REGISTRO"));
				solxNiveles.setFechaRegistro(rs.getTimestamp("FECHA_REGISTRO"));
				solxNiveles.setEstadoSolicitud(rs.getString("ESTADO_SOLICITUD"));
				solxNiveles.setId(rs.getInt("ID"));
				
				lstSolicitudesxNiveles.add(solxNiveles);
			}
		
		} 
		
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.info("Error al obtener solxoperaciones x JDBC"+e);
		}
		
		finally
		{
			if(conexionVisado != null)
			{
				try {
					conexionVisado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(rs != null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
					
		
		return lstSolicitudesxNiveles;
	}

	
}

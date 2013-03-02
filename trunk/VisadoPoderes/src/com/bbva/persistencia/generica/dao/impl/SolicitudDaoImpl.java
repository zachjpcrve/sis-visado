package com.bbva.persistencia.generica.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.dao.SolicitudDao;
import com.hildebrando.visado.dto.AgrupacionDelegadosDto;
import com.hildebrando.visado.dto.AgrupacionPlazoDto;
import com.hildebrando.visado.modelo.Liquidacion;
import com.hildebrando.visado.modelo.RecaudacionTipoServ;
import com.hildebrando.visado.modelo.SolicitudesOficina;
import com.hildebrando.visado.modelo.SolicitudesTipoServicio;
import com.hildebrando.visado.modelo.TiivsAnexoSolicitud;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

public abstract class SolicitudDaoImpl<K, T extends Serializable> extends
		GenericDaoImpl<K, Serializable> implements
		SolicitudDao<K, Serializable> {
	
	public static Logger logger = Logger.getLogger(SolicitudDaoImpl.class);
	
	public SolicitudDaoImpl() {
	}

	@SuppressWarnings("unchecked")
	public String obtenerPKNuevaSolicitud() throws Exception {

		final String sql = "select LPAD(nvl(MAX(COD_SOLI+1),1),7,'0')COD_SOLI from TIIVS_SOLICITUD";

		String codigoSol = "";
		List ResultList = (List) getHibernateTemplate().execute(
				new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException {
						SQLQuery sq = session.createSQLQuery(sql);
						return sq.list();
					}
				});

		if (ResultList.size() > 0) {
			for (int i = 0; i <= ResultList.size() - 1; i++) {
				codigoSol = (String) ResultList.get(i);

			}
		}
		return codigoSol;

	}
	
	 
		@SuppressWarnings("unchecked")
		public List<AgrupacionDelegadosDto>  obtenerPKDelegados() throws Exception {

			final String sql = "select  distinct ni.des_niv, n.grupo, n.estado "+
					 " from tiivs_miembro_nivel n, tiivs_miembro m, tiivs_nivel ni "+
					" where  n.cod_miembro = m.cod_miembro "+
					  " and  n.cod_niv = ni.cod_niv "+
					 " and n.tipo_rol = 'D' "+
					 " group by ni.des_niv, n.grupo, n.estado, n.cod_miembro, m.descripcion "+
					  " order by ni.des_niv, n.grupo asc";

			
			List ResultList = (ArrayList<AgrupacionDelegadosDto>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public List doInHibernate(Session session)
								throws HibernateException {
							SQLQuery sq = session.createSQLQuery(sql);
							return sq.list();
						}
					});
			
			AgrupacionDelegadosDto nuevo=null;
			List<AgrupacionDelegadosDto> tmpLista=new ArrayList<AgrupacionDelegadosDto>();
			if (ResultList.size() > 0) {
				for(int i=0;i<=ResultList.size()-1;i++)
				{
				    Object[] row =  (Object[]) ResultList.get(i);
				    nuevo = new AgrupacionDelegadosDto();
				    
				    nuevo.setGrupo(row[1].toString());
				    nuevo.setDes_niv(row[0].toString());
				    if(row[2]== null){
				    	nuevo.setEstado("");
				    }else{
				    	nuevo.setEstado(row[2].toString());
				    }
				  
				    tmpLista.add(nuevo);
				}
			}
			return tmpLista;

		}
	@SuppressWarnings("unchecked")
	public List<AgrupacionDelegadosDto>  obtenerDelegados() throws Exception {

		final String sql = "select  ni.des_niv,n.grupo, n.cod_miembro, m.descripcion "
				+ " from tiivs_miembro_nivel n, tiivs_miembro m, tiivs_nivel ni  "
				+ " where  "
				+ "n.cod_miembro = m.cod_miembro  "
				+ "  and  n.cod_niv = ni.cod_niv "
				+ " and n.estado_miembro = '1' "
				+ " and n.tipo_rol = 'D' "
				+ " group by ni.des_niv,n.grupo,n.cod_miembro, m.descripcion "
				+ "  order by ni.des_niv,n.grupo asc";

		
		List ResultList = (ArrayList<AgrupacionDelegadosDto>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException {
						SQLQuery sq = session.createSQLQuery(sql);
						return sq.list();
					}
				});
		
		AgrupacionDelegadosDto nuevo=null;
		List<AgrupacionDelegadosDto> tmpLista=new ArrayList<AgrupacionDelegadosDto>();
		if (ResultList.size() > 0) {
			for(int i=0;i<=ResultList.size()-1;i++)
			{
			    Object[] row =  (Object[]) ResultList.get(i);
			    nuevo = new AgrupacionDelegadosDto();
			    
			    nuevo.setGrupo(row[1].toString());
			    nuevo.setDes_niv(row[0].toString());
			    nuevo.setCod_miembro(row[2].toString());
			    nuevo.setDescripcion(row[3].toString());
			  
			    tmpLista.add(nuevo);
			}
		}
		return tmpLista;

	}
	
	@SuppressWarnings("unchecked")
	public List<TiivsNivel> listarNivelesDistinct() throws Exception {
		List<TiivsNivel> listaNivel;
		TiivsNivel nivel;
		final String sql = "select distinct n.des_niv, n.cod_niv "
				+ "from tiivs_nivel n " + "order by n.cod_niv asc";

		List ResultList = (ArrayList<TiivsNivel>) getHibernateTemplate()
				.execute(new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException {
						SQLQuery sq = session.createSQLQuery(sql);
						return sq.list();
					}
				});
		nivel = null;
		listaNivel = new ArrayList<TiivsNivel>();
		if(ResultList.size()>0){
			for(int i = 0; i < ResultList.size(); i++){
				
				nivel = new TiivsNivel();
				Object[] row = (Object[]) ResultList.get(i);
				nivel.setDesNiv(row[0].toString());
				nivel.setCodNiv(row[1].toString());
				listaNivel.add(nivel);
			}
		}
		return listaNivel;
	}
	
	@SuppressWarnings("unchecked")
	public String obtenerMaximoMovimiento(String codSolicitud) throws Exception {

		final String sql = "select max(cast(movimiento as number))movimiento from tiivs_hist_solicitud where cod_soli='"
				+ codSolicitud + "' group by cod_soli";

		String movimiento = "";
		BigDecimal nMovimiento = null;
		List ResultList = (List) getHibernateTemplate().execute(
				new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException {
						SQLQuery sq = session.createSQLQuery(sql);
						return sq.list();
					}
				});

		if (ResultList.size() > 0) {
			for (int i = 0; i <= ResultList.size() - 1; i++) {
				nMovimiento = (BigDecimal) ResultList.get(i);

			}
		}
		if (nMovimiento != null) {
			movimiento = nMovimiento.toString();
		}
		return movimiento;

	}

	public TiivsSolicitud obtenerTiivsSolicitud(TiivsSolicitud solicitud)
			throws Exception 
	{
		GenericDao<TiivsSolicitud, Object> service = (GenericDao<TiivsSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		solicitud = service.buscarById(TiivsSolicitud.class,solicitud.getCodSoli());
		return solicitud;
	}

	public List<TiivsSolicitudOperban> obtenerListarOperacionesBancarias(
			TiivsSolicitud solicitud) throws Exception 
	{
		List<TiivsSolicitudOperban> lstOperacionesBancarias = new ArrayList<TiivsSolicitudOperban>();
		GenericDao<TiivsSolicitudOperban, Object> service = (GenericDao<TiivsSolicitudOperban, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsSolicitudOperban.class);
		filtro.add(Restrictions.eq("id.codSoli", solicitud.getCodSoli()));
		return lstOperacionesBancarias = service.buscarDinamico(filtro);
	}

	public List<TiivsAnexoSolicitud> obtenerListarAnexosSolicitud(
			TiivsSolicitud solicitud) throws Exception 
	{
		List<TiivsAnexoSolicitud> lstAnexoSolicitud = new ArrayList<TiivsAnexoSolicitud>();
		GenericDao<TiivsAnexoSolicitud, Object> service = (GenericDao<TiivsAnexoSolicitud, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsAnexoSolicitud.class);
		filtro.add(Restrictions.eq("id.codSoli", solicitud.getCodSoli()));
		return lstAnexoSolicitud = service.buscarDinamico(filtro);
	}
	
	@SuppressWarnings("unchecked")
	public List<SolicitudesTipoServicio> obtenerSolicitudesxTipoServicio(TiivsSolicitud solicitud, String idOpeBan,String cadTipoServ,String cadEstudio,String rangoImpG,
			Double importeIni,Double importeFin,Date dFechaInicio, Date dFechaFin) throws Exception
	{	
		logger.info("***************En el obtenerSolicitudesxTipoServicio*************************");
		String sql ="";
		String sCadFecha="";
		String sWhere="";
		List<SolicitudesTipoServicio> tmpLista = new ArrayList<SolicitudesTipoServicio>();
		
		if (solicitud!=null)
		{
			if (solicitud.getCodSoli().trim().compareTo("")!=0)
			{
				if (sWhere.compareTo("")!=0)
				{
					sWhere += " and so.cod_soli = '" + solicitud.getCodSoli() + "'";
				}
				else
				{
					sWhere = "where so.cod_soli = '" + solicitud.getCodSoli() + "'";
				}
 			}
			
			if (cadTipoServ.trim().compareTo("")!=0)
			{
				if (sWhere.compareTo("")!=0)
				{
					sWhere += " and ts.cod_tip_solic in ('" + cadTipoServ + "')";
				}
				else
				{
					sWhere = "where ts.cod_tip_solic in ('" + cadTipoServ + "')";
				}
			}		
			
			if (rangoImpG.trim().compareTo("")!=0)
			{
				if (sWhere.compareTo("")!=0)
				{
					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) 
					{
						logger.info("Filtro por importe: " + rangoImpG);
						
						sWhere += " and so.importe <= 50";
					}

					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
					{
						logger.debug("Filtro por importe: " + rangoImpG);
						
						sWhere += " and so.importe >50 and so.importe<=120";
					}

					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
					{
						logger.debug("Filtro por importe: " + rangoImpG);
						
						sWhere += " and so.importe >=120 and so.importe<=250";
					}

					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
					{
						logger.debug("Filtro por importe: " + rangoImpG);
						
						sWhere += " and so.importe >250";
					}
				}
				else
				{
					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MENOR_CINCUENTA)) 
					{
						logger.info("Filtro por importe: " + rangoImpG);
						
						sWhere = "where so.importe <= 50";
					}

					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CINCUENTA_MENOR_CIENTO_VEINTE)) 
					{
						logger.debug("Filtro por importe: " + rangoImpG);
						
						sWhere = "where so.importe >50 and so.importe<=120";
					}

					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_CIENTO_VEINTE_MENOR_DOSCIENTOS_CINCUENTA)) 
					{
						logger.debug("Filtro por importe: " + rangoImpG);
						
						sWhere = "where so.importe >=120 and so.importe<=250";
					}

					if (rangoImpG.equals(ConstantesVisado.ID_RANGO_IMPORTE_MAYOR_DOSCIENTOS_CINCUENTA)) 
					{
						logger.debug("Filtro por importe: " + rangoImpG);
						
						sWhere = "where so.importe >250";
					}
				}
			}
			
			if (importeIni!=0 && importeFin != 0)
			{
				if (sWhere.compareTo("")!=0)
				{
					sWhere += " and ob.importe between " + importeIni + " and " + importeFin;
				}
				else
				{
					sWhere = " where ob.importe between " + importeIni + " and " + importeFin;
				}
			}
			
			if (cadEstudio.trim().compareTo("")!=0)
			{
				if (sWhere.compareTo("")!=0)
				{
					sWhere += " and so.cod_estudio in ('" + cadEstudio + "')";
				}
				else
				{
					sWhere = " where so.cod_estudio in ('" + cadEstudio + "')";
				}
			}
			
			if (dFechaInicio!=null && dFechaFin!=null)
			{
				DateFormat formato = new SimpleDateFormat("dd/MM/yy");
				
				String tmpFecIni = formato.format(dFechaInicio);
				String tmpFecFin = formato.format(dFechaFin);
				
				sCadFecha = " and so.fecha between '" + tmpFecIni + "'" + " and '" + tmpFecFin + "'" +  " ";
				
				if (sWhere.compareTo("")!=0)
				{
					sWhere += sCadFecha;
				}
				else
				{
					sWhere = "where so.fecha between '" + tmpFecIni + "'" + " and '" + tmpFecFin + "'" +  " ";
				}
			}
			
			if (solicitud.getMoneda().trim().compareTo("")!=0)
			{
				if (sWhere.compareTo("")!=0)
				{
					sWhere += " and so.moneda = '" + solicitud.getMoneda() + "'";
				}
				else
				{
					sWhere = " where so.moneda = '" + solicitud.getMoneda() + "'";
				}
			}
			
			//Filtros de estados a buscar por RN048 del Doc Funcional
			if (sWhere.compareTo("")!=0)
			{
				sWhere += " and so.estado in ('" + ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02 + "'," +
											" '" + ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02 + "'," +
											" '" + ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02 + "'," +
											" '" + ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_T02 + "'," +
											" '" + ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02 + "')" ;
						
			}
			else
			{
				sWhere = " where so.estado in ('" + ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02 + "'," +
						" '" + ConstantesVisado.ESTADOS.ESTADO_COD_IMPROCEDENTE_T02 + "'," +
						" '" + ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02 + "'," +
						" '" + ConstantesVisado.ESTADOS.ESTADO_COD_REVOCADO_T02 + "'," +
						" '" + ConstantesVisado.ESTADOS.ESTADO_COD_VENCIDO_T02 + "')" ;
			}
			
			if (idOpeBan.trim().compareTo("")!=0)
			{
				sql = "select so.cod_soli,NVL(es.des_estudio,' ') Estudio, " +
					  "ts.des_tip_servicio tipo_servicio,op.cod_oper_ban, op.des_oper_ban tipo_operacion," +
					  "case  when mul.valor2 = 'PEN' then 'Soles' " +
					  "      when  mul.valor2 = 'USD' then 'Dolares' " +
					  "      when mul.valor2 = 'EUR' then 'Euros' " +
					  "end Moneda,	NVL(ob.importe,0) Importe, NVL(ob.tipo_cambio,0.0) Tipo_cambio, " +
					  "NVL((ob.tipo_cambio * ob.importe),0) Importe_Soles " +
					  "from tiivs_solicitud_operban ob " +
					  "left join tiivs_solicitud so on ob.cod_soli = so.cod_soli " +
					  "left join tiivs_estudio es on so.cod_estudio = es.cod_estudio " +
					  "left join tiivs_tipo_solicitud ts on so.cod_tipo_solic=ts.cod_tip_solic " +
					  "join tiivs_multitabla mul on so.moneda = mul.cod_elem and mul.cod_mult='T08' " +
					  "left join tiivs_operacion_bancaria op on ob.cod_oper_ban = op.cod_oper_ban " +
					  "where ob.cod_soli in ( " +
					  "select * from (select s.cod_soli from tiivs_solicitud s " +
					  "left join tiivs_solicitud_operban o on s.cod_soli=o.cod_soli " +
					  "where o.cod_oper_ban='" + idOpeBan  + "') " +
					  ") order by cod_soli";
			}
			else
			{
				//Aplicando filtros
				sql = "select so.cod_soli,NVL(es.des_estudio,' ') Estudio, " +
					  "ts.des_tip_servicio tipo_servicio, op.des_oper_ban tipo_operacion, " +
					  "case  when mul.valor2 = 'PEN' then 'Soles' " +
					  "      when  mul.valor2 = 'USD' then 'Dolares' " +
					  "      when mul.valor2 = 'EUR' then 'Euros' " +
					  "end Moneda,	NVL(ob.importe,0) Importe, NVL(ob.tipo_cambio,0.0) Tipo_cambio, " +
					  "NVL((ob.tipo_cambio * ob.importe),0) Importe_Soles " +
					  "from tiivs_solicitud so " +
					  "left join tiivs_estudio es on so.cod_estudio = es.cod_estudio " +
					  "left join tiivs_tipo_solicitud ts on so.cod_tipo_solic=ts.cod_tip_solic " +
					  "left join tiivs_solicitud_operban ob on so.cod_soli = ob.cod_soli " +
					  "left join tiivs_multitabla mul on so.moneda = mul.cod_elem and mul.cod_mult='T08' " +
					  "join tiivs_operacion_bancaria op on ob.cod_oper_ban = op.cod_oper_ban " + sWhere + 
					  " order by so.cod_soli";
			}
			
			
			logger.info("SQL : "+sql);
			 
			 final String sSQL=sql;
			
			 SolicitudesTipoServicio nuevo;
			 List ResultList = (ArrayList<SolicitudesTipoServicio>)getHibernateTemplate().execute(new HibernateCallback() 
			 {
					public List<Object> doInHibernate(Session session) throws HibernateException 
					{
						SQLQuery sq =session.createSQLQuery(sSQL);
						return sq.list();
					}
			 });
			 
			 if(ResultList.size()>0)
			 {
				logger.info("ResultList.size "+ResultList.size());
				for(int i=0;i<=ResultList.size()-1;i++)
				{
				    Object[] row =  (Object[]) ResultList.get(i);
				    nuevo = new SolicitudesTipoServicio();
				    
				    nuevo.setCodSolicitud(row[0].toString());
				    nuevo.setEstudio(row[1].toString());
				    nuevo.setTipoServicio(row[2].toString());
				    nuevo.setTipoOperacion(row[3].toString());
				    nuevo.setMoneda(row[4].toString());
				    
				    if (row[4].toString().toLowerCase().equals("soles") || row[4].toString().toLowerCase().equals("dolares"))
				    {
				    	nuevo.setImporte(buscarAbrevMoneda(row[4].toString()) + row[5].toString());
				    }
				    else
				    {
				    	nuevo.setImporte(row[5].toString() + buscarAbrevMoneda(row[4].toString()));
				    }
				    
				    nuevo.setTipoCambio(row[6].toString());
				    nuevo.setImporteSoles(row[7].toString());
				    
				    tmpLista.add(nuevo);
				}
				
				logger.info("Tamanio Lista "+tmpLista.size());
			 }
			 
		}
		
		return tmpLista;
	}
	
	private String buscarAbrevMoneda(String moneda)
	{
		String abrev="";
		
		if (moneda.toLowerCase().equals("soles"))
		{
			abrev="S/.";
		}
		
		if (moneda.toLowerCase().equals("dolares"))
		{
			abrev="$";
		}
		
		if (moneda.toLowerCase().equals("euros"))
		{
			abrev="€";
		}
		
		return abrev;
	}
	
	@SuppressWarnings("unchecked")
	public List<AgrupacionPlazoDto> obtenerLiquidacion (String cadEstudio, int anio, int mes, double impuesto) throws Exception
	{
		List<Liquidacion> tmpListaAT = new ArrayList<Liquidacion>();
		List<Liquidacion> tmpListaFT = new ArrayList<Liquidacion>();
		List<AgrupacionPlazoDto> tmpLista = new ArrayList<AgrupacionPlazoDto>();
		
		String sWhere = "";
		String sql = "";
		String estudio = "";
		
		int sumaAT=0;
		int sumaFT=0;
		int contFxEstudio=1;
		
		//boolean bAgregar=false;
		
		/*int contDia1AT=0, contDia2AT=0, contDia3AT=0,contDia4AT=0, contDia5AT=0,contDia6AT=0,contDia7AT=0, contDia8AT=0,contDia9AT=0,contDia10AT=0,
			contDia11AT=0, contDia12AT=0, contDia13AT=0,contDia14AT=0, contDia15AT=0,contDia16AT=0,contDia17AT=0, contDia18AT=0,contDia19AT=0,contDia20AT=0,
			contDia21AT=0, contDia22AT=0, contDia23AT=0,contDia24AT=0, contDia25AT=0,contDia26AT=0,contDia27AT=0, contDia28AT=0,contDia29AT=0,contDia30AT=0, contDia31AT=0;
		
		int contDia1FT=0, contDia2FT=0, contDia3FT=0,contDia4FT=0, contDia5FT=0,contDia6FT=0,contDia7FT=0, contDia8FT=0,contDia9FT=0,contDia10FT=0,
			contDia11FT=0, contDia12FT=0, contDia13FT=0,contDia14FT=0, contDia15FT=0,contDia16FT=0,contDia17FT=0, contDia18FT=0,contDia19FT=0,contDia20FT=0,
			contDia21FT=0, contDia22FT=0, contDia23FT=0,contDia24FT=0, contDia25FT=0,contDia26FT=0,contDia27FT=0, contDia28FT=0,contDia29FT=0,contDia30FT=0, contDia31FT=0;*/
		
		if (cadEstudio != null && anio!=0 && mes!= 0)
		{
			if (cadEstudio.compareTo("")!=0)
			{
				sWhere += " and so.cod_estudio in ('" + cadEstudio + "') ";
			}
			
			if (anio!=0)
			{
				sWhere += " and to_char(hst.fecha, 'YYYY')= '" + anio  + "' " ;
			}
			
			if (mes!=0)
			{
				sWhere += " and to_char(hst.fecha, 'MM')= '0" + mes  + "' " ;
			}
			
			/*sql="select es.des_estudio, es.costo, hst.fecha Fecha_atencion, " +
				"so.fecha_respuesta Fecha_RPTA, hst.reg_usuario " +
				"from tiivs_solicitud so " +
				"join tiivs_estudio es on so.cod_estudio = es.cod_estudio " +
				"left join tiivs_hist_solicitud hst on so.cod_soli = hst.cod_soli " +
				"join tiivs_miembro m on hst.reg_usuario = m.cod_miembro " +
				"where so.estado in " +
				"('0003','0009','0004','0017','0012','0010','0013','0015','0008') and " +
				"hst.estado in ('0003','0009','0004') " + sWhere +
				"order by so.cod_estudio";*/
			
			sql= "SELECT des_estudio,costo,dia_atencion, filtro, COUNT(filtro) contador " + 
				"FROM (select es.des_estudio, to_char(hst.fecha,'dd') dia_atencion, es.costo, " +
				"case when to_char(hst.fecha,'dd')<to_char(so.fecha_respuesta,'dd') then 'A' else 'B' END AS FILTRO " +
				"from tiivs_solicitud so " +
				"join tiivs_estudio es on so.cod_estudio = es.cod_estudio " +
				"left join tiivs_hist_solicitud hst on so.cod_soli = hst.cod_soli " +
				"join tiivs_miembro m on hst.reg_usuario = m.cod_miembro " +
				"where so.estado in ('0003','0009','0004','0017','0012','0010','0013','0015','0008') and hst.estado in ('0003','0009','0004') " + sWhere +
				"order by so.cod_estudio) A "  +
				"group by DES_ESTUDIO,dia_atencion,filtro,costo";
			
			 logger.info("SQL : "+sql);
			 
			 final String sSQL=sql;
			
			 Liquidacion objLiqAT;
			 Liquidacion objLiqFT;
			 AgrupacionPlazoDto objAgrp;
			 
			 List ResultList = (ArrayList<Liquidacion>)getHibernateTemplate().execute(new HibernateCallback() 
			 {
					public List<Object> doInHibernate(Session session) throws HibernateException 
					{
						SQLQuery sq =session.createSQLQuery(sSQL);
						return sq.list();
					}
			 });

			if(ResultList.size()>0)
			{
				for(int i=0;i<=ResultList.size()-1;i++)
				{
				    Object[] row =  (Object[]) ResultList.get(i);
				    objAgrp = new AgrupacionPlazoDto();
				    
				    if (!row[0].toString().equals(estudio))
				    {
				    	objAgrp.setEstudio(row[0].toString());
				    	estudio=row[0].toString();
				    }				    
				    
				    String dia = row[2].toString();
				    
				    if (row[3].toString().toUpperCase().equals("A"))
				    {
				    	 objLiqAT = new Liquidacion();
				    	 
				    	 if (dia.equals("01"))
				    	 {
				    		 objLiqAT.setTotalDia1(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("02"))
				    	 {
				    		 objLiqAT.setTotalDia2(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("03"))
				    	 {
				    		 objLiqAT.setTotalDia3(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("04"))
				    	 {
				    		 objLiqAT.setTotalDia4(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("05"))
				    	 {
				    		 objLiqAT.setTotalDia5(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("06"))
				    	 {
				    		 objLiqAT.setTotalDia6(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("07"))
				    	 {
				    		 objLiqAT.setTotalDia7(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("08"))
				    	 {
				    		 objLiqAT.setTotalDia8(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("09"))
				    	 {
				    		 objLiqAT.setTotalDia9(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("10"))
				    	 {
				    		 objLiqAT.setTotalDia10(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("11"))
				    	 {
				    		 objLiqAT.setTotalDia11(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("12"))
				    	 {
				    		 objLiqAT.setTotalDia12(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("13"))
				    	 {
				    		 objLiqAT.setTotalDia13(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("14"))
				    	 {
				    		 objLiqAT.setTotalDia14(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("15"))
				    	 {
				    		 objLiqAT.setTotalDia15(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("16"))
				    	 {
				    		 objLiqAT.setTotalDia16(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("17"))
				    	 {
				    		 objLiqAT.setTotalDia17(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("18"))
				    	 {
				    		 objLiqAT.setTotalDia18(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("19"))
				    	 {
				    		 objLiqAT.setTotalDia19(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("20"))
				    	 {
				    		 objLiqAT.setTotalDia20(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("21"))
				    	 {
				    		 objLiqAT.setTotalDia21(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("22"))
				    	 {
				    		 objLiqAT.setTotalDia22(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("23"))
				    	 {
				    		 objLiqAT.setTotalDia23(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("24"))
				    	 {
				    		 objLiqAT.setTotalDia24(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("25"))
				    	 {
				    		 objLiqAT.setTotalDia25(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("26"))
				    	 {
				    		 objLiqAT.setTotalDia26(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("27"))
				    	 {
				    		 objLiqAT.setTotalDia27(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("28"))
				    	 {
				    		 objLiqAT.setTotalDia28(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("29"))
				    	 {
				    		 objLiqAT.setTotalDia29(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("30"))
				    	 {
				    		 objLiqAT.setTotalDia30(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("31"))
				    	 {
				    		 objLiqAT.setTotalDia31(Integer.valueOf(row[4].toString())); 
						 }
				    	 
				    	 sumaAT=	objLiqAT.getTotalDia1() + objLiqAT.getTotalDia2() + objLiqAT.getTotalDia3()
				    		 		+ objLiqAT.getTotalDia4() + objLiqAT.getTotalDia5() + objLiqAT.getTotalDia6()
				    				+ objLiqAT.getTotalDia7() + objLiqAT.getTotalDia8() + objLiqAT.getTotalDia9()
				    				+ objLiqAT.getTotalDia10() + objLiqAT.getTotalDia11() + objLiqAT.getTotalDia12()
				    				+ objLiqAT.getTotalDia13() + objLiqAT.getTotalDia14() + objLiqAT.getTotalDia15()
				    				+ objLiqAT.getTotalDia16() + objLiqAT.getTotalDia17() + objLiqAT.getTotalDia18()
				    				+ objLiqAT.getTotalDia19() + objLiqAT.getTotalDia20() + objLiqAT.getTotalDia21()
				    				+ objLiqAT.getTotalDia22() + objLiqAT.getTotalDia23() + objLiqAT.getTotalDia24()
				    				+ objLiqAT.getTotalDia25() + objLiqAT.getTotalDia26() + objLiqAT.getTotalDia27()
				    				+ objLiqAT.getTotalDia28() + objLiqAT.getTotalDia29() + objLiqAT.getTotalDia30()
				    				+ objLiqAT.getTotalDia31();
					    	 
					    objLiqAT.setCosto(Double.valueOf(row[1].toString()));
					    objLiqAT.setTotalMes(sumaAT);
					    objLiqAT.setHonorarios(objLiqAT.getTotalMes()*objLiqAT.getCosto());
						objLiqAT.setImpuesto(objLiqAT.getHonorarios()*impuesto);
						objLiqAT.setTotal(objLiqAT.getHonorarios()+objLiqAT.getImpuesto());
						
						if (sumaAT>0)
						{
							tmpListaAT.add(objLiqAT);
							objAgrp.setLstSolAT(tmpListaAT);
						}
						
				    }
				    else
				    {
				    	 objLiqFT = new Liquidacion();
				    	 if (dia.equals("01"))
				    	 {
				    		 objLiqFT.setTotalDia1(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("02"))
				    	 {
				    		 objLiqFT.setTotalDia2(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("03"))
				    	 {
				    		 objLiqFT.setTotalDia3(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("04"))
				    	 {
				    		 objLiqFT.setTotalDia4(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("05"))
				    	 {
				    		 objLiqFT.setTotalDia5(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("06"))
				    	 {
				    		 objLiqFT.setTotalDia6(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("07"))
				    	 {
				    		 objLiqFT.setTotalDia7(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("08"))
				    	 {
				    		 objLiqFT.setTotalDia8(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("09"))
				    	 {
				    		 objLiqFT.setTotalDia9(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("10"))
				    	 {
				    		 objLiqFT.setTotalDia10(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("11"))
				    	 {
				    		 objLiqFT.setTotalDia11(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("12"))
				    	 {
				    		 objLiqFT.setTotalDia12(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("13"))
				    	 {
				    		 objLiqFT.setTotalDia13(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("14"))
				    	 {
				    		 objLiqFT.setTotalDia14(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("15"))
				    	 {
				    		 objLiqFT.setTotalDia15(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("16"))
				    	 {
				    		 objLiqFT.setTotalDia16(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("17"))
				    	 {
				    		 objLiqFT.setTotalDia17(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("18"))
				    	 {
				    		 objLiqFT.setTotalDia18(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("19"))
				    	 {
				    		 objLiqFT.setTotalDia19(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("20"))
				    	 {
				    		 objLiqFT.setTotalDia20(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("21"))
				    	 {
				    		 objLiqFT.setTotalDia21(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("22"))
				    	 {
				    		 objLiqFT.setTotalDia22(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("23"))
				    	 {
				    		 objLiqFT.setTotalDia23(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("24"))
				    	 {
				    		 objLiqFT.setTotalDia24(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("25"))
				    	 {
				    		 objLiqFT.setTotalDia25(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("26"))
				    	 {
				    		 objLiqFT.setTotalDia26(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("27"))
				    	 {
				    		 objLiqFT.setTotalDia27(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("28"))
				    	 {
				    		 objLiqFT.setTotalDia28(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("29"))
				    	 {
				    		 objLiqFT.setTotalDia29(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("30"))
				    	 {
				    		 objLiqFT.setTotalDia30(Integer.valueOf(row[4].toString())); 
						 }
				    	 if (dia.equals("31"))
				    	 {
				    		 objLiqFT.setTotalDia31(Integer.valueOf(row[4].toString())); 
						 }
				    	 
				    	 sumaFT=	objLiqFT.getTotalDia1() + objLiqFT.getTotalDia2() + objLiqFT.getTotalDia3()
				    		 		+ objLiqFT.getTotalDia4() + objLiqFT.getTotalDia5() + objLiqFT.getTotalDia6()
				    				+ objLiqFT.getTotalDia7() + objLiqFT.getTotalDia8() + objLiqFT.getTotalDia9()
				    				+ objLiqFT.getTotalDia10() + objLiqFT.getTotalDia11() + objLiqFT.getTotalDia12()
				    				+ objLiqFT.getTotalDia13() + objLiqFT.getTotalDia14() + objLiqFT.getTotalDia15()
				    				+ objLiqFT.getTotalDia16() + objLiqFT.getTotalDia17() + objLiqFT.getTotalDia18()
				    				+ objLiqFT.getTotalDia19() + objLiqFT.getTotalDia20() + objLiqFT.getTotalDia21()
				    				+ objLiqFT.getTotalDia22() + objLiqFT.getTotalDia23() + objLiqFT.getTotalDia24()
				    				+ objLiqFT.getTotalDia25() + objLiqFT.getTotalDia26() + objLiqFT.getTotalDia27()
				    				+ objLiqFT.getTotalDia28() + objLiqFT.getTotalDia29() + objLiqFT.getTotalDia30()
				    				+ objLiqFT.getTotalDia31();
					    	 
					    objLiqFT.setCosto(Double.valueOf(row[1].toString()));
					    objLiqFT.setTotalMes(sumaFT);
					    objLiqFT.setHonorarios(objLiqFT.getTotalMes()*objLiqFT.getCosto());
						objLiqFT.setImpuesto(objLiqFT.getHonorarios()*impuesto);
						objLiqFT.setTotal(objLiqFT.getHonorarios()+objLiqFT.getImpuesto());
						
						if (sumaFT>0)
						{
							tmpListaFT.add(objLiqFT);
							objAgrp.setLstSolFT(tmpListaFT);
						}
				    }
				    
				    //Agregar agrupacion
				    String id = String.valueOf(i);
				    objAgrp.setsId(id);
				    
				    tmpLista.add(objAgrp);
				}
							 
				logger.info("Tamanio Lista AT:" + tmpListaAT.size());
				logger.info("Tamanio Lista FT:" + tmpListaFT.size());
				logger.info("Tamanio Lista:" + tmpLista.size());
				
				AgrupacionPlazoDto subTotales=new AgrupacionPlazoDto();
				subTotales.setsId("");
				subTotales.setEstudio("");
				
				int totalDia1=0;
				int totalDia2=0;
				int totalDia3=0;
				int totalDia4=0;
				int totalDia5=0;
				int totalDia6=0;
				int totalDia7=0;
				int totalDia8=0;
				int totalDia9=0;
				int totalDia10=0;
				int totalDia11=0;
				int totalDia12=0;
				int totalDia13=0;
				int totalDia14=0;
				int totalDia15=0;
				int totalDia16=0;
				int totalDia17=0;
				int totalDia18=0;
				int totalDia19=0;
				int totalDia20=0;
				int totalDia21=0;
				int totalDia22=0;
				int totalDia23=0;
				int totalDia24=0;
				int totalDia25=0;
				int totalDia26=0;
				int totalDia27=0;
				int totalDia28=0;
				int totalDia29=0;
				int totalDia30=0;
				int totalDia31=0;
				int totalMes=0;
				double costo = 0.0;
				double honorarios = 0.0;
				double imp = 0.0;
				double total = 0.0;
				
				for (AgrupacionPlazoDto tmp: tmpLista)
				{
					if (tmp.getLstSolAT()!=null)
					{
						for (Liquidacion tmpAT: tmp.getLstSolAT())
						{
							totalDia1 += tmpAT.getTotalDia1();
							totalDia2 += tmpAT.getTotalDia2(); 
							totalDia3 += tmpAT.getTotalDia3(); 
							totalDia4 += tmpAT.getTotalDia4();
							totalDia5 += tmpAT.getTotalDia5();
							totalDia6 += tmpAT.getTotalDia6(); 
							totalDia7 += tmpAT.getTotalDia7(); 
							totalDia8 += tmpAT.getTotalDia8(); 
							totalDia9 += tmpAT.getTotalDia9(); 
							totalDia10 += tmpAT.getTotalDia10();
							totalDia11 += tmpAT.getTotalDia11();
							totalDia12 += tmpAT.getTotalDia12();
							totalDia13 += tmpAT.getTotalDia13();
							totalDia14 += tmpAT.getTotalDia14();
							totalDia15 += tmpAT.getTotalDia15();
							totalDia16 += tmpAT.getTotalDia16();
							totalDia17 += tmpAT.getTotalDia17();
							totalDia18 += tmpAT.getTotalDia18();
							totalDia19 += tmpAT.getTotalDia19();
							totalDia20 += tmpAT.getTotalDia20();
							totalDia21 += tmpAT.getTotalDia21();
							totalDia22 += tmpAT.getTotalDia22();
							totalDia23 += tmpAT.getTotalDia23();
							totalDia24 += tmpAT.getTotalDia24();
							totalDia25 += tmpAT.getTotalDia25();
							totalDia26 += tmpAT.getTotalDia26();
							totalDia27 += tmpAT.getTotalDia27();
							totalDia28 += tmpAT.getTotalDia28();
							totalDia29 += tmpAT.getTotalDia29();
							totalDia30 += tmpAT.getTotalDia30();
							totalDia31 += tmpAT.getTotalDia31();
							totalMes += tmpAT.getTotalMes();
							costo += tmpAT.getCosto();
							honorarios += tmpAT.getHonorarios() ;
							imp += tmpAT.getImpuesto() ;
							total += tmpAT.getTotal() ;
						}	
					}
					
					if (tmp.getLstSolFT()!=null)
					{
						for (Liquidacion tmpFT: tmp.getLstSolFT())
						{
							totalDia1 += tmpFT.getTotalDia1(); 
							totalDia2 += tmpFT.getTotalDia2();
							totalDia3 += tmpFT.getTotalDia3();
							totalDia4 += tmpFT.getTotalDia4();
							totalDia5 += tmpFT.getTotalDia5();
							totalDia6 += tmpFT.getTotalDia6();
							totalDia7 += tmpFT.getTotalDia7();
							totalDia8 += tmpFT.getTotalDia8();
							totalDia9 += tmpFT.getTotalDia9();
							totalDia10 += tmpFT.getTotalDia10();
							totalDia11 += tmpFT.getTotalDia11();
							totalDia12 += tmpFT.getTotalDia12();
							totalDia13 += tmpFT.getTotalDia13();
							totalDia14 += tmpFT.getTotalDia14();
							totalDia15 += tmpFT.getTotalDia15();
							totalDia16 += tmpFT.getTotalDia16();
							totalDia17 += tmpFT.getTotalDia17();
							totalDia18 += tmpFT.getTotalDia18();
							totalDia19 += tmpFT.getTotalDia19();
							totalDia20 += tmpFT.getTotalDia20();
							totalDia21 += tmpFT.getTotalDia21();
							totalDia22 += tmpFT.getTotalDia22();
							totalDia23 += tmpFT.getTotalDia23();
							totalDia24 += tmpFT.getTotalDia24();
							totalDia25 += tmpFT.getTotalDia25();
							totalDia26 += tmpFT.getTotalDia26();
							totalDia27 += tmpFT.getTotalDia27();
							totalDia28 += tmpFT.getTotalDia28();
							totalDia29 += tmpFT.getTotalDia29();
							totalDia30 += tmpFT.getTotalDia30();
							totalDia31 += tmpFT.getTotalDia31();
							totalMes += tmpFT.getTotalMes();
							costo += tmpFT.getCosto();
							honorarios += tmpFT.getHonorarios();
							imp += tmpFT.getImpuesto();
							total += tmpFT.getTotal();
						}
					}
				}
				
				subTotales.setSubTotalDia1(totalDia1);
				subTotales.setSubTotalDia2(totalDia2);
				subTotales.setSubTotalDia3(totalDia3);
				subTotales.setSubTotalDia4(totalDia4);
				subTotales.setSubTotalDia5(totalDia5);
				subTotales.setSubTotalDia6(totalDia6);
				subTotales.setSubTotalDia7(totalDia7);
				subTotales.setSubTotalDia8(totalDia8);				
				subTotales.setSubTotalDia9(totalDia9);
				subTotales.setSubTotalDia10(totalDia10);
				subTotales.setSubTotalDia11(totalDia11);
				subTotales.setSubTotalDia12(totalDia12);
				subTotales.setSubTotalDia13(totalDia13);
				subTotales.setSubTotalDia14(totalDia14);
				subTotales.setSubTotalDia15(totalDia15);
				subTotales.setSubTotalDia16(totalDia16);	
				subTotales.setSubTotalDia17(totalDia17);
				subTotales.setSubTotalDia18(totalDia18);
				subTotales.setSubTotalDia19(totalDia19);
				subTotales.setSubTotalDia20(totalDia20);
				subTotales.setSubTotalDia21(totalDia21);
				subTotales.setSubTotalDia22(totalDia22);
				subTotales.setSubTotalDia23(totalDia23);
				subTotales.setSubTotalDia24(totalDia24);
				subTotales.setSubTotalDia25(totalDia25);
				subTotales.setSubTotalDia26(totalDia26);
				subTotales.setSubTotalDia27(totalDia27);
				subTotales.setSubTotalDia28(totalDia28);
				subTotales.setSubTotalDia29(totalDia29);
				subTotales.setSubTotalDia30(totalDia30);
				subTotales.setSubTotalDia31(totalDia31);
				subTotales.setSubTotalMes(totalMes);
				subTotales.setCosto(costo);
				subTotales.setHonorarios(honorarios);
				subTotales.setImpuesto(imp);
				subTotales.setgTotal(total);				
				
				tmpLista.add(subTotales);
				logger.info("Tamanio Lista Final:" + tmpLista.size());
			}
		}
		
		return tmpLista;
	}
	
	@SuppressWarnings("unchecked")
	public List<RecaudacionTipoServ> obtenerListarRecaudacionxTipoServicio(TiivsSolicitud solicitud, Date dFechaInicio, Date dFechaFin) throws Exception
	{	
		logger.info("***************En el obtenerListarTotalSolicitudesxEstado*************************");
		String sql ="";
		//String sCadFecha="";
		List<RecaudacionTipoServ> tmpLista = new ArrayList<RecaudacionTipoServ>();
		
		if (solicitud!=null)
		{
			//Aplicando filtros
			String sWhere = "";
			
			if (solicitud.getTiivsOficina1().getTiivsTerritorio().getCodTer()!=null)
			{
				if (sWhere.compareTo("")!=0)
				{
					sWhere += " and ofi.cod_terr = '" + solicitud.getTiivsOficina1().getTiivsTerritorio().getCodTer() + "' ";
				}
				else
				{
					sWhere = " where ofi.cod_terr = '" + solicitud.getTiivsOficina1().getTiivsTerritorio().getCodTer() + "' ";
				}
			}
			
			if (solicitud.getTiivsOficina1().getCodOfi()!=null)
			{
				if (solicitud.getTiivsOficina1().getCodOfi()!=null && solicitud.getTiivsOficina1().getCodOfi().compareTo("")!=0)
				{
					if (sWhere.compareTo("")!=0)
					{
						sWhere += " and ofi.cod_ofi= '" + solicitud.getTiivsOficina1().getCodOfi() + "' " ; 
					}
					else
					{
						sWhere = " where ofi.cod_ofi= '" + solicitud.getTiivsOficina1().getCodOfi() + "' " ;
					}
				}
			}
			
			if (dFechaInicio!=null && dFechaFin!=null)
			{
				DateFormat formato = new SimpleDateFormat("dd/MM/yy");
				
				String tmpFecIni = formato.format(dFechaInicio);
				String tmpFecFin = formato.format(dFechaFin);
				
				if (sWhere.compareTo("")!=0)
				{
					sWhere += " and so.fecha between '" + tmpFecIni + "'" + " and '" + tmpFecFin + "'" +  " ";
				}
				else
				{
					sWhere = " where so.fecha between '" + tmpFecIni + "'" + " and '" + tmpFecFin + "'" +  " ";
				}				
			}
			
			//Filtros de estados a buscar por RN054 del Doc Funcional
			/*if (sWhere.compareTo("")!=0)
			{
				sWhere += " and so.estado in ('" + ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02 + "'," +
											" '" + ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02 + "'," +
											" '" + ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02 + "')" ;
						
			}
			else
			{
				sWhere = " where so.estado in ('" + ConstantesVisado.ESTADOS.ESTADO_COD_ACEPTADO_T02 + "'," +
						" '" + ConstantesVisado.ESTADOS.ESTADO_COD_RECHAZADO_T02 + "'," +
						" '" + ConstantesVisado.ESTADOS.ESTADO_COD_EN_VERIFICACION_A_T02 + "')" ;
			}*/
			
			sql="select distinct terr.des_ter, so.cod_ofi, ofi.des_ofi, NVL(PN.cont,0) Persona_Natural, " +
				"NVL((PN.cont*PN.valor2),0) Recaudacion, NVL(PJ.cont,0) Persona_Juridica,NVL((PJ.cont*PJ.valor2),0) Recaudacion1, " +
				"NVL(PF.cont,0) Persona_FallecidaX, NVL((PF.cont*PF.valor2),0) Recaudacion2, " +
				"NVL(PFX.cont,0) Persona_FallecidaX1, NVL((PFX.cont*PFX.valor2),0) Recaudacion3, " +
				"NVL((PN.cont*PN.valor2),0) + NVL((PJ.cont*PJ.valor2),0) + NVL((PF.cont*PF.valor2),0) + " +
				"NVL((PFX.cont*PFX.valor2),0) as Recaudacion_Total " +
				"from tiivs_solicitud so " +
				"left join tiivs_oficina1 ofi on so.cod_ofi = ofi.cod_ofi " +
				"join tiivs_territorio terr on ofi.cod_terr = terr.cod_ter " +
				"left join (select cod_ofi,multPN.valor2, count(so.tipo_comision) cont " + 
				"          from tiivs_solicitud so " +
				"          join tiivs_multitabla multPN on multPN.cod_elem = so.tipo_comision and multPN.cod_mult = 'T11' and " +
				"          multPN.cod_elem='0001' group by cod_ofi,multPN.valor2 " +
				"          ) PN on so.cod_ofi = PN.cod_ofi " +
				"left join (select cod_ofi,multPJ.valor2, count(so.tipo_comision) cont " +
				"          from tiivs_solicitud so " +
				"          join tiivs_multitabla multPJ on multPJ.cod_elem = so.tipo_comision and multPJ.cod_mult = 'T11' and " +
				"          multPJ.cod_elem='0002' group by cod_ofi,multPJ.valor2 " +
				"          ) PJ on so.cod_ofi = PJ.cod_ofi " +
				"left join (select cod_ofi,multPF.valor2, count(so.tipo_comision) cont " +
				"          from tiivs_solicitud so " +
				"          join tiivs_multitabla multPF on multPF.cod_elem = so.tipo_comision and multPF.cod_mult = 'T11' and " +
				"          multPF.cod_elem='0003' group by cod_ofi,multPF.valor2 " +
				"          ) PF on so.cod_ofi = PF.cod_ofi " +
				"left join (select cod_ofi,multPFX.valor2, count(so.tipo_comision) cont " +
				"          from tiivs_solicitud so " +
				"          join tiivs_multitabla multPFX on multPFX.cod_elem = so.tipo_comision and multPFX.cod_mult = 'T11' and " +
				"          multPFX.cod_elem='0004' group by cod_ofi,multPFX.valor2 " +
				"          ) PFX on so.cod_ofi = PFX.cod_ofi " + sWhere +
				"order by so.cod_ofi " ;
			 
			
			 logger.info("SQL : "+sql);
			 
			 final String sSQL=sql;
			
			 RecaudacionTipoServ objRecaudacion;
			 List ResultList = (ArrayList<RecaudacionTipoServ>)getHibernateTemplate().execute(new HibernateCallback() 
			 {
					public List<Object> doInHibernate(Session session) throws HibernateException 
					{
						SQLQuery sq =session.createSQLQuery(sSQL);
						return sq.list();
					}
			 });

			if(ResultList.size()>0)
			{
				logger.info("ResultList.size "+ResultList.size());
				for(int i=0;i<=ResultList.size()-1;i++)
				{
				    Object[] row =  (Object[]) ResultList.get(i);
				    objRecaudacion = new RecaudacionTipoServ();
				    
				    objRecaudacion.setTerritorio(row[0].toString());
				    objRecaudacion.setCodOficina(row[1].toString());
				    objRecaudacion.setOficina(row[2].toString());
				    objRecaudacion.setiContPersonasNaturales(Integer.valueOf(row[3].toString()));
				    objRecaudacion.setsTotalPersonasNat(Double.valueOf(row[4].toString()));
				    objRecaudacion.setiContPersonasJuridicas(Integer.valueOf(row[5].toString()));
				    objRecaudacion.setsTotalPersonasJurd(Double.valueOf(row[6].toString()));
				    objRecaudacion.setiContPersonasFallecX(Integer.valueOf(row[7].toString()));
				    objRecaudacion.setsTotalPersonasFallecX(Double.valueOf(row[8].toString()));
				    objRecaudacion.setiContPersonasFallecX1(Integer.valueOf(row[9].toString()));
				    objRecaudacion.setsTotalPersonasFallecX1(Double.valueOf(row[10].toString()));
				    objRecaudacion.setsRecaudacionTotal(Double.valueOf(row[11].toString()));
				    
					tmpLista.add(objRecaudacion);
				}
		     }
		}
		
		logger.info("Tamanio Lista "+tmpLista.size());
		
		RecaudacionTipoServ subTotales=new RecaudacionTipoServ();
		subTotales.setTerritorio("");
		subTotales.setCodOficina("");
		subTotales.setOficina("SUBTOTAL");
		
		int iContPN=0;
		Double iTotalPN=0.0;
		int iContPJ=0;
		Double iTotalPJ=0.0;
		int iContPFX=0;
		Double iTotalPFX=0.0;
		int iContPFX1=0;
		Double iTotalPX1=0.0;
		Double totalReca=0.0;
		
		for (RecaudacionTipoServ tmpR: tmpLista)
		{
			if (tmpR.getiContPersonasNaturales()>0)
			{
				iContPN+=tmpR.getiContPersonasNaturales();
			}
			
			if (tmpR.getsTotalPersonasNat()>0)
			{
				iTotalPN+=tmpR.getsTotalPersonasNat();
			}
			
			if (tmpR.getiContPersonasJuridicas()>0)
			{
				iContPJ+=tmpR.getiContPersonasJuridicas();
			}
			
			if (tmpR.getsTotalPersonasJurd()>0)
			{
				iTotalPJ+=tmpR.getsTotalPersonasJurd();
			}
			
			if (tmpR.getiContPersonasFallecX()>0)
			{
				iContPFX+=tmpR.getiContPersonasFallecX();
			}
			
			if (tmpR.getsTotalPersonasFallecX()>0)
			{
				iTotalPFX+=tmpR.getsTotalPersonasFallecX();
			}
			
			if (tmpR.getiContPersonasFallecX1()>0)
			{
				iContPFX1+=tmpR.getiContPersonasFallecX1();
			}
			
			if (tmpR.getsTotalPersonasFallecX1()>0)
			{
				iTotalPX1+=tmpR.getsTotalPersonasFallecX1();
			}
			
			if (tmpR.getsRecaudacionTotal()>0)
			{
				totalReca+=tmpR.getsRecaudacionTotal();
			}
		}
		
		subTotales.setiContPersonasNaturales(iContPN);
		subTotales.setsTotalPersonasNat(iTotalPN);
		
		subTotales.setiContPersonasJuridicas(iContPJ);
		subTotales.setsTotalPersonasJurd(iTotalPJ);
		
		subTotales.setiContPersonasFallecX(iContPFX);
		subTotales.setsTotalPersonasFallecX(iTotalPFX);
		
		subTotales.setiContPersonasFallecX1(iContPFX1);
		subTotales.setsTotalPersonasFallecX1(iTotalPX1);
		
		subTotales.setsRecaudacionTotal(totalReca);
		
		tmpLista.add(subTotales);

		return tmpLista;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<SolicitudesOficina> obtenerListarTotalSolicitudesxEstado(TiivsSolicitud solicitud, Date dFechaInicio, Date dFechaFin) throws Exception
	{	
		logger.info("***************En el obtenerListarTotalSolicitudesxEstado*************************");
		String sql ="";
		String sCadFecha="";
		List<SolicitudesOficina> tmpLista = new ArrayList<SolicitudesOficina>();
		
		if (solicitud!=null)
		{
			//Aplicando filtros
			String sWhere = "where d.cod_mult='T02'";
			
			if (solicitud.getTiivsOficina1().getTiivsTerritorio().getCodTer()!=null)
			{
				sWhere += " and c.cod_ter = '" + solicitud.getTiivsOficina1().getTiivsTerritorio().getCodTer() + "' ";
			}
			
			if (solicitud.getTiivsOficina1().getCodOfi()!=null)
			{
				if (solicitud.getTiivsOficina1().getCodOfi()!=null && solicitud.getTiivsOficina1().getCodOfi().compareTo("")!=0)
				{
					sWhere += " and a.cod_ofi= '" + solicitud.getTiivsOficina1().getCodOfi() + "' " ; 
				}
			}
			
			if (dFechaInicio!=null && dFechaFin!=null)
			{
				DateFormat formato = new SimpleDateFormat("dd/MM/yy");
				
				String tmpFecIni = formato.format(dFechaInicio);
				String tmpFecFin = formato.format(dFechaFin);
				
				sCadFecha = " and a.fecha between '" + tmpFecIni + "'" + " and '" + tmpFecFin + "'" +  " ";
				
				sWhere += sCadFecha;
			}
			
			sql="select distinct c.des_ter,a.cod_ofi,b.des_ofi,NVL(regis.total,0) Registrado, " + 
					"NVL(env.total,0) Enviado, NVL(res.total,0) Reservado, NVL(acep.total,0) Aceptado," + 
					"NVL(verA.total,0) Verificacion_A, NVL(rechaz.total,0) Rechazado, " + 
					"NVL(rev.total,0) En_Revision,NVL(preEj.total,0) Pre_Ejecutado," + 
					"NVL(ej.total,0) Ejecutado,NVL(proc.total,0) Procedente," + 
					"NVL(verB.total,0) Verificacion_B,NVL(improc.total,0) Improcedente," + 
					"NVL(ven.total,0) Vencido, NVL(revo.total,0) Revocado, " + 
					"(NVL(regis.total,0)+NVL(env.total,0)+ NVL(res.total,0) + NVL(acep.total,0)+" + 
					"NVL(verA.total,0)+ NVL(rechaz.total,0) +" + 
					"NVL(rev.total,0)+ NVL(preEj.total,0) +" + 
					"NVL(ej.total,0) + NVL(proc.total,0)+" + 
					"NVL(verB.total,0) + NVL(improc.total,0)+" + 
					"NVL(ven.total,0)+ NVL(revo.total,0)) totalFila " + 
					"from tiivs_solicitud a " + 
					"join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"join tiivs_territorio c on b.cod_terr= c.cod_ter " + 
					"join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"     from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0001' " + sCadFecha +
					"      group by a.cod_ofi) regis on a.cod_ofi = regis.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0007' " + sCadFecha +
					"      group by a.cod_ofi) env on a.cod_ofi = env.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0002' " + sCadFecha +
					"      group by a.cod_ofi) res on a.cod_ofi = res.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0003' " + sCadFecha +
					"      group by a.cod_ofi) acep on a.cod_ofi = acep.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0009' " + sCadFecha +
					"      group by a.cod_ofi) verA on a.cod_ofi = verA.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0004' " + sCadFecha +
					"      group by a.cod_ofi) rechaz on a.cod_ofi = rechaz.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0017' " + sCadFecha +
					"      group by a.cod_ofi) rev on a.cod_ofi = rev.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0014' " + sCadFecha +
					"      group by a.cod_ofi) preEj on a.cod_ofi = preEj.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0015' " + sCadFecha +
					"      group by a.cod_ofi) ej on a.cod_ofi = ej.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0012' " + sCadFecha +
					"      group by a.cod_ofi) proc on a.cod_ofi = proc.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0010' " + sCadFecha +
					"      group by a.cod_ofi) verB on a.cod_ofi = verB.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0013' " + sCadFecha +
					"      group by a.cod_ofi) improc on a.cod_ofi = improc.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " +  
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0008' " + sCadFecha +
					"      group by a.cod_ofi) ven on a.cod_ofi = ven.cod_ofi " + 
					"left join (select a.cod_ofi, count(a.estado) as total " + 
					"      from tiivs_solicitud a " + 
					"      join tiivs_oficina1 b on a.cod_ofi=b.cod_ofi " + 
					"      join tiivs_multitabla d on a.estado = d.cod_elem " + 
					"      where d.cod_mult='T02' and a.estado='0016' " + sCadFecha +
					"      group by a.cod_ofi) revo on a.cod_ofi = revo.cod_ofi " + sWhere + 
					"order by a.cod_ofi";
			 
			
			 logger.info("SQL : "+sql);
			 
			 final String sSQL=sql;
			
			 SolicitudesOficina nuevo;
			 List ResultList = (ArrayList<SolicitudesOficina>)getHibernateTemplate().execute(new HibernateCallback() 
			 {
					public List<Object> doInHibernate(Session session) throws HibernateException 
					{
						SQLQuery sq =session.createSQLQuery(sSQL);
						return sq.list();
					}
			 });

			if(ResultList.size()>0)
			{
				logger.info("ResultList.size "+ResultList.size());
				for(int i=0;i<=ResultList.size()-1;i++)
				{
				    Object[] row =  (Object[]) ResultList.get(i);
				    nuevo = new SolicitudesOficina();
				    
				    nuevo.setTerritorio(row[0].toString());
				    nuevo.setCodOficina(row[1].toString());
				    nuevo.setOficina(row[2].toString());
				    nuevo.setTotalRegistrados(Integer.valueOf(row[3].toString()));
				    nuevo.setTotalEnviados(Integer.valueOf(row[4].toString()));
				    nuevo.setTotalReservado(Integer.valueOf(row[5].toString()));
				    nuevo.setTotalAceptado(Integer.valueOf(row[6].toString()));
				    nuevo.setTotalVerificacionA(Integer.valueOf(row[7].toString()));
				    nuevo.setTotalRechazado(Integer.valueOf(row[8].toString()));
				    nuevo.setTotalEnRevision(Integer.valueOf(row[9].toString()));
				    nuevo.setTotalPreEjecutado(Integer.valueOf(row[10].toString()));
				    nuevo.setTotalEjecutado(Integer.valueOf(row[11].toString()));
				    nuevo.setTotalProcedente(Integer.valueOf(row[12].toString()));
				    nuevo.setTotalVerificacionB(Integer.valueOf(row[13].toString()));
				    nuevo.setTotalImprocedente(Integer.valueOf(row[14].toString()));
				    nuevo.setTotalVencido(Integer.valueOf(row[15].toString()));
				    nuevo.setTotalRevocado(Integer.valueOf(row[16].toString()));
				    nuevo.setTotalFila(Integer.valueOf(row[17].toString()));
				    				
					tmpLista.add(nuevo);
				}
		     }
		}
		
		logger.info("Tamanio Lista "+tmpLista.size());
		
		SolicitudesOficina subTotales=new SolicitudesOficina();
		subTotales.setTerritorio("");
		subTotales.setCodOficina("");
		subTotales.setOficina("SUBTOTAL");
		
		int iTotalReg=0;
		int iTotalEnv=0;
		int iTotalRes=0;
		int iTotalAceptado=0;
		int iTotalVerA=0;
		int iRechazado=0;
		int iTotalEnRev=0;
		int iTotalPreEje=0;
		int iTotalEje=0;
		int iProcedente=0;
		int iTotalVerB=0;
		int iTotalImpro=0;
		int iTotalVen=0;
		int iTotalRevo=0;
		int iTotalFila=0;
		
		for (SolicitudesOficina tmpSol: tmpLista)
		{
			if (tmpSol.getTotalRegistrados()>0)
			{
				iTotalReg+=tmpSol.getTotalRegistrados();
			}
			
			if (tmpSol.getTotalEnviados()>0)
			{
				iTotalEnv+=tmpSol.getTotalEnviados();
			}
			
			if (tmpSol.getTotalReservado()>0)
			{
				iTotalRes+=tmpSol.getTotalReservado();
			}
			
			if (tmpSol.getTotalAceptado()>0)
			{
				iTotalAceptado+=tmpSol.getTotalAceptado();
			}
			
			if (tmpSol.getTotalVerificacionA()>0)
			{
				iTotalVerA+=tmpSol.getTotalVerificacionA();
			}
			
			if (tmpSol.getTotalRechazado()>0)
			{
				iRechazado+=tmpSol.getTotalRechazado();
			}
			
			if (tmpSol.getTotalEnRevision()>0)
			{
				iTotalEnRev+=tmpSol.getTotalEnRevision();
			}
			
			if (tmpSol.getTotalPreEjecutado()>0)
			{
				iTotalEnRev+=tmpSol.getTotalPreEjecutado();
			}
			
			if (tmpSol.getTotalPreEjecutado()>0)
			{
				iTotalPreEje+=tmpSol.getTotalPreEjecutado();
			}
			
			if (tmpSol.getTotalEjecutado()>0)
			{
				iTotalEje+=tmpSol.getTotalEjecutado();
			}
			
			if (tmpSol.getTotalProcedente()>0)
			{
				iProcedente+=tmpSol.getTotalProcedente();
			}
			
			if (tmpSol.getTotalVerificacionB()>0)
			{
				iTotalVerB+=tmpSol.getTotalVerificacionB();
			}
			
			if (tmpSol.getTotalImprocedente()>0)
			{
				iTotalImpro+=tmpSol.getTotalImprocedente();
			}
			
			if (tmpSol.getTotalVencido()>0)
			{
				iTotalVen+=tmpSol.getTotalVencido();
			}
			
			if (tmpSol.getTotalRevocado()>0)
			{
				iTotalRevo+=tmpSol.getTotalRevocado();
			}
			
			if (tmpSol.getTotalFila()>0)
			{
				iTotalFila+=tmpSol.getTotalFila();
			}
		}
		
		subTotales.setTotalRegistrados(iTotalReg);
		subTotales.setTotalEnviados(iTotalEnv);
		subTotales.setTotalReservado(iTotalRes);
		subTotales.setTotalAceptado(iTotalAceptado);
		subTotales.setTotalVerificacionA(iTotalVerA);
		subTotales.setTotalRechazado(iRechazado);
		subTotales.setTotalEnRevision(iTotalEnRev);
		subTotales.setTotalPreEjecutado(iTotalPreEje);
		subTotales.setTotalEjecutado(iTotalEje);
		subTotales.setTotalProcedente(iProcedente);
		subTotales.setTotalVerificacionB(iTotalVerB);
		subTotales.setTotalImprocedente(iTotalImpro);
		subTotales.setTotalVencido(iTotalVen);
		subTotales.setTotalRevocado(iTotalRevo);
		subTotales.setTotalFila(iTotalFila);
		
		tmpLista.add(subTotales);

		return tmpLista;
		
	}
}

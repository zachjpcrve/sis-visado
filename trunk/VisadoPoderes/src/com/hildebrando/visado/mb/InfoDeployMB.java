package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.RangosImporte;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.dto.TiposFecha;
import com.hildebrando.visado.modelo.TiivsMultitabla;

/* CAMBIO 23/07/2014 HVB
 *  SE CREA CLASE PARA CONTEMPLAR INFORMACION QUE SE CARGUE AL MOMENTO DE INICIAR/REINICIAR
 *  EL APLICATIVO DE VISADO (COMBOS, PARAMETROS)
 */
@ManagedBean(name = "infoDeployMB",eager=true)
@ApplicationScoped
public class InfoDeployMB
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(InfoDeployMB.class);
	private List<RangosImporte> lstRangosImporte;
	private List<TiposFecha> lstTiposFecha;
	private List<Moneda> lstMoneda;
	private List<TipoDocumento> lstTipoDocumentos;
	private List<ComboDto> lstTipoRegistroPersona;
	private Map<String, String> estados;
	private Map<String, String> estadosNivel;
	private List<TiivsMultitabla>lstEstadosSolicitudes;
	private int pmaxResultadosConsulta;

	public InfoDeployMB()
	{
		logger.info("***INICIALIZANDO DATOS DE APLICACION***");
		long inicio = System.currentTimeMillis();
		cargaEstados();
		cargaEstadosNivel();
		cargaMonedas();
		cargaRangosImporte();
		cargaTipoDocumentos();
		cargaTipoRegistroPersona();
		cargaTiposFecha();
		cargaEstadoSolicitudes();
		cargaParametros();
		logger.debug("Tiempo de respuesta actualizando grilla de solicitudes: " + (System.currentTimeMillis()-inicio)/1000 + " segundos");
	}
	
	private void cargaParametros()
	{
		//CARGA PARAMETRO MAX RESULTADOS CONSULTA
		GenericDao<TiivsMultitabla, Object> busDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		filtro.add(Restrictions.eq("id.codMult", "T23"));
		filtro.add(Restrictions.eq("id.codElem", "0001"));
		
		List<TiivsMultitabla> lstMultiTablaTemp;
		try 
		{
			lstMultiTablaTemp = busDAO.buscarDinamico(filtro);
			if(lstMultiTablaTemp!= null && lstMultiTablaTemp.size() > 0)
			{
				//si fallara la consulta se trae como valor por defecto 10,000
				pmaxResultadosConsulta = lstMultiTablaTemp.get(0).getValor2()==null?-1:Integer.parseInt(lstMultiTablaTemp.get(0).getValor2());
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}
	
	private void cargaEstadoSolicitudes()
	{
		GenericDao<TiivsMultitabla, Object> busDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		filtro.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		try
		{
			lstEstadosSolicitudes = busDAO.buscarDinamico(filtro);
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"la lista de Estados por codigo: ",e);
		}
//		filtro.add(Restrictions.eq("id.codElem", codigo));
//		String resultado="";
//		List<TiivsMultitabla> tmpLista = new ArrayList<TiivsMultitabla>();
//		GenericDao<TiivsMultitabla, Object> busDAO = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
//		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
//		filtro.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
//		filtro.add(Restrictions.eq("id.codElem", codigo));
//		
//		try {
//			tmpLista = busDAO.buscarDinamico(filtro);
//		} catch (Exception e) {
//			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"la lista de Estados por codigo: ",e);
//		}
//		
//		if (tmpLista.size()>0)
//		{
//			resultado = tmpLista.get(0).getValor1().toUpperCase();
//		}
//		
//		return resultado;
	}
	
	private void cargaRangosImporte()
	{
		GenericDao<TiivsMultitabla,Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultiTabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultiTabla.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_IMPORTES));
		try 
		{
			List<TiivsMultitabla> lstMultiTablaTemp = service.buscarDinamico(filtroMultiTabla);
			
			lstRangosImporte = new ArrayList<RangosImporte>();
			
			for (TiivsMultitabla tiivsMultitabla : lstMultiTablaTemp) 
			{
				RangosImporte elementoRango = new RangosImporte();
				elementoRango.setCodigoRango(tiivsMultitabla.getId().getCodElem());
				elementoRango.setDescripcion(tiivsMultitabla.getValor1().toUpperCase());
				lstRangosImporte.add(elementoRango);
			}

		} 
		catch (Exception e) 
		{
			logger.error("Error al cargar la informacion de listado de importes: "+e);
		}

	}
	
	private void cargaTiposFecha()
	{
		GenericDao<TiivsMultitabla,Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultiTabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultiTabla.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA));
		try 
		{
			List<TiivsMultitabla> lstMultiTablaTemp = service.buscarDinamico(filtroMultiTabla);
			
			lstTiposFecha = new ArrayList<TiposFecha>();
			
			for (TiivsMultitabla tiivsMultitabla : lstMultiTablaTemp) 
			{
				TiposFecha elementoTipoFecha = new TiposFecha();
				elementoTipoFecha.setCodigoTipoFecha(tiivsMultitabla.getId().getCodElem());
				elementoTipoFecha.setDescripcion(tiivsMultitabla.getValor1().toUpperCase());
				lstTiposFecha.add(elementoTipoFecha);
			}

		} 
		catch (Exception e) 
		{
			logger.error("Error al cargar la informacion de listado de tipo fechas: "+e);
		}
		
	}
	
	private void cargaMonedas()
	{
		GenericDao<TiivsMultitabla,Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultiTabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultiTabla.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_MONEDA));
		try 
		{
			List<TiivsMultitabla> lstMultiTablaTemp = service.buscarDinamico(filtroMultiTabla);
			
			lstMoneda = new ArrayList<Moneda>();
			
			for (TiivsMultitabla tiivsMultitabla : lstMultiTablaTemp) 
			{
				Moneda elementoMoneda = new Moneda();
				elementoMoneda.setCodMoneda(tiivsMultitabla.getId().getCodElem());
				elementoMoneda.setDesMoneda(tiivsMultitabla.getValor1().toUpperCase());
				lstMoneda.add(elementoMoneda);
			}

		} 
		catch (Exception e) 
		{
			logger.error("Error al cargar la informacion de listado de monedas: "+e);
		}
		
	}
	
	private void cargaTipoDocumentos()
	{
		
			GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
			Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
			filtro.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC));
			filtro.add(Restrictions.eq("valor2", "1"));
			filtro.addOrder(Order.asc("valor5"));
			try 
			{	
				lstTipoDocumentos = new ArrayList<TipoDocumento>();
				List<TiivsMultitabla> lstMultiTablaTemp = service.buscarDinamico(filtro);	
				for (TiivsMultitabla tmp: lstMultiTablaTemp)
				{
					TipoDocumento doc = new TipoDocumento();
					doc.setCodTipoDoc(tmp.getId().getCodElem());
					doc.setDescripcion(tmp.getValor1().toUpperCase());
					lstTipoDocumentos.add(doc);
				}	
			}
			
			catch (Exception e) 
			{
				logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR_CARGA_LISTA+" de tipos de documento: ",e);
			}
		
		
	}
	
	private void cargaTipoRegistroPersona()
	{
		
		GenericDao<TiivsMultitabla,Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultiTabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultiTabla.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA));
		try 
		{
			List<TiivsMultitabla> lstMultiTablaTemp = service.buscarDinamico(filtroMultiTabla);
			
			lstTipoRegistroPersona = new ArrayList<ComboDto>();
			
			for (TiivsMultitabla tiivsMultitabla : lstMultiTablaTemp) 
			{
				ComboDto elementoTipoPersona = new ComboDto();
				elementoTipoPersona.setKey(tiivsMultitabla.getId().getCodElem());
				elementoTipoPersona.setDescripcion(tiivsMultitabla.getValor1().toUpperCase());
				lstTipoRegistroPersona .add(elementoTipoPersona);
			}

		} 
		catch (Exception e) 
		{
			logger.error("Error al cargar la informacion de listado de registro personas: "+e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void cargaEstados()
	{
	
		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);
		filtro.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		filtro.add(Restrictions.eq("valor2", "1"));
		filtro.add(Restrictions.eq("valor3", ConstantesVisado.FLUJO_ESTADO)); 
		
		estados = new HashMap<String, String>();
		Map<String,String> tmpEstados = new HashMap<String, String>();
		
		try 
		{
			List<TiivsMultitabla> lstMultiTablaTemp = service.buscarDinamico(filtro);
			
			for(TiivsMultitabla tiivsMultitabla:lstMultiTablaTemp)
			{
				tmpEstados.put(tiivsMultitabla.getValor1().toUpperCase(),tiivsMultitabla.getId().getCodElem());
			}
		} 
		catch (Exception e) 
		{
			logger.error(ConstantesVisado.MENSAJE.OCURRE_EXCEPCION,e);
		}
	
		estados = sortByComparator(tmpEstados);
		
	}
	
	private void cargaEstadosNivel()
	{
		GenericDao<TiivsMultitabla,Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroMultiTabla = Busqueda.forClass(TiivsMultitabla.class);
		filtroMultiTabla.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL));
		try 
		{
			List<TiivsMultitabla> lstMultiTablaTemp = service.buscarDinamico(filtroMultiTabla);
			
			estadosNivel = new HashMap<String, String>();
			
			
			for (TiivsMultitabla tiivsMultitabla : lstMultiTablaTemp) 
			{
				estadosNivel.put(tiivsMultitabla.getValor1().toUpperCase(),tiivsMultitabla.getId().getCodElem());
			}

		} 
		catch (Exception e) 
		{
			logger.error("Error al cargar la informacion de listado de monedas: "+e);
		}
		
	}
	
	public List<RangosImporte> getLstRangosImporte() {
		return lstRangosImporte;
	}
	public void setLstRangosImporte(List<RangosImporte> lstRangosImporte) {
		this.lstRangosImporte = lstRangosImporte;
	}
	public List<TiposFecha> getLstTiposFecha() {
		return lstTiposFecha;
	}
	public void setLstTiposFecha(List<TiposFecha> lstTiposFecha) {
		this.lstTiposFecha = lstTiposFecha;
	}
	public List<Moneda> getLstMoneda() {
		return lstMoneda;
	}
	public void setLstMoneda(List<Moneda> lstMoneda) {
		this.lstMoneda = lstMoneda;
	}
	public List<TipoDocumento> getLstTipoDocumentos() {
		return lstTipoDocumentos;
	}
	public void setLstTipoDocumentos(List<TipoDocumento> lstTipoDocumentos) {
		this.lstTipoDocumentos = lstTipoDocumentos;
	}

	public Map<String, String> getEstados() {
		return estados;
	}
	public void setEstados(Map<String, String> estados) {
		this.estados = estados;
	}
	public Map<String, String> getEstadosNivel() {
		return estadosNivel;
	}
	public void setEstadosNivel(Map<String, String> estadosNivel) {
		this.estadosNivel = estadosNivel;
	}

	public List<ComboDto> getLstTipoRegistroPersona() {
		return lstTipoRegistroPersona;
	}

	public void setLstTipoRegistroPersona(List<ComboDto> lstTipoRegistroPersona) {
		this.lstTipoRegistroPersona = lstTipoRegistroPersona;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map sortByComparator(Map unsortMap) {	 
		List list = new LinkedList(unsortMap.entrySet());		
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getKey()).compareTo(((Map.Entry) (o2)).getKey());
			}
		});
 		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public void setLstEstadosSolicitudes(List<TiivsMultitabla> lstEstadosSolicitudes) {
		this.lstEstadosSolicitudes = lstEstadosSolicitudes;
	}

	public List<TiivsMultitabla> getLstEstadosSolicitudes() {
		return lstEstadosSolicitudes;
	}

	public void setPmaxResultadosConsulta(int pmaxResultadosConsulta) {
		this.pmaxResultadosConsulta = pmaxResultadosConsulta;
	}

	public int getPmaxResultadosConsulta() {
		return pmaxResultadosConsulta;
	}


}

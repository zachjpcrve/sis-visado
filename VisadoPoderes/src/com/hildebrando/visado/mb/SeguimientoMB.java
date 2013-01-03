package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.Moneda;
import com.hildebrando.visado.dto.TipoDocumento;
import com.hildebrando.visado.modelo.TiivsAgrupacionPersona;
import com.hildebrando.visado.modelo.TiivsNivel;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.modelo.TiivsSolicitud;
import com.hildebrando.visado.modelo.TiivsSolicitudOperban;
import com.hildebrando.visado.modelo.TiivsTipoSolicDocumento;

@ManagedBean(name = "seguimientoMB")
@SessionScoped
public class SeguimientoMB 
{
	private List<TiivsSolicitud> solicitudes;  
	private List<TiivsAgrupacionPersona> lstAgrupPer;
	private String textoTotalResultados;
	private Boolean noHabilitarExportar;
	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;
	public static Logger logger = Logger.getLogger(SeguimientoMB.class);
	
	public SeguimientoMB()
	{
		solicitudes = new ArrayList<TiivsSolicitud>();
		lstAgrupPer= new ArrayList<TiivsAgrupacionPersona>();
		combosMB= new CombosMB();
		combosMB.cargarMultitabla();
		// Carga combo Rango Importes
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_IMPORTES);
		// Carga combo Estados
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS);
		// Carga combo Estados Nivel
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_ESTADOS_NIVEL);
		// Carga combo Tipos de Fecha
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPOS_FECHA);
		// Carga lista de monedas
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_MONEDA);
		// Carga lista de tipos de persona
		combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA);
		combosMB.cargarCombosNoMultitabla();
		cargarSolicitudes();
		if (solicitudes.size() == 0) 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_SIN_REGISTROS,solicitudes.size());
			setNoHabilitarExportar(true);
		} 
		else 
		{
			setearTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS + solicitudes.size() + ConstantesVisado.MSG_REGISTROS,solicitudes.size());
			setNoHabilitarExportar(false);
		}
	}
	
	// Descripcion: Metodo que se encarga de cargar las solicitudes en la grilla
	// @Autor: Cesar La Rosa
	// @Version: 1.0
	// @param: -
	public void cargarSolicitudes() 
	{
		GenericDao<TiivsSolicitud, Object> solicDAO = (GenericDao<TiivsSolicitud, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtroSol = Busqueda.forClass(TiivsSolicitud.class);
		filtroSol.addOrder(Order.asc(ConstantesVisado.CAMPO_COD_SOLICITUD));
		
		try {
			solicitudes = solicDAO.buscarDinamico(filtroSol);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Error al buscar las solicitudes");
		}

		actualizarDatosGrilla();
	}

	private void actualizarDatosGrilla() 
	{	
		String cadena="";
		Double importeTMP=0.0;
		Double rangoIni=0.0;
		Double rangoFin=0.0;
		
		// Se obtiene y setea la descripcion del Estado en la grilla
		for (TiivsSolicitud tmpSol : solicitudes) 
		{
			// Se obtiene y setea la descripcion del Estado en la grilla
			if (tmpSol.getEstado() != null) {
				if (tmpSol.getEstado().trim().equalsIgnoreCase(combosMB.getLstEstado().get(0).getCodEstado())) {
					tmpSol.setTxtEstado(combosMB.getLstEstado().get(0).getDescripcion());
				}
			}
			
			// Se obtiene la moneda y se coloca las iniciales en la columna Importe Total
			if (tmpSol.getMoneda() != null) 
			{
				String moneda = buscarAbrevMoneda(tmpSol.getMoneda());
				tmpSol.setTxtImporte(moneda.concat(ConstantesVisado.DOS_PUNTOS).concat(String.valueOf(tmpSol.getImporte())));
			} 
			else 
			{
				if (tmpSol.getImporte() != 0) 
				{
					tmpSol.setTxtImporte(String.valueOf(tmpSol.getImporte()));
				}
			}
			
			//Cargar data de poderdantes
			cadena="";
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getId().getCodSoli().equals(tmpSol.getCodSoli()) && tmp.getId().getClasifPer().equals(ConstantesVisado.PODERDANTE))
				{
					if (tmp.getId().getClasifPer().equals(combosMB.getLstTipoRegistroPersona().get(0).getKey()))
					{
						cadena += devolverDesTipoDOI(tmp.getTiivsPersona().getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmp.getTiivsPersona().getNumDoi() +
								  ConstantesVisado.GUION + tmp.getTiivsPersona().getApePat() + " " + tmp.getTiivsPersona().getApeMat() + " " + 
								  tmp.getTiivsPersona().getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
					}
				}
			}
			tmpSol.setTxtPoderdante(cadena);
			
			// Cargar data de apoderados
			cadena="";
			for (TiivsAgrupacionPersona tmp: combosMB.getLstTiposPersona())
			{
				if (tmp.getId().getCodSoli().equals(tmpSol.getCodSoli()) && tmp.getId().getClasifPer().equals(ConstantesVisado.APODERADO))
				{
					if (tmp.getId().getClasifPer().equals(combosMB.getLstTipoRegistroPersona().get(0).getKey()))
					{
						cadena += devolverDesTipoDOI(tmp.getTiivsPersona().getTipDoi()) + ConstantesVisado.DOS_PUNTOS + tmp.getTiivsPersona().getNumDoi() +
								  ConstantesVisado.GUION + tmp.getTiivsPersona().getApePat() + " " + tmp.getTiivsPersona().getApeMat() + " " + 
								  tmp.getTiivsPersona().getNombre() + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
					}
				}
			}
			
			tmpSol.setTxtApoderado(cadena);
			
			//Carga las operaciones bancarias asociadas a una solicitud
			cadena="";
			for (TiivsSolicitudOperban tmp: combosMB.getLstSolOperBan())
			{
				if (tmp.getId().getCodSoli().equals(tmpSol.getCodSoli()))
				{
					cadena +=  devolverDesOperBan(tmp.getId().getCodOperBan())  + ConstantesVisado.SLASH + ConstantesVisado.SALTO_LINEA;
				}
			}
			
			tmpSol.setTxtOpeBan(cadena);
			
			//Proceso para obtener los niveles de cada solicitud
			if (tmpSol.getImporte() != 0) 
			{
				if (combosMB.getLstNivel().size() > 0) 
				{
					String txtNivelTMP = "";
					String descripcion = buscarDescripcionMoneda(tmpSol.getMoneda());
					logger.debug("Moneda encontrada: " + descripcion);

					for (TiivsNivel tmp : combosMB.getLstNivel()) 
					{
						if (tmp.getId().getMoneda().equalsIgnoreCase(tmpSol.getMoneda())) 
						{
							if (tmp.getId().getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL1)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni = Double.valueOf(tmp.getId().getRangoInicio());
								rangoFin = Double.valueOf(tmp.getId().getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									txtNivelTMP += ConstantesVisado.CAMPO_NIVEL1;
								}
							}

							if (tmp.getId().getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL2)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni =  Double.valueOf(tmp.getId().getRangoInicio());
								rangoFin =  Double.valueOf(tmp.getId().getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									if (txtNivelTMP.length() > 0) 
									{
										txtNivelTMP += "," + ConstantesVisado.CAMPO_NIVEL2;
									} 
									else 
									{
										txtNivelTMP += ConstantesVisado.CAMPO_NIVEL2;
									}
								}
							}

							if (tmp.getId().getDesNiv().equalsIgnoreCase(ConstantesVisado.CAMPO_NIVEL3)) 
							{
								importeTMP = tmpSol.getImporte();
								rangoIni = Double.valueOf(tmp.getId().getRangoInicio());
								rangoFin = Double.valueOf(tmp.getId().getRangoFin());

								if (importeTMP.compareTo(rangoIni) >= 0 && importeTMP.compareTo(rangoFin) <= 0) 
								{
									if (txtNivelTMP.length() > 0) 
									{
										txtNivelTMP += "," 	+ ConstantesVisado.CAMPO_NIVEL3;
									} 
									else 
									{
										txtNivelTMP += ConstantesVisado.CAMPO_NIVEL3;
									}
								}
							}
						}

					}
					
					tmpSol.setTxtNivel(txtNivelTMP);
				}
				else
				{
					/*
					 * txtNivelTMP+=ConstantesVisado.CAMPO_NIVEL4;
					 * tmpSol.setTxtNivel(txtNivelTMP);
					 */
				}
			}
			else 
			{
				logger.debug("No se pudo obtener los rangos de los niveles para la solicitud. Verificar base de datos!!");
			}
		}
		
	}
	
	public String devolverDesTipoDOI(String codigo)
	{
		String resultado="";
		if (codigo!= null) {
			for (TipoDocumento tmp: combosMB.getLstTipoDocumentos())
			{
				if (codigo.equalsIgnoreCase(tmp.getCodTipoDoc())) 
				{
					resultado = tmp.getDescripcion();
					break;
				}
			}
		}
		
		return resultado;
	}
	
	public String devolverDesOperBan(String codigo)
	{
		String resultado="";
		if (codigo!= null) {
			for (TiivsOperacionBancaria tmp: combosMB.getLstOpeBancaria())
			{
				if (codigo.equalsIgnoreCase(tmp.getCodOperBan())) 
				{
					resultado = tmp.getDesOperBan();
					break;
				}
			}
		}
		
		return resultado;
	}
	
	private void setearTextoTotalResultados(String cadena, int total) {
		if (total == 1) {
			setTextoTotalResultados(ConstantesVisado.MSG_TOTAL_REGISTROS
					+ total + ConstantesVisado.MSG_REGISTRO);
		} else {
			setTextoTotalResultados(cadena);
		}
	}
	
	public String buscarDescripcionMoneda(String codMoneda) {
		String descripcion = "";
		for (Moneda tmpMoneda : combosMB.getLstMoneda()) {
			if (tmpMoneda.getCodMoneda().equalsIgnoreCase(codMoneda)) {
				descripcion = tmpMoneda.getDesMoneda();
				break;
			}
		}
		return descripcion;
	}
	
	public String buscarAbrevMoneda(String codigo) {
		int i = 0;
		String descripcion = "";

		for (; i <= combosMB.getLstMoneda().size() - 1; i++) 
		{
			if (combosMB.getLstMoneda().get(i).getCodMoneda().equalsIgnoreCase(codigo)) {
				if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_SOLES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_SOLES;
				} 
				else if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_DOLARES_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_DOLARES;
				} 
				else if (combosMB.getLstMoneda().get(i).getDesMoneda().equalsIgnoreCase(ConstantesVisado.CAMPO_EUROS_TBL_MONEDA)) 
				{
					descripcion = ConstantesVisado.CAMPO_ABREV_EUROS;
				} 
				else 
				{
					descripcion = combosMB.getLstMoneda().get(i).getDesMoneda();
				}
				break;
			}
		}

		return descripcion;
	}

	public List<TiivsSolicitud> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(List<TiivsSolicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

	public CombosMB getCombosMB() {
		return combosMB;
	}

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public List<TiivsAgrupacionPersona> getLstAgrupPer() {
		return lstAgrupPer;
	}

	public void setLstAgrupPer(List<TiivsAgrupacionPersona> lstAgrupPer) {
		this.lstAgrupPer = lstAgrupPer;
	}

	public String getTextoTotalResultados() {
		return textoTotalResultados;
	}

	public void setTextoTotalResultados(String textoTotalResultados) {
		this.textoTotalResultados = textoTotalResultados;
	}

	public Boolean getNoHabilitarExportar() {
		return noHabilitarExportar;
	}

	public void setNoHabilitarExportar(Boolean noHabilitarExportar) {
		this.noHabilitarExportar = noHabilitarExportar;
	}
}

package com.hildebrando.visado.mb;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.bbva.persistencia.generica.util.Utilitarios;
import com.hildebrando.visado.modelo.TiivsOperacionBancaria;
import com.hildebrando.visado.service.OperacionBancariaService;

@ManagedBean(name = "operacionBancariaMB")
@SessionScoped
public class OperacionBancariaMB {
	public static Logger logger = Logger.getLogger(DocumentoMB.class);
	
	private OperacionBancariaService operacionBancariaService;
	private TiivsOperacionBancaria operacion;
	private List<TiivsOperacionBancaria> operaciones;
	private List<TiivsOperacionBancaria> operacionEditar;
	private boolean bMsgActualizar;
	private boolean bValidacion;
	
	public OperacionBancariaMB() {
		
		operacion = new TiivsOperacionBancaria();
		operacionBancariaService = new OperacionBancariaService();
		bMsgActualizar = false;
		bValidacion = false;
		listarOperaciones();
		obtenerMaximo();
	}

	public void obtenerMaximo() 
	{
		logger.info("OperacionBancariaMB : obtenerMaximo");
		String secuencial = null;
		int parseSecuencial = 0;
		
		try {

			secuencial = operacionBancariaService.obtenerMaximo();
			parseSecuencial = Integer.parseInt(secuencial) + 1;
			secuencial = String.valueOf(parseSecuencial);

			if (secuencial.length() == 1) 
			{
				secuencial = "000000" + secuencial;
			} 
			else 
			{
				if (secuencial.length() == 2) 
				{
					secuencial = "00000" + secuencial;
				} 
				else 
				{
					if (secuencial.length() == 3) 
					{
						secuencial = "0000" + secuencial;
					} 
					else 
					{
						secuencial = secuencial;
					}
				}
			}
			
			logger.info("OperacionBancariaMB : secuencial" + " " + secuencial);
			operacion.setCodOperBan(secuencial);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("OperacionBancariaMB :" + e.getLocalizedMessage());
		}
	}

	public void listarOperaciones() {
		logger.info("OperacionBancariaMB: listarOperaciones");
		try {
			operaciones = operacionBancariaService.listarOperaciones();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("OperacionBancariaMB :" + e.getLocalizedMessage());
		}

	}

	public void registrar() 
	{
		logger.info("OperacionBancariaMB : Registrar");
		
		if (operacion.getActivo().isEmpty()) 
		{
			bValidacion = false;
		} 
		else 
		{
			bValidacion = true;
		}
		
		if (isbValidacion() == true) 
		{
			try 
			{
				operacionBancariaService.registrar(operacion);
				
				if (isbMsgActualizar() == true) 
				{
					Utilitarios.mensajeInfo("NIVEL","Se actualizó correctamente");
				} 
				else 
				{
					Utilitarios.mensajeInfo("NIVEL","Se registró correctamente");
				}
				
				operacion = new TiivsOperacionBancaria();
				obtenerMaximo();
				bMsgActualizar = false;
				bValidacion = false;
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("OperacionBancariaMB :" + e.getLocalizedMessage());
				Utilitarios.mensajeError("Error","Error al registrar Operaciones Bancarias");
			}
		} 
		else 
		{
			Utilitarios.mensajeError("Error","El campo descripción es requerido");
		}

	}

	public void editarOperacion() 
	{
		logger.info("OperacionBancariaMB : listarOperacion");
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String codElemento;
		codElemento = params.get("codElemento");
		
		try 
		{
			operacionEditar = operacionBancariaService.editarOperacion(codElemento);
			
			for(TiivsOperacionBancaria oper:operacionEditar){
				operacion = oper;
			}
			bMsgActualizar = true;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("DocumentoMB : listarOperacionesBancarias: " + ex.getLocalizedMessage());
		}
	}

	public void limpiarCampos() {
		logger.info("OperacionBancariaMB : limpiarCampos");
		operacion = new TiivsOperacionBancaria();
		obtenerMaximo();
		bMsgActualizar = false;
	}
	
	public boolean isbMsgActualizar() {
		return bMsgActualizar;
	}

	public void setbMsgActualizar(boolean bMsgActualizar) {
		this.bMsgActualizar = bMsgActualizar;
	}

	public boolean isbValidacion() {
		return bValidacion;
	}

	public void setbValidacion(boolean bValidacion) {
		this.bValidacion = bValidacion;
	}

	public TiivsOperacionBancaria getOperacion() {
		return operacion;
	}

	public void setOperacion(TiivsOperacionBancaria operacion) {
		this.operacion = operacion;
	}

	public List<TiivsOperacionBancaria> getOperaciones() {
		return operaciones;
	}

	public void setOperaciones(List<TiivsOperacionBancaria> operaciones) {
		this.operaciones = operaciones;
	}

	public List<TiivsOperacionBancaria> getOperacionEditar() {
		return operacionEditar;
	}

	public void setOperacionEditar(List<TiivsOperacionBancaria> operacionEditar) {
		this.operacionEditar = operacionEditar;
	}
	
}

package com.hildebrando.visado.dto;

import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsSolicitudAgrupacionId;

public class AgrupacionSimpleDto {
	
	
	private TiivsSolicitudAgrupacionId id;
	private List<TiivsPersona> lstPoderdantes;
	private List<TiivsPersona> lstApoderdantes;
	private List<TiivsPersona>  lstPersonas;
	private String sEstado;
	private int iEstado;
	
	private JRDataSource PERSONAS;
	
	public JRDataSource getPERSONAS() {
		return new JRBeanCollectionDataSource(lstPersonas); 
	}
		
		
	public AgrupacionSimpleDto(TiivsSolicitudAgrupacionId id,
			List<TiivsPersona> lstPoderdantes,
			List<TiivsPersona> lstApoderdantes, String sEstado, int iEstado) {
		super();
		this.id = id;
		this.lstPoderdantes = lstPoderdantes;
		this.lstApoderdantes = lstApoderdantes;
		this.sEstado = sEstado;
		this.iEstado = iEstado;
	}
	public AgrupacionSimpleDto() {
		
	}
	public TiivsSolicitudAgrupacionId getId() {
		return id;
	}
	public void setId(TiivsSolicitudAgrupacionId id) {
		this.id = id;
	}
	public List<TiivsPersona> getLstPoderdantes() {
		return lstPoderdantes;
	}
	public void setLstPoderdantes(List<TiivsPersona> lstPoderdantes) {
		this.lstPoderdantes = lstPoderdantes;
	}
	public List<TiivsPersona> getLstApoderdantes() {
		return lstApoderdantes;
	}
	public void setLstApoderdantes(List<TiivsPersona> lstApoderdantes) {
		this.lstApoderdantes = lstApoderdantes;
	}
	public String getsEstado() {
		return sEstado;
	}
	public void setsEstado(String sEstado) {
		this.sEstado = sEstado;
	}
	public int getiEstado() {
		return iEstado;
	}
	public void setiEstado(int iEstado) {
		this.iEstado = iEstado;
	}
	public List<TiivsPersona> getLstPersonas() {
		return lstPersonas;
	}
	public void setLstPersonas(List<TiivsPersona> lstPersonas) {
		this.lstPersonas = lstPersonas;
	}
	
}

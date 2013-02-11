package com.hildebrando.visado.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class SolicitudPDF implements Serializable 
{
	private String codSoli;
	private String estado;
	private String nroVoucher;
	private String comision;
	private String oficina;
	private String importe;
	private String tipoServicio;
	private JRDataSource DOCUMENTOS;
	private JRDataSource OPERACIONES;
	private JRDataSource AGRUPACION;
	private List<OperacionesPDF> lstOperaciones=new ArrayList<OperacionesPDF>();	
	private List<DocumentoTipoSolicitudDTO> lstDocumentos=new ArrayList<DocumentoTipoSolicitudDTO>();
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto = new ArrayList<AgrupacionSimpleDto>();
	
	public JRDataSource getDOCUMENTOS() {
		return new JRBeanCollectionDataSource(lstDocumentos); 
	}
	
	public JRDataSource getOPERACIONES() {
		return new JRBeanCollectionDataSource(lstOperaciones); 
	}	
	
	public JRDataSource getAGRUPACION(){
		return new JRBeanCollectionDataSource(this.lstAgrupacionSimpleDto); 
	}
	
	public String getCodSoli() {
		return codSoli;
	}
	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getNroVoucher() {
		return nroVoucher;
	}
	public void setNroVoucher(String nroVoucher) {
		this.nroVoucher = nroVoucher;
	}
	public String getComision() {
		return comision;
	}
	public void setComision(String comision) {
		this.comision = comision;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getImporte() {
		return importe;
	}
	public void setImporte(String importe) {
		this.importe = importe;
	}	
		
	public List<DocumentoTipoSolicitudDTO> getLstDocumentos() {
		return lstDocumentos;
	}

	public void setLstDocumentos(List<DocumentoTipoSolicitudDTO> lstDocumentos) {
		this.lstDocumentos = lstDocumentos;
	}

	public List<OperacionesPDF> getLstOperaciones() {
		return lstOperaciones;
	}

	public void setLstOperaciones(List<OperacionesPDF> lstOperaciones) {
		this.lstOperaciones = lstOperaciones;
	}	

	public String getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public void setLstAgrupacionSimpleDto(List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;		
	}
}

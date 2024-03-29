package com.hildebrando.visado.modelo;

// Generated 12/12/2012 12:08:20 PM by Hibernate Tools 3.4.0.CR
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hildebrando.visado.dto.AgrupacionSimpleDto;
import com.hildebrando.visado.dto.ComboDto;
import com.hildebrando.visado.dto.DocumentoTipoSolicitudDTO;

/**
 * TiivsSolicitud generated by hbm2java
 */
public class TiivsSolicitud implements java.io.Serializable {

	private String codSoli;
	private TiivsEstudio tiivsEstudio;
	private TiivsTipoSolicitud tiivsTipoSolicitud;
	private TiivsOficina1 tiivsOficina1;
	private String estado;
    private String descEstado;
	private Date fecha;
	private String regAbogado;
	private String obs;
	private String regUsuario;
	private String nomUsuario;
	private Double importe;
	private Timestamp fechaEnvio;
	private Timestamp fechaEstado;
	private String moneda;
    private String sImporteMoneda;  
	private Timestamp fechaRespuesta;
// MODIFICACION 
	private String flagReimprimir;
	private String tipoComision;
	private Double comision;
	private String nroVoucher;
	private Set<TiivsSolicitudNivel> tiivsSolicitudNivels = new HashSet<TiivsSolicitudNivel>(); //new HashSet(0);
	private Set<TiivsSolicitudAgrupacion> tiivsSolicitudAgrupacions = new HashSet<TiivsSolicitudAgrupacion>();
	private String txtEstado;
	private String txtImporte;
	private String txtApoderado;
	private String txtPoderdante;
	private String txtOpeBan;
	private String txtNivel;
	private Boolean bLiberado;
	private String exoneraComision;
	
	private Set<TiivsSolicitudOperban> tiivsSolicitudOperBanc = new HashSet<TiivsSolicitudOperban>(); //new HashSet(0);
	private List<TiivsSolicitudOperban> lstSolicBancarias; ///Solo para el reporte
	private List<ComboDto> listaPersonasXAgrupacionXSolicitud; ///Para comparar revocados
	
	private List<ComboDto> listaNum_ListaPersonas;///Para comparar revocados
	
	public List<ComboDto> getListaNum_ListaPersonas() {
		return this.listaNum_ListaPersonas;
	}

	public void setListaNum_ListaPersonas(List<ComboDto> listaNum_ListaPersonas) {
		this.listaNum_ListaPersonas = listaNum_ListaPersonas;
	}

	private List<DocumentoTipoSolicitudDTO> lstDocumentos; 
	private List<AgrupacionSimpleDto> lstAgrupacionSimpleDto;
	
	public TiivsSolicitud() {
	}

	public TiivsSolicitud(String codSoli,
			TiivsTipoSolicitud tiivsTipoSolicitud, TiivsOficina1 tiivsOficina1,
			String estado, Timestamp fecha, String regUsuario,
			String nomUsuario, String exoneraComision) {
		this.codSoli = codSoli;
		this.tiivsTipoSolicitud = tiivsTipoSolicitud;
		this.tiivsOficina1 = tiivsOficina1;
		this.estado = estado;
		this.fecha = fecha;
		this.regUsuario = regUsuario;
		this.nomUsuario = nomUsuario;
		this.exoneraComision = exoneraComision;
	}

	public TiivsSolicitud(String codSoli, TiivsEstudio tiivsEstudio,
			TiivsTipoSolicitud tiivsTipoSolicitud, TiivsOficina1 tiivsOficina1,
			String estado, Timestamp fecha, String regAbogado, String obs,
			String regUsuario, String nomUsuario, Double importe,
			Timestamp fechaEnvio, Timestamp fechaEstado, String moneda,
			Timestamp fechaRespuesta, Double comision,
			String nroVoucher, Set tiivsSolicitudNivels,
			Set tiivsSolicitudAgrupacions,
			Set tiivsSolicitudOperBanc) {
		this.codSoli = codSoli;
		this.tiivsEstudio = tiivsEstudio;
		this.tiivsTipoSolicitud = tiivsTipoSolicitud;
		this.tiivsOficina1 = tiivsOficina1;
		this.estado = estado;
		this.fecha = fecha;
		this.regAbogado = regAbogado;
		this.obs = obs;
		this.regUsuario = regUsuario;
		this.nomUsuario = nomUsuario;
		this.importe = importe;
		this.fechaEnvio = fechaEnvio;
		this.fechaEstado = fechaEstado;
		this.moneda = moneda;
		this.fechaRespuesta = fechaRespuesta;
		this.comision = comision;
		this.nroVoucher = nroVoucher;
		this.tiivsSolicitudNivels = tiivsSolicitudNivels;
		this.tiivsSolicitudAgrupacions = tiivsSolicitudAgrupacions;
		this.tiivsSolicitudOperBanc = tiivsSolicitudOperBanc;
	}

	public TiivsSolicitud(String codSoli) {
		this.codSoli = codSoli;
	}

	public String getCodSoli() {
		return this.codSoli;
	}

	public void setCodSoli(String codSoli) {
		this.codSoli = codSoli;
	}

	public TiivsEstudio getTiivsEstudio() {
		return this.tiivsEstudio;
	}

	public void setTiivsEstudio(TiivsEstudio tiivsEstudio) {
		this.tiivsEstudio = tiivsEstudio;
	}

	public TiivsTipoSolicitud getTiivsTipoSolicitud() {
		return this.tiivsTipoSolicitud;
	}

	public void setTiivsTipoSolicitud(TiivsTipoSolicitud tiivsTipoSolicitud) {
		this.tiivsTipoSolicitud = tiivsTipoSolicitud;
	}

	public TiivsOficina1 getTiivsOficina1() {
		return this.tiivsOficina1;
	}

	public void setTiivsOficina1(TiivsOficina1 tiivsOficina1) {
		this.tiivsOficina1 = tiivsOficina1;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}


	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {

		this.fecha = fecha;
	}

	public String getRegAbogado() {
		return this.regAbogado;
	}

	public void setRegAbogado(String regAbogado) {
		this.regAbogado = regAbogado;
	}

	public String getObs() {
		return this.obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getRegUsuario() {
		return this.regUsuario;
	}

	public void setRegUsuario(String regUsuario) {
		this.regUsuario = regUsuario;
	}

	public String getNomUsuario() {
		return this.nomUsuario;
	}

	public void setNomUsuario(String nomUsuario) {
		this.nomUsuario = nomUsuario;
	}

	public Double getImporte() {
		return this.importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}


	public Timestamp getFechaEnvio() {
		return this.fechaEnvio;
	}

	public void setFechaEnvio(Timestamp fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public Timestamp getFechaEstado() {
		return this.fechaEstado;
	}

	public void setFechaEstado(Timestamp fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	public String getMoneda() {
		return this.moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}


	public Timestamp getFechaRespuesta() {
		return this.fechaRespuesta;
	}

	public void setFechaRespuesta(Timestamp fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}
	
	public String getTipoComision(){
		return tipoComision;
	}
	
	public void setTipoComision(String tipoComision){
		this.tipoComision = tipoComision;
	}

	public Double getComision() {
		return this.comision;
	}

	public void setComision(Double comision) {
		this.comision = comision;
	}

	public String getNroVoucher() {
		return this.nroVoucher;
	}

	public void setNroVoucher(String nroVoucher) {
		this.nroVoucher = nroVoucher;
	}

	public Set getTiivsSolicitudNivels() {
		return this.tiivsSolicitudNivels;
	}

	public void setTiivsSolicitudNivels(Set tiivsSolicitudNivels) {
		this.tiivsSolicitudNivels = tiivsSolicitudNivels;
	}

	
	public Set<TiivsSolicitudAgrupacion> getTiivsSolicitudAgrupacions() {
		return tiivsSolicitudAgrupacions;
	}

	public void setTiivsSolicitudAgrupacions(
			Set<TiivsSolicitudAgrupacion> tiivsSolicitudAgrupacions) {
		this.tiivsSolicitudAgrupacions = tiivsSolicitudAgrupacions;
	}

	public String getDescEstado() {
		return descEstado;
	}

	public void setDescEstado(String descEstado) {
		this.descEstado = descEstado;
	}

	public String getsImporteMoneda() {
		return sImporteMoneda;
	}

	public void setsImporteMoneda(String sImporteMoneda) {
		this.sImporteMoneda = sImporteMoneda;
	}

	public String getTxtEstado() {
		return txtEstado;
	}

	public void setTxtEstado(String txtEstado) {
		this.txtEstado = txtEstado;
	}

	public String getTxtImporte() {
		return txtImporte;
	}

	public void setTxtImporte(String txtImporte) {
		this.txtImporte = txtImporte;
	}

	public String getTxtApoderado() {
		return txtApoderado;
	}

	public void setTxtApoderado(String txtApoderado) {
		this.txtApoderado = txtApoderado;
	}

	public String getTxtPoderdante() {
		return txtPoderdante;
	}

	public void setTxtPoderdante(String txtPoderdante) {
		this.txtPoderdante = txtPoderdante;
	}

	public String getTxtOpeBan() {
		return txtOpeBan;
	}

	public void setTxtOpeBan(String txtOpeBan) {
		this.txtOpeBan = txtOpeBan;
	}

	public String getTxtNivel() {
		return txtNivel;
	}

	public void setTxtNivel(String txtNivel) {
		this.txtNivel = txtNivel;
	}

	public Boolean getbLiberado() {
		return bLiberado;
	}

	public void setbLiberado(Boolean bLiberado) {
		this.bLiberado = bLiberado;
	}
	
	public String getExoneraComision() {
		return exoneraComision;
	}

	public void setExoneraComision(String exoneraComision) {
		this.exoneraComision = exoneraComision;
	}

	//solo para el reporte
	public List<TiivsSolicitudOperban> getLstSolicBancarias() {
		return lstSolicBancarias;
	}

	public void setLstSolicBancarias(List<TiivsSolicitudOperban> lstSolicBancarias) {
		this.lstSolicBancarias = lstSolicBancarias;
	}

	public List<DocumentoTipoSolicitudDTO> getLstDocumentos() {
		return lstDocumentos;
	}

	public void setLstDocumentos(List<DocumentoTipoSolicitudDTO> lstDocumentos) {
		this.lstDocumentos = lstDocumentos;
	}

	public void setLstAgrupacionSimpleDto(List<AgrupacionSimpleDto> lstAgrupacionSimpleDto) {
		this.lstAgrupacionSimpleDto = lstAgrupacionSimpleDto;		
	}
	
	public List<AgrupacionSimpleDto> getLstAgrupacionSimpleDto() {
		return lstAgrupacionSimpleDto;		
	}

	public List<ComboDto> getListaPersonasXAgrupacionXSolicitud() {
		return this.listaPersonasXAgrupacionXSolicitud;
	}

	public void setListaPersonasXAgrupacionXSolicitud(
			List<ComboDto> listaPersonasXAgrupacionXSolicitud) {
		this.listaPersonasXAgrupacionXSolicitud = listaPersonasXAgrupacionXSolicitud;
	}

	public String getFlagReimprimir() {
		return this.flagReimprimir;
	}

	public void setFlagReimprimir(String flagReimprimir) {
		this.flagReimprimir = flagReimprimir;
	}

	public Set<TiivsSolicitudOperban> getTiivsSolicitudOperBanc() {
		return tiivsSolicitudOperBanc;
	}

	public void setTiivsSolicitudOperBanc(
			Set<TiivsSolicitudOperban> tiivsSolicitudOperBanc) {
		this.tiivsSolicitudOperBanc = tiivsSolicitudOperBanc;
	}

	
	
	
	
}

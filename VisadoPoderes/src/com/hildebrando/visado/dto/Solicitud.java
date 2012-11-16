package com.hildebrando.visado.dto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Solicitud {
	private String item;
	private String descEstado;
	private String descTipDocPoder;
	private String descTipDocApoder;
	private String desOfi;
	private String desOfiEdit;
	private String codTerritorio;
	private String desTerritorio;
    private String codTipoSolicitud;
    private String desTipoSolicitud;
    private String numVoucher;
    private String numCelular;
    private String numFijo;
    private Double tipoCambio;
    private Double importeSoles;
	private String estadoDictamen;
	private Date fechaEnvio;
	private Date fechaRespuesta;
	private String niveles;
	private Date fechaEstado;
	private BigDecimal importe;
	private String codOperBan;
	private String desOperBan;
	private String strImporte;
	private String codEstudio;
	private String desEstudio;
	private String instrucciones;
	private String operacionesBancarias;
	private BigDecimal importeDesde;
	private String codSoli;
	private String codOfi;
	private String codOfiEdit;
	private String poderante;
	private String tipDocPoder;
	private String numDocPoder;
	private String codCentral;
	private String apoderado;
	private String tipDocApoder;
	private String numDocApoder;
	private String estado;
	private Date fecha;
	private String codOper;
	private String nomSolicitante;
	private String regAbogado;
	private String obs;
	private String regUsuario;
	private String nomUsuario;
	private String moneda;
	private String desMoneda;
	private String monedaAbr;
	private String fechaVencimiento;
	private boolean chekDelegado;
	private boolean chekNivel;
	private boolean chekReclamoRevision;
	private String tipoFecha;
	private String tipoFechaEnvio;
	private String tipoFechaRespuesta;
	private String codNivel;
	private String codEstadoNivel;
	private Date fechaInicio;
	private Date fechaFin;
	
	public String getCodSoli() {
		return this.codSoli;
	}

	public void setCodSoli(String codSoli) {
		this.codSoli = (codSoli == null ? null : codSoli.trim());
	}

	public String getCodOfi() {
		return this.codOfi;
	}

	public void setCodOfi(String codOfi) {
		this.codOfi = (codOfi == null ? null : codOfi.trim());
	}

	public String getPoderante() {
		return this.poderante;
	}

	public void setPoderante(String poderante) {
		this.poderante = (poderante == null ? null : poderante.trim());
	}

	public String getTipDocPoder() {
		return this.tipDocPoder;
	}

	public void setTipDocPoder(String tipDocPoder) {
		this.tipDocPoder = (tipDocPoder == null ? null : tipDocPoder.trim());
	}

	public String getNumDocPoder() {
		return this.numDocPoder;
	}

	public void setNumDocPoder(String numDocPoder) {
		this.numDocPoder = (numDocPoder == null ? null : numDocPoder.trim());
	}

	public String getCodCentral() {
		return this.codCentral;
	}

	public void setCodCentral(String codCentral) {
		this.codCentral = (codCentral == null ? null : codCentral.trim());
	}

	public String getApoderado() {
		return this.apoderado;
	}

	public void setApoderado(String apoderado) {
		this.apoderado = (apoderado == null ? null : apoderado.trim());
	}

	public String getTipDocApoder() {
		return this.tipDocApoder;
	}

	public void setTipDocApoder(String tipDocApoder) {
		this.tipDocApoder = (tipDocApoder == null ? null : tipDocApoder.trim());
	}

	public String getNumDocApoder() {
		return this.numDocApoder;
	}

	public void setNumDocApoder(String numDocApoder) {
		this.numDocApoder = (numDocApoder == null ? null : numDocApoder.trim());
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = (estado == null ? null : estado.trim());
	}

	public String getCodOper() {
		return this.codOper;
	}

	public void setCodOper(String codOper) {
		this.codOper = (codOper == null ? null : codOper.trim());
	}

	public String getNomSolicitante() {
		return this.nomSolicitante;
	}

	public void setNomSolicitante(String nomSolicitante) {
		this.nomSolicitante = (nomSolicitante == null ? null : nomSolicitante
				.trim());
	}

	public String getRegAbogado() {
		return this.regAbogado;
	}

	public void setRegAbogado(String regAbogado) {
		this.regAbogado = (regAbogado == null ? null : regAbogado.trim());
	}

	public String getObs() {
		return this.obs;
	}

	public void setObs(String obs) {
		this.obs = (obs == null ? null : obs.trim());
	}

	public String getRegUsuario() {
		return this.regUsuario;
	}

	public void setRegUsuario(String regUsuario) {
		this.regUsuario = (regUsuario == null ? null : regUsuario.trim());
	}

	public String getNomUsuario() {
		return this.nomUsuario;
	}

	public String getFechaDate() {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return formato.format(this.fecha);
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public void setNomUsuario(String nomUsuario) {
		this.nomUsuario = (nomUsuario == null ? null : nomUsuario.trim());
	}

	public String getDescEstado() {
		return this.descEstado;
	}

	public void setDescEstado(String descEstado) {
		this.descEstado = descEstado;
	}

	public String getDescTipDocPoder() {
		return this.descTipDocPoder;
	}

	public void setDescTipDocPoder(String descTipDocPoder) {
		this.descTipDocPoder = descTipDocPoder;
	}

	public String getDescTipDocApoder() {
		return this.descTipDocApoder;
	}

	public void setDescTipDocApoder(String descTipDocApoder) {
		this.descTipDocApoder = descTipDocApoder;
	}

	public String getDesOfi() {
		return this.desOfi;
	}

	public void setDesOfi(String desOfi) {
		this.desOfi = desOfi;
	}

	public String getEstadoDictamen() {
		return this.estadoDictamen;
	}

	public void setEstadoDictamen(String estadoDictamen) {
		this.estadoDictamen = estadoDictamen;
	}

	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Date getFechaEnvio() {
		return this.fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public String getFechaEnvioDate() {
		if (this.fechaEnvio == null) {
			return "";
		}
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formato.format(this.fechaEnvio);
	}

	public Date getFechaEstado() {
		return this.fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	public String getFechaEstadoDate() {
		if (this.fechaEstado == null) {
			return "";
		}
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formato.format(this.fechaEstado);
	}

	public String getFechaEstadoDate1() {
		if (this.fechaEstado == null) {
			return "";
		}
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
		return formato.format(this.fechaEstado);
	}

	public String getCodOperBan() {
		return this.codOperBan;
	}

	public void setCodOperBan(String codOperBan) {
		this.codOperBan = codOperBan;
	}

	public String getDesOperBan() {
		return this.desOperBan;
	}

	public void setDesOperBan(String desOperBan) {
		this.desOperBan = desOperBan;
	}

	public String getStrImporte() {
		if ((getImporte() != null) && (this.strImporte == null)) {
			return getImporte().toString();
		}
		return this.strImporte;
	}

	public void setStrImporte(String strImporte) {
		this.strImporte = strImporte;
	}

	public String getCodEstudio() {
		return this.codEstudio;
	}

	public void setCodEstudio(String codEstudio) {
		this.codEstudio = codEstudio;
	}

	public String getDesEstudio() {
		return this.desEstudio;
	}

	public void setDesEstudio(String desEstudio) {
		this.desEstudio = desEstudio;
	}

	public String getInstrucciones() {
		return this.instrucciones;
	}

	public void setInstrucciones(String instrucciones) {
		this.instrucciones = instrucciones;
	}

	public String getOperacionesBancarias() {
		return this.operacionesBancarias;
	}

	public void setOperacionesBancarias(String operacionesBancarias) {
		this.operacionesBancarias = operacionesBancarias;
	}

	public BigDecimal getImporteDesde() {
		return this.importeDesde;
	}

	public void setImporteDesde(BigDecimal importeDesde) {
		this.importeDesde = importeDesde;
	}

	public Object clone() {
		Object clone = null;
		try {
			clone = super.clone();
		} catch (CloneNotSupportedException localCloneNotSupportedException) {
		}

		return clone;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getMoneda() {
		return this.moneda;
	}

	public void setDesMoneda(String desMoneda) {
		this.desMoneda = desMoneda;
	}

	public String getDesMoneda() {
		return this.desMoneda;
	}

	public void setMonedaAbr(String monedaAbr) {
		this.monedaAbr = monedaAbr;
	}

	public String getMonedaAbr() {
		if (this.desMoneda != null) {
			String[] array = this.desMoneda.split(" ");
			if (array.length > 0)
				this.monedaAbr = array[0];
		} else {
			this.monedaAbr = "";
		}
		return this.monedaAbr;
	}

	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public String getFechaVencimiento() {
		return this.fechaVencimiento;
	}

	public String getCodOfiEdit() {
		return codOfiEdit;
	}

	public void setCodOfiEdit(String codOfiEdit) {
		this.codOfiEdit = codOfiEdit;
	}

	public String getDesOfiEdit() {
		return desOfiEdit;
	}

	public void setDesOfiEdit(String desOfiEdit) {
		this.desOfiEdit = desOfiEdit;
	}

	public String getCodTerritorio() {
		return codTerritorio;
	}

	public void setCodTerritorio(String codTerritorio) {
		this.codTerritorio = codTerritorio;
	}

	public String getCodTipoSolicitud() {
		return codTipoSolicitud;
	}

	public void setCodTipoSolicitud(String codTipoSolicitud) {
		this.codTipoSolicitud = codTipoSolicitud;
	}

	public Date getFechaRespuesta() {
		return fechaRespuesta;
	}

	public void setFechaRespuesta(Date fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}
	public String getFechaRespuestaDate() {
		if (this.fechaRespuesta == null) {
			return "";
		}
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formato.format(this.fechaRespuesta);
	}

	public boolean getChekDelegado() {
		return chekDelegado;
	}

	public void setChekDelegado(boolean chekDelegado) {
		this.chekDelegado = chekDelegado;
	}

	public boolean getChekNivel() {
		return chekNivel;
	}

	public void setChekNivel(boolean chekNivel) {
		this.chekNivel = chekNivel;
	}


	public boolean getChekReclamoRevision() {
		return chekReclamoRevision;
	}

	public void setChekReclamoRevision(boolean chekReclamoRevision) {
		this.chekReclamoRevision = chekReclamoRevision;
	}

	public String getTipoFecha() {
		return tipoFecha;
	}

	public void setTipoFecha(String tipoFecha) {
		this.tipoFecha = tipoFecha;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getCodNivel() {
		return codNivel;
	}

	public void setCodNivel(String codNivel) {
		this.codNivel = codNivel;
	}

	public String getCodEstadoNivel() {
		return codEstadoNivel;
	}

	public void setCodEstadoNivel(String codEstadoNivel) {
		this.codEstadoNivel = codEstadoNivel;
	}

	public String getDesTerritorio() {
		return desTerritorio;
	}

	public void setDesTerritorio(String desTerritorio) {
		this.desTerritorio = desTerritorio;
	}

	public String getDesTipoSolicitud() {
		return desTipoSolicitud;
	}

	public void setDesTipoSolicitud(String desTipoSolicitud) {
		this.desTipoSolicitud = desTipoSolicitud;
	}

	public String getNiveles() {
		return niveles;
	}

	public void setNiveles(String niveles) {
		this.niveles = niveles;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getTipoFechaEnvio() {
		return tipoFechaEnvio;
	}

	public void setTipoFechaEnvio(String tipoFechaEnvio) {
		this.tipoFechaEnvio = tipoFechaEnvio;
	}

	public String getTipoFechaRespuesta() {
		return tipoFechaRespuesta;
	}

	public void setTipoFechaRespuesta(String tipoFechaRespuesta) {
		this.tipoFechaRespuesta = tipoFechaRespuesta;
	}

	public String getNumVoucher() {
		return numVoucher;
	}

	public void setNumVoucher(String numVoucher) {
		this.numVoucher = numVoucher;
	}

	public String getNumCelular() {
		return numCelular;
	}

	public void setNumCelular(String numCelular) {
		this.numCelular = numCelular;
	}

	public String getNumFijo() {
		return numFijo;
	}

	public void setNumFijo(String numFijo) {
		this.numFijo = numFijo;
	}

	public Double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(Double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public Double getImporteSoles() {
		return importeSoles;
	}

	public void setImporteSoles(Double importeSoles) {
		this.importeSoles = importeSoles;
	}	
}
package com.hildebrando.visado.dto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Revocado {
	
	private String tipoDoiAp;
	private String nroDoiAp;
	private String nombreAp;
	private String codigoCentralAp;
	private String tipoDoiPo;
	private String nroDoiPo;
	private String nombrePo;	
	private String estado;
	private Date fechaRegistro;
	private String codigoRegistro;
	
	public Revocado(){
		
	}
	
	public Revocado(String tipoDoi1, String nroDoi1, String nombre1,
			String codigoCenral, String tipoDoi2, String nroDoi2, String nombre2,
			String estado, String fechaRegistro, String codRegistro) {
		this.tipoDoiAp = tipoDoi1;
		this.nroDoiAp = nroDoi1;
		this.nombreAp = nombre1;
		this.codigoCentralAp = codigoCenral;
		this.tipoDoiPo = tipoDoi2;
		this.nroDoiPo = nroDoi2;
		this.nombrePo = nombre2;
		this.estado = estado;				
		this.fechaRegistro = getDate(fechaRegistro);
		this.codigoRegistro = codRegistro;
		
	}
	public String getNombrePo() {
		return nombrePo;
	}
	public void setNombrePo(String nombrePo) {
		this.nombrePo = nombrePo;
	}
	
	public String getTipoDoiAp() {
		return tipoDoiAp;
	}
	public void setTipoDoiAp(String tipoDoiAp) {
		this.tipoDoiAp = tipoDoiAp;
	}
	public String getNroDoiAp() {
		return nroDoiAp;
	}
	public void setNroDoiAp(String nroDoiAp) {
		this.nroDoiAp = nroDoiAp;
	}
	public String getNombreAp() {
		return nombreAp;
	}
	public void setNombreAp(String nombreAp) {
		this.nombreAp = nombreAp;
	}
	public String getCodigoCentralAp() {
		return codigoCentralAp;
	}
	public void setCodigoCentralAp(String codigoCentralAp) {
		this.codigoCentralAp = codigoCentralAp;
	}
	public String getTipoDoiPo() {
		return tipoDoiPo;
	}
	public void setTipoDoiPo(String tipoDoiPo) {
		this.tipoDoiPo = tipoDoiPo;
	}
	public String getNroDoiPo() {
		return nroDoiPo;
	}
	public void setNroDoiPo(String nroDoiPo) {
		this.nroDoiPo = nroDoiPo;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Date getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public String getCodigoRegistro() {
		return codigoRegistro;
	}
	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}		
	
	private Date getDate(String date)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(date);
        } catch (ParseException ex) {
        }
        return null;
    }
}

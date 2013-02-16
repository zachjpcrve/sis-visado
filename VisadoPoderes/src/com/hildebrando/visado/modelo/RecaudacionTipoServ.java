package com.hildebrando.visado.modelo;

public class RecaudacionTipoServ {
	private String territorio;
	private String codOficina;
	private String oficina;
	private int iContPersonasNaturales;
	private Double sTotalPersonasNat;
	private int iContPersonasJuridicas;
	private Double sTotalPersonasJurd;
	private int iContPersonasFallecX;
	private Double sTotalPersonasFallecX;
	private int iContPersonasFallecX1;
	private Double sTotalPersonasFallecX1;
	private Double sRecaudacionTotal;
	
	public String getTerritorio() {
		return territorio;
	}
	public void setTerritorio(String territorio) {
		this.territorio = territorio;
	}
	public String getCodOficina() {
		return codOficina;
	}
	public void setCodOficina(String codOficina) {
		this.codOficina = codOficina;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public int getiContPersonasNaturales() {
		return iContPersonasNaturales;
	}
	public void setiContPersonasNaturales(int iContPersonasNaturales) {
		this.iContPersonasNaturales = iContPersonasNaturales;
	}
	public Double getsTotalPersonasNat() {
		return sTotalPersonasNat;
	}
	public void setsTotalPersonasNat(Double sTotalPersonasNat) {
		this.sTotalPersonasNat = sTotalPersonasNat;
	}
	public int getiContPersonasJuridicas() {
		return iContPersonasJuridicas;
	}
	public void setiContPersonasJuridicas(int iContPersonasJuridicas) {
		this.iContPersonasJuridicas = iContPersonasJuridicas;
	}
	public Double getsTotalPersonasJurd() {
		return sTotalPersonasJurd;
	}
	public void setsTotalPersonasJurd(Double sTotalPersonasJurd) {
		this.sTotalPersonasJurd = sTotalPersonasJurd;
	}
	public int getiContPersonasFallecX() {
		return iContPersonasFallecX;
	}
	public void setiContPersonasFallecX(int iContPersonasFallecX) {
		this.iContPersonasFallecX = iContPersonasFallecX;
	}
	public Double getsTotalPersonasFallecX() {
		return sTotalPersonasFallecX;
	}
	public void setsTotalPersonasFallecX(Double sTotalPersonasFallecX) {
		this.sTotalPersonasFallecX = sTotalPersonasFallecX;
	}
	public int getiContPersonasFallecX1() {
		return iContPersonasFallecX1;
	}
	public void setiContPersonasFallecX1(int iContPersonasFallecX1) {
		this.iContPersonasFallecX1 = iContPersonasFallecX1;
	}
	public Double getsTotalPersonasFallecX1() {
		return sTotalPersonasFallecX1;
	}
	public void setsTotalPersonasFallecX1(Double sTotalPersonasFallecX1) {
		this.sTotalPersonasFallecX1 = sTotalPersonasFallecX1;
	}
	public Double getsRecaudacionTotal() {
		return sRecaudacionTotal;
	}
	public void setsRecaudacionTotal(Double sRecaudacionTotal) {
		this.sRecaudacionTotal = sRecaudacionTotal;
	}
}

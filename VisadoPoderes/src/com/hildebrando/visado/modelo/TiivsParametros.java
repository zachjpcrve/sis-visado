package com.hildebrando.visado.modelo;

public class TiivsParametros implements java.io.Serializable {
	private int idParam;
	private int idEmpresa;
	private int idSistema;
	private String rutaLocal;
	private String server;
	private String loginServer;
	private String passServer;
	private String carpetaRemota;
	private String urlAPP;
	private String codUsuario;
	private String rutaArchivoExcel;
	
	public int getIdParam() {
		return idParam;
	}
	public void setIdParam(int idParam) {
		this.idParam = idParam;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public int getIdSistema() {
		return idSistema;
	}
	public void setIdSistema(int idSistema) {
		this.idSistema = idSistema;
	}
	public String getRutaLocal() {
		return rutaLocal;
	}
	public void setRutaLocal(String rutaLocal) {
		this.rutaLocal = rutaLocal;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getLoginServer() {
		return loginServer;
	}
	public void setLoginServer(String loginServer) {
		this.loginServer = loginServer;
	}
	public String getPassServer() {
		return passServer;
	}
	public void setPassServer(String passServer) {
		this.passServer = passServer;
	}
	public String getCarpetaRemota() {
		return carpetaRemota;
	}
	public void setCarpetaRemota(String carpetaRemota) {
		this.carpetaRemota = carpetaRemota;
	}
	public String getUrlAPP() {
		return urlAPP;
	}
	public void setUrlAPP(String urlAPP) {
		this.urlAPP = urlAPP;
	}
	public String getCodUsuario() {
		return codUsuario;
	}
	public void setCodUsuario(String codUsuario) {
		this.codUsuario = codUsuario;
	}
	public String getRutaArchivoExcel() {
		return rutaArchivoExcel;
	}
	public void setRutaArchivoExcel(String rutaArchivoExcel) {
		this.rutaArchivoExcel = rutaArchivoExcel;
	}
}

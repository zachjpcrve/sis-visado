package com.hildebrando.visado.dto;

import java.io.Serializable;
import org.primefaces.model.StreamedContent;

public class Escaneado implements Serializable {
	private String nomArchivo;
	private String documento;
	private String formato;
	private StreamedContent scPDF;
	private String rutaArchivoLarga;
	
	public String getNomArchivo() {
		return nomArchivo;
	}
	public void setNomArchivo(String nomArchivo) {
		this.nomArchivo = nomArchivo;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getFormato() {
		return formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public StreamedContent getScPDF() {
		return scPDF;
	}
	public void setScPDF(StreamedContent scPDF) {
		this.scPDF = scPDF;
	}
	public String getRutaArchivoLarga() {
		return rutaArchivoLarga;
	}
	public void setRutaArchivoLarga(String rutaArchivoLarga) {
		this.rutaArchivoLarga = rutaArchivoLarga;
	}
}

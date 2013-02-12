package com.hildebrando.visado.dto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hildebrando.visado.modelo.TiivsPersona;



public class Revocado {
	
	private String correlativo;
	private String codAgrupacion;
	private String nombreCompletoApoderados;
	private List<TiivsPersona> apoderados;
	private String nombreCompletoPoderdantes;
	private List<TiivsPersona> poderdantes;
	private String fechaRegistro;
	private String estado;
	
	private boolean flagEditPend;
	private boolean flagEditAct;
	private boolean flagDelete;

	public Revocado(){
		
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	private Date getDate(String date)
    {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return df.parse(date);
        } catch (ParseException ex) {
        }
        return null;
    }

	public String getNombreCompletoApoderados() {
		return nombreCompletoApoderados;
	}

	public void setNombreCompletoApoderados(String nombreCompletoApoderados) {
		this.nombreCompletoApoderados = nombreCompletoApoderados;
	}

	public String getNombreCompletoPoderdantes() {
		return nombreCompletoPoderdantes;
	}

	public void setNombreCompletoPoderdantes(String nombreCompletoPoderdantes) {
		this.nombreCompletoPoderdantes = nombreCompletoPoderdantes;
	}

	public String getCodAgrupacion() {
		return codAgrupacion;
	}

	public void setCodAgrupacion(String codAgrupacion) {
		this.codAgrupacion = codAgrupacion;
	}

	public String getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(String correlativo) {
		this.correlativo = correlativo;
	}

	public boolean isFlagEditPend() {
		return flagEditPend;
	}

	public void setFlagEditPend(boolean flagEditPend) {
		this.flagEditPend = flagEditPend;
	}

	public boolean isFlagEditAct() {
		return flagEditAct;
	}

	public void setFlagEditAct(boolean flagEditAct) {
		this.flagEditAct = flagEditAct;
	}

	public boolean isFlagDelete() {
		return flagDelete;
	}

	public void setFlagDelete(boolean flagDelete) {
		this.flagDelete = flagDelete;
	}
	public List<TiivsPersona> getApoderados() {
		return apoderados;
	}
	public void setApoderados(List<TiivsPersona> apoderados) {
		this.apoderados = apoderados;
	}
	public List<TiivsPersona> getPoderdantes() {
		return poderdantes;
	}
	public void setPoderdantes(List<TiivsPersona> poderdantes) {
		this.poderdantes = poderdantes;
	}

}

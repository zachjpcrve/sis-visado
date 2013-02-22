package com.hildebrando.visado.mb;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import com.bbva.common.util.ConstantesVisado;

/**
 * Clase que se encarga de manejar la calculadora de comision y que muestra de 
 * forma dinamica el importe en base a ciertos parametros seleccionados.
 * @author eramos
 * */

@ManagedBean(name = "calculadoraMB")
@SessionScoped
public class CaculadoraComisionMB {
	
	public static Logger logger = Logger.getLogger(CaculadoraComisionMB.class);
	private Boolean item11;
	private Boolean item12;
	private Boolean item21;
	private Boolean item22;
	private Boolean item31;
	private Boolean item32;
	private Boolean item41;
	private Boolean item42;	
	private Double resultado;
	private Double importeLimite;
	
	
	private String [] importes;
	
	

	
	public CaculadoraComisionMB(){
		item11 = new Boolean(false);
		item12 = new Boolean(false);
		item21 = new Boolean(false);
		item22 = new Boolean(false);
		item31 = new Boolean(false);
		item32 = new Boolean(false);
		item41 = new Boolean(false);
		item42 = new Boolean(false);
		resultado = new Double(0);		
		obtenerParametros();
	}

	public void calcularComision(){
		int criterio=0;
		resultado = new Double(200);
		if(this.item11){
			if(this.item21){
				criterio = 1;
			} else {
				criterio = 2;
			}
		} else {
			if(this.item31){
				criterio = 3;				
			} else {
				if(this.item41){
					criterio = 4;
				} 
			}
		}
			
		try {
			resultado = Double.valueOf(importes[criterio]);
		} catch (Exception e) {
			resultado = new Double(0);
		}

	}

	private void obtenerParametros() {
		String sMontoLimite;	
		importes = new String [5];		
		//Obtenemos los parametros de comisión
		try{
			sMontoLimite = RegistroUtilesMB.getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_COMISION,
					ConstantesVisado.CODIGO_CAMPO_COMISION_X).getValor2();	
			importeLimite = Double.valueOf(sMontoLimite);			
			
			importes[1] = RegistroUtilesMB.getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_COMISION,
					ConstantesVisado.CODIGO_CAMPO_FALLECIDO_MAYORA).getValor2();
			importes[2] = RegistroUtilesMB.getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_COMISION,
					ConstantesVisado.CODIGO_CAMPO_FALLECIDO_MENORA).getValor2();
			importes[3] = RegistroUtilesMB.getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_COMISION,
					ConstantesVisado.CODIGO_CAMPO_PERSONA_NATURAL).getValor2();
			importes[4] = RegistroUtilesMB.getRowFromMultitabla(
					ConstantesVisado.CODIGO_MULTITABLA_COMISION,
					ConstantesVisado.CODIGO_CAMPO_PERSONA_JURIDICA).getValor2();
			
		}catch(Exception e){
			logger.error(ConstantesVisado.MENSAJE.OCURRE_ERROR+"al obtener los parámetros de comisión: "+e.getMessage(),e);			
			importeLimite = new Double(0);
		}
	}	

	
	public Boolean getItem11() {
		return item11;
	}
	
	public void setItem11(Boolean item11) {
		this.item11 = item11;		
	}
	
	public Boolean getItem12() {
		return item12;
	}	
	
	public void setItem12(Boolean item12) {
		this.item12 = item12;
	}
	
	public Boolean getItem21() {
		return item21;
	}
	
	public void setItem21(Boolean item21) {
		this.item21 = item21;
	}
	
	public Boolean getItem22() {
		return item22;
	}
	
	public void setItem22(Boolean item22) {
		this.item22 = item22;
	}
	
	public Boolean getItem31() {
		return item31;
	}
	
	public void setItem31(Boolean item31) {
		this.item31 = item31;
	}
	
	public Boolean getItem32() {
		return item32;
	}
	
	public void setItem32(Boolean item32) {
		this.item32 = item32;
	}
	
	public Boolean getItem41() {
		return item41;
	}
	
	public void setItem41(Boolean item41) {
		this.item41 = item41;
	}
	
	public Boolean getItem42() {
		return item42;
	}
	
	public void setItem42(Boolean item42) {
		this.item42 = item42;
	}
	
	public Double getResultado() {
		return resultado;
	}
	
	public void setResultado(Double resultado) {
		this.resultado = resultado;
	}
	
	public Double getImporteLimite() {
		return importeLimite;
	}
	
	public void setImporteLimite(Double importeLimite) {
		this.importeLimite = importeLimite;
	}

	public void setNoItem11(Boolean item11){
		this.item11 = !item11;
	}
	
	public Boolean getNoItem11(){
		return !this.item11;
	}
	
	public void setNoItem21(Boolean item21){
		this.item21 = !item21;
	}
	
	public Boolean getNoItem21(){
		return !this.item21;
	}
	
	public void setNoItem31(Boolean item31){
		this.item31 = !item31;
	}
	
	public Boolean getNoItem31(){
		return !this.item31;
	}
	
	public void setNoItem41(Boolean item41){
		this.item41 = !item41;
	}
	
	public Boolean getNoItem41(){
		return !this.item41;
	}
	
}

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

	private Integer item1;
	private Integer item2;
	private Integer item3;
	private Integer item4;
	private boolean verItem1 = false;
	private boolean verItem2 = true;
	private boolean verItem3 = true;
	private boolean verItem4 = true;
	private Double resultado;
	private Double importeLimite;
	private String [] importes;
		
	public CaculadoraComisionMB()
	{
		resultado = new Double(0);		
		obtenerParametros();
	}

	public void calcularComision()
	{
		int criterio=0;
		resultado = new Double(200);
		
		if(item1!=null && item1 == 1)	{
			if(item2!=null && item2 == 1){
				criterio = 1;
			} else {
				criterio = 2;
			}
		} else {
			if(item3!=null && item3 ==1 ) {
				criterio = 3;				
			} else {
				if(item4!=null && item4==1) {
					criterio = 4;
				} 
			}
		}
			
		try {
			resultado = Double.valueOf(importes[criterio]);
		} catch (Exception e) {
			resultado = new Double(0);
		}
		
		if(item1!=null && item1 == 1){
			verItem2 = false;
			verItem3 = true;
			verItem4 = true;
			item3 = item4 = null;
		} else if(item1!=null && item1 == 0) {
			verItem2 = true;
			verItem3 = false;
			verItem4 = false;
			item2=null;
			
/*			if(item3!=null && item3 == 1){
				verItem4 = true;
			} 
			if(item3!=null && item3 == 0) {
				verItem4 = false;
			}
			
			if(item4!=null && item4 == 1){
				verItem3 = true;
			} 
			if(item4!=null && item4 == 0) {
				verItem3 = false;
			}*/
		}
		
		
		
	}

	private void obtenerParametros() 
	{
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
	
	public void limpiar(){
		item1 = null;
		item2 = null;
		item3 = null;
		item4 = null;
		resultado = new Double(0);
		verItem1 = false;
		verItem2 = true;
		verItem3 = true;
		verItem4 = true;
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

	public Integer getItem1() {
		return item1;
	}

	public void setItem1(Integer item1) {
		this.item1 = item1;
	}

	public Integer getItem2() {
		return item2;
	}

	public void setItem2(Integer item2) {
		this.item2 = item2;
	}

	public Integer getItem3() {
		return item3;
	}

	public void setItem3(Integer item3) {
		this.item3 = item3;
		if(item3!=null && item3 == 0){
			item4 = new Integer(1);
		} else if(item3!=null && item3 == 1) {
			item4 = new Integer(0);
		}
	}

	public Integer getItem4() {
		return item4;
	}

	public void setItem4(Integer item4) {		
		this.item4 = item4;
		if(item4!=null && item4 == 0){
			item3 = new Integer(1);
		} else if(item4!=null && item4 == 1) {
			item3 = new Integer(0);
		}
	}

	public boolean isVerItem1() {
		return verItem1;
	}

	public void setVerItem1(boolean verItem1) {
		this.verItem1 = verItem1;
	}

	public boolean isVerItem2() {
		return verItem2;
	}

	public void setVerItem2(boolean verItem2) {
		this.verItem2 = verItem2;
	}

	public boolean isVerItem3() {
		return verItem3;
	}

	public void setVerItem3(boolean verItem3) {
		this.verItem3 = verItem3;
	}

	public boolean isVerItem4() {
		return verItem4;
	}

	public void setVerItem4(boolean verItem4) {
		this.verItem4 = verItem4;
	}
	
}

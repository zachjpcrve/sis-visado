package com.hildebrando.visado.dto;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.hildebrando.visado.modelo.TiivsSolicitudOperban;

public class ComisionDTO {
	
	
	private String item1;
	private String item2;
	private String item3;
	private String item4;
	private Double importeLimite;
	private Double importe;
	public String getItem1() {
		return item1;
	}
	public void setItem1(String item1) {
		this.item1 = item1;
	}
	public String getItem2() {
		return item2;
	}
	public void setItem2(String item2) {
		this.item2 = item2;
	}
	public String getItem3() {
		return item3;
	}
	public void setItem3(String item3) {
		this.item3 = item3;
	}
	public String getItem4() {
		return item4;
	}
	public void setItem4(String item4) {
		this.item4 = item4;
	}
	public Double getImporteLimite() {
		return importeLimite;
	}
	public void setImporteLimite(Double importeLimite) {
		this.importeLimite = importeLimite;
	}
	public Double getImporte() {
		return importe;
	}
	public void setImporte(Double importe) {
		this.importe = importe;
	}

	
	
}
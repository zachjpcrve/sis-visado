package com.hildebrando.visado.mb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.Revocado;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsRevocado;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

@ManagedBean(name = "revocadosMB")
@SessionScoped
public class RevocadosMB {

	public static Logger logger = Logger.getLogger(RevocadosMB.class);

	private List<TiivsRevocado> revocados;
	private Revocado revocado;
	private String nroRegistros;

	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaBusqueda2;
	//private TiivsOficina1 tiivsOficina1;
	private Character estadoRevocado;
	
	private Date fechaInicio;
	private Date fechaFin;

	@ManagedProperty(value = "#{combosMB}")
	private CombosMB combosMB;

	public void setCombosMB(CombosMB combosMB) {
		this.combosMB = combosMB;
	}

	public RevocadosMB() {
		combosMB = new CombosMB();
		// combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA);
		// combosMB.cargarCombosMultitabla(ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC);

		objTiivsPersonaBusqueda = new TiivsPersona();
		estadoRevocado= new Character('S');
		//tiivsOficina1 = new TiivsOficina1();
	}

	public List<TiivsPersona> completePersona(String query) {
		List<TiivsPersona> lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		List<TiivsPersona> lstTiivsPersonaBusqueda = new ArrayList<TiivsPersona>();

		GenericDao<TiivsPersona, Object> service = (GenericDao<TiivsPersona, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsPersona.class);
		try {
			lstTiivsPersonaBusqueda = service.buscarDinamico(filtro);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (TiivsPersona pers : lstTiivsPersonaBusqueda) {

			if(pers!= null){
				
				if (pers.getNombre().toUpperCase() != ""
						&& pers.getApePat().toUpperCase() != ""
						&& pers.getApeMat().toUpperCase() != "") {
					
					String nombreCompletoMayuscula = pers.getNombre().toUpperCase()
							+ " " + pers.getApePat().toUpperCase() + " "
							+ pers.getApeMat().toUpperCase();

					String nombreCompletoMayusculaBusqueda = pers.getApePat()
							.toUpperCase()
							+ " "
							+ pers.getApeMat().toUpperCase()
							+ " " + pers.getNombre().toUpperCase();

					if (nombreCompletoMayusculaBusqueda.contains(query
							.toUpperCase())) {

						pers.setNombreCompletoMayuscula(nombreCompletoMayuscula);

						lstTiivsPersonaResultado.add(pers);
					}

				}
				
			}
			
		}

		return lstTiivsPersonaResultado;
	}

	/*
	public List<TiivsOficina1> completeOficina(String query) {
		List<TiivsOficina1> listTiivsOficina1Resultado = new ArrayList<TiivsOficina1>();
		List<TiivsOficina1> listTiivsOficina1Busqueda = new ArrayList<TiivsOficina1>();
		listTiivsOficina1Busqueda = combosMB.getLstOficina();

		for (TiivsOficina1 tiivsOficina1 : listTiivsOficina1Busqueda) {

			if(tiivsOficina1!= null ){
				
				if(tiivsOficina1.getCodOfi()!= ""
						&&  tiivsOficina1.getDesOfi() != ""
						  && tiivsOficina1.getTiivsTerritorio().getCodTer() != ""
						    && tiivsOficina1.getTiivsTerritorio().getDesTer()  != ""){
					
					String texto = tiivsOficina1.getCodOfi() + "-"
							+ tiivsOficina1.getDesOfi().toUpperCase() + "("
							+ tiivsOficina1.getTiivsTerritorio().getCodTer()
							+ tiivsOficina1.getTiivsTerritorio().getDesTer() + ")";

					if (texto.contains(query.toUpperCase())) {

						tiivsOficina1.setNombreDetallado(texto);
						listTiivsOficina1Resultado.add(tiivsOficina1);
					}
				}
			}
			

		}

		return listTiivsOficina1Resultado;
	}*/

	public void buscar() {
		revocados = new ArrayList<TiivsRevocado>();
		//addRevocados();
		
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		
		if(objTiivsPersonaBusqueda.getTipPartic().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("codMultPer1", objTiivsPersonaBusqueda.getTipPartic()));
			filtro.add(Restrictions.eq("codMultPer2", objTiivsPersonaBusqueda.getTipPartic()));
		}
		
		if(objTiivsPersonaBusqueda.getTipDoi().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer1.tipDoi", objTiivsPersonaBusqueda.getTipDoi()));
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer2.tipDoi", objTiivsPersonaBusqueda.getTipDoi()));
		}
		
		if(objTiivsPersonaBusqueda.getNumDoi().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer1.numDoi", objTiivsPersonaBusqueda.getNumDoi()));
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer2.numDoi", objTiivsPersonaBusqueda.getNumDoi()));
		}
		
		if(objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer1.nombre", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[0]));
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer2.nombre", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[0]));
			
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer1.apePat", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[1]));
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer2.apePat", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[1]));
			
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer1.apeMat", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[2]));
			filtro.add(Restrictions.eq("tiivsPersonaByCodPer2.apeMat", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[2]));
			
		}
		
		if(estadoRevocado.compareTo('S')!=0){
			
			filtro.add(Restrictions.eq("estado", estadoRevocado));
		}
		
		if(fechaInicio.compareTo(null)!=0  ||  fechaFin.compareTo(null)!=0){

			filtro.add(Restrictions.between("fecha_revocatoria", fechaInicio, fechaFin));
		}
		
		  
		try {
			revocados = service.buscarDinamico(filtro);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void limpiar() {
		revocados = null;
	}

	/*public void addRevocados() {

		revocados.add(new Revocado("DNI", "34533", "Vargas Días Cinthia",
				"345634", "DNI", "3323312", "Gonzales Maria", "Activo",
				"29/05/2012", "P001342"));
		revocados.add(new Revocado("DNI", "24533", "Montecarlo Diana",
				"983999", "DNI", "49999", "Perez Montes", "Activo",
				"12/07/2012", "P05673"));
		revocados.add(new Revocado("C.E", "4540043", "Piriz Javier",
				"34444449", "DNI", "48849494", "Morales Suarez", "Activo",
				"09/12/2012", "P05273"));
		revocados.add(new Revocado("DNI", "4523333", "Mendoza Lujan",
				"3434444", "DNI", "99999999", "Alcorta Julian", "Activo",
				"01/01/2011", "P05273"));
		revocados.add(new Revocado("DNI", "45233333", "Moreno Yuli",
				"34900004", "C.E.", "5959950", "Leguia Martha", "Activo",
				"05/04/2011", "P05273"));

	}*/

	public String reset() {
		return "/faces/paginas/registrarRevocado.xhtml";
	}

	public String editar() {
		System.out.print("editar");
		return "/faces/paginas/registrarRevocado.xhtml";
	}

	public String guardar() {
		System.out.print("guardar");
		return "/faces/paginas/bandejaRevocados.xhtml";
	}

	

	public Revocado getRevocado() {
		return revocado;
	}

	public void setRevocado(Revocado revocado) {
		this.revocado = revocado;
	}

	public String getNroRegistros() {
		Integer nReg;
		if (revocados != null) {
			nReg = new Integer(revocados.size());
		} else {
			nReg = new Integer(0);
		}
		return nReg.toString();
	}

	public TiivsPersona getObjTiivsPersonaBusqueda() {
		return objTiivsPersonaBusqueda;
	}

	public void setObjTiivsPersonaBusqueda(TiivsPersona objTiivsPersonaBusqueda) {
		this.objTiivsPersonaBusqueda = objTiivsPersonaBusqueda;
	}

	/*public TiivsOficina1 getTiivsOficina1() {
		return tiivsOficina1;
	}

	public void setTiivsOficina1(TiivsOficina1 tiivsOficina1) {
		this.tiivsOficina1 = tiivsOficina1;
	}*/

	public CombosMB getCombosMB() {
		return combosMB;
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

	public TiivsPersona getObjTiivsPersonaBusqueda2() {
		return objTiivsPersonaBusqueda2;
	}

	public void setObjTiivsPersonaBusqueda2(TiivsPersona objTiivsPersonaBusqueda2) {
		this.objTiivsPersonaBusqueda2 = objTiivsPersonaBusqueda2;
	}

	public Character getEstadoRevocado() {
		return estadoRevocado;
	}

	public void setEstadoRevocado(Character estadoRevocado) {
		this.estadoRevocado = estadoRevocado;
	}

	public List<TiivsRevocado> getRevocados() {
		return revocados;
	}

	public void setRevocados(List<TiivsRevocado> revocados) {
		this.revocados = revocados;
	}

}

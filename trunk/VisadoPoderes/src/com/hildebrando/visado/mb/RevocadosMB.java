package com.hildebrando.visado.mb;


import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import com.hildebrando.visado.dto.Revocado;


import org.apache.log4j.Logger;




@ManagedBean(name = "revocadosMB")
@SessionScoped
public class RevocadosMB 
{
	
	
	public static Logger logger = Logger.getLogger(RevocadosMB.class);
	
	private List<Revocado> revocados;
	private Revocado revocado;
	private String nroRegistros;
	
	public RevocadosMB() 
	{				
	}
	
	public void buscar(){
		revocados = new ArrayList<Revocado>();		
		addRevocados();
	}
	
	public void limpiar(){
		revocados = null;
	}
	
	public void addRevocados()
	{
		
		revocados.add(new Revocado("DNI","34533","Vargas Días Cinthia","345634","DNI","3323312","Gonzales Maria","Activo","29/05/2012","P001342"));
		revocados.add(new Revocado("DNI","24533","Montecarlo Diana","983999","DNI","49999","Perez Montes","Activo","12/07/2012","P05673"));
		revocados.add(new Revocado("C.E","4540043","Piriz Javier","34444449","DNI","48849494","Morales Suarez","Activo","09/12/2012","P05273"));
		revocados.add(new Revocado("DNI","4523333","Mendoza Lujan","3434444","DNI","99999999","Alcorta Julian","Activo","01/01/2011","P05273"));
		revocados.add(new Revocado("DNI","45233333","Moreno Yuli","34900004","C.E.","5959950","Leguia Martha","Activo","05/04/2011","P05273"));
		
	}
		
	public String reset(){
		return "/faces/paginas/registrarRevocado.xhtml";
	}
	
	public String editar(){
		System.out.print("editar");
		return "/faces/paginas/registrarRevocado.xhtml";
	}
	
	public String guardar(){
		System.out.print("guardar");
		return "/faces/paginas/bandejaRevocados.xhtml";
	}
	
	public List<Revocado> getRevocados(){
		return revocados;
	}
	
	public Revocado getRevocado(){
		return revocado;
	}
	
	public void setRevocado(Revocado revocado){
		this.revocado = revocado;
	}
	
	public String getNroRegistros(){
		Integer nReg;
		if(revocados!=null){
			nReg = new Integer(revocados.size());
		} else {
			nReg = new Integer(0);
		}
		return nReg.toString();
	}
	
}

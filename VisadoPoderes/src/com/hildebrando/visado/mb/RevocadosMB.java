package com.hildebrando.visado.mb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.dto.Persona;
import com.hildebrando.visado.dto.Revocado;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsPersona;
import com.hildebrando.visado.modelo.TiivsRevocado;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@ManagedBean(name = "revocadosMB")
@SessionScoped
public class RevocadosMB {

	public static Logger logger = Logger.getLogger(RevocadosMB.class);

	private List<Revocado> revocados;
	private Revocado revocado;
	private Revocado revocadoVer;
	private Revocado revocadoEdit;
	private String nroRegistros;

	private TiivsPersona objTiivsPersonaBusqueda;
	private TiivsPersona objTiivsPersonaBusqueda2;
	
	private TiivsPersona objTiivsPersonaBusquedaDlg;
	
	private TiivsPersona objTiivsPersonaAgregar;
	private TiivsPersona objTiivsPersonaAgregar2;
	
	private TiivsPersona deletePersonaEdit;
	
	//private TiivsOficina1 tiivsOficina1;
	private Character estadoRevocado;
	
	private List<TiivsPersona> personaClientes;
	private List<Revocado> personaClientesActivoEdit;
	private List<Revocado> personaClientesPendEdit;
	private List<TiivsPersona> personaClientesPopUp;
	private List<Revocado> personaClientesVer;
	
	private TiivsPersona selectPersonaBusqueda;
	private TiivsPersona selectPersonaPendEdit;
	private TiivsPersona selectPersonaActEdit;
	
	
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
		objTiivsPersonaBusqueda2 = new TiivsPersona();
		
		objTiivsPersonaBusquedaDlg = new TiivsPersona();
		
		estadoRevocado= new Character('S');
		//tiivsOficina1 = new TiivsOficina1();
	}

	public void verRevocado() {

		personaClientesVer = new ArrayList<Revocado>();
		personaClientesVer.add(revocadoVer);

	}
	
	public void editPendRevocado() {
		
		objTiivsPersonaBusquedaDlg= new TiivsPersona();
		objTiivsPersonaAgregar = new TiivsPersona();

		personaClientesPendEdit = new ArrayList<Revocado>();
		personaClientesPendEdit.add(revocadoEdit);

	}
	
	public void editActRevocado() {

		personaClientesActivoEdit = new ArrayList<Revocado>();
		personaClientesActivoEdit.add(revocadoEdit);

	}
	
	
	public void  buscarCliente(ActionEvent actionEvent) {
		
		
		
		
	}
	
	public void inactivarCombinacion(ActionEvent actionEvent) {

		List<TiivsMultitabla> tiivsMultitablas2 = new ArrayList<TiivsMultitabla>();
		
		GenericDao<TiivsMultitabla, Object> service3 = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		Busqueda filtro4 = Busqueda.forClass(TiivsMultitabla.class);
		filtro4.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		
		try {
			tiivsMultitablas2 = service3.buscarDinamico(filtro4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//aqui lo inactivamos
		List<TiivsRevocado> tiivsrevocados = new ArrayList<TiivsRevocado>();
		
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		filtro.add(Restrictions.eq("codAgrup", Integer.parseInt(revocadoEdit.getCodAgrupacion())));
		
		//String estadoInactivo = getValor1(ConstantesVisado.ESTADOS.ESTADO_INACTIVO_REVOCADO,tiivsMultitablas2);
		
		try {
			tiivsrevocados = service.buscarDinamico(filtro);
			
			for(TiivsRevocado  tiivsRevocado:tiivsrevocados){
				
				tiivsRevocado.setEstado(ConstantesVisado.ESTADOS.ESTADO_INACTIVO_REVOCADO);
				
				service.modificar(tiivsRevocado);
			}
			

			logger.debug("exitoso al inactivar revocados!");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block

			logger.debug("error al inactivar revocados!" + e.toString());
		}
		
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
		revocados = new ArrayList<Revocado>();
		List<TiivsRevocado> tiivsrevocados = new ArrayList<TiivsRevocado>();
		List<Integer> tiivsrevocados2 = new ArrayList<Integer>();
		List<TiivsMultitabla> tiivsMultitablas = new ArrayList<TiivsMultitabla>();
		List<TiivsMultitabla> tiivsMultitablas2 = new ArrayList<TiivsMultitabla>();
		List<TiivsMultitabla> tiivsMultitablas3 = new ArrayList<TiivsMultitabla>();
		//addRevocados();
		
		GenericDao<TiivsRevocado, Object> service = (GenericDao<TiivsRevocado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsRevocado.class);
		
		if(objTiivsPersonaBusqueda.getTipPartic().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("tipPartic", objTiivsPersonaBusqueda.getTipPartic()));
		
		}
		
		if(objTiivsPersonaBusqueda.getTipDoi().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("tiivsPersona.tipDoi", objTiivsPersonaBusqueda.getTipDoi()));
		}
		
		
		if(objTiivsPersonaBusqueda.getNumDoi().compareTo("")!=0){
			
			filtro.add(Restrictions.eq("tiivsPersona.numDoi", objTiivsPersonaBusqueda.getNumDoi()));
		}
		
		if(objTiivsPersonaBusqueda2 != null){
			
			if(objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().compareTo("")!=0){
				
				filtro.add(Restrictions.eq("tiivsPersona.nombre", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[0]));
				filtro.add(Restrictions.eq("tiivsPersona.apePat", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[1]));
				filtro.add(Restrictions.eq("tiivsPersona.apeMat", objTiivsPersonaBusqueda2.getNombreCompletoMayuscula().split(" ")[2]));
				
			}
			
		}
				
		
		if(estadoRevocado.compareTo('S')!=0){
			
			filtro.add(Restrictions.eq("estado", estadoRevocado));
		}
		
		if(fechaInicio!= null ||  fechaFin!=null){

			filtro.add(Restrictions.between("fecha_revocatoria", fechaInicio, fechaFin));
		}
		
		
		try {
			tiivsrevocados = service.buscarDinamico(filtro.addOrder(Order.desc("codAgrup")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		GenericDao<TiivsRevocado, Object> service2 = (GenericDao<TiivsRevocado, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro2 = Busqueda.forClass(TiivsRevocado.class).setProjection(Projections.distinct(Projections.property("codAgrup")));
		
		try {
			tiivsrevocados2 = service.buscarDinamicoInteger(filtro2.addOrder(Order.desc("codAgrup")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GenericDao<TiivsMultitabla, Object> service3 = (GenericDao<TiivsMultitabla, Object>) SpringInit.getApplicationContext().getBean("genericoDao");
		Busqueda filtro3 = Busqueda.forClass(TiivsMultitabla.class);
		filtro3.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_TIPO_DOC));
		
		try {
			tiivsMultitablas = service3.buscarDinamico(filtro3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Busqueda filtro4 = Busqueda.forClass(TiivsMultitabla.class);
		filtro4.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_ESTADOS));
		
		try {
			tiivsMultitablas2 = service3.buscarDinamico(filtro4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Busqueda filtro5 = Busqueda.forClass(TiivsMultitabla.class);
		filtro5.add(Restrictions.eq("id.codMult", ConstantesVisado.CODIGO_MULTITABLA_TIPO_REGISTRO_PERSONA));
		
		try {
			tiivsMultitablas3 = service3.buscarDinamico(filtro5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		Revocado revocado;
		String codAgrup="";
		String nombreCompletoApoderados="";
		String nombreCompletoPoderdantes="";
		String fecha="";
		String estado="";
		int numCorrelativo=0;
		List<TiivsPersona> apoderados;
		TiivsPersona apoderado;
		
		List<TiivsPersona> poderdantes;
		TiivsPersona poderdante;
		
		
		for(Integer tiivsRevocado2:tiivsrevocados2){
			numCorrelativo++;
			apoderados= new ArrayList<TiivsPersona>();
			poderdantes= new ArrayList<TiivsPersona>();
			for(TiivsRevocado tiivsRevocado:tiivsrevocados){
				
				if(tiivsRevocado.getCodAgrup().compareTo(tiivsRevocado2)==0){
					
					fecha=getDate(tiivsRevocado.getFechaRevocatoria());
					estado=getValor1(tiivsRevocado.getEstado(),tiivsMultitablas2);
					
					if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.APODERADO)){
						
						apoderado= new TiivsPersona();
						apoderado = tiivsRevocado.getTiivsPersona();
						
						String descDoiApod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),tiivsMultitablas);
						String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), tiivsMultitablas3);
						
						nombreCompletoApoderados = nombreCompletoApoderados
														+ " " + descDoiApod
														+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
														+ " - " + tiivsRevocado.getTiivsPersona().getApePat() 
														+ " " + tiivsRevocado.getTiivsPersona().getApeMat()
														+ " " + tiivsRevocado.getTiivsPersona().getNombre() + "\n";
						
						apoderado.setsDesctipDoi( descDoiApod);
						apoderado.setsDesctipPartic(descTipPart);
						
						apoderados.add(apoderado);
					}
						
					if(tiivsRevocado.getTipPartic().equals(ConstantesVisado.PODERDANTE)){
						
						poderdante = new TiivsPersona();
						poderdante = tiivsRevocado.getTiivsPersona();
						
						String descDoiPod =  getValor1(tiivsRevocado.getTiivsPersona().getTipDoi(),tiivsMultitablas);
						String descTipPart =  getValor1(tiivsRevocado.getTipPartic(), tiivsMultitablas3);
						
								
						nombreCompletoPoderdantes = nombreCompletoPoderdantes 
															+ " " + descDoiPod
															+ ":" + tiivsRevocado.getTiivsPersona().getNumDoi()
															+ " - " + tiivsRevocado.getTiivsPersona().getApePat() 
															+ " " + tiivsRevocado.getTiivsPersona().getApeMat() 
															+ " " + tiivsRevocado.getTiivsPersona().getNombre() + "\n";
						
						poderdante.setsDesctipDoi( descDoiPod);
						poderdante.setsDesctipPartic(descTipPart);
						
						poderdantes.add(poderdante);
					}
					
				}
				
				
			}
			
			revocado = new Revocado();
			revocado.setCodAgrupacion(tiivsRevocado2+"");
			revocado.setNombreCompletoApoderados(nombreCompletoApoderados.trim());
			revocado.setApoderados(apoderados);
			revocado.setNombreCompletoPoderdantes(nombreCompletoPoderdantes.trim());
			revocado.setPoderdantes(poderdantes);
			revocado.setFechaRegistro(fecha);
			revocado.setEstado(estado);
			revocado.setCorrelativo(String.valueOf(numCorrelativo));
			

			if(estado.compareTo("Activo")==0){
				revocado.setFlagEditAct(true);
				revocado.setFlagEditPend(false);
				revocado.setFlagDelete(false);
			}
			
			if(estado.compareTo("Pendiente")==0){
				revocado.setFlagEditPend(true);
				revocado.setFlagEditAct(false);
				revocado.setFlagDelete(true);
			}
			
			if(estado.compareTo("Inactivo")==0){
				revocado.setFlagEditPend(false);
				revocado.setFlagEditAct(false);
				revocado.setFlagDelete(false);
				
				
			}
			
			revocados.add(revocado);
			
			nombreCompletoApoderados="";
			nombreCompletoPoderdantes="";
			fecha="";
			estado="";
		}
		
		
		
	}
	
	private String getValor1(String codElem, List<TiivsMultitabla> multitabla){
		
		for(TiivsMultitabla obj:multitabla){
			
			if(obj.getId().getCodElem().compareTo(codElem)==0){
				return obj.getValor1();
			}
			
		}
		
		return "";
	}
	
	
	private String getDate(Date date)
    {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return df.format(date);
        } catch (Exception ex) {
        }
        return null;
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

	public List<Revocado> getRevocados() {
		return revocados;
	}

	public void setRevocados(List<Revocado> revocados) {
		this.revocados = revocados;
	}

	public void setNroRegistros(String nroRegistros) {
		this.nroRegistros = nroRegistros;
	}

	public TiivsPersona getObjTiivsPersonaBusquedaDlg() {
		return objTiivsPersonaBusquedaDlg;
	}

	public void setObjTiivsPersonaBusquedaDlg(
			TiivsPersona objTiivsPersonaBusquedaDlg) {
		this.objTiivsPersonaBusquedaDlg = objTiivsPersonaBusquedaDlg;
	}

	public TiivsPersona getObjTiivsPersonaAgregar() {
		return objTiivsPersonaAgregar;
	}

	public void setObjTiivsPersonaAgregar(TiivsPersona objTiivsPersonaAgregar) {
		this.objTiivsPersonaAgregar = objTiivsPersonaAgregar;
	}

	public TiivsPersona getObjTiivsPersonaAgregar2() {
		return objTiivsPersonaAgregar2;
	}

	public void setObjTiivsPersonaAgregar2(TiivsPersona objTiivsPersonaAgregar2) {
		this.objTiivsPersonaAgregar2 = objTiivsPersonaAgregar2;
	}

	public List<TiivsPersona> getPersonaClientes() {
		return personaClientes;
	}

	public void setPersonaClientes(List<TiivsPersona> personaClientes) {
		this.personaClientes = personaClientes;
	}

	public List<TiivsPersona> getPersonaClientesPopUp() {
		return personaClientesPopUp;
	}

	public void setPersonaClientesPopUp(List<TiivsPersona> personaClientesPopUp) {
		this.personaClientesPopUp = personaClientesPopUp;
	}


	public TiivsPersona getSelectPersonaBusqueda() {
		return selectPersonaBusqueda;
	}

	public void setSelectPersonaBusqueda(TiivsPersona selectPersonaBusqueda) {
		this.selectPersonaBusqueda = selectPersonaBusqueda;
	}

	public TiivsPersona getSelectPersonaPendEdit() {
		return selectPersonaPendEdit;
	}

	public void setSelectPersonaPendEdit(TiivsPersona selectPersonaPendEdit) {
		this.selectPersonaPendEdit = selectPersonaPendEdit;
	}

	public TiivsPersona getSelectPersonaActEdit() {
		return selectPersonaActEdit;
	}

	public void setSelectPersonaActEdit(TiivsPersona selectPersonaActEdit) {
		this.selectPersonaActEdit = selectPersonaActEdit;
	}

	public Revocado getRevocadoVer() {
		return revocadoVer;
	}

	public void setRevocadoVer(Revocado revocadoVer) {
		this.revocadoVer = revocadoVer;
	}

	public List<Revocado> getPersonaClientesVer() {
		return personaClientesVer;
	}

	public void setPersonaClientesVer(List<Revocado> personaClientesVer) {
		this.personaClientesVer = personaClientesVer;
	}

	public List<Revocado> getPersonaClientesActivoEdit() {
		return personaClientesActivoEdit;
	}

	public void setPersonaClientesActivoEdit(
			List<Revocado> personaClientesActivoEdit) {
		this.personaClientesActivoEdit = personaClientesActivoEdit;
	}

	public List<Revocado> getPersonaClientesPendEdit() {
		return personaClientesPendEdit;
	}

	public void setPersonaClientesPendEdit(List<Revocado> personaClientesPendEdit) {
		this.personaClientesPendEdit = personaClientesPendEdit;
	}

	public Revocado getRevocadoEdit() {
		return revocadoEdit;
	}

	public void setRevocadoEdit(Revocado revocadoEdit) {
		this.revocadoEdit = revocadoEdit;
	}

	public TiivsPersona getDeletePersonaEdit() {
		return deletePersonaEdit;
	}

	public void setDeletePersonaEdit(TiivsPersona deletePersonaEdit) {
		this.deletePersonaEdit = deletePersonaEdit;
	}

	

}

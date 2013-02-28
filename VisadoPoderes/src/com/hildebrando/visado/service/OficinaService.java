package com.hildebrando.visado.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsOficina1;
import com.hildebrando.visado.modelo.TiivsTerritorio;

public class OficinaService {
	public static Logger logger = Logger.getLogger(OficinaService.class);
	String estadoString = "1";
	char estadoChar = estadoString.charAt(0);
	Character estado = Character.valueOf(estadoChar);

	String estadoNoString = "0";
	char estadoNoChar = estadoNoString.charAt(0);
	Character estadoNo = Character.valueOf(estadoNoChar);

	public List<TiivsOficina1> listarOficinas() {
		logger.info("OficinaService : listarOficinas");
		List<TiivsOficina1> oficinas = new ArrayList<TiivsOficina1>();

		GenericDao<TiivsOficina1, Object> service = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);
		filtro.setMaxResults(1000);

		try {
			oficinas = service.buscarDinamico(filtro);

			for (int i = 0; i < oficinas.size(); i++) {
				oficinas.get(i).setDescTerritorio(
						oficinas.get(i).getTiivsTerritorio().getDesTer());

				if (oficinas.get(i).getActivo() == estado) {
					oficinas.get(i).setDescEstado("Activo");
					oficinas.get(i).setEstado("Inactivar");
				} else {

					oficinas.get(i).setDescEstado("Inactivo");
					oficinas.get(i).setEstado("Activar");

				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("OficinaService : listarOficinas: "
					+ ex.getLocalizedMessage());
		}
		return oficinas;
	}

	@SuppressWarnings("unchecked")
	public List<TiivsOficina1> editarOficina(String codOficina) {

		logger.info("OficinaService : editarOficina");
		List<TiivsOficina1> oficinaEditar = new ArrayList<TiivsOficina1>();

		GenericDao<TiivsOficina1, Object> service = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);
		try {
			oficinaEditar = service.buscarDinamico(filtro.add(Restrictions.eq(
					"codOfi", codOficina)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("OficinaService : editarOficina: "
					+ ex.getLocalizedMessage());
		}

		return oficinaEditar;
	}

	public void cambiarEstado(TiivsOficina1 oficina) {
		logger.info("OficinaService : cambiarEstado");
		GenericDao<TiivsOficina1, Object> service = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			if (oficina.getActivo() == estado) {
				oficina.setActivo(estadoNo);
			} else {
				oficina.setActivo(estado);
			}
			service.insertarMerge(oficina);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("OficinaService : cambiarEstado: "
					+ ex.getLocalizedMessage());
		}
	}

	public List<TiivsOficina1> cargarComboOficina() {
		logger.info("OficinaService : cargarComboOficina");
		List<TiivsOficina1> oficinas = new ArrayList<TiivsOficina1>();

		GenericDao<TiivsOficina1, Object> service = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);

		try {
			oficinas = service.buscarDinamico(filtro);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("OficinaService : cargarComboOficina: "
					+ ex.getLocalizedMessage());
		}
		return oficinas;
	}

	public List<TiivsTerritorio> cargarComboTerritorio() {
		logger.info("OficinaService : cargarComboTerritorio");
		List<TiivsTerritorio> territorios = new ArrayList<TiivsTerritorio>();

		GenericDao<TiivsTerritorio, Object> service = (GenericDao<TiivsTerritorio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsTerritorio.class);

		try {
			territorios = service.buscarDinamico(filtro);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("OficinaService : cargarComboTerritorio: "
					+ ex.getLocalizedMessage());
		}
		return territorios;
	}

	public List<TiivsOficina1> selecTerritorioOficina(TiivsTerritorio territorio) {
		logger.info("OficinaService : cargarComboTerritorio");
		List<TiivsOficina1> oficinas = new ArrayList<TiivsOficina1>();

		GenericDao<TiivsOficina1, Object> service = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);

		try {
			if(territorio.getCodTer().equals("-1")){
				oficinas = service.buscarDinamico(filtro);
			}else{
				oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsTerritorio.codTer", territorio.getCodTer())));
			}
			
			if(oficinas.size()==0){
				TiivsOficina1 oficina= new TiivsOficina1();
				oficina.setDesOfi("NO HAY OFICINAS");
				oficina.setCodOfi("-1");
				oficinas.add(oficina);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("OficinaService : cargarComboOficina: "
					+ ex.getLocalizedMessage());
		}
		return oficinas;
	}

	public List<TiivsOficina1> listarOficinasCombo(TiivsTerritorio territorio, TiivsOficina1 oficina1, String estado1) {
		logger.info("OficinaService : listarOficinasCombo");
		List<TiivsOficina1> oficinas = new ArrayList<TiivsOficina1>();
		char estado1Char = estado1.charAt(0);
		Character estadoCharacter1 = Character.valueOf(estado1Char);
		
		GenericDao<TiivsOficina1, Object> service = (GenericDao<TiivsOficina1, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsOficina1.class);

		try {
			if(territorio.getCodTer().equals("-1") && oficina1.getCodOfi().equals("-2")){
				if(estado1.equals("-1")){
					oficinas = service.buscarDinamico(filtro);
				}else{
					oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("activo", estadoCharacter1)));
				}
				
			}else{
				if(territorio.getCodTer().equals("-1") && !oficina1.getCodOfi().equals("-2")){
					if(estado1.equals("-1")){
						oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("codOfi", oficina1.getCodOfi())));
					}else{
						oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("codOfi", oficina1.getCodOfi())).add(Restrictions.eq("activo", estadoCharacter1)));
					}				
				}else{
					if(!territorio.getCodTer().equals("-1") && oficina1.getCodOfi().equals("-2")){
						if(estado1.equals("-1")){
							oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsTerritorio.codTer", territorio.getCodTer())));
						}else{
							oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("tiivsTerritorio.codTer", territorio.getCodTer())).add(Restrictions.eq("activo", estadoCharacter1)));
						}
					}else{
						if(estado1.equals("-1")){
							oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("codOfi", oficina1.getCodOfi())).add(Restrictions.eq("tiivsTerritorio.codTer", territorio.getCodTer())));	
						}else{
							
						}
						oficinas = service.buscarDinamico(filtro.add(Restrictions.eq("codOfi", oficina1.getCodOfi())).add(Restrictions.eq("tiivsTerritorio.codTer", territorio.getCodTer())).add(Restrictions.eq("activo", estadoCharacter1)));
					}
				}
				
			}
			
			for (int i = 0; i < oficinas.size(); i++) {
				oficinas.get(i).setDescTerritorio(
						oficinas.get(i).getTiivsTerritorio().getDesTer());

				if (oficinas.get(i).getActivo() == estado) {
					oficinas.get(i).setDescEstado("Activo");
					oficinas.get(i).setEstado("Inactivar");
				} else {

					oficinas.get(i).setDescEstado("Inactivo");
					oficinas.get(i).setEstado("Activar");

				}
			}
			if(oficinas.size()==0){
				TiivsOficina1 oficina= new TiivsOficina1();
				oficina.setDesOfi("NO HAY OFICINAS");
				oficina.setCodOfi("-1");
				oficinas.add(oficina);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("OficinaService : cargarComboOficina: "
					+ ex.getLocalizedMessage());
		}
		return oficinas;
	}

}

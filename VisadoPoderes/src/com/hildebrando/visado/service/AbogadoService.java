package com.hildebrando.visado.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.hildebrando.visado.modelo.TiivsEstudio;
import com.hildebrando.visado.modelo.TiivsGrupo;
import com.hildebrando.visado.modelo.TiivsMiembro;
import com.hildebrando.visado.modelo.TiivsMultitabla;

public class AbogadoService {
	public static Logger logger = Logger.getLogger(AbogadoService.class);

	public List<TiivsEstudio> listarEstudios() {
		logger.info("AbogadoService : listarEstudios");
		List<TiivsEstudio> estudios = new ArrayList<TiivsEstudio>();
		GenericDao<TiivsEstudio, Object> service = (GenericDao<TiivsEstudio, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsEstudio.class);

		try {
			estudios = service.buscarDinamico(filtro);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : listarEstudios: "
					+ ex.getLocalizedMessage());
		}
		return estudios;
	}

	public List<TiivsMiembro> listarAbogados(List<TiivsEstudio> estudios,
			List<TiivsMultitabla> criterios) {
		logger.info("AbogadoService : listarAbogados");
		List<TiivsMiembro> abogados = new ArrayList<TiivsMiembro>();
		/* String codigoGrupo = "0000002"; */
		String codCriterio = "";

		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);

		try {
			abogados = service.buscarDinamico(filtro.add(Restrictions.eq("activo", "1")));

			for (int i = 0; i < abogados.size(); i++) {

				for (int h = 0; h < criterios.size(); h++) {
					codCriterio = criterios.get(h).getId().getCodMult()
							+ criterios.get(h).getId().getCodElem();
					if (abogados.get(i).getCriterio().equals(codCriterio)) {
						abogados.get(i).setCriterio(
								criterios.get(h).getValor1());
					}
				}

				if (abogados.get(i).getEstudio() == null) {
					abogados.get(i).setEstudio(" ");

				} else {

					for (int j = 0; j < estudios.size(); j++) {
						if (abogados.get(i).getTiivsGrupo().getCodGrupo()
								.equals(estudios.get(j).getCodEstudio())) {
							abogados.get(i).setEstudio(
									estudios.get(j).getDesEstudio());
						}
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : listarAbogados: "
					+ ex.getLocalizedMessage());
		}
		return abogados;
	}

	public List<TiivsMultitabla> listarCriterios() {
		logger.info("AbogadoService : listarCriterios");
		List<TiivsMultitabla> criterios = new ArrayList<TiivsMultitabla>();

		GenericDao<TiivsMultitabla, Object> service = (GenericDao<TiivsMultitabla, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMultitabla.class);

		try {
			criterios = service
					.buscarDinamico(filtro.add(Restrictions.eq("id.codMult",
							ConstantesVisado.CODIGO_MULTITABLA_CRITERIO)));

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : listarCriterios: "
					+ ex.getLocalizedMessage());
		}
		return criterios;
	}

	public List<TiivsGrupo> listarGrupos() {
		logger.info("AbogadoService : listarCriterios");
		List<TiivsGrupo> grupos = new ArrayList<TiivsGrupo>();

		GenericDao<TiivsGrupo, Object> service = (GenericDao<TiivsGrupo, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsGrupo.class);

		try {
			grupos = service.buscarDinamico(filtro);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : listarCriterios: "
					+ ex.getLocalizedMessage());
		}
		return grupos;
	}

	public List<TiivsMiembro> listarAbogadosCombo(TiivsGrupo grupoFiltro,
			List<TiivsMultitabla> criterios, List<TiivsEstudio> estudios,
			TiivsMiembro abogado) {
		logger.info("AbogadoService : listarOficinasCombo");
		List<TiivsMiembro> abogados = new ArrayList<TiivsMiembro>();
		String codCriterio = "";

		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);

		try {
			if (grupoFiltro.getCodGrupo().equals("-1")) {
				abogados = service.buscarDinamico(filtro.add(Restrictions.eq("activo", "1")));
			} else {
				if (abogado.getEstudio() == null) {
					abogados = service.buscarDinamico(filtro.add(Restrictions
							.eq("tiivsGrupo.codGrupo",
									grupoFiltro.getCodGrupo())).add(Restrictions.eq("activo", "1")));
				} else {
					if (abogado.getEstudio().equals("-1")) {
						abogados = service.buscarDinamico(filtro
								.add(Restrictions.eq("tiivsGrupo.codGrupo",
										grupoFiltro.getCodGrupo())).add(Restrictions.eq("activo", "1")));
					} else {
						abogados = service.buscarDinamico(filtro.add(
								Restrictions.eq("tiivsGrupo.codGrupo",
										grupoFiltro.getCodGrupo()))
								.add(Restrictions.eq("estudio",
										abogado.getEstudio())).add(Restrictions.eq("activo", "1")));
					}

				}
			}

			for (int i = 0; i < abogados.size(); i++) {

				for (int h = 0; h < criterios.size(); h++) {
					codCriterio = criterios.get(h).getId().getCodMult()
							+ criterios.get(h).getId().getCodElem();
					if (abogados.get(i).getCriterio().equals(codCriterio)) {
						abogados.get(i).setCriterio(
								criterios.get(h).getValor1());
					}
				}

				if (!(abogados.get(i).getEstudio() == null)) {

					for (int j = 0; j < estudios.size(); j++) {
						if (abogados.get(i).getEstudio()
								.equals(estudios.get(j).getCodEstudio())) {
							abogados.get(i).setEstudio(
									estudios.get(j).getDesEstudio());
						}
					}
				} else {
					abogados.get(i).setEstudio(" ");
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : listarAbogados: "
					+ ex.getLocalizedMessage());
		}
		return abogados;
	}

	public void registrar(TiivsMiembro abogado) {
		logger.info("AbogadoService : registrar");
		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		String criterioG = "";
		try {
			criterioG = abogado.getCriterio();
			criterioG = ConstantesVisado.CODIGO_MULTITABLA_CRITERIO + criterioG;
			abogado.setCriterio(criterioG);
			service.insertarMerge(abogado);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : : " + ex.getLocalizedMessage());
		}

	}
	
	public void eliminarAbogado(TiivsMiembro abogado) {
		logger.info("AbogadoService : registrar");
		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		try {
			service.insertarMerge(abogado);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : : " + ex.getLocalizedMessage());
		}

	}
	public List<TiivsMiembro> editarAbogado(String codAbogado) {
		logger.info("AbogadoService : editarAbogado");
		List<TiivsMiembro> abogadoEditar = new ArrayList<TiivsMiembro>();

		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);
		try {
			abogadoEditar = service.buscarDinamico(filtro.add(Restrictions.eq(
					"codMiembro", codAbogado)));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : editarAbogado: "
					+ ex.getLocalizedMessage());
		}
		return abogadoEditar;
	}

	public <E> String validarCodigo(TiivsMiembro abogado) {
		logger.info("AbogadoService : validarCodigo");
		List<TiivsMiembro> existeCodigo = new ArrayList<TiivsMiembro>();
		String contador = "0";
		
		GenericDao<TiivsMiembro, Object> service = (GenericDao<TiivsMiembro, Object>) SpringInit
				.getApplicationContext().getBean("genericoDao");
		Busqueda filtro = Busqueda.forClass(TiivsMiembro.class);
		try {
			existeCodigo = service.buscarDinamico(filtro.add(
					Restrictions.eq("codMiembro", abogado.getCodMiembro()))
					.setProjection(Projections.rowCount()));
			
			List<E> parse = new ArrayList<E>();
			parse = (List<E>) existeCodigo;
			contador = parse.get(0).toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("AbogadoService : validarCodigo: "
					+ ex.getLocalizedMessage());
		}
		return contador;
	}
}

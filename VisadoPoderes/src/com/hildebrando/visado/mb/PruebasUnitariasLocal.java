package com.hildebrando.visado.mb;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.criterion.Restrictions;

import com.bbva.common.listener.SpringInit.SpringInit;
import com.bbva.common.util.ConstantesVisado;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.persistencia.generica.dao.Busqueda;
import com.bbva.persistencia.generica.dao.GenericDao;
import com.bbva.persistencia.generica.util.Utilitarios;
import com.grupobbva.bc.per.tele.ldap.serializable.IILDPeUsuario;
import com.grupobbva.seguridad.client.webservice.domain.Usuario;
import com.hildebrando.visado.converter.PersonaDataModal;
import com.hildebrando.visado.modelo.TiivsFeriado;
import com.hildebrando.visado.modelo.TiivsHistSolicitud;
import com.hildebrando.visado.modelo.TiivsMultitabla;
import com.hildebrando.visado.modelo.TiivsMultitablaId;
import com.hildebrando.visado.modelo.TiivsPersona;

public class PruebasUnitariasLocal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testCompletePersona();
		//consultarCliente();
		//verificarMontoComision();
		//probarAsignacionEquivTipoDoi();
		//probarCambiarPatternTipDoi();
		//obtenerFechaRespuesta();
		//validarString();
		//formatMontos();
		//limpiarDirectorioFiles();
		pruebRpta();
		
	}
	
	private static void pruebRpta(){
		//2013-11-08-12.24.01.123456
		//AAAA-MM-DD-HH.mm.SS.ssssss
		//2013-11-14-10.46.37.000350
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS");
		String fecha = sdf.format(date);
		
		System.out.println("fechita:"+"-> "+ fecha);
		System.out.println("fechita:"+"-> "+ Utilitarios.obtenerFechaHoraSegMil());
		String d="VISADO";
		String e="P007734";
		java.util.Date dateNuev = new java.util.Date();
		java.text.SimpleDateFormat formaTx = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSSSSS");
		String fechita = formaTx.format(dateNuev);
		System.out.println("Tx Fecha: "+ fechita+d+e);
		
		System.out.println("Tx Fechi: "+ Utilitarios.obtenerFechaHoraSegMilTx());
		//AAAAMMDDHHmmSSssssss
		//20131108122401123456VISADOP007734
		//20131114152903000289
	}
	
	/**
	 * Realiza la limpieza de los archivos temporales que 
	 * se almacena en la carpeta "files" de la ruta del File Server
	 * */
	private static  void limpiarDirectorioFiles(){
		IILDPeUsuario usuario = new IILDPeUsuario();
		usuario.setNombre("DIEG");
		/*usuario.setApellido1("CLEM");
		usuario.setApellido2("OR");*/
		 String nombreCompleto="".concat(usuario.getNombre()!=null?usuario.getNombre():"")
					.concat(" ").concat(usuario.getApellido1()!=null?usuario.getApellido1():"")
					.concat(" ").concat(usuario.getApellido2()!=null?usuario.getApellido2():"");
		TiivsHistSolicitud his= new TiivsHistSolicitud();
		his.setNomUsuario(nombreCompleto);
		System.out.println("histo:"+his.getNomUsuario());
		
		System.out.println("==== limpiarDirectorioFiles() ==== ");		
		int diferenciaEnDias = 4;
		Date fechaActual = Calendar.getInstance().getTime();
		System.out.println("fechaAactual: "+fechaActual);
		long tiempoActual = fechaActual.getTime();
		System.out.println("tiempoActual: "+tiempoActual);
		long unDia = diferenciaEnDias * 24 * 60 * 60 * 1000;
		System.out.println("unDia: "+unDia);
		System.out.println("tiempoActual - unDia: "+ (tiempoActual - unDia));
		Date fechaAyer = new Date(tiempoActual - unDia);
		
		System.out.println("[limpiarDirectorio]-Fecha corte :" + fechaAyer);
	}

	@SuppressWarnings("unchecked")
	private static void limpiarDirectorio(File directory) {	
		System.out.println("== limpiarDirectorio():INICIO ==");
		int diferenciaEnDias = 1;
		Date fechaActual = Calendar.getInstance().getTime();
		long tiempoActual = fechaActual.getTime();
		long unDia = diferenciaEnDias * 24 * 60 * 60 * 1000;
		Date fechaAyer = new Date(tiempoActual - unDia);
		System.out.println("Iniciando tarea de limpieza de archivos:");
		System.out.println("[limpiarDirectorio]-Fecha corte :" + fechaAyer);
		//Si el directorio existe
		if (directory.exists()) {
			List<File> files = null;
			String ext[] = { "pdf", "PDF" };
			files = (List<File>) FileUtils.listFiles(directory, ext, true);
			System.out.println("[limpiarDirectorio]-Nro archivos(PDF) a depurar:"
					+ FileUtils.listFiles(directory, ext, false).size());
			int num1 = 0, num2 = 0;
			for (File f : files) {
				if (FileUtils.isFileOlder(f, fechaAyer)) {
					System.out.println("[limpiarDirectorio]-Archivo-Depurar: " + f.getName());
					//f.delete();
					num1++;
				} else {
					System.out.println("[limpiarDirectorio]-Archivo-Mantener: " + f.getName());
					num2++;
				}
			}
			System.out.println("[limpiarDirectorio]-Nro archivos (depurados): " + num1);
			System.out.println("[limpiarDirectorio]-Nro archivos (mantenidos): " + num2);
		} else {
			System.out.println("El directorio de depuracion no existe.");
		}
		System.out.println("== limpiarDirectorio():FIN ==");
	}
	
	public static void formatMontos(){
		Double d = new Double(351187878);
		System.out.println("formateado: "+formatearImporte(d));
	}
	
	public static void validarString(){
		String valor="s";
		System.out.println("valor 1: "+valor);
		System.out.println("con valid: "+validarCampoNull(valor));
		
	}
	
	private static String formatearImporte(Double importe){
    	DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
	    simbolo.setDecimalSeparator('.');
	    simbolo.setGroupingSeparator(',');
		//DecimalFormat df = new DecimalFormat("###,###.##", simbolo);
	    DecimalFormat df = new DecimalFormat("#,###,###.00", simbolo);
	    return df.format(importe);
    }
	
	
	public static String validarCampoNull(String campo)
	{
		String resultado="";
		if (campo==null)
		{
			resultado="";
		}
		else
		{
			resultado=campo;
		}
		return resultado;
	}
	public static Date obtenerFechaRespuesta(){
		System.out.println("Desc-Soles: "+ConstantesVisado.MONEDAS.SOLES.toLowerCase());
		System.out.println("=== obtenerFechaRespuesta():INICIO ===");
		Date fechaRetorno;
		String sHoraCorte = "18:00";//getHoraCorte();  //Para obtener hora de corte (parámetro de sistema)		
		System.out.println("[obtFechRpta]-sHoraCorte:"+sHoraCorte);
		String []aHora = null;		
		try{			
			aHora = sHoraCorte.split(":");
		}catch(Exception e){
			aHora = "00:00".split(":");
		}
		int iHoraCorte  =Integer.valueOf(aHora[0] + aHora[1]);
		System.out.println("[obtFechRpta]-iHoraCorte: "+iHoraCorte);
		//Hora actual
		Calendar fechaActual = Calendar.getInstance();	
		int iHoraActual = Integer.valueOf(String.valueOf(fechaActual.get(Calendar.HOUR_OF_DAY))
				+ String.valueOf(fechaActual.get(Calendar.MINUTE)));		
		
		System.out.println("[obtFechRpta]-Compara fecha:"+iHoraActual+"<"+iHoraCorte);
		if(iHoraActual <= iHoraCorte ){ //Si hora actual es menor igual a la hora de corte	
			System.out.println("[obtFechRpta]-Hora es MENOR o IGUAL que la Hora de Corte");
			fechaRetorno = fechaActual.getTime();	 //Retornamos la fecha actual
		} else {
			System.out.println("[obtFechRpta]-Hora es MAYOR a la Hora de Corte, se obtendrá siguiente fecha");
			fechaRetorno = obtenSiguienteDiaHabil(fechaActual);
		}		
		System.out.println("[obtFechRpta]-fechaRetorno: "+fechaRetorno);
		return fechaRetorno;
	}
	
	private static Date obtenSiguienteDiaHabil(Calendar fecha) {
		System.out.println("== obtenSiguienteDiaHabil():INICIO ==");
		Date fechaRetorno;	
		System.out.println("[obtSiguienteDiaHabil]-fecha.DAY_OF_WEEK: "+fecha.get(Calendar.DAY_OF_WEEK));
		fecha.add(Calendar.DATE, 1); // sumamos 1 día a la fecha			
		if(fecha.get(Calendar.DAY_OF_WEEK)==7){ //si es sabado sumamos 2 dias a la fecha
			System.out.println("Es SABADO");
			System.out.println("[obtSiguienteDiaHabil]-Sabado se agregara (02) dias.");
			fecha.add(Calendar.DATE, 2);
			System.out.println("[obtSiguienteDiaHabil]-Fecha:'" + fecha + "' es Sábado se obtendrá el siguiente día hábil ");
		}		
		if(fecha.get(Calendar.DAY_OF_WEEK)==1){ //si es Domingo sumamos 1 dia a la fecha
			System.out.println("Es DOMINGO");
			fecha.add(Calendar.DATE, 1);
			
			System.out.println("[obtSiguienteDiaHabil]-Fecha:'" + fecha + "' es Domingo se obtendrá el siguiente día hábil ");
			System.out.println("[obtSiguienteDiaHabil]-Domingo se agregara (01) dia");
		}
		
			
		//Buscamos si la fecha es feriado
		//filtro.add(Restrictions.eq("indicador", 'N'));//Feriado Nacional
		//filtro.add(Restrictions.eq("fecha", fecha.getTime()));
		
		List<TiivsFeriado> lstFeriados= new ArrayList<TiivsFeriado>();
		try {
			lstFeriados = obtenerFeriadosBD(fecha.getTime());	
			if(lstFeriados!=null){
				System.out.println("[obtSiguienteDiaHabil]-"+ConstantesVisado.MENSAJE.TAMANHIO_LISTA+"de feriados es: "+lstFeriados.size());
			}
		} catch (Exception e) {
			System.out.println("Ocurrio un error en obtener lstFeriados (N): "+e);
		}
		
			
		if(!lstFeriados.isEmpty()){ // Si la fecha es feriado 
			System.out.println("[obtSiguienteDiaHabil]-HayFeriado-Fecha:'" + fecha + "' es Feriado se obtendra el siguiente dia habil ");
			fechaRetorno = obtenSiguienteDiaHabil(fecha);
		} else {
			System.out.println("Como la fecha (No es Feriado) se calcula por dia.");
			
				fechaRetorno = fecha.getTime();
						
		}
		
		
		//fechaRetorno = fecha.getTime();
		//System.out.println("[obtSiguienteDiaHabil]-fechaRetorno: "+fechaRetorno);
		System.out.println("== obtenSiguienteDiaHabil():FIN ==");
		return fechaRetorno;
	}	
	
	private static List<TiivsFeriado> obtenerFeriadosBD(Date fech){
		List<TiivsFeriado> listafinal= new ArrayList<TiivsFeriado>();
		TiivsFeriado fer = new TiivsFeriado();
		fer.setIdFeriado(1);
		fer.setIndicador('N');
		fer.setFecha(new Date());
		listafinal.add(fer);
		
		TiivsFeriado fer2 = new TiivsFeriado();
		fer2.setIdFeriado(3);
		fer2.setIndicador('N');
		fer2.setFecha(new Date(2013, 10, 25));
		listafinal.add(fer2);
		
		return listafinal;
	}
	static void probarCambiarPatternTipDoi(){
		String patron="";
		List<TiivsMultitabla> lista= obtenerMultitabla("T04","0002");
		patron=lista.get(0).getValor4();
		System.out.println("el valor es: "+patron);
		
	}
	
	public static List<TiivsMultitabla> obtenerMultitabla(String cod,String pat){
		List<TiivsMultitabla> listTipBD= new ArrayList<TiivsMultitabla>();
		if(cod.equalsIgnoreCase("T04")){
			//T04	0016	DNI	1	0001	N
			//T04	0002	CARNET DE EXTRANJERIA	1	0001	LN
			if(pat.equalsIgnoreCase("0016") || pat.equalsIgnoreCase("0002")){
				TiivsMultitablaId grupTip= new TiivsMultitablaId();		
				grupTip.setCodMult("T04");
				grupTip.setCodElem("0016");
				TiivsMultitabla data = new TiivsMultitabla();
				data.setId(grupTip);
				data.setValor1("DNI");
				data.setValor4("N");
				listTipBD.add(data);
				
				TiivsMultitablaId grupTip2= new TiivsMultitablaId();
				grupTip2.setCodMult("T04");
				grupTip2.setCodElem("0002");		
				TiivsMultitabla data2 = new TiivsMultitabla();
				data2.setId(grupTip2);
				data2.setValor1("CARNET DE EXTRANJERIA");
				data2.setValor4("LN");
				listTipBD.add(data2);
			}
			
		}
		
		return listTipBD;
	}
	
	static void probarAsignacionEquivTipoDoi(){
		String tipo="0016";
		
		String nuevoTipoHost = Utilitarios.asignarEquivTipoDoi(tipo);
		System.out.println("nuevoTipoHost:"+nuevoTipoHost);
		
		}
	
	static void verificarMontoComision(){
		BResult host = new BResult();
		host.setCode(0001);
		String constante="0001";
		
		if(host.getCode()!=Integer.parseInt(constante)){
			System.out.println("Son iguales");
		}
		//System.out.println("son iguales");
	}
	
	static void consultarCliente(){
		System.out.println("==== consultarCliente() =====");
		TiivsPersona objTiivsPersonaResultado= new TiivsPersona();
		boolean bBooleanPopup=false;
		String tipo="D";
		//String numDoi="44389599";		//LOCAL
		//String numDoi="44389590";		//RENIEC
		String numDoi = "03160115"; 	//HOST
		List<TiivsPersona> lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		try {
			List<TiivsPersona> lstTiivsPersonaLocal = new ArrayList<TiivsPersona>();
			//Se realiza la busqueda en BD Local
			lstTiivsPersonaLocal = buscarPersonaLocal(tipo, numDoi);
			
			List<TiivsPersona> lstTiivsPersonaReniec = new ArrayList<TiivsPersona>();
			List<TiivsPersona> lstTiivsPersonaHost = new ArrayList<TiivsPersona>();
			if (lstTiivsPersonaLocal.size() == 0) {
				System.out.println("1.NO HAY PERSONA LOCAL");				
				//Se realizara la busqueda mediante el servicio HOST
				lstTiivsPersonaHost = buscarPersonaHOST(tipo, numDoi);
				if(lstTiivsPersonaHost.size()==0){
					System.out.println("2.NO HAY PERSONA HOST");	
					/*objTiivsPersonaResultado = new TiivsPersona();
					bBooleanPopup = false;
					Utilitarios.mensajeInfo("INFO",ConstantesVisado.MENSAJE.NO_RESULTADOS+"para la busqueda.");*/
					//Se realiza la busqueda mediente servicio de RENIEC
					lstTiivsPersonaReniec = buscarPersonaReniec(tipo, numDoi);
					if (lstTiivsPersonaReniec.size() == 0) {
						System.out.println("3.NO HAY PERSONA RENIEC");
						objTiivsPersonaResultado = new TiivsPersona();
						bBooleanPopup = false;
						 Utilitarios.mensajeInfo("INFO",ConstantesVisado.MENSAJE.NO_RESULTADOS+"para la busqueda.");
					} else if (lstTiivsPersonaReniec.size() == 1) {
						System.out.println("EXISTE EN RENIEC 1 PERSONA");
						objTiivsPersonaResultado = lstTiivsPersonaReniec.get(0);
						bBooleanPopup = false;
					} else if (lstTiivsPersonaReniec.size() > 1) {
						System.out.println("EXISTE EN RENIEC MAS DE 1 PERSONA");
						bBooleanPopup = true;
						lstTiivsPersonaResultado = lstTiivsPersonaReniec;
					}
				}else if(lstTiivsPersonaHost.size()==1){
					System.out.println("2.b -> EXISTE EN HOST 1 PERSONA");	
					objTiivsPersonaResultado = lstTiivsPersonaHost.get(0);
					bBooleanPopup = false;
				}else if(lstTiivsPersonaHost.size()>1){
					System.out.println("2.c -> EXISTE EN HOST MAS DE 1 PERSONA");	
					lstTiivsPersonaResultado = lstTiivsPersonaHost;
					bBooleanPopup = true;
				}
				
				
			} else if (lstTiivsPersonaLocal.size() == 1) {
				System.out.println("1.b -> EXISTE EN LOCAL 1 PERSONA");	
				System.out.println(ConstantesVisado.MENSAJE.SI_RESULTADOS+"lstTiivsPersonaLocal:  "+ lstTiivsPersonaLocal.size());
				bBooleanPopup = false;
				objTiivsPersonaResultado = lstTiivsPersonaLocal.get(0);
			} else if (lstTiivsPersonaLocal.size() > 1) {
				System.out.println("1.c -> HAY MAS DE 1 PERSONA LOCAL");
				bBooleanPopup = true;
				lstTiivsPersonaResultado = lstTiivsPersonaLocal;

				//personaDataModal = new PersonaDataModal(
					//	lstTiivsPersonaResultado);
			} else {
				System.out.println("ELSE ----");
				bBooleanPopup = true;
			}
			
			System.out.println("MOSTRAR POPUP RESULT:"+bBooleanPopup);
		
		} 
		catch (Exception e) {
			Utilitarios.mensajeError("ERROR", e.getMessage());
			e.printStackTrace();
			System.out.println(ConstantesVisado.MENSAJE.OCURRE_ERROR_CONSULT+"la persona (Local/Reniec)");
		}
	}
	
	public static List<TiivsPersona> buscarPersonaLocal(String tipo, String numDoc){
		TiivsPersona local = new TiivsPersona();
		List<TiivsPersona> listaLocal= new ArrayList<TiivsPersona>();
		System.out.println("\t[local]-numDoc:"+numDoc);
		if(numDoc.equalsIgnoreCase("44389599")){
			System.out.println("=== TRAMA LOCAL ==");
			local.setCodPer(10);
			local.setNombre("DIEGO");
			local.setApePat("CLEMENTE");
			local.setApeMat("ORRR");
			local.setTipDoi("D");
			local.setNumDoi("44389599");
			
			listaLocal.add(local);
		}
		if(numDoc.equalsIgnoreCase("44389599")){
			System.out.println("=== TRAMA LOCAL ==");
			local.setCodPer(10);
			local.setNombre("MARTIN");
			local.setApePat("MARTINEZ");
			local.setApeMat("MARTINUZZI");
			local.setTipDoi("D");
			local.setNumDoi("44389599");
			
			listaLocal.add(local);
		}
		return listaLocal;		
	}
	public static List<TiivsPersona> buscarPersonaReniec(String tipo, String numDoc){
		TiivsPersona persRen = new TiivsPersona();
		List<TiivsPersona> listaReniec= new ArrayList<TiivsPersona>();
		System.out.println("\t[reniec]-numDoc:"+numDoc);
		if(numDoc.equalsIgnoreCase("44389590")){
			System.out.println("=== TRAMA RENIEC ==");
			persRen.setCodPer(10);
			persRen.setNombre("DARIO");
			persRen.setApePat("CCCCCCCCCCCC");
			persRen.setApeMat("EEEEEEEE");
			persRen.setTipDoi("D");
			persRen.setNumDoi("44389554");
			
			listaReniec.add(persRen);
		}
		return listaReniec;		
	}
	public static List<TiivsPersona> buscarPersonaHOST(String tipo, String numDoc){
		TiivsPersona persHost = new TiivsPersona();
		List<TiivsPersona> listaHost= new ArrayList<TiivsPersona>();
		System.out.println("\t[host]-numDoc:"+numDoc);
		if(numDoc.equalsIgnoreCase("03160115")){
			System.out.println("=== TRAMA HOST ==");
			persHost.setCodPer(10);
			persHost.setNombre("LEON");
			persHost.setApePat("CANO");
			persHost.setApeMat("CABALLERO");
			persHost.setTipDoi("L");
			persHost.setNumDoi("03160115");
			persHost.setCodCen("00001481");
			
			listaHost.add(persHost);
		}
		return listaHost;		
	}
	
	static void testCompletePersona(){
		List<TiivsPersona> lstTiivsPersonaResultado = new ArrayList<TiivsPersona>();
		List<TiivsPersona> lstTiivsPersonaBusqueda = new ArrayList<TiivsPersona>();
		
		TiivsPersona pers1= new TiivsPersona();
		pers1.setCodPer(25);
		pers1.setNombre("DIEGO");
		pers1.setApePat("CLEMENTE");
		pers1.setApeMat("OROZCO");
		
		lstTiivsPersonaBusqueda.add(pers1);
		
		TiivsPersona pers2= new TiivsPersona();
		pers2.setCodPer(48);
		pers2.setNombre("DARIO");
		pers2.setApePat("HHH");
		//pers2.setApeMat("WWWWWWWWW");
		lstTiivsPersonaBusqueda.add(pers2);
		
		String query="D";
		System.out.println("Size lista:"+lstTiivsPersonaBusqueda.size());
		
		for (TiivsPersona pers : lstTiivsPersonaBusqueda) 
		{
			if(pers!= null)
			{	
				System.out.println("CodPersona: -->"+pers.getCodPer());
				//if (pers.getApePat()!=null && pers.getApeMat()!=null)
				//{
					
					if (pers.getNombre() != ""
							) 
					{
						
						String nombreCompletoMayuscula = 
						"".concat(pers.getNombre()!=null?pers.getNombre().toUpperCase():"")
						.concat(" ")
						.concat(pers.getApePat()!=null?pers.getApePat().toUpperCase():"")
						.concat(" ")
						.concat(pers.getApeMat()!=null?pers.getApeMat().toUpperCase():"");
						
						System.out.println("completooo->"+nombreCompletoMayuscula);
						String nombreCompletoMayusculaBusqueda = 
								"".concat(pers.getApePat()!=null?pers.getApePat().toUpperCase():"")
								.concat(" ")
								.concat(pers.getApeMat()!=null?pers.getApeMat().toUpperCase():"")
								.concat(" ")
								.concat(pers.getNombre()!=null?pers.getNombre().toUpperCase():"");
						System.out.println("nombreCompletoMayusculaBusqueda->"+nombreCompletoMayusculaBusqueda);
						System.out.println("query:"+query.toUpperCase());
						if (nombreCompletoMayusculaBusqueda.
								contains(query.toUpperCase())) {

							pers.setNombreCompletoMayuscula(nombreCompletoMayuscula);
							System.out.println("final: "+pers.getNombreCompletoMayuscula());
							lstTiivsPersonaResultado.add(pers);
						}
					}
				//}
				
			}
		}

	}

}


package com.bbva.consulta.host.impl;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.bbva.consulta.host.ObtenerDatosVoucherService;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.hildebrando.visado.modelo.TiivsHostVoucher;

/**
 * Implementación DUMMY del servicio consulta Reniec, solo se está verificando
 * que el dni enviado como parámetro coincida con el valor definido.
 * @author dmclemente
 * @version 1.0
 */
public class ObtenerDatosVoucherDUMMY implements ObtenerDatosVoucherService {
	
	private Logger logger = Logger.getLogger(ObtenerDatosVoucherDUMMY.class);

	@Override
	public BResult obtenerDatosVoucher(String nroVoucher)
			throws ServiceException {
		logger.debug("===== obtenerDatosVoucher() - DUMMY ====");
		BResult result = new BResult();		
		TiivsHostVoucher voucher = null;
		
		if(nroVoucher!=null){
			if(nroVoucher.compareTo("56824432344")==0){
				voucher = new TiivsHostVoucher();
				voucher.setNroVoucher("56824432344");
				voucher.setTipoPago("CUENTA");
				voucher.setCuenta("0011-0241-050125252515");
				voucher.setMontoComision("25.00");
				voucher.setDivisa("SOLES");
				voucher.setCodServicio("00305-VISADO POD.SSJJ RED LIMA/PROVINCIA");
				voucher.setFechaPago("25/07/2013");
				voucher.setHoraPago("15:35");
				voucher.setCentroPago("SOLES");
				voucher.setTerminalPago("0486-OF.SAN ISIDRO");
				voucher.setUsuarioPago("P016549");
				
				result.setCode(Constantes.EXITO);
				result.setMessage(Constantes.VACIO);
			}else{
				result.setCode(Integer.parseInt(Constantes.VOUCHER_NO_EXISTE));	
				result.setMessage(Constantes.VOUCHER_NO_EXISTE_CLIENTE);
			}
		}
		result.setObject(voucher);
		return result;
	}

	@Override
	public boolean validaCobroComision(String nroVoucher, Double comision) throws ServiceException {
 
		return false;
	}
	
	/*public BResult devolverPersonaReniecDNI(String usuario, String oficina,String dni) throws ServiceException {
		logger.debug("===== devolverPersonaReniecDNI() - DUMMY ====");
		
		BResult result = new BResult();		
		Persona p = null;
		
		//TODO Agregar mas personas DUMMY si se desea.
		if(dni.equalsIgnoreCase("11111111")){
			p = new Persona();
			p.setNombre("DIEGO");
			p.setApellidoPaterno("CLEMENTE");
			p.setApellidoMaterno("O ");
			p.setNombreCompleto("DIEGO CLEMENTE O ");
			p.setDireccion("URB. ALAMEDA G 53");
			p.setFechaNac("19870220");
			p.setNumerodocIdentidad("11111111");
			
			result.setCode(Constantes.EXITO);
			result.setMessage(Constantes.VACIO);
			
		}else{
			result.setCode(Integer.parseInt(Constantes.RENIEC_NO_EXISTE));	
			result.setMessage(Constantes.RENIEC_NO_EXISTE_CLIENTE);
		}
				
		result.setObject(p);
		return result;
	}*/
}


package com.bbva.consulta.host.impl;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.bbva.consulta.host.ObtenerDatosVoucherService;
import com.bbva.consulta.reniec.util.BResult;
import com.bbva.consulta.reniec.util.Constantes;
import com.hildebrando.visado.modelo.TiivsHostVoucher;

/**
 * Implementación DUMMY del servicio consulta para servicio de Comision, 
 * solo se está verificando que el NroVoucher enviado como parámetro coincida con 
 * el valor definido.
 * @author dmclemente
 * @version 1.0
 */
public class ObtenerDatosVoucherDUMMY implements ObtenerDatosVoucherService {
	
	private Logger logger = Logger.getLogger(ObtenerDatosVoucherDUMMY.class);

	@Override
	public BResult obtenerDatosVoucher(String nroVoucher) throws ServiceException {
		logger.debug("===== obtenerDatosVoucher() - DUMMY-HOST ====");
		BResult result = new BResult();		
		TiivsHostVoucher voucher = null;
		result.setCode(Integer.parseInt(Constantes.HOST_ERROR));
		result.setMessage(Constantes.MSJ_SERV_HOST_ERROR);
		
		if(nroVoucher!=null){
			logger.debug("Dummy-[nroVoucher]: "+nroVoucher);
			if(nroVoucher.compareTo("56824432344")==0){
				voucher = new TiivsHostVoucher();
				voucher.setNroVoucher("56824432344");
				voucher.setTipoPago("CUENTA");
				voucher.setCuenta("0011-0241-050125252515");
				voucher.setMontoComision("200.00");
				voucher.setDivisa("SOLES");
				voucher.setCodServicio("00305-VISADO POD.SSJJ RED LIMA/PROVINCIA");
				voucher.setFechaPago("25/07/2013");
				voucher.setHoraPago("15:35");
				voucher.setCentroPago("SOLES");
				voucher.setTerminalPago("0486-OF.SAN ISIDRO");
				voucher.setUsuarioPago("P016549");
				
				result.setCode(Constantes.EXITO);
				result.setMessage(Constantes.VACIO);
				result.setObject(voucher);
			}
			else if(nroVoucher.compareTo("11111111100")==0){
				voucher = new TiivsHostVoucher();
				voucher.setNroVoucher("11111111100");
				voucher.setTipoPago("CUENTA_OK");
				voucher.setCuenta("0011-0241-050125312222");
				voucher.setMontoComision("100.00");
				voucher.setDivisa("SOLES");
				voucher.setCodServicio("00305-VISADO POD.SSJJ RED LIMA/PROVINCIA");
				voucher.setFechaPago("10/12/2013");
				voucher.setHoraPago("11:08");
				voucher.setCentroPago("SOLES");
				voucher.setTerminalPago("0486-OF.SAN ISIDRO");
				voucher.setUsuarioPago("P018697");
				
				result.setCode(Constantes.EXITO);
				result.setMessage(Constantes.VACIO);
				result.setObject(voucher);
			}
			else if(nroVoucher.compareTo("11111111200")==0){
				voucher = new TiivsHostVoucher();
				voucher.setNroVoucher("11111111200");
				voucher.setTipoPago("CUENTA_200");
				voucher.setCuenta("0011-0241-050125312200");
				voucher.setMontoComision("150.00");
				voucher.setDivisa("SOLES");
				voucher.setCodServicio("00305-VISADO POD.SSJJ RED LIMA/PROVINCIA");
				voucher.setFechaPago("07/12/2013");
				voucher.setHoraPago("10:23");
				voucher.setCentroPago("SOLES");
				voucher.setTerminalPago("0486-OF.SAN ISIDRO");
				voucher.setUsuarioPago("P017433");
				
				result.setCode(Constantes.EXITO);
				result.setMessage(Constantes.VACIO);
				result.setObject(voucher);
			}
			//Si el Nro de Voucher no coincide con el de Host
			else{
				result.setCode(Integer.parseInt(Constantes.VOUCHER_NO_EXISTE));	
				result.setMessage(Constantes.VOUCHER_NO_EXISTE_CLIENTE);
			}
		}

		logger.debug("Dummy-[codigoRptaHost]: "+result.getCode());
		logger.debug("Dummy-[mensajRptaHost]: "+result.getMessage());
		return result;
	}

	@Override
	public boolean validaCobroComision(String nroVoucher, Double comision) throws ServiceException {
 
		return false;
	}
	
}

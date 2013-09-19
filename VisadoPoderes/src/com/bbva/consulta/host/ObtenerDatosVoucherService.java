package com.bbva.consulta.host;

import javax.xml.rpc.ServiceException;

import com.bbva.consulta.reniec.util.BResult;

/**
 * Interface que obtiene datos de un voucher en Host
 * @author dmclemente
 * @version 1.0
 */
public interface ObtenerDatosVoucherService {

	/**
	 * Método que obtiene los datos de un voucher según su el nro de voucher
	 * @param nroVoucher Nro de voucher que desea consultar de Host
	 * @return {@link BResult} con los datos del {@link Voucher}
	 */
	public abstract BResult obtenerDatosVoucher(String nroVoucher) throws ServiceException;
	public abstract boolean validaCobroComision(String nroVoucher, Double comision) throws ServiceException;
}

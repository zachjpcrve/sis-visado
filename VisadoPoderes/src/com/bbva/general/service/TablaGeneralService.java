/**
 * TablaGeneralService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.service;

public interface TablaGeneralService extends javax.xml.rpc.Service {
    public java.lang.String getTablaGeneralAddress();

    public com.bbva.general.service.TablaGeneral getTablaGeneral() throws javax.xml.rpc.ServiceException;

    public com.bbva.general.service.TablaGeneral getTablaGeneral(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

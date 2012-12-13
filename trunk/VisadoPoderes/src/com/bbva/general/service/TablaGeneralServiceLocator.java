/**
 * TablaGeneralServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.service;

public class TablaGeneralServiceLocator extends org.apache.axis.client.Service implements com.bbva.general.service.TablaGeneralService {

    public TablaGeneralServiceLocator() {
    }


    public TablaGeneralServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TablaGeneralServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TablaGeneral
    private java.lang.String TablaGeneral_address = "http://172.31.9.39:80/general/services/TablaGeneral";

    public java.lang.String getTablaGeneralAddress() {
        return TablaGeneral_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TablaGeneralWSDDServiceName = "TablaGeneral";

    public java.lang.String getTablaGeneralWSDDServiceName() {
        return TablaGeneralWSDDServiceName;
    }

    public void setTablaGeneralWSDDServiceName(java.lang.String name) {
        TablaGeneralWSDDServiceName = name;
    }

    public com.bbva.general.service.TablaGeneral getTablaGeneral() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TablaGeneral_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTablaGeneral(endpoint);
    }

    public com.bbva.general.service.TablaGeneral getTablaGeneral(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.bbva.general.service.TablaGeneralSoapBindingStub _stub = new com.bbva.general.service.TablaGeneralSoapBindingStub(portAddress, this);
            _stub.setPortName(getTablaGeneralWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTablaGeneralEndpointAddress(java.lang.String address) {
        TablaGeneral_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.bbva.general.service.TablaGeneral.class.isAssignableFrom(serviceEndpointInterface)) {
                com.bbva.general.service.TablaGeneralSoapBindingStub _stub = new com.bbva.general.service.TablaGeneralSoapBindingStub(new java.net.URL(TablaGeneral_address), this);
                _stub.setPortName(getTablaGeneralWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("TablaGeneral".equals(inputPortName)) {
            return getTablaGeneral();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.general.bbva.com", "TablaGeneralService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.general.bbva.com", "TablaGeneral"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TablaGeneral".equals(portName)) {
            setTablaGeneralEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

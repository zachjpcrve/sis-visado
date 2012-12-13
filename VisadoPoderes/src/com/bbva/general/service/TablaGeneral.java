/**
 * TablaGeneral.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.service;

public interface TablaGeneral extends java.rmi.Remote {
    public com.bbva.general.entities.Oficina[] getOficinas(java.lang.String codOficina, java.lang.String descOficina) throws java.rmi.RemoteException;
    public java.lang.String[] getCentroCostosPorTerritorio(java.lang.String codTerritorio) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Producto[] getProductoListado(java.lang.String productoId, java.lang.String subProductoId, java.lang.String productoNombre, java.lang.String subProductoNombre) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Territorio getTerritorio(java.lang.String codOficina) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Ubigeo[] getProvinciaListado() throws java.rmi.RemoteException;
    public com.bbva.general.entities.CentroSuperior[] getCentroSuperior(java.lang.String codigoOficina, java.lang.String codigoAgrupacion) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Ubigeo[] getDistritoListado() throws java.rmi.RemoteException;
    public java.lang.String getCentroCosto(java.lang.String codOficina) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Ubigeo[] getProvincia(java.lang.String IDUbigeo, java.lang.String descripcion) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Feriado[] getFeriadoListado() throws java.rmi.RemoteException;
    public com.bbva.general.entities.Producto getProducto(java.lang.String productoId, java.lang.String subProductoId) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Centro[] getCentroListado(java.lang.String codigoOficina) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Territorio[] getTerritorioListado() throws java.rmi.RemoteException;
    public com.bbva.general.entities.Ubigeo[] getDepartamentoListado() throws java.rmi.RemoteException;
    public com.bbva.general.entities.Ubigeo[] getDepartamento(java.lang.String descripcion) throws java.rmi.RemoteException;
    public com.bbva.general.entities.Ubigeo[] getDistrito(java.lang.String IDUbigeo, java.lang.String descripcion) throws java.rmi.RemoteException;
}

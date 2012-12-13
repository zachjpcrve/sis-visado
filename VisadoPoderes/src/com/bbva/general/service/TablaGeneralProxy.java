package com.bbva.general.service;

public class TablaGeneralProxy implements com.bbva.general.service.TablaGeneral {
  private String _endpoint = null;
  private com.bbva.general.service.TablaGeneral tablaGeneral = null;
  
  public TablaGeneralProxy() {
    _initTablaGeneralProxy();
  }
  
  public TablaGeneralProxy(String endpoint) {
    _endpoint = endpoint;
    _initTablaGeneralProxy();
  }
  
  private void _initTablaGeneralProxy() {
    try {
      tablaGeneral = (new com.bbva.general.service.TablaGeneralServiceLocator()).getTablaGeneral();
      if (tablaGeneral != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)tablaGeneral)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)tablaGeneral)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (tablaGeneral != null)
      ((javax.xml.rpc.Stub)tablaGeneral)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.bbva.general.service.TablaGeneral getTablaGeneral() {
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral;
  }
  
  public com.bbva.general.entities.Oficina[] getOficinas(java.lang.String codOficina, java.lang.String descOficina) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getOficinas(codOficina, descOficina);
  }
  
  public java.lang.String[] getCentroCostosPorTerritorio(java.lang.String codTerritorio) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getCentroCostosPorTerritorio(codTerritorio);
  }
  
  public com.bbva.general.entities.Producto[] getProductoListado(java.lang.String productoId, java.lang.String subProductoId, java.lang.String productoNombre, java.lang.String subProductoNombre) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getProductoListado(productoId, subProductoId, productoNombre, subProductoNombre);
  }
  
  public com.bbva.general.entities.Territorio getTerritorio(java.lang.String codOficina) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getTerritorio(codOficina);
  }
  
  public com.bbva.general.entities.Ubigeo[] getProvinciaListado() throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getProvinciaListado();
  }
  
  public com.bbva.general.entities.CentroSuperior[] getCentroSuperior(java.lang.String codigoOficina, java.lang.String codigoAgrupacion) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getCentroSuperior(codigoOficina, codigoAgrupacion);
  }
  
  public com.bbva.general.entities.Ubigeo[] getDistritoListado() throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getDistritoListado();
  }
  
  public java.lang.String getCentroCosto(java.lang.String codOficina) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getCentroCosto(codOficina);
  }
  
  public com.bbva.general.entities.Ubigeo[] getProvincia(java.lang.String IDUbigeo, java.lang.String descripcion) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getProvincia(IDUbigeo, descripcion);
  }
  
  public com.bbva.general.entities.Feriado[] getFeriadoListado() throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getFeriadoListado();
  }
  
  public com.bbva.general.entities.Producto getProducto(java.lang.String productoId, java.lang.String subProductoId) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getProducto(productoId, subProductoId);
  }
  
  public com.bbva.general.entities.Centro[] getCentroListado(java.lang.String codigoOficina) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getCentroListado(codigoOficina);
  }
  
  public com.bbva.general.entities.Territorio[] getTerritorioListado() throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getTerritorioListado();
  }
  
  public com.bbva.general.entities.Ubigeo[] getDepartamentoListado() throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getDepartamentoListado();
  }
  
  public com.bbva.general.entities.Ubigeo[] getDepartamento(java.lang.String descripcion) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getDepartamento(descripcion);
  }
  
  public com.bbva.general.entities.Ubigeo[] getDistrito(java.lang.String IDUbigeo, java.lang.String descripcion) throws java.rmi.RemoteException{
    if (tablaGeneral == null)
      _initTablaGeneralProxy();
    return tablaGeneral.getDistrito(IDUbigeo, descripcion);
  }
  
  
}
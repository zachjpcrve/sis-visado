/**
 * Centro.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.entities;

public class Centro  implements java.io.Serializable {
    private java.lang.String codigoOficina;

    private java.lang.String nombre;

    private java.lang.Boolean oficinaBaja;

    private com.bbva.general.entities.Ubigeo departamento;

    private com.bbva.general.entities.Ubigeo provincia;

    private com.bbva.general.entities.Ubigeo distrito;

    private com.bbva.general.entities.Territorio territorio;

    private java.lang.String tipoVia;

    private java.lang.String nombreVia;

    private java.lang.String numeroInterior;

    private java.lang.String numeroExterior;

    private java.lang.String tipZona;

    private java.lang.String nombreZona;

    private java.lang.String prefijoTelefono;

    private java.lang.String telefono1;

    private java.lang.String telefono2;

    private java.lang.String numeroFax;

    private java.util.Calendar fechaApertura;

    private java.util.Calendar fechaBaja;

    private java.util.Calendar fechaAltaCentro;

    private java.lang.String tipoOficina;

    private java.lang.String tipoOficinaCentro;

    private com.bbva.general.entities.CentroSuperior[] centroSuperiores;

    public Centro() {
    }

    public Centro(
           java.lang.String codigoOficina,
           java.lang.String nombre,
           java.lang.Boolean oficinaBaja,
           com.bbva.general.entities.Ubigeo departamento,
           com.bbva.general.entities.Ubigeo provincia,
           com.bbva.general.entities.Ubigeo distrito,
           com.bbva.general.entities.Territorio territorio,
           java.lang.String tipoVia,
           java.lang.String nombreVia,
           java.lang.String numeroInterior,
           java.lang.String numeroExterior,
           java.lang.String tipZona,
           java.lang.String nombreZona,
           java.lang.String prefijoTelefono,
           java.lang.String telefono1,
           java.lang.String telefono2,
           java.lang.String numeroFax,
           java.util.Calendar fechaApertura,
           java.util.Calendar fechaBaja,
           java.util.Calendar fechaAltaCentro,
           java.lang.String tipoOficina,
           java.lang.String tipoOficinaCentro,
           com.bbva.general.entities.CentroSuperior[] centroSuperiores) {
           this.codigoOficina = codigoOficina;
           this.nombre = nombre;
           this.oficinaBaja = oficinaBaja;
           this.departamento = departamento;
           this.provincia = provincia;
           this.distrito = distrito;
           this.territorio = territorio;
           this.tipoVia = tipoVia;
           this.nombreVia = nombreVia;
           this.numeroInterior = numeroInterior;
           this.numeroExterior = numeroExterior;
           this.tipZona = tipZona;
           this.nombreZona = nombreZona;
           this.prefijoTelefono = prefijoTelefono;
           this.telefono1 = telefono1;
           this.telefono2 = telefono2;
           this.numeroFax = numeroFax;
           this.fechaApertura = fechaApertura;
           this.fechaBaja = fechaBaja;
           this.fechaAltaCentro = fechaAltaCentro;
           this.tipoOficina = tipoOficina;
           this.tipoOficinaCentro = tipoOficinaCentro;
           this.centroSuperiores = centroSuperiores;
    }


    /**
     * Gets the codigoOficina value for this Centro.
     * 
     * @return codigoOficina
     */
    public java.lang.String getCodigoOficina() {
        return codigoOficina;
    }


    /**
     * Sets the codigoOficina value for this Centro.
     * 
     * @param codigoOficina
     */
    public void setCodigoOficina(java.lang.String codigoOficina) {
        this.codigoOficina = codigoOficina;
    }


    /**
     * Gets the nombre value for this Centro.
     * 
     * @return nombre
     */
    public java.lang.String getNombre() {
        return nombre;
    }


    /**
     * Sets the nombre value for this Centro.
     * 
     * @param nombre
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }


    /**
     * Gets the oficinaBaja value for this Centro.
     * 
     * @return oficinaBaja
     */
    public java.lang.Boolean getOficinaBaja() {
        return oficinaBaja;
    }


    /**
     * Sets the oficinaBaja value for this Centro.
     * 
     * @param oficinaBaja
     */
    public void setOficinaBaja(java.lang.Boolean oficinaBaja) {
        this.oficinaBaja = oficinaBaja;
    }


    /**
     * Gets the departamento value for this Centro.
     * 
     * @return departamento
     */
    public com.bbva.general.entities.Ubigeo getDepartamento() {
        return departamento;
    }


    /**
     * Sets the departamento value for this Centro.
     * 
     * @param departamento
     */
    public void setDepartamento(com.bbva.general.entities.Ubigeo departamento) {
        this.departamento = departamento;
    }


    /**
     * Gets the provincia value for this Centro.
     * 
     * @return provincia
     */
    public com.bbva.general.entities.Ubigeo getProvincia() {
        return provincia;
    }


    /**
     * Sets the provincia value for this Centro.
     * 
     * @param provincia
     */
    public void setProvincia(com.bbva.general.entities.Ubigeo provincia) {
        this.provincia = provincia;
    }


    /**
     * Gets the distrito value for this Centro.
     * 
     * @return distrito
     */
    public com.bbva.general.entities.Ubigeo getDistrito() {
        return distrito;
    }


    /**
     * Sets the distrito value for this Centro.
     * 
     * @param distrito
     */
    public void setDistrito(com.bbva.general.entities.Ubigeo distrito) {
        this.distrito = distrito;
    }


    /**
     * Gets the territorio value for this Centro.
     * 
     * @return territorio
     */
    public com.bbva.general.entities.Territorio getTerritorio() {
        return territorio;
    }


    /**
     * Sets the territorio value for this Centro.
     * 
     * @param territorio
     */
    public void setTerritorio(com.bbva.general.entities.Territorio territorio) {
        this.territorio = territorio;
    }


    /**
     * Gets the tipoVia value for this Centro.
     * 
     * @return tipoVia
     */
    public java.lang.String getTipoVia() {
        return tipoVia;
    }


    /**
     * Sets the tipoVia value for this Centro.
     * 
     * @param tipoVia
     */
    public void setTipoVia(java.lang.String tipoVia) {
        this.tipoVia = tipoVia;
    }


    /**
     * Gets the nombreVia value for this Centro.
     * 
     * @return nombreVia
     */
    public java.lang.String getNombreVia() {
        return nombreVia;
    }


    /**
     * Sets the nombreVia value for this Centro.
     * 
     * @param nombreVia
     */
    public void setNombreVia(java.lang.String nombreVia) {
        this.nombreVia = nombreVia;
    }


    /**
     * Gets the numeroInterior value for this Centro.
     * 
     * @return numeroInterior
     */
    public java.lang.String getNumeroInterior() {
        return numeroInterior;
    }


    /**
     * Sets the numeroInterior value for this Centro.
     * 
     * @param numeroInterior
     */
    public void setNumeroInterior(java.lang.String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }


    /**
     * Gets the numeroExterior value for this Centro.
     * 
     * @return numeroExterior
     */
    public java.lang.String getNumeroExterior() {
        return numeroExterior;
    }


    /**
     * Sets the numeroExterior value for this Centro.
     * 
     * @param numeroExterior
     */
    public void setNumeroExterior(java.lang.String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }


    /**
     * Gets the tipZona value for this Centro.
     * 
     * @return tipZona
     */
    public java.lang.String getTipZona() {
        return tipZona;
    }


    /**
     * Sets the tipZona value for this Centro.
     * 
     * @param tipZona
     */
    public void setTipZona(java.lang.String tipZona) {
        this.tipZona = tipZona;
    }


    /**
     * Gets the nombreZona value for this Centro.
     * 
     * @return nombreZona
     */
    public java.lang.String getNombreZona() {
        return nombreZona;
    }


    /**
     * Sets the nombreZona value for this Centro.
     * 
     * @param nombreZona
     */
    public void setNombreZona(java.lang.String nombreZona) {
        this.nombreZona = nombreZona;
    }


    /**
     * Gets the prefijoTelefono value for this Centro.
     * 
     * @return prefijoTelefono
     */
    public java.lang.String getPrefijoTelefono() {
        return prefijoTelefono;
    }


    /**
     * Sets the prefijoTelefono value for this Centro.
     * 
     * @param prefijoTelefono
     */
    public void setPrefijoTelefono(java.lang.String prefijoTelefono) {
        this.prefijoTelefono = prefijoTelefono;
    }


    /**
     * Gets the telefono1 value for this Centro.
     * 
     * @return telefono1
     */
    public java.lang.String getTelefono1() {
        return telefono1;
    }


    /**
     * Sets the telefono1 value for this Centro.
     * 
     * @param telefono1
     */
    public void setTelefono1(java.lang.String telefono1) {
        this.telefono1 = telefono1;
    }


    /**
     * Gets the telefono2 value for this Centro.
     * 
     * @return telefono2
     */
    public java.lang.String getTelefono2() {
        return telefono2;
    }


    /**
     * Sets the telefono2 value for this Centro.
     * 
     * @param telefono2
     */
    public void setTelefono2(java.lang.String telefono2) {
        this.telefono2 = telefono2;
    }


    /**
     * Gets the numeroFax value for this Centro.
     * 
     * @return numeroFax
     */
    public java.lang.String getNumeroFax() {
        return numeroFax;
    }


    /**
     * Sets the numeroFax value for this Centro.
     * 
     * @param numeroFax
     */
    public void setNumeroFax(java.lang.String numeroFax) {
        this.numeroFax = numeroFax;
    }


    /**
     * Gets the fechaApertura value for this Centro.
     * 
     * @return fechaApertura
     */
    public java.util.Calendar getFechaApertura() {
        return fechaApertura;
    }


    /**
     * Sets the fechaApertura value for this Centro.
     * 
     * @param fechaApertura
     */
    public void setFechaApertura(java.util.Calendar fechaApertura) {
        this.fechaApertura = fechaApertura;
    }


    /**
     * Gets the fechaBaja value for this Centro.
     * 
     * @return fechaBaja
     */
    public java.util.Calendar getFechaBaja() {
        return fechaBaja;
    }


    /**
     * Sets the fechaBaja value for this Centro.
     * 
     * @param fechaBaja
     */
    public void setFechaBaja(java.util.Calendar fechaBaja) {
        this.fechaBaja = fechaBaja;
    }


    /**
     * Gets the fechaAltaCentro value for this Centro.
     * 
     * @return fechaAltaCentro
     */
    public java.util.Calendar getFechaAltaCentro() {
        return fechaAltaCentro;
    }


    /**
     * Sets the fechaAltaCentro value for this Centro.
     * 
     * @param fechaAltaCentro
     */
    public void setFechaAltaCentro(java.util.Calendar fechaAltaCentro) {
        this.fechaAltaCentro = fechaAltaCentro;
    }


    /**
     * Gets the tipoOficina value for this Centro.
     * 
     * @return tipoOficina
     */
    public java.lang.String getTipoOficina() {
        return tipoOficina;
    }


    /**
     * Sets the tipoOficina value for this Centro.
     * 
     * @param tipoOficina
     */
    public void setTipoOficina(java.lang.String tipoOficina) {
        this.tipoOficina = tipoOficina;
    }


    /**
     * Gets the tipoOficinaCentro value for this Centro.
     * 
     * @return tipoOficinaCentro
     */
    public java.lang.String getTipoOficinaCentro() {
        return tipoOficinaCentro;
    }


    /**
     * Sets the tipoOficinaCentro value for this Centro.
     * 
     * @param tipoOficinaCentro
     */
    public void setTipoOficinaCentro(java.lang.String tipoOficinaCentro) {
        this.tipoOficinaCentro = tipoOficinaCentro;
    }


    /**
     * Gets the centroSuperiores value for this Centro.
     * 
     * @return centroSuperiores
     */
    public com.bbva.general.entities.CentroSuperior[] getCentroSuperiores() {
        return centroSuperiores;
    }


    /**
     * Sets the centroSuperiores value for this Centro.
     * 
     * @param centroSuperiores
     */
    public void setCentroSuperiores(com.bbva.general.entities.CentroSuperior[] centroSuperiores) {
        this.centroSuperiores = centroSuperiores;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Centro)) return false;
        Centro other = (Centro) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codigoOficina==null && other.getCodigoOficina()==null) || 
             (this.codigoOficina!=null &&
              this.codigoOficina.equals(other.getCodigoOficina()))) &&
            ((this.nombre==null && other.getNombre()==null) || 
             (this.nombre!=null &&
              this.nombre.equals(other.getNombre()))) &&
            ((this.oficinaBaja==null && other.getOficinaBaja()==null) || 
             (this.oficinaBaja!=null &&
              this.oficinaBaja.equals(other.getOficinaBaja()))) &&
            ((this.departamento==null && other.getDepartamento()==null) || 
             (this.departamento!=null &&
              this.departamento.equals(other.getDepartamento()))) &&
            ((this.provincia==null && other.getProvincia()==null) || 
             (this.provincia!=null &&
              this.provincia.equals(other.getProvincia()))) &&
            ((this.distrito==null && other.getDistrito()==null) || 
             (this.distrito!=null &&
              this.distrito.equals(other.getDistrito()))) &&
            ((this.territorio==null && other.getTerritorio()==null) || 
             (this.territorio!=null &&
              this.territorio.equals(other.getTerritorio()))) &&
            ((this.tipoVia==null && other.getTipoVia()==null) || 
             (this.tipoVia!=null &&
              this.tipoVia.equals(other.getTipoVia()))) &&
            ((this.nombreVia==null && other.getNombreVia()==null) || 
             (this.nombreVia!=null &&
              this.nombreVia.equals(other.getNombreVia()))) &&
            ((this.numeroInterior==null && other.getNumeroInterior()==null) || 
             (this.numeroInterior!=null &&
              this.numeroInterior.equals(other.getNumeroInterior()))) &&
            ((this.numeroExterior==null && other.getNumeroExterior()==null) || 
             (this.numeroExterior!=null &&
              this.numeroExterior.equals(other.getNumeroExterior()))) &&
            ((this.tipZona==null && other.getTipZona()==null) || 
             (this.tipZona!=null &&
              this.tipZona.equals(other.getTipZona()))) &&
            ((this.nombreZona==null && other.getNombreZona()==null) || 
             (this.nombreZona!=null &&
              this.nombreZona.equals(other.getNombreZona()))) &&
            ((this.prefijoTelefono==null && other.getPrefijoTelefono()==null) || 
             (this.prefijoTelefono!=null &&
              this.prefijoTelefono.equals(other.getPrefijoTelefono()))) &&
            ((this.telefono1==null && other.getTelefono1()==null) || 
             (this.telefono1!=null &&
              this.telefono1.equals(other.getTelefono1()))) &&
            ((this.telefono2==null && other.getTelefono2()==null) || 
             (this.telefono2!=null &&
              this.telefono2.equals(other.getTelefono2()))) &&
            ((this.numeroFax==null && other.getNumeroFax()==null) || 
             (this.numeroFax!=null &&
              this.numeroFax.equals(other.getNumeroFax()))) &&
            ((this.fechaApertura==null && other.getFechaApertura()==null) || 
             (this.fechaApertura!=null &&
              this.fechaApertura.equals(other.getFechaApertura()))) &&
            ((this.fechaBaja==null && other.getFechaBaja()==null) || 
             (this.fechaBaja!=null &&
              this.fechaBaja.equals(other.getFechaBaja()))) &&
            ((this.fechaAltaCentro==null && other.getFechaAltaCentro()==null) || 
             (this.fechaAltaCentro!=null &&
              this.fechaAltaCentro.equals(other.getFechaAltaCentro()))) &&
            ((this.tipoOficina==null && other.getTipoOficina()==null) || 
             (this.tipoOficina!=null &&
              this.tipoOficina.equals(other.getTipoOficina()))) &&
            ((this.tipoOficinaCentro==null && other.getTipoOficinaCentro()==null) || 
             (this.tipoOficinaCentro!=null &&
              this.tipoOficinaCentro.equals(other.getTipoOficinaCentro()))) &&
            ((this.centroSuperiores==null && other.getCentroSuperiores()==null) || 
             (this.centroSuperiores!=null &&
              java.util.Arrays.equals(this.centroSuperiores, other.getCentroSuperiores())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCodigoOficina() != null) {
            _hashCode += getCodigoOficina().hashCode();
        }
        if (getNombre() != null) {
            _hashCode += getNombre().hashCode();
        }
        if (getOficinaBaja() != null) {
            _hashCode += getOficinaBaja().hashCode();
        }
        if (getDepartamento() != null) {
            _hashCode += getDepartamento().hashCode();
        }
        if (getProvincia() != null) {
            _hashCode += getProvincia().hashCode();
        }
        if (getDistrito() != null) {
            _hashCode += getDistrito().hashCode();
        }
        if (getTerritorio() != null) {
            _hashCode += getTerritorio().hashCode();
        }
        if (getTipoVia() != null) {
            _hashCode += getTipoVia().hashCode();
        }
        if (getNombreVia() != null) {
            _hashCode += getNombreVia().hashCode();
        }
        if (getNumeroInterior() != null) {
            _hashCode += getNumeroInterior().hashCode();
        }
        if (getNumeroExterior() != null) {
            _hashCode += getNumeroExterior().hashCode();
        }
        if (getTipZona() != null) {
            _hashCode += getTipZona().hashCode();
        }
        if (getNombreZona() != null) {
            _hashCode += getNombreZona().hashCode();
        }
        if (getPrefijoTelefono() != null) {
            _hashCode += getPrefijoTelefono().hashCode();
        }
        if (getTelefono1() != null) {
            _hashCode += getTelefono1().hashCode();
        }
        if (getTelefono2() != null) {
            _hashCode += getTelefono2().hashCode();
        }
        if (getNumeroFax() != null) {
            _hashCode += getNumeroFax().hashCode();
        }
        if (getFechaApertura() != null) {
            _hashCode += getFechaApertura().hashCode();
        }
        if (getFechaBaja() != null) {
            _hashCode += getFechaBaja().hashCode();
        }
        if (getFechaAltaCentro() != null) {
            _hashCode += getFechaAltaCentro().hashCode();
        }
        if (getTipoOficina() != null) {
            _hashCode += getTipoOficina().hashCode();
        }
        if (getTipoOficinaCentro() != null) {
            _hashCode += getTipoOficinaCentro().hashCode();
        }
        if (getCentroSuperiores() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCentroSuperiores());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCentroSuperiores(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Centro.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Centro"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoOficina");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codigoOficina"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombre");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oficinaBaja");
        elemField.setXmlName(new javax.xml.namespace.QName("", "oficinaBaja"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departamento");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departamento"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Ubigeo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("provincia");
        elemField.setXmlName(new javax.xml.namespace.QName("", "provincia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Ubigeo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distrito");
        elemField.setXmlName(new javax.xml.namespace.QName("", "distrito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Ubigeo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("territorio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "territorio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Territorio"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoVia");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipoVia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreVia");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombreVia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroInterior");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroInterior"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroExterior");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroExterior"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipZona");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipZona"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreZona");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombreZona"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prefijoTelefono");
        elemField.setXmlName(new javax.xml.namespace.QName("", "prefijoTelefono"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telefono1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "telefono1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telefono2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "telefono2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroFax");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroFax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaApertura");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fechaApertura"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaBaja");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fechaBaja"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaAltaCentro");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fechaAltaCentro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoOficina");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipoOficina"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoOficinaCentro");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipoOficinaCentro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("centroSuperiores");
        elemField.setXmlName(new javax.xml.namespace.QName("", "centroSuperiores"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "CentroSuperior"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "CentroSuperior"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

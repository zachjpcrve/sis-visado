/**
 * Oficina.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.entities;

public class Oficina  implements java.io.Serializable {
    private java.lang.String nombre;

    private java.lang.String estado;

    private com.bbva.general.entities.Ubigeo departamento;

    private com.bbva.general.entities.Ubigeo provincia;

    private com.bbva.general.entities.Ubigeo distrito;

    private com.bbva.general.entities.Territorio territorio;

    private java.lang.String direccion;

    private java.lang.String codEstado;

    public Oficina() {
    }

    public Oficina(
           java.lang.String nombre,
           java.lang.String estado,
           com.bbva.general.entities.Ubigeo departamento,
           com.bbva.general.entities.Ubigeo provincia,
           com.bbva.general.entities.Ubigeo distrito,
           com.bbva.general.entities.Territorio territorio,
           java.lang.String direccion,
           java.lang.String codEstado) {
           this.nombre = nombre;
           this.estado = estado;
           this.departamento = departamento;
           this.provincia = provincia;
           this.distrito = distrito;
           this.territorio = territorio;
           this.direccion = direccion;
           this.codEstado = codEstado;
    }


    /**
     * Gets the nombre value for this Oficina.
     * 
     * @return nombre
     */
    public java.lang.String getNombre() {
        return nombre;
    }


    /**
     * Sets the nombre value for this Oficina.
     * 
     * @param nombre
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }


    /**
     * Gets the estado value for this Oficina.
     * 
     * @return estado
     */
    public java.lang.String getEstado() {
        return estado;
    }


    /**
     * Sets the estado value for this Oficina.
     * 
     * @param estado
     */
    public void setEstado(java.lang.String estado) {
        this.estado = estado;
    }


    /**
     * Gets the departamento value for this Oficina.
     * 
     * @return departamento
     */
    public com.bbva.general.entities.Ubigeo getDepartamento() {
        return departamento;
    }


    /**
     * Sets the departamento value for this Oficina.
     * 
     * @param departamento
     */
    public void setDepartamento(com.bbva.general.entities.Ubigeo departamento) {
        this.departamento = departamento;
    }


    /**
     * Gets the provincia value for this Oficina.
     * 
     * @return provincia
     */
    public com.bbva.general.entities.Ubigeo getProvincia() {
        return provincia;
    }


    /**
     * Sets the provincia value for this Oficina.
     * 
     * @param provincia
     */
    public void setProvincia(com.bbva.general.entities.Ubigeo provincia) {
        this.provincia = provincia;
    }


    /**
     * Gets the distrito value for this Oficina.
     * 
     * @return distrito
     */
    public com.bbva.general.entities.Ubigeo getDistrito() {
        return distrito;
    }


    /**
     * Sets the distrito value for this Oficina.
     * 
     * @param distrito
     */
    public void setDistrito(com.bbva.general.entities.Ubigeo distrito) {
        this.distrito = distrito;
    }


    /**
     * Gets the territorio value for this Oficina.
     * 
     * @return territorio
     */
    public com.bbva.general.entities.Territorio getTerritorio() {
        return territorio;
    }


    /**
     * Sets the territorio value for this Oficina.
     * 
     * @param territorio
     */
    public void setTerritorio(com.bbva.general.entities.Territorio territorio) {
        this.territorio = territorio;
    }


    /**
     * Gets the direccion value for this Oficina.
     * 
     * @return direccion
     */
    public java.lang.String getDireccion() {
        return direccion;
    }


    /**
     * Sets the direccion value for this Oficina.
     * 
     * @param direccion
     */
    public void setDireccion(java.lang.String direccion) {
        this.direccion = direccion;
    }


    /**
     * Gets the codEstado value for this Oficina.
     * 
     * @return codEstado
     */
    public java.lang.String getCodEstado() {
        return codEstado;
    }


    /**
     * Sets the codEstado value for this Oficina.
     * 
     * @param codEstado
     */
    public void setCodEstado(java.lang.String codEstado) {
        this.codEstado = codEstado;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Oficina)) return false;
        Oficina other = (Oficina) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nombre==null && other.getNombre()==null) || 
             (this.nombre!=null &&
              this.nombre.equals(other.getNombre()))) &&
            ((this.estado==null && other.getEstado()==null) || 
             (this.estado!=null &&
              this.estado.equals(other.getEstado()))) &&
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
            ((this.direccion==null && other.getDireccion()==null) || 
             (this.direccion!=null &&
              this.direccion.equals(other.getDireccion()))) &&
            ((this.codEstado==null && other.getCodEstado()==null) || 
             (this.codEstado!=null &&
              this.codEstado.equals(other.getCodEstado())));
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
        if (getNombre() != null) {
            _hashCode += getNombre().hashCode();
        }
        if (getEstado() != null) {
            _hashCode += getEstado().hashCode();
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
        if (getDireccion() != null) {
            _hashCode += getDireccion().hashCode();
        }
        if (getCodEstado() != null) {
            _hashCode += getCodEstado().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Oficina.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Oficina"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombre");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estado");
        elemField.setXmlName(new javax.xml.namespace.QName("", "estado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("direccion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "direccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codEstado");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codEstado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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

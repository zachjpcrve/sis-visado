/**
 * CentroSuperior.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.entities;

public class CentroSuperior  implements java.io.Serializable {
    private java.lang.String codigoCentroSuperior;

    private java.lang.String codigoAgrupacion;

    private java.lang.String codigoNivel;

    public CentroSuperior() {
    }

    public CentroSuperior(
           java.lang.String codigoCentroSuperior,
           java.lang.String codigoAgrupacion,
           java.lang.String codigoNivel) {
           this.codigoCentroSuperior = codigoCentroSuperior;
           this.codigoAgrupacion = codigoAgrupacion;
           this.codigoNivel = codigoNivel;
    }


    /**
     * Gets the codigoCentroSuperior value for this CentroSuperior.
     * 
     * @return codigoCentroSuperior
     */
    public java.lang.String getCodigoCentroSuperior() {
        return codigoCentroSuperior;
    }


    /**
     * Sets the codigoCentroSuperior value for this CentroSuperior.
     * 
     * @param codigoCentroSuperior
     */
    public void setCodigoCentroSuperior(java.lang.String codigoCentroSuperior) {
        this.codigoCentroSuperior = codigoCentroSuperior;
    }


    /**
     * Gets the codigoAgrupacion value for this CentroSuperior.
     * 
     * @return codigoAgrupacion
     */
    public java.lang.String getCodigoAgrupacion() {
        return codigoAgrupacion;
    }


    /**
     * Sets the codigoAgrupacion value for this CentroSuperior.
     * 
     * @param codigoAgrupacion
     */
    public void setCodigoAgrupacion(java.lang.String codigoAgrupacion) {
        this.codigoAgrupacion = codigoAgrupacion;
    }


    /**
     * Gets the codigoNivel value for this CentroSuperior.
     * 
     * @return codigoNivel
     */
    public java.lang.String getCodigoNivel() {
        return codigoNivel;
    }


    /**
     * Sets the codigoNivel value for this CentroSuperior.
     * 
     * @param codigoNivel
     */
    public void setCodigoNivel(java.lang.String codigoNivel) {
        this.codigoNivel = codigoNivel;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CentroSuperior)) return false;
        CentroSuperior other = (CentroSuperior) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codigoCentroSuperior==null && other.getCodigoCentroSuperior()==null) || 
             (this.codigoCentroSuperior!=null &&
              this.codigoCentroSuperior.equals(other.getCodigoCentroSuperior()))) &&
            ((this.codigoAgrupacion==null && other.getCodigoAgrupacion()==null) || 
             (this.codigoAgrupacion!=null &&
              this.codigoAgrupacion.equals(other.getCodigoAgrupacion()))) &&
            ((this.codigoNivel==null && other.getCodigoNivel()==null) || 
             (this.codigoNivel!=null &&
              this.codigoNivel.equals(other.getCodigoNivel())));
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
        if (getCodigoCentroSuperior() != null) {
            _hashCode += getCodigoCentroSuperior().hashCode();
        }
        if (getCodigoAgrupacion() != null) {
            _hashCode += getCodigoAgrupacion().hashCode();
        }
        if (getCodigoNivel() != null) {
            _hashCode += getCodigoNivel().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CentroSuperior.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "CentroSuperior"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoCentroSuperior");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codigoCentroSuperior"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoAgrupacion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codigoAgrupacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoNivel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codigoNivel"));
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

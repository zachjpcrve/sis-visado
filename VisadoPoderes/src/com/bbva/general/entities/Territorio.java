/**
 * Territorio.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.entities;

public class Territorio  implements java.io.Serializable {
    private java.lang.String codigoTerritorio;

    private java.lang.String descripcionTerritorio;

    public Territorio() {
    }

    public Territorio(
           java.lang.String codigoTerritorio,
           java.lang.String descripcionTerritorio) {
           this.codigoTerritorio = codigoTerritorio;
           this.descripcionTerritorio = descripcionTerritorio;
    }


    /**
     * Gets the codigoTerritorio value for this Territorio.
     * 
     * @return codigoTerritorio
     */
    public java.lang.String getCodigoTerritorio() {
        return codigoTerritorio;
    }


    /**
     * Sets the codigoTerritorio value for this Territorio.
     * 
     * @param codigoTerritorio
     */
    public void setCodigoTerritorio(java.lang.String codigoTerritorio) {
        this.codigoTerritorio = codigoTerritorio;
    }


    /**
     * Gets the descripcionTerritorio value for this Territorio.
     * 
     * @return descripcionTerritorio
     */
    public java.lang.String getDescripcionTerritorio() {
        return descripcionTerritorio;
    }


    /**
     * Sets the descripcionTerritorio value for this Territorio.
     * 
     * @param descripcionTerritorio
     */
    public void setDescripcionTerritorio(java.lang.String descripcionTerritorio) {
        this.descripcionTerritorio = descripcionTerritorio;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Territorio)) return false;
        Territorio other = (Territorio) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codigoTerritorio==null && other.getCodigoTerritorio()==null) || 
             (this.codigoTerritorio!=null &&
              this.codigoTerritorio.equals(other.getCodigoTerritorio()))) &&
            ((this.descripcionTerritorio==null && other.getDescripcionTerritorio()==null) || 
             (this.descripcionTerritorio!=null &&
              this.descripcionTerritorio.equals(other.getDescripcionTerritorio())));
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
        if (getCodigoTerritorio() != null) {
            _hashCode += getCodigoTerritorio().hashCode();
        }
        if (getDescripcionTerritorio() != null) {
            _hashCode += getDescripcionTerritorio().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Territorio.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Territorio"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoTerritorio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codigoTerritorio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("descripcionTerritorio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "descripcionTerritorio"));
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

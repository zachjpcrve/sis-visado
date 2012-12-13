/**
 * Producto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.bbva.general.entities;

public class Producto  implements java.io.Serializable {
    private java.lang.String productoId;

    private java.lang.String subProductoId;

    private java.lang.String productoNombre;

    private java.lang.String subProductoNombre;

    public Producto() {
    }

    public Producto(
           java.lang.String productoId,
           java.lang.String subProductoId,
           java.lang.String productoNombre,
           java.lang.String subProductoNombre) {
           this.productoId = productoId;
           this.subProductoId = subProductoId;
           this.productoNombre = productoNombre;
           this.subProductoNombre = subProductoNombre;
    }


    /**
     * Gets the productoId value for this Producto.
     * 
     * @return productoId
     */
    public java.lang.String getProductoId() {
        return productoId;
    }


    /**
     * Sets the productoId value for this Producto.
     * 
     * @param productoId
     */
    public void setProductoId(java.lang.String productoId) {
        this.productoId = productoId;
    }


    /**
     * Gets the subProductoId value for this Producto.
     * 
     * @return subProductoId
     */
    public java.lang.String getSubProductoId() {
        return subProductoId;
    }


    /**
     * Sets the subProductoId value for this Producto.
     * 
     * @param subProductoId
     */
    public void setSubProductoId(java.lang.String subProductoId) {
        this.subProductoId = subProductoId;
    }


    /**
     * Gets the productoNombre value for this Producto.
     * 
     * @return productoNombre
     */
    public java.lang.String getProductoNombre() {
        return productoNombre;
    }


    /**
     * Sets the productoNombre value for this Producto.
     * 
     * @param productoNombre
     */
    public void setProductoNombre(java.lang.String productoNombre) {
        this.productoNombre = productoNombre;
    }


    /**
     * Gets the subProductoNombre value for this Producto.
     * 
     * @return subProductoNombre
     */
    public java.lang.String getSubProductoNombre() {
        return subProductoNombre;
    }


    /**
     * Sets the subProductoNombre value for this Producto.
     * 
     * @param subProductoNombre
     */
    public void setSubProductoNombre(java.lang.String subProductoNombre) {
        this.subProductoNombre = subProductoNombre;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Producto)) return false;
        Producto other = (Producto) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.productoId==null && other.getProductoId()==null) || 
             (this.productoId!=null &&
              this.productoId.equals(other.getProductoId()))) &&
            ((this.subProductoId==null && other.getSubProductoId()==null) || 
             (this.subProductoId!=null &&
              this.subProductoId.equals(other.getSubProductoId()))) &&
            ((this.productoNombre==null && other.getProductoNombre()==null) || 
             (this.productoNombre!=null &&
              this.productoNombre.equals(other.getProductoNombre()))) &&
            ((this.subProductoNombre==null && other.getSubProductoNombre()==null) || 
             (this.subProductoNombre!=null &&
              this.subProductoNombre.equals(other.getSubProductoNombre())));
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
        if (getProductoId() != null) {
            _hashCode += getProductoId().hashCode();
        }
        if (getSubProductoId() != null) {
            _hashCode += getSubProductoId().hashCode();
        }
        if (getProductoNombre() != null) {
            _hashCode += getProductoNombre().hashCode();
        }
        if (getSubProductoNombre() != null) {
            _hashCode += getSubProductoNombre().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Producto.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://entities.general.bbva.com", "Producto"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productoId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "productoId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subProductoId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subProductoId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productoNombre");
        elemField.setXmlName(new javax.xml.namespace.QName("", "productoNombre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subProductoNombre");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subProductoNombre"));
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

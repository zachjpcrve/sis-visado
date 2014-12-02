/*
 * An XML document type.
 * Localname: actualizarHistorialDocResponse
 * Namespace: http://ws.negocio.escaner.bbva.pe
 * Java type: pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.negocio.ws;


/**
 * A document containing one actualizarHistorialDocResponse(@http://ws.negocio.escaner.bbva.pe) element.
 *
 * This is a complex type.
 */
public interface ActualizarHistorialDocResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ActualizarHistorialDocResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF6BAC28F19A60AACAA7EB4357BCCF1B0").resolveHandle("actualizarhistorialdocresponseb188doctype");
    
    /**
     * Gets the "actualizarHistorialDocResponse" element
     */
    pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse getActualizarHistorialDocResponse();
    
    /**
     * Sets the "actualizarHistorialDocResponse" element
     */
    void setActualizarHistorialDocResponse(pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse actualizarHistorialDocResponse);
    
    /**
     * Appends and returns a new empty "actualizarHistorialDocResponse" element
     */
    pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse addNewActualizarHistorialDocResponse();
    
    /**
     * An XML actualizarHistorialDocResponse(@http://ws.negocio.escaner.bbva.pe).
     *
     * This is a complex type.
     */
    public interface ActualizarHistorialDocResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ActualizarHistorialDocResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF6BAC28F19A60AACAA7EB4357BCCF1B0").resolveHandle("actualizarhistorialdocresponsec0f2elemtype");
        
        /**
         * Gets the "actualizarHistorialDocReturn" element
         */
        pe.bbva.escaner.common.dto.HistorialDocDTO getActualizarHistorialDocReturn();
        
        /**
         * Tests for nil "actualizarHistorialDocReturn" element
         */
        boolean isNilActualizarHistorialDocReturn();
        
        /**
         * Sets the "actualizarHistorialDocReturn" element
         */
        void setActualizarHistorialDocReturn(pe.bbva.escaner.common.dto.HistorialDocDTO actualizarHistorialDocReturn);
        
        /**
         * Appends and returns a new empty "actualizarHistorialDocReturn" element
         */
        pe.bbva.escaner.common.dto.HistorialDocDTO addNewActualizarHistorialDocReturn();
        
        /**
         * Nils the "actualizarHistorialDocReturn" element
         */
        void setNilActualizarHistorialDocReturn();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse newInstance() {
              return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument newInstance() {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}

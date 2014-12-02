/*
 * XML Type:  AuditoriaDTO
 * Namespace: http://dto.common.escaner.bbva.pe
 * Java type: pe.bbva.escaner.common.dto.AuditoriaDTO
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.common.dto;


/**
 * An XML AuditoriaDTO(@http://dto.common.escaner.bbva.pe).
 *
 * This is a complex type.
 */
public interface AuditoriaDTO extends pe.bbva.escaner.common.dto.PaginacionDTO
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AuditoriaDTO.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF6BAC28F19A60AACAA7EB4357BCCF1B0").resolveHandle("auditoriadto29a9type");
    
    /**
     * Gets the "idUsuaCrea" element
     */
    java.lang.String getIdUsuaCrea();
    
    /**
     * Gets (as xml) the "idUsuaCrea" element
     */
    org.apache.xmlbeans.XmlString xgetIdUsuaCrea();
    
    /**
     * Tests for nil "idUsuaCrea" element
     */
    boolean isNilIdUsuaCrea();
    
    /**
     * Sets the "idUsuaCrea" element
     */
    void setIdUsuaCrea(java.lang.String idUsuaCrea);
    
    /**
     * Sets (as xml) the "idUsuaCrea" element
     */
    void xsetIdUsuaCrea(org.apache.xmlbeans.XmlString idUsuaCrea);
    
    /**
     * Nils the "idUsuaCrea" element
     */
    void setNilIdUsuaCrea();
    
    /**
     * Gets the "feUsuaCrea" element
     */
    java.util.Calendar getFeUsuaCrea();
    
    /**
     * Gets (as xml) the "feUsuaCrea" element
     */
    org.apache.xmlbeans.XmlDateTime xgetFeUsuaCrea();
    
    /**
     * Tests for nil "feUsuaCrea" element
     */
    boolean isNilFeUsuaCrea();
    
    /**
     * Sets the "feUsuaCrea" element
     */
    void setFeUsuaCrea(java.util.Calendar feUsuaCrea);
    
    /**
     * Sets (as xml) the "feUsuaCrea" element
     */
    void xsetFeUsuaCrea(org.apache.xmlbeans.XmlDateTime feUsuaCrea);
    
    /**
     * Nils the "feUsuaCrea" element
     */
    void setNilFeUsuaCrea();
    
    /**
     * Gets the "txTermCrea" element
     */
    java.lang.String getTxTermCrea();
    
    /**
     * Gets (as xml) the "txTermCrea" element
     */
    org.apache.xmlbeans.XmlString xgetTxTermCrea();
    
    /**
     * Tests for nil "txTermCrea" element
     */
    boolean isNilTxTermCrea();
    
    /**
     * Sets the "txTermCrea" element
     */
    void setTxTermCrea(java.lang.String txTermCrea);
    
    /**
     * Sets (as xml) the "txTermCrea" element
     */
    void xsetTxTermCrea(org.apache.xmlbeans.XmlString txTermCrea);
    
    /**
     * Nils the "txTermCrea" element
     */
    void setNilTxTermCrea();
    
    /**
     * Gets the "idUsuaModi" element
     */
    java.lang.String getIdUsuaModi();
    
    /**
     * Gets (as xml) the "idUsuaModi" element
     */
    org.apache.xmlbeans.XmlString xgetIdUsuaModi();
    
    /**
     * Tests for nil "idUsuaModi" element
     */
    boolean isNilIdUsuaModi();
    
    /**
     * Sets the "idUsuaModi" element
     */
    void setIdUsuaModi(java.lang.String idUsuaModi);
    
    /**
     * Sets (as xml) the "idUsuaModi" element
     */
    void xsetIdUsuaModi(org.apache.xmlbeans.XmlString idUsuaModi);
    
    /**
     * Nils the "idUsuaModi" element
     */
    void setNilIdUsuaModi();
    
    /**
     * Gets the "feUsuaModi" element
     */
    java.util.Calendar getFeUsuaModi();
    
    /**
     * Gets (as xml) the "feUsuaModi" element
     */
    org.apache.xmlbeans.XmlDateTime xgetFeUsuaModi();
    
    /**
     * Tests for nil "feUsuaModi" element
     */
    boolean isNilFeUsuaModi();
    
    /**
     * Sets the "feUsuaModi" element
     */
    void setFeUsuaModi(java.util.Calendar feUsuaModi);
    
    /**
     * Sets (as xml) the "feUsuaModi" element
     */
    void xsetFeUsuaModi(org.apache.xmlbeans.XmlDateTime feUsuaModi);
    
    /**
     * Nils the "feUsuaModi" element
     */
    void setNilFeUsuaModi();
    
    /**
     * Gets the "txTermModi" element
     */
    java.lang.String getTxTermModi();
    
    /**
     * Gets (as xml) the "txTermModi" element
     */
    org.apache.xmlbeans.XmlString xgetTxTermModi();
    
    /**
     * Tests for nil "txTermModi" element
     */
    boolean isNilTxTermModi();
    
    /**
     * Sets the "txTermModi" element
     */
    void setTxTermModi(java.lang.String txTermModi);
    
    /**
     * Sets (as xml) the "txTermModi" element
     */
    void xsetTxTermModi(org.apache.xmlbeans.XmlString txTermModi);
    
    /**
     * Nils the "txTermModi" element
     */
    void setNilTxTermModi();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static pe.bbva.escaner.common.dto.AuditoriaDTO newInstance() {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static pe.bbva.escaner.common.dto.AuditoriaDTO parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (pe.bbva.escaner.common.dto.AuditoriaDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}

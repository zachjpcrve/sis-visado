/*
 * XML Type:  HistorialDocDTO
 * Namespace: http://dto.common.escaner.bbva.pe
 * Java type: pe.bbva.escaner.common.dto.HistorialDocDTO
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.common.dto;


/**
 * An XML HistorialDocDTO(@http://dto.common.escaner.bbva.pe).
 *
 * This is a complex type.
 */
public interface HistorialDocDTO extends pe.bbva.escaner.common.dto.AuditoriaDTO
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(HistorialDocDTO.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF6BAC28F19A60AACAA7EB4357BCCF1B0").resolveHandle("historialdocdtoe0e4type");
    
    /**
     * Gets the "idHistorial" element
     */
    long getIdHistorial();
    
    /**
     * Gets (as xml) the "idHistorial" element
     */
    org.apache.xmlbeans.XmlLong xgetIdHistorial();
    
    /**
     * Tests for nil "idHistorial" element
     */
    boolean isNilIdHistorial();
    
    /**
     * Sets the "idHistorial" element
     */
    void setIdHistorial(long idHistorial);
    
    /**
     * Sets (as xml) the "idHistorial" element
     */
    void xsetIdHistorial(org.apache.xmlbeans.XmlLong idHistorial);
    
    /**
     * Nils the "idHistorial" element
     */
    void setNilIdHistorial();
    
    /**
     * Gets the "nroSolicitud" element
     */
    java.lang.String getNroSolicitud();
    
    /**
     * Gets (as xml) the "nroSolicitud" element
     */
    org.apache.xmlbeans.XmlString xgetNroSolicitud();
    
    /**
     * Tests for nil "nroSolicitud" element
     */
    boolean isNilNroSolicitud();
    
    /**
     * Sets the "nroSolicitud" element
     */
    void setNroSolicitud(java.lang.String nroSolicitud);
    
    /**
     * Sets (as xml) the "nroSolicitud" element
     */
    void xsetNroSolicitud(org.apache.xmlbeans.XmlString nroSolicitud);
    
    /**
     * Nils the "nroSolicitud" element
     */
    void setNilNroSolicitud();
    
    /**
     * Gets the "nombreArchivo" element
     */
    java.lang.String getNombreArchivo();
    
    /**
     * Gets (as xml) the "nombreArchivo" element
     */
    org.apache.xmlbeans.XmlString xgetNombreArchivo();
    
    /**
     * Tests for nil "nombreArchivo" element
     */
    boolean isNilNombreArchivo();
    
    /**
     * Sets the "nombreArchivo" element
     */
    void setNombreArchivo(java.lang.String nombreArchivo);
    
    /**
     * Sets (as xml) the "nombreArchivo" element
     */
    void xsetNombreArchivo(org.apache.xmlbeans.XmlString nombreArchivo);
    
    /**
     * Nils the "nombreArchivo" element
     */
    void setNilNombreArchivo();
    
    /**
     * Gets the "fechaHora" element
     */
    java.lang.String getFechaHora();
    
    /**
     * Gets (as xml) the "fechaHora" element
     */
    org.apache.xmlbeans.XmlString xgetFechaHora();
    
    /**
     * Tests for nil "fechaHora" element
     */
    boolean isNilFechaHora();
    
    /**
     * Sets the "fechaHora" element
     */
    void setFechaHora(java.lang.String fechaHora);
    
    /**
     * Sets (as xml) the "fechaHora" element
     */
    void xsetFechaHora(org.apache.xmlbeans.XmlString fechaHora);
    
    /**
     * Nils the "fechaHora" element
     */
    void setNilFechaHora();
    
    /**
     * Gets the "inEsta" element
     */
    java.lang.String getInEsta();
    
    /**
     * Gets (as xml) the "inEsta" element
     */
    org.apache.xmlbeans.XmlString xgetInEsta();
    
    /**
     * Tests for nil "inEsta" element
     */
    boolean isNilInEsta();
    
    /**
     * Sets the "inEsta" element
     */
    void setInEsta(java.lang.String inEsta);
    
    /**
     * Sets (as xml) the "inEsta" element
     */
    void xsetInEsta(org.apache.xmlbeans.XmlString inEsta);
    
    /**
     * Nils the "inEsta" element
     */
    void setNilInEsta();
    
    /**
     * Gets the "estadoProceso" element
     */
    java.lang.String getEstadoProceso();
    
    /**
     * Gets (as xml) the "estadoProceso" element
     */
    org.apache.xmlbeans.XmlString xgetEstadoProceso();
    
    /**
     * Tests for nil "estadoProceso" element
     */
    boolean isNilEstadoProceso();
    
    /**
     * Sets the "estadoProceso" element
     */
    void setEstadoProceso(java.lang.String estadoProceso);
    
    /**
     * Sets (as xml) the "estadoProceso" element
     */
    void xsetEstadoProceso(org.apache.xmlbeans.XmlString estadoProceso);
    
    /**
     * Nils the "estadoProceso" element
     */
    void setNilEstadoProceso();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static pe.bbva.escaner.common.dto.HistorialDocDTO newInstance() {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static pe.bbva.escaner.common.dto.HistorialDocDTO parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (pe.bbva.escaner.common.dto.HistorialDocDTO) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}

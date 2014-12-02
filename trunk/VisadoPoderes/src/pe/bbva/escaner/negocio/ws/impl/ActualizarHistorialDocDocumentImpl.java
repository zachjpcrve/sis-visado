/*
 * An XML document type.
 * Localname: actualizarHistorialDoc
 * Namespace: http://ws.negocio.escaner.bbva.pe
 * Java type: pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.negocio.ws.impl;
/**
 * A document containing one actualizarHistorialDoc(@http://ws.negocio.escaner.bbva.pe) element.
 *
 * This is a complex type.
 */
public class ActualizarHistorialDocDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument
{
    
    public ActualizarHistorialDocDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ACTUALIZARHISTORIALDOC$0 = 
        new javax.xml.namespace.QName("http://ws.negocio.escaner.bbva.pe", "actualizarHistorialDoc");
    
    
    /**
     * Gets the "actualizarHistorialDoc" element
     */
    public pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc getActualizarHistorialDoc()
    {
        synchronized (monitor())
        {
            check_orphaned();
            pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc target = null;
            target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc)get_store().find_element_user(ACTUALIZARHISTORIALDOC$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "actualizarHistorialDoc" element
     */
    public void setActualizarHistorialDoc(pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc actualizarHistorialDoc)
    {
        synchronized (monitor())
        {
            check_orphaned();
            pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc target = null;
            target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc)get_store().find_element_user(ACTUALIZARHISTORIALDOC$0, 0);
            if (target == null)
            {
                target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc)get_store().add_element_user(ACTUALIZARHISTORIALDOC$0);
            }
            target.set(actualizarHistorialDoc);
        }
    }
    
    /**
     * Appends and returns a new empty "actualizarHistorialDoc" element
     */
    public pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc addNewActualizarHistorialDoc()
    {
        synchronized (monitor())
        {
            check_orphaned();
            pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc target = null;
            target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc)get_store().add_element_user(ACTUALIZARHISTORIALDOC$0);
            return target;
        }
    }
    /**
     * An XML actualizarHistorialDoc(@http://ws.negocio.escaner.bbva.pe).
     *
     * This is a complex type.
     */
    public static class ActualizarHistorialDocImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements pe.bbva.escaner.negocio.ws.ActualizarHistorialDocDocument.ActualizarHistorialDoc
    {
        
        public ActualizarHistorialDocImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName HISTORIAL$0 = 
            new javax.xml.namespace.QName("", "historial");
        
        
        /**
         * Gets the "historial" element
         */
        public pe.bbva.escaner.common.dto.HistorialDocDTO getHistorial()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(HISTORIAL$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "historial" element
         */
        public boolean isNilHistorial()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(HISTORIAL$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * Sets the "historial" element
         */
        public void setHistorial(pe.bbva.escaner.common.dto.HistorialDocDTO historial)
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(HISTORIAL$0, 0);
                if (target == null)
                {
                    target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().add_element_user(HISTORIAL$0);
                }
                target.set(historial);
            }
        }
        
        /**
         * Appends and returns a new empty "historial" element
         */
        public pe.bbva.escaner.common.dto.HistorialDocDTO addNewHistorial()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().add_element_user(HISTORIAL$0);
                return target;
            }
        }
        
        /**
         * Nils the "historial" element
         */
        public void setNilHistorial()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(HISTORIAL$0, 0);
                if (target == null)
                {
                    target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().add_element_user(HISTORIAL$0);
                }
                target.setNil();
            }
        }
    }
}

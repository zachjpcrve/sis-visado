/*
 * An XML document type.
 * Localname: actualizarHistorialDocResponse
 * Namespace: http://ws.negocio.escaner.bbva.pe
 * Java type: pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.negocio.ws.impl;
/**
 * A document containing one actualizarHistorialDocResponse(@http://ws.negocio.escaner.bbva.pe) element.
 *
 * This is a complex type.
 */
public class ActualizarHistorialDocResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument
{
    
    public ActualizarHistorialDocResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ACTUALIZARHISTORIALDOCRESPONSE$0 = 
        new javax.xml.namespace.QName("http://ws.negocio.escaner.bbva.pe", "actualizarHistorialDocResponse");
    
    
    /**
     * Gets the "actualizarHistorialDocResponse" element
     */
    public pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse getActualizarHistorialDocResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse target = null;
            target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse)get_store().find_element_user(ACTUALIZARHISTORIALDOCRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "actualizarHistorialDocResponse" element
     */
    public void setActualizarHistorialDocResponse(pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse actualizarHistorialDocResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse target = null;
            target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse)get_store().find_element_user(ACTUALIZARHISTORIALDOCRESPONSE$0, 0);
            if (target == null)
            {
                target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse)get_store().add_element_user(ACTUALIZARHISTORIALDOCRESPONSE$0);
            }
            target.set(actualizarHistorialDocResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "actualizarHistorialDocResponse" element
     */
    public pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse addNewActualizarHistorialDocResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse target = null;
            target = (pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse)get_store().add_element_user(ACTUALIZARHISTORIALDOCRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML actualizarHistorialDocResponse(@http://ws.negocio.escaner.bbva.pe).
     *
     * This is a complex type.
     */
    public static class ActualizarHistorialDocResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements pe.bbva.escaner.negocio.ws.ActualizarHistorialDocResponseDocument.ActualizarHistorialDocResponse
    {
        
        public ActualizarHistorialDocResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName ACTUALIZARHISTORIALDOCRETURN$0 = 
            new javax.xml.namespace.QName("", "actualizarHistorialDocReturn");
        
        
        /**
         * Gets the "actualizarHistorialDocReturn" element
         */
        public pe.bbva.escaner.common.dto.HistorialDocDTO getActualizarHistorialDocReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(ACTUALIZARHISTORIALDOCRETURN$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Tests for nil "actualizarHistorialDocReturn" element
         */
        public boolean isNilActualizarHistorialDocReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(ACTUALIZARHISTORIALDOCRETURN$0, 0);
                if (target == null) return false;
                return target.isNil();
            }
        }
        
        /**
         * Sets the "actualizarHistorialDocReturn" element
         */
        public void setActualizarHistorialDocReturn(pe.bbva.escaner.common.dto.HistorialDocDTO actualizarHistorialDocReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(ACTUALIZARHISTORIALDOCRETURN$0, 0);
                if (target == null)
                {
                    target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().add_element_user(ACTUALIZARHISTORIALDOCRETURN$0);
                }
                target.set(actualizarHistorialDocReturn);
            }
        }
        
        /**
         * Appends and returns a new empty "actualizarHistorialDocReturn" element
         */
        public pe.bbva.escaner.common.dto.HistorialDocDTO addNewActualizarHistorialDocReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().add_element_user(ACTUALIZARHISTORIALDOCRETURN$0);
                return target;
            }
        }
        
        /**
         * Nils the "actualizarHistorialDocReturn" element
         */
        public void setNilActualizarHistorialDocReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                pe.bbva.escaner.common.dto.HistorialDocDTO target = null;
                target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().find_element_user(ACTUALIZARHISTORIALDOCRETURN$0, 0);
                if (target == null)
                {
                    target = (pe.bbva.escaner.common.dto.HistorialDocDTO)get_store().add_element_user(ACTUALIZARHISTORIALDOCRETURN$0);
                }
                target.setNil();
            }
        }
    }
}

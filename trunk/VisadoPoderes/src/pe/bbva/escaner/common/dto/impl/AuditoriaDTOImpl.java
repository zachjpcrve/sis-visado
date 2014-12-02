/*
 * XML Type:  AuditoriaDTO
 * Namespace: http://dto.common.escaner.bbva.pe
 * Java type: pe.bbva.escaner.common.dto.AuditoriaDTO
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.common.dto.impl;
/**
 * An XML AuditoriaDTO(@http://dto.common.escaner.bbva.pe).
 *
 * This is a complex type.
 */
public class AuditoriaDTOImpl extends pe.bbva.escaner.common.dto.impl.PaginacionDTOImpl implements pe.bbva.escaner.common.dto.AuditoriaDTO
{
    
    public AuditoriaDTOImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName IDUSUACREA$0 = 
        new javax.xml.namespace.QName("", "idUsuaCrea");
    private static final javax.xml.namespace.QName FEUSUACREA$2 = 
        new javax.xml.namespace.QName("", "feUsuaCrea");
    private static final javax.xml.namespace.QName TXTERMCREA$4 = 
        new javax.xml.namespace.QName("", "txTermCrea");
    private static final javax.xml.namespace.QName IDUSUAMODI$6 = 
        new javax.xml.namespace.QName("", "idUsuaModi");
    private static final javax.xml.namespace.QName FEUSUAMODI$8 = 
        new javax.xml.namespace.QName("", "feUsuaModi");
    private static final javax.xml.namespace.QName TXTERMMODI$10 = 
        new javax.xml.namespace.QName("", "txTermModi");
    
    
    /**
     * Gets the "idUsuaCrea" element
     */
    public java.lang.String getIdUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDUSUACREA$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "idUsuaCrea" element
     */
    public org.apache.xmlbeans.XmlString xgetIdUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUACREA$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "idUsuaCrea" element
     */
    public boolean isNilIdUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUACREA$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "idUsuaCrea" element
     */
    public void setIdUsuaCrea(java.lang.String idUsuaCrea)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDUSUACREA$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(IDUSUACREA$0);
            }
            target.setStringValue(idUsuaCrea);
        }
    }
    
    /**
     * Sets (as xml) the "idUsuaCrea" element
     */
    public void xsetIdUsuaCrea(org.apache.xmlbeans.XmlString idUsuaCrea)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUACREA$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(IDUSUACREA$0);
            }
            target.set(idUsuaCrea);
        }
    }
    
    /**
     * Nils the "idUsuaCrea" element
     */
    public void setNilIdUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUACREA$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(IDUSUACREA$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "feUsuaCrea" element
     */
    public java.util.Calendar getFeUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FEUSUACREA$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "feUsuaCrea" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetFeUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUACREA$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "feUsuaCrea" element
     */
    public boolean isNilFeUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUACREA$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "feUsuaCrea" element
     */
    public void setFeUsuaCrea(java.util.Calendar feUsuaCrea)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FEUSUACREA$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FEUSUACREA$2);
            }
            target.setCalendarValue(feUsuaCrea);
        }
    }
    
    /**
     * Sets (as xml) the "feUsuaCrea" element
     */
    public void xsetFeUsuaCrea(org.apache.xmlbeans.XmlDateTime feUsuaCrea)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUACREA$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(FEUSUACREA$2);
            }
            target.set(feUsuaCrea);
        }
    }
    
    /**
     * Nils the "feUsuaCrea" element
     */
    public void setNilFeUsuaCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUACREA$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(FEUSUACREA$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "txTermCrea" element
     */
    public java.lang.String getTxTermCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TXTERMCREA$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "txTermCrea" element
     */
    public org.apache.xmlbeans.XmlString xgetTxTermCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMCREA$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "txTermCrea" element
     */
    public boolean isNilTxTermCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMCREA$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "txTermCrea" element
     */
    public void setTxTermCrea(java.lang.String txTermCrea)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TXTERMCREA$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TXTERMCREA$4);
            }
            target.setStringValue(txTermCrea);
        }
    }
    
    /**
     * Sets (as xml) the "txTermCrea" element
     */
    public void xsetTxTermCrea(org.apache.xmlbeans.XmlString txTermCrea)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMCREA$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TXTERMCREA$4);
            }
            target.set(txTermCrea);
        }
    }
    
    /**
     * Nils the "txTermCrea" element
     */
    public void setNilTxTermCrea()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMCREA$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TXTERMCREA$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "idUsuaModi" element
     */
    public java.lang.String getIdUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDUSUAMODI$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "idUsuaModi" element
     */
    public org.apache.xmlbeans.XmlString xgetIdUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUAMODI$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "idUsuaModi" element
     */
    public boolean isNilIdUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUAMODI$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "idUsuaModi" element
     */
    public void setIdUsuaModi(java.lang.String idUsuaModi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDUSUAMODI$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(IDUSUAMODI$6);
            }
            target.setStringValue(idUsuaModi);
        }
    }
    
    /**
     * Sets (as xml) the "idUsuaModi" element
     */
    public void xsetIdUsuaModi(org.apache.xmlbeans.XmlString idUsuaModi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUAMODI$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(IDUSUAMODI$6);
            }
            target.set(idUsuaModi);
        }
    }
    
    /**
     * Nils the "idUsuaModi" element
     */
    public void setNilIdUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(IDUSUAMODI$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(IDUSUAMODI$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "feUsuaModi" element
     */
    public java.util.Calendar getFeUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FEUSUAMODI$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "feUsuaModi" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetFeUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUAMODI$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "feUsuaModi" element
     */
    public boolean isNilFeUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUAMODI$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "feUsuaModi" element
     */
    public void setFeUsuaModi(java.util.Calendar feUsuaModi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FEUSUAMODI$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FEUSUAMODI$8);
            }
            target.setCalendarValue(feUsuaModi);
        }
    }
    
    /**
     * Sets (as xml) the "feUsuaModi" element
     */
    public void xsetFeUsuaModi(org.apache.xmlbeans.XmlDateTime feUsuaModi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUAMODI$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(FEUSUAMODI$8);
            }
            target.set(feUsuaModi);
        }
    }
    
    /**
     * Nils the "feUsuaModi" element
     */
    public void setNilFeUsuaModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(FEUSUAMODI$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(FEUSUAMODI$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "txTermModi" element
     */
    public java.lang.String getTxTermModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TXTERMMODI$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "txTermModi" element
     */
    public org.apache.xmlbeans.XmlString xgetTxTermModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMMODI$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "txTermModi" element
     */
    public boolean isNilTxTermModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMMODI$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "txTermModi" element
     */
    public void setTxTermModi(java.lang.String txTermModi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TXTERMMODI$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TXTERMMODI$10);
            }
            target.setStringValue(txTermModi);
        }
    }
    
    /**
     * Sets (as xml) the "txTermModi" element
     */
    public void xsetTxTermModi(org.apache.xmlbeans.XmlString txTermModi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMMODI$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TXTERMMODI$10);
            }
            target.set(txTermModi);
        }
    }
    
    /**
     * Nils the "txTermModi" element
     */
    public void setNilTxTermModi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TXTERMMODI$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TXTERMMODI$10);
            }
            target.setNil();
        }
    }
}

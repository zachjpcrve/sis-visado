/*
 * XML Type:  PaginacionDTO
 * Namespace: http://dto.common.escaner.bbva.pe
 * Java type: pe.bbva.escaner.common.dto.PaginacionDTO
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.common.dto.impl;
/**
 * An XML PaginacionDTO(@http://dto.common.escaner.bbva.pe).
 *
 * This is a complex type.
 */
public class PaginacionDTOImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements pe.bbva.escaner.common.dto.PaginacionDTO
{
    
    public PaginacionDTOImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INDEX$0 = 
        new javax.xml.namespace.QName("", "index");
    private static final javax.xml.namespace.QName NUPRIMPAGI$2 = 
        new javax.xml.namespace.QName("", "nuPrimPagi");
    private static final javax.xml.namespace.QName NUULTIPAGI$4 = 
        new javax.xml.namespace.QName("", "nuUltiPagi");
    private static final javax.xml.namespace.QName NUACTUPAGI$6 = 
        new javax.xml.namespace.QName("", "nuActuPagi");
    private static final javax.xml.namespace.QName NUTOTALPAG$8 = 
        new javax.xml.namespace.QName("", "nuTotalPag");
    private static final javax.xml.namespace.QName NUACTUPAGICAD$10 = 
        new javax.xml.namespace.QName("", "nuActuPagiCad");
    private static final javax.xml.namespace.QName NUTOTALPAGCAD$12 = 
        new javax.xml.namespace.QName("", "nuTotalPagCad");
    private static final javax.xml.namespace.QName DISABLEATRAS$14 = 
        new javax.xml.namespace.QName("", "disableAtras");
    private static final javax.xml.namespace.QName DISABLESIGUIENTE$16 = 
        new javax.xml.namespace.QName("", "disableSiguiente");
    private static final javax.xml.namespace.QName VISIBLEPAGINACION$18 = 
        new javax.xml.namespace.QName("", "visiblePaginacion");
    
    
    /**
     * Gets the "index" element
     */
    public int getIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDEX$0, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "index" element
     */
    public org.apache.xmlbeans.XmlInt xgetIndex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(INDEX$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "index" element
     */
    public void setIndex(int index)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INDEX$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INDEX$0);
            }
            target.setIntValue(index);
        }
    }
    
    /**
     * Sets (as xml) the "index" element
     */
    public void xsetIndex(org.apache.xmlbeans.XmlInt index)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(INDEX$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(INDEX$0);
            }
            target.set(index);
        }
    }
    
    /**
     * Gets the "nuPrimPagi" element
     */
    public int getNuPrimPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUPRIMPAGI$2, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "nuPrimPagi" element
     */
    public org.apache.xmlbeans.XmlInt xgetNuPrimPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUPRIMPAGI$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nuPrimPagi" element
     */
    public boolean isNilNuPrimPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUPRIMPAGI$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nuPrimPagi" element
     */
    public void setNuPrimPagi(int nuPrimPagi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUPRIMPAGI$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUPRIMPAGI$2);
            }
            target.setIntValue(nuPrimPagi);
        }
    }
    
    /**
     * Sets (as xml) the "nuPrimPagi" element
     */
    public void xsetNuPrimPagi(org.apache.xmlbeans.XmlInt nuPrimPagi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUPRIMPAGI$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUPRIMPAGI$2);
            }
            target.set(nuPrimPagi);
        }
    }
    
    /**
     * Nils the "nuPrimPagi" element
     */
    public void setNilNuPrimPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUPRIMPAGI$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUPRIMPAGI$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "nuUltiPagi" element
     */
    public int getNuUltiPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUULTIPAGI$4, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "nuUltiPagi" element
     */
    public org.apache.xmlbeans.XmlInt xgetNuUltiPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUULTIPAGI$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nuUltiPagi" element
     */
    public boolean isNilNuUltiPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUULTIPAGI$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nuUltiPagi" element
     */
    public void setNuUltiPagi(int nuUltiPagi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUULTIPAGI$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUULTIPAGI$4);
            }
            target.setIntValue(nuUltiPagi);
        }
    }
    
    /**
     * Sets (as xml) the "nuUltiPagi" element
     */
    public void xsetNuUltiPagi(org.apache.xmlbeans.XmlInt nuUltiPagi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUULTIPAGI$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUULTIPAGI$4);
            }
            target.set(nuUltiPagi);
        }
    }
    
    /**
     * Nils the "nuUltiPagi" element
     */
    public void setNilNuUltiPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUULTIPAGI$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUULTIPAGI$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "nuActuPagi" element
     */
    public int getNuActuPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUACTUPAGI$6, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "nuActuPagi" element
     */
    public org.apache.xmlbeans.XmlInt xgetNuActuPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUACTUPAGI$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nuActuPagi" element
     */
    public boolean isNilNuActuPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUACTUPAGI$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nuActuPagi" element
     */
    public void setNuActuPagi(int nuActuPagi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUACTUPAGI$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUACTUPAGI$6);
            }
            target.setIntValue(nuActuPagi);
        }
    }
    
    /**
     * Sets (as xml) the "nuActuPagi" element
     */
    public void xsetNuActuPagi(org.apache.xmlbeans.XmlInt nuActuPagi)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUACTUPAGI$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUACTUPAGI$6);
            }
            target.set(nuActuPagi);
        }
    }
    
    /**
     * Nils the "nuActuPagi" element
     */
    public void setNilNuActuPagi()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUACTUPAGI$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUACTUPAGI$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "nuTotalPag" element
     */
    public int getNuTotalPag()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUTOTALPAG$8, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "nuTotalPag" element
     */
    public org.apache.xmlbeans.XmlInt xgetNuTotalPag()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUTOTALPAG$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nuTotalPag" element
     */
    public boolean isNilNuTotalPag()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUTOTALPAG$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nuTotalPag" element
     */
    public void setNuTotalPag(int nuTotalPag)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUTOTALPAG$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUTOTALPAG$8);
            }
            target.setIntValue(nuTotalPag);
        }
    }
    
    /**
     * Sets (as xml) the "nuTotalPag" element
     */
    public void xsetNuTotalPag(org.apache.xmlbeans.XmlInt nuTotalPag)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUTOTALPAG$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUTOTALPAG$8);
            }
            target.set(nuTotalPag);
        }
    }
    
    /**
     * Nils the "nuTotalPag" element
     */
    public void setNilNuTotalPag()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(NUTOTALPAG$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(NUTOTALPAG$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "nuActuPagiCad" element
     */
    public java.lang.String getNuActuPagiCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUACTUPAGICAD$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "nuActuPagiCad" element
     */
    public org.apache.xmlbeans.XmlString xgetNuActuPagiCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUACTUPAGICAD$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nuActuPagiCad" element
     */
    public boolean isNilNuActuPagiCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUACTUPAGICAD$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nuActuPagiCad" element
     */
    public void setNuActuPagiCad(java.lang.String nuActuPagiCad)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUACTUPAGICAD$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUACTUPAGICAD$10);
            }
            target.setStringValue(nuActuPagiCad);
        }
    }
    
    /**
     * Sets (as xml) the "nuActuPagiCad" element
     */
    public void xsetNuActuPagiCad(org.apache.xmlbeans.XmlString nuActuPagiCad)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUACTUPAGICAD$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUACTUPAGICAD$10);
            }
            target.set(nuActuPagiCad);
        }
    }
    
    /**
     * Nils the "nuActuPagiCad" element
     */
    public void setNilNuActuPagiCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUACTUPAGICAD$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUACTUPAGICAD$10);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "nuTotalPagCad" element
     */
    public java.lang.String getNuTotalPagCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUTOTALPAGCAD$12, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "nuTotalPagCad" element
     */
    public org.apache.xmlbeans.XmlString xgetNuTotalPagCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUTOTALPAGCAD$12, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nuTotalPagCad" element
     */
    public boolean isNilNuTotalPagCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUTOTALPAGCAD$12, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nuTotalPagCad" element
     */
    public void setNuTotalPagCad(java.lang.String nuTotalPagCad)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NUTOTALPAGCAD$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NUTOTALPAGCAD$12);
            }
            target.setStringValue(nuTotalPagCad);
        }
    }
    
    /**
     * Sets (as xml) the "nuTotalPagCad" element
     */
    public void xsetNuTotalPagCad(org.apache.xmlbeans.XmlString nuTotalPagCad)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUTOTALPAGCAD$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUTOTALPAGCAD$12);
            }
            target.set(nuTotalPagCad);
        }
    }
    
    /**
     * Nils the "nuTotalPagCad" element
     */
    public void setNilNuTotalPagCad()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NUTOTALPAGCAD$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NUTOTALPAGCAD$12);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "disableAtras" element
     */
    public boolean getDisableAtras()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DISABLEATRAS$14, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "disableAtras" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetDisableAtras()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(DISABLEATRAS$14, 0);
            return target;
        }
    }
    
    /**
     * Sets the "disableAtras" element
     */
    public void setDisableAtras(boolean disableAtras)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DISABLEATRAS$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DISABLEATRAS$14);
            }
            target.setBooleanValue(disableAtras);
        }
    }
    
    /**
     * Sets (as xml) the "disableAtras" element
     */
    public void xsetDisableAtras(org.apache.xmlbeans.XmlBoolean disableAtras)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(DISABLEATRAS$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(DISABLEATRAS$14);
            }
            target.set(disableAtras);
        }
    }
    
    /**
     * Gets the "disableSiguiente" element
     */
    public boolean getDisableSiguiente()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DISABLESIGUIENTE$16, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "disableSiguiente" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetDisableSiguiente()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(DISABLESIGUIENTE$16, 0);
            return target;
        }
    }
    
    /**
     * Sets the "disableSiguiente" element
     */
    public void setDisableSiguiente(boolean disableSiguiente)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DISABLESIGUIENTE$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DISABLESIGUIENTE$16);
            }
            target.setBooleanValue(disableSiguiente);
        }
    }
    
    /**
     * Sets (as xml) the "disableSiguiente" element
     */
    public void xsetDisableSiguiente(org.apache.xmlbeans.XmlBoolean disableSiguiente)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(DISABLESIGUIENTE$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(DISABLESIGUIENTE$16);
            }
            target.set(disableSiguiente);
        }
    }
    
    /**
     * Gets the "visiblePaginacion" element
     */
    public boolean getVisiblePaginacion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VISIBLEPAGINACION$18, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "visiblePaginacion" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetVisiblePaginacion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(VISIBLEPAGINACION$18, 0);
            return target;
        }
    }
    
    /**
     * Sets the "visiblePaginacion" element
     */
    public void setVisiblePaginacion(boolean visiblePaginacion)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VISIBLEPAGINACION$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VISIBLEPAGINACION$18);
            }
            target.setBooleanValue(visiblePaginacion);
        }
    }
    
    /**
     * Sets (as xml) the "visiblePaginacion" element
     */
    public void xsetVisiblePaginacion(org.apache.xmlbeans.XmlBoolean visiblePaginacion)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(VISIBLEPAGINACION$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(VISIBLEPAGINACION$18);
            }
            target.set(visiblePaginacion);
        }
    }
}

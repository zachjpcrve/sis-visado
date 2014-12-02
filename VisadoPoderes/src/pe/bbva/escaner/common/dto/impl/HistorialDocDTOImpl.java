/*
 * XML Type:  HistorialDocDTO
 * Namespace: http://dto.common.escaner.bbva.pe
 * Java type: pe.bbva.escaner.common.dto.HistorialDocDTO
 *
 * Automatically generated - do not modify.
 */
package pe.bbva.escaner.common.dto.impl;
/**
 * An XML HistorialDocDTO(@http://dto.common.escaner.bbva.pe).
 *
 * This is a complex type.
 */
public class HistorialDocDTOImpl extends pe.bbva.escaner.common.dto.impl.AuditoriaDTOImpl implements pe.bbva.escaner.common.dto.HistorialDocDTO
{
    
    public HistorialDocDTOImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName IDHISTORIAL$0 = 
        new javax.xml.namespace.QName("", "idHistorial");
    private static final javax.xml.namespace.QName NROSOLICITUD$2 = 
        new javax.xml.namespace.QName("", "nroSolicitud");
    private static final javax.xml.namespace.QName NOMBREARCHIVO$4 = 
        new javax.xml.namespace.QName("", "nombreArchivo");
    private static final javax.xml.namespace.QName FECHAHORA$6 = 
        new javax.xml.namespace.QName("", "fechaHora");
    private static final javax.xml.namespace.QName INESTA$8 = 
        new javax.xml.namespace.QName("", "inEsta");
    private static final javax.xml.namespace.QName ESTADOPROCESO$10 = 
        new javax.xml.namespace.QName("", "estadoProceso");
    
    
    /**
     * Gets the "idHistorial" element
     */
    public long getIdHistorial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDHISTORIAL$0, 0);
            if (target == null)
            {
                return 0L;
            }
            return target.getLongValue();
        }
    }
    
    /**
     * Gets (as xml) the "idHistorial" element
     */
    public org.apache.xmlbeans.XmlLong xgetIdHistorial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlLong target = null;
            target = (org.apache.xmlbeans.XmlLong)get_store().find_element_user(IDHISTORIAL$0, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "idHistorial" element
     */
    public boolean isNilIdHistorial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlLong target = null;
            target = (org.apache.xmlbeans.XmlLong)get_store().find_element_user(IDHISTORIAL$0, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "idHistorial" element
     */
    public void setIdHistorial(long idHistorial)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDHISTORIAL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(IDHISTORIAL$0);
            }
            target.setLongValue(idHistorial);
        }
    }
    
    /**
     * Sets (as xml) the "idHistorial" element
     */
    public void xsetIdHistorial(org.apache.xmlbeans.XmlLong idHistorial)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlLong target = null;
            target = (org.apache.xmlbeans.XmlLong)get_store().find_element_user(IDHISTORIAL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlLong)get_store().add_element_user(IDHISTORIAL$0);
            }
            target.set(idHistorial);
        }
    }
    
    /**
     * Nils the "idHistorial" element
     */
    public void setNilIdHistorial()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlLong target = null;
            target = (org.apache.xmlbeans.XmlLong)get_store().find_element_user(IDHISTORIAL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlLong)get_store().add_element_user(IDHISTORIAL$0);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "nroSolicitud" element
     */
    public java.lang.String getNroSolicitud()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NROSOLICITUD$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "nroSolicitud" element
     */
    public org.apache.xmlbeans.XmlString xgetNroSolicitud()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NROSOLICITUD$2, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nroSolicitud" element
     */
    public boolean isNilNroSolicitud()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NROSOLICITUD$2, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nroSolicitud" element
     */
    public void setNroSolicitud(java.lang.String nroSolicitud)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NROSOLICITUD$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NROSOLICITUD$2);
            }
            target.setStringValue(nroSolicitud);
        }
    }
    
    /**
     * Sets (as xml) the "nroSolicitud" element
     */
    public void xsetNroSolicitud(org.apache.xmlbeans.XmlString nroSolicitud)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NROSOLICITUD$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NROSOLICITUD$2);
            }
            target.set(nroSolicitud);
        }
    }
    
    /**
     * Nils the "nroSolicitud" element
     */
    public void setNilNroSolicitud()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NROSOLICITUD$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NROSOLICITUD$2);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "nombreArchivo" element
     */
    public java.lang.String getNombreArchivo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NOMBREARCHIVO$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "nombreArchivo" element
     */
    public org.apache.xmlbeans.XmlString xgetNombreArchivo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMBREARCHIVO$4, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "nombreArchivo" element
     */
    public boolean isNilNombreArchivo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMBREARCHIVO$4, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "nombreArchivo" element
     */
    public void setNombreArchivo(java.lang.String nombreArchivo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NOMBREARCHIVO$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NOMBREARCHIVO$4);
            }
            target.setStringValue(nombreArchivo);
        }
    }
    
    /**
     * Sets (as xml) the "nombreArchivo" element
     */
    public void xsetNombreArchivo(org.apache.xmlbeans.XmlString nombreArchivo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMBREARCHIVO$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NOMBREARCHIVO$4);
            }
            target.set(nombreArchivo);
        }
    }
    
    /**
     * Nils the "nombreArchivo" element
     */
    public void setNilNombreArchivo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NOMBREARCHIVO$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NOMBREARCHIVO$4);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "fechaHora" element
     */
    public java.lang.String getFechaHora()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FECHAHORA$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "fechaHora" element
     */
    public org.apache.xmlbeans.XmlString xgetFechaHora()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FECHAHORA$6, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "fechaHora" element
     */
    public boolean isNilFechaHora()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FECHAHORA$6, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "fechaHora" element
     */
    public void setFechaHora(java.lang.String fechaHora)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FECHAHORA$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FECHAHORA$6);
            }
            target.setStringValue(fechaHora);
        }
    }
    
    /**
     * Sets (as xml) the "fechaHora" element
     */
    public void xsetFechaHora(org.apache.xmlbeans.XmlString fechaHora)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FECHAHORA$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FECHAHORA$6);
            }
            target.set(fechaHora);
        }
    }
    
    /**
     * Nils the "fechaHora" element
     */
    public void setNilFechaHora()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FECHAHORA$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FECHAHORA$6);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "inEsta" element
     */
    public java.lang.String getInEsta()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INESTA$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "inEsta" element
     */
    public org.apache.xmlbeans.XmlString xgetInEsta()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INESTA$8, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "inEsta" element
     */
    public boolean isNilInEsta()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INESTA$8, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "inEsta" element
     */
    public void setInEsta(java.lang.String inEsta)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INESTA$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INESTA$8);
            }
            target.setStringValue(inEsta);
        }
    }
    
    /**
     * Sets (as xml) the "inEsta" element
     */
    public void xsetInEsta(org.apache.xmlbeans.XmlString inEsta)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INESTA$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INESTA$8);
            }
            target.set(inEsta);
        }
    }
    
    /**
     * Nils the "inEsta" element
     */
    public void setNilInEsta()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INESTA$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INESTA$8);
            }
            target.setNil();
        }
    }
    
    /**
     * Gets the "estadoProceso" element
     */
    public java.lang.String getEstadoProceso()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ESTADOPROCESO$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "estadoProceso" element
     */
    public org.apache.xmlbeans.XmlString xgetEstadoProceso()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ESTADOPROCESO$10, 0);
            return target;
        }
    }
    
    /**
     * Tests for nil "estadoProceso" element
     */
    public boolean isNilEstadoProceso()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ESTADOPROCESO$10, 0);
            if (target == null) return false;
            return target.isNil();
        }
    }
    
    /**
     * Sets the "estadoProceso" element
     */
    public void setEstadoProceso(java.lang.String estadoProceso)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ESTADOPROCESO$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ESTADOPROCESO$10);
            }
            target.setStringValue(estadoProceso);
        }
    }
    
    /**
     * Sets (as xml) the "estadoProceso" element
     */
    public void xsetEstadoProceso(org.apache.xmlbeans.XmlString estadoProceso)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ESTADOPROCESO$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ESTADOPROCESO$10);
            }
            target.set(estadoProceso);
        }
    }
    
    /**
     * Nils the "estadoProceso" element
     */
    public void setNilEstadoProceso()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ESTADOPROCESO$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ESTADOPROCESO$10);
            }
            target.setNil();
        }
    }
}

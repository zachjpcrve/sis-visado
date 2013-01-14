// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 21/12/2011 07:54:17 p.m.
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ConexionOld.java

package com.grupobbva.bc.per.tele.ldap.conexion;

import com.grupobbva.bc.per.tele.ldap.serializable.*;
import java.sql.*;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class __Conexion
{

    public __Conexion()
    {
    }

    private Vector resultadosConsulta(String sql, int limite)
    {
        Vector res = null;
        IILDPeUsuario ldapperu = null;
        Connection con = null;
        ResultSet rst = null;
        int i = 0;
        try
        {
            Context initialContext = new InitialContext();
            DataSource datasource = (DataSource)initialContext.lookup("jdbc/ORA_Biiwx001");
            con = datasource.getConnection();
            Statement s = con.createStatement();
            try
            {
                for(rst = s.executeQuery(sql); rst.next();)
                {
                    ldapperu = new IILDPeUsuario();
                    if(res == null)
                        res = new Vector(0, 1);
                    ldapperu = new IILDPeUsuario();
                    ldapperu.setRegistroU(rst.getString("REGISTROU"));
                    ldapperu.setRegistroP(rst.getString("REGISTROP"));
                    ldapperu.setPais(new IILDPePais(rst.getString("CODPAIS"), rst.getString("DESPAIS")));
                    ldapperu.setEmpresa(new IILDPeEmpresa(rst.getString("CODEMP"), rst.getString("DESEMP")));
                    ldapperu.setMatricula(rst.getString("NUMMAT"));
                    ldapperu.setUID(rst.getString("CODUSU"));
                    ldapperu.setNombre(rst.getString("NOMBRE"));
                    ldapperu.setApellido1(rst.getString("APEPAT"));
                    ldapperu.setApellido2(rst.getString("APEMAT"));
                    ldapperu.setSexo(rst.getString("SEXO"));
                    ldapperu.setFechaNacimiento(rst.getString("FECNAC"));
                    ldapperu.setTipDoc(rst.getString("TIPDOC"));
                    ldapperu.setNumDoc(rst.getString("NUMDOC"));
                    ldapperu.setTelefono(rst.getString("TELEFONO1"));
                    ldapperu.setMovil(rst.getString("TELEFONO2"));
                    ldapperu.setEmail(rst.getString("CORELEC"));
                    ldapperu.setCargo(new IILDPeCargo(rst.getString("CODCARGO"), rst.getString("NOMCARGO")));
                    ldapperu.setCodEmpAnt(rst.getString("CODEMPANT"));
                    ldapperu.setBancoOficina(new IILDPeOficina(rst.getString("CODOFI"), rst.getString("DESOFI"), rst.getString("TIPOFI"), rst.getString("TELOFI1"), rst.getString("TELOFI2")));
                    ldapperu.setGesPer(rst.getString("GESPER"));
                    ldapperu.setFecInig(rst.getString("FECING"));
                    ldapperu.setNHijosEscolar(rst.getInt("NROHIJOS"));
                    ldapperu.setCodGer(rst.getString("CODGER"));
                    ldapperu.setNomGer(rst.getString("NOMGER"));
                    ldapperu.setCarBlan(rst.getString("CARBLAN"));
                    ldapperu.setProInf(rst.getString("PROINF"));
                    ldapperu.setUIDJefe(rst.getString("CODUSUJEFE"));
                    ldapperu.setUnidadOrganizativa(new IILDPeUnidadOrganizativa(rst.getString("CODUNIORG"), rst.getString("DESUNIORG")));
                    ldapperu.setNivel1(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL1"), rst.getString("DESNIVEL1")));
                    ldapperu.setNivel2(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL2"), rst.getString("DESNIVEL2")));
                    ldapperu.setNivel3(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL3"), rst.getString("DESNIVEL3")));
                    ldapperu.setNivel4(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL4"), rst.getString("DESNIVEL4")));
                    ldapperu.setNivel5(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL5"), rst.getString("DESNIVEL5")));
                    ldapperu.setNivel6(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL6"), rst.getString("DESNIVEL6")));
                    ldapperu.setNivel7(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL7"), rst.getString("DESNIVEL7")));
                    ldapperu.setNivel8(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL8"), rst.getString("DESNIVEL8")));
                    ldapperu.setNivel9(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL9"), rst.getString("DESNIVEL9")));
                    ldapperu.setNivel10(new IILDPeUnidadOrganizativa(rst.getString("CODNIVEL10"), rst.getString("DESNIVEL10")));
                    ldapperu.setUsoFut1(rst.getString("USOFUT1"));
                    ldapperu.setUsoFut2(rst.getString("USOFUT2"));
                    ldapperu.setUsoFut3(rst.getString("USOFUT3"));
                    ldapperu.setUsoFut4(rst.getString("USOFUT4"));
                    ldapperu.setUsoFut5(rst.getString("USOFUT5"));
                    ldapperu.setUsoFut6(rst.getString("USOFUT6"));
                    ldapperu.setUsoFut7(rst.getString("USOFUT7"));
                    ldapperu.setUsoFut8(rst.getString("USOFUT8"));
                    ldapperu.setUsoFut9(rst.getString("USOFUT9"));
                    ldapperu.setUsoFut10(rst.getString("USOFUT10"));
                    res.add(ldapperu);
                    if(limite > 0 && ++i >= limite)
                        break;
                }

            }
            catch(SQLException e1)
            {
                e1.printStackTrace();
            }
            s.close();
            con.close();
            initialContext.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public IILDPePais recuperarPais(String codigo)
    {
        IILDPePais res = null;
        String sql = "select * from ldapperu2 where codpais = '" + codigo + "' ORDER BY o.codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        if(consulta != null)
            res = ((IILDPeUsuario)consulta.elementAt(0)).getPais();
        return res;
    }

    public IILDPeUnidadOrganizativa recuperarUnidadOrganizativa(String codigo)
    {
        IILDPeUnidadOrganizativa res = null;
        String sql = "select * from Ldapperu2 where  coduniorg = '" + codigo + "' ORDER BY codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        if(consulta != null)
            res = ((IILDPeUsuario)consulta.elementAt(0)).getUnidadOrganizativa();
        return res;
    }

    public IILDPeUsuario recuperarUsuarioMatricula(String matricula)
    {
        IILDPeUsuario res = null;
        String sql = "select * from Ldapperu2 where nummat = '" + matricula + "' ORDER BY codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        if(consulta != null)
            res = (IILDPeUsuario)consulta.elementAt(0);
        return res;
    }

    public String getOfActual(String codusu)
    {
        String res = null;
        String sql = "select * from Ldapperu2 where codusu = '" + codusu + "' ORDER BY codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        if(consulta != null)
            res = ((IILDPeUsuario)consulta.elementAt(0)).getBancoOficina().getCodigo();
        return res;
    }

    public String getNomCompleto(String codusu)
    {
        String res = null;
        IILDPeUsuario query = null;
        String sql = "select * from Ldapperu2 where codusu = '" + codusu + "' ORDER BY codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        if(consulta != null)
        {
            query = (IILDPeUsuario)consulta.elementAt(0);
            String nombre = "";
            String apepat = "";
            String apemat = "";
            if(query.getNombre() != null)
                nombre = query.getNombre().toLowerCase();
            if(query.getApellido1() != null)
                apepat = query.getApellido1().toLowerCase();
            if(query.getApellido2() != null)
                apemat = query.getApellido2().toLowerCase();
            else
                apemat = ".";
            char completo[] = (nombre + " " + apepat + " " + apemat).toCharArray();
            completo[0] = Character.toUpperCase(completo[0]);
            for(int i = 1; i < completo.length; i++)
                if(completo[i] == ' ' || completo[i] == '-')
                    completo[i + 1] = Character.toUpperCase(completo[i + 1]);

            res = new String(completo);
        }
        return res;
    }

    public IILDPeUsuario recuperarUsuario(String identificador)
    {
        IILDPeUsuario res = null;
        String sql = "select * from Ldapperu2 where codusu = '" + identificador + "' ORDER BY codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        if(consulta != null)
            res = (IILDPeUsuario)consulta.elementAt(0);
        return res;
    }

    public boolean existe(String uid)
    {
        boolean res = false;
        String sql = "select * from Ldapperu2 where codusu = '" + uid + "' ORDER BY codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        res = consulta != null;
        return res;
    }

    public Vector buscarNombreApellidos(String nombre, String apellido1, String apellido2)
    {
        Vector res = null;
        if(nombre == null)
            nombre = "";
        if(apellido1 == null)
            apellido1 = "";
        if(apellido2 == null)
            apellido2 = "";
        String sql = "select * from Ldapperu2 where nombre like '%" + nombre + "%' and " + "apepat like '%" + apellido1 + "%' and " + "apemat like '%" + apellido2 + "%' " + "ORDER BY codusu ASC";
        res = resultadosConsulta(sql, 0);
        return res;
    }

    public Vector buscarSubordinados(String uidJefe)
    {
        Vector res = null;
        String sql = "select * from Ldapperu2 where codusujefe = '" + uidJefe + "' ORDER BY codusu ASC";
        res = resultadosConsulta(sql, 0);
        return res;
    }

    public Vector buscarUsuarios(String nombre, String registroP, String registroU, String uid, String jefe, String apellido1, String apellido2, 
            String telefono, String movil, String mail, String codigoUnidadOrg, String matricula, String descUnidadOrg, String codigoOficina, 
            String descOficina, String codigoEmpresa, long limiteOcurrencias)
    {
        if(nombre == null)
            nombre = "";
        if(registroP == null)
            registroP = "";
        if(registroU == null)
            registroU = "";
        if(uid == null)
            uid = "";
        if(jefe == null)
            jefe = "";
        if(apellido1 == null)
            apellido1 = "";
        if(apellido2 == null)
            apellido2 = "";
        if(telefono == null)
            telefono = "";
        if(movil == null)
            movil = "";
        if(mail == null)
            mail = "";
        if(codigoUnidadOrg == null)
            codigoUnidadOrg = "";
        if(matricula == null)
            matricula = "";
        if(descUnidadOrg == null)
            descUnidadOrg = "";
        if(codigoOficina == null)
            codigoOficina = "";
        if(descOficina == null)
            descOficina = "";
        if(codigoEmpresa == null)
            codigoEmpresa = "";
        Vector res = null;
        String sql = "select * from Ldapperu2 where registrop like '%" + registroP + "%' and " + "registrou like '%" + registroU + "%' and " + "nombre like '%" + nombre + "%' and " + "codusu like '%" + uid + "%' and " + "codusujefe like '%" + jefe + "%' and " + "apepat like '%" + apellido1 + "%' and " + "apemat like '%" + apellido2 + "%' and " + "telefono1 like '%" + telefono + "%' and " + "telefono2 like '%" + movil + "%' and " + "corelec like '%" + mail + "%' and " + "coduniorg like '%" + codigoUnidadOrg + "%' and " + "nummat like '%" + matricula + "%' and " + "desuniorg like '%" + descUnidadOrg + "%' and " + "codofi like '%" + codigoOficina + "%' and " + "desofi like '%" + descOficina + "%' and " + "codemp like '%" + codigoEmpresa + "%' " + "ORDER BY codusu ASC";
        res = resultadosConsulta(sql, 0);
        return res;
    }

    public IILDPeEmpresa recuperarEmpresa(String codigoEmpresa)
    {
        IILDPeEmpresa res = null;
        String sql = "select * from ldapperu2 where codemp = '" + codigoEmpresa + "' ORDER BY o.codusu DESC";
        Vector consulta = resultadosConsulta(sql, 1);
        if(consulta != null)
            res = ((IILDPeUsuario)consulta.elementAt(0)).getEmpresa();
        return res;
    }

    public Vector buscarCodOfixCargos(String codofi, Vector cargos)
    {
        Vector res = null;
        StringBuffer listaCargos = new StringBuffer();
        for(int i = 0; i < cargos.size(); i++)
        {
            listaCargos.append("'" + ((ArgVal)cargos.get(i)).getValor() + "'");
            if(i + 1 < cargos.size())
                listaCargos.append(",");
        }

        String sql = "select * from Ldapperu2 where codofi = '" + codofi + "' and " + "codcargo in (" + listaCargos + ") ORDER BY codofi, codusu ASC";
        res = resultadosConsulta(sql, 0);
        return res;
    }

}
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="javax.faces.bean.SessionScoped"%>
<%@page import="javax.faces.context.ExternalContext"%>
<%@page import="javax.faces.context.FacesContext"%>
<%@page import="javax.servlet.http.HttpServletRequest"%>
<%@page import="javax.servlet.http.HttpServletResponse"%>
<%@page import="javax.servlet.http.HttpSession"%>
<%

FacesContext context = FacesContext.getCurrentInstance();
HttpSession sesion = ((HttpServletRequest)request).getSession();
sesion.invalidate();

%>
<html>
<head>
<title>Sistema de Visado de Poderes</title>
<style type="text/css">
	.TxtWhiteBold {font-weight: bold;}
</style>
<script language="javascript">
	var pagina=document.URL;
	//pagina = pagina.substring(0, pagina.indexOf("VisadoPoderes"));
	pagina=pagina.substring(0, pagina.search("/VisadoPoderes/")+8);

	/*function redireccionar() 
	{
		location.href=pagina;
	}*/

	function cerrar() 
	{
		//var ventana = window.self;
		//ventana.opener = window.self;
		window.close();
	}
	
	setTimeout ("cerrar()", 1000);
	
	function redireccionar(){
	var url = location.href;
	var str = url.substring(url.indexOf("VisadoPoderes"), url.length);
	url = url.substring(0, url.indexOf("VisadoPoderes"));
	var inicio = str.indexOf("/");
	str= str.substring(0,inicio);
	url = url+str;
	// alert('var inicio ' +inicio+' url samira ' +url +' str '+str);
	 location.href=url;
	   }
	
</script>
                  
</head>
<body onload="redireccionar()">

<table width="100%" height="100%">
  <tr>
    <td align="center" valign="middle">
      <div style="border: #00529B solid 1; width: 325;" align="center">
        <table width="325" height="20" cellpadding="0" cellspacing="0" border="0">
          <tr>
            <td align="center" bgcolor="#00529B"><!-- background="images/barraazul4.gif"-->
              <font color="white" font: 62.5% "Trebuchet MS", sans-serif>&nbsp;Mensaje</font>
            </td>
          </tr>
        </table>
        <br><br><br>
        <center>El cierre de la sesi&oacute;n se realiz&oacute; con &eacute;xito.</center>
        <br><br><br>
      </div>
    </td>
  </tr>
</table>

</body>
</html>
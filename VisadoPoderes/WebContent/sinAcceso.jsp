<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Sin Acceso</TITLE>
</HEAD>
<BODY>
<% String strMensaje = (String)request.getSession().getAttribute("strMensaje"); %>
<TABLE border="1" align="center">
		<TBODY>
			<TR>				
				<TD colspan="2" align="center"><h1>Aviso del Sistema</h1></TD>
			</TR>
			<TR>
				<TD><img src="./resources/images/attention.png"></img> </TD>
				<TD align="center"><span><%=strMensaje %></span><br/><br/>
				
				</TD>
			</TR>			
		</TBODY>
	</TABLE>
</BODY>
</HTML>

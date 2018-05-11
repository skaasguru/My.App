<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="java.sql.*,com.skaas.core.*" %>

<%
	String user_id = (String)session.getAttribute("id");
	Boolean isLoggedIn = (user_id != null);
	if ( !isLoggedIn ){
		response.sendRedirect("index.jsp");
	}
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Contacts</title>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="styles/styles.css" type="text/css" media="screen">
</head>
<body>
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
			      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
			        <span class="icon-bar"></span>
			        <span class="icon-bar"></span>
			        <span class="icon-bar"></span>                        
			      </button>
				<a class="navbar-brand" href="index.jsp">My App</a>
			</div>
		    <div class="collapse navbar-collapse" id="myNavbar">
				<ul class="nav navbar-nav">
					<li><a href="index.jsp">Dashboard</a></li>
					<li class="active"><a href="contacts.jsp">My Contacts</a></li>
					<li><a href="gallery.jsp">My Gallery</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="loginservlet?logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
				</ul>
			</div>
		</div>
	</nav>
	<div class="container">
		<h2 class="text-center">My Contacts</h2>
		<a class="btn btn-default btn-circle pull-right" href="contacts-add.jsp">
			<span class="glyphicon glyphicon-plus"></span> Add
		</a>
		<table class="table table-hover">
			<thead>
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>Phone No.</th>
				</tr>
			</thead>
			<tbody>
				<%
				   	MySQLConnector mysql =	new MySQLConnector();
			   		ResultSet resultset = mysql.executeQuery("SELECT id,name,phone FROM contacts WHERE user_id="+user_id);
				   	while(resultset.next()){
			   	%>
				<tr>
				    <td><%= resultset.getString(1) %></td>
				    <td><%= resultset.getString(2) %></td>
				    <td><%= resultset.getString(3) %></td>
					<td>
						<a href="contactservlet?delete=<%= resultset.getString(1) %>">
							<span class="glyphicon glyphicon-trash"></span>
						</a>
					</td>
				</tr>
				<%
					}
				   	mysql.close(resultset);
			   	%>					
			</tbody>
		</table>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@page import="java.util.List"%>
<%@page import="com.datastax.driver.core.Row"%>
<%@ page import="com.skaas.core.CassandraConnector,com.skaas.core.AppConfig,java.io.File" %>
<%
	String user_id = (String) session.getAttribute("id");
	Boolean isLoggedIn = (user_id != null);
	
	String fileLocation = AppConfig.fileLocation;
%>

<!DOCTYPE html>
<html>
<head>
    <title>My App!</title>
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
					<li class="active"><a href="index.jsp">Dashboard</a></li>
					<li><a href="contacts.jsp">My Contacts</a></li>
					<li><a href="gallery.jsp">My Gallery</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<% if (!isLoggedIn) { %>
					<li><a href="signup.jsp"><span class="glyphicon glyphicon-user"></span> Sign Up</a></li>
					<li><a href="login.jsp"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
					<% } else { %>
					<li><a href="loginservlet?logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
					<% } %>
				</ul>
			</div>
		</div>
	</nav>
	
	<div class="container">
		<div class="row">
		<% if (isLoggedIn) { %>
			<div class="col-sm-6">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h2 class="text-center">My Contacts</h2>
					</div>
					<div class="panel-body text-center">
						<%
						String query = "SELECT COUNT(*) from contacts WHERE user_id=" + user_id;

						CassandraConnector cassandra = new CassandraConnector();
						Long contactsCount = cassandra.execute(query).all().get(0).getLong("count");
						out.println("<p>You have <b>" + contactsCount + "</b> contacts in your account.</p>");
				        cassandra.close();
					   	%>
					   	<br>
					   	<br>
					   	<div class="btn-group">
							  <a href="contacts.jsp" class="btn btn-default">View Contacts</a>
							  <a href="contacts-add.jsp" class="btn btn-default">Add Contact</a>
						</div>
					</div>
				</div>
			</div>
			<div class="col-sm-6">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h2 class="text-center">My Gallery</h2>
					</div>
					<div class="panel-body text-center">
						<%
							File folder = new File(fileLocation + File.separator + user_id);
							if(folder.exists()){
								out.println("<p>You have <b>" + folder.listFiles().length + "</b> photos in your account.</p>");
							} else {
								out.println("<p>Add photos to your account by clicking <b>Add Photo</b> button below.</p>");
							}
					   	%>
					   	<br>
					   	<br>
					   	<div class="btn-group">
							  <a href="gallery.jsp" class="btn btn-default">View Photos</a>
							  <a href="gallery-add.jsp" class="btn btn-default">Add Photo</a>
						</div>
					</div>
				</div>
			</div>
		<% } else { %>
			<div class="col-sm-6 col-sm-offset-3">
				<div class="panel panel-danger">
					<div class="panel-heading">
						<h2 class="text-center">Please Login to Continue</h2>
					</div>
					<div class="panel-body text-center">
						<p>You are not logged in. Please login to view your dashboard.</p>
						<p>If you're a new user, please register a new account.</p>
					   	<br>
					   	<br>
					   	<div class="btn-group">
							  <a href="login.jsp" class="btn btn-primary">Login</a>
							  <a href="signup.jsp" class="btn btn-primary">Sign Up</a>
						</div>
					</div>
				</div>
			</div>
		<% } %>
		</div>
		<% if (isLoggedIn) { %>
		<div class="row">
			<div class="col-sm-12 text-center">	
				<a href="signupservlet?confirm=true" class="btn btn-danger">Delete Account and clear everything</a>
			</div>
		</div>
		<% } %>	
	</div>
</body>
</html>
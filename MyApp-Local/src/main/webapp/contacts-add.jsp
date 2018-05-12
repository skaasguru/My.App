<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

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
    <title>My Contacts - Add</title>
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
		
		<div class="col-md-8 col-md-offset-2">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h2 class="text-center">Add Contact</h2>
				</div>
				<div class="panel-body">
					<form class="form-horizontal" method="post" action="contactservlet">
						<div class="form-group">
							<label class="control-label col-sm-2" for="name">Name:</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" placeholder="Enter name" name="name">
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-sm-2" for="phone">Phone No:</label>
							<div class="col-sm-10">
								<input type="number" class="form-control" placeholder="Enter phone number" name="phone">
							</div>
						</div>
						<div class="form-group">
							<div class="text-center">
								<button type="submit" class="btn btn-primary">Submit</button>
								<a class="btn btn-default" href="contacts.jsp">Cancel</a>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
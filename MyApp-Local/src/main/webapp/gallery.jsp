<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="java.io.File" %>

<%
	String user_id = (String)session.getAttribute("id");
	Boolean isLoggedIn = (user_id != null);  
	if ( !isLoggedIn ){
		response.sendRedirect("index.jsp");
	}
	String fileLocation = "E:\\eclipse\\temp_files\\Uploads";
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Gallery</title>
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
					<li><a href="contacts.jsp">My Contacts</a></li>
					<li class="active"><a href="gallery.jsp">My Gallery</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="loginservlet?logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="container">
		<h2 class="text-center">My Gallery</h2>
		<div class="row">
			<a class="btn btn-default btn-circle pull-right"
				href="gallery-add.jsp"> <span class="glyphicon glyphicon-plus"></span>
				Add
			</a>
		</div>
		<br>
		
		<div class="row">
			<%
			if ( isLoggedIn ) {
				File folder = new File(fileLocation + File.separator + user_id);
				if(folder.exists() && folder.listFiles().length > 0){
					File[] listOfFiles = folder.listFiles();
		
					for (int i = 0; i < listOfFiles.length; i++)
					{
						if (listOfFiles[i].isFile())
						{
			%>
			<div class="col-md-3">
				<div class="thumbnail thumbnail-height">
					<a href="galleryservlet/<%= user_id %>/<%=listOfFiles[i].getName()%>">
						<img class="img-rounded" src="galleryservlet/<%= user_id %>/<%=listOfFiles[i].getName()%>" alt="Image">
						<span class="caption"><%=listOfFiles[i].getName()%></span>
					</a>
					<a class="top-right" href="galleryservlet/<%= user_id %>/<%=listOfFiles[i].getName()%>?delete">
						<span class="glyphicon glyphicon-trash"></span>
					</a>
				</div>
			</div>
			<%	 		}
					} 
				} else {%>
			<h3 class="text-center">No Images found. Please upload your images to list here.</h3>
				<%}
			}
			%>
		</div>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="com.amazonaws.services.s3.AmazonS3,
com.amazonaws.services.s3.AmazonS3Client,
com.amazonaws.services.s3.AmazonS3ClientBuilder,
com.amazonaws.services.s3.model.ListObjectsRequest,
com.amazonaws.services.s3.model.ListObjectsV2Request,
com.amazonaws.services.s3.model.ListObjectsV2Result,
com.amazonaws.services.s3.model.ObjectListing,
com.amazonaws.services.s3.model.S3ObjectSummary" %>
<%@ page import="com.skaas.core.AppConfig" %>

<%
	String user_id = (String)session.getAttribute("id");
	Boolean isLoggedIn = (user_id != null);  
	if ( !isLoggedIn ){
		response.sendRedirect("index.jsp");
	}
	String bucketName = AppConfig.bucket;
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
			AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
			final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix("images/"+user_id+"/");
			ListObjectsV2Result	result = s3.listObjectsV2(req);
			if (result.getKeyCount() > 0) {
				for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
			%>
			<div class="col-md-3">
				<div class="thumbnail thumbnail-height">
					<a href="https://<%= bucketName %>.s3.amazonaws.com/<%= objectSummary.getKey() %>">
						<img class="img-rounded" src="https://<%= bucketName %>.s3.amazonaws.com/<%= objectSummary.getKey() %>" alt="Image">
						<span class="caption"><%=objectSummary.getSize()%></span>
					</a>
					<a class="top-right" href="galleryservlet/<%=objectSummary.getKey()%>?delete">
						<span class="glyphicon glyphicon-trash"></span>
					</a>
				</div>
			</div>
				<% } 
			} else { %>
				<h3 class="text-center">No Images found. Please upload your images to list here.</h3>
			<% } %>
		</div>
	</div>
</body>
</html>
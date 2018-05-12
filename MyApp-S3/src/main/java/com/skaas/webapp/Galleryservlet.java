package com.skaas.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Servlet implementation class Galleryservlet
 */

@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class Galleryservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String bucket = "yourbucketname";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Galleryservlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		if(session != null && session.getAttribute("id") != null){
	        String objectName = URLDecoder.decode(request.getPathInfo(), "UTF-8").substring(1);

	        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
	        System.out.println(objectName);

			if (request.getParameterMap().containsKey("delete")) {
				s3.deleteObject(new DeleteObjectRequest(bucket, objectName));
				response.sendRedirect(request.getContextPath()+"/gallery.jsp");
	    	} else {
	    		
	    		S3Object file = s3.getObject(new GetObjectRequest(bucket, objectName));
		        
		    	String mimetype = file.getObjectMetadata().getContentType();
		    	response.setContentType(mimetype == null ? "application/octet-stream" : mimetype);
		    	response.setContentLength((int) file.getObjectMetadata().getContentLength());
		    	
		    	S3ObjectInputStream fis = file.getObjectContent();
		    	OutputStream fos = response.getOutputStream();
	
		    	try {
		    		byte[] buffer = new byte[4096];
		    	    int byteRead = 0;
		    	    while ((byteRead = fis.read(buffer)) >= 0) {
		    	       fos.write(buffer, 0, byteRead);
		    	    }
		    	    fos.flush();
		    	} catch (Exception e) {
		    	    e.printStackTrace();
		    	} finally {
		    	    fos.close();
		    	    fis.close();
		    	}
	    	}	    	
		} else {
			PrintWriter out = response.getWriter();
			out.println("You're not logged in");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		if(session != null && session.getAttribute("id") != null){  
	    	String user_id = (String)session.getAttribute("id");
	    	
	    	Part filePart = request.getPart("file");

	    	String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	    	InputStream fileContent = filePart.getInputStream();
	    	 
	        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
	        String objectName = "images/"+user_id+"/"+fileName;
	        s3.putObject(new PutObjectRequest(bucket, objectName, fileContent, null));
	        
			response.sendRedirect("gallery.jsp");
		} else {
			PrintWriter out = response.getWriter();
			out.println("You're not logged in");
		}
	}
}

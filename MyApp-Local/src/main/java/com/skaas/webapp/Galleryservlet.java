package com.skaas.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 * Servlet implementation class Galleryservlet
 */

@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class Galleryservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String fileLocation = "E:\\eclipse\\temp_files\\Uploads";

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
	    	File file = new File(fileLocation, URLDecoder.decode(request.getPathInfo(), "UTF-8"));
	    	
	    	if (request.getParameterMap().containsKey("delete")) {
	    		file.delete();
				response.sendRedirect(request.getContextPath()+"/gallery.jsp");
	    	} else {
		    	String mimetype = URLConnection.guessContentTypeFromName(file.getName());
		    	response.setContentType(mimetype == null ? "application/octet-stream" : mimetype);
		    	response.setContentLength((int) file.length());
		    	
		    	FileInputStream fis = new FileInputStream(file);
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
	    	
	    	String fs = File.separator;
	        String savePath = fileLocation + fs + user_id;
	        
	        File fileSaveDir = new File(savePath);
	        if (!fileSaveDir.exists()) {
	        	if (!fileSaveDir.mkdirs()) {
			        System.out.println("Cannot able to create the Upload Directory");	        		
			        PrintWriter out = response.getWriter();
					out.println("Cannot able to create the Upload Directory");
	        	}
	        }

	        Part filePart = request.getPart("file");
	        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	        filePart.write(savePath + fs + fileName);

			response.sendRedirect("gallery.jsp");
		} else {
			PrintWriter out = response.getWriter();
			out.println("You're not logged in");
		}
	}
}

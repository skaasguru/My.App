package com.skaas.webapp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.datastax.driver.core.utils.UUIDs;
import com.skaas.core.AppConfig;
import com.skaas.core.CassandraConnector;

/**
 * Servlet implementation class Signupservlet
 */
public class Signupservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String fileLocation = AppConfig.fileLocation;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signupservlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		System.out.println((session != null) + "--" + (session.getAttribute("id") != null));
		if(session != null && session.getAttribute("id") != null){  
	    	String user_id = (String)session.getAttribute("id");
	    	
	    	String query1 = "DELETE FROM contacts WHERE user_id=" + user_id + ";";
	    	String query2 = "DELETE FROM users WHERE id=" + user_id + ";";

	    	CassandraConnector cassandra = new CassandraConnector();
	        cassandra.execute(query1);
	        cassandra.execute(query2);
	        cassandra.close();
			
	    	
		    String savePath = fileLocation + File.separator + user_id;
			File folder = new File(savePath);
			if(folder.exists()){
				File[] listOfFiles = folder.listFiles();
	
				for (int i = 0; i < listOfFiles.length; i++)
				{
				    if (listOfFiles[i].isFile()){
				    	listOfFiles[i].delete();
				    }
			    }
			    new File(savePath).delete();
			}
		    session.invalidate();
			response.sendRedirect("index.jsp");
		} else {
			PrintWriter out = response.getWriter();
			out.println("You're not logged in");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String  name = request.getParameter("name"),
				email = request.getParameter("email"),
				password = request.getParameter("password");
		
		if (name != null && email != null && password != null) {
			String query = "INSERT INTO users (id, name, email, password) VALUES ( " + UUIDs.timeBased() + ", '" + name + "', '" + email + "', '" + password + "');";

			CassandraConnector cassandra = new CassandraConnector();
	        cassandra.execute(query);
	        cassandra.close();
	        
			response.sendRedirect("login.jsp");
		} else {
			out.println("The parameters 'name', 'email' and/or 'password' is not found in the request");
		}
	}

}

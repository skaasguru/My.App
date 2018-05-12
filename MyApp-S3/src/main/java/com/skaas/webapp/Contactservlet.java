package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.datastax.driver.core.utils.UUIDs;
import com.skaas.core.CassandraConnector;

/**
 * Handles Operations related to "My Contact"
 */
public class Contactservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Contactservlet() {
        super();
    }

	/**
	 * Deletes a contact by getting the query param "delete" from the request
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session=request.getSession(false);  
		if(session != null && session.getAttribute("id") != null){
			String user_id = (String) session.getAttribute("id");
			
			if (request.getParameter("delete") != null) {
				
				String query = "DELETE FROM contacts WHERE user_id=" + user_id + " AND id=" + request.getParameter("delete") + ";";

				CassandraConnector cassandra = new CassandraConnector();
		        cassandra.execute(query);
		        cassandra.close();

				response.sendRedirect("contacts.jsp");	
			} else {
				out.println("The parameter 'delete' is not found in the request");
			}
		} else {
			out.println("You're not logged in");
		}
	}

	/**
	 * Saves a contact to the DB
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session=request.getSession(false);
		
		if(session != null && session.getAttribute("id") != null){  
			String user_id = (String) session.getAttribute("id");
			
			if (request.getParameter("name") != null && request.getParameter("phone") != null) {
		    	
				String query = "INSERT INTO contacts (id, user_id, name, phone) VALUES (" + UUIDs.timeBased() + ", " + user_id + ", '" + request.getParameter("name") + "', '" + request.getParameter("phone") + "');";
				
				CassandraConnector cassandra = new CassandraConnector();
		        cassandra.execute(query);
		        cassandra.close();
				
		        response.sendRedirect("contacts.jsp");
			} else {
				out.println("The parameters 'name' and/or 'phone' is not found in the request");
			}
		} else {
			out.println("You're not logged in");
		}
		
	}
	
}

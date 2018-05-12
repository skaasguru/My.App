package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

import com.datastax.driver.core.Row;
import com.skaas.core.CassandraConnector;

/**
 * Servlet implementation class Loginservlet
 */
public class Loginservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Loginservlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session=request.getSession(false);  
        if(session != null){  
        	if (request.getParameter("logout") != null) {        		
        		session.invalidate();
        	}
        	response.sendRedirect("index.jsp");
        } else {
        	out.println("You're not logged in");
        }  
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
        String email = request.getParameter("email");  
        String password = request.getParameter("password");

        String query = "SELECT * FROM users WHERE email = '" + email + "' LIMIT 1;";
        CassandraConnector cassandra = new CassandraConnector();
        List<Row> queryResult = cassandra.execute(query).all();
        if (queryResult.size() == 0 ) {
			out.print("<h1>User Not Found!</h1>");
			out.close();
        }
        Row user = queryResult.get(0);
        cassandra.close();

        String actualPassword = user.getString("password");
        UUID user_id = user.getUUID("id");
		System.out.println(email + " (" + password + " == " + actualPassword  + ") =" + password.equals(actualPassword));
        
        if (actualPassword != null) {
	        if(password.equals(actualPassword)){  
		        HttpSession httpSession = request.getSession();  
		        httpSession.setAttribute("id", user_id.toString());
		        response.sendRedirect("index.jsp");
	        }  
	        else{  
	            out.print("<h1>Wrong Password!</h1>"); 
	        }   
        }
	}

}

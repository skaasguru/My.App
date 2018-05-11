package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;
import com.skaas.core.MySQLConnector;

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
        String actualPassword = null;
        String user_id = null;
        
        String query = "SELECT * FROM users WHERE `email`='" + email + "'";
        try {
			MySQLConnector mysql = new MySQLConnector();
			ResultSet resultset = mysql.executeQuery(query);
			if (resultset.next()){
				user_id = resultset.getString(1);
				actualPassword = resultset.getString(4);
			} else {
				out.print("<h1>User Not Found!</h1>");
				out.close();
			}			
		} catch (ClassNotFoundException | SQLException e) {
			out.print("<h1>DB Error!</h1>");
			out.print(e.getMessage());
			out.close();
			e.printStackTrace();
		}
        
        if (actualPassword != null) {
	        if(password.equals(actualPassword)){  
		        HttpSession session = request.getSession();  
		        session.setAttribute("id",user_id);
		        response.sendRedirect("index.jsp");
	        }  
	        else{  
	            out.print("<h1>Wrong Password!</h1>"); 
	        }   
        }
	}

}

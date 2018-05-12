package com.skaas.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;



public class Helloservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Helloservlet() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("name", "Contact Service");
		body.put("version", "1.0.0");
		
		response.setContentType("application/json");
		response.getWriter().println((new Gson()).toJson(body));
	}
}

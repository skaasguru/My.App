package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.datastax.driver.core.utils.UUIDs;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.skaas.core.CassandraConnector;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;


public class Signupservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public Signupservlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
        Map<String,Object> body = new HashMap<String,Object>();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer")){  
        	 String jwt = authorization.substring("Bearer".length()).trim();
        	 try {
	        	 Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parseClaimsJws(jwt).getBody();
	        	 // Call Delete all endpoint of contact service and gallery service
	        	 
        	 } catch (ExpiredJwtException e) {
        		 body.put("error", "TOKEN_EXPIRED");
        		 body.put("error_detail", e.getMessage());
        		 response.setStatus(401);
        	 } catch (SignatureException e) {
        		 body.put("error", "INVALID_SIGNATURE");
        		 body.put("error_detail", e.getMessage());
        		 response.setStatus(403);
        	 } catch (JwtException e) {
        		 body.put("error", "UNKNOWN_ERROR");
        		 body.put("error_detail", e.getMessage());
        		 response.setStatus(500);
        	 }
         } else {
        	 body.put("error", "TOKEN_NOT_FOUND");
        	 response.setStatus(400);
         }  

		out.println(gson.toJson(body));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String requestChunk;
        while ((requestChunk = request.getReader().readLine()) != null) { stringBuilder.append(requestChunk); }
        Gson gson = new Gson();
        JsonObject requestBody = gson.fromJson(stringBuilder.toString(), JsonObject.class);
		
        Map<String,Object> body = new HashMap<String,Object>();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

        
		String 	name = requestBody.get("name").getAsString(),
				email = requestBody.get("email").getAsString(),
				password = requestBody.get("password").getAsString();
		
		if (name != null && email != null && password != null) {
			String query = "INSERT INTO users (id, name, email, password) VALUES ( " + UUIDs.timeBased() + ", '" + name + "', '" + email + "', '" + password + "');";

			CassandraConnector cassandra = new CassandraConnector();
	        cassandra.execute(query);
	        cassandra.close();

        	body.put("message", "User Created Successfully");
		} else {
        	body.put("error", "INSUFFICIENT_PARAMETERS");
        	response.setStatus(400);
		}

		out.println(gson.toJson(body));
	}

}

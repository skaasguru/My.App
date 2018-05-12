package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.datastax.driver.core.Row;
import com.skaas.core.CassandraConnector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;



public class Loginservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Loginservlet() {
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
	        	 Claims tokenBody = Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parseClaimsJws(jwt).getBody();
	        	 body.put("id", tokenBody.getSubject());
	        	 body.put("email", tokenBody.get("email", String.class));
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

        
		String email = requestBody.get("email").getAsString();  
        String password = requestBody.get("password").getAsString();

        String query = "SELECT * FROM users WHERE email = '" + email + "' LIMIT 1;";
        CassandraConnector cassandra = new CassandraConnector();
        List<Row> queryResult = cassandra.execute(query).all();
        if (queryResult.size() == 0 ) {
        	body.put("error", "USER_NOT_FOUND");
    		out.println(gson.toJson(body));
    		out.close();
    		response.setStatus(404);
        }
        Row user = queryResult.get(0);
        cassandra.close();

        String actualPassword = user.getString("password");
        UUID user_id = user.getUUID("id");
		System.out.println(email + " (" + password + " == " + actualPassword  + ") =" + password.equals(actualPassword));
		
        if (actualPassword != null) {
	        if(password.equals(actualPassword)){
	        	String jwt = Jwts.builder()
	        			  .setSubject(user_id.toString())
	        			  .setExpiration(new Date(System.currentTimeMillis() + 3600 * 10 * 1000))
	        			  .claim("email", email)
	        			  .signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
	        			  .compact();
	    		body.put("token", jwt);
	        }  
	        else{
	        	body.put("error", "INCORRECT_PASSWORD");
	        	response.setStatus(400);
	        }   
        } else {
        	body.put("error", "NO_PASSWORD_EXIST");
        	response.setStatus(422);
        }

		out.println(gson.toJson(body));
	}

}

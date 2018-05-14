package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.datastax.driver.core.utils.UUIDs;
import com.skaas.core.AppConfig;
import com.skaas.core.CassandraConnector;
import com.datastax.driver.core.Row;

public class Contactservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static HttpClient HTTP = HttpClientBuilder.create().build();

    public Contactservlet() {
        super();
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
        Map<String,Object> body = new HashMap<String,Object>();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		HttpGet authRequest = new HttpGet(AppConfig.authServiceEndpoint + "/token");
		authRequest.setHeader("Authorization", request.getHeader("Authorization"));
		HttpResponse authResponse = HTTP.execute(authRequest);
		String authJson = EntityUtils.toString(authResponse.getEntity());
		if (authResponse.getStatusLine().getStatusCode() != 200) {
			response.setStatus(authResponse.getStatusLine().getStatusCode());
    		out.println(authJson);
    		out.close();
    		return;				
		}
		JsonObject authData = gson.fromJson(authJson, JsonObject.class);
		
		
		String query = "SELECT id, name, phone from contacts WHERE user_id=" + authData.get("id").getAsString();
		
		CassandraConnector cassandra = new CassandraConnector();
		List<Row> rowList = cassandra.execute(query).all();

		List<Map<String,String>> contacts = new ArrayList<>();
		Map<String,String> contact;
        for (Row row: rowList) {
        	contact = new HashMap<String,String>();
        	contact.put("id", row.getUUID("id").toString());
        	contact.put("name", row.getString("name"));
        	contact.put("phone", row.getString("phone"));
        	contacts.add(contact);
        }
        cassandra.close();
		
		body.put("contacts", contacts);
		
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
		
		HttpGet authRequest = new HttpGet(AppConfig.authServiceEndpoint + "/token");
		authRequest.setHeader("Authorization", request.getHeader("Authorization"));
		HttpResponse authResponse = HTTP.execute(authRequest);
		String authJson = EntityUtils.toString(authResponse.getEntity());
		if (authResponse.getStatusLine().getStatusCode() != 200) {
			response.setStatus(authResponse.getStatusLine().getStatusCode());
    		out.println(authJson);
    		out.close();
    		return;				
		}
		JsonObject authData = gson.fromJson(authJson, JsonObject.class);
		
		
		String 	user_id = authData.get("id").getAsString(),
				name = requestBody.get("name").getAsString(),
				phone = requestBody.get("phone").getAsString();
		
		if (name != null && phone != null) {
			String query = "INSERT INTO contacts (id, user_id, name, phone) VALUES (" + UUIDs.timeBased() + ", " + user_id + ", '" + name + "', '" + phone + "');";
			
			CassandraConnector cassandra = new CassandraConnector();
	        cassandra.execute(query);
	        cassandra.close();
	        
	        body.put("message", "Contact added successfully");
	        
		} else {
			body.put("error", "INSUFFICIENT_PARAMETERS");
			response.setStatus(400);
		}

		out.println(gson.toJson(body));
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
        Map<String,Object> body = new HashMap<String,Object>();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		HttpGet authRequest = new HttpGet(AppConfig.authServiceEndpoint + "/token");
		authRequest.setHeader("Authorization", request.getHeader("Authorization"));
		HttpResponse authResponse = HTTP.execute(authRequest);
		String authJson = EntityUtils.toString(authResponse.getEntity());
		if (authResponse.getStatusLine().getStatusCode() != 200) {
			response.setStatus(authResponse.getStatusLine().getStatusCode());
    		out.println(authJson);
    		out.close();
    		return;				
		}
		JsonObject authData = gson.fromJson(authJson, JsonObject.class);
		
		
		String user_id = authData.get("id").getAsString();
		
		String query = "DELETE FROM contacts WHERE user_id=" + user_id + ";";

		CassandraConnector cassandra = new CassandraConnector();
        cassandra.execute(query);
        cassandra.close();
        
        body.put("message", "All Contacts deleted successfully");

		out.println(gson.toJson(body));
	}


	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
        Map<String,Object> body = new HashMap<String,Object>();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		HttpGet authRequest = new HttpGet(AppConfig.authServiceEndpoint + "/token");
		authRequest.setHeader("Authorization", request.getHeader("Authorization"));
		HttpResponse authResponse = HTTP.execute(authRequest);
		String authJson = EntityUtils.toString(authResponse.getEntity());
		if (authResponse.getStatusLine().getStatusCode() != 200) {
			response.setStatus(authResponse.getStatusLine().getStatusCode());
    		out.println(authJson);
    		out.close();
    		return;				
		}
		JsonObject authData = gson.fromJson(authJson, JsonObject.class);
		
		
		String user_id = authData.get("id").getAsString();
		
		if (request.getParameter("delete") != null) {
			
			String query = "DELETE FROM contacts WHERE user_id=" + user_id + " AND id=" + request.getParameter("delete") + ";";

			CassandraConnector cassandra = new CassandraConnector();
	        cassandra.execute(query);
	        cassandra.close();

	        body.put("message", "Contact deleted successfully");
		} else {
			body.put("error", "INSUFFICIENT_PARAMETERS");
			response.setStatus(400);
		}
		
		out.println(gson.toJson(body));
	}
}

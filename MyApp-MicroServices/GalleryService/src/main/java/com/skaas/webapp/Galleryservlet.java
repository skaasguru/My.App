package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import com.skaas.core.AppConfig;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;


import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class Galleryservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String bucket = AppConfig.bucket;
	private static HttpClient HTTP = HttpClientBuilder.create().build();

    public Galleryservlet() {
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
		
		String 	user_id = authData.get("id").getAsString();

		AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
		ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucket).withPrefix("images/" + user_id + "/");
		ListObjectsV2Result	listObjectsResult = s3.listObjectsV2(listObjectsRequest);
		
		List<Map<String,Object>> images = new ArrayList<>();
		Map<String,Object> image;
		String url;
		
		for (S3ObjectSummary objectSummary : listObjectsResult .getObjectSummaries()) {
	        url = s3.generatePresignedUrl(
        		new GeneratePresignedUrlRequest(bucket, objectSummary.getKey())
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + 3600 * 10 * 1000))
            ).toString();

			image = new HashMap<String,Object>();
			image.put("key", objectSummary.getKey());
			image.put("size", objectSummary.getSize());
			image.put("url", url);
			
			images.add(image);
		}
		
		body.put("images", images);
		body.put("endpoint", "https://" + bucket + ".s3.amazonaws.com/");

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
		
		String 	user_id = authData.get("id").getAsString();
		String fileName = requestBody.get("filename").getAsString();

        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String objectName = "images/"+user_id+"/"+fileName;
        
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, objectName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(new Date(System.currentTimeMillis() + 3600 * 10 * 1000));
        String url = s3.generatePresignedUrl(generatePresignedUrlRequest).toString();
        
		body.put("url", url);

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
		
		String 	user_id = authData.get("id").getAsString();

		AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        
		ListObjectsV2Request objectsListRequest = new ListObjectsV2Request().withBucketName(bucket).withPrefix("images/"+user_id+"/");
		ListObjectsV2Result	result = s3.listObjectsV2(objectsListRequest);
		
		ArrayList<KeyVersion> keys = new ArrayList<KeyVersion>();
		for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
			keys.add(new KeyVersion(objectSummary.getKey()));
		}

		if (keys.size() > 0) {				
			s3.deleteObjects((new DeleteObjectsRequest(bucket)).withKeys(keys));
		}
		body.put("message", "All Files deleted successfully");
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

        String objectName = request.getParameter("delete");

		if (objectName != null) {
	        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
			s3.deleteObject(new DeleteObjectRequest(bucket, objectName));
	        body.put("message", "File deleted successfully");
		} else {
			body.put("error", "INSUFFICIENT_PARAMETERS");
			response.setStatus(400);
    	}

		out.println(gson.toJson(body));	
	}
}

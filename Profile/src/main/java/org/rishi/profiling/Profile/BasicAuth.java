package org.rishi.profiling.Profile;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
//import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Provider
public class BasicAuth  {
	static Connector connector = new Connector();
	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BASIC_AUTH_PREFIX = "Basic ";

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "pass";

 
    public Response filter(String credentials) {
    	
//        String authHeader = request.getHeaderValue(AUTHORIZATION_HEADER_KEY);
 try {
	 byte[] decodedBytes = Base64.getDecoder().decode(credentials);
		String decodedString = new String(decodedBytes);
        
            String[] userPass = decodedString.split(":");
            String username = userPass[0];
            String password = userPass[1];

//            if (!isValidCredentials(username, password)) {
//            	throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity("Authentication Unsuccefull ").build());            }
            isValidCredentials(username, password);
            System.out.println("logging");
            String token=new String();
            if(!AlreadyExists(username)) {
        token = issueToken(username);
        System.out.println("logging");
        return Response.ok(token).build();}
            else {
            	Connection connection =connector.getConnection();
        		String state="SELECT * FROM token_details WHERE username=?";
        		PreparedStatement statement1=connection.prepareStatement(state);
        		statement1.setString(1,username);
        		ResultSet result=statement1.executeQuery();
        		
            	while(result.next()) {
            		token=result.getString("token");
            	}
//            	Connection connection1 =connector.getConnection();
        		String state2="UPDATE token_details SET created_time=?,expiry_time=? WHERE username=?";
        		PreparedStatement statement2=connection.prepareStatement(state2);
        		long now = System.currentTimeMillis();
        		long nowPlus5Minutes = now + TimeUnit.MINUTES.toMillis(5);
        		java.sql.Timestamp Plus5min_new= new java.sql.Timestamp(nowPlus5Minutes);
        		System.out.println(new Date());
        		java.util.Date utilDate = new java.util.Date();
        		java.sql.Timestamp sqlTimestamp_new = new java.sql.Timestamp(utilDate.getTime());
        		statement2.setTimestamp(1,sqlTimestamp_new);
        		statement2.setTimestamp(2,Plus5min_new);
        		statement2.setString(3,username);
        		int rowsInserted = statement2.executeUpdate();
        		if (rowsInserted > 0) {
        			System.out.print("Repeated string");
                	return Response.ok(token).build();
        		} else {
        			System.out.println("Failed to update.");
        			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to update").build();
        		}
            	
            }
        }
        catch(Exception e) {
        	return Response.status(Response.Status.FORBIDDEN).build();        }
        }
//		return request;
//        // If credentials are invalid or not provided, return a 401 Unauthorized response
//        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
    

    private void isValidCredentials(String username, String password) throws Exception {
        // You can implement your own logic to validate the username and password
    	Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection =connector.getConnection();
		String state="SELECT * FROM user_details WHERE user_id=?";
		PreparedStatement statement1=connection.prepareStatement(state);
		statement1.setString(1,username);
		try {
			
		int i=0;
      
    	   ResultSet result=statement1.executeQuery();

           // Process the results
           while (result.next()) {
               if(result.getString("Passwords").equals(password)) {
            	   i++;
            	   System.out.println("Magic");
               }
           }
       if(i>0) {
    	   
       }
       else {
    	   throw new Exception();
       }}
		catch(SQLException e) {
			throw new Exception();
		}
    }
    private String issueToken(String username) throws SQLException, ClassNotFoundException {
    	String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
    	Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection =connector.getConnection();
		
		
		
		

    	Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), 
    	                            SignatureAlgorithm.HS256.getJcaName());
    	String jwtToken=Jwts.builder().claim("username", username).setSubject("Rishith").setId(UUID.randomUUID().toString()).setIssuedAt(Date.from(Instant.now())).setExpiration(Date.from(Instant.now().plus(5l,ChronoUnit.MINUTES))).signWith(hmacKey).compact();
    	System.out.println(jwtToken);
    	String state="INSERT INTO token_details (username,token,secret_key,created_time,expiry_time) VALUES(?,?,?,?,?)";
		PreparedStatement statement1=connection.prepareStatement(state);
		statement1.setString(1, username);
		statement1.setString(2, jwtToken);
		statement1.setString(3, secret);
//		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		long now = System.currentTimeMillis();
		long nowPlus5Minutes = now + TimeUnit.MINUTES.toMillis(5);
		java.sql.Timestamp Plus5min= new java.sql.Timestamp(nowPlus5Minutes);
		System.out.println(new Date());
		java.util.Date utilDate = new java.util.Date();
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());
		System.out.println(sqlTimestamp);
		System.out.println(Plus5min);
		System.out.println(new java.sql.Date(Date.from(Instant.now()).getTime()));
		statement1.setTimestamp(4, sqlTimestamp);
		statement1.setTimestamp(5,Plus5min);
		try {
		int rowsInserted = statement1.executeUpdate();
		if (rowsInserted > 0) {
			System.out.println("Data inserted successfully!");
//			return Response.status(Status.CREATED).entity(ob1).build();
		} else {
			System.out.println("Failed to insert data.");
//			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to add to database").build();
		}}
		catch(SQLException e) {
			e.printStackTrace();
		}
    	return jwtToken;
    }
    public static boolean ValidateToken(String token,String username) throws ClassNotFoundException {
    	String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
//    	System.out.println("Hi");
    	String Toki=token.substring(7,token.length());
    	System.out.println(Toki);
//    	Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), 
//    	                            SignatureAlgorithm.HS256.getJcaName());
    	
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection;
			
			connection = connector.getConnection();
			String state="SELECT * FROM token_details WHERE username=?";
			PreparedStatement statement1=connection.prepareStatement(state);
			
			statement1.setString(1,username);
			ResultSet result=statement1.executeQuery();
//			System.out.println(token);
			java.util.Date utilDate = new java.util.Date();
			java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());
			while (result.next()) {
				if(result.getString("token").equals(Toki)) {
//				System.out.println(result.getTimestamp("expiry_time"));
				if(result.getTimestamp("expiry_time").before(sqlTimestamp)) {
						System.out.println("session time exceed,login again");
			        	return false;    
			        	}
				else {
					return true;
				}}
				
			        
				}
			
			
		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("logging2");
		return false;
		
		
    }
    public static boolean AlreadyExists(String username) throws ClassNotFoundException {
    	Class.forName("com.mysql.cj.jdbc.Driver");
    	System.out.println("Checking");
    	try {
    	
		Connection connection =connector.getConnection();
		String state="SELECT * FROM token_details WHERE username=?";
		PreparedStatement statement1=connection.prepareStatement(state);
		statement1.setString(1,username);
		int i=0;
	      
 	   ResultSet result=statement1.executeQuery();

        // Process the results
        while (result.next()) {
        	i++;
        }
        if(i>0) {
        	return true;
        }
        else {
        	return false;
        }
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
		
    }
}

package org.rishi.profiling.Profile;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.Response.Status;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

@Path("/message2")
public class UserResources {
	static Connector connector = new Connector();
	static Validator val = new Validator();
	static MetadataReader reader = new MetadataReader();
	public static String[] params;

	@POST
//	@RolesAllowed("user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addprofiles(@Context UriInfo uriinfo, JSONObject obj)
			throws URISyntaxException, JSONException, ClassNotFoundException, SQLException {
		params = reader.process();
		JSONObject ob1 = new JSONObject();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = connector.getConnection();
//			String Insertsql = Operations.userOperations(obj,"Insert");
			PreparedStatement statement = Operations.userOperations(obj,"Insert");
			// FROM HERE
			if (val.Validata(obj)) {
				for (int i = 0; i < params.length; i++) {
//					System.out.println(obj.get(params[i]));
					if(obj.has(params[i])) {
					ob1.put(params[i], obj.get(params[i]));}
				}
				int j=0;
				for (int i = 0; i < params.length; i++) {
					if(obj.has(params[i])) {
					String req=MetadataReader.getreq(params[i]);
					if(req.equals("no") && obj.get(params[i]).equals(null)) {
//						i--;
					}
					else {
						j++;
					if (obj.get(params[i]).getClass().equals(String.class)) {
						statement.setString(j, obj.get(params[i]).toString());
					} else if (obj.get(params[i]).getClass().equals(Integer.class)) {
						statement.setInt(j, Integer.parseInt(obj.get(params[i]).toString()));
					} else {
//						System.out.println(obj.get(params[i]).getClass());
					}}
				}
				}
//				System.out.println(statement);
				int rowsInserted = statement.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("Data inserted successfully!");
					return Response.status(Status.CREATED).entity(ob1).build();
				} else {
					System.out.println("Failed to insert data.");
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to add to database").build();
				}

			} else {
				System.out.println(
						"Extra/Insufficient information passed in the request please refer to the documentation");
				return Response.status(Status.BAD_REQUEST).entity(
						"Extra/Insufficient information passed in the request please refer to the documentation")
						.build();

			}
		} catch (SQLException e1) {
//			System.out.println("HI2");
			e1.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("SQL ERROR").build();
//		return null;
		}
	}
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getallprofiles(JSONObject obj) throws ClassNotFoundException, SQLException, JSONException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection =connector.getConnection();
		PreparedStatement statement1=Operations.userOperations(obj,"getall");
//		statement1.setInt(1, (int)messageid);
		ResultSet result=statement1.executeQuery();
		 
		ResultSetMetaData metaData = result.getMetaData();
		int columnCount = metaData.getColumnCount();
		JSONArray jarray=new JSONArray();
		while(result.next()) { 
			Map <String,Object>s20= new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
		        String columnName = metaData.getColumnName(i);
		        Object columnValue = result.getObject(i);
		        s20.put(columnName,columnValue);
		    }
			JSONObject j1=new JSONObject(s20);
			jarray.put(j1);
	}
		result.close();
        statement1.close();
        connection.close();
        return Response.ok(jarray.toString(),MediaType.APPLICATION_JSON).build();}
	
	@GET
	@Path("/{messageid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getprofile(@PathParam("messageid") long messageid,JSONObject obj) throws SQLException, JSONException {
//		BasicAuth auth= new BasicAuth();
//		System.out.print(httpHeaders);
//		auth.filter(httpHeaders);
		Map <String,Object>s20= new HashMap<String, Object>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection =connector.getConnection();
		PreparedStatement statement1=Operations.userOperations(obj,"SingleGet");
		statement1.setInt(1, (int)messageid);
		ResultSet result=statement1.executeQuery();
		 
		ResultSetMetaData metaData = result.getMetaData();
		int columnCount = metaData.getColumnCount();

		while (result.next()) {
		    for (int i = 1; i <= columnCount; i++) {
		        String columnName = metaData.getColumnName(i);
		        Object columnValue = result.getObject(i);
		        s20.put(columnName,columnValue);
		    }
		}
        result.close();
        statement1.close();
        connection.close();

        JSONObject jsonObject = new JSONObject(s20);
        return Response.ok(jsonObject.toString(),MediaType.APPLICATION_JSON).build();
		}
		catch(SQLException e1) {
			e1.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("SQL ERROR").build();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Certain classes are not found").build();
		}
	}
//	@RolesAllowed("ADMIN")
	@DELETE
	@Path("/{messageid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteprofile(@PathParam("messageid") long messageid,JSONObject obj) throws SQLException, JSONException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection connection =connector.getConnection();
		PreparedStatement statement1=Operations.userOperations(obj,"delete");
		statement1.setInt(1, (int)messageid);
		int rowsAffected = statement1.executeUpdate();
		if (rowsAffected > 0) {
		    System.out.println("Row deleted successfully.");
		    return Response.status(Status.NO_CONTENT).build();
		} else {
		    System.out.println("No rows were deleted.");
		    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to delete,please check the messageid").build();
		}

	}
	@PUT	
	@Path("/{messageid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateprofile(@PathParam("messageid") int messageid,JSONObject obj) throws SQLException, JSONException {
		params = reader.process();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(val.validrequest(obj)) {
		Connection connection =connector.getConnection();
		PreparedStatement statement=Operations.userOperations(obj,"update");
		int j=0;
		System.out.println(obj.length());
		for (int i = 0; i < params.length; i++) {
			if(obj.has(params[i])) {
				j++;
			if (obj.get(params[i]).getClass().equals(String.class)) {
				statement.setString(j, obj.get(params[i]).toString());
				System.out.println(obj.get(params[i]).toString()+ " "+j);
			} else if (obj.get(params[i]).getClass().equals(Integer.class)) {
				statement.setInt(j, Integer.parseInt(obj.get(params[i]).toString()));
			} }
		}
		System.out.println(j+1);
		statement.setInt(j+1, messageid);
//		System.out.println(statement);
		System.out.println(statement);
		int rowsInserted = statement.executeUpdate();
		if (rowsInserted > 0) {
			System.out.println("Data inserted successfully!");
			return Response.status(Status.CREATED).build();
		} else {
			System.out.println("Failed to insert data.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to add to database").build();
		}
		}
		else {
			return Response.status(Status.BAD_REQUEST).entity("Invalid Object found in the request please refer to the documentation").build();
		}
	}
	
}

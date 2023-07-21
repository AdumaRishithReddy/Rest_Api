package org.rishi.profiling.Profile;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

//import org.json.JSONException;
//import org.json.JSONObject;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
//import Profiling.Profiles.MetadataReader;

public class Validator {
	static Connector connector = new Connector();
	public static String[] params = MetadataReader.process();

	public Validator() {

	}

	public static boolean isColumnPresent(String tableName, String columnName) {
		try (Connection connection = connector.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();

			try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
				return columns.next(); // true if column exists, false otherwise
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false; // Return false if an exception occurs
	}

	public static String check(int i, JSONObject obj) throws JSONException {
		String dataType;
		Object columnValue = obj.get(params[i]);
		if (columnValue instanceof Integer) {
			dataType = "INT";
		} else if (columnValue instanceof String) {
			dataType = "VARCHAR(255)";
		} else if (columnValue instanceof Double) {
			dataType = "DOUBLE";
		} else {
			dataType = "VARCHAR(255)"; // Default to a generic data type
		}
		return dataType;
	}

	public static boolean Validata(JSONObject obj) {
		int k = 0;
		JSONObject ob1 = new JSONObject();
		for (int i = 0; i < params.length; i++) {
			String req=MetadataReader.getreq(params[i]);
			System.out.println(obj.has(params[i])+ " "+params[i]+" "+req);
			if(obj.has(params[i])) {
			try {
//				System.out.println(obj.get(params[i]));
//				JSONObject j1=(JSONObject) obj.get(params[i]);
				
		
//			System.out.println(obj.get(params[i])==null); req.equals("yes") &&
				if(req.equals("yes")&& obj.get(params[i]).toString()== "null") {
					System.out.println("Hey Im required");
					k++;
					return false;
				}
				else {
					
				ob1.put(params[i], obj.get(params[i]));}
			} catch (Exception e) {
				k++;
				System.out.println(params[i] + " Is not found");

			}
			
			}else if(!obj.has(params[i]) && req.equals("yes")){
				System.out.println("why are u here");
				return false;
			}else {
				System.out.println("Im here "+k);
				continue;
			}
		}

		if (k != 0) {
			System.out.println("One or more objects not found");
			return false;
//			return Response.status(Status.BAD_REQUEST).entity("Wrong format of the request please refer to the documentation").build();
		} 
//		else if (obj.length() != params.length) {
//			
//			System.out.println("Extra information passed in the request please refer to the documentation");
//			return false;
////			return Response.status(Status.BAD_REQUEST).entity("Extra information passed in the request please refer to the documentation").build();
//		}
		else {
			return true;
		}
	}
	public static String validstate(String sq1,String sq2,JSONObject obj ) {
		String[] s1=MetadataReader.param(obj);
		for(String s:s1) {
			for(String si:params) {
				
				if(si.equals(s)) {
					System.out.println(si+s);
					sq1=sq1+" "+si+"= ?,";
				}
			}
		}
		sq1=sq1.substring(0,sq1.length()-1);
		return (sq1+sq2);
	}
	public static boolean validrequest(JSONObject obj) {
		String[] s1=MetadataReader.param(obj);
		for(String s:s1) {
			int i=0;
			for(String si:params) {
				if(si.equals(s)) {
					i++;
				}
			}
			System.out.println(i);
			if(i==0) {
				return false;
			}
		}
		return true;
	}
}

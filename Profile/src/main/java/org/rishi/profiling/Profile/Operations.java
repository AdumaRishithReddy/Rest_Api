package org.rishi.profiling.Profile;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

//import org.json.JSONException;
//import org.json.JSONObject;

public class Operations {
	static MetadataReader reader =new MetadataReader();
	public static String[] params=reader.process();
	static Connector connector= new Connector();
	static Validator val=new Validator();
	public Operations() {
		
	}
	public static PreparedStatement userOperations(JSONObject obj,String operation) throws SQLException, JSONException {
		String state="";
		if(operation=="Insert") {
		String Insertsql1 = "INSERT INTO personal (";
		String Insertsql2 =" VALUES (";
		String Insertsql="";
		for(int i=0;i<params.length;i++) {
			String req=MetadataReader.getreq(params[i]);
			if(!obj.has(params[i])) {
				
			}else if(obj.has(params[i])) {
//			System.out.println(obj.get(params[i]).toString().equals(null)+" "+req.equals("no")+" "+params[i]);
			if(!params[i].isEmpty()&& val.isColumnPresent("personal",params[i] )&& !obj.get(params[i]).equals(null)) {
				System.out.println(params[i] + " is going "+obj.get(params[i]).equals(null));
			Insertsql1=Insertsql1+params[i]+",";
			Insertsql2+="?,";}
			else if(val.isColumnPresent("personal",params[i] ) && req.equals("no") && obj.get(params[i]).equals(null)) {
				
			}
			else if(!params[i].isEmpty()&& !val.isColumnPresent("personal",params[i] ) && req.equals("yes") && !obj.get(params[i]).equals(null)) {
				try {
					Connection connection =connector.getConnection();
				String datatype=val.check(i,obj);
				
				String alter="ALTER TABLE personal ADD "+params[i]+" "+datatype+" ;";
				PreparedStatement statementis=connection.prepareStatement(alter);
				System.out.println(statementis);
				statementis.executeUpdate(alter);
				System.out.println("hello");
				Insertsql1=Insertsql1+params[i]+",";
				Insertsql2+="?,";}
				catch(SQLException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}}
		Insertsql1=Insertsql1.substring(0,Insertsql1.length()-1)+")";
		Insertsql2=Insertsql2.substring(0,Insertsql2.length()-1);
		Insertsql=Insertsql1+Insertsql2+");";
		System.out.print(Insertsql);
		state=Insertsql;
//		return Insertsql;
	}else if(operation=="SingleGet"){
		 state="SELECT * FROM personal WHERE id = ?";
	}
	else if(operation=="delete"){
		 state="DELETE FROM personal WHERE id = ?";
	}
	else if(operation=="update") {
//		state="SELECT * FROM personal;";
		String updatesql1 = "UPDATE personal SET ";
		String updatesql2 =" WHERE id= ?;";
		if(val.validrequest(obj)) {
		state=val.validstate(updatesql1,updatesql2,obj);
		
		System.out.println(state);
	}}
	else if(operation=="getall") {
		state="SELECT * FROM personal;";
	}
		Connection connection =connector.getConnection();
	 PreparedStatement statement= connection.prepareStatement(state);
	return statement;	
	
	}
	
}

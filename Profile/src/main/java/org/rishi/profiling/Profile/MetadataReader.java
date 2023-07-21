package org.rishi.profiling.Profile;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.codehaus.jettison.json.JSONArray;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MetadataReader {
	public static String[] params;
	public static JSONObject json;

	public MetadataReader() {

	}

	public static String[] process() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse("C:/Users/rishi/Downloads/nodic.xml");
			json = convert(doc.getDocumentElement());
//	            System.out.println(json.toString(4));
			FileWriter f3 = new FileWriter("C:/Users/rishi/Downloads/file4.json");
			params = param(json);
//	            String[] a=getparam();
//				System.out.println(a[4]);
			f3.write(json.toString());
			f3.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	private static JSONObject convert(Element element) throws JSONException {
		JSONObject json = new JSONObject();
//	        JSONObject j1 = new JSONObject();
		NodeList nodeList = element.getChildNodes();
//	        System.out.println(element);
		if (nodeList.getLength() == 1 && nodeList.item(0).getNodeType() == Node.TEXT_NODE) {

			json.put(element.getNodeName(), nodeList.item(0).getNodeValue().trim());
		} else {
			JSONArray arr = new JSONArray();
//	        	System.out.println(element);
//	        	json.put(element.getNodeName(), )
			for (int i = 0; i < nodeList.getLength(); i++) {

				Node childNode = nodeList.item(i);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					if (childNode.hasChildNodes()) {
						NodeList nodea = childNode.getChildNodes();
						if (nodea.getLength() == 1 && nodea.item(0).getNodeType() == Node.TEXT_NODE) {
							json.put(childNode.getNodeName(), nodea.item(0).getNodeValue().trim());
						} else {
//	                    		System.out.println(element);
							json.put(childNode.getNodeName(), convert((Element) childNode));
						}
					} else {
						String textContent = childNode.getTextContent().trim();
						if (!textContent.isEmpty()) {
							json.put(childNode.getNodeName(), textContent);
						} else {
							json.put(childNode.getNodeName(), "");
						}
					}
				}
			}
		}

		return json;
	}

	public static String[] param(JSONObject j1) {
		String[] s1 = new String[j1.length()];
		int i = 0;
//	    	j1.keySet();
		Iterator<String> itr = j1.keys();
		while (itr.hasNext()) {
//			System.out.println(itr.next());
			String s = itr.next();
			s1[i] = s;
			i++;
		}

		return s1;
	}
	public static String getreq(String key){
		try {
		JSONObject j1=  (JSONObject) json.get(key);
		String req;
		
			req = j1.getString("required");
		
//		System.out.println(req +" is the requirement for "+key);
		return req;} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}
}

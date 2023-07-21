package org.rishi.profiling.Profile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Connector {
	public Connector() {

	}

	private static final String DB_URL = "jdbc:mysql://localhost:3306/world";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "Rishi@5480";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	}}
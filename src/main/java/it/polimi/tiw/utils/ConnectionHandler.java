package it.polimi.tiw.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class ConnectionHandler {
	/**
	 * Creates a new connection to the database.
	 * 
	 * @param context the servlet context
	 * @return the newly created connection
	 * @throws UnavailableException
	 */
	public static Connection getConnection(ServletContext context) throws UnavailableException {
		Connection connection;
		
		try {
			// gets the needed information
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			
			// searches for the driver
			// throws exception if the driver is not found
			Class.forName(driver);
			
			// creates the connection
			connection = DriverManager.getConnection(url, user, password);
			
		} catch (ClassNotFoundException e) {
			// driver not found
			throw new UnavailableException("Cannot load database drivers");			
		} catch (SQLException e) {
			// database connection failed
			throw new UnavailableException("Could not get database connection");
		}
		
		return connection;
	}
	
	/**
	 * Closes the given connection.
	 * 
	 * @param connection the connection to be closed
	 * @throws SQLException
	 */
	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}

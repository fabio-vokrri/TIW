package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {
	private final Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Returns the user with the given credentials
	 * @param username the user name
	 * @param email the user email
	 * @param password the user password
	 * @return the corresponding user
	 * @throws SQLException if no user found with those credentials
	 */
	public User logIn(String username, String password) throws SQLException {
		String query = "SELECT email FROM users WHERE username = ? AND password = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setString(1, username);
		statement.setString(2, password);
		
		ResultSet result = statement.executeQuery();
		
		// no user found
		if (!result.isBeforeFirst())
			return null;
		
		result.next();
		return new User(
			username,
			result.getString("email"),
			password
		);
	}
	
	/**
	 * Check if the given username already exists in the database.
	 * @param username the username to be checked
	 * @return 
	 * @throws SQLException
	 */
	public boolean checkRegistration(String username) throws SQLException {
		String query = "SELECT * FROM users WHERE username = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setString(1, username);
		
		ResultSet result = statement.executeQuery();
		
		return result.isBeforeFirst();
	}
	
	/**
	 * Registers a new user with the given credentials
	 * @param username the user name
	 * @param email the user email
	 * @param password the user password
	 * @return the id of the registered user
	 * @throws SQLException if something goes wrong with the registration
	 */
	public void registerUser(String username, String email, String password) throws SQLException {
		String query = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setString(1, username);
		statement.setString(2, email);
		statement.setString(3, password);
		
		int code = statement.executeUpdate();		
		if (code == 0) throw new SQLException("User registration failed: no rows added to table");
	}
	
	/**
	 * Gets the user by the given id
	 * @param username the user name to be found
	 * @return the user with the given id or null
	 * @throws SQLException if no user is found
	 */
	public User getByUserName(String userName) throws SQLException {
		String query = "SELECT email, password FROM users WHERE username = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setString(1, userName);
		
		ResultSet result = statement.executeQuery();
		
		// no user found
		if (!result.isBeforeFirst())
			return null;
		
		result.next();
		return new User(
			userName,
			result.getString("email"),
			result.getString("password")
		);
	}
}












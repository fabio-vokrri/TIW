package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CheckRegistration")
@MultipartConfig
public class CheckRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();    	
    	connection = ConnectionHandler.getConnection(context);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String userName = request.getParameter("username");
		String password1 = request.getParameter("password1");
		String password2 = request.getParameter("password2");
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
				
		if (
				userName == null || email == null || password1 == null || password2 == null ||
				userName.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()
		) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid credentials");
			
			return;
		}
		
		// checks email validity
		Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		if (!emailPattern.matcher(email).matches()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid email");
			
			return;
		}
		
		// checks matching passwords
		if (!password1.equals(password2)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Inserted passwords do not match");
			
			return;
		}
		
		UserDAO userDao = new UserDAO(connection);
		try {
			// checks if user name already exists
			if (userDao.checkRegistration(userName)) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				response.getWriter().println("Username already exists");
				
				return;
			}			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Something went wrong while validating credentials on registration");
			
			return;
		}
		
		// if all the above is passed, registers user and redirects to login page
		try {
			userDao.registerUser(userName, email, password1);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Something went wrong while registering user");
		}
	}
	
	@Override
	public void destroy() {
		try {
    		ConnectionHandler.closeConnection(connection);
    	} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

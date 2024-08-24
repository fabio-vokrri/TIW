package it.polimi.tiw.controllers;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateDocument")
@MultipartConfig
public class CreateDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();    	
    	connection = ConnectionHandler.getConnection(context);  
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String summary = request.getParameter("summary");
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		
		Integer parentId;		
		try {
			parentId = Integer.parseInt(request.getParameter("parentId"));
		} catch (NullPointerException|NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid directory");
			
			return;
		}
		
		if (
			name == null || type == null || summary == null ||
			name.isEmpty() || type.isEmpty()
		) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid inputs");
			
			return;
		}
		
		User user = (User) session.getAttribute("user");
		DocumentDAO documentDAO = new DocumentDAO(connection);		
		try {
			Date now = new Date();			
			documentDAO.addNewDocument(parentId, user.getUserName(), name, type, summary, new Timestamp(now.getTime()));
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Something went wrong while inserting new document in database");
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

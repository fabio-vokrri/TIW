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
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateFolder")
@MultipartConfig
public class CreateFolder extends HttpServlet {
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
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		
		String name = request.getParameter("name");
		if (name == null || name.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid folder name");
			
			return;
		}
		
		Integer parentId;
		try {
			String parameter = request.getParameter("parentId");
			parentId = Integer.parseInt(parameter);
		} catch (NullPointerException|NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid directory");
			
			return;
		}		
		
		User user = (User) session.getAttribute("user");
		FolderDAO folderDao = new FolderDAO(connection);		
		try {
			Date now = new Date();
			folderDao.addNewFolder(parentId, user.getUserName(), name, new Timestamp(now.getTime()));
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Something went wrong while inserting new document in the database");
			
			return;
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

package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/MoveFolder")
public class MoveFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
private Connection connection;
	
	@Override
	public void init() throws ServletException {
		ServletContext context = getServletContext();    	
    	connection = ConnectionHandler.getConnection(context);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		
		Integer sourceFolderId;
		try {
			sourceFolderId = Integer.parseInt(request.getParameter("sourceFolderId"));
		} catch(NumberFormatException|NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid source folder");
			
			return;
		}
		
		Integer destinationFolderId;
		try {
			destinationFolderId = Integer.parseInt(request.getParameter("destinationFolderId"));
		} catch(NumberFormatException|NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid destination folder");
			
			return;
		}
		
		FolderDAO folderDao = new FolderDAO(connection);
		try {
			folderDao.moveFolderToFolder(sourceFolderId, destinationFolderId);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(
				"Something went wrong while moving the specified folder: " + e.getMessage()
			);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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

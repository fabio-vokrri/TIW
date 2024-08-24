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

import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/MoveDocument")
public class MoveDocument extends HttpServlet {
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
		
		Integer documentId;
		try {
			documentId = Integer.parseInt(request.getParameter("documentId"));
		} catch(NumberFormatException|NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid document");
			
			return;
		}
		
		Integer folderId;
		try {
			folderId = Integer.parseInt(request.getParameter("folderId"));
		} catch(NumberFormatException|NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid destination folder");
			
			return;
		}
		
		DocumentDAO documentDao = new DocumentDAO(connection);
		try {
			documentDao.moveDocumentToFolder(documentId, folderId);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Something went wrong while moving the specified document");
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

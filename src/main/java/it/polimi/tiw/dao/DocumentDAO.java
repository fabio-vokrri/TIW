package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Document;

public class DocumentDAO {
	private final Connection connection;
	
	public DocumentDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Document> getChildrenDocumentsOf(int folderId) throws SQLException {
		String query = "SELECT id, username, name, type, summary, date FROM documents WHERE parent_id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, folderId);
		
		ResultSet result = statement.executeQuery();
		
		List<Document> documents = new ArrayList<>();
		while (result.next()) {
			Document document = new Document(
				result.getInt("id"),
				folderId,
				result.getString("username"),
				result.getString("name"),
				result.getString("type"),
				result.getString("summary"),
				result.getTimestamp("date")
			);
			
			documents.add(document);
		}
		
		return documents;
	}

	public Document getById(int id) throws SQLException {
		String query = "SELECT parent_id, username, name, type, summary, date FROM documents WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, id);
		
		ResultSet result = statement.executeQuery();
		
		if (!result.isBeforeFirst())
			return null;
		
		result.next();
		return new Document(
			id,
			result.getInt("parent_id"),
			result.getString("username"),
			result.getString("name"),
			result.getString("type"),
			result.getString("summary"),
			result.getTimestamp("date")
		);
	}

	public boolean moveDocumentToFolder(int documentId, int newFolderId) throws SQLException {
		String query = "UPDATE documents SET parent_id = ? WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, newFolderId);
		statement.setInt(2, documentId);
		
		int code = statement.executeUpdate();
		return code != 0;
	}
	
	public void addNewDocument(int parentId, String userName, String name, String type, String summary, Timestamp date) throws SQLException {
		String query = "INSERT INTO documents(parent_id, username, name, type, summary, date) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, parentId);
		statement.setString(2, userName);
		statement.setString(3, name);
		statement.setString(4, type);
		statement.setString(5, summary);
		statement.setTimestamp(6, date);
		
		int code = statement.executeUpdate();
		if (code == 0) throw new SQLException("Failed to insert new document");
	}

	public boolean deleteDocument(int documentId) throws SQLException {
		String query = "DELETE FROM documents WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, documentId);
		int code = statement.executeUpdate();
		
		// delete statement successful if code != 0
		return code != 0;
	}
}

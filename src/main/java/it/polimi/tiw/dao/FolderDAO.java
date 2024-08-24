package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;

public class FolderDAO {
	private final Connection connection;
	
	public FolderDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Folder getById(int folderId) throws SQLException {
		String query = "SELECT parent_id, username, name, date FROM folders WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, folderId);
		
		ResultSet result = statement.executeQuery();
		
		if (!result.isBeforeFirst())
			return null;
		
		result.next();
		Folder folder = new Folder(
			folderId,
			result.getInt("parent_id"),
			result.getString("username"),
			result.getString("name"),
			result.getTimestamp("date")
		);
		
		folder.setSubFolders(getChildrenFoldersOf(folderId, folder.getUserName()));
		folder.setDocuments(getChildrenDocumentsOf(folderId, folder.getUserName()));
		return folder;
	}

	public List<Folder> getFirstlevelFoldersOf(String userName) throws SQLException {
		String query = "SELECT id, name, date FROM folders WHERE parent_id IS NULL AND username = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setString(1, userName);
		
		ResultSet result = statement.executeQuery();
		
		List<Folder> folders = new ArrayList<>();
		while (result.next()) {
			Folder folder = new Folder(
				result.getInt("id"),
				-1, // no parent folder
				userName,
				result.getString("name"),
				result.getTimestamp("date")
			);
			
			folder.setSubFolders(getChildrenFoldersOf(folder.getId(), userName));
			folder.setDocuments(getChildrenDocumentsOf(folder.getId(), userName));
			folders.add(folder);
		}
		
		return folders;
	}
	
	public List<Folder> getChildrenFoldersOf(int folderId, String userName) throws SQLException {
		String query = "SELECT id, name, date FROM folders WHERE parent_id = ? AND username = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, folderId);
		statement.setString(2, userName);
		
		ResultSet result = statement.executeQuery();
		
		List<Folder> folders = new ArrayList<>();
		while (result.next()) {
			Folder folder = new Folder(
				result.getInt("id"),
				folderId,
				userName,
				result.getString("name"),
				result.getTimestamp("date")				
			);
			
			folder.setSubFolders(getChildrenFoldersOf(folder.getId(), userName));
			folder.setDocuments(getChildrenDocumentsOf(folder.getId(), userName));
			folders.add(folder);
		}
		
		return folders;
	}
	
	public List<Document> getChildrenDocumentsOf(int folderId, String userName) throws SQLException {
		String query = "SELECT id, name, type, summary, date FROM documents WHERE parent_id = ? AND username = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, folderId);
		statement.setString(2, userName);
		
		ResultSet result = statement.executeQuery();
		
		List<Document> documents = new ArrayList<>();		
		while (result.next()) {
			Document document = new Document(
				result.getInt("id"), 
				folderId, 
				userName,
				result.getString("name"), 
				result.getString("type"),
				result.getString("summary"), 
				result.getTimestamp("date")
			);
			
			documents.add(document);
		}
		
		return documents;		
	}
	
	public void addNewFolder(int parentId, String userName, String name, Timestamp date) throws SQLException {
		String query = "INSERT INTO folders(parent_id, username, name, date) VALUES (?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		
		if (parentId == -1) statement.setNull(1, java.sql.Types.INTEGER);
		else statement.setInt(1, parentId);		
		statement.setString(2, userName);
		statement.setString(3, name);
		statement.setTimestamp(4, date);
		
		int code = statement.executeUpdate();
		if (code == 0) throw new SQLException("Failed to insert new folder");
	}
	
	public boolean deleteFolder(int folderId) throws SQLException {
		String query = "DELETE FROM folders WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, folderId);
		int code = statement.executeUpdate();
		
		return code != 0;
	}
	
	public boolean moveFolderToFolder(int sourceFolder, int destinationFolder) throws SQLException {
		String query = "UPDATE folders SET parent_id = ? WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		if(destinationFolder == -1) statement.setNull(1, java.sql.Types.INTEGER);
		else statement.setInt(1, destinationFolder);
		statement.setInt(2, sourceFolder);
		
		int code = statement.executeUpdate();
		return code != 0;
	}
}

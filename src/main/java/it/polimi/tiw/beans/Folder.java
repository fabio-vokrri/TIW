package it.polimi.tiw.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Folder {
	private int id;
	private int parentId;

	private String userName;
	private String name;
	private Date date;
	private List<Folder> subFolders;
	private List<Document> documents;

	public Folder(int id, int parentId, String userName, String name, Date date) {
		this.id = id;
		this.parentId = parentId;
		this.userName = userName;
		this.name = name;
		this.date = date;
		this.subFolders = new ArrayList<>();
		this.documents = new ArrayList<>();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setOwnerId(String userName) {
		this.userName = userName;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean getIsInRoot() {
		return this.parentId == -1;
	}
	
	public List<Folder> getSubFolders() {
		return this.subFolders;
	}

	public void addSubFolder(Folder subFolder) {
		this.subFolders.add(subFolder);
	}
	
	public void setSubFolders(List<Folder> subFolders) {
		this.subFolders = subFolders;
	}
	
	
	public List<Document> getDocuments() {
		return this.documents;
	}
	
	public void addDocument(Document document) {
		this.documents.add(document);
	}
	
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
}

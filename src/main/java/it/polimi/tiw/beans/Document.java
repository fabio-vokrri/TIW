package it.polimi.tiw.beans;

import java.util.Date;

public class Document {
	private int id;
	private int parentId;
	
	private String userName;
	private String name;
	private String type;	
	private String summary;	
	private Date date;
	
	public Document(int id, int parentId, String userName, String name, String type, String summary, Date date) {
		this.id = id;
		this.parentId = parentId;
		this.userName = userName;
		this.name = name;
		this.type = type;
		this.summary = summary;
		this.date = date;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
}

package com.cloudnotes;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable
public class Note {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String id;
	@Persistent private String description;
	@Persistent private String emailAddress;

	public Note() {
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setId(String idIn) {
		this.id = idIn;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}

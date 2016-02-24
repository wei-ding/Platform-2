package org.clinical3PO.common.security.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AccumuloRole implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private int userId;
	private int id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
			return new ToStringBuilder(this)
			.append("Role", name)
			.toString();
	}

}
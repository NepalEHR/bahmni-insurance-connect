package org.bahmni.insurance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class SupportingInfoLineValueAttachmentRequest {

	@SerializedName("title")
	@Expose
	private String title;

	@SerializedName("creation")
	@Expose
	private Date creation;

	@SerializedName("hash")
	@Expose
	private byte[] hash;

	@SerializedName("data")
	@Expose
	private byte[] data;

	@SerializedName("contentType")
	@Expose
	private String contentType;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}

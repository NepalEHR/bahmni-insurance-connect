package org.bahmni.insurance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class SupportingInfoLineCodingRequest {

	@SerializedName("coding")
	@Expose
	private List<CodingRequest> coding;

	@SerializedName("text")
	@Expose
	private String text;

	public List<CodingRequest> getCoding() {
		return coding;
	}

	public void setCoding(List<CodingRequest> coding) {
		this.coding = coding;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

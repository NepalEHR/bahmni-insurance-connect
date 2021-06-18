package org.bahmni.insurance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CodingRequest {

	@SerializedName("code")
	@Expose
	private String code;

	@SerializedName("display")
	@Expose
	private String display;


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
}

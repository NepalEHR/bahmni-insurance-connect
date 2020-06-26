package org.bahmni.insurance.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "uuid", "startDateTime", "stopDateTime", "visitType", "admissionDetails", "dischargeDetails" })
public class Patient {

	@JsonProperty("uuid")
	private String uuid;
	@JsonProperty("startDateTime")
	private Long startDateTime;
	@JsonProperty("stopDateTime")
	private Long stopDateTime;
	@JsonProperty("visitType")
	private String visitType;
	@JsonProperty("admissionDetails")
	private Object admissionDetails;
	@JsonProperty("dischargeDetails")
	private Object dischargeDetails;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@JsonProperty("uuid")
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@JsonProperty("startDateTime")
	public Long getStartDateTime() {
		return startDateTime;
	}

	@JsonProperty("startDateTime")
	public void setStartDateTime(Long startDateTime) {
		this.startDateTime = startDateTime;
	}

	@JsonProperty("stopDateTime")
	public Long getStopDateTime() {
		return stopDateTime;
	}

	@JsonProperty("stopDateTime")
	public void setStopDateTime(Long stopDateTime) {
		this.stopDateTime = stopDateTime;
	}

	@JsonProperty("visitType")
	public String getVisitType() {
		return visitType;
	}

	@JsonProperty("visitType")
	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	@JsonProperty("admissionDetails")
	public Object getAdmissionDetails() {
		return admissionDetails;
	}

	@JsonProperty("admissionDetails")
	public void setAdmissionDetails(Object admissionDetails) {
		this.admissionDetails = admissionDetails;
	}

	@JsonProperty("dischargeDetails")
	public Object getDischargeDetails() {
		return dischargeDetails;
	}

	@JsonProperty("dischargeDetails")
	public void setDischargeDetails(Object dischargeDetails) {
		this.dischargeDetails = dischargeDetails;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
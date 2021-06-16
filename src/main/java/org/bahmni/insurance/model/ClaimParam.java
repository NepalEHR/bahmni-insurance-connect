package org.bahmni.insurance.model;

import java.math.BigDecimal;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimParam {

	@SerializedName("patientUUID")
	@Expose
	private String patientUUID;
	
	@SerializedName("visitUUID")
	@Expose
	private String visitUUID;
	
	@SerializedName("claimId")
	@Expose
	private String claimId;
	
	@SerializedName("insureeId")
	@Expose
	private String insureeId;

	@SerializedName("item")
	@Expose
	private List<ClaimLineItemRequest> item;

	@SerializedName("supportingInfo")
	@Expose
	private List<ClaimLineSupportingInfoRequest> supportingInfo;

	@SerializedName("total")
	@Expose
	private BigDecimal total;

	public String getPatientUUID() {
		return patientUUID;
	}

	public void setPatientUUID(String patientUUID) {
		this.patientUUID = patientUUID;
	}

	public String getVisitUUID() {
		return visitUUID;
	}

	public void setVisitUUID(String visitUUID) {
		this.visitUUID = visitUUID;
	}

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public String getInsureeId() {
		return insureeId;
	}

	public void setInsureeId(String insureeId) {
		this.insureeId = insureeId;
	}

	public List<ClaimLineItemRequest> getItem() {
		return item;
	}

	public void setItem(List<ClaimLineItemRequest> item) {
		this.item = item;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<ClaimLineSupportingInfoRequest> getSupportingInfo() {
		return supportingInfo;
	}

	public void setSupportingInfo(List<ClaimLineSupportingInfoRequest> supportingInfo) {
		this.supportingInfo = supportingInfo;
	}
}
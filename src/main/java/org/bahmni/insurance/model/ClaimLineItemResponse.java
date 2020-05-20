package org.bahmni.insurance.model;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimLineItemResponse {

	@SerializedName("quantityApproved")
	@Expose
	private BigDecimal quantityApproved;

	@SerializedName("sequence")
	@Expose
	private Integer sequence;

	@SerializedName("status")
	@Expose
	private String status;
	
	@SerializedName("code")
	@Expose
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@SerializedName("rejectedReason")
	@Expose
	private String rejectedReason;

	@SerializedName("totalApproved")
	@Expose
	private BigDecimal totalApproved;

	public BigDecimal getQuantityApproved() {
		return quantityApproved;
	}

	public void setQuantityApproved(BigDecimal quantity) {
		this.quantityApproved = quantity;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRejectedReason() {
		return rejectedReason;
	}

	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}

	public BigDecimal getTotalApproved() {
		return totalApproved;
	}

	public void setTotalApproved(BigDecimal totalApproved) {
		this.totalApproved = totalApproved;
	}

}

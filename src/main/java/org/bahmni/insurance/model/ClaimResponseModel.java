package org.bahmni.insurance.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ClaimResponseModel {

	private String claimId;
	private String claimUUID;
	private BigDecimal claimedTotal;
	private BigDecimal approvedTotal;
	private Date dateProcessed;
	private String rejectionReason;
	private String claimStatus;
	private String paymentType;
	private List<ClaimLineItemResponse> claimLineItems;

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public BigDecimal getClaimedTotal() {
		return claimedTotal;
	}

	public void setClaimedTotal(BigDecimal claimedTotal) {
		this.claimedTotal = claimedTotal;
	}

	public BigDecimal getApprovedTotal() {
		return approvedTotal;
	}

	public void setApprovedTotal(BigDecimal approvedTotal) {
		this.approvedTotal = approvedTotal;
	}

	

	public Date getDateProcessed() {
		return dateProcessed;
	}

	public void setDateProcessed(Date dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public List<ClaimLineItemResponse> getClaimLineItems() {
		return claimLineItems;
	}

	public void setClaimLineItems(List<ClaimLineItemResponse> claimLineItems) {
		this.claimLineItems = claimLineItems;
	}

	public String getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(String claimStatus) {
		this.claimStatus = claimStatus;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getClaimUUID() {
		return claimUUID;
	}

	public void setClaimUUID(String claimUUID) {
		this.claimUUID = claimUUID;
	}

}

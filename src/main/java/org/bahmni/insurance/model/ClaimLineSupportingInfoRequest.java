package org.bahmni.insurance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class ClaimLineSupportingInfoRequest {

	@SerializedName("category")
	@Expose
	private List<SupportingInfoLineCodingRequest> category;

	@SerializedName("valueAttachment")
	@Expose
	private List<SupportingInfoLineValueAttachmentRequest> valueAttachment;

	public List<SupportingInfoLineCodingRequest> getCategory() {
		return category;
	}

	public void setCategory(List<SupportingInfoLineCodingRequest> category) {
		this.category = category;
	}

	public List<SupportingInfoLineValueAttachmentRequest> getValueAttachment() {
		return valueAttachment;
	}

	public void setValueAttachment(List<SupportingInfoLineValueAttachmentRequest> valueAttachment) {
		this.valueAttachment = valueAttachment;
	}
}

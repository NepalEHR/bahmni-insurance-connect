package org.bahmni.insurance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class ClaimLineSupportingInfoRequest {

	@SerializedName("category")
	@Expose
	private SupportingInfoLineCodingRequest category;

	@SerializedName("valueAttachment")
	@Expose
	private SupportingInfoLineValueAttachmentRequest valueAttachment;

	public SupportingInfoLineCodingRequest getCategory() {
		return category;
	}

	public void setCategory(SupportingInfoLineCodingRequest category) {
		this.category = category;
	}

	public SupportingInfoLineValueAttachmentRequest getValueAttachment() {
		return valueAttachment;
	}

	public void setValueAttachment(SupportingInfoLineValueAttachmentRequest valueAttachment) {
		this.valueAttachment = valueAttachment;
	}
}

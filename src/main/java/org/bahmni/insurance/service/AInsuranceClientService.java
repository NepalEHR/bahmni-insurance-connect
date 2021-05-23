package org.bahmni.insurance.service;

import java.net.URISyntaxException;

import org.bahmni.insurance.client.ClientHelperFactory;
import org.bahmni.insurance.model.ClaimResponseModel;
import org.bahmni.insurance.model.ClaimTrackingModel;
import org.bahmni.insurance.model.EligibilityResponseModel;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.ClaimResponse;
import org.hl7.fhir.r4.model.CoverageEligibilityRequest;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.exceptions.FHIRException;
/*import org.openmrs.module.fhir.api.helper.ClientHelper;*/
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public abstract class AInsuranceClientService {
	/*private ClientHelper clientHelper;

	public ClientHelper getClientHelper(String clientType) {
		clientHelper = ClientHelperFactory.createClient(clientType);
		return clientHelper;
	}
*/
	public abstract ClaimResponseModel submitClaim(Claim claimRequest) throws RestClientException, URISyntaxException;

	public abstract EligibilityResponseModel getElibilityResponse(CoverageEligibilityRequest eligbilityRequest)
			throws RestClientException, URISyntaxException, FHIRException;

	public abstract ClaimResponse getClaimStatus(Task claimStatusRequest);

	public abstract String loginCheck();

	public abstract EligibilityResponseModel getDummyEligibilityResponse() throws FHIRException;

	public abstract ClaimTrackingModel getDummyClaimTrack();

	public abstract ClaimResponseModel getDummyClaimResponse(Claim claimRequest);

}

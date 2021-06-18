package org.bahmni.insurance.serviceImpl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bahmni.insurance.AppProperties;
import org.bahmni.insurance.ImisConstants;
import org.bahmni.insurance.exception.FhirFormatException;
import org.bahmni.insurance.model.ClaimLineItemRequest;
import org.bahmni.insurance.model.ClaimLineSupportingInfoRequest;
import org.bahmni.insurance.model.ClaimParam;
import org.bahmni.insurance.model.VisitSummary;
import org.bahmni.insurance.service.AFhirConstructorService;
import org.bahmni.insurance.utils.InsuranceUtils;
import org.bahmni.insurance.validation.FhirInstanceValidator;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Claim.ItemComponent;
import org.hl7.fhir.r4.model.Claim.SupportingInformationComponent;
import org.hl7.fhir.r4.model.CoverageEligibilityRequest.EligibilityRequestStatus;
//import org.hl7.fhir.r4.model.EligibilityRequest;
//import org.hl7.fhir.r4.model.EligibilityRequest.EligibilityRequestStatus;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

@Component
public class FhirConstructorServiceImpl extends AFhirConstructorService {

	@Autowired
	private AppProperties properties;
	
	@Autowired 
	private BahmniOpenmrsApiClientServiceImpl bahmniApiService;

	@Override
	public String getFhirPatient(String name) {
		HttpHeaders headers = createHeaders(properties.openmrsUser, properties.openmrsPassword);
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		return this.getApiClient()
				.exchange(properties.openmrsFhirUrl + "?name=" + name, HttpMethod.GET, entity, String.class).getBody();
	}

	@Override
	public ResponseEntity<String> createFhirPatient(String patientJson) {
		HttpHeaders headers = createHeaders(properties.openmrsUser, properties.openmrsPassword);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Content-Type", "application/fhir+json;q=1.0, application/json+fhir;q=0.9");
		HttpEntity<String> entity = new HttpEntity<String>(patientJson, headers);
		return this.getApiClient().exchange(properties.openmrsFhirUrl + "/", HttpMethod.POST, entity, String.class);
	}

	private HttpHeaders createHeaders(String username, String password) {
		return new HttpHeaders() {
			private static final long serialVersionUID = 1L;
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}

	@Override
	public Claim constructFhirClaimRequest(ClaimParam claimParam) throws IOException {

		Claim claimReq = new Claim();

		// claim number
		List<Identifier> identifierList = new ArrayList<>();
		Identifier identifier2 = new Identifier();
		CodeableConcept codeableConcept2 = new CodeableConcept();
		Coding code2 = new Coding();
		code2.setSystem(ImisConstants.FHIR_VALUESET_SYSTEM);
		code2.setCode(ImisConstants.FHIR_CODE_FOR_IMIS_CLAIM_CODE_TYPE);
		codeableConcept2.addCoding(code2);
		identifier2.setType(codeableConcept2);
		identifier2.setUse(IdentifierUse.USUAL);
		identifier2.setValue(claimParam.getClaimId());
		identifierList.add(identifier2);
		claimReq.setIdentifier(identifierList);

		// Insuree patient
		Reference patientReference = new Reference();
		patientReference.setReference("Patient/" + claimParam.getInsureeId());
		claimReq.setPatient(patientReference);

		// BillablePeriod
		
		Period period = new Period();
		VisitSummary visitDetails = bahmniApiService.getVisitDetail(claimParam.getVisitUUID());
		period.setStart(new Date( visitDetails.getStartDateTime()));
		period.setEnd(new Date( visitDetails.getStopDateTime()));
		claimReq.setBillablePeriod(period);
		claimReq.setCreated(new Date());

		// Diagnosis 
		Claim.DiagnosisComponent diagnosisComponent = new Claim.DiagnosisComponent();
		CodeableConcept concept = new CodeableConcept();
		Coding code = new Coding();
		code.setCode("A09"); // TODO: extract from openmrs api
		concept.addCoding(code);
		diagnosisComponent.setDiagnosis(concept);
		
		diagnosisComponent.setSequence(1);
		
		CodeableConcept conceptType = new CodeableConcept();
		conceptType.setText("icd_0"); //TODO: remove hardcoded
		diagnosisComponent.addType(conceptType);
		

		claimReq.addDiagnosis(diagnosisComponent);
		
		// Items/services for claims

		List<ItemComponent> listItemComponent = populateClaimableItems(claimParam.getItem());
		claimReq.setItem(listItemComponent);

		// "enterer"
		Reference entererReference = new Reference();
		entererReference.setReference("Practitioner/" + properties.openImisEntererId);
		claimReq.setEnterer(entererReference);

		// "Facility"
		Reference facilityReference = new Reference();
		facilityReference.setReference("Location/" + properties.openImisHFCode);
		claimReq.setFacility(facilityReference);
		claimReq.setId(claimParam.getClaimId());
		
		Money total  = new Money();
		total.setValue(claimParam.getTotal());
		claimReq.setTotal(total);
		
		
		CodeableConcept typeValue =  new CodeableConcept();
		typeValue.setText(visitDetails.getVisitType()) ; 
		claimReq.setType(typeValue);

		//Supporting Info
		List<SupportingInformationComponent> listSupportingInfo = populateClaimableSupportingInfo(claimParam.getSupportingInfo());
		claimReq.setSupportingInfo(listSupportingInfo);

		return claimReq;
	}

	private List<ItemComponent> populateClaimableItems(List<ClaimLineItemRequest> listItem) {
		List<ItemComponent> listItemComponent = new ArrayList<>();
		for (ClaimLineItemRequest claimItem : listItem) {
			ItemComponent itemComponent = new ItemComponent();
			itemComponent.setSequence(claimItem.getSequence());

			CodeableConcept codeConceptCategory = new CodeableConcept();
			codeConceptCategory.setText(claimItem.getCategory());
			itemComponent.setCategory(codeConceptCategory);

			SimpleQuantity simpleQuantity = new SimpleQuantity();
			simpleQuantity.setValue(claimItem.getQuantity());
			itemComponent.setQuantity(simpleQuantity);

//			CodeableConcept codeConceptService = new CodeableConcept();
//			codeConceptService.setText(claimItem.getCode());
//			itemComponent.setServiced(codeConceptService);

			Money value = new Money();
			value.setValue(claimItem.getUnitPrice());
			itemComponent.setUnitPrice(value);
			listItemComponent.add(itemComponent);
		}
		return listItemComponent;
	}

	private List<SupportingInformationComponent> populateClaimableSupportingInfo(List<ClaimLineSupportingInfoRequest> listSupportingInfo) {
		List<SupportingInformationComponent> listSupportingInfoComponent = new ArrayList<>();
		int i = 0,j=0;
		for (ClaimLineSupportingInfoRequest claimsupportingInfo : listSupportingInfo) {
			SupportingInformationComponent supportingInfoComponent = new SupportingInformationComponent();
			CodeableConcept codeConceptCategory = new CodeableConcept();
			codeConceptCategory.setText(claimsupportingInfo.getCategory().get(i).getText();
			Coding categoryCoding = new Coding();
			categoryCoding.setDisplay(claimsupportingInfo.getCategory().get(i).getCoding().get(j).getDisplay());
			categoryCoding.setCode(claimsupportingInfo.getCategory().get(i).getCoding().get(j).getCode());
			codeConceptCategory.addCoding(categoryCoding);
			supportingInfoComponent.setCategory(codeConceptCategory);

			Attachment doc = new Attachment();
			doc.setContentType(claimsupportingInfo.getContentType());
			doc.setData(claimsupportingInfo.getData());
			doc.setCreation(claimsupportingInfo.getCreation());
			doc.setHash(claimsupportingInfo.getHash());
			doc.setTitle(claimsupportingInfo.getTitle());
			supportingInfoComponent.setValue(doc);


			listSupportingInfoComponent.add(supportingInfoComponent);
			i++;
			j++;
		}
		return listSupportingInfoComponent;
	}

	@Override
	public CoverageEligibilityRequest constructFhirEligibilityRequest(String insuranceID) throws IOException {

		CoverageEligibilityRequest eligibilityRequest = new CoverageEligibilityRequest();

//		List<Identifier> identifierList = new ArrayList<>();
//		Identifier identifier = new Identifier();
//		identifier.setSystem("SenderID");
//		identifier.setValue(insuranceID);
//		identifierList.add(identifier);
//		eligibilityRequest.setIdentifier(identifierList);
//
		eligibilityRequest.setStatus(EligibilityRequestStatus.ACTIVE);

		Reference patientReference = new Reference();
		patientReference.setReference("Patient/" + insuranceID);
		eligibilityRequest.setPatient(patientReference);

//		Reference referenceOrg = new Reference();
//		referenceOrg.setReference("Organization/1");
//		eligibilityRequest.setOrganization(referenceOrg);
//
//		Reference referenceInsurer = new Reference();
//		referenceInsurer.setReference("Organization/2");
//		eligibilityRequest.setInsurer(referenceInsurer);



		return eligibilityRequest;
	}

	@Override
	public Task constructFhirClaimTrackRequest(String insuranceID) throws IOException {
		Task claimTracking = new Task();

		List<Identifier> identifierList = new ArrayList<>();
		Identifier identifier = new Identifier();
		identifier.setSystem("SenderID");
		identifier.setValue(insuranceID);
		identifierList.add(identifier);
		claimTracking.setIdentifier(identifierList);

		claimTracking.setStatus(TaskStatus.READY);

		Reference referenceOrg = new Reference();
		referenceOrg.setReference("Organization/1");
		claimTracking.setOwner(referenceOrg);

		return claimTracking;
	}

	@Override
	public boolean validateRequest(String eligibilityRequestValidation) throws IOException {
		FhirContext ctx = FhirContext.forR4();

		FhirValidator validator = ctx.newValidator();
		FhirInstanceValidator instanceValidator = new FhirInstanceValidator();
		validator.registerValidatorModule(instanceValidator);
		instanceValidator.setAnyExtensionsAllowed(true);

		ValidationResult result = validator.validateWithResult(eligibilityRequestValidation);

		if (!result.isSuccessful()) {
			String errorMsg = "";
			for (SingleValidationMessage next : result.getMessages()) {
				errorMsg = next.getSeverity() + " - " + next.getLocationString() + " - " + next.getMessage();
			}

			throw new FhirFormatException(errorMsg);
		}

		return result.isSuccessful();
	}

}

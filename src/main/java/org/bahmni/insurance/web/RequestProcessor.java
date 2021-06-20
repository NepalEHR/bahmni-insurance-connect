package org.bahmni.insurance.web;

import static org.apache.log4j.Logger.getLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bahmni.insurance.AppProperties;
import org.bahmni.insurance.ImisConstants;
import org.bahmni.insurance.client.RestTemplateFactory;
import org.bahmni.insurance.dao.FhirResourceDaoServiceImpl;
import org.bahmni.insurance.dao.IFhirResourceDaoService;
import org.bahmni.insurance.model.ClaimParam;
import org.bahmni.insurance.model.ClaimResponseModel;
import org.bahmni.insurance.model.ClaimTrackingModel;
import org.bahmni.insurance.model.EligibilityResponseModel;
import org.bahmni.insurance.model.FhirResourceModel;
import org.bahmni.insurance.service.AFhirConstructorService;
import org.bahmni.insurance.service.FInsuranceServiceFactory;
import org.bahmni.insurance.service.IOpenmrsOdooService;
import org.bahmni.insurance.serviceImpl.FhirConstructorServiceImpl;
import org.bahmni.insurance.serviceImpl.OpenmrsOdooServiceImpl;
import org.bahmni.insurance.utils.InsuranceUtils;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.ClaimResponse;
import org.hl7.fhir.r4.model.CoverageEligibilityRequest;
import org.hl7.fhir.r4.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;

@RestController
public class RequestProcessor {

	private final Logger logger = getLogger(RequestProcessor.class);

	private final AFhirConstructorService fhirConstructorService;
	private final IOpenmrsOdooService odooService;
	private final IFhirResourceDaoService fhirDaoService;
	private final FInsuranceServiceFactory insuranceImplFactory;
	private final IParser FhirParser = FhirContext.forR4().newJsonParser();

	private final AppProperties properties;

	@Autowired
	public RequestProcessor(FhirConstructorServiceImpl fhirConstructorServiceImpl,
			OpenmrsOdooServiceImpl openmrsOdooServiceImpl, FhirResourceDaoServiceImpl fhirServiceImpl,
			FInsuranceServiceFactory insuranceImplFactory, RestTemplateFactory restFactory, AppProperties props) {
		this.fhirConstructorService = fhirConstructorServiceImpl;
		this.odooService = openmrsOdooServiceImpl;
		this.fhirDaoService = fhirServiceImpl;
		this.insuranceImplFactory = insuranceImplFactory;
		this.properties = props;
	}

	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
		return new ResponseEntity<Object>("Access denied ", new HttpHeaders(), HttpStatus.FORBIDDEN);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get/eligibilityResponse/{InsureeId}", produces = "application/json")
	@ResponseBody
	public String getEligibilityResponse(HttpServletResponse response, @PathVariable("InsureeId") String InsureeId)
			throws DataFormatException, IOException, URISyntaxException {
		logger.debug("eligibityResponse");
		CoverageEligibilityRequest eligReq = fhirConstructorService.constructFhirEligibilityRequest(InsureeId);
		fhirConstructorService.validateRequest(FhirParser.encodeResourceToString(eligReq));
		EligibilityResponseModel eligibilityResponse = insuranceImplFactory
				.getInsuranceServiceImpl(ImisConstants.OPENIMIS_FHIR, properties).getElibilityResponse(eligReq);

		/*
		 * EligibilityResponseModel eligibilityResponse =
		 * insuranceImplFactory.getInsuranceServiceImpl(ImisConstants.OPENIMIS_FHIR,
		 * properties).getElibilityResponse(eligReq);
		 */
		return InsuranceUtils.mapToJson(eligibilityResponse);
		//return gson.toJson(eligibilityResponse);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/submit/claim", produces = "application/json")
	@ResponseBody
	public ClaimResponseModel submitClaim(HttpServletResponse response, @RequestBody ClaimParam claimParams)
			throws RestClientException, URISyntaxException, DataFormatException, IOException {
		Claim claimRequest = fhirConstructorService.constructFhirClaimRequest(claimParams);

		String claimReqStr = FhirParser.encodeResourceToString(claimRequest);
		if(properties.saveClaimResource) {
			fhirDaoService.insertFhirResource(claimReqStr, ImisConstants.FHIR_RESOURCE_TYPE.CLAIM.getValue() );
		}
		fhirConstructorService.validateRequest(claimReqStr);

		ClaimResponseModel claimResponseModel = insuranceImplFactory
				.getInsuranceServiceImpl(ImisConstants.OPENIMIS_FHIR, properties).submitClaim(claimRequest);

		return claimResponseModel;

	}

	@RequestMapping(method = RequestMethod.POST, value = "get/claim/response", produces = "application/json")
	@ResponseBody
	public String getClaimResponse(HttpServletResponse response, @RequestBody ClaimParam claimParams)
			throws IOException {
		Claim claimRequest = fhirConstructorService.constructFhirClaimRequest(claimParams);
		fhirConstructorService.validateRequest(FhirParser.encodeResourceToString(claimRequest));
		ClaimResponseModel claimResponseModel = insuranceImplFactory
				.getInsuranceServiceImpl(ImisConstants.OPENIMIS_FHIR, properties).getDummyClaimResponse(claimRequest);
		return InsuranceUtils.mapToJson(claimResponseModel);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/get/claimTracking/{claimId}", produces = "application/json")
	@ResponseBody
	public String getClaimTrackingStatus(HttpServletResponse response, @PathVariable("claimId") String claimId)
			throws IOException {
		Task claimTrackTask = fhirConstructorService.constructFhirClaimTrackRequest(claimId);
		ClaimTrackingModel claimTracking = insuranceImplFactory
				.getInsuranceServiceImpl(ImisConstants.OPENIMIS_FHIR, properties).getDummyClaimTrack();
		logger.debug("ClaimTracking model == " + InsuranceUtils.mapToJson(claimTracking));
		return InsuranceUtils.mapToJson(claimTracking);

	}

	@RequestMapping(path = "/openIMIS/login")
	@ResponseBody
	public String checkLogin(HttpServletResponse response) throws RestClientException, URISyntaxException {
		logger.debug("checkLogin");
		return insuranceImplFactory.getInsuranceServiceImpl(ImisConstants.OPENIMIS_FHIR, properties).loginCheck();
	}

	@RequestMapping(path = "/get/fhir/claims")
	@ResponseBody
	public List<FhirResourceModel> getFhirClaim() {
		return fhirDaoService.findAll();
	}

/*	@RequestMapping(path = "/add/fhir/claim")
	@ResponseBody
	public int addFhirClaim() {
		return fhirDaoService.insertFhirResource();
	}*/

	@RequestMapping(path = "/get/fhir/claim/id")
	@ResponseBody
	public List<String> getFhirClaimId() {
		return fhirDaoService.getClaimId();

	}

	@RequestMapping(method = RequestMethod.GET, value = "/patient/{name}", produces = "application/json")
	@ResponseBody
	public String retrievePatientByName(HttpServletResponse response, @PathVariable("name") String name) {
		logger.debug("retreivePatient : ");
		return fhirConstructorService.getFhirPatient(name);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/patient", produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> generatePatient(HttpServletResponse response, @RequestBody String personJson) {
		logger.debug("generatePatient : ");
		return fhirConstructorService.createFhirPatient(personJson);
	}

	/*
	 * @RequestMapping(path = "/odercost") public void
	 * getOrderCost(HttpServletResponse response) { logger.debug("getOrderCost");
	 * System.out.println(odooService.getOrderCost(
	 * "9065024b-9499-4c9b-9a2f-a53f703be2aa"));
	 * 
	 * }
	 */

}

package org.bahmni.insurance.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.bahmni.insurance.AppProperties;
import org.bahmni.insurance.ImisConstants;
import org.bahmni.insurance.client.RestTemplateFactory;
import org.bahmni.insurance.dao.FhirResourceDaoServiceImpl;
import org.bahmni.insurance.model.EligibilityParam;
import org.bahmni.insurance.model.EligibilityResponseModel;
import org.bahmni.insurance.model.Insurance;
import org.bahmni.insurance.service.AFhirConstructorService;
import org.bahmni.insurance.service.FInsuranceServiceFactory;
import org.bahmni.insurance.serviceImpl.FhirConstructorServiceImpl;
import org.bahmni.insurance.serviceImpl.OpenmrsOdooServiceImpl;
import org.hl7.fhir.dstu3.model.EligibilityRequest;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Controller
public class BahmniUIController {

	private final FInsuranceServiceFactory insuranceImplFactory;
	private final AFhirConstructorService fhirConstructorService;
	private final AppProperties properties;
	private final IParser FhirParser = FhirContext.forDstu3().newJsonParser();


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@Autowired
	public BahmniUIController(FhirConstructorServiceImpl fhirConstructorServiceImpl,
			OpenmrsOdooServiceImpl openmrsOdooServiceImpl, FhirResourceDaoServiceImpl fhirServiceImpl,
			FInsuranceServiceFactory insuranceImplFactory, RestTemplateFactory restFactory,
			AFhirConstructorService fhirConstructorService, AppProperties props) {
		this.insuranceImplFactory = insuranceImplFactory;
		this.properties = props;
		this.fhirConstructorService = fhirConstructorService;

		// this.restFactory = restFactory;
	}

	@RequestMapping(value="/add-info", method = RequestMethod.GET)
	public String addInfo(Insurance insurance) {
		
		return "add-info";
	}
	
	@RequestMapping(value="/add-info", method = RequestMethod.POST)
	public String showWelcomePage(@ModelAttribute @Valid Insurance insurance, BindingResult bindingResult, Model model,
			@RequestParam String nhisNumber) throws IOException, RestClientException, FHIRException, URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		if (bindingResult.hasErrors()) {
			System.out.println("BINDING RESULT ERROR");
			model.addAttribute("error", bindingResult.getAllErrors());
			return "add-info";
		} else {
			EligibilityRequest eligRequest = fhirConstructorService.constructFhirEligibilityRequest(nhisNumber);
			EligibilityResponseModel eligibilityResponse = insuranceImplFactory.
					getInsuranceServiceImpl(ImisConstants.OPENIMIS_FHIR, properties).checkEligibility(eligRequest);
			// String nhisId = eligibilityResponse.getNhisId();
			String patientId = eligibilityResponse.getPatientId();
			BigDecimal benefitBalance = eligibilityResponse.getEligibilityBalance().get(0).getBenefitBalance();
			String code = eligibilityResponse.getEligibilityBalance().get(0).getCategory();

			model.addAttribute("patientId", patientId);
			model.addAttribute("benefitBalance", benefitBalance);
			model.addAttribute("code", code);
			model.addAttribute("insurance", insurance);
			model.addAttribute("nhisNumber", insurance.getNhisNumber());

			return "index";

		}
	}

}

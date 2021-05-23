package org.bahmni.insurance.test.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.bahmni.insurance.model.ClaimParam;
import org.bahmni.insurance.serviceImpl.FhirConstructorServiceImpl;
import org.bahmni.insurance.test.AbstractWebTest;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.CoverageEligibilityRequest;
import org.hl7.fhir.r4.model.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestFhirConstructorService extends AbstractWebTest {

	public TestFhirConstructorService() {
		super();
	}

	@Autowired
	FhirConstructorServiceImpl fhirConstructorServiceImpl;

	@Test
	public void constructFhirClaimRequestTest() throws Exception {
		String claimParamJson = "{\r\n" + "	\"patientUUID\":\"123123123avfa21\",\r\n"
				+ "	\"visitUUID\":\"1231231231123212\",\r\n" + "	\"claimId\": \"123\",\r\n"
				+ "	\"insureeId\": \"Patient123\",\r\n" + "	\"item\": [\r\n" + "		{\r\n"
				+ "		\"category\": \"item\",\r\n" + "		\"quantity\": 10,\r\n" + "		\"sequence\": 1,\r\n"
				+ "		\"service\": \"ICode\",\r\n" + "		\"unitPrice\": 20,\r\n"
				+ "		\"totalClaimed\":30,\r\n" + "		\"status\":\"Aproved\",\r\n"
				+ "		\"rejectedReason\":\"Dont know\",\r\n" + "		\"totalApproved\":20\r\n" + "      },\r\n"
				+ "	  {\r\n" + "		\"category\": \"service\",\r\n" + "		\"quantity\": 20,\r\n"
				+ "		\"sequence\": 1,\r\n" + "		\"service\": \"SCode\",\r\n" + "		\"unitPrice\": 20,\r\n"
				+ "		\"totalClaimed\":20,\r\n" + "		\"status\":\"Aproved\",\r\n"
				+ "		\"rejectedReason\":\"Dont know\",\r\n" + "		\"totalApproved\":20\r\n" + "      }\r\n"
				+ "	],\r\n" + "	\"total\": \"40\"\r\n" + "\r\n" + "}";

		Claim claimReqSample = fhirConstructorServiceImpl
				.constructFhirClaimRequest(gson.fromJson(claimParamJson, ClaimParam.class));
		String claimRequestStr = FhirParser.encodeResourceToString(claimReqSample);

		assertTrue(fhirConstructorServiceImpl.validateRequest(claimRequestStr));

	}

	@Test
	public void constructFhirEligRequestTest() throws IOException {
		CoverageEligibilityRequest eligReqSample = fhirConstructorServiceImpl.constructFhirEligibilityRequest("insurance-12321312");
		String eligReqSampleStr = FhirParser.encodeResourceToString(eligReqSample);
		assertTrue(fhirConstructorServiceImpl.validateRequest(eligReqSampleStr));

	}

	@Test
	public void constructFhirClaimTrackRequestTest() throws IOException {
		Task trackReq = fhirConstructorServiceImpl.constructFhirClaimTrackRequest("claim-12321312");
		String trackReqStr = FhirParser.encodeResourceToString(trackReq);
		assertTrue(fhirConstructorServiceImpl.validateRequest(trackReqStr));

	}

}

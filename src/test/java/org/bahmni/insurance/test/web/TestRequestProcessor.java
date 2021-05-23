package org.bahmni.insurance.test.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletException;

import org.bahmni.insurance.test.AbstractWebTest;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;

import ca.uhn.fhir.context.FhirContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRequestProcessor extends AbstractWebTest {

	@Autowired
	protected MockMvc mvc;
	

	public TestRequestProcessor(){
		super();
	}
	
	@Test
	@WithMockUser
	public void createPatientTest() throws Exception {
		String personJson = "{\r\n" + "  \"resourceType\": \"Person\",\r\n"
				+ "  \"id\": \"dda12af7-1691-11df-97a5-7038c432aabf\",\r\n" + "  \"name\": [\r\n" + "    {\r\n"
				+ "      \"use\": \"official\",\r\n" + "      \"family\": [\r\n" + "        \"Chalmers\"\r\n"
				+ "      ],\r\n" + "      \"given\": [\r\n" + "        \"Dummy\",\r\n" + "        \"Test\"\r\n"
				+ "      ]\r\n" + "    },\r\n" + "    {\r\n" + "      \"use\": \"usual\",\r\n"
				+ "      \"given\": [\r\n" + "        \"Jim\"\r\n" + "      ]\r\n" + "    }\r\n" + "  ],\r\n"
				+ "  \"gender\": \"male\",\r\n" + "  \"birthDate\": \"1974-12-25\",\r\n" + "  \"address\": [\r\n"
				+ "    {\r\n" + "      \"use\": \"home\",\r\n" + "      \"line\": [\r\n"
				+ "        \"534 Erewhon St\"\r\n" + "      ],\r\n" + "      \"city\": \"PleasantVille\",\r\n"
				+ "      \"state\": \"Vic\",\r\n" + "      \"postalCode\": \"3999\"\r\n" + "    }\r\n" + "  ],\r\n"
				+ "  \"active\": true\r\n" + "}";

		MvcResult mvcresult = mvc
				.perform(MockMvcRequestBuilders.get("/patient/Dummy").accept(MediaType.APPLICATION_JSON)).andReturn();
		String resultContent = mvcresult.getResponse().getContentAsString();
		Bundle bundle = (Bundle) FhirParser.parseResource(resultContent);
		int statusRetrieve = mvcresult.getResponse().getStatus();

		if (bundle.getTotal() < 1) {
			MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/patient")
					.contentType("application/fhir+json;q=1.0, application/json+fhir;q=0.9").content(personJson))
					.andReturn();

			int statusCreate = mvcResult.getResponse().getStatus();
			assertEquals(201, statusCreate);
		} else {
			assertEquals(200, statusRetrieve);
		}
	}

	@Test
	@WithMockUser
	public void extractPatientTest() throws Exception {
		MvcResult mvcresult = mvc
				.perform(MockMvcRequestBuilders.get("/patient/Dummy").accept(MediaType.APPLICATION_JSON)).andReturn();
		String resultContent = mvcresult.getResponse().getContentAsString();
		Bundle bundle = (Bundle) FhirParser.parseResource(resultContent);
		assertTrue(bundle.getTotal() > 0);

	}
	
	@Test
	@WithMockUser
	public void getClaimResponse() throws Exception {

		String claimParamJson = "{\r\n" + "	\"patientUUID\":\"123123123avfa21\",\r\n"
				+ "	\"visitUUID\":\"63273a41-b589-464c-af03-355de823d1b3\",\r\n" + "	\"claimId\": \"CID00091\",\r\n"
				+ "	\"insureeId\": \"105000002\",\r\n" + "	\"item\": [\r\n" + "		{\r\n"
				+ "		\"category\": \"item\",\r\n" + "		\"quantity\": 10,\r\n" + "		\"sequence\": 1,\r\n"
				+ "		\"service\": \"ICode\",\r\n" + "		\"unitPrice\": 20,\r\n"
				+ "		\"totalClaimed\":30,\r\n" + "		\"status\":\"Aproved\",\r\n"
				+ "		\"rejectedReason\":\"Dont know\",\r\n" + "		\"totalApproved\":20\r\n" + "      },\r\n"
				+ "	  {\r\n" + "		\"category\": \"service\",\r\n" + "		\"quantity\": 20,\r\n"
				+ "		\"sequence\": 1,\r\n" + "		\"service\": \"SCode\",\r\n" + "		\"unitPrice\": 20,\r\n"
				+ "		\"totalClaimed\":20,\r\n" + "		\"status\":\"Aproved\",\r\n"
				+ "		\"rejectedReason\":\"Dont know\",\r\n" + "		\"totalApproved\":20\r\n" + "      }\r\n"
				+ "	],\r\n" + "	\"total\": \"40\"\r\n" + "\r\n" + "}";

		MvcResult mvcresult = mvc.perform(MockMvcRequestBuilders.post("/get/claim/response")
				.accept(MediaType.APPLICATION_JSON)
				.contentType("application/json").content(claimParamJson))
				.andReturn();
		
		int status = mvcresult.getResponse().getStatus();
		assertEquals(200, status);
		
		/*String resultContent = mvcresult.getResponse().getContentAsString();
		OperationOutcome outcome = (OperationOutcome) FhirParser.parseResource(resultContent);
		assertEquals(IssueSeverity.INFORMATION, outcome.getIssue().get(0).getSeverity());*/
		

	}

	@Test
	@WithMockUser
	public void submitClaimTest() throws Exception {

		String claimParamJson = "{\r\n" + "	\"patientUUID\":\"123123123avfa21\",\r\n"
				+ "	\"visitUUID\":\"1231231231123212\",\r\n" + "	\"claimId\": \"CID00091\",\r\n"
				+ "	\"insureeId\": \"105000002\",\r\n" + "	\"item\": [\r\n" + "		{\r\n"
				+ "		\"category\": \"item\",\r\n" + "		\"quantity\": 10,\r\n" + "		\"sequence\": 1,\r\n"
				+ "		\"service\": \"ICode\",\r\n" + "		\"unitPrice\": 20,\r\n"
				+ "		\"totalClaimed\":30,\r\n" + "		\"status\":\"Aproved\",\r\n"
				+ "		\"rejectedReason\":\"Dont know\",\r\n" + "		\"totalApproved\":20\r\n" + "      },\r\n"
				+ "	  {\r\n" + "		\"category\": \"service\",\r\n" + "		\"quantity\": 20,\r\n"
				+ "		\"sequence\": 1,\r\n" + "		\"service\": \"SCode\",\r\n" + "		\"unitPrice\": 20,\r\n"
				+ "		\"totalClaimed\":20,\r\n" + "		\"status\":\"Aproved\",\r\n"
				+ "		\"rejectedReason\":\"Dont know\",\r\n" + "		\"totalApproved\":20\r\n" + "      }\r\n"
				+ "	],\r\n" + "	\"total\": \"40\"\r\n" + "\r\n" + "}";
		
		System.out.println("claimParamJson : "+claimParamJson);
		int status = 500;
		try {
			MvcResult mvcresult = mvc.perform(MockMvcRequestBuilders.post("/submit/claim")
				.accept(MediaType.APPLICATION_JSON)
				.contentType("application/json").content(claimParamJson))
				.andReturn();
			status = mvcresult.getResponse().getStatus();
		} catch (Exception e) {
		
		}
		
		assertEquals(200, status);
		/*
		OperationOutcome outcome = (OperationOutcome) FhirParser.parseResource(resultContent);
		assertEquals(IssueSeverity.INFORMATION, outcome.getIssue().get(0).getSeverity());*/
		

	}

}

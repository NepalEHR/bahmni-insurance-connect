package org.bahmni.insurance.web;

import static org.apache.log4j.Logger.getLogger;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bahmni.insurance.exception.ErrorJson;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.BadGateway;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;

import ca.uhn.fhir.context.FhirContext;

@RestController
public class CustomWebErrorController implements ErrorController {

	private static final String PATH = "/error";

	private final Logger logger = getLogger(CustomWebErrorController.class);
	
	@Value("${imisconnect.errortrace.include}")
	public boolean includeErrorTrace;

	@Autowired
	private ErrorAttributes errorAttributes;

	@RequestMapping("/error")
	public ErrorJson handleError(WebRequest request, HttpServletResponse response) {
		String  operationOutcomeException = null;
		if (errorAttributes.getError(request) instanceof HttpStatusCodeException) {
			HttpStatusCodeException errorHttp = (HttpStatusCodeException) errorAttributes.getError(request);
			logger.error(errorHttp.getResponseBodyAsString());
			if (errorHttp.getRawStatusCode() == 502 || errorHttp.getRawStatusCode() == 403) {
				operationOutcomeException = errorHttp.getResponseBodyAsString();
			} else {
				OperationOutcome operationOutcome = (OperationOutcome) FhirContext.forDstu3().newJsonParser().parseResource(errorHttp.getResponseBodyAsString());
				operationOutcomeException = operationOutcome.getIssue().get(0).getDetails().getText();
			}
			response.setStatus(errorHttp.getRawStatusCode());
		}
		ErrorJson error = new ErrorJson(response.getStatus(), operationOutcomeException, getErrorAttributes(request, includeErrorTrace));
		logger.error(error.getError() + " " + error.getMessage()+ " " +error.getOperationOutComeException());
		return error;

	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

	private Map<String, Object> getErrorAttributes(WebRequest request, boolean includeStackTrace) {
		return errorAttributes.getErrorAttributes(request, includeStackTrace);
	}
}
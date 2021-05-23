package org.bahmni.insurance.exception;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;

public class OperationOutcomeException {
	
	OperationOutcome operationOutComeException = new OperationOutcome();
	
	public OperationOutcomeException(String errorMessage) {
		List<OperationOutcomeIssueComponent> issueList = new ArrayList<>();
		OperationOutcomeIssueComponent operationOutcomeIssueComponent = new OperationOutcomeIssueComponent();
		operationOutcomeIssueComponent.setCode(IssueType.EXCEPTION);
		operationOutcomeIssueComponent.setSeverity(IssueSeverity.ERROR);
		CodeableConcept codeableConceptValue = new CodeableConcept();
		codeableConceptValue.setText(errorMessage);
		operationOutcomeIssueComponent.setDetails(codeableConceptValue);
		issueList.add(operationOutcomeIssueComponent);
		operationOutComeException.setIssue(issueList);
	}
	
	public OperationOutcome getOperationOutcome() {
		return this.operationOutComeException;
	}

}

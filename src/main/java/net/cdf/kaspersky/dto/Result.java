package net.cdf.kaspersky.dto;

import lombok.Data;

@Data
public class Result {//TESTE2
	private Boolean succeeded;
	private String errorCode;
	private String errorId;
	private String errorMessage;

	private String currencyCode;
	private String partnerAmount;

	private String kormOrderNumber;
	private String activationCode;

	private String licenseTerm;

	private String periodDescription;
	private String term;
	private String licenseId;
	
	private String request;
	private String response;
}

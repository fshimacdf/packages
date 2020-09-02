package net.cdf.kaspersky.dto;

import lombok.Data;

@Data
public class Result {
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
}

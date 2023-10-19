package com.gr.censusmanagement.constant;

public interface CensusConstants {
	
	final String[] dataTypes = {"Address"};
	final String[] regardsOf = {"Commission","Membership","RP Code","Payment","Other"};
	interface Claims {
		String SOURCE_ACCOUNT_ID = "sourceAccountId";
		String SOURCE_POC_ID = "sourcePocId";
	}
	final String TRCMFORM_STATUS_REPORT_NAME="Bulk Import Report";
	final int ROW_COUNT=200;
	final String GRID_ACKNOWLEDGEMENT_STATUS = "ackTravelerDataSync";

	interface CrmStatusCodes {
		Integer SUCCESS = 20;
		Integer FAILURE = 21;
	}
}

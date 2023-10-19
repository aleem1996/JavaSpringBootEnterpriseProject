package com.gr.censusmanagement.external.model;

public enum Status {
	PENDING("Pending"), INPROGRESS("InProgress"), SUCCESS("Success"), FAILURE("Failure");
	private String value;

	private Status(String value) {
		this.value = value;
	}
}

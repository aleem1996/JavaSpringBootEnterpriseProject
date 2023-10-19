package com.gr.censusmanagement.external.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BulkSessionDto {
	private Date createdOn = new Date();
	private String accountId;
	private String accountName;
	private String requestedBy;
	private String statusReportPath;
	private Status status = Status.PENDING;
	private Integer progress = 0;
	private Integer successfulRecords = 0;
	private Integer unSuccessfulRecords = 0;
	private String uploaderEmail;

	public void updateProgress() {
		++this.progress;
	}
	
	public void updateSuccessfulRecords() {
		++this.successfulRecords;
	}
	
	public void updateUnSuccessfulRecords() {
		++this.unSuccessfulRecords;
	}
}

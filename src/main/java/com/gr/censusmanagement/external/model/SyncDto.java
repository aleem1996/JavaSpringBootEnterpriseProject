package com.gr.censusmanagement.external.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SyncDto {
	private AccountDto accountDto;
	private AccountPocDto accountPocDto;
	private String sourceAccountId;
	private String trcmFormDataId;
	private Boolean isGrUser;
}

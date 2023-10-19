package com.gr.censusmanagement.external.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ExcelFileDataDto {
	private String excelFileName = "TrcmRecords";
	private byte[] fileContent;
	

}

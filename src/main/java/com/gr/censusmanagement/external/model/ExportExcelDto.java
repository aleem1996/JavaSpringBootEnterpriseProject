package com.gr.censusmanagement.external.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ExportExcelDto {
	
	private DataTableOptionsDto dataTableOptionsDto;
	private String uploaderEmail;
	private String[] trcmFormDataIds;

}

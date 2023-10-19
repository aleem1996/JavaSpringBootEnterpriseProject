package com.gr.censusmanagement.external.model;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.gr.censusmanagement.constant.ErrorCodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DataTableOptionsDto {

	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Integer start;
	
	@Valid
	@NotNull(message = ErrorCodes.BR_CAS_256)
	private Integer pageSize;
	
	private String sortOrder;
	private String sortColumn;
	private String attributeName;
	
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date coverageStartDate;
	
	@JsonFormat(pattern="MM/dd/yyyy", shape = Shape.STRING, lenient = OptBoolean.FALSE)
	private Date coverageEndDate;
	
	private String filter;
	
	private String membershipType;
	
	private String filterType;

}

package com.gr.censusmanagement.external.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public interface DashboardDto {
    Integer getQuarter();
    Integer getYear();
    Date getStartDate();
    Date getEndDate();
    Long getDays();
    Long getCount();
    Integer getIsCoverageStartDateHidden();
}

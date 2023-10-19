package com.gr.censusmanagement.external.v1.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.gr.censusmanagement.external.model.AccountDto;
import com.gr.censusmanagement.external.model.AccountPocDto;
import com.gr.censusmanagement.external.model.GridContactSilentLoginRequestDto;
import com.gr.censusmanagement.external.model.SyncDto;
import com.gr.censusmanagement.model.response.IdDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Source System API", tags = "Source System API")
@ApiResponses(value = { @ApiResponse(code = 401, message = "authorization failure") })
public interface SourceSystemApi {

	@ApiOperation("Create Or Update Account")
	ResponseEntity<IdDto> syncAccount(String apiKey, String sourceAccountId, AccountDto accountDto) throws Exception;

	@ApiOperation("Create Or Update Account POC")
	ResponseEntity<IdDto> syncPoc(String sourceAccountId, AccountPocDto accountPocDto) throws Exception;

//	@ApiOperation("Generate Token For POC Silent Login")
//	ResponseEntity<?> generateSilentLoginToken(String apiKey, String sourceAccountId, @RequestBody SyncDto syncDto) throws Exception;

	@ApiOperation("Generate Token For Paricipant Silent Login")
	ResponseEntity<?> generateSilentLoginTokenForParicipant(GridContactSilentLoginRequestDto gridContactSilentLoginRequestDto);
}

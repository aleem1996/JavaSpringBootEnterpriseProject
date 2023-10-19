package com.gr.censusmanagement.internal.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gr.censusmanagement.external.model.SyncDto;
import com.gr.censusmanagement.integration.dto.CrmUpdateRequestDto;
import com.gr.censusmanagement.integration.dto.GenericDto;
import com.gr.censusmanagement.service.AccountService;
import com.gr.censusmanagement.service.AuthService;
import com.gr.censusmanagement.service.TrcmFormService;
import com.gr.censusmanagement.util.Util;
import com.gr.common.v2.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/internal/v1/crm")
public class ICrmController {
	
	@Autowired
	TrcmFormService trcmFormService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AuthService authService;
	
	@Value("${silentLoginBaseUrl}")
	private String silentLoginBaseUrl;
	
	@PutMapping("/updatetraveler")
	public ResponseEntity<GenericDto> updateTravelerbyId(@RequestBody CrmUpdateRequestDto requestBody) {
		if (Util.isNullOrEmpty(requestBody.getTrcmFormDataId()) || Util.isNullOrEmpty(requestBody.getCrmContactGuid())) {
			return ResponseEntity.badRequest().build();
		}

		try {
			trcmFormService.updateTraveler(requestBody);
			GenericDto genericDto = GenericDto.getSuccessDto("Updated successfully.");
			return ResponseEntity.ok(genericDto);
		} catch (NotFoundException e) {
			log.error("Traveler not found", e);
			return ResponseEntity.ok(GenericDto.getFailureDto(e.getMessage()));
		}
	}
	
	@PostMapping(path = "/silentlogindynamics")
	public ResponseEntity<?> generateSilentLoginToken(@RequestBody SyncDto syncDto) throws Exception {
//		accountService.findBySourceAccountIdOrSync(AccountMapper.toEntity(accountDto), apiKey);
//		accountPocService.findBySourceAccountIdOrSyncPoc(AccountPocMapper.toEntity(accountPocDto), sourceAccountId);
		String redirectionUrl = silentLoginBaseUrl + "#/silent-login?crmToken="
				+ authService.generateSilentLoginTokenForDynamics(accountService.findBySourceAccountId(syncDto.getSourceAccountId()), syncDto.getTrcmFormDataId());
		return ResponseEntity.ok(new HashMap<String, String>() {
			{
				put("redirectionUrl", redirectionUrl);
			}
		});
	}
}

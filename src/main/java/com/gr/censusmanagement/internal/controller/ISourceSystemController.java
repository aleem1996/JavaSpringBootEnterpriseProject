package com.gr.censusmanagement.internal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.external.model.SyncDto;
import com.gr.censusmanagement.model.mapper.AccountMapper;
import com.gr.censusmanagement.model.mapper.AccountPocMapper;
import com.gr.censusmanagement.service.AccountPocService;
import com.gr.censusmanagement.service.AccountService;
import com.gr.censusmanagement.service.AuthService;
import com.gr.common.v2.constant.Constants;

@RestController
@RequestMapping("/api/internal/v1")
public class ISourceSystemController {

	@Autowired
	private AccountPocService accountPocService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AuthService authService;

	@PostMapping(path = "/{sourceAccountId}/silentlogin")
	public ResponseEntity<?> generateSilentLoginToken(@RequestHeader(Constants.Header.X_API_KEY) String apiKey, @PathVariable("sourceAccountId") String sourceAccountId,
		 @RequestBody SyncDto syncDto) throws Exception {
		String token = "";
		if (Boolean.TRUE.equals(syncDto.getIsGrUser())) {
			// add into Global Rescue Account
			Account account = accountService.findActiveAccountByName("Global Rescue LLC");			
			token = authService.generateSilentLoginToken(accountService.findBySourceAccountIdOrSync(AccountMapper.toEntity(syncDto.getAccountDto()), apiKey), accountPocService.findBySourceAccountIdOrSyncPoc(AccountPocMapper.toEntity(syncDto.getAccountPocDto()), account.getSourceAccountId()), syncDto.getAccountDto().getType());
		} else {
			token = authService.generateSilentLoginToken(accountService.findBySourceAccountIdOrSync(AccountMapper.toEntity(syncDto.getAccountDto()), apiKey), accountPocService.findBySourceAccountIdOrSyncPoc(AccountPocMapper.toEntity(syncDto.getAccountPocDto()), sourceAccountId), syncDto.getAccountDto().getType());
		}
		Map<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("token", token);
		return ResponseEntity.ok(hashMap);
	}
}

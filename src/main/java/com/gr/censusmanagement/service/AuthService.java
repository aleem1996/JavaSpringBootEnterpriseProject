package com.gr.censusmanagement.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.auth.service.JwtService;
import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.entity.AccountPoc;
import com.gr.censusmanagement.external.model.GridContactSilentLoginRequestDto;
import com.gr.censusmanagement.external.model.GridPocSilentLoginRequestDto;
import com.gr.censusmanagement.util.Util;
import com.gr.common.v2.constant.Constants;
import com.gr.common.v2.exception.BadRequestException;
import com.gr.common.v2.exception.UnAuthorizedException;
import com.gr.common.v2.exception.model.ApiError;
import com.nimbusds.jwt.JWTClaimsSet;

@Service
public class AuthService {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountPocService accountPocService;

	public String generateSilentLoginToken(GridPocSilentLoginRequestDto gridSilentLoginRequestDto) {
		String sourceAccountId = gridSilentLoginRequestDto.getSourceAccountId();
		String sourcePocId = gridSilentLoginRequestDto.getSourcePocId();
		Account account = accountService.findBySourceAccountId(sourceAccountId);
		AccountPoc accountPoc = accountPocService.findBySourcePocId(sourcePocId);
		if (Boolean.FALSE.equals(account.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account is not active.");
		}

		if (Boolean.FALSE.equals(accountPoc.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account POC is not active.");
		}

		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sourceAccountId", sourceAccountId);
		claims.put("sourcePocId", sourcePocId);
		claims.put("accountType", account.getType());
		return jwtService.generateToken(sourceAccountId, claims);
	}
	
	public String generateSilentLoginToken(Account account, AccountPoc accountPoc, String accountType) {
		if (Boolean.FALSE.equals(account.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account is not active.");
		}

		if (Boolean.FALSE.equals(accountPoc.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account POC is not active.");
		}

		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sourceAccountId", account.getSourceAccountId());
		claims.put("sourcePocId", accountPoc.getSourcePocId());
		claims.put("isPhoneCallIntakeFlow", Boolean.FALSE);
		claims.put("accountType", accountType);
		return jwtService.generateToken(account.getSourceAccountId(), claims);
	}
	
	public String generateSilentLoginTokenForDynamics(Account account, String trcmFormDataId) {
		if (Boolean.FALSE.equals(account.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account is not active.");
		}
		if (Util.isNullOrEmpty(trcmFormDataId)) {
			throw BadRequestException.of(ApiError.FIELD_NOT_NULL, "TrcmFormDataId can not be null");
		}

		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sourceAccountId", account.getSourceAccountId());
		claims.put("trcmFormDataId", trcmFormDataId);
		claims.put("isPhoneCallIntakeFlow", Boolean.TRUE);
		claims.put("accountType", account.getType());
		return jwtService.generateToken(account.getSourceAccountId(), claims);
	}
	
	public String generateSilentLoginTokenForParticipant(GridContactSilentLoginRequestDto gridContactSilentLoginRequestDto) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("firstName", gridContactSilentLoginRequestDto.getFirstName());
		claims.put("lastName", gridContactSilentLoginRequestDto.getLastName());
		claims.put("sourceContactId", gridContactSilentLoginRequestDto.getSourceContactId());
//		claims.put("accountType", account.getType());
		return jwtService.generateToken(gridContactSilentLoginRequestDto.getFirstName(), claims);
	}	

	public String generateFinalToken(String token) throws ParseException {
		if (token == null || !token.startsWith(Constants.Header.X_AUTH_TOKEN_PREFIX)) {
			throw UnAuthorizedException.ofMissingToken();
		}
		token = token.substring(7).trim();
		JWTClaimsSet existingClaims = jwtService.validateTokenAndGetClaimSet(token);
		String sourceAccountId = existingClaims.getStringClaim("sourceAccountId");
		String sourcePocId = existingClaims.getStringClaim("sourcePocId");
		String accountType = existingClaims.getStringClaim("accountType");
		Account account = accountService.findBySourceAccountId(sourceAccountId);
		AccountPoc accountPoc = accountPocService.findBySourcePocId(sourcePocId);
		if ( Boolean.FALSE.equals(account.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account is not active.");
		}

		if (Boolean.FALSE.equals(accountPoc.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account POC is not active.");
		}
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sourceAccountId", sourceAccountId);
		claims.put("sourcePocId", sourcePocId);
		claims.put("firstName", accountPoc.getFirstName());
		claims.put("lastName", accountPoc.getLastName());
		claims.put("email", accountPoc.getEmail());
		claims.put("accountId", account.getId());
		claims.put("accountPocId", accountPoc.getId());
		claims.put("accountName", account.getName());
		claims.put("sourceSystemName", account.getSourceSystem().getName());
		claims.put("allowBulkUpload", account.getAllowBulkUpload());
		claims.put("addTrcmFormData", account.getAddTrcmFormData());
		claims.put("isPhoneCallIntakeFlow", Boolean.FALSE);
		claims.put("allowContactUs", account.getAllowContactUs());
		claims.put("accountType", accountType);
		claims.put("emailTemplateName", account.getEmailTemplateFileName());
		return jwtService.generateToken(sourceAccountId, claims);
	}
	
	public String generateFinalTokenDynamics(String token) throws ParseException {
		if (token == null || !token.startsWith(Constants.Header.X_AUTH_TOKEN_PREFIX)) {
			throw UnAuthorizedException.ofMissingToken();
		}
		token = token.substring(7).trim();
		JWTClaimsSet existingClaims = jwtService.validateTokenAndGetClaimSet(token);
		String sourceAccountId = existingClaims.getStringClaim("sourceAccountId");
		String trcmFormDataId = existingClaims.getStringClaim("trcmFormDataId");
		Account account = accountService.findBySourceAccountId(sourceAccountId);
		if ( Boolean.FALSE.equals(account.getIsActive())) {
			throw BadRequestException.of(ApiError.GENERIC, "Account is not active.");
		}

		if (Util.isNullOrEmpty(trcmFormDataId)) {
			throw BadRequestException.of(ApiError.FIELD_NOT_NULL, "TrcmFormDataId can not be null.");
		}
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sourceAccountId", sourceAccountId);
		claims.put("accountId", account.getId());
		claims.put("accountName", account.getName());
		claims.put("sourceSystemName", account.getSourceSystem().getName());
		claims.put("allowBulkUpload", account.getAllowBulkUpload());
		claims.put("addTrcmFormData", account.getAddTrcmFormData());
		claims.put("trcmFormDataId", trcmFormDataId);
		claims.put("isPhoneCallIntakeFlow", Boolean.TRUE);
		claims.put("allowContactUs", account.getAllowContactUs());
		claims.put("accountType", account.getType());
		return jwtService.generateToken(sourceAccountId, claims);
	}
}

package com.gr.censusmanagement.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.censusmanagement.entity.AccountPoc;
import com.gr.censusmanagement.repository.AccountPocRepository;
import com.gr.censusmanagement.util.NullAwareBeanUtils;
import com.gr.common.v2.exception.NotFoundException;

@Service
public class AccountPocService {
	
	@Autowired
	private AccountPocRepository accountPocRepository;

	@Autowired 
	private AccountService accountService;
	
	public AccountPoc findBySourcePocId(String sourcePocId) {
		Optional<AccountPoc> oAccountPoc = accountPocRepository.findBySourcePocId(sourcePocId);
		if (!oAccountPoc.isPresent()) {
			throw NotFoundException.ofResource(AccountPoc.class.getSimpleName(), sourcePocId);
		}
		return oAccountPoc.get();
	}
	
	public AccountPoc saveOrUpdate(AccountPoc newAccountPoc, String sourceAccountId) throws Exception {
		Optional<AccountPoc> oAccountPoc = accountPocRepository.findBySourcePocId(newAccountPoc.getSourcePocId());
		if(oAccountPoc.isPresent()) {
			AccountPoc existingAccountPoc = oAccountPoc.get();
			NullAwareBeanUtils.copyProperties(existingAccountPoc, newAccountPoc);
			return accountPocRepository.save(existingAccountPoc);
		}else {
			newAccountPoc.setAccount(accountService.findBySourceAccountId(sourceAccountId));
			return accountPocRepository.save(newAccountPoc);
		}
	}

	public AccountPoc findBySourceAccountIdOrSyncPoc(AccountPoc accountPoc, String sourceAccountId) throws Exception {
		Optional<AccountPoc> oAccountPoc = accountPocRepository.findBySourcePocId(accountPoc.getSourcePocId());
		if (!oAccountPoc.isPresent()) {
			return this.saveOrUpdate(accountPoc, sourceAccountId);
		}
		return oAccountPoc.get();
	}
}

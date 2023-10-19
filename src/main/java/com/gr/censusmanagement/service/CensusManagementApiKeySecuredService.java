package com.gr.censusmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.auth.service.ApiKeySecuredService;

@Service
public class CensusManagementApiKeySecuredService extends ApiKeySecuredService {

	@Autowired
	private SourceSystemService sourceSystemService;

	@Override
	public Boolean isApiKeyValid(String apiKey) {
		return sourceSystemService.findByApiKey(apiKey).isPresent();
	}

}

package com.gr.censusmanagement.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gr.censusmanagement.entity.SourceSystem;
import com.gr.censusmanagement.repository.SourceSystemRepository;

@Service
public class SourceSystemService {
	@Autowired
	private SourceSystemRepository sourceSystemRepository;

	@Cacheable(cacheNames = "epsourceSystemCache", key = "#apiKey")
	public Optional<SourceSystem> findByApiKey(String apiKey) {
		return sourceSystemRepository.findByApiKey(apiKey);
	}
}

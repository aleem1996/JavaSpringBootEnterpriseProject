package com.gr.censusmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.gr.censusmanagement.entity.ApiConfiguration;
import com.gr.censusmanagement.entity.SourceSystem;

public interface ApiConfigurationRepository extends JpaRepository<ApiConfiguration, String>  {
	public Optional<ApiConfiguration> findByApiKey(@Param("apiKey") String apiKey);

}

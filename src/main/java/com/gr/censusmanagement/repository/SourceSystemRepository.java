package com.gr.censusmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.gr.censusmanagement.entity.SourceSystem;

public interface SourceSystemRepository extends JpaRepository<SourceSystem, String> {
	public Optional<SourceSystem> findByApiKey(@Param("apiKey") String apiKey);
}

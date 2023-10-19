package com.gr.censusmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gr.censusmanagement.entity.AccountPoc;

public interface AccountPocRepository extends JpaRepository<AccountPoc, String> {
	public Optional<AccountPoc> findBySourcePocId(String sourcePocId);
}

package com.gr.censusmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.gr.censusmanagement.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

	public Optional<Account> findBySourceAccountId(@Param("sourceAccountId") String sourceAccountId);
	
	public Optional<Account> findById(@Param("id") String id);

	public Optional<Account> findByNameAndIsActiveTrue(@Param("name") String name);
}

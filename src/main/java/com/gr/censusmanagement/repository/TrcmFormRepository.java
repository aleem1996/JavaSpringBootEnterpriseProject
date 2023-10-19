package com.gr.censusmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.gr.censusmanagement.entity.TrcmForm;

public interface TrcmFormRepository extends JpaRepository<TrcmForm, String> {

	Optional<List<TrcmForm>> findByAccountId(@Param("accountId") String accountId);

	Optional<TrcmForm> findByAccountIdAndIsActive(@Param("accountId") String accountId, @Param("isActive") Boolean isActive);

	Optional<TrcmForm> findByIdAndIsActive(@Param("id") String accountId, @Param("isActive") Boolean isActive);
}

package com.apiintegration.core.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Account;

public interface AccountRepo extends JpaRepository<Account, Long> {

	Optional<Account> findById(Long accountId);

	Account findByAccountName(String accountName);

	boolean existsByAccountName(String accountName);

}

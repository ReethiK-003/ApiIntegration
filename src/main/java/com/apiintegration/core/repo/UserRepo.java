package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findByUserEmail(String userEmail);

	Optional<User> findById(Long id);

	List<User> findByAccount(Account account);

//	User findByUserFullName(String userFullName);
//
//	User findByUserFullNameAndUserEmail(String userFullName, String userEmail);
//
//	User findAllByAccountIdAndUserRole(Long accountId, String userRole);

	boolean existsByUserEmail(String userEmail);

}

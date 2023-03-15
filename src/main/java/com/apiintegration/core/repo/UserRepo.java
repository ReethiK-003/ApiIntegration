package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.apiintegration.core.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	User findByUserEmail(String userEmail);
	
	Optional<User> findById(Long id);;

//	User findByUserEmai(String userEmail);

//	User findByUserFullName(String userFullName);
//
//	User findByUserFullNameAndUserEmail(String userFullName, String userEmail);
//
//	User findAllByAccountId(Long accountId);
//
//	User findAllByAccountIdAndUserRole(Long accountId, String userRole);
	
	boolean existsByUserEmail(String userEmail);

}

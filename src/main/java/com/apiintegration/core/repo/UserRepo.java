package com.apiintegration.core.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.apiintegration.core.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	User findByUserEmail(String userEmail);

	Optional<User> findById(Long id);;

	boolean existsByUserEmail(String userEmail);

}
package com.apiintegration.core.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.apiintegration.core.model.Token;
import com.apiintegration.core.model.User;

public interface TokenRepo extends PagingAndSortingRepository<Token, Long>, JpaRepository<Token, Long> {

	Token findByToken(String token);

	List<Token> findAllByUserAndType(User user, String type);

	Optional<Token> findByTokenAndType(String token, String tokenType);

	Optional<Token> findByTokenAndTypeAndUser(String token, String tokenType, User user);

	Optional<Token> findByTypeAndUserAndExpiresAtAfter(String tokenType, User user, Date date);

	@Modifying
	@Transactional
	@Query("DELETE FROM Token t WHERE t.expiresAt < :now")
	void deleteExpiredTokens(@Param("now") Date now);
}

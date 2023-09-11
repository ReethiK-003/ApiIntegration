package com.apiintegration.core.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.apiintegration.core.model.Api;
import com.apiintegration.core.model.ApiLogs;

public interface ApiLogsRepo extends JpaRepository<ApiLogs, Long> ,PagingAndSortingRepository<ApiLogs, Long> {

	Page<ApiLogs> findAll(Pageable pageable);
	
	Page<ApiLogs> getAllByApi(Api api, Pageable pageable);
}
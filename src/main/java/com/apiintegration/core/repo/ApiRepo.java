package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.apiintegration.core.model.Api;
import com.apiintegration.core.model.Services;

public interface ApiRepo extends JpaRepository<Api, Long> {

	Optional<Api> findById(Long apiId);

	Api findByIdAndApiName(Long apiId, String apiName);

	List<Api> findByServices(Services services);

}
package com.apiintegration.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.apiintegration.core.repo")
public class JpaRepositoryConfig {

}
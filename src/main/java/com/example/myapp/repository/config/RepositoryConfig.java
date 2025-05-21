package com.example.myapp.repository.config;

import com.example.myapp.repository.base.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for custom repository implementations.
 * Sets BaseRepositoryImpl as the base class for all repositories.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.myapp.repository",
    repositoryBaseClass = BaseRepositoryImpl.class
)
public class RepositoryConfig {
    // No son necesarias configuraciones adicionales por ahora
}
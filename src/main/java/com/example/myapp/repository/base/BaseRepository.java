package com.example.myapp.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface providing common functionality.
 * This interface defines operations that will be available to all repositories.
 *
 * @param <T> The entity type
 * @param <ID> The type of the entity's identifier
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    
    /**
     * Soft deletes an entity by setting its active status to false.
     * 
     * @param id The ID of the entity to soft delete
     */
    void softDelete(ID id);
    
    /**
     * Restores a previously soft-deleted entity by setting its active status to true.
     * 
     * @param id The ID of the entity to restore
     */
    void restore(ID id);
}
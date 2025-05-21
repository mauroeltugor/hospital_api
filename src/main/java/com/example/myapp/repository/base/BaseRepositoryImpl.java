package com.example.myapp.repository.base;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * Implementation of BaseRepository that provides common functionality
 * for all repository implementations.
 *
 * @param <T> The entity type
 * @param <ID> The type of the entity's identifier
 */
public class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
    
    /**
     * Creates a new BaseRepositoryImpl with the given entity information and entity manager.
     *
     * @param entityInformation Must not be null
     * @param entityManager Must not be null
     */
    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        // No necesitamos guardar entityManager como campo si no lo usamos directamente
    }
    
    /**
     * Soft deletes an entity by setting its active status to false.
     * Attempts to use setActive() or setIsActive() method via reflection.
     *
     * @param id Must not be null
     */
    @Override
    @Transactional
    public void softDelete(ID id) {
        findById(id).ifPresent(entity -> {
            try {
                // Try different naming conventions for the active field
                setActiveStatus(entity, false);
                save(entity);
            } catch (Exception e) {
                throw new RuntimeException("Could not soft delete entity: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Restores an entity by setting its active status to true.
     * Attempts to use setActive() or setIsActive() method via reflection.
     *
     * @param id Must not be null
     */
    @Override
    @Transactional
    public void restore(ID id) {
        findById(id).ifPresent(entity -> {
            try {
                // Try different naming conventions for the active field
                setActiveStatus(entity, true);
                save(entity);
            } catch (Exception e) {
                throw new RuntimeException("Could not restore entity: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Helper method to set active status using reflection.
     * Tries different naming conventions commonly used.
     *
     * @param entity The entity to modify
     * @param status The status to set (true for active, false for inactive)
     * @throws Exception If no appropriate method can be found
     */
    private void setActiveStatus(T entity, boolean status) throws Exception {
        Class<?> entityClass = entity.getClass();
        
        // Try different possible method names
        Method method = null;
        try {
            method = entityClass.getMethod("setActive", Boolean.class);
        } catch (NoSuchMethodException e1) {
            try {
                method = entityClass.getMethod("setActive", boolean.class);
            } catch (NoSuchMethodException e2) {
                try {
                    method = entityClass.getMethod("setIsActive", Boolean.class);
                } catch (NoSuchMethodException e3) {
                    try {
                        method = entityClass.getMethod("setIsActive", boolean.class);
                    } catch (NoSuchMethodException e4) {
                        throw new RuntimeException("Entity does not have a method to set active status");
                    }
                }
            }
        }
        
        // Invoke the method found
        method.invoke(entity, status);
    }
}
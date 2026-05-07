package com.jenwright.booking.resource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Resource entities.
 *
 * Provides data access operations for resources including standard CRUD operations
 * inherited from JpaRepository, and custom query methods for filtering resources
 * by their active status.
 *
 * @author jen
 * @version 1.0
 */
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    /**
     * Finds all active resources.
     *
     * Retrieves all resources where the active flag is true. This is used to filter
     * out deactivated resources from the available resources list.
     *
     * @return a list of active resources, or an empty list if no active resources exist
     */
    List<Resource> findByActiveTrue();
}

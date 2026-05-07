package com.jenwright.booking.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing resource operations.
 *
 * Handles retrieval, creation, updating, and deactivation of resources.
 * This service provides business logic for managing bookable resources
 * including availability management through the active status flag.
 *
 * @author jen
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    /**
     * Retrieves all active resources.
     *
     * @return a list of all active resources, or an empty list if none exist
     */
    public List<Resource> getAllActive() {
        return resourceRepository.findByActiveTrue();
    }

    /**
     * Retrieves a resource by its ID.
     *
     * @param id the ID of the resource to retrieve
     * @return the Resource with the specified ID
     * @throws IllegalArgumentException if the resource does not exist
     */
    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));
    }

    /**
     * Creates a new resource.
     *
     * Persists the resource to the database. The resource is created in an
     * active state by default.
     *
     * @param resource the resource to create
     * @return the created resource with generated ID
     */
    public Resource create(Resource resource) {
        return resourceRepository.save(resource);
    }

    /**
     * Updates an existing resource.
     *
     * Updates the name, description, and capacity of the specified resource.
     * The active status is not modified by this operation.
     *
     * @param id the ID of the resource to update
     * @param updated the resource object containing updated values
     * @return the updated resource
     * @throws IllegalArgumentException if the resource does not exist
     */
    public Resource update(Long id, Resource updated) {
        Resource existing = getById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCapacity(updated.getCapacity());
        return resourceRepository.save(existing);
    }

    /**
     * Deactivates a resource.
     *
     * Sets the active flag to false, preventing the resource from appearing
     * in the list of available resources for booking. Historical booking data
     * is preserved.
     *
     * @param id the ID of the resource to deactivate
     * @throws IllegalArgumentException if the resource does not exist
     */
    public void deactivate(Long id) {
        Resource resource = getById(id);
        resource.setActive(false);
        resourceRepository.save(resource);
    }
}

package com.jenwright.booking.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing bookable resources.
 *
 * Provides endpoints for retrieving and managing resources that can be booked.
 * Customers can view all active resources, while administrators can create, update,
 * and deactivate resources. All endpoints require bearer token authentication.
 *
 * Supports the following operations:
 * - List all active resources (public to authenticated users)
 * - Retrieve a specific resource by ID (public to authenticated users)
 * - Create a new resource (admin only)
 * - Update an existing resource (admin only)
 * - Deactivate a resource (admin only)
 *
 * @author jen
 * @version 1.0
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Manage bookable resources (rooms, slots, etc.)")
@SecurityRequirement(name = "bearerAuth")
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * Retrieves all active resources.
     *
     * Returns a list of all resources that are currently active and available for booking.
     * Only active resources are included in the response.
     *
     * @return ResponseEntity containing a list of active resources
     */
    @GetMapping
    @Operation(summary = "List all active resources")
    public ResponseEntity<List<Resource>> getAll() {
        return ResponseEntity.ok(resourceService.getAllActive());
    }

    /**
     * Retrieves a specific resource by its ID.
     *
     * @param id the ID of the resource to retrieve
     * @return ResponseEntity containing the requested resource
     * @throws IllegalArgumentException if the resource does not exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a resource by ID")
    public ResponseEntity<Resource> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getById(id));
    }

    /**
     * Creates a new resource.
     *
     * Only administrators can create resources. The resource will be created in an
     * active state by default.
     *
     * @param resource the resource to create containing name, description, and capacity
     * @return ResponseEntity containing the created resource with generated ID
     * @throws IllegalArgumentException if the request is invalid
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a resource (Admin only)")
    public ResponseEntity<Resource> create(@RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.create(resource));
    }

    /**
     * Updates an existing resource.
     *
     * Only administrators can update resources. Allows updating resource details
     * such as name, description, and capacity.
     *
     * @param id the ID of the resource to update
     * @param resource the updated resource information
     * @return ResponseEntity containing the updated resource
     * @throws IllegalArgumentException if the resource does not exist
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a resource (Admin only)")
    public ResponseEntity<Resource> update(@PathVariable Long id, @RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.update(id, resource));
    }

    /**
     * Deactivates a resource.
     *
     * Only administrators can deactivate resources. Deactivating a resource prevents
     * it from appearing in the list of available resources and stops new bookings,
     * but does not delete historical booking data.
     *
     * @param id the ID of the resource to deactivate
     * @return ResponseEntity with no content status (204)
     * @throws IllegalArgumentException if the resource does not exist
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a resource (Admin only)")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        resourceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}

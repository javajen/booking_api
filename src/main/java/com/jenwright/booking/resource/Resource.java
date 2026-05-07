package com.jenwright.booking.resource;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a bookable resource.
 *
 * This entity represents physical or virtual resources that can be booked by users,
 * such as meeting rooms, equipment, or facilities. Each resource has a name, description,
 * capacity, and active status to control availability.
 *
 * @author jen
 * @version 1.0
 */
@Entity
@Table(name = "resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    /**
     * The unique identifier for the resource.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the resource.
     * Must not be null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional description of the resource.
     * Can be null or empty.
     */
    @Column
    private String description;

    /**
     * The maximum capacity of the resource.
     * Must not be null. Represents the number of people or items the resource can accommodate.
     */
    @Column(nullable = false)
    private int capacity;

    /**
     * Indicates whether the resource is currently active and available for booking.
     * Must not be null. Defaults to true for new resources.
     */
    @Column(nullable = false)
    private boolean active = true;
}

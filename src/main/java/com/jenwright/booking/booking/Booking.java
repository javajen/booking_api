package com.jenwright.booking.booking;

import com.jenwright.booking.resource.Resource;
import com.jenwright.booking.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a booking of a resource by a user.
 *
 * This entity tracks reservations of resources with start and end times, notes,
 * and the current status of the booking. It maintains relationships to both the
 * User who made the booking and the Resource being booked.
 *
 * @author jen
 * @version 1.0
 */
@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    /**
     * The unique identifier for the booking.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who made the booking.
     * Must not be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The resource being booked.
     * Must not be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    /**
     * The start time of the booking.
     * Must not be null.
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * The end time of the booking.
     * Must not be null. Should be after the startTime.
     */
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * Optional notes or additional information about the booking.
     * Can be null or empty.
     */
    @Column
    private String notes;

    /**
     * The current status of the booking.
     * Must not be null. Possible values are defined in BookingStatus enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}

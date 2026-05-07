package com.jenwright.booking.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing Booking entities.
 *
 * Provides data access operations for bookings including retrieval by user or resource,
 * and conflict detection for time slot availability checking.
 *
 * @author jen
 * @version 1.0
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds all bookings for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of bookings made by the user, or an empty list if none exist
     */
    List<Booking> findByUserId(Long userId);

    /**
     * Finds all bookings for a specific resource.
     *
     * @param resourceId the ID of the resource
     * @return a list of bookings for the resource, or an empty list if none exist
     */
    List<Booking> findByResourceId(Long resourceId);

    /**
     * Detects conflicting bookings for a resource during a requested time slot.
     *
     * Finds all CONFIRMED bookings that overlap with the specified time range.
     * This is used to prevent double-booking of resources. The query checks for any
     * existing confirmed bookings where the start time is before the requested end time
     * AND the end time is after the requested start time (indicating an overlap).
     *
     * @param resourceId the ID of the resource to check
     * @param startTime the requested start time of the booking
     * @param endTime the requested end time of the booking
     * @return a list of conflicting CONFIRMED bookings, or an empty list if the time slot is available
     */
    @Query("""
        SELECT b FROM Booking b
        WHERE b.resource.id = :resourceId
        AND b.status = 'CONFIRMED'
        AND b.startTime < :endTime
        AND b.endTime > :startTime
    """)
    List<Booking> findConflicts(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}

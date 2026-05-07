package com.jenwright.booking.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a new booking.
 *
 * This class represents the client request to book a resource for a specific time period.
 * All timestamp-based fields must be in the future and the end time must be after the start time.
 *
 * @author jen
 * @version 1.0
 */
@Data
public class BookingRequest {

    /**
     * The ID of the resource to be booked.
     * Must not be null.
     */
    @NotNull
    private Long resourceId;

    /**
     * The start time of the booking.
     * Must not be null and must be in the future.
     */
    @NotNull
    @Future
    private LocalDateTime startTime;

    /**
     * The end time of the booking.
     * Must not be null and must be in the future.
     * Should be after the startTime.
     */
    @NotNull
    @Future
    private LocalDateTime endTime;

    /**
     * Optional notes or additional information about the booking.
     * Can be null or empty.
     */
    private String notes;
}

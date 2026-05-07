package com.jenwright.booking.booking;

/**
 * Enumeration representing the possible states of a booking.
 *
 * A booking transitions through different states during its lifecycle, from creation
 * to cancellation. This enum defines the allowed status values for bookings.
 *
 * @author jen
 * @version 1.0
 */
public enum BookingStatus {
    /**
     * Indicates that the booking has been successfully created and confirmed.
     * This is the initial status when a new booking is created.
     */
    CONFIRMED,

    /**
     * Indicates that the booking has been canceled by the user or an administrator.
     * Once canceled, a booking cannot be reactivated.
     */
    CANCELED
}

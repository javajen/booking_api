package com.jenwright.booking.user;

/**
 * Enumeration representing user roles in the booking system.
 *
 * Defines the different role types available for users in the application.
 * Each role has specific permissions and capabilities within the system.
 *
 * @author jen
 * @version 1.0
 */
public enum Role {
    /**
     * Standard user role with basic booking capabilities.
     * Customers can create bookings, view their own bookings, and cancel their own bookings.
     */
    CUSTOMER,

    /**
     * Administrative user role with elevated privileges.
     * Administrators can manage resources, view all bookings, cancel any booking,
     * and perform other administrative operations.
     */
    ADMIN
}

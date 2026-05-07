package com.jenwright.booking.booking;

import com.jenwright.booking.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST Controller for managing booking operations.
 *
 * Provides endpoints to create, retrieve, and cancel bookings for resources.
 * All endpoints require bearer token authentication.
 *
 * Supports the following operations:
 * - Create a new booking for a resource
 * - Retrieve bookings for the current authenticated user
 * - Retrieve bookings for a specific resource
 * - Cancel an existing booking
 *
 * @author jen
 * @version 1.0
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Create, view, and cancel bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Creates a new booking for a resource.
     *
     * Validates the booking request and checks for resource availability at the requested time.
     * Returns the created booking if successful.
     *
     * @param request the booking request containing resource ID and time information
     * @param currentUser the authenticated user making the booking
     * @return ResponseEntity containing the created Booking
     * @throws IllegalArgumentException if the request is invalid or end time is not after start time
     * @throws IllegalStateException if the resource is already booked for that time slot
     */
    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<Booking> create(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(bookingService.create(request, currentUser));
    }

    /**
     * Retrieves all bookings made by the current authenticated user.
     *
     * @param currentUser the authenticated user
     * @return ResponseEntity containing a list of bookings for the user, or an empty list if none exist
     */
    @GetMapping("/my")
    @Operation(summary = "Get all bookings for the current user")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(bookingService.getMyBookings(currentUser.getId()));
    }

    /**
     * Retrieves all bookings for a specific resource.
     *
     * @param resourceId the ID of the resource
     * @return ResponseEntity containing a list of bookings for the resource, or an empty list if none exist
     */
    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Get all bookings for a specific resource")
    public ResponseEntity<List<Booking>> getByResource(@PathVariable Long resourceId) {
        return ResponseEntity.ok(bookingService.getBookingsForResource(resourceId));
    }

    /**
     * Cancels an existing booking.
     *
     * Only the booking owner or an administrator can cancel a booking. The booking ID must
     * correspond to an existing, non-cancelled booking.
     *
     * @param id the ID of the booking to cancel
     * @param currentUser the authenticated user requesting the cancellation
     * @return ResponseEntity containing the cancelled Booking with status set to CANCELLED
     * @throws IllegalArgumentException if the booking does not exist
     * @throws SecurityException if the current user is neither the booking owner nor an admin
     * @throws IllegalStateException if the booking is already cancelled
     */
    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<Booking> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(bookingService.cancel(id, currentUser));
    }
}

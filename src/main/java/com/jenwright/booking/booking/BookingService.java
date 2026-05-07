package com.jenwright.booking.booking;

import com.jenwright.booking.resource.Resource;
import com.jenwright.booking.resource.ResourceService;
import com.jenwright.booking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing booking operations.
 *
 * Handles the creation, retrieval, and cancellation of bookings. This service ensures
 * business logic constraints such as time validation and resource availability checking.
 * It also manages authorization for booking cancellations.
 *
 * @author jen
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ResourceService resourceService;

    /**
     * Creates a new booking for the provided resource and time slot.
     *
     * Validates that the end time is after the start time and verifies that the
     * resource is not already booked for the requested time period. If all validations
     * pass, creates a new booking with CONFIRMED status.
     *
     * @param request the booking request containing resource ID and time information
     * @param currentUser the user making the booking
     * @return the created Booking entity
     * @throws IllegalArgumentException if end time is not after start time
     * @throws IllegalStateException if the resource is already booked for that time slot
     * @throws IllegalArgumentException if the resource does not exist
     */
    public Booking create(BookingRequest request, User currentUser) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Resource resource = resourceService.getById(request.getResourceId());

        List<Booking> conflicts = bookingRepository.findConflicts(
                resource.getId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Resource is already booked for that time slot");
        }

        Booking booking = Booking.builder()
                .user(currentUser)
                .resource(resource)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .notes(request.getNotes())
                .status(BookingStatus.CONFIRMED)
                .build();

        return bookingRepository.save(booking);
    }

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of bookings for the user, or an empty list if none exist
     */
    public List<Booking> getMyBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    /**
     * Retrieves all bookings for a specific resource.
     *
     * @param resourceId the ID of the resource
     * @return a list of bookings for the resource, or an empty list if none exist
     */
    public List<Booking> getBookingsForResource(Long resourceId) {
        return bookingRepository.findByResourceId(resourceId);
    }

    /**
     * Cancels an existing booking.
     *
     * Only the booking owner or an admin user can cancel a booking. Attempting to
     * cancel a booking that is already cancelled will result in an error.
     *
     * @param bookingId the ID of the booking to cancel
     * @param currentUser the user requesting the cancellation
     * @return the cancelled Booking entity with status set to CANCELLED
     * @throws IllegalArgumentException if the booking does not exist
     * @throws SecurityException if the current user is neither the booking owner nor an admin
     * @throws IllegalStateException if the booking is already cancelled
     */
    public Booking cancel(Long bookingId, User currentUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new SecurityException("You are not allowed to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        return bookingRepository.save(booking);
    }
}

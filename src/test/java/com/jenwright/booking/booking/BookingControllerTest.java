package com.jenwright.booking.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jenwright.booking.auth.JwtService;
import com.jenwright.booking.user.Role;
import com.jenwright.booking.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * Unit tests for the BookingController REST API endpoints.
 *
 * Tests the functionality of all booking operations including creating bookings,
 * retrieving bookings, and cancelling bookings. Tests cover both happy path scenarios
 * and error cases including authorization failures, invalid requests, and resource conflicts.
 *
 * Uses MockMvc for HTTP testing and Mockito for mocking service dependencies.
 *
 * @author jen
 * @version 1.0
 */
@WebMvcTest(BookingController.class)
@DisplayName("BookingController Tests")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private User testUser;
    private Booking testBooking;
    private BookingRequest bookingRequest;
    private LocalDateTime futureTime;
    private LocalDateTime futureTimeEnd;

    /**
     * Sets up test fixtures before each test.
     *
     * Initializes test user, booking request, and booking response objects
     * with realistic data for use in test scenarios.
     */
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .fullName("Test User")
                .role(Role.CUSTOMER)
                .build();

        futureTime = LocalDateTime.now().plusHours(1);
        futureTimeEnd = LocalDateTime.now().plusHours(2);

        bookingRequest = new BookingRequest();
        bookingRequest.setResourceId(1L);
        bookingRequest.setStartTime(futureTime);
        bookingRequest.setEndTime(futureTimeEnd);
        bookingRequest.setNotes("Test booking");

        testBooking = Booking.builder()
                .id(1L)
                .user(testUser)
                .resource(new com.jenwright.booking.resource.Resource())
                .startTime(futureTime)
                .endTime(futureTimeEnd)
                .notes("Test booking")
                .status(BookingStatus.CONFIRMED)
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("should create booking successfully with valid request")
    void shouldCreateBookingWithValidRequest() throws Exception {
        when(bookingService.create(any(BookingRequest.class), any(User.class)))
                .thenReturn(testBooking);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser
    @DisplayName("should return bad request when booking request is invalid")
    void shouldReturnBadRequestForInvalidBookingRequest() throws Exception {
        BookingRequest invalidRequest = new BookingRequest();
        invalidRequest.setResourceId(null);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("should throw exception when end time is not after start time")
    void shouldThrowExceptionWhenEndTimeNotAfterStartTime() throws Exception {
        when(bookingService.create(any(BookingRequest.class), any(User.class)))
                .thenThrow(new IllegalArgumentException("End time must be after start time"));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("should throw exception when resource is already booked")
    void shouldThrowExceptionWhenResourceAlreadyBooked() throws Exception {
        when(bookingService.create(any(BookingRequest.class), any(User.class)))
                .thenThrow(new IllegalStateException("Resource is already booked for that time slot"));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should require authentication for create endpoint")
    void shouldRequireAuthenticationForCreateEndpoint() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("should retrieve all bookings for current user")
    void shouldRetrieveAllBookingsForCurrentUser() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(testBooking);

        when(bookingService.getMyBookings(1L))
                .thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/my")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("should return empty list when user has no bookings")
    void shouldReturnEmptyListWhenUserHasNoBookings() throws Exception {
        when(bookingService.getMyBookings(1L))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/bookings/my")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("should require authentication for get my bookings endpoint")
    void shouldRequireAuthenticationForGetMyBookingsEndpoint() throws Exception {
        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("should retrieve all bookings for specific resource")
    void shouldRetrieveAllBookingsForSpecificResource() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(testBooking);

        when(bookingService.getBookingsForResource(1L))
                .thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/resource/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("should return empty list when resource has no bookings")
    void shouldReturnEmptyListWhenResourceHasNoBookings() throws Exception {
        when(bookingService.getBookingsForResource(1L))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/bookings/resource/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("should allow public access to get bookings for resource")
    void shouldAllowPublicAccessToGetBookingsForResource() throws Exception {
        when(bookingService.getBookingsForResource(1L))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/bookings/resource/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("should cancel booking successfully when user is owner")
    void shouldCancelBookingWhenUserIsOwner() throws Exception {
        testBooking.setStatus(BookingStatus.CANCELED);
        when(bookingService.cancel(1L, testUser))
                .thenReturn(testBooking);

        mockMvc.perform(patch("/api/bookings/1/cancel")
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    @Test
    @WithMockUser
    @DisplayName("should throw exception when non-owner tries to cancel booking")
    void shouldThrowExceptionWhenNonOwnerTriesToCancelBooking() throws Exception {
        User otherUser = User.builder()
                .id(2L)
                .email("other@test.com")
                .fullName("Other User")
                .role(Role.CUSTOMER)
                .build();

        when(bookingService.cancel(1L, otherUser))
                .thenThrow(new SecurityException("You are not allowed to cancel this booking"));

        mockMvc.perform(patch("/api/bookings/1/cancel")
                        .with(user(otherUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("should throw exception when booking not found")
    void shouldThrowExceptionWhenBookingNotFound() throws Exception {
        when(bookingService.cancel(999L, testUser))
                .thenThrow(new IllegalArgumentException("Booking not found: 999"));

        mockMvc.perform(patch("/api/bookings/999/cancel")
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("should throw exception when cancelling already cancelled booking")
    void shouldThrowExceptionWhenCancellingAlreadyCancelledBooking() throws Exception {
        when(bookingService.cancel(1L, testUser))
                .thenThrow(new IllegalStateException("Booking is already cancelled"));

        mockMvc.perform(patch("/api/bookings/1/cancel")
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should require authentication for cancel endpoint")
    void shouldRequireAuthenticationForCancelEndpoint() throws Exception {
        mockMvc.perform(patch("/api/bookings/1/cancel")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("should allow admin to cancel any booking")
    void shouldAllowAdminToCancelAnyBooking() throws Exception {
        User adminUser = User.builder()
                .id(3L)
                .email("admin@test.com")
                .fullName("Admin User")
                .role(Role.ADMIN)
                .build();

        testBooking.setStatus(BookingStatus.CANCELED);
        when(bookingService.cancel(1L, adminUser))
                .thenReturn(testBooking);

        mockMvc.perform(patch("/api/bookings/1/cancel")
                        .with(user(adminUser))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }
}

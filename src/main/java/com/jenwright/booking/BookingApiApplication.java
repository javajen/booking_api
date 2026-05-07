package com.jenwright.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the Booking API.
 *
 * This is the main application class that bootstraps and starts the Booking API
 * REST service. The @SpringBootApplication annotation enables auto-configuration
 * and component scanning for all classes in this package and sub-packages.
 *
 * The Booking API provides endpoints for managing resource bookings with JWT
 * authentication and role-based access control.
 *
 * @author jen
 * @version 1.0
 */
@SpringBootApplication
public class BookingApiApplication {
    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(BookingApiApplication.class, args);
    }
}

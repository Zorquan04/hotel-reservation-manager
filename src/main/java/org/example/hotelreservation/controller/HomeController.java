package org.example.hotelreservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Home", description = "Welcome page for the Hotel Reservation App")
public class HomeController {
    @GetMapping("/")
    @Operation(summary = "Welcome page", description = "Displays a welcome message for the Hotel Reservation App")
    public String home() { return "Welcome to the Hotel Reservation App!"; }
}
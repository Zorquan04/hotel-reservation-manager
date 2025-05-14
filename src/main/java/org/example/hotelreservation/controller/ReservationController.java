package org.example.hotelreservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.entity.Reservation;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Creating and managing reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a reservation", description = "Books a room for a user on a specified date")
    public ResponseEntity<Reservation> createReservation(@Parameter(description = "Reservation details") @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID", description = "Retrieves a reservation by its unique ID")
    public ResponseEntity<Reservation> getReservationById(@Parameter(description = "ID of the reservation") @PathVariable Long id) {
        return reservationService.getReservationById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    @Operation(summary = "Get reservations for a user", description = "Returns all reservations made by a specific user")
    public ResponseEntity<List<Reservation>> getUserReservations(@Parameter(description = "User entity for which to fetch reservations")
                                                                     @RequestBody User user) {
        return ResponseEntity.ok(reservationService.getUserReservations(user));
    }

    @GetMapping("/date")
    @Operation(summary = "Get reservations by date", description = "Fetches all reservations for a given date (YYYY-MM-DD)")
    public ResponseEntity<List<Reservation>> getReservationsByDate(@Parameter(description = "Date to filter reservations by", example = "2025-05-01")
                                                                       @RequestParam String date) {
        return ResponseEntity.ok(reservationService.getReservationsByDate(LocalDate.parse(date)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reservation", description = "Updates details of an existing reservation")
    public ResponseEntity<Reservation> updateReservation(@Parameter(description = "ID of the reservation to update") @PathVariable Long id,
                                                         @Parameter(description = "Updated reservation data") @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation", description = "Cancels a reservation by its ID")
    public ResponseEntity<Void> deleteReservation(@Parameter(description = "ID of the reservation to delete") @PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
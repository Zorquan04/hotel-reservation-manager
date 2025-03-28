package org.example.hotelreservation.controller;

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
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<List<Reservation>> getUserReservations(@RequestBody User user) {
        return ResponseEntity.ok(reservationService.getUserReservations(user));
    }

    @GetMapping("/date")
    public ResponseEntity<List<Reservation>> getReservationsByDate(@RequestParam String date) {
        return ResponseEntity.ok(reservationService.getReservationsByDate(LocalDate.parse(date)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
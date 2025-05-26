package org.example.hotelreservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.service.ReservationService;
import org.example.hotelreservation.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Creating and managing reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    private boolean isUnauthorized(Authentication auth) {
        return auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Long getCurrentUserId(Authentication auth) {
        return userService.findByUsername(auth.getName()).map(User::getId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
    }

    @PostMapping
    @Operation(summary = "Create a reservation", description = "Books a room for a user on a specified date")
    public ResponseEntity<ReservationResponseDTO> createReservation(@Parameter(description = "Reservation details") @Valid @RequestBody ReservationRequestDTO dto,
                                                                    @Parameter(hidden = true) Authentication auth) {
        if (isUnauthorized(auth) && !dto.getUserId().equals(getCurrentUserId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot book for another user");
        }
        return ResponseEntity.ok(reservationService.createReservation(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID", description = "Retrieves a reservation by its unique ID")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@Parameter(description = "ID of the reservation") @PathVariable Long id,
                                                                     @Parameter(hidden = true) Authentication auth) {
        ReservationResponseDTO dto = reservationService.getReservationById(id);
        if (isUnauthorized(auth) && !dto.getUserId().equals(getCurrentUserId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reservations for a user", description = "Returns all reservations made by a specific user")
    public ResponseEntity<List<ReservationResponseDTO>> getUserReservations(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                                                            @Parameter(hidden = true) Authentication auth) {
        if (isUnauthorized(auth) && !userId.equals(getCurrentUserId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    @GetMapping("/date")
    @Operation(summary = "Get reservations by date", description = "Fetches all reservations for a given date (YYYY-MM-DD)")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByDate(@Parameter(description = "Date to filter reservations by", example = "2025-05-01")
                                                                                  @RequestParam String date, @Parameter(hidden = true) Authentication auth) {
        LocalDate parsedDate = LocalDate.parse(date);

        if (isUnauthorized(auth)) {
            Long currentUserId = getCurrentUserId(auth);
            return ResponseEntity.ok(reservationService.getReservationsByDateAndUser(parsedDate, currentUserId));
        }

        return ResponseEntity.ok(reservationService.getReservationsByDate(parsedDate));
    }

    @GetMapping
    @Operation(summary = "List all reservations", description = "Returns all reservations (ADMIN) or only your own (USER)")
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(@Parameter(hidden = true) Authentication auth) {
        if (isUnauthorized(auth)) {
            Long userId = getCurrentUserId(auth);
            return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
        }
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reservation", description = "Updates details of an existing reservation")
    public ResponseEntity<ReservationResponseDTO> updateReservation(@Parameter(description = "ID of the reservation to update") @PathVariable Long id,
                                                                    @Parameter(description = "Updated reservation data") @Valid @RequestBody ReservationRequestDTO dto,
                                                                    @Parameter(hidden = true) Authentication auth) {
        ReservationResponseDTO existing = reservationService.getReservationById(id);
        if (isUnauthorized(auth) && !existing.getUserId().equals(getCurrentUserId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return ResponseEntity.ok(reservationService.updateReservation(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation", description = "Cancels a reservation by its ID")
    public ResponseEntity<Void> deleteReservation(@Parameter(description = "ID of the reservation to delete") @PathVariable Long id,
                                                  @Parameter(hidden = true) Authentication auth) {
        ReservationResponseDTO existing = reservationService.getReservationById(id);
        Long currentUserId = getCurrentUserId(auth);

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !existing.getUserId().equals(currentUserId)) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"); }

        reservationService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }
}
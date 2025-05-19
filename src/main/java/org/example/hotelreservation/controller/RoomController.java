package org.example.hotelreservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.dto.RoomRequestDTO;
import org.example.hotelreservation.dto.RoomResponseDTO;
import org.example.hotelreservation.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Operations for managing hotel rooms")
public class RoomController {
    private final RoomService roomService;

    private boolean isUnauthorized(Authentication auth) {
        return auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    @Operation(summary = "Add a new room", description = "Creates a new hotel room (ADMIN only)")
    public ResponseEntity<RoomResponseDTO> addRoom(@Parameter(description = "Room data to create") @RequestBody RoomRequestDTO dto,
                                                   @Parameter(hidden = true) Authentication auth) {
        if (isUnauthorized(auth)) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can add rooms"); }
        return ResponseEntity.ok(roomService.createRoom(dto));
    }

    @GetMapping
    @Operation(summary = "List all rooms", description = "Retrieves a list of all hotel rooms")
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() { return ResponseEntity.ok(roomService.getAllRoomsDto()); }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieves details of a room by its ID")
    public ResponseEntity<RoomResponseDTO> getRoomById(@Parameter(description = "ID of the room") @PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomDtoById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search rooms by standard", description = "Finds all rooms matching the given standard")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsByStandard(@Parameter(description = "Standard to filter rooms by", example = "Deluxe")
                                                                        @RequestParam String standard) {
        return ResponseEntity.ok(roomService.getRoomsByStandardDto(standard));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room", description = "Updates an existing room's details (ADMIN only)")
    public ResponseEntity<RoomResponseDTO> updateRoom(@Parameter(description = "ID of the room to update") @PathVariable Long id,
                                                      @Parameter(description = "Updated room data") @RequestBody RoomRequestDTO dto,
                                                      @Parameter(hidden = true) Authentication auth) {
        if (isUnauthorized(auth)) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can update rooms"); }
        return ResponseEntity.ok(roomService.updateRoom(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room", description = "Deletes a room by its ID (ADMIN only)")
    public ResponseEntity<Void> deleteRoom(@Parameter(description = "ID of the room to delete") @PathVariable Long id,
                                           @Parameter(hidden = true) Authentication auth) {
        if (isUnauthorized(auth)) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can delete rooms"); }
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
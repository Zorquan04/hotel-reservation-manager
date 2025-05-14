package org.example.hotelreservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.entity.Room;
import org.example.hotelreservation.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Operations for managing hotel rooms")
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Add a new room", description = "Creates a new hotel room with given details")
    public ResponseEntity<Room> addRoom(@Parameter(description = "Room data to create") @RequestBody Room room) {
        return ResponseEntity.ok(roomService.addRoom(room));
    }

    @GetMapping
    @Operation(summary = "List all rooms", description = "Retrieves a list of all hotel rooms")
    public ResponseEntity<List<Room>> getAllRooms() { return ResponseEntity.ok(roomService.getAllRooms()); }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieves details of a room by its ID")
    public ResponseEntity<Room> getRoomById(@Parameter(description = "ID of the room") @PathVariable Long id) {
        return roomService.getRoomById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search rooms by standard", description = "Finds all rooms matching the given standard")
    public ResponseEntity<List<Room>> getRoomsByStandard(@Parameter(description = "Standard to filter rooms by", example = "Deluxe")
                                                             @RequestParam String standard) {
        return ResponseEntity.ok(roomService.getRoomsByStandard(standard));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room", description = "Updates an existing room's details")
    public ResponseEntity<Room> updateRoom(@Parameter(description = "ID of the room to update") @PathVariable Long id,
                                           @Parameter(description = "Updated room data") @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room", description = "Deletes a room by its ID")
    public ResponseEntity<Void> deleteRoom(@Parameter(description = "ID of the room to delete") @PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
package org.example.hotelreservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.entity.Room;
import org.example.hotelreservation.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> addRoom(@RequestBody Room room) { return ResponseEntity.ok(roomService.addRoom(room)); }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() { return ResponseEntity.ok(roomService.getAllRooms()); }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Room>> getRoomsByStandard(@RequestParam String standard) {
        return ResponseEntity.ok(roomService.getRoomsByStandard(standard));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
package org.example.hotelreservation.service;

import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.entity.Room;
import org.example.hotelreservation.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room addRoom(Room room) { return roomRepository.save(room); }

    public List<Room> getAllRooms() { return roomRepository.findAll(); }

    public Optional<Room> getRoomById(Long id) { return roomRepository.findById(id); }

    public List<Room> getRoomsByStandard(String standard) { return roomRepository.findByStandard(standard); }

    public Room updateRoom(Long id, Room roomDetails) {
        return roomRepository.findById(id).map(room -> {
            room.setStandard(roomDetails.getStandard());
            room.setPrice(roomDetails.getPrice());
            return roomRepository.save(room);
        }).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public void deleteRoom(Long id) { roomRepository.deleteById(id); }
}
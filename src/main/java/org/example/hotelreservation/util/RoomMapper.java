package org.example.hotelreservation.util;

import org.example.hotelreservation.dto.RoomRequestDTO;
import org.example.hotelreservation.dto.RoomResponseDTO;
import org.example.hotelreservation.entity.Room;

public class RoomMapper {

    public static Room toEntity(RoomRequestDTO dto) {
        Room r = new Room();
        updateEntity(r, dto);
        return r;
    }

    public static RoomResponseDTO toResponse(Room room) {
        return new RoomResponseDTO(
                room.getId(),
                room.getNumber(),
                room.getStandard(),
                room.getPrice()
        );
    }

    public static void updateEntity(Room room, RoomRequestDTO dto) {
        room.setNumber(dto.getNumber());
        room.setStandard(dto.getStandard());
        room.setPrice(dto.getPrice());
    }
}
package org.example.hotelreservation.util;

import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.*;
import org.example.hotelreservation.repository.*;

public class ReservationMapper {

    public static Reservation toEntity(ReservationRequestDTO dto, UserRepository userRepo, RoomRepository roomRepo) {
        Reservation r = new Reservation();
        r.setUser(userRepo.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId())));
        r.setRoom(roomRepo.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found: " + dto.getRoomId())));
        r.setDate(dto.getDate());
        return r;
    }

    public static ReservationResponseDTO toResponse(Reservation r) {
        return new ReservationResponseDTO(r.getId(), r.getUser().getId(), r.getRoom().getId(), r.getDate());
    }

    public static void updateEntity(Reservation existing, ReservationRequestDTO dto, UserRepository userRepo, RoomRepository roomRepo) {
        existing.setDate(dto.getDate());
        existing.setUser(userRepo.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId())));
        existing.setRoom(roomRepo.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found: " + dto.getRoomId())));
    }
}
package org.example.hotelreservation.service;

import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.dto.RoomRequestDTO;
import org.example.hotelreservation.dto.RoomResponseDTO;
import org.example.hotelreservation.entity.Room;
import org.example.hotelreservation.repository.RoomRepository;
import org.example.hotelreservation.util.RoomMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomResponseDTO createRoom(RoomRequestDTO dto) {
        Room entity = RoomMapper.toEntity(dto);
        Room saved = roomRepository.save(entity);
        return RoomMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getAllRoomsDto() {
        return roomRepository.findAll().stream().map(RoomMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomResponseDTO getRoomDtoById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Room not found: " + id));
        return RoomMapper.toResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getRoomsByStandardDto(String standard) {
        return roomRepository.findByStandard(standard).stream().map(RoomMapper::toResponse).collect(Collectors.toList());
    }

    public RoomResponseDTO updateRoom(Long id, RoomRequestDTO dto) {
        Room room = roomRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Room not found: " + id));
        RoomMapper.updateEntity(room, dto);
        Room saved = roomRepository.save(room);
        return RoomMapper.toResponse(saved);
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) { throw new RuntimeException("Room not found: " + id); }
        roomRepository.deleteById(id);
    }
}
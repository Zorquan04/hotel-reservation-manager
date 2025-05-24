package org.example.hotelreservation.unitTest;

import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.Room;
import org.example.hotelreservation.repository.RoomRepository;
import org.example.hotelreservation.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    private RoomRepository roomRepo;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomRepo = mock(RoomRepository.class);
        roomService = new RoomService(roomRepo);
    }

    @Test @DisplayName("createRoom should save and return dto")
    void testCreateRoom() {
        RoomRequestDTO req = new RoomRequestDTO("101", "Standard", 100);
        when(roomRepo.save(any())).thenAnswer(i -> {
            Room r = i.getArgument(0);
            r.setId(1L);
            return r;
        });
        RoomResponseDTO resp = roomService.createRoom(req);
        assertEquals(1L, resp.getId());
        assertEquals("101", resp.getNumber());
    }

    @Test @DisplayName("getRoomDtoById – not found throws")
    void testGetRoomDtoById_notFound() {
        when(roomRepo.findById(2L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> roomService.getRoomDtoById(2L));
        assertTrue(ex.getMessage().contains("Room not found"));
    }

    @Test @DisplayName("getRoomsByStandardDto returns filtered")
    void testGetRoomsByStandard() {
        Room r = new Room();
        r.setId(5L);
        r.setStandard("Standard");
        when(roomRepo.findByStandard("Standard")).thenReturn(List.of(r));
        List<RoomResponseDTO> out = roomService.getRoomsByStandardDto("Standard");
        assertEquals(1, out.size());
        assertEquals(5L, out.getFirst().getId());
    }

    @Test @DisplayName("updateRoom – success and error")
    void testUpdateRoom() {
        Room existing = new Room();
        existing.setId(7L);
        when(roomRepo.findById(7L)).thenReturn(Optional.of(existing));
        when(roomRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomResponseDTO dto = roomService.updateRoom(7L, new RoomRequestDTO("101","Standard",200));
        assertEquals(7L, dto.getId());

        when(roomRepo.findById(8L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> roomService.updateRoom(8L, new RoomRequestDTO()));
    }

    @Test @DisplayName("deleteRoom throws if it doesn't exist")
    void testDeleteRoom() {
        when(roomRepo.existsById(9L)).thenReturn(true);
        assertDoesNotThrow(() -> roomService.deleteRoom(9L));
        when(roomRepo.existsById(10L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> roomService.deleteRoom(10L));
    }

    @Test @DisplayName("getRoomDtoById – returns dto if found")
    void testGetRoomDtoById_found() {
        Room room = new Room();
        room.setId(3L);
        room.setNumber("202");
        room.setStandard("Deluxe");
        room.setPrice(150);

        when(roomRepo.findById(3L)).thenReturn(Optional.of(room));

        RoomResponseDTO dto = roomService.getRoomDtoById(3L);

        assertEquals(3L, dto.getId());
        assertEquals("202", dto.getNumber());
        assertEquals("Deluxe", dto.getStandard());
        assertEquals(150, dto.getPrice());
    }

    @Test @DisplayName("getAllRoomsDto returns all rooms as DTOs")
    void testGetAllRoomsDto() {
        Room room1 = new Room();
        room1.setId(1L);
        room1.setNumber("101");
        room1.setStandard("Standard");
        room1.setPrice(100);

        Room room2 = new Room();
        room2.setId(2L);
        room2.setNumber("102");
        room2.setStandard("Deluxe");
        room2.setPrice(200);

        when(roomRepo.findAll()).thenReturn(List.of(room1, room2));

        List<RoomResponseDTO> dtos = roomService.getAllRoomsDto();

        assertEquals(2, dtos.size());
        assertEquals("101", dtos.get(0).getNumber());
        assertEquals("102", dtos.get(1).getNumber());
    }
}
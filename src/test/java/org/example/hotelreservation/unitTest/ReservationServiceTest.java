package org.example.hotelreservation.unitTest;

import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.*;
import org.example.hotelreservation.repository.*;
import org.example.hotelreservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository resRepo;
    private UserRepository userRepo;
    private RoomRepository roomRepo;
    private ReservationService svc;

    @BeforeEach
    void setUp() {
        resRepo = mock(ReservationRepository.class);
        userRepo = mock(UserRepository.class);
        roomRepo = mock(RoomRepository.class);
        svc = new ReservationService(resRepo, userRepo, roomRepo);
    }

    @Test @DisplayName("createReservation – OK")
    void testCreateReservation_success() {
        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 2L, LocalDate.now());
        when(resRepo.existsByRoomIdAndDate(2L, dto.getDate())).thenReturn(false);
        User u = new User();
        u.setId(1L);
        Room r = new Room();
        r.setId(2L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(roomRepo.findById(2L)).thenReturn(Optional.of(r));
        when(resRepo.save(any())).thenAnswer(i -> {
            Reservation x = i.getArgument(0);
            x.setId(100L);
            return x;
        });

        ReservationResponseDTO out = svc.createReservation(dto);
        assertEquals(100L, out.getId());
        verify(resRepo).save(any());
    }

    @Test @DisplayName("createReservation – conflict throws")
    void testCreateReservation_conflict() {
        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 2L, LocalDate.now());
        when(resRepo.existsByRoomIdAndDate(2L, dto.getDate())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> svc.createReservation(dto));
    }

    @Test @DisplayName("getReservationById – not found")
    void testGetReservationById_notFound() {
        when(resRepo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> svc.getReservationById(5L));
    }

    @Test @DisplayName("updateReservation - conflict after change")
    void testUpdateReservation_conflict() {
        Reservation existing = new Reservation();
        existing.setId(9L);
        existing.setDate(LocalDate.of(2025,1,1));
        Room oldR = new Room();
        oldR.setId(11L);
        existing.setRoom(oldR);

        when(resRepo.findById(9L)).thenReturn(Optional.of(existing));
        when(resRepo.existsByRoomIdAndDate(12L, LocalDate.of(2025,2,2))).thenReturn(true);

        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 12L, LocalDate.of(2025,2,2));
        assertThrows(RuntimeException.class, () -> svc.updateReservation(9L, dto));
    }

    @Test @DisplayName("deleteReservation - success and failure")
    void testDeleteReservation() {
        when(resRepo.existsById(20L)).thenReturn(true);
        assertDoesNotThrow(() -> svc.deleteReservation(20L));
        verify(resRepo).deleteById(20L);

        when(resRepo.existsById(21L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> svc.deleteReservation(21L));
    }
}
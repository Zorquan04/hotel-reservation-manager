package org.example.hotelreservation.unitTest;

import org.example.hotelreservation.dto.ReservationRequestDTO;
import org.example.hotelreservation.dto.ReservationResponseDTO;
import org.example.hotelreservation.entity.Reservation;
import org.example.hotelreservation.entity.Room;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.repository.ReservationRepository;
import org.example.hotelreservation.repository.RoomRepository;
import org.example.hotelreservation.repository.UserRepository;
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
        User u = new User(); u.setId(1L);
        Room r = new Room(); r.setId(2L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(roomRepo.findById(2L)).thenReturn(Optional.of(r));
        when(resRepo.save(any())).thenAnswer(i -> {
            Reservation x = i.getArgument(0);
            x.setId(100L);
            return x;
        });

        ReservationResponseDTO out = svc.createReservation(dto);
        assertEquals(100L, out.getId());
        assertEquals(1L, out.getUserId());
        assertEquals(2L, out.getRoomId());
        verify(resRepo).save(any());
    }

    @Test @DisplayName("createReservation – conflict throws")
    void testCreateReservation_conflict() {
        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 2L, LocalDate.now());
        when(resRepo.existsByRoomIdAndDate(2L, dto.getDate())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> svc.createReservation(dto));
    }

    @Test @DisplayName("getAllReservations returns list")
    void testGetAllReservations() {
        User u = new User(); u.setId(1L);
        Room r = new Room(); r.setId(2L);
        Reservation res1 = new Reservation();
        res1.setId(1L);
        res1.setUser(u);
        res1.setRoom(r);
        res1.setDate(LocalDate.of(2025,5,20));
        Reservation res2 = new Reservation();
        res2.setId(2L);
        res2.setUser(u);
        res2.setRoom(r);
        res2.setDate(LocalDate.of(2025,5,21));
        when(resRepo.findAll()).thenReturn(List.of(res1, res2));

        List<ReservationResponseDTO> list = svc.getAllReservations();
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
        verify(resRepo).findAll();
    }

    @Test @DisplayName("getReservationById – success")
    void testGetReservationById_success() {
        User u = new User(); u.setId(3L);
        Room r = new Room(); r.setId(4L);
        Reservation res = new Reservation();
        res.setId(5L);
        res.setUser(u);
        res.setRoom(r);
        res.setDate(LocalDate.of(2025,5,22));
        when(resRepo.findById(5L)).thenReturn(Optional.of(res));

        ReservationResponseDTO dto = svc.getReservationById(5L);
        assertEquals(5L, dto.getId());
        assertEquals(3L, dto.getUserId());
        assertEquals(4L, dto.getRoomId());
        verify(resRepo).findById(5L);
    }

    @Test @DisplayName("getReservationById – not found throws")
    void testGetReservationById_notFound() {
        when(resRepo.findById(6L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> svc.getReservationById(6L));
    }

    @Test @DisplayName("getReservationsByUser returns filtered list")
    void testGetReservationsByUser() {
        User u = new User(); u.setId(7L);
        Room r = new Room(); r.setId(8L);
        Reservation res = new Reservation();
        res.setId(10L);
        res.setUser(u);
        res.setRoom(r);
        res.setDate(LocalDate.of(2025,5,23));
        when(resRepo.findByUserId(7L)).thenReturn(List.of(res));

        List<ReservationResponseDTO> list = svc.getReservationsByUser(7L);
        assertEquals(1, list.size());
        assertEquals(10L, list.getFirst().getId());
        assertEquals(7L, list.getFirst().getUserId());
        verify(resRepo).findByUserId(7L);
    }

    @Test @DisplayName("getReservationsByDate returns filtered list")
    void testGetReservationsByDate() {
        LocalDate date = LocalDate.of(2025,5,24);
        User u = new User(); u.setId(9L);
        Room r = new Room(); r.setId(10L);
        Reservation res = new Reservation();
        res.setId(11L);
        res.setUser(u);
        res.setRoom(r);
        res.setDate(date);
        when(resRepo.findByDate(date)).thenReturn(List.of(res));

        List<ReservationResponseDTO> list = svc.getReservationsByDate(date);
        assertEquals(1, list.size());
        assertEquals(11L, list.getFirst().getId());
        assertEquals(date, list.getFirst().getDate());
        verify(resRepo).findByDate(date);
    }

    @Test @DisplayName("getReservationsByDateAndUser returns filtered list")
    void testGetReservationsByDateAndUser() {
        LocalDate date = LocalDate.of(2025,5,25);
        User u = new User(); u.setId(12L);
        Room r = new Room(); r.setId(13L);
        Reservation res = new Reservation();
        res.setId(14L);
        res.setUser(u);
        res.setRoom(r);
        res.setDate(date);
        when(resRepo.findByDateAndUserId(date, 12L)).thenReturn(List.of(res));

        List<ReservationResponseDTO> list = svc.getReservationsByDateAndUser(date, 12L);
        assertEquals(1, list.size());
        assertEquals(14L, list.getFirst().getId());
        assertEquals(12L, list.getFirst().getUserId());
        verify(resRepo).findByDateAndUserId(date, 12L);
    }

    @Test @DisplayName("updateReservation – change and no conflict")
    void testUpdateReservation_changeNoConflict() {
        User oldUser = new User(); oldUser.setId(17L);
        Room oldRoom = new Room(); oldRoom.setId(18L);
        Reservation existing = new Reservation();
        existing.setId(21L);
        existing.setUser(oldUser);
        existing.setRoom(oldRoom);
        existing.setDate(LocalDate.of(2025,6,2));

        when(resRepo.findById(21L)).thenReturn(Optional.of(existing));
        when(resRepo.existsByRoomIdAndDate(19L, LocalDate.of(2025,6,3))).thenReturn(false);
        User newUser = new User(); newUser.setId(17L);
        Room newRoom = new Room(); newRoom.setId(19L);
        when(userRepo.findById(17L)).thenReturn(Optional.of(newUser));
        when(roomRepo.findById(19L)).thenReturn(Optional.of(newRoom));

        ReservationRequestDTO dto = new ReservationRequestDTO(17L, 19L, LocalDate.of(2025,6,3));
        when(resRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        ReservationResponseDTO resp = svc.updateReservation(21L, dto);
        assertEquals(21L, resp.getId());
        verify(resRepo).existsByRoomIdAndDate(19L, LocalDate.of(2025,6,3));
        verify(resRepo).save(any());
    }

    @Test @DisplayName("updateReservation – conflict after change throws")
    void testUpdateReservation_conflict() {
        Reservation existing = new Reservation();
        existing.setId(9L);
        existing.setUser(new User());
        existing.setRoom(new Room());
        existing.setDate(LocalDate.of(2025,1,1));

        when(resRepo.findById(9L)).thenReturn(Optional.of(existing));
        when(resRepo.existsByRoomIdAndDate(12L, LocalDate.of(2025,2,2))).thenReturn(true);

        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 12L, LocalDate.of(2025,2,2));
        assertThrows(RuntimeException.class, () -> svc.updateReservation(9L, dto));
    }

    @Test @DisplayName("deleteReservation – success and failure")
    void testDeleteReservation() {
        when(resRepo.existsById(20L)).thenReturn(true);
        assertDoesNotThrow(() -> svc.deleteReservation(20L));
        verify(resRepo).deleteById(20L);

        when(resRepo.existsById(21L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> svc.deleteReservation(21L));
    }
}
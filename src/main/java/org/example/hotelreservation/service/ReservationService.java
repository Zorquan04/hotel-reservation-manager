package org.example.hotelreservation.service;

import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.Reservation;
import org.example.hotelreservation.repository.*;
import org.example.hotelreservation.util.ReservationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public ReservationResponseDTO createReservation(ReservationRequestDTO dto) {
        if (reservationRepository.existsByRoomIdAndDate(dto.getRoomId(), dto.getDate())) {
            throw new RuntimeException("Room is already booked on this date.");
        }
        Reservation r = ReservationMapper.toEntity(dto, userRepository, roomRepository);
        Reservation saved = reservationRepository.save(r);
        return ReservationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream().map(ReservationMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(Long id) {
        Reservation r = reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found: " + id));
        return ReservationMapper.toResponse(r);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream().map(ReservationMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDate(date).stream().map(ReservationMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByDateAndUser(LocalDate date, Long userId) {
        return reservationRepository.findByDateAndUserId(date, userId).stream().map(ReservationMapper::toResponse).collect(Collectors.toList());
    }


    public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO dto) {
        Reservation existing = reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found: " + id));

        boolean isRoomOrDateChanged = !existing.getRoom().getId().equals(dto.getRoomId()) || !existing.getDate().equals(dto.getDate());

        if (isRoomOrDateChanged) {
            boolean conflict = reservationRepository.existsByRoomIdAndDate(dto.getRoomId(), dto.getDate());
            if (conflict) { throw new RuntimeException("Room is already booked on this date."); }
        }

        ReservationMapper.updateEntity(existing, dto, userRepository, roomRepository);
        Reservation saved = reservationRepository.save(existing);
        return ReservationMapper.toResponse(saved);
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) { throw new RuntimeException("Reservation not found: " + id); }
        reservationRepository.deleteById(id);
    }
}
package org.example.hotelreservation.repository;

import org.example.hotelreservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDate(LocalDate date);
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByDateAndUserId(LocalDate date, Long userId);
    boolean existsByRoomIdAndDate(Long roomId, LocalDate date);
}
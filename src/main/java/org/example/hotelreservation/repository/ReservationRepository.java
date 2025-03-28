package org.example.hotelreservation.repository;

import org.example.hotelreservation.entity.Reservation;
import org.example.hotelreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByDate(LocalDate date);
}
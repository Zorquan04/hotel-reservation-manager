package org.example.hotelreservation.service;

import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.entity.Reservation;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getUserReservations(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    public Optional<Reservation> getReservationById(Long id) { return reservationRepository.findById(id); }

    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setDate(reservationDetails.getDate());
            reservation.setRoom(reservationDetails.getRoom());
            return reservationRepository.save(reservation);
        }).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public void deleteReservation(Long id) { reservationRepository.deleteById(id); }
}
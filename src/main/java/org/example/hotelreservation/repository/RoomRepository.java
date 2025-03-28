package org.example.hotelreservation.repository;

import org.example.hotelreservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStandard(String standard);
}
package org.example.hotelreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.example.hotelreservation.entity")
@EnableJpaRepositories("org.example.hotelreservation.repository")
public class HotelReservationApplication {
    public static void main(String[] args) { SpringApplication.run(HotelReservationApplication.class, args); }
}
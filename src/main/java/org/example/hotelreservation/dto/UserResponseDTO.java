package org.example.hotelreservation.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String role;
    private String password;
    private List<Long> reservations;
}
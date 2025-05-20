package org.example.hotelreservation.util;

import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.Reservation;
import org.example.hotelreservation.entity.Role;
import org.example.hotelreservation.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;

public class UserMapper {

    public static void updateEntity(User user, UserRequestDTO dto, PasswordEncoder encoder) {
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole()));
    }

    public static User toEntity(UserRequestDTO dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setRole(Role.valueOf(dto.getRole()));
        return u;
    }

    public static UserResponseDTO toResponse(User user) {
        UserResponseDTO r = new UserResponseDTO();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setRole(user.getRole().name());
        r.setPassword(user.getPassword());
        r.setReservations(user.getReservations().stream().map(Reservation::getId).collect(Collectors.toList()));
        return r;
    }

    public static UserSummaryDTO toSummary(User user) { return new UserSummaryDTO(user.getId(), user.getUsername()); }
}
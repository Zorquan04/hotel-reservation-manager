package org.example.hotelreservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.dto.UserRequestDTO;
import org.example.hotelreservation.dto.UserResponseDTO;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.service.UserService;
import org.example.hotelreservation.util.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User registration and management")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Parameter(description = "User data to register") @Valid @RequestBody UserRequestDTO dto
    ) {
        dto.setRole("USER");
        UserResponseDTO created = userService.createUser(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) { return ResponseEntity.ok(userService.getUserDTOById(id)); }

        String currentUsername = auth.getName();
        if (userService.findByUsername(currentUsername).map(User::getId).filter(myId -> myId.equals(id)).isPresent()) {
            return ResponseEntity.ok(userService.getUserDTOById(id));
        }

        return ResponseEntity.ok(userService.getAllUsersSummary()
                .stream()
                .filter(sum -> sum.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Find user by username", description = "Returns either full DTO (admin or self) or summary")
    public ResponseEntity<?> findByUsername(@PathVariable String username, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String current = auth.getName();

        if (isAdmin || current.equals(username)) {
            return ResponseEntity.ok(userService.getUserDtoByUsername(username));
        } else {
            return ResponseEntity.ok(userService.getUserSummaryByUsername(username));
        }
    }

    @GetMapping
    @Operation(summary = "List users")
    public ResponseEntity<?> listUsers(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            List<UserResponseDTO> all = userService.getAllUsers().stream().map(UserMapper::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(all);
        } else {
            return ResponseEntity.ok(userService.getAllUsersSummary());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user information")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Updated user data") @RequestBody User user
    ) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
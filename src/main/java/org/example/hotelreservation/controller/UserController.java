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

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
    }

    private Long getCurrentUserId(Authentication auth) {
        return userService.findByUsername(auth.getName()).map(User::getId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
    }

    private boolean isSelf(Authentication auth, Long id) { return getCurrentUserId(auth).equals(id); }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Parameter(description = "User data to register") @Valid @RequestBody UserRequestDTO dto,
            @Parameter(hidden = true) Authentication auth
    ) {
        if (auth != null && auth.isAuthenticated() && auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_USER"))
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only anonymous or ADMIN may register new users");
        }

        dto.setRole("USER");
        UserResponseDTO created = userService.createUser(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Long id,
            @Parameter(hidden = true) Authentication auth
    ) {
        if (isAdmin(auth) || isSelf(auth, id)) { return ResponseEntity.ok(userService.getUserDTOById(id)); }

        return ResponseEntity.ok(userService.getAllUsersSummary()
                .stream()
                .filter(sum -> sum.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Find user by username", description = "Returns either full DTO (admin or self) or summary")
    public ResponseEntity<?> findByUsername(
            @Parameter(description = "Username of the user to find") @PathVariable String username,
            @Parameter(hidden = true) Authentication auth
    ) {
        boolean isSelf = auth.getName().equals(username);

        if (isAdmin(auth) || isSelf) { return ResponseEntity.ok(userService.getUserDtoByUsername(username)); }

        return ResponseEntity.ok(userService.getUserSummaryByUsername(username));
    }

    @GetMapping
    @Operation(summary = "List users")
    public ResponseEntity<?> listUsers(@Parameter(hidden = true) Authentication auth) {
        if (isAdmin(auth)) {
            List<UserResponseDTO> all = userService.getAllUsers().stream().map(UserMapper::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(all);
        }
        return ResponseEntity.ok(userService.getAllUsersSummary());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user information")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "Updated user data") @Valid @RequestBody UserRequestDTO dto,
            @Parameter(hidden = true) Authentication auth
    ) {
        boolean admin = isAdmin(auth);
        Long currentId = getCurrentUserId(auth);

        if (!admin && !currentId.equals(id)) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"); }

        if (!admin) {
            String originalRole = userService.getUserDTOById(id).getRole();
            dto.setRole(originalRole);
        }
        UserResponseDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id,
            @Parameter(hidden = true) Authentication auth
    ) {
        if (!isAdmin(auth)) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"); }

        if (getCurrentUserId(auth).equals(id)) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete your own account"); }

        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
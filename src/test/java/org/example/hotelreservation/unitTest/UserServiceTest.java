package org.example.hotelreservation.unitTest;

import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.Role;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.repository.UserRepository;
import org.example.hotelreservation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepo;
    private PasswordEncoder pwEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);
        pwEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepo, pwEncoder);
    }

    @Test @DisplayName("registerUser should encode and save")
    void testRegisterUser() {
        User in = new User();
        in.setPassword("raw");
        when(pwEncoder.encode("raw")).thenReturn("ENC");
        when(userRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        User out = userService.registerUser(in);
        assertEquals("ENC", out.getPassword());
        verify(userRepo).save(out);
    }

    @Test @DisplayName("createUser returns UserResponseDTO")
    void testCreateUser() {
        UserRequestDTO dto = new UserRequestDTO("u1","p1","USER");
        when(pwEncoder.encode(any())).thenReturn("E");
        when(userRepo.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(10L);
            return u;
        });

        UserResponseDTO resp = userService.createUser(dto);
        assertEquals(10, resp.getId());
        assertEquals("u1", resp.getUsername());
        assertNotNull(resp.getRole());
        verify(userRepo).save(any(User.class));
    }

    @Test @DisplayName("findByUsername returns optional")
    void testFindByUsername() {
        User u2 = new User();
        u2.setUsername("u2");
        when(userRepo.findByUsername("u2")).thenReturn(Optional.of(u2));
        Optional<User> got = userService.findByUsername("u2");
        assertTrue(got.isPresent());
        assertEquals("u2", got.get().getUsername());
    }

    @Test @DisplayName("getUserDtoByUsername – not found throws RuntimeException")
    void testGetUserDtoByUsername_notFound() {
        when(userRepo.findByUsername("u3")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserDtoByUsername("u3"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test @DisplayName("getAllUsersSummary returns a list of summary")
    void testGetAllUsersSummary() {
        User u4 = new User();
        u4.setId(1L);
        u4.setUsername("u4");
        u4.setRole(Role.valueOf("USER"));
        when(userRepo.findAll()).thenReturn(List.of(u4));
        List<UserSummaryDTO> list = userService.getAllUsersSummary();
        assertEquals(1, list.size());
        assertEquals("u4", list.getFirst().getUsername());
        verify(userRepo).findAll();
    }

    @Test @DisplayName("updateUser – missing throws")
    void testUpdateUser_notFound() {
        when(userRepo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateUser(5L, new UserRequestDTO()));
    }

    @Test @DisplayName("deleteUser – delegates to the repo")
    void testDeleteUser() {
        userService.deleteUser(99L);
        verify(userRepo).deleteById(99L);
    }
}
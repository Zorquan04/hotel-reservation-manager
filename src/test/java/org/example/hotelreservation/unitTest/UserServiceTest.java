package org.example.hotelreservation.unitTest;

import org.example.hotelreservation.dto.*;
import org.example.hotelreservation.entity.Role;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.repository.UserRepository;
import org.example.hotelreservation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
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
        User u = new User();
        u.setUsername("u2");
        when(userRepo.findByUsername("u2")).thenReturn(Optional.of(u));
        Optional<User> got = userService.findByUsername("u2");
        assertTrue(got.isPresent());
        assertEquals("u2", got.get().getUsername());
    }

    @Test @DisplayName("getUserDtoByUsername – success")
    void testGetUserDtoByUsername_success() {
        User u = new User();
        u.setId(5L);
        u.setUsername("u3");
        u.setRole(Role.valueOf("USER"));
        when(userRepo.findByUsername("u3")).thenReturn(Optional.of(u));

        UserResponseDTO dto = userService.getUserDtoByUsername("u3");
        assertEquals(5L, dto.getId());
        assertEquals("u3", dto.getUsername());
        assertEquals("USER", dto.getRole());
    }

    @Test @DisplayName("getUserDtoByUsername – not found throws RuntimeException")
    void testGetUserDtoByUsername_notFound() {
        when(userRepo.findByUsername("u4")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserDtoByUsername("u4"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test @DisplayName("getUserSummaryByUsername – success")
    void testGetUserSummaryByUsername_success() {
        User u = new User();
        u.setId(6L);
        u.setUsername("u5");
        when(userRepo.findByUsername("u5")).thenReturn(Optional.of(u));

        UserSummaryDTO summary = userService.getUserSummaryByUsername("u5");
        assertEquals(6L, summary.getId());
        assertEquals("u5", summary.getUsername());
    }

    @Test @DisplayName("getUserSummaryByUsername – not found throws")
    void testGetUserSummaryByUsername_notFound() {
        when(userRepo.findByUsername("u6")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserSummaryByUsername("u6"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test @DisplayName("getUserById returns optional")
    void testGetUserById() {
        User u = new User();
        u.setId(7L);
        when(userRepo.findById(7L)).thenReturn(Optional.of(u));

        Optional<User> opt = userService.getUserById(7L);
        assertTrue(opt.isPresent());
        assertEquals(7L, opt.get().getId());
        verify(userRepo).findById(7L);
    }

    @Test @DisplayName("getUserDTOById – success")
    void testGetUserDTOById_success() {
        User u = new User();
        u.setId(8L);
        u.setUsername("u8");
        u.setRole(Role.valueOf("USER"));
        when(userRepo.findById(8L)).thenReturn(Optional.of(u));

        UserResponseDTO dto = userService.getUserDTOById(8L);
        assertEquals(8L, dto.getId());
        assertEquals("u8", dto.getUsername());
    }

    @Test @DisplayName("getUserDTOById – not found throws")
    void testGetUserDTOById_notFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserDTOById(99L));
    }

    @Test @DisplayName("getAllUsers returns list")
    void testGetAllUsers() {
        User u = new User();
        u.setId(9L);
        User uu = new User();
        uu.setId(10L);
        when(userRepo.findAll()).thenReturn(Arrays.asList(u, uu));

        List<User> list = userService.getAllUsers();
        assertEquals(2, list.size());
        assertEquals(9L, list.getFirst().getId());
        verify(userRepo).findAll();
    }

    @Test @DisplayName("getAllUsersSummary returns a list of summary")
    void testGetAllUsersSummary() {
        User u = new User();
        u.setId(1L);
        u.setUsername("u11");
        u.setRole(Role.valueOf("USER"));
        when(userRepo.findAll()).thenReturn(List.of(u));
        List<UserSummaryDTO> list = userService.getAllUsersSummary();
        assertEquals(1, list.size());
        assertEquals("u11", list.getFirst().getUsername());
        verify(userRepo).findAll();
    }

    @Test @DisplayName("updateUser – success")
    void testUpdateUser_success() {
        User existing = new User();
        existing.setId(3L);
        existing.setUsername("old");
        existing.setPassword("oldpw");
        when(userRepo.findById(3L)).thenReturn(Optional.of(existing));
        when(pwEncoder.encode(any())).thenReturn("newpw");
        when(userRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        UserRequestDTO dto = new UserRequestDTO("old", "new", "USER");
        UserResponseDTO resp = userService.updateUser(3L, dto);

        assertEquals(3L, resp.getId());
        assertEquals("old", resp.getUsername());
        assertEquals("USER", resp.getRole());
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        assertEquals("newpw", cap.getValue().getPassword());
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
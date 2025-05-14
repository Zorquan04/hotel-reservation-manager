package org.example.hotelreservation.service;

import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.dto.UserRequestDTO;
import org.example.hotelreservation.dto.UserResponseDTO;
import org.example.hotelreservation.dto.UserSummaryDTO;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.repository.UserRepository;
import org.example.hotelreservation.util.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User entity = UserMapper.toEntity(dto);
        User saved = registerUser(entity);
        return UserMapper.toResponse(saved);
    }

    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }

    public UserResponseDTO getUserDtoByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found with username = " + username));
        return UserMapper.toResponse(user);
    }

    public UserSummaryDTO getUserSummaryByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found with username = " + username));
        return UserMapper.toSummary(user);
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }

    public List<UserSummaryDTO> getAllUsersSummary() {
        return userRepository.findAll().stream().map(UserMapper::toSummary).collect(Collectors.toList());
    }

    public Optional<User> getUserById(Long id) { return userRepository.findById(id); }

    public UserResponseDTO getUserDTOById(Long id) {
        User u = getUserById(id).orElseThrow(() -> new RuntimeException("User not found with id = " + id));
        return UserMapper.toResponse(u);
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) { userRepository.deleteById(id); }
}
package org.example.hotelreservation.service;

import lombok.RequiredArgsConstructor;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }

    public List<User> getAllUsers() { return userRepository.findAll(); }

    public Optional<User> getUserById(Long id) { return userRepository.findById(id); }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) { userRepository.deleteById(id); }
}
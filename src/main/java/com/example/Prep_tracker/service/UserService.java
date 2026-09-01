package com.example.Prep_tracker.service;

import com.example.Prep_tracker.dto.LoginRequest;
import com.example.Prep_tracker.dto.RegisterRequest;
import com.example.Prep_tracker.dto.UserResponse;
import com.example.Prep_tracker.entity.User;
import com.example.Prep_tracker.exception.EmailAlreadyExistsException;
import com.example.Prep_tracker.exception.InvalidCredentialsException;
import com.example.Prep_tracker.mapper.UserMapper;
import com.example.Prep_tracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.email(), hashedPassword);
        User saved = userRepository.save(user);

        return UserMapper.toResponse(saved);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    public User getCurrentUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
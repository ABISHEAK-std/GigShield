package com.allixia.service;

import com.allixia.dto.AuthResponse;
import com.allixia.dto.LoginRequest;
import com.allixia.dto.RegisterRequest;
import com.allixia.dto.WorkerStatusResponse;
import com.allixia.entity.User;
import com.allixia.entity.WorkerStatus;
import com.allixia.exception.DuplicateException;
import com.allixia.exception.NotFoundException;
import com.allixia.exception.UnauthorizedException;
import com.allixia.repository.UserRepository;
import com.allixia.repository.WorkerStatusRepository;
import com.allixia.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final WorkerStatusRepository workerStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateException("Phone number already registered");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Email already registered");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        user = userRepository.save(user);
        
        // Create worker status
        WorkerStatus workerStatus = new WorkerStatus();
        workerStatus.setUser(user);
        workerStatus.setIsActive(false);
        workerStatusRepository.save(workerStatus);
        
        String token = jwtTokenProvider.generateToken(user.getId().toString(), user.getPhone());
        
        return new AuthResponse(token, user.getId().toString(), user.getName(), user.getEmail());
    }
    
    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new NotFoundException("User not found with phone: " + request.getPhone()));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        String token = jwtTokenProvider.generateToken(user.getId().toString(), user.getPhone());
        
        return new AuthResponse(token, user.getId().toString(), user.getName(), user.getEmail());
    }
    
    @Transactional
    public WorkerStatusResponse updateWorkerStatus(UUID userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        WorkerStatus status = workerStatusRepository.findByUserId(userId)
                .orElseGet(() -> {
                    WorkerStatus newStatus = new WorkerStatus();
                    newStatus.setUser(user);
                    return newStatus;
                });
        
        status.setIsActive(isActive);
        if (isActive) {
            status.setLastActiveAt(LocalDateTime.now());
        }
        
        status = workerStatusRepository.save(status);
        
        return new WorkerStatusResponse(
                userId.toString(),
                status.getIsActive(),
                status.getLastActiveAt() != null ? status.getLastActiveAt().toString() : null
        );
    }
    
    public WorkerStatusResponse getWorkerStatus(UUID userId) {
        WorkerStatus status = workerStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Worker status not found"));
        
        return new WorkerStatusResponse(
                userId.toString(),
                status.getIsActive(),
                status.getLastActiveAt() != null ? status.getLastActiveAt().toString() : null
        );
    }
}

package com.taskflow.taskflow_backend.service;

import com.taskflow.taskflow_backend.dto.request.LoginRequest;
import com.taskflow.taskflow_backend.dto.request.RegisterRequest;
import com.taskflow.taskflow_backend.dto.response.AuthResponse;
import com.taskflow.taskflow_backend.dto.response.UserResponse;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.Role;
import com.taskflow.taskflow_backend.exception.UserAlreadyExistsException;
import com.taskflow.taskflow_backend.repository.UserRepository;
import com.taskflow.taskflow_backend.security.CustomUserDetails;
import com.taskflow.taskflow_backend.security.JwtService;
import com.taskflow.taskflow_backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User already exists with this email.");
        }
        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .gender(request.gender())
                .profileImgUrl("/images/default-profile-img.png")
                .roles(Set.of(Role.USER))
                .isDeleted(false)
                .build();

        User savedUser = userRepository.save(newUser);
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String jwtToken = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .jwtToken(jwtToken)
                .userResponse(mapToUserResponse(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(customUserDetails);
        return AuthResponse.builder()
                .jwtToken(jwtToken)
                .userResponse(mapToUserResponse(customUserDetails.getUser()))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .gender(user.getGender())
                .profileImgUrl(user.getProfileImgUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

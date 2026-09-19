package com.taskflow.taskflow_backend.service;

import com.taskflow.taskflow_backend.dto.request.Auth.LoginRequest;
import com.taskflow.taskflow_backend.dto.request.Auth.RegisterRequest;
import com.taskflow.taskflow_backend.dto.response.AuthResponse;
import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.enums.Gender;
import com.taskflow.taskflow_backend.enums.Role;
import com.taskflow.taskflow_backend.exception.UserAlreadyExistsException;
import com.taskflow.taskflow_backend.repository.UserRepository;
import com.taskflow.taskflow_backend.security.CustomUserDetails;
import com.taskflow.taskflow_backend.security.JwtService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service class Test")
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("Ravi")
                .email("ravi@email.com")
                .password("hashedPassword")
                .gender(Gender.MALE)
                .profileImgUrl("/images/default-profile-img.png")
                .roles(Set.of(Role.USER))
                .build();

        registerRequest = new RegisterRequest(
                "Ravi",
                "ravi@email.com",
                "ravi@123",
                Gender.MALE
        );

        loginRequest = new LoginRequest(
                "ravi@email.com",
                "ravi@123"
        );
    }

    @Nested
    @DisplayName("register method")
    class WhenRegisterTest {

        @Test
        @DisplayName("register - should return UserAlreadyExistsException when user already exists")
        void register_shouldReturnUserAlreadyExistsException_whenUserAlreadyExists() {

            Mockito.when(userRepository.existsByEmail("ravi@email.com")).thenReturn(true);

            Assertions.assertThatThrownBy(() -> userService.register(registerRequest))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists with this email.");

            Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any(User.class));

            Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.anyString());

            Mockito.verify(jwtService, Mockito.never()).generateToken(ArgumentMatchers.any());
        }

        @Test
        @DisplayName("register - should register successfully when email is new")
        void register_shouldRegisterSuccessfully_whenEmailIsNew(){
            Mockito.when(userRepository.existsByEmail("ravi@email.com")).thenReturn(false);
            Mockito.when(passwordEncoder.encode("ravi@123")).thenReturn("hashedPassword");
            Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(testUser);
            Mockito.when(jwtService.generateToken(ArgumentMatchers.any(CustomUserDetails.class))).thenReturn("fake.jwt.token");

            AuthResponse response = userService.register(registerRequest);

            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.jwtToken()).isEqualTo("fake.jwt.token");
            Assertions.assertThat(response.userResponse().email()).isEqualTo("ravi@email.com");
            Assertions.assertThat(response.userResponse().username()).isEqualTo("Ravi");

            Mockito.verify(passwordEncoder,Mockito.times(1)).encode("ravi@123");
            Mockito.verify(userRepository,Mockito.times(1)).save(ArgumentMatchers.any(User.class));
            Mockito.verify(jwtService,Mockito.times(1)).generateToken(ArgumentMatchers.any(CustomUserDetails.class));
        }
    }

    @Nested
    @DisplayName("Login method")
    class WhenLoginTest{

        @Test
        @DisplayName("login - should login successfully with correct credentials")
        void login_shouldLoginSuccessfully_withCorrectCredentials(){
            Authentication authentication = mock(Authentication.class);
            Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(testUser));
            Mockito.when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("fake.jwt.token");

            AuthResponse response = userService.login(loginRequest);

            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.jwtToken()).isEqualTo("fake.jwt.token");
            Assertions.assertThat(response.userResponse().email()).isEqualTo("ravi@email.com");

            Mockito.verify(authenticationManager,Mockito.times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            Mockito.verify(jwtService,Mockito.times(1)).generateToken(any(CustomUserDetails.class));
        }

        @Test
        @DisplayName("login - should throw exception when credentials are wrong")
        void login_shouldThrowException_whenCredentialsAreWrong(){
            Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            Assertions.assertThatThrownBy(() -> userService.login(loginRequest)).isInstanceOf(BadCredentialsException.class);
            Mockito.verify(jwtService,Mockito.never()).generateToken(any());
        }

    }
}
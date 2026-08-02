package com.facilityflow.service;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.LoginRequest;
import com.facilityflow.entity.Role;
import com.facilityflow.entity.User;
import com.facilityflow.exception.DuplicateResourceException;
import com.facilityflow.exception.InvalidCredentialsException;
import com.facilityflow.mapper.UserMapper;
import com.facilityflow.repository.RefreshTokenRepository;
import com.facilityflow.repository.UserRepository;
import com.facilityflow.security.JwtService;
import com.facilityflow.security.UserPrincipal;
import com.facilityflow.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserMapper userMapper;
    @Mock private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenExpirationMs", 604800000L);
    }

    @Test
    void register_throwsDuplicateResourceException_whenEmailAlreadyExists() {
        var request = com.facilityflow.dto.request.RegisterRequest.builder()
                .fullName("Test User")
                .email("existing@facilityflow.com")
                .password("Password123")
                .role(Role.EMPLOYEE)
                .build();

        when(userRepository.existsByEmail("existing@facilityflow.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void login_throwsInvalidCredentialsException_whenUserNotFound() {
        LoginRequest request = LoginRequest.builder()
                .email("ghost@facilityflow.com")
                .password("whatever")
                .build();

        when(userRepository.findByEmail("ghost@facilityflow.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_throwsInvalidCredentialsException_whenPasswordIncorrect() {
        User user = User.builder().fullName("Karan Verma").email("employee@facilityflow.com")
                .password("hashed").role(Role.EMPLOYEE).enabled(true).build();
        user.setId(4L);

        LoginRequest request = LoginRequest.builder()
                .email("employee@facilityflow.com")
                .password("wrongpassword")
                .build();

        when(userRepository.findByEmail("employee@facilityflow.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}

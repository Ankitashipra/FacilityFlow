package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.LoginRequest;
import com.facilityflow.dto.request.RefreshTokenRequest;
import com.facilityflow.dto.request.RegisterRequest;
import com.facilityflow.dto.response.AuthResponse;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.entity.RefreshToken;
import com.facilityflow.entity.User;
import com.facilityflow.exception.AccountLockedException;
import com.facilityflow.exception.DuplicateResourceException;
import com.facilityflow.exception.InvalidCredentialsException;
import com.facilityflow.exception.InvalidTokenException;
import com.facilityflow.mapper.UserMapper;
import com.facilityflow.repository.RefreshTokenRepository;
import com.facilityflow.repository.UserRepository;
import com.facilityflow.security.JwtService;
import com.facilityflow.security.UserPrincipal;
import com.facilityflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles registration, login, refresh-token rotation, and logout.
 * <p>
 * Access tokens are short-lived signed JWTs (see {@link JwtService}).
 * Refresh tokens are opaque random strings persisted in the database so
 * they can be revoked server-side (on logout, or in bulk if compromised).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AuditService auditService;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .designation(request.getDesignation())
                .department(request.getDepartment())
                .role(request.getRole())
                .enabled(true)
                .build();

        user = userRepository.save(user);
        auditService.record(AuditAction.CREATE, "User", user.getId(), "New user registered: " + user.getEmail());

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.isAccountLocked()) {
            throw new AccountLockedException("This account has been locked. Contact an administrator.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        auditService.record(AuditAction.LOGIN, "User", user.getId(), "User logged in: " + user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token not recognized"));

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token has expired or was revoked. Please log in again.");
        }

        User user = storedToken.getUser();

        // Rotate: revoke the old refresh token and issue a brand-new pair.
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return buildAuthResponse(user);
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            auditService.record(AuditAction.LOGOUT, "User", token.getUser().getId(), "User logged out");
        });
    }

    private AuthResponse buildAuthResponse(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshTokenValue = generateAndStoreRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .user(userMapper.toResponse(user))
                .build();
    }

    private String generateAndStoreRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString() + "-" + UUID.randomUUID();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }
}

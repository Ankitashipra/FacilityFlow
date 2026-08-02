package com.facilityflow.service;

import com.facilityflow.dto.request.LoginRequest;
import com.facilityflow.dto.request.RefreshTokenRequest;
import com.facilityflow.dto.request.RegisterRequest;
import com.facilityflow.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    void logout(String refreshToken);
}

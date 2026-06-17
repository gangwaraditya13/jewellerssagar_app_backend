package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.AuthResponse;
import com.sagar.jewellery.dto.LoginRequest;
import com.sagar.jewellery.dto.RefreshTokenRequest;
import com.sagar.jewellery.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    void logout(String refreshToken);

    //todo reset password
}

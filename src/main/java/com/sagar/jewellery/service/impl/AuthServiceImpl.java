package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.AuthResponse;
import com.sagar.jewellery.dto.LoginRequest;
import com.sagar.jewellery.dto.RefreshTokenRequest;
import com.sagar.jewellery.dto.RegisterRequest;
import com.sagar.jewellery.exception.DuplicateEmailException;
import com.sagar.jewellery.exception.InvalidTokenException;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.exception.UnauthorizedException;
import com.sagar.jewellery.model.Admin;
import com.sagar.jewellery.model.JewelleryMaker;
import com.sagar.jewellery.model.RefreshToken;
import com.sagar.jewellery.model.User;
import com.sagar.jewellery.model.enums.UserRole;
import com.sagar.jewellery.repository.AdminRepository;
import com.sagar.jewellery.repository.JewelleryMakerRepository;
import com.sagar.jewellery.repository.UserRepository;
import com.sagar.jewellery.security.JwtUtil;
import com.sagar.jewellery.security.RefreshTokenService;
import com.sagar.jewellery.service.AuthService;
import com.sagar.jewellery.dto.ForgotPasswordRequest;
import com.sagar.jewellery.dto.ResetPasswordRequest;
import com.sagar.jewellery.model.OtpToken;
import com.sagar.jewellery.repository.OtpTokenRepository;
import com.sagar.jewellery.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JewelleryMakerRepository makerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email) || makerRepository.existsByEmail(email) || adminRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email " + email + " is already in use");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String userId;
        UserRole role = request.getRole();

        if (role == UserRole.USER) {
            User user = User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .role(UserRole.USER)
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            User saved = userRepository.save(user);
            userId = saved.getId();
        } else if (role == UserRole.MAKER) {
            JewelleryMaker maker = JewelleryMaker.builder()
                    .email(email)
                    .password(encodedPassword)
                    .role(UserRole.MAKER)
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .isApproved(false)
                    .shopName(request.getShopName())
                    .address(request.getAddress())
                    .createdAt(LocalDateTime.now())
                    .build();
            JewelleryMaker saved = makerRepository.save(maker);
            userId = saved.getId();
        } else if (role == UserRole.ADMIN) {
            Admin admin = Admin.builder()
                    .email(email)
                    .password(encodedPassword)
                    .role(UserRole.ADMIN)
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .createdAt(LocalDateTime.now())
                    .build();
            Admin saved = adminRepository.save(admin);
            userId = saved.getId();
        } else {
            throw new IllegalArgumentException("Unsupported user role");
        }

        String accessToken = jwtUtil.generateToken(userId, email, role.name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(userId)
                .email(email)
                .role(role.name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        String userId;
        UserRole role;
        String encodedPassword;

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            userId = user.getId();
            role = user.getRole();
            encodedPassword = user.getPassword();
        } else {
            JewelleryMaker maker = makerRepository.findByEmail(email).orElse(null);
            if (maker != null) {
                userId = maker.getId();
                role = maker.getRole();
                encodedPassword = maker.getPassword();
            } else {
                Admin admin = adminRepository.findByEmail(email).orElse(null);
                if (admin != null) {
                    userId = admin.getId();
                    role = admin.getRole();
                    encodedPassword = admin.getPassword();
                } else {
                    throw new ResourceNotFoundException("No user found with email: " + email);
                }
            }
        }

        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new UnauthorizedException("Invalid password credentials");
        }

        String accessToken = jwtUtil.generateToken(userId, email, role.name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(userId)
                .email(email)
                .role(role.name())
                .build();
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenStr = request.getRefreshToken();
        RefreshToken token = refreshTokenService.findByToken(tokenStr)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is not present in database"));

        refreshTokenService.verifyExpiration(token);

        String userId = token.getUserId();
        String email;
        UserRole role;

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            email = user.getEmail();
            role = user.getRole();
        } else {
            JewelleryMaker maker = makerRepository.findById(userId).orElse(null);
            if (maker != null) {
                email = maker.getEmail();
                role = maker.getRole();
            } else {
                Admin admin = adminRepository.findById(userId).orElse(null);
                if (admin != null) {
                    email = admin.getEmail();
                    role = admin.getRole();
                } else {
                    throw new ResourceNotFoundException("User not found for this token");
                }
            }
        }

        String accessToken = jwtUtil.generateToken(userId, email, role.name());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(token.getToken())
                .userId(userId)
                .email(email)
                .role(role.name())
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        
        // Check if email exists
        boolean userExists = userRepository.existsByEmail(email) || 
                             makerRepository.existsByEmail(email) || 
                             adminRepository.existsByEmail(email);
        
        if (!userExists) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        // Generate 6 digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Delete existing OTP for this email if any
        otpTokenRepository.deleteByEmail(email);

        // Save new OTP (expires in 5 minutes)
        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
        otpTokenRepository.save(otpToken);

        // Send email
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();

        OtpToken otpToken = otpTokenRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new UnauthorizedException("Invalid OTP"));

        if (otpToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpTokenRepository.delete(otpToken);
            throw new UnauthorizedException("OTP has expired");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        // Update password for the corresponding user type
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setPassword(encodedPassword);
            userRepository.save(user);
        } else {
            JewelleryMaker maker = makerRepository.findByEmail(email).orElse(null);
            if (maker != null) {
                maker.setPassword(encodedPassword);
                makerRepository.save(maker);
            } else {
                Admin admin = adminRepository.findByEmail(email).orElse(null);
                if (admin != null) {
                    admin.setPassword(encodedPassword);
                    adminRepository.save(admin);
                }
            }
        }

        // Clean up OTP
        otpTokenRepository.delete(otpToken);
    }
}

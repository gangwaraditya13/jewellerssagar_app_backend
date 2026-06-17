package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.AdminDashboardResponse;
import com.sagar.jewellery.dto.MakerProfileResponse;
import com.sagar.jewellery.dto.UserProfileResponse;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.mapper.MakerMapper;
import com.sagar.jewellery.mapper.UserMapper;
import com.sagar.jewellery.model.JewelleryMaker;
import com.sagar.jewellery.model.User;
import com.sagar.jewellery.model.enums.PaymentStatus;
import com.sagar.jewellery.repository.*;
import com.sagar.jewellery.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JewelleryMakerRepository makerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MakerMapper makerMapper;

    @Override
    public List<MakerProfileResponse> getAllMakers() {
        return makerRepository.findAll().stream()
                .map(makerMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MakerProfileResponse approveMaker(String makerId) {
        JewelleryMaker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery Maker not found with id: " + makerId));
        maker.setApproved(true);
        JewelleryMaker saved = makerRepository.save(maker);
        return makerMapper.toProfileResponse(saved);
    }

    @Override
    @Transactional
    public MakerProfileResponse revokeMaker(String makerId) {
        JewelleryMaker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery Maker not found with id: " + makerId));
        maker.setApproved(false);
        JewelleryMaker saved = makerRepository.save(maker);
        return makerMapper.toProfileResponse(saved);
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
        refreshTokenRepository.deleteByUserId(userId);
        cartRepository.findByUserId(userId).ifPresent(cartRepository::delete);
    }

    @Override
    public AdminDashboardResponse getDashboardSummary() {
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();

        double totalRevenue = paymentRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(payment -> payment.getAmount())
                .sum();
        totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .build();
    }
}

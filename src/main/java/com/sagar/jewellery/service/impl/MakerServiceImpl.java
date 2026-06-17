package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.MakerProfileResponse;
import com.sagar.jewellery.dto.MakerUpdateRequest;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.mapper.MakerMapper;
import com.sagar.jewellery.model.JewelleryMaker;
import com.sagar.jewellery.model.Order;
import com.sagar.jewellery.model.enums.OrderStatus;
import com.sagar.jewellery.repository.JewelleryMakerRepository;
import com.sagar.jewellery.repository.OrderRepository;
import com.sagar.jewellery.service.MakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MakerServiceImpl implements MakerService {

    @Autowired
    private JewelleryMakerRepository makerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MakerMapper makerMapper;

    @Override
    public MakerProfileResponse getProfile(String makerId) {
        JewelleryMaker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery Maker not found with id: " + makerId));
        return makerMapper.toProfileResponse(maker);
    }

    @Override
    public MakerProfileResponse updateProfile(String makerId, MakerUpdateRequest request) {
        JewelleryMaker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new ResourceNotFoundException("Jewellery Maker not found with id: " + makerId));
        if (request.getFullName() != null) maker.setFullName(request.getFullName());
        if (request.getPhone() != null) maker.setPhone(request.getPhone());
        if (request.getShopName() != null) maker.setShopName(request.getShopName());
        if (request.getAddress() != null) maker.setAddress(request.getAddress());
        
        JewelleryMaker saved = makerRepository.save(maker);
        return makerMapper.toProfileResponse(saved);
    }

    @Override
    public double getEarningsSummary(String makerId) {
        List<Order> orders = orderRepository.findByMakerId(makerId);
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }
}

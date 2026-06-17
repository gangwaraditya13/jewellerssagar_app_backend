package com.sagar.jewellery.security;

import com.sagar.jewellery.model.Admin;
import com.sagar.jewellery.model.JewelleryMaker;
import com.sagar.jewellery.model.User;
import com.sagar.jewellery.repository.AdminRepository;
import com.sagar.jewellery.repository.JewelleryMakerRepository;
import com.sagar.jewellery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JewelleryMakerRepository jewelleryMakerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            return new CustomUserDetails(u.getId(), u.getEmail(), u.getPassword(), u.getRole().name());
        }

        Optional<JewelleryMaker> makerOpt = jewelleryMakerRepository.findByEmail(email);
        if (makerOpt.isPresent()) {
            JewelleryMaker m = makerOpt.get();
            return new CustomUserDetails(m.getId(), m.getEmail(), m.getPassword(), m.getRole().name());
        }

        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin a = adminOpt.get();
            return new CustomUserDetails(a.getId(), a.getEmail(), a.getPassword(), a.getRole().name());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

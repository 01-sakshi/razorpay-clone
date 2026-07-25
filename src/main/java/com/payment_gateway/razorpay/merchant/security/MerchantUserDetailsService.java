package com.payment_gateway.razorpay.merchant.security;

import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.merchant.entity.AppUser;
import com.payment_gateway.razorpay.merchant.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantUserDetailsService implements UserDetailsService {
    /* For spring security JWT Authentication, this MerchantUserDetailsService implements UserDetailsService */

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("APPUSER", username));
    }
}

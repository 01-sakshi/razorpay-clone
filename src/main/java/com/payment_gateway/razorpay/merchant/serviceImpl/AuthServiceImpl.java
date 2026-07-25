package com.payment_gateway.razorpay.merchant.serviceImpl;

import com.payment_gateway.razorpay.common.constants.ExceptionErrorCode;
import com.payment_gateway.razorpay.common.enums.UserRole;
import com.payment_gateway.razorpay.common.exceptions.DuplicateResourceException;
import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.merchant.dto.request.LoginRequest;
import com.payment_gateway.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.payment_gateway.razorpay.merchant.dto.response.LoginResponse;
import com.payment_gateway.razorpay.merchant.dto.response.MerchantResponse;
import com.payment_gateway.razorpay.merchant.entity.AppUser;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import com.payment_gateway.razorpay.merchant.mapper.MerchantMapper;
import com.payment_gateway.razorpay.merchant.repository.AppUserRepository;
import com.payment_gateway.razorpay.merchant.repository.MerchantRepository;
import com.payment_gateway.razorpay.merchant.security.JwtUtil;
import com.payment_gateway.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public MerchantResponse signUp(MerchantSignupRequest merchantSignupRequest) {
        //Check if merchant already exists
        if (merchantRepository.findByEmail(merchantSignupRequest.email()) != null) {
            throw new DuplicateResourceException(ExceptionErrorCode.DUPLICATE_MERCHANT_EMAIL, "Merchant already exists with email :" + merchantSignupRequest.email());
        }

        //prepare to persist merchant in database
        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(merchantSignupRequest);
        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser
                .builder()
                .merchant(merchant)
                .email(merchant.getEmail())
                .passwordHash(passwordEncoder.encode(merchantSignupRequest.password()))
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);
        return merchantMapper.toResponse(merchant);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.email(), loginRequest.password()));

        AppUser appUser = appUserRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("APP_USER", loginRequest.email()));

        String jwtToken = jwtUtil.generateAccessToken(loginRequest.email(),
                appUser.getMerchant().getId(), appUser.getRole().name());
        return new LoginResponse(jwtToken);
    }
}

package com.payment_gateway.razorpay.merchant.serviceImpl;

import com.payment_gateway.razorpay.common.constants.ExceptionErrorCode;
import com.payment_gateway.razorpay.common.enums.UserRole;
import com.payment_gateway.razorpay.common.exceptions.DuplicateResourceException;
import com.payment_gateway.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.payment_gateway.razorpay.merchant.dto.response.MerchantResponse;
import com.payment_gateway.razorpay.merchant.entity.AppUser;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import com.payment_gateway.razorpay.merchant.repository.AppUserRepository;
import com.payment_gateway.razorpay.merchant.repository.MerchantRepository;
import com.payment_gateway.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;

    @Override
    @Transactional
    public MerchantResponse signUp(MerchantSignupRequest merchantSignupRequest) {
        //Check if merchant already exists
        if(merchantRepository.findByEmail(merchantSignupRequest.email()) != null) {
            throw new DuplicateResourceException(ExceptionErrorCode.DUPLICATE_MERCHANT_EMAIL, "Merchant already exists with email :" + merchantSignupRequest.email());
        }

        //prepare to persist merchant in database
        Merchant merchant = Merchant
                .builder()
                .name(merchantSignupRequest.name())
                .email(merchantSignupRequest.email())
                .businessName(merchantSignupRequest.businessName())
                .businessType(merchantSignupRequest.businessType())
                .build();

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser
                .builder()
                .merchant(merchant)
                .email(merchant.getEmail())
                .passwordHash(merchantSignupRequest.password()) //TODO: will be encrypted later using BCrypt
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);
        return new MerchantResponse(merchant.getId(),
                merchant.getName(),
                merchant.getEmail(),
                merchant.getBusinessName(),
                merchant.getBusinessType(),
                merchant.getStatus());
    }
}

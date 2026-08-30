package com.payment_gateway.razorpay.merchant.serviceImpl;

import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.merchant.entity.Customer;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import com.payment_gateway.razorpay.merchant.repository.CustomerRepository;
import com.payment_gateway.razorpay.merchant.repository.MerchantRepository;
import com.payment_gateway.razorpay.merchant.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public UUID findOrCreate(UUID merchantId, String name, String phone, String email) {
        /* if email in param is null, request could be from a guest account wherein user doesn't
        have an active account and only wants to place an order as a guest. */
        if (email == null || email.isBlank()) return null;

        return customerRepository.findByEmailAndMerchantId(email, merchantId)
                .map(Customer::getId)
                .orElseGet(() -> createNew(merchantId, name, phone, email));
    }

    private UUID createNew(UUID merchantId, String name, String phone, String email) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));
        Customer customer = Customer.builder()
                .merchant(merchant)
                .email(email)
                .name(name)
                .contactNumber(phone)
                .build();
        customer = customerRepository.save(customer);
        log.info("Customer created for merchant: {} with email: {}, name: {} and contact: {}",
                merchantId, email, name, phone);
        return customer.getId();
    }
}

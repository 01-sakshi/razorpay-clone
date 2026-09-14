package com.payment_gateway.razorpay.merchant.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/merchant/webhooks")
public class DummyRealMerchantController {
    /* TEST API */
    @PostMapping
    public ResponseEntity<String> test(@RequestBody Object o) {
        return ResponseEntity.ok("Test Successfull");
    }
}

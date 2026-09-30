package com.payment_gateway.razorpay.operations.settlement;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.payment_gateway.razorpay.common.dto.SettlementBankDetails;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.operations.settlement.dto.BankTransferResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankTransferProcessorImpl implements BankTransferProcessor {
    /** Generates a synthetic transfer registration reference; this implementation does not call a bank API. */
    @Override
    public BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
            SettlementBankDetails bankDetails) {

        // Call the Bank API
        String registrationRef = "TXN_" + RandomizerUtil.randomBase64(12);
        log.debug("Bank Transfer call completed for settlementId: {}, registrationRef: {}",
                settlementId, registrationRef);
        return new BankTransferResult(registrationRef);
    }
}

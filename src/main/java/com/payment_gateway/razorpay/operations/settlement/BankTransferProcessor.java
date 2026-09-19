package com.payment_gateway.razorpay.operations.settlement;

import java.util.UUID;

import com.payment_gateway.razorpay.common.dto.SettlementBankDetails;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.operations.settlement.dto.BankTransferResult;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId,
            Money amount, SettlementBankDetails bankDetails);
}

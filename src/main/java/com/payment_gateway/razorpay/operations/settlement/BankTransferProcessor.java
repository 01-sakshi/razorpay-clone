package com.payment_gateway.razorpay.operations.settlement;

import java.util.UUID;

import com.payment_gateway.razorpay.common.dto.SettlementBankDetails;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.operations.settlement.dto.BankTransferResult;

public interface BankTransferProcessor {

    /**
     * Submits the settlement's net amount to the merchant's settlement destination.
     *
     * @param settlementId persisted settlement being transferred
     * @param merchantId owner of the destination account
     * @param amount net amount to transfer
     * @param bankDetails merchant settlement account details
     * @return bank registration reference used to correlate the later transfer result
     */
    BankTransferResult initiate(UUID settlementId, UUID merchantId,
            Money amount, SettlementBankDetails bankDetails);
}

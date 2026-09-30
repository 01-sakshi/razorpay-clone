package com.payment_gateway.razorpay.operations.settlement.dto;

/**
 * Result of submitting a settlement transfer to the bank processor.
 *
 * @param bankReference processor reference returned by the bank
 */
public record BankTransferResult(
        String bankReference) {

}

package com.payment_gateway.razorpay.operations.settlement;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.payment_gateway.razorpay.common.dto.SettlementBankDetails;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.EventAggregateType;
import com.payment_gateway.razorpay.common.enums.SettlementStatus;
import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.merchant.api.MerchantLookupService;
import com.payment_gateway.razorpay.operations.entity.Settlement;
import com.payment_gateway.razorpay.operations.entity.SettlementPayment;
import com.payment_gateway.razorpay.operations.entity.SettlementPaymentId;
import com.payment_gateway.razorpay.operations.repository.SettlementPaymentRepository;
import com.payment_gateway.razorpay.operations.repository.SettlementRepository;
import com.payment_gateway.razorpay.operations.settlement.dto.BankTransferResult;
import com.payment_gateway.razorpay.payment.api.PaymentLookupService;
import com.payment_gateway.razorpay.payment.entity.Payment;
import com.payment_gateway.razorpay.payment.outbox.OutboxEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementTransactionExecutor {

    private static final double FEE_RATE = 0.02;
    private static final double GST_RATE = 0.18;
    private final PaymentLookupService paymentLookupService;
    private final SettlementRepository settlementRepository;
    private final SettlementPaymentRepository settlementPaymentRepository;
    private final MerchantLookupService merchantLookupService;
    private final BankTransferProcessor bankTransferProcessor;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void processForMerchant(UUID merchantId, Instant dateTime) {
        LocalDate localDate = dateTime.atZone(ZoneId.systemDefault()).toLocalDate();
        List<Payment> unsettledPaymentsForMerchant = paymentLookupService.findUnsettledCapturedPayments(merchantId);

        if (unsettledPaymentsForMerchant.size() == 0)
            return;

        log.info("Processing {} unsettled payments for merchant : {} on date : {}", unsettledPaymentsForMerchant.size(),
                merchantId, localDate);

        Money gross = unsettledPaymentsForMerchant.stream().map(payment -> payment.getAmount())
                .reduce((am1, am2) -> am1.add(am2)).orElseThrow();

        long fee = Math.round(gross.getAmountUnits() * FEE_RATE);
        long gst = Math.round(fee * GST_RATE);
        Money feeAmount = Money.of(fee, gross.getCurrency());
        Money gstAmount = Money.of(gst, gross.getCurrency());
        Money netAmount = gross.subtract(feeAmount).subtract(gstAmount);

        Settlement settlement = Settlement.builder()
                .merchantId(merchantId)
                .feeAmount(feeAmount)
                .grossAmount(gross)
                .gstAmount(gstAmount)
                .netAmount(netAmount)
                .refundAmount(Money.of(0l, gross.getCurrency()))
                .status(SettlementStatus.INITIATED)
                .build();
        settlement = settlementRepository.save(settlement);

        /* DOUBT */
        List<SettlementPayment> settlementPayments = new ArrayList<>();
        for (Payment payment : unsettledPaymentsForMerchant) {
            settlementPayments.add(SettlementPayment.builder()
                    .settlementPaymentId(new SettlementPaymentId(settlement.getId(), payment.getId()))
                    .settlement(settlement)
                    .build());
        }
        settlementPaymentRepository.saveAll(settlementPayments);

        try {
            SettlementBankDetails settlementBankDetails = merchantLookupService.getSettlementBankDetails(merchantId);
            BankTransferResult bankTransferResult = bankTransferProcessor.initiate(settlement.getId(), merchantId,
                    netAmount, settlementBankDetails);

            settlement.setStatus(SettlementStatus.TRANSFER_PENDING);
            settlement.setBankReference(bankTransferResult.bankReference());
        } catch (Exception e) {
            log.error("Settlement failed for settlement id : {} on date : {}", settlement.getId(), localDate);
            settlement.setStatus(SettlementStatus.FAILED);
        }
        settlementRepository.save(settlement);
    }

    @Transactional
    public void resolveTransfer(UUID settlementId, String errorCode, String errorDescription) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("SETTLEMENT", settlementId));
        if (!settlement.getStatus().equals(SettlementStatus.TRANSFER_PENDING)) {
            log.info("Settlement : {} skipped due to ineligible settlement status: {}", settlementId,
                    settlement.getStatus());
            return;
        }

        if (errorCode == null) {
            settlement.setStatus(SettlementStatus.PROCESSED);
            settlement.setSettledAt(Instant.now());
            settlementRepository.save(settlement);
            log.info("Settlement settled successfully, settlement id : {}", settlementId);
            outboxEventPublisher.publish(EventAggregateType.SETTLEMENT, settlementId,
                    "SETTLEMENT_PROCESSED", Map.of(
                            "settlementId", settlementId,
                            "merchantId", settlement.getMerchantId(),
                            "settlementStatus", settlement.getStatus().name(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits()));
        } else {
            settlement.setStatus(SettlementStatus.FAILED);
            settlement.setFailureReason(errorCode + " : " + errorDescription);
            settlementRepository.save(settlement);
            /* Use log.error() when an exception is thrown or else use log.warn() */
            log.warn("Settlement settling failed, settlement id : {}", settlementId);
            outboxEventPublisher.publish(EventAggregateType.SETTLEMENT, settlementId,
                    "SETTLEMENT_FAILED", Map.of(
                            "settlementId", settlementId,
                            "merchantId", settlement.getMerchantId(),
                            "settlementStatus", settlement.getStatus().name(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits()));
        }
    }
}

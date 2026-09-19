package com.payment_gateway.razorpay.operations.settlement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.payment_gateway.razorpay.merchant.api.MerchantLookupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementEngine {

    private final MerchantLookupService merchantLookupService;
    private final SettlementTransactionExecutor settlementTransactionExecutor;

    @Scheduled(cron = "0 0 23 * * *")
    public void runScheduled() {
        log.info("Nightly settlement scheduler running");
        run();
    }

    public void run() {
        List<UUID> activeMerchants = merchantLookupService.getAllActiveMerchants();
        log.info("Processing the settlements for merchant ids: {}", activeMerchants.size());

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>(); // Future<?> ??

            for (UUID merchantId : activeMerchants) {
                futures.add(executorService.submit(() -> {
                    settlementTransactionExecutor.processForMerchant(merchantId, Instant.now());
                }));
            }

            for (var f : futures) {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Settlement batch future failed ", e);
                }
            }
        }
        log.info("Settlement batch completed");
    }
}

package com.payment_gateway.razorpay.payment.serviceImpl;

import com.payment_gateway.razorpay.common.enums.OrderStatus;
import com.payment_gateway.razorpay.common.enums.PaymentStatus;
import com.payment_gateway.razorpay.common.exceptions.BusinessRuleViolationException;
import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.payment.dto.request.PaymentInitRequest;
import com.payment_gateway.razorpay.payment.dto.response.PaymentResponse;
import com.payment_gateway.razorpay.payment.entity.OrderRecord;
import com.payment_gateway.razorpay.payment.entity.Payment;
import com.payment_gateway.razorpay.payment.gateway.PaymentGatewayRouter;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentRequest;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentResult;
import com.payment_gateway.razorpay.payment.mapper.PaymentMapper;
import com.payment_gateway.razorpay.payment.repository.OrderRepository;
import com.payment_gateway.razorpay.payment.repository.PaymentRepository;
import com.payment_gateway.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;

    /**
     * @param merchantId
     * @param request
     * @return PaymentResponse
     */
    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchant(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("order", request.orderId()));

        if (!orderRecord.getStatus().equals(OrderStatus.CREATED) &&
                !orderRecord.getStatus().equals(OrderStatus.ATTEMPTED)) {
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE", "order", request.orderId());
        }

        orderRecord.setStatus(OrderStatus.ATTEMPTED);
        orderRecord.setAttempts(orderRecord.getAttempts() + 1);

        Payment payment = Payment
                .builder()
                .orderRecord(orderRecord)
                .status(PaymentStatus.CREATED)
                .merchantId(merchantId)
                .amount(orderRecord.getAmount())
                .method(request.paymentMethod())
                .methodDetails(request.methodDetails())
                .build();
        payment = paymentRepository.save(payment);

        PaymentResult paymentResult = paymentGatewayRouter.initiate(new PaymentRequest(
                payment.getId(),
                merchantId,
                request.orderId(),
                payment.getMethod(),
                payment.getMethodDetails(),
                payment.getAmount()));

        switch (paymentResult) {
            case PaymentResult.Pending pending->    //record pattern
                    payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
                System.out.println("failure:" + failure);
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case null, default -> {
            }
        }

        payment = paymentRepository.save(payment);
        orderRecord = orderRepository.save(orderRecord);
        return paymentMapper.toResponse(payment);
    }

}

package com.payment_gateway.razorpay.payment.serviceImpl;

import com.payment_gateway.razorpay.common.enums.OrderStatus;
import com.payment_gateway.razorpay.common.exceptions.BusinessRuleViolationException;
import com.payment_gateway.razorpay.common.exceptions.DuplicateResourceException;
import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.merchant.service.CustomerService;
import com.payment_gateway.razorpay.payment.dto.request.CreateOrderRequest;
import com.payment_gateway.razorpay.payment.dto.response.OrderResponse;
import com.payment_gateway.razorpay.payment.dto.response.PaymentResponse;
import com.payment_gateway.razorpay.payment.entity.OrderRecord;
import com.payment_gateway.razorpay.payment.mapper.OrderMapper;
import com.payment_gateway.razorpay.payment.mapper.PaymentMapper;
import com.payment_gateway.razorpay.payment.repository.OrderRepository;
import com.payment_gateway.razorpay.payment.repository.PaymentRepository;
import com.payment_gateway.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)     /*For GET queries, readOnly=true will not lock the connection*/
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-seconds}")
    private int defaultOrderExpirySeconds;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest createOrderRequest) {
        if (createOrderRequest.receipt() != null &&
                orderRepository.existsByReceiptAndMerchant(createOrderRequest.receipt(), merchantId))
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order already exists");

        UUID customerId = null;
        CreateOrderRequest.CustomerDetails customer = createOrderRequest.customer();
        if(customer != null) {
            //Meaning, the order request is from an active customer account, not a guest one.
            customerId = customerService.findOrCreate(merchantId, customer.name(), customer.phone(), customer.email());
        }

        OrderRecord orderRecord = OrderRecord.builder()
                .notes(createOrderRequest.notes())
                .amount(createOrderRequest.amount())
                .receipt(createOrderRequest.receipt())
                .merchant(merchantId)
                .customerId(customerId)
                .expiresAt(createOrderRequest.expiresAt() != null ? createOrderRequest.expiresAt()
                        : Instant.now().plusSeconds(defaultOrderExpirySeconds))
                .build();
        return orderMapper.toResponse(orderRepository.save(orderRecord));
    }

    /**
     * @param merchantId
     * @param orderId
     * @return OrderResponse
     */
    @Override
    public OrderResponse get(UUID merchantId, UUID orderId) {
        return orderRepository.findByIdAndMerchant(orderId, merchantId)
                .map(orderRecord -> orderMapper.toResponse(orderRecord))
                .orElseThrow(() -> new ResourceNotFoundException("order", orderId));
    }

    /**
     * @param merchantId
     * @param orderId
     * @return String
     */
    @Override
    @Transactional
    public String cancel(UUID merchantId, UUID orderId) {
        return orderRepository.findByIdAndMerchant(orderId, merchantId)
                .map(orderRecord -> {
                    if (orderRecord.getStatus().equals(OrderStatus.CANCELLED) || orderRecord.getStatus().equals(OrderStatus.PAID))
                        throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL", "Order", orderId);
                    orderRecord.setStatus(OrderStatus.CANCELLED);
                    orderRecord.setUpdatedAt(Instant.now());
                    orderRepository.save(orderRecord);
                    return "Order with orderId:" + orderId + "Cancelled Successfully";
                })
                .orElseThrow(() -> new ResourceNotFoundException("order", orderId));
    }

    /**
     * @param merchantId
     * @param orderId
     * @return List<PaymentResponse>
     */
    @Override
    public List<PaymentResponse> list(UUID merchantId, UUID orderId) {
        if(!orderRepository.existsByIdAndMerchant(orderId, merchantId))
            throw new ResourceNotFoundException("order", orderId);
        return paymentMapper.toResponseList(paymentRepository.findAllByOrderRecordId(orderId));
    }
}

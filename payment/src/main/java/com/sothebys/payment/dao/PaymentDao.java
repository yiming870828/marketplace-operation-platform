package com.sothebys.payment.dao;

import com.sothebys.payment.entity.Payment;
import com.sothebys.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentDao {
    @Autowired
    PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

}

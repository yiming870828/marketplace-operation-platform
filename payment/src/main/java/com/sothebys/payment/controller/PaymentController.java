package com.sothebys.payment.controller;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.service.exception.ApiException;
import com.adyen.util.HMACValidator;
import com.sothebys.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    PaymentService paymentService;

    @PostMapping("/link")
    public ResponseEntity<String> createPayByLinks() throws IOException, ApiException {
        String payByLinks = paymentService.createPayByLinks();
        return ResponseEntity.ok(payByLinks);
    }

    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody NotificationRequest notificationRequest) {
        String result = paymentService.getNotifications(notificationRequest);
        return ResponseEntity.ok().body("[accepted]");
    }

}

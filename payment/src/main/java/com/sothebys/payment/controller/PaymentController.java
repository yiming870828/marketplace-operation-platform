package com.sothebys.payment.controller;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.service.exception.ApiException;
import com.adyen.util.HMACValidator;
import com.sothebys.payment.service.PaymentService;
import lombok.extern.log4j.Log4j;
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

    private final static String HMACKEY = "0D28CF073243B5C13565F8961713B7202A435F357F3D52F8F1331D69F1FA06CE";

    @Autowired
    PaymentService paymentService;

    @PostMapping("/link")
    public ResponseEntity<String> createPayByLinks() throws IOException, ApiException {
        String payByLinks = paymentService.createPayByLinks();
        return ResponseEntity.ok(payByLinks);
    }

    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody NotificationRequest notificationRequest) {

        notificationRequest.getNotificationItems().forEach(
                item -> {
                    // We recommend validate HMAC signature in the webhooks for security reasons
                    try {
                        if (new HMACValidator().validateHMAC(item, HMACKEY)) {
                            log.info("Received webhook with event {} : \n" +
                                            "Merchant Reference: {}\n" +
                                            "Alias : {}\n" +
                                            "payment method : {}\n" +
                                            "Successful : {}\n" +
                                            "Reason : {}\n" +
                                            "PSP reference : {}"
                                    , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("alias"),
                                    item.getPaymentMethod(), item.isSuccess(), item.getReason(), item.getPspReference());
                        } else {
                            // invalid HMAC signature: do not send [accepted] response
                            log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                            throw new RuntimeException("Invalid HMAC signature");
                        }
                    } catch (SignatureException e) {
                        log.error("Error while validating HMAC Key", e);
                    }
                }
        );

        // Notifying the server we're accepting the payload
        return ResponseEntity.ok().body("[accepted]");
    }

}

package com.sothebys.payment.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.CreatePaymentLinkRequest;
import com.adyen.model.checkout.PaymentLinkResource;
import com.adyen.model.checkout.UpdatePaymentLinkRequest;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.service.PaymentLinks;
import com.adyen.service.exception.ApiException;
import com.adyen.util.HMACValidator;
import com.sothebys.payment.client.EmailClient;
import com.sothebys.payment.controller.PaymentController;
import com.sothebys.payment.dao.PaymentDao;
import com.sothebys.payment.entity.Payment;
import com.sothebys.payment.repository.PaymentRepository;
import com.sothebys.payment.utile.PaymentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final static String ADYEN_API_KEY = "AQEqhmfxKYjPaRFKw0m/n3Q5qf3Ve4RZBZZIQXb/Ch0/SF7EM3Pd/O17Wo9bEMFdWw2+5HzctViMSCJMYAc=-6iqrgBLu/gEU3oAxIl2z23OvuKhDiMHyLLox5tKO2uA=-I2VUyp$7yY9H3r7{";

    private final static String ADYEN_MERCHANT_ACCOUNT = "SothebysEU";

    private final static String ADYEN_CLIENT_KEY = "live_HDU67CDFWVEDRICFTMF5CVEM6A6O6OAF";
    private final static String LIVE_URL_PREFIX = "1aa91d85f667dbfe-Sothebys";
    private final static String HMACKEY = "0D28CF073243B5C13565F8961713B7202A435F357F3D52F8F1331D69F1FA06CE";

    @Autowired
    PaymentDao paymentDao;

    @Autowired
    EmailClient emailClient;

    public String createPayByLinks() throws IOException, ApiException {
        String xApiKey = ADYEN_API_KEY;
        Client client = new Client(xApiKey, Environment.LIVE, LIVE_URL_PREFIX);
        PaymentLinks paymentLinks = new PaymentLinks(client);
        CreatePaymentLinkRequest createPaymentLinkRequest = new CreatePaymentLinkRequest();
        Amount amount = new Amount();
        amount.setCurrency("CNY");
        amount.setValue(100L);

        createPaymentLinkRequest.setAmount(amount);
        createPaymentLinkRequest.setReference("SothebysEU");
        createPaymentLinkRequest.setCountryCode("CN");
        createPaymentLinkRequest.setMerchantAccount(ADYEN_MERCHANT_ACCOUNT);

        PaymentLinkResource response = paymentLinks.create(createPaymentLinkRequest);
        System.out.println(response.getUrl());

        String url = response.getUrl();

        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Payment payment = Payment.builder().createDate(date).updateDate(date).pblId(PaymentUtil.getPBLId(url)).status("unpaid").build();

        paymentDao.savePayment(payment);

        emailClient.sendEmail(url);

        return url;
    }


    public String getNotifications(NotificationRequest notificationRequest) {

        notificationRequest.getNotificationItems().forEach(
                item -> {
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
        return "";
    }

    public static String getPayByLinks(String linkId) throws IOException, ApiException {
        String xApiKey = ADYEN_API_KEY;
        Client client = new Client(xApiKey, Environment.LIVE, LIVE_URL_PREFIX);
        PaymentLinks paymentLinks = new PaymentLinks(client);

        PaymentLinkResource paymentLinkResource = paymentLinks.retrieve(linkId);

        System.out.println(paymentLinkResource.getStatus());
        return xApiKey;
    }

    public static String expiredPayByLinks(String linkId) throws IOException, ApiException {
        String xApiKey = ADYEN_API_KEY;
        Client client = new Client(xApiKey, Environment.LIVE, LIVE_URL_PREFIX);
        PaymentLinks paymentLinks = new PaymentLinks(client);

        UpdatePaymentLinkRequest updatePaymentLinkRequest = new UpdatePaymentLinkRequest();
        updatePaymentLinkRequest.setStatus(UpdatePaymentLinkRequest.StatusEnum.EXPIRED);

        PaymentLinkResource paymentLinkResource = paymentLinks.update(linkId, updatePaymentLinkRequest);

        System.out.println(paymentLinkResource.getStatus());
        return xApiKey;
    }

}

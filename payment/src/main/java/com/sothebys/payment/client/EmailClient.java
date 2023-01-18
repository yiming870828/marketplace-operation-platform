package com.sothebys.payment.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "email-api",url = "${fegin.email}")
public interface EmailClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/email")
    String sendEmail(@RequestParam(value = "id") String id);

}

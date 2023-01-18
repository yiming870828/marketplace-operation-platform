package com.sothebys.ses.controller;

import com.sothebys.ses.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmailController {
    private final Logger log = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestParam String id) {
        emailService.sendEmail(id);
        return ResponseEntity.ok().build();
    }
}

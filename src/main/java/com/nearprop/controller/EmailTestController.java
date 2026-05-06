package com.nearprop.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email-test") // Removed /api prefix since it's already in the context path
@RequiredArgsConstructor
@Slf4j
public class EmailTestController {

    private final JavaMailSender mailSender;

    @Data
    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getEmailTestForm() {
        return "<html><head><title>Email Test</title></head><body>" +
               "<h1>Email Test Form</h1>" +
               "<form id='emailForm'>" +
               "  <div><label>To: <input type='email' id='to' name='to' required></label></div>" +
               "  <div><label>Subject: <input type='text' id='subject' name='subject' required></label></div>" +
               "  <div><label>Body: <textarea id='body' name='body' rows='5' cols='40' required></textarea></label></div>" +
               "  <div><button type='button' onclick='sendEmail()'>Send Email</button></div>" +
               "</form>" +
               "<div id='result'></div>" +
               "<script>" +
               "function sendEmail() {" +
               "  const to = document.getElementById('to').value;" +
               "  const subject = document.getElementById('subject').value;" +
               "  const body = document.getElementById('body').value;" +
               "  " +
               "  fetch('/api/email-test/send', {" +
               "    method: 'POST'," +
               "    headers: {" +
               "      'Content-Type': 'application/json'" +
               "    }," +
               "    body: JSON.stringify({to, subject, body})" +
               "  })" +
               "  .then(response => response.json())" +
               "  .then(data => {" +
               "    document.getElementById('result').innerHTML = " +
               "      `<pre>${JSON.stringify(data, null, 2)}</pre>`;" +
               "  })" +
               "  .catch(error => {" +
               "    document.getElementById('result').innerHTML = " +
               "      `<pre>Error: ${error.message}</pre>`;" +
               "  });" +
               "}" +
               "</script>" +
               "</body></html>";
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendTestEmail(@RequestBody EmailRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Attempting to send test email to: {}", request.getTo());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), true); // true indicates HTML content
            helper.setFrom("sandeep.acoreithub@gmail.com");
            
            mailSender.send(message);
            
            log.info("Test email sent successfully to: {}", request.getTo());
            
            response.put("success", true);
            response.put("message", "Email sent successfully");
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            log.error("Failed to send test email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            log.error("Unexpected error while sending email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
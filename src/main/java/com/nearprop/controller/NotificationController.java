package com.nearprop.controller;

import com.nearprop.service.FcmService;
import com.nearprop.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fcm")
class FcmController {

    private final FcmService fcmService;
    private final NotificationService notificationService;

    public FcmController(
            FcmService fcmService,
            NotificationService notificationService
    ) {
        this.fcmService = fcmService;
        this.notificationService = notificationService;
    }

    // 1️⃣ Send to a specific device
    @PostMapping("/send")
    public String sendToToken(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String body
    ) throws Exception {
        return fcmService.sendNotification(token, title, body);
    }

    @PostMapping("/sendallll")
    public String sendToTokenAlll(
            @RequestParam String title,
            @RequestParam String body
    ) throws Exception {
        return fcmService.sendToAllUsers( title, body);
    }

    // 2️⃣ Send to all devices (broadcast)
    @PostMapping("/sendAll")
    public String sendToAll(
            @RequestParam String title,
            @RequestParam String body
    ) throws Exception {
        return fcmService.sendToTopic("all", title, body);
    }

    // 3️⃣ 🔔 District-wise PLAN notification (TEST API)
    @PostMapping("/district-plan")
    public String notifyDistrictPlan(
            @RequestParam String district,
            @RequestParam Long planId
    ) {

        notificationService.notifyDistrictPlanPublished(district, planId);

        return "Plan notification triggered for district: " + district;
    }

//    @PostMapping("/sendData")
//    public String sendToTopicWithData(@RequestBody Map<String, Object> req) throws Exception {
//
//        String topic = req.get("topic").toString();
//        String title = req.get("title").toString();
//        String body  = req.get("body").toString();
//
//        @SuppressWarnings("unchecked")
//        Map<String, String> data = (Map<String, String>) req.get("data");
//
//        return fcmService.sendToTopicWithData(topic, title, body, data);
//    }

    @PostMapping("/sendData")
    public String sendToTopicWithData(@RequestBody Map<String, Object> req) throws Exception {

        log.info("📥 /fcm/sendData API HIT");
        log.info("📥 Raw Request Body: {}", req);

        String topic = req.get("topic").toString();
        String title = req.get("title").toString();
        String body  = req.get("body").toString();

        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) req.get("data");

        log.info("➡️ Parsed Topic : {}", topic);
        log.info("➡️ Parsed Title : {}", title);
        log.info("➡️ Parsed Body  : {}", body);
        log.info("➡️ Parsed Data  : {}", data);

        String response = fcmService.sendToTopicWithData(topic, title, body, data);

        log.info("✅ /fcm/sendData COMPLETED");
        log.info("📨 Firebase Response: {}", response);

        return response;
    }

}

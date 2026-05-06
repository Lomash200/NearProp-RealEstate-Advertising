//package com.nearprop.service;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FcmService {
//
//    // Send to a specific token
//    public String sendNotification(String token, String title, String body) throws Exception {
//        Notification notification = Notification.builder()
//                .setTitle(title)
//                .setBody(body)
//                .build();
//
//        Message message = Message.builder()
//                .setToken(token)
//                .setNotification(notification)
//                .build();
//
//        return FirebaseMessaging.getInstance().send(message);
//    }
//
//    // Send to a topic (all devices)
//    public String sendToTopic(String topic, String title, String body) throws Exception {
//        Notification notification = Notification.builder()
//                .setTitle(title)
//                .setBody(body)
//                .build();
//
//        Message message = Message.builder()
//                .setTopic(topic)
//                .setNotification(notification)
//                .build();
//
//        return FirebaseMessaging.getInstance().send(message);
//    }
//}

package com.nearprop.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nearprop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FcmService {
    private final UserRepository userRepository;

    public FcmService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Send to a specific token
    public String sendNotification(String token, String title, String body) throws Exception {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    public String sendToAllUsers(String title, String body) {

        log.info("📤 FCM SEND TO ALL USERS START");
        log.info("➡️ Title : {}", title);
        log.info("➡️ Body  : {}", body);

        List<String> tokens = userRepository.findAllFcmTokens();

        log.info("👥 Total tokens found: {}", tokens.size());

        int success = 0;
        int failed = 0;

        for (String token : tokens) {

            try {
                Message message = Message.builder()
                        .setToken(token)
                        .setNotification(
                                Notification.builder()
                                        .setTitle(title)
                                        .setBody(body)
                                        .build()
                        )
                        .build();

                String response = FirebaseMessaging.getInstance().send(message);
                success++;

                log.debug("✅ Sent to token: {} | msgId={}", token, response);

            } catch (Exception e) {
                failed++;
                log.warn("❌ Failed for token: {}", token);
            }
        }

        log.info("📊 FCM SEND SUMMARY → success={} failed={}", success, failed);
        return title;
    }

//    public String sendToAll(String title, String body) throws Exception {
//
//        String topic = "all"; // 🔔 global broadcast topic
//
//        log.info("📤 FCM SEND ALL START");
//        log.info("➡️ Topic : {}", topic);
//        log.info("➡️ Title : {}", title);
//        log.info("➡️ Body  : {}", body);
//
//        Message message = Message.builder()
//                .setTopic(topic)
//                .setNotification(
//                        Notification.builder()
//                                .setTitle(title)
//                                .setBody(body)
//                                .build()
//                )
//                .build();
//
//        try {
//            String response = FirebaseMessaging.getInstance().send(message);
//
//            log.info("✅ FCM SEND ALL SUCCESS");
//            log.info("📨 Firebase messageId: {}", response);
//
//            return response;
//
//        } catch (Exception e) {
//            log.error("❌ FCM SEND ALL FAILED");
//            log.error("❌ Error : {}", e.getMessage(), e);
//            throw e;
//        }
//    }
//

    // Send to a topic (all devices)
    public String sendToTopic(String topic, String title, String body) throws Exception {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

//    public String sendToTopicWithData(
//            String topic,
//            String title,
//            String body,
//            Map<String, String> data
//    ) throws Exception {
//
//        Message message = Message.builder()
//                .setTopic(topic)
//                .setNotification(
//                        Notification.builder()
//                                .setTitle(title)
//                                .setBody(body)
//                                .build()
//                )
//                .putAllData(data)
//                .build();
//
//        return FirebaseMessaging.getInstance().send(message);
//    }

    public String sendToTopicWithData(
            String topic,
            String title,
            String body,
            Map<String, String> data
    ) throws Exception {

        log.info("📤 FCM SEND START");
        log.info("➡️ Topic      : {}", topic);
        log.info("➡️ Title      : {}", title);
        log.info("➡️ Body       : {}", body);
        log.info("➡️ Data       : {}", data);

        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putAllData(data)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);

            log.info("✅ FCM SEND SUCCESS");
            log.info("📨 Firebase messageId: {}", response);

            return response;

        } catch (Exception e) {
            log.error("❌ FCM SEND FAILED");
            log.error("❌ Topic : {}", topic);
            log.error("❌ Error : {}", e.getMessage(), e);
            throw e;
        }
    }


    public String sendNotificationWithImage(
            String token,
            String title,
            String body,
            String imageUrl
    ) throws Exception {

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(imageUrl)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    // 🔔 SUBSCRIBE TOKEN TO TOPIC  ✅ (THIS WAS MISSING)
    public void subscribeToTopic(String fcmToken, String topic) {

        if (fcmToken == null || fcmToken.isBlank()
                || topic == null || topic.isBlank()) {
            return;
        }

        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(List.of(fcmToken), topic);

            log.info("FCM token subscribed to topic {}", topic);

        } catch (Exception e) {
            log.error("Failed to subscribe token to topic {}", topic, e);
        }
    }

    public String sendNotificationWithData(
            String token,
            String title,
            String body,
            Map<String, String> data
    ) throws Exception {

        log.info("📤 FCM SEND TOKEN + DATA");
        log.info("➡️ Token : {}", token);
        log.info("➡️ Title : {}", title);
        log.info("➡️ Body  : {}", body);
        log.info("➡️ Data  : {}", data);

        Message message = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putAllData(data)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);

        log.info("✅ FCM TOKEN SEND SUCCESS | msgId={}", response);
        return response;
    }

}

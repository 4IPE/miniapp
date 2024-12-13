package ru.rubytunnel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rubytunnel.service.PaymentService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @GetMapping("/payment/link")
    public ResponseEntity<?> getPaymentLink(@RequestParam Long userId) {
        try {
            String paymentLink = paymentService.generatePaymentLink(userId);
            return ResponseEntity.ok(Map.of("paymentLink", paymentLink));
        } catch (Exception e) {
            log.error("Error generating payment link: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/payment/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, String> payload) {
        try {
            // Извлечение необходимых параметров из тела запроса
            String label = payload.get("label");
            String sha1Hash = payload.get("sha1_hash");
            String notificationType = payload.get("notification_type");

            if (label == null || sha1Hash == null) {
                log.error("Missing required parameters: label or sha1_hash");
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required parameters: label or sha1_hash"));
            }

            // Проверка подписи (sha1_hash)
            String secret = "5EG2nUl0OXHIb61XNnAn4POd"; // Используйте значение из настроек
            String hashString = String.format(
                    "%s&%s&%s",
                    notificationType,
                    label,
                    secret
            );

            String calculatedHash = org.apache.commons.codec.digest.DigestUtils.sha1Hex(hashString);
            if (!calculatedHash.equals(sha1Hash)) {
                log.error("Invalid sha1_hash: {}", sha1Hash);
                return ResponseEntity.status(403).body(Map.of("error", "Invalid sha1_hash"));
            }

            // Логика обработки платежа
            Map<String, String> result = paymentService.savePaymentStatus(label);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error handling webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


}

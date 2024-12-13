package ru.rubytunnel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    public ResponseEntity<?> getPaymentLink(@RequestParam(name="userId") Long userId) {
        try {
            String paymentLink = paymentService.generatePaymentLink(userId);
            return ResponseEntity.ok(Map.of("paymentLink", paymentLink));
        } catch (Exception e) {
            log.error("Error generating payment link: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/payment/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> handleWebhook(@RequestParam Map<String, String> payload) {
        try {
            // Логика обработки
            String label = payload.get("label");
            String sha1Hash = payload.get("sha1_hash");

            if (label == null || sha1Hash == null) {
                log.error("Missing required parameters: label or sha1_hash");
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required parameters: label or sha1_hash"));
            }

            // Проверка подписи и другая логика
            Map<String, String> result = paymentService.savePaymentStatus(label);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error handling webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


}

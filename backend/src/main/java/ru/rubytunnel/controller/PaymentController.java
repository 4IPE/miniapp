package ru.rubytunnel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/payment/save")
    public ResponseEntity<?> getPaymentStatus(@RequestParam String label,
                                              @RequestParam Long chatId) {
        try {
            Map<String, String> status = paymentService.savePaymentStatus(label);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error fetching payment status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }


}

package ru.rubytunnel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rubytunnel.model.User;
import ru.rubytunnel.service.UserService;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/profile")
    public ResponseEntity<?> getOrCreateUserProfile(@RequestParam("userId") Long userId,
                                                    @RequestParam(value = "username") String username) {
        try {
            User user = userService.getOrCreateUser(userId, username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error processing user profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/profile/info")
    public ResponseEntity<?> getUserProfile(@RequestParam(name = "chatId") Long chatId) {
        try {
            User user = userService.getUserProfile(chatId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error get user profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/config")
    public ResponseEntity<?> getVpnConfig(@PathVariable Long userId) {
        try {
            Map<String, String> result = userService.generateVpnConfigAndQr(userId);
            Path configPath = Path.of(result.get("config_path"));
            Resource resource = new UrlResource(configPath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + configPath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error generating VPN config: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/qr")
    public ResponseEntity<?> getVpnQr(@PathVariable Long userId) {
        try {
            Map<String, String> result = userService.generateVpnConfigAndQr(userId);
            Path qrPath = Path.of(result.get("qr_code_path"));
            Resource resource = new UrlResource(qrPath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + qrPath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error generating QR code: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/export")
    public ResponseEntity<?> exportUsers(@RequestParam Long adminId) {
        try {
            byte[] data = userService.sendUserData(adminId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users_data.xlsx\"")
                    .body(data);
        } catch (Exception e) {
            log.error("Error exporting users: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/user/{userId}/disable")
    public ResponseEntity<?> disableUser(@PathVariable Long userId) {
        try {
            log.info("/user/{userId}/disable");
            User user = userService.disableClient(userId);
            return ResponseEntity.ok(Map.of("message", "User disabled successfully"));
        } catch (Exception e) {
            log.error("Error disabling user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/payment/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> data) {
        try {
            User user = userService.createPayment(data);
            return ResponseEntity.ok(Map.of("message", "Payment created successfully"));
        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/payment/status/{paymentId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentId) {
        try {
            Map<String, Object> status = userService.getPaymentStatus(paymentId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting payment status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/referral/input")
    public ResponseEntity<?> enterReferralCode(@RequestParam(name = "chatId") Long chatId,
                                               @RequestParam(name = "referralCode") Long referralCode
    ) {
        try {
            log.info("/user/referral/input");
            User user = userService.enterReferralCode(chatId, referralCode);
            return ResponseEntity.ok(Map.of("message", "Referral code applied successfully"));
        } catch (Exception e) {
            log.error("Error applying referral code: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/referral/count")
    public ResponseEntity<?> getCountReferal(@RequestParam(name = "chatId") Long chatId) {
        try {
            log.info("/user/referral/count");
            return ResponseEntity.ok().body(userService.countReferalWithUsers(chatId));
        } catch (Exception e) {
            log.error("Error count referral users: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Fail");
        }

    }
}

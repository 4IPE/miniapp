package ru.rubytunnel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/user/config")
    public ResponseEntity<?> getVpnConfig(@RequestParam(name = "userId") Long userId) {
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

    @PostMapping("/user/disable")
    public ResponseEntity<?> disableUser(@RequestParam(name = "userId") Long userId) {
        try {
            log.info("/user/{userId}/disable");
            User user = userService.disableClient(userId);
            return ResponseEntity.ok(Map.of("message", "User disabled successfully"));
        } catch (Exception e) {
            log.error("Error disabling user: {}", e.getMessage());
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
    @GetMapping("/user/checkSubscriptions")
    public ResponseEntity<Map<Long, String>> checkSubscriptions() {
        try {
            Map<Long, String> result = userService.checkSubscriptions();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error checking subscriptions", e);
            return ResponseEntity.badRequest().body(Map.of(-1L, "Error: " + e.getMessage()));
        }
    }
}

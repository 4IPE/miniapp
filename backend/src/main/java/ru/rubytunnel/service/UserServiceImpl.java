package ru.rubytunnel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rubytunnel.api.WgApi;
import ru.rubytunnel.model.User;
import ru.rubytunnel.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WgApi wgApi;
    private final Map<String, Map<String, Object>> payments = new HashMap<>();

    @Override
    public User getUserProfile(Long userId) {
        return userRepository.findByChatId(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Override
    public User getOrCreateUser(Long chatId, String username) {
        return userRepository.findByChatId(chatId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setChatId(chatId);
                    newUser.setNameTg(username);
                    return userRepository.save(newUser);
                });
    }
    @Override
    public User createUser(Long chatId, String name) {
        if (userRepository.existsByChatId(chatId)) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setChatId(chatId);
        user.setNameTg(name);
        return userRepository.save(user);
    }

    @Override
    public User enterReferralCode(Long userId, Long referralCode) {
        User user = getUserProfile(userId);
        User referralUser = userRepository.findByChatId(referralCode)
            .orElseThrow(() -> new RuntimeException("Referral user not found"));

        if (user.getChatId().equals(referralUser.getChatId())) {
            throw new RuntimeException("Cannot use own referral code");
        }

        if (user.getActive()) {
            user.setReferalCode(referralCode);
        } else {
            String wgId = wgApi.createClientWg(user.getNameTg()).get("id").toString();
            user.setWgId(wgId);
            user.setActive(true);
            user.setDemo(true);
            user.setSubscriptionEndDate(LocalDateTime.now().plusDays(7));
            user.setReferalCode(referralCode);
        }

        return userRepository.save(user);
    }

    @Override
    public User activateUserVpn(Long userId) {
        User user = getUserProfile(userId);

        if (user.getWgId() == null) {
            String wgId = wgApi.createClientWg(user.getNameTg()).get("id").toString();
            user.setWgId(wgId);
        } else {
            wgApi.enableClient(user.getWgId());
        }

        user.setActive(true);
        user.setDemo(false);
        user.setSubscriptionEndDate(LocalDateTime.now().plusDays(30));

        return userRepository.save(user);
    }

    @Override
    public Map<String, String> generateVpnConfigAndQr(Long userId) throws IOException {
        User user = getUserProfile(userId);
        if (!user.getActive()) {
            throw new RuntimeException("User is not active");
        }

        String configFileName = user.getNameTg() + ".conf";
        wgApi.downloadConfiguration(user.getWgId(), user.getNameTg());

        byte[] configContent = wgApi.generateQrCode(new String(java.nio.file.Files.readAllBytes(
            java.nio.file.Path.of(configFileName))));

        String qrFileName = user.getNameTg() + "_qrcode.png";
        java.nio.file.Files.write(java.nio.file.Path.of(qrFileName), configContent);

        Map<String, String> result = new HashMap<>();
        result.put("config_path", configFileName);
        result.put("qr_code_path", qrFileName);
        return result;
    }

    @Override
    public byte[] sendUserData(Long adminId) {
        // Implement Excel export logic here
        return new byte[0];
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User disableClient(Long userId) {
        User user = getUserProfile(userId);
        
        if (user.getWgId() != null) {
            wgApi.disableClientWg(user.getWgId());
        }

        user.setActive(false);
        user.setSubscriptionEndDate(null);
        user.setWgId(null);

        return userRepository.save(user);
    }

    @Override
    public User createPayment(Map<String, Object> data) {
        String paymentId = "payment_" + UUID.randomUUID().toString();
        payments.put(paymentId, data);
        // Implement payment logic
        return null;
    }

    @Override
    public Map<String, Object> getPaymentStatus(String paymentId) {
        return payments.getOrDefault(paymentId, Map.of("error", "Payment not found"));
    }
}

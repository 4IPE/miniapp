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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WgApi wgApi;

    @Override
    public User getUserProfile(Long userId) {
        return userRepository.findByChatId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserWithLabel(String label) {
        return userRepository.findByPayLabel(label)
                .orElseThrow(() -> new RuntimeException("This operation don t found"));
    }

    @Override
    @Transactional
    public User getOrCreateUser(Long chatId, String username) {
        User user = userRepository.findByChatId(chatId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setChatId(chatId);
                    newUser.setNameTg(username);
                    return userRepository.save(newUser);
                });
        Long count = user.getCountReferalUsers();
        Integer newPrice = Math.toIntExact(user.getPrice() - count <= 5 ? count * 10 : 50);
        if (!newPrice.equals(user.getPrice())) {
            user.setPrice(newPrice);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
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
        try {
            User user = getUserProfile(userId);

            User referralUser = userRepository.findByChatId(referralCode)
                    .orElseThrow(() -> new RuntimeException("Referral user not found"));

            if (user.getChatId().equals(referralUser.getChatId())) {
                throw new RuntimeException("Cannot use own referral code");
            }

            if (user.getReferalCode() != null) {
                throw new RuntimeException("Referral code already used");
            }

            if (user.getActive()) {
                user.setReferalCode(referralCode);
            } else {
                try {
                    Map<String, Object> wgResponse = wgApi.createClientWg(user.getNameTg());
                    if (wgResponse == null || wgResponse.get("success").equals(false)) {
                        throw new RuntimeException("Failed to create WireGuard client");
                    }

                    String wgId = wgApi.getClients(user.getNameTg());


                    user.setWgId(wgId);
                    user.setActive(true);
                    user.setDemo(true);
                    user.setSubscriptionEndDate(LocalDateTime.now().plusDays(7).withSecond(0).withNano(0));
                    user.setReferalCode(referralCode);

                    userRepository.save(referralUser);
                } catch (Exception e) {
                    throw new RuntimeException("Error creating WireGuard client: " + e.getMessage());
                }
            }

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error processing referral code: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public User activateUserVpn(Long userId) {
        User user = getUserProfile(userId);

        if (user.getWgId() == null) {
            wgApi.createClientWg(user.getNameTg());
            String wgId = wgApi.getClients(user.getNameTg());
            user.setWgId(wgId);
        } else {
            wgApi.enableClient(user.getWgId());
        }

        user.setActive(true);
        user.setDemo(false);
        user.setSubscriptionEndDate(LocalDateTime.now().plusDays(30).withSecond(0).withNano(0));

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

        Map<String, String> result = new HashMap<>();
        result.put("config_path", configFileName);
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

    @Transactional
    @Override
    public Long countReferalWithUsers(Long chatId) {
        User user = getUserProfile(chatId);
        Long count = userRepository.countActiveReferrals(String.valueOf(chatId));
        user.setCountReferalUsers(count);
        userRepository.save(user);
        return count;
    }
}

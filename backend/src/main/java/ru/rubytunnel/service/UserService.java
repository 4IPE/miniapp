package ru.rubytunnel.service;

import org.springframework.transaction.annotation.Transactional;
import ru.rubytunnel.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    User getUserProfile(Long userId);

    User getUserWithLabel(String label);

    User getOrCreateUser(Long chatId, String username);

    User createUser(Long chatId, String name);

    User enterReferralCode(Long userId, Long referralCode);

    User activateUserVpn(Long userId);

    Map<String, String> generateVpnConfigAndQr(Long userId) throws IOException;

    byte[] sendUserData(Long adminId);

    List<User> getAllUsers();

    User disableClient(Long userId);

    @Transactional
    Long countReferalWithUsers(Long chatId);

    Map<Long, String> checkSubscriptions();
}

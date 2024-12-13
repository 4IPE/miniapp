package ru.rubytunnel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rubytunnel.model.User;
import ru.rubytunnel.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final  UserService userService;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public String generatePaymentLink(Long userId) {
        User user = userService.getUserProfile(userId);

        String paymentLabel = UUID.randomUUID().toString();
        user.setPayLabel(paymentLabel);
        userRepository.save(user);

        String paymentLink = String.format(
                "https://yoomoney.ru/quickpay/confirm?receiver=%s&quickpay-form=shop&targets=%s&paymentType=%s&sum=%d&label=%s",
                "4100118840385217",            // Замените на ваш идентификатор
                "Оплата подписки",            // Цель платежа
                "AC",                         // Тип платежа (AC - банковская карта, SB - Сбербанк)
                user.getPrice(),              // Сумма
                paymentLabel                  // Уникальный label
        );

        return paymentLink;
    }


    @Override
    public Map<String, String> savePaymentStatus(String label) {
        User user = userService.getUserWithLabel(label);
        if (!user.getPayLabel().equalsIgnoreCase(label)) {
            Map<String, String> result = new HashMap<>();
            result.put("error", "label с таким статусом не был найден " + label);
            return result;
        }
        userService.activateUserVpn(user.getChatId());
        Map<String, String> result = new HashMap<>();
        result.put("success", "успешный платеж с label: " + label);
        return result;
    }


}

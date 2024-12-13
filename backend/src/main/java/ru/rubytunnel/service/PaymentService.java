package ru.rubytunnel.service;

import jakarta.transaction.Transactional;

import java.util.Map;

public interface PaymentService {
    @Transactional
    String generatePaymentLink(Long userId);


    @Transactional
    Map<String, String> savePaymentStatus(String label);
}

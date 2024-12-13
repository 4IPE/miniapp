package ru.rubytunnel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WgApi {

    private final String urlWg;
    private final String passwordWg;
    private final RestTemplate restTemplate;
    private String cookies;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WgApi(@Value("${wg.url}") String urlWg,
                 @Value("${wg.password}") String passwordWg) {
        this.urlWg = urlWg;
        this.passwordWg = passwordWg;
        this.restTemplate = new RestTemplate();
        adminLogin();
    }

    private void adminLogin() {
        String url = urlWg + "/api/session";
        Map<String, String> payload = new HashMap<>();
        payload.put("password", passwordWg);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            this.cookies = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            log.info("Сессия создана{}", cookies);
            if (this.cookies == null) {
                throw new RuntimeException("No session cookie returned by the server");
            }
        } else {
            throw new RuntimeException("Authentication failed: " + response.getStatusCode());
        }
    }

    public Map<String, Object> createClientWg(String clientName) {
        String url = urlWg + "/api/wireguard/client";
        Map<String, String> payload = new HashMap<>();
        payload.put("name", clientName);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                return objectMapper.readValue(response.getBody(), Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse create client response", e);
            }
        } else {
            throw new RuntimeException("Failed to create client: " + response.getStatusCode());
        }
    }

    public String getClients(String name) {
        String url = urlWg + "/api/wireguard/client";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Map<String, Object>> clients = response.getBody();
            return clients.stream()
                    .filter(client -> name.equals(client.get("name")))
                    .findFirst()
                    .map(client -> client.get("id").toString())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
        } else {
            throw new RuntimeException("Failed to get clients: " + response.getStatusCode());
        }
    }

    public void deleteClient(String clientId) {
        String url = urlWg + "/api/wireguard/client/" + clientId;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to delete client");
        }
    }

    public void downloadConfiguration(String clientId, String clientName) {
        String url = urlWg + "/api/wireguard/client/" + clientId + "/configuration";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, request, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                Files.write(Path.of(clientName + ".conf"), response.getBody());
            } catch (Exception e) {
                throw new RuntimeException("Failed to save configuration", e);
            }
        } else {
            throw new RuntimeException("Failed to download configuration");
        }
    }

    public byte[] generateQrCode(String configData) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            var bitMatrix = qrCodeWriter.encode(configData, BarcodeFormat.QR_CODE, 350, 350);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public void disableClientWg(String clientId) {
        String url = urlWg + "/api/wireguard/client/" + clientId + "/disable";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            Map<String, Object> respMap;
            try {
                respMap = objectMapper.readValue(response.getBody(), Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse disable client response", e);
            }
            if (!Boolean.TRUE.equals(respMap.get("success"))) {
                throw new RuntimeException("Disable client response is not success");
            }
        } else {
            throw new RuntimeException("Failed to disable client");
        }
    }

    public void enableClient(String clientId) {
        String url = urlWg + "/api/wireguard/client/" + clientId + "/enable";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            Map<String, Object> respMap;
            try {
                respMap = objectMapper.readValue(response.getBody(), Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse enable client response", e);
            }
            if (!Boolean.TRUE.equals(respMap.get("success"))) {
                throw new RuntimeException("Enable client response is not success");
            }
        } else {
            throw new RuntimeException("Failed to enable client");
        }
    }
}

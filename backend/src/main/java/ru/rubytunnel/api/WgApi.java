package ru.rubytunnel.api;

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

    public WgApi(@Value("${wg.url}") String urlWg,
                 @Value("${wg.password}") String passwordWg) {
        this.urlWg = urlWg;
        this.passwordWg = passwordWg;
        this.restTemplate = new RestTemplate();
        this.adminLogin();
    }

    private void adminLogin() {
        try {
            String url = urlWg + "/api/session";
            Map<String, String> payload = new HashMap<>();
            payload.put("password", passwordWg);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                this.cookies = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            } else {
                throw new RuntimeException("Authentication failed");
            }
        } catch (Exception e) {
            log.error("Error in adminLogin: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize WgApi", e);
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

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to create client");
        }
    }

    public String getClients(String name) {
        String url = urlWg + "/api/wireguard/client";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                List.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> clients = response.getBody();
            return clients.stream()
                    .filter(client -> name.equals(client.get("name")))
                    .findFirst()
                    .map(client -> client.get("id").toString())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
        } else {
            throw new RuntimeException("Failed to get clients");
        }
    }

    public void deleteClient(String clientId) {
        String url = urlWg + "/api/wireguard/client/" + clientId;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to delete client");
        }
    }

    public void downloadConfiguration(String clientId, String clientName) {
        String url = urlWg + "/api/wireguard/client/" + clientId + "/configuration";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
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

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Void.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to disable client");
        }
    }

    public void enableClient(String clientId) {
        String url = urlWg + "/api/wireguard/client/" + clientId + "/enable";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Void.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to enable client");
        }
    }
}

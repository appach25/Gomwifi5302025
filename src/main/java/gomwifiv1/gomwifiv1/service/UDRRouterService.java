package gomwifiv1.gomwifiv1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.config.RequestConfig;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class UDRRouterService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String routerIp;
    private final String username;
    private final String password;
    private String token;

    public UDRRouterService() {
        this.objectMapper = new ObjectMapper();
        this.routerIp = "192.168.1.1";
        this.username = "icnhaiti1@gmail.com";
        this.password = "Pjrappach251184";
        
        // Configure SSL to trust all certificates
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        
        SSLContext sslContext;
        try {
            sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            
            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(30000)  // 30 seconds
                    .setConnectionRequestTimeout(30000)
                    .setSocketTimeout(30000)
                    .build())
                .build();
            
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            requestFactory.setConnectTimeout(30000);
            requestFactory.setReadTimeout(30000);
            
            this.restTemplate = new RestTemplate(requestFactory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure SSL context", e);
        }
    }

    private void login() {
        // First, get the CSRF token
        String csrfUrl = "https://" + routerIp + "/api/csrf";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("User-Agent", "Mozilla/5.0");
        
        HttpEntity<String> csrfEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> csrfResponse = restTemplate.exchange(
                csrfUrl,
                HttpMethod.GET,
                csrfEntity,
                String.class
            );
            
            if (csrfResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode csrfJson = objectMapper.readTree(csrfResponse.getBody());
                String csrfToken = csrfJson.path("csrf_token").asText();
                if (csrfToken != null && !csrfToken.isEmpty()) {
                    headers.add("X-CSRF-Token", csrfToken);
                }
            }
            
            // Now perform the actual login
            String loginUrl = "https://" + routerIp + "/api/auth/login";
            
            Map<String, String> loginBody = new HashMap<>();
            loginBody.put("email", username);
            loginBody.put("password", password);
            loginBody.put("strict", "true");
            
            HttpEntity<Map<String, String>> loginEntity = new HttpEntity<>(loginBody, headers);
            
            ResponseEntity<String> loginResponse = restTemplate.exchange(
                loginUrl,
                HttpMethod.POST,
                loginEntity,
                String.class
            );
            
            if (loginResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(loginResponse.getBody());
                if (jsonNode.has("meta") && jsonNode.get("meta").has("rc") && 
                    "ok".equals(jsonNode.get("meta").get("rc").asText())) {
                    // Get the token from the cookie
                    String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
                    if (cookie != null && !cookie.isEmpty()) {
                        this.token = cookie;
                    } else {
                        throw new RuntimeException("No authentication cookie received");
                    }
                } else {
                    throw new RuntimeException("Login failed: " + loginResponse.getBody());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to login to UDR router: " + e.getMessage());
        }
    }

    public ResponseEntity<String> getVoucherList() {
        if (token == null) {
            login();
        }
        
        String url = "https://" + routerIp + "/api/s/default/stat/voucher";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("User-Agent", "Mozilla/5.0");
        headers.add("Cookie", token);  // Use the cookie for authentication
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
        } catch (Exception e) {
            // If unauthorized, try to login again
            if (e.getMessage().contains("401")) {
                login();
                headers.set("Cookie", token);
                entity = new HttpEntity<>(headers);
                return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
                );
            }
            throw e;
        }
    }
}

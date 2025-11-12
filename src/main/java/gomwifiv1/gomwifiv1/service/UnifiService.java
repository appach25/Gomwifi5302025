package gomwifiv1.gomwifiv1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gomwifiv1.gomwifiv1.config.UnifiConfig;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.client.config.RequestConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UnifiService {

    @Autowired
    private UnifiConfig unifiConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CloseableHttpClient httpClient = createHttpClient();
    private String csrfToken;
    private String cookie;
    private boolean isLegacy() {
        String url = unifiConfig.getControllerUrl();
        return url != null && url.contains(":8443");
    }

    private static final X509TrustManager TRUST_ALL_MANAGER = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    };

    private CloseableHttpClient createHttpClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{TRUST_ALL_MANAGER}, new SecureRandom());

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1.2"},
                null,
                NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslSocketFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(100);

            return HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLSocketFactory(sslSocketFactory)
                    .setConnectionManager(cm)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(5000)
                            .setSocketTimeout(5000)
                            .setRedirectsEnabled(false)
                            .build())
                    .disableRedirectHandling()
                    .disableAutomaticRetries()
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HTTP client", e);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("UniFi initialization disabled");
    }

    private void login() {
        System.out.println("Attempting to login to UniFi controller...");
        if (isLegacy()) {
            loginLegacy();
        } else {
            loginOs();
        }
    }

    private void loginOs() {
        try {
            String csrfUrl = unifiConfig.getControllerUrl() + "/api/auth/csrf";
            HttpGet csrfRequest = new HttpGet(csrfUrl);
            System.out.println("[OS] Fetching CSRF from: " + csrfRequest.getURI());
            try (CloseableHttpResponse csrfResponse = httpClient.execute(csrfRequest)) {
                int csrfStatus = csrfResponse.getStatusLine().getStatusCode();
                String csrfBody = csrfResponse.getEntity() != null ? EntityUtils.toString(csrfResponse.getEntity()) : "";
                System.out.println("[OS] CSRF response status: " + csrfStatus);
                System.out.println("[OS] CSRF response body: " + csrfBody);

                // Cookies
                StringBuilder cookieStr = new StringBuilder();
                for (org.apache.http.Header header : csrfResponse.getHeaders("Set-Cookie")) {
                    String headerValue = header.getValue();
                    int semicolonIndex = headerValue.indexOf(';');
                    if (semicolonIndex != -1) headerValue = headerValue.substring(0, semicolonIndex);
                    if (cookieStr.length() > 0) cookieStr.append("; ");
                    cookieStr.append(headerValue);
                }
                if (cookieStr.length() > 0) {
                    cookie = cookieStr.toString();
                    System.out.println("[OS] Cookies from CSRF: " + cookie);
                }

                try {
                    JsonNode csrfJson = csrfBody.isEmpty() ? null : objectMapper.readTree(csrfBody);
                    if (csrfJson != null && csrfJson.has("csrfToken")) {
                        csrfToken = csrfJson.get("csrfToken").asText();
                        System.out.println("[OS] Parsed CSRF token: " + csrfToken);
                    }
                } catch (Exception ignore) {}
            }

            HttpPost request = new HttpPost(unifiConfig.getControllerUrl() + "/api/auth/login");
            ObjectNode loginBody = objectMapper.createObjectNode();
            loginBody.put("username", unifiConfig.getUsername());
            loginBody.put("password", unifiConfig.getPassword());
            loginBody.put("rememberMe", true);
            String jsonBody = objectMapper.writeValueAsString(loginBody);
            request.setEntity(new StringEntity(jsonBody));
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json, */*;q=0.8");
            request.setHeader("User-Agent", "Mozilla/5.0");
            if (csrfToken != null) request.setHeader("X-CSRF-Token", csrfToken);
            if (cookie != null) request.setHeader("Cookie", cookie);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                System.out.println("[OS] Login response status: " + statusCode);
                System.out.println("[OS] Login response body: " + responseBody);
                if (statusCode != 200) throw new RuntimeException("OS login failed: " + statusCode + ", " + responseBody);
                for (org.apache.http.Header header : response.getHeaders("Set-Cookie")) {
                    String val = header.getValue();
                    if (val.startsWith("TOKEN=")) {
                        String tokenCookie = val.split(";")[0];
                        cookie = (cookie == null || cookie.isEmpty()) ? tokenCookie : (cookie.contains("TOKEN=") ? cookie : cookie + "; " + tokenCookie);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed OS login: " + e.getMessage(), e);
        }
    }

    private void loginLegacy() {
        try {
            HttpPost request = new HttpPost(unifiConfig.getControllerUrl() + "/api/login");
            ObjectNode loginBody = objectMapper.createObjectNode();
            loginBody.put("username", unifiConfig.getUsername());
            loginBody.put("password", unifiConfig.getPassword());
            String jsonBody = objectMapper.writeValueAsString(loginBody);
            request.setEntity(new StringEntity(jsonBody));
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");
            request.setHeader("User-Agent", "Mozilla/5.0");
            if (cookie != null) request.setHeader("Cookie", cookie);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                System.out.println("[LEGACY] Login response status: " + statusCode);
                System.out.println("[LEGACY] Login response body: " + responseBody);
                if (statusCode != 200) throw new RuntimeException("Legacy login failed: " + statusCode + ", " + responseBody);
                // Collect session cookie (unifises)
                StringBuilder cookieStr = new StringBuilder(cookie == null ? "" : cookie);
                for (org.apache.http.Header header : response.getHeaders("Set-Cookie")) {
                    String headerValue = header.getValue();
                    int semicolonIndex = headerValue.indexOf(';');
                    if (semicolonIndex != -1) headerValue = headerValue.substring(0, semicolonIndex);
                    if (cookieStr.length() > 0) cookieStr.append("; ");
                    cookieStr.append(headerValue);
                }
                cookie = cookieStr.toString();
                csrfToken = null; // not used on legacy
                System.out.println("[LEGACY] Using cookies: " + cookie);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed legacy login: " + e.getMessage(), e);
        }
    }

    public JsonNode createVoucher(int minutes, int quantity) {
        System.out.println("Creating voucher - Minutes: " + minutes + ", Quantity: " + quantity);
        try {
            String base = unifiConfig.getControllerUrl();
            String url = isLegacy() ? (base + "/api/s/" + unifiConfig.getSite() + "/cmd/hotspot")
                                    : (base + "/proxy/network/api/s/" + unifiConfig.getSite() + "/cmd/hotspot");
            HttpPost request = new HttpPost(url);
            
            ObjectNode body = objectMapper.createObjectNode();
            body.put("cmd", "create-voucher");
            body.put("expire", minutes);
            body.put("n", quantity);
            body.put("quota", 1);
            body.put("note", "Created via Gomwifi");
            body.put("up", 1000);
            body.put("down", 1000);
            body.put("bytes", -1);
            body.put("qos_overwrite", false);

            String jsonBody = objectMapper.writeValueAsString(body);
            System.out.println("Request body: " + jsonBody);
            request.setEntity(new StringEntity(jsonBody));
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");
            if (cookie != null) request.setHeader("Cookie", cookie);
            if (!isLegacy() && csrfToken != null) request.setHeader("X-Csrf-Token", csrfToken);
            
            System.out.println("Sending request to: " + request.getURI());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String jsonResponse = EntityUtils.toString(entity);
                return objectMapper.readTree(jsonResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create voucher", e);
        }
    }

    public List<JsonNode> getVouchers() {
        System.out.println("Fetching vouchers list...");
        try {
            // Try to login again to refresh the session
            login();
            
            // Then get vouchers
            String base = unifiConfig.getControllerUrl();
            String url = isLegacy() ? (base + "/api/s/" + unifiConfig.getSite() + "/stat/voucher")
                                    : (base + "/proxy/network/api/s/" + unifiConfig.getSite() + "/stat/voucher");
            HttpGet request = new HttpGet(url);
            System.out.println("GET Vouchers URL: " + request.getURI());
            
            if (cookie != null) request.setHeader("Cookie", cookie);
            if (!isLegacy() && csrfToken != null) request.setHeader("X-Csrf-Token", csrfToken);
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                System.out.println("Response status: " + statusCode);
                System.out.println("Voucher list response: " + responseBody);
                
                if (statusCode == 401) {
                    // Try logging in again and retry
                    login();
                    if (cookie != null) request.setHeader("Cookie", cookie);
                    if (!isLegacy() && csrfToken != null) request.setHeader("X-Csrf-Token", csrfToken);
                    try (CloseableHttpResponse retryResponse = httpClient.execute(request)) {
                        statusCode = retryResponse.getStatusLine().getStatusCode();
                        responseBody = EntityUtils.toString(retryResponse.getEntity());
                        System.out.println("Retry response status: " + statusCode);
                        System.out.println("Retry response body: " + responseBody);
                    }
                }
                
                JsonNode responseJson = objectMapper.readTree(responseBody);
                if (responseJson.has("data")) {
                    List<JsonNode> vouchers = new ArrayList<>();
                    responseJson.get("data").forEach(vouchers::add);
                    return vouchers;
                } else {
                    System.out.println("Failed to retrieve vouchers. Response: " + responseBody);
                    return new ArrayList<>();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get vouchers", e);
        }
    }

    public boolean deleteVoucher(String voucherId) {
        try {
            String base = unifiConfig.getControllerUrl();
            String url = isLegacy() ? (base + "/api/s/" + unifiConfig.getSite() + "/cmd/hotspot")
                                    : (base + "/proxy/network/api/s/" + unifiConfig.getSite() + "/cmd/hotspot");
            HttpPost request = new HttpPost(url);
            
            ObjectNode body = objectMapper.createObjectNode();
            body.put("cmd", "delete-voucher");
            body.put("_id", voucherId);

            request.setEntity(new StringEntity(objectMapper.writeValueAsString(body)));
            request.setHeader("Content-Type", "application/json");
            if (cookie != null) request.setHeader("Cookie", cookie);
            if (!isLegacy() && csrfToken != null) request.setHeader("X-Csrf-Token", csrfToken);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getStatusLine().getStatusCode() == 200;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete voucher", e);
        }
    }
}

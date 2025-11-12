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

/*
 * UDR Router Service has been temporarily disabled
 */
@Service
public class UDRRouterService {
    private final UnifiService unifiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UDRRouterService(UnifiService unifiService) {
        this.unifiService = unifiService;
    }

    public ResponseEntity<String> getVoucherList() {
        try {
            var vouchers = unifiService.getVouchers();
            String body = objectMapper.writeValueAsString(vouchers);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\":\"Failed to fetch vouchers from UniFi: " + ex.getMessage().replace("\"", "'") + "\"}");
        }
    }
}


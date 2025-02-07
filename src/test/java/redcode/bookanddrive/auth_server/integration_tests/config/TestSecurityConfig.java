package redcode.bookanddrive.auth_server.integration_tests.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@TestConfiguration
@RequiredArgsConstructor
public class TestSecurityConfig {


    public TestRestTemplate createTestRestTemplate() {
        // Create HttpClient that disables SSL verification
        CloseableHttpClient httpClient = HttpClients.custom()  // Disable hostname verification
            .build();

        // Create TestRestTemplate using HttpClient
        return new TestRestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}

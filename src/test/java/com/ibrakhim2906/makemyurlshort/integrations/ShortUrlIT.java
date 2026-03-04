package com.ibrakhim2906.makemyurlshort.integrations;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibrakhim2906.makemyurlshort.dtos.ShortenRequest;
import com.ibrakhim2906.makemyurlshort.dtos.ShortenResponse;
import com.ibrakhim2906.makemyurlshort.entities.ShortUrl;
import com.ibrakhim2906.makemyurlshort.repositories.ShortUrlRepository;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShortUrlIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    )
        .withDatabaseName("urlshortener")
        .withUsername("postgres")
        .withPassword("postgres");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // needed because your service injects app.base-url
        registry.add("app.base-url", () -> "http://localhost");
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    ShortUrlRepository repo;

    @BeforeEach
    void cleanDb() {
        repo.deleteAll();
    }

    @Test
    void shorten_success_returnsShortUrl() {
        ShortenRequest req = new ShortenRequest("https://example.com", null, 3);

        ResponseEntity<ShortenResponse> res = rest.postForEntity(
            "/api/shorten",
            req,
            ShortenResponse.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().code()).isNotBlank();
        assertThat(res.getBody().longUrl()).isEqualTo("https://example.com");
        assertThat(res.getBody().shortUrl()).contains(res.getBody().code());
        assertThat(res.getBody().expiresAt()).isNotNull();
    }

    @Test
    void redirect_incrementsClicks_andSetsLastAccessed() {
        // create
        ShortenRequest req = new ShortenRequest(
            "https://example.com",
            "myCode123",
            null
        );
        ShortenResponse created = rest
            .postForEntity("/api/shorten", req, ShortenResponse.class)
            .getBody();
        assertThat(created).isNotNull();

        // redirect (do NOT follow redirects, we want the 302)
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.HOST, "localhost:" + port);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> redirectRes = rest.exchange(
            "/{code}",
            HttpMethod.GET,
            entity,
            Void.class,
            created.code()
        );

        assertThat(redirectRes.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        URI location = redirectRes.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.toString()).isEqualTo("https://example.com");

        ShortUrl dbRow = repo.findByCode(created.code()).orElseThrow();
        assertThat(dbRow.getClicks()).isEqualTo(1L);
        assertThat(dbRow.getLastAccessedAt()).isNotNull();
    }

    @Test
    void customCode_conflict_returns409() {
        ShortenRequest req = new ShortenRequest(
            "https://example.com",
            "sameCode",
            null
        );

        ResponseEntity<ShortenResponse> first = rest.postForEntity(
            "/api/shorten",
            req,
            ShortenResponse.class
        );
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> second = rest.postForEntity(
            "/api/shorten",
            req,
            String.class
        );

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        // optional: your ErrorResponse JSON should contain message
        assertThat(second.getBody()).contains("Custom code already taken");
    }
}

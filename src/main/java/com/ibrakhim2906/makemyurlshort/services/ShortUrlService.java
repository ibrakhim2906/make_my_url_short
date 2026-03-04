package com.ibrakhim2906.makemyurlshort.services;

import com.ibrakhim2906.makemyurlshort.dtos.ShortenRequest;
import com.ibrakhim2906.makemyurlshort.dtos.ShortenResponse;
import com.ibrakhim2906.makemyurlshort.dtos.StatsResponse;
import com.ibrakhim2906.makemyurlshort.entities.ShortUrl;
import com.ibrakhim2906.makemyurlshort.exceptions.ResourceNotFoundException;
import com.ibrakhim2906.makemyurlshort.repositories.ShortUrlRepository;
import com.ibrakhim2906.makemyurlshort.utilities.CodeGenerator;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShortUrlService {

    private final ShortUrlRepository repo;

    @Value("${app.base-url}")
    private String baseUrl;

    public ShortUrlService(ShortUrlRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest req) {
        if (req.customCode() != null && !req.customCode().isBlank()) {
            String code = req.customCode().trim();

            ShortUrl n = new ShortUrl();
            n.setCode(code);
            n.setLongUrl(req.url());
            n.setClicks(0L);

            if (req.expiresInDays() != null) {
                n.setExpiresAt(
                    Instant.now().plus(req.expiresInDays(), ChronoUnit.DAYS)
                );
            }

            try {
                repo.save(n);
            } catch (DataIntegrityViolationException e) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Custom code already taken"
                );
            }

            String shortUrl = baseUrl.endsWith("/")
                ? baseUrl + code
                : baseUrl + "/" + code;
            return new ShortenResponse(
                n.getCode(),
                shortUrl,
                n.getLongUrl(),
                n.getExpiresAt(),
                n.getCreatedAt(),
                n.getClicks()
            );
        }

        final int length = 7;
        final int maxAttempts = 10;

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            String code = CodeGenerator.randomCode(length);

            ShortUrl n = new ShortUrl();
            n.setCode(code);
            n.setLongUrl(req.url());
            n.setClicks(0L);

            if (req.expiresInDays() != null) {
                n.setExpiresAt(
                    Instant.now().plus(req.expiresInDays(), ChronoUnit.DAYS)
                );
            }

            try {
                repo.save(n);
                String shortUrl = baseUrl.endsWith("/")
                    ? baseUrl + code
                    : baseUrl + "/" + code;
                return new ShortenResponse(
                    n.getCode(),
                    shortUrl,
                    n.getLongUrl(),
                    n.getExpiresAt(),
                    n.getCreatedAt(),
                    n.getClicks()
                );
            } catch (DataIntegrityViolationException e) {
                // collision -> try again with another code
            }
        }

        throw new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Failed to generate unique code, try again"
        );
    }

    @Transactional
    public String resolve(String code) {
        ShortUrl n = repo
            .findByCode(code)
            .orElseThrow(() ->
                new ResourceNotFoundException("Short URL not found")
            );

        if (
            n.getExpiresAt() != null && Instant.now().isAfter(n.getExpiresAt())
        ) {
            throw new ResponseStatusException(
                HttpStatus.GONE,
                "Short URL has expired"
            );
        }

        n.setClicks(n.getClicks() + 1);
        n.setLastAccessedAt(Instant.now());

        return n.getLongUrl();
    }

    public StatsResponse getStats(String code) {
        ShortUrl n = repo
            .findByCode(code)
            .orElseThrow(() ->
                new ResourceNotFoundException("Short URL not found")
            );

        return new StatsResponse(
            n.getCode(),
            n.getLongUrl(),
            n.getClicks(),
            n.getCreatedAt(),
            n.getLastAccessedAt(),
            n.getExpiresAt()
        );
    }
}

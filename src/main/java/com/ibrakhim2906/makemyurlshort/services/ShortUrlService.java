package com.ibrakhim2906.makemyurlshort.services;

import com.ibrakhim2906.makemyurlshort.dtos.ShortenRequest;
import com.ibrakhim2906.makemyurlshort.dtos.ShortenResponse;
import com.ibrakhim2906.makemyurlshort.entities.ShortUrl;
import com.ibrakhim2906.makemyurlshort.exceptions.ResourceNotFoundException;
import com.ibrakhim2906.makemyurlshort.repositories.ShortUrlRepository;
import com.ibrakhim2906.makemyurlshort.utilies.CodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
public class ShortUrlService {

    private final ShortUrlRepository repo;

    @Value("${app.base-url}")
    String baseUrl;

    public ShortUrlService(ShortUrlRepository repo) {
        this.repo=repo;
    }

    @Transactional
    public ShortenResponse shortenUrl(ShortenRequest req) {
        URI url = URI.create(req.url());

        String scheme = url.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalStateException("URLs starting with http/https schemas are only allowed.");
        }

        String code;
        if (req.customCode()!=null && !req.customCode().isEmpty()) {
            code = req.customCode();

            if (repo.existsByCode(code)) {
                throw new IllegalStateException("Custom code already taken");
            }
        } else {
            code = generateUniqueCode(7);
        }

        ShortUrl n = new ShortUrl();
        n.setCode(code);
        n.setLongURL(req.url());
        n.setClicks(0L);

        if (req.expiresInDays()!=null && req.expiresInDays() > 0) {
            n.setExpiresAt(Instant.now().plus(req.expiresInDays(), ChronoUnit.DAYS));
        }

        repo.save(n);

        String shortUrl = baseUrl.endsWith("/") ? baseUrl + code : baseUrl + "/" +code;

        return new ShortenResponse(n.getCode(), shortUrl, n.getLongURL(), n.getExpiresAt());
    }

    // GENERATE CODE HELPER
    private String generateUniqueCode(int length) {
        for (int attempts = 0; attempts <10; attempts++) {
            String c = CodeGenerator.randomCode(7);
            if (!repo.existsByCode(c)) {
                return c;
            }
        }
        throw new IllegalStateException("Failed to create unique code");
    }

    @Transactional
    public String resolve(String code) {
        ShortUrl n = repo.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        if (n.getExpiresAt() != null && Instant.now().isAfter(n.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL has expired");
        }

        return n.getLongURL();
    }

}

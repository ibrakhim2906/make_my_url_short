package com.ibrakhim2906.makemyurlshort.services;

import com.ibrakhim2906.makemyurlshort.dtos.ShortenRequest;
import com.ibrakhim2906.makemyurlshort.dtos.ShortenResponse;
import com.ibrakhim2906.makemyurlshort.repositories.ShortUrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        return new ShortenResponse();
    }

}

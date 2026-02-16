package com.ibrakhim2906.makemyurlshort.controllers;

import com.ibrakhim2906.makemyurlshort.dtos.ShortenRequest;
import com.ibrakhim2906.makemyurlshort.dtos.ShortenResponse;
import com.ibrakhim2906.makemyurlshort.services.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class ShortUrlController {
    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest req) {
        return ResponseEntity.ok(service.shortenUrl(req));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String longUrl = service.resolve(code);

        HttpHeaders header = new HttpHeaders();
        header.setLocation(URI.create(longUrl));

        return ResponseEntity.status(302).headers(header).build();
    }
}




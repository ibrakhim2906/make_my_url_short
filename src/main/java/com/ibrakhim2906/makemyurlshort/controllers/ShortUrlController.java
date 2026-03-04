package com.ibrakhim2906.makemyurlshort.controllers;

import com.ibrakhim2906.makemyurlshort.dtos.ShortenRequest;
import com.ibrakhim2906.makemyurlshort.dtos.ShortenResponse;
import com.ibrakhim2906.makemyurlshort.dtos.StatsResponse;
import com.ibrakhim2906.makemyurlshort.services.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(service.resolve(code))).build();
    }
    
    @GetMapping("/{code}/stats")
    public ResponseEntity<StatsResponse> getStats(@PathVariable String code) {
        return ResponseEntity.ok(service.getStats(code));
    }
}




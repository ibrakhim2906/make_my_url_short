package com.ibrakhim2906.makemyurlshort.dtos;

import java.time.Instant;

public record ShortenResponse(
        String code,
        String shortUrl,
        String longUrl,
        Instant expiresAt,
        Instant createdAt,
        Long clicks
) {
}

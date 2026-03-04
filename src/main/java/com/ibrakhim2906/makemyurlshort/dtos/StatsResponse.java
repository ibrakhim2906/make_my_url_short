package com.ibrakhim2906.makemyurlshort.dtos;

import java.time.Instant;

public record StatsResponse(
    String code,
    String longUrl,
    Long clicks,
    Instant createdAt,
    Instant lastAccessedAt,
    Instant expiresAt
) {}

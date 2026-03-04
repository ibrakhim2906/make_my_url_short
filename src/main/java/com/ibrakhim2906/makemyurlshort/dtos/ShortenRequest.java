package com.ibrakhim2906.makemyurlshort.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ShortenRequest (
        @NotBlank
        @Pattern(regexp = "^(https?://).+$",
                message = "url should start with http:// or https://")
        String url,
        @Pattern(regexp = "^[a-zA-Z0-9_-]{3,16}$",
                message = "custom code must contain 3-16 characters: letters, digits or _ or -")
        String customCode,
        @Min(value=1,
                message = "expiresInDays should be >=1")
        Integer expiresInDays
) {
}


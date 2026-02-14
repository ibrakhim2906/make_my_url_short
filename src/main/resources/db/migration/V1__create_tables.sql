CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(16) NOT NULL UNIQUE,
    long_url TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    clicks BIGINT NOT NULL DEFAULT 0,
    last_accessed_at TIMESTAMPTZ NULL
);

CREATE INDEX idx_short_urls_expires_at ON short_urls(expires_at);




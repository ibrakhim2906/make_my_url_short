# MakeMy URL Short

A REST API for shortening URLs, built with Spring Boot. Supports custom codes, expiry, and click tracking.

## Tech Stack
- Java 17, Spring Boot 3.5
- PostgreSQL + Flyway migrations
- Testcontainers for integration tests
- Docker

## Run with Docker
```bash
docker compose up
```
API will be available at `http://localhost:8080`.

## Run Manually
1. Start PostgreSQL and create a database: `CREATE DATABASE urlshortener;`
2. Copy `.env.example` to `.env` and fill in your values
3. Run: `./mvnw spring-boot:run`

## API

### Shorten a URL
`POST /api/shorten`
```json
{ "url": "https://example.com" }
```
```json
{ "code": "aB3xY7z", "shortUrl": "http://localhost:8080/aB3xY7z", "longUrl": "https://example.com", "expiresAt": null, "createdAt": "2024-01-01T00:00:00Z", "clicks": 0 }
```

### Shorten with custom code + expiry
`POST /api/shorten`
```json
{ "url": "https://example.com", "customCode": "my-link", "expiresInDays": 7 }
```

### Redirect
`GET /{code}` → 302 redirect to original URL

### Stats
`GET /{code}/stats`
```json
{ "code": "aB3xY7z", "longUrl": "https://example.com", "clicks": 42, "createdAt": "2024-01-01T00:00:00Z", "lastAccessedAt": "2024-01-05T10:00:00Z", "expiresAt": null }
```

## Tests
```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw verify -P integration
```

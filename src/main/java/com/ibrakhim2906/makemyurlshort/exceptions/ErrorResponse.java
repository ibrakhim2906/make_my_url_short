package com.ibrakhim2906.makemyurlshort.exceptions;

import java.time.Instant;

public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String message;
    private String path;

    public ErrorResponse(int status, String message, String path) {
        this.timestamp=Instant.now();
        this.status=status;
        this.message=message;
        this.path=path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

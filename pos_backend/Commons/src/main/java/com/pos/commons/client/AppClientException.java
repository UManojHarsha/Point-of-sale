package com.pos.commons.client;

public class AppClientException extends Exception {
    
    public AppClientException(String message) {
        super(message);
    }

    public AppClientException(String message, Throwable cause) {
        super(message, cause);
    }
} 
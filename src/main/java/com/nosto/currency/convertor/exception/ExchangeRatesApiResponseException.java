package com.nosto.currency.convertor.exception;

import org.springframework.http.HttpStatus;

public class ExchangeRatesApiResponseException extends RuntimeException {

    private final HttpStatus status;

    public ExchangeRatesApiResponseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

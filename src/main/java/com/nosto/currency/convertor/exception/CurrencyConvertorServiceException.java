package com.nosto.currency.convertor.exception;

public class CurrencyConvertorServiceException extends RuntimeException {

    public CurrencyConvertorServiceException(String message) {
        super(message);
    }

    public CurrencyConvertorServiceException(Throwable ex) {
        super(ex.getMessage());
    }
}

package com.nosto.currency.convertor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CurrencyConvertorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyConvertorApplication.class, args);
    }
}

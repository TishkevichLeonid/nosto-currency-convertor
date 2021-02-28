package com.nosto.currency.convertor.service;

import com.nosto.currency.convertor.cache.CacheClient;
import com.nosto.currency.convertor.controller.dto.CurrencyConvertorResponseDTO;
import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;
import com.nosto.currency.convertor.exception.CurrencyNotSupportedException;
import com.nosto.currency.convertor.rest.ExchangeRatesRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class CurrencyConvertorService {

    private final ExchangeRatesRestClient exchangeRatesRestClient;

    private final CacheClient cacheClient;

    @Autowired
    public CurrencyConvertorService(ExchangeRatesRestClient exchangeRatesRestClient, CacheClient cacheClient) {
        this.exchangeRatesRestClient = exchangeRatesRestClient;
        this.cacheClient = cacheClient;
    }

    public CurrencyConvertorResponseDTO convert(String source, String target, BigDecimal amount) {
        ExchangeRateResponseDTO exchangeRateFromCache = cacheClient.get(source);
        CurrencyConvertorResponseDTO currencyConvertorResponseDTO;
        if (exchangeRateFromCache != null) {
            currencyConvertorResponseDTO = processConvert(source, target, amount,
                    exchangeRateFromCache);
        } else {
            ExchangeRateResponseDTO exchangeRates = exchangeRatesRestClient.getExchangeRates(source);
            currencyConvertorResponseDTO = processConvert(source, target, amount, exchangeRates);
            cacheClient.put(source, exchangeRates);
        }
        return currencyConvertorResponseDTO;
    }

    private CurrencyConvertorResponseDTO processConvert(String source, String target, BigDecimal amount,
                                                        ExchangeRateResponseDTO exchangeRates) {
        BigDecimal rate = exchangeRates.getRates().get(target);
        if (rate == null) {
            String errorMessage = String.format("Target currency '%s' not supported", target);
            log.error(errorMessage);
            throw new CurrencyNotSupportedException(errorMessage);
        }
        BigDecimal resultAmount = amount.multiply(rate);
        CurrencyConvertorResponseDTO currencyConvertorResponseDTO = CurrencyConvertorResponseDTO.builder()
                .amount(resultAmount)
                .source(source)
                .target(target)
                .build();
        log.info("Created a response dto: {}", currencyConvertorResponseDTO.toString());
        return currencyConvertorResponseDTO;
    }
}

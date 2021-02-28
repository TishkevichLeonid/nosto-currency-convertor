package com.nosto.currency.convertor.rest;

import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;
import com.nosto.currency.convertor.exception.CurrencyNotSupportedException;
import com.nosto.currency.convertor.exception.ExchangeRatesApiResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class ExchangeRatesRestClient {

    private static final String LATEST_PATH = "/latest";
    private static final String BASE_CURRENCY_REQUEST_PARAM = "base";

    @Value("${currency-convertor-service.exchange-rates.base-url}")
    private String exchangeRatesUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public ExchangeRatesRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ExchangeRateResponseDTO getExchangeRates(String sourceCurrency) {
        log.info("Request for exchange rates api with source currency: {}", sourceCurrency);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(exchangeRatesUrl)
                .path(LATEST_PATH)
                .queryParam(BASE_CURRENCY_REQUEST_PARAM, sourceCurrency);
        try {
            ResponseEntity<ExchangeRateResponseDTO> responseEntity = restTemplate
                    .getForEntity(uriBuilder.toUriString(), ExchangeRateResponseDTO.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            HttpStatus statusCode = ex.getStatusCode();
            if (HttpStatus.BAD_REQUEST == statusCode) {
                String message = ex.getMessage();
                if (message != null && message.contains("is not supported")) {
                    message = String.format("Source currency '%s' is not supported.", sourceCurrency);
                    log.error(message);
                    throw new CurrencyNotSupportedException(message);
                }
            }
            String errorMessage = String.format("Exchange rate api service exception: '%s'", ex.getMessage());
            log.error(errorMessage);
            throw new ExchangeRatesApiResponseException(errorMessage, ex.getStatusCode());
        } catch (ResourceAccessException ex) {
            log.error(ex.getMessage());
            throw new ExchangeRatesApiResponseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

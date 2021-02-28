package com.nosto.currency.convertor.rest;

import com.nosto.currency.convertor.controller.dto.ExchangeRateResponseDTO;
import com.nosto.currency.convertor.exception.CurrencyNotSupportedException;
import com.nosto.currency.convertor.exception.ExchangeRatesApiResponseException;
import com.nosto.currency.convertor.util.ResponsesUtil;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRatesRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeRatesRestClient exchangeRatesRestClient;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(exchangeRatesRestClient, "exchangeRatesUrl", "https://test-url.xyz");
    }

    @Test
    public void successQueryForExchangeRates() {
        ExchangeRateResponseDTO exchangeRatesMock = ResponsesUtil.validExchangeEURtoUSDServiceResponse();
        Mockito.when(restTemplate.getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(ResponseEntity.of(Optional.of(exchangeRatesMock)));
        ExchangeRateResponseDTO exchangeRatesResponse = exchangeRatesRestClient.getExchangeRates("EUR");
        Assertions.assertThat(exchangeRatesMock).isEqualTo(exchangeRatesResponse);
    }

    @Test
    public void clientErrorExceptionCurrencyNotSupported() {
        String invalidCurrency = "EU";
        Mockito.when(restTemplate.getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                        String.format("error: Base '%s' is not supported.", invalidCurrency)));
        try {
            exchangeRatesRestClient.getExchangeRates(invalidCurrency);
        } catch (CurrencyNotSupportedException ex) {
            Assertions.assertThat(ex.getMessage())
                    .isEqualTo(String.format("Source currency '%s' is not supported.", invalidCurrency));
        }
    }

    @Test
    public void internalExchangeServiceError() {
        String errorFromExchangeService = "500 Internal server error";
        Mockito.when(restTemplate.getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
        try {
            exchangeRatesRestClient.getExchangeRates("EUR");
        } catch (ExchangeRatesApiResponseException ex) {
            Assertions.assertThat(ex.getMessage())
                    .isEqualTo(String.format("Exchange rate api service exception: '%s'", errorFromExchangeService));
        }
    }

    @Test
    public void badRequestException() {
        String errorFromExchangeService = "400 Bad request";
        Mockito.when(restTemplate.getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));
        try {
            exchangeRatesRestClient.getExchangeRates("EUR");
        } catch (ExchangeRatesApiResponseException ex) {
            Assertions.assertThat(ex.getMessage())
                    .isEqualTo(String.format("Exchange rate api service exception: '%s'", errorFromExchangeService));
        }
    }

    @Test
    public void resourceAccessException() {
        String localUriError = "Invalid uri";
        Mockito.when(restTemplate.getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenThrow(new ResourceAccessException(localUriError));
        try {
            exchangeRatesRestClient.getExchangeRates("EUR");
        } catch (ExchangeRatesApiResponseException ex) {
            Assertions.assertThat(ex.getMessage())
                    .isEqualTo(localUriError, localUriError);
        }
    }
}

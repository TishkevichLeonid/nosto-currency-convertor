package com.nosto.currency.convertor.exception.handler;

import com.nosto.currency.convertor.exception.CurrencyConvertorServiceException;
import com.nosto.currency.convertor.exception.CurrencyNotSupportedException;
import com.nosto.currency.convertor.exception.ExchangeRatesApiResponseException;
import com.nosto.currency.convertor.exception.handler.template.CurrencyConvertorExceptionTemplate;
import com.nosto.currency.convertor.exception.handler.template.ExceptionArgumentsResponseTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class CurrencyConvertorServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CurrencyNotSupportedException.class)
    public ResponseEntity<CurrencyConvertorExceptionTemplate> handleCurrencyNotSupportedException(
            CurrencyNotSupportedException ex) {
        return new ResponseEntity<>(new CurrencyConvertorExceptionTemplate(OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrencyConvertorServiceException.class)
    public ResponseEntity<CurrencyConvertorExceptionTemplate> handleCurrencyConvertorServiceException(
            CurrencyConvertorServiceException ex) {
        return new ResponseEntity<>(new CurrencyConvertorExceptionTemplate(OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExchangeRatesApiResponseException.class)
    public ResponseEntity<CurrencyConvertorExceptionTemplate> handleCurrencyExchangeRatesApiResponseException(
            ExchangeRatesApiResponseException ex) {
        return new ResponseEntity<>(new CurrencyConvertorExceptionTemplate(OffsetDateTime.now(),
                ex.getStatus(), ex.getMessage()), ex.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleRequestArgumentsNotValid(ConstraintViolationException ex) {
        String message = ex.getMessage();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        Map<String, Object> errorMap = new HashMap<>();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            Iterator<Path.Node> iterator = constraintViolation.getPropertyPath().iterator();
            String paramName = null;
            while (iterator.hasNext()) {
                Path.Node name = iterator.next();
                if (!iterator.hasNext()) {
                    paramName = name.getName();
                }
            }
            errorMap.put(paramName, constraintViolation.getInvalidValue());
        }
        return new ResponseEntity<>(new ExceptionArgumentsResponseTemplate(message, HttpStatus.BAD_REQUEST.value(),
                "ArgumentNotValidException", errorMap), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        return new ResponseEntity<>(new CurrencyConvertorExceptionTemplate(OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

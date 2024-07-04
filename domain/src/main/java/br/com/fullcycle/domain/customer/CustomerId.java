package br.com.fullcycle.domain.customer;

import br.com.fullcycle.domain.exceptions.ValidationException;

import java.util.UUID;

import static java.util.Objects.isNull;

public record CustomerId(String value) {
    
    public CustomerId {
        if(isNull(value)) {
            throw new ValidationException("Invalid value for CustomerId");
        }
    }
    
    public static CustomerId unique() {
        return new CustomerId(UUID.randomUUID().toString());
    }
    
    public static CustomerId with(final String value) {
        try {
            return new CustomerId(UUID.fromString(value).toString());
        } catch(IllegalArgumentException ex) {
            throw new ValidationException("Invalid value for CustomerId");
        }
    }
}

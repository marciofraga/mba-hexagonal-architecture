package br.com.fullcycle.hexagonal.application.domain.partner;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

import java.util.UUID;

import static java.util.Objects.isNull;

public record PartnerId(String value) {

    public PartnerId {
        if(isNull(value)) {
            throw new ValidationException("Invalid value for PartnerId");
        }
    }
    
    public static PartnerId unique() {
        return new PartnerId(UUID.randomUUID().toString());
    }
    
    public static PartnerId with(final String value) {
        try {
            return new PartnerId(UUID.fromString(value).toString());
        } catch(IllegalArgumentException ex) {
            throw new ValidationException("Invalid value for PartnerId");
        }
    }
}

package br.com.fullcycle.domain.person;

import br.com.fullcycle.domain.exceptions.ValidationException;

import static java.util.Objects.isNull;

public record Name(String value) {
    
    public Name {
        if(isNull(value)) {
            throw new ValidationException("Invalid value for name");
        }
    }
    
}

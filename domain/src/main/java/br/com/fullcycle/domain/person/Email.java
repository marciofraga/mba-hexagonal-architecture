package br.com.fullcycle.domain.person;

import br.com.fullcycle.domain.exceptions.ValidationException;

import static java.util.Objects.isNull;

public record Email(String value) {
    
    public Email {
        if(isNull(value) || !value.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")) {
            throw new ValidationException("Invalid value for email");
        }
    }
}

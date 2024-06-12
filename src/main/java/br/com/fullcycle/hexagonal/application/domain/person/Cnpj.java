package br.com.fullcycle.hexagonal.application.domain.person;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

import static java.util.Objects.isNull;

public record Cnpj(String value) {

    public Cnpj {
        if(isNull(value) || !value.matches("^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}$")) {
            throw new ValidationException("Invalid value for cnpj");
        }
    }
}

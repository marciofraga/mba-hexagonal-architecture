package br.com.fullcycle.hexagonal.application.domain.person;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

import static java.util.Objects.isNull;

public record Cpf(String value) {

    public Cpf {
        if(isNull(value) || !value.matches("^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$")) {
            throw new ValidationException("Invalid value for cpf");
        }
    }
}

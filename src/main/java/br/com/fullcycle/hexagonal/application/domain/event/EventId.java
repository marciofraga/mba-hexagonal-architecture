package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

import java.util.UUID;

import static java.util.Objects.isNull;

public record EventId(String value) {

    public EventId {
        if(isNull(value)) {
            throw new ValidationException("Invalid value for EventId");
        }
    }
    
    public static EventId unique() {
        return new EventId(UUID.randomUUID().toString());
    }
    
    public static EventId with(final String value) {
        try {
            return new EventId(UUID.fromString(value).toString());
        } catch(IllegalArgumentException ex) {
            throw new ValidationException("Invalid value for EventId");
        }
    }
}

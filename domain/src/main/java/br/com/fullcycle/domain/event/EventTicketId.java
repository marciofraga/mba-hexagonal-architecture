package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.exceptions.ValidationException;

import java.util.UUID;

import static java.util.Objects.isNull;

public record EventTicketId(String value) {

    public EventTicketId {
        if(isNull(value)) {
            throw new ValidationException("Invalid value for EventTicketId");
        }
    }
    
    public static EventTicketId unique() {
        return new EventTicketId(UUID.randomUUID().toString());
    }
    
    public static EventTicketId with(final String value) {
        try {
            return new EventTicketId(UUID.fromString(value).toString());
        } catch(IllegalArgumentException ex) {
            throw new ValidationException("Invalid value for EventTicketId");
        }
    }
}

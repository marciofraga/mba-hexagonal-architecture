package br.com.fullcycle.domain.event.ticket;

import br.com.fullcycle.domain.exceptions.ValidationException;

import java.util.UUID;

import static java.util.Objects.isNull;

public record TicketId(String value) {

    public TicketId {
        if(isNull(value)) {
            throw new ValidationException("Invalid value for TicketId");
        }
    }
    
    public static TicketId unique() {
        return new TicketId(UUID.randomUUID().toString());
    }
    
    public static TicketId with(final String value) {
        try {
            return new TicketId(UUID.fromString(value).toString());
        } catch(IllegalArgumentException ex) {
            throw new ValidationException("Invalid value for TicketId");
        }
    }
}

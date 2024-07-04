package br.com.fullcycle.domain.event.ticket;

import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.isNull;

public class Ticket {
    
    private final TicketId ticketId;
    private CustomerId customerId;
    private EventId eventId;
    private TicketStatus status;
    private Instant paidAt;
    private Instant reservedAt;

    public Ticket(
            final TicketId ticketId, 
            final CustomerId customerId, 
            final EventId eventId, 
            final TicketStatus status, 
            final Instant paidAt, 
            final Instant reservedAt
    ) {
        this.ticketId = ticketId;
        this.setCustomerId(customerId);
        this.setEventId(eventId);
        this.setStatus(status);
        this.setPaidAt(paidAt);
        this.setReservedAt(reservedAt);
    }
    
    public static Ticket create(final CustomerId customerId, final EventId eventId) {
        return new Ticket(TicketId.unique(), customerId, eventId, TicketStatus.PENDING, null, Instant.now());
    }

    public TicketId ticketId() {
        return ticketId;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public EventId eventId() {
        return eventId;
    }

    public TicketStatus status() {
        return status;
    }

    public Instant paidAt() {
        return paidAt;
    }

    public Instant reservedAt() {
        return reservedAt;
    }

    private void setCustomerId(CustomerId customerId) {
        if(isNull(customerId)) {
            throw new ValidationException("Invalid customerId for Ticket");    
        }
        
        this.customerId = customerId;
    }

    private void setEventId(EventId eventId) {
        if(isNull(eventId)) {
            throw new ValidationException("Invalid eventId for Ticket");
        }
        
        this.eventId = eventId;
    }

    private void setStatus(TicketStatus status) {
        if(isNull(status)) {
            throw new ValidationException("Invalid status for Ticket");
        }
        
        this.status = status;
    }

    private void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    private void setReservedAt(Instant reservedAt) {
        if(isNull(reservedAt)) {
            throw new ValidationException("Invalid reservedAt for Ticket");
        }
        
        this.reservedAt = reservedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }
}

package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.ticket.TicketId;
import br.com.fullcycle.domain.exceptions.ValidationException;

import static java.util.Objects.isNull;

public class EventTicket {
    
    private final EventTicketId eventTicketId;
    private final EventId eventId;
    private final CustomerId customerId;
    private TicketId ticketId;
    private int ordering;

    public EventTicket(EventTicketId eventTicketId, EventId eventId, CustomerId customerId, TicketId ticketId, int ordering) {
        if(isNull(eventTicketId)) {
            throw new ValidationException("Invalid ticketId for EventTicket");
        }

        if(isNull(eventId)) {
            throw new ValidationException("Invalid eventId for EventTicket");
        }
        
        if(isNull(customerId)) {
            throw new ValidationException("Invalid customerId for EventTicket");
        }
        
        this.eventTicketId = eventTicketId;
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.customerId = customerId;
        this.setOrdering(ordering);
    }

    public static EventTicket create(EventId eventId, CustomerId customerId, int ordering) {
        return new EventTicket(EventTicketId.unique(), eventId, customerId, null, ordering);
    }
    
    public EventTicket associatedTicket(final TicketId aTicket) {
        this.ticketId = aTicket;
        return this;
    }
    
    public TicketId getTicketId() {
        return ticketId;
    }

    public EventId getEventId() {
        return eventId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public int getOrdering() {
        return ordering;
    }

    public EventTicketId getEventTicketId() {
        return eventTicketId;
    }

    private void setOrdering(int ordering) {
        if(isNull(ordering)) {
            throw new ValidationException("Invalid ordering for EventTicket");
        }
        
        this.ordering = ordering;
    }
}

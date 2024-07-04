package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.ticket.TicketId;
import br.com.fullcycle.domain.exceptions.ValidationException;

import static java.util.Objects.isNull;

public class EventTicket {
    
    private final TicketId ticketId;
    private final EventId eventId;
    private final CustomerId customerId;
    private int ordering;

    public EventTicket(TicketId ticketId, EventId eventId, CustomerId customerId, int ordering) {
        if(isNull(ticketId)) {
            throw new ValidationException("Invalid ticketId for EventTicket");
        }

        if(isNull(eventId)) {
            throw new ValidationException("Invalid eventId for EventTicket");
        }
        
        if(isNull(customerId)) {
            throw new ValidationException("Invalid customerId for EventTicket");
        }
        
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.customerId = customerId;
        this.setOrdering(ordering);
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

    private void setOrdering(int ordering) {
        if(isNull(ordering)) {
            throw new ValidationException("Invalid ordering for EventTicket");
        }
        
        this.ordering = ordering;
    }
}

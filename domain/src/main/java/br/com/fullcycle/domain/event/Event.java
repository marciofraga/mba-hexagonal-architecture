package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.partner.PartnerId;
import br.com.fullcycle.domain.person.Name;
import br.com.fullcycle.domain.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.isNull;

public class Event {

    private static final int ONE = 1;
    private final EventId eventId;
    private final Set<EventTicket> tickets;
    private final Set<DomainEvent> domainEvents;
    private Name name;
    private LocalDate date;
    private int totalSpots;
    private PartnerId partnerId;

    public Event(
            EventId eventId, 
            String name, 
            String date, 
            Integer totalSpots, 
            PartnerId partnerId, 
            Set<EventTicket> tickets
    ) {
        this(eventId, tickets);
        this.setName(name);
        this.setDate(date);
        this.setTotalSpots(totalSpots);
        this.setPartnerId(partnerId);
    }
    
    private Event(final EventId eventId, final Set<EventTicket> tickets) {
        if(isNull(eventId)) {
            throw new ValidationException("Invalid eventId for Event");
        }
        
        this.eventId = eventId;
        this.tickets = tickets != null ? tickets : new HashSet<>(0);
        this.domainEvents = new HashSet<>(2);
    }
    
    public static Event create(String name, String date, Integer totalSpots, Partner partner) {
        return new Event(
                EventId.unique(),
                name,
                date,
                totalSpots,
                partner.getPartnerId(),
                null
        );
    }

    public static Event restore(
            final String id, 
            final String name, 
            final String date, 
            final int totalSpots, 
            final String partnerId, 
            Set<EventTicket> tickets
    ) {
        return new Event(EventId.with(id), name, date, totalSpots, PartnerId.with(partnerId), tickets);    
    }

    public EventId getEventId() {
        return eventId;
    }

    public Name getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTotalSpots() {
        return totalSpots;
    }

    public PartnerId getPartnerId() {
        return partnerId;
    }

    public Set<EventTicket> getTickets() {
        return Collections.unmodifiableSet(tickets);
    }

    public Set<DomainEvent> allDomainEvents() {
        return Collections.unmodifiableSet(domainEvents);
    }

    public EventTicket reserveTicket(final CustomerId customerId) {
        this.getTickets().stream()
                .filter(it -> Objects.equals(it.getCustomerId(), customerId))
                .findFirst()
                .ifPresent(it -> {
                    throw new ValidationException("Email already registered");
                });
        
        if(getTotalSpots() < getTickets().size() + ONE) {
            throw new ValidationException("Event sold out");
        }
        
        final var aTicket = EventTicket.create(getEventId(), customerId, getTickets().size() + 1);
        this.tickets.add(aTicket);
        this.domainEvents.add(new EventTicketReserved(aTicket.getEventTicketId(), getEventId(), customerId));
        return aTicket;
    }

    private void setDate(String date) {
        if(isNull(date)) {
            throw new ValidationException("Invalid date for Event");
        }
        
        try {
            this.date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);   
        }catch (RuntimeException ex) {
            throw new ValidationException("Invalid date for Event", ex);
        }
    }

    private void setName(String name) {
        this.name = new Name(name);
    }

    private void setTotalSpots(Integer totalSpots) {
        if(isNull(totalSpots)) {
            throw new ValidationException("Invalid totalSpots for Event");
        }
        
        this.totalSpots = totalSpots;
    }

    private void setPartnerId(PartnerId partnerId) {
        if(isNull(partnerId)) {
            throw new ValidationException("Invalid partnerId for Event");
        }
        
        this.partnerId = partnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}

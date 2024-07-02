package br.com.fullcycle.hexagonal.infrastructure.jpa.entities;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.EventTicket;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "EventTicket")
@Table(name = "events_tickets")
public class EventTicketEntity {

    @Id
    private UUID ticketId;
    private UUID customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    private EventEntity event;
    private int ordering;


    public EventTicketEntity() {
    }

    public EventTicketEntity(
            final UUID ticketId,
            final UUID customerId,
            final EventEntity event,
            final int ordering
    ) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.event = event;
        this.ordering = ordering;
    }

    public static EventTicketEntity of(final EventEntity event, final EventTicket ev) {
        return new EventTicketEntity(
                UUID.fromString(ev.getTicketId().value()),
                UUID.fromString(ev.getCustomerId().value()),
                event,
                ev.getOrdering()
        );
    }

    public EventTicket toEventTicket() {
        return new EventTicket(
                TicketId.with(this.ticketId.toString()),
                EventId.with(this.event.getId().toString()),
                CustomerId.with(this.customerId.toString()),
                this.ordering
        );
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventTicketEntity that = (EventTicketEntity) o;
        return ordering == that.ordering && Objects.equals(ticketId, that.ticketId) && Objects.equals(customerId, that.customerId) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, customerId, event, ordering);
    }
}

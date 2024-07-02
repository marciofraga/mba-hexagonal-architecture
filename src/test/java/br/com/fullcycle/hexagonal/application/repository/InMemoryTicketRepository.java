package br.com.fullcycle.hexagonal.application.repository;

import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryTicketRepository implements TicketRepository {

    private final Map<String, Ticket> ticket;

    public InMemoryTicketRepository() {
        this.ticket = new HashMap<>();
    }

    @Override
    public Optional<Ticket> ticketOfId(TicketId anId) {
        return Optional.ofNullable(this.ticket.get(Objects.requireNonNull(anId).value()));
    }

   

    @Override
    public Ticket create(Ticket ticket) {
        this.ticket.put(ticket.ticketId().value(), ticket);
        return ticket;
    }

    @Override
    public Ticket update(Ticket ticket) {
        this.ticket.put(ticket.ticketId().value(), ticket);
        return ticket;
    }

    @Override
    public void deleteAll() {
        ticket.clear();
    }
}

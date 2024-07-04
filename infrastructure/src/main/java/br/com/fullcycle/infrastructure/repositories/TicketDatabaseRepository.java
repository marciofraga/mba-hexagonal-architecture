package br.com.fullcycle.infrastructure.repositories;

import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.event.ticket.TicketId;
import br.com.fullcycle.domain.event.ticket.TicketRepository;
import br.com.fullcycle.infrastructure.jpa.entities.TicketEntity;
import br.com.fullcycle.infrastructure.jpa.repositories.TicketJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class TicketDatabaseRepository implements TicketRepository {
    
    private final TicketJpaRepository ticketJpaRepository;

    public TicketDatabaseRepository(TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @Override
    public Optional<Ticket> ticketOfId(final TicketId anId) {
        return this.ticketJpaRepository.findById(UUID.fromString(anId.value()))
                .map(TicketEntity::toTicket);
    }

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        return this.ticketJpaRepository.save(TicketEntity.of(ticket))
                .toTicket();
    }

    @Override
    public Ticket update(Ticket ticket) {
        return this.ticketJpaRepository.save(TicketEntity.of(ticket))
                .toTicket();
    }

    @Override
    public void deleteAll() {
        this.ticketJpaRepository.deleteAll();
    }
}

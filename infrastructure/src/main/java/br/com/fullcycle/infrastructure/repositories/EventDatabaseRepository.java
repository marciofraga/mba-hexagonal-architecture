package br.com.fullcycle.infrastructure.repositories;

import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.EventRepository;
import br.com.fullcycle.infrastructure.jpa.entities.EventEntity;
import br.com.fullcycle.infrastructure.jpa.repositories.EventJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class EventDatabaseRepository implements EventRepository {
    
    private final EventJpaRepository eventJpaRepository;

    public EventDatabaseRepository(EventJpaRepository eventJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
    }

    @Override
    public Optional<Event> eventOfId(final EventId anId) {
        return this.eventJpaRepository.findById(UUID.fromString(anId.value()))
                .map(EventEntity::toEvent);
    }

    @Override
    @Transactional
    public Event create(Event event) {
        return this.eventJpaRepository.save(EventEntity.of(event))
                .toEvent();
    }

    @Override
    public Event update(Event ticket) {
        return this.eventJpaRepository.save(EventEntity.of(ticket))
                .toEvent();
    }

    @Override
    public void deleteAll() {
        this.eventJpaRepository.deleteAll();
    }
}

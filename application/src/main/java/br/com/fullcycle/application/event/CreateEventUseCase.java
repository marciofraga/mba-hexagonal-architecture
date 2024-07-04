package br.com.fullcycle.application.event;

import br.com.fullcycle.application.UseCase;
import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.event.EventRepository;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.domain.partner.PartnerId;
import br.com.fullcycle.domain.partner.PartnerRepository;

import java.util.Objects;

public class CreateEventUseCase extends UseCase<CreateEventUseCase.Input, CreateEventUseCase.Output> {

    private final EventRepository eventRepository;
    private final PartnerRepository partnerRepository;

    public CreateEventUseCase(final EventRepository eventRepository, final PartnerRepository partnerRepository) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.partnerRepository = Objects.requireNonNull(partnerRepository);
    }

    @Override
    public Output execute(final Input input) {
        final var aPartner = partnerRepository.partnerOfId(PartnerId.with(input.partnerId))
                .orElseThrow(() -> new ValidationException("Partner not found"));
        
        final var anEvent = eventRepository.create(Event.create(input.name, input.date, input.totalSpots, aPartner));
        return new Output(
                anEvent.getEventId().value(), 
                input.date, 
                anEvent.getName().value(), 
                anEvent.getTotalSpots(), 
                anEvent.getPartnerId().value()
        );
    }

    public record Input(String date, String name, String partnerId, Integer totalSpots) { }
    public record Output(String id, String date, String name, int totalSpots, String partnerId) { }
}

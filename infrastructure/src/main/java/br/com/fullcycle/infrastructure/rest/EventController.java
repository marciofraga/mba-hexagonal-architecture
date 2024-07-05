package br.com.fullcycle.infrastructure.rest;

import br.com.fullcycle.application.event.CreateEventUseCase;
import br.com.fullcycle.application.event.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.infrastructure.dtos.NewEventDTO;
import br.com.fullcycle.infrastructure.dtos.SubscribeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "events")
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase;

    public EventController(CreateEventUseCase createEventUseCase, SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase) {
        this.createEventUseCase = createEventUseCase;
        this.subscribeCustomerToEventUseCase = subscribeCustomerToEventUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody NewEventDTO dto) {
        try {
            final var output = createEventUseCase.execute(
                    new CreateEventUseCase.Input(
                            dto.date(),
                            dto.name(),
                            dto.partnerID(),
                            dto.totalSpots()
                    ));
            return ResponseEntity.created(URI.create("/events/" + output.id())).body(output);
        } catch (ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }
    
    @PostMapping(value = "/{id}/subscribe")
    public ResponseEntity<?> subscribe(@PathVariable String id, @RequestBody SubscribeDTO dto) {
        try {
            final var output = subscribeCustomerToEventUseCase.execute(
                    new SubscribeCustomerToEventUseCase.Input(id, dto.customerId()));
            return ResponseEntity.ok(output);
        } catch (ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }
}

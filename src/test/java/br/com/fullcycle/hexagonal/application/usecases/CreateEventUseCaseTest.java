package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateEventUseCaseTest {

    @Test
    @DisplayName("Deve criar um evento")
    void testCreate() {

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = TSID.fast().toLong();

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        final var eventService = Mockito.mock(EventService.class);
        final var partnerService = Mockito.mock(PartnerService.class);

        when(partnerService.findById(expectedPartnerId)).thenReturn(Optional.of(new Partner()));

        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            e.setId(expectedPartnerId);
            return e;
        });

        final var useCase = new CreateEventUseCase(eventService, partnerService);
        final var output = useCase.execute(createInput);

        assertNotNull(output.id());
        assertEquals(expectedDate, output.date());
        assertEquals(expectedName, output.name());
        assertEquals(expectedTotalSpots, output.totalSpots());
        assertEquals(expectedPartnerId, output.id());

    }

    @Test
    @DisplayName("Não deve criar um evento quando um partner não for encontrado")
    void shouldThrowErrorWhenPartnerDoesntExists() {

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = TSID.fast().toLong();
        final var expectedError = "Partner not found";

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        final var eventService = Mockito.mock(EventService.class);
        final var partnerService = Mockito.mock(PartnerService.class);

        when(partnerService.findById(expectedPartnerId)).thenReturn(Optional.empty());

        final var useCase = new CreateEventUseCase(eventService, partnerService);
        final var actualError = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        assertEquals(expectedError, actualError.getMessage());


    }

}
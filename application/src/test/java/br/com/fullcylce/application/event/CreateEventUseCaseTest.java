package br.com.fullcylce.application.event;

import br.com.fullcycle.application.event.CreateEventUseCase;
import br.com.fullcylce.application.repository.InMemoryEventRepository;
import br.com.fullcylce.application.repository.InMemoryPartnerRepository;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.partner.PartnerId;
import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateEventUseCaseTest {

    @Test
    @DisplayName("Deve criar um evento")
    void testCreate() {

        final var aPartner = Partner
                .create("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = aPartner.getPartnerId().value();

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        final var eventRepository = new InMemoryEventRepository();
        final var partnerRepository = new InMemoryPartnerRepository();
        
        partnerRepository.create(aPartner);

        final var useCase = new CreateEventUseCase(eventRepository, partnerRepository);
        final var output = useCase.execute(createInput);

        assertNotNull(output.id());
        assertEquals(expectedDate, output.date());
        assertEquals(expectedName, output.name());
        assertEquals(expectedTotalSpots, output.totalSpots());
        assertEquals(expectedPartnerId, output.partnerId());
    }

    @Test
    @DisplayName("Não deve criar um evento quando um partner não for encontrado")
    void shouldThrowErrorWhenPartnerDoesntExists() {

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = PartnerId.unique().value();
        final var expectedError = "Partner not found";

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        final var eventRepository = new InMemoryEventRepository();
        final var partnerRepository = new InMemoryPartnerRepository();

        final var useCase = new CreateEventUseCase(eventRepository, partnerRepository);
        final var actualError = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        assertEquals(expectedError, actualError.getMessage());


    }

}
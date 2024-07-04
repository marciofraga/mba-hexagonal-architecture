package br.com.fullcycle.application.event;

import br.com.fullcycle.AbstractIntegrationTest;
import br.com.fullcycle.domain.event.EventRepository;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.partner.PartnerId;
import br.com.fullcycle.domain.partner.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateEventUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private CreateEventUseCase useCase;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PartnerRepository partnerRepository;

    @BeforeEach
    void tearDown() {
        eventRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um evento")
    void testCreate() {

        final var partner = createPartner("12.345.678/0001-00", "john.doe@gmail.com", "John Doe");
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = partner.getPartnerId().value();

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);
        final var output = useCase.execute(createInput);

        assertNotNull(output.id());
        assertEquals(expectedDate, output.date());
        assertEquals(expectedName, output.name());
        assertEquals(expectedPartnerId, output.partnerId());
        assertEquals(expectedTotalSpots, output.totalSpots());

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
        final var actualError = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        assertEquals(expectedError, actualError.getMessage());


    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        return partnerRepository.create(Partner.create(name, cnpj, email));
    }
}
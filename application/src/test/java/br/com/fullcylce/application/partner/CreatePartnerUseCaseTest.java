package br.com.fullcylce.application.partner;

import br.com.fullcycle.application.partner.CreatePartnerUseCase;
import br.com.fullcylce.application.repository.InMemoryPartnerRepository;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreatePartnerUseCaseTest {

    @Test
    @DisplayName("Deve criar um parceiro")
    void testCreatePartner() {
        final var expectedCNPJ = "12.456.789/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        final var partnerRepository = new InMemoryPartnerRepository();
        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var output = useCase.execute(createInput);

        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com email duplicado")
    void testCreateWithDuplicatedCNPJShouldFail() {
        final var expectedCNPJ = "12.456.789/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var aPartner = Partner.create(expectedName, "12.456.789/0002-00", expectedEmail);
        
        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);
        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);


        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    void testCreateWithDuplicatedEmailShouldFail() {
        final var expectedCNPJ = "12.456.789/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var aPartner = Partner.create(expectedName, "12.456.789/0002-00", expectedEmail);

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);
        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);

        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }
}

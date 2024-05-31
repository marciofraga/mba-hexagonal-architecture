package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreatePartnerUseCaseTest {

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreatePartner() {
        final var expectedCNPJ = "12345678900";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findByCnpj(expectedCNPJ)).thenReturn(Optional.empty());
        when(partnerService.findByEmail(expectedEmail)).thenReturn(Optional.empty());
        when(partnerService.save(any())).thenAnswer(a -> {
            var partner = a.getArgument(0, Partner.class);
            partner.setId(UUID.randomUUID().getMostSignificantBits());
            return partner;
        });

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var output = useCase.execute(createInput);

        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com email duplicado")
    public void testCreateWithDuplicatedCNPJShouldFail() {
        final var expectedCNPJ = "12345678900";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        final var aPartner = new Partner();
        aPartner.setId(UUID.randomUUID().getMostSignificantBits());
        aPartner.setCnpj(expectedCNPJ);
        aPartner.setEmail(expectedEmail);
        aPartner.setName(expectedName);

        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findByCnpj(expectedCNPJ)).thenReturn(Optional.of(aPartner));

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        final var expectedCPF = "12345678900";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var createInput = new CreatePartnerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var aPartner = new Partner();
        aPartner.setId(UUID.randomUUID().getMostSignificantBits());
        aPartner.setCnpj(expectedCPF);
        aPartner.setEmail(expectedEmail);
        aPartner.setName(expectedName);

        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findByEmail(expectedEmail)).thenReturn(Optional.of(aPartner));

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }
}

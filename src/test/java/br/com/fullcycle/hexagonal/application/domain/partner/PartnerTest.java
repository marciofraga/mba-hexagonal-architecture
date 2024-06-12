package br.com.fullcycle.hexagonal.application.domain.partner;

import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartnerTest {
    
    @Test
    @DisplayName("Deve instanciar um cliente")
    void testCreatePartner() {
        final var expectedCNPJ = "41.536.538/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        
        final var actualPartner = Partner.create(expectedName, expectedCNPJ, expectedEmail);
        
        assertNotNull(actualPartner.getPartnerId());
        assertEquals(expectedCNPJ, actualPartner.getCnpj().value());
        assertEquals(expectedEmail, actualPartner.getEmail().value());
        assertEquals(expectedName, actualPartner.getName().value());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com CNPJ invalido")
    void testCreatePartnerWithInvalidCNPJ() {
        final var expectedError = "Invalid value for cnpj";

        final var actualError = assertThrows(ValidationException.class, 
                () -> Partner.create("John Doe", "123456.789-00", "john.doe@gmail.com"));

        assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com nome invalido")
    void testCreatePartnerWithInvalidNome() {
        final var expectedError = "Invalid value for name";

        final var actualError = assertThrows(ValidationException.class,
                () -> Partner.create(null, "41.536.538/0001-00", "john.doe@gmail.com"));

        assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com email invalido")
    void testCreatePartnerWithInvalidEmail() {
        final var expectedError = "Invalid value for email";

        final var actualError = assertThrows(ValidationException.class,
                () -> Partner.create("John Doe", "41.536.538/0001-00", "john.doe@gmail"));

        assertEquals(expectedError, actualError.getMessage());
    }
}

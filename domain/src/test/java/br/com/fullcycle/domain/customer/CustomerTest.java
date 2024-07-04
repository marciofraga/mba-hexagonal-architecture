package br.com.fullcycle.domain.customer;

import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest {
    
    @Test
    @DisplayName("Deve instanciar um cliente")
    void testCreateCustomer() {
        final var expectedCPF = "123.456.789-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        
        final var actualCustomer = Customer.create(expectedName, expectedCPF, expectedEmail);
        
        assertNotNull(actualCustomer.getCustomerId());
        assertEquals(expectedCPF, actualCustomer.getCpf().value());
        assertEquals(expectedEmail, actualCustomer.getEmail().value());
        assertEquals(expectedName, actualCustomer.getName().value());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com CPF invalido")
    void testCreateCustomerWithInvalidCPF() {
        final var expectedError = "Invalid value for cpf";

        final var actualError = assertThrows(ValidationException.class, 
                () -> Customer.create("John Doe", "123456.789-00", "john.doe@gmail.com"));

        assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com nome invalido")
    void testCreateCustomerWithInvalidNome() {
        final var expectedError = "Invalid value for name";

        final var actualError = assertThrows(ValidationException.class,
                () -> Customer.create(null, "123.456.789-00", "john.doe@gmail.com"));

        assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com email invalido")
    void testCreateCustomerWithInvalidEmail() {
        final var expectedError = "Invalid value for email";

        final var actualError = assertThrows(ValidationException.class,
                () -> Customer.create("John Doe", "123.456.789-00", "john.doe@gmail"));

        assertEquals(expectedError, actualError.getMessage());
    }
}

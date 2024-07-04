package br.com.fullcycle.application.customer;

import br.com.fullcycle.AbstractIntegrationTest;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.customer.CustomerRepository;
import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class CreateCustomerUseCaseIT extends AbstractIntegrationTest {

    @Autowired
    private CreateCustomerUseCase useCase;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um cliente")
    void testCreateCustomer() {
        final var expectedCPF = "123.456.789-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var output = useCase.execute(createInput);

        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com CPF duplicado")
    void testCreateWithDuplicatedCPFShouldFail()  {
        final var expectedCPF = "123.456.789-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";

        createCustomer(expectedCPF, expectedEmail, expectedName);
        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    void testCreateWithDuplicatedEmailShouldFail()  {
        final var expectedCPF = "123.456.089-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";

        createCustomer("249.999.999-99", expectedEmail, expectedName);

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }

    private void createCustomer(final String cpf, final String email, final String name) {
        customerRepository.create(Customer.create(name, cpf, email));
    }
}

package br.com.fullcylce.application.customer;

import br.com.fullcycle.application.customer.CreateCustomerUseCase;
import br.com.fullcylce.application.repository.InMemoryCustomerRepository;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateCustomerUseCaseTest {

    @Test
    @DisplayName("Deve criar um cliente")
    void testCreateCustomer() {
        final var expectedCPF = "123.456.789-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var customerRepository = new InMemoryCustomerRepository();
        final var useCase = new CreateCustomerUseCase(customerRepository);
        final var output = useCase.execute(createInput);

        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com CPF duplicado")
    void testCreateWithDuplicatedCPFShouldFail() {
        final var expectedCPF = "123.456.789-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";

        final var aCustomer = Customer.create(expectedName, expectedCPF, expectedEmail);
        
        final var customerRepository = new InMemoryCustomerRepository();
        customerRepository.create(aCustomer);
        
        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var useCase = new CreateCustomerUseCase(customerRepository);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    void testCreateWithDuplicatedEmailShouldFail()  {
        final var expectedCPF = "123.456.789-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";

        final var aCustomer = Customer.create(expectedName, expectedCPF, expectedEmail);
        final var customerRepository = new InMemoryCustomerRepository();
        customerRepository.create(aCustomer);
        
        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);
        

        final var useCase = new CreateCustomerUseCase(customerRepository);
        final var output = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        Assertions.assertEquals(expectedError, output.getMessage());
    }
}

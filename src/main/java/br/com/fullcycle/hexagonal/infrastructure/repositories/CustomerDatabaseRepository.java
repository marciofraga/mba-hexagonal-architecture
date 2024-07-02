package br.com.fullcycle.hexagonal.infrastructure.repositories;

import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.person.Cpf;
import br.com.fullcycle.hexagonal.application.domain.person.Email;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.CustomerEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.CustomerJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerDatabaseRepository implements CustomerRepository {
    
    private final CustomerJpaRepository customerJpaRepository;

    public CustomerDatabaseRepository(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Optional<Customer> customerOfId(final CustomerId anId) {
        return this.customerJpaRepository.findById(UUID.fromString(anId.value()))
                .map(CustomerEntity::toCustomer);
    }

    @Override
    public Optional<Customer> customerOfCPF(Cpf cpf) {
        return this.customerJpaRepository.findByCpf(cpf.value())
                .map(CustomerEntity::toCustomer);
    }

    @Override
    public Optional<Customer> customerOfEmail(Email email) {
        return this.customerJpaRepository.findByEmail(email.value())
                .map(CustomerEntity::toCustomer);
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        return this.customerJpaRepository.save(CustomerEntity.of(customer))
                .toCustomer();
    }

    @Override
    public Customer update(Customer customer) {
        return this.customerJpaRepository.save(CustomerEntity.of(customer))
                .toCustomer();
    }

    @Override
    public void deleteAll() {
        this.customerJpaRepository.deleteAll();
    }
}

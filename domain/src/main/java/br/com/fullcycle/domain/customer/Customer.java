package br.com.fullcycle.domain.customer;

import br.com.fullcycle.domain.person.Cpf;
import br.com.fullcycle.domain.person.Email;
import br.com.fullcycle.domain.person.Name;
import br.com.fullcycle.domain.exceptions.ValidationException;

import java.util.Objects;

import static java.util.Objects.isNull;

public class Customer {
    
    private final CustomerId customerId;
    private Name name;
    private Cpf cpf;
    private Email email;

    public Customer(final CustomerId customerId, final String name, final String cpf, final String email) {
        if(isNull(customerId)) {
            throw new ValidationException("Invalid customerId for Customer");
        }
        
        this.customerId = customerId;
        this.setName(name);
        this.setCpf(cpf);
        this.setEmail(email);
    }
    
    public static Customer create(String name, String cpf, String email) {
        return new Customer(CustomerId.unique(), name, cpf, email);
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Name getName() {
        return name;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }

    private void setCpf(String cpf) {
        this.cpf = new Cpf(cpf);
    }

    private void setEmail(String email) {
        this.email = new Email(email);
    }

    private void setName(String name) {
        this.name = new Name(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}

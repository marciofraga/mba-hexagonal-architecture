package br.com.fullcycle.domain.partner;

import br.com.fullcycle.domain.person.Cnpj;
import br.com.fullcycle.domain.person.Email;
import br.com.fullcycle.domain.person.Name;
import br.com.fullcycle.domain.exceptions.ValidationException;

import java.util.Objects;

import static java.util.Objects.isNull;

public class Partner {
    
    private final PartnerId partnerId;
    private Name name;
    private Cnpj cnpj;
    private Email email;

    public Partner(final PartnerId partnerId, final String name, final String cnpj, final String email) {
        if(isNull(partnerId)) {
            throw new ValidationException("Invalid partnerId for Partner");
        }
        
        this.partnerId = partnerId;
        this.setName(name);
        this.setCnpj(cnpj);
        this.setEmail(email);
    }
    
    public static Partner create(String name, String cnpj, String email) {
        return new Partner(PartnerId.unique(), name, cnpj, email);
    }

    public PartnerId getPartnerId() {
        return partnerId;
    }

    public Name getName() {
        return name;
    }

    public Cnpj getCnpj() {
        return cnpj;
    }

    public Email getEmail() {
        return email;
    }

    private void setCnpj(String cnpj) {
        this.cnpj = new Cnpj(cnpj);
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
        Partner partner = (Partner) o;
        return Objects.equals(partnerId, partner.partnerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partnerId);
    }
}

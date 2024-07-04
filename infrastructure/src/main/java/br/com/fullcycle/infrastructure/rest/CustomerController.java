package br.com.fullcycle.infrastructure.rest;

import br.com.fullcycle.application.Presenter;
import br.com.fullcycle.application.customer.CreateCustomerUseCase;
import br.com.fullcycle.application.customer.GetCustomerByIdUseCase;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.infrastructure.dtos.NewCustomerDTO;
import br.com.fullcycle.infrastructure.rest.presenters.GetCustomerByIdResponseEntity;
import br.com.fullcycle.infrastructure.rest.presenters.PublicGetCustomerByIdResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(value = "customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase, 
            GetCustomerByIdUseCase getCustomerByIdUseCase
    ) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerByIdUseCase = getCustomerByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewCustomerDTO dto) {
        try {
            final var output = createCustomerUseCase
                    .execute(new CreateCustomerUseCase.Input(dto.cpf(), dto.email(), dto.name()));
            return ResponseEntity.created(URI.create("/customers/" + output.id())).body(output);
        }catch (ValidationException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable String id, @RequestHeader(name = "X-Public", required = false) String xPublic) {
        Presenter<Optional<GetCustomerByIdUseCase.Output>, Object> presenter = new GetCustomerByIdResponseEntity();
        
        if(xPublic != null) {
            presenter = new PublicGetCustomerByIdResponseEntity();
        }
        return getCustomerByIdUseCase
                .execute(new GetCustomerByIdUseCase.Input(id), presenter);
        
    }
}
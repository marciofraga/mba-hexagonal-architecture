package br.com.fullcycle.infrastructure.rest.presenters;

import br.com.fullcycle.application.Presenter;
import br.com.fullcycle.application.customer.GetCustomerByIdUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class GetCustomerByIdResponseEntity implements Presenter<Optional<GetCustomerByIdUseCase.Output>, Object> {
    
    private static final Logger LOG = LoggerFactory.getLogger(GetCustomerByIdResponseEntity.class);
    
    @Override
    public ResponseEntity<?> present(Optional<GetCustomerByIdUseCase.Output> output) {
        return output.map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @Override
    public Object present(Throwable error) {
        LOG.error("An error was observer at GetCustomerByIdUseCase", error);
        return ResponseEntity.notFound().build();
    }
}

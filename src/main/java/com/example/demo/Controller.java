package com.example.demo;

import com.example.demo.model.Balance;
import com.example.demo.repository.BankPostgresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.scheduler.Schedulers.boundedElastic;

@Component
public class Controller {
    public static final String ID = "id";
    @Autowired
    private BankPostgresRepository bankPostgresRepository;

    public RouterFunction<ServerResponse> router() {
        return nest(path("/"), route()
                .GET("/client/{" + ID + "}/balance", getBalance())
                .build()
        );
    }

    public HandlerFunction<ServerResponse> getBalance() {
        return r -> Mono.just(r.pathVariable(ID))
                .flatMap(s -> bankPostgresRepository.getBalance(Integer.valueOf(s)))
                .subscribeOn(boundedElastic())
                .transform(subscriptions -> ok().contentType(APPLICATION_JSON).body(subscriptions, Balance.class));
    }
}

package com.example.demo;

import com.example.demo.model.Balance;
import com.example.demo.model.Client;
import com.example.demo.model.Transaction;
import com.example.demo.repository.BankPostgresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.scheduler.Schedulers.boundedElastic;

public class Controller {

    private final BankPostgresRepository bankPostgresRepository;

    public Controller(BankPostgresRepository bankPostgresRepository) {
        this.bankPostgresRepository = bankPostgresRepository;
    }

    public RouterFunction<ServerResponse> router() {
        return nest(path("/"), route()
                .POST("/client/new/{balance}", createClient())
                .GET("/client/{id}/balance", getBalance())
                .POST("/transaction", createTransaction())
                .build()
        );
    }

    private HandlerFunction<ServerResponse> createTransaction() {
        return r -> r.bodyToMono(Transaction.class)
                .flatMap(transaction -> bankPostgresRepository.createTransaction(transaction))
                .subscribeOn(boundedElastic())
                .transform(subscriptions -> ok().contentType(APPLICATION_JSON).body(subscriptions, Transaction.class));

    }

    public HandlerFunction<ServerResponse> createClient() {
        return r -> bankPostgresRepository.createClient(new Client(0, "", "", ""))
                .doOnSuccess(client -> bankPostgresRepository
                        .createTransaction(new Transaction(0, 0, client.getId(), Integer.valueOf(r.pathVariable("balance"))))
                        .subscribe()
                )
                .subscribeOn(boundedElastic())
                .transform(subscriptions -> ok().contentType(APPLICATION_JSON).body(subscriptions, Client.class));
    }

    public HandlerFunction<ServerResponse> getBalance() {
        return r -> bankPostgresRepository.getBalance(Integer.valueOf(r.pathVariable("id")))
                .subscribeOn(boundedElastic())
                .transform(subscriptions -> ok().contentType(APPLICATION_JSON).body(subscriptions, Balance.class));
    }
}

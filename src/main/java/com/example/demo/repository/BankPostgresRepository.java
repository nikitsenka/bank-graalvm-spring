package com.example.demo.repository;

import com.example.demo.model.Balance;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BankPostgresRepository {
    public static final String GET_BALANCE_SQL = "SELECT debit - credit FROM (SELECT COALESCE(sum(amount), 0) AS debit FROM transaction WHERE to_client_id = $1 ) a, ( SELECT COALESCE(sum(amount), 0) AS credit FROM transaction WHERE from_client_id = $2 ) b;";

    @Autowired
    private ConnectionFactory connectionFactory;

    public Mono<Balance> getBalance(Integer clientId) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> Mono.from(connection.createStatement(GET_BALANCE_SQL)
                        .bind("$1", clientId)
                        .bind("$2", clientId)
                        .execute())
                        .doFinally((st) -> close(connection)))
                .flatMap(result -> Mono.from(result.map((row, meta) -> {
                    Balance balance = new Balance();
                    balance.setBalance(row.get(0, Integer.class));
                    return balance;
                })));
    }


    private <T> Mono<T> close(Connection connection) {
        return Mono.from(connection.close())
                .then(Mono.empty());
    }


}

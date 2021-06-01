package com.example.demo.repository;

import com.example.demo.model.Balance;
import com.example.demo.model.Client;
import com.example.demo.model.Transaction;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BankPostgresRepository {
    public static final String GET_BALANCE_SQL = "SELECT debit - credit FROM (SELECT COALESCE(sum(amount), 0) AS debit FROM transaction WHERE to_client_id = $1 ) a, ( SELECT COALESCE(sum(amount), 0) AS credit FROM transaction WHERE from_client_id = $1 ) b;";
    public static final String INSERT_CLIENT_SQL = "INSERT INTO client(name, email, phone) VALUES ($1, $2, $3) RETURNING id";
    public static final String INSERT_TRANSACTION_SQL = "INSERT INTO transaction(from_client_id, to_client_id, amount) VALUES ($1, $2, $3) RETURNING id";

    @Autowired
    private ConnectionFactory connectionFactory;

    public Mono<Balance> getBalance(Integer clientId) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> Mono.from(connection.createStatement(
                        //Quick fix because of issue https://github.com/pgjdbc/r2dbc-postgresql/issues/411
                        GET_BALANCE_SQL.replaceAll("\\$1", String.valueOf(clientId)))
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


    public Mono<Client> createClient(Client client) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> Mono.from(connection.createStatement(INSERT_CLIENT_SQL)
                        .bind("$1", client.getName())
                        .bind("$2", client.getEmail())
                        .bind("$3", client.getPhone())
                        .execute())
                        .doFinally((st) -> close(connection)))
                .flatMap(result -> Mono.from(result.map((row, meta) -> {
                    client.setId(row.get(0, Integer.class));
                    return client;
                })));
    }

    public Mono<Transaction> createTransaction(Transaction transaction) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> Mono.from(connection.createStatement(INSERT_TRANSACTION_SQL)
                        .bind("$1", transaction.getFromClientId())
                        .bind("$2", transaction.getToClientId())
                        .bind("$3", transaction.getAmount())
                        .execute())
                        .doFinally((st) -> close(connection)))
                .flatMap(result -> Mono.from(result.map((row, meta) -> {
                    transaction.setId(row.get(0, Integer.class));
                    return transaction;
                })));
    }
}

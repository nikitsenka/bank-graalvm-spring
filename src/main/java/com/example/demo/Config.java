package com.example.demo;

import com.example.demo.repository.BankPostgresRepository;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class Config {

    /**
     * Enables routes.
     *
     * @return functional routes
     */
    @Bean
    public RouterFunction<ServerResponse> routes(Controller controller) {
        return controller.router();
    }

    /**
     * Controller for REST configuration.
     * @param repository
     * @return
     */
    @Bean
    public Controller controller(BankPostgresRepository repository){
        return new Controller(repository);
    }

    /**
     * Repository for Postgres DB connection.
     * @param connectionFactory
     * @return
     */
    @Bean
    public BankPostgresRepository repository(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        return new BankPostgresRepository(connectionFactory);
    }
}

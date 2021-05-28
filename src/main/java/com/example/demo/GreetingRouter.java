package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GreetingRouter {

    /**
     * Enables routes.
     *
     * @return functional routes
     */
    @Bean
    public RouterFunction<ServerResponse> routes(Controller controller) {
        return controller.router();
    }
}
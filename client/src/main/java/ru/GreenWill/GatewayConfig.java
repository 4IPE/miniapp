package ru.GreenWill;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("backend", r -> r.path("/api/**")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .filter((exchange, chain) -> {
                                    System.out.println("Request: " + exchange.getRequest().getPath());
                                    return chain.filter(exchange);
                                }))
                        .uri("http://api:9090"))
                .build();
    }


}

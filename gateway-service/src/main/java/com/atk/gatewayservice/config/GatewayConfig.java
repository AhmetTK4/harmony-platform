package com.atk.gatewayservice.config;

import com.atk.gatewayservice.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    public GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("auth-endpoints", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://user-service"))

                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://product-service"))

                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://order-service"))

                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://payment-service"))

                .route("shipping-service", r -> r
                        .path("/api/shipping/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://shipping-service"))

                .build();
    }

}

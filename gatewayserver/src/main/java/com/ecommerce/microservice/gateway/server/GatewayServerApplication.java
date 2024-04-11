package com.ecommerce.microservice.gateway.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Date;


@SpringBootApplication
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/ecommerce/admin/users/**")
                        .filters(f -> f.rewritePath("/ecommerce/admin/users/(?<segment>.*)","/${segment}")
                                .addResponseHeader("X-Response-Time",new Date().toString()))
                        .uri("lb://ADMIN"))
                .route(p -> p
                    .path("/ecommerce/admin/customers/**")
                    .filters(f -> f.rewritePath("/ecommerce/admin/customers/(?<segment>.*)","/${segment}")
                            .addResponseHeader("X-Response-Time",new Date().toString()))
                    .uri("lb://CUSTOMER")).build();
    }
}

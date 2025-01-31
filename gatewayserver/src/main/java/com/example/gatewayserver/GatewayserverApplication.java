package com.example.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(final RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
								  .route(p -> p.path("/alpine/accounts/**")
											   .filters(f -> f.rewritePath("alpine/accounts/(?<segment>.*)","/${segment}")
															 .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
											   .uri("lb://ACCOUNTS"))
								  .route(p -> p.path("/alpine/loans/**")
											   .filters(f -> f.rewritePath("alpine/loans/(?<segment>.*)","/${segment}")
															  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
											   .uri("lb://LOANS"))
								  .route(p -> p.path("/alpine/cards/**")
											   .filters(f -> f.rewritePath("alpine/cards/(?<segment>.*)","/${segment}")
															  .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
											   .uri("lb://CARDS"))
								  .build();
	}

}

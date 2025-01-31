package com.example.gatewayserver.filters;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    private final FilterUtility filterUtility;

    public RequestTraceFilter(final FilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, final GatewayFilterChain chain) {
        final HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if (this.isCorrelationIdPresent(requestHeaders)) {
            logger.debug("alpine-correlation-id found in RequestTraceFilter : {}", this.filterUtility.getCorrelationId(requestHeaders));
        } else {
            final String correlationID = this.generateCorrelationId();
            exchange = this.filterUtility.setCorrelationId(exchange, correlationID);
            logger.debug("alpine-correlation-id generated in RequestTraceFilter : {}", correlationID);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(final HttpHeaders requestHeaders) {
        return StringUtils.isNotEmpty(this.filterUtility.getCorrelationId(requestHeaders));
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

}

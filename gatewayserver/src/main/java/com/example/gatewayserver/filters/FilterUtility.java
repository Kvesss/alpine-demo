package com.example.gatewayserver.filters;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

@Component
public class FilterUtility {

    static final String CORRELATION_ID = "alpine-correlation-id";

    public String getCorrelationId(final HttpHeaders requestHeaders) {
        return Optional.ofNullable(requestHeaders.get(CORRELATION_ID))
                       .map(strings -> strings.stream()
                                              .findFirst()
                                              .orElse(StringUtils.EMPTY))
                       .orElse(StringUtils.EMPTY);
    }

    public ServerWebExchange setRequestHeader(final ServerWebExchange exchange, final String name, final String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }

    public ServerWebExchange setCorrelationId(final ServerWebExchange exchange, final String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

}

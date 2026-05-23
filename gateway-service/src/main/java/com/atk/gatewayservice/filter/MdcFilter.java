package com.atk.gatewayservice.filter;


import org.slf4j.MDC;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MdcFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst("X-TRace-Id");
        if (traceId != null){
            MDC.put("traceId",traceId);
        } else {
            MDC.put("traceId", UUID.randomUUID().toString());
        }
        return chain.filter(exchange)
                .doFinally(signalType -> MDC.clear());
    }
}

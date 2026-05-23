package com.atk.gatewayservice.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TraceIdFilter  implements GlobalFilter, Ordered {

    private static final String  TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String incomingTraceId = exchange.getRequest()
                .getHeaders()
                .getFirst(TRACE_ID_HEADER);

        final String traceId = (incomingTraceId == null || incomingTraceId.isEmpty()) ?
                UUID.randomUUID().toString() : incomingTraceId;

        MDC.put("traceId", traceId);
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.header(TRACE_ID_HEADER, traceId))
                .build();

        return chain.filter(mutatedExchange)
                .doFinally(signalType -> MDC.clear());
    }

    @Override
    public int getOrder() {
        return -100; //Must run first!!!
    }
}

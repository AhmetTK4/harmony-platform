package com.atk.gatewayservice;

import com.atk.gatewayservice.filter.AuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationFilterTest {
    private static final String SECRET = "test-only-key-not-for-deployment-0123456789";
    private AuthenticationFilter filter;

    @BeforeEach void setUp() {
        filter = new AuthenticationFilter();
        ReflectionTestUtils.setField(filter, "secretKeyString", SECRET);
    }

    @Test void missingTokenDoesNotReachService() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/products"));
        var chain = mock(GatewayFilterChain.class);
        filter.apply(new AuthenticationFilter.Config()).filter(exchange, chain).block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(chain);
    }

    @Test void malformedTokenDoesNotReachService() {
        assertRejected("not-a-jwt");
    }

    @Test void expiredTokenDoesNotReachService() {
        assertRejected(token(new Date(System.currentTimeMillis() - 60000)));
    }

    @Test void validTokenReplacesSpoofedUsernameBeforeForwarding() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/products")
                .header("Authorization", "Bearer " + token(new Date(System.currentTimeMillis() + 60000)))
                .header("username", "spoofed"));
        var forwarded = new AtomicReference<String>();
        filter.apply(new AuthenticationFilter.Config()).filter(exchange, next -> {
            forwarded.set(next.getRequest().getHeaders().getFirst("username"));
            return Mono.empty();
        }).block();
        assertThat(forwarded.get()).isEqualTo("demo");
    }

    private void assertRejected(String token) {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/products")
                .header("Authorization", "Bearer " + token));
        var chain = mock(GatewayFilterChain.class);
        filter.apply(new AuthenticationFilter.Config()).filter(exchange, chain).block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(chain);
    }

    private String token(Date expiry) {
        return Jwts.builder().setSubject("demo").setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}

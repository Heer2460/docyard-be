package com.infotech.docyard.gateway.config;

import com.infotech.docyard.gateway.util.AppConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;


@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;
    public static final String AUTHORIZATION_TOKEN = "auth_token";

    @Value("${infotech.gw.oauth.clientId}")
    private String clientID;

    @Value("${infotech.gw.oauth.clientSecret}")
    private String clientSecret;

    @Value("${infotech.gw.services.authentication}")
    private String authenticationService;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (exchange.getRequest().getURI().getRawPath().contains("/un-auth")) {
                return chain.filter(exchange)
                        .then(Mono.fromRunnable(() -> {
                        }));
            }
            if (!exchange.getRequest().getHeaders().containsKey(AUTHORIZATION_TOKEN)) {
                throw new RuntimeException("Missing authorization information");
            }

            String authHeader = exchange.getRequest().getHeaders().get(AUTHORIZATION_TOKEN).get(0);
            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new RuntimeException("Incorrect authorization structure");
            }
            String token = parts[1];
            String uri = AppConstants.OAUTH_TOKEN_API_PROTOCOL + authenticationService + AppConstants.OAUTH_TOKEN_CHECK_API + token;

            return webClientBuilder.build()
                    .post()
                    .uri(uri)
                    .retrieve().bodyToMono(Object.class)
                    .map(userDto -> {
                        exchange.getRequest()
                                .mutate()
                                .header("client_id", clientID)
                                .header("client_secret", clientSecret);
                        return exchange;
                    }).flatMap(chain::filter);
        };
    }

    public static class Config {

    }
}

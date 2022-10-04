package com.infotech.docyard.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


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
            if (!exchange.getRequest().getHeaders().containsKey(AUTHORIZATION_TOKEN)) {
                throw new RuntimeException("Missing authorization information");
            }

            String authHeader = exchange.getRequest().getHeaders().get(AUTHORIZATION_TOKEN).get(0);

            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new RuntimeException("Incorrect authorization structure");
            }

            String token = parts[1];

            StringBuffer uri = new StringBuffer("http://");
            uri.append(authenticationService).append("/oauth/check_token?token=").append(token);

            return webClientBuilder.build()
                    .post()
                    .uri(uri.toString())
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

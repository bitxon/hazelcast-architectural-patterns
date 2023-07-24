package bitxon.hz.utils;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class FilterUtils {

    public static Mono<Void> mutateResponse(ServerWebExchange exchange, String newResponse) {
        final var response = exchange.getResponse();
        final var responseBuffer = response.bufferFactory().wrap(newResponse.getBytes(StandardCharsets.UTF_8));

        response.setRawStatusCode(200);
        //response.getHeaders().setContentLength(INVALID_BODY.length()); // redundant
        response.writeWith(Mono.just(responseBuffer)).subscribe();

        exchange.mutate().response(response).build();
        return Mono.empty();
    }
}

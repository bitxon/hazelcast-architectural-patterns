package bitxon.hz.filters;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

@Component
@RequiredArgsConstructor
public class HzPutCacheGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final HazelcastInstance hazelcastInstance;

    @Override
    public GatewayFilter apply(Object config) {
        return ((exchange, chain) -> {
            final Map<String, String> pathParams = exchange.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            var cache = pathParams.get("cache");
            var key = pathParams.get("key");
            var value = pathParams.get("value");

            hazelcastInstance.getMap(cache).put(key, value);

            return chain.filter(exchange);
        });
    }
}

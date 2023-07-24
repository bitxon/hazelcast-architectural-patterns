package bitxon.hz.filters;

import bitxon.hz.utils.FilterUtils;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

@Component
@RequiredArgsConstructor
public class HzGetCacheGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final HazelcastInstance hazelcastInstance;

    @Override
    public GatewayFilter apply(Object config) {
        return ((exchange, chain) -> {
            final Map<String, String> pathParams = exchange.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            var cache = pathParams.get("cache");
            var key = pathParams.get("key");

            String value = getCache(cache).get(key);
            if (value != null) {
                return FilterUtils.mutateResponse(exchange, value);
            }

            return chain.filter(exchange);
        });
    }

    private ConcurrentMap<String, String> getCache(String name) {
        return hazelcastInstance.getMap(name);
    }
}

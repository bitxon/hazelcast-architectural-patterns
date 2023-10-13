package bitxon.hz;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Configuration
public class HzConfiguration {

    @Bean // Spring automatically detects method shutdown() as destroyMethod
    public HazelcastInstance hazelcastInstance() {
        // By default, it will try to load configuration from hazelcast-client.yaml or hazelcast-client.xml
        var start = LocalDateTime.now();
        var instance = HazelcastClient.newHazelcastClient();
        log.info("Hazelcast started @{} sec", Duration.between(start, LocalDateTime.now()).getSeconds());
        return instance;
    }
}

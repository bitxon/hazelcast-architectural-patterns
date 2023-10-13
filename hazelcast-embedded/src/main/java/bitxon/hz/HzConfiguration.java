package bitxon.hz;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

@Slf4j
@Configuration
public class HzConfiguration {

    @Bean // Spring automatically detects method shutdown() as destroyMethod
    public HazelcastInstance hazelcastInstance() {
        // By default, it will try to load configuration from hazelcast.yaml or hazelcast.xml
        var start = LocalDateTime.now();
        var instance = Hazelcast.newHazelcastInstance();
        log.info("Hazelcast started @{} sec", Duration.between(start, LocalDateTime.now()).getSeconds());
        return instance;
    }
}

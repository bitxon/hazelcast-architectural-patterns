package bitxon.hz;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HzConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        // By default, it will try to load configuration from hazelcast.yaml or hazelcast.xml
        return Hazelcast.newHazelcastInstance();
    }
}

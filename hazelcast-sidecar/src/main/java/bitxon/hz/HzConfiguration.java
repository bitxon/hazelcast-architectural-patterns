package bitxon.hz;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HzConfiguration {

    @Bean // Spring automatically detects method shutdown() as destroyMethod
    public HazelcastInstance hazelcastInstance() {
        // By default, it will try to load configuration from hazelcast-client.yaml or hazelcast-client.xml
        return HazelcastClient.newHazelcastClient();
    }
}

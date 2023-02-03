package bitxon.hz;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HzConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        var conf = new ClientConfig();
        conf.getNetworkConfig().addAddress("localhost:5701");
        return HazelcastClient.newHazelcastClient(conf);
    }
}

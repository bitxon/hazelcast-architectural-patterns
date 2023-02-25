package bitxon.hz;

import java.util.Properties;

import com.hazelcast.config.replacer.spi.ConfigReplacer;

public class EnvironmentConfigReplacer implements ConfigReplacer {
    @Override
    public void init(Properties properties) {
        System.out.println("HZ init: start");
        properties.entrySet().stream()
            .map(entry -> String.format("HZ init: [%s:%s]", entry.getKey(), entry.getValue()))
            .forEach(System.out::println);
    }

    @Override
    public String getPrefix() {
        return "env";
    }

    @Override
    public String getReplacement(String placeholder) {
        var environmentVariable = System.getenv(placeholder);
        System.out.println(String.format("HZ Replace: '%s'->'%s'", placeholder, environmentVariable));
        return environmentVariable;
    }
}

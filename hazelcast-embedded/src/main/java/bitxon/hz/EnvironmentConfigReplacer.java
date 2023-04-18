package bitxon.hz;

import java.util.Properties;

import com.hazelcast.config.replacer.spi.ConfigReplacer;

public class EnvironmentConfigReplacer implements ConfigReplacer {
    @Override
    public void init(Properties properties) {
        // Do nothing
    }

    @Override
    public String getPrefix() {
        return "env";
    }

    @Override
    public String getReplacement(String placeholder) {
        return System.getenv(placeholder);
    }
}
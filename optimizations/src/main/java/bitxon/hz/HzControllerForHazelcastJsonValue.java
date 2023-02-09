package bitxon.hz;

import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HzControllerForHazelcastJsonValue {

    public static final String HAZELCAST_JSON_VALUE = "hazelcastjsonvalue";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping(value = "/hazelcastjsonvalue/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNestedObject(@PathVariable("key") String key) {
        return getCache().get(key).toString();
    }

    @PutMapping(value = "/hazelcastjsonvalue/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String putNestedObject(@PathVariable("key") String key, @RequestBody @NotNull String value) {
        getCache().put(key, new HazelcastJsonValue(value));
        return value;
    }


    private ConcurrentMap<String, HazelcastJsonValue> getCache() {
        return hazelcastInstance.getMap(HAZELCAST_JSON_VALUE);
    }

}

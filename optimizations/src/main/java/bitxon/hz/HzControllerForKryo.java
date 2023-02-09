package bitxon.hz;

import java.util.concurrent.ConcurrentMap;

import bitxon.hz.model.kryo.User;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HzControllerForKryo {

    public static final String KRYO_CACHE = "kryo";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping("/kryo/{key}")
    public User getNestedObject(@PathVariable("key") String key) {
        return getCache().get(key);
    }

    @PutMapping("/kryo/{key}")
    public User putNestedObject(@PathVariable("key") String key, @RequestBody @NotNull User value) {
        getCache().put(key, value);
        return value;
    }


    private ConcurrentMap<String, User> getCache() {
        return hazelcastInstance.getMap(KRYO_CACHE);
    }

}

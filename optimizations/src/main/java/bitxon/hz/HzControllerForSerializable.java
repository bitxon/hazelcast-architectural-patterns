package bitxon.hz;

import java.util.concurrent.ConcurrentMap;

import bitxon.hz.model.serializable.User;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HzControllerForSerializable {

    public static final String SERIALIZABLE_CACHE = "serializable";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping("/serializable/{key}")
    public User getNestedObject(@PathVariable("key") String key) {
        return getCache().get(key);
    }

    @PutMapping("/serializable/{key}")
    public User putNestedObject(@PathVariable("key") String key, @RequestBody @NotNull User value) {
        getCache().put(key, value);
        return value;
    }


    private ConcurrentMap<String, User> getCache() {
        return hazelcastInstance.getMap(SERIALIZABLE_CACHE);
    }


}

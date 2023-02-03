package bitxon.hz;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class HzController {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping("/cache/{key}")
    public String get(@PathVariable("key") String key) {
        return getCache().get(key);
    }

    @PutMapping("/cache/{key}/value/{value}")
    public void put(@PathVariable("key") String key, @PathVariable("value") String value) {
        getCache().put(key, value);
    }

    @SneakyThrows
    @GetMapping("/locked-cache/{key}")
    public String getWithLock(@PathVariable("key") String key) {
        var lock = getLock(key);

        if (lock.tryLock(5, SECONDS)) {
            try {
                SECONDS.sleep(10); // pretend that this is long operation
                return getCache().get(key);
            } finally {
                lock.unlock();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Lock not acquired");
        }
    }


    private ConcurrentMap<String, String> getCache() {
        return hazelcastInstance.getMap("mainCache");
    }

    private FencedLock getLock(String key) {
        return hazelcastInstance.getCPSubsystem().getLock(key);
    }

}
